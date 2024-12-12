import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Chip8Interface extends JPanel {
  private KeyboardListener keyboardListener;
  private long[] frameBuffer;

  public Chip8Interface() {
    super();
    frameBuffer = new long[64 * 32];
    keyboardListener = new KeyboardListener();
  }

  public KeyboardListener getInputListener() {
    return keyboardListener;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // Set the color for the pixels
//    g.setColor(Color.RED);
//    // Draw some pixels
//    g.drawLine(50, 50, 50, 50); // Draw a single pixel at (50, 50)
//    g.drawLine(60, 60, 60, 60); // Draw a single pixel at (60, 60)
//    g.drawLine(70, 70, 70, 70); // Draw a single pixel at (70, 70)
//    g.fillRect(50,50,10,10);
//    g.setColor(Color.BLUE);
//    g.fillRect(60,50,10,10);
//    for(int i = 0; i < 64; i++) {
//      g.setColor(Color.BLACK);
//      g.fillRect(5 * (i+1),20,5,5);
//    }

    g.setColor(Color.BLACK);
    boolean alternate;
//    for(int i = 0; i < 32; i++) {
//      alternate = i % 2 == 0;
//      for(int j = 0; j < 64; j++) {
//        g.fillRect(20 + (10*j), 20 + (10*i), 10, 10 );
//        if(alternate) {
//          g.setColor(Color.WHITE);
//        } else {
//          g.setColor(Color.BLACK);
//        }
//        alternate = !alternate;
//      }
//      if(alternate) {
//        g.setColor(Color.WHITE);
//      } else {
//        g.setColor(Color.BLACK);
//      }
//    }

//    boolean alt = true;
//    for(int i = 0; i < 64*32; i++) {
//      if(i % 64 == 0) {
//        alt = !alt;
//      }
//      if(alt) {
//        g.setColor(Color.WHITE);
//      } else {
//        g.setColor(Color.BLACK);
//      }
//      alt = !alt;
//      g.fillRect(20 + (10 * (i % 64)), 20 + (10 * (i / 64)) , 10, 10);
//    }
    for(int i = 0; i < 64*32; i++) {
      if(frameBuffer[i] > 0) {
        g.setColor(Color.WHITE);
      } else {
        g.setColor(Color.BLACK);
      }
      g.fillRect(20 + (10 * (i % 64)), 20 + (10 * (i / 64)) , 10, 10);
    }
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Pixel Drawing");
    Chip8Interface panel = new Chip8Interface();
    frame.addKeyListener(panel.keyboardListener);
    frame.add(panel);
    frame.setSize(720, 480);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);

    new Scanner(System.in).next();
    panel.repaint();
  }

  public void updateScreen(long[] frameBuffer) {
    this.frameBuffer = frameBuffer.clone();
    this.repaint();
  }

  public boolean[] getStaticKeys() {
    return keyboardListener.getStaticKeys();
  }




}
