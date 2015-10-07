/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.adm20.converter;

import open.dolphin.infomodel.DocInfoModel;

/**
 *
 * @author kazushi
 */
public class IFastDocInfo implements java.io.Serializable {
    
    // DocumentModel.id
    private long docPk;
    
    public long getDocPk() {
        return docPk;
    }

    public void setDocPk(long docPk) {
        this.docPk = docPk;
    }
    
    public void fromModel(DocInfoModel model) {           
        this.setDocPk(model.getDocPk());
    }
}
