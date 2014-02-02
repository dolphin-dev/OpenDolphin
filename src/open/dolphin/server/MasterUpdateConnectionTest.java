/*
 * MasterUpdateConnectionTest.java
 * Copyright (C) 2003 Dolphin Project. All rights reserved.
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
package open.dolphin.server;

import java.io.*;
import java.net.*;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MasterUpdateConnectionTest {

    /** Creates new PVTTestClient */
    public MasterUpdateConnectionTest() {
    }

    public static void main(String args[]) {
        
        Socket s = null;
        
        try {
            s = new Socket("202.230.44.16", 6001);
            System.out.println("connected to server");
            s.close();
        }
        catch (Exception e) {
            System.out.println(e);
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e2) {
                }
            }
        }
        System.exit(1);
    }
}
