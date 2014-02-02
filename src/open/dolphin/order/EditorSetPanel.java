package open.dolphin.order;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.client.IStampEditorDialog;
import open.dolphin.client.StampEditorDialog;
import open.dolphin.client.StampModelEditor;
import open.dolphin.client.StampTask;
import open.dolphin.client.StampTree;
import open.dolphin.client.StampTreeNode;
import open.dolphin.client.TimeoutWarning;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.util.BeanUtils;

/**
 * EditorSetPanel
 *
 * @author Minagawa,Kazushi
 *
 */
public class EditorSetPanel extends JPanel implements IStampEditorDialog, PropertyChangeListener, TreeSelectionListener {
    
    private static final long serialVersionUID = -1749656093805207712L;
    
    private LBacteriaStampEditor bacteria;
    private LBaseChargeStampEditor baseCharge;
    private LDiagnosisEditor diagnosis;
    private LGeneralStampEditor general;
    private LInjectionStampEditor injection;
    private LInstractionChargeStampEditor instraction;
    private LMedStampEditor2 med;
    private LOtherStampEditor other;
    private LPhysiologyStampEditor physiology;
    private LRadiologyStampEditor radiology;
    private LSurgeryStampEditor surgery;
    private LTestStampEditor test;
    private LTreatmentStampEditor treatment;
    private MasterSetPanel masterSet;
    
    private StampModelEditor curEditor;
    
    private JPanel editorSet;
    private CardLayout cardLayout;
    
    private Hashtable<String, StampModelEditor> table;
    
    private JButton rightArrow;
    private JButton leftArrow;
    
