package open.dolphin.client;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import open.dolphin.infomodel.*;
import open.dolphin.message.ClaimHelper;
import open.dolphin.message.MessageBuilder;
import open.dolphin.project.Project;
import open.dolphin.util.ZenkakuUtils;

/**
 * Karte と Diagnosis の CLAIM を送る
 * KarteEditor の sendClaim を独立させた
 * DiagnosisDocument の CLAIM 送信部分もここにまとめた
 * @author pns
 */
public class ClaimSender implements IKarteSender {

    // Context
    private Chart context;

    // CLAIM 送信リスナ
    private ClaimMessageListener claimListener;

    //minagawa^ UUIDの変わりに保険情報モジュールを送信する
    private PVTHealthInsuranceModel insuranceToApply;

    // Logger
    private static final boolean DEBUG = false;
    private static final java.util.logging.Logger logger;
    static {
        logger = java.util.logging.Logger.getLogger(ClaimSender.class.getName());
        logger.setLevel(DEBUG ? Level.FINE : Level.INFO);
    }
    
    private boolean send;

    public ClaimSender() {
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
    public void prepare(DocumentModel data) {
 //minagawa^ 2012-07 claimConnectionを追加
        send = (Project.claimSenderIsClient() && data!=null && data.getDocInfoModel().isSendClaim());
        if (send) {
            insuranceToApply = context.getHealthInsuranceToApply(data.getDocInfoModel().getHealthInsuranceGUID());
            claimListener  = context.getCLAIMListener();
        }
        send = send && (insuranceToApply!=null);
        send = send && (claimListener!=null);
    }

    /**
     * DocumentModel の CLAIM 送信を行う。
     * @param sendModel
     */
    @Override
    public void send(DocumentModel sendModel) {

//minagawa^ 2012-07 claimConnectionを追加
        if (!send) {
            return;
        }

        // ヘルパークラスを生成しVelocityが使用するためのパラメータを設定する
        ClaimHelper helper = new ClaimHelper();

//minagawa^
        //DocInfoModel docInfo = sendModel.getDocInfo();
        DocInfoModel docInfo = sendModel.getDocInfoModel();
        Collection<ModuleModel> modules = sendModel.getModules();
//minagawa$

 //minagawa^ CLAIM送信 日をまたいだが、前日で送る必要がある場合等(予定カルテ対応)
        //String confirmedStr = ModelUtils.getDateTimeAsString(docInfo.getFirstConfirmDate());
        Date sendDate = docInfo.getClaimDate()!=null ? docInfo.getClaimDate() : docInfo.getFirstConfirmDate();
        String confirmedStr = ModelUtils.getDateTimeAsString(sendDate);
        helper.setConfirmDate(confirmedStr);
//minagawa$
        //--------------------------------------------- DG
        helper.setConfirmDate(confirmedStr);
        logger.finer(confirmedStr);

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
            jamriCode = Project.getString(Project.JMARI_CODE);
        }
        
        logger.finer(deptName);
        logger.finer(deptCode);
        logger.finer(doctorName);
        logger.finer(doctorId);
        logger.finer(jamriCode);
        
        helper.setCreatorDeptDesc(deptName);
        helper.setCreatorDept(deptCode);
        helper.setCreatorName(doctorName);
        helper.setCreatorId(doctorId);
        helper.setCreatorLicense(Project.getUserModel().getLicenseModel().getLicense());
        helper.setJmariCode(jamriCode);
        helper.setFacilityName(Project.getUserModel().getFacilityModel().getFacilityName());
        
        //DG -------------------------------------------
        //helper.setPatientId(sendModel.getKarte().getPatient().getPatientId());
        //helper.setPatientId(sendModel.getKarteBean().getPatientModel().getPatientId());
        helper.setPatientId(context.getPatient().getPatientId());
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
        logger.finer(helper.getHealthInsuranceGUID());
        logger.finer(helper.getHealthInsuranceClassCode());
        logger.finer(helper.getHealthInsuranceDesc());

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
        cvt.setPatientId(context.getPatient().getPatientId());
        cvt.setPatientName(context.getPatient().getFullName());
        cvt.setPatientSex(context.getPatient().getGender());
        cvt.setTitle(sendModel.getDocInfoModel().getTitle());
        //---------------------------------------------- DG
        cvt.setConfirmDate(confirmedStr);

        // debug 出力を行う
        logger.finer(cvt.getClaimInsutance());

        claimListener.claimMessageEvent(cvt);
    }
}
