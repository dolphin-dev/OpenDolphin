/*
 * Created on 2005/02/27
 *
 */
package open.dolphin.infomodel;

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class SimpleDatePeriod extends InfoModel {
	
	private static final long serialVersionUID = 6266116050884820395L;
	private SimpleDate startDate;
	private SimpleDate endDate;
	
	public SimpleDatePeriod()  {
	}
	
	public SimpleDatePeriod(SimpleDate start, SimpleDate end) {
		this();
		setStartDate(start);
		setEndDate(end);
	}
	
	public void setStartDate(SimpleDate startDate) {
		this.startDate = startDate;
	}
	
	public SimpleDate getStartDate() {
		return startDate;
	}
	
	public void setEndDate(SimpleDate endDate) {
		this.endDate = endDate;
	}
	
	public SimpleDate getEndDate() {
		return endDate;
	}

}
