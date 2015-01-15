package open.dolphin.client;

import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;
import open.dolphin.delegater.MasudaDelegater;
import open.dolphin.delegater.OrcaDelegater;
import open.dolphin.delegater.OrcaDelegaterFactory;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.*;
import open.dolphin.util.Log;

/**
 * KarteEditorで保存したとき呼ばれる
 * KartePane内の薬剤をリストアップしてOrcaの薬剤併用データベースで検索
 * 内服薬のみ。注射はなし。
 * 臨時処方の日数や長期処方制限、２錠／分３などの確認も行う
 * 
 * @author masuda, Masuda Naika
 */
public class CheckMedication {
    
    protected static final String MEDICATION_CHECK_RESULT = "medicationCheckResult";
    // 2013/04/22
//minagawa^ 定例打ち合わせ    
    private static final String yakuzaiClassCode = "2";    // 薬剤のclaim class code
    private static final int searchPeriod = 3;
    private HashMap<String, String[]> rirekiItems;      // カルテに記録されている薬剤
    private long karteId;
//minagawa$    
    
    private HashMap<String, String> drugCodeNameMap;
    private List<ModuleModel> moduleList;
//    private List<BundleMed> medList;         // 内服薬
    private List<BundleDolphin> bundleList;  // 注射も含む
    
    private PropertyChangeSupport boundSupport;
    private boolean result;
    
    public CheckMedication() {
        boundSupport = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(MEDICATION_CHECK_RESULT, l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(MEDICATION_CHECK_RESULT, l);
    }
    
    public void setResult(boolean newResult) {
        result = newResult;
        boundSupport.firePropertyChange(MEDICATION_CHECK_RESULT, !result, result);
    }
 
    public void checkStart(Chart context, List<ModuleModel> stamps) {
        // 2013/04/22
//minagawa^ 定例打ち合わせ        
        karteId = context.getKarte().getId();
//minagawa$        
        moduleList = stamps;
        makeDrugList();
        int len = drugCodeNameMap.size();
        // 薬なかったらリターン
        if (len == 0){
            setResult(false);
            return;
        }

        DBTask task = new DBTask<List<DrugInteractionModel>, Void>(context) {

            @Override
            protected List<DrugInteractionModel> doInBackground() throws Exception {

                // 2013/04/22                
//minagawa^ 定例打ち合わせ　過去３ヶ月分の処方を取得する
                Collection<String> codes = drugCodeNameMap.keySet();
                collectMedicine();
//s.oh^ 2013/09/13 併用禁忌チェック修正
                //Collection<String> pastCodes = (rirekiItems!=null && !rirekiItems.isEmpty())
                //        ? rirekiItems.keySet()
                //        : codes;
                Collection<String> pastCodes = new ArrayList();
                List<String> keys = new ArrayList<String>(codes);
                for(int i = 0; i < keys.size(); i++) {
                    pastCodes.add(keys.get(i));
                }
                if(rirekiItems != null && !rirekiItems.isEmpty()) {
                    keys = new ArrayList<String>(rirekiItems.keySet());
                    for(int i = 0; i < keys.size(); i++) {
                        pastCodes.add(keys.get(i));
                    }
                }
//s.oh$
                OrcaDelegater odl = OrcaDelegaterFactory.create();
                //List<DrugInteractionModel> list = odl.checkInteraction(codes, codes);
                List<DrugInteractionModel> list = odl.checkInteraction(codes, pastCodes);
//minagawa$                
                return list;
            }
            
            @Override
            protected void succeeded(List<DrugInteractionModel> list) {
                
                if (list!=null && !list.isEmpty()){
                    StringBuilder sb = new StringBuilder();
                    for (DrugInteractionModel model : list){
                        StringBuilder tmp = new StringBuilder();
                        tmp.append("<併用禁忌> ");
                        tmp.append(drugCodeNameMap.get(model.getSrycd1()));
                        tmp.append(" と ");
                        // 2013/04/22
//minagawa^ 定期打ち合わせ                        
                        //tmp.append(drugCodeNameMap.get(model.getSrycd2()));
                        if (rirekiItems!=null && !rirekiItems.isEmpty()) {
//s.oh^ 2013/09/13 併用禁忌チェック修正
                            //tmp.append(rirekiItems.get(model.getSrycd2()));
                            String[] str = rirekiItems.get(model.getSrycd2());
                            if(str != null && str.length > 0) {
                                tmp.append(str[0]);
                            } else {
                                tmp.append(drugCodeNameMap.get(model.getSrycd2()));
                            }
//s.oh$
                        } else {
                            tmp.append(drugCodeNameMap.get(model.getSrycd2()));
                        }
//minagawa$                        
                        tmp.append("\n");
                        tmp.append(model.getSskijo());
                        tmp.append(" ");
                        tmp.append(model.getSyojyoucd());
                        tmp.append("\n");
                        sb.append(formatMsg(tmp.toString()));
                    }
                    String msg = sb.toString();
                    Toolkit.getDefaultToolkit().beep();
                    //String[] options = {"取消", "無視"};
                    String[] options = {GUIFactory.getCancelButtonText(), "無視"};
                    int val = JOptionPane.showOptionDialog(context.getFrame(), msg, ClientContext.getFrameTitle("薬剤併用警告"),
                            JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, ClientContext.getFrameTitle("薬剤併用警告"), msg);
                    
                    switch (val) {
                        case 0:
                            Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, "取消");
                            setResult(true);
                            break;
                            
                        case 1:
                            Log.outputOperLogDlg(null, Log.LOG_LEVEL_0, "無視");
                            setResult(false);
                            break;
                    }
                } else {
                    setResult(false);
                }
            }
            
            @Override
            protected void failed(Throwable e) {
                setResult(false);
            }
        };
        
        task.execute();
    }

