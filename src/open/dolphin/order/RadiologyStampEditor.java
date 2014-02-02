package open.dolphin.order;

import javax.swing.*;
import javax.swing.border.*;

import open.dolphin.client.*;

import java.awt.*;
import java.util.EnumSet;
import open.dolphin.client.GUIConst;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class RadiologyStampEditor extends StampModelEditor  {
    
    private static final long serialVersionUID = 2467212598346800512L;
    
    private RadItemTablePanel testTable;
    private MasterTabPanel masterPanel;
    
    /**
     * Creates new InjectionStampEditor
     */
    public RadiologyStampEditor() {
    }
    
    @Override
    public void start() {
        
        // 放射線のCLAIM 仕様を得る
        ClaimConst.ClaimSpec spec = ClaimConst.ClaimSpec.RADIOLOGY;
        
        // セットテーブルを生成し CLAIM パラメータを設定する
        testTable = new RadItemTablePanel(this);
        testTable.setOrderName(spec.getName());
        testTable.setClassCode(spec.getClassCode());
        testTable.setClassCodeId(ClaimConst.CLASS_CODE_ID);
        testTable.setSubClassCodeId(ClaimConst.SUBCLASS_CODE_ID);
        
        // 放射線のマスタセットを生成する
        EnumSet<ClaimConst.MasterSet> set = EnumSet.of(
                ClaimConst.MasterSet.TREATMENT,
                ClaimConst.MasterSet.MEDICAL_SUPPLY,
                ClaimConst.MasterSet.INJECTION_MEDICINE,
                ClaimConst.MasterSet.TOOL_MATERIAL);
        
        // マスタパネルを生成し、診療行為の検索対象コード範囲を設定する
        masterPanel = new MasterTabPanel(set);
        masterPanel.setSearchClass(spec.getSearchCode());
        masterPanel.setRadLocationEnabled(true);
        masterPanel.startTest(testTable);
        
        // タイトルを設定しレイアウトする
        setTitle(spec.getName());
        Border b = BorderFactory.createEtchedBorder();
        testTable.setBorder(BorderFactory.createTitledBorder(b, spec.getName()));
        
        JPanel top = new JPanel(new BorderLayout());
        top.add(testTable, BorderLayout.CENTER);
        
        setLayout(new BorderLayout(0, GUIConst.DEFAULT_CMP_V_SPACE));
        add(top, BorderLayout.NORTH);
        add(masterPanel, BorderLayout.CENTER);
        setPreferredSize(GUIConst.DEFAULT_STAMP_EDITOR_SIZE);
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