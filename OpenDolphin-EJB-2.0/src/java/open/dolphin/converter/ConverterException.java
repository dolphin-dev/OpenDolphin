package open.dolphin.converter;

/**
 * DolphinException
 * 
 * @author  Kazushi Minagawa
 */
public class ConverterException extends java.lang.RuntimeException {
    
    public ConverterException() {
        super();
    }

    public ConverterException(String msg) {        
        super(msg);
    }
    
    public ConverterException(java.lang.Throwable t) {        
        super(t);
    } 
    
    public ConverterException(String s, java.lang.Throwable t) {        
        super(s, t);
    }     
}