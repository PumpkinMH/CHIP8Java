import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class KeyboardListener implements KeyListener {
    private boolean[] keypad;
    private HashMap<Character, Character> keypadMapping;


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyChar());


    }

    @Override
    public void keyReleased(KeyEvent e) {
//        System.out.println(e.getKeyChar());
    }

    public KeyboardListener() {
        keypad = new boolean[16];
        keypadMapping = new HashMap<Character, Character>();

        // first arg is key pressed on real keyboard, the second is key pressed on CHIP8 keypad
        keypadMapping.put('1','1');
        keypadMapping.put('2','2');
        keypadMapping.put('3','3');
        keypadMapping.put('4', 'C');
        keypadMapping.put('q', '4');
        keypadMapping.put('w', '5');
        keypadMapping.put('e', '6');
        keypadMapping.put('r', 'D');
        keypadMapping.put('a','7');
        keypadMapping.put('s','8');
        keypadMapping.put('d','9');
        keypadMapping.put('f','E');
        keypadMapping.put('z','A');
        keypadMapping.put('x', '0');
        keypadMapping.put('c','B');
        keypadMapping.put('v', 'F');
    }

    private void updateKeys(boolean pressed, char key) {

    }

    public boolean[] getStaticKeys() {
        return keypad.clone();
    }

}
