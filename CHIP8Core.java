import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Random;
import java.io.InputStream;
import java.io.FileInputStream;

// TODO Finish opcode decoding, the main cycle, and the graphics
public class CHIP8Core {
  private short[] registers;
  private int indexRegister;
  private int programCounter;
  private int[] cpuStack;
  private short stackPointer;
  private int opcode;

  private boolean[] keypad;

  private long[] screen;

  private short delayTimer;
  private short soundTimer;

  private short[] memory;

  private boolean romLoaded;

  final private short[] fontSet =
          {
                  0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
                  0x20, 0x60, 0x20, 0x20, 0x70, // 1
                  0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
                  0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
                  0x90, 0x90, 0xF0, 0x10, 0x10, // 4
                  0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
                  0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
                  0xF0, 0x10, 0x20, 0x40, 0x40, // 7
                  0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
                  0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
                  0xF0, 0x90, 0xF0, 0x90, 0x90, // A
                  0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
                  0xF0, 0x80, 0x80, 0x80, 0xF0, // C
                  0xE0, 0x90, 0x90, 0x90, 0xE0, // D
                  0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
                  0xF0, 0x80, 0xF0, 0x80, 0x80  // F
          };

  public CHIP8Core() {
    registers = new short[16];
    cpuStack = new int[16];
    keypad = new boolean[16];
    screen = new long[64 * 32];
    memory = new short[4096];
    programCounter = 0x200;
    romLoaded = false;

    for(int i = 0; i < fontSet.length; i++) {
      memory[0x50 + i] = fontSet[i];
    }
  }

  private void OP_00E0() { //CLS: clear video memory
    for(int i = 0; i < screen.length; i++) {
      screen[i] = 0;
    }
  }

  private void OP_00EE() { //RET: return from a subroutine
    stackPointer--;
    programCounter = cpuStack[stackPointer];
  }

  private void OP_1nnn() { // JP addr: jump to the address without touching stack
    int address = opcode & 0xFFF;
    programCounter = address;
  }

  private void OP_2nnn() { // CALL addr: call the address (jump to it and change the stack)
    int address = opcode & 0xFFF;
    cpuStack[stackPointer] = programCounter;
    programCounter = address;
    stackPointer++;
  }

  private void OP_3xkk() { // SE Vx, byte: skip next instruction if Vx == kk
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short kk = (short) (opcode & 0x00FF);

    if(registers[vx] == kk) {
      programCounter += 2;
    }
  }

  private void OP_4xkk() { // SNE Vx, byte: skip next instruction if Vx != kk
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short kk = (short) (opcode & 0x00FF);

    if(registers[vx] != kk) {
      programCounter += 2;
    }
  }

  private void OP_5xy0() { // SE Vx, Vy: skip next instruction if Vx != kk
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short vy = (short) ((opcode & 0x00F0) >> 4 & 0xFF);

    if(registers[vx] == registers[vy]) {
      programCounter += 2;
    }
  }

  private void OP_6xkk() { // LD Vx, byte: loads the value of kk into Vx
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short kk = (short) (opcode & 0x00FF);

    registers[vx] = kk;
  }

  private void OP_7xkk() { // ADD Vx, byte: adds the value of kk to Vx and stores the result
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short kk = (short) (opcode & 0x00FF);

//    registers[vx] += kk;
    registers[vx] = (short) ((registers[vx] + kk) & 0xFF);
  }

  private void OP_8xy0() { // LD Vx, Vy: loads the value of vy into vx
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short vy = (short) ((opcode & 0x00F0) >> 4 & 0xFF);

    registers[vx] = registers[vy];
  }

  private void OP_8xy1() { // OR Vx, Vy: sets Vx to Vx OR Vy
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short vy = (short) ((opcode & 0x00F0) >> 4 & 0xFF);

    registers[vx] = (short) (registers[vx] | registers[vy]);
  }

  private void OP_8xy2() { // AND Vx, Vy: sets Vx to Vx AND Vy
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short vy = (short) ((opcode & 0x00F0) >> 4 & 0xFF);

    registers[vx] = (short) (registers[vx] & registers[vy]);
  }

  private void OP_8xy3() { // XOR Vx, Vy: sets Vx to Vx XOR Vy
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short vy = (short) ((opcode & 0x00F0) >> 4 & 0xFF);

    registers[vx] = (short) (registers[vx] ^ registers[vy]);
  }

  private void OP_8xy4() { // ADD Vx, Vy: adds the value of Vy to Vx, and if the result is larger than 8 bits, VF is set to 1
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short vy = (short) ((opcode & 0x00F0) >> 4 & 0xFF);

    short sum = (short) ((registers[vx] + registers[vy]));
    if(sum > 255) {
      registers[15] = 1;
    } else {
      registers[15]= 0;
    }
    registers[vx] = (short) (sum & 0xFF);
  }

