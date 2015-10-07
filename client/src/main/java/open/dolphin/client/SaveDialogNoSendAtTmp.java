package open.dolphin.client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.project.Project;

/**
 * SaveDialog
 * (予定カルテ対応)
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 */
public final class SaveDialogNoSendAtTmp extends AbstractSaveDialog {
    
    private final String SAVE;
    // 設定: 仮保存の時送信しない
    private final String CHK_TITLE_NO_SEND_AT_TMP;
    private final String TOOLTIP_NO_SEND;
    
    // 保存ボタン
    private JButton okButton;
    
    // MML送信時のアクセス権設定
    private JCheckBox patientCheck;
    private JCheckBox clinicCheck;
    
    private boolean claimDateEditable;
    private Date claimDate;
    private JTextField dateField;  
    
    // LabTest 送信
    private JCheckBox sendLabtest;

    public SaveDialogNoSendAtTmp() {
        super();
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(SaveDialogNoSendAtTmp.class);
        SAVE = bundle.getString("optionText.save");
        CHK_TITLE_NO_SEND_AT_TMP = bundle.getString("toolTipText.send");
        TOOLTIP_NO_SEND = bundle.getString("toolTipText.noSend");
    }
    
    @Override
    public void start() {
        dialog.setVisible(true);
    }
    
    @Override
    public SaveParamsM getValue() {
        return value;
    }
    
    public Date getClaimDate() {
        return claimDate;
    }
    
    public void setClaimDate(Date gc) {
        if (gc==null) {
            claimDate = enterParams.getClaimDate();
        } else {
            claimDate = gc;
        }
        String dateFmt = ClientContext.getBundle().getString("DATE_FORMAT_FOR_SCHEDULE");
        SimpleDateFormat frmt = new SimpleDateFormat(dateFmt);
        dateField.setText(frmt.format(claimDate));
    }
    
    /**
     * コンポーネントにSaveParamsの値を設定する。
     * @param params
     */
    @Override
    public void setValue(SaveParamsM params) {
        
        enterParams = params;
        
        JPanel contentPanel = createComponent();

        Object[] options = new Object[]{okButton, tmpButton, cancelButton};

        JOptionPane jop = new JOptionPane(
                contentPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                okButton);

        dialog = jop.createDialog(parent, ClientContext.getFrameTitle(TITLE));
        
        // Titleを表示する
//masuda^ 修正元のタイトルもコンボボックスに入れる   
        String[] titles = new String[]{params.getOldTitle(), params.getTitle()};
//masuda$  
        String progress = titles[0];
        for (String str : titles) {
            if (str != null && (!str.equals("") && (!str.equals(progress)))) {
                titleCombo.insertItemAt(str, 0);
            }
        }
        titleCombo.setSelectedIndex(0);
        
        // 診療科を表示する
        // 受付情報からの診療科を設定する
        String val = params.getDepartment();
        if (val != null) {
            String[] depts = val.split("\\s*,\\s*");
            if (depts[0] != null) {
                departmentLabel.setText(depts[0]);
            } else {
                departmentLabel.setText(val);
            }
        }
        
        // 印刷部数選択
        int count = params.getPrintCount();
        if (count != -1) {
            printCombo.setSelectedItem(String.valueOf(count));
            
        } else {
            printCombo.setEnabled(false);
        }

        //--------------------------------
        // CLAIM 送信をチェックする
        //--------------------------------
        claimDate = params.getClaimDate();
        boolean sendEnabled = params.isSendEnabled();
        sendClaimAction.setEnabled(sendEnabled);
        if (sendEnabled && params.isSendClaim()) {
            sendClaim.doClick();
        }

        //-------------------------------
        // MML 送信の場合、アクセス権を設定する
        //-------------------------------
        if (params.getSendMML()) {
            // 患者への参照と診療歴のある施設の参照許可を設定する
            boolean permit = params.isAllowPatientRef();
            patientCheck.setSelected(permit);
            permit = params.isAllowClinicRef();
            clinicCheck.setSelected(permit);
            
        } else {
            // MML 送信をしないときdiasbleにする
            patientCheck.setEnabled(false);
            clinicCheck.setEnabled(false);
        }
        
        //-------------------------------
        // 検体検査オーダー送信
        //-------------------------------
        sendLabtest.setSelected(params.isSendLabtest() && params.isHasLabtest());
        sendLabtest.setEnabled((sendEnabled && params.isHasLabtest()));
        
        checkTitle();
        
        controlButton();
        
//s.oh^ 2013/05/07 入力不具合修正
        setFocus(okButton);
//s.oh$
    }

