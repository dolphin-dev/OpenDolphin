/*
 * ClaimServer.java
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
import java.text.*;

/**
 * Claim socket server.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClaimServer  extends Thread {
    
    public static final int EOT             = 0x04;
    public static final int ACK             = 0x06;
    public static final int NAK             = 0x15;
    public static final String UTF8         = "UTF8";
    public static final String SJIS         = "SHIFT_JIS";
    public static final String EUC          = "EUC_JIS";
    public static final String AUTO_DETECT  = "JISAutoDetect";
    
    private static final int DEFAULT_PORT = 5001;
    
    private int port = DEFAULT_PORT;
    
    private ServerSocket listenSocket;
    
    private String encoding = UTF8;
    
    private Trace trace;

    /** Creates new ClaimServer */
    public ClaimServer() {
        super();
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getEncoding() {
        return encoding;
    }
    
    public void setEncoding(String enc) {
        encoding = enc;
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
                String time = DateFormat.getDateTimeInstance().format(new Date());
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
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            String recieved = null;
            
            try {
                while (true) {
                    if ( (c = reader.read()) == -1 ) {
                        throw new IOException("Exception while reading streams");
                    }
                    else if (c == EOT) {
                        // EncodingToUnicode
                        recieved = buf.toString(encoding);
                        if (trace != null) {
                            int len = recieved.length();
                            trace.debug("recieved EOT length = " + len + " bytes");
                        }
                        break;
                    }
                    else {
                        buf.write(c);
                    }
                }
                trace.debug(recieved);
                boolean result = true;
                
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
        ClaimServer server = new ClaimServer();
        Trace trace = new SystemTrace();
        trace.setDebug(true);
        server.setTrace(trace);
        server.setEncoding("UTF8");
        server.startServer();
    }
}
