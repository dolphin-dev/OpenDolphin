/*
 * PVTClientServer.java
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
package open.dolphin.server;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import open.dolphin.client.ClientContext;
import open.dolphin.delegater.PVTDelegater;
import open.dolphin.infomodel.PatientVisitModel;

/**
 * PVT socket server<br>
 * <br>
 * PVTServer() listen to ORCA for MML file through socket<br>
 * <br>
 * Creates 'Connection' thread on getting MML file from ORCA<br>
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 * Modified by Mirror-i corp for writing into postgreSQL
 *
 */
public class PVTClientServer implements Runnable {
    
    public static final int EOT = 0x04;
    
    public static final int ACK = 0x06;
    
    public static final int NAK = 0x15;
    
    public static final String UTF8 = "UTF8";
    
    public static final String SJIS = "SHIFT_JIS";
    
    public static final String EUC = "EUC_JIS";
    
    //public static final String AUTO_DETECT = "JISAutoDetect";
    
    private static final int DEFAULT_PORT = 5002;
    
    private static final int BUFFER_SIZE = 4096*10; // 40K
    
    private static final int READ_BUFFER_SIZE = 2042;
    
    private static ByteBuffer ackBuf = ByteBuffer.wrap(new byte[]{ACK});
    
    private static ByteBuffer nakBuf = ByteBuffer.wrap(new byte[]{NAK});
    
    private int port = DEFAULT_PORT;
    
    private String encoding = UTF8;
    
    private Logger logger;
    
    private Selector selector;
    
    private ServerSocketChannel serverSocketChannel;
    
    private ByteBuffer buffer = ByteBuffer.allocateDirect(READ_BUFFER_SIZE);

    //private static Charset charset = Charset.forName("UTF-8");
    
    //private static CharsetDecoder decoder = charset.newDecoder();
    
    private int length;
    
    private byte[] dst;
    
    private ExecutorService service;
    
    
    /** Creates new ClaimServer */
    public PVTClientServer() {
        logger = ClientContext.getLogger("pvt");
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
    
    private void setup() {
         
        try {
            selector = SelectorProvider.provider().openSelector();
            
            serverSocketChannel 
                = SelectorProvider.provider().openServerSocketChannel();
            
            serverSocketChannel.configureBlocking(false);

            InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), port);
            
            serverSocketChannel.socket().bind(address);
            
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            logger.info("PVT ServerSocket is binded " + address);
            
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn(e.toString());
        }
    }
    
    /**
     * ソケットサーバを開始する。
     */
    public void startService() {
        
        try {
            setup();
            service = Executors.newSingleThreadExecutor();
            service.execute(this);
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn(e.toString());
        }
    }
    
    /**
     * ソケットサーバを終了する。
     */    
    public void stopService() {
        
        try {
            if (service != null) {
                service.shutdownNow();
                service = null;
            }
            
            if (serverSocketChannel != null) {
                serverSocketChannel.close();
                serverSocketChannel = null;
            }
            
            if (selector != null) {
                selector.close();
                selector = null;
            }
            
            logger.info("PVT Server is stoped.");
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("IOException while stopping the ServerSocket: "
                    + e.toString());
        }
    }
            
    /**
     * ソケットサーバを開始する。
     */
    public void run() {
        
        try {
            
            while (selector.select() > 0) {
                
                Iterator keyIterator = selector.selectedKeys().iterator();
                
                while (keyIterator.hasNext()) {
                    
                    SelectionKey key = (SelectionKey) keyIterator.next();
                    keyIterator.remove();
  
                     if (key.isAcceptable()) {
                        
                        ServerSocketChannel serverSocketChannel 
                            = (ServerSocketChannel) key.channel();
                        accept(serverSocketChannel);

                    } else if (key.isReadable()) {

                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        readPvt(socketChannel);
                    }
                }
            }

            
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn(e.toString());
        }
    }
    
    /**
     * コネクションを処理する。
     */
    private void accept(ServerSocketChannel serverSocketChannel) throws IOException {
        
        SocketChannel socketChannel = serverSocketChannel.accept();

        socketChannel.configureBlocking(false);
        
        socketChannel.register(selector, SelectionKey.OP_READ);
        
        dst = new byte[BUFFER_SIZE];
        
        length = 0;
        
        logger.info(socketChannel.socket().getInetAddress() + " connected");
    }
    
    /**
     * クアライントからデータを読み込む。
     */
    private void readPvt(SocketChannel socketChannel) throws IOException {

        buffer.clear();
        int len = socketChannel.read(buffer);
        logger.debug(len + " bytes read");
        
        if (len < 0){
            socketChannel.close();
            logger.info(socketChannel.socket().getInetAddress() + " closed");
            return;
        }
        
        if (len > 0) {
        
            buffer.flip();
            buffer.get(dst, length, len);
            length += len;
        
            boolean eot = buffer.get(len -1) == EOT ? true : false;;
        
            if (eot) {
                
                logger.debug("EOT");
                String pvtXml = new String(dst, 0, length -1, encoding);
                
                buffer.clear();
                dst = null;
                
                logger.debug(pvtXml);
                
                boolean ret = addPvt(pvtXml);
                
                // Returns result code
                if (ret) {
                    logger.info("ACK");
                    socketChannel.write(ackBuf);  
                    
                } else {
                    logger.info("NAK");
                    socketChannel.write(nakBuf);  
                }
            } 
        }
    }

    /**
     * 来院情報をパースし登録する。
     */
    private boolean addPvt(String pvtXml) {
        
        logger.info("CLAIM をパース中...");
        
        BufferedReader r = new BufferedReader(
                new StringReader(pvtXml));
        PVTBuilder builder = new PVTBuilder();
        builder.setLogger(logger);
        builder.parse(r);
        PatientVisitModel model = builder.getProduct();

        // PVT を登録する
        logger.info("来院情報を登録中...");
        PVTDelegater pdl = new PVTDelegater();
        pdl.setLogger(logger);
        pdl.addPvt(model);
        
        return pdl.isNoError();
    }
    
}