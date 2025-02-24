package com.pumpkinmh.chip8;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

//TODO Reimplement the screen as a buffered image
public class CHIP8Driver {
    static int cycleTimeMillis = 1;
    static int pixelScale = 1;
    static int soundFrequency = 440;
    static boolean soundEnabled = false;
    static double bufferSecondsTime = .05;

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

        // Get saved settings from .properties file
        Properties ch8properties = new Properties();
        try (FileInputStream propertiesStream = new FileInputStream("chip8.properties")) {
            ch8properties.load(propertiesStream);
        } catch (FileNotFoundException e) {
          try {
            new File("chip8.properties").createNewFile();
          } catch (IOException ignored) {

          }
        }
        catch (IOException ignored) {

        }

        // Used save settings only if they are valid, otherwise do nothing
        String cycleString = ch8properties.getProperty("ch8.cpuDelay");
        if(isInt(cycleString)) {
            cycleTimeMillis = Integer.parseInt(cycleString);
        }

        String scaleString = ch8properties.getProperty("ch8.displayScale");
        if(isInt(scaleString)) {
            pixelScale = Integer.parseInt(scaleString);
        }

        String frequencyString = ch8properties.getProperty("ch8.soundFrequency");
        if(isInt(frequencyString)) {
            soundFrequency = Integer.parseInt(frequencyString);
        }

        String enableSoundString = ch8properties.getProperty("ch8.enableSound");
        soundEnabled = Boolean.parseBoolean(enableSoundString);

        // Prompt the user for interpreter options
        CHIP8OptionsDialog optionsDialog = new CHIP8OptionsDialog();
        optionsDialog.pack();
        optionsDialog.setLocationRelativeTo(null);
        optionsDialog.setInitialValues(pixelScale,cycleTimeMillis,soundFrequency,soundEnabled);
        optionsDialog.setVisible(true);
        if(optionsDialog.isChangesMade()) {
            pixelScale = optionsDialog.getScaleValue();
            cycleTimeMillis = optionsDialog.getDelayValue();
            soundFrequency = optionsDialog.getSoundFrequency();
            soundEnabled = optionsDialog.isSoundEnabled();
        } else {
            return;
        }

        // Write properties to .properties file
        ch8properties.setProperty("ch8.cpuDelay", String.valueOf(cycleTimeMillis));
        ch8properties.setProperty("ch8.displayScale", String.valueOf(pixelScale));
        ch8properties.setProperty("ch8.soundFrequency", String.valueOf(soundFrequency));
        ch8properties.setProperty("ch8.enableSound", String.valueOf(soundEnabled));
        try(FileOutputStream propertiesStream = new FileOutputStream("chip8.properties")) {
            new File("chip8.properties").createNewFile();
            ch8properties.store(propertiesStream, null);
        } catch (IOException ignored) {
          System.err.println(".properties file could not be written to");
        }

      // Initialize interface
        Chip8Interface ch8interface = new Chip8Interface(pixelScale);
        JFrame frame = new JFrame("CHIP-8 Interpreter");
        frame.addKeyListener(ch8interface.getInputListener());
        frame.add(ch8interface);
        frame.pack();
        frame.setLocationRelativeTo(null);
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

        // Initialize sound
        Thread soundThread;
        if(soundEnabled) {
            try {
                AudioFormat audioFormat = new AudioFormat(44100, 8, 1, true, false);
                SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
                sourceDataLine.open();
                sourceDataLine.start();

                Runnable runnableAudio = () -> {
                    int sampleRate = (int) audioFormat.getSampleRate();
                    int frequency = soundFrequency;
                    while(soundEnabled) {
                        if(ch8core.isSoundPlaying()) {
                            ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();
                            for(int i = 0; i < sampleRate * bufferSecondsTime; i++) {
                                double angle = 2 * Math.PI * frequency * i / sampleRate;
                                byte sample = (byte) (Math.sin(angle) * 127);
                                audioBuffer.write(sample);
                            }
                            byte[] dataLineBytes = audioBuffer.toByteArray();
                            sourceDataLine.write(dataLineBytes,0,dataLineBytes.length);
                        } else {
                            sourceDataLine.flush();
                        }
                    }
                    sourceDataLine.close();
                };
                soundThread = new Thread(runnableAudio);
                soundThread.setPriority(Thread.MAX_PRIORITY);
                soundThread.start();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ch8interface, "An error occurred initializing the sound thread. Sound will now be disabled", "Sound Thread Error", JOptionPane.ERROR_MESSAGE);
                soundEnabled = false;
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
                try {
                    ch8core.cycle();
                } catch (UnknownOpcodeException e) {
                    JOptionPane.showMessageDialog(ch8interface, e.getStackTrace(), "Unknown Opcode Exception: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                }
                ch8interface.updateScreen(ch8core.getStaticScreen());
            }
        }
    }

    public static boolean isInt(String string) {
        if(string == null) {
            return false;
        }
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