    private PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);
    private Object editorValue;
    private StampTreeNode selectedNode;
    
    private Timer taskTimer;
    private StampTask worker;
    private ProgressMonitor monitor;
    
    /** EditorSetPanel を生成する。 */
    public EditorSetPanel() {
        editorSet = new JPanel();
        cardLayout = new CardLayout();
        editorSet.setLayout(cardLayout);
        initComponent();
    }
    
    /**
     * 編集したスタンプオブジェクトを返す。
     * @return 編集したスタンプオブジェクト
     */
    public Object getEditorValue() {
        return editorValue;
    }
    
    /**
     * 編集値をセットする。この属性は束縛プロパティであり、リスナへ通知される。
     * @param value 編集されたスタンプ
     */
    public void setEditorValue(Object value) {
        editorValue = value;
        boundSupport.firePropertyChange(IStampEditorDialog.EDITOR_VALUE_PROP, null, editorValue);
        curEditor.setValue(null);
    }
    
    public StampTreeNode getSelectedNode() {
        return selectedNode;
    }
    
    /**
     * スタンプボックスで選択されているノード（スタンプ）をセットする。
     * @param node スタンプボックスで選択されているスタンプノード
     */
    public void setSelectedNode(StampTreeNode node) {
        selectedNode = node;
    }
    
    public void close() {
        if (curEditor != null) {
            curEditor.dispose();
        }
        masterSet.dispose();
    }
    
    /**
     * プロパティチェンジリスナを登録する。
     * @param prop プロパティ名
     * @param listener プロパティチェンジリスナ
     */
    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        boundSupport.addPropertyChangeListener(prop, listener);
    }
    
    /**
     * プロパティチェンジリスナを削除する。
     * @param prop プロパティ名
     * @param listener プロパティチェンジリスナ
     */
    public void remopvePropertyChangeListener(String prop, PropertyChangeListener listener) {
        boundSupport.removePropertyChangeListener(prop, listener);
    }
    
    /**
     * スタンプボックスのタブが切り替えられた時、対応するエディタを show する。
     * @param show するエディタのエンティティ名
     */
    public void show(String entity) {
        
        // 現在エディタがあれば後始末する
        if (curEditor != null) {
            curEditor.dispose();
            curEditor.removePropertyChangeListener(StampEditorDialog.VALIDA_DATA_PROP, this);
            rightArrow.setEnabled(false);
            leftArrow.setEnabled(false);
        }
        
        // 要求されたエディタを開始する
        curEditor = table.get(entity);
        // このクラスは VALID_DATA_PROP のリスナになっている
        curEditor.addPropertyChangeListener(StampEditorDialog.VALIDA_DATA_PROP, this);
        curEditor.start();
        
        if (entity.equals("diagnosis")) {
            leftArrow.setEnabled(false);
            curEditor.setValue(null);
            
        } else {
            ModuleModel stamp = new ModuleModel();
            ModuleInfoBean stampInfo = new ModuleInfoBean();
            stampInfo.setStampName("エディタから発行...");
            stampInfo.setStampRole("p");
            stampInfo.setEntity(entity);
            stamp.setModuleInfo(stampInfo);
            curEditor.setValue(stamp);
        }
        
        if (curEditor instanceof LRadiologyStampEditor) {
            masterSet.setRadLocationEnabled(true);
        } else {
            masterSet.setRadLocationEnabled(false);
        }
        
        cardLayout.show(editorSet, entity);
    }
    
    /**
     * 編集中のスタンプの有効/無効の属性通知を受け、スタンプボックスへ登録する
     * 右向きボタンを制御する。
     */
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        
        if (prop.equals(StampEditorDialog.VALIDA_DATA_PROP)) {
            
            Boolean i = (Boolean) e.getNewValue();
            boolean state = i.booleanValue();
            
            if (state) {
                rightArrow.setEnabled(true);
            } else {
                rightArrow.setEnabled(false);
            }   
        }
    }
    
    /**
     * スタンプツリーで選択されたスタンプに応じて取り込みボタンを制御する。
     */
    public void valueChanged(TreeSelectionEvent e) {
        
        StampTree tree = (StampTree) e.getSource();
        StampTreeNode node =(StampTreeNode) tree.getLastSelectedPathComponent();
        boolean enabled = false;
        StampTreeNode selected = null;
        
        // ノードが葉で傷病名でない時のみ enabled にする
        // またその時以外は選択ノード属性をnullにする
        if (node != null && node.isLeaf()) {
            
            ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
               
            if (info.isSerialized() && (!info.getEntity().equals(IInfoModel.ENTITY_DIAGNOSIS)) ) {
                enabled = true;
                selected = node;
            }
        }
        
        leftArrow.setEnabled(enabled);
        setSelectedNode(selected);
    }
    
    public JButton getOkButton() {
        return null;
    }
    
    /**
     * 編集したスタンプをボックスへ通知するためのアクションリスナ。
     * 右向きボタンのリスナでエディタの編集値をgetし束縛プロパティに設定する。
     * 最後に右向きボタンをdisabledにする。
     */
    class EditorValueListener implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            Object obj = curEditor.getValue();
            setEditorValue(obj);
            rightArrow.setEnabled(false);
        }
    }
    
    /**
     * スタンプボックスで選択されているスタンプをエディタへ取り込んで編集するための
     * 左向きボタンのリスナ。
     */
    class EditStampListener implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            
            // StampInfoからスタンプをロードしエディタにセットする
            StampTreeNode node = getSelectedNode();
            if (node == null || !(node.getUserObject() instanceof ModuleInfoBean)) {
                return;
            }
            final ModuleInfoBean stampInfo = (ModuleInfoBean) node.getUserObject();
            
            final StampDelegater sdl = new StampDelegater();
            
            int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
            int delay = ClientContext.getInt("task.default.delay");
            int decideToPopup = ClientContext.getInt("task.default.decideToPopup");
            int milisToPopup = ClientContext.getInt("task.default.milisToPopup");
            String updateMsg = ClientContext.getString("task.default.searchMessage");
            
            worker = new StampTask(stampInfo.getStampId(), sdl, maxEstimation / delay);
            
            monitor = new ProgressMonitor(SwingUtilities.getWindowAncestor(editorSet), null, updateMsg, 0, maxEstimation / delay);
            monitor.setProgress(0);
            monitor.setMillisToDecideToPopup(milisToPopup);
            monitor.setMillisToPopup(decideToPopup);
            
            taskTimer = new javax.swing.Timer(delay, new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    
                    monitor.setProgress(worker.getCurrent());
                    
                    if (worker.isDone()) {
                        taskTimer.stop();
                        monitor.close();
                        
                        if (sdl.isNoError()) {
                            StampModel stampModel = worker.getStampModel();
                            if (stampModel != null) {
                                IInfoModel model = (IInfoModel) BeanUtils.xmlDecode(stampModel.getStampBytes());
                                if (model != null) {
                                    ModuleModel stamp = new ModuleModel();
                                    stamp.setModel(model);
                                    stamp.setModuleInfo(stampInfo);
                                    if (curEditor != null) {
                                        curEditor.setValue(stamp);
                                    }
                                }
                            }
                            
                        } else {
                            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(editorSet),
                                    sdl.getErrorMessage(),
                                    ClientContext.getFrameTitle("Stamp取得"),
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    } else if (worker.isTimeOver()) {
                        taskTimer.stop();
                        monitor.close();
                        String title = "Stamp取得";
                        new TimeoutWarning(SwingUtilities.getWindowAncestor(editorSet), title, null).start();
                    }
                }
            });
            leftArrow.setEnabled(false);
            worker.start();
            taskTimer.start();
        }
    }
    
    /**
     * GUI コンポーネントを生成する。
     */
    private void initComponent() {
        
        // 編集したスタンプをボックスへ登録する右向きボタンを生成する
        rightArrow = new JButton(ClientContext.getImageIcon("forwd_16.gif"));
        rightArrow.addActionListener(new EditorValueListener());
        rightArrow.setEnabled(false);
        
        // スタンプボックスのスタンプをセットテーブルへ取り込む左向きのボタンを生成する
        leftArrow = new JButton(ClientContext.getImageIcon("back_16.gif"));
        leftArrow.addActionListener(new EditStampListener());
        leftArrow.setEnabled(false);
        
        // マスターセットパネルを生成する
        masterSet = new MasterSetPanel();
        
        // エディタ(セットテーブル)を生成する
        bacteria = new LBacteriaStampEditor(this, masterSet);
        baseCharge = new LBaseChargeStampEditor(this, masterSet);
        diagnosis = new LDiagnosisEditor(this, masterSet);
        general = new LGeneralStampEditor(this, masterSet);
        injection = new LInjectionStampEditor(this, masterSet);
        instraction = new LInstractionChargeStampEditor(this, masterSet);
        med = new LMedStampEditor2(this, masterSet);
        other = new LOtherStampEditor(this, masterSet);
        physiology = new LPhysiologyStampEditor(this, masterSet);
        radiology = new LRadiologyStampEditor(this, masterSet);
        surgery = new LSurgeryStampEditor(this, masterSet);
        test = new LTestStampEditor(this, masterSet);
        treatment = new LTreatmentStampEditor(this, masterSet);
        
        // カードパネルにエディタを追加する
        editorSet.add(bacteria, "bacteriaOrder");
        editorSet.add(baseCharge, "baseChargeOrder");
        editorSet.add(diagnosis, "diagnosis");
        editorSet.add(general, "generalOrder");
        editorSet.add(injection, "injectionOrder");
        editorSet.add(instraction, "instractionChargeOrder");
        editorSet.add(med, "medOrder");
        editorSet.add(other, "otherOrder");
        editorSet.add(physiology, "physiologyOrder");
        editorSet.add(radiology, "radiologyOrder");
        editorSet.add(surgery, "surgeryOrder");
        editorSet.add(test, "testOrder");
        editorSet.add(treatment, "treatmentOrder");
        
        // カードパネルの PreferedSize を設定する
        editorSet.setPreferredSize(new Dimension(GUIConst.DEFAULT_EDITOR_WIDTH, GUIConst.DEFAULT_EDITOR_HEIGHT));
        
        // 配置する
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.add(editorSet, BorderLayout.NORTH);
        center.add(masterSet, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        BoxLayout box = new BoxLayout(btnPanel, BoxLayout.Y_AXIS);
        btnPanel.setLayout(box);
        btnPanel.add(Box.createVerticalStrut(100));
        btnPanel.add(rightArrow);
        btnPanel.add(leftArrow);
        btnPanel.add(Box.createVerticalGlue());
        
        this.setLayout(new BorderLayout(0, 0));
        this.add(center, BorderLayout.CENTER);
        this.add(btnPanel, BorderLayout.EAST);
        
        // 全体の PreferedSize を設定する
        this.setPreferredSize(GUIConst.DEFAULT_STAMP_EDITOR_SIZE);
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 0));
        
        // Hashテーブルに登録し show(entity) で使用する
        table = new Hashtable<String, StampModelEditor>();
        table.put("bacteriaOrder", bacteria);
        table.put("baseChargeOrder", baseCharge);
        table.put("diagnosis", diagnosis);
        table.put("generalOrder", general);
        table.put("injectionOrder", injection);
        table.put("instractionChargeOrder", instraction);
        table.put("medOrder", med);
        table.put("otherOrder", other);
        table.put("physiologyOrder", physiology);
        table.put("radiologyOrder", radiology);
        table.put("surgeryOrder", surgery);
        table.put("testOrder", test);
        table.put("treatmentOrder", treatment);
    }
}
