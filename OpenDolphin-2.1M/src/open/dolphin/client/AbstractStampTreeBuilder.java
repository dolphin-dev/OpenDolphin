package open.dolphin.client;

import java.util.List;

import open.dolphin.infomodel.IInfoModel;


/**
 * AbstractStampTreeBuilder
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractStampTreeBuilder {
    
    /** Creates new DefaultStampTreeBuilder */
    public AbstractStampTreeBuilder() {
    }
    
    public abstract List<StampTree> getProduct();
    
    public abstract void buildStart();
    
    public abstract void buildRoot(String name, String entity);
    
    public abstract void buildNode(String name);
    
    public abstract void buildStampInfo(String name, String role, String entity, String editable, String memo, String id);
    
    public abstract void buildNodeEnd();
    
    public abstract void buildRootEnd();
    
    public abstract void buildEnd();
    
    protected static String getEntity(String rootName) {
        
        String ret = null;
        
        if (rootName == null) {
            return ret;
        }
        
        for (int i = 0; i < IInfoModel.STAMP_ENTITIES.length; i++) {
            if (IInfoModel.STAMP_NAMES[i].equals(rootName)) {
                ret = IInfoModel.STAMP_ENTITIES[i];
                break;
            }
        }
        
        return ret;
    }
}