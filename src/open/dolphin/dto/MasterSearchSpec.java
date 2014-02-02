package open.dolphin.dto;

import java.io.Serializable;

/**
 * MasterSearchSpec
 * 
 * @author Minagawa, Kazushi
 * 
 */
public class MasterSearchSpec implements Serializable {
	
	private static final long serialVersionUID = 3372385562424949589L;
	
	public static final int ADMIN_CLASS 			= 0;
	public static final int ADMINISTRATION 		= 1;
	public static final int ADMIN_COMENT 			= 2;
	public static final int RADIOLOGY_METHOD 		= 3;
	public static final int RADIOLOGY_COMENT 		= 4;
	
	private int code;
	private String hierarchyCode1;
	private String from;
	
	/**
	 * @param code The code to set.
	 */
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * @return Returns the code.
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param hierarchyCode1 The hierarchyCode1 to set.
	 */
	public void setHierarchyCode1(String hierarchyCode1) {
		this.hierarchyCode1 = hierarchyCode1;
	}
	/**
	 * @return Returns the hierarchyCode1.
	 */
	public String getHierarchyCode1() {
		return hierarchyCode1;
	}
	/**
	 * @param from The from to set.
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	/**
	 * @return Returns the from.
	 */
	public String getFrom() {
		return from;
	}
}
