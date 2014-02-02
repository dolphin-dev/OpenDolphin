/*
 * ClassFactory.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
 * Factory to create Dolphin Class.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClassFactory {
    
    //private static final Class me = open.dolphin.client.ClassFactory.class;

    /** 
     * Creates new ServiceFactory 
     */
    public ClassFactory() {
        super();
    }  
    
    /**
     * Returns new IDolphinService
     */
    public static Object create(String className) {
        
        Object o = null;
        try {
            o = Class.forName(className).newInstance();
        }
        catch (NullPointerException ne) {
            //assert false : "NullPointerException";
            ne.printStackTrace();
            
        }
        catch (ClassNotFoundException ce) {
            //assert false : "ClassNotFoundException";
            ce.printStackTrace();
        }
        catch (InstantiationException ie) {
            //assert false : "InstantiationException";
            ie.printStackTrace();
        }
        catch (IllegalAccessException le) {
            //assert false : "IllegalAccessException";
            le.printStackTrace();
        }
        catch (Exception un) {
            //assert false : un;
            un.printStackTrace();
        }
        
        return o;
    }
}