    private void makeDrugList() {
        
        drugCodeNameMap = new HashMap<String, String>();
        bundleList = new ArrayList<BundleDolphin>();
//        medList = new ArrayList<BundleMed>();
        
        for (ModuleModel stamp : moduleList) {
            String entity = stamp.getModuleInfoBean().getEntity();
            if (IInfoModel.ENTITY_MED_ORDER.equals(entity) || IInfoModel.ENTITY_INJECTION_ORDER.equals(entity)) {
                BundleDolphin bundle = (BundleDolphin) stamp.getModel();
                bundleList.add(bundle);
                ClaimItem[] ci = bundle.getClaimItem();
                if(ci != null) {
                    for (ClaimItem c : ci) {
                        if (c.getClassCode() != null && ClaimConst.YAKUZAI == Integer.valueOf(c.getClassCode())) {
                            drugCodeNameMap.put(c.getCode(), c.getName());
                        }
                    }
                }
            }
//            if (IInfoModel.ENTITY_MED_ORDER.equals(entity)) {
//                //System.err.println(stamp.getModel());
//                BundleMed bundle = (BundleMed) stamp.getModel();
//                medList.add(bundle);
//            }
        }
    }

        // 2013/04/22    
//minagawa^ 定例打ち合わせ masuda先生コードをコピー   
    private void collectMedicine() {

        rirekiItems = new HashMap();

        // 過去３ヶ月の薬剤・注射ののModuleModelを取得する
        MasudaDelegater del = MasudaDelegater.getInstance();
        List<String> entities = new ArrayList();
        entities.add(IInfoModel.ENTITY_MED_ORDER);
        entities.add(IInfoModel.ENTITY_INJECTION_ORDER);

        GregorianCalendar gcTo = new GregorianCalendar();
        gcTo.add(GregorianCalendar.DAY_OF_MONTH,1);
        Date toDate = gcTo.getTime();
        GregorianCalendar gcFrom = new GregorianCalendar();
        gcFrom.add(GregorianCalendar.MONTH, -searchPeriod);
        Date fromDate = gcFrom.getTime();
        
        List<ModuleModel> pastModuleList = del.getModulesEntitySearch(karteId, fromDate, toDate, entities);
        if (pastModuleList == null) {
            return;
        }

        // ModuleModelの薬剤を取得
        for (ModuleModel mm : pastModuleList) {
            ClaimBundle cb = (ClaimBundle) mm.getModel();
            for (ClaimItem ci : cb.getClaimItem()) {
                if (yakuzaiClassCode.equals(ci.getClassCode())) {     // 用法などじゃなくて薬剤なら、薬剤リストに追加
                    final SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
                    String code = ci.getCode();     // コード
                    String name = ci.getName();     // 薬剤名
                    String date = frmt.format(mm.getStarted());     // 処方日
                    rirekiItems.put(code, new String[]{name, date});
                }
            }
        }
    }
//minagawa$    

    private String formatMsg(String str) {
        final int width = 40;       // 桁数
        int pos = 0;
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < str.length(); ++i) {
            String c = str.substring(i, i + 1);
            if ("\n".equals(c)){
                pos = 0;
            } else if (pos == width){
                pos = 0;
                buf.append("\n");
            } else {
                ++pos;
            }
            buf.append(c);
        }

        return buf.toString();
    }
}
