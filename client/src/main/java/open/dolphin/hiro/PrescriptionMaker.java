package open.dolphin.hiro;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import open.dolphin.client.Chart;
import open.dolphin.client.ClientContext;
import open.dolphin.delegater.OrcaDelegater;
import open.dolphin.delegater.OrcaDelegaterFactory;
import open.dolphin.helper.InfiniteProgressBar;
import open.dolphin.infomodel.BundleMed;
import open.dolphin.infomodel.ClaimConst;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.PriscriptionModel;
import open.dolphin.project.Project;

/**
 *
 * @author Hiro Clinic MASATO
 * minagawa 2012-09-07 OpenDolphin へポート
 */
public class PrescriptionMaker {
    
    /** 処方せん記載確認パネル */
    private PostConfirmPanel prePanel;
    
    /** 処方せん記載確認ダイアログ */
    private JDialog dialog;
    
    /** 患者情報記載有無フラグ */
    private boolean chkPatientInfo;
    
    /** 麻薬施用者番号記載有無フラグ */
    private boolean chkUseDrugInfo;
    
    /** 在宅記載有無フラグ */
    private boolean chkHomeMedical;
    
    /** 処方せん使用期間 */
    private Date period; // @009
    
    /** 現在日 */
    private Date now; // @009
// @003    private final String LICENSE_DOCTOR = "doctor";
    /**
     * @002 2009/09/11 追加
     * 患者情報転記確認ダイアログを生成する。
     */
    private Chart chart;
    
    private DocumentModel docModel;
    
    private InfiniteProgressBar progressBar;
    
    public void setChart(Chart chart) {
        this.chart = chart;
        
    }
    
    public void setDocumentModel(DocumentModel docModel) {
        this.docModel = docModel;
    }
    
    public void start() {
        
        // Dialog のViewを生成する
        prePanel = new PostConfirmPanel();
        
        // Options
        final JButton okBtn = new JButton("PDF作成");
        final JButton cancelBtn = new JButton((String)UIManager.get("OptionPane.cancelButtonText"));
        Object[] options = new Object[]{okBtn, cancelBtn};
        
        // ダイアログを生成する
        JOptionPane jop = new JOptionPane(prePanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, okBtn);
        dialog = jop.createDialog(chart.getFrame(), ClientContext.getFrameTitle("処方せん出力"));
        
        // 備考欄に「在宅」を記入 default=true;
        chkHomeMedical = true;
        prePanel.getChkHomeMedical().setSelected(chkHomeMedical);
        
        // 患者氏名、住所を転帰 default=true;
        chkPatientInfo = true;
        prePanel.getChkPatientInfo().setSelected(chkPatientInfo);
        
        // 麻薬施用者免許番号を転帰
        chkUseDrugInfo = false;
        prePanel.getChkUseDrugInfo().setSelected(chkUseDrugInfo);

        // 麻薬施用者免許番号  新規属性 2012090
        String useDrugId = Project.getUserModel().getUseDrugId();
        prePanel.getUseDrugId().setText(useDrugId);
        
        // @003 2009/09/14 仕様変更：麻薬施用者免許証番号が登録されていない場合は、非活性にする
        if (useDrugId == null || "".equals(useDrugId)) {
            prePanel.getChkUseDrugInfo().setEnabled(false);
            prePanel.getUseDrugId().setEnabled(false);
            prePanel.getLblUseDrugId().setEnabled(false);
            prePanel.getLblNarcoticsPractitioner().setEnabled(false);
        } else {
            prePanel.getChkUseDrugInfo().setEnabled(true);
            prePanel.getUseDrugId().setEnabled(true);
            prePanel.getLblUseDrugId().setEnabled(true);
            prePanel.getLblNarcoticsPractitioner().setEnabled(true);
            // login user
            prePanel.getNarcoticsPractitioner().setText(Project.getUserModel().getCommonName());
        }

    // ********** @009 2010/07/01 ↓↓ **********
        // 使用期間
        PopupListener pl = new PopupListener(prePanel.getTfPeriod());
    // ********** @009 2010/07/01 ↑↑ **********
        
//minagawa^ 保険医療機関コードの取得を促す
        prePanel.getFacilitCodeField().setText(Project.getBasicInfo());
        prePanel.getFacilitCodeField().setEnabled(false);
        
        // 一般名で出力する
        boolean check = Project.getBoolean("prescription.output.general");
        prePanel.getGeneralChk().setSelected(check);
        
//minagawa$        

        /* OKボタンアクションリスナー設定 */
        okBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (chkDate() > 0) {
                    return; // @009 2010/07/01
                }
                dialog.setVisible(false);
                dialog.dispose();
                createPDF();
            }
        });

        /* キャンセルボタンアクションリスナー設定 */
        cancelBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        /* 患者住所氏名転記チェックボックスアクションリスナー設定 */
        prePanel.getChkPatientInfo().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (chkPatientInfo) {
                    chkPatientInfo = false;
                } else {
                    chkPatientInfo = true;
                }
            }
        });

        /* 麻薬施用者免許証番号転記チェックボックスアクションリスナー設定 */
        prePanel.getChkUseDrugInfo().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (chkUseDrugInfo) {
                    if (!prePanel.getChkPatientInfo().isEnabled()) {
                        prePanel.getChkPatientInfo().setEnabled(true);
                    }
                    chkUseDrugInfo = false;
                } else {
                    chkUseDrugInfo = true;
                    prePanel.getChkPatientInfo().setSelected(true);
                    chkPatientInfo = true;
                    prePanel.getChkPatientInfo().setEnabled(false);
                }
            }
        });

        /* 備考欄に「在宅」を記載するかどうかのチェックボックスアイテムリスナーの設定 */
        prePanel.getChkHomeMedical().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (chkHomeMedical) {
                    chkHomeMedical = false;
                } else {
                    chkHomeMedical = true;
                    prePanel.getChkPatientInfo().setSelected(true);
                }
            }
        });
        
        // 一般名出力
        prePanel.getGeneralChk().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Project.setBoolean("prescription.output.general",prePanel.getGeneralChk().isSelected());
            }
        });

        // ダイアログ表示
        dialog.setVisible(true);
    }

