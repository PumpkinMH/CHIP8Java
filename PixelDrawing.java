import javax.swing.*;
import java.awt.*;

public class PixelDrawing extends JPanel {

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // Set the color for the pixels
    g.setColor(Color.RED);
    // Draw some pixels
    g.drawLine(50, 50, 50, 50); // Draw a single pixel at (50, 50)
    g.drawLine(60, 60, 60, 60); // Draw a single pixel at (60, 60)
    g.drawLine(70, 70, 70, 70); // Draw a single pixel at (70, 70)
    g.fillRect(50,50,10,10);
    g.setColor(Color.BLUE);
    g.fillRect(60,50,10,10);
    for(int i = 0; i < 64; i++) {
      g.setColor(Color.BLACK);
      g.fillRect(5 * (i+1),20,5,5);
    }
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Pixel Drawing");
    PixelDrawing panel = new PixelDrawing();
    frame.add(panel);
    frame.setSize(200, 200);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
