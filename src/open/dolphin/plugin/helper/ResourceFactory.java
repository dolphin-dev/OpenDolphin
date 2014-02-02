/*
 * Created on 2005/06/20
 *
 */
package open.dolphin.plugin.helper;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.CompoundName;
import javax.naming.Context;
import javax.naming.Name;

import open.dolphin.plugin.AbstractObjectFactory;

/**
 * ResourceFactory
 *
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class ResourceFactory extends AbstractObjectFactory  {
    
    public ResourceFactory() {
    }
    
    @SuppressWarnings("unchecked")
    public Object getObjectInstance(Object obj,			// リソース文字列
            Name name, 			// リソースの Name(Composite)
            Context ctx,
            Hashtable env) throws Exception {
        
        if (obj instanceof String) {
            
            CompoundName resLine = getValueCompoundName((String)obj);
            int size = resLine.size();
            
            if (size == 1) {
                // 文字列そのまま返す
                return resLine.get(0);
                
            } else if (size > 1 ) {
                ArrayList list = new ArrayList(size);
                for (int i = 0; i < size; i++) {
                    list.add(resLine.get(i));
                }
                return list;
            }
        }
        
        return null;
    }
}
