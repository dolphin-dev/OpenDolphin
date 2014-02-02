/*
 * CalendarEvent.java
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
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class CalendarEvent extends java.util.EventObject {
    
    private static final long serialVersionUID = -9042706233806609258L;
	
    private SimpleCalendarPanel c0;
    private SimpleCalendarPanel c1;
    private SimpleCalendarPanel c2;
    
    /** Creates a new instance of CalendarEvent */
    public CalendarEvent(Object source) {
        super(source);
    }
    
    public SimpleCalendarPanel getC0() {
        return c0;
    }
    
    public void setC0(SimpleCalendarPanel val) {
        c0 = val;
    }
    
    public SimpleCalendarPanel getC1() {
        return c1;
    }
    
    public void setC1(SimpleCalendarPanel val) {
        c1 = val;
    }
    
    public SimpleCalendarPanel getC2() {
        return c2;
    }
    
    public void setC2(SimpleCalendarPanel val) {
        c2 = val;
    }    
}