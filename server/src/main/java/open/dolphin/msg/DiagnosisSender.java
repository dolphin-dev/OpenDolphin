package open.dolphin.msg;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import open.dolphin.infomodel.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Claim 送信クラス。
 * オリジナルは元町皮ふ科
 * 2012-05
 * 在宅医療をサポートするため、CLAIM 送信を JMS+MDBへ移行
 * そのため DocInfo から CLAIM 送信に必要な全ての情報を取得する
 * @author kazushi Minagawa.
 */
public class DiagnosisSender {
    
    private static final int EOT = 0x04;
    private static final int ACK = 0x06;
    private static final int NAK = 0x15;
    private static final String ACK_STR = "ACK: ";
    private static final String NAK_STR = "NAK: ";
    private static final String OBJECT_NAME = "diseaseHelper";
    private static final String TEMPLATE_NAME = "diseaseHelper.vm";
    private static final String TEMPLATE_ENC = "SHIFT_JIS";
    private static final String DORCA_UPDATED = "DORCA_UPDATED";
    
    private String host;
    private int port;
    private String enc;
    
    private boolean DEBUG;

    public DiagnosisSender() {
    }
    
    public DiagnosisSender(String host, int port, String enc) {
        this();
        this.host = host;
        this.port = port;
        this.enc = enc;
//minagawa^ CLAIM Log        
        DEBUG = Logger.getLogger("dolphin.claim").getLevel().equals(java.util.logging.Level.FINE);
//minagawa$        
    }

    /**
     * CLAIM送信を行う。
     * @param sendModel 送信するDocuentModel
     * @throws Exception 
     */
    public void send(DiagnosisSendWrapper wrapper) throws Exception {
        
        // 新規病名
        List<RegisteredDiagnosisModel> addedDiagnosis = wrapper.getAddedDiagnosis();
        
        // 更新病名
        List<RegisteredDiagnosisModel> updatedDiagnosis = wrapper.getUpdatedDiagnosis();
        
        // 実際にCLAIM送信する病名
        List<RegisteredDiagnosisModel> actualList = new ArrayList<RegisteredDiagnosisModel>();
        
        // 新規病名を送信する
        if (addedDiagnosis!=null && addedDiagnosis.size()>0) {
            
            for (RegisteredDiagnosisModel rdm : addedDiagnosis) {
                if (isDorcaUpdatedDisease(rdm) || isPureDisease(rdm)) {
                    actualList.add(rdm);
                }
            }
            
            if (!actualList.isEmpty()) {
                if (DEBUG) {
                    debug("-------- Send Diagnosis List ----------------");
                    for (RegisteredDiagnosisModel r : actualList) {
                        debug(r.getDiagnosis());
                    }
                }
            }
        }

        // 更新された病名を CLAIM 送信する
        // detuched object のみ
        if (updatedDiagnosis!=null && updatedDiagnosis.size()>0) {
            if (DEBUG) {
                debug("-------- Send Diagnosis List ----------------");
                for (RegisteredDiagnosisModel r : updatedDiagnosis) {
                    debug(r.getDiagnosis());
                }
            }
            actualList.addAll(updatedDiagnosis);
        }
        
        // DocInfo & RD をカプセル化したアイテムを生成する
        ArrayList<DiagnosisModuleItem> moduleItems = new ArrayList<DiagnosisModuleItem>();

        for (RegisteredDiagnosisModel rd : actualList) {
            
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
        String confirmDate = wrapper.getConfirmDate();

        // ヘルパークラスを生成する
        DiseaseHelper dhl = new DiseaseHelper();
        dhl.setPatientId(wrapper.getPatientId());           // 患者ID
        dhl.setConfirmDate(confirmDate);                    // 確定日
        dhl.setDiagnosisModuleItems(moduleItems);           // RD+DocInfo
        dhl.setGroupId(GUIDGenerator.generate(dhl));        // GroupId
        
        dhl.setDepartment(wrapper.getDepartment());         // 診療科コード
        dhl.setDepartmentDesc(wrapper.getDepartmentDesc()); // 診療科名
        dhl.setCreatorName(wrapper.getCreatorName());       // 担当医名
        dhl.setCreatorId(wrapper.getCreatorLicense());      // 担当医コード
        dhl.setJmariCode(wrapper.getJamariCode());          // JMARI code
        dhl.setCreatorLicense(wrapper.getCreatorLicense()); // 医療資格
        dhl.setFacilityName(wrapper.getFacilityName());     // 施設名 
        
        if (DEBUG) {
            debug("患者ID=" + dhl.getPatientId());
            debug("確定日=" + dhl.getConfirmDate());
            debug("GroupId=" + dhl.getGroupId());
            debug("診療科コード=" + dhl.getDepartment());
            debug("診療科名=" + dhl.getDepartmentDesc());
            debug("担当医名=" + dhl.getCreatorName());
            debug("担当医コード=" + dhl.getCreatorId());
            debug("JMARI code=" + dhl.getJmariCode());
            debug("医療資格=" + dhl.getCreatorLicense());
            debug("施設名=" + dhl.getFacilityName());
        }
        
        // ログのために基本情報を生成する
        StringBuilder sb = new StringBuilder();
        sb.append(confirmDate).append(" ");
        sb.append(wrapper.getPatientId()).append(" ");
        sb.append(wrapper.getPatientName()).append(" ");
        sb.append(wrapper.getPatientGender());
        String baseInfo = sb.toString();
        
        //--------------------------------------------------------
        // CLIAM message を生成する
        //--------------------------------------------------------
        VelocityContext context = VelocityHelper.getContext();
        context.put(OBJECT_NAME, dhl);
        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);
        Velocity.mergeTemplate(TEMPLATE_NAME, TEMPLATE_ENC, context, bw);
        bw.flush();
        bw.close();
        String claimMessage = sw.toString();
//minagawa^ CLAIM Log    
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
    
//minagawa^ CLAIM Log    
    private void log(String msg) {
        Logger.getLogger("dolphin.claim").info(msg);
    }
    
    private void debug(String msg) {
        Logger.getLogger("dolphin.claim").fine(msg);
    }
    
    private void warning(String msg) {
        Logger.getLogger("dolphin.claim").warning(msg);
    }
//minagawa$    

    private boolean isDorcaUpdatedDisease(RegisteredDiagnosisModel test) {
        return (test!=null && test.getStatus()!=null && test.getStatus().equals(DORCA_UPDATED));
    }

    private boolean isPureDisease(RegisteredDiagnosisModel test) {
        return (test!=null && test.getStatus()!=null && test.getStatus().equals(IInfoModel.STATUS_FINAL));
    }
}
