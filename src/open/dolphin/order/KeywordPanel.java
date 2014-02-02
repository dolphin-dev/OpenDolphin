/*
 * KeywordPanel.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.order;

import javax.swing.*;
import javax.swing.event.*;

import open.dolphin.client.*;

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.*;
import java.beans.*;
import java.awt.im.InputSubset;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc. 
 */
public final class KeywordPanel extends JPanel {
    
    private static final int KEYWORD_WIDTH  = 250;
    private static final int KEYWORD_HEIGHT = 21;

    private String keyword;
    private JLabel keywordLabel = new JLabel();
    private String borderTitle;
    private JTextField keywordField;
    private JButton searchButton;
    private JRadioButton startsWith;
    private JRadioButton contains;
    private PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);

    /** Creates new KeywordPanel */
    public KeywordPanel() {
        
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        // キーワードフィールド
        keywordField = new JTextField();
        Dimension d = new Dimension(KEYWORD_WIDTH, KEYWORD_HEIGHT);
        keywordField.setPreferredSize(d);
        keywordField.setMaximumSize(d);
        keywordField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchButton.doClick();
            }
        });
        keywordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
               keywordField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
            }
            public void focusLosted(FocusEvent event) {
               keywordField.getInputContext().setCharacterSubsets(null);
            }
        });

        keywordField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                boolean b = (! keywordField.getText().equals("")) ? true : false;
                searchButton.setEnabled(b);
            }

            public void removeUpdate(DocumentEvent e) {
                boolean b = (! keywordField.getText().equals("")) ? true : false;
                searchButton.setEnabled(b);     
            }  

            public void changedUpdate(DocumentEvent e) {
            } 
        });
        
        // 検索パターン
        startsWith = new JRadioButton("前方一致");
        contains = new JRadioButton("部分一致");
        ButtonGroup bg = new ButtonGroup();
        bg.add(startsWith);
        bg.add(contains);
        Preferences pref = ClientContext.getPreferences();
        boolean bStartsWith = pref.getBoolean("keyword.startsWith", false);
        startsWith.setSelected(bStartsWith);
        contains.setSelected(! bStartsWith);
        ActionListener al = new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                boolean b = startsWith.isSelected();
                Preferences pref = ClientContext.getPreferences();
                pref.putBoolean("keyword.startsWith", b);
            }
        };
        startsWith.addActionListener(al);
        contains.addActionListener(al);
        
        // 検索ボタン
        searchButton = new JButton("(F)", ClientContext.getImageIcon("Zoom24.gif"));
        searchButton.setEnabled(false);
        searchButton.setMnemonic('F');
        searchButton.addActionListener(new ActionListener() {

            // ボタンクリックでマスタ検索を行う
            public void actionPerformed(ActionEvent e) {

                // キワード文字を取得する
                String text = keywordField.getText().trim();
                if (text.equals("")) {
                    return;
                }

                setKeyword(text);
            }
        });

        this.add(keywordLabel);
        this.add(Box.createRigidArea(new Dimension(7,0)));
        this.add(keywordField);
        this.add(Box.createRigidArea(new Dimension(11,0)));
        this.add(startsWith);
        this.add(Box.createRigidArea(new Dimension(5,0)));
        this.add(contains);
        this.add(Box.createRigidArea(new Dimension(11,0)));
        this.add(searchButton);
        this.add(Box.createHorizontalGlue());
    }
    
    /*public boolean isStartsWith() {
        return bStartsWith;
    }
    
    public void setStartsWith(boolean b) {
        boolean old = bStartsWith;
        bStartsWith = b;
        boundSupport.firePropertyChange("startsWithProp", new Boolean(old), new Boolean(bStartsWith));
    }*/
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String key) {
        String old = keyword;
        keyword = key;
        Object[] o = new Object[2];
        o[0] = new Boolean(startsWith.isSelected());
        o[1] = keyword;
        boundSupport.firePropertyChange("keywordProp", null, o);
    }

    public String getLabelText() {
        return keywordLabel.getText();
    }

    public void setLabelText(String val) {
        keywordLabel.setText(val + " :");
    }

    public String getBorderTitle() {
        return borderTitle;
    }

    public void setBorderTitle(String val) {
        if (val != null) {
            this.setBorder(BorderFactory.createTitledBorder(val));
        }
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }    
}