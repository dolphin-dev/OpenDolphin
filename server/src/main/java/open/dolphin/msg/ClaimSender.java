package open.dolphin.msg;

import java.beans.XMLDecoder;
import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;
import open.dolphin.infomodel.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Claim 送信クラス。
 * オリジナルアイデアは元町皮ふ科
 * 2012-05
 * 在宅医療をサポートするため、CLAIM 送信を JMS+MDBへ移行
 * そのため DocInfo から CLAIM 送信に必要な全ての情報を取得する
 * @author kazushi Minagawa.
 */
public class ClaimSender {
    
    private static final int EOT = 0x04;
    private static final int ACK = 0x06;
    private static final int NAK = 0x15;
    private static final String ACK_STR = "ACK: ";
    private static final String NAK_STR = "NAK: ";
    private static final String OBJECT_NAME = "claimHelper";
    private static final String TEMPLATE_NAME = "claimHelper.vm";
    private static final String TEMPLATE_ENC = "SHIFT_JIS";
    
    private String host;
    private int port;
    private String enc;
    
    private boolean DEBUG;

    public ClaimSender() {
    }
    
    public ClaimSender(String host, int port, String enc) {
        this();
        this.host = host;
        this.port = port;
        this.enc = enc;
//minagawa^ CLAIM log        
        DEBUG = Logger.getLogger("dolphin.claim").getLevel().equals(java.util.logging.Level.FINE);
//minagawa$        
    }

    /**
     * CLAIM送信を行う。
     * @param sendModel 送信するDocuentModel
     * @throws Exception 
     */
    public void send(DocumentModel sendModel) throws Exception {

        // ヘルパークラスを生成しVelocityが使用するためのパラメータを設定する
        ClaimHelper helper = new ClaimHelper();

        // DocInfo
        DocInfoModel docInfo = sendModel.getDocInfoModel();
        String purpose = docInfo.getPurpose();
        String docId = docInfo.getDocId();

        //------------------------------------------
        // (予定カルテ対応)
        //// 過去日で送信するために firstConfirmDate へ変更
        //String confirmedStr = ModelUtils.getDateTimeAsString(docInfo.getFirstConfirmDate());
 //minagawa^ CLAIM送信 日をまたいだが、前日で送る必要がある場合等
        Date sendDate = docInfo.getClaimDate()!=null ? docInfo.getClaimDate() : docInfo.getFirstConfirmDate();
        String confirmedStr = ModelUtils.getDateTimeAsString(sendDate);
//minagawa$
        helper.setConfirmDate(confirmedStr);
        //------------------------------------------

        // JMS+MDB に移行のため DocInfo から取得する
        String deptName = docInfo.getDepartmentName();          // 診療科名
        String deptCode = docInfo.getDepartmentCode();          // 診療科コード
        String doctorName = docInfo.getAssignedDoctorName();    // 担当医名
        String doctorId = docInfo.getAssignedDoctorId();        // 担当医コード
        String license = docInfo.getCreaterLisence();           // 医療資格
        String facilityName = docInfo.getFacilityName();        // 施設名
        String jamriCode = docInfo.getJMARICode();              // JMARI
        String patientId = docInfo.getPatientId();              // 患者ID
        String patientName = docInfo.getPatientName();          // 患者氏名
        String patientGender = docInfo.getPatientGender();      // 患者性別
        
        // 保健関連
        String insuranceGUID = docInfo.getHealthInsuranceGUID();
        String insurance = docInfo.getHealthInsurance();
        String insuranceDesc = docInfo.getHealthInsuranceDesc();
        PVTHealthInsuranceModel pvtIns = docInfo.getPVTHealthInsuranceModel();
        
        if (DEBUG) {
            debug("patientId=" + patientId);
            debug("patientName=" + patientName);
            debug("patientGender=" + patientGender);
            debug("confirmedStr=" + confirmedStr);
            debug("facilityName=" + facilityName);
            debug("jamriCode=" + jamriCode);
            debug("deptName=" + deptName);
            debug("deptCode=" + deptCode);
            debug("doctorName=" + doctorName);
            debug("doctorId=" + doctorId);
            debug("license=" + license);
            debug("purpose=" + purpose);
            debug("docId=" + docId);
            debug("insuranceGUID=" + insuranceGUID);
            debug("insurance=" + insurance);
            debug("insuranceDesc=" + insuranceDesc);
            String pvtInsStr = pvtIns!=null ? pvtIns.toString() : "NULL";
            debug("pvtIns=" + pvtInsStr);
        }
        helper.setCreatorDeptDesc(deptName);
        helper.setCreatorDept(deptCode);
        helper.setCreatorName(doctorName);
        helper.setCreatorId(doctorId);
        helper.setCreatorLicense(license);
        helper.setJmariCode(jamriCode);
        helper.setFacilityName(facilityName);
     
        // 患者ID
        helper.setPatientId(docInfo.getPatientId());
        
        // 健康保険関連
        helper.setGenerationPurpose(purpose);
        helper.setDocId(docId);
        helper.setHealthInsuranceGUID(insuranceGUID);
        helper.setHealthInsuranceClassCode(insurance);  // use?
        helper.setHealthInsuranceDesc(insuranceDesc);   // use?

        //-----------------------------------------------------------
        // 2010-11-10 UUIDの変わりに保険情報モジュールを送信する
        helper.setSelectedInsurance(pvtIns);
        
        //-----------------------------------------------------------

        //---------------------------------------------------
        // 保存する KarteModel の全モジュールをチェックし
        // それが ClaimBundle ならヘルパーへ追加する
        //---------------------------------------------------
        Collection<ModuleModel> modules = sendModel.getModules();
        
        for (ModuleModel module : modules) {
            
            if (module.getModuleInfoBean().getEntity().contains("progressCourse")) {
                continue;
            }
            
            IInfoModel m = (IInfoModel)xmlDecode(module.getBeanBytes());
            
            if (m instanceof BundleDolphin) {
                
                //DG-----------------------------------
                BundleDolphin bundle = (BundleDolphin) m;
                ClaimItem[] items = bundle.getClaimItem();
                
                if (items!=null && items.length>0) {
                    for (ClaimItem cl : items) {
                        //System.err.println(cl.getName());
                        cl.setName(ZenkakuUtils.utf8Replace(cl.getName()));
                    }
                }
                //-------------------------------------DG
                helper.addClaimBundle(bundle);
            }
        }
        
        // ログのために基本情報を生成する
        StringBuilder sb = new StringBuilder();
        sb.append(confirmedStr).append(" ").append(patientId).append(" ").append(patientName).append(" ").append(patientGender);
        String baseInfo = sb.toString();
        
        //--------------------------------------------------------
        // CLIAM message を生成する
        //--------------------------------------------------------
        VelocityContext context = VelocityHelper.getContext();
        context.put(OBJECT_NAME, helper);
        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);
        Velocity.mergeTemplate(TEMPLATE_NAME, TEMPLATE_ENC, context, bw);
        bw.flush();
        bw.close();
        String claimMessage = sw.toString();
//minagawa^ CLAIM Log INFOで          
        log(claimMessage);
//        if (DEBUG) {
//            debug(claimMessage);
//        }
//minagawa$        
        //--------------------------------------------------------
        
