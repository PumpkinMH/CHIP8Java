import java.io.*;
import java.util.Random;

public class Test {
  public static void main(String[] args) {
    byte hexTest = (byte) 255;
//    System.out.print(hexTest & 0xFF);

    short shortHexTest = (short) 65535;
//    System.out.print(shortHexTest & 0xFFFF);
//    System.out.println(Integer.toBinaryString(0x0));
//      System.out.println(Integer.toBinaryString(0xFFF));
    System.out.println(0x0F00 >> 8);

//    try {
//      File file = new File("tetris.ch8");
//      InputStream inputStream = new FileInputStream(file);
//
//      int data;
//      while((data = inputStream.read()) != -1) {
//        System.out.println(Integer.toHexString(data));
//      }
//    } catch (IOException e) {
//        e.printStackTrace();
//    }

    CHIP8Core computer = new CHIP8Core();
    try {
        computer.loadROM("tetris.ch8");
    } catch (IOException e) {
        e.printStackTrace();
        return;
    }

    System.out.println();

    Random randomByteGenerator = new Random();
    for(int i = 0; i < 100; i++) {
      System.out.println(randomByteGenerator.nextInt(256));
    }
  }
}
