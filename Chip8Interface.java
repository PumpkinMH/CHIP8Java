import java.awt.image.*;
import javax.swing.*;
import java.awt.*;

public class Chip8Interface extends JPanel {
  private final KeyboardListener keyboardListener;
  private int[] frameBuffer;
  private final int SCALE;
  private final BufferedImage pixelImage;

  public Chip8Interface() {
    super();
    SCALE = 10;
    frameBuffer = new int[64 * 32];
    keyboardListener = new KeyboardListener();
    pixelImage = new BufferedImage(64, 32, BufferedImage.TYPE_BYTE_BINARY);
  }

  public Chip8Interface(int scale) {
    super();
    SCALE = scale;
    frameBuffer = new int[64 * 32];
    keyboardListener = new KeyboardListener();
    pixelImage = new BufferedImage(64, 32, BufferedImage.TYPE_BYTE_BINARY);
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
    g.drawImage(pixelImage.getScaledInstance(64 * SCALE, 32 * SCALE, Image.SCALE_FAST),0,0,null);

//    for(int i = 0; i < 64*32; i++) {
//      if(frameBuffer[i] > 0) {
//        g.setColor(Color.WHITE);
//      } else {
//        g.setColor(Color.BLACK);
//      }
//      g.fillRect((SCALE * (i % 64)), (SCALE * (i / 64)) , SCALE, SCALE);
//    }
  }

  public void updateScreen(int[] frameBuffer) {
    this.frameBuffer = frameBuffer.clone();
    WritableRaster smallRaster = pixelImage.getRaster();
    smallRaster.setPixels(0,0,64,32,frameBuffer);
    this.repaint();
  }

  public boolean[] getStaticKeys() {
    return keyboardListener.getStaticKeys();
  }
}
