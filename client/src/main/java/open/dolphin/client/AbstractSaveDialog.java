package open.dolphin.client;

import java.awt.Window;
import javax.swing.*;

/**
 * SaveDialog
 * (予定カルテ対応)
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractSaveDialog {
    
    protected static final String[] PRINT_COUNT 
            = {"0", "1",  "2",  "3",  "4", "5"};
    protected static final String[] TITLE_LIST = {"経過記録", "予定", "処方", "処置", "検査", "画像", "指導"};
    protected static final String TITLE = "ドキュメント保存";
    protected static final String TMP_SAVE = "仮保存";
    
    // 親Window
    protected Window parent;
    
    // ダイアログ
    protected JDialog dialog;
    
    // キャンセルボタン
    protected JButton cancelButton;
    
    // 仮保存ボタン
    protected JButton tmpButton;
    
    // 文書タイトル
    protected JTextField titleField;
    protected JComboBox titleCombo;
    
    // 印刷枚数Combo
    protected JComboBox printCombo;
    
    // 診療科を表示するラベル
    protected JLabel departmentLabel;
    
    // CLAIM 送信 ChckBox
    protected JCheckBox sendClaim;
    
    // CLAIM送信Action
    protected AbstractAction sendClaimAction;
    
    // 戻り値のSaveParams
    protected SaveParamsM value; 

    // 入力値のSaveParams
    protected SaveParamsM enterParams;
    
    public void setWindowParent(Window parent) {
        this.parent = parent;
    }
    
    public void start() {
        dialog.setVisible(true);
    }
    
    public SaveParamsM getValue() {
        return value;
    }
    
    public abstract void setValue(SaveParamsM params);
}