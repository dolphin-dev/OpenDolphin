/*
 * PatientDelegater.java
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.delegater;

import java.util.Collection;
import java.util.Iterator;

import javax.naming.NamingException;

import open.dolphin.dto.PatientSearchSpec;
import open.dolphin.ejb.RemotePatientService;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.util.BeanUtils;

/**
 * 患者関連の Business Delegater　クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class  PatientDelegater extends BusinessDelegater {
    
    /**
     * 患者情報を保存する。
     * @param patientValue PatientValue
     * @return Result Code
     */
    public long putPatient(PatientModel patientValue) {
        
        try {
            return getService().addPatient(patientValue);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0L;
    }
    
    /**
     * 患者情報を検索して返す。
     * @param id 患者ID
     * @return PatientValue
     */
    public PatientModel getPatient(String qId) {
        
        PatientModel ret = null;
        
        try {
            ret = getService().getPatient(qId);
            decodeHealthInsurance(ret);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    /**
     * 患者情報を検索して返す。
     * @param spec PatientSearchSpec 検索仕様
     * @return PatientValue の Collection
     */
    public Collection getPatients(PatientSearchSpec spec) {
        
        Collection ret= null;
        
        try {
            ret = getService().getPatients(spec);
            for (Iterator iter=ret.iterator(); iter.hasNext(); ) {
                PatientModel patient = (PatientModel)iter.next();
                decodeHealthInsurance(patient);
            }
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    /**
     * バイナリの健康保険データをオブジェクトにデコードする。
     * @param patient 患者モデル
     */
    private void decodeHealthInsurance(PatientModel patient) {
        
        // Health Insurance を変換をする beanXML2PVT
        Collection<HealthInsuranceModel> c = patient.getHealthInsurances();
        
        if (c != null) {
            
            for (HealthInsuranceModel model : c) {
                try {
                    // byte[] を XMLDecord
                    PVTHealthInsuranceModel hModel = (PVTHealthInsuranceModel)BeanUtils.xmlDecode(model.getBeanBytes());
                    patient.addPvtHealthInsurance(hModel);
                } catch (Exception e) {
                    e.printStackTrace();
                    processError(e);
                }
            }
            
            c.clear();
            patient.setHealthInsurances(null);
            
        }
    }
    
    /**
     * 患者メモを更新する。
     * @param patient 患者
     * @return
     */
    public int updatePatient(PatientModel patient) {
        
        int retCode = -1;
        
        try {
            retCode = getService().update(patient);
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return retCode;
    }
    
    private RemotePatientService getService() throws NamingException {
        return (RemotePatientService) getService("RemotePatientService");
    }
}
