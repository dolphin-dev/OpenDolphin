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
      
    private static final String yakuzaiClassCode = "2";    // 薬剤のclaim class code
    private static final int searchPeriod = 3;
    private HashMap<String, String[]> rirekiItems;      // カルテに記録されている薬剤
    private long karteId;   
    
    private HashMap<String, String> drugCodeNameMap;
    private List<ModuleModel> moduleList;
    private List<BundleDolphin> bundleList;  // 注射も含む
    
    private final PropertyChangeSupport boundSupport;
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
        
        karteId = context.getKarte().getId();    
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
                
                Collection<String> codes = drugCodeNameMap.keySet();
                collectMedicine();
                
                Collection<String> pastCodes = new ArrayList();
                List<String> keys = new ArrayList<>(codes);
                for(int i = 0; i < keys.size(); i++) {
                    pastCodes.add(keys.get(i));
                }
                if(rirekiItems != null && !rirekiItems.isEmpty()) {
                    keys = new ArrayList<>(rirekiItems.keySet());
                    for(int i = 0; i < keys.size(); i++) {
                        pastCodes.add(keys.get(i));
                    }
                }
                OrcaDelegater odl = OrcaDelegaterFactory.create();
                List<DrugInteractionModel> list = odl.checkInteraction(codes, pastCodes);         
                return list;
            }
            
            @Override
            protected void succeeded(List<DrugInteractionModel> list) {
                
                if (list!=null && !list.isEmpty()){
                    java.util.ResourceBundle bundle = ClientContext.getMyBundle(CheckMedication.class);
                    StringBuilder sb = new StringBuilder();
                    for (DrugInteractionModel model : list){
                        StringBuilder tmp = new StringBuilder();
                        tmp.append(bundle.getString("ContraindicationsForCoadministration"));
                        tmp.append(drugCodeNameMap.get(model.getSrycd1()));
                        tmp.append(bundle.getString("text.and "));
                        
                        if (rirekiItems!=null && !rirekiItems.isEmpty()) {
                            String[] str = rirekiItems.get(model.getSrycd2());
                            if(str != null && str.length > 0) {
                                tmp.append(str[0]);
                            } else {
                                tmp.append(drugCodeNameMap.get(model.getSrycd2()));
                            }
                        } else {
                            tmp.append(drugCodeNameMap.get(model.getSrycd2()));
                        }         
                        tmp.append("\n");
                        tmp.append(model.getSskijo());
                        tmp.append(" ");
                        tmp.append(model.getSyojyoucd());
                        tmp.append("\n");
                        sb.append(formatMsg(tmp.toString()));
                    }
                    String msg = sb.toString();
                    Toolkit.getDefaultToolkit().beep();
                    String[] options = {GUIFactory.getCancelButtonText(), bundle.getString("option.ignore")};
                    int val = JOptionPane.showOptionDialog(context.getFrame(), msg, ClientContext.getFrameTitle(bundle.getString("title.optionPane.Coadministration")),
                            JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                    
                    switch (val) {
                        case 0:
                            setResult(true);
                            break;
                            
                        case 1:
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
        
        drugCodeNameMap = new HashMap<>();
        bundleList = new ArrayList<>();
        
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
        }
    }
  
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
