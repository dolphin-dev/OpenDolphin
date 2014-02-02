/*
 * SearchSpec.java
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
package open.dolphin.order;

/**
 *
 * @author  KAzushi Minagawa, Digital Globe,. Inc.
 */
public final class SearchSpec extends Object {
    
    private String masterName;
    private String keyword;
    private boolean startsWith;

    /** 
     * Creates new SearchSpec 
     */
    public SearchSpec() {
    }
    
    public String getMasterName() {
        return masterName;
    }
    
    public void setMasterName(String val) {
        masterName = val;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String val) {
        keyword = val;
    }  
    
    public boolean getStartsWith() {
        return startsWith;
    }
    
    public void setStartsWith(boolean val) {
        startsWith = val;
    }   
}



