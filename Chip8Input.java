import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Chip8Input implements KeyListener {
    private boolean[] keypad;

    @Override
    public void keyTyped(KeyEvent e) {
        // Do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println(e.getKeyChar());
    }

    public Chip8Input(boolean[] keypad) {
        if(keypad.length != 16) {
            throw new ArrayIndexOutOfBoundsException("Keypad array is not of length 16");
        }
    }
}
