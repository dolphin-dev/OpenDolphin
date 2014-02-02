package open.dolphin.order;

import javax.swing.*;
import javax.swing.event.*;

import open.dolphin.client.BlockGlass;
import open.dolphin.client.GUIFactory;
import open.dolphin.client.SeparatorPanel;

import java.awt.*;
import java.beans.*;
import java.util.EnumSet;
import open.dolphin.client.ClientContext;
import org.apache.log4j.Logger;

/**
 * マスタ検索パネルを格納するタブパネルクラス。
 * タブでマスタを切り替える。
 *
 * @author  Kazushi Minagawa, Digital Globe, INc.
 */
public class MasterTabPanel extends JPanel{
    
    private static final long serialVersionUID = 4282518548618120301L;
    
    /** 傷病名マスタのインデックス */
    private static final int DIGNOSIS_INDEX         = 0;
    
    /** 診療行為マスタのインデックス */
    private static final int MEDICAL_TRAET_INDEX    = 1;
    
    /** 医薬品マスタのインデックス */
    private static final int MEDICAL_SUPPLY_INDEX   = 2;
    
    /** 用法マスのインデックス */
    private static final int ADMIN_INDEX            = 3;
    
    /** 注射薬マスタのインデックス */
    private static final int INJECTION_INDEX        = 4;
    
    /** 特定器材マスタのインデックス */
    private static final int TOOL_MATERIAL_INDEX    = 5;

    /** マスタ検索パネルを格納するタブペイン */
    private JTabbedPane tabbedPane;
    
    /** 傷病名マスタ */
    private DiagnosisMaster diagnosis;
    
    /** 診療行為マスタ */
    private TreatmentMaster treatment;
    
    /** 医薬品マスタ */
    private MedicalSuppliesMaster medicalSupplies;
    
    /** 用法マスタ */
    private AdminMaster administration;
    
    /** 注射薬マスタ */
    private InjectionMedicineMaster injection;
    
    /** 特定器材マスタ */
    private ToolMaterialMaster toolMaterial;
    
    /** 使用するマスタの Set */
    private EnumSet<ClaimConst.MasterSet> enumSet;
    
    private BusyListener busyListener;
    private ItemCountListener itemCountListener;
    
    // Status 関連
    /** Progressbar */
    //private UltraSonicProgressLabel pulse;
    
    /** 件数表示ラベル */
    private JLabel countLabel;
    
    /** 診療行為ラベル */
    private JLabel classCodeLabel;
    
    private Logger logger;
    
    /** 
     * Creates new MasterTabPanel
     */
    public MasterTabPanel() {
        super(new BorderLayout(0, 11));
        logger = ClientContext.getBootLogger();
        enumSet = EnumSet.of(
                ClaimConst.MasterSet.TREATMENT,
                ClaimConst.MasterSet.MEDICAL_SUPPLY,
                ClaimConst.MasterSet.ADMINISTRATION,
                ClaimConst.MasterSet.INJECTION_MEDICINE,
                ClaimConst.MasterSet.TOOL_MATERIAL);
        intialize();
    }
    
    /**
     * Creates new MasterTabPanel 
     * @param enumSet 使用するマスタの Set
     */
    public MasterTabPanel(EnumSet<ClaimConst.MasterSet> enumSet) {
        
        super(new BorderLayout(0, 11));
        
        logger = ClientContext.getBootLogger();
        this.enumSet = enumSet;
        
        if (this.enumSet != null) {
            logger.debug("enumSet is not null");
        } else {
            logger.debug("enumSet is null");
        }
        
        intialize();
    }
    
    /**
     * Glass pane を設定する。
     * @param glass イベントブロックする Glass Pane 
     */
    public void setGlass(BlockGlass glass) {
    }
    
