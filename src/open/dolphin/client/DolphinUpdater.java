/*
 * DolphinUpdater.java
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
package open.dolphin.client;

import swingworker.SwingWorker;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * DolphinUpdater
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DolphinUpdater  {
    
    private ArrayList<String> downLoadList;
    private String proxyHost;
    private String proxyPort;
    private DataInputStream din;
    private ByteArrayOutputStream bo;
    private BufferedOutputStream bout;
    private ArrayList<byte[]> readBytes;
    private boolean result;
    private int current;
    private int totalLength;
    private String statMessage;
    private final String MESSAGE_APPEND = " をダウンロードしています";
    
    /** Creates new DolphinUpdater */
    public DolphinUpdater() {
    }
    
    /**
     * @param proxyHost The proxyHost to set.
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        Properties prop = System.getProperties();
        prop.put("http.proxyHost", proxyHost);
    }
    
    /**
     * @return Returns the proxyHost.
     */
    public String getProxyHost() {
        return proxyHost;
    }
    
    /**
     * @param proxyPort The proxyPort to set.
     */
    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
        Properties prop = System.getProperties();
        prop.put("http.proxyPort", proxyPort);
    }
    
    /**
     * @return Returns the proxyPort.
     */
    public String getProxyPort() {
        return proxyPort;
    }
    
    public int getTotalLength() {
        return totalLength;
    }
    
    public int getCurrent() {
        return current;
    }
    
    public boolean done() {
        return current < totalLength ? false : true;
    }
    
    public String getMessage() {
        return statMessage;
    }
    
    public boolean getResult() {
        return result;
    }
    
    public ArrayList<byte[]> getReadBytes() {
        return readBytes;
    }
    
    public ArrayList<String> getLastModified(ArrayList<String> list) {
        
        int cnt = list.size();
        ArrayList<String> ret = new ArrayList<String>(cnt);
        URL url = null;
        
        try {
            for (int i = 0; i < cnt; i++) {
                url = new URL(list.get(i));
                long lm = url.openConnection().getLastModified();
                if (lm == 0L) {
                    // jar file が存在しない場合
                    // 更新の必要なし
                }
                //System.out.println(lm);
                //System.out.println(String.valueOf(lm));
                ret.add(String.valueOf(lm));
            }
            result = true;
            
        } catch (Exception e) {
            result = false;
        }
        
        return ret;
    }
    
    public ArrayList<String> getContentLength(ArrayList<String> list) {
        
        int cnt = list.size();
        ArrayList<String> ret = new ArrayList<String>(cnt);
        URL url = null;
        int length = 0;
        
        try {
            for (int i = 0; i < cnt; i++) {
                url = new URL(list.get(i));
                length = url.openConnection().getContentLength();
                totalLength += length;
                ret.add(String.valueOf(length));
            }
            result = true;
        } catch (Exception e) {
            result = false;
        }
        
        return ret;
    }
    
    public void downLoad(ArrayList<String> list) {
        
        this.downLoadList = list;
        result = false;
        
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new ActualTask();
            }
        };
        worker.start();
    }
    
    public void cancel() {
        close();
    }
    
    private class ActualTask {
        
        ActualTask() {
            doTask();
        }
    }
    
    private void doTask() {
        
        try {
            URL url = null;
            URLConnection con = null;
            String urlString = null;
            int cnt = downLoadList.size();
            
            for (int i = 0; i < cnt; i++) {
                
                urlString = downLoadList.get(i);
                int index = urlString.lastIndexOf("/");
                statMessage = urlString.substring(index + 1) + MESSAGE_APPEND;
                url = new URL(urlString);
                con = url.openConnection();
                int contentLength = con.getContentLength();
                
                // Create streams
                din = new DataInputStream(new BufferedInputStream(con.getInputStream()));
                bo = new ByteArrayOutputStream();
                bout = new BufferedOutputStream(bo);
                byte aByte;
                int cur = 0;
                
                // Read untill EOF
                while (cur < contentLength) {
                    // Read byte
                    aByte = din.readByte();
                    bout.write(aByte);
                    cur++;
                    current++;
                }
                
                bout.flush();
                if (readBytes == null) {
                    readBytes = new ArrayList<byte[]>();
                }
                readBytes.add(bo.toByteArray());
                close();
            }
            
            result = true;
            
        } catch (Exception e) {
            e.printStackTrace();
            close();
            current = totalLength;
            result = false;
        }
    }
    
    private void close() {
        
        // Clean up
        if (din != null) {
            try {
                din.close();
            } catch(IOException ie2) {
            }
        }
        if (bout != null) {
            try {
                bout.close();
            } catch (Exception e) {
            }
        }
    }
}