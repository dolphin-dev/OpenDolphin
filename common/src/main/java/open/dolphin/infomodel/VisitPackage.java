package open.dolphin.infomodel;

import java.util.List;

/**
 *
 * @author Kazushi Minagawa.
 */
public class VisitPackage extends InfoModel implements java.io.Serializable {
    
    // KarteBeanのPK
    private long kartePk;
    
    // 保健医療機関コードとJMARIコードの連結
    private String number;
    
    // 来院情報
    private PatientVisitModel patientVisitModel;
    
    // 患者情報
    private PatientModel patientModel;
    
    // 文書（カルテ）
    private DocumentModel documenModel;
    
    // Active病名
    private List<RegisteredDiagnosisModel> disease;
    
    // アレルギー
    private List<AllergyModel> allergies;
    
    // メモ
    private PatientMemoModel patientMemoModel;
    
    // 作成モード
    private int mode;

    public long getKartePk() {
        return kartePk;
    }
    
    public void setKartePk(long kartePk) {
        this.kartePk = kartePk;
    }
    
    public PatientVisitModel getPatientVisitModel() {
        return patientVisitModel;
    }
    
    public void setPatientVisitModel(PatientVisitModel patientVisitModel) {
        this.patientVisitModel = patientVisitModel;
    }
    
    public PatientModel getPatientModel() {
        return patientModel;
    }

    public void setPatientModel(PatientModel patientMode) {
        this.patientModel = patientMode;
    }
    
    public DocumentModel getDocumenModel() {
        return documenModel;
    }

    public void setDocumenModel(DocumentModel documenModel) {
        this.documenModel = documenModel;
    }

    public int getMode() {
        return mode;
    }
    
    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<AllergyModel> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<AllergyModel> allergies) {
        this.allergies = allergies;
    }

    public PatientMemoModel getPatientMemoModel() {
        return patientMemoModel;
    }

    public void setPatientMemoModel(PatientMemoModel patientMemoModel) {
        this.patientMemoModel = patientMemoModel;
    }

    public List<RegisteredDiagnosisModel> getDisease() {
        return disease;
    }

    public void setDisease(List<RegisteredDiagnosisModel> disease) {
        this.disease = disease;
    }
}
