/*
 * CSGWServer.java
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

import java.util.*;
import java.io.*;
import java.net.*;

import open.dolphin.util.*;

/**
 * CSGW socket server.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class CSGWServer  extends Thread {
    
    public static final int ACK            = 0x06;
    public static final int NAK            = 0x15;
    public static final int CR             = 0x0D;
    
    private static final int DEFAULT_PORT = 5021;
    
    private int port = DEFAULT_PORT;
    
    private ServerSocket listenSocket;
    
    private String destPath;
    
    private Trace trace;

    /** Creates new ClaimServer */
    public CSGWServer() {
        super();
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getDestPath() {
        return destPath;
    }
    
    public void setDestPath(String enc) {
        destPath = enc;
    }    
    
    public Trace getTrace() {
        return trace;
    }
    
    public void setTrace(Trace trace) {
        this.trace = trace;
    }
    
    public void startServer() {
        try {
            listenSocket = new ServerSocket(port);
            if (trace != null) {
                trace.debug("Server listening on port " + port);
            }
            this.start();
        }
        catch (IOException e) {
            if (trace != null) {
                trace.error("Exception creating socket: " + e.toString());
            }
        }
    }
    
    public void stopServer() {
        if (listenSocket != null) {
            try {
                listenSocket.close();
                listenSocket = null;
            }
            catch (Exception e) {
                if (trace != null) {
                    trace.error("Exception closing socket: " + e.toString());
                }
            }
        }
    }
    
    public void restartServer() {
        stopServer();
        startServer();
    }
    
    public void run() {
        
        try {
            while (true) {
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
            }
        }
        catch (IOException e) {
            if (trace != null) {
                trace.error("Exception while listening for connections");
            }
        }
    }
    
    protected final class Connection extends Thread {
        
        private BufferedInputStream reader;
        private BufferedOutputStream writer;
        private Socket client;
        
        public Connection(Socket clientSocket) {
            super();
            
            client = clientSocket;
            
            if (trace != null) {
                String addr = client.getInetAddress().getHostAddress();
                String time = MMLDate.getDateTime(new GregorianCalendar());
                trace.debug("Connected from " + addr + " at " + time);
            }
            
            try {
                reader = new BufferedInputStream(new DataInputStream(client.getInputStream()));
                writer = new BufferedOutputStream(new DataOutputStream(client.getOutputStream()));
                
                this.start();
            }
            catch (IOException e) {
                try {
                    client.close();
                }
                catch (IOException e2) {
                }
                if (trace != null) {
                    trace.error("Exception while getting socket streams: " + e.toString());
                }
            }
        }
        
        public void run() {
            
            int c;
            ByteArrayOutputStream line = new ByteArrayOutputStream();
            int state = 0;
            String fileName = null;
            int length;
            
            try {
                while (true) {
                    
                    if ( (c = reader.read()) == CR ) {

                        if (state == 0) {
                            fileName = line.toString();
                            line = new ByteArrayOutputStream();
                            state++;
                        }
                        else {
                            length = Integer.parseInt(line.toString());
                            break;
                        }
                    }
                    else {
                        line.write(c);
                    }
                }
                
                byte[] buf = new byte[length];
                reader.read(buf, 0, length);
                
                StringBuffer sb = new StringBuffer();
                sb.append(destPath);
                sb.append("/");
                sb.append(fileName);
                String path = sb.toString();
                sb.append(".tmp");
                String temp = sb.toString();
                File f = new File(temp);
                if (f.exists()) {
                    f.delete();
                }
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
                out.write(buf);
                out.flush();
                out.close();
                boolean result = f.renameTo(new File(path));

                // Returns result code
                if (result) {
                    writer.write(ACK);
                }
                else {
                    writer.write(NAK);
                }
                writer.flush();
                
                // Close socket
                client.close();
                client = null;
            }
            catch (IOException e) {   
                if (trace != null) {
                    trace.error(e.toString());
                }
            }
            finally {
                if (client != null) {
                    try {
                        client.close();
                    }
                    catch (IOException e2) {
                    }
                }
            }
        }
    }
    
    public static void main(String args[]) {
        
        CSGWServer server = new CSGWServer();
        Trace trace = new SystemTrace();
        trace.setDebug(true);
        server.setTrace(trace);
        server.setDestPath("D:/develop-2001/");
        server.startServer();
        Socket s = null;
        
        try {
            s = new Socket("localhost", 5021);
            //System.out.println("connected to server");
            
            //File f = new File("D:/develop-2001/src/claim-test.xml");
            //File f = new File("D:/develop-2001/src/kenho20011004.xml");
            String name = "picture.jpg";
            //String name = "kenho20011004.xml";
            
            File f = new File("D:/develop-2001/src/" + name);
            long len = f.length();
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(f));
            byte[] bytes =new byte[(int)len];
            bin.read(bytes, 0, (int)len);

            BufferedOutputStream writer = new BufferedOutputStream(new DataOutputStream(s.getOutputStream()));
            BufferedInputStream reader = new BufferedInputStream(new DataInputStream(s.getInputStream()));
            
            // Send filename
            String fileName = name;
            writer.write(fileName.getBytes());
            writer.write(CR);
            
            // Send data length
            String byteString = String.valueOf((int)len);
            writer.write(byteString.getBytes());
            writer.write(CR);
            
            // Send data
            writer.write(bytes);
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