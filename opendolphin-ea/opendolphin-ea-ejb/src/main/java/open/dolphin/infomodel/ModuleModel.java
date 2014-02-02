package open.dolphin.infomodel;

import javax.persistence.*;


/**
 * ModuleModel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_module")
public class ModuleModel extends KarteEntryBean 
        implements Stamp, java.io.Serializable, java.lang.Cloneable {
    
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ModuleModel ret = new ModuleModel();
        ret.setConfirmed(this.getConfirmed());
        ret.setEnded(this.getEnded());
        ret.setFirstConfirmed(this.getConfirmed());
        ret.setLinkId(this.getLinkId());
        ret.setLinkRelation(this.getLinkRelation());
        ret.setModuleInfoBean((ModuleInfoBean)this.getModuleInfoBean().clone());
        ret.setRecorded(this.getRecorded());
        ret.setStarted(this.getStarted());
        ret.setStatus(this.getStatus());

        byte[] bytes = this.getBeanBytes();
        if (bytes!=null) {
            byte[] dest = new byte[bytes.length];
            System.arraycopy(bytes, 0, dest, 0, bytes.length);
            ret.setBeanBytes(dest);
        }

        if (model!=null) {
            if (model instanceof BundleDolphin) {
                BundleDolphin m = (BundleDolphin)model;
                ret.setModel((BundleDolphin)m.clone());
            } else if (model instanceof BundleMed) {
                BundleMed m = (BundleMed)model;
                ret.setModel((BundleMed)m.clone());
            } else if (model instanceof ProgressCourse) {
                ProgressCourse m = (ProgressCourse)model;
                ret.setModel((ProgressCourse)m.clone());
            } else {
                throw new CloneNotSupportedException();
            }
        }

        // 下記は利用側で再設定する
        //ret.setKarteBean(this.getKarteBean());
        //ret.setUserModel(this.getUserModel());
        //ret.setDocumentModel(this.getDocumentModel());

        return ret;
    }
}
