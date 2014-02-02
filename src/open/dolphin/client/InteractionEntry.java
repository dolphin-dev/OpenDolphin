/*
 * InteractionEntry.java
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
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

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class InteractionEntry {
    
    private String code1;
    
    private String name1;
    
    private String code2;
    
    private String name2;
    
    private String interactionCode;
    
    private String interaction;
    
    /** Creates a new instance of InteractionEntry */
    public InteractionEntry() {
    }
    
    public String getCode1() {
        return code1;
    }
    
    public void setCode1(String val) {
        code1 = val;
    }
    
    public String getName1() {
        return name1;
    }
    
    public void setName1(String val) {
        name1 = val;
    }    
    
    public String getCode2() {
        return code2;
    }
    
    public void setCode2(String val) {
        code2 = val;
    }
    
    public String getName2() {
        return name2;
    }
    
    public void setName2(String val) {
        name2 = val;
    } 
    
    public String getInteractionCode() {
        return interactionCode;
    }
    
    public void setInteractionCode(String val) {
        interactionCode = val;
    } 
    
    public String getInteraction() {
        return interaction;
    }
    
    public void setInteraction(String val) {
        interaction = val;
    }
    
    public String toString() {
        
        if (code1 == null || code2 == null || interaction == null) {
            return null;
        }
     
        StringBuffer buf = new StringBuffer();
        buf.append("[ ");
        
        if (name1 != null) {
            buf.append(name1);
        } else {
            buf.append(code1);
        }
        
        buf.append(" : ");
        
        if (name2 != null) {
            buf.append(name2);
        } else {
            buf.append(code2);
        }
        buf.append(" ]\n");
        
        buf.append("ÅE");
        buf.append(interaction);
        
        return buf.toString();
    }
    
}
