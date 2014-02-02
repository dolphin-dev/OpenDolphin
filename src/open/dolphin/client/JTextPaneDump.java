/*
 * JTextPaneDump.java 
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 *
 * @author  kazm
 */
public class JTextPaneDump extends Panel implements ActionListener {
       
    JTextPane textPane = new JTextPane();
    JButton stamp = new JButton("Stamp");
    JButton dump = new JButton("Dump");
    JButton restore = new JButton("ReStore");
    String dumped;
    Position topPos;
    
    /** Creates a new instance of JTextPaneDump */
    public JTextPaneDump() {
        
        JMenuBar menubar = createMenubar();
	
        stamp.addActionListener(this);
        dump.addActionListener(this);
        restore.addActionListener(this);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalGlue());
        p.add(stamp);
        p.add(Box.createHorizontalStrut(5));
        p.add(dump);
        p.add(Box.createHorizontalStrut(5));
        p.add(restore);
        
        this.setLayout(new BorderLayout());
        this.add(menubar, BorderLayout.NORTH);
        this.add(textPane, BorderLayout.CENTER);
        this.add(p, BorderLayout.SOUTH);
        
        DefaultStyledDocument doc = (DefaultStyledDocument)textPane.getDocument();
        
        // TimeStamp 用の Style を登録する
        Style style = doc.addStyle("timeStamp", null);
        StyleConstants.setForeground(style, new Color(255,0,255));
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
        
        // カルテの先頭のラインの Style を登録する
        style = doc.addStyle("headLine", null);
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/open/dolphin/resources/images/line.jpg"));;
        StyleConstants.setIcon(style, icon);
        
