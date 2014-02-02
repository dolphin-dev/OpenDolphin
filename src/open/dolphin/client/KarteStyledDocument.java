/*
 * KarteStyledDocument.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2005 Digital Globe, Inc. All rights reserved.
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

import javax.swing.text.*;

/**
 * KartePane の StyledDocument class。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class KarteStyledDocument extends DefaultStyledDocument {
    
    private static final long serialVersionUID = 3078315320512749196L;
    
    /** Style */
    private final String STAMP_STYLE      = "stampHolder";
    private final String SCHEMA_STYLE     = "schemaHolder";
    
    // オーナの KartePane
    private KartePane kartePane;
    
    /** Creates new TestDocument */
    public KarteStyledDocument() {
    }
    
    public void setParent(KartePane kartePane) {
        this.kartePane = kartePane;
    }
    
    public void setLogicalStyle(String str) {
        Style style = this.getStyle(str);
        this.setLogicalStyle(this.getLength(), style);
    }
    
    public void clearLogicalStyle() {
        this.setLogicalStyle(this.getLength(), null);
    }
    
    public void makeParagraph() {
        try {
            insertString(getLength(), "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Stamp を挿入する。
     * @param sh 挿入するスタンプホルダ
     */
    public void stamp(final StampHolder sh) {
        
        
        try {
            // このスタンプ用のスタイルを動的に生成する
            Style runStyle = addStyle(STAMP_STYLE, null);
            StyleConstants.setComponent(runStyle, sh);
            //StyleConstants.setLeftIndent(runStyle, 10);
            
            // キャレット位置を取得する
            int start = kartePane.getTextPane().getCaretPosition();
            
            // Stamp を挿入する
            //insertString(start, " ", null);
            insertString(start, "S", runStyle);
            insertString(start+1, "\n", null);  // 改行をつけないとテキスト入力制御がやりにくくなる
            
            // スタンプの開始と終了位置を生成して保存する
            sh.setEntry(createPosition(start), createPosition(start+1));
            
        } catch(BadLocationException be) {
            be.printStackTrace();
        } catch(NullPointerException ne) {
            ne.printStackTrace();
        }
    }
    
    /**
     * Stamp を挿入する。
     * @param sh 挿入するスタンプホルダ
     */
    public void flowStamp(final StampHolder sh) {
        
        
        try {
            // このスタンプ用のスタイルを動的に生成する
            Style runStyle = addStyle(STAMP_STYLE, null);
            StyleConstants.setComponent(runStyle, sh);
            
            // キャレット位置を取得する
            int start = kartePane.getTextPane().getCaretPosition();
            
            // Stamp を挿入する
            insertString(start, "S", runStyle);
            
            // スタンプの開始と終了位置を生成して保存する
            sh.setEntry(createPosition(start), createPosition(start+1));
            
        } catch(BadLocationException be) {
            be.printStackTrace();
        } catch(NullPointerException ne) {
            ne.printStackTrace();
        }
    }
    
    /**
     * Stampを削除する。
     * @param start 削除開始のオフセット位置
     * @param len
     */
    public void removeStamp(int start, int len) {
        
        try {
            // Stamp は一文字で表されている
            remove(start, 1);
        } catch(BadLocationException be) {
            be.printStackTrace();
        }
    }
    
    /**
     * Stampを指定されたポジションに挿入する。
     * @param inPos　挿入ポジション
     * @param sh　挿入する StampHolder
     */
    public void insertStamp(Position inPos, StampHolder sh) {
        
        try {
            Style runStyle = this.addStyle(STAMP_STYLE, null);
            StyleConstants.setComponent(runStyle, sh);
            
            // 挿入位置
            int start = inPos.getOffset();
            insertString(start, "S", runStyle);
            sh.setEntry(createPosition(start), createPosition(start+1));
        } catch(BadLocationException be) {
            be.printStackTrace();
        }
    }
    
    public void stampSchema(SchemaHolder sc) {
        
        try {
            // このスタンプ用のスタイルを動的に生成する
            Style runStyle = addStyle(SCHEMA_STYLE, null);
            StyleConstants.setComponent(runStyle, sc);
            
            // Stamp同様
            int start = kartePane.getTextPane().getCaretPosition();
            insertString(start, "I", runStyle);
            insertString(start+1, "\n", null);  // 改行をつけないとテキスト入力制御がやりにくくなる
            sc.setEntry(createPosition(start), createPosition(start+1));
        } catch(BadLocationException be) {
            be.printStackTrace();
        }
    }
    
    public void flowSchema(final SchemaHolder sh) {
        
        try {
            // このスタンプ用のスタイルを動的に生成する
            Style runStyle = addStyle(SCHEMA_STYLE, null);
            StyleConstants.setComponent(runStyle, sh);
            
            // キャレット位置を取得する
            int start = kartePane.getTextPane().getCaretPosition();
            
            // Stamp を挿入する
            insertString(start, "I", runStyle);
            
            // スタンプの開始と終了位置を生成して保存する
            sh.setEntry(createPosition(start), createPosition(start+1));
            
        } catch(BadLocationException be) {
            be.printStackTrace();
        } catch(NullPointerException ne) {
            ne.printStackTrace();
        }
    }
    
    public void insertTextStamp(String text) {
        
        try {
            //System.out.println("insertTextStamp");
            clearLogicalStyle();
            setLogicalStyle("default"); // mac 2207-03-31
            int pos = kartePane.getTextPane().getCaretPosition();
            //System.out.println("pos = " + pos);
            insertString(pos, text, null);
            //System.out.println("inserted TextStamp");
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    public void insertFreeString(String text, AttributeSet a) {
        try {
            insertString(getLength(), text, a);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}