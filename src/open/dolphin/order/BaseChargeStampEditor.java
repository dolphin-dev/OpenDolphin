package open.dolphin.order;

import javax.swing.*;
import javax.swing.border.*;

import open.dolphin.client.*;

import java.awt.*;
import java.util.EnumSet;
import open.dolphin.client.GUIConst;

/**
 * BaseCharge editor.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class BaseChargeStampEditor extends StampModelEditor {

    //private static final String orderClass          = "110-125";   //"10";
    //private static final String orderName           = "初診・再診";
    //private static final String classCodeId         = "Claim007";
    //private static final String subclassCodeId      = "Claim003";
    private static final long serialVersionUID = 8284352054746435316L;
    private ItemTablePanel testTable;
    private MasterTabPanel masterPanel;

    /** 
     * Creates new InjectionStampEditor 
     */
    public BaseChargeStampEditor() {
    }

    @Override
    public void start() {

        // 診断料のCLAIM 仕様を得る
        ClaimConst.ClaimSpec spec = ClaimConst.ClaimSpec.BASE_CHARGE;

        // セットテーブルを生成し CLAIM パラメータを設定する
        testTable = new ItemTablePanel(this);
        testTable.setOrderName(spec.getName());
        testTable.setFindClaimClassCode(true);         // 診療行為区分はマスタアイテムから
        testTable.setClassCodeId(ClaimConst.CLASS_CODE_ID);
        testTable.setSubClassCodeId(ClaimConst.SUBCLASS_CODE_ID);

        // 診断料のマスタセットを生成する
        EnumSet<ClaimConst.MasterSet> enumSet = EnumSet.of(
                ClaimConst.MasterSet.TREATMENT);
        // マスタパネルを生成し、診療行為の検索対象コード範囲を設定する
        masterPanel = new MasterTabPanel(enumSet);
        masterPanel.setSearchClass(spec.getSearchCode());
        masterPanel.startCharge(testTable);

        // タイトルを設定しレイアウトする
        setTitle(spec.getName());
        Border b = BorderFactory.createEtchedBorder();
        testTable.setBorder(BorderFactory.createTitledBorder(b, spec.getName()));

        setLayout(new BorderLayout(0, GUIConst.DEFAULT_CMP_V_SPACE));
        add(testTable, BorderLayout.NORTH);
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
        masterPanel.stopCharge(testTable);
    }
}