        try {
            style = doc.getStyle("timeStamp");
            doc.setLogicalStyle(doc.getLength(), style);
            doc.insertString(doc.getLength(), "2003-12-22", null);
            doc.insertString(doc.getLength(), "\n", null);
            
            //setLogicalStyle(start, stampMark);
            AttributeSet a = (AttributeSet)doc.getStyle("headLine");
            doc.insertString(doc.getLength()," ", a);
            doc.insertString(doc.getLength(),"\n", null);
            
            int top = doc.getLength();
            
            // 論理スタイルをクリアする
            doc.setLogicalStyle(doc.getLength(), null);
            doc.insertString(doc.getLength(),"\n", null); // 改行しておく
            textPane.setCaretPosition(doc.getLength());
            
            // フリーテキストの先頭ポジションを保存する
            topPos = doc.createPosition(top);
            
            
        } catch (BadLocationException e) {
            System.out.println(e);
        }
    }
    
    JMenuBar createMenubar() {
        
        JMenuBar mb = new JMenuBar();
        
        JMenu menu = new JMenu("Style");
        mb.add(menu);
	
        // Red
        Action action = new StyledEditorKit.ForegroundAction("", Color.red);
        JMenuItem mi = menu.add(action);
        mi.setText("Red");
        
            // Blue
        action = new StyledEditorKit.ForegroundAction("", Color.blue);
        mi = menu.add(action);
        mi.setText("Blue");
        
            // Green
        action = new StyledEditorKit.ForegroundAction("", Color.green);
        mi = menu.add(action);
        mi.setText("Green");

            // 10
        action = new StyledEditorKit.FontSizeAction("",10);;
        mi = menu.add(action);
        mi.setText("10");
        
            // 12
        action = new StyledEditorKit.FontSizeAction("",12);
        mi = menu.add(action);
        mi.setText("12");
        
            // 14
        action = new StyledEditorKit.FontSizeAction("",14);
        mi = menu.add(action);
        mi.setText("14");
        
            // 16
        action = new StyledEditorKit.FontSizeAction("",16);
        mi = menu.add(action);
        mi.setText("16");

            // 18
        action = new StyledEditorKit.FontSizeAction("",18);
        mi = menu.add(action);
        mi.setText("18");
        
            // 20
        action = new StyledEditorKit.FontSizeAction("",20);
        mi = menu.add(action);
        mi.setText("20");
        
            // 24
        action = new StyledEditorKit.FontSizeAction("",24);
        mi = menu.add(action);
        mi.setText("24");
        
            // Bold
        action = new StyledEditorKit.BoldAction();
        mi = menu.add(action);
        mi.setText("bold");
        
            // Italic
        action = new StyledEditorKit.ItalicAction();
        mi = menu.add(action);
        mi.setText("italic");
        
            // Underline
        action = new StyledEditorKit.UnderlineAction();
        mi = menu.add(action);
        mi.setText("underline");

	return mb;
    }
    
    void insertStamp() {
       
        JLabel sh = new JLabel("Stamp");
        
        DefaultStyledDocument doc = (DefaultStyledDocument)textPane.getDocument();
        int start = textPane.getCaretPosition();
        Style runStyle = doc.addStyle("stampHolder", null);
        StyleConstants.setComponent(runStyle, sh);
        
        try {
            doc.insertString(start, " ", runStyle);
        } catch (BadLocationException e) {
            System.out.println(e);
        }
    }
    
    public void insertStamp(JLabel stamp) {
        
        DefaultStyledDocument doc = (DefaultStyledDocument)textPane.getDocument();
        int start = doc.getLength();
        Style runStyle = doc.addStyle("stampHolder", null);
        StyleConstants.setComponent(runStyle, stamp);
        
        try {
            doc.insertString(start, " ", runStyle);
            
        } catch (BadLocationException e) {
            System.out.println(e);
        }
    }
    
    public void insertString(String str, AttributeSet atts) {
        
        try {
            
            DefaultStyledDocument doc = (DefaultStyledDocument)textPane.getDocument();
            doc.insertString(doc.getLength(), str, atts);
            
        } catch (BadLocationException e) {
            System.out.println(e);
        }
    }
    
    public void insertIcon(String style) {
        
        try {
            DefaultStyledDocument doc = (DefaultStyledDocument)textPane.getDocument();
            AttributeSet a = (AttributeSet)doc.getStyle(style);
            doc.insertString(doc.getLength()," ", a);
            doc.insertString(doc.getLength(),"\n", null);
        } catch (BadLocationException e) {
            System.out.println(e);
        }
    }
    
    public void setLogicalStyle(String str) {
        
        DefaultStyledDocument doc = (DefaultStyledDocument)textPane.getDocument();
        
        Style style = doc.getStyle(str);
        doc.setLogicalStyle(doc.getLength(), style);
    }
    
    public void clearLogicalStyle() {
        
        DefaultStyledDocument doc = (DefaultStyledDocument)textPane.getDocument();
        doc.setLogicalStyle(doc.getLength(), null);
    }
    
    public void makeParagraph() {
        
        try {
            DefaultStyledDocument doc = (DefaultStyledDocument)textPane.getDocument();
            doc.insertString(doc.getLength(), "\n", null);
            
        } catch (BadLocationException e) {
            System.out.println(e);
        }
    }
    
    void dumpPane() {
        
        TextPaneDumpBuilder builder = new TextPaneDumpBuilder();
        dumped = builder.build((DefaultStyledDocument)textPane.getDocument());
        System.out.println(dumped);
    }
        
    public void actionPerformed(ActionEvent e) {
        
        Object o = e.getSource();
        
        if (o == stamp) {
            
            insertStamp();
            
        } else if (o == dump) {
            dumpPane();
        
        }
        else if (o == restore) {
            //textPane.selectAll();
            //textPane.replaceSelection("");

            if (dumped == null) {
                return;
            }
            
            TextPaneRestoreBuilder builder = new TextPaneRestoreBuilder();
            builder.build(this, dumped);
        }
    }
    
    public static void main(String[] args) {
        
        JTextPaneDump jt = new JTextPaneDump();
        JFrame f = new JFrame();
        f.getContentPane().add(jt);
        f.setDefaultCloseOperation(3);
        f.pack();
        f.setVisible(true);
    }   
}