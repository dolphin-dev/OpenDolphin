/*
 * PVTTestClient.java
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
package open.dolphin.server;

import java.io.*;
import java.net.*;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PVTTestClient {

    /** Creates new PVTTestClient */
    public PVTTestClient() {
    }

    public static void main(String args[]) {
        
        Socket s = null;
        
        for (int i = 0; i < 1; i++) {
        
            try {
                //s = new Socket("192.168.4.40", 5002);
                s = new Socket("172.168.158.2", 5002);
                //s = new Socket("10.255.253.30", 5002);
                System.out.println("connected to server");

                File f = new File("D:/develop/hippocrates/open/dolphin/resources/claim_ORCA_2.xml");
                //File f = new File("D:/develop-2001/src/sample/claim_send_front0000000009-1.xml");            
                //File f = new File("D:/dolphin-2003/src/20030416204752-1.xml");
                //File f = new File("D:/dolphin-2003/src/20030317155904-1.xml");
                long len = f.length();
                BufferedInputStream bin = new BufferedInputStream(new FileInputStream(f));
                byte[] bytes =new byte[(int)len];
                bin.read(bytes, 0, (int)len);
                String data = new String(bytes, "SHIFT_JIS");
                //String data = new String(bytes, "UTF8");

                BufferedOutputStream writer = new BufferedOutputStream(new DataOutputStream(s.getOutputStream()));
                BufferedInputStream reader = new BufferedInputStream(new DataInputStream(s.getInputStream()));

                // Write UTF8 data
                writer.write(data.getBytes("UTF8"));
                //writer.write(bytes);
                writer.write(0x04);
                writer.flush();

                // Read result
                int c = reader.read();
                if (c == 0x06) {
                    System.out.println("ACK");
                }
                else {
                    System.out.println("NAK");
                }
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
        }
        System.exit(1);
    }
}
