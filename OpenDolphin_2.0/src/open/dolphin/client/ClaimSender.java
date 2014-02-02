package open.dolphin.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TooManyListenersException;
import open.dolphin.infomodel.ClaimBundle;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientLiteModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.message.ClaimHelper;
import open.dolphin.message.DiagnosisModuleItem;
import open.dolphin.message.DiseaseHelper;
import open.dolphin.message.MessageBuilder;
import open.dolphin.project.Project;
import open.dolphin.util.GUIDGenerator;
import open.dolphin.util.ZenkakuUtils;
import org.apache.log4j.Level;

/**
 * Karte と Diagnosis の CLAIM を送る
 * KarteEditor の sendClaim を独立させた
 * DiagnosisDocument の CLAIM 送信部分もここにまとめた
 * @author pns
 */
public class ClaimSender {

    // CLAIM 送信リスナ
    private ClaimMessageListener claimListener;

    // diagnosis では pvt が必要
    private PatientVisitModel pvt;

    // DG UUID の変わりに保険情報モジュールを送信する
    private PVTHealthInsuranceModel insuranceToApply;

    private boolean DEBUG;

    public ClaimSender() {
        DEBUG = ClientContext.getBootLogger().getLevel() == Level.DEBUG ? true : false;
    }

    // DG add/removeCLAIMListener は不便なのでコンストラクタの引数にする
    public ClaimSender(ClaimMessageListener listener) {
        this();
        claimListener = listener;
    }

    /**
     * Diagnosis では pvt をセットする必要がある
     * @param model
     */
    public void setPatientVisitModel(PatientVisitModel model) {
        pvt = model;
    }

    /**
     * DG 適用する保険をセットする
     */
    public void setInsuranceToApply(PVTHealthInsuranceModel insuranceToApply) {
        this.insuranceToApply = insuranceToApply;
    }

    /**
     * CLAIMリスナを追加する。
     * @param listener CLAIMリスナ
     * @throws TooManyListenersException
     */
    public void addCLAIMListener(ClaimMessageListener listener)
            throws TooManyListenersException {
        if (claimListener != null) {
            throw new TooManyListenersException();
        }
        claimListener = listener;
    }

    /**
     * CLAIM リスナを削除する。
     * @param listener 削除するCLAIMリスナ
     */
    public void removeCLAIMListener(ClaimMessageListener listener) {
        if (claimListener != null && claimListener == listener) {
            claimListener = null;
        }
    }

    /**
     * CLAIM リスナを返す
     * @return
     */
    public ClaimMessageListener getListener() {
        return claimListener;
    }

    private void sendEvent(ClaimMessageEvent event) {
        // debug 出力を行う
        if (ClientContext.getClaimLogger() != null) {
            ClientContext.getClaimLogger().debug(event.getClaimInsutance());
        }

        if (claimListener != null) {
            claimListener.claimMessageEvent(event);
        }
    }

