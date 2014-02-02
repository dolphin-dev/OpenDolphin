package open.dolphin.order;

import javax.swing.*;
import javax.swing.border.*;

import open.dolphin.client.*;

import java.awt.*;
import open.dolphin.client.GUIConst;

/**
 * PhysiologyStampeditor.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LPhysiologyStampEditor extends StampModelEditor  {
    
    private static final long serialVersionUID = 4688829655457430787L;
    
    private ItemTablePanel testTable;
    private MasterSetPanel masterPanel;
    
    /**
     * Creates new InjectionStampEditor
     */
    public LPhysiologyStampEditor(IStampEditorDialog context, MasterSetPanel masterPanel) {
        setContext(context);
        this.masterPanel = masterPanel;
        initComponent();
    }
    
    @Override
    public void start() {
        ClaimConst.ClaimSpec spec = ClaimConst.ClaimSpec.PHYSIOLOGY;
        masterPanel.setSearchClass(spec.getSearchCode());
        masterPanel.startTest(testTable);
    }
    
    private void initComponent() {
        
        // 生理内視鏡検査のCLAIM 仕様を得る
        ClaimConst.ClaimSpec spec = ClaimConst.ClaimSpec.PHYSIOLOGY;
        
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
    
    public Object getValue() {
        return testTable.getValue();
    }
    
    public void setValue(Object val) {
        testTable.setValue(val);
    }
    
    @Override
    public void dispose() {
        masterPanel.stopTest(testTable);
    }
}