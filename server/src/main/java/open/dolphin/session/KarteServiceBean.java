package open.dolphin.session;

import java.util.*;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.jms.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.*;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Named
@Stateless
public class KarteServiceBean {
    
    // parameters
    private static final String PATIENT_PK = "patientPk";
    private static final String KARTE_ID = "karteId";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";
    private static final String ID = "id";
    private static final String ENTITY = "entity";
    private static final String FID = "fid";
    private static final String PID = "pid";

    private static final String QUERY_KARTE = "from KarteBean k where k.patient.id=:patientPk";
    private static final String QUERY_ALLERGY = "from ObservationModel o where o.karte.id=:karteId and o.observation='Allergy'";
    private static final String QUERY_BODY_HEIGHT = "from ObservationModel o where o.karte.id=:karteId and o.observation='PhysicalExam' and o.phenomenon='bodyHeight'";
    private static final String QUERY_BODY_WEIGHT = "from ObservationModel o where o.karte.id=:karteId and o.observation='PhysicalExam' and o.phenomenon='bodyWeight'";
    // Cancel status=64 を where へ追加
    private static final String QUERY_PATIENT_VISIT = "from PatientVisitModel p where p.patient.id=:patientPk and p.pvtDate >= :fromDate and p.status!=64";
    private static final String QUERY_DOC_INFO = "from DocumentModel d where d.karte.id=:karteId and d.started >= :fromDate and (d.status='F' or d.status='T')";
    private static final String QUERY_PATIENT_MEMO = "from PatientMemoModel p where p.karte.id=:karteId";

    private static final String QUERY_DOCUMENT_INCLUDE_MODIFIED = "from DocumentModel d where d.karte.id=:karteId and d.started >= :fromDate and d.status !='D'";
    private static final String QUERY_DOCUMENT = "from DocumentModel d where d.karte.id=:karteId and d.started >= :fromDate and (d.status='F' or d.status='T')";
    private static final String QUERY_DOCUMENT_BY_LINK_ID = "from DocumentModel d where d.linkId=:id";

    private static final String QUERY_MODULE_BY_DOC_ID = "from ModuleModel m where m.document.id=:id";
    private static final String QUERY_SCHEMA_BY_DOC_ID = "from SchemaModel i where i.document.id=:id";
    private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id";
//minagawa^ LSC Test
    //private static final String QUERY_MODULE_BY_ENTITY = "from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity=:entity and m.started between :fromDate and :toDate and m.status='F'";
    private static final String QUERY_MODULE_BY_ENTITY = "from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity=:entity and m.started between :fromDate and :toDate and m.status='F' order by m.started";
//minagawa$
    private static final String QUERY_SCHEMA_BY_KARTE_ID = "from SchemaModel i where i.karte.id =:karteId and i.started between :fromDate and :toDate and i.status='F'";

    private static final String QUERY_SCHEMA_BY_FACILITY_ID = "from SchemaModel i where i.karte.patient.facilityId like :fid and i.extRef.sop is not null and i.status='F'";

    private static final String QUERY_DIAGNOSIS_BY_KARTE_DATE = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.started >= :fromDate";
    private static final String QUERY_DIAGNOSIS_BY_KARTE_DATE_ACTIVEONLY = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.started >= :fromDate and r.ended is NULL";
    private static final String QUERY_DIAGNOSIS_BY_KARTE = "from RegisteredDiagnosisModel r where r.karte.id=:karteId";
    private static final String QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.ended is NULL";

    private static final String TOUTOU = "TOUTOU";
    private static final String TOUTOU_REPLY = "TOUTOU_REPLY";
    private static final String QUERY_LETTER_BY_KARTE_ID = "from TouTouLetter f where f.karte.id=:karteId";
    private static final String QUERY_REPLY_BY_KARTE_ID = "from TouTouReply f where f.karte.id=:karteId";
    private static final String QUERY_LETTER_BY_ID = "from TouTouLetter t where t.id=:id";
    private static final String QUERY_REPLY_BY_ID = "from TouTouReply t where t.id=:id";

    private static final String QUERY_APPO_BY_KARTE_ID_PERIOD = "from AppointmentModel a where a.karte.id = :karteId and a.date between :fromDate and :toDate";

    private static final String QUERY_PATIENT_BY_FID_PID = "from PatientModel p where p.facilityId=:fid and p.patientId=:pid";
    
//masuda^
    private static final String QUERY_LASTDOC_DATE 
            = "select max(m.started) from DocumentModel m where m.karte.id = :karteId and (m.status = 'F' or m.status = 'T')";
//masuda$
    
    @PersistenceContext
    private EntityManager em;
    
    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;
    
    @Resource(mappedName = "java:/queue/dolphin")
    private javax.jms.Queue queue;
    