// ********** @009 2010/07/01 ↓↓ **********
    /**
     * 処方せん出力確認ダイアログの入力項目チェックメソッド
     * 日付の前後チェックなどを行う。
     * @return int 0:エラーなし 0以外:エラーあり
     */
    private int chkDate() {
        String errTitle = "入力エラー";
        //String alertTitle = "年月日アラート";
        String errMsg = "は「2010-05-10」形式で入力してください。";
        //String alertMsg = "が今日より前の日付ですがよろしいですか？";
        int ret = 0; // チェック結果
        //int select = 0; // メッセージダイアログのボタン選択値
        try {       
            Date delivery = Utils.chkDate(Utils.getDateFormat().format(docModel.getStarted()));    
            now = Utils.chkDate(Utils.getDateFormat().format(new Date())); // 現在日
            // 使用期間入力チェック
            period = null;
            JTextField chkTarget = prePanel.getTfPeriod();
            if (!"".equals(chkTarget.getText())) {
                // 使用期間が入力あり
                period = Utils.chkDate(chkTarget.getText());
                if (period == null) {
                    // 使用期間の日付チェックエラー
                    ret = 1;
                    JOptionPane.showMessageDialog(prePanel, chkTarget.getName() + errMsg, errTitle, JOptionPane.ERROR_MESSAGE);
                    chkTarget.requestFocus();
                } else if ((delivery != null) && period.before(delivery)) {
                    JOptionPane.showMessageDialog(prePanel, chkTarget.getName() + "に交付年月日より前の日が入力されています。", errTitle, JOptionPane.ERROR_MESSAGE);
                    chkTarget.requestFocus();
                    ret = 1;
                } else if (period.before(now)) {
                    JOptionPane.showMessageDialog(prePanel, chkTarget.getName() + "に今日より前の日が入力されています。", errTitle, JOptionPane.ERROR_MESSAGE);
                    chkTarget.requestFocus();
                    ret = 1;
                }
            }
        } catch (Exception e) {
            ret = 2;
            ClientContext.getBootLogger().error(e.getMessage(), e.getCause());
        }
        return ret;
    }
// ********** @009 2010/07/01 ↑↑ **********
  
