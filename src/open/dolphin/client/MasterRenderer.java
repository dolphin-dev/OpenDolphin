/*
 * MasterRenderer.java
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
package open.dolphin.client;

import java.awt.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * MasterRenderer
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MasterRenderer extends DefaultTableCellRenderer {
    
    private static final long serialVersionUID = 4397037194989155042L;

	protected Color beforStartColor;
    
    protected Color afterEndColor;
    
    protected Color inUseColor;
    
    protected String refDate;
    
    protected static final Color DEFAULT_ODD_COLOR = Color.white;
    protected static final Color DEFAULT_EVENN_COLOR = new Color(237,243,254);
    protected Color oddColor;
    protected Color evenColor;
    
    /** Creates a new instance of MasterRenderer */
    public MasterRenderer() {
    	setOpaque(true);
		setOddColor(oddColor);
		setEvenColor(evenColor);
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        refDate = f.format(gc.getTime()).toString();
    }
    
    public MasterRenderer(String refDate) {
        super();
        this.refDate = refDate;
    }
    
	/**
	 * @param oddColor The oddColor to set.
	 */
	public void setOddColor(Color oddColor) {
		this.oddColor = oddColor;
	}

	/**
	 * @return Returns the oddColor.
	 */
	public Color getOddColor() {
		return oddColor;
	}
	
	/**
	 * @param evenColor The evenColor to set.
	 */
	public void setEvenColor(Color evenColor) {
		this.evenColor = evenColor;
	}

	/**
	 * @return Returns the evenColor.
	 */
	public Color getEvenColor() {
		return evenColor;
	}
    
    public Color getBeforStartColor() {
        return beforStartColor;
    }
    
    public void setBeforStartColor(Color val) {
        beforStartColor = val;
    }
    
    public Color getAfterEndColor() {
        return afterEndColor;
    }
    
    public void setAfterEndColor(Color val) {
        afterEndColor = val;
    }
    
    public Color getInUseColor() {
        return inUseColor;
    }
    
    public void setInUseColor(Color val) {
        inUseColor = val;
    }
    
    public void setColor(JLabel label, String startDate, String endDate) {
        
        switch (useState(startDate, endDate)) {
            
            case 0:
                label.setEnabled(false);
                label.setForeground(beforStartColor);
                break;
                
            case 1:
                label.setEnabled(true);
                label.setForeground(inUseColor);
                break;
                
            case 2:
                label.setEnabled(false);
                label.setForeground(afterEndColor);
                break;
        }
    }
    
    
    public void setColor(JLabel label, String endDate) {
                
        setColor(label, null, endDate);
    }
    
    protected int useState(String startDate, String endDate) {
        
        if (startDate != null && refDate.compareTo(startDate) < 0) {
            return 0;
        
        } else if (endDate != null && refDate.compareTo(endDate) > 0) {
            return 2;
        }
        
        return 1;  
    }
}