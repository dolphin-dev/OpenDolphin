
package open.dolphin.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.exception.CanNotDeleteException;

import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

import open.dolphin.dto.AppointSpec;
import open.dolphin.dto.DiagnosisSearchSpec;
import open.dolphin.dto.DocumentSearchSpec;
import open.dolphin.dto.ImageSearchSpec;
import open.dolphin.dto.ModuleSearchSpec;
import open.dolphin.dto.ObservationSearchSpec;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.AppointmentModel;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
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

@Stateless
@SecurityDomain("openDolphin")
@RolesAllowed("user")
@Remote({RemoteKarteService.class})
@RemoteBinding(jndiBinding="openDolphin/RemoteKarteService")
public class RemoteKarteServiceImpl extends DolphinService implements RemoteKarteService {
    
    //@Resource
    //private SessionContext sctx;
    
    @PersistenceContext
    private EntityManager em;
    
    /**
     * カルテの基礎的な情報をまとめて返す。
     * @param patientPk 患者の Database Primary Key
     * @param fromDate 各種エントリの検索開始日
     * @return 基礎的な情報をフェッチした KarteBean
     */
    public KarteBean getKarte(long patientPk, Date fromDate) {
        
        try {
            // 最初に患者のカルテを取得する
            KarteBean karte = (KarteBean) em.createQuery("from KarteBean k where k.patient.id = :patientPk")
            .setParameter("patientPk", patientPk)
            .getSingleResult();
            
            // カルテの PK を得る
            long karteId = karte.getId();
            
            // アレルギーデータを取得する
            List observations = em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.observation='Allergy'")
            .setParameter("karteId", karteId)
            .getResultList();
            if (observations != null && observations.size() > 0) {
                List<AllergyModel> allergies = new ArrayList<AllergyModel>(observations.size());
                for (Iterator iter = observations.iterator(); iter.hasNext(); ) {
                    ObservationModel observation = (ObservationModel) iter.next();
                    AllergyModel allergy = new AllergyModel();
                    allergy.setObservationId(observation.getId());
                    allergy.setFactor(observation.getPhenomenon());
                    allergy.setSeverity(observation.getCategoryValue());
                    allergy.setIdentifiedDate(observation.confirmDateAsString());
                    allergies.add(allergy);
                }
                karte.addEntryCollection("allergy", allergies);
            }
            
            // 身長データを取得する
            observations = em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.observation='PhysicalExam' and o.phenomenon='bodyHeight'")
            .setParameter("karteId", karteId)
            .getResultList();
            if (observations != null && observations.size() > 0) {
                List<PhysicalModel> physicals = new ArrayList<PhysicalModel>(observations.size());
                for (Iterator iter = observations.iterator(); iter.hasNext(); ) {
                    ObservationModel observation = (ObservationModel) iter.next();
                    PhysicalModel physical = new PhysicalModel();
                    physical.setHeightId(observation.getId());
                    physical.setHeight(observation.getValue());
                    physical.setIdentifiedDate(observation.confirmDateAsString());
                    physical.setMemo(ModelUtils.getDateAsString(observation.getRecorded()));
                    physicals.add(physical);
                }
                karte.addEntryCollection("height", physicals);
            }
            
            // 体重データを取得する
            observations = em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.observation='PhysicalExam' and o.phenomenon='bodyWeight'")
            .setParameter("karteId", karteId)
            .getResultList();
            if (observations != null && observations.size() > 0) {
                List<PhysicalModel> physicals = new ArrayList<PhysicalModel>(observations.size());
                for (Iterator iter = observations.iterator(); iter.hasNext(); ) {
                    ObservationModel observation = (ObservationModel) iter.next();
                    PhysicalModel physical = new PhysicalModel();
                    physical.setWeightId(observation.getId());
                    physical.setWeight(observation.getValue());
                    physical.setIdentifiedDate(observation.confirmDateAsString());
                    physical.setMemo(ModelUtils.getDateAsString(observation.getRecorded()));
                    physicals.add(physical);
                }
                karte.addEntryCollection("weight", physicals);
            }
            
            // 直近の来院日エントリーを取得しカルテに設定する
            List latestVisits = em.createQuery("from PatientVisitModel p where p.patient.id = :patientPk and p.pvtDate >= :fromDate")
            .setParameter("patientPk", patientPk)
            .setParameter("fromDate", ModelUtils.getDateAsString(fromDate))
            .getResultList();
            
            if (latestVisits != null && latestVisits.size() > 0) {
                List<String> visits = new ArrayList<String>();
                for (Iterator iter=latestVisits.iterator(); iter.hasNext() ;) {
                    PatientVisitModel bean = (PatientVisitModel) iter.next();
                    visits.add(bean.getPvtDate());
                }
                karte.addEntryCollection("visit", visits);
            }
            
            // 文書履歴エントリーを取得しカルテに設定する
            List documents = em.createQuery("from DocumentModel d where d.karte.id = :karteId and d.started >= :fromDate and (d.status='F' or d.status='T')")
            .setParameter("karteId", karteId)
            .setParameter("fromDate", fromDate)
            .getResultList();
            
            if (documents != null && documents.size() > 0) {
                List<DocInfoModel> c = new ArrayList<DocInfoModel>();
                for (Iterator iter = documents.iterator(); iter.hasNext() ;) {
                    DocumentModel docBean = (DocumentModel) iter.next();
                    docBean.toDetuch();
                    c.add(docBean.getDocInfo());
                }
                karte.addEntryCollection("docInfo", c);
            }
            
            // 患者Memoを取得する
            List memo = em.createQuery("from PatientMemoModel p where p.karte.id = :karteId")
            .setParameter("karteId", karteId)
            .getResultList();
            if (memo != null && memo.size() >0) {
                karte.addEntryCollection("patientMemo", memo);
            }
            
            return karte;
            
        } catch (NoResultException e) {
            // 患者登録の際にカルテも生成してある
            e.printStackTrace();
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
    public List getDocumentList(DocumentSearchSpec spec) {
        
        List documents = null;
        
        if (spec.isIncludeModifid()) {
            documents = em.createQuery("from DocumentModel d where d.karte.id = :karteId and d.started >= :fromDate and d.status !='D'")
            .setParameter("karteId", spec.getKarteId())
            .setParameter("fromDate", spec.getFromDate())
            .getResultList();
        } else {
            documents = em.createQuery("from DocumentModel d where d.karte.id = :karteId and d.started >= :fromDate and (d.status='F' or d.status='T')")
            .setParameter("karteId", spec.getKarteId())
            .setParameter("fromDate", spec.getFromDate())
            .getResultList();
        }
        
        List<DocInfoModel> result = new ArrayList<DocInfoModel>();
        for (Iterator iter = documents.iterator(); iter.hasNext() ;) {
            DocumentModel docBean = (DocumentModel) iter.next();
            // モデルからDocInfo へ必要なデータを移す
            // クライアントが DocInfo だけを利用するケースがあるため
            docBean.toDetuch();
            result.add(docBean.getDocInfo());
        }
        return result;
    }
    
    /**
     * 文書(DocumentModel Object)を取得する。
     * @param ids DocumentModel の pkコレクション
     * @return DocumentModelのコレクション
     */
    @SuppressWarnings({ "unchecked", "unchecked" })
    public List<DocumentModel> getDocuments(List<Long> ids) {
        
        List<DocumentModel> ret = new ArrayList<DocumentModel>(3);
        
        // ループする
        for (Long id : ids) {
            
            // DocuentBean を取得する
            DocumentModel document = (DocumentModel) em.find(DocumentModel.class, id);
            
            // ModuleBean を取得する
            List modules = em.createQuery("from ModuleModel m where m.document.id = :id")
            .setParameter("id", id)
            .getResultList();
            document.setModules(modules);
            
            // SchemaModel を取得する
            List images = em.createQuery("from SchemaModel i where i.document.id = :id")
            .setParameter("id", id)
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
    public long addDocument(DocumentModel document) {
        
        // 永続化する
        em.persist(document);
        
        // ID
        long id = document.getId();
        
        // 修正版の処理を行う
        long parentPk = document.getDocInfo().getParentPk();
        
        if (parentPk != 0L) {
            
            // 適合終了日を新しい版の確定日にする
            Date ended = document.getConfirmed();
            
            // オリジナルを取得し 終了日と status = M を設定する
            DocumentModel old = (DocumentModel) em.find(DocumentModel.class, parentPk);
            old.setEnded(ended);
            old.setStatus(STATUS_MODIFIED);
            
            // 関連するモジュールとイメージに同じ処理を実行する
            Collection oldModules = em.createQuery("from ModuleModel m where m.document.id = :id")
            .setParameter("id", parentPk).getResultList();
            for (Iterator iter = oldModules.iterator(); iter.hasNext(); ) {
                ModuleModel model = (ModuleModel)iter.next();
                model.setEnded(ended);
                model.setStatus(STATUS_MODIFIED);
            }
            
            Collection oldImages = em.createQuery("from SchemaModel s where s.document.id = :id")
            .setParameter("id", parentPk).getResultList();
            for (Iterator iter = oldImages.iterator(); iter.hasNext(); ) {
                SchemaModel model = (SchemaModel)iter.next();
                model.setEnded(ended);
                model.setStatus(STATUS_MODIFIED);
            }
        }
        
        return id;
    }
    
    /**
     * ドキュメントを論理削除する。
     * @param pk 論理削除するドキュメントの primary key
     * @return 削除した件数
     */
    public int deleteDocument(long pk) {
        
        //
        // 対象 Document を取得する
        //
        Date ended = new Date();
        DocumentModel delete = (DocumentModel) em.find(DocumentModel.class, pk);
        
        //
        // 参照している場合は例外を投げる
        //
        if (delete.getLinkId() != 0L) {
            throw new CanNotDeleteException("他のドキュメントを参照しているため削除できません。");
        }
        
        //
        // 参照されている場合は例外を投げる
        //
        Collection refs = em.createQuery("from DocumentModel d where d.linkId=:pk")
        .setParameter("pk", pk).getResultList();
        if (refs != null && refs.size() >0) {
            CanNotDeleteException ce = new CanNotDeleteException("他のドキュメントから参照されているため削除できません。");
            throw ce;
        }
        
        //
        // 単独レコードなので削除フラグをたてる
        //
        delete.setStatus(STATUS_DELETE);
        delete.setEnded(ended);
        
        //
        // 関連するモジュールに同じ処理を行う
        //
        Collection deleteModules = em.createQuery("from ModuleModel m where m.document.id = :pk")
        .setParameter("pk", pk).getResultList();
        for (Iterator iter = deleteModules.iterator(); iter.hasNext(); ) {
            ModuleModel model = (ModuleModel) iter.next();
            model.setStatus(STATUS_DELETE);
            model.setEnded(ended);
        }
        
        //
        // 関連する画像に同じ処理を行う
        //
        Collection deleteImages = em.createQuery("from SchemaModel s where s.document.id = :pk")
        .setParameter("pk", pk).getResultList();
        for (Iterator iter = deleteImages.iterator(); iter.hasNext(); ) {
            SchemaModel model = (SchemaModel) iter.next();
            model.setStatus(STATUS_DELETE);
            model.setEnded(ended);
        }
        
        return 1;
    }
    
    /**
     * ドキュメントのタイトルを変更する。
     * @param pk 変更するドキュメントの primary key
     * @return 変更した件数
     */
    public int updateTitle(long pk, String title) {
        
        DocumentModel update = (DocumentModel) em.find(DocumentModel.class, pk);
        update.getDocInfo().setTitle(title);
        return 1;
    }
    
    /**
     * ModuleModelエントリを取得する。
     * @param spec モジュール検索仕様
     * @return ModuleModelリストのリスト
     */
    public List<List> getModules(ModuleSearchSpec spec) {
        
        // 抽出期間は別けられている
        Date[] fromDate = spec.getFromDate();
        Date[] toDate = spec.getToDate();
        int len = fromDate.length;
        List<List> ret = new ArrayList<List>(len);
        
        // 抽出期間セットの数だけ繰り返す
        for (int i = 0; i < len; i++) {
            
            List modules
                    = em.createQuery("from ModuleModel m where m.karte.id = :karteId and m.moduleInfo.entity = :entity and m.started between :fromDate and :toDate and m.status='F'")
                    .setParameter("karteId", spec.getKarteId())
                    .setParameter("entity", spec.getEntity())
                    .setParameter("fromDate", fromDate[i])
                    .setParameter("toDate", toDate[i])
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
    @SuppressWarnings("unchecked")
    public List<List> getImages(ImageSearchSpec spec) {
        
        // 抽出期間は別けられている
        Date[] fromDate = spec.getFromDate();
        Date[] toDate = spec.getToDate();
        int len = fromDate.length;
        List<List> ret = new ArrayList<List>(len);
        
        // 抽出期間セットの数だけ繰り返す
        for (int i = 0; i < len; i++) {
            
            List modules
                    = em.createQuery("from SchemaModel i where i.karte.id = :karteId and i.started between :fromDate and :toDate and i.status='F'")
                    .setParameter("karteId", spec.getKarteId())
                    .setParameter("fromDate", fromDate[i])
                    .setParameter("toDate", toDate[i])
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
    
    /**
     * 傷病名リストを取得する。
     * @param spec 検索仕様
     * @return 傷病名のリスト
     */
    @SuppressWarnings("unchecked")
    public List<RegisteredDiagnosisModel> getDiagnosis(DiagnosisSearchSpec spec) {
        
        List ret = null;
        
        // 疾患開始日を指定している
        if (spec.getFromDate() != null) {
            ret = em.createQuery("from RegisteredDiagnosisModel r where r.karte.id = :karteId and r.started >= :fromDate")
                    .setParameter("karteId", spec.getKarteId())
                    .setParameter("fromDate", spec.getFromDate())
                    .getResultList();
        } else {
            // 全期間の傷病名を得る
            ret = em.createQuery("from RegisteredDiagnosisModel r where r.karte.id = :karteId")
                    .setParameter("karteId", spec.getKarteId())
                    .getResultList();
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
    @SuppressWarnings("unchecked")
    public List<ObservationModel> getObservations(ObservationSearchSpec spec) {
        
        List ret = null;
        String observation = spec.getObservation();
        String phenomenon = spec.getPhenomenon();
        Date firstConfirmed = spec.getFirstConfirmed();
        
        if (observation != null) {
            if (firstConfirmed != null) {
                ret = em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.observation=:observation and o.started >= :firstConfirmed")
                .setParameter("karteId", spec.getKarteId())
                .setParameter("observation", observation)
                .setParameter("firstConfirmed", firstConfirmed)
                .getResultList();
                
            } else {
                ret = em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.observation=:observation")
                .setParameter("karteId", spec.getKarteId())
                .setParameter("observation", observation)
                .getResultList();
            }
        } else if (phenomenon != null) {
            if (firstConfirmed != null) {
                ret = em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.phenomenon=:phenomenon and o.started >= :firstConfirmed")
                .setParameter("karteId", spec.getKarteId())
                .setParameter("phenomenon", phenomenon)
                .setParameter("firstConfirmed", firstConfirmed)
                .getResultList();
            } else {
                ret = em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.phenomenon=:phenomenon")
                .setParameter("karteId", spec.getKarteId())
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
    
    /**
     * 予約を保存、更新、削除する。
     * @param spec 予約情報の DTO
     */
    public int putAppointments(AppointSpec spec) {
        
        int cnt = 0;
        
        Collection added = spec.getAdded();
        Collection updated = spec.getUpdared();
        Collection removed = spec.getRemoved();
        AppointmentModel bean = null;
        
        // 登録する
        if (added != null && added.size() > 0 ) {
            Iterator iter = added.iterator();
            while(iter.hasNext()) {
                bean = (AppointmentModel)iter.next();
                em.persist(bean);
                cnt++;
            }
        }
        
        // 更新する
        if (updated != null && updated.size() > 0 ) {
            Iterator iter = updated.iterator();
            while(iter.hasNext()) {
                bean = (AppointmentModel)iter.next();
                // av は分離オブジェクトである
                em.merge(bean);
                cnt++;
            }
        }
        
        // 削除
        if (removed != null && removed.size() > 0 ) {
            Iterator iter = removed.iterator();
            while(iter.hasNext()) {
                bean = (AppointmentModel)iter.next();
                // 分離オブジェクトは remove に渡せないので対象を検索する
                AppointmentModel target = (AppointmentModel)em.find(AppointmentModel.class, bean.getId());
                em.remove(target);
                cnt++;
            }
        }
        
        return cnt;
    }
    
    /**
     * 予約を検索する。
     * @param spec 検索仕様
     * @return 予約の Collection
     */
    public List<List> getAppointmentList(ModuleSearchSpec spec) {
        
        // 抽出期間は別けられている
        Date[] fromDate = spec.getFromDate();
        Date[] toDate = spec.getToDate();
        int len = fromDate.length;
        List<List> ret = new ArrayList<List>(len);
        
        // 抽出期間ごとに検索しコレクションに加える
        for (int i = 0; i < len; i++) {
            
            List c = em.createQuery("from AppointmentModel a where a.karte.id = :karteId and a.date between :fromDate and :toDate")
            .setParameter("karteId", spec.getKarteId())
            .setParameter("fromDate", fromDate[i])
            .setParameter("toDate", toDate[i])
            .getResultList();
            ret.add(c);
        }
        
        return ret;
    }
    
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
        
        if (docType.equals("TOUTOU")) {
            List<LetterModel> ret = (List<LetterModel>)
                        em.createQuery("from TouTouLetter f where f.karte.id = :karteId")
                        .setParameter("karteId", karteId)
                        .getResultList();
            return ret;
            
        } else if (docType.equals("TOUTOU_REPLY")) {
            List<LetterModel> ret = (List<LetterModel>)
                        em.createQuery("from TouTouReply f where f.karte.id = :karteId")
                        .setParameter("karteId", karteId)
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
                        em.createQuery("from TouTouLetter t where t.id = :id")
                        .setParameter("id", letterPk)
                        .getSingleResult();
        return ret;
    }
    
    public LetterModel getLetterReply(long letterPk) {
        
        LetterModel ret = (LetterModel)
                        em.createQuery("from TouTouReply t where t.id = :id")
                        .setParameter("id", letterPk)
                        .getSingleResult();
        return ret;
    }
}






















