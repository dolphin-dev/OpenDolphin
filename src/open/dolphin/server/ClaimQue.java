/*
 * ClaimQue.java
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

import javax.swing.*;

import open.dolphin.client.*;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * Work Queue to send CLAIM sever CLAIM instance.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClaimQue {
    
    // Socket constants
    private final int EOT         = 0x04;
    private final int ACK         = 0x06;
    private final int NAK         = 0x15;
    private final String UTF8     = "UTF8";
    
    // Alert constants
    private final int TT_QUEUE_SIZE         = 0;
    private final int TT_NAK_SIGNAL         = 1;
    private final int TT_SENDING_TROUBLE    = 2;
    private final int TT_CONNECTION_REJECT  = 3;
    
    // Strings
    private final String proceedString = "継続";
    private final String dumpString    = "ファイルへ記録";
    
    // Singleton
    private static final ClaimQue instance = new ClaimQue();
    
    private LinkedList queue;    
    private String host;    
    private int port = 5001;    
    private String enc = UTF8;    
    private int watingTime = 3*60;          // 3 minuets. 
    private int sleepTime = 3;              // 30 sec.
    private int alertQueueSize = 5;
    private String journalDir = "CLAIM-JOURNALS";
    private Trace trace;

    /** Creates new ClaimQue */
    private ClaimQue() {
        super();
        
        queue = new LinkedList();
        Kicker kicker = new Kicker();
        kicker.start();
    }
    
    public static ClaimQue sharedInstance() {
        return instance;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getEncoding() {
        return enc;
    }
    
    public void setEncoding(String enc) {
        this.enc = enc;
    }
    
    public void setTrace(Trace t) {
        this.trace = t;
    }
    
    public int getWatingTime() {
        return watingTime;
    }
    
    public void setWatingTime(int val) {
        watingTime = val;
    }
    
    public String getJournalDirectory() {
        return journalDir;
    }
    
    public void setJournalDirectory(String val) {
        journalDir = val;
    }
    
    public int getAlertQueueSize() {
        return alertQueueSize;
    }
    
    public void getAlertQueueSize(int val) {
        alertQueueSize = val;
    }
    
    public synchronized void addWork(Object o) {
        queue.addLast(o);
        
        if ( (queue.size() % alertQueueSize) == 0) {
            int option = alertDialog(TT_QUEUE_SIZE);
            if (option == 1) {
                // Already get lock
                int size = queue.size();
                String data = null;
                StringBuffer buf;
                String fileName = getDateTime();
                
                for (int i = 0; i < size; i++) {
                    data = (String)queue.removeFirst();
                    buf = new StringBuffer();
                    buf.append(fileName);
                    buf.append("-");
                    buf.append(i);
                    writeJournal(buf.toString(), data);
                }
            }
            else {
                notify();
            }
        }
        else {
            notify();
        }
    }
    
    public synchronized Object getWork() throws InterruptedException {        
       while (queue.size() == 0) {
          wait();
       }
       return queue.removeFirst();
    }
    
    public synchronized int getQueueSize() {
        return queue.size();
    }
    
    public void dump() {        
        int size = getQueueSize();
        if (size == 0) {
            return;
        }
        String claim;
        StringBuffer buf;
        String fileName = getDateTime();
        
        for (int i = 0; i < size; i++) {
            try {
                claim = (String)getWork();
                buf = new StringBuffer();
                buf.append(fileName);
                buf.append("-");
                buf.append(i);
                writeJournal(buf.toString(), claim);
            }
            catch (InterruptedException e) {
            }
        }
    }
    
    private void log(String msg) {
        if (trace != null) {
            trace.debug(msg);
        }
    }

    private void error(String msg) {
        if (trace != null) {
            trace.error(msg);
        }
    }

    private int alertDialog(int code) {

        int option = -1;
        String title = "Dolphin: CLAIM 送信";
        StringBuffer buf = null;
        
        switch(code) {
            
            case TT_QUEUE_SIZE:
                buf = new StringBuffer();
                buf.append("未送信のCLAIM(レセプト)データが");
                buf.append(queue.size());
                buf.append(" 個あります。CLAIM サーバの電源及び接続を確認してください。\n");
                buf.append("1. このまま処理を継続することもできます。\n");
                buf.append("2. 未送信データをファイルに記録することができます。\n");
                buf.append("   この場合、データは送信されず、診療報酬は手入力となります。");
                
                option = JOptionPane.showOptionDialog(
                            null, 
                            buf.toString(),
                            title,
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.WARNING_MESSAGE,null,
                            new String[]{proceedString, dumpString},proceedString);  
                break;

            case TT_NAK_SIGNAL:
                buf = new StringBuffer();
                buf.append("CLAIM(レセプト)データがサーバにより拒否されました。\n");
                buf.append("送信中のデータはファイルに記録します。診療報酬の自動入力はできません。");
                JOptionPane.showMessageDialog(
                        null, 
                        buf.toString(),
                        title,
                        JOptionPane.ERROR_MESSAGE);
                break;

            case TT_SENDING_TROUBLE:
                buf = new StringBuffer();
                buf.append("CLAIM(レセプト)データの送信中にエラーがおきました。\n");
                buf.append("送信中のデータはファイルに記録します。診療報酬の自動入力はできません。");
                JOptionPane.showMessageDialog(
                        null, 
                        buf.toString(),
                        title,
                        JOptionPane.ERROR_MESSAGE);
                break;

            case TT_CONNECTION_REJECT:
                buf = new StringBuffer();
                buf.append("CLAIM(レセプト)サーバが ");
                buf.append(watingTime);
                buf.append(" 秒以上応答しません。サーバの電源及び接続を確認してください。\n");
                buf.append("1. このまま処理を継続することもできます。\n");
                buf.append("2. データをファイルに記録することができます。\n");
                buf.append("   この場合、データは送信されず、診療報酬は手入力となります。");
                
                option = JOptionPane.showOptionDialog(
                            null, 
                            buf.toString(),
                            title,
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.WARNING_MESSAGE,null,
                            new String[]{proceedString, dumpString},proceedString);
                break;
        }
        
        return option;
    }

    // Records error & data to the error-file
    private void writeJournal(String fileName, String data) {

        try {
            String path = ClientContext.getUserDirectory();
            path = path + File.separator + journalDir;
            File f = new File(path);
            if (! f.exists()) {
                f.mkdirs();
            }
            path = path + File.separator + fileName + ".xml";
            f = new File(path);
            BufferedOutputStream  writer = new BufferedOutputStream(new FileOutputStream(f));
            writer.write(data.getBytes(enc));
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            error("Exception while writing " + journalDir + ": " + e.toString());
        }
    }

    private String getDateTime() {

        GregorianCalendar gc = new GregorianCalendar();
        StringBuffer buf = new StringBuffer();

        // yyyy-mm-dd
        buf.append (gc.get(Calendar.YEAR));
        buf.append ("-");
        int val = gc.get(Calendar.MONTH);
        val++;
        if (val < 10) {
            buf.append("0");
        }
        buf.append (val);
        buf.append ("-");
        val = gc.get(Calendar.DAY_OF_MONTH);
        if (val < 10) {
            buf.append("0");
        }
        buf.append(val);

        // -hh-mm-ss
        buf.append("-");
        val = gc.get(Calendar.HOUR_OF_DAY);
        if (val < 10) {
            buf.append("0");
        }
        buf.append(val);
        buf.append ("-");
        int m = gc.get (Calendar.MINUTE);
        m++;
        if (m < 10) {
            buf.append("0");
        }        
        buf.append (m);
        buf.append ("-");
        val = gc.get(Calendar.SECOND);
        if (val < 10) {
            buf.append("0");
        }
        buf.append(val);

        return buf.toString();
    }
    
    protected class Kicker extends Thread {
        
        public void run() {
            
            String claim = null;
            Socket socket = null;
            String data = null;
            BufferedOutputStream writer;
            BufferedInputStream reader;
            
            while (true) {
       
                try {                     
                    // Gets object                   
                    data = (String)getWork();
                    log("got claim data");
                    log(data);
                    
                    // Gets connection                
                    socket = getSocket();
                    if (socket == null) {
                        String fileName = getDateTime() + "-1";
                        writeJournal(fileName, data);
                        continue;
                    }
                    log("got claim socket");
                    
                    // Gets io stream
                    writer = new BufferedOutputStream(new DataOutputStream(socket.getOutputStream()));
                    reader = new BufferedInputStream(new DataInputStream(socket.getInputStream()));
            
                    // Writes UTF8 data
                    writer.write(data.getBytes(enc));
                    writer.write(EOT);
                    writer.flush();   
                    log("sent claim data");
                    
                    // Reads result
                    int c = reader.read();
                    if (c == ACK) {
                        log("recieved ACK, transaction succeded");
                    }
                    else if (c == NAK) {
                        log("recieved NAK, transaction failed. recordes the data to the erro-log");
                        alertDialog(TT_NAK_SIGNAL);
                        String fileName = getDateTime() + "-1";
                        writeJournal(fileName, data);
                    }
                    socket.close();
                }
                catch (Exception e) {
                    alertDialog(TT_SENDING_TROUBLE);
                    if (data != null) {
                        String fileName = getDateTime() + "-1";
                        writeJournal(fileName, data);
                    }
                }
            }
        }
        
        private Socket getSocket() {
            
            Socket s = null;
            long enter = System.currentTimeMillis();
            
            while (true) {
                try {
                    s = new Socket(host, port);
                    break;
                }
                catch (Exception e) {
                    long failed = System.currentTimeMillis();
                    if ( ((failed - enter)/1000) > watingTime ) {
                        int option = alertDialog(TT_CONNECTION_REJECT);
                        if (option == 1) {
                            break;
                        }
                        else {
                            enter = System.currentTimeMillis();
                        }
                    }
                
                    // Sleep a while
                    try {                       
                        sleep(sleepTime*1000);
                    }
                    catch (Exception e2) {
                    }
                }
            }
            return s;
        }
    }
}