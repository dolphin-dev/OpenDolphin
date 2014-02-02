/*
 * ClientId.java
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
package open.dolphin.infomodel;


/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClientId extends InfoModel {

    String group;

    String number;

    protected ClientId() {
    }

    public String getGroup() {
        return group;
    }
    
    public void setGroup(String val) {
        group = val;
    }
    
    public String getNumber() {
        return number;
    }
    
    public void setNumber(String val) {
        number = val;
    }
    
    public boolean isValidMML() {
        return ((group != null) && (number != null)) ? true : false;
    }
}