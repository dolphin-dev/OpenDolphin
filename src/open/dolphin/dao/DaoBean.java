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
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DaoBean {
    
    public static final int TT_NONE              = 10;
    public static final int TT_NO_ERROR          =  0;
    public static final int TT_CONNECTION_ERROR  = -1;
    public static final int TT_DATABASE_ERROR    = -2;

    String host;

    int port;

    String user;

    String passwd;
    
    int resultCode; // TT_NO_ERROR
    
    String errorMessage;
    
    /** Creates a new instance of DaoBean */
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
    
    public int getResultCode() {
        return resultCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    protected void processError(Object conn, Object ret, String es) {
        
        if (ret != null) {
            ret = null;
        }
        errorMessage = es;
        System.out.println(es);
        resultCode = conn == null ? TT_CONNECTION_ERROR : TT_DATABASE_ERROR;
    }
}