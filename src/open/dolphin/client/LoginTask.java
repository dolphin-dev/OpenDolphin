/*
 * LoginDialog.java
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.client;

import open.dolphin.delegater.UserDelegater;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.DolphinPrincipal;

/**
 * LoginTask
 *
 * @author Minagawa,Kazushi
 */
public class LoginTask extends AbstractInfiniteTask {
    
    private String password;
    private DolphinPrincipal principal;
    private UserModel userModel;
    private UserDelegater udl;
    
    public LoginTask(DolphinPrincipal principal, String password, UserDelegater udl, int taskLength) {
        super();
        this.password = password;
        this.principal = principal;
        this.udl = udl;
        this.taskLength = taskLength;
    }
    
    public UserModel getUserModel() {
        return userModel;
    }
    
    protected void doTask() {
        userModel = udl.login(principal, password);
        done = true;
    }
}
