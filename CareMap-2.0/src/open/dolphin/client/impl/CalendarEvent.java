package open.dolphin.client.impl;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class CalendarEvent extends java.util.EventObject {
    
    private static final long serialVersionUID = -9042706233806609258L;
	
    private SimpleCalendarPanel c0;
    private SimpleCalendarPanel c1;
    private SimpleCalendarPanel c2;
    
    /** Creates a new instance of CalendarEvent */
    public CalendarEvent(Object source) {
        super(source);
    }
    
    public SimpleCalendarPanel getC0() {
        return c0;
    }
    
    public void setC0(SimpleCalendarPanel val) {
        c0 = val;
    }
    
    public SimpleCalendarPanel getC1() {
        return c1;
    }
    
    public void setC1(SimpleCalendarPanel val) {
        c1 = val;
    }
    
    public SimpleCalendarPanel getC2() {
        return c2;
    }
    
    public void setC2(SimpleCalendarPanel val) {
        c2 = val;
    }    
}