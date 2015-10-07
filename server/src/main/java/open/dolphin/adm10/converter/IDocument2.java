package open.dolphin.adm10.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.*;

/**
 * IDocument2
 * 
 * 2014/02/06 iPadのFreeText対応
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class IDocument2 implements java.io.Serializable {
    
    // PK
    private long id;
    
    // 確定日時 Date
    private String confirmed;
    
    // 記録の有効開始日時(最初に確定した日）Date
    private String started;
    
    // 記録の終了日時（有効ではなくなった日）Date
    private String ended;
    
    // 記録日時 Date
    private String recorded;
    
    // 親エントリーの PK
    private long linkId;
    
    // 親エントリーとの関係
    private String linkRelation;
    
    // エントリーのステータス(Final,Modifyed等）
    private String status;
    
    // 記録責任者（システムの利用者）
    private UserModel userModel;
    
    private IUserModel iuser;
    
    // カルテへの外部参照
    private KarteBean karteBean;
    
    // docInfo 変換
    private IDocInfo docInfo;
    
    // SOA ProgressCourse 変換
    private IProgressCourseModule2 soaProgressCourse;
    
    // P ProgressCourse 変換
    private IProgressCourseModule2 ptextProgressCourse;  // 20131206

    // SchemaModel 変換
    private List<ISchemaModel> schema;
    
    // JClaimBundle
    private List<IBundleModule> bundles;
    
    private List<IAttachmentModel> attachments;
    
    public IDocument2() {
        docInfo = new IDocInfo();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getEnded() {
        return ended;
    }

    public void setEnded(String ended) {
        this.ended = ended;
    }

    public String getRecorded() {
        return recorded;
    }

    public void setRecorded(String recorded) {
        this.recorded = recorded;
    }

    public long getLinkId() {
        return linkId;
    }

    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }

    public String getLinkRelation() {
        return linkRelation;
    }

    public void setLinkRelation(String linkRelation) {
        this.linkRelation = linkRelation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
    
    public IUserModel getIuser() {
        return iuser;
    }

    public void setIuser(IUserModel iuser) {
        this.iuser = iuser;
    }

    public KarteBean getKarteBean() {
        return karteBean;
    }

    public void setKarteBean(KarteBean karteBean) {
        this.karteBean = karteBean;
    }

    public IDocInfo getDocInfo() {
        return docInfo;
    }

    public void setDocInfo(IDocInfo docInfo) {
        this.docInfo = docInfo;
    }

    public IProgressCourseModule2 getSoaProgressCourse() {
        return soaProgressCourse;
    }

    public void setSoaProgressCourse(IProgressCourseModule2 soaProgressCourse) {
        this.soaProgressCourse = soaProgressCourse;
    }

    // 20131206 add funabashi
    public IProgressCourseModule2 getPtextProgressCourse() {
        return ptextProgressCourse;
    }
    
    // 20131206 add funabashi
    public void setPtextProgressCourse(IProgressCourseModule2 pProgressCourse) {
        this.ptextProgressCourse = pProgressCourse;
    }

    public List<IBundleModule> getBundles() {
        return bundles;
    }

    public void setBundles(List<IBundleModule> bundles) {
        this.bundles = bundles;
    }
    
    public List<ISchemaModel> getSchema() {
        return schema;
    }

    public void setSchema(List<ISchemaModel> schemas) {
        this.schema = schemas;
    }
    
    public List<IAttachmentModel> getAttachment() {
        return attachments;
    }
    
    public void setAttachment(List<IAttachmentModel> attachments) {
        this.attachments = attachments;
    }
    // S.Oh 2014/02/06 Add End
    
    public void fromModel(DocumentModel model) {
        
        this.setId(model.getId());
        this.setStarted(IOSHelper.toDateStr(model.getStarted()));
        this.setConfirmed(IOSHelper.toDateStr(model.getConfirmed()));
        this.setRecorded(IOSHelper.toDateStr(model.getRecorded()));
        this.setEnded(IOSHelper.toDateStr(model.getEnded()));
        
        this.setLinkId(model.getLinkId());
        this.setLinkRelation(model.getLinkRelation());
        this.setStatus(model.getStatus());
        // 変換なし
        //this.setKarteBean(model.getKarteBean());
        //this.setUserModel(model.getUserModel());
        
        // DocInfo
        IDocInfo info = new IDocInfo();
        info.fromModel(model.getDocInfoModel());
        this.setDocInfo(info);
        
        // Module
        ModuleModel soa = null;
        ModuleModel p = null; // 20131206 add funabashi
        if (model.getModules()!=null && model.getModules().size()>0) {
            
            List<IBundleModule> list = new ArrayList();
            for (ModuleModel module : model.getModules()) {
                //System.err.println(module.getModuleInfoBean().getEntity());
                //System.err.println(module.getModuleInfoBean().getStampRole());
                if (module.getModuleInfoBean().getEntity().equals(IInfoModel.MODULE_PROGRESS_COURSE)) {
                    if (module.getModuleInfoBean().getStampRole().equals(IInfoModel.ROLE_SOA_SPEC)) {
                        soa = module;
                    } else if(module.getModuleInfoBean().getStampRole().equals(IInfoModel.ROLE_P_SPEC)) {
                        p = module; // 20131206 add funabashi
                    } else {
                        continue;
                    }
                } else {
                    IBundleModule ib = new IBundleModule();
                    ib.fromModel(module);
                    // trick
                    if (module.getModel() instanceof BundleDolphin) {
                        BundleDolphin bd = (BundleDolphin)module.getModel();
                        ib.getModel().setOrderName(bd.getOrderName());
                    } else {
                        ib.getModel().setOrderName(module.getModuleInfoBean().getEntity());
                    }
                    list.add(ib);
                }
            }
            if (!list.isEmpty()) {
                this.setBundles(list);
            }
        }
        // soa
        if (soa!=null) {
            IProgressCourseModule2 ip = new IProgressCourseModule2();
            ip.fromModel(soa);
            this.setSoaProgressCourse(ip);
        }
        // p freeText add 20131206 funabashi
        if(p!=null){
            IProgressCourseModule2 ip = new IProgressCourseModule2();
            ip.fromModel(p);
            this.setPtextProgressCourse(ip);
        }
        
        // Schema
        if (model.getSchema()!=null) {
            
            List<ISchemaModel> list = new ArrayList();
            this.setSchema(list);
            
            for (SchemaModel s : model.getSchema()) {
                // 変換
                ISchemaModel is = new ISchemaModel();
                is.fromModel(s);
                list.add(is);
            }
        }
        
        if(model.getAttachment() != null) {
            List<IAttachmentModel> list = new ArrayList();
            this.setAttachment(list);
            for(AttachmentModel a : model.getAttachment()) {
                IAttachmentModel ia = new IAttachmentModel();
                ia.fromModel(a);
                list.add(ia);
            }
        }
        
//minagawa^ カルテのタイトルへ表示
        if (model.getUserModel()!=null)
        {
            IUserModel u = new IUserModel();
            u.setModel(model.getUserModel());
            this.setIuser(u);
        }
//mingawa$        
    }
    
    /**
     * iOSからIDocumentのJSONが送信される。　
     * パースしたIDocumentをDocumentModelへ変換する。
     * @return DocumentModel
     */
    public DocumentModel toModel() {
//minagawa^  EHR Touch及び Visit Touch を削除する       
        if (this.getSoaProgressCourse()!=null && this.getSoaProgressCourse().getModel()!=null) {
            String freeText = this.getSoaProgressCourse().getModel().getFreeText();
            if (freeText.equals("EHR Touch") || freeText.equals("Visit Touch")) {
                this.getSoaProgressCourse().getModel().setFreeText("");
            }
        }
//minagawa$ 
        
        DocumentModel ret = new DocumentModel();
        
        // pk
        ret.setId(this.getId());
        
        // 確定日 Date
        ret.setConfirmed(IOSHelper.toDate(this.getConfirmed()));
        
        // 開始日 Date
        ret.setStarted(IOSHelper.toDate(this.getStarted()));
        
        // 終了日 Date
        ret.setEnded(IOSHelper.toDate(this.getEnded()));
        
        // 記録日 Date
        ret.setRecorded(IOSHelper.toDate(this.getRecorded()));
        
        // リンクpk
        ret.setLinkId(this.getLinkId());
        
        // リンクの関連
        ret.setLinkRelation(this.getLinkRelation());
        
        // ステータス
        ret.setStatus(this.getStatus());
        
        // UserModel 変換なし
        ret.setUserModel(this.getUserModel());
        
        // KarteBean 変換なし
        ret.setKarte(this.getKarteBean());
        
        // DocInfo 変換
        ret.setDocInfoModel(this.docInfo.toModel());
        
        int number = 0;
        
        // Schema
        if (this.getSchema()!=null && this.getSchema().size()>0) {
            for (ISchemaModel s : this.schema) {
                // 変換
                SchemaModel sm = s.toModel();
                // 関係構築
                sm.setDocumentModel(ret);
                ret.addSchema(sm);
            }
            
            int len = this.getSchema().size();
            StringBuilder sb = new StringBuilder();
            sb.append("<section>");
            
            if (this.getSoaProgressCourse()!=null && this.getSoaProgressCourse().getModel()!=null) {
                sb.append("<paragraph>");
                sb.append("<content>");
                sb.append("<text>");
                sb.append(this.getSoaProgressCourse().getModel().getFreeText());
                sb.append("</text>");
                sb.append("</content>");
                sb.append("<content><text>\n</text></content>");    // CR
                sb.append("</paragraph>");
            }
            
            for (int i = 0; i < len; i++) {
                sb.append("<paragraph>");
                sb.append("<content><text>\n</text></content>");
                sb.append("</paragraph>");
                
                sb.append("<paragraph>");
                sb.append("<component component=").append("\"").append(i).append("\"").append(" name=\"schemaHolder\">").append("</component>");
                sb.append("<content><text></text></content>");
                sb.append("<content><text>\n</text></content>");    // CR
                sb.append("</paragraph>");
            }
            
            sb.append("</section>");
            String soaText = sb.toString();
            
//minagawa^ BUG SOAがない時は落ちる-iPhone側でdummy text 挿入           
            // freeText を変更
            //this.getSoaProgressCourse().getModel().setFreeText(soaText);
//minagawa$            
            // Modelへ変換する
            ModuleModel module = this.getSoaProgressCourse().toModel();
            module.setDocumentModel(ret);
            module.getModuleInfoBean().setStampNumber(number++);
            ret.addModule(module);
            
        } else if (this.getSoaProgressCourse()!=null) {
            // freeText -> soaSpec
            //this.getSoaProgressCourse().getModel().toSoaSpec();
            ModuleModel module = this.getSoaProgressCourse().toModel();
            module.setDocumentModel(ret);
            module.getModuleInfoBean().setStampNumber(number++);
            ret.addModule(module);
        }
        
        // PModule
//s.oh^ 2013/12/09
        //if (this.getBundles()!=null && this.getBundles().size()>0) {
        if(this.docInfo != null && this.docInfo.getDocType() != null && this.docInfo.getDocType().equals(IInfoModel.DOCTYPE_S_KARTE)) {
            // s_karteここ
            // 今は何もしない
        }else if (this.getBundles()!=null && this.getBundles().size()>0) {
//s.oh$
            
            for (IBundleModule bundle : this.bundles) {
                // 変換
                ModuleModel module = bundle.toModel();
                //System.err.println(module.toString());
                // 関係構築
                module.setDocumentModel(ret);
                module.getModuleInfoBean().setStampNumber(number++);
                ret.addModule(module);
            }
            
            ModuleModel infoToSet = ret.getModules().get(0);
            
            // PSpecを追加する
            int len = this.getBundles().size();
            StringBuilder sb = new StringBuilder();
            sb.append("<section>");
            for (int i = 0; i < len; i++) {
                
                // Stamp の間隔をあける
                if (i != 0)  {
                    // CR
                    sb.append("<paragraph>");
                    sb.append("<content><text>\n</text></content>");
                    sb.append("</paragraph>");
                }
                sb.append("<paragraph>");
                sb.append("<component component=").append("\"").append(i).append("\"").append(" name=\"stampHolder\">").append("</component>");
                sb.append("<content><text></text></content>");
                sb.append("<content><text>\n</text></content>");
                sb.append("</paragraph>");
            }
            
            // funabashi insert start 20131206
            System.out.println(this.ptextProgressCourse);
            if(this.ptextProgressCourse!=null){
                sb.append("<paragraph>");
                sb.append("<content><text>");
                sb.append(this.ptextProgressCourse.getModel().getFreeText());
                sb.append("</text></content>");
                sb.append("</paragraph>");
            }
            // funabashi insert end 20131206

            sb.append("</section>");
            String pText = sb.toString();
            ProgressCourse pProgress = new ProgressCourse();
            //pProgress.setFreeText(pText);
            if(this.ptextProgressCourse != null && this.ptextProgressCourse.getModel() != null && this.ptextProgressCourse.getModel().getFreeText().length() > 0) {
                pProgress.setFreeText(this.ptextProgressCourse.getModel().getFreeText());
            }
            
            ModuleModel pSpecModule = new ModuleModel();
            pSpecModule.setBeanBytes(IOSHelper.toXMLBytes(pProgress));
            pSpecModule.setConfirmed(infoToSet.getConfirmed());
            pSpecModule.setStarted(infoToSet.getStarted());
            pSpecModule.setRecorded(infoToSet.getRecorded());
            pSpecModule.setStatus(infoToSet.getStatus());
            pSpecModule.setUserModel(infoToSet.getUserModel());
            pSpecModule.setKarteBean(infoToSet.getKarteBean());
            pSpecModule.getModuleInfoBean().setStampName("progressCourse");
            pSpecModule.getModuleInfoBean().setStampRole("pSpec");
            pSpecModule.getModuleInfoBean().setEntity("progressCourse");
            pSpecModule.getModuleInfoBean().setStampNumber(number++);
            pSpecModule.setDocumentModel(ret);
            ret.addModule(pSpecModule);
        }
        // modules==0
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("<section>");
            sb.append("<paragraph>");
            //sb.append("<content><text>\n</text></content>");  // del 20131206 funabashi
            // funabashi insert start 20131206
            sb.append("<content><text>");
            sb.append((this.ptextProgressCourse!=null) ? this.ptextProgressCourse.getModel().getFreeText() : "\n");
            sb.append("</text></content>");
            // funabashi insert end 20131206
            sb.append("</paragraph>");
            sb.append("</section>");
            String pText = sb.toString();
            ProgressCourse pProgress = new ProgressCourse();
            //pProgress.setFreeText(pText);
            if(this.ptextProgressCourse != null && this.ptextProgressCourse.getModel() != null && this.ptextProgressCourse.getModel().getFreeText().length() > 0) {
                pProgress.setFreeText(this.ptextProgressCourse.getModel().getFreeText());
            }
            ModuleModel pSpecModule = new ModuleModel();
            pSpecModule.setBeanBytes(IOSHelper.toXMLBytes(pProgress));
            pSpecModule.setConfirmed(ret.getConfirmed());
            pSpecModule.setStarted(ret.getStarted());
            pSpecModule.setRecorded(ret.getRecorded());
            pSpecModule.setStatus(ret.getStatus());
            pSpecModule.setUserModel(ret.getUserModel());
            pSpecModule.setKarteBean(ret.getKarteBean());
            pSpecModule.getModuleInfoBean().setStampName("progressCourse");
            pSpecModule.getModuleInfoBean().setStampRole("pSpec");
            pSpecModule.getModuleInfoBean().setEntity("progressCourse");
            pSpecModule.getModuleInfoBean().setStampNumber(0);
            pSpecModule.setDocumentModel(ret);
            ret.addModule(pSpecModule);
        }
        
        return ret;
    }
}