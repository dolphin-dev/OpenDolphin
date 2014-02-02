/*
 * AskOneQuestion.java
 *
 * Created on 2001/11/28, 11:14
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import jp.ac.kumamoto_u.kuh.fc.jsato.math_mml.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.net.*;
import java.util.*;

import java.nio.*;

/**
 *
 * @author  Junzo SATO
 * @version 
 */
public class AskOneQuestion {
    AClientSocket client = null;
    boolean continueSending = true;
    boolean continueReceiving = true;
    AClientOutputStream out = null;
    AClientInputStream in = null;

    byte[] messageBytes = null;
    
    //static final int EOT = 0x04;// 4
    //static final int DLE = 0x10;// 16
    static final byte EOT = 0x04;// 4
    static final byte DLE = 0x10;// 16

    //=======================================================================================================
    /** Creates new AskOneQuestion */
    public AskOneQuestion(String host, int port, byte[] messageBytes) {
        this.messageBytes = messageBytes;
        
        try {
            client = (AClientSocket)(new AClientClientSocketFactory()).createSocket(host, port );
            if (client == null) return;
        } catch (IOException e) {
            System.out.println("Couldn't connect to the server.");
            return;
        }
        
        if (client.isBound() == false) {
            System.out.println("The socket does not successfuly bound to an address.");
            return;
        }
        
        try {
            if (client.getOutputStream() == null) {
                System.out.println("I'm gonna halt because of lack of the connection.");
                return;
            }
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
        
        sendMessage(messageBytes);
    }
    
    //=======================================================================================================
    
    public AskOneQuestion(String host, int port) {
        try {
            client = (AClientSocket)(new AClientClientSocketFactory()).createSocket(host, port );
            if (client == null) return;
        } catch (IOException e) {
            System.out.println("Couldn't connect to the server.");
            return;
        }
        
        if (client.isBound() == false) {
            System.out.println("The socket does not successfully bound to an address.");
            return;
        }
        
        try {
            if (client.getOutputStream() == null) {
                System.out.println("I'm gonna halt because of lack of the connection.");
                return;
            }
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
    }
    
    public AClientSocket getClient() {
        return client;
    }
    
    public void closeClient() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            client = null;
        }    
    }
    
    // the flag gotResult is for waiting reply from the server
    boolean gotResult = false;
    
    //==============
    String resultString = "";
    public String getResultString() {
        return resultString;
    }
    
    Mml resultMmlObject = null;
    public Mml getResultMmlObject() {
        return resultMmlObject;
    }
    
    Vector succeededItems = null;
    synchronized public Vector getSucceededItems() {
        return succeededItems;
    }
    
    Vector extRefInContent = null;
    synchronized public Vector getExtRefInContent() {
        return extRefInContent;
    }
    
    public void clearResult() {
        resultString = "";
        
        resultMmlObject = null;
        
        if (succeededItems != null) {
            succeededItems.removeAllElements();
            succeededItems = null;
        }
        
        extRefInContent = null;
    }
    //==============
    
    public String sendMessage(byte[] messageBytes) {
        this.messageBytes = messageBytes;
        
        // CLEAR gotResult flag first.
        gotResult = false;
        // reset counter
        tokenCNT = 0;
        
        clearResult();
        
        // start two threads. the one is for output message, the another is for input result
        new SenderThread("Sender").start();
        new ReceiverThread("Receiver").start();
        
        //================================================
        // WAIT FOR RESULT BY BLOCKING
        //================================================
        while (gotResult == false) {
            try {
                Thread.sleep((long)(Math.random() * 753));
            } catch (InterruptedException e) {}
        }
        
        return resultString;
    }
    
    //=======================================================================================================
    
    class SenderThread extends Thread {
        public SenderThread(String str) {
            super(str);
        }
        
        public void run() {
            continueSending = true;
            
            try {
                out = (AClientOutputStream)client.getOutputStream();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            if (out == null) {
                interrupt();
                return;
            }
            
            while (client.isConnected() && continueSending) {
                //System.out.println(getName());
                
                try {
                    // only one message is put
                    continueSending = false;
                    
                    // send the message
                    if (messageBytes != null && messageBytes.length > 0) {
                        // write byte[] to the output stream
                        out.write(messageBytes);
                    }
                    out.flush();
                } catch (UnsupportedEncodingException ec) {
                    ec.printStackTrace();
                } catch (IOException ie) {
                    ie.printStackTrace();
                    break;
                }
                
                try {
                    sleep((long)(Math.random() * 753));
                } catch (InterruptedException e) {}
            }
            
            interrupt();
            //System.out.println(getName() + " halted.");
            //System.out.println("Done sending message.");
        }
    }
    
