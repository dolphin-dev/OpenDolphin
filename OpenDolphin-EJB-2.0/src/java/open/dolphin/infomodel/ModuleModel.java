package open.dolphin.infomodel;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * ModuleModel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_module")
public class ModuleModel extends KarteEntryBean implements Stamp, java.io.Serializable {
    
    @Embedded
    private ModuleInfoBean moduleInfo;
    
    @Transient
    private IInfoModel model;
    
    @Lob
    @Column(nullable=false)
    private byte[] beanBytes;
    
    @ManyToOne
    @JoinColumn(name="doc_id", nullable=false)
    private DocumentModel document;
    
    /**
     * ModuleModelオブジェクトを生成する。
     */
    public ModuleModel() {
        moduleInfo = new ModuleInfoBean();
    }
    
    public DocumentModel getDocumentModel() {
        return document;
    }
    
    public void setDocumentModel(DocumentModel document) {
        this.document = document;
    }
    
    /**
     * モジュール情報を設定する。
     * @param moduleInfo モジュール情報
     */
    public void setModuleInfoBean(ModuleInfoBean moduleInfo) {
        this.moduleInfo = moduleInfo;
    }
    
    /**
     * モジュール情報を返す。
     * @return モジュール情報
     */
    public ModuleInfoBean getModuleInfoBean() {
        return moduleInfo;
    }
    
    /**
     * モジュールの情報モデル（実体のPOJO)を設定する。
     * @param model モデル
     */
    public void setModel(IInfoModel model) {
        this.model = model;
    }
    
    /**
     * モジュールの情報モデル（実体のPOJO)を返す。
     * @return モデル
     */
    public IInfoModel getModel() {
        return model;
    }
    
    /**
     * モジュールの永続化バイト配列を返す。
     * @return モジュールの永続化バイト配列
     */
    public byte[] getBeanBytes() {
        return beanBytes;
    }
    
    /**
     * モジュールの永続化バイト配列を設定する。
     * @param beanBytes モジュールの永続化バイト配列
     */
    public void setBeanBytes(byte[] beanBytes) {
        this.beanBytes = beanBytes;
    }
    
    /**
     * ドキュメントに現れる順番で比較する。
     * @return 比較値
     */
    @Override
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            ModuleInfoBean moduleInfo1 = getModuleInfoBean();
            ModuleInfoBean moduleInfo2 = ((ModuleModel)other).getModuleInfoBean();
            return moduleInfo1.compareTo(moduleInfo2);
        }
        return -1;
    }
}
