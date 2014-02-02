/*
 * Created on 2005/06/10
 *
 */
package open.dolphin.plugin.helper;

import open.dolphin.plugin.ILongTask;

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public abstract class AbstractLongTask implements ILongTask {
	
	private String message;
	private boolean result = true;

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	public boolean getResult() {
		return result;
	}
	
	public void setResult(boolean b) {
		result = b;
	}
	
	public abstract void run();
}
