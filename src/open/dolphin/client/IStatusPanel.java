/*
 * Created on 2005/07/12
 *
 */
package open.dolphin.client;


public interface IStatusPanel {
	
	public void setMessage(String msg);
	    
	public void start();
	
	public void start(String startMsg);
	
	public void stop();
	
	public void stop(String stopMsg);
	
	public void setRightInfo(String info);
    
    public void setLeftInfo(String info);

}
