package open.dolphin.order;

import java.awt.BorderLayout;
import java.util.EnumSet;

import javax.swing.*;
import javax.swing.border.*;

import open.dolphin.client.*;
import open.dolphin.client.GUIConst;

/**
 * 処方スタンプエディタ。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class MedStampEditor2 extends StampModelEditor  {
    
    private static final String MEDICINE_TABLETITLE_BORDER    = "処方セット";
    private static final String EDITOR_NAME = "処方";
    
    /** 処方セット作成パネル */
    private MedicineTablePanel medicineTable;
    
    /** マスタセットパネル */
    private MasterTabPanel masterPanel;
    
    /** Creates new MedStampEditor2 */
    public MedStampEditor2() {
    }
    
    /**
     * プログラムを開始する。
     */
    @Override
    public void start() {
        
        setTitle(EDITOR_NAME);
        
        // Medicine table
        medicineTable = new MedicineTablePanel(this);
        Border b = BorderFactory.createEtchedBorder();
        medicineTable.setBorder(BorderFactory.createTitledBorder(b, MEDICINE_TABLETITLE_BORDER));
        
        //
        // 処方で使用するマスタを指定し、マスタセットパネルを生成する
        //
        EnumSet<ClaimConst.MasterSet> set = EnumSet.of(
                ClaimConst.MasterSet.MEDICAL_SUPPLY,
                ClaimConst.MasterSet.ADMINISTRATION,
                ClaimConst.MasterSet.INJECTION_MEDICINE,
                ClaimConst.MasterSet.TOOL_MATERIAL);
        masterPanel = new MasterTabPanel(set);
        
        //
        // 処方作成であることを通知する
        //
        masterPanel.startMedicine(medicineTable);
        
        //
        // Connects
        //
        medicineTable.setParent(this);
        
        //
        // 上にスタンプのセットパネル、下にマスタのセットパネルを配置する
        // 全てのスタンプエディタに共通
        //
        JPanel top = new JPanel(new BorderLayout());
        top.add(medicineTable, BorderLayout.CENTER);
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(top);
        add(masterPanel);
        setPreferredSize(GUIConst.DEFAULT_STAMP_EDITOR_SIZE);
    }
    
    /**
     * 作成したスタンプを返す。
     * @return 作成したスタンプ
     */
    public Object getValue() {
        return medicineTable.getValue();
    }
    
    /**
     * 編集するスタンプを設定する。
     * @param val 編集するスタンプ
     */
    public void setValue(Object val) {
        System.err.println("setValue");
        medicineTable.setValue(val);
        System.err.println("setValue1");
    }
    
    /**
     * プログラムを終了する。
     */
    @Override
    public void dispose() {
        masterPanel.stopMedicine(medicineTable);
    }
}