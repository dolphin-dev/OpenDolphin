package open.dolphin.adm20.converter;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import open.dolphin.converter.IInfoModelConverter;
import open.dolphin.converter.PVTHealthInsuranceModelConverter;
import open.dolphin.converter.SimpleAddressModelConverter;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class IPatientModel implements IInfoModelConverter {

    private PatientModel model;
    
    private long kartePK;

    public IPatientModel() {
    }

    public long getId() {
        return model.getId();
    }
    
    public long getKartePK() {
        return kartePK;
    }
    
    public void setKartePK(long pk) {
        kartePK = pk;
    }

    public String getFacilityId() {
        return model.getFacilityId();
    }

    public String getPatientId() {
        return model.getPatientId();
    }

    public String getFullName() {
        return model.getFullName();
    }

    public String getKanaName() {
        return model.getKanaName();
    }

    public String getRomanName() {
        return model.getRomanName();
    }

    public String getGender() {
        return model.getGender();
    }

    public String getGenderDesc() {
        return model.getGenderDesc();
    }

    public String getBirthday() {
        return model.getBirthday();
    }

    public String getNationality() {
        return model.getNationality();
    }

    public String getNationalityDesc() {
        return model.getNationalityDesc();
    }

    public String getMaritalStatus() {
        return model.getMaritalStatus();
    }

    public byte[] getJpegPhoto() {
        return model.getJpegPhoto();
    }

    public String getMemo() {
        return model.getMemo();
    }

    public SimpleAddressModelConverter getSimpleAddressModel() {
        if (model.getSimpleAddressModel()!=null) {
            SimpleAddressModelConverter con = new SimpleAddressModelConverter();
            con.setModel(model.getSimpleAddressModel());
            return con;
        }
        return null;
    }

    public String getTelephone() {
        return model.getTelephone();
    }

    public String getMobilePhone() {
        return model.getMobilePhone();
    }

    public String getEmail() {
        return model.getEmail();
    }
    
//minagawa^ ios7 EHRTouchで新患検索用に追加    
    public String getFirstVisited() {
        return IOSHelper.toDateStr(model.getFirstVisited());
    }
//minagawa$
    
    public List<PVTHealthInsuranceModelConverter> getHealthInsurances() {
        
        List<HealthInsuranceModel> list = model.getHealthInsurances();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        // 差し替え
        List<PVTHealthInsuranceModelConverter> ret = new ArrayList<PVTHealthInsuranceModelConverter>();
        
        // HealthInsuranceModelをイテレートし
        for (HealthInsuranceModel hm : list) {
            // PVTHealthInsuranceに戻す
            PVTHealthInsuranceModel hModel = (PVTHealthInsuranceModel)xmlDecode(hm.getBeanBytes());
            // そのコンバーターを作成し
            PVTHealthInsuranceModelConverter conv = new PVTHealthInsuranceModelConverter();
            conv.setModel(hModel);
            // リターンリストに追加する
            ret.add(conv);
        }
   
        return ret;
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (PatientModel)m;
    }
    
    private Object xmlDecode(byte[] bytes)  {

        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                new ByteArrayInputStream(bytes)));

        return d.readObject();
    }
}
