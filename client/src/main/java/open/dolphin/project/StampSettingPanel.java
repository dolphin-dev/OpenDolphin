package open.dolphin.project;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import javax.swing.*;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.GUIFactory;
import open.dolphin.helper.GridBagBuilder;
import open.dolphin.util.ZenkakuUtils;

/**
 * KarteSettingPanel
 *
 * @author Minagawa,Kazushi
 */
public class StampSettingPanel extends AbstractSettingPanel {

    private static final String ID = "stampSetting";
    private static final String TITLE = "スタンプ";
//minagawa^ Icon Server    
    private static final String ICON = "icon_stamp_settings_small";
//minagawa$    
    
    // Stamp
    private JRadioButton replaceStamp;
    private JRadioButton showAlert;
    private JCheckBox stampSpace;
    private JCheckBox laboFold;
    private JTextField defaultZyozaiNum;
    private JTextField defaultMizuyakuNum;
    private JTextField defaultSanyakuNum;
    private JTextField defaultCapsuleNum;
    private JTextField defaultRpNum;
    private JCheckBox masterItemColoring;
    private JRadioButton stampEditorButtonIcon;
    private JRadioButton stampEditorButtonText;
    private JCheckBox mergeWithSameAdmin;
//minagawa^ LSC Test
    private JCheckBox showStampNameOnKarte;
//minagawa$    
    
    private StampModel model;
    private boolean ok = true;

    public StampSettingPanel() {
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }

    /**
     * 設定画面を開始する。
     */
    @Override
    public void start() {

        // モデルを生成し初期化する
        model = new StampModel();
        model.populate(getProjectStub());

        // GUI を構築する
        initComponents();

        // bindModel
        bindModelToView();

    }

    /**
     * 設定値を保存する。
     */
    @Override
    public void save() {
        bindViewToModel();
        model.restore(getProjectStub());
    }