    public KarteBean getKarte(String fid, String pid, Date fromDate) {
        
        try {
            
            // 患者レコードは FacilityId と patientId で複合キーになっている
            PatientModel patient
                = (PatientModel)em.createQuery(QUERY_PATIENT_BY_FID_PID)
                .setParameter(FID, fid)
                .setParameter(PID, pid)
                .getSingleResult();

            long patientPK = patient.getId();
            
            // 最初に患者のカルテを取得する
            List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                                  .setParameter(PATIENT_PK, patientPK)
                                  .getResultList();
            KarteBean karte = kartes.get(0);

            // カルテの PK を得る
            long karteId = karte.getId();

            // アレルギーデータを取得する
            List<ObservationModel> list1 =
                    (List<ObservationModel>)em.createQuery(QUERY_ALLERGY)
                                              .setParameter(KARTE_ID, karteId)
                                              .getResultList();
            if (!list1.isEmpty()) {
                List<AllergyModel> allergies = new ArrayList<AllergyModel>(list1.size());
                for (ObservationModel observation : list1) {
                    AllergyModel allergy = new AllergyModel();
                    allergy.setObservationId(observation.getId());
                    allergy.setFactor(observation.getPhenomenon());
                    allergy.setSeverity(observation.getCategoryValue());
                    allergy.setIdentifiedDate(observation.confirmDateAsString());
                    allergy.setMemo(observation.getMemo());
                    allergies.add(allergy);
                }
                karte.setAllergies(allergies);
            }

            // 身長データを取得する
            List<ObservationModel> list2 =
                    (List<ObservationModel>)em.createQuery(QUERY_BODY_HEIGHT)
                                              .setParameter(KARTE_ID, karteId)
                                              .getResultList();
            if (!list2.isEmpty()) {
                List<PhysicalModel> physicals = new ArrayList<PhysicalModel>(list2.size());
                for (ObservationModel observation : list2) {
                    PhysicalModel physical = new PhysicalModel();
                    physical.setHeightId(observation.getId());
                    physical.setHeight(observation.getValue());
                    physical.setIdentifiedDate(observation.confirmDateAsString());
                    physical.setMemo(ModelUtils.getDateAsString(observation.getRecorded()));
                    physicals.add(physical);
                }
                karte.setHeights(physicals);
            }

            // 体重データを取得する
            List<ObservationModel> list3 =
                    (List<ObservationModel>)em.createQuery(QUERY_BODY_WEIGHT)
                                              .setParameter(KARTE_ID, karteId)
                                              .getResultList();
            if (!list3.isEmpty()) {
                List<PhysicalModel> physicals = new ArrayList<PhysicalModel>(list3.size());
                for (ObservationModel observation : list3) {
                    PhysicalModel physical = new PhysicalModel();
                    physical.setWeightId(observation.getId());
                    physical.setWeight(observation.getValue());
                    physical.setIdentifiedDate(observation.confirmDateAsString());
                    physical.setMemo(ModelUtils.getDateAsString(observation.getRecorded()));
                    physicals.add(physical);
                }
                karte.setWeights(physicals);
            }

            // 直近の来院日エントリーを取得しカルテに設定する
            List<PatientVisitModel> latestVisits =
                    (List<PatientVisitModel>)em.createQuery(QUERY_PATIENT_VISIT)
                                               .setParameter(PATIENT_PK, patientPK)
                                               .setParameter(FROM_DATE, ModelUtils.getDateAsString(fromDate))
                                               .getResultList();

            if (!latestVisits.isEmpty()) {
                List<String> visits = new ArrayList<String>(latestVisits.size());
                for (PatientVisitModel bean : latestVisits) {
                    // 2012-07-23
                    // cancelしている場合は返さない
                    // 来院日のみを使用する
                    visits.add(bean.getPvtDate());
                }
                karte.setPatientVisits(visits);
            }

            // 文書履歴エントリーを取得しカルテに設定する
            List<DocumentModel> documents =
                    (List<DocumentModel>)em.createQuery(QUERY_DOC_INFO)
                                           .setParameter(KARTE_ID, karteId)
                                           .setParameter(FROM_DATE, fromDate)
                                           .getResultList();

            if (!documents.isEmpty()) {
                List<DocInfoModel> c = new ArrayList<DocInfoModel>(documents.size());
                for (DocumentModel docBean : documents) {
                    docBean.toDetuch();
                    c.add(docBean.getDocInfoModel());
                }
                karte.setDocInfoList(c);
            }

            // 患者Memoを取得する
            List<PatientMemoModel> memo =
                    (List<PatientMemoModel>)em.createQuery(QUERY_PATIENT_MEMO)
                                              .setParameter(KARTE_ID, karteId)
                                              .getResultList();
            if (!memo.isEmpty()) {
                karte.setMemoList(memo);
            }
            
//masuda^
            // 最終文書日
            try {
                Date lastDocDate = (Date)
                        em.createQuery(QUERY_LASTDOC_DATE)
                        .setParameter(KARTE_ID, karteId)
                        .getSingleResult();
                karte.setLastDocDate(lastDocDate);
            } catch (NoResultException e) {
            }
//masuda$            

            return karte;
        
            
        } catch (Exception e) {
            
        }
        
        return null;
    }

