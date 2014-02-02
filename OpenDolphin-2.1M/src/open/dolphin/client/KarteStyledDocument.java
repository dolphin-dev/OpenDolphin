package open.dolphin.client;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Position;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import open.dolphin.project.Project;

/**
 * KartePane の StyledDocument class。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class KarteStyledDocument extends DefaultStyledDocument {
    
    // スタンプの先頭を改行する
    private boolean topSpace;
    
    // stampHolder Style
    private final String STAMP_STYLE = "stampHolder";
    
    // schemaHolder
    private final String SCHEMA_STYLE = "schemaHolder";
    
    // KartePane
    private KartePane kartePane;
    
    
    /** Creates new TestDocument */
    public KarteStyledDocument() {
        topSpace = Project.getBoolean("stampSpace", true);
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
            e.printStackTrace(System.err);
        }
    }
    
    /**
     * Stamp を挿入する。
     * @param sh 挿入するスタンプホルダ
     */
    public void stamp(final StampHolder sh) {
        
        try {
            Style runStyle = this.getStyle(STAMP_STYLE);
            if (runStyle == null) {
                runStyle = addStyle(STAMP_STYLE, null);
            }
            StyleConstants.setComponent(runStyle, sh);
            
            // キャレット位置を取得する
            int start = kartePane.getTextPane().getCaretPosition();
            //System.out.println("getCaretPosition=" + start);
            
            // Stamp を挿入する
            if (topSpace) {
                insertString(start, "\n", null);
                insertString(start+1, " ", runStyle);
                insertString(start+2, "\n", null);                           // 改行をつけないとテキスト入力制御がやりにくくなる
                sh.setEntry(createPosition(start+1), createPosition(start+2)); // スタンプの開始と終了位置を生成して保存する
            } else {
                insertString(start, " ", runStyle);
                insertString(start+1, "\n", null);                           // 改行をつけないとテキスト入力制御がやりにくくなる
                sh.setEntry(createPosition(start), createPosition(start+1)); // スタンプの開始と終了位置を生成して保存する
            }
            
        } catch(BadLocationException be) {
            be.printStackTrace(System.err);
        } catch(NullPointerException ne) {
            ne.printStackTrace(System.err);
        }
    }
    
    /**
     * Stamp を挿入する。
     * @param sh 挿入するスタンプホルダ
     */
    public void flowStamp(final StampHolder sh) {
        
        try {
            Style runStyle = this.getStyle(STAMP_STYLE);
            if (runStyle == null) {
                runStyle = addStyle(STAMP_STYLE, null);
            }
            // このスタンプ用のスタイルを動的に生成する
            //Style runStyle = addStyle(STAMP_STYLE, null);
            StyleConstants.setComponent(runStyle, sh);
            
            // キャレット位置を取得する
            int start = kartePane.getTextPane().getCaretPosition();
            //int start = this.getLength();
            //System.out.println("getCaretPosition=" + start);
            
            // Stamp を挿入する
            insertString(start, " ", runStyle);
            
            // スタンプの開始と終了位置を生成して保存する
            Position stPos = createPosition(start);
            Position endPos = createPosition(start+1);
            //System.out.println("Position="+stPos+","+endPos);
            sh.setEntry(stPos, endPos);
            
        } catch(BadLocationException be) {
            be.printStackTrace(System.err);
        } catch(NullPointerException ne) {
            ne.printStackTrace(System.err);
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
            be.printStackTrace(System.err);
        }
    }
    
    /**
     * Stampを指定されたポジションに挿入する。
     * @param inPos　挿入ポジション
     * @param sh　挿入する StampHolder
     */
    public void insertStamp(Position inPos, StampHolder sh) {
        
        try {
            Style runStyle = this.getStyle(STAMP_STYLE);
            if (runStyle == null) {
                runStyle = addStyle(STAMP_STYLE, null);
            }
            //Style runStyle = this.addStyle(STAMP_STYLE, null);
            StyleConstants.setComponent(runStyle, sh);
            
            // 挿入位置
            int start = inPos.getOffset();
            insertString(start, " ", runStyle);
            sh.setEntry(createPosition(start), createPosition(start+1));
        } catch(BadLocationException be) {
            be.printStackTrace(System.err);
        }
    }
    
    public void stampSchema(SchemaHolder sc) {
        
        try {
            Style runStyle = this.getStyle(SCHEMA_STYLE);
            if (runStyle == null) {
                runStyle = addStyle(SCHEMA_STYLE, null);
            }
            // このスタンプ用のスタイルを動的に生成する
            //Style runStyle = addStyle(SCHEMA_STYLE, null);
            StyleConstants.setComponent(runStyle, sc);
            
            // Stamp同様
            int start = kartePane.getTextPane().getCaretPosition();
            insertString(start, " ", runStyle);
            insertString(start+1, "\n", null);
            sc.setEntry(createPosition(start), createPosition(start+1));
        } catch(BadLocationException be) {
            be.printStackTrace(System.err);
        }
    }
    
    public void flowSchema(final SchemaHolder sh) {
        
        try {
            Style runStyle = this.getStyle(SCHEMA_STYLE);
            if (runStyle == null) {
                runStyle = addStyle(SCHEMA_STYLE, null);
            }
            // このスタンプ用のスタイルを動的に生成する
            //Style runStyle = addStyle(SCHEMA_STYLE, null);
            StyleConstants.setComponent(runStyle, sh);
            
            // キャレット位置を取得する
            int start = kartePane.getTextPane().getCaretPosition();
            
            // Stamp を挿入する
            insertString(start, " ", runStyle);
            
            // スタンプの開始と終了位置を生成して保存する
            sh.setEntry(createPosition(start), createPosition(start+1));
            
        } catch(BadLocationException be) {
            be.printStackTrace(System.err);
        } catch(NullPointerException ne) {
            ne.printStackTrace(System.err);
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
            e.printStackTrace(System.err);
        }
    }
    
    public void insertFreeString(String text, AttributeSet a) {
        try {
            insertString(getLength(), text, a);
        } catch (BadLocationException e) {
            e.printStackTrace(System.err);
        }
    }
}