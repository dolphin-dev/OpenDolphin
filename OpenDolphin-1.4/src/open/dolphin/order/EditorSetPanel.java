package open.dolphin.order;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import open.dolphin.client.ClientContext;
import open.dolphin.client.StampTree;
import open.dolphin.client.StampTreeNode;
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
public class EditorSetPanel extends JPanel implements PropertyChangeListener, TreeSelectionListener {

    public static final String EDITOR_VALUE_PROP = "editorValue";

    // エディタ の組
    private AbstractStampEditor bacteria;
    private AbstractStampEditor baseCharge;
    private AbstractStampEditor diagnosis;
    private AbstractStampEditor general;
    private AbstractStampEditor injection;
    private AbstractStampEditor instraction;
    private AbstractStampEditor med;
    private AbstractStampEditor other;
    private AbstractStampEditor physiology;
    private AbstractStampEditor radiology;
    private AbstractStampEditor surgery;
    private AbstractStampEditor test;
    private AbstractStampEditor treatment;
    
    // 上記エディタを格納するカードパネル
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // 現在使用中のエディタ
    private AbstractStampEditor curEditor;
    
    // 辞書
    private Hashtable<String, AbstractStampEditor> table;

    // StampBox とやりとりするボタン
    private JButton rightArrow;
    private JButton leftArrow;

    // StampBox と連携するためのオブジェクト
    private PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);
    private Object editorValue;
    private StampTreeNode selectedNode;

    
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
        boundSupport.firePropertyChange(EditorSetPanel.EDITOR_VALUE_PROP, null, editorValue);
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

    /**
     * EditorSet を終了する。
     */
    public void close() {

        if (curEditor!=null) {
            curEditor.dispose();
            curEditor.remopvePropertyChangeListener(AbstractStampEditor.VALIDA_DATA_PROP, this);
        }
    }
    
    /**
     * プロパティチェンジリスナを登録する。
     * @param prop プロパティ名
     * @param listener プロパティチェンジリスナ
     */
    @Override
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

        // ORCA及びパスタブの場合
        if (table.get(entity)==null) {
            return;
        }
        
        // 現在エディタがあれば後始末する
        if (curEditor != null) {
            curEditor.dispose();
            curEditor.remopvePropertyChangeListener(AbstractStampEditor.VALIDA_DATA_PROP, this);
            rightArrow.setEnabled(false);
            leftArrow.setEnabled(false);
        }
        
        // 要求されたエディタに切り替える
        curEditor = table.get(entity);
        curEditor.addPropertyChangeListener(AbstractStampEditor.VALIDA_DATA_PROP, this);
        
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
        
        cardLayout.show(cardPanel, entity);
    }
    
    /**
     * 編集中のスタンプの有効/無効の属性通知を受け、右向きボタンを制御する。
     */
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        
        if (prop.equals(AbstractStampEditor.VALIDA_DATA_PROP)) {

            // 有効か無効かで右矢印ボタンを制御する
            Boolean i = (Boolean) e.getNewValue();
            boolean state = i.booleanValue();
            rightArrow.setEnabled(state);
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
        
        // ノードが葉で傷病名でない時のみ 取り込みボタン（左矢印）をenabled にする
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
    
    /**
     * 編集したスタンプをボックスへ通知するためのアクションリスナ。
     * 右向きボタンのリスナでエディタの編集値をgetし束縛プロパティに設定する。
     * 最後に右向きボタンをdisabledにする。
     */
    class EditorValueListener implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {

            // cureditor から値を取得し、自分自身のプロパティに設定する。
            // 束縛プロパティによりリスナへこの値が通知される。
            Object obj = curEditor.getValue();
            setEditorValue(obj);
            rightArrow.setEnabled(false);
        }
    }
    
    /**
     * スタンプボックスで選択されているスタンプをエディタへ取り込んで編集するための
     * 左向きボタンのリスナクラス。
     */
    class EditStampListener implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            
            // StampInfoからスタンプをロードしエディタにセットする
            StampTreeNode node = getSelectedNode();
            if (node == null || !(node.getUserObject() instanceof ModuleInfoBean)) {
                return;
            }
            final ModuleInfoBean stampInfo = (ModuleInfoBean) node.getUserObject();

            Runnable r = new Runnable() {

                public void run() {

                    final StampDelegater sdl = new StampDelegater();
                    final StampModel stampModel = sdl.getStamp(stampInfo.getStampId());

                    Runnable awt = new Runnable() {

                        public void run() {
                            
                            if (sdl.isNoError() && stampModel != null) {
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
                                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(cardPanel),
                                        sdl.getErrorMessage(),
                                        ClientContext.getFrameTitle("Stamp取得"),
                                        JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    };

                    SwingUtilities.invokeLater(awt);
                }
            };

            Thread t = new Thread(r);
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
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

        //
        // 個別のエディタを生成する
        //
        bacteria = new BaseEditor("bacteriaOrder", false);
        baseCharge = new BaseEditor("baseChargeOrder", false);
        diagnosis = new DiseaseEditor(false);
        general = new BaseEditor("generalOrder", false);
        injection = new BaseEditor("injectionOrder", false);
        instraction = new BaseEditor("instractionChargeOrder", false);
        med = new RpEditor("medOrder", false);
        other = new BaseEditor("otherOrder", false);
        physiology = new BaseEditor("physiologyOrder", false);
        radiology = new RadEditor("radiologyOrder", false);
        surgery = new BaseEditor("surgeryOrder", false);
        test = new BaseEditor("testOrder", false);
        treatment = new BaseEditor("treatmentOrder", false);

        // Hashテーブルに登録し show(entity) で使用する
        table = new Hashtable<String, AbstractStampEditor>();
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

        //
        // カードパネルにエディタを追加する
        //
        cardPanel.add(bacteria.getView(), "bacteriaOrder");
        cardPanel.add(baseCharge.getView(), "baseChargeOrder");
        cardPanel.add(diagnosis.getView(), "diagnosis");
        cardPanel.add(general.getView(), "generalOrder");
        cardPanel.add(injection.getView(), "injectionOrder");
        cardPanel.add(instraction.getView(), "instractionChargeOrder");
        cardPanel.add(med.getView(), "medOrder");
        cardPanel.add(other.getView(), "otherOrder");
        cardPanel.add(physiology.getView(), "physiologyOrder");
        cardPanel.add(radiology.getView(), "radiologyOrder");
        cardPanel.add(surgery.getView(), "surgeryOrder");
        cardPanel.add(test.getView(), "testOrder");
        cardPanel.add(treatment.getView(), "treatmentOrder");
        
        // StampBox との間にある矢印ボタンパネル
        JPanel btnPanel = new JPanel();
        BoxLayout box = new BoxLayout(btnPanel, BoxLayout.Y_AXIS);
        btnPanel.setLayout(box);
        btnPanel.add(Box.createVerticalStrut(100));
        btnPanel.add(rightArrow);
        btnPanel.add(leftArrow);
        btnPanel.add(Box.createVerticalGlue());

        // 配置する
        this.setLayout(new BorderLayout(0, 0));
        this.add(cardPanel, BorderLayout.CENTER);
        this.add(btnPanel, BorderLayout.EAST);
    }

    /** EditorSetPanel を生成する。 */
    public EditorSetPanel() {
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        initComponent();
    }
}