    /**
     * カルテの基礎的な情報をまとめて返す。
     * @param patientPk 患者の Database Primary Key
     * @param fromDate 各種エントリの検索開始日
     * @return 基礎的な情報をフェッチした KarteBean
     */
    public KarteBean getKarte(long patientPK, Date fromDate) {

        try {
            // 最初に患者のカルテを取得する
            List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                                  .setParameter(PATIENT_PK, patientPK)
                                  .getResultList();
            KarteBean karte = kartes.get(0);

            // カルテの PK を得る
            long karteId = karte.getId();

            // アレルギーデータを取得する
            List<ObservationModel> list1 =
                    (List<ObservationModel>)em.createQuery(QUERY_ALLERGY)
                                              .setParameter(KARTE_ID, karteId)
                                              .getResultList();
            if (!list1.isEmpty()) {
                List<AllergyModel> allergies = new ArrayList<AllergyModel>(list1.size());
                for (ObservationModel observation : list1) {
                    AllergyModel allergy = new AllergyModel();
                    allergy.setObservationId(observation.getId());
                    allergy.setFactor(observation.getPhenomenon());
                    allergy.setSeverity(observation.getCategoryValue());
                    allergy.setIdentifiedDate(observation.confirmDateAsString());
                    allergy.setMemo(observation.getMemo());
                    allergies.add(allergy);
                }
                karte.setAllergies(allergies);
            }

            // 身長データを取得する
            List<ObservationModel> list2 =
                    (List<ObservationModel>)em.createQuery(QUERY_BODY_HEIGHT)
                                              .setParameter(KARTE_ID, karteId)
                                              .getResultList();
            if (!list2.isEmpty()) {
                List<PhysicalModel> physicals = new ArrayList<PhysicalModel>(list2.size());
                for (ObservationModel observation : list2) {
                    PhysicalModel physical = new PhysicalModel();
                    physical.setHeightId(observation.getId());
                    physical.setHeight(observation.getValue());
                    physical.setIdentifiedDate(observation.confirmDateAsString());
                    physical.setMemo(ModelUtils.getDateAsString(observation.getRecorded()));
                    physicals.add(physical);
                }
                karte.setHeights(physicals);
            }

            // 体重データを取得する
            List<ObservationModel> list3 =
                    (List<ObservationModel>)em.createQuery(QUERY_BODY_WEIGHT)
                                              .setParameter(KARTE_ID, karteId)
                                              .getResultList();
            if (!list3.isEmpty()) {
                List<PhysicalModel> physicals = new ArrayList<PhysicalModel>(list3.size());
                for (ObservationModel observation : list3) {
                    PhysicalModel physical = new PhysicalModel();
                    physical.setWeightId(observation.getId());
                    physical.setWeight(observation.getValue());
                    physical.setIdentifiedDate(observation.confirmDateAsString());
                    physical.setMemo(ModelUtils.getDateAsString(observation.getRecorded()));
                    physicals.add(physical);
                }
                karte.setWeights(physicals);
            }

            // 直近の来院日エントリーを取得しカルテに設定する
            List<PatientVisitModel> latestVisits =
                    (List<PatientVisitModel>)em.createQuery(QUERY_PATIENT_VISIT)
                                               .setParameter(PATIENT_PK, patientPK)
                                               .setParameter(FROM_DATE, ModelUtils.getDateAsString(fromDate))
                                               .getResultList();

            if (!latestVisits.isEmpty()) {
                List<String> visits = new ArrayList<String>(latestVisits.size());
                for (PatientVisitModel bean : latestVisits) {
                    // 来院日のみを使用する
                    visits.add(bean.getPvtDate());
                }
                karte.setPatientVisits(visits);
            }

            // 文書履歴エントリーを取得しカルテに設定する
            List<DocumentModel> documents =
                    (List<DocumentModel>)em.createQuery(QUERY_DOC_INFO)
                                           .setParameter(KARTE_ID, karteId)
                                           .setParameter(FROM_DATE, fromDate)
                                           .getResultList();

            if (!documents.isEmpty()) {
                List<DocInfoModel> c = new ArrayList<DocInfoModel>(documents.size());
                for (DocumentModel docBean : documents) {
                    docBean.toDetuch();
                    c.add(docBean.getDocInfoModel());
                }
                karte.setDocInfoList(c);
            }

            // 患者Memoを取得する
            List<PatientMemoModel> memo =
                    (List<PatientMemoModel>)em.createQuery(QUERY_PATIENT_MEMO)
                                              .setParameter(KARTE_ID, karteId)
                                              .getResultList();
            if (!memo.isEmpty()) {
                karte.setMemoList(memo);
            }
            
//masuda^
            // 最終文書日
            try {
                Date lastDocDate = (Date)
                        em.createQuery(QUERY_LASTDOC_DATE)
                        .setParameter(KARTE_ID, karteId)
                        .getSingleResult();
                karte.setLastDocDate(lastDocDate);
            } catch (NoResultException e) {
            }
//masuda$
            return karte;

        } catch (NoResultException e) {
            // 患者登録の際にカルテも生成してある
        }

        return null;
    }

