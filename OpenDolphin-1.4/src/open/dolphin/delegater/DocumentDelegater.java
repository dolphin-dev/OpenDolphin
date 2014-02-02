package open.dolphin.delegater;

import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.swing.ImageIcon;

import open.dolphin.client.ImageEntry;
import open.dolphin.dto.DiagnosisSearchSpec;
import open.dolphin.dto.DocumentSearchSpec;
import open.dolphin.dto.ImageSearchSpec;
import open.dolphin.dto.ModuleSearchSpec;
import open.dolphin.dto.ObservationSearchSpec;
import open.dolphin.ejb.RemoteKarteService;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.InfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.LetterModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.TouTouLetter;
import open.dolphin.infomodel.TouTouReply;
import open.dolphin.util.BeanUtils;

/**
 * Session と Document の送受信を行う Delegater クラス。
 *
 * @author Kazushi Minagawa
 *
 */
public class  DocumentDelegater extends BusinessDelegater {
    
    /**
     * 患者のカルテを取得する。
     * @param patientPk 患者PK
     * @param fromDate 履歴の検索開始日
     * @return カルテ
     */
    public KarteBean getKarte(long patientPk, Date fromDate) {
        
        KarteBean karte = null;
        
        try {
            karte = getService().getKarte(patientPk, fromDate);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return karte;
    }
    
    /**
     * Documentを保存する。
     * @param karteModel KarteModel
     * @return Result Code
     */
    public long putKarte(DocumentModel karteModel) {
        
        long retCode = 0;
        
        try {
            // 確定日、適合開始日、記録日、ステータスを
            // DocInfo から DocumentModel(KarteEntry) に移す
            karteModel.toPersist();
            
            // 保存する
            retCode = getService().addDocument(karteModel);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return retCode;
    }
    
    /**
     * Documentを検索して返す。
     * @param id DocumentID
     * @return DocumentValue
     */
    public List<DocumentModel> getDocuments(List<Long> ids) {
        
        List<DocumentModel> ret = null;
        
        // 検索する
        try {
            ret = getService().getDocuments(ids);
            for (DocumentModel doc : ret) {
                
                // Module byte をオブジェクトへ戻す
                Collection<ModuleModel> mc = doc.getModules();
                for (ModuleModel module : mc) {
                    module.setModel((InfoModel) BeanUtils.xmlDecode(module.getBeanBytes()));
                    //module.toDetuch();
                }
                
                // JPEG byte をアイコンへ戻す
                Collection<SchemaModel> sc = doc.getSchema();
                for (SchemaModel schema : sc) {
                    ImageIcon icon = new ImageIcon(schema.getJpegByte());
                    schema.setIcon(icon);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    /**
     * 文書履歴を検索して返す。
     * @param spec DocumentSearchSpec 検索仕様
     * @return DocInfoModel の Collection
     */
    public List getDocumentList(DocumentSearchSpec spec) {
        
        if (spec.getDocType().equals(IInfoModel.DOCTYPE_KARTE)) {
            return getKarteList(spec);
            
        } else if (spec.getDocType().equals(IInfoModel.DOCTYPE_LETTER)) {
            return getLetterList(spec);
        
        } else if (spec.getDocType().equals(IInfoModel.DOCTYPE_LETTER_REPLY)) {
            return getLetterReplyList(spec);
            
        } else if (spec.getDocType().equals(IInfoModel.DOCTYPE_LETTER_REPLY2)) {
            return getLetterReplyList(spec);
        }
        
        return null;
    }
    
    private List getKarteList(DocumentSearchSpec spec) {
        
        List ret= null;
        
        try {
            ret = getService().getDocumentList(spec);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    private List<DocInfoModel> getLetterList(DocumentSearchSpec spec) {
        
        List<DocInfoModel> ret = new ArrayList<DocInfoModel>(1);
        
        try {
            List<LetterModel> result = (List<LetterModel>) getService().getLetterList(spec.getKarteId(), "TOUTOU");
            for (LetterModel model : result) {
                TouTouLetter letter = (TouTouLetter) model;
                DocInfoModel docInfo = new DocInfoModel();
                docInfo.setDocPk(letter.getId());
                docInfo.setDocType(IInfoModel.DOCTYPE_LETTER);
                docInfo.setDocId(String.valueOf(letter.getId()));
                docInfo.setConfirmDate(letter.getConfirmed());
                docInfo.setFirstConfirmDate(letter.getConfirmed());
                docInfo.setTitle(letter.getConsultantHospital());

                ret.add(docInfo);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    private List<DocInfoModel> getLetterReplyList(DocumentSearchSpec spec) {
        
        List<DocInfoModel> ret = new ArrayList<DocInfoModel>(1);
        
        try {
            List<LetterModel> result = (List<LetterModel>) getService().getLetterList(spec.getKarteId(), "TOUTOU_REPLY");
            
            for (LetterModel model : result) {
                TouTouReply letter = (TouTouReply) model;
                DocInfoModel docInfo = new DocInfoModel();
                docInfo.setDocPk(letter.getId());
                if (spec.getDocType().equals(IInfoModel.DOCTYPE_LETTER_REPLY)) {
                    docInfo.setDocType(IInfoModel.DOCTYPE_LETTER_REPLY);
                } else {
                    docInfo.setDocType(IInfoModel.DOCTYPE_LETTER_REPLY2);
                }
                docInfo.setDocId(String.valueOf(letter.getId()));
                docInfo.setConfirmDate(letter.getConfirmed());
                docInfo.setFirstConfirmDate(letter.getConfirmed());
                docInfo.setTitle(letter.getClientHospital());

                ret.add(docInfo);
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    public LetterModel getLetter(long letterPk) {
        
        LetterModel ret = null;
        
        try {
            LetterModel result = getService().getLetter(letterPk);
            byte[] bytes = result.getBeanBytes();
            System.out.println("get letter bean bytes");
            ret = (LetterModel) BeanUtils.xmlDecode(bytes);
            System.out.println("decoded letter bean bytes");
            ret.setId(result.getId());
            ret.setBeanBytes(null);
             
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    public LetterModel getLetterReply(long letterPk) {
        
        LetterModel ret = null;
        
        try {
            LetterModel result = getService().getLetterReply(letterPk);
            byte[] bytes = result.getBeanBytes();
            ret = (LetterModel) BeanUtils.xmlDecode(bytes);
            ret.setId(result.getId());
            ret.setBeanBytes(null);
             
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    /**
     * ドキュメントを論理削除する。
     * @param pk 論理削除するドキュメントの prmary key
     * @return 削除件数
     */
    public int deleteDocument(long pk) {
        
        try {
            return getService().deleteDocument(pk);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0;
    }
    
    /**
     * 文書履歴のタイトルを変更する。
     * @param pk Document の pk
     * @return 変更した件数
     */
    public int updateTitle(DocInfoModel docInfo) {
        
        try {
            return getService().updateTitle(docInfo.getDocPk(), docInfo.getTitle());
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0;
    }
    
    
    /**
     * Moduleを検索して返す。
     * @param spec ModuleSearchSpec 検索仕様
     * @return Module の Collection
     */
    public List<List> getModuleList(ModuleSearchSpec spec) {
        
        List<List> ret= null;
        
        try {
            ret = getService().getModules(spec);
            for (List list : ret) {
                for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                    ModuleModel module = (ModuleModel)iter.next();
                    module.setModel((InfoModel)BeanUtils.xmlDecode(module.getBeanBytes()));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    /**
     * イメージを取得する。
     * @param id 画像のId
     * @return SchemaModel
     */
    public SchemaModel getImage(long id) {
        
        SchemaModel model = null;
        
        try {
            model = getService().getImage(id);
            
            if (model != null) {
                byte[] bytes = model.getJpegByte();
                ImageIcon icon = new ImageIcon(bytes);
                if (icon != null) {
                    model.setIcon(icon);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return model;
    }
    
    /**
     * Imageを検索して返す。
     * @param spec ImageSearchSpec 検索仕様
     * @return Imageリストのリスト
     */
    public List<List> getImageList(ImageSearchSpec spec) {
        
//        Logger logger = ClientContext.getLogger("boot");
//        logger.debug("search code = " + spec.getCode());
//        logger.debug("karte id = " + spec.getKarteId());
//        Date[] from = spec.getFromDate();
//        Date[] to = spec.getToDate();
//        for (int i = 0; i < from.length; i++) {
//            logger.debug(from[i] + " ~ " + to[i]);
//        }
        
        List<List> ret= new ArrayList<List>(3);
        
        try {
            // 検索結果
            List result = getService().getImages(spec);
            //logger.debug("got result, count = " + result.size());
            
            //System.out.println("got image list");
            
            for (Iterator iter = result.iterator(); iter.hasNext(); ) {
                
                // 抽出期間毎のリスト
                List periodList = (List)iter.next();
                
                // ImageEntry 用のリスト
                List<ImageEntry> el = new ArrayList<ImageEntry>();
                
                // 抽出期間をイテレートする
                for (Iterator iter2 = periodList.iterator(); iter2.hasNext(); ) {
                    // シェーマモデルをエントリに変換しリストに加える
                    SchemaModel model = (SchemaModel)iter2.next();
                    ImageEntry entry = getImageEntry(model, spec.getIconSize());
                    el.add(entry);
                }
                
                // リターンリストへ追加する
                ret.add(el);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    /**
     * シェーマモデルをエントリに変換する。
     * @param schema シェーマモデル
     * @param iconSize アイコンのサイズ
     * @return ImageEntry
     */
    private ImageEntry getImageEntry(SchemaModel schema, Dimension iconSize) {
        
        ImageEntry model = new ImageEntry();
        
        model.setId(schema.getId());
        //model.setConfirmDate(ModelUtils.getDateTimeAsString(schema.getConfirmDate()));  // First?
        model.setConfirmDate(ModelUtils.getDateTimeAsString(schema.getConfirmed()));  // First?
        model.setContentType(schema.getExtRef().getContentType());
        model.setTitle(schema.getExtRef().getTitle());
        model.setMedicalRole(schema.getExtRef().getMedicalRole());
        
        byte[] bytes = schema.getJpegByte();
        
        // Create ImageIcon
        ImageIcon icon = new ImageIcon(bytes);
        if (icon != null) {
            model.setImageIcon(adjustImageSize(icon, iconSize));
        }
        
        return model;
    }
    
    public List<Long> putDiagnosis(List<RegisteredDiagnosisModel> beans) {
        
        List<Long> ret = null;
        
        try {
            ret = getService().addDiagnosis(beans);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    public int updateDiagnosis(List<RegisteredDiagnosisModel> beans) {
        
        int retCode = 0;
        
        try {
            retCode = getService().updateDiagnosis(beans);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return retCode;
    }
    
    public int removeDiagnosis(List<Long> ids) {
        
        int retCode = 0;
        
        try {
            retCode = getService().removeDiagnosis(ids);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return retCode;
    }
    
    /**
     * Diagnosisを検索して返す。
     * @param spec DiagnosisSearchSpec 検索仕様
     * @return DiagnosisModel の Collection
     */
    public List<RegisteredDiagnosisModel> getDiagnosisList(DiagnosisSearchSpec spec) {
        
        List<RegisteredDiagnosisModel> ret= null;
        
        try {
            ret = getService().getDiagnosis(spec);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    public List<Long> addObservations(List<ObservationModel> observations) {
        
        try {
            return getService().addObservations(observations);
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        return null;
    }
    
    
    public List<ObservationModel> getObservations(ObservationSearchSpec spec) {
        
        try {
            return getService().getObservations(spec);
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        return null;
    }
    
    public int updateObservations(List<ObservationModel> observations) {
        
        try {
            return getService().updateObservations(observations);
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        return 0;
    }
    
    public int removeObservations(List<Long> ids) {
        
        try {
            return getService().removeObservations(ids);
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        return 0;
    }
    
    public int updatePatientMemo(PatientMemoModel pm) {
        
        try {
            return getService().updatePatientMemo(pm);
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        return 0;
    }
    
    public List getAppoinmentList(ModuleSearchSpec spec) {
        
        List ret = null;
        
        try {
            ret = getService().getAppointmentList(spec);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    private RemoteKarteService getService() throws NamingException {
        return (RemoteKarteService) getService("RemoteKarteService");
    }
    
    private ImageIcon adjustImageSize(ImageIcon icon, Dimension dim) {
        
        if ( (icon.getIconHeight() > dim.height) ||
                (icon.getIconWidth() > dim.width) ) {
            Image img = icon.getImage();
            float hRatio = (float)icon.getIconHeight() / dim.height;
            float wRatio = (float)icon.getIconWidth() / dim.width;
            int h, w;
            
            if (hRatio > wRatio) {
                h = dim.height;
                w = (int)(icon.getIconWidth() / hRatio);
                
            } else {
                w = dim.width;
                h = (int)(icon.getIconHeight() / wRatio);
            }
            
            img = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
            
        } else {
            return icon;
        }
    }
}




















