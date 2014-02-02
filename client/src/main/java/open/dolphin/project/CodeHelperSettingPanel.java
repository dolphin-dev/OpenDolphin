package open.dolphin.project;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIFactory;
import open.dolphin.helper.GridBagBuilder;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.project.AbstractSettingPanel.State;

/**
 * コードヘルパー設定パネル。
 *
 * @author Kazushi Minagawa
 */
public class CodeHelperSettingPanel extends AbstractSettingPanel {
    
    private static final String ID = "codeHelperSetting";
    private static final String TITLE = "コード";
//minagawa^ Icon Server    
    private static final String ICON = "icon_code_helper_settings_small";
//minagawa$    
    
//s.oh^ 機能改善
    private static final int TEXTFIELD_WIDTH_MIN = 67;
    private static final int TEXTFIELD_HEIGHT_MIN = 33;
//s.oh$
    
    private JRadioButton ctrlMask;
    
    private JRadioButton metaMask;
    
    private JTextField text;
    
    private JTextField path;
    
    private JTextField general;
    
    private JTextField other;
    
    private JTextField treatment;
    
    private JTextField surgery;
    
    private JTextField radiology;
    
    private JTextField labo;
    
    private JTextField physiology;
    
    private JTextField bacteria;
    
    private JTextField injection;
    
    private JTextField rp;
    
    private JTextField baseCharge;
    
    private JTextField instraction;
    
    private JTextField orca;
    
    private State curState = State.NONE_STATE;
    
    private HelperModel model;
            
    
    /** 
     * Creates a new instance of CodeHelperSettingPanel 
     */
    public CodeHelperSettingPanel() {
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }

    /**
     * GUI を生成しプログラムを開始する。
     */
    @Override
    public void start() {
        
        //
        // モデルを生成する
        //
        model = new HelperModel();
        
        //
        // GUI を構築する
        //
        initComponents();
        
        //
        // ModelToView
        //
        model.populate(getProjectStub());
        
    }
    
    /**
     * 保存する。
     */
    @Override
    public void save() {
        model.restore(getProjectStub());
    }
    
