package open.dolphin.adm10.rest;


/**
 * AbstractStampTreeBuilder
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractStampTreeBuilder {
    
    /** Creates new DefaultStampTreeBuilder */
    public AbstractStampTreeBuilder() {
    }
    
    public abstract String getProduct();
    
    public abstract void buildStart();
    
    public abstract void buildRoot(String name, String entity);
    
    public abstract void buildNode(String name);
    
    public abstract void buildStampInfo(String name, String role, String entity, String editable, String memo, String id);
    
    public abstract void buildNodeEnd();
    
    public abstract void buildRootEnd();
    
    public abstract void buildEnd();
    
}