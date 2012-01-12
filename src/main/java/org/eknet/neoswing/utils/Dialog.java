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

import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.NeoSwing;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.prefs.Preferences;

import static java.awt.Dialog.*;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 19:21
 */
public class Dialog {
  
  private String title;
  private Component content;

  public enum Option { OK, CANCEL }

  private final static Preferences prefs = Preferences.userNodeForPackage(NeoSwing.class);

  private ActionListener okActionListener;
  private ActionListener cancelActionListener;

  private final ComponentFactory componentFactory;

  private boolean showCancelOption = true;
  private boolean showOkOption = true;
  private Image icon;
  
  private JDialog jdialog;
  private DefaultButtons buttons;
  private List<Option> results;
  
  public Dialog(String title) {
    this.title = title;
    this.componentFactory = NeoSwingUtil.getFactory(true);
    this.buttons = new DefaultButtons(this.componentFactory);
  }

  public void setContent(Component content) {
    this.content = content;
    if (this.content != null) {
      if (content instanceof JComponent) {
        JComponent comp = (JComponent) content;
        Border border = comp.getBorder();
        if (border != null) {
          Border dialogBorder = BorderFactory.createCompoundBorder(
                  BorderFactory.createEmptyBorder(5, 5, 5, 5),
                  border
          );
          comp.setBorder(dialogBorder);
        } else {
          comp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        } 
      }
    }
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ActionListener getOkActionListener() {
    return okActionListener;
  }

  public void setOkActionListener(ActionListener okActionListener) {
    this.okActionListener = okActionListener;
  }

  public ActionListener getCancelActionListener() {
    return cancelActionListener;
  }

  public void setCancelActionListener(ActionListener cancelActionListener) {
    this.cancelActionListener = cancelActionListener;
  }

  public boolean isShowCancelOption() {
    return showCancelOption;
  }

  public void setShowCancelOption(boolean showCancelOption) {
    this.showCancelOption = showCancelOption;
  }

  public boolean isShowOkOption() {
    return showOkOption;
  }

  public void setShowOkOption(boolean showOkOption) {
    this.showOkOption = showOkOption;
  }

  public Image getIcon() {
    return icon;
  }

  public void setIcon(Image icon) {
    this.icon = icon;
  }

  public Option show() {
    return this.show(null, ModalityType.APPLICATION_MODAL);
  }

  public JDialog getJdialog() {
    return jdialog;
  }

  public Buttons getButtons() {
    return buttons;
  }

  private void create(Window owner, ModalityType modalityType) {
    JDialog dialog = new InnerDialog(owner, title, modalityType);
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    if (icon != null) {
      dialog.setIconImage(icon);
    }

    if (content != null) {
      panel.add(content, BorderLayout.CENTER);
    }
    List<Option> result = new ArrayList<Option>();
    result.add(Option.CANCEL);
    panel.add(buttons, BorderLayout.SOUTH);
    JButton okButton = buttons.getButton(Option.OK);
    if (okButton != null) {
      if (okActionListener != null) {
        okButton.addActionListener(okActionListener);
      }
      okButton.addActionListener(new DefaultAction(result, dialog));
      okButton.setVisible(isShowOkOption());
    }
    JButton cancelButton = buttons.getButton(Option.CANCEL);
    if (cancelButton != null) {
      if (cancelActionListener != null) {
        cancelButton.addActionListener(cancelActionListener);
      }
      cancelButton.addActionListener(new DefaultAction(result, dialog));
      cancelButton.setVisible(isShowCancelOption());
    }

    dialog.pack();
    dialog.setMinimumSize(new Dimension(350, 180));
    if (owner != null) {
      NeoSwingUtil.center(dialog, owner);
    }
    WindowUtil.bindToPrefs(dialog, prefs, "dialog." + title + ".%s");
    this.jdialog = dialog;
    this.results = result;
  }
  
  public Option show(Window owner, ModalityType modalityType) {
    if (results != null) {
      results.clear();
      results.add(Option.CANCEL);
    }
    if (jdialog == null) {
      create(owner, modalityType);
    }
    jdialog.setVisible(true);
    if (!results.isEmpty()) {
      return results.get(results.size() - 1);
    } else {
      return null;
    }
  }

  private static final class DefaultAction extends AbstractAction {
    private final Collection<Option> result;
    private JDialog dialog;

    private DefaultAction(Collection<Option> result, JDialog dialog) {
      this.result = result;
      this.dialog = dialog;
    }

    private DefaultAction(JDialog dialog) {
      this.result = new ArrayList<Option>();
      this.dialog = dialog;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      Component c = (Component) e.getSource();
      if (c.getName() != null) {
        Option option = Option.valueOf(c.getName());
        this.result.add(option);
      }
      this.dialog.setVisible(false);
      this.dialog.dispose();
    }
  }

  private static class InnerDialog extends JDialog {

    private InnerDialog(Window owner, String title, ModalityType modalityType) {
      super(owner, title, modalityType);
    }

    @Override
    protected JRootPane createRootPane() {
      JRootPane rootPane = super.createRootPane();
      KeyStroke escStroke = KeyStroke.getKeyStroke("ESCAPE");
      InputMap map = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

      map.put(escStroke, "ESCAPE");
      rootPane.getActionMap().put("ESCAPE", new DefaultAction(this));
      return rootPane;
    }
  }

  public static interface Buttons {

    JButton getButton(Option option);

  }
  
  private static class DefaultButtons extends JPanel implements Buttons {

    private JButton okButton;
    private JButton cancelButton;
    
    private DefaultButtons(ComponentFactory componentFactory) {
      BoxLayout boxl = new BoxLayout(this, BoxLayout.X_AXIS);
      setLayout(boxl);
      add(Box.createHorizontalGlue());
      okButton = componentFactory.createButton();
      okButton.setName(Option.OK.name());
      okButton.setText("Ok");
      okButton.setIcon(NeoSwingUtil.icon("accept"));
      add(okButton);

      add(Box.createHorizontalStrut(8));

      cancelButton = componentFactory.createButton();
      cancelButton.setName(Option.CANCEL.name());
      cancelButton.setText("Cancel");
      cancelButton.setIcon(NeoSwingUtil.icon("cancel"));
      add(cancelButton);

      setBorder(BorderFactory.createCompoundBorder(NeoSwingUtil.topEtchBorder(),
          BorderFactory.createEmptyBorder(7, 7, 7, 7)));
    }

    @Override
    public JButton getButton(Option option) {
      if (option == Option.OK) {
        return okButton;
      }
      if (option == Option.CANCEL) {
        return cancelButton;
      }
      return null;
    }
  }
}
