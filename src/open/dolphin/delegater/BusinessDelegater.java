package open.dolphin.delegater;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import open.dolphin.project.Project;

/**
 * Bsiness Delegater のルートクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class BusinessDelegater {
    
    public static final int NO_ERROR            = 0;
    public static final int COMMUNICATION_ERROR = -1;
    public static final int EJB_ACCESS_ERROR    = -2;
    public static final int SECURITY_ERROR      = -3;
    public static final int UNDECLARED_ERROR    = -4;
    public static final int UNKNOWM_ERROR       = -5;
    
    private static final String SECURITY_DOMAIN = "openDolphin";
    
    private String securityDomain;
    private int errorCode;
    private String errorMessage;
    
    
    public BusinessDelegater() {
        setSecurityDomain(SECURITY_DOMAIN);
    }
    
    /**
     * @return Returns the enviroment.
     */
    private Hashtable getEnviroment() {
        Hashtable<String, String> enviroment = new Hashtable<String, String>(5,0.9f);
        enviroment.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        enviroment.put("java.naming.provider.url", Project.getProviderURL());
        enviroment.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        return enviroment;
    }
    
    /**
     * サービスを返す。
     * @param name サービス名
     * @return リモートサービス
     * @throws NamingException
     */
    public Object getService(String name) throws NamingException {
        InitialContext ctx = new InitialContext(getEnviroment());
        StringBuilder sb = new StringBuilder();
        sb.append(SECURITY_DOMAIN);
        sb.append("/");
        sb.append(name);
        return ctx.lookup(sb.toString());
    }
    
    /**
     * @return Returns the securityDomain.
     */
    public String getSecurityDomain() {
        return securityDomain;
    }
    
    public void setSecurityDomain(String value) {
        securityDomain = value;
    }
    
    /**
     * @param errCode The errCode to set.
     */
    public void setErrorCode(int errCode) {
        this.errorCode = errCode;
    }
    
    /**
     * @return Returns the errCode.
     */
    public int getErrorCode() {
        return errorCode;
    }
    
    /**
     * エラーが生じているかどうかを返す。
     * @return エラーが生じていない時 true
     */
    public boolean isNoError() {
        return errorCode == NO_ERROR ? true : false;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMesseage) {
        this.errorMessage = errorMesseage;
    }
    
    public void processError(Exception e) {
        
        StringBuilder sb = new StringBuilder();
        
        if (e instanceof javax.ejb.EJBAccessException) {
            setErrorCode(EJB_ACCESS_ERROR);
            sb.append("認証できません。ユーザIDまたはパスワードに間違いがあります。");
            sb.append("\n");
            sb.append(appendExceptionInfo(e));
            setErrorMessage(sb.toString());
            
        } else if (e instanceof javax.naming.CommunicationException) {
            setErrorCode(COMMUNICATION_ERROR);
            sb.append("サーバに接続できません。ネットワーク環境をお確かめください。");
            sb.append("\n");
            sb.append(appendExceptionInfo(e));
            setErrorMessage(sb.toString());
            
        } else if (e instanceof LoginException) {
            setErrorCode(SECURITY_ERROR);
            sb.append("セキュリティエラーが生じました。");
            sb.append("\n");
            sb.append("クライアントの環境が実行を許可されない設定になっている可能性があります。");
            sb.append("\n");
            sb.append(appendExceptionInfo(e));
            setErrorMessage(sb.toString());
            
        } else if (e instanceof UndeclaredThrowableException) {
            setErrorCode(UNDECLARED_ERROR);
            sb.append("処理を実行できません。");
            sb.append("\n");
            sb.append("クライアントのバージョンが古い可能性があります。");
            sb.append(appendExceptionInfo(e));
            setErrorMessage(sb.toString());
            
        } else {
            setErrorCode(UNKNOWM_ERROR);
            sb.append("アプリケーションエラー");
            sb.append("\n");
            sb.append(appendExceptionInfo(e));
            setErrorMessage(sb.toString());
        }
    }
    
    private String appendExceptionInfo(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append("例外クラス: ");
        sb.append(e.getClass().getName());
        sb.append("\n");
        if (e.getCause() != null) {
            sb.append("原因: ");
            sb.append(e.getCause().getMessage());
            sb.append("\n");
        }
        if (e.getMessage() != null) {
            sb.append("内容: ");
            sb.append(e.getMessage());
            sb.append("\n");
        }
        return sb.toString();
    }
}