    /**
     * GUI を構築する
     */
    private void initComponents() {
        
        ctrlMask = new JRadioButton("コントロール");
        String str = ClientContext.isMac() ? "アップル" : "メタ";
        metaMask = new JRadioButton(str);
        text = new JTextField(5);
        path = new JTextField(5);
        general = new JTextField(5);
        other = new JTextField(5);
        treatment = new JTextField(5);
        surgery = new JTextField(5);
        radiology = new JTextField(5);
        labo = new JTextField(5);
        physiology = new JTextField(5);
        bacteria = new JTextField(5);
        injection = new JTextField(5);
        rp = new JTextField(5);
        baseCharge = new JTextField(5);
        instraction = new JTextField(5);
        orca = new JTextField(5);
        
//s.oh^ 機能改善
        text.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        path.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        general.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        other.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        treatment.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        surgery.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        radiology.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        labo.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        physiology.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        bacteria.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        injection.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        rp.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        baseCharge.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        instraction.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
        orca.setMinimumSize(new Dimension(TEXTFIELD_WIDTH_MIN, TEXTFIELD_HEIGHT_MIN));
//s.oh$
        
        //
        // 修飾キー
        //
        GridBagBuilder gbl;
        if (ClientContext.isMac()) {
            gbl = new GridBagBuilder("修飾キー + リターン = 補完ポップアップ");
        } else {
            gbl = new GridBagBuilder("修飾キー + スペース = 補完ポップアップ");
        }
        
        gbl.add(new JLabel("修飾キー:"),  0, 0, GridBagConstraints.EAST);
        gbl.add(GUIFactory.createRadioPanel(new JRadioButton[]{ctrlMask,metaMask}), 1, 0, GridBagConstraints.CENTER);
        JPanel keyBind = gbl.getProduct();
        
        //
        // Stamptree
        //
        gbl = new GridBagBuilder("スタンプ箱のキーワード");
        
        gbl.add(new JLabel("テキスト:"),         0, 0, GridBagConstraints.EAST);
        gbl.add(text,                           1, 0, GridBagConstraints.WEST);
                
        gbl.add(new JLabel("パス:"),            2, 0, GridBagConstraints.EAST);
        gbl.add(path,                           3, 0, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("汎 用:"),           0, 1, GridBagConstraints.EAST);
        gbl.add(general,                        1, 1, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("その他:"),           2, 1, GridBagConstraints.EAST);
        gbl.add(other,                          3, 1, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("処 置:"),            0, 2, GridBagConstraints.EAST);
        gbl.add(treatment,                       1, 2, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("手 術:"),            2, 2, GridBagConstraints.EAST);
        gbl.add(surgery,                         3, 2, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("放射線:"),           0, 3, GridBagConstraints.EAST);
        gbl.add(radiology,                       1, 3, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("検体検査:"),          2, 3, GridBagConstraints.EAST);
        gbl.add(labo,                            3, 3, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("生体検査:"),          0, 4, GridBagConstraints.EAST);
        gbl.add(physiology,                      1, 4, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("細菌検査:"),          2, 4, GridBagConstraints.EAST);
        gbl.add(bacteria,                        3, 4, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("注 射:"),            0, 5, GridBagConstraints.EAST);
        gbl.add(injection,                       1, 5, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("処 方:"),            2, 5, GridBagConstraints.EAST);
        gbl.add(rp,                              3, 5, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("診断料:"),           0, 6, GridBagConstraints.EAST);
        gbl.add(baseCharge,                      1, 6, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("指導・在宅:"),         2, 6, GridBagConstraints.EAST);
        gbl.add(instraction,                     3, 6, GridBagConstraints.WEST);
        
        gbl.add(new JLabel("ORCA:"),            0, 7, GridBagConstraints.EAST);
        gbl.add(orca,                           1, 7, GridBagConstraints.WEST);
        
        JPanel stamp = gbl.getProduct();
        
        // 全体をレイアウトする
        gbl = new GridBagBuilder();
        gbl.add(keyBind,        0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbl.add(stamp,          0, 1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbl.add(new JLabel(""), 0, 2, GridBagConstraints.BOTH,       1.0, 1.0);
        
        setUI(gbl.getProduct());
        
    }
    
    private void connect() {
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(ctrlMask);
        bg.add(metaMask);
        
        //
        // DocumentListener
        //
        //DocumentListener dl = ProxyDocumentListener.create(this, "checkState");
        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkState();
            }
        };
        
        text.getDocument().addDocumentListener(dl);
        path.getDocument().addDocumentListener(dl);
        general.getDocument().addDocumentListener(dl);
        other.getDocument().addDocumentListener(dl);
        treatment.getDocument().addDocumentListener(dl);
        surgery.getDocument().addDocumentListener(dl);
        radiology.getDocument().addDocumentListener(dl);
        labo.getDocument().addDocumentListener(dl);
        physiology.getDocument().addDocumentListener(dl);
        bacteria.getDocument().addDocumentListener(dl);
        injection.getDocument().addDocumentListener(dl);
        rp.getDocument().addDocumentListener(dl);
        baseCharge.getDocument().addDocumentListener(dl);
        instraction.getDocument().addDocumentListener(dl);
        orca.getDocument().addDocumentListener(dl);
        
    }
    
    public void checkState() {
        
        State newState;
        
        if (text.getText().trim().equals("") || 
                path.getText().trim().equals("") || 
                general.getText().trim().equals("") || 
                other.getText().trim().equals("") || 
                treatment.getText().trim().equals("") || 
                surgery.getText().trim().equals("") || 
                radiology.getText().trim().equals("") || 
                labo.getText().trim().equals("") || 
                physiology.getText().trim().equals("") || 
                bacteria.getText().trim().equals("") ||     
                injection.getText().trim().equals("") ||     
                injection.getText().trim().equals("") ||  
                rp.getText().trim().equals("") ||  
                baseCharge.getText().trim().equals("") ||  
                instraction.getText().trim().equals("") ||  
                orca.getText().trim().equals("")) {
            
            newState = State.INVALID_STATE;
            
        } else {
            newState = State.VALID_STATE;
        }
        
        if (curState != newState) {
            curState = newState;
            setState(curState);
        }
    }
    
