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

package org.eknet.neoswing.view;

import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A panel that allows to edit or add a property of a {@link PropertyContainer}.
 * <p/>
 * If a non-null key is supplied, the "edit" mode is uses. If the supplied key is null
 * this panel goes in "add" mode. The difference is, that in edit-mode the key is
 * set readonly. Also, when in "add" mode a value is required while in edit mode an
 * empty value is removing the property.
 * <p/>
 * A call to {@link #commit()} opens a neo4j transaction, sets the value from
 * the ui into the {@link PropertyContainer} and commits a transaction.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 20:37
 */
public class PropertyEditor extends JPanel {

  public static final String VALID_VALUE = "validValue";
  public static final String PROPERTY_VALUE = "propertyValue";

  private final ComponentFactory componentFactory;
  
  private PropertyContainer element;

  private Border normalBorder;
  private Border errorBorder = BorderFactory.createLineBorder(Color.RED);
  
  private JLabel infoLabel;
  private JTextField keyValue;
  private JTextField valueField;
  private JComboBox typeSelector;

  private boolean valueValid = true;

  private final ActionListener validateValueActionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      validateForm();
    }
  };
  private final DocumentListener validateValueDocumentListener = new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
      validateForm();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      validateForm();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      validateForm();
    }
  };

  
  public PropertyEditor(@Nullable PropertyContainer element, @Nullable String key, @NotNull ComponentFactory factory) {
    this.componentFactory = factory;
    initComponents(key);
    setPropertyContainer(element, key);
  }

  public PropertyEditor(@Nullable PropertyContainer element, @Nullable String key) {
    this(element, key, NeoSwingUtil.getFactory(true));
  }

  public PropertyEditor(@NotNull ComponentFactory factory) {
    this(null, null, factory);
  }

  public PropertyEditor() {
    this(null, null, NeoSwingUtil.getFactory(true));
  }

  public static Dialog inDialog(@NotNull PropertyContainer element, @Nullable String key) {
    final Dialog dialog = new Dialog("Edit Property");
    final PropertyEditor editor = new PropertyEditor(element, key);
    editor.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(VALID_VALUE)) {
          Boolean valid = (Boolean) evt.getNewValue();
          dialog.getButtons().getButton(Dialog.Option.OK).setEnabled(valid != null && valid);
        }
      }
    });
    dialog.setContent(editor);
    dialog.setOkActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        editor.commit();
      }
    });
    editor.validateForm();
    return dialog;
  }

  public void setPropertyContainer(@Nullable PropertyContainer container, @Nullable final String key) {
    this.element = container;
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        updateComponents(key);
        validateKey();
        validateValue();
      }
    });
  }

  private boolean isAdd() {
    return keyValue.isEnabled();
  }
  
  private boolean isUpdate() {
    return !isAdd();
  }

  public String getKey() {
    String key = keyValue.getText();
    if (key == null || key.trim().isEmpty()) {
      return null;
    }
    return key;
  }

  public void commit() {
    if (this.element == null) {
      throw new IllegalStateException("No property container set");
    }
    Transaction tx = element.getGraphDatabase().beginTx();
    try {
      String key = getKey();
      if (key == null) {
        throw new IllegalStateException("Key is required");
      }
      Object old = null;
      if (element.hasProperty(key)) {
        old = element.getProperty(key);
      }
      Object value = readValue();
      if (value == null) {
        element.removeProperty(key);
      } else {
        element.setProperty(key, value);
      }
      tx.success();
      firePropertyChange(PROPERTY_VALUE, old, value);
    } finally {
      tx.finish();
    }
  }

  protected void validateForm() {
    boolean result = validateKey() && validateValue();
    firePropertyChange(VALID_VALUE, this.valueValid, result);
    this.valueValid = result;
  }

  public boolean validateKey() {
    // key required
    String key = getKey();
    if (key == null) {
      this.keyValue.setBorder(errorBorder);
      return false;
    } else {
      this.keyValue.setBorder(normalBorder);
      return true;
    }
  }

  public  boolean validateValue() {
    try {
      // value
      Object value = readValue();
      if (isAdd() && value == null) {
        throw new IllegalArgumentException("Value required");
      }
      valueField.setBorder(normalBorder);
      return true;
    } catch (IllegalArgumentException e1) {
      valueField.setBorder(errorBorder);
      return false;
    }
  }

  /**
   * Bound property that indicates if the value is valid according to
   * the selected type.
   * 
   * @return
   */
  public boolean isValueValid() {
    return valueValid;
  }

  private Object readValue() throws IllegalArgumentException {
    String value = valueField.getText();
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    PropertyType pt = (PropertyType) typeSelector.getSelectedItem();
    try {
      return pt.parse(value);
    } catch (Exception e) {
      if (e instanceof IllegalArgumentException) {
        throw (IllegalArgumentException) e;
      }
      throw new IllegalArgumentException(e);
    }
  }
  
  private void initComponents(@Nullable String key) {
    GridBagLayout gbl = new GridBagLayout();
    setLayout(gbl);
    GridBagConstraints gbc = new GridBagConstraints();

    Insets insets = new Insets(5, 5, 5, 5);
    
    infoLabel = componentFactory.createLabel();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    gbc.insets = insets;
    add(infoLabel, gbc);

    //key
    JLabel keyLabel = componentFactory.createLabel();
    keyLabel.setText("Key");
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 0.2;
    gbc.insets = insets;
    add(keyLabel, gbc);

    keyValue = componentFactory.createTextField();
    keyValue.getDocument().addDocumentListener(validateValueDocumentListener);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 0.8;
    gbc.insets = insets;
    add(keyValue, gbc);
    
    //type
    JLabel typeLabel = componentFactory.createLabel();
    typeLabel.setText("Type");
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 0.2;
    gbc.insets = insets;
    add(typeLabel, gbc);

    typeSelector = componentFactory.createComboBox(PropertyType.values(), true, false);
    typeSelector.addActionListener(validateValueActionListener);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 0.8;
    gbc.insets = insets;
    add(typeSelector, gbc);
    
    //property
    JLabel valueLabel = componentFactory.createLabel();
    valueLabel.setText("Value");
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 0.2;
    gbc.insets = insets;
    add(valueLabel, gbc);

    valueField = componentFactory.createTextField();
    valueField.getDocument().addDocumentListener(this.validateValueDocumentListener);
    normalBorder = valueField.getBorder();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 0.8;
    gbc.insets = insets;
    add(valueField, gbc);

    updateComponents(key);
  }
  
  private void updateComponents(String key) {
    keyValue.setText(key);
    keyValue.setEnabled(key == null);
    if (element == null) {
      infoLabel.setText(null);
      valueField.setText(null);
    } else {
      infoLabel.setText(element instanceof Node ?
          "Edit Property of Node " + NeoSwingUtil.getId(element) :
          "Edit Property of Relationship " + NeoSwingUtil.getId(element) + " / " + ((Relationship) element).getType().name());
      if (key != null) {
        if (element.hasProperty(key)) {
          Object value = element.getProperty(key);
          valueField.setText(value.toString());
          PropertyType pt = PropertyType.forClass(value.getClass());
          typeSelector.setSelectedItem(pt);
        }
      }
    }
  }
  
  public enum PropertyType {
    
    String {
      @Override
      public Class<?> getTypeClass() {
        return String.class;
      }

      @Override
      public Object parse(@NotNull String value) {
        return value;
      }
    },
    Long {
      @Override
      public Class<?> getTypeClass() {
        return Long.class;
      }

      @Override
      public Object parse(@NotNull String value) {
        return java.lang.Long.parseLong(value);
      }
    },
    Byte {
      @Override
      public Class<?> getTypeClass() {
        return Byte.class;
      }

      @Override
      public Object parse(@NotNull String value) {
        return java.lang.Byte.parseByte(value);
      }
    },
    Short {
      @Override
      public Class<?> getTypeClass() {
        return Short.class;
      }

      @Override
      public Object parse(@NotNull String value) {
        return java.lang.Short.parseShort(value);
      }
    },
    Boolean {
      @Override
      public Class<?> getTypeClass() {
        return Boolean.class;
      }

      @Override
      public Object parse(@NotNull String value) {
        return java.lang.Boolean.parseBoolean(value);
      }
    },
    Float {
      @Override
      public Class<?> getTypeClass() {
        return Float.class;
      }

      @Override
      public Object parse(@NotNull String value) {
        return java.lang.Float.parseFloat(value);
      }
    },
    Double {
      @Override
      public Class<?> getTypeClass() {
        return Double.class;
      }

      @Override
      public Object parse(@NotNull String value) {
        return java.lang.Double.parseDouble(value);
      }
    },
    Integer {
      @Override
      public Class<?> getTypeClass() {
        return Integer.class;
      }

      @Override
      public Object parse(@NotNull String value) {
        return java.lang.Integer.parseInt(value);
      }
    };
    
    
    public abstract Class<?> getTypeClass();
    
    public static PropertyType forClass(Class<?> type) {
      for (PropertyType pt : values()) {
        if (pt.getTypeClass() == type) {
          return pt;
        }
      }
      throw new IllegalArgumentException("No property type for " + type);
    }

    public abstract Object parse(@NotNull String value);
  }
}
