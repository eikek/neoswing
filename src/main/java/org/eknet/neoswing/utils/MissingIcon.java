/*
 * Copyright (c) 2012 Eike Kettner
 *
 * This file is part of NeoSwing.
 *
 * NeoSwing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NeoSwing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NeoSwing.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eknet.neoswing.utils;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
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
