package open.dolphin.dao;

import java.text.MessageFormat;
import open.dolphin.client.ClientContext;


/**
 * DaoBean
 *
 * @author  Kazushi Minagawa
 */
public class DaoBean {
    
    public static final int TT_NONE              = 10;
    public static final int TT_NO_ERROR          =  0;
    public static final int TT_CONNECTION_ERROR  = -1;
    public static final int TT_DATABASE_ERROR    = -2;
    public static final int TT_UNKNOWN_ERROR     = -3;
    
    protected String host;
    protected int port;
    protected String user;
    protected String passwd;
    
    protected int errorCode;
    protected String errorMessage;
    
    /**
     * Creates a new instance of DaoBean
     */
    public DaoBean() {
    }
    
    public final String getHost() {
        return host;
    }
    
    public final void setHost(String host) {
        this.host = host;
    }
    
    public final int getPort() {
        return port;
    }
    
    public final void setPort(int port) {
        this.port = port;
    }
    
    public final String getUser() {
        return user;
    }
    
    public final void setUser(String user) {
        this.user = user;
    }
    
    public final String getPasswd() {
        return passwd;
    }
    
    public final void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    
    public final boolean isNoError() {
        return errorCode == TT_NO_ERROR;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    /**
     * 例外を解析しエラーコードとエラーメッセージを設定する。
     *
     * @param e Exception
     */
    protected void processError(Exception e) {
        
        java.util.logging.Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
        
        StringBuilder sb  = new StringBuilder();
        
        if (e instanceof org.postgresql.util.PSQLException) {
            setErrorCode(TT_CONNECTION_ERROR);
            String fmt = ClientContext.getMyBundle(DaoBean.class).getString("messageFormat.cannnotConnect");
            String errMsg = new MessageFormat(fmt).format(new String[]{appenExceptionInfo(e)});
            setErrorMessage(errMsg);
            
        } else if (e instanceof java.sql.SQLException) {
            setErrorCode(TT_DATABASE_ERROR);
            String fmt = ClientContext.getMyBundle(DaoBean.class).getString("messageFormat.dbAccessError");
            String errMsg = new MessageFormat(fmt).format(new String[]{appenExceptionInfo(e)});
            setErrorMessage(errMsg);
        } else {
            setErrorCode(TT_UNKNOWN_ERROR);
            String fmt = ClientContext.getMyBundle(DaoBean.class).getString("messageFormat.appError");
            String errMsg = new MessageFormat(fmt).format(new String[]{appenExceptionInfo(e)});
            setErrorMessage(errMsg);
        }
    }
    
    /**
     * 例外の持つ情報を加える。
     * @param e 例外
     * @return 
     */
    protected String appenExceptionInfo(Exception e) {
        
        String fmt = ClientContext.getMyBundle(DaoBean.class).getString("messageFormat.exception");
        MessageFormat msf = new MessageFormat(fmt);
        Object[] obj = new Object[3];
        obj[0] = e.getClass().getName();
        obj[1] =  (e.getCause() != null && e.getCause().getMessage() != null) 
                ? e.getCause().getMessage()
                : "";
        obj[3] = e.getMessage();
        
        return msf.format(obj);
    }
}