    /**
     * 初期化する。
     * 使用するマスタ検索パネルを生成しタブへ格納する。
     */
    private void intialize() {
        
        logger.debug("initialize start");
        
        // 超音波進捗バーを生成する
        //pulse = new UltraSonicProgressLabel();
        
        // 傷病名マスタを生成する
        if (enumSet.contains(ClaimConst.MasterSet.DIAGNOSIS)) {
            diagnosis = new DiagnosisMaster(ClaimConst.MasterSet.DIAGNOSIS.getName());
            logger.debug("enumSet contains DIAGNOSIS");
        }
        
        // 診療行為マスタを生成する
        if (enumSet.contains(ClaimConst.MasterSet.TREATMENT)) {
            treatment = new TreatmentMaster(ClaimConst.MasterSet.TREATMENT.getName());
            logger.debug("enumSet contains TREATMENT");
        }
        
        // 医薬品マスタを生成する
        if (enumSet.contains(ClaimConst.MasterSet.MEDICAL_SUPPLY)) {
            medicalSupplies = new MedicalSuppliesMaster(ClaimConst.MasterSet.MEDICAL_SUPPLY.getName());
            logger.debug("enumSet contains medicalSupplies");
        }
        
        // 用法マスタを生成する
        if (enumSet.contains(ClaimConst.MasterSet.ADMINISTRATION)) {
            administration = new AdminMaster(ClaimConst.MasterSet.ADMINISTRATION.getName());
            logger.debug("enumSet contains ADMINISTRATION");
        }
        
        // 注射薬マスタを生成する
        if (enumSet.contains(ClaimConst.MasterSet.INJECTION_MEDICINE)) {
            injection = new InjectionMedicineMaster(ClaimConst.MasterSet.INJECTION_MEDICINE.getName());
            logger.debug("enumSet contains INJECTION_MEDICINE");
        }
        
        // 特定器材マスタを生成する
        if (enumSet.contains(ClaimConst.MasterSet.TOOL_MATERIAL)) {
            toolMaterial = new ToolMaterialMaster(ClaimConst.MasterSet.TOOL_MATERIAL.getName());
            logger.debug("enumSet contains toolMaterial");
        }
        
        // BUSY リスナを生成する
        busyListener = new BusyListener();
        
        // 件数リスナを生成する
        itemCountListener = new ItemCountListener();
        
        // タブペインを生成しマスタを格納する
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab(ClaimConst.MasterSet.DIAGNOSIS.getDispName(), diagnosis);
        tabbedPane.addTab(ClaimConst.MasterSet.TREATMENT.getDispName(), treatment);
        tabbedPane.addTab(ClaimConst.MasterSet.MEDICAL_SUPPLY.getDispName(), medicalSupplies);
        tabbedPane.addTab(ClaimConst.MasterSet.ADMINISTRATION.getDispName(), administration);
        tabbedPane.addTab(ClaimConst.MasterSet.INJECTION_MEDICINE.getDispName(), injection);
        tabbedPane.addTab(ClaimConst.MasterSet.TOOL_MATERIAL.getDispName(), toolMaterial);
        logger.debug("craeted tabbedPane");
        
        // タブへ ChangeListener を登録する
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                //
                // 選択されたインデックスに対応するマスタへ enter() を通知する
                //
                int index = tabbedPane.getSelectedIndex();
                MasterPanel masterPanel = (MasterPanel) tabbedPane.getComponentAt(index);
                masterPanel.enter();
            }
        });
        
        //
        // Tab の enable/disable制御を行う
        //
        enabled();
        
        // 件数 Label を生成する
        countLabel = new JLabel(paddCount(0));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // 診療行為番号を表示するラベルを生成する
        classCodeLabel = new JLabel("");
        classCodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Fontを設定する
        Font font = GUIFactory.createSmallFont();
        countLabel.setFont(font);
        classCodeLabel.setFont(font);
        
        // Statusパネルを生成する
        JPanel statusP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //statusP.add(pulse);
        statusP.add(countLabel);
        statusP.add(new SeparatorPanel());
        statusP.add(classCodeLabel);
        statusP.add(Box.createHorizontalStrut(6));
        
        // レイアウトする
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(statusP, BorderLayout.SOUTH);
    }
    
    /**
     * オーダクラス(スタンプボックスのタブに関連づけされている番号)を設定する。
     * このコードをもつ診療行為をマスタから検索する。
     * @param code オーダクラス
     */
    public void setSearchClass(String serchClass) {
        treatment.setSearchClass(serchClass);
        if (serchClass != null) {
            classCodeLabel.setText("診療行為:" + serchClass);
        } else {
            classCodeLabel.setText("診療行為:100-999");
        }
    }
    
    /**
     * 撮影部位検索の enable/disable を制御する。
     * @param enabled enableにする時 true
     */
    public void setRadLocationEnabled(boolean enabled) {
        treatment.setRadLocationEnabled(enabled);
    }
    
    /**
     * マスタ検索パネルに項目が選択された時のリスナを登録する。
     * マスタ検索パネルの結果テーブルでの項目選択は束縛プロパティになっている。
     * @param l 選択項目プロパティへのリスナ
     */
    private void addListeners(PropertyChangeListener l) {
        
        // 
        // 生成するスタンプによって使用するマスタのセットがある
        // 使用されるかどうかを Set で判断する
        //
        if (enumSet.contains(ClaimConst.MasterSet.DIAGNOSIS)) {
            diagnosis.addPropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            diagnosis.addPropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            diagnosis.addPropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.TREATMENT)) {
            treatment.addPropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            treatment.addPropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            treatment.addPropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.MEDICAL_SUPPLY)) {
            medicalSupplies.addPropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            medicalSupplies.addPropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            medicalSupplies.addPropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
        
        // 用法
        if (enumSet.contains(ClaimConst.MasterSet.ADMINISTRATION)) {
            administration.addPropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            administration.addPropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            administration.addPropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.INJECTION_MEDICINE)) {
            injection.addPropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            injection.addPropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            injection.addPropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.TOOL_MATERIAL)) {
            toolMaterial.addPropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            toolMaterial.addPropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            toolMaterial.addPropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
    }
    
    /**
     * プログラムの終了時に項目選択への束縛リスナを削除する。
     * @param l マスタ項目選択への束縛リスナ
     */
    private void removeListeners(PropertyChangeListener l) {
        
        if (enumSet.contains(ClaimConst.MasterSet.DIAGNOSIS)) {
            diagnosis.removePropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            diagnosis.removePropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            diagnosis.removePropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.TREATMENT)) {
            treatment.removePropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            treatment.removePropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            treatment.removePropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.MEDICAL_SUPPLY)) {
            medicalSupplies.removePropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            medicalSupplies.removePropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            medicalSupplies.removePropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
        
        // 用法
        if (enumSet.contains(ClaimConst.MasterSet.ADMINISTRATION)) {
            administration.removePropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            administration.removePropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            administration.removePropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.INJECTION_MEDICINE)) {
            injection.removePropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            injection.removePropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            injection.removePropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.TOOL_MATERIAL)) {
            toolMaterial.removePropertyChangeListener(MasterPanel.SELECTED_ITEM_PROP, l);
            toolMaterial.removePropertyChangeListener(MasterPanel.BUSY_PROP, busyListener);
            toolMaterial.removePropertyChangeListener(MasterPanel.ITEM_COUNT_PROP, itemCountListener);
        }
    }
    
    /**
     * 使用されるマスタのタブを enabled にする。
     * 使用されないマスタのタブは disabled にする。
     */
    private void enabled() {
        
        tabbedPane.setEnabledAt(DIGNOSIS_INDEX, false);
        tabbedPane.setEnabledAt(MEDICAL_TRAET_INDEX, false);
        tabbedPane.setEnabledAt(MEDICAL_SUPPLY_INDEX, false);
        tabbedPane.setEnabledAt(ADMIN_INDEX, false);
        tabbedPane.setEnabledAt(INJECTION_INDEX, false);
        tabbedPane.setEnabledAt(TOOL_MATERIAL_INDEX, false);
        
        if (enumSet.contains(ClaimConst.MasterSet.DIAGNOSIS)) {
            tabbedPane.setEnabledAt(DIGNOSIS_INDEX, true);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.TREATMENT)) {
            tabbedPane.setEnabledAt(MEDICAL_TRAET_INDEX, true);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.MEDICAL_SUPPLY)) {
            tabbedPane.setEnabledAt(MEDICAL_SUPPLY_INDEX, true);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.ADMINISTRATION)) {
            tabbedPane.setEnabledAt(ADMIN_INDEX, true);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.INJECTION_MEDICINE)) {
            tabbedPane.setEnabledAt(INJECTION_INDEX, true);
        }
        
        if (enumSet.contains(ClaimConst.MasterSet.TOOL_MATERIAL)) {
            tabbedPane.setEnabledAt(TOOL_MATERIAL_INDEX, true);
        }
    }
    
    /**
     * 傷病名スタンプ作成を開始する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void startDiagnosis(PropertyChangeListener l) {
        classCodeLabel.setText("MEDIS ICD10");
        addListeners(l);
        tabbedPane.setSelectedIndex(DIGNOSIS_INDEX);
        diagnosis.enter();
    }
    
    /**
     * 傷病名スタンプ作成を終了する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void stopDiagnosis(PropertyChangeListener l) {
        removeListeners(l);
    }
    
    /**
     * 処方スタンプ作成を開始する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void startMedicine(PropertyChangeListener l) {
        // 器材・医薬品・用法・注射薬
        classCodeLabel.setText("診療行為:210-230");
        addListeners(l);
        tabbedPane.setSelectedIndex(MEDICAL_SUPPLY_INDEX);
    }
    
    /**
     * 処方スタンプ作成を終了する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void stopMedicine(PropertyChangeListener l) {
        removeListeners(l);
    }
    
    /**
     * 注射スタンプ作成を開始する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void startInjection(PropertyChangeListener l) {
        //診療行為・注射薬・器材
        addListeners(l);
        tabbedPane.setSelectedIndex(MEDICAL_TRAET_INDEX);
    }
    
    /**
     * 注射スタンプ作成を終了する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void stopInjection(PropertyChangeListener l) {
        removeListeners(l);
    }
    
    /**
     * 診断料スタンプ作成を開始する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void startCharge(PropertyChangeListener l) {
        // 診療行為設定
        addListeners(l);
        tabbedPane.setSelectedIndex(MEDICAL_TRAET_INDEX);
    }
    
    /**
     * 診断料スタンプ作成を終了する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void stopCharge(PropertyChangeListener l) {
        removeListeners(l);
    }
    
    /**
     * 検査スタンプ作成を開始する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void startTest(PropertyChangeListener l) {
        // 診療行為・器材・医薬品・注射薬
        addListeners(l);
        tabbedPane.setSelectedIndex(MEDICAL_TRAET_INDEX);
    }
    
    /**
     * 検査スタンプ作成を終了する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void stopTest(PropertyChangeListener l) {
        removeListeners(l);
    }
    
    /**
     * 汎用スタンプ作成を開始する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void startGeneral(PropertyChangeListener l) {
        // 汎用検索
        treatment.setSearchClass(null);
        
        // 診療行為・器材・医薬品・注射薬
        addListeners(l);
        tabbedPane.setSelectedIndex(MEDICAL_TRAET_INDEX);
    }
    
    /**
     * 汎用スタンプ作成を終了する。
     * @param マスタ項目選択への束縛リスナ
     */
    public void stopGeneral(PropertyChangeListener l) {
        removeListeners(l);
    }
        
    /**
     * 親フレームの GlassPane を返す。
     * @return 親フレームの BlockGlass
     */
    protected BlockGlass getBlockGlass() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null && w instanceof JDialog) {
            JDialog frame = (JDialog) w;
            Component c = frame.getGlassPane();
            return c != null && c instanceof BlockGlass ? (BlockGlass) c : null;
        }
        return null;
    }
    
    /**
     * Block する。
     */
    protected void block() {
        BlockGlass glass = getBlockGlass();
        if (glass != null) {
            glass.block();
        }
    }
    
    /**
     * Unblock する。
     */
    protected void unblock() {
        BlockGlass glass = getBlockGlass();
        if (glass != null) {
            glass.unblock();
        }
    }
    
    protected class BusyListener implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent e) {
            
            if (e.getPropertyName().equals(MasterPanel.BUSY_PROP)) {
                
                boolean busy = ((Boolean)e.getNewValue()).booleanValue();
                if (busy) {
                    //glass.start();
                    countLabel.setText("件数:  ? ");
                    block();
                } else {
                    //glass.stop();
                    unblock();
                }
            }
        }
    }
    
    protected class ItemCountListener implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent e) {
            
            if (e.getPropertyName().equals(MasterPanel.ITEM_COUNT_PROP)) {
                int count = ((Integer)e.getNewValue()).intValue();
                countLabel.setText(paddCount(count));
            }
        }
    }
    
    private String paddCount(int num) {
        StringBuilder sb = new StringBuilder();
        sb.append("件数:");
        String numStr = String.valueOf(num);
        int len = numStr.length();
        int cnt = 4 - len;
        for (int i = 0; i < cnt; i++) {
            sb.append(" ");
        }
        sb.append(numStr);
        return sb.toString();
    }
}
