/*
 * Created on 2004/02/03
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
package open.dolphin.infomodel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * 患者来院情報クラス。
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class PatientVisit extends InfoModel {
	
	final public static DataFlavor PVT_FLAVOR =
		new DataFlavor (open.dolphin.infomodel.PatientVisit.class, "Patient Visit");

	static DataFlavor flavors[] = {PVT_FLAVOR};
	
	private Patient patient;
      
	/** 受付リスト上の番号 */
	private int number;
	
	/** 来院時間 */
	private String time;
	
	/** 予約 */
	private boolean appointment;
    
	/** 診療科 */
	private String department;

	/** 終了フラグ */
	private int state;

	/** 保健種別 */
	private DInsuranceInfo[] insurances;
	private static final int CAP = 1;
	private int insCount;

	public PatientVisit() {
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public void addInsuranceInfo(DInsuranceInfo val) {
        
		if (getInsurance() == null) {
			setInsurance(new DInsuranceInfo[1]);
        
		} else if ( (insCount % CAP) == 0) {
		   DInsuranceInfo[] dest = new DInsuranceInfo[insCount + CAP];
		   System.arraycopy(getInsurance(), 0, dest, 0, insCount);
		   setInsurance(dest);
		}
		getInsurance()[insCount] = val;
		insCount++;
	}

	public boolean isInsuranceAvailable() {
		boolean ret = false;
		int len = getInsurance().length;

		for (int i = 0; i < len; i++) {
		   if (! getInsurance()[i].isSelected()) {
			   ret = true;
			   break;
		   }
		}
		return ret;
		//return true;  // 2002-8-01 
	}

	/////////////////// Transferable //////////////////////////

	public boolean isDataFlavorSupported (DataFlavor df) {
		return df.equals(PVT_FLAVOR);
	}

	/** implements Transferable interface */
	public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
		if (df.equals(PVT_FLAVOR)) {
		  return this;
		}
		else throw new UnsupportedFlavorException(df);
	}

	/** implements Transferable interface */
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	///////////////// Serializable ////////////////////////////

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		 out.defaultWriteObject();
	}

	private void readObject(java.io.ObjectInputStream in)
	 throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		return time;
	}

	public void setAppointment(boolean appointment) {
		this.appointment = appointment;
	}

	public boolean isAppointment() {
		return appointment;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDepartment() {
		return department;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

	public void setInsurance(DInsuranceInfo[] insurances) {
		this.insurances = insurances;
	}

	public DInsuranceInfo[] getInsurance() {
		return insurances;
	}
}
