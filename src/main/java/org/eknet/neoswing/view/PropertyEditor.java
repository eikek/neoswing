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

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.NeoSwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A panel that allows to edit or add a property of a {@link Element}.
 * <p/>
 * If a non-null key is supplied, the "edit" mode is uses. If the supplied key is null
 * this panel goes in "add" mode. The difference is, that in edit-mode the key is
 * set readonly. Also, when in "add" mode a value is required while in edit mode an
 * empty value is removing the property.
 * <p/>
 * A call to {@link #commit()} opens a neo4j transaction, sets the value from
 * the ui into the {@link Element} and commits a transaction.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 20:37
 */
public class PropertyEditor extends JPanel {

  public static final String VALID_VALUE = "validValue";
  public static final String PROPERTY_VALUE = "propertyValue";

  private final ComponentFactory componentFactory;
  private final GraphDb database;

  private Element element;

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

  
  public PropertyEditor(GraphDb database, Element element, String key, ComponentFactory factory) {
    this.componentFactory = factory;
    this.database = database;
    initComponents(key);
    setPropertyContainer(element, key);
  }

  public PropertyEditor(GraphDb database, Element element, String key) {
    this(database, element, key, NeoSwingUtil.getFactory(true));
  }

  public PropertyEditor(GraphDb database, ComponentFactory factory) {
    this(database, null, null, factory);
  }

  public PropertyEditor(GraphDb database) {
    this(database, null, null, NeoSwingUtil.getFactory(true));
  }

  /**
   * Creates a dialog containing this editor for the specified element.
   *
   * @param element
   * @param key
   * @return
   */
  public static Dialog inDialog(GraphDb db, Element element, String key) {
    final Dialog dialog = new Dialog("Edit Property");
    final PropertyEditor editor = new PropertyEditor(db, element, key);
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

  public void setPropertyContainer(Element container, final String key) {
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
    GraphDb.Tx tx = database.beginTx();
    try {
      String key = getKey();
      if (key == null) {
        throw new IllegalStateException("Key is required");
      }
      Object old = null;
      if (element.getProperty(key) != null) {
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
  
  private void initComponents(String key) {
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
      infoLabel.setText(element instanceof Vertex ?
          "Edit Property of Node " + NeoSwingUtil.getId(element) :
          "Edit Property of Relationship " + NeoSwingUtil.getId(element) + " / " + ((Edge) element).getLabel());
      if (key != null) {
        if (element.getProperty(key) != null) {
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
      public Object parse(String value) {
        return value;
      }
    },
    Long {
      @Override
      public Class<?> getTypeClass() {
        return Long.class;
      }

      @Override
      public Object parse(String value) {
        return java.lang.Long.parseLong(value);
      }
    },
    Byte {
      @Override
      public Class<?> getTypeClass() {
        return Byte.class;
      }

      @Override
      public Object parse(String value) {
        return java.lang.Byte.parseByte(value);
      }
    },
    Short {
      @Override
      public Class<?> getTypeClass() {
        return Short.class;
      }

      @Override
      public Object parse(String value) {
        return java.lang.Short.parseShort(value);
      }
    },
    Boolean {
      @Override
      public Class<?> getTypeClass() {
        return Boolean.class;
      }

      @Override
      public Object parse(String value) {
        return java.lang.Boolean.parseBoolean(value);
      }
    },
    Float {
      @Override
      public Class<?> getTypeClass() {
        return Float.class;
      }

      @Override
      public Object parse(String value) {
        return java.lang.Float.parseFloat(value);
      }
    },
    Double {
      @Override
      public Class<?> getTypeClass() {
        return Double.class;
      }

      @Override
      public Object parse(String value) {
        return java.lang.Double.parseDouble(value);
      }
    },
    Integer {
      @Override
      public Class<?> getTypeClass() {
        return Integer.class;
      }

      @Override
      public Object parse(String value) {
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

    public abstract Object parse(String value);
  }
}
