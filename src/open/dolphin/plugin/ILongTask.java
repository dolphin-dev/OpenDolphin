/*
 * Created on 2005/06/08
 *
 */
package open.dolphin.plugin;


/**
 * 非決定的な長時間タスクのインターフェイス。
 * このクラスの run() が TaskManager からコールバックされる。
 * 
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public interface ILongTask {

	public void setMessage(String message);

	public String getMessage();
	
	public boolean getResult();
	
	public void run();
}
