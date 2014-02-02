package open.dolphin.order;

import javax.swing.*;
import javax.swing.border.*;

import open.dolphin.client.*;

import java.awt.*;
import open.dolphin.client.GUIConst;

/**
 * TreatmentStampEditor.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LTreatmentStampEditor extends StampModelEditor  {
    
    private static final long serialVersionUID = -2173356408762423668L;
    
    private ItemTablePanel testTable;
    private MasterSetPanel masterPanel;
    
    /**
     * Creates new InjectionStampEditor
     */
    public LTreatmentStampEditor(IStampEditorDialog context, MasterSetPanel masterPanel) {
        setContext(context);
        this.masterPanel = masterPanel;
        initComponent();
    }
    
    @Override
    public void start() {
        ClaimConst.ClaimSpec spec = ClaimConst.ClaimSpec.TREATMENT;
        masterPanel.setSearchClass(spec.getSearchCode());
        masterPanel.startTest(testTable);
    }
    
    /**
     * プログラムを開始する。
     */
    private void initComponent() {
        
        // 処置の CLAIM 仕様を得る
        ClaimConst.ClaimSpec spec = ClaimConst.ClaimSpec.TREATMENT;
        
        // セットテーブルを生成し CLAIM パラメータを設定する
        testTable = new ItemTablePanel(this);
        testTable.setOrderName(spec.getName());
        testTable.setClassCode(spec.getClassCode());
        testTable.setClassCodeId(ClaimConst.CLASS_CODE_ID);
        testTable.setSubClassCodeId(ClaimConst.SUBCLASS_CODE_ID);
        
        // タイトルを設定しレイアウトする
        setTitle(spec.getName());
        Border b = BorderFactory.createEtchedBorder();
        testTable.setBorder(BorderFactory.createTitledBorder(b, spec.getName()));
        
        setLayout(new BorderLayout(0, GUIConst.DEFAULT_CMP_V_SPACE));
        add(testTable, BorderLayout.CENTER);
        //setPreferredSize(GUIConst.DEFAULT_STAMP_EDITOR_SIZE);
    }
    
    /**
     * エディタで生成した値(ClaimBundle)を返す。
     */
    public Object getValue() {
        return testTable.getValue();
    }
    
    /**
     * エディタで編集する値(ClaimBundle)を設定する。
     */
    public void setValue(Object val) {
        testTable.setValue(val);
    }
    
    /**
     * エディタを終了する。
     */
    @Override
    public void dispose() {
        masterPanel.stopTest(testTable);
    }
}
