import java.io.*;

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

    CHIP8 computer = new CHIP8();
    try {
        computer.loadROM("tetris.ch8");
    } catch (IOException e) {
        e.printStackTrace();
        return;
    }
  }
}
