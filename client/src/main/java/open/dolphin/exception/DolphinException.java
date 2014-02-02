package open.dolphin.exception;

/**
 * DolphinException
 * 
 * @author  Kazushi Minagawa
 */
public class DolphinException extends java.lang.RuntimeException {
    
    public DolphinException() {
        super();
    }

    public DolphinException(String msg) {        
        super(msg);
    }
    
    public DolphinException(java.lang.Throwable t) {        
        super(t);
    } 
    
    public DolphinException(String s, java.lang.Throwable t) {        
        super(s, t);
    }     
}