    /*
     class HandleMmlRun implements Runnable {
        String dataStr = null;
        public HandleMmlRun(String dataStr) {
            this.dataStr = dataStr;
        }
        
        public void run() {
            // parse dataStr which has Mml instance
            //System.out.println("running HandleMmlRun");
            //System.out.println(dataStr);
            
            // pass dataStr to the parser
            new AQueryResultProcessor().processMmlString(dataStr);
        }
    }
     */
    
    int tokenCNT = 0;
    class ReceiverThread extends Thread {
        public ReceiverThread(String str) {
            super(str);
        }
        
        public void run() {
            gotResult = false;
            //
            clearResult();
            //
            continueReceiving = true;
            
            try {
                in = (AClientInputStream)client.getInputStream();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            if (in == null) {
                interrupt();
                return;
            }
            
            // Unfortunately, using ByteArrayOutputStream didn't work.
            // I define simple byte array here.
            int fixed = 1024*32;
            byte buf[] = new byte[fixed];
            int cnt = 0;
            
            String dataString = "";
            while (client.isConnected() && continueReceiving) {
                try {
                    int numBytes = ((InputStream)in).available();
                    byte c = 0x00;
                    while (numBytes > 0) {
                        c = in.readByte();
                        if (c == -1) {
                            // end of stream
                            break;
                        }
                        
                        if (c == DLE || c == EOT) {
                            ++tokenCNT;
                            
                            dataString = new String(buf, 0, cnt, "UTF8");
                            
                            //========================================================================
                            // tokenCNT == 1 means that <loginResult> or <Mml> was received.
                            // tokenCNT > 1 means that <MmlAddendum> was received after the <Mml>
                            //
                            // <loginRequest>EOT --> <loginResult>EOT
                            // [mmlQuery]EOT --> <Mml>EOT or <Mml>DLE<MmlAddendum>DLE<MmlAddendum>EOT
                            //========================================================================
                            
                            //===========================================================================
                            // pass dataString to the result processor to analyze what result was received, 
                            // <loginResult>, <Mml> or <MmlAddendum>.
                            AQueryResultProcessor proc = new AQueryResultProcessor();
                            proc.processMmlString(dataString);
                            ////////////////////////////////////////////////////
                            // get results /////////////////////////////////////
                            //System.out.println("GETTING RESULT***************" + tokenCNT);
                            if (tokenCNT == 1) {
                                resultString = proc.getResultString();
                                resultMmlObject = proc.getResultMmlObject();
                                succeededItems = proc.getSucceededItems();
                                //
                                //extRefInContent = proc.getExtRefInContent();
                                if (proc.getExtRefInContent() != null) {
                                    extRefInContent = new Vector();
                                    for (int k = 0; k < ((Vector)proc.getExtRefInContent()).size(); ++k) {
                                        
                                        //System.out.println(">>> " + 
                                        //    ((mmlCmextRef)((Vector)proc.getExtRefInContent()).elementAt(k)).getMmlCmhref() + " <<<");
                                        
                                        extRefInContent.addElement(
                                            ((Vector)proc.getExtRefInContent()).elementAt(k)
                                        );
                                    }
                                }
                            }
                            ////////////////////////////////////////////////////
                            //===========================================================================
                            
                            if (c == DLE) {
                                // prepare for next
                                buf = null;
                                buf = new byte[fixed];
                                cnt = 0;
                            } else if (c == EOT) {
                                // end of data
                                buf = null;
                                buf = new byte[fixed];
                                cnt = 0;
                                
                                numBytes = -1;
                                // end of transfer
                                continueReceiving = false;
                                // turn the flag on
                                gotResult = true;
                            }
                        } else {
                            if (cnt < buf.length) {
                                buf[cnt] = c;
                                cnt++;
                            } else {
                                byte tmp[] = new byte[buf.length + fixed];
                                System.arraycopy(buf, 0, tmp, 0, buf.length);
                                tmp[cnt] = c;
                                cnt++;
                                buf = tmp;
                            }
                        }

                        --numBytes;
                    }
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
                
                try {
                    sleep((long)(Math.random() * 753));
                } catch (InterruptedException e) {}
            }

            // DON'T CLOSE CLIENT SOCKET:-)
            /*
             if (client != null) {
                try {
                    client.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
                client = null;
            }
             */
            
            interrupt();
            //System.out.println("Done receiving message.");
        }
    }
}