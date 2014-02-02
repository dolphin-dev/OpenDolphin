/*
 * DaoBean.java
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.dao;

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
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public String getPasswd() {
        return passwd;
    }
    
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    
    public boolean isNoError() {
        return errorCode == TT_NO_ERROR ? true : false;
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
        
        StringBuilder sb  = new StringBuilder();
        
        if (e instanceof org.postgresql.util.PSQLException) {
            setErrorCode(TT_CONNECTION_ERROR);
            sb.append("サーバに接続できません。ネットワーク環境をお確かめください。");
            sb.append("\n");
            sb.append(appenExceptionInfo(e));
            setErrorMessage(sb.toString());
            
        } else if (e instanceof java.sql.SQLException) {
            setErrorCode(TT_DATABASE_ERROR);
            sb.append("データベースアクセスエラー");
            sb.append("\n");
            sb.append(appenExceptionInfo(e));
            setErrorMessage(sb.toString());
        } else {
            setErrorCode(TT_UNKNOWN_ERROR);
            sb.append("アプリケーションエラー");
            sb.append("\n");
            sb.append(appenExceptionInfo(e));
            setErrorMessage(sb.toString());
        }
    }
    
    /**
     * 例外の持つ情報を加える。
     * @param e 例外
     */
    protected String appenExceptionInfo(Exception e) {
        
        StringBuilder sb  = new StringBuilder();
        sb.append("例外クラス: ");
        sb.append(e.getClass().getName());
        sb.append("\n");
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            sb.append("原因: ");
            sb.append(e.getCause().getMessage());
            sb.append("\n");
        }
        if (e.getMessage() != null) {
            sb.append("内容: ");
            sb.append(e.getMessage());
        }
        
        return sb.toString();
    }
}