/*
 * DemoFactory.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
package open.dolphin.project;

import open.dolphin.dao.*;

/**
 *
 * @author  Kazushi Minagawa, Digital globe, Inc.
 */
public class DemoFactory extends DebugFactory {
       
    /** Creates a new instance of DemoFactory */
    public DemoFactory() {
    }
    
    public AuthenticationDao createAuthentication(String host, int port, String userId, String passwd) {
        return new AuthenticationDao(host, port, "Manager", "cn=Manager,o=Dolphin", "hanagui");
    }
}