    /**
     * GUI を構築する。
     */
    private void initComponents() {

        // Buttons, Fields
        replaceStamp = new JRadioButton("置き換える");
        showAlert = new JRadioButton("警告する");
        stampSpace = new JCheckBox("DnD時にスタンプの間隔を空ける");
        laboFold = new JCheckBox("検体検査の項目を折りたたみ表示する");
        defaultZyozaiNum = new JTextField(3);
        defaultMizuyakuNum = new JTextField(3);
        defaultSanyakuNum = new JTextField(3);
        defaultCapsuleNum = new JTextField(3);
        defaultRpNum = new JTextField(3);
        defaultZyozaiNum.setHorizontalAlignment(SwingConstants.RIGHT);
        defaultMizuyakuNum.setHorizontalAlignment(SwingConstants.RIGHT);
        defaultSanyakuNum.setHorizontalAlignment(SwingConstants.RIGHT);
        defaultCapsuleNum.setHorizontalAlignment(SwingConstants.RIGHT);
        defaultRpNum.setHorizontalAlignment(SwingConstants.RIGHT);
        masterItemColoring = new JCheckBox("マスター項目をカラーリングする");
        stampEditorButtonIcon = new JRadioButton("アイコン");
        stampEditorButtonText = new JRadioButton("テキスト");
        mergeWithSameAdmin = new JCheckBox("同じ用法をまとめる");
//minagawa^ LSC Test
        showStampNameOnKarte = new JCheckBox("カルテ展開時にスタンプ名を表示する");
//minagawa$        
        
        // Button Group
        ButtonGroup bg = new ButtonGroup();
        bg.add(replaceStamp);
        bg.add(showAlert);
        
        bg = new ButtonGroup();
        bg.add(stampEditorButtonIcon);
        bg.add(stampEditorButtonText);

        //----------------------------------------------------------------
        // Panel
        //----------------------------------------------------------------
        JPanel stampPanel = new JPanel();
        stampPanel.setLayout(new BoxLayout(stampPanel, BoxLayout.Y_AXIS));

        // 動作
        GridBagBuilder gbb = new GridBagBuilder("スタンプ動作の設定");
        int row = 0;
        JLabel label = new JLabel("スタンプの上にDnDした場合:", SwingConstants.RIGHT);
        JPanel stmpP = GUIFactory.createRadioPanel(new JRadioButton[]{replaceStamp, showAlert});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(stmpP, 1, row, 1, 1, GridBagConstraints.WEST);
        row++;
        gbb.add(stampSpace, 0, row, 2, 1, GridBagConstraints.WEST);
        row++;
        gbb.add(laboFold, 0, row, 2, 1, GridBagConstraints.WEST);
        row++;
        gbb.add(showStampNameOnKarte, 0, row, 2, 1, GridBagConstraints.WEST);
        stampPanel.add(gbb.getProduct());
        
        // デフォルト数量
        gbb = new GridBagBuilder("スタンプエディタのデフォルト数量");
        row = 0;
        label = new JLabel("錠剤の場合:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(createUnitFieldPanel(defaultZyozaiNum, "T"), 1, row, 1, 1, GridBagConstraints.WEST);
        row++;
        label = new JLabel("水薬の場合:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(createUnitFieldPanel(defaultMizuyakuNum, "ml"), 1, row, 1, 1, GridBagConstraints.WEST);
        row++;
        label = new JLabel("散薬の場合:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(createUnitFieldPanel(defaultSanyakuNum, "g"), 1, row, 1, 1, GridBagConstraints.WEST);
        row++;
        label = new JLabel("カプセルの場合:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(createUnitFieldPanel(defaultCapsuleNum, "カプセル"), 1, row, 1, 1, GridBagConstraints.WEST);
        row++;
        label = new JLabel("処方日数:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(createUnitFieldPanel(defaultRpNum, "日/回"), 1, row, 1, 1, GridBagConstraints.WEST);
        stampPanel.add(gbb.getProduct());
        
        // 同じ用法の処方をまとめる
        gbb = new GridBagBuilder("処方");
        row = 0;
        gbb.add(mergeWithSameAdmin, 0, row, 2, 1, GridBagConstraints.WEST);
        // ***設定項目の移動***
        //stampPanel.add(gbb.getProduct());
        JPanel panel = new JPanel();
        panel.add(gbb.getProduct());
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        gbb = new GridBagBuilder("スタンプエディターのボタンタイプ");
        row = 0;
        JPanel stButton = GUIFactory.createRadioPanel(new JRadioButton[]{stampEditorButtonIcon, stampEditorButtonText});
        gbb.add(stButton, 0, row, 1, 1, GridBagConstraints.WEST);
        panel.add(gbb.getProduct());
        stampPanel.add(panel);

        // マスター検索
        gbb = new GridBagBuilder("マスター検索");
        row = 0;
        gbb.add(masterItemColoring, 0, row, 2, 1, GridBagConstraints.WEST);
        stampPanel.add(gbb.getProduct());

        getUI().setLayout(new BorderLayout());
//s.oh^ 機能改善
        //getUI().add(stampPanel);
        setUI(stampPanel);
//s.oh$
    }

    private void checkState() {

        boolean newOk = true;

        if (ok != newOk) {
            ok = newOk;
            if (ok) {
                setState(AbstractSettingPanel.State.VALID_STATE);
            } else {
                setState(AbstractSettingPanel.State.INVALID_STATE);
            }
        }
    }

    public void inspectorChanged(int state) {
        if (state == ItemEvent.SELECTED) {
            checkState();
        }
    }

    /**
     * ModelToView
     */
    private void bindModelToView() {

        // スタンプ動作
        replaceStamp.setSelected(model.isReplaceStamp());
        showAlert.setSelected(!model.isReplaceStamp());
        stampSpace.setSelected(model.isStampSpace());
        laboFold.setSelected(model.isLaboFold());
        defaultZyozaiNum.setText(model.getDefaultZyozaiNum());
        defaultMizuyakuNum.setText(model.getDefaultMizuyakuNum());
        defaultSanyakuNum.setText(model.getDefaultSanyakuNum());
        defaultCapsuleNum.setText(model.getDefaultCapsuleNum());
        defaultRpNum.setText(model.getDefaultRpNum());
        defaultZyozaiNum.addFocusListener(AutoRomanListener.getInstance());
        defaultMizuyakuNum.addFocusListener(AutoRomanListener.getInstance());
        defaultSanyakuNum.addFocusListener(AutoRomanListener.getInstance());
        defaultCapsuleNum.addFocusListener(AutoRomanListener.getInstance());
        defaultRpNum.addFocusListener(AutoRomanListener.getInstance());
        masterItemColoring.setSelected(model.isMasterItemColoring());
        if (model.getEditorButtonType().equals("icon")) {
            stampEditorButtonIcon.doClick();
        } else if (model.getEditorButtonType().equals("text")) {
            stampEditorButtonText.doClick();
        }
        // 同一処方
        mergeWithSameAdmin.setSelected(model.isMergeWithSameAdmin());
//minagawa^ LSC Test
        showStampNameOnKarte.setSelected(model.isShowStampName());
//minagawa$        

        // この設定画面は常に有効状態である
        setState(AbstractSettingPanel.State.VALID_STATE);
    }

    /**
     * ViewToModel
     */
    private void bindViewToModel() {

        // スタンプ関連
        model.setReplaceStamp(replaceStamp.isSelected());
        model.setStampSpace(stampSpace.isSelected());
        model.setLaboFold(laboFold.isSelected());
        model.setDefaultZyozaiNum(defaultZyozaiNum.getText().trim());
        model.setDefaultMizuyakuNum(defaultMizuyakuNum.getText().trim());
        model.setDefaultSanyakuNum(defaultSanyakuNum.getText().trim());
        model.setDefaultCapsuleNum(defaultCapsuleNum.getText().trim());
        model.setDefaultRpNum(defaultRpNum.getText().trim());
        model.setMasterItemColoring(masterItemColoring.isSelected());
        if (stampEditorButtonIcon.isSelected()) {
            model.setEditorButtonType("icon");
        } else if (stampEditorButtonText.isSelected()) {
            model.setEditorButtonType("text");
        }
        // 同一処方
        model.setMergeWithSameAdmin(mergeWithSameAdmin.isSelected());
//minagawa^ LSC Test
        model.setShowStampName(showStampNameOnKarte.isSelected());
//minagawa$        
    }

    /**
     * 画面モデルクラス。
     */
    class StampModel {

        // スタンプ動作
        private boolean replaceStamp;
        private boolean stampSpace;
        private boolean laboFold;
        private String defaultZyozaiNum;
        private String defaultMizuyakuNum;
        private String defaultSanyakuNum;
        private String defaultCapsuleNum;
        private String defaultRpNum;
        private boolean itemColoring;
        private String editorButtonType;
        private boolean mergeWithSameAdmin;
//minagawa^ LSC Test
        private boolean showStampName;
//minagawa$        

        /**
         * ProjectStub から populate する。
         */
        public void populate(ProjectStub stub) {

            // スタンプの上にスタンプを DnD した場合に置き換えるかどうか
            setReplaceStamp(Project.getBoolean(Project.STAMP_REPLACE));  // stub.isReplaceStamp()

            // スタンプのDnDで間隔を空けるかどうか
            setStampSpace(Project.getBoolean(Project.STAMP_SPACE));    // stub.isStampSpace()

            // 検体検査スタンプを折りたたみ表示するかどうか
            setLaboFold(Project.getBoolean(Project.LABTEST_FOLD));  // stub.isLaboFold()

            //-------------------
            
            // 錠剤のデフォルト数量
            setDefaultZyozaiNum(Project.getString(Project.DEFAULT_ZYOZAI_NUM));  // stub.getDefaultZyozaiNum()

            // 水薬のデフォルト数量
            setDefaultMizuyakuNum(Project.getString(Project.DEFAULT_MIZUYAKU_NUM));    // stub.getDefaultMizuyakuNum()

            // 散薬のデフォルト数量
            setDefaultSanyakuNum(Project.getString(Project.DEFAULT_SANYAKU_NUM)); // stub.getDefaultSanyakuNum()
            
            // カプセルのデフォルト数量
            setDefaultCapsuleNum(Project.getString(Project.DEFAULT_CAPSULE_NUM));

            // 処方日数のデフォルト
            setDefaultRpNum(Project.getString(Project.DEFAULT_RP_NUM));  // stub.getDefaultRpNum()
            
            // 同じ用法をまとめる
            setMergeWithSameAdmin(Project.getBoolean(Project.KARTE_MERGE_RP_WITH_SAME_ADMIN));
            
            //-------------------

            // マスタ項目をカラーリングする
            setMasterItemColoring(Project.getBoolean(Project.MASTER_SEARCH_ITEM_COLORING));    // stub.getMasterItemColoring()
            
            // スタンプエディタのボタンタイプ
            setEditorButtonType(Project.getString(Project.STAMP_EDITOR_BUTTON_TYPE));
            
//minagawa^ LSC Test
            setShowStampName(Project.getBoolean("karte.show.stampName"));
//minagawa$
        }

        /**
         * ProjectStubへ保存する。
         */
        public void restore(ProjectStub stub) {

            Project.setBoolean(Project.STAMP_REPLACE, isReplaceStamp());    //stub.setReplaceStamp(isReplaceStamp());

            Project.setBoolean(Project.STAMP_SPACE, isStampSpace());    //stub.setStampSpace(isStampSpace());

            Project.setBoolean(Project.LABTEST_FOLD, isLaboFold()); //stub.setLaboFold(isLaboFold());

            //-------------------------------
            String test = testNumber(getDefaultZyozaiNum());
            if (test != null) {
                Project.setString(Project.DEFAULT_ZYOZAI_NUM, test);    //stub.setDefaultZyozaiNum(test);
            }

            test = testNumber(getDefaultMizuyakuNum());
            if (test != null) {
                Project.setString(Project.DEFAULT_MIZUYAKU_NUM, test);  //stub.setDefaultMizuyakuNum(test);
            }

            test = testNumber(getDefaultSanyakuNum());
            if (test != null) {
                Project.setString(Project.DEFAULT_SANYAKU_NUM, test);   //stub.setDefaultSanyakuNum(test);
            }
            
            test = testNumber(getDefaultCapsuleNum());
            if (test != null) {
                Project.setString(Project.DEFAULT_CAPSULE_NUM, test);   //stub.setDefaultSanyakuNum(test);
            }

            test = testNumber(getDefaultRpNum());
            if (test != null) {
                Project.setString(Project.DEFAULT_RP_NUM, test);    //stub.setDefaultRpNum(test);
            }
            //-------------------------------

            Project.setBoolean(Project.MASTER_SEARCH_ITEM_COLORING, isMasterItemColoring());    //stub.setMasterItemColoring(isMasterItemColoring());
            
            String btype =  stampEditorButtonIcon.isSelected() ? "icon" : "text";
            Project.setString(Project.STAMP_EDITOR_BUTTON_TYPE, btype);
            
            Project.setBoolean(Project.KARTE_MERGE_RP_WITH_SAME_ADMIN, isMergeWithSameAdmin());
            
//minagawa^ LSC Test
            Project.setBoolean("karte.show.stampName", isShowStampName());
//minagawa$            
        }

        public boolean isReplaceStamp() {
            return replaceStamp;
        }

        public void setReplaceStamp(boolean replaceStamp) {
            this.replaceStamp = replaceStamp;
        }

        public boolean isStampSpace() {
            return stampSpace;
        }

        public void setStampSpace(boolean stampSpace) {
            this.stampSpace = stampSpace;
        }

        public boolean isLaboFold() {
            return laboFold;
        }

        public void setLaboFold(boolean laboFold) {
            this.laboFold = laboFold;
        }

        public String getDefaultZyozaiNum() {
            return defaultZyozaiNum;
        }

        public void setDefaultZyozaiNum(String defaultZyozaiNum) {
            this.defaultZyozaiNum = defaultZyozaiNum;
        }

        public String getDefaultMizuyakuNum() {
            return defaultMizuyakuNum;
        }

        public void setDefaultMizuyakuNum(String defaultMizuyakuNum) {
            this.defaultMizuyakuNum = defaultMizuyakuNum;
        }

        public String getDefaultSanyakuNum() {
            return defaultSanyakuNum;
        }

        public void setDefaultSanyakuNum(String defaultSanyakuNum) {
            this.defaultSanyakuNum = defaultSanyakuNum;
        }
        
        public String getDefaultCapsuleNum() {
            return defaultCapsuleNum;
        }

        public void setDefaultCapsuleNum(String defaultCapsuleNum) {
            this.defaultCapsuleNum = defaultCapsuleNum;
        }

        public String getDefaultRpNum() {
            return defaultRpNum;
        }

        public void setDefaultRpNum(String defaultRpNum) {
            this.defaultRpNum = defaultRpNum;
        }

        public boolean isMasterItemColoring() {
            return itemColoring;
        }

        public void setMasterItemColoring(boolean itemColoring) {
            this.itemColoring = itemColoring;
        }
        
        public String getEditorButtonType() {
            return editorButtonType;
        }
        
        public void setEditorButtonType(String b) {
            this.editorButtonType = b;
        }
        
        public boolean isMergeWithSameAdmin() {
            return mergeWithSameAdmin;
        }
        
        public void setMergeWithSameAdmin(boolean b) {
            mergeWithSameAdmin = b;
        }
//minagawa^ LSC Test        
        public boolean isShowStampName() {
            return showStampName;
        }
        public void setShowStampName(boolean b) {
            showStampName = b;
        }
//minagawa$        
    }

    private JPanel createUnitFieldPanel(JTextField tf, String unit) {

        JPanel ret = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        ret.add(tf);
        ret.add(new JLabel(unit));
        return ret;
    }

    private String testNumber(String test) {
        String ret = null;
        try {
            Float.parseFloat(test);
            ret = ZenkakuUtils.toHalfNumber(test);
        } catch (Exception e) {
        }
        return ret;
    }
}
