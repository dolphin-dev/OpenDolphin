package open.dolphin.order;

import javax.swing.*;
import javax.swing.border.Border;

import open.dolphin.client.*;
import open.dolphin.client.GUIConst;

import java.awt.*;

/**
 * Diagnosis editor.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LDiagnosisEditor extends StampModelEditor  {
    
    private static final String EDITOR_NAME = "傷病名";
    
    /** 傷病名編集テーブル */
    private DiagnosisTablePanel diagnosisTable;
    
    /** マスターセットパネル */
    private MasterSetPanel masterPanel;
    
    
    /** 
     * Creates new DiagnosisEditor 
     */
    public LDiagnosisEditor(IStampEditorDialog context, MasterSetPanel masterPanel) {
        setContext(context);
        this.masterPanel = masterPanel;
        initComponent();
    }
    
    /**
     * エディタを開始する。
     */
    @Override
    public void start() {
        masterPanel.startDiagnosis(diagnosisTable);
    }
    
    /**
     * Componentを初期化する。
     */
    private void initComponent() {
        
        setTitle(EDITOR_NAME);
        
        //
        // 傷病名編集テーブル
        // マスターセットパネル
        // を生成しレイアウトする
        //
        diagnosisTable = new DiagnosisTablePanel(this);
        Border b = BorderFactory.createEtchedBorder();
        diagnosisTable.setBorder(BorderFactory.createTitledBorder(b, EDITOR_NAME));
        
        setLayout(new BorderLayout(0, GUIConst.DEFAULT_CMP_V_SPACE));
        add(diagnosisTable, BorderLayout.CENTER);
    }
    
    /**
     * 編集した傷病名を返す。
     * @return RegisteredDiagnosisModel
     */
    public Object getValue() {
        return diagnosisTable.getValue();
    }
    
    /**
     * 編集する傷病名を設定する。
     * @param val RegisteredDiagnosisModel
     */
    public void setValue(Object val) {
        diagnosisTable.setValue((Object[])val);
    }
    
    /**
     * リソースを解放する。
     */
    @Override
    public void dispose() {
        masterPanel.stopDiagnosis(diagnosisTable);
    }
}