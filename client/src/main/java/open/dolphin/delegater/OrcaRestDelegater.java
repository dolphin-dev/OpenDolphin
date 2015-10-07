package open.dolphin.delegater;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.DocumentModelConverter;
import open.dolphin.converter.InteractionCodeListConverter;
import open.dolphin.infomodel.*;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author kazushi Minagawa.
 */
public class OrcaRestDelegater extends BusinessDelegater implements OrcaDelegater {
    
    //-------------------------------------------------------------
    // 保険医療機関コード & JMARIコードの連結
    //-------------------------------------------------------------
    @Override
    public String getFacilityCodeBy1001() throws Exception {
        
        // PATH
        String path = "/orca/facilitycode";
        
        // GET
        String fcode = getEasyText(path, String.class);
        
        return fcode;       
    }
    
    //-------------------------------------------------------------------------
    // 併用禁忌チェック
    // masuda 先生の SqlMiscDao からcheckInteractionポーティング。
    //-------------------------------------------------------------------------
    @Override
    public List<DrugInteractionModel> checkInteraction(Collection<String> drug1, Collection<String> drug2) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/orca/interaction");
        String path = sb.toString();
        
        // Wrapper
        List<String> codes1 = new ArrayList<>(drug1);
        List<String> codes2 = new ArrayList<>(drug2);
        InteractionCodeList warpper = new InteractionCodeList();
        warpper.setCodes1(codes1);
        warpper.setCodes2(codes2);
        
        // Converter
        InteractionCodeListConverter conv = new InteractionCodeListConverter();
        conv.setModel(warpper);

        // JSON
        ObjectMapper mapper = this.getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // GET
        DrugInteractionList result = putEasyJson(path, MediaType.APPLICATION_JSON, data, DrugInteractionList.class);
        
        // LIST
        return result.getList();
    }
    
    //-------------------------------------------------------------
    // マスター検索
    //-------------------------------------------------------------
    @Override
    public List<TensuMaster> getTensuMasterByShinku(String shinku, String now) throws Exception {
        
        // ^ とる
        if (shinku.startsWith("^")) {
            shinku = shinku.substring(1);
        }
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/orca/tensu/shinku/");
        sb.append(shinku).append(CAMMA).append(now);
        String path = sb.toString();
        
        // GET
        TensuList result = getEasyJson(path, TensuList.class);
        
        // List
        return result.getList();
    }
    
    @Override
    public List<TensuMaster> getTensuMasterByName(String name, String now, boolean partialMatch) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/orca/tensu/name/");
        sb.append(name).append(CAMMA).append(now).append(CAMMA).append(String.valueOf(partialMatch));
        String path = sb.toString();
        
        // GET
        TensuList result = getEasyJson(path, TensuList.class);
        
        // List
        return result.getList();
    }
    
    @Override
    public List<TensuMaster> getTensuMasterByCode(String regExp, String now) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/orca/tensu/code/");
        sb.append(regExp).append(CAMMA).append(now);
        String path = sb.toString();
        
        // GET
        TensuList result = getEasyJson(path, TensuList.class);
        
        // List
        return result.getList();
    }
    
    @Override
    public List<TensuMaster> getTensuMasterByTen(String ten, String now) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/orca/tensu/ten/");
        sb.append(ten).append(CAMMA).append(now);
        String path = sb.toString();
        
