/*
 * IChartPlugin.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
package open.dolphin.plugin;

import java.awt.Dimension;
import java.util.ArrayList;

import open.dolphin.infomodel.BaseClinicModule;
import open.dolphin.infomodel.Karte;
import open.dolphin.infomodel.LifestyleModule;
import open.dolphin.infomodel.Patient;
import open.dolphin.infomodel.Schema;

/**
 * Dolphin の電子カルテモデル。 
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public interface IChartContext extends IMainWindowPlugin,IContext {
	
	public Patient getPatient();
    
    public void setClaimSent(boolean b);
    
    public boolean isReadOnly();
    
    public void setReadOnly(boolean b);
    
	public ArrayList getDocumentHistory(String pid, String docType, String fromDate, boolean historyDisplay);
	
	public ArrayList getDiagnosisHistory(String pid, String fromDate);
	
	public ArrayList getOrderHistory(String pid, String orderName, String fromDate, String toDate);
	
	public ArrayList getImageHistory(String pid, String fromDate, String toDate, Dimension iconSize);
	
	public Schema getSchema(String oid);
	
	public ArrayList getPvtHistory(String pid, String fromDate, String toDate);
	
	public ArrayList getOrderDateHistory(String pid, String orderName, String fromDate, String toDate);
	
	public ArrayList getImageDateHistory(String pid, String fromDate, String toDate);
	
	public Karte getKarte(String docId);
	
	public ArrayList getAppointments(String pid, String fromDate, String toDate);
	
	public BaseClinicModule getBaseClinicModule(String pid);
	
	public LifestyleModule getLifestyleModule(String pid);
}