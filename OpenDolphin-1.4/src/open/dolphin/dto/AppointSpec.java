package open.dolphin.dto;

import java.util.Collection;

/**
 * AppointSpec
 * 
 * @author Minagawa,Kazushi
 */
public class AppointSpec extends DolphinDTO {
	
	private static final long serialVersionUID = -2819531469105475380L;
	
	private Collection added;
	private Collection updared;
	private Collection removed;
	
	/**
	 * @param added The added to set.
	 */
	public void setAdded(Collection added) {
		this.added = added;
	}
	/**
	 * @return Returns the added.
	 */
	public Collection getAdded() {
		return added;
	}
	/**
	 * @param updared The updared to set.
	 */
	public void setUpdared(Collection updared) {
		this.updared = updared;
	}
	/**
	 * @return Returns the updared.
	 */
	public Collection getUpdared() {
		return updared;
	}
	/**
	 * @param removed The removed to set.
	 */
	public void setRemoved(Collection removed) {
		this.removed = removed;
	}
	/**
	 * @return Returns the removed.
	 */
	public Collection getRemoved() {
		return removed;
	}

}
