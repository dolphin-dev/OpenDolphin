/*
 * CSGWTestClient.java
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
public class CSGWTestClient {
    
    public static final int ACK            = 0x06;
    public static final int NAK            = 0x15;
    public static final int CR             = 0x0D;

    /** Creates new CSGWTestClient */
    public CSGWTestClient() {
    }
    
    public static void main(String args[]) {
        
        Socket s = null;
        
        try {
            s = new Socket("172.168.158.2", 5001);
            //String name = "a.jpg";            
            File f = new File("D:/develop-2001/Dec022001/open/dolphin/resources/claim_ORCA_2.xml");
            long len = f.length();
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(f));
            byte[] bytes =new byte[(int)len];
            bin.read(bytes, 0, (int)len);

            BufferedOutputStream writer = new BufferedOutputStream(new DataOutputStream(s.getOutputStream()));
            BufferedInputStream reader = new BufferedInputStream(new DataInputStream(s.getInputStream()));
            
            // Send filename
            //String fileName = name;
            //writer.write(fileName.getBytes());
           //writer.write(CR);
            
            // Send data length
           // String byteString = String.valueOf((int)len);
           // writer.write(byteString.getBytes());
           // writer.write(CR);
            
            // Send data
            writer.write(bytes);
            writer.write(0x04);
            writer.flush();

            // Read result
            int c = reader.read();
            if (c == ACK) {
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
        System.exit(1);
    }    
}