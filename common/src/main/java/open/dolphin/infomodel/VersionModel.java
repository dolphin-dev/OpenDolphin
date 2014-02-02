/*
 * Version.java
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
package open.dolphin.infomodel;

/**
 * VersionModel
 * 
 * @author Kazushi Minagawa
 *
 */
public class VersionModel extends InfoModel {
    
    private int number;
    private int revision;
    private String releaseNote;

    public void initialize() {
        number = 1;
    }

    public String getVersionNumber() {
        StringBuilder buf = new StringBuilder();
        buf.append(String.valueOf(number));
        buf.append(".");
        buf.append(String.valueOf(revision));
        return buf.toString();
    }

    public void setVersionNumber(String vn) {
        int index = vn.indexOf('.');
        try {
            if (index >= 0) {
                String n = vn.substring(0, index);
                String r = vn.substring(index + 1);
                number = Integer.parseInt(n);
                revision = Integer.parseInt(r);
            } else {
                number = Integer.parseInt(vn);
            }
        } catch (NumberFormatException e) {
            System.out.println(e);
        }
    }

    public void incrementNumber() {
        number++;
    }

    public void incrementRevision() {
        revision++;
    }

    public void setReleaseNote(String releaseNote) {
        this.releaseNote = releaseNote;
    }

    public String getReleaseNote() {
        return releaseNote;
    }
}
