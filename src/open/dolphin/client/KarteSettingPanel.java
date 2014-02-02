package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.text.NumberFormat;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import open.dolphin.project.Project;
import open.dolphin.project.ProjectStub;

/**
 * KarteSettingPanel
 *
 * @author Minagawa,Kazushi
 */
public class KarteSettingPanel extends AbstractSettingPanel {
    
    private Preferences prefs;
    
    // デフォルト値
    private int defaultMemoLocation;
    private boolean defaultLocator;
    private boolean defaultAsc;
    private boolean defaultShowModified;
    private int defaultFetchCount;
    private int minFetchCount;
    private int maxFetchCount;
    private int stepFetchCount;
    private boolean defaultScDirection;
    private int defaultPeriod;
    private boolean defaultDiagnosisAsc;
    private int defaultDiagnosisPeriod;
    private int defaultOffsetOutcomeDate;
    private int defaultLaboTestPeriod;
    
    // インスペクタ画面
    //private JRadioButton memoTop;
    //private JRadioButton memoBottom;
    private JComboBox memoLocCombo;
    private JRadioButton pltform;
    private JRadioButton prefLoc;
    
    // カルテ文書関係
    private JRadioButton asc;
    private JRadioButton desc;
    private JCheckBox showModifiedCB;
    private JSpinner spinner;
    private JComboBox periodCombo;
    private JRadioButton vSc;
    private JRadioButton hSc;
    private NameValuePair[] periodObjects;
    
    // 病名関係
    private JRadioButton diagnosisAsc;
    private JRadioButton diagnosisDesc;
    private JComboBox diagnosisPeriodCombo;
    private JSpinner outcomeSpinner;
    private NameValuePair[] diagnosisPeriodObjects;
    
    // 検体検査
    private NameValuePair[] laboTestPeriodObjects;
    private JComboBox laboTestPeriodCombo;
    
    // コマンドボタン
    private JButton restoreDefaultBtn;
    
    //
    // CLAIM 送信関係
    //
    private JRadioButton sendAtTmp;
    private JRadioButton noSendAtTmp;
    private JRadioButton sendAtSave;
    private JRadioButton noSendAtSave;
    private JRadioButton sendAtModify;
    private JRadioButton noSendAtModify;
    private JRadioButton sendDiagnosis;
    private JRadioButton noSendDiagnosis;
    
    //
    // 確認ダイアログ関係
    //
    private JCheckBox noConfirmAtNew;
    private JRadioButton copyNew;
    private JRadioButton applyRp;
    private JRadioButton emptyNew;
    private JRadioButton placeWindow;
    private JRadioButton palceTabbedPane;
    
    private JCheckBox noConfirmAtSave;
    private JRadioButton save;
    private JRadioButton saveTmp;
    private JFormattedTextField printCount;
    
    private KarteModel model;
    
    /**
     * 設定画面を開始する。
     */
    public void start() {
       
        prefs = Project.getPreferences();
        
        //
        // モデルを生成し初期化する
        //
        model = new KarteModel();
        model.populate(getProjectStub());
        
        //
        // GUI を構築する
        //
        initComponents();
        
        //
        // bindModel
        //
        bindModelToView();
    
    }
    
    /**
     * 設定値を保存する。
     */
    public void save() {
        bindViewToModel();
        model.restore(getProjectStub());
    }
    
