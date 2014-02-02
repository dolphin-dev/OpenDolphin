/*
 * StatusPanel.java
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.delegater;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import open.dolphin.dto.AppointSpec;
import open.dolphin.ejb.RemoteKarteService;
import open.dolphin.infomodel.AppointmentModel;

/**
 * AppointmentDelegater
 * 
 * @author Kazushi Minagawa
 */
public class AppointmentDelegater extends BusinessDelegater {

	public int putAppointments(ArrayList results) {

		int size = results.size();
		ArrayList<AppointmentModel> added = new ArrayList<AppointmentModel>();
		ArrayList<AppointmentModel> updated = new ArrayList<AppointmentModel>();
		ArrayList<AppointmentModel> removed = new ArrayList<AppointmentModel>();

		for (int i = 0; i < size; i++) {

			AppointmentModel model = (AppointmentModel) results.get(i);
			int state = model.getState();
			String appoName = model.getName();

			if (state == AppointmentModel.TT_NEW) {
				// V‹K—\–ñ
				added.add(model);

			} else if (state == AppointmentModel.TT_REPLACE && appoName != null) {
				// •ÏX‚³‚ê‚½—\–ñ
				updated.add(model);

			} else if (state == AppointmentModel.TT_REPLACE && appoName == null) {
				// Žæ‚èÁ‚³‚ê‚½—\–ñ
				removed.add(model);
			}
		}

		int retCode = 0;
		AppointSpec spec = new AppointSpec();
		spec.setAdded(added);
		spec.setUpdared(updated);
		spec.setRemoved(removed);

		try {
			getService().putAppointments(spec);

		} catch (Exception e) {
			processError(e);
			e.printStackTrace();
		}

		return retCode;
	}

	private RemoteKarteService getService() throws CreateException,
			RemoteException, NamingException {
		return (RemoteKarteService) getService("RemoteKarteService");
	}
}
