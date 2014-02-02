package open.dolphin.exception;

/**
 * DolphinException
 * (予定カルテ対応)
 * 
 * @author  Kazushi Minagawa
 */
public class CancelSaveAllException extends java.lang.RuntimeException {
    
    public CancelSaveAllException() {
        super();
    }

    public CancelSaveAllException(String msg) {        
        super(msg);
    }
    
    public CancelSaveAllException(java.lang.Throwable t) {        
        super(t);
    } 
    
    public CancelSaveAllException(String s, java.lang.Throwable t) {        
        super(s, t);
    }     
}