    /**
     * GUI を構築する。
     */
    private void initComponents() {
        
        //
        // デフォルト値を取得する
        //
        defaultMemoLocation = 0; // top=0, bottom=1
        defaultLocator = true;
        defaultAsc = ClientContext.getBoolean("docHistory.default.ascending");
        defaultShowModified = ClientContext.getBoolean("docHistory.default.showModified");
        defaultFetchCount = ClientContext.getInt("docHistory.default.fetchCount");
        minFetchCount = ClientContext.getInt("docHistory.min.fetchCount");
        maxFetchCount = ClientContext.getInt("docHistory.max.fetchCount");
        stepFetchCount = ClientContext.getInt("docHistory.step.fetchCount");
        defaultScDirection = ClientContext.getBoolean("karte.default.scDirection");
        defaultPeriod = ClientContext.getInt("docHistory.default.period");
        defaultDiagnosisAsc = ClientContext.getBoolean("diagnosis.default.ascending");
        defaultDiagnosisPeriod = ClientContext.getInt("diagnosis.default.period");
        defaultOffsetOutcomeDate = ClientContext.getInt("diagnosis.default.offsetOutcomeDate");
        defaultLaboTestPeriod = ClientContext.getInt("laboTest.default.period");
        
        //
        // GUI コンポーネントを生成する
        //
        
        // Memo の位置
        //memoTop = new JRadioButton("トップ");
        //memoBottom = new JRadioButton("ボトム");
        String[] memoLoc = new String[]{"カレンダ・文書履歴・メモ", "メモ・カレンダ・文書履歴", "メモ・文書履歴・カレンダ"};
        memoLocCombo = new JComboBox(memoLoc);
        
        // 患者インスペクタ画面のロケータ
        pltform = new JRadioButton("プラットフォーム");
        prefLoc = new JRadioButton("位置と大きさを記憶する");
        
        // カルテ文書関係
        asc = new JRadioButton("昇順");
        desc = new JRadioButton("降順");
        showModifiedCB = new JCheckBox("修正履歴表示");
        periodObjects = ClientContext.getNameValuePair("docHistory.combo.period");
        periodCombo = new JComboBox(periodObjects);
        vSc = new JRadioButton("垂直");
        hSc = new JRadioButton("水平");
        
        // 病名関係
        diagnosisAsc = new JRadioButton("昇順");
        diagnosisDesc = new JRadioButton("降順");
        diagnosisPeriodObjects = ClientContext.getNameValuePair("diagnosis.combo.period");
        diagnosisPeriodCombo = new JComboBox(diagnosisPeriodObjects);
        
        // 検体検査
        laboTestPeriodObjects = ClientContext.getNameValuePair("docHistory.combo.period");
        laboTestPeriodCombo = new JComboBox(laboTestPeriodObjects);
        
        // コマンドボタン
        restoreDefaultBtn = new JButton("デフォルト設定に戻す");
        
        //
        // CLAIM 送信関係
        //
        sendAtTmp = new JRadioButton("送信する");
        noSendAtTmp = new JRadioButton("送信しない");
        sendAtSave = new JRadioButton("送信する");
        noSendAtSave = new JRadioButton("送信しない");
        sendAtModify = new JRadioButton("送信する");
        noSendAtModify = new JRadioButton("送信しない");
        sendDiagnosis = new JRadioButton("送信する");
        noSendDiagnosis = new JRadioButton("送信しない");
        
        //
        // 確認ダイアログ関係
        //
        noConfirmAtNew = new JCheckBox("確認ダイアログを表示しない");
        copyNew = new JRadioButton("全てコピー");
        applyRp = new JRadioButton("前回処方を適用");
        emptyNew = new JRadioButton("空白の新規カルテ");
        placeWindow = new JRadioButton("別ウィンドウで編集");
        palceTabbedPane = new JRadioButton("タブパネルへ追加");
        
        noConfirmAtSave = new JCheckBox("確認ダイアログを表示しない");
        save = new JRadioButton("保 存");
        saveTmp = new JRadioButton("仮保存");
        
        //
        // 自動文書取得数の Spinner
        //
        int currentFetchCount = prefs.getInt(Project.DOC_HISTORY_FETCHCOUNT, defaultFetchCount);
        SpinnerModel fetchModel = new SpinnerNumberModel(currentFetchCount,minFetchCount,maxFetchCount,stepFetchCount);
        spinner = new JSpinner(fetchModel);
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "#"));
        
        //
        // 転帰入力時に日付を入力する場合のおふせっと値
        //
        int currentOffsetOutcomeDate = prefs.getInt(Project.OFFSET_OUTCOME_DATE, defaultOffsetOutcomeDate);
        SpinnerModel outcomeModel = new SpinnerNumberModel(currentOffsetOutcomeDate, -31, 0, 1);
        outcomeSpinner = new JSpinner(outcomeModel);
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "#"));
        
        //
        // インスペクタ画面 Memo & ロケータ
        //
        JPanel memoLocatin = new JPanel();
        //memoLocatin.add(memoTop);
        //memoLocatin.add(memoBottom);
        memoLocatin.add(memoLocCombo);
        JPanel frameLocator = new JPanel();
        frameLocator.add(pltform);
        frameLocator.add(prefLoc);
        
        //
        // 文書履歴の昇順降順
        //
        JPanel ascDesc = new JPanel();
        ascDesc.add(asc);
        ascDesc.add(desc);
        ascDesc.add(showModifiedCB);
        
        //
        // スクロール方向
        //
        JPanel scrP = new JPanel();
        scrP.add(vSc);
        scrP.add(hSc);
        
        // インスペクタタブ
        GridBagBuilder gbb = new GridBagBuilder("インスペクタ画面");
        int row = 0;
        JLabel label = new JLabel("メモ位置:", SwingConstants.RIGHT);
        gbb.add(label,       0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(memoLocatin, 1, row, 1, 1, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("画面ロケータ:", SwingConstants.RIGHT);
        gbb.add(label, 	      0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(frameLocator, 1, row, 1, 1, GridBagConstraints.WEST);
        JPanel insP = gbb.getProduct();
        
        gbb = new GridBagBuilder();
        gbb.add(insP,           0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbb.add(new JLabel(""), 0, 1, GridBagConstraints.BOTH,       1.0, 1.0);
        JPanel inspectorPanel = gbb.getProduct();
                
        // 文書関連タブ
        // Karte
        gbb = new GridBagBuilder("カルテ");
        row = 0;
        label = new JLabel("文書履歴:", SwingConstants.RIGHT);
        gbb.add(label,   0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(ascDesc, 1, row, 1, 1, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("自動文書取得数:", SwingConstants.RIGHT);
        gbb.add(label,   0, row,  1, 1, GridBagConstraints.EAST);
        gbb.add(spinner, 1, row,  1, 1, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("スクロール方向:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(scrP,  1, row, 1, 1, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("文書抽出期間:", SwingConstants.RIGHT);
        gbb.add(label,       0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(periodCombo, 1, row, 1, 1, GridBagConstraints.WEST);
        JPanel kartePanel = gbb.getProduct();
        
        
        // Diagnosis
        JPanel diagAscDesc = new JPanel();
        diagAscDesc.add(diagnosisAsc);
        diagAscDesc.add(diagnosisDesc);
        gbb = new GridBagBuilder("傷病名");
        row = 0;
        label = new JLabel("表示順:", SwingConstants.RIGHT);
        gbb.add(label,       0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(diagAscDesc, 1, row, 1, 1, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("抽出期間:", SwingConstants.RIGHT);
        gbb.add(label,                0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(diagnosisPeriodCombo, 1, row, 1, 1, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("転帰入力時の終了日オフセット:", SwingConstants.RIGHT);
        gbb.add(label,          0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(outcomeSpinner, 1, row, 1, 1, GridBagConstraints.WEST);
        JPanel diagnosisPanel = gbb.getProduct();
        
        // LaboTest
        gbb = new GridBagBuilder("ラボテスト");
        row = 0;
        label = new JLabel("抽出期間:", SwingConstants.RIGHT);
        gbb.add(label, 		     0,	row, 1, 1, GridBagConstraints.EAST);
        gbb.add(laboTestPeriodCombo, 1, row, 1, 1, GridBagConstraints.WEST);
        JPanel laboPanel = gbb.getProduct();
        
        // Set default button
        JPanel cmd = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        cmd.add(restoreDefaultBtn);
        
        gbb = new GridBagBuilder();
        gbb.add(kartePanel,        0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbb.add(diagnosisPanel,    0, 1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbb.add(cmd,               0, 2, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbb.add(new JLabel(""),    0, 3, GridBagConstraints.BOTH,       1.0, 1.0);
        
        JPanel docPanel = gbb.getProduct();
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(asc);
        bg.add(desc);
        
        bg = new ButtonGroup();
        bg.add(diagnosisAsc);
        bg.add(diagnosisDesc);
        
        //bg = new ButtonGroup();
        //bg.add(memoTop);
        //bg.add(memoBottom);
        
        bg = new ButtonGroup();
        bg.add(pltform);
        bg.add(prefLoc);
        
        bg = new ButtonGroup();
        bg.add(vSc);
        bg.add(hSc);
        
        restoreDefaultBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "restoreDefault"));
        
        //
        // CLAIM 送信のデフォルト設定
        //
        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BoxLayout(sendPanel, BoxLayout.Y_AXIS));
        
        gbb = new GridBagBuilder("診療行為送信のデフォルト設定");
        row = 0;
        label = new JLabel("仮保存時:", SwingConstants.RIGHT);
        JPanel p9 = GUIFactory.createRadioPanel(new JRadioButton[]{sendAtTmp, noSendAtTmp});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p9,    1, row, 1, 1, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("保存時:", SwingConstants.RIGHT);
        p9 = GUIFactory.createRadioPanel(new JRadioButton[]{sendAtSave, noSendAtSave});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p9,    1, row, 1, 1, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("修正時:", SwingConstants.RIGHT);
        p9 = GUIFactory.createRadioPanel(new JRadioButton[]{sendAtModify, noSendAtModify});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p9,    1, row, 1, 1, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("傷病名:", SwingConstants.RIGHT);
        p9 = GUIFactory.createRadioPanel(new JRadioButton[]{sendDiagnosis, noSendDiagnosis});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p9,    1, row, 1, 1, GridBagConstraints.WEST);
        
        sendPanel.add(gbb.getProduct());
        sendPanel.add(Box.createVerticalStrut(500));
        sendPanel.add(Box.createVerticalGlue());
        
        
        //
        // 新規カルテ作成時と保存時の確認ダイアログオプション
        //
        JPanel confirmPanel = new JPanel();
        confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.Y_AXIS));
        NumberFormat numFormat = NumberFormat.getNumberInstance();
        printCount = new JFormattedTextField(numFormat);
        printCount.setValue(new Integer(0));
        
        row = 0;
        gbb = new GridBagBuilder("新規カルテ作成時");
        gbb.add(noConfirmAtNew, 0, row, 2, 1, GridBagConstraints.WEST);
        
        row+=1;
        label = new JLabel("作成方法:", SwingConstants.RIGHT);
        JPanel p = GUIFactory.createRadioPanel(new JRadioButton[]{copyNew, applyRp, emptyNew});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p,     1, row, 1, 1, GridBagConstraints.WEST);
        
        row+=1;
        label = new JLabel("配置方法:", SwingConstants.RIGHT);
        JPanel p2 = GUIFactory.createRadioPanel(new JRadioButton[]{placeWindow, palceTabbedPane});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p2,    1, row, 1, 1, GridBagConstraints.WEST);
        confirmPanel.add(gbb.getProduct());
        
        gbb = new GridBagBuilder("カルテ保存時");
        row = 0;
        gbb.add(noConfirmAtSave, 0, row, 2, 1, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("印刷枚数:", SwingConstants.RIGHT);
        gbb.add(label,      0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(printCount, 1, row, 1, 1, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("動 作:", SwingConstants.RIGHT);
        JPanel p4 = GUIFactory.createRadioPanel(new JRadioButton[]{save, saveTmp});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p4,    1, row, 1, 1, GridBagConstraints.WEST);
        confirmPanel.add(gbb.getProduct());
        
        confirmPanel.add(Box.createVerticalStrut(200));
        confirmPanel.add(Box.createVerticalGlue());
        
        bg = new ButtonGroup();
        bg.add(copyNew);
        bg.add(applyRp);
        bg.add(emptyNew);
        
        bg = new ButtonGroup();
        bg.add(placeWindow);
        bg.add(palceTabbedPane);
        
        bg = new ButtonGroup();
        bg.add(sendAtTmp);
        bg.add(noSendAtTmp);
        
        bg = new ButtonGroup();
        bg.add(sendAtSave);
        bg.add(noSendAtSave);
        
        bg = new ButtonGroup();
        bg.add(sendAtModify);
        bg.add(noSendAtModify);
        
        bg = new ButtonGroup();
        bg.add(sendDiagnosis);
        bg.add(noSendDiagnosis);
        
        bg = new ButtonGroup();
        bg.add(save);
        bg.add(saveTmp);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("インスペクタ", inspectorPanel);
        tabbedPane.addTab("文書関連", docPanel);
        tabbedPane.addTab("診療行為送信", sendPanel);
        tabbedPane.addTab("確認ダイアログ", confirmPanel);
        tabbedPane.setPreferredSize(docPanel.getPreferredSize());
        
        getUI().setLayout(new BorderLayout());
        getUI().add(tabbedPane);
    }
    
    /**
     * ModelToView
     */
    private void bindModelToView() {
        
        // メモ位置
        int curMemoLoc = model.getMemoLocation();
//        if (curMemoLoc == 0) {
//            memoTop.setSelected(true);
//        } else if (curMemoLoc == 1) {
//            memoBottom.setSelected(true);
//        } else {
//            memoBottom.setSelected(true);
//        }
        memoLocCombo.setSelectedIndex(curMemoLoc);
        
        // インスペクタ画面のロケータ
        boolean curLocator = model.isLocateByPlatform();
        pltform.setSelected(curLocator);
        prefLoc.setSelected(!curLocator);
        
        // カルテの昇順表示
        boolean currentAsc = model.isAscendingKarte();
        asc.setSelected(currentAsc);
        desc.setSelected(!currentAsc);
        
        // 修正履歴表示
        showModifiedCB.setSelected(model.isShowModifiedKarte());
        
        // 抽出期間
        int currentPeriod = model.getKarteExtractionPeriod();
        periodCombo.setSelectedIndex(NameValuePair.getIndex(String.valueOf(currentPeriod), periodObjects));
        
        // カルテの取得枚数
        spinner.setValue(new Integer(model.getFetchKarteCount()));
        
        // 複数カルテのスクロール方向
        boolean vscroll = model.isScrollKarteV();
        vSc.setSelected(vscroll);
        hSc.setSelected(!vscroll);
        
        // 病名の昇順表示
        boolean currentDiagnosisAsc = model.isAscendingDiagnosis();
        diagnosisAsc.setSelected(currentDiagnosisAsc);
        diagnosisDesc.setSelected(!currentDiagnosisAsc);
        
        // 病名の抽出期間
        int currentDiagnosisPeriod = model.getDiagnosisExtractionPeriod();
        diagnosisPeriodCombo.setSelectedIndex(NameValuePair.getIndex(String.valueOf(currentDiagnosisPeriod), diagnosisPeriodObjects));
        
        // 転帰のオフセット
        
        // ラボテストの抽出期間
        int currentLaboTestPeriod = model.getLabotestExtractionPeriod();
        laboTestPeriodCombo.setSelectedIndex(NameValuePair.getIndex(String.valueOf(currentLaboTestPeriod), laboTestPeriodObjects));
        
        //
        // CLAIM 送信関係
        // 仮保存の時は送信できない。理由は CRC 等の入力するケース。
        //
        noSendAtTmp.doClick();
        sendAtTmp.setEnabled(false);
        noSendAtTmp.setEnabled(false);
        
        // 保存時の送信
        if (model.isSendClaimSave()) {
            sendAtSave.doClick();
        } else {
            noSendAtSave.doClick();
        }
        
        // 修正時の送信
        if (model.isSendClaimModify()) {
            sendAtModify.doClick();
        } else {
            noSendAtModify.doClick();
        }
        
        // 病名送信
        if (model.isSendDiagnosis()) {
            sendDiagnosis.doClick();
        } else {
            noSendDiagnosis.doClick();
        }
        
        //
        // 確認ダイアログ関係
        //
        ActionListener al = new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                boolean enabled = noConfirmAtNew.isSelected();
                emptyNew.setEnabled(enabled);
                applyRp.setEnabled(enabled);
                copyNew.setEnabled(enabled);
                placeWindow.setEnabled(enabled);
                palceTabbedPane.setEnabled(enabled);
            }
        };
        
        ActionListener al2 = new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                boolean enabled = noConfirmAtSave.isSelected();
                printCount.setEnabled(enabled);
                save.setEnabled(enabled);
                saveTmp.setEnabled(enabled);
            }
        };
        
        // カルテの作成モード
        switch (model.getCreateKarteMode()) {
            case 0:
                emptyNew.setSelected(true);
                break;
                
            case 1:
                applyRp.setSelected(true);
                break;
                
            case 2:
                copyNew.setSelected(true);
                break;
        }
        
        // 配置方法
        if (model.isPlaceKarteMode()) {
            placeWindow.setSelected(true);
        } else {
            palceTabbedPane.setSelected(true);
        }
        
        // 新規カルテ時の確認ダイログ
        boolean curConfirmAtNew = model.isConfirmAtNew();
        noConfirmAtNew.setSelected(!curConfirmAtNew);
        emptyNew.setEnabled(!curConfirmAtNew);
        applyRp.setEnabled(!curConfirmAtNew);
        copyNew.setEnabled(!curConfirmAtNew);
        placeWindow.setEnabled(!curConfirmAtNew);
        palceTabbedPane.setEnabled(!curConfirmAtNew);
        noConfirmAtNew.addActionListener(al);
        
        // 保存時のデフォルト動作
        if (model.getSaveKarteMode() == 0) {
            save.setSelected(true);
        } else {
            saveTmp.setSelected(true);
        }
        
        // 保存時の確認ダイログ
        boolean curConfirmAtSave = model.isConfirmAtSave();
        noConfirmAtSave.setSelected(!curConfirmAtSave);
        printCount.setValue(new Integer(model.getPrintKarteCount()));
        printCount.setEnabled(!curConfirmAtSave);
        save.setEnabled(!curConfirmAtSave);
        saveTmp.setEnabled(!curConfirmAtSave);
        noConfirmAtSave.addActionListener(al2);
        
        //
        // この設定画面は常に有効状態である
        //
        setState(AbstractSettingPanel.State.VALID_STATE);      
        
    }
    
    /**
     * ViewToModel
     */
    private void bindViewToModel() {
        
        // インスペクタのメモ位置
//        if (memoTop.isSelected()) {
//            model.setMemoLocation(0);
//        } else if (memoBottom.isSelected()) {
//            model.setMemoLocation(1);
//        }
        int loc = memoLocCombo.getSelectedIndex();
        model.setMemoLocation(loc);
               
        // インスペクタ画面のロケータ
        model.setLocateByPlatform(pltform.isSelected());
        
        // カルテの昇順表示
        model.setAscendingKarte(asc.isSelected());
        
        // カルテの修正履歴表示
        model.setShowModifiedKarte(showModifiedCB.isSelected());
        
        // カルテの取得枚数
        String value = spinner.getValue().toString();
        model.setFetchKarteCount(Integer.parseInt(value));
        
        // 複数カルテのスクロール方向
        model.setScrollKarteV(vSc.isSelected());
        
        // カルテの抽出期間
        String code = ((NameValuePair) periodCombo.getSelectedItem()).getValue();
        model.setKarteExtractionPeriod(Integer.parseInt(code));
        
        // 病名の昇順表示
        model.setAscendingDiagnosis(diagnosisAsc.isSelected());
        
        // 病名の抽出期間
        code = ((NameValuePair) diagnosisPeriodCombo.getSelectedItem()).getValue();
        model.setDiagnosisExtractionPeriod(Integer.parseInt(code));
        
        // 転帰入力時の終了日オフセット
        String val = outcomeSpinner.getValue().toString();
        prefs.putInt(Project.OFFSET_OUTCOME_DATE, Integer.parseInt(val));
        
        // ラボテストの抽出期間
        code = ((NameValuePair) laboTestPeriodCombo.getSelectedItem()).getValue();
        model.setLabotestExtractionPeriod(Integer.parseInt(code));
        
        // 仮保存時の CLAIM 送信
        model.setSendClaimTmp(sendAtTmp.isSelected());
        
        // 保存時の CLAIM 送信
        model.setSendClaimSave(sendAtSave.isSelected());
        
        // 修正時の CLAIM 送信
        model.setSendClaimModify(sendAtModify.isSelected());
        
        // 病名の CLAIM 送信
        model.setSendDiagnosis(sendDiagnosis.isSelected());
        
        // 新規カルテ時の確認ダイアログ
        model.setConfirmAtNew(!noConfirmAtNew.isSelected());
        
        // 保存時の確認ダイアログ
        model.setConfirmAtSave(!noConfirmAtSave.isSelected());
        
        // 新規カルテの作成モード
        int cMode = 0;
        if (emptyNew.isSelected()) {
            cMode = 0;
        } else if (applyRp.isSelected()) {
            cMode = 1;
        } else if (copyNew.isSelected()) {
            cMode = 2;
        }
        model.setCreateKarteMode(cMode); // 0=emptyNew, 1=applyRp, 2=copyNew
        
        // 新規カルテの配置方法
        model.setPlaceKarteMode(placeWindow.isSelected());
        
        // 印刷枚数
        Integer ival = (Integer) printCount.getValue();
        model.setPrintKarteCount(ival.intValue());
        
        // 保存時のデフォルト動作
        int sMode = save.isSelected() ? 0 : 1;
        model.setSaveKarteMode(sMode); // 0=save, 1=saveTmp
        
    }
    
    /**
     * 画面モデルクラス。
     */
    class KarteModel {
        
        // メモ位置
        private int memoLocation;
        
        // インスペクタ画面のロケータ
        private boolean locateByPlatform;
        
        // カルテ文書関係
        private int fetchKarteCount;
        private boolean ascendingKarte;
        private boolean showModifiedKarte;
        private boolean scrollKarteV;
        private int karteExtractionPeriod;
        
        // 病名関係
        private boolean ascendingDiagnosis;
        private int diagnosisExtractionPeriod;
        
        // 検体検査
        private int labotestExtractionPeriod;
        
        //
        // CLAIM 送信関係
        //
        private boolean sendClaimTmp;
        private boolean sendClaimSave;
        private boolean sendClaimModify;
        private boolean sendDiagnosis;
        
        //
        // 確認ダイアログ関係
        //
        private boolean confirmAtNew;
        private int createKarteMode;
        private boolean placeKarteMode;
        private boolean confirmAtSave;
        private int saveKarteMode;
        private int printKarteCount;
        
        /**
         * ProjectStub から populate する。
         */
        public void populate(ProjectStub stub) {
            
            setMemoLocation(stub.getInspectorMemoLocation());
            
            setLocateByPlatform(stub.getLocateByPlatform());
            
            setFetchKarteCount(stub.getFetchKarteCount());
            
            setScrollKarteV(stub.getScrollKarteV());
            
            setAscendingKarte(stub.getAscendingKarte());
            
            setKarteExtractionPeriod(stub.getKarteExtractionPeriod());
            
            setShowModifiedKarte(stub.getShowModifiedKarte());
            
            setAscendingDiagnosis(stub.getAscendingDiagnosis());
            
            setDiagnosisExtractionPeriod(stub.getDiagnosisExtractionPeriod());
            
            setLabotestExtractionPeriod(stub.getLabotestExtractionPeriod());
            
            setSendClaimTmp(stub.getSendClaimTmp());
            
            setSendClaimSave(stub.getSendClaimSave());
            
            setSendClaimModify(stub.getSendClaimModify());
            
            setSendDiagnosis(stub.getSendDiagnosis());
            
            setConfirmAtNew(stub.getConfirmAtNew());
            
            setCreateKarteMode(stub.getCreateKarteMode());
            
            setPlaceKarteMode(stub.getPlaceKarteMode());
            
            setConfirmAtSave(stub.getConfirmAtSave());
            
            setPrintKarteCount(stub.getPrintKarteCount());
            
            setSaveKarteMode(stub.getSaveKarteMode());
            
        }
        
        public void restore(ProjectStub stub) {
            
            stub.setInspectorMemoLocation(getMemoLocation());
            
            stub.setLocateByPlatform(isLocateByPlatform());
            
            stub.setFetchKarteCount(getFetchKarteCount());
            
            stub.setScrollKarteV(isScrollKarteV());
            
            stub.setAscendingKarte(isAscendingKarte());
            
            stub.setKarteExtractionPeriod(getKarteExtractionPeriod());
            
            stub.setShowModifiedKarte(isShowModifiedKarte());
            
            stub.setAscendingDiagnosis(isAscendingDiagnosis());
            
            stub.setDiagnosisExtractionPeriod(getDiagnosisExtractionPeriod());
            
            stub.setLabotestExtractionPeriod(getLabotestExtractionPeriod());
            
            stub.setSendClaimTmp(isSendClaimTmp());
            
            stub.setSendClaimSave(isSendClaimSave());
            
            stub.setSendClaimModify(isSendClaimModify());
            
            stub.setSendDiagnosis(isSendDiagnosis());
            
            stub.setConfirmAtNew(isConfirmAtNew());
            
            stub.setCreateKarteMode(getCreateKarteMode());
            
            stub.setPlaceKarteMode(isPlaceKarteMode());
            
            stub.setConfirmAtSave(isConfirmAtSave());
            
            stub.setPrintKarteCount(getPrintKarteCount());
            
            stub.setSaveKarteMode(getSaveKarteMode());
            
        }
        
        public int getMemoLocation() {
            return memoLocation;
        }
        
        public void setMemoLocation(int memoLocation) {
            this.memoLocation = memoLocation;
        }
        
        public boolean isLocateByPlatform() {
            return locateByPlatform;
        }
        
        public void setLocateByPlatform(boolean locateByPlatform) {
            this.locateByPlatform = locateByPlatform;
        }
        
        public int getFetchKarteCount() {
            return fetchKarteCount;
        }
        
        public void setFetchKarteCount(int fetchKarteCount) {
            this.fetchKarteCount = fetchKarteCount;
        }
        
        public boolean isAscendingKarte() {
            return ascendingKarte;
        }
        
        public void setAscendingKarte(boolean ascendingKarte) {
            this.ascendingKarte = ascendingKarte;
        }
        
        public boolean isShowModifiedKarte() {
            return showModifiedKarte;
        }
        
        public void setShowModifiedKarte(boolean showModifiedKarte) {
            this.showModifiedKarte = showModifiedKarte;
        }
        
        public boolean isScrollKarteV() {
            return scrollKarteV;
        }
        
        public void setScrollKarteV(boolean scrollKarteV) {
            this.scrollKarteV = scrollKarteV;
        }
        
        public int getKarteExtractionPeriod() {
            return karteExtractionPeriod;
        }
        
        public void setKarteExtractionPeriod(int karteExtractionPeriod) {
            this.karteExtractionPeriod = karteExtractionPeriod;
        }
        
        public boolean isAscendingDiagnosis() {
            return ascendingDiagnosis;
        }
        
        public void setAscendingDiagnosis(boolean ascendingDiagnosis) {
            this.ascendingDiagnosis = ascendingDiagnosis;
        }
        
        public int getDiagnosisExtractionPeriod() {
            return diagnosisExtractionPeriod;
        }
        
        public void setDiagnosisExtractionPeriod(int diagnosisExtractionPeriod) {
            this.diagnosisExtractionPeriod = diagnosisExtractionPeriod;
        }
        
        public int getLabotestExtractionPeriod() {
            return labotestExtractionPeriod;
        }
        
        public void setLabotestExtractionPeriod(int laboTestExtractionPeriod) {
            this.labotestExtractionPeriod = laboTestExtractionPeriod;
        }
        
        public boolean isSendClaimTmp() {
            return sendClaimTmp;
        }
        
        public void setSendClaimTmp(boolean sendClaimTmp) {
            this.sendClaimTmp = sendClaimTmp;
        }
        
        public boolean isSendClaimSave() {
            return sendClaimSave;
        }
        
        public void setSendClaimSave(boolean sendClaimSave) {
            this.sendClaimSave = sendClaimSave;
        }
        
        public boolean isSendClaimModify() {
            return sendClaimModify;
        }
        
        public void setSendClaimModify(boolean sendClaimModify) {
            this.sendClaimModify = sendClaimModify;
        }
        
        public boolean isSendDiagnosis() {
            return sendDiagnosis;
        }
        
        public void setSendDiagnosis(boolean sendDiagnosis) {
            this.sendDiagnosis = sendDiagnosis;
        }
        
        public boolean isConfirmAtNew() {
            return confirmAtNew;
        }
        
        public void setConfirmAtNew(boolean confirmAtNew) {
            this.confirmAtNew = confirmAtNew;
        }
        
        public int getCreateKarteMode() {
            return createKarteMode;
        }
        
        public void setCreateKarteMode(int createKarteMode) {
            this.createKarteMode = createKarteMode;
        }
        
        public boolean isPlaceKarteMode() {
            return placeKarteMode;
        }
        
        public void setPlaceKarteMode(boolean placeKarteMode) {
            this.placeKarteMode = placeKarteMode;
        }
        
        public boolean isConfirmAtSave() {
            return confirmAtSave;
        }
        
        public void setConfirmAtSave(boolean confirmAtSave) {
            this.confirmAtSave = confirmAtSave;
        }
        
        public int getSaveKarteMode() {
            return saveKarteMode;
        }
        
        public void setSaveKarteMode(int saveKarteMode) {
            this.saveKarteMode = saveKarteMode;
        }
        
        public int getPrintKarteCount() {
            return printKarteCount;
        }
        
        public void setPrintKarteCount(int printKarteCount) {
            this.printKarteCount = printKarteCount;
        }
    }
        
    private void restoreDefault() {
        
        pltform.setSelected(defaultLocator);
        prefLoc.setSelected(!defaultLocator);
        asc.setSelected(defaultAsc);
        desc.setSelected(!defaultAsc);
        showModifiedCB.setSelected(defaultShowModified);
        spinner.setValue(new Integer(defaultFetchCount));
        periodCombo.setSelectedIndex(NameValuePair.getIndex(String.valueOf(defaultPeriod), periodObjects));
        vSc.setSelected(defaultScDirection);
        
        diagnosisAsc.setSelected(defaultDiagnosisAsc);
        diagnosisDesc.setSelected(!defaultDiagnosisAsc);
        diagnosisPeriodCombo.setSelectedIndex(NameValuePair.getIndex(String.valueOf(defaultDiagnosisPeriod), diagnosisPeriodObjects));
        outcomeSpinner.setValue(new Integer(defaultOffsetOutcomeDate));
        
        laboTestPeriodCombo.setSelectedIndex(NameValuePair.getIndex(String.valueOf(defaultLaboTestPeriod), laboTestPeriodObjects));
    }
}
