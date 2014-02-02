package open.dolphin.session;

/**
 * CanNotDeleteException
 *
 * @author Kazushi Minagawa
 */
public final class CanNotDeleteException extends RuntimeException {
    
    private String message;
    
    public CanNotDeleteException(String message) {
        super(message);
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }   
}