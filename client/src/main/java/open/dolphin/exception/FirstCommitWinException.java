package open.dolphin.exception;

/**
 * DolphinException
 * 
 * @author  Kazushi Minagawa
 */
public class FirstCommitWinException extends java.lang.RuntimeException {
    
    public FirstCommitWinException() {
        super();
    }

    public FirstCommitWinException(String msg) {        
        super(msg);
    }
    
    public FirstCommitWinException(java.lang.Throwable t) {        
        super(t);
    } 
    
    public FirstCommitWinException(String s, java.lang.Throwable t) {        
        super(s, t);
    }     
}