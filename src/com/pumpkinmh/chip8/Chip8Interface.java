package com.pumpkinmh.chip8;

import javax.swing.*;
import java.awt.*;

public class Chip8Interface extends JPanel {
  private final KeyboardListener keyboardListener;
  private long[] frameBuffer;
  private final int SCALE;

  public Chip8Interface() {
    super();
    SCALE = 10;
    frameBuffer = new long[64 * 32];
    keyboardListener = new KeyboardListener();
  }

  public Chip8Interface(int scale) {
    super();
    SCALE = scale;
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