  private void OP_8xy5() { // SUB Vx, Vy: If vx > vy, vf is 1, then vx - vy, store in vx
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short vy = (short) ((opcode & 0x00F0) >> 4 & 0xFF);

    if(registers[vx] > registers[vy]) {
      registers[15] = 1;
    } else {
      registers[15] = 0;
    }

    registers[vx] = (short) ((registers[vx] - registers[vy]) & 0xFF);
  }

  private void OP_8xy6() { // SHR Vx : Shift Vx right, and store the result in Vx and the lsb in vf
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);

    registers[15] = (short) (registers[vx] & 0x1);
    registers[vx] = (short) ((registers[vx] >> 1) & 0xFF);
  }

  private void OP_8xy7() { // SUBN Vx, Vy: Same as 8xy5, but with the operands swapped
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short vy = (short) ((opcode & 0x00F0) >> 4 & 0xFF);

    if(registers[vx] < registers[vy]) {
      registers[15] = 1;
    } else {
      registers[15] = 0;
    }

    registers[vx] = (short) ((registers[vy] - registers[vx]) & 0xFF);
  }

  private void OP_8xyE() { // SHL Vx: Shift Vx left, and store the result in Vx and the msb in vf
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);

    registers[15] = (short) ((registers[vx] & 0x80) >> 7);
    registers[vx] = (short) ((registers[vx] << 1) & 0xFF);
  }

  private void OP_9xy0() { // SNE Vx, Vy: Skip the next instruction if Vx and Vy are not equal
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short vy = (short) ((opcode & 0x00F0) >> 4 & 0xFF);

    if(registers[vx] != registers[vy]) {
      programCounter += 2;
    }
  }

  private void OP_Annn() { // LD I, addr: set the index register to nnn
    int address = opcode & 0xFFF;
    indexRegister = address & 0xFFFF;
  }

  private void OP_Bnnn() { // JP V0, addr: jump to nnn + v0
    int address = opcode & 0xFFF;
    programCounter = registers[0] + address & 0xFFFF;
  }

  private void OP_Cxkk() { // RND Vx, byte: generate a random number to be ANDed with kk and store in vx
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short kk = (short) (opcode & 0x00FF);

    Random randomByteGenerator = new Random();
    int randomByte = randomByteGenerator.nextInt(256);

    registers[vx] = (short) ((randomByte & kk) & 0xFF);
  }

  private void OP_Dxyn() {
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short vy = (short) ((opcode & 0x00F0) >> 4 & 0xFF);
    short height = (short) (opcode & 0xF);

    short xPos = (short) (registers[vx] % 64);
    short yPos = (short) (registers[vy] % 32);

    registers[15] = 0;

    for(int row = 0; row < height; row++) {
      short spriteByte = (short) (memory[indexRegister + row] & 0xFF);
      for(int col = 0; col < 8; col++) {
        short spritePixel = (short) (spriteByte & (0x80 >> col) & 0xFF);
        long screenPixel = screen[(yPos + row) * 64 + (xPos + col )];

        if(spritePixel > 0) {
          if(screenPixel == 0xFFFFFFFFL) {
            registers[15] = 1;
          }

          screen[(yPos + row) * 64 + (xPos + col )] ^= 0xFFFFFFFFL;
        }
      }
    }

  }

  private void OP_Ex9E() { // SKP Vx: skip next instruction if a key with the value of vx is pressed
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    int pressedKey = registers[vx];
    if(keypad[pressedKey]) {
      programCounter += 2;
    }
  }

  private void OP_ExA1() { // SKNP Vx: skip next instruction if a key with the value of vx is NOT pressed
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    int pressedKey = registers[vx];
    if(!keypad[pressedKey]) {
      programCounter += 2;
    }
  }

  private void OP_Fx07() { // Set Vx to the value of the Delay Timer
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    registers[vx] = (short) (delayTimer & 0xFF);
  }

  private void OP_Fx0A() { // wait until a keypress, and then store the key in Vx
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    boolean success = false;

    for(int i = 0; i < keypad.length && !success; i++) {
      if(keypad[i]) {
        success = true;
        registers[vx] = (short) (i & 0xFF);
      }
    }

    if(!success) {
      programCounter -= 2;
    }
  }

  private void OP_Fx15() { // set the delay timer to the value stored in Vx
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    delayTimer = registers[vx];
  }

  private void OP_Fx18() { // set the sound timer to the value stored in Vx
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    soundTimer = registers[vx];
  }

  private void OP_Fx1E() { // Set the index register to the current value plus the value stored in Vx
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    indexRegister = (indexRegister + registers[vx]) & 0xFFFF;
  }

  private void OP_Fx29() { // Set index register to the location of sprite for digit Vx
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short digit = registers[vx];

    indexRegister = 0x50 + (5 * digit); // 0x50 is the start of the characters, and each character is 5 bytes
  }

  private void OP_Fx33() { // Store the BCD representation of the value in Vx in index register, with I representing the 100s, I+1 representing the 10s, and I+2 representing the 1s
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);
    short value = (short) (registers[vx] & 0xFF);

    memory[indexRegister + 2] = (short) (value % 10);
    value /= 10;

    memory[indexRegister + 1] = (short) (value % 10);
    value /= 10;

    memory[indexRegister] = (short) (value % 10);
  }

  private void OP_Fx55() { // store the values of V0 through VX starting at I
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);

    for(int i = 0; i <= vx; i++) {
      memory[indexRegister + i] = registers[i];
    }
  }

  private void OP_Fx65() { // read the values of I through I + x into V0 through VX
    short vx = (short) ((opcode & 0x0F00) >> 8 & 0xFF);

    for(int i = 0; i <= vx; i++) {
      registers[i] = (short) (memory[indexRegister + i] & 0xFF);
    }
  }

  private void executeOpcode() { // Decodes the opcode and executes the specific instruction
    if(((opcode & 0xFFF0) >> 4) == 0x00E) { // first three digits are 0x00E
      int lastDigit = opcode & 0xF;
      switch(lastDigit) {
        case 0x0:
          OP_00E0();
          break;
        case 0xE:
          OP_00EE();
          break;
        default:
          throw new UnknownOpcodeException(Integer.toHexString(opcode));
      }
    } else if(((opcode & 0xF000) >> 12) == 8) { // opcode starts with 0x8
      int lastDigit = opcode & 0xF;
      switch(lastDigit) {
        case 0x0:
          OP_8xy0();
          break;
        case 0x1:
          OP_8xy1();
          break;
        case 0x2:
          OP_8xy2();
          break;
        case 0x3:
          OP_8xy3();
          break;
        case 0x4:
          OP_8xy4();
          break;
        case 0x5:
          OP_8xy5();
          break;
        case 0x6:
          OP_8xy6();
          break;
        case 0x7:
          OP_8xy7();
          break;
        case 0xE:
          OP_8xyE();
          break;
        default:
          throw new UnknownOpcodeException(Integer.toHexString(opcode));
      }
    } else if (((opcode & 0xF000) >> 12) == 0xE) { // Opcode begins with E
      int lastDigits = opcode & 0xFF;
      switch(lastDigits) {
        case 0xA1:
          OP_ExA1();
          break;
        case 0x9E:
          OP_Ex9E();
          break;
        default:
          throw new UnknownOpcodeException(Integer.toHexString(opcode));
      }
    } else if ((((opcode & 0xF000) >> 12) == 0xF)) { // Opcode begins with F
      int lastDigits = opcode & 0xFF;
      switch(lastDigits) {
        case 0x07:
          OP_Fx07();
          break;
        case 0x0A:
          OP_Fx0A();
          break;
        case 0x15:
          OP_Fx15();
          break;
        case 0x18:
          OP_Fx18();
          break;
        case 0x1E:
          OP_Fx1E();
          break;
        case 0x29:
          OP_Fx29();
          break;
        case 0x33:
          OP_Fx33();
          break;
        case 0x55:
          OP_Fx55();
          break;
        case 0x65:
          OP_Fx65();
          break;
        default:
          throw new UnknownOpcodeException(Integer.toHexString(opcode));
      }
    } else { // First digit / entire opcode is unique
      int firstDigit = (opcode & 0xF000) >> 12;
      switch(firstDigit) {
        case 0x1:
          OP_1nnn();
          break;
        case 0x2:
          OP_2nnn();
          break;
        case 0x3:
          OP_3xkk();
          break;
        case 0x4:
          OP_4xkk();
          break;
        case 0x5:
          OP_5xy0();
          break;
        case 0x6:
          OP_6xkk();
          break;
        case 0x7:
          OP_7xkk();
          break;
        case 0x9:
          OP_9xy0();
          break;
        case 0xA:
          OP_Annn();
          break;
        case 0xB:
          OP_Bnnn();
          break;
        case 0xC:
          OP_Cxkk();
          break;
        case 0xD:
          OP_Dxyn();
          break;
        default:
          throw new UnknownOpcodeException(Integer.toHexString(opcode));
      }
    }
  }

  public void cycle() {
    opcode = ((memory[programCounter] << 8) | memory[programCounter+1]) & 0xFFFF;

    programCounter += 2;

    executeOpcode();

    if(delayTimer > 0) {
      delayTimer--;
    }

    if(soundTimer > 0) {
      soundTimer--;
    }
  }

  public void loadROM(String fileName) throws IOException {
      if(!romLoaded) {
        InputStream inputStream = new FileInputStream(fileName);
        int data;
        int offset = 0;
        while((data = inputStream.read()) != -1) {
          memory[0x200 + offset] = (short) (data & 0xFF);
          offset++;
        }
        romLoaded = true;
      } else {
        System.out.println("ROM has already been loaded!");
      }
  }

  public void updateKeypad(boolean[] keypad) {
    if(keypad.length == 64 * 32) {
      this.keypad = keypad.clone();
    }
  }

  public long[] getStaticScreen() {
    return screen.clone();
  }






}
