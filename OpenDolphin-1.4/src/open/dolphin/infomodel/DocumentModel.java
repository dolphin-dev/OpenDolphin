package open.dolphin.infomodel;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * DocumentBean
 *
 * @author Minagawa,Kazushi
 *
 */
@Entity
@Table(name = "d_document")
public class DocumentModel extends KarteEntryBean {
    
    private static final long serialVersionUID = 8273677751373923433L;
    
    @Embedded
    private DocInfoModel docInfo;
    
    @OneToMany(mappedBy="document", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private Collection<ModuleModel> modules;
    
    @OneToMany(mappedBy="document", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    //@Column(name="c_schema")
    private Collection<SchemaModel> schema;

    
    /**
     * DocumentModelを生成する。
     */
    public DocumentModel() {
        setDocInfo(new DocInfoModel());
        getDocInfo().setDocType(DOCTYPE_KARTE);
    }
    
    public void toDetuch() {
        docInfo.setDocPk(getId());
        docInfo.setParentPk(getLinkId());
        docInfo.setConfirmDate(getConfirmed());
        docInfo.setFirstConfirmDate(getStarted());
        docInfo.setStatus(getStatus());
    }
    
    public void toPersist() {
        setLinkId(docInfo.getParentPk());
        setLinkRelation(docInfo.getParentIdRelation());
        setConfirmed(docInfo.getConfirmDate());
        setFirstConfirmed(docInfo.getFirstConfirmDate());
        setStatus(docInfo.getStatus());
    }
    
    /**
     * 文書情報を返す。
     * @return 文書情報
     */
    public DocInfoModel getDocInfo() {
        return docInfo;
    }
    
    /**
     * 文書情報を設定する。
     * @param docInfo 文書情報
     */
    public void setDocInfo(DocInfoModel docInfo) {
        this.docInfo = docInfo;
    }
    
    /**
     * シェーマを返す。
     * @return シェーマ
     */
    public Collection<SchemaModel> getSchema() {
        return schema;
    }
    
    /**
     * シェーマを設定する。
     * @param images シェーマ
     */
    public void setSchema(Collection<SchemaModel> images) {
        this.schema = images;
    }
    
    /**
     * シェーマを追加する。
     * @param model シェーマ
     */
    public void addSchema(SchemaModel model) {
        if (this.schema == null) {
            this.schema = new ArrayList<SchemaModel>();
        }
        this.schema.add(model);
    }
    
    /**
     * シェーマコレクションをクリアする。
     */
    public void clearSchema() {
        if (schema != null && schema.size() > 0) {
            schema.clear();
        }
    }
    
    
    public SchemaModel getSchema(int index) {
        if (schema != null && schema.size() > 0) {
            int cnt = 0;
            for (SchemaModel bean : schema) {
                if (index == cnt) {
                    return bean;
                }
                cnt++;
            }
        }
        return null;
    }
    
    /**
     * モジュールを返す。
     * @return モジュール
     */
    public Collection<ModuleModel> getModules() {
        return modules;
    }
    
    /**
     * モジュールを設定する。
     * @param modules モジュール
     */
    public void setModules(Collection<ModuleModel> modules) {
        this.modules = modules;
    }
    
    /**
     * モジュールモデルの配列を追加する。
     * @param moules モジュールモデルの配列
     */
    public void addModule(ModuleModel[] addArray) {
        if (modules == null) {
            modules = new ArrayList<ModuleModel>(addArray.length);
        }
        for (ModuleModel bean : addArray) {
            modules.add(bean);
        }
    }
    
    /**
     * モジュールモデルを追加する。
     * @param value モジュールモデル
     */
    public void addModule(ModuleModel addModule) {
        if (modules == null) {
            modules = new ArrayList<ModuleModel>();
        }
        modules.add(addModule);
    }
    
    /**
     * モジュールをクリアする。
     */
    public void clearModules() {
        if (modules != null && modules.size() > 0) {
            modules.clear();
        }
    }
    
    /**
     * 引数のエンティティを持つモジュールモデルを返す。
     * @param entityName エンティティの名前
     * @return 該当するモジュールモデル
     */
    public ModuleModel getModule(String entityName) {
        
        if (modules != null) {
            
            ModuleModel ret = null;
            
            for (ModuleModel model : modules) {
                if (model.getModuleInfo().getEntity().equals(entityName)) {
                    ret = model;
                    break;
                }
            }
            return ret;
        }
        
        return null;
    }
    
    /**
     * 引数のエンティティ名を持つモジュール情報を返す。
     * @param entityName エンティティの名前
     * @return モジュール情報
     */
    public ModuleInfoBean[] getModuleInfo(String entityName) {
        
        if (modules != null) {
            
            ArrayList<ModuleInfoBean> list = new ArrayList<ModuleInfoBean>(2);
            
            for (ModuleModel model : modules) {
                
                if (model.getModuleInfo().getEntity().equals(entityName)) {
                    list.add(model.getModuleInfo());
                }
            }
            
            if (list.size() > 0) {
                return  (ModuleInfoBean[])list.toArray(new ModuleInfoBean[list.size()]);
            }
        }
        
        return null;
    }    
}
