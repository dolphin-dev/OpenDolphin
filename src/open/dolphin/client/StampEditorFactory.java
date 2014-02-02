/*
 * StampEditorFactory.java        1.0 2001/3/1
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
package open.dolphin.client;

/**
 * StampEditorをインスタンス化するクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class StampEditorFactory {
    
               
    public StampEditorFactory() {
    }
        
    public static Object create(String entity) {
        
        Object ret = null;
        
        // TODO マップの方法
        String className = ClientContext.getString("stampEditor." + entity);
        if (className == null) {
        	className = entity;
        }
        System.out.println("Requested to create " + className);
        
        try {
            ret = Class.forName(className).newInstance();
        
        } catch (NullPointerException ne) {
            System.out.println(ne);
            ne.printStackTrace();
        
        } catch (ClassNotFoundException ce) {
            System.out.println(ce);
            ce.printStackTrace();
        
        } catch (InstantiationException ie) {
            System.out.println(ie);
            ie.printStackTrace();
        
        } catch (IllegalAccessException le) {
            System.out.println(le);
            le.printStackTrace();
        
        } catch (Exception un) {
            System.out.println(un);
            un.printStackTrace();
        }
        
        return ret;
    }    
}