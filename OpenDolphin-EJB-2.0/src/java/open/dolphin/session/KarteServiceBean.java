package open.dolphin.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.LetterModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.PhysicalModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SchemaModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Stateless
public class KarteServiceBean implements KarteServiceBeanLocal {

    //private static final String QUERY_PATIENT_BY_FIDPID = "from PatientModel p where p.facilityId=:fid and p.patientId=:pid";

    private static final String PATIENT_PK = "patientPk";
    private static final String KARTE_ID = "karteId";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";
    private static final String ID = "id";
    private static final String ENTITY = "entity";
    private static final String FID = "fid";

    private static final String QUERY_KARTE = "from KarteBean k where k.patient.id=:patientPk";
    private static final String QUERY_ALLERGY = "from ObservationModel o where o.karte.id=:karteId and o.observation='Allergy'";
    private static final String QUERY_BODY_HEIGHT = "from ObservationModel o where o.karte.id=:karteId and o.observation='PhysicalExam' and o.phenomenon='bodyHeight'";
    private static final String QUERY_BODY_WEIGHT = "from ObservationModel o where o.karte.id=:karteId and o.observation='PhysicalExam' and o.phenomenon='bodyWeight'";
    private static final String QUERY_PATIENT_VISIT = "from PatientVisitModel p where p.patient.id=:patientPk and p.pvtDate >= :fromDate";
    private static final String QUERY_DOC_INFO = "from DocumentModel d where d.karte.id=:karteId and d.started >= :fromDate and (d.status='F' or d.status='T')";
    private static final String QUERY_PATIENT_MEMO = "from PatientMemoModel p where p.karte.id=:karteId";

    private static final String QUERY_DOCUMENT_INCLUDE_MODIFIED = "from DocumentModel d where d.karte.id=:karteId and d.started >= :fromDate and d.status !='D'";
    private static final String QUERY_DOCUMENT = "from DocumentModel d where d.karte.id=:karteId and d.started >= :fromDate and (d.status='F' or d.status='T')";
    private static final String QUERY_DOCUMENT_BY_LINK_ID = "from DocumentModel d where d.linkId=:id";

    private static final String QUERY_MODULE_BY_DOC_ID = "from ModuleModel m where m.document.id=:id";
    private static final String QUERY_SCHEMA_BY_DOC_ID = "from SchemaModel i where i.document.id=:id";
    private static final String QUERY_MODULE_BY_ENTITY = "from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity=:entity and m.started between :fromDate and :toDate and m.status='F'";
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

    //private static final String QUERY_PVT_BY_ID = "from PatientVisitModel p where p.id=id";

    @PersistenceContext
    private EntityManager em;

    /**
     * カルテの基礎的な情報をまとめて返す。
     * @param patientPk 患者の Database Primary Key
     * @param fromDate 各種エントリの検索開始日
     * @return 基礎的な情報をフェッチした KarteBean
     */
    @Override
    public KarteBean getKarte(long patientPK, Date fromDate) {

        try {
            // 最初に患者のカルテを取得する
            KarteBean karte =
                    (KarteBean) em.createQuery(QUERY_KARTE)
                                  .setParameter(PATIENT_PK, patientPK)
                                  .getSingleResult();

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
    @Override
    public List<DocInfoModel> getDocumentList(long karteId, Date fromDate, boolean includeModifid) {

        List<DocumentModel> documents = null;

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
    @Override
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

            ret.add(document);
        }

        return ret;
    }

    /**
     * ドキュメント DocumentModel オブジェクトを保存する。
     * @param karteId カルテId
     * @param document 追加するDocumentModel オブジェクト
     * @return 追加した数
     */
    @Override
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

            Collection oldImages = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
            .setParameter(ID, parentPk).getResultList();
            for (Iterator iter = oldImages.iterator(); iter.hasNext(); ) {
                SchemaModel model = (SchemaModel)iter.next();
                model.setEnded(ended);
                model.setStatus(IInfoModel.STATUS_MODIFIED);
            }
        }

