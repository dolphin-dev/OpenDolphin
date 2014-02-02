package open.dolphin.client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.project.Project;

/**
 * SaveDialog
 * (予定カルテ対応)
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 */
public final class SaveDialogDependsOnCheckAtTmp extends AbstractSaveDialog {
    
    private static final String SAVE = "保存";

    // 設定: 仮保存の時チェックに従う
    private static final String CHK_TITLE_DEPENDS_ON_CHECK_AT_TMP = "診療行為を送信する";
    private static final String TOOLTIP_DEPENDS_ON_CHECK = "診療行為の送信はチェックボックスに従います。";
    
    // 保存ボタン
    private JButton okButton;
    
    // MML送信時のアクセス権設定
    private JCheckBox patientCheck;
    private JCheckBox clinicCheck;
    
//minagawa CLAIM送信日
    private boolean claimDateEditable;
    private Date claimDate;
    private JTextField dateField;
//minagawa$    
    
    // LabTest 送信
    private JCheckBox sendLabtest;

    public SaveDialogDependsOnCheckAtTmp() {
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
        SimpleDateFormat frmt = new SimpleDateFormat(IInfoModel.DATE_FORMAT_FOR_SCHEDULE);
        dateField.setText(frmt.format(claimDate));
    }
    
    /**
     * コンポーネントにSaveParamsの値を設定する。
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
        for (String str : titles) {
            if (str != null && (!str.equals("") && (!str.equals("経過記録")))) {
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
    }

    /**
     * GUIコンポーネントを初期化する。
     */
    private JPanel createComponent() {
                
        // content
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(0, 1));
        
        // 文書Title
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleCombo = new JComboBox(TITLE_LIST);
        titleCombo.setPreferredSize(new Dimension(220, titleCombo.getPreferredSize().height));
        titleCombo.setMaximumSize(titleCombo.getPreferredSize());
        titleCombo.setEditable(true);
        p.add(new JLabel("タイトル:"));
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
        p1.add(new JLabel("診療科:"));
        p1.add(departmentLabel);
        
        p1.add(Box.createRigidArea(new Dimension(11, 0)));
        
        // Print
        printCombo = new JComboBox(PRINT_COUNT);
        printCombo.setSelectedIndex(1);
        p1.add(new JLabel("印刷部数:"));
        p1.add(printCombo);
        
        content.add(p1);
        
        // AccessRightを設定するボタンとパネルを生成する
        patientCheck = new JCheckBox("患者に参照を許可する");
        clinicCheck = new JCheckBox("診療歴のある病院に参照を許可する");
        
        //---------------------------
        // CLAIM 送信ありなし
        //--------------------------- 
        sendClaimAction = new AbstractAction(CHK_TITLE_DEPENDS_ON_CHECK_AT_TMP) {
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
            dateField.setToolTipText("右クリックでカレンダーがポップアップします");
            // 1ヶ月前まで
            int[] range = {-1, 0};
            // 今日以前でないと駄目
            SimpleDate[] acceptRange = new SimpleDate[2];
            acceptRange[0] = null;
            acceptRange[1] = new SimpleDate(new GregorianCalendar());
            PopupListener pl = new PopupListener(dateField, range, acceptRange);
            JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p2.add(new JLabel("送信日:"));
            p2.add(dateField);
            content.add(p2);
        }
//minagawa&        

        //---------------------------
        // 検体検査オーダー送信ありなし
        //---------------------------
        sendLabtest = new JCheckBox("検体検査オーダー（仮保存の場合はしない）");
        if (Project.getBoolean(Project.SEND_LABTEST)) {
            JPanel p6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p6.add(sendLabtest);
            content.add(p6);
        }

        // OK button
        okButton = new JButton(SAVE);
        okButton.setToolTipText(TOOLTIP_DEPENDS_ON_CHECK);
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // 戻り値のSaveparamsを生成する
                value = viewToModel(false);
                if (value != null) {
                    close();
                }
            }
        });
        okButton.setEnabled(false);
        
        // Cancel Button
        String buttonText =  (String)UIManager.get("OptionPane.cancelButtonText");
        cancelButton = new JButton(buttonText);
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                value = null;
                close();
            }
        });
        
        // 仮保存 button
        tmpButton = new JButton(TMP_SAVE);
        tmpButton.setToolTipText(TOOLTIP_DEPENDS_ON_CHECK);
        tmpButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // 戻り値のSaveparamsを生成する
                value = viewToModel(true);
                if (value != null) {
                    close();
                }
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
        setFocus(okButton);
    }
    
    private void setFocus(final JComponent c) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                c.requestFocusInWindow();
            }
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
//minagawa^ LSC Test        
        model.setSendMML(enterParams.getSendMML());
//minagawa$        
        
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
        
        switch (returnOption) {
            
            case SaveParamsM.SAVE_AS_FINAL:
                // 確定ボタンが押された時
                model.setTmpSave(false);
                model.setClaimDate(getClaimDate());                     // Property
                model.setSendClaim(sendClaim.isSelected());             // CLAIM送信->CheckBox
                model.setSendLabtest(sendLabtest.isSelected());         // Lab.Test送信->CheckBox
                model.setAllowPatientRef(patientCheck.isSelected());    // MML->CheckBox
                model.setAllowClinicRef(clinicCheck.isSelected());      // MML->CheckBox
                titleCand = "経過記録";
                break;
                
            case SaveParamsM.SAVE_AS_TMP:
                // 仮ボタンが押された時
                model.setTmpSave(true);
                model.setSendClaim(sendClaim.isSelected());             // CLAIM送信->CheckBoxへ従う
                model.setClaimDate(getClaimDate());                     // Property
                model.setSendLabtest(false);                            // Lab.Test送信->false 互換性を確保..
                model.setAllowPatientRef(false);                        // MML->送信しない
                model.setAllowClinicRef(false);                         // MML->送信しない
                model.setSendMML(false);
                titleCand = "仮保存";
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