    class HelperModel {

        /**
         * 設定した値をプレファレンスに保存する。
         */
        public void restore(ProjectStub stub) {

            String mask = ctrlMask.isSelected() ? "ctrl" : "meta";

            Project.setString("modifier", mask);

            Project.setString(IInfoModel.ENTITY_TEXT, text.getText().trim());

            Project.setString(IInfoModel.ENTITY_PATH, path.getText().trim());

            Project.setString(IInfoModel.ENTITY_GENERAL_ORDER, general.getText().trim());

            Project.setString(IInfoModel.ENTITY_OTHER_ORDER, other.getText().trim());

            Project.setString(IInfoModel.ENTITY_TREATMENT, treatment.getText().trim());

            Project.setString(IInfoModel.ENTITY_SURGERY_ORDER, surgery.getText().trim());

            Project.setString(IInfoModel.ENTITY_RADIOLOGY_ORDER, radiology.getText().trim());

            Project.setString(IInfoModel.ENTITY_LABO_TEST, labo.getText().trim());

            Project.setString(IInfoModel.ENTITY_PHYSIOLOGY_ORDER, physiology.getText().trim());

            Project.setString(IInfoModel.ENTITY_BACTERIA_ORDER, bacteria.getText().trim());

            Project.setString(IInfoModel.ENTITY_INJECTION_ORDER, injection.getText().trim());

            Project.setString(IInfoModel.ENTITY_MED_ORDER, rp.getText().trim());

            Project.setString(IInfoModel.ENTITY_BASE_CHARGE_ORDER, baseCharge.getText().trim());

            Project.setString(IInfoModel.ENTITY_INSTRACTION_CHARGE_ORDER, instraction.getText().trim());

            Project.setString(IInfoModel.ENTITY_ORCA, orca.getText().trim());
        }

        /**
         * プレファレンスから値をGUIにセットする。
         */
        public void populate(ProjectStub stub) {

            String mask = ClientContext.isMac() ? "meta" : "ctrl";
            String modifier = Project.getString("modifier", mask);

            if (modifier.equals("ctrl")) {
                ctrlMask.setSelected(true);
                metaMask.setSelected(false);
            } else {
                ctrlMask.setSelected(false);
                metaMask.setSelected(true);
            }

            text.setText(Project.getString(IInfoModel.ENTITY_TEXT, "tx").trim());

            path.setText(Project.getString(IInfoModel.ENTITY_PATH, "pat").trim());

            general.setText(Project.getString(IInfoModel.ENTITY_GENERAL_ORDER, "gen").trim());

            other.setText(Project.getString(IInfoModel.ENTITY_OTHER_ORDER, "oth").trim());

            treatment.setText(Project.getString(IInfoModel.ENTITY_TREATMENT, "tr").trim());

            surgery.setText(Project.getString(IInfoModel.ENTITY_SURGERY_ORDER, "sur").trim());

            radiology.setText(Project.getString(IInfoModel.ENTITY_RADIOLOGY_ORDER, "rad").trim());

            labo.setText(Project.getString(IInfoModel.ENTITY_LABO_TEST, "lab").trim());

            physiology.setText(Project.getString(IInfoModel.ENTITY_PHYSIOLOGY_ORDER, "phy").trim());

            bacteria.setText(Project.getString(IInfoModel.ENTITY_BACTERIA_ORDER, "bac").trim());

            injection.setText(Project.getString(IInfoModel.ENTITY_INJECTION_ORDER, "inj").trim());

            rp.setText(Project.getString(IInfoModel.ENTITY_MED_ORDER, "rp").trim());

            baseCharge.setText(Project.getString(IInfoModel.ENTITY_BASE_CHARGE_ORDER, "base").trim());

            instraction.setText(Project.getString(IInfoModel.ENTITY_INSTRACTION_CHARGE_ORDER, "ins").trim());

            orca.setText(Project.getString(IInfoModel.ENTITY_ORCA, "orca").trim());

            connect();
            checkState();

        }
    }
}