    /**
     * 文書履歴エントリを取得する。
     * @param karteId カルテId
     * @param fromDate 取得開始日
     * @param status ステータス
     * @return DocInfo のコレクション
     */
    public List<DocInfoModel> getDocumentList(long karteId, Date fromDate, boolean includeModifid) {

        List<DocumentModel> documents;

        if (includeModifid) {
            documents = (List<DocumentModel>)em.createQuery(QUERY_DOCUMENT_INCLUDE_MODIFIED)
            .setParameter(KARTE_ID, karteId)
            .setParameter(FROM_DATE, fromDate)
            .getResultList();
        } else {
            documents = (List<DocumentModel>)em.createQuery(QUERY_DOCUMENT)
            .setParameter(KARTE_ID, karteId)
            .setParameter(FROM_DATE, fromDate)
            .getResultList();
        }

        List<DocInfoModel> result = new ArrayList<DocInfoModel>();
        for (DocumentModel doc : documents) {
            // モデルからDocInfo へ必要なデータを移す
            // クライアントが DocInfo だけを利用するケースがあるため
            doc.toDetuch();
            result.add(doc.getDocInfoModel());
        }
        return result;
    }

    /**
     * 文書(DocumentModel Object)を取得する。
     * @param ids DocumentModel の pkコレクション
     * @return DocumentModelのコレクション
     */
    public List<DocumentModel> getDocuments(List<Long> ids) {

        List<DocumentModel> ret = new ArrayList<DocumentModel>(3);

        // ループする
        for (Long id : ids) {

            // DocuentBean を取得する
            DocumentModel document = (DocumentModel) em.find(DocumentModel.class, id);

            // ModuleBean を取得する
            List modules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
            .setParameter(ID, id)
            .getResultList();
            document.setModules(modules);

            // SchemaModel を取得する
            List images = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
            .setParameter(ID, id)
            .getResultList();
            document.setSchema(images);
            
            // AttachmentModel を取得する
            List attachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
            .setParameter(ID, id)
            .getResultList();
            document.setAttachment(attachments);

            ret.add(document);
        }
        
//s.oh^ 不具合修正
        for (DocumentModel doc : ret) {
            // モデルからDocInfo へ必要なデータを移す
            // クライアントがDocInfo だけを利用するケースがあるため
            doc.toDetuch();
        }
//s.oh$

        return ret;
    }
    
    /**
     * ドキュメント DocumentModel オブジェクトを保存する。
     * @param karteId カルテId
     * @param document 追加するDocumentModel オブジェクト
     * @return 追加した数
     */
    public long addDocument(DocumentModel document) {

        // 永続化する
        em.persist(document);

        // ID
        long id = document.getId();

        // 修正版の処理を行う
        long parentPk = document.getDocInfoModel().getParentPk();

        if (parentPk != 0L) {

            // 適合終了日を新しい版の確定日にする
            Date ended = document.getConfirmed();

            // オリジナルを取得し 終了日と status = M を設定する
            DocumentModel old = (DocumentModel)em.find(DocumentModel.class, parentPk);
            old.setEnded(ended);
            old.setStatus(IInfoModel.STATUS_MODIFIED);

            // 関連するモジュールとイメージに同じ処理を実行する
            Collection oldModules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
            .setParameter(ID, parentPk).getResultList();
            for (Iterator iter = oldModules.iterator(); iter.hasNext(); ) {
                ModuleModel model = (ModuleModel)iter.next();
                model.setEnded(ended);
                model.setStatus(IInfoModel.STATUS_MODIFIED);
            }

            // Schema
            Collection oldImages = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
            .setParameter(ID, parentPk).getResultList();
            for (Iterator iter = oldImages.iterator(); iter.hasNext(); ) {
                SchemaModel model = (SchemaModel)iter.next();
                model.setEnded(ended);
                model.setStatus(IInfoModel.STATUS_MODIFIED);
            }
            
            // Attachment
            Collection oldAttachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
            .setParameter(ID, parentPk).getResultList();
            for (Iterator iter = oldAttachments.iterator(); iter.hasNext(); ) {
                AttachmentModel model = (AttachmentModel)iter.next();
                model.setEnded(ended);
                model.setStatus(IInfoModel.STATUS_MODIFIED);
            }
        }
        
        //-------------------------------------------------------------
        // CLAIM送信
        //-------------------------------------------------------------
        if (!document.getDocInfoModel().isSendClaim()) {
            return id;
        }
        //Logger.getLogger("open.dolphin").info("KarteServiceBean will send claim");
        sendDocument(document);
        
        return id;
    }

