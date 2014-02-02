/*
 * Period.java
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

import java.util.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class Period extends EventObject {
    
    private String startDate;
    private String endDate;
    
    /** Creates a new instance of Period */
    public Period(Object source) {
        super(source);
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String val) {
        startDate = val;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String val) {
        endDate = val;
    }    
}
