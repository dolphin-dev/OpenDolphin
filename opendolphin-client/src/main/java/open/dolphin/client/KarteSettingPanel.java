package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.EventHandler;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.helper.GridBagBuilder;
import open.dolphin.project.Project;
import open.dolphin.project.ProjectStub;
import open.dolphin.util.ZenkakuUtils;

/**
 * KarteSettingPanel
 *
 * @author Minagawa,Kazushi
 */
public class KarteSettingPanel extends AbstractSettingPanel {

    private static final String ID = "karteSetting";
    private static final String TITLE = "カルテ";
    private static final String ICON = "hist_16.gif";
    
    // インスペクタ画面
    private JComboBox topCompo;
    private JComboBox secondCompo;
    private JComboBox thirdCompo;
    private JComboBox forthCompo;
    private JLabel infoLabel;
    private JRadioButton pltform;
    private JRadioButton prefLoc;
    
    // カルテ文書関係
    private JRadioButton asc;
    private JRadioButton desc;
    private JSpinner spinner;
    private JComboBox periodCombo;
    private JRadioButton vSc;
    private JRadioButton hSc;
    private NameValuePair[] periodObjects;
    
    // 病名関係
    private JRadioButton diagnosisAsc;
    private JRadioButton diagnosisDesc;
    private JComboBox diagnosisPeriodCombo;
    private JCheckBox autoOutcomeInput;
    private JSpinner outcomeSpinner;
    private NameValuePair[] diagnosisPeriodObjects;
    private JCheckBox activeOnlyChk;
    
    // コマンドボタン
    private JButton restoreDefaultBtn;
    
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
    
    // CLAIM 送信関係
    private JRadioButton sendAtTmp;
    private JRadioButton noSendAtTmp;
    private JRadioButton sendAtSave;
    private JRadioButton noSendAtSave;
    private JRadioButton sendAtModify;
    private JRadioButton noSendAtModify;
    private JRadioButton sendDiagnosis;
    private JRadioButton noSendDiagnosis;
    private JCheckBox useTop15AsTitle;
    private JTextField defaultKarteTitle;
    
    // その他
    private JCheckBox noConfirmAtNew;
    private JRadioButton copyNew;
    private JRadioButton applyRp;
    private JRadioButton emptyNew;
    private JRadioButton placeWindow;
    private JRadioButton palceTabbedPane;
    private JCheckBox noConfirmAtSave;      // 保存時確認ダイアログ
    private JRadioButton save;              // 保存
    private JRadioButton saveTmp;           // 仮保存
    private JFormattedTextField printCount; // 印刷枚数
    private JCheckBox autoCloseAfterSaving; // 自動close
    private JTextField ageToNeedMonth;      // 月齢表示
    
    private KarteModel model;
    private boolean ok = true;