        // ORCAへ接続する
        Socket socket = new Socket(host, port);
        OutputStream out = socket.getOutputStream();
        DataOutputStream dout = new DataOutputStream(out);
        BufferedOutputStream writer = new BufferedOutputStream(dout);

        InputStream in = socket.getInputStream();
        DataInputStream din = new DataInputStream(in);
        BufferedInputStream reader = new BufferedInputStream(din);

        // Writes UTF8 data
        writer.write(claimMessage.getBytes(enc));
        writer.write(EOT);
        writer.flush();
        
        // Reads result
        int c = reader.read();
        if (c == ACK) {
            sb = new StringBuilder();
            sb.append(ACK_STR).append(baseInfo);
            log(sb.toString());
        } else if (c == NAK) {
            sb = new StringBuilder();
            sb.append(NAK_STR).append(baseInfo);
            log(sb.toString());
        }

        writer.close();
        reader.close();
        socket.close();
    }
    
    private void log(String msg) {
//minagawa^ CLAIM Log        
        Logger.getLogger("dolphin.claim").info(msg);
    }
    
    private void debug(String msg) {
        Logger.getLogger("dolphin.claim").fine(msg);
    }
    
    private void warning(String msg) {
        Logger.getLogger("dolphin.claim").warning(msg);
    }
//minagawa$    
    
    private Object xmlDecode(byte[] bytes)  {

        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                new ByteArrayInputStream(bytes)));

        return d.readObject();
    }
}
