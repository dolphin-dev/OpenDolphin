package open.dolphin.stampbox;

import java.util.List;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.ModuleInfoBean;

/**
 * IStampBox
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public interface IStampBox {
    
    public StampBoxPlugin getContext();
    
    public void setContext(StampBoxPlugin plugin);
    
    public IStampTreeModel getStampTreeModel();
    
    public void setStampTreeModel(IStampTreeModel stampTreeModel);
    
    public List<StampTree> getAllTrees();
    
    public StampTree getStampTree(String entity);
    
    public StampTree getStampTree(int index);
    
    public boolean isHasEditor(int index);
    
    public void setHasNoEditorEnabled(boolean enabled);
    
    public List<TreeInfo> getAllTreeInfos();
    
    public List<ModuleInfoBean> getAllStamps(String entity);
    
    public List<String> getEntities();
    
    public String getInfo();
    
}











