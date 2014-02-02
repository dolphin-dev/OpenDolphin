package open.dolphin.ejb;

public class ServiceLocatorException extends RuntimeException {
    
    public ServiceLocatorException() {
        super();
    }
    
    public ServiceLocatorException(String message) {
        super(message);
    }
    
    public ServiceLocatorException(Throwable cause) {
        super(cause);
    }
    
    public ServiceLocatorException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