    public long addDocumentAndUpdatePVTState(DocumentModel document, long pvtPK, int state) {

        // 永続化する
        em.persist(document);

        // ID
        long id = document.getId();

        // 修正版の処理を行う
        long parentPk = document.getDocInfoModel().getParentPk();

        if (parentPk != 0L) {

            // 適合終了日を新しい版の確定日にする
            Date ended = document.getConfirmed();

            // オリジナルを取得し 終了日と status = M を設定する
            DocumentModel old = (DocumentModel) em.find(DocumentModel.class, parentPk);
            old.setEnded(ended);
            old.setStatus(IInfoModel.STATUS_MODIFIED);

            // 関連するモジュールとイメージに同じ処理を実行する
            Collection oldModules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
            .setParameter(ID, parentPk).getResultList();
            for (Iterator iter = oldModules.iterator(); iter.hasNext(); ) {
                ModuleModel model = (ModuleModel)iter.next();
                model.setEnded(ended);
                model.setStatus(IInfoModel.STATUS_MODIFIED);
            }

            // Schema
            Collection oldImages = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
            .setParameter(ID, parentPk).getResultList();
            for (Iterator iter = oldImages.iterator(); iter.hasNext(); ) {
                SchemaModel model = (SchemaModel)iter.next();
                model.setEnded(ended);
                model.setStatus(IInfoModel.STATUS_MODIFIED);
            }
            
            // Attachment
            Collection oldAttachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
            .setParameter(ID, parentPk).getResultList();
            for (Iterator iter = oldAttachments.iterator(); iter.hasNext(); ) {
                AttachmentModel model = (AttachmentModel)iter.next();
                model.setEnded(ended);
                model.setStatus(IInfoModel.STATUS_MODIFIED);
            }
        }
        
        //-------------------------------------------------------------
        // CLAIM送信
        //-------------------------------------------------------------
        if (!document.getDocInfoModel().isSendClaim()) {
            return id;
        }
        sendDocument(document);
        
        //------------------------------------------------------------
        // PVT 更新  state==2 || state == 4
        //------------------------------------------------------------
        try {
            // PVT 更新  state==2 || state == 4
            PatientVisitModel exist = (PatientVisitModel) em.find(PatientVisitModel.class, new Long(pvtPK));
            exist.setState(state);
        } catch (Throwable e) {
            e.printStackTrace(System.err);
        }

        return id;
    }
    
//    private void sendDocument(DocumentModel document)  {
//        try {
//            PVTHealthInsuranceModel insm = document.getDocInfoModel().getPVTHealthInsuranceModel();
//            if (insm!=null) {
//                System.err.println("PVTHealthInsuranceModel!=null");
//                List<PVTPublicInsuranceItemModel> pub = insm.getPublicItems();
//                if (pub!=null) {
//                    System.err.println("pub!=null");
//                    System.err.println("pub count=" + pub.size());
//                } else {
//                    System.err.println("pub is null");
//                }
//                System.err.println(insm);
//            } else {
//                System.err.println("PVTHealthInsuranceModel! is null");
//            }
//            ClaimSender sender = new ClaimSender("172.31.210.101",8210,"UTF-8");
//            sender.send(document);
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//        }
//    }
    
