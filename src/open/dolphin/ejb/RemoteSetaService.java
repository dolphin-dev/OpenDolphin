package open.dolphin.ejb;

import java.util.List;
import open.dolphin.infomodel.FirstEncounter0Model;
import open.dolphin.infomodel.FirstEncounterModel;
import open.dolphin.infomodel.LetterModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;

/**
 * 瀬田クリニックサービス。
 */
public interface RemoteSetaService {
    
    public long[] saveOrUpdateAsPvt(PatientVisitModel pvt, FirstEncounter0Model model);
    
    public long[] saveOrUpdatePatient(PatientModel patient, FirstEncounter0Model model);
    
    public long saveOrUpdateFirstEncounter(FirstEncounterModel model);
    
//    public long saveOrUpdateFirstEncounter(FirstEncounterModel model, List<CompositeImageModel> imageList);
    
    public List<FirstEncounterModel> getFirstEncounter(long karteId, String docType);
    
    public long saveOrUpdateLetter(LetterModel model);
    
    public List<LetterModel> getLetters(long karteId, String docType);
    
    public LetterModel getLetter(long letterPk);
    
}
