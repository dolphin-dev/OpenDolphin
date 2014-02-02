/*
 * PVTDelegater.java
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

import java.util.ArrayList;
import java.util.Collection;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import open.dolphin.dto.PatientVisitSpec;
import open.dolphin.ejb.RemotePvtService;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.util.BeanUtils;

/**
 * User 関連の Business Delegater　クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class PVTDelegater extends BusinessDelegater {
    
    private Logger logger;
    
    public void setLogger(Logger l) {
        logger = l;
    }
    
    /**
     * 受付情報 PatientVisitModel をデータベースに登録する。
     * @param pvtModel   受付情報 PatientVisitModel
     * @param principal  UserId と FacilityId
     * @return 保存に成功した個数
     */
    public int addPvt(PatientVisitModel pvtModel) {
        
        int retCode = 0;
        
        try {
            retCode = getService().addPvt(pvtModel);
            logger.info("受付情報を保存しました");
            
        } catch (Exception e) {
            logger.info("受付情報の保存に失敗しました");
            logger.warn(e.toString());
            e.printStackTrace();
            processError(e);
        }
        
        return retCode;
    }
    
    /**
     * 来院情報をデータベースから取得する。
     * @param date     検索する来院日
     * @param firstRecord 何番目のレコードから取得するか
     * @return PatientVisitModel のコレクション
     */
    @SuppressWarnings("unchecked")
    public Collection<PatientVisitModel> getPvt(String[] date, int firstRecord) {
        
        PatientVisitSpec spec = new PatientVisitSpec();
        spec.setDate(date[0]);
        spec.setAppodateFrom(date[1]);
        spec.setAppodateTo(date[2]);
        spec.setSkipCount(firstRecord);
        
        try {
            Collection<PatientVisitModel> ret = getService().getPvt(spec);
            
            for (PatientVisitModel model : ret) {
                PatientModel patient = model.getPatient();
                decodeHealthInsurance(patient);
            }
            
            return ret;
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return null;
    }
    
    
    /**
     * バイナリの健康保険データをオブジェクトにデコードする。
     * @param patient 患者モデル
     */
    private void decodeHealthInsurance(PatientModel patient) {
        
        // Health Insurance を変換をする beanXML2PVT
        Collection<HealthInsuranceModel> c = patient.getHealthInsurances();
        
        if (c != null) {
            
            ArrayList<PVTHealthInsuranceModel> list = new ArrayList<PVTHealthInsuranceModel>(c.size());
            
            for (HealthInsuranceModel model : c) {
                try {
                    // byte[] を XMLDecord
                    PVTHealthInsuranceModel hModel = (PVTHealthInsuranceModel)BeanUtils.xmlDecode(model.getBeanBytes());
                    list.add(hModel);
                } catch (Exception e) {
                    e.printStackTrace();
                    processError(e);
                }
            }
            
            patient.setPvtHealthInsurances(list);
            patient.getHealthInsurances().clear();
            patient.setHealthInsurances(null);
        }
    }
    
    public int removePvt(long id) {
        try {
            return getService().removePvt(id);
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        return 0;
    }
    
    public int updatePvtState(long pk, int state) {
        try {
            return getService().updatePvtState(pk, state);
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        return 0;
    }
    
    private RemotePvtService getService() throws NamingException {
        return (RemotePvtService)getService("RemotePvtService");
    }
}
