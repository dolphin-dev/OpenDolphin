/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.session;

import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.LetterModel;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SchemaModel;

/**
 *
 * @author kazushi
 */
@Local
public interface KarteServiceBeanLocal {

    public KarteBean getKarte(long patientPk, Date fromDate);


    public List<DocInfoModel> getDocumentList(long karteId, Date fromDate, boolean includeModifid);

    public List<DocumentModel> getDocuments(List<java.lang.Long> ids);

    public long addDocument(DocumentModel document);

    public int deleteDocument(long pk);

    public int updateTitle(long pk, String title);


    public List<List> getModules(long karteId, String entity, List fromDate, List toDate);

    public List<List> getImages(long karteId, List fromDate, List toDate);

    public SchemaModel getImage(long id);


    public List<RegisteredDiagnosisModel> getDiagnosis(long karteId, Date fromDate, boolean activeOnly);
    
    public List<Long> addDiagnosis(List<RegisteredDiagnosisModel> addList);

    public int updateDiagnosis(List<RegisteredDiagnosisModel> updateList);

    public int removeDiagnosis(List<Long> removeList);


    public List<ObservationModel> getObservations(long karteId, String observation, String phenomenon, Date firstConfirmed);

    public List<Long> addObservations(List<ObservationModel> observations);

    public int updateObservations(List<ObservationModel> observations);

    public int removeObservations(List<Long> observations);


    public int updatePatientMemo(PatientMemoModel memo);

    
    public long saveOrUpdateLetter(LetterModel model);

    public List<LetterModel> getLetterList(long karteId, String docType);

    public LetterModel getLetter(long letterPk);

    public LetterModel getLetterReply(long letterPk);

    public List<List> getAppointmentList(long karteId, List fromDate, List toDate);

    public java.util.List<open.dolphin.infomodel.SchemaModel> getS3Images(java.lang.String fid, int firstResult, int maxResult);

    public void deleteS3Image(long pk);

    public long addDocumentAndUpdatePVTState(open.dolphin.infomodel.DocumentModel document, long pvtPK, int state);
}
