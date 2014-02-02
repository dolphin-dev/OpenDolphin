package open.dolphin.client;

import open.dolphin.infomodel.ModuleModel;

/**
 * OrderList
 * 
 * @author  Kazushi Minagawa
 */
public final class OrderList implements java.io.Serializable {
    
    private static final long serialVersionUID = -6049175115811888229L;
	
    public ModuleModel[] orderList;

    /** Creates new OrderList */
    public OrderList() {
    }
    
    public OrderList(ModuleModel[] stamp) {
    	this();
    	setOrderStamp(stamp);
    }
    
    public ModuleModel[] getOrderList() {
    	return orderList;
    }
    
    public void setOrderStamp(ModuleModel[] stamp) {
    	orderList = stamp;
    }
}