//minagawa^     
    // 内用かどうかを返す
    private boolean bundleIsNaiyo(BundleMed med) {
        // 内用のみ
        ClaimItem[] items = med.getClaimItem();
        boolean naiyo = (items!=null && items.length>0);
        if (naiyo) {
            for (ClaimItem item : items) {
                if (item.getYkzKbn()==null || (!item.getYkzKbn().equals(ClaimConst.YKZ_KBN_NAIYO))) {
                    naiyo = false;
                    break;
                }
            }
        }
        return naiyo;
    }
    
    private void createPDF() {
        
        final SwingWorker worker = new SwingWorker<String,Void>() {

            @Override
            protected String doInBackground() throws Exception {
                
                List<BundleMed> naiyoList = new ArrayList<BundleMed>();
                List<BundleMed> otherList = new ArrayList<BundleMed>();
                
                Collection<ModuleModel> modules = docModel.getModules();
                
                // 院外処方のみ集める
                for (ModuleModel module : modules) {
                    
                    String entity = module.getModuleInfoBean().getEntity();
                    IInfoModel model = module.getModel();
                    
                    if (IInfoModel.ENTITY_MED_ORDER.equals(entity) && model instanceof BundleMed) {
                        
                        BundleMed med = (BundleMed)model;
                        
                        // 院外処方
                        if (med.getClassCode()!=null && med.getClassCode().endsWith("2")) {
                            
                            // クローン生成
                            BundleMed clone = ModelUtils.cloneBundleMed(med);
                            if (clone!=null) {
                            
                                // 内用のみ
                                if (bundleIsNaiyo(med)) {
                                    //臨時の診療行為コードを変換する
                                    if (clone.getClassCode().startsWith("29")) {
                                        clone.setClassCode("212");  // 臨時の内用を内用に
                                    }
                                    naiyoList.add(clone);

                                } else {
                                    //臨時の診療行為コードを変換する
                                    if (clone.getClassCode().startsWith("29")) {
                                        clone.setClassCode("232");  // 臨時の外用を外用に
                                    }
                                    otherList.add(clone);
                                }
                            }
                        }
                    }
                }
                
                // 内用の同じ用法をまとめる
                if (!naiyoList.isEmpty()) {
                    
                    HashMap<String,BundleMed> map = new HashMap<String,BundleMed>();
                    
                    for (BundleMed test : naiyoList) {
                        
                        String key = test.getAdminCode()+test.getBundleNumber();
                        
                        if (map.get(key)!=null) {
                            BundleMed same = map.get(key);
                            same.merge(test);
                        } else {
                            map.put(key, test);
                        }
                    }
                    
                    // Map から新規に内用のリストを作る
                    naiyoList.clear();
                    naiyoList.addAll(map.values());
                    map.clear();
                }
                
                List<BundleMed> allList = new ArrayList<BundleMed>(naiyoList.size()+otherList.size());
                allList.addAll(naiyoList);
                allList.addAll(otherList);
                naiyoList.clear();
                otherList.clear();
                
                // 一般名に変更する
                if (Project.getBoolean("prescription.output.general")) {
                    
                    for (BundleMed med : allList) {

                        ClaimItem[] items = med.getClaimItem();
                        if (items==null || items.length==0) {
                            continue;
                        }

                        for (ClaimItem item : items) {

                            // 薬剤ではない
                            if (!item.getClassCode().equals("2")) {
                                continue;
                            }

                            // 一般名を検索し名称を入れ替える
                            OrcaDelegater odl = OrcaDelegaterFactory.create();
                            String gname = odl.getGeneralName(item.getCode());
                            item.setName("【般】" + gname);
                        }
                    }
                }
                
                PriscriptionModel pkg = new PriscriptionModel();
                pkg.setPatientId(chart.getPatient().getPatientId());
                pkg.setPatientName(chart.getPatient().getFullName());
                pkg.setPatientKana(chart.getPatient().getKanaName());
                pkg.setPatientSex(chart.getPatient().getGenderDesc());
                pkg.setPatientBirthday(chart.getPatient().getBirthday());
                if (chart.getPatient().getSimpleAddressModel()!=null) {
                    pkg.setPatientZipcode(chart.getPatient().getSimpleAddressModel().getZipCode());
                    pkg.setPatientAddress(chart.getPatient().getSimpleAddressModel().getAddress());
                }
                pkg.setPatientTelephone(chart.getPatient().getTelephone());
                
                pkg.setPriscriptionList(allList);
                pkg.setIssuanceDate(docModel.getStarted());
                pkg.setApplyedInsurance(chart.getHealthInsuranceToApply(docModel.getDocInfoModel().getHealthInsuranceGUID()));
                pkg.setInstitutionNumber(Project.getBasicInfo());
                
                pkg.setPhysicianName(Project.getUserModel().getCommonName());
                pkg.setDrugLicenseNumber(Project.getUserModel().getUseDrugId());
                pkg.setInstitutionName(Project.getUserModel().getFacilityModel().getFacilityName());
                pkg.setInstitutionZipcode(Project.getUserModel().getFacilityModel().getZipCode());
                pkg.setInstitutionAddress(Project.getUserModel().getFacilityModel().getAddress());
                pkg.setInstitutionTelephone(Project.getUserModel().getFacilityModel().getTelephone());
                
                pkg.setChkPatientInfo(chkPatientInfo);
                pkg.setChkUseDrugInfo(chkUseDrugInfo);
                pkg.setChkHomeMedical(chkHomeMedical);
                pkg.setPeriod(period);
                
                PrescriptionPDFMaker maker = new PrescriptionPDFMaker(pkg);
                
                if (Project.getString(Project.LOCATION_PDF)!=null) {
                    maker.setDocumentDir(Project.getString(Project.LOCATION_PDF));
                } else {
                    maker.setDocumentDir(ClientContext.getPDFDirectory());
                }
                
                String pathToPDF = maker.output();
                return pathToPDF;               
            }
            
            @Override
            protected void done() {
                try {
                    String pathToPDF = get();
                    if (pathToPDF!=null) {
                        try {
                            Desktop.getDesktop().open(new File(pathToPDF));
                        } catch (IOException ex) {
                            ex.printStackTrace(System.err);
                            JOptionPane.showMessageDialog(chart.getFrame(), ex.getMessage(), "処方せん出力エラー", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                } catch (ExecutionException ex) {
                    ex.printStackTrace(System.err);
                    JOptionPane.showMessageDialog(chart.getFrame(), ex.getMessage(), "処方せん出力エラー", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getNewValue()==SwingWorker.StateValue.STARTED) {
                    progressBar = new InfiniteProgressBar("処方せん出力", "処方せんを作成中です...", chart.getFrame());
                    progressBar.start();
                }
                else if (e.getNewValue()==SwingWorker.StateValue.DONE) {
                    progressBar.stop();
                    progressBar = null;
                    worker.removePropertyChangeListener(this);
                }
            }
        });
        
        worker.execute();
        
//minagawa$         
    }
}
