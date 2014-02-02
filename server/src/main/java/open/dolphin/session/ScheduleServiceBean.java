package open.dolphin.session;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.AttachmentModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.UserModel;

/**
 * (予定カルテ対応)
 * @author kazushi Minagawa.
 */
@Named
@Stateless
public class ScheduleServiceBean {
    
    private static final String QUERY_PVT_BY_FID_DATE
            = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date order by p.pvtDate";
    
    private static final String QUERY_PVT_BY_FID_DID_DATE
            = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date and (doctorId=:did or doctorId=:unassigned) order by p.pvtDate";
    
    private static final String QUERY_INSURANCE_BY_PATIENT_ID 
            = "from HealthInsuranceModel h where h.patient.id=:id";
    
    private static final String QUERY_KARTE 
            = "from KarteBean k where k.patient.id=:patientPk";

    private static final String QUERY_LASTDOC_DATE_BY_KARTEID_FINAL
//minagawa^ LSC Test             
            //= "select max(m.started) from DocumentModel m where m.karte.id=:karteId and (m.status='F' or m.status='T')";
    = "select max(m.started) from d_document m where m.karte_id=:karteId and m.docType=:docType and (m.status = 'F' or m.status = 'T')";
//minagawa$    
    
    private static final String QUERY_DOCUMENT_BY_KARTEID_STARTDATE 
            = "from DocumentModel d where d.karte.id=:karteId and d.started=:started and (d.status='F' or d.status='T')";
    
    private static final String QUERY_DOCUMENT_BY_LINK_ID 
            = "from DocumentModel d where d.linkId=:id";
    
    private static final String QUERY_MODULE_BY_DOC_ID 
            = "from ModuleModel m where m.document.id=:id";
    
    private static final String QUERY_SCHEMA_BY_DOC_ID 
            = "from SchemaModel i where i.document.id=:id";
    
    private static final String QUERY_ATTACHMENT_BY_DOC_ID 
            = "from AttachmentModel a where a.document.id=:id";
    
    @PersistenceContext
    private EntityManager em;
    
    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;
    
    @Resource(mappedName = "java:/queue/dolphin")
    private javax.jms.Queue queue;
    
    public List<PatientVisitModel> getPvt(String fid, String did, String unassigned, String date) {
        
        List<PatientVisitModel> result;
        
        if (did==null && unassigned==null) {
            result = (List<PatientVisitModel>) em.createQuery(QUERY_PVT_BY_FID_DATE)
                                  .setParameter("fid", fid)
                                  .setParameter("date", date+"%")
                                  .getResultList();
        } else {
            result = (List<PatientVisitModel>) em.createQuery(QUERY_PVT_BY_FID_DID_DATE)
                                  .setParameter("fid", fid)
                                  .setParameter("did", did)
                                  .setParameter("unassigned", unassigned)
                                  .setParameter("date", date+"%")
                                  .getResultList();
        }

        int len = result.size();

        if (len == 0) {
            return result;
        }
        
        // Dateへ変換
        Date startDate = dateFromString(date);

        // 来院情報と患者は ManyToOne の関係である
        for (int i = 0; i < len; i++) {
            
            PatientVisitModel pvt = result.get(i);
            PatientModel patient = pvt.getPatientModel();

            // 患者の健康保険を取得する
            List<HealthInsuranceModel> insurances = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
            .setParameter("id", patient.getId()).getResultList();
            patient.setHealthInsurances(insurances);
            
            List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                                  .setParameter("patientPk", patient.getId())
                                  .getResultList();
            KarteBean karte = kartes.get(0);
            
            // この日のカルテが存在するか
            List<DocumentModel> list = (List<DocumentModel>)em.createQuery(QUERY_DOCUMENT_BY_KARTEID_STARTDATE)
                                                 .setParameter("karteId", karte.getId())
                                                 .setParameter("started", startDate)
                                                 .getResultList();
            if (list!=null && !list.isEmpty()) {
                pvt.setLastDocDate(startDate);
            }
        }

        return result;
    }
    
