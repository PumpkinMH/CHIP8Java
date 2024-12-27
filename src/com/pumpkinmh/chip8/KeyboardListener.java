package com.pumpkinmh.chip8;

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
//        System.out.println(e.getKeyChar());
        Character keypadChar;
        if((keypadChar = keypadMapping.get(e.getKeyChar())) != null) {
            updateKeys(true, keypadChar);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
//        System.out.println(e.getKeyChar());
        Character keypadChar;
        if((keypadChar = keypadMapping.get(e.getKeyChar())) != null) {
            updateKeys(false, keypadChar);
        }
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
        switch (key) {
            case '0':
                keypad[0x0] = pressed;
                break;
            case '1':
                keypad[0x1] = pressed;
                break;
            case '2':
                keypad[0x2] = pressed;
                break;
            case '3':
                keypad[0x3] = pressed;
                break;
            case '4':
                keypad[0x4] = pressed;
                break;
            case '5':
                keypad[0x5] = pressed;
                break;
            case '6':
                keypad[0x6] = pressed;
                break;
            case '7':
                keypad[0x7] = pressed;
                break;
            case '8':
                keypad[0x8] = pressed;
                break;
            case '9':
                keypad[0x9] = pressed;
                break;
            case 'A':
                keypad[0xA] = pressed;
                break;
            case 'B':
                keypad[0xB] = pressed;
                break;
            case 'C':
                keypad[0xC] = pressed;
                break;
            case 'D':
                keypad[0xD] = pressed;
                break;
            case 'E':
                keypad[0xE] = pressed;
                break;
            case 'F':
                keypad[0xF] = pressed;
                break;
        }
    }

    public boolean[] getStaticKeys() {
        return keypad.clone();
    }

}
