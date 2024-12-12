import javax.swing.*;
import java.io.IOException;

public class CHIP8Driver {
    static int cycleTimeMillis = 10;

    public static void main(String[] args) {
        Chip8Interface ch8interface = new Chip8Interface();

        JFrame frame = new JFrame("Chip 8");
        frame.addKeyListener(ch8interface.getInputListener());
        frame.add(ch8interface);
//        frame.setSize(640, 320);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        CHIP8Core ch8core = new CHIP8Core();
        try {
            ch8core.loadROM("tetris.ch8");
        } catch (IOException e) {
            System.out.println("File not found");
            return;
        }

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