    public KarteSettingPanel() {
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
        model = new KarteModel();
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

          int defaultFetchCount = 1;
          int minFetchCount = 1;
          int maxFetchCount = 10;
          int stepFetchCount = 1;
          int defaultOffsetOutcomeDate = Project.getDefaultInt(Project.OFFSET_OUTCOME_DATE);

        // GUI コンポーネントを生成する
        String[] compo = new String[]{
            "メモ", "アレルギ", "身長体重", "文書履歴", "カレンダ"
        };
        topCompo = new JComboBox(compo);
        secondCompo = new JComboBox(compo);
        thirdCompo = new JComboBox(compo);
        forthCompo = new JComboBox(compo);
        infoLabel = new JLabel("有効な組み合わせになっています。");

        // 患者インスペクタ画面のロケータ
        pltform = new JRadioButton("プラットフォーム");
        prefLoc = new JRadioButton("位置と大きさを記憶する");

        // カルテ文書関係
        asc = new JRadioButton("昇順");
        desc = new JRadioButton("降順");
        //showModifiedCB = new JCheckBox("修正履歴表示");
        periodObjects = ClientContext.getNameValuePair("docHistory.combo.period");
        periodCombo = new JComboBox(periodObjects);
        vSc = new JRadioButton("垂直");
        hSc = new JRadioButton("水平");

        // 病名関係
        diagnosisAsc = new JRadioButton("昇順");
        diagnosisDesc = new JRadioButton("降順");
        diagnosisPeriodObjects = ClientContext.getNameValuePair("diagnosis.combo.period");
        diagnosisPeriodCombo = new JComboBox(diagnosisPeriodObjects);
        autoOutcomeInput = new JCheckBox("終了日を自動入力する");
        activeOnlyChk = new JCheckBox("アクティブ病名のみ表示");

        // コマンドボタン
        restoreDefaultBtn = new JButton("デフォルト設定に戻す");

        // スタンプ動作
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

        // CLAIM 送信関係
        sendAtTmp = new JRadioButton("送信する");
        noSendAtTmp = new JRadioButton("送信しない");
        sendAtSave = new JRadioButton("送信する");
        noSendAtSave = new JRadioButton("送信しない");
        sendAtModify = new JRadioButton("送信する");
        noSendAtModify = new JRadioButton("送信しない");
        sendDiagnosis = new JRadioButton("送信する");
        noSendDiagnosis = new JRadioButton("送信しない");
        useTop15AsTitle = new JCheckBox("カルテの先頭15文字を使用する");
        defaultKarteTitle = new JTextField(10);

        // その他
        noConfirmAtNew = new JCheckBox("確認ダイアログを表示しない");
        copyNew = new JRadioButton("全てコピー");
        applyRp = new JRadioButton("前回処方を適用");
        emptyNew = new JRadioButton("空白の新規カルテ");
        placeWindow = new JRadioButton("別ウィンドウで編集");
        palceTabbedPane = new JRadioButton("タブパネルへ追加");
        autoCloseAfterSaving = new JCheckBox("編集ウインドウを自動的に閉じる");

        noConfirmAtSave = new JCheckBox("確認ダイアログを表示しない");
        save = new JRadioButton("保 存");
        saveTmp = new JRadioButton("仮保存");

        ageToNeedMonth = new JTextField(3);
        ageToNeedMonth.addFocusListener(AutoRomanListener.getInstance());
        ageToNeedMonth.setToolTipText("-1:表示しない");
        ageToNeedMonth.setHorizontalAlignment(JTextField.RIGHT);

        int currentFetchCount = Project.getInt(Project.DOC_HISTORY_FETCHCOUNT, defaultFetchCount);
        SpinnerModel fetchModel = new SpinnerNumberModel(currentFetchCount, minFetchCount, maxFetchCount, stepFetchCount);
        spinner = new JSpinner(fetchModel);
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "#"));

        //転帰入力時に日付を入力する場合のオフセット値
        int currentOffsetOutcomeDate = Project.getInt(Project.OFFSET_OUTCOME_DATE, defaultOffsetOutcomeDate);
        System.out.println(currentOffsetOutcomeDate);
        SpinnerModel outcomeModel = new SpinnerNumberModel(currentOffsetOutcomeDate, -31, 0, 1);
        outcomeSpinner = new JSpinner(outcomeModel);
        outcomeSpinner.setEditor(new JSpinner.NumberEditor(outcomeSpinner, "#"));

        // インスペクタ画面 Memo & ロケータ
        JPanel frameLocator = new JPanel();
        frameLocator.add(pltform);
        frameLocator.add(prefLoc);

        // 文書履歴の昇順降順
        JPanel ascDesc = new JPanel();
        ascDesc.add(asc);
        ascDesc.add(desc);
        //ascDesc.add(showModifiedCB);

        // スクロール方向
        JPanel scrP = new JPanel();
        scrP.add(vSc);
        scrP.add(hSc);

        // インスペクタタブ
        GridBagBuilder gbb = new GridBagBuilder("インスペクタ画面");
        int row = 0;
        JLabel label = new JLabel("左側トップ:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(topCompo, 1, row, 1, 1, GridBagConstraints.WEST);
        row++;
        label = new JLabel("2番目:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(secondCompo, 1, row, 1, 1, GridBagConstraints.WEST);
        row++;
        label = new JLabel("3番目:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(thirdCompo, 1, row, 1, 1, GridBagConstraints.WEST);
        row++;
        label = new JLabel("ボトム:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(forthCompo, 1, row, 1, 1, GridBagConstraints.WEST);
        row++;
        label = new JLabel(ClientContext.getImageIcon("about_16.gif"));
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(infoLabel, 1, row, 1, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("画面ロケータ:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(frameLocator, 1, row, 1, 1, GridBagConstraints.WEST);
        JPanel insP = gbb.getProduct();

        gbb = new GridBagBuilder();
        gbb.add(insP,           0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbb.add(new JLabel(""), 0, 2, GridBagConstraints.BOTH, 1.0, 1.0);
        JPanel inspectorPanel = gbb.getProduct();

        // 文書関連タブ
        // Karte
        gbb = new GridBagBuilder("カルテ");
        row = 0;

        row++;
        label = new JLabel("文書履歴:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(ascDesc, 1, row, 1, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("自動文書取得数:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(spinner, 1, row, 1, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("スクロール方向:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(scrP, 1, row, 1, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("文書抽出期間:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(periodCombo, 1, row, 1, 1, GridBagConstraints.WEST);
        JPanel kartePanel = gbb.getProduct();

        // Diagnosis
        JPanel diagAscDesc = new JPanel();
        diagAscDesc.add(diagnosisAsc);
        diagAscDesc.add(diagnosisDesc);
        GridBagBuilder gbb2 = new GridBagBuilder("傷病名");
        row = 0;
        label = new JLabel("表示順:", SwingConstants.RIGHT);
        gbb2.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb2.add(diagAscDesc, 1, row, 1, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("抽出期間:", SwingConstants.RIGHT);
        gbb2.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb2.add(diagnosisPeriodCombo, 1, row, 1, 1, GridBagConstraints.WEST);

        row++;
        //label = new JLabel("抽出期間:", SwingConstants.RIGHT);
        //gbb2.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb2.add(activeOnlyChk, 1, row, 2, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("転帰入力時:", SwingConstants.RIGHT);
        gbb2.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb2.add(autoOutcomeInput, 1, row, 1, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("入力する日(前):", SwingConstants.RIGHT);
        gbb2.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb2.add(outcomeSpinner, 1, row, 1, 1, GridBagConstraints.WEST);
        JPanel diagnosisPanel = gbb2.getProduct();

        // Set default button
        JPanel cmd = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        cmd.add(restoreDefaultBtn);

        gbb = new GridBagBuilder();
        gbb.add(kartePanel, 0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbb.add(diagnosisPanel, 0, 1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbb.add(cmd, 0, 2, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbb.add(new JLabel("　"), 0, 3, GridBagConstraints.BOTH, 1.0, 1.0);
        gbb.add(new JLabel("　"), 0, 4, GridBagConstraints.BOTH, 1.0, 1.0);
        gbb.add(new JLabel("　"), 0, 5, GridBagConstraints.BOTH, 1.0, 1.0);
        //gbb.add(new JLabel("　"), 0, 6, GridBagConstraints.BOTH, 1.0, 1.0);
        //gbb.add(Box.createVerticalStrut(200), GridBagConstraints.BOTH, 1.0, 1.0);

        JPanel docPanel = gbb.getProduct();

        ButtonGroup bg = new ButtonGroup();

        bg.add(asc);
        bg.add(desc);

        bg = new ButtonGroup();
        bg.add(diagnosisAsc);
        bg.add(diagnosisDesc);

        bg = new ButtonGroup();
        bg.add(pltform);
        bg.add(prefLoc);

        bg = new ButtonGroup();
        bg.add(vSc);
        bg.add(hSc);

        restoreDefaultBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "restoreDefault"));

        // スタンプ動作
        JPanel stampPanel = new JPanel();
        stampPanel.setLayout(new BoxLayout(stampPanel, BoxLayout.Y_AXIS));

        gbb = new GridBagBuilder("スタンプ動作の設定");
        row = 0;
        label = new JLabel("スタンプの上にDnDした場合:", SwingConstants.RIGHT);
        JPanel stmpP = GUIFactory.createRadioPanel(new JRadioButton[]{replaceStamp, showAlert});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(stmpP, 1, row, 1, 1, GridBagConstraints.WEST);
        row++;
        gbb.add(stampSpace, 0, row, 2, 1, GridBagConstraints.WEST);
        row++;
        gbb.add(laboFold, 0, row, 2, 1, GridBagConstraints.WEST);
        stampPanel.add(gbb.getProduct());
        bg = new ButtonGroup();
        bg.add(replaceStamp);
        bg.add(showAlert);
        
        bg = new ButtonGroup();
        bg.add(stampEditorButtonIcon);
        bg.add(stampEditorButtonText);

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

        gbb = new GridBagBuilder("マスター検索");
        row = 0;
        gbb.add(masterItemColoring, 0, row, 2, 1, GridBagConstraints.WEST);
        stampPanel.add(gbb.getProduct());
        
        gbb = new GridBagBuilder("スタンプエディターのボタンタイプ");
        row = 0;
        JPanel stButton = GUIFactory.createRadioPanel(new JRadioButton[]{stampEditorButtonIcon, stampEditorButtonText});
        gbb.add(stButton, 0, row, 1, 1, GridBagConstraints.WEST);
        stampPanel.add(gbb.getProduct());

        stampPanel.add(Box.createVerticalStrut(400));
        stampPanel.add(Box.createVerticalGlue());

        // CLAIM 送信のデフォルト設定
        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BoxLayout(sendPanel, BoxLayout.Y_AXIS));

        gbb = new GridBagBuilder("カルテの保存時に設定するタイトル");
        row = 0;
        gbb.add(useTop15AsTitle, 0, row, 2, 1, GridBagConstraints.WEST);
        row++;
        label = new JLabel("デフォルトのタイトル:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(defaultKarteTitle, 1, row, 1, 1, GridBagConstraints.WEST);
        sendPanel.add(gbb.getProduct());

        gbb = new GridBagBuilder("診療行為送信のデフォルト設定");
        row = 0;
        label = new JLabel("仮保存時:", SwingConstants.RIGHT);
        JPanel p9 = GUIFactory.createRadioPanel(new JRadioButton[]{sendAtTmp, noSendAtTmp});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p9, 1, row, 1, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("保存時:", SwingConstants.RIGHT);
        p9 = GUIFactory.createRadioPanel(new JRadioButton[]{sendAtSave, noSendAtSave});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p9, 1, row, 1, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("修正時:", SwingConstants.RIGHT);
        p9 = GUIFactory.createRadioPanel(new JRadioButton[]{sendAtModify, noSendAtModify});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p9, 1, row, 1, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("傷病名:", SwingConstants.RIGHT);
        p9 = GUIFactory.createRadioPanel(new JRadioButton[]{sendDiagnosis, noSendDiagnosis});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p9, 1, row, 1, 1, GridBagConstraints.WEST);

        sendPanel.add(gbb.getProduct());
        sendPanel.add(Box.createVerticalStrut(500));
        sendPanel.add(Box.createVerticalGlue());

        // 新規カルテ作成時と保存時の確認ダイアログオプション
        JPanel confirmPanel = new JPanel();
        confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.Y_AXIS));
        NumberFormat numFormat = NumberFormat.getNumberInstance();
        printCount = new JFormattedTextField(numFormat);
        printCount.setValue(new Integer(0));

        row = 0;
        gbb = new GridBagBuilder("新規カルテ作成時");
        gbb.add(noConfirmAtNew, 0, row, 2, 1, GridBagConstraints.WEST);

        row += 1;
        label = new JLabel("作成方法:", SwingConstants.RIGHT);
        JPanel p = GUIFactory.createRadioPanel(new JRadioButton[]{copyNew, applyRp, emptyNew});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p, 1, row, 1, 1, GridBagConstraints.WEST);

        row += 1;
        label = new JLabel("配置方法:", SwingConstants.RIGHT);
        JPanel p2 = GUIFactory.createRadioPanel(new JRadioButton[]{placeWindow, palceTabbedPane});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p2, 1, row, 1, 1, GridBagConstraints.WEST);
        confirmPanel.add(gbb.getProduct());

        gbb = new GridBagBuilder("カルテ保存時");
        row = 0;
        gbb.add(autoCloseAfterSaving, 0, row, 2, 1, GridBagConstraints.WEST);

        row++;
        gbb.add(noConfirmAtSave, 0, row, 2, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("印刷枚数:", SwingConstants.RIGHT);
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(printCount, 1, row, 1, 1, GridBagConstraints.WEST);

        row++;
        label = new JLabel("動 作:", SwingConstants.RIGHT);
        JPanel p4 = GUIFactory.createRadioPanel(new JRadioButton[]{save, saveTmp});
        gbb.add(label, 0, row, 1, 1, GridBagConstraints.EAST);
        gbb.add(p4, 1, row, 1, 1, GridBagConstraints.WEST);
        confirmPanel.add(gbb.getProduct());

        // 月齢表示
        gbb = new GridBagBuilder("年齢");
        row = 0;
        JPanel mp = new JPanel();
        mp.add(new JLabel("月齢表示:"));
        mp.add(ageToNeedMonth);
        mp.add(new JLabel("未満"));
        gbb.add(mp, 0, row, 2, 1,GridBagConstraints.WEST);
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
        tabbedPane.addTab("文 書", docPanel);
        tabbedPane.addTab("スタンプ", stampPanel);
        tabbedPane.addTab("診療行為", sendPanel);
        tabbedPane.addTab("その他", confirmPanel);
        tabbedPane.setPreferredSize(docPanel.getPreferredSize());

        getUI().setLayout(new BorderLayout());
        getUI().add(tabbedPane);
    }

    private void checkState() {

        boolean inspectorOk = true;
        int topIndex = topCompo.getSelectedIndex();
        int secondIndex = secondCompo.getSelectedIndex();
        int thirdIndex = thirdCompo.getSelectedIndex();
        int forthIndex = forthCompo.getSelectedIndex();
        if (topIndex == secondIndex || topIndex == thirdIndex || topIndex == forthIndex) {
            inspectorOk = false;
        } else if (secondIndex == thirdIndex || secondIndex == forthIndex) {
            inspectorOk = false;
        } else if (thirdIndex == forthIndex) {
            inspectorOk = false;
        }
        if (inspectorOk) {
            infoLabel.setText("有効な組み合わせになっています。");
            setState(AbstractSettingPanel.State.VALID_STATE);
        } else {
            infoLabel.setText("重複があります。");
            setState(AbstractSettingPanel.State.INVALID_STATE);
        }

        boolean titleOk = true;
        if (!useTop15AsTitle.isSelected()) {
            String test = defaultKarteTitle.getText().trim();
            if (test.equals("")) {
                titleOk = false;
            }
        }

        boolean newOk = (inspectorOk && titleOk) ? true : false;

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

        // インスペクタの左
        topCompo.setSelectedItem((String) model.getTopInspector());
        secondCompo.setSelectedItem((String) model.getSecondInspector());
        thirdCompo.setSelectedItem((String) model.getThirdInspector());
        forthCompo.setSelectedItem((String) model.getForthInspector());

        // 重複をチェックするためのリスナ
        topCompo.addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "inspectorChanged", "stateChange"));
        secondCompo.addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "inspectorChanged", "stateChange"));
        thirdCompo.addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "inspectorChanged", "stateChange"));
        forthCompo.addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "inspectorChanged", "stateChange"));

        // インスペクタ画面のロケータ
        boolean curLocator = model.isLocateByPlatform();
        pltform.setSelected(curLocator);
        prefLoc.setSelected(!curLocator);

        // カルテの昇順表示
        boolean currentAsc = model.isAscendingKarte();
        asc.setSelected(currentAsc);
        desc.setSelected(!currentAsc);

        // 修正履歴表示
        //showModifiedCB.setSelected(model.isShowModifiedKarte());

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

        // アクティブ病名のみ表示
        activeOnlyChk.setSelected(model.isActiveOnly());

        // 転帰のオフセット
        autoOutcomeInput.setSelected(model.isAutoOutcomeInput());
        autoOutcomeInput.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                outcomeSpinner.setEnabled(autoOutcomeInput.isSelected());
            }
        });
        outcomeSpinner.setEnabled(autoOutcomeInput.isSelected());
        outcomeSpinner.setValue(new Integer(model.getOffsetOutcomeDate()));

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

        // カルテタイトル
        ActionListener a = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean enabled = useTop15AsTitle.isSelected();
                defaultKarteTitle.setEnabled(!enabled);
            }
        };
        useTop15AsTitle.addActionListener(a);
        defaultKarteTitle.setText(model.getDefaultKarteTitle());
        if (model.isUseTop15AsTitle()) {
            useTop15AsTitle.doClick();
        }
        DocumentListener emptyListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                checkState();
            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {
                checkState();
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                checkState();
            }
        };
        defaultKarteTitle.getDocument().addDocumentListener(emptyListener);
        defaultKarteTitle.addFocusListener(AutoKanjiListener.getInstance());

        //----------------------
        // 確認ダイアログ関係
        //----------------------
        ActionListener al = new ActionListener() {
            @Override
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
            @Override
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

        // 保存時の動作
        // 編集ウインドウを自動的に閉じる
        autoCloseAfterSaving.setSelected(model.isAutoCloseAtSave());
        
        // 保存時に確認ダイアログを表示しない
        // 保存時のデフォルト動作
        if (model.getSaveKarteMode() == 0) {
            save.setSelected(true);
        } else {
            saveTmp.setSelected(true);
        }
        boolean curConfirmAtSave = model.isConfirmAtSave();
        noConfirmAtSave.setSelected(!curConfirmAtSave);
        save.setEnabled(!curConfirmAtSave);
        saveTmp.setEnabled(!curConfirmAtSave);
        noConfirmAtSave.addActionListener(al2);

        // 印刷枚数
        printCount.setValue(new Integer(model.getPrintKarteCount()));
        printCount.setEnabled(!curConfirmAtSave);

        // 月齢
        ageToNeedMonth.setText(String.valueOf(model.getAgeNeedMonth()));

        // この設定画面は常に有効状態である
        setState(AbstractSettingPanel.State.VALID_STATE);
    }

    /**
     * ViewToModel
     */
    private void bindViewToModel() {

        // インスペクタの左
        model.setTopInspector((String) topCompo.getSelectedItem());
        model.setSecondInspector((String) secondCompo.getSelectedItem());
        model.setThirdInspector((String) thirdCompo.getSelectedItem());
        model.setForthInspector((String) forthCompo.getSelectedItem());

        // インスペクタ画面のロケータ
        model.setLocateByPlatform(pltform.isSelected());

        // カルテの昇順表示
        model.setAscendingKarte(asc.isSelected());

        // カルテの修正履歴表示
        //model.setShowModifiedKarte(showModifiedCB.isSelected());

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

        // アクティブ病名のみ表示
        model.setActiveOnly(activeOnlyChk.isSelected());

        // 転帰入力時の終了日オフセット
        model.setAutoOutcomeInput(autoOutcomeInput.isSelected());
        String val = outcomeSpinner.getValue().toString();
        model.setOffsetOutcomeDate(Integer.parseInt(val));

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

        // 仮保存時の CLAIM 送信
        model.setSendClaimTmp(sendAtTmp.isSelected());

        // 保存時の CLAIM 送信
        model.setSendClaimSave(sendAtSave.isSelected());

        // 修正時の CLAIM 送信
        model.setSendClaimModify(sendAtModify.isSelected());

        // 保存時に設定するカルテのタイトル
        model.setUseTop15AsTitle(useTop15AsTitle.isSelected());
        model.setDefaultKarteTitle(defaultKarteTitle.getText().trim());

        // 病名の CLAIM 送信
        model.setSendDiagnosis(sendDiagnosis.isSelected());

        // 新規カルテ時の確認ダイアログ
        model.setConfirmAtNew(!noConfirmAtNew.isSelected());

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

        // 自動クローズ
        model.setAutoCloseAtSave(autoCloseAfterSaving.isSelected());

        // 保存時の確認ダイアログ
        model.setConfirmAtSave(!noConfirmAtSave.isSelected());

        // 保存時のデフォルト動作
        int sMode = save.isSelected() ? 0 : 1;
        model.setSaveKarteMode(sMode); // 0=save, 1=saveTmp

        // 印刷枚数
        Object o = printCount.getValue();
        if (o instanceof Long) {
            Long l = (Long)o;
            model.setPrintKarteCount(l.intValue());
        } else if (o instanceof Integer) {
            Integer i = (Integer)o;
            model.setPrintKarteCount(i.intValue());
        }

        // 月齢表示年齢
        String valStr = ageToNeedMonth.getText().trim();
        if (!valStr.equals("")) {
            try {
                model.setAgeNeedMonth(Integer.parseInt(valStr));

            } catch (Throwable e) {
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * 画面モデルクラス。
     */
    class KarteModel {

        // インスペクタ
        private String topInspector;
        private String secondInspector;
        private String thirdInspector;
        private String forthInspector;
        
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
        private boolean autoOutcomeInput;
        private int offsetOutcomeDate;
        private boolean activeOnly;
        // 検体検査
        private int labotestExtractionPeriod;
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

        // CLAIM 送信関係
        private boolean sendClaimTmp;
        private boolean sendClaimSave;
        private boolean sendClaimModify;
        private boolean sendDiagnosis;
        private String defaultKarteTitle;
        private boolean useTop15AsTitle;

        // その他
        private boolean confirmAtNew;           // 新規作成時の確認ダイアログ
        private int createKarteMode;            // カルテの作成モード
        private boolean placeKarteMode;         // 新規カルテの配置方法
       
        private boolean autoCloseAtSave;        // 自動クローズ
        private boolean confirmAtSave;          // 保存時の確認ダイアログ表示
        private int saveKarteMode;              // 表示しない場合の保存モード
        private int printKarteCount;            // 表示しない場合のプリント枚数

        private int ageNeedMonth;               // 月齢表示をする年齢（未満）

        /**
         * ProjectStub から populate する。
         */
        public void populate(ProjectStub stub) {

            setTopInspector(Project.getString(Project.TOP_INSPECTOR));  // stub.getTopInspector()

            setSecondInspector(Project.getString(Project.SECOND_INSPECTOR));   // stub.getSecondInspector()

            setThirdInspector(Project.getString(Project.THIRD_INSPECTOR));    // stub.getThirdInspector()

            setForthInspector(Project.getString(Project.FORTH_INSPECTOR));    // stub.getForthInspector()

            setLocateByPlatform(Project.getBoolean(Project.LOCATION_BY_PLATFORM));  // stub.getLocateByPlatform()

            setFetchKarteCount(Project.getInt(Project.DOC_HISTORY_FETCHCOUNT));   // stub.getFetchKarteCount()

            setScrollKarteV(Project.getBoolean(Project.KARTE_SCROLL_DIRECTION));  // stub.getScrollKarteV()

            setAscendingKarte(Project.getBoolean(Project.DOC_HISTORY_ASCENDING));    // stub.getAscendingKarte()

            setKarteExtractionPeriod(Project.getInt(Project.DOC_HISTORY_PERIOD)); // stub.getKarteExtractionPeriod()

            setShowModifiedKarte(Project.getBoolean(Project.DOC_HISTORY_SHOWMODIFIED)); // stub.getShowModifiedKarte()

            setAscendingDiagnosis(Project.getBoolean(Project.DIAGNOSIS_ASCENDING));    // stub.getAscendingDiagnosis()

            setDiagnosisExtractionPeriod(Project.getInt(Project.DIAGNOSIS_PERIOD)); // stub.getDiagnosisExtractionPeriod()

            setActiveOnly(Project.getBoolean("diagnosis.activeOnly"));

            setAutoOutcomeInput(Project.getBoolean(Project.DIAGNOSIS_AUTO_OUTCOME_INPUT));  // stub.isAutoOutcomeInput()

            setOffsetOutcomeDate(Project.getInt(Project.OFFSET_OUTCOME_DATE));

            setLabotestExtractionPeriod(Project.getInt(Project.LABOTEST_PERIOD));  // stub.getLabotestExtractionPeriod()

            setReplaceStamp(Project.getBoolean(Project.STAMP_REPLACE));  // stub.isReplaceStamp()

            setStampSpace(Project.getBoolean(Project.STAMP_SPACE));    // stub.isStampSpace()

            setLaboFold(Project.getBoolean(Project.LABTEST_FOLD));  // stub.isLaboFold()

            //-------------------
            setDefaultZyozaiNum(Project.getString(Project.DEFAULT_ZYOZAI_NUM));  // stub.getDefaultZyozaiNum()

            setDefaultMizuyakuNum(Project.getString(Project.DEFAULT_MIZUYAKU_NUM));    // stub.getDefaultMizuyakuNum()

            setDefaultSanyakuNum(Project.getString(Project.DEFAULT_SANYAKU_NUM)); // stub.getDefaultSanyakuNum()
            
            setDefaultCapsuleNum(Project.getString(Project.DEFAULT_CAPSULE_NUM));

            setDefaultRpNum(Project.getString(Project.DEFAULT_RP_NUM));  // stub.getDefaultRpNum()
            //-------------------

            setMasterItemColoring(Project.getBoolean(Project.MASTER_SEARCH_ITEM_COLORING));    // stub.getMasterItemColoring()
            
            setEditorButtonType(Project.getString("stamp.editor.buttonType"));

            setSendClaimTmp(Project.getBoolean(Project.SEND_CLAIM_TMP));  // stub.getSendClaimTmp()

            setSendClaimSave(Project.getBoolean(Project.SEND_CLAIM_SAVE)); // stub.getSendClaimSave()

            setSendClaimModify(Project.getBoolean(Project.SEND_CLAIM_MODIFY));   // stub.getSendClaimModify()

            setUseTop15AsTitle(Project.getBoolean(Project.KARTE_USE_TOP15_AS_TITLE));   // stub.isUseTop15AsTitle()

            setDefaultKarteTitle(Project.getString(Project.KARTE_DEFAULT_TITLE)); // stub.getDefaultKarteTitle()

            setSendDiagnosis(Project.getBoolean(Project.SEND_DIAGNOSIS)); // stub.getSendDiagnosis()

            setConfirmAtNew(Project.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW));  // stub.getConfirmAtNew()
            //ClientContext.getBootLogger().debug("populate-新規作成時の確認ダイアログ表示:"+ isConfirmAtNew());

            setCreateKarteMode(Project.getInt(Project.KARTE_CREATE_MODE));   // stub.getCreateKarteMode()
            //ClientContext.getBootLogger().debug("populate-新規作成モード:"+ getCreateKarteMode());

            setPlaceKarteMode(Project.getBoolean(Project.KARTE_PLACE_MODE));    // stub.getPlaceKarteMode()
            //ClientContext.getBootLogger().debug("populate-新規作成カルテの配置方法:"+ isPlaceKarteMode());

            // 保存時の確認ダイアログ
            setConfirmAtSave(Project.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_SAVE));
            //ClientContext.getBootLogger().debug("populate-保存時の確認ダイアログ表示:"+ isConfirmAtSave());

            // プリント枚数
            setPrintKarteCount(Project.getInt(Project.KARTE_PRINT_COUNT));   // stub.getPrintKarteCount()
            //ClientContext.getBootLogger().debug("populate-印刷枚数:"+ getPrintKarteCount());

            // 保存モード
            setSaveKarteMode(Project.getInt(Project.KARTE_SAVE_ACTION));     // stub.getSaveKarteMode()
            //ClientContext.getBootLogger().debug("populate-保存モード:"+ getSaveKarteMode());

            // 自動クローズ
            setAutoCloseAtSave(Project.getBoolean(Project.KARTE_AUTO_CLOSE_AFTER_SAVE));   // stub.isAutoCloseAfterSaving()

            // 月齢表示年齢
            setAgeNeedMonth(Project.getInt(Project.KARTE_AGE_TO_NEED_MONTH));      // stub.getAgeToNeedMonth()
        }

        /**
         * ProjectStubへ保存する。
         */
        public void restore(ProjectStub stub) {

            Project.setString(Project.TOP_INSPECTOR, getTopInspector());    //stub.setTopInspector(getTopInspector());

            Project.setString(Project.SECOND_INSPECTOR, getSecondInspector());  //stub.setSecondInspector(getSecondInspector());

            Project.setString(Project.THIRD_INSPECTOR, getThirdInspector());  //stub.setThirdInspector(getThirdInspector());

            Project.setString(Project.FORTH_INSPECTOR, getForthInspector());    //stub.setForthInspector(getForthInspector());

            Project.setBoolean(Project.LOCATION_BY_PLATFORM, isLocateByPlatform());    //stub.setLocateByPlatform(isLocateByPlatform());

            Project.setInt(Project.DOC_HISTORY_FETCHCOUNT, getFetchKarteCount());   //stub.setFetchKarteCount(getFetchKarteCount());

            Project.setBoolean(Project.KARTE_SCROLL_DIRECTION, isScrollKarteV());   //stub.setScrollKarteV(isScrollKarteV());

            Project.setBoolean(Project.DOC_HISTORY_ASCENDING, isAscendingKarte());  //stub.setAscendingKarte(isAscendingKarte());

            Project.setInt(Project.DOC_HISTORY_PERIOD, getKarteExtractionPeriod()); //stub.setKarteExtractionPeriod(getKarteExtractionPeriod());

            Project.setBoolean(Project.DOC_HISTORY_SHOWMODIFIED, isShowModifiedKarte());    //stub.setShowModifiedKarte(isShowModifiedKarte());

            Project.setBoolean(Project.DIAGNOSIS_ASCENDING, isAscendingDiagnosis());    //stub.setAscendingDiagnosis(isAscendingDiagnosis());

            Project.setInt(Project.DIAGNOSIS_PERIOD, getDiagnosisExtractionPeriod());    //stub.setDiagnosisExtractionPeriod(getDiagnosisExtractionPeriod());

            Project.setBoolean("diagnosis.activeOnly", isActiveOnly());

            Project.setBoolean(Project.DIAGNOSIS_AUTO_OUTCOME_INPUT, isAutoOutcomeInput()); //stub.setAutoOutcomeInput(isAutoOutcomeInput());

            Project.setInt(Project.OFFSET_OUTCOME_DATE, getOffsetOutcomeDate());

            Project.setInt(Project.LABOTEST_PERIOD, getLabotestExtractionPeriod()); //stub.setLabotestExtractionPeriod(getLabotestExtractionPeriod());

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
            Project.setString("stamp.editor.buttonType", btype);

            Project.setBoolean(Project.SEND_CLAIM_TMP, isSendClaimTmp());   //stub.setSendClaimTmp(isSendClaimTmp());

            Project.setBoolean(Project.SEND_CLAIM_SAVE, isSendClaimSave()); //stub.setSendClaimSave(isSendClaimSave());

            Project.setBoolean(Project.SEND_CLAIM_MODIFY, isSendClaimModify()); //stub.setSendClaimModify(isSendClaimModify());

            Project.setBoolean(Project.KARTE_USE_TOP15_AS_TITLE, isUseTop15AsTitle());  //stub.setUseTop15AsTitle(isUseTop15AsTitle());

            test = getDefaultKarteTitle();
            if (test != null && (!test.equals(""))) {
                Project.setString(Project.KARTE_DEFAULT_TITLE, test);   //stub.setDefaultKarteTitle(test);
            }

            Project.setBoolean(Project.SEND_DIAGNOSIS, isSendDiagnosis());  //stub.setSendDiagnosis(isSendDiagnosis());

            // 新規作成時のダイアログ表示
            Project.setBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, isConfirmAtNew());    //stub.setConfirmAtNew(isConfirmAtNew());
            //ClientContext.getBootLogger().debug("restore-新規作成時の確認ダイアログ表示:"+ isConfirmAtNew());

            // 新規作成モード
            Project.setInt(Project.KARTE_CREATE_MODE, getCreateKarteMode());    //stub.setCreateKarteMode(getCreateKarteMode());
            //ClientContext.getBootLogger().debug("restore-新規作成モード:"+ getCreateKarteMode());

            // 配置モード
            Project.setBoolean(Project.KARTE_PLACE_MODE, isPlaceKarteMode());   //stub.setPlaceKarteMode(isPlaceKarteMode());
            //ClientContext.getBootLogger().debug("restore-新規作成カルテの配置方法:"+ isPlaceKarteMode());

            // 保存時の確認ダイアログ
            Project.setBoolean(Project.KARTE_SHOW_CONFIRM_AT_SAVE, isConfirmAtSave());
            //ClientContext.getBootLogger().debug("restore-保存時の確認ダイアログ表示:"+ isConfirmAtSave());

            // 印刷枚数
            Project.setInt(Project.KARTE_PRINT_COUNT, getPrintKarteCount());    //stub.setPrintKarteCount(getPrintKarteCount());
            //ClientContext.getBootLogger().debug("restore-印刷枚数:"+ getPrintKarteCount());

            // カルテ保存モード
            Project.setInt(Project.KARTE_SAVE_ACTION, getSaveKarteMode());  //stub.setSaveKarteMode(getSaveKarteMode());
            //ClientContext.getBootLogger().debug("restore-保存モード:"+ getSaveKarteMode());

            // 自動クローズ
            Project.setBoolean(Project.KARTE_AUTO_CLOSE_AFTER_SAVE, isAutoCloseAtSave());   //stub.setAutoCloseAfterSaving(isAutoCloseAtSave());

            // 月齢表示年齢
            Project.setInt(Project.KARTE_AGE_TO_NEED_MONTH, getAgeNeedMonth()); //stub.setAgeToNeedMonth(getAgeNeedMonth());
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

        public boolean isActiveOnly() {
            return activeOnly;
        }

        public void setActiveOnly(boolean b) {
            activeOnly = b;
        }

        public boolean isAutoOutcomeInput() {
            return autoOutcomeInput;
        }

        public void setAutoOutcomeInput(boolean b) {
            autoOutcomeInput = b;
        }

        public int getOffsetOutcomeDate() {
            return offsetOutcomeDate;
        }

        public void setOffsetOutcomeDate(int date) {
            offsetOutcomeDate = date;
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

        public boolean isAutoCloseAtSave() {
            return autoCloseAtSave;
        }

        public void setAutoCloseAtSave(boolean b) {
            autoCloseAtSave = b;
        }

        public int getAgeNeedMonth() {
            return ageNeedMonth;
        }

        public void setAgeNeedMonth(int age) {
            ageNeedMonth = age;
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

        public String getTopInspector() {
            return topInspector;
        }

        public void setTopInspector(String topInspector) {
            this.topInspector = topInspector;
        }

        public String getSecondInspector() {
            return secondInspector;
        }

        public void setSecondInspector(String secondInspector) {
            this.secondInspector = secondInspector;
        }

        public String getThirdInspector() {
            return thirdInspector;
        }

        public void setThirdInspector(String thirdInspector) {
            this.thirdInspector = thirdInspector;
        }

        public String getForthInspector() {
            return forthInspector;
        }

        public void setForthInspector(String forthInspector) {
            this.forthInspector = forthInspector;
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

        public String getDefaultKarteTitle() {
            return defaultKarteTitle;
        }

        public void setDefaultKarteTitle(String defaultKarteTitle) {
            this.defaultKarteTitle = defaultKarteTitle;
        }

        public boolean isUseTop15AsTitle() {
            return useTop15AsTitle;
        }

        public void setUseTop15AsTitle(boolean useTop15AsTitle) {
            this.useTop15AsTitle = useTop15AsTitle;
        }
    }

    public void restoreDefault() {

        pltform.setSelected(Project.getDefaultBoolean(Project.LOCATION_BY_PLATFORM));
        prefLoc.setSelected(!Project.getDefaultBoolean(Project.LOCATION_BY_PLATFORM));

        asc.setSelected(Project.getDefaultBoolean(Project.DOC_HISTORY_ASCENDING));
        desc.setSelected(!Project.getDefaultBoolean(Project.DOC_HISTORY_ASCENDING));
        spinner.setValue(new Integer(Project.getDefaultInt(Project.DOC_HISTORY_FETCHCOUNT)));
        periodCombo.setSelectedIndex(NameValuePair.getIndex(String.valueOf(Project.getDefaultInt(Project.DOC_HISTORY_PERIOD)), periodObjects));
        vSc.setSelected(Project.getDefaultBoolean(Project.KARTE_SCROLL_DIRECTION));

        diagnosisAsc.setSelected(Project.getDefaultBoolean(Project.DIAGNOSIS_ASCENDING));
        diagnosisDesc.setSelected(!Project.getDefaultBoolean(Project.DIAGNOSIS_ASCENDING));
        diagnosisPeriodCombo.setSelectedIndex(NameValuePair.getIndex(String.valueOf(Project.getDefaultInt(Project.DIAGNOSIS_PERIOD)), diagnosisPeriodObjects));
        autoOutcomeInput.setSelected(Project.getDefaultBoolean(Project.DIAGNOSIS_AUTO_OUTCOME_INPUT));
        outcomeSpinner.setValue(new Integer(Project.getDefaultInt(Project.OFFSET_OUTCOME_DATE)));

        masterItemColoring.setSelected(true);
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
