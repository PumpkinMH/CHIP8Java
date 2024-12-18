import java.io.File;
import javax.swing.*;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CHIP8Driver {
    static int cycleTimeMillis = 10;
    static int pixelScale = 10;

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

        CHIP8OptionsDialog optionsDialog = new CHIP8OptionsDialog();
        optionsDialog.pack();
        optionsDialog.setVisible(true);
        if(optionsDialog.isChangesMade()) {
            pixelScale = optionsDialog.getScaleValue();
            cycleTimeMillis = optionsDialog.getDelayValue();
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
