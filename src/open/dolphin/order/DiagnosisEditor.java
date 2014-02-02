package open.dolphin.order;

import javax.swing.*;
import javax.swing.border.Border;

import open.dolphin.client.*;
import open.dolphin.client.GUIConst;

import java.awt.*;
import java.util.*;

/**
 * 傷病名エディタクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DiagnosisEditor extends StampModelEditor {

    /** エディタ名 */
    private static final String EDITOR_NAME = "傷病名";
    /** 傷病名編集テーブル */
    private DiagnosisTablePanel diagnosisTable;
    /** マスターセットパネル */
    private MasterTabPanel masterPanel;

    /** 
     * Creates new DiagnosisEditor 
     */
    public DiagnosisEditor() {
    }

    /**
     * プログラムを開始する。
     */
    @Override
    public void start() {

        setTitle(EDITOR_NAME);

        //
        // 傷病名編集テーブルを生成する
        //
        diagnosisTable = new DiagnosisTablePanel(this);
        Border b = BorderFactory.createEtchedBorder();
        diagnosisTable.setBorder(BorderFactory.createTitledBorder(b, EDITOR_NAME));

        //
        // 傷病名で使用するマスタのセットを生成する
        //
        EnumSet<ClaimConst.MasterSet> set = EnumSet.of(
                ClaimConst.MasterSet.DIAGNOSIS);

        //
        // マスターセットを生成する
        //
        masterPanel = new MasterTabPanel(set);
        masterPanel.startDiagnosis(diagnosisTable);

        //
        // 全体をレイアウトする
        //
        setLayout(new BorderLayout(0, GUIConst.DEFAULT_CMP_V_SPACE));
        add(diagnosisTable, BorderLayout.NORTH);
        add(masterPanel, BorderLayout.CENTER);
        setPreferredSize(GUIConst.DEFAULT_STAMP_EDITOR_SIZE);
    }

    /**
     * 編集した傷病名を返す。
     * @return 編集した RegisteredDiagnosisModel
     */
    public Object getValue() {
        return diagnosisTable.getValue();
    }

    /**
     * 編集する傷病名を設定する。
     * @param val 編集する RegisteredDiagnosisModel
     */
    public void setValue(Object val) {
        diagnosisTable.setValue((Object[]) val);
    }

    /**
     * リソースを解放する。
     */
    @Override
    public void dispose() {
        masterPanel.stopDiagnosis(diagnosisTable);
    }
}
