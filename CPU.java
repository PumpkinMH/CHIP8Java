import java.util.Stack;

public class CPU {
  private byte[] registers;
  private short indexRegister;
  private short programCounter;
  private short[] cpuStack;
  private byte stackPointer;

  public CPU() {
    registers = new byte[16];
    cpuStack = new short[16];

  }


}
