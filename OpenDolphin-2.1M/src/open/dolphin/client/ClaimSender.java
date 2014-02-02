package open.dolphin.client;

import java.util.Collection;
import open.dolphin.infomodel.ClaimBundle;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.message.ClaimHelper;
import open.dolphin.message.MessageBuilder;
import open.dolphin.project.Project;
import open.dolphin.util.ZenkakuUtils;
import org.apache.log4j.Level;

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

    // DG UUID の変わりに保険情報モジュールを送信する
    private PVTHealthInsuranceModel insuranceToApply;

    private boolean DEBUG;

    public ClaimSender() {
        DEBUG = (ClientContext.getBootLogger().getLevel()==Level.DEBUG);
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
        if (data==null || (!data.getDocInfoModel().isSendClaim())) {
            return;
        }
        insuranceToApply = context.getHealthInsuranceToApply(data.getDocInfoModel().getHealthInsuranceGUID());
        claimListener  = context.getCLAIMListener();
    }

    /**
     * DocumentModel の CLAIM 送信を行う。
     */
    @Override
    public void send(DocumentModel sendModel) {

        if (sendModel==null ||
            sendModel.getDocInfoModel().isSendClaim()==false ||
            insuranceToApply==null ||
            claimListener==null) {
            return;
        }

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
            jamriCode = Project.getString(Project.JMARI_CODE);
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
        if (DEBUG) {
            debug(helper.getHealthInsuranceGUID());
            debug(helper.getHealthInsuranceClassCode());
            debug(helper.getHealthInsuranceDesc());
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
        cvt.setPatientId(context.getPatient().getPatientId());
        cvt.setPatientName(context.getPatient().getFullName());
        cvt.setPatientSex(context.getPatient().getGender());
        cvt.setTitle(sendModel.getDocInfoModel().getTitle());
        //---------------------------------------------- DG
        cvt.setConfirmDate(confirmedStr);

        // debug 出力を行う
        if (ClientContext.getClaimLogger() != null) {
            ClientContext.getClaimLogger().debug(cvt.getClaimInsutance());
        }

        claimListener.claimMessageEvent(cvt);
    }

    private void debug(String msg) {
        if (DEBUG) {
            ClientContext.getBootLogger().debug(msg);
        }
    }
}
