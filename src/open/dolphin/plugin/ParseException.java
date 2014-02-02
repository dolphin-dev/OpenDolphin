/*
 * Created on 2005/06/21
 *
 */
package open.dolphin.plugin;

/**
 * ParseException
 * 
 * @author Kazushi Minagawa
 *
 */
public class ParseException extends Exception {
	
	private static final long serialVersionUID = 6967624430588478224L;

	public ParseException(String msg) {
		super(msg);
	}
	
    public ParseException(java.lang.Throwable t) {        
        super(t);
    } 
    
    public ParseException(String s, java.lang.Throwable t) {        
        super(s, t);
    }  
}
