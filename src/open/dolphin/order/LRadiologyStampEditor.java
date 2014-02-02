package open.dolphin.order;

import javax.swing.*;
import javax.swing.border.*;

import open.dolphin.client.*;

import java.awt.*;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LRadiologyStampEditor extends StampModelEditor  {
    
    private static final long serialVersionUID = 2467212598346800512L;
    
    private RadItemTablePanel testTable;
    private MasterSetPanel masterPanel;
    
    /**
     * Creates new InjectionStampEditor
     */
    public LRadiologyStampEditor(IStampEditorDialog context, MasterSetPanel masterPanel) {
        setContext(context);
        this.masterPanel = masterPanel;
        initComponent();
    }
    
    @Override
    public void start() {
        ClaimConst.ClaimSpec spec = ClaimConst.ClaimSpec.RADIOLOGY;
        masterPanel.setSearchClass(spec.getSearchCode());
        masterPanel.setRadLocationEnabled(true);
        masterPanel.startTest(testTable);
    }
    
    private void initComponent() {
        
        // 放射線のCLAIM 仕様を得る
        ClaimConst.ClaimSpec spec = ClaimConst.ClaimSpec.RADIOLOGY;
        
        // セットテーブルを生成し CLAIM パラメータを設定する
        testTable = new RadItemTablePanel(this);
        testTable.setOrderName(spec.getName());
        testTable.setClassCode(spec.getClassCode());
        testTable.setClassCodeId(ClaimConst.CLASS_CODE_ID);
        testTable.setSubClassCodeId(ClaimConst.SUBCLASS_CODE_ID);
        
//        // 放射線メソッドのリストボックスとセットテーブルをリスナ関係にする
//        RadiologyMethod method = new RadiologyMethod();
//        method.addPropertyChangeListener(RadiologyMethod.RADIOLOGY_MEYTHOD_PROP, testTable);
        
        // タイトルを設定しレイアウトする
        setTitle(spec.getName());
        Border b = BorderFactory.createEtchedBorder();
        testTable.setBorder(BorderFactory.createTitledBorder(b, spec.getName()));
        
        this.setLayout(new BorderLayout());
        //this.add(method, BorderLayout.WEST);
        this.add(testTable, BorderLayout.CENTER);
        
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