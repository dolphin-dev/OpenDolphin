package open.dolphin.message;

import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;

/**
 *
 * @author Kazushi Minagawa.
 */
public class DiagnosisModuleItem {
    
    // DocInfo
    private DocInfoModel docInfo;
    
    // RegisteredDiagnosisModel
    private RegisteredDiagnosisModel registeredDiagnosisModule;
    
    /** Creates a new instance of DiagnosisModuleItem */
    public DiagnosisModuleItem() {
    }

    public DocInfoModel getDocInfo() {
        return docInfo;
    }

    public void setDocInfo(DocInfoModel docInfo) {
        this.docInfo = docInfo;
    }

    public RegisteredDiagnosisModel getRegisteredDiagnosisModule() {
        return registeredDiagnosisModule;
    }

    public void setRegisteredDiagnosisModule(RegisteredDiagnosisModel registeredDiagnosisModule) {
        this.registeredDiagnosisModule = registeredDiagnosisModule;
    }
}
