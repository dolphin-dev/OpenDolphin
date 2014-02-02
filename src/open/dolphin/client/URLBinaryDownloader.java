/*
 * URLBinaryDownloader.java
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

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.beans.*;

/**
 * Binary resource downloader.
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class URLBinaryDownloader implements Runnable {
    
    public static final String READ_BYTES_PROP = "readBytesProp";
    
    private String urlSpec;
    private String message;
    private byte[] readBytes;
    private PropertyChangeSupport boundSupport;
    
    private ProgressMonitor monitor;
    private int contentLength;
    private int current;
    private boolean updateScheduled;
    private boolean cancelScheduled;
    private boolean canceled;
    private int resultCode = -1;
            
    /**
     * Crates new URLBinaryDownloader
     */
    public URLBinaryDownloader()  {                    
        boundSupport = new PropertyChangeSupport(this);   
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(READ_BYTES_PROP, l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(READ_BYTES_PROP, l);
    }  
            
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String msg) {
        message = msg;
    }
    
    public String getURLString() {
        return urlSpec;
    }
    
    public void setURLString(String url) {
        this.urlSpec = url;
    }
      
    public byte[] getReadBytes() {
        return readBytes;
    }
    
    private void notifyResult() {
        if (resultCode != 1) {
            readBytes = null;
        }
        boundSupport.firePropertyChange(READ_BYTES_PROP, null, readBytes);
    }
      
    public void run() {
                
        DataInputStream din = null;
        ByteArrayOutputStream bo = null;
        BufferedOutputStream bout = null;

        try {
            // Connect to the URL
            URL url = new URL(urlSpec);
            URLConnection con = url.openConnection();
            
            // Read the content length
            contentLength = con.getContentLength();
            
            // Create ProgressMonitor
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    monitor = new ProgressMonitor(
                                  (Component)null,
                                  message, 
                                  null,
                                  0,
                                  contentLength);
                }
            }); 
            
            // Create streams
            din = new DataInputStream(new BufferedInputStream(con.getInputStream()));
            bo = new ByteArrayOutputStream();
            bout = new BufferedOutputStream(bo);
            byte aByte;
            
            // Read untill EOF
            while (true) {
                
                if (canceled) {
                    resultCode = 0;
                    break;
                }

                // Read byte
                aByte = din.readByte();
                bout.write(aByte);
                current++;

                // Update progressbar
                if (updateScheduled == false) {                     
                    updateScheduled = true;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            monitor.setProgress(current);
                            updateScheduled = false;
                        }
                    }); 
                }
                
                // Check cancel
                if (cancelScheduled == false) {                     
                    cancelScheduled = true;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            canceled = monitor.isCanceled();
                            cancelScheduled = false;
                        }
                    }); 
                }
            }
        }
        catch (EOFException eof) {
            resultCode = (current == contentLength) ? 1 : -1;
            if (resultCode != 1) {
                if (monitor != null) {
                    monitor.close();
                }
                showErrorMessage(eof.toString());
            }
        }
        catch (IOException ie) {
            if (monitor != null) {
                monitor.close();
            }
            showErrorMessage(ie.toString());
        }
        
        // Clean up
        if (din != null) {
            try {
                din.close();
            }
            catch(IOException ie2) {
            }
        }
        if (bout != null) {
            try {
                bout.flush();
                readBytes = bo.toByteArray();
                bout.close();
            }
            catch (Exception e) {
            }
        }
        
        notifyResult();
    }
    
    private void showErrorMessage(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(
                    (Component)null,
                    "ダウンロード中にエラーが発生しました。" + msg,
                    "Dolphin: Software download",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}