package open.dolphin.client;

import open.dolphin.infomodel.SchemaModel;

/**
 * SchemaList
 * 
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SchemaList implements java.io.Serializable {
    
    private static final long serialVersionUID = 5408868424299902180L;
	
    SchemaModel[] schemaList;

    /** Creates new ImageList */
    public SchemaList() {
    }
}