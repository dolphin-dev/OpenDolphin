package open.dolphin.client;

import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import javax.swing.JOptionPane;
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
                OrcaDelegater odl = OrcaDelegaterFactory.create();
                List<DrugInteractionModel> list = odl.checkInteraction(codes, codes);
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
                        tmp.append(drugCodeNameMap.get(model.getSrycd2()));
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
        
        drugCodeNameMap = new HashMap<String, String>();
        bundleList = new ArrayList<BundleDolphin>();
//        medList = new ArrayList<BundleMed>();
        
        for (ModuleModel stamp : moduleList) {
            String entity = stamp.getModuleInfoBean().getEntity();
            if (IInfoModel.ENTITY_MED_ORDER.equals(entity) || IInfoModel.ENTITY_INJECTION_ORDER.equals(entity)) {
                BundleDolphin bundle = (BundleDolphin) stamp.getModel();
                bundleList.add(bundle);
                ClaimItem[] ci = bundle.getClaimItem();
                for (ClaimItem c : ci) {
                    if (ClaimConst.YAKUZAI == Integer.valueOf(c.getClassCode())) {
                        drugCodeNameMap.put(c.getCode(), c.getName());
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
