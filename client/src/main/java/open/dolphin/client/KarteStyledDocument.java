package open.dolphin.client;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.*;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.project.Project;

/**
 * KartePane の StyledDocument class。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class KarteStyledDocument extends DefaultStyledDocument {
    
    // stampHolder Style
    private final String STAMP_STYLE = "stampHolder";
    
    // schemaHolder
    private final String SCHEMA_STYLE = "schemaHolder";
    
    // attachmentHolder
    private final String ATTACHMENT_STYLE = "attachmentHolder";
    
    // KartePane
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
            
            // Stamp を挿入する
            if (Project.getBoolean("stampSpace") && getLength()>0) {         // 長さ==0なら先頭spaceは不要
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
            StyleConstants.setComponent(runStyle, sh);
            
            // キャレット位置を取得する
//masuda^   EDTでなくてもいいように
            //int start = kartePane.getTextPane().getCaretPosition();
            int start = this.getLength();
//masuda$
            // Stamp を挿入する
            insertString(start, " ", runStyle);
            
            // スタンプの開始と終了位置を生成して保存する
            Position stPos = createPosition(start);
            Position endPos = createPosition(start+1);
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
//masuda^   Stamp/Schemaをremoveするときは直後の改行も削除する
            // Stamp は一文字で表されている
            //remove(start, 1);
            if (start < getLength() && "\n".equals(getText(start+1, 1))) {
                remove(start, 2);
            } else {
                remove(start, 1);
            }
//masuda$
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
            StyleConstants.setComponent(runStyle, sh);
            
            // キャレット位置を取得する
//masuda^   EDTでなくてもいいように
            //int start = kartePane.getTextPane().getCaretPosition();
            int start = this.getLength();
//masuda$
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
        
    public void stampAttachment(AttachmentHolder sc) {
        
        try {
            Style runStyle = this.getStyle(ATTACHMENT_STYLE);
            if (runStyle == null) {
                runStyle = addStyle(ATTACHMENT_STYLE, null);
            }
            // このスタンプ用のスタイルを動的に生成する
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
    
    public void flowAttachment(final AttachmentHolder sh) {
        
        try {
            Style runStyle = this.getStyle(ATTACHMENT_STYLE);
            if (runStyle == null) {
                runStyle = addStyle(ATTACHMENT_STYLE, null);
            }
            // このスタンプ用のスタイルを動的に生成する
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
            insertString(pos, text, null);
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
   
 //masuda^   KarteStyledDocument内のStampHolderを取得する。pns先生のコード
    public List<StampHolder> getStampHolders() {

        List<StampHolder> list = new ArrayList<StampHolder>();
        int length = getLength();
        for (int i = 0; i < length; ++i) {
            StampHolder sh = (StampHolder) StyleConstants.getComponent(getCharacterElement(i).getAttributes());
            if (sh != null) {
                list.add(sh);
            }
        }
        return list;
    }
    
    // StampHolder内のModuleModelだけ返す  sh.getStamp().getModuleInfoBean().getEntity().equals(entity)
    public List<ModuleModel> getStamps() {
        
        List<ModuleModel> list = new ArrayList<ModuleModel>();
        int length = getLength();
        for (int i = 0; i < length; ++i) {
            StampHolder sh = (StampHolder) StyleConstants.getComponent(getCharacterElement(i).getAttributes());
            if (sh != null) {
                list.add(sh.getStamp());
            }
        }
        return list;
    }
    
//minagawa^ LSC Test
    public List<ModuleModel> getStampsToMatch(String entity) {
        
        List<ModuleModel> list = new ArrayList();
        int length = getLength();
        for (int i = 0; i < length; ++i) {
            StampHolder sh = (StampHolder) StyleConstants.getComponent(getCharacterElement(i).getAttributes());
            if (sh != null && sh.getStamp().getModuleInfoBean().getEntity().equals(entity)) {
                list.add(sh.getStamp());
            }
        }
        return list;
    }
    
    public List<StampHolder> getStampHoldersToMatch(String entity) {

        List<StampHolder> list = new ArrayList();
        int length = getLength();
        for (int i = 0; i < length; ++i) {
            StampHolder sh = (StampHolder) StyleConstants.getComponent(getCharacterElement(i).getAttributes());
            if (sh != null && sh.getStamp().getModuleInfoBean().getEntity().equals(entity)) {
                list.add(sh);
            }
        }
        return list;
    }
//minagawa$

    // StampHolder直後の改行がない場合は補う
    public void fixCrAfterStamp() {

        try {
            int i = 0;
            while (i < getLength()) {
                StampHolder sh = (StampHolder) StyleConstants.getComponent(getCharacterElement(i).getAttributes());
                String strNext = getText(++i, 1);
                if (sh != null && !"\n".equals(strNext)) {
                    insertString(i, "\n", null);
                }
            }
        } catch (BadLocationException ex) {
        }
    }

    // 文書末の余分な改行文字を削除する
    public void removeExtraCR() {

        int len = getLength();
        try {
            int pos = len;
            // 改行文字以外が出てくるまで文書末からスキャン
            for (pos = len - 1; pos >= 0; --pos) {
                if (!"\n".equals(getText(pos, 1))) {
                    break;
                }
            }
            // 一文字戻す
            ++pos;
            if (len - pos > 0) {
                remove(pos, len - pos);
            }

        } catch (Exception ex) {
        }
    }

    // 文頭に挿入する場合、現在の文頭がComponentHolderならばそのEntryを更新する。
    // position=0は特殊で移動しても変わらないため、スタンプホルダの開始位置がずれてしまうことへの対応 11/06/07
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

        if (offs == 0) {
            ComponentHolder ch = (ComponentHolder) StyleConstants.getComponent(getCharacterElement(offs).getAttributes());
            if (ch != null) {
                super.insertString(offs, str, a);
                int pos = offs + str.length();
                ch.setEntry(createPosition(pos), createPosition(pos + 1));
                return;
            }
        }
        super.insertString(offs, str, a);
    }

    // KartePaneを返す。SOA/PTransferHandlerでインポート先を、JTextPane->KarteStyledDocument->KartePaneとたぐることができる
    // JTextPane textPane.getClientProperty("kartePane")でもＯＫ？
    public KartePane getKartePane() {
        return kartePane;
    }
//masuda$

}