    /**
     * GUIコンポーネントを初期化する。
     */
    private JPanel createComponent() {
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(SaveDialogNoSendAtTmp.class);
        String labelTextDocTitle = bundle.getString("labelText.docTitle");
        String labelTextDeptName = bundle.getString("labelText.deptName");
        String labelTextPrintCount = bundle.getString("labelText.printCount");
        String chkBoxTextAllowPatient = bundle.getString("chkBoxText.allowPatientRef");
        String chkBoxTextAllowHospital = bundle.getString("chkBoxText.allowHospitalRef");
        String toolTipTextClaimDate = bundle.getString("toolTipText.claimDate");
        String labelTextSendDate = bundle.getString("labelText.sendDate");
        String chkBoxTextLabTest = bundle.getString("chkBoxText.labTest");
        String chkBoxTextLabTestWithTmpSave = bundle.getString("chkBoxText.labTestWithTempSave");
        String toolTipTextOkBtn = bundle.getString("toolTipText.okBtn");
                
        // content
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(0, 1));
        
        // 文書Title
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleCombo = new JComboBox(TITLE_LIST);
        titleCombo.setPreferredSize(new Dimension(220, titleCombo.getPreferredSize().height));
        titleCombo.setMaximumSize(titleCombo.getPreferredSize());
        titleCombo.setEditable(true);
        p.add(new JLabel(labelTextDocTitle));
        p.add(titleCombo);
        content.add(p);
        
        // ComboBox のエディタコンポーネントへリスナを設定する
        titleField = (JTextField)titleCombo.getEditor().getEditorComponent();
        titleField.addFocusListener(AutoKanjiListener.getInstance());
        titleField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkTitle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkTitle();
            }
        });
        
        // 診療科、印刷部数を表示するラベルとパネルを生成する
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        departmentLabel = new JLabel();
        p1.add(new JLabel(labelTextDeptName));
        p1.add(departmentLabel);
        
        p1.add(Box.createRigidArea(new Dimension(11, 0)));
        
        // Print
        printCombo = new JComboBox(PRINT_COUNT);
        printCombo.setSelectedIndex(1);
        p1.add(new JLabel(labelTextPrintCount));
        p1.add(printCombo);
        
        content.add(p1);
        
        // AccessRightを設定するボタンとパネルを生成する
        patientCheck = new JCheckBox(chkBoxTextAllowPatient);
        clinicCheck = new JCheckBox(chkBoxTextAllowHospital);
        
        //---------------------------
        // CLAIM 送信ありなし
        //--------------------------- 
        sendClaimAction = new AbstractAction(CHK_TITLE_NO_SEND_AT_TMP) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (claimDateEditable) {
                    if (sendClaim.isSelected()) {
                        setClaimDate(getClaimDate());
                    } else {
                        dateField.setText("");
                    }
                }
            }
        };
        sendClaim = new JCheckBox();
        sendClaim.setAction(sendClaimAction);
        JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p5.add(sendClaim);
        content.add(p5);
        
//minagawa^ CLAIM送信日
        claimDateEditable = enterParams.getEnterOption()!=SaveParamsM.SCHEDULE_SCHEDULE;  // 予定画面からは変更できない
        claimDateEditable = (claimDateEditable && enterParams.isSendEnabled());                   // 送信が許可されれいる
        claimDateEditable = (claimDateEditable && enterParams.getClaimDate()!=null);              // パラメータが設定されている
        if (claimDateEditable) {
            dateField = new JTextField(12);
            dateField.setEditable(false); 
            dateField.setToolTipText(toolTipTextClaimDate);
            // 1ヶ月前まで
            int[] range = {-1, 0};
            // 今日以前でないと駄目
            SimpleDate[] acceptRange = new SimpleDate[2];
            acceptRange[0] = null;
            acceptRange[1] = new SimpleDate(new GregorianCalendar());
            PopupListener pl = new PopupListener(dateField, range, acceptRange);
            JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p2.add(new JLabel(labelTextSendDate));
            p2.add(dateField);
            content.add(p2);
        }
//minagawa&        

        //---------------------------
        // 検体検査オーダー送信ありなし
        //---------------------------
//s.oh^ 2014/11/04 仮保存時のオーダー出力
        //sendLabtest = new JCheckBox("検体検査オーダー（仮保存の場合はしない）");
        sendLabtest = new JCheckBox(Project.getBoolean(Project.SEND_TMPKARTE_LABTEST) ? chkBoxTextLabTest : chkBoxTextLabTestWithTmpSave);