    /**
     * DocumentModel の CLAIM 送信を行う。
     */
    public void send(DocumentModel sendModel) {

        // ヘルパークラスを生成しVelocityが使用するためのパラメータを設定する
        ClaimHelper helper = new ClaimHelper();

        //DG ------
        //DocInfoModel docInfo = sendModel.getDocInfo();
        DocInfoModel docInfo = sendModel.getDocInfoModel();
        Collection<ModuleModel> modules = sendModel.getModules();
        //--------DG

        //DG ------------------------------------------
        // 過去日で送信するために firstConfirmDate へ変更
        //String confirmedStr = ModelUtils.getDateTimeAsString(docInfo.getConfirmDate());
        String confirmedStr = ModelUtils.getDateTimeAsString(docInfo.getFirstConfirmDate());
        //--------------------------------------------- DG
        helper.setConfirmDate(confirmedStr);
        debug(confirmedStr);

        String deptName = docInfo.getDepartmentName();
        String deptCode = docInfo.getDepartmentCode();
        String doctorName = docInfo.getAssignedDoctorName();
        if (doctorName == null) {
            doctorName = Project.getUserModel().getCommonName();
        }
        String doctorId = docInfo.getAssignedDoctorId();
        if (doctorId == null) {
            doctorId = Project.getUserModel().getOrcaId()!=null
                    ? Project.getUserModel().getOrcaId()
                    : Project.getUserModel().getUserId();
        }
        String jamriCode = docInfo.getJMARICode();
        if (jamriCode == null) {
            jamriCode = Project.getJMARICode();
        }
        if (DEBUG) {
            debug(deptName);
            debug(deptCode);
            debug(doctorName);
            debug(doctorId);
            debug(jamriCode);
        }
        helper.setCreatorDeptDesc(deptName);
        helper.setCreatorDept(deptCode);
        helper.setCreatorName(doctorName);
        helper.setCreatorId(doctorId);
        helper.setCreatorLicense(Project.getUserModel().getLicenseModel().getLicense());
        helper.setJmariCode(jamriCode);
        helper.setFacilityName(Project.getUserModel().getFacilityModel().getFacilityName());
        
        //DG -------------------------------------------
        //helper.setPatientId(sendModel.getKarte().getPatient().getPatientId());
        helper.setPatientId(sendModel.getKarteBean().getPatientModel().getPatientId());
        //--------------------------------------------- DG
        helper.setGenerationPurpose(docInfo.getPurpose());
        helper.setDocId(docInfo.getDocId());

        helper.setHealthInsuranceGUID(docInfo.getHealthInsuranceGUID());
        helper.setHealthInsuranceClassCode(docInfo.getHealthInsurance());
        helper.setHealthInsuranceDesc(docInfo.getHealthInsuranceDesc());

        //DG -----------------------------------------------
        // 2010-11-10 UUIDの変わりに保険情報モジュールを送信する
        helper.setSelectedInsurance(insuranceToApply);
        //-------------------------------------------------- DG
        if (DEBUG) {
            debug(helper.getHealthInsuranceGUID());
            debug(helper.getHealthInsuranceClassCode());
            debug(helper.getHealthInsuranceDesc());
            //debug(helper.getSelectedInsurance().toString());
        }

        // 保存する KarteModel の全モジュールをチェックし
        // それが ClaimBundle ならヘルパーへ追加する
        for (ModuleModel module : modules) {
            IInfoModel m = module.getModel();
            if (m instanceof ClaimBundle) {
                //DG-----------------------------------
                ClaimBundle bundle = (ClaimBundle) m;
                ClaimItem[] items = bundle.getClaimItem();
                if (items!=null && items.length>0) {
                    for (ClaimItem cl : items) {
                        cl.setName(ZenkakuUtils.utf8Replace(cl.getName()));
                    }
                }
                //-------------------------------------DG
                helper.addClaimBundle(bundle);
            }
        }

        MessageBuilder mb = new MessageBuilder();
        String claimMessage = mb.build(helper);
        ClaimMessageEvent cvt = new ClaimMessageEvent(this);
        cvt.setClaimInstance(claimMessage);
        //DG ----------------------------------------------
        //cvt.setPatientId(sendModel.getKarte().getPatient().getPatientId());
        //cvt.setPatientName(sendModel.getKarte().getPatient().getFullName());
        //cvt.setPatientSex(sendModel.getKarte().getPatient().getGender());
        //cvt.setTitle(sendModel.getDocInfo().getTitle());
        cvt.setPatientId(sendModel.getKarteBean().getPatientModel().getPatientId());
        cvt.setPatientName(sendModel.getKarteBean().getPatientModel().getFullName());
        cvt.setPatientSex(sendModel.getKarteBean().getPatientModel().getGender());
        cvt.setTitle(sendModel.getDocInfoModel().getTitle());
        //---------------------------------------------- DG
        cvt.setConfirmDate(confirmedStr);

        sendEvent(cvt);
    }

    /**
     * 診断名の CLAIM 送信
     * @param rd
     */
    public void send(List<RegisteredDiagnosisModel> diagnoses) {

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

        sendEvent(event);
    }

    private void debug(String msg) {
        if (DEBUG) {
            ClientContext.getBootLogger().debug(msg);
        }
    }
}