        return id;
    }


    @Override
    public long addDocumentAndUpdatePVTState(DocumentModel document, long pvtPK, int state) {

        // 永続化する
        em.persist(document);

        // PVT 更新  state==2 || state == 4
        PatientVisitModel exist = (PatientVisitModel) em.find(PatientVisitModel.class, new Long(pvtPK));
        exist.setState(state);

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

            Collection oldImages = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
            .setParameter(ID, parentPk).getResultList();
            for (Iterator iter = oldImages.iterator(); iter.hasNext(); ) {
                SchemaModel model = (SchemaModel)iter.next();
                model.setEnded(ended);
                model.setStatus(IInfoModel.STATUS_MODIFIED);
            }
        }

        return id;
    }

    /**
     * ドキュメントを論理削除する。
     * @param pk 論理削除するドキュメントの primary key
     * @return 削除した件数
     */
    @Override
    public int deleteDocument(long id) {

        //
        // 対象 Document を取得する
        //
        Date ended = new Date();
        DocumentModel delete = (DocumentModel) em.find(DocumentModel.class, id);

        //
        // 参照している場合は例外を投げる
        //
        if (delete.getLinkId() != 0L) {
            throw new CanNotDeleteException("他のドキュメントを参照しているため削除できません。");
        }

        //
        // 参照されている場合は例外を投げる
        //
        Collection refs = em.createQuery(QUERY_DOCUMENT_BY_LINK_ID)
        .setParameter(ID, id).getResultList();
        if (refs != null && refs.size() >0) {
            CanNotDeleteException ce = new CanNotDeleteException("他のドキュメントから参照されているため削除できません。");
            throw ce;
        }

        //
        // 単独レコードなので削除フラグをたてる
        //
        delete.setStatus(IInfoModel.STATUS_DELETE);
        delete.setEnded(ended);

        //
        // 関連するモジュールに同じ処理を行う
        //
        Collection deleteModules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
        .setParameter(ID, id).getResultList();
        for (Iterator iter = deleteModules.iterator(); iter.hasNext(); ) {
            ModuleModel model = (ModuleModel) iter.next();
            model.setStatus(IInfoModel.STATUS_DELETE);
            model.setEnded(ended);
        }

        //
        // 関連する画像に同じ処理を行う
        //
        Collection deleteImages = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
        .setParameter(ID, id).getResultList();
        for (Iterator iter = deleteImages.iterator(); iter.hasNext(); ) {
            SchemaModel model = (SchemaModel) iter.next();
            model.setStatus(IInfoModel.STATUS_DELETE);
            model.setEnded(ended);
        }

        return 1;
    }

    /**
     * ドキュメントのタイトルを変更する。
     * @param pk 変更するドキュメントの primary key
     * @return 変更した件数
     */
    @Override
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
    @Override
    public List<List> getModules(long karteId, String entity, List fromDate, List toDate) {

        // 抽出期間は別けられている
        int len = fromDate.size();
        List<List> ret = new ArrayList<List>(len);

        // 抽出期間セットの数だけ繰り返す
        for (int i = 0; i < len; i++) {

            List modules
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
    @Override
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
    @Override
    public SchemaModel getImage(long id) {
        SchemaModel image = (SchemaModel)em.find(SchemaModel.class, id);
        return image;
    }

    @Override
    public List<SchemaModel> getS3Images(String fid, int firstResult, int maxResult) {

        List<SchemaModel> ret = (List<SchemaModel>)
                                em.createQuery(QUERY_SCHEMA_BY_FACILITY_ID)
                                .setParameter(FID, fid+"%")
                                .setFirstResult(firstResult)
                                .setMaxResults(maxResult)
                                .getResultList();
        return ret;
    }

    @Override
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
    @Override
    public List<RegisteredDiagnosisModel> getDiagnosis(long karteId, Date fromDate, boolean activeOnly) {

        List<RegisteredDiagnosisModel> ret = null;

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
     * 傷病名を追加する。
     * @param addList 追加する傷病名のリスト
     * @return idのリスト
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public long saveOrUpdateLetter(LetterModel model) {
        LetterModel saveOrUpdate = em.merge(model);
        return saveOrUpdate.getId();
    }

    /**
     * 紹介状のリストを取得する。
     */
    @Override
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
    @Override
    public LetterModel getLetter(long letterPk) {

        LetterModel ret = (LetterModel)
                        em.createQuery(QUERY_LETTER_BY_ID)
                        .setParameter(ID, letterPk)
                        .getSingleResult();
        return ret;
    }

    @Override
    public LetterModel getLetterReply(long letterPk) {

        LetterModel ret = (LetterModel)
                        em.createQuery(QUERY_REPLY_BY_ID)
                        .setParameter(ID, letterPk)
                        .getSingleResult();
        return ret;
    }

    //--------------------------------------------------------------------------

    @Override
    public List<List> getAppointmentList(long karteId, List fromDate, List toDate) {

        // 抽出期間は別けられている
        int len = fromDate.size();
        List<List> ret = new ArrayList<List>(len);

        // 抽出期間セットの数だけ繰り返す
        for (int i = 0; i < len; i++) {

            List modules
                    = em.createQuery(QUERY_APPO_BY_KARTE_ID_PERIOD)
                    .setParameter(KARTE_ID, karteId)
                    .setParameter(FROM_DATE, fromDate.get(i))
                    .setParameter(TO_DATE, toDate.get(i))
                    .getResultList();

            ret.add(modules);
        }

        return ret;
    }
}
