package open.dolphin.session;

public final class ServiceLocatorException extends RuntimeException {
    
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
