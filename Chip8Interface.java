import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Chip8Interface extends JPanel {
  private KeyboardListener keyboardListener;
  private long[] frameBuffer;
  private final int SCALE = 10;

  public Chip8Interface() {
    super();
    frameBuffer = new long[64 * 32];
    keyboardListener = new KeyboardListener();
  }

  public KeyboardListener getInputListener() {
    return keyboardListener;
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(64 * SCALE,32 * SCALE);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    for(int i = 0; i < 64*32; i++) {
      if(frameBuffer[i] > 0) {
        g.setColor(Color.WHITE);
      } else {
        g.setColor(Color.BLACK);
      }
      g.fillRect((SCALE * (i % 64)), (SCALE * (i / 64)) , SCALE, SCALE);
    }
  }

  public void updateScreen(long[] frameBuffer) {
    this.frameBuffer = frameBuffer.clone();
    this.repaint();
  }

  public boolean[] getStaticKeys() {
    return keyboardListener.getStaticKeys();
  }




}