    public int makeScheduleAndSend(long pvtPK, long userPK, Date startDate, boolean send) {
        
        try {
            // 受付情報を取得する
            PatientVisitModel pvt = (PatientVisitModel)em.find(PatientVisitModel.class, pvtPK);
            PatientModel patient = pvt.getPatientModel();

            // 患者の健康保険を取得する
            List<HealthInsuranceModel> insurances = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
            .setParameter("id", patient.getId()).getResultList();
            patient.setHealthInsurances(insurances);
            
            // 受け付けた保険をデコードする
            PVTHealthInsuranceModel pvtHealthInsurance=null;
            for (HealthInsuranceModel m : insurances) {
                XMLDecoder d = new XMLDecoder(
                    new BufferedInputStream(
                    new ByteArrayInputStream(m.getBeanBytes())));
                pvtHealthInsurance = (PVTHealthInsuranceModel)d.readObject();
                break;
            }
            
            // Creator
            UserModel user = em.find(UserModel.class, userPK);
            
            // 患者のカルテを取得する
            List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                                  .setParameter("patientPk", patient.getId())
                                  .getResultList();
            KarteBean karte = kartes.get(0);
            
            // startDateに相当する日の文書があるか startDate(00:00:00)
            List<DocumentModel> list = (List<DocumentModel>)em.createQuery(QUERY_DOCUMENT_BY_KARTEID_STARTDATE)
                                                 .setParameter("karteId", karte.getId())
                                                 .setParameter("started", startDate)
                                                 .getResultList();
            if (!list.isEmpty()) {
                // 当日のカルテがある場合は何もしない
                Logger.getLogger("open.dolphin").log(Level.INFO, "{0} has karte at {1}", new Object[]{patient.getFullName(), startDate});
                return 0;
            }
            
            // 当日のカルテがない場合
            DocumentModel schedule;
            
            try {
                // Documentの最終日を得る
                Date lastDocDate = (Date)
//minagawa^ LSC Test                        
                        em.createNativeQuery(QUERY_LASTDOC_DATE_BY_KARTEID_FINAL)
                        .setParameter("karteId", karte.getId())
                        .setParameter("docType", "karte")
                        .getSingleResult();
//minagawa$                
                
                // そのDocumentを得る ToDo
                List<DocumentModel> list2 = (List<DocumentModel>)em.createQuery(QUERY_DOCUMENT_BY_KARTEID_STARTDATE)
                                                     .setParameter("karteId", karte.getId())
                                                     .setParameter("started", lastDocDate)
                                                     .getResultList();
                DocumentModel latest = list2.get(0);

                // 予定文書（カルテ）
                schedule = latest.rpClone();
                Logger.getLogger("open.dolphin").info("did rpClone");
                
            } catch (Exception e) {
                Logger.getLogger("open.dolphin").info("lastDocDate dose not exist");
                schedule = new DocumentModel();
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                schedule.getDocInfoModel().setDocId(uuid);
                schedule.getDocInfoModel().setDocType(IInfoModel.DOCTYPE_KARTE);
                schedule.getDocInfoModel().setTitle("予定");
                schedule.getDocInfoModel().setPurpose(IInfoModel.PURPOSE_RECORD);
                schedule.getDocInfoModel().setHasRp(false);
                schedule.getDocInfoModel().setVersionNumber("1.0");
                Logger.getLogger("open.dolphin").info("did create new karte");
            }
            
            // Confirmed
            Date now = new Date();
            
            // DocInfoを設定する
            StringBuilder sb = new StringBuilder();
            sb.append(pvt.getDeptName()).append(",");           // 診療科名
            sb.append(pvt.getDeptCode()).append(",");           // 診療科コード : 受けと不一致、受信？
            sb.append(user.getCommonName()).append(",");        // 担当医名
            if (pvt.getDoctorId()!=null) {
                sb.append(pvt.getDoctorId()).append(",");       // 担当医コード: 受付でIDがある場合
            } else if (user.getOrcaId()!=null) {
                sb.append(user.getOrcaId()).append(",");        // 担当医コード: ORCA ID がある場合
            } else {
                sb.append(user.getUserId()).append(",");        // 担当医コード: ログインユーザーID
            }
            sb.append(pvt.getJmariNumber());                    // JMARI
            schedule.getDocInfoModel().setDepartmentDesc(sb.toString());    // 上記をカンマ区切りで docInfo.departmentDesc へ設定
            schedule.getDocInfoModel().setDepartment(pvt.getDeptCode());    // 診療科コード 01 内科等

            // 施設名、ライセンス、患者情報
            schedule.getDocInfoModel().setFacilityName(user.getFacilityModel().getFacilityName());
            schedule.getDocInfoModel().setCreaterLisence(user.getLicenseModel().getLicense());
            schedule.getDocInfoModel().setPatientId(patient.getPatientId());
            schedule.getDocInfoModel().setPatientName(patient.getFullName());
            schedule.getDocInfoModel().setPatientGender(patient.getGenderDesc());

            // 健康保険を設定する-新規カルテダイアログで選択された保険をセットしている
            schedule.getDocInfoModel().setHealthInsurance(pvtHealthInsurance.getInsuranceClassCode());      // classCode
            schedule.getDocInfoModel().setHealthInsuranceDesc(pvtHealthInsurance.toString());               // 説明
            schedule.getDocInfoModel().setHealthInsuranceGUID(pvtHealthInsurance.getGUID());                // UUID
            schedule.getDocInfoModel().setPVTHealthInsuranceModel(pvtHealthInsurance);                      // 適用保険
            
            // 基本属性
            schedule.setStarted(startDate);
            schedule.setConfirmed(now);
            schedule.setRecorded(now);
            schedule.setKarteBean(karte);
            schedule.setUserModel(user);
            schedule.setStatus("T");
            
            // 関係構築
            List<ModuleModel> modules = schedule.getModules();
            if (modules!=null) {
                for (ModuleModel module : modules) {
                    module.setStarted(schedule.getStarted());
                    module.setConfirmed(schedule.getConfirmed());
                    module.setRecorded(schedule.getRecorded());
                    module.setKarteBean(schedule.getKarteBean());
                    module.setUserModel(user);
                    module.setStatus(schedule.getStatus());
                    module.setDocumentModel(schedule);
                }
            }
            
            // CLAIM送信
            send = send && (modules!=null && !modules.isEmpty());
            schedule.getDocInfoModel().setSendClaim(send);
            
            // 永続化
            em.persist(schedule);
            
            // CLAIM送信
            if (send) {
                // DocInfoへ開始日等を設定する
                schedule.toDetuch();
                Connection conn = null;
                try {
                    conn = connectionFactory.createConnection();
                    Session session = conn.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
                    ObjectMessage msg = session.createObjectMessage(schedule);
                    MessageProducer producer = session.createProducer(queue);
                    producer.send(msg);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                } finally {
                    if(conn != null) {
                        try {
                            conn.close();
                        } catch (JMSException e) { 
                        }
                    }
                }
            }
            
            return 1;
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
        return 0;
    }
    
    public int removePvt(long pvtPK, long ptPK, Date startDate) {
        // 受付咲くジョン
        PatientVisitModel exist = (PatientVisitModel)em.find(PatientVisitModel.class, new Long(pvtPK));
        em.remove(exist);
        
        // 患者のカルテを取得する
        List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                              .setParameter("patientPk", ptPK)
                              .getResultList();
        KarteBean karte = kartes.get(0);
        
        // 当日のドキュメントを検索
        List<DocumentModel> list = (List<DocumentModel>)em.createQuery(QUERY_DOCUMENT_BY_KARTEID_STARTDATE)
                                                 .setParameter("karteId", karte.getId())
                                                 .setParameter("started", startDate)
                                                 .getResultList();
        if (list.isEmpty()) {
            return 1;
        }
        
        // それを削除
        int cnt=1;
        for (DocumentModel d : list) {
            List<String> l = deleteDocument(d.getId());
            cnt+=l.size();
        }
        
        return cnt;
    }
        
    public List<String> deleteDocument(long id) {
        
        //----------------------------------------
        // 参照されているDocumentの場合は例外を投げる
        //----------------------------------------
        Collection refs = em.createQuery(QUERY_DOCUMENT_BY_LINK_ID)
        .setParameter("id", id).getResultList();
        if (refs != null && refs.size() >0) {
            CanNotDeleteException ce = new CanNotDeleteException("他のドキュメントから参照されているため削除できません。");
            throw ce;
        } 
        
        // 終了日
        Date ended = new Date();
        
        // 削除件数
        int cnt=0;
        
        // 削除リスト　文書ID
        List<String> list = new ArrayList<String>();
        
        // Loop で削除
        while (true) {
            
            try {
                //-----------------------
                // 対象 Document を取得する
                //-----------------------
                DocumentModel delete = (DocumentModel)em.find(DocumentModel.class, id);
                
                //------------------------
                // 削除フラグをたてる
                //------------------------
                delete.setStatus(IInfoModel.STATUS_DELETE);
                delete.setEnded(ended);
                cnt++;
                list.add(delete.getDocInfoModel().getDocId());
                
                //------------------------------
                // 関連するモジュールに同じ処理を行う
                //------------------------------
                Collection deleteModules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
                .setParameter("id", id).getResultList();
                for (Iterator iter = deleteModules.iterator(); iter.hasNext(); ) {
                    ModuleModel model = (ModuleModel) iter.next();
                    model.setStatus(IInfoModel.STATUS_DELETE);
                    model.setEnded(ended);
                }

                //------------------------------
                // 関連する画像に同じ処理を行う
                //------------------------------
                Collection deleteImages = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
                .setParameter("id", id).getResultList();
                for (Iterator iter = deleteImages.iterator(); iter.hasNext(); ) {
                    SchemaModel model = (SchemaModel) iter.next();
                    model.setStatus(IInfoModel.STATUS_DELETE);
                    model.setEnded(ended);
                }

                //------------------------------
                // 関連するAttachmentに同じ処理を行う
                //------------------------------
                Collection deleteAttachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
                .setParameter("id", id).getResultList();
                for (Iterator iter = deleteAttachments.iterator(); iter.hasNext(); ) {
                    AttachmentModel model = (AttachmentModel)iter.next();
                    model.setStatus(IInfoModel.STATUS_DELETE);
                    model.setEnded(ended);
                }
                
                // 削除したDocumentのlinkID を 削除するDocument id(PK) にしてLoopさせる
                id = delete.getLinkId();
                
            } catch (Exception e) {
                break;
            }
        }

        return list;
    }
    
    private Date dateFromString(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(str);
        } catch (Exception e) {           
        }
        return null;
    }
}
