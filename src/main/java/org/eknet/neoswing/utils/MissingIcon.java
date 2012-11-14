/*
 * Copyright 2012 Eike Kettner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eknet.neoswing.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 29.10.11 13:30
 */
public class MissingIcon implements Icon {

  public static final MissingIcon tiny = new MissingIcon(16);
  public static final MissingIcon small = new MissingIcon(32);
  public static final MissingIcon large = new MissingIcon(64);

  private final int size;

  private final BufferedImage image;

  public MissingIcon(int size) {
    this.size = size;

    Dimension dim = new Dimension(size, size);
    this.image = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics g = image.createGraphics();

    Color back = Color.white;
    g.setColor(back);
    g.fillRect(0, 0, getIconWidth(), getIconHeight());

    Color front = Color.black;
    g.setColor(front);
    g.drawRect(0, 0, getIconWidth(), getIconHeight());

    g.setColor(Color.red);
    g.drawLine(5, 5, getIconWidth() - 5, getIconHeight() - 5);
    g.drawLine(5, getIconHeight() - 5, getIconWidth() - 5, 5);
  }

  public MissingIcon() {
    this(16);
  }

  public static MissingIcon ofSize(int size) {
    return new MissingIcon(size);
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    g.drawImage(image, x, y, c);
  }

  public int getSize() {
    return size;
  }

  public BufferedImage getImage() {
    return image;
  }

  @Override
  public int getIconWidth() {
    return size;
  }

  @Override
  public int getIconHeight() {
    return size;
  }

  public static void main(String[] args) {
    JLabel label = new JLabel(MissingIcon.small);
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(label);
    frame.pack();
    frame.setVisible(true);

  }
}
