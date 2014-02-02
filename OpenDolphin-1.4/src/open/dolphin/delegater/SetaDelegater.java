package open.dolphin.delegater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.naming.NamingException;
import open.dolphin.ejb.RemoteSetaService;
import open.dolphin.infomodel.CompositeImageModel;
import open.dolphin.infomodel.FirstEncounter0Model;
import open.dolphin.infomodel.FirstEncounter2Model;
import open.dolphin.infomodel.FirstEncounterModel;
import open.dolphin.infomodel.LetterModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.util.BeanUtils;

/**
 * 瀬田クリニックサービス。
 */
public class SetaDelegater extends BusinessDelegater {
    
    /** Creates a new instance of SetaDelegater */
    public SetaDelegater() {
    }
    
    private RemoteSetaService getService() throws NamingException {
        return (RemoteSetaService) getService("RemoteSetaService");
    }
    
    public Object[] saveOrUpdateAsPvt(PatientVisitModel pvt, FirstEncounter0Model model) {
        
        Object[] ret = null;
        
        try {
            byte[] beanBytes = BeanUtils.xmlEncode(model);
            model.setBeanBytes(beanBytes);
            ret = getService().saveOrUpdateAsPvt(pvt, model);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    public Object[] saveOrUpdatePatient(PatientModel patient, FirstEncounter0Model model) {
        
        Object[] ret = null;
        
        try {
            byte[] beanBytes = BeanUtils.xmlEncode(model);
            model.setBeanBytes(beanBytes);
            ret = getService().saveOrUpdatePatient(patient, model);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    public long saveOrUpdateFirstEncounter(FirstEncounterModel model) {
        
        long ret = 0L;
        
        try {
            byte[] beanBytes = BeanUtils.xmlEncode(model);
            model.setBeanBytes(beanBytes);
            ret = getService().saveOrUpdateFirstEncounter(model);
            return ret;
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0L;
    }
    
    public long saveOrUpdateFirstEncounter(FirstEncounter2Model model, List<CompositeImageModel> imageList) {
        
        long ret = 0L;
        
        try {
            byte[] beanBytes = BeanUtils.xmlEncode(model);
            model.setBeanBytes(beanBytes);
            model.setCompositeImages(imageList);
            ret = getService().saveOrUpdateFirstEncounter((FirstEncounterModel) model);
            return ret;
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0L;
    }
    
    public List<FirstEncounterModel> getFirstEncounter(long karteId, String docType) {
        
        List ret = new ArrayList<FirstEncounterModel>(1);
        
        try {
            List<FirstEncounterModel> result = getService().getFirstEncounter(karteId, docType);
            
            for (FirstEncounterModel model : result) {
                byte[] bytes = model.getBeanBytes();
                FirstEncounterModel f1 = (FirstEncounterModel) BeanUtils.xmlDecode(bytes);
                f1.setId(model.getId());
                f1.setBeanBytes(null);
                
                if (f1 instanceof FirstEncounter2Model) {
                    Collection<CompositeImageModel> c = ((FirstEncounter2Model)model).getCompositeImages();
                    ((FirstEncounter2Model)f1).setCompositeImages(c);
                    
                }
                ret.add(f1);
            }
             
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
        
    }
    
    public long saveOrUpdateLetter(LetterModel model) {
        
        long ret = 0L;
        
        try {
            byte[] beanBytes = BeanUtils.xmlEncode(model);
            model.setBeanBytes(beanBytes);
            ret = getService().saveOrUpdateLetter(model);
            return ret;
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0L;
    }
    
    public String getNextPatientId() {
        
        String ret = null;
        
        try {
            
            ret = getService().getNextPatientId();
            
        }catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
}
