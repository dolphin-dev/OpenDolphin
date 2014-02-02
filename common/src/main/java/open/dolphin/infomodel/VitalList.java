/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.infomodel;

import java.util.List;

/**
 * バイタル対応
 * 
 * @author Life Sciences Computing Corporation.
 */
public class VitalList  extends InfoModel implements java.io.Serializable {
    
    private List<VitalModel> list;

    public List<VitalModel> getList() {
        return list;
    }

    public void setList(List<VitalModel> list) {
        this.list = list;
    }
}
