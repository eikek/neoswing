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

package org.eknet.neoswing.view;

import org.eknet.neoswing.ComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 16.11.12 11:39
 */
public class LaFComboBox implements ActionListener {
  private static UIManager.LookAndFeelInfo[] allLaF = UIManager.getInstalledLookAndFeels();
  private static final Logger log = LoggerFactory.getLogger(LaFComboBox.class);

  private final ComponentFactory factory;

  private final JComboBox box;
  public LaFComboBox(ComponentFactory factory) {
    this.factory = factory;
    this.box = factory.createComboBox();
    this.box.setModel(new LafModel());
    this.box.setRenderer(new Renderer());
    this.box.addActionListener(this);
  }

  public JComboBox getComponent() {
    return box;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    UIManager.LookAndFeelInfo selected = getSelected();
    if (selected != null) {
      try {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        UIManager.setLookAndFeel(selected.getClassName());
        factory.updateLookAndFeel(selected);
        log.info("Setting look and feel to: {}", selected.getClassName());
      } catch (Exception e) {
        log.warn("Cannot set look-and-feel '" + selected.getClassName() + "'!", e);
      }
    }
  }

  public UIManager.LookAndFeelInfo getSelected() {
    return (UIManager.LookAndFeelInfo) box.getSelectedItem();
  }

  public void setSelected(UIManager.LookAndFeelInfo info) {
    this.box.setSelectedItem(info);
  }

  static class LafModel extends DefaultComboBoxModel {
    private UIManager.LookAndFeelInfo selected;
    @Override
    public void setSelectedItem(Object anItem) {
      this.selected = (UIManager.LookAndFeelInfo) anItem;
    }
    @Override
    public Object getSelectedItem() {
      return selected;
    }
    @Override
    public int getSize() {
      return allLaF.length;
    }
    @Override
    public Object getElementAt(int index) {
      return allLaF[index];
    }
  }

  static class Renderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (value == null) {
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      } else {
        return super.getListCellRendererComponent(list, ((UIManager.LookAndFeelInfo) value).getName(), index, isSelected, cellHasFocus);
      }
    }
  }
}