// GET
        TensuList result = getEasyJson(path, TensuList.class);
        
        // List
        return result.getList();
    }
    
    @Override
    public List<DiseaseEntry> getDiseaseByName(String name, String now, boolean partialMatch) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/orca/disease/name/");
        sb.append(name).append(CAMMA).append(now);
        sb.append(CAMMA).append(String.valueOf(partialMatch));
        String path = sb.toString();
        
        // GET
        DiseaseList result = getEasyJson(path, DiseaseList.class);
        
        // List
        return result.getList();
    }
    
    //-------------------------------------------------------------
    // 一般名検索
    //-------------------------------------------------------------
    @Override
    public String getGeneralName(String code) throws Exception {
        // PATH
        String path = "/orca/general/" + code;
        
        // GET
        CodeNamePack result = getEasyJson(path, CodeNamePack.class);
        
        // Name
        return result.getName();        
    }
    
    //-------------------------------------------------------------
    // ORCA入力セット
    //-------------------------------------------------------------
    @Override
    public ArrayList<OrcaInputCd> getOrcaInputSet() throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/orca/inputset");
        String path = sb.toString();
        
        // GET
        OrcaInputCdList result = getEasyJson(path, OrcaInputCdList.class);
        
        // List
        return (ArrayList<OrcaInputCd>)result.getList();
    }
    
    /**
     * 指定された入力セットコードから診療セットを Stamp にして返す。
     * @param inputSetInfo 入力セットの StampInfo
     * @return 入力セットのStampリスト
     * @throws java.lang.Exception
     */    
    @Override
    public ArrayList<ModuleModel> getStamp(ModuleInfoBean inputSetInfo) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/orca/stamp/");
        sb.append(inputSetInfo.getStampId());
        sb.append(CAMMA);
        sb.append(inputSetInfo.getStampName());
        String path = sb.toString();
        
        // GET
        ModuleList result = getEasyJson(path, ModuleList.class);

        // List
        ArrayList<ModuleModel> ret = new ArrayList<>();
        List<ModuleModel> list = result.getList();
        for (ModuleModel m : list) {
            byte[] bytes = m.getBeanBytes();
            IInfoModel model = (IInfoModel)xmlDecode(bytes);
            m.setModel(model);
            m.setBeanBytes(null);
            ret.add(m);
        }
        return ret;
    }
    
    //-------------------------------------------------------------
    // 病名インポート
    //-------------------------------------------------------------
    @Override
    public ArrayList<RegisteredDiagnosisModel> getOrcaDisease(String patientId, String from, String to, Boolean ascend) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/orca/disease/import/");
        sb.append(patientId);
        sb.append(CAMMA);
        sb.append(from);
        sb.append(CAMMA);
        sb.append(to);
        sb.append(CAMMA);
        sb.append(String.valueOf(ascend));
        String path = sb.toString();
        
        // GET
        RegisteredDiagnosisList result = getEasyJson(path, RegisteredDiagnosisList.class);
        
        // List
        return (ArrayList<RegisteredDiagnosisModel>)result.getList();
    }
    
    @Override
    public ArrayList<RegisteredDiagnosisModel> getActiveOrcaDisease(String patientId, boolean asc) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/orca/disease/active/");
        sb.append(patientId);
        sb.append(CAMMA);
        sb.append(String.valueOf(asc));
        String path = sb.toString();
        
        // GET
        RegisteredDiagnosisList result = getEasyJson(path, RegisteredDiagnosisList.class);
        
        // List
        return (ArrayList<RegisteredDiagnosisModel>)result.getList();
    }
    
    
    private Object xmlDecode(byte[] bytes)  {
        
        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                new ByteArrayInputStream(bytes)));
        return d.readObject();
    }
    
    /**
     * CLAIM 送信
     * @param sendModel
     * @return
     * @throws Exception 
     */
    public int sendDocument(DocumentModel sendModel) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/claim");
        String path = sb.toString();
        
        // Converter
        DocumentModelConverter conv = new DocumentModelConverter();
        conv.setModel(sendModel);

        // JSON
        ObjectMapper mapper = this.getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // PUT
        String entityStr = putEasyJson(path, data, String.class);

        // Count
        return Integer.parseInt(entityStr);
    }
    
//s.oh^ 2014/03/13 傷病名削除診療科対応
    @Override
    public ArrayList<String> getDeptInfo() throws Exception {
        ArrayList<String> list = new ArrayList<>(0);
        
        // PATH
        String path = "/orca/deptinfo";
        
        // GET
        String line = getEasyText(path, String.class);
        
        String[] tmps = line.split(",");       
        for(String tmp : tmps) {
            if(tmp.trim().length() > 0) {
                list.add(tmp);
            }
        }
        
        return list;
    }
//s.oh$
}