    // JMS+MDB
    public void sendDocument(DocumentModel document) {
        
        Connection conn = null;
        try {
            conn = connectionFactory.createConnection();
            Session session = conn.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);

            ObjectMessage msg = session.createObjectMessage(document);
            MessageProducer producer = session.createProducer(queue);
            producer.send(msg);
            

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage());

        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (JMSException e) { 
                }
            }
        }
    }

    /**
     * ドキュメントを論理削除する。
     * @param pk 論理削除するドキュメントの primary key
     * @return 削除したドキュメントの文書IDリスト
     */
    public List<String> deleteDocument(long id) {
        
        //----------------------------------------
        // 参照されているDocumentの場合は例外を投げる
        //----------------------------------------
        Collection refs = em.createQuery(QUERY_DOCUMENT_BY_LINK_ID)
        .setParameter(ID, id).getResultList();
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
                .setParameter(ID, id).getResultList();
                for (Iterator iter = deleteModules.iterator(); iter.hasNext(); ) {
                    ModuleModel model = (ModuleModel) iter.next();
                    model.setStatus(IInfoModel.STATUS_DELETE);
                    model.setEnded(ended);
                }

                //------------------------------
                // 関連する画像に同じ処理を行う
                //------------------------------
                Collection deleteImages = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
                .setParameter(ID, id).getResultList();
                for (Iterator iter = deleteImages.iterator(); iter.hasNext(); ) {
                    SchemaModel model = (SchemaModel) iter.next();
                    model.setStatus(IInfoModel.STATUS_DELETE);
                    model.setEnded(ended);
                }

                //------------------------------
                // 関連するAttachmentに同じ処理を行う
                //------------------------------
                Collection deleteAttachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
                .setParameter(ID, id).getResultList();
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

    /**
     * ドキュメントのタイトルを変更する。
     * @param pk 変更するドキュメントの primary key
     * @return 変更した件数
     */
    public int updateTitle(long pk, String title) {
        DocumentModel update = (DocumentModel) em.find(DocumentModel.class, pk);
        update.getDocInfoModel().setTitle(title);
        return 1;
    }

    /**
     * ModuleModelエントリを取得する。
     * @param spec モジュール検索仕様
     * @return ModuleModelリストのリスト
     */
    public List<List<ModuleModel>> getModules(long karteId, String entity, List fromDate, List toDate) {

        // 抽出期間は別けられている
        int len = fromDate.size();
        List<List<ModuleModel>> ret = new ArrayList<List<ModuleModel>>(len);

        // 抽出期間セットの数だけ繰り返す
        for (int i = 0; i < len; i++) {

            List<ModuleModel> modules
                    = em.createQuery(QUERY_MODULE_BY_ENTITY)
                    .setParameter(KARTE_ID, karteId)
                    .setParameter(ENTITY, entity)
                    .setParameter(FROM_DATE, fromDate.get(i))
                    .setParameter(TO_DATE, toDate.get(i))
                    .getResultList();

            ret.add(modules);
        }

        return ret;
    }

    /**
     * SchemaModelエントリを取得する。
     * @param karteId カルテID
     * @param fromDate
     * @param toDate
     * @return SchemaModelエントリの配列
     */
    public List<List> getImages(long karteId, List fromDate, List toDate) {

        // 抽出期間は別けられている
        int len = fromDate.size();
        List<List> ret = new ArrayList<List>(len);

        // 抽出期間セットの数だけ繰り返す
        for (int i = 0; i < len; i++) {

            List modules
                    = em.createQuery(QUERY_SCHEMA_BY_KARTE_ID)
                    .setParameter(KARTE_ID, karteId)
                    .setParameter(FROM_DATE, fromDate.get(i))
                    .setParameter(TO_DATE, toDate.get(i))
                    .getResultList();

            ret.add(modules);
        }

        return ret;
    }

    /**
     * 画像を取得する。
     * @param id SchemaModel Id
     * @return SchemaModel
     */
    public SchemaModel getImage(long id) {
        SchemaModel image = (SchemaModel)em.find(SchemaModel.class, id);
        return image;
    }

    public List<SchemaModel> getS3Images(String fid, int firstResult, int maxResult) {

        List<SchemaModel> ret = (List<SchemaModel>)
                                em.createQuery(QUERY_SCHEMA_BY_FACILITY_ID)
                                .setParameter(FID, fid+"%")
                                .setFirstResult(firstResult)
                                .setMaxResults(maxResult)
                                .getResultList();
        return ret;
    }

    public void deleteS3Image(long pk) {
        SchemaModel target = em.find(SchemaModel.class, pk);
        target.getExtRefModel().setBucket(null);
        target.getExtRefModel().setSop(null);
        target.getExtRefModel().setUrl(null);
    }

    /**
     * 傷病名リストを取得する。
     * @param spec 検索仕様
     * @return 傷病名のリスト
     */
    public List<RegisteredDiagnosisModel> getDiagnosis(long karteId, Date fromDate, boolean activeOnly) {

        List<RegisteredDiagnosisModel> ret;

        // 疾患開始日を指定している
        if (fromDate != null) {
            String query = activeOnly ? QUERY_DIAGNOSIS_BY_KARTE_DATE_ACTIVEONLY : QUERY_DIAGNOSIS_BY_KARTE_DATE;
            ret = (List<RegisteredDiagnosisModel>) em.createQuery(query)
                    .setParameter(KARTE_ID, karteId)
                    .setParameter(FROM_DATE, fromDate)
                    .getResultList();
        } else {
            // 全期間の傷病名を得る
            String query = activeOnly ? QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY : QUERY_DIAGNOSIS_BY_KARTE;
            ret = (List<RegisteredDiagnosisModel>)em.createQuery(query)
                    .setParameter(KARTE_ID, karteId)
                    .getResultList();
        }

        return ret;
    }
    
    /**
     * 新規病名保存、病名更新、CLAIM送信を一括して実行する。
     * @param wrapper DiagnosisSendWrapper
     * @return 新規病名のPKリスト
     */
    public List<Long> postPutSendDiagnosis(DiagnosisSendWrapper wrapper) {
        
        // 更新
        if (wrapper.getUpdatedDiagnosis()!=null) {
            
            int cnt = 0;
            List<RegisteredDiagnosisModel> updateList = wrapper.getUpdatedDiagnosis();
            
            for (RegisteredDiagnosisModel bean : updateList) {
                em.merge(bean);
                cnt++;
            }
        }
        
        // 永続化
        List<Long> ret = new ArrayList<Long>(3);
        if (wrapper.getAddedDiagnosis()!=null) {
            
            List<RegisteredDiagnosisModel> addList = wrapper.getAddedDiagnosis();
            
            for (RegisteredDiagnosisModel bean : addList) {
                em.persist(bean);
                ret.add(new Long(bean.getId()));
            }
        }
        
        //-------------------------------------------------------------
        // CLAIM送信
        //-------------------------------------------------------------
        if (wrapper.getSendClaim() && wrapper.getConfirmDate()!=null) {
            
            Connection conn = null;
            try {
                conn = connectionFactory.createConnection();
                Session session = conn.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);

                ObjectMessage msg = session.createObjectMessage(wrapper);
                MessageProducer producer = session.createProducer(queue);
                producer.send(msg);

            } catch (Exception e) {
                e.printStackTrace(System.err);
                throw new RuntimeException(e.getMessage());

            } 
            finally {
                if(conn != null)
                {
                    try
                    {
                    conn.close();
                    }
                    catch (JMSException e)
                    { 
                    }
                }
            }
        }
        
        return ret;
    }
    

    /**
     * 傷病名を追加する。
     * @param addList 追加する傷病名のリスト
     * @return idのリスト
     */
    public List<Long> addDiagnosis(List<RegisteredDiagnosisModel> addList) {

        List<Long> ret = new ArrayList<Long>(addList.size());

        for (RegisteredDiagnosisModel bean : addList) {
            em.persist(bean);
            ret.add(new Long(bean.getId()));
        }

        return ret;
    }

    /**
     * 傷病名を更新する。
     * @param updateList
     * @return 更新数
     */
    public int updateDiagnosis(List<RegisteredDiagnosisModel> updateList) {

        int cnt = 0;

        for (RegisteredDiagnosisModel bean : updateList) {
            em.merge(bean);
            cnt++;
        }

        return cnt;
    }

    /**
     * 傷病名を削除する。
     * @param removeList 削除する傷病名のidリスト
     * @return 削除数
     */
    public int removeDiagnosis(List<Long> removeList) {

        int cnt = 0;

        for (Long id : removeList) {
            RegisteredDiagnosisModel bean = (RegisteredDiagnosisModel) em.find(RegisteredDiagnosisModel.class, id);
            em.remove(bean);
            cnt++;
        }

        return cnt;
    }

    /**
     * Observationを取得する。
     * @param spec 検索仕様
     * @return Observationのリスト
     */
    public List<ObservationModel> getObservations(long karteId, String observation, String phenomenon, Date firstConfirmed) {

        List ret = null;

        if (observation != null) {
            if (firstConfirmed != null) {
                ret = em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.observation=:observation and o.started >= :firstConfirmed")
                .setParameter(KARTE_ID, karteId)
                .setParameter("observation", observation)
                .setParameter("firstConfirmed", firstConfirmed)
                .getResultList();

            } else {
                ret = em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.observation=:observation")
                .setParameter(KARTE_ID, karteId)
                .setParameter("observation", observation)
                .getResultList();
            }
        } else if (phenomenon != null) {
            if (firstConfirmed != null) {
                ret = em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.phenomenon=:phenomenon and o.started >= :firstConfirmed")
                .setParameter(KARTE_ID, karteId)
                .setParameter("phenomenon", phenomenon)
                .setParameter("firstConfirmed", firstConfirmed)
                .getResultList();
            } else {
                ret = em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.phenomenon=:phenomenon")
                .setParameter(KARTE_ID, karteId)
                .setParameter("phenomenon", phenomenon)
                .getResultList();
            }
        }
        return ret;
    }

    /**
     * Observationを追加する。
     * @param observations 追加するObservationのリスト
     * @return 追加したObservationのIdリスト
     */
    public List<Long> addObservations(List<ObservationModel> observations) {

        if (observations != null && observations.size() > 0) {

            List<Long> ret = new ArrayList<Long>(observations.size());

            for (ObservationModel model : observations) {
                em.persist(model);
                ret.add(new Long(model.getId()));
            }

            return ret;
        }
        return null;
    }

    /**
     * Observationを更新する。
     * @param observations 更新するObservationのリスト
     * @return 更新した数
     */
    public int updateObservations(List<ObservationModel> observations) {

        if (observations != null && observations.size() > 0) {
            int cnt = 0;
            for (ObservationModel model : observations) {
                em.merge(model);
                cnt++;
            }
            return cnt;
        }
        return 0;
    }

    /**
     * Observationを削除する。
     * @param observations 削除するObservationのリスト
     * @return 削除した数
     */
    
    public int removeObservations(List<Long> observations) {
        if (observations != null && observations.size() > 0) {
            int cnt = 0;
            for (Long id : observations) {
                ObservationModel model = (ObservationModel) em.find(ObservationModel.class, id);
                em.remove(model);
                cnt++;
            }
            return cnt;
        }
        return 0;
    }

    /**
     * 患者メモを更新する。
     * @param memo 更新するメモ
     */
    
    public int updatePatientMemo(PatientMemoModel memo) {

        int cnt = 0;

        if (memo.getId() == 0L) {
            em.persist(memo);
        } else {
            em.merge(memo);
        }
        cnt++;
        return cnt;
    }

    //--------------------------------------------------------------------------

    /**
     * 紹介状を保存または更新する。
     */
    
    public long saveOrUpdateLetter(LetterModel model) {
        LetterModel saveOrUpdate = em.merge(model);
        return saveOrUpdate.getId();
    }

    /**
     * 紹介状のリストを取得する。
     */
    
    public List<LetterModel> getLetterList(long karteId, String docType) {

        if (docType.equals(TOUTOU)) {
            // 紹介状
            List<LetterModel> ret = (List<LetterModel>)
                        em.createQuery(QUERY_LETTER_BY_KARTE_ID)
                        .setParameter(KARTE_ID, karteId)
                        .getResultList();
            return ret;

        } else if (docType.equals(TOUTOU_REPLY)) {
            // 返書
            List<LetterModel> ret = (List<LetterModel>)
                        em.createQuery(QUERY_REPLY_BY_KARTE_ID)
                        .setParameter(KARTE_ID, karteId)
                        .getResultList();
            return ret;
        }

        return null;
    }

    /**
     * 紹介状を取得する。
     */
    
    public LetterModel getLetter(long letterPk) {

        LetterModel ret = (LetterModel)
                        em.createQuery(QUERY_LETTER_BY_ID)
                        .setParameter(ID, letterPk)
                        .getSingleResult();
        return ret;
    }

    
    public LetterModel getLetterReply(long letterPk) {

        LetterModel ret = (LetterModel)
                        em.createQuery(QUERY_REPLY_BY_ID)
                        .setParameter(ID, letterPk)
                        .getSingleResult();
        return ret;
    }

    //--------------------------------------------------------------------------

    
    public List<List<AppointmentModel>> getAppointmentList(long karteId, List fromDate, List toDate) {

        // 抽出期間は別けられている
        int len = fromDate.size();
        List<List<AppointmentModel>> ret = new ArrayList<List<AppointmentModel>>(len);

        // 抽出期間セットの数だけ繰り返す
        for (int i = 0; i < len; i++) {

            List<AppointmentModel> modules
                    = em.createQuery(QUERY_APPO_BY_KARTE_ID_PERIOD)
                    .setParameter(KARTE_ID, karteId)
                    .setParameter(FROM_DATE, fromDate.get(i))
                    .setParameter(TO_DATE, toDate.get(i))
                    .getResultList();

            ret.add(modules);
        }

        return ret;
    }
    
    //---------------------------------------------------------------------------
     
    // 指定したEntityのModuleModleを一括取得
    @SuppressWarnings("unchecked")
    public List<ModuleModel> getModulesEntitySearch(String fid, long karteId, Date fromDate, Date toDate, List<String> entities) {
        
        // 指定したentityのModuleModelを返す
        List<ModuleModel> ret;
        
        //if (karteId != 0){
            final String sql = "from ModuleModel m where m.karte.id = :karteId " +
                    "and m.started between :fromDate and :toDate and m.status='F' " +
                    "and m.moduleInfo.entity in (:entities)";

            ret = em.createQuery(sql)
                    .setParameter("karteId", karteId)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .setParameter("entities", entities)
                    .getResultList();
//          } else {
//            // karteIdが指定されていなかったら、施設の指定期間のすべて患者のModuleModelを返す
//            long fPk = getFacilityPk(fid);
//            final String sql = "from ModuleModel m " +
//                    "where m.started between :fromDate and :toDate " +
//                    "and m.status='F' " +
//                    "and m.moduleInfo.entity in (:entities)" +
//                    "and m.creator.facility.id = :fPk";
//
//            ret = em.createQuery(sql)
//                    .setParameter("fromDate", fromDate)
//                    .setParameter("toDate", toDate)
//                    .setParameter("entities",entities)
//                    .setParameter("fPk", fPk)
//                    .getResultList();
//        }

        return ret;
    }

}
