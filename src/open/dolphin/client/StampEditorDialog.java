/*
 * StampEditorDialog.java        1.0 2001/3/1
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.client;

import javax.swing.*;

import open.dolphin.exception.*;
import open.dolphin.util.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

/**
 * Stamp 編集用の外枠を提供する Dialog.
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class StampEditorDialog extends JDialog 
implements PropertyChangeListener, Runnable {
          
    /** button text */
    private static final String OK_TEXT          = "カルテに展開(O)";
    private String okButtonText = OK_TEXT;
    private static final String CANCEL_TEXT      = 
        (String)UIManager.get("OptionPane.cancelButtonText") + "(C)";
    
    /** command buttons */
    private final JButton okButton = new JButton(okButtonText);
    private final JButton cancelButton = new JButton(CANCEL_TEXT);
    
    /** target editor */
    private IStampModelEditor editor;
    private PropertyChangeSupport boundSupport;
    private Object value;
    
    /**
     * Constructor. Use layered inititialization pattern.
     */
    public StampEditorDialog(String category) throws DolphinException {
        
        super((Frame)null, true);     // create a modal dialog
        
        try {
        	// 実際の（中味となる）エディタを生成して Dialog に add する
            editor = (IStampModelEditor)StampEditorFactory.create(category);
            
        } catch (Exception e) {
        	String error = "Problems creating stamp editor: " + e.toString();
            System.out.println(error);
            e.printStackTrace();
            throw new DolphinException(error);
        }
            
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

        panel.add((Component)editor);
        panel.add(Box.createVerticalStrut(17));  // Adds 17 pixels spacing
        panel.add(createButtonPane(this));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));

        getContentPane().add(panel, BorderLayout.CENTER);

        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
            	// CloseBox がクリックされた場合はキャンセルとする
                value = null;
                notifyValue();
                close();
            }
        });

        editor.addPropertyChangeListener("validData", this);
        boundSupport = new PropertyChangeSupport(this);
        setTitle(editor.getTitle());
    }
    
    /**
     * このエディタは Thread として実行される。
     */
    public void run() {
    	
    	// TODO pack 後にコンポーネントへアクセスしている
        pack();
        Point loc = DesignFactory.getCenterLoc(getWidth(), getHeight());
        setLocation(loc.x, loc.y);
        
        // Modal state にする
        show();
        
        // Block が解除されたら値を通知する
        notifyValue();
    }    
    
    /**
     * 編集した Stamp を返す。
     */    
    public Object getValue() {
        return editor.getValue();
    }
    
    /**
     * 編集するスタンプをセットする
     */
    public void setValue(Object val) {
        editor.setValue(val);
    }
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void remopvePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }    
    
    public String getOkButtonText() {
        return okButtonText;
    }
    
    public void setOkButtonText(String text) {
        okButtonText = text + "(O)";
        okButton.setText(okButtonText);
        okButton.setMnemonic('O');
    }
        
    public void okButtonClicked(ActionEvent e) {
        value = getValue();
        close();
    }
    
    public void cancelButtonClicked(ActionEvent e) {
        value = null;
        close();
    }
    
    public void addStampButtonClicked(ActionEvent e) {
        value = null;
        close();
    }    
    
    private void notifyValue() {
        boundSupport.firePropertyChange("value", null, value);
    }
    
    /**
     * 編集中のモデル値が有効な値かどうかの通知を受け、
     * カルテに展開ボタンを enable/disable にする
     */
    public void propertyChange(PropertyChangeEvent evt) {
     
        Boolean i = (Boolean)evt.getNewValue();
        boolean state = i.booleanValue();
        
        if (state) {
            okButton.setEnabled(true);
        }
        else {
            okButton.setEnabled(false);
        }
    }
      
     /**
      * ダイアログを閉じる
      */
     public void close() {
         editor.dispose();
         setVisible(false);
         dispose();
     }
   
     /**
      * キャンセル・カルテに展開ボタン　のペインを生成する。
      */
     private JPanel createButtonPane(Object target) {
         
         JPanel buttonPane = new JPanel();
         buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
         buttonPane.add(Box.createHorizontalGlue());
         
         // OK ボタンとそのアクションを生成
         ActionListener action = (ActionListener) (GenericListener.create (
                                    ActionListener.class,
                                    "actionPerformed",
                                    target,
                                    "okButtonClicked"));
         okButton.addActionListener(action);
         okButton.setMnemonic('O');
         okButton.setEnabled(false);
         buttonPane.add(okButton);
      
         // Adds 5 spacing (see the design guide)
         buttonPane.add(DesignFactory.createtButtonHSpace());
      
         // Cancel button　とそのアクションを生成
         action = (ActionListener) (GenericListener.create (
                                    ActionListener.class,
                                    "actionPerformed",
                                    target,
                                    "cancelButtonClicked"));
         cancelButton.addActionListener(action);
         cancelButton.setMnemonic('C');
         buttonPane.add(cancelButton);
      
         return buttonPane;     
    }
}