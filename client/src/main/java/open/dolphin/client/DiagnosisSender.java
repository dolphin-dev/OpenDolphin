package open.dolphin.client;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.*;
import open.dolphin.message.DiagnosisModuleItem;
import open.dolphin.message.DiseaseHelper;
import open.dolphin.message.MessageBuilder;
import open.dolphin.project.Project;
import open.dolphin.util.GUIDGenerator;
import org.apache.log4j.Level;

/**
 * Karte と Diagnosis の CLAIM を送る
 * KarteEditor の sendClaim を独立させた
 * DiagnosisDocument の CLAIM 送信部分もここにまとめた
 * @author pns
 */
public class DiagnosisSender implements IDiagnosisSender {

    private Chart context;

    // CLAIM 送信リスナ
    private ClaimMessageListener claimListener;

    // diagnosis では pvt が必要
    private PatientVisitModel pvt;

    private boolean DEBUG;
    
    private boolean send;

    public DiagnosisSender() {
        DEBUG = (ClientContext.getBootLogger().getLevel() == Level.DEBUG);
    }

    @Override
    public Chart getContext() {
        return context;
    }

    @Override
    public void setContext(Chart context) {
        this.context = context;
    }

    @Override
    public void prepare(List<RegisteredDiagnosisModel> diagnoses) {
//        if (diagnoses==null || diagnoses.isEmpty()) {
//            claimListener = null;
//            pvt = null;
//            return;
//        }
        
        // 2012-07 claimConnectionIsClientを追加
        send = (Project.claimSenderIsClient() && 
                diagnoses!=null &&
                (!diagnoses.isEmpty()));
        
        if (send) {
            claimListener  = context.getCLAIMListener();
            pvt = context.getPatientVisit();
        }
        
        send = send && (pvt!=null);
        send = send && (claimListener!=null);
    }

    /**
     * 診断名の CLAIM 送信
     * @param rd
     */
    @Override
    public void send(List<RegisteredDiagnosisModel> diagnoses) {

        // 2012-07 claimConnectionIsClientを追加
        if (!send) {
            return;
        }

        // DocInfo & RD をカプセル化したアイテムを生成する
        ArrayList<DiagnosisModuleItem> moduleItems = new ArrayList<DiagnosisModuleItem>();

        for (RegisteredDiagnosisModel rd : diagnoses) {
            
            DocInfoModel docInfo = new DocInfoModel();
            
            docInfo.setDocId(GUIDGenerator.generate(docInfo));
            docInfo.setTitle(IInfoModel.DEFAULT_DIAGNOSIS_TITLE);
            docInfo.setPurpose(IInfoModel.PURPOSE_RECORD);
            docInfo.setFirstConfirmDate(ModelUtils.getDateTimeAsObject(rd.getConfirmDate()));
            docInfo.setConfirmDate(ModelUtils.getDateTimeAsObject(rd.getFirstConfirmDate()));

            DiagnosisModuleItem mItem = new DiagnosisModuleItem();
            mItem.setDocInfo(docInfo);
            mItem.setRegisteredDiagnosisModule(rd);
            moduleItems.add(mItem);
        }

        // ヘルパー用の値を生成する
        String confirmDate = diagnoses.get(0).getConfirmDate();
        PatientLiteModel patient = diagnoses.get(0).getPatientLiteModel();

        // ヘルパークラスを生成する
        DiseaseHelper dhl = new DiseaseHelper();
        dhl.setPatientId(patient.getPatientId());
        dhl.setConfirmDate(confirmDate);
        dhl.setDiagnosisModuleItems(moduleItems);
        dhl.setGroupId(GUIDGenerator.generate(dhl));

        // DG ------------------------------------
        //dhl.setDepartment(pvt.getDepartmentCode());
        //dhl.setDepartmentDesc(pvt.getDepartment());
        //dhl.setCreatorName(pvt.getAssignedDoctorName());
        //dhl.setCreatorId(pvt.getAssignedDoctorId());
        //dhl.setCreatorLicense(Project.getUserModel().getLicenseModel().getLicense());
        //dhl.setFacilityName(Project.getUserModel().getFacilityModel().getFacilityName());
        //dhl.setJmariCode(pvt.getJmariCode());
        dhl.setDepartment(pvt.getDeptCode());     // 診療科コード
        dhl.setDepartmentDesc(pvt.getDeptName()); // 診療科名
        dhl.setCreatorName(pvt.getDoctorName());  // 担当医名
        dhl.setCreatorId(pvt.getDoctorId());      // 担当医コード
        dhl.setJmariCode(pvt.getJmariNumber());   // JMARI code
        dhl.setCreatorLicense(Project.getUserModel().getLicenseModel().getLicense());
        dhl.setFacilityName(Project.getUserModel().getFacilityModel().getFacilityName());
        //------------------------------------ DG

        MessageBuilder mb = new MessageBuilder();
        String claimMessage = mb.build(dhl);
        ClaimMessageEvent event = new ClaimMessageEvent(this);
        event.setPatientId(patient.getPatientId());
        event.setPatientName(patient.getFullName());
        event.setPatientSex(patient.getGender());
        event.setTitle(IInfoModel.DEFAULT_DIAGNOSIS_TITLE);
        event.setClaimInstance(claimMessage);
        event.setConfirmDate(confirmDate);

        // debug 出力を行う
        if (ClientContext.getClaimLogger() != null) {
            ClientContext.getClaimLogger().debug(event.getClaimInsutance());
        }

        if (claimListener != null) {
            claimListener.claimMessageEvent(event);
        }
    }

    private void debug(String msg) {
        if (DEBUG) {
            ClientContext.getBootLogger().debug(msg);
        }
    }
}
