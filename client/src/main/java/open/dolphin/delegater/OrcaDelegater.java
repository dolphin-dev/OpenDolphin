package open.dolphin.delegater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import open.dolphin.infomodel.*;

/**
 *
 * @author Kazushi Minagawa.
 */
public interface OrcaDelegater {
    
    // 保険医療機関コードとJMARIコードを取得する
    public String getFacilityCodeBy1001() throws Exception;
    
    // 併用禁忌をチェックする
    public List<DrugInteractionModel> checkInteraction(Collection<String> drug1, Collection<String> drug2) throws Exception;
    
    // 点数マスター検索
    public List<TensuMaster> getTensuMasterByShinku(String shinku, String now) throws Exception;
    
    // 点数マスター検索
    public List<TensuMaster> getTensuMasterByName(String name, String now, boolean partialMatch) throws Exception;
    
    // 点数マスター検索
    public List<TensuMaster> getTensuMasterByCode(String regExp, String now) throws Exception;
    
    // 点数マスター検索
    public List<TensuMaster> getTensuMasterByTen(String ten, String now) throws Exception;
    
    // 病名マスター検索
    public List<DiseaseEntry> getDiseaseByName(String name, String now, boolean partialMatch) throws Exception;
    
    // 一般名を検索する
    public String getGeneralName(String code) throws Exception;
    
    // ORCA入力セット検索
    public ArrayList<OrcaInputCd> getOrcaInputSet() throws Exception;
    
    // 入力セットをスタンプとして返す
    public ArrayList<ModuleModel> getStamp(ModuleInfoBean inputSetInfo) throws Exception;
    
    // 病名インポート
    public ArrayList<RegisteredDiagnosisModel> getOrcaDisease(String patientId, String from, String to, Boolean ascend) throws Exception;
    
    // Active病名検索
    public ArrayList<RegisteredDiagnosisModel> getActiveOrcaDisease(String patientId, boolean asc) throws Exception;
}