//s.oh$
        if (Project.getBoolean(Project.SEND_LABTEST)) {
            JPanel p6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p6.add(sendLabtest);
            content.add(p6);
        }

        // OK button
        okButton = new JButton(SAVE);
        okButton.setToolTipText(toolTipTextOkBtn);
        okButton.addActionListener((ActionEvent e) -> {
            // 戻り値のSaveparamsを生成する
            value = viewToModel(false);
            if (value != null) {
                close();
            }
        });
        okButton.setEnabled(false);
        
        // Cancel Button
        String buttonText =  (String)UIManager.get("OptionPane.cancelButtonText");
        cancelButton = new JButton(buttonText);
        cancelButton.addActionListener((ActionEvent e) -> {
            value = null;
            close();
        });
        
        // 仮保存 button
        tmpButton = new JButton(TMP_SAVE);
        tmpButton.setToolTipText(TOOLTIP_NO_SEND);
        tmpButton.addActionListener((ActionEvent e) -> {
            // 戻り値のSaveparamsを生成する
            value = viewToModel(true);
            if (value != null) {
                close();
            }
        });
        tmpButton.setEnabled(false);
        
        return content;
    }
    
    private void close() {
        dialog.setVisible(false);
        dialog.dispose();
    }
    
    private void controlButton() {
        okButton.setEnabled((enterParams.getEnterOption()!=SaveParamsM.SCHEDULE_SCHEDULE));
        tmpButton.setEnabled(true);
//s.oh^ 2013/05/07 入力不具合修正
        //setFocus(okButton);
//s.oh$
    }
    
    private void setFocus(final JComponent c) {
        SwingUtilities.invokeLater(() -> {
            c.requestFocusInWindow();
        });
    }
    
    /**
     * タイトルフィールドの有効性をチェックする。
     */
    private void checkTitle() {    
        boolean hasTitle = !titleField.getText().trim().isEmpty();
        if (hasTitle) {
            controlButton();
        } else {
            okButton.setEnabled(false);
            tmpButton.setEnabled(false);
        }
    }

    private SaveParamsM viewToModel(boolean temp) {
        
        // 戻り値のSaveparamsを生成する
        SaveParamsM model = new SaveParamsM();
        
        model.setSendMML(enterParams.getSendMML());         
        
        // 戻り値の整理
        // 確定ボタンが押された時    0
        // 仮ボタンが押された時     1
        int returnOption;
        
        if (!temp) {
            returnOption = SaveParamsM.SAVE_AS_FINAL;
            
        } else {
            returnOption = SaveParamsM.SAVE_AS_TMP;
        }
        
        // 開始時と終了時のオプションでKarteEditorで制御する
        model.setEnterOption(enterParams.getEnterOption());
        model.setReturnOption(returnOption);
        // Title候補
        String titleCand = "";
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(SaveDialogNoSendAtTmp.class);
        switch (returnOption) {
            
            case SaveParamsM.SAVE_AS_FINAL:
                // 確定ボタンが押された時
                model.setTmpSave(false);
                model.setClaimDate(getClaimDate());                     // Property
                model.setSendClaim(sendClaim.isSelected());             // CLAIM送信->CheckBox
                model.setSendLabtest(sendLabtest.isSelected());         // Lab.Test送信->CheckBox
                model.setAllowPatientRef(patientCheck.isSelected());    // MML->CheckBox
                model.setAllowClinicRef(clinicCheck.isSelected());      // MML->CheckBox
                titleCand = bundle.getString("title.candidate.progressCourse");
                break;
                
            case SaveParamsM.SAVE_AS_TMP:
                // 仮ボタンが押された時
                model.setTmpSave(true);                                 // 仮保存である
                model.setSendClaim(false);                              // CLAIM送信はしない設定である
                model.setClaimDate(getClaimDate());                     // Property
//s.oh^ 2014/11/04 仮保存時のオーダー出力
                //model.setSendLabtest(false);                            // Lab.Test送信->false 互換性を確保..
                model.setSendLabtest(Project.getBoolean(Project.SEND_TMPKARTE_LABTEST) ? sendLabtest.isSelected() : false);
//s.oh$
                model.setAllowPatientRef(false);                        // MML->送信しない
                model.setAllowClinicRef(false);                         // MML->送信しない
                model.setSendMML(false);
                titleCand = bundle.getString("title.candidate.temporalSave");
                break;
        }
        
        // 文書タイトルを取得する
        String val = (String)titleCombo.getSelectedItem();
        val = (val.isEmpty()) ? titleCand : val;
        model.setTitle(val);
        
        // Department
        val = departmentLabel.getText();
        model.setDepartment(val);
        
        // 印刷部数を取得する
        int count = Integer.parseInt((String)printCombo.getSelectedItem());
        model.setPrintCount(count);
        
        return model;
    }

    private class PopupListener extends PopupCalendarListener {
        
        private PopupListener(JTextField tf, int[] range, SimpleDate[] disabled) {
            super(tf, range,disabled);
        }     

        @Override
        public void setValue(SimpleDate sd) {
            if (!sendClaim.isSelected()) {
                return;
            }
            GregorianCalendar gc = new GregorianCalendar();
            gc.clear();
            gc.set(GregorianCalendar.YEAR, sd.getYear());
            gc.set(GregorianCalendar.MONTH, sd.getMonth());
            gc.set(GregorianCalendar.DATE, sd.getDay());
            setClaimDate(gc.getTime());
        }
    }
}