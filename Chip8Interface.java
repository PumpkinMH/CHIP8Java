import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Chip8Interface extends JPanel {
  private KeyboardListener keyboardListener;
  private long[] frameBuffer;
//  private final int SCALE;

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
    return new Dimension(640,320);
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
      g.fillRect((10 * (i % 64)), (10 * (i / 64)) , 10, 10);
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
