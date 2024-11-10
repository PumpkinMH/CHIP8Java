public class Test {
  public static void main(String[] args) {
    byte hexTest = (byte) 255;
//    System.out.print(hexTest & 0xFF);

    short shortHexTest = (short) 65535;
//    System.out.print(shortHexTest & 0xFFFF);
//    System.out.println(Integer.toBinaryString(0x0));
//      System.out.println(Integer.toBinaryString(0xFFF));
    System.out.println(0x0F00 >> 8);
  }
}
