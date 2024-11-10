public class CHIP8 {
  private byte[] registers;
  private short indexRegister;
  private short programCounter;
  private short[] cpuStack;
  private byte stackPointer;
  private short opcode;

  private byte[] keypad;

  private int[] screen;

  private byte delayTimer;
  private byte soundTimer;

  private byte[] memory;

  public CHIP8() {
    registers = new byte[16];
    cpuStack = new short[16];
    keypad = new byte[16];
    screen = new int[64 * 32];
    memory = new byte[4096];
  }

  private void OP_00E0() { //CLS: clear video memory
    for(int i = 0; i < memory.length; i++) {
      memory[i] = 0;
    }
  }

  private void OP_00EE() { //RET: return from a subroutine
    stackPointer--;
    programCounter = cpuStack[stackPointer];
  }

  private void OP_1nnn() { // JP addr: jump to the address without touching stack

  }
}
