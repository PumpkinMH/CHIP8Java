import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

//TODO Implement audio via the techniques in SoundTest and ThreadTest. Reimplement the screen as a buffered image
public class CHIP8Driver {
    static int cycleTimeMillis = 10;
    static int pixelScale = 10;
    static int soundFrequency = 440;
    static boolean soundEnabled = false;

    public static void main(String[] args) {
        // Attempt to set look and feel to the system one
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Just use the default look and feel
        }

        // Prompt user for file, return if the file is invalid
        JFileChooser romSelectionDialog = new JFileChooser();
        FileNameExtensionFilter romFilter = new FileNameExtensionFilter("CHIP-8 ROM Files", "ch8");
        romSelectionDialog.setDialogTitle("Open CHIP-8 ROM File");
        romSelectionDialog.setFileFilter(romFilter);
        romSelectionDialog.setAcceptAllFileFilterUsed(false);
        int returnVal = romSelectionDialog.showOpenDialog(null);
        File romFile;
        if(returnVal == JFileChooser.CANCEL_OPTION) {
            return;
        } else {
            romFile = romSelectionDialog.getSelectedFile();
        }

        // Prompt the user for a pixel scale and cycle delay
        CHIP8OptionsDialog optionsDialog = new CHIP8OptionsDialog();
        optionsDialog.pack();
        optionsDialog.setVisible(true);
        if(optionsDialog.isChangesMade()) {
            pixelScale = optionsDialog.getScaleValue();
            cycleTimeMillis = optionsDialog.getDelayValue();
            soundFrequency = optionsDialog.getSoundFrequency();
            soundEnabled = optionsDialog.isSoundEnabled();
        } else {
            return;
        }

        // Initialize interface
        Chip8Interface ch8interface = new Chip8Interface(pixelScale);
        JFrame frame = new JFrame("CHIP-8 Interpreter");
        frame.addKeyListener(ch8interface.getInputListener());
        frame.add(ch8interface);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Load ROM
        CHIP8Core ch8core = new CHIP8Core();
        try {
            ch8core.loadROM(romFile);
        } catch (IOException e) {
            System.out.println("Error reading file");
            JOptionPane.showMessageDialog(null, "An error occurred reading the ROM file", "ROM File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Thread soundThread;
        // Initialize sound
        if(soundEnabled) {
            try {
                AudioFormat audioFormat = new AudioFormat(44100, 8, 1, true, false);
                SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
                sourceDataLine.open();
                sourceDataLine.start();

                Runnable runnableAudio = () -> {
                    int sampleRate = (int) audioFormat.getSampleRate();
                    int frequency = soundFrequency;
                    int index = 0;
                    byte[] instantSoundByte = new byte[1];
                    while(soundEnabled) {
                        if(ch8core.isSoundPlaying()) {
                            index %= sampleRate;
                            double angle = 2 * Math.PI * frequency * index++ / sampleRate;
                            byte sample = (byte) (Math.sin(angle) * 127);
                            instantSoundByte[0] = sample;
                            sourceDataLine.write(instantSoundByte, 0 ,1);
                        } else {
                            index = 0;
                            sourceDataLine.drain();
                        }
                    }
                };
                soundThread = new Thread(runnableAudio);
                soundThread.setPriority(Thread.MAX_PRIORITY);
                soundThread.start();
            } catch (Exception ignored) {

            }
        }

        // Main Cycle
        long startTime = System.currentTimeMillis();
        while(true) {
            ch8core.updateKeypad(ch8interface.getStaticKeys());
            long endTime = System.currentTimeMillis();
            long deltaTime = endTime - startTime;

            if(deltaTime > cycleTimeMillis) {
                startTime = System.currentTimeMillis();
                ch8core.cycle();
                ch8interface.updateScreen(ch8core.getStaticScreen());
            }
        }
    }
}
