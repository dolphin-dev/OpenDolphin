/*
 * BackGround.java
 *
 * Created on 2001/11/30, 20:07
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

/**
 *
 * @author  Junzo SATO
 * @version 
 */
import java.io.*;

import open.dolphin.client.*;
import open.dolphin.project.*;
import open.dolphin.util.MMLDate;

import jp.ac.kumamoto_u.kuh.fc.jsato.*;

public class BackGround {
    /** Creates new BackGround */
    public BackGround() {
        System.out.println("DIRECTORY: " + ClientContext.getUserDirectory());
    }

    final int EOT = 0x04;// 4
    final int DLE = 0x10;// 16

    public AskOneQuestion questionObj = null;

    public void LogOut() {
        // close client socket
        if (questionObj != null) {
            questionObj.closeClient();
            questionObj = null;
        }
    }
    
    public boolean RequestLogin() {
        // here, we gonna get connected with CSGW so that we can start searching area data
        
        //==========================================================
        // get user info
        //ServerConnection sc = Karte.getServerConnection();
        String host = Project.getHostAddress();
        int port = 5101;
        String userId = Project.getUserId();
        String passwd = Project.getPasswd();
        /*
        System.out.println("Host Address: " + sc.getHostAddress());
        System.out.println("User ID: " + sc.getUserId());
        System.out.println("Password: " + sc.getAuthPasswd());
         */
        //==========================================================
        
        // Create <loginRequest>
        String mml = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
            "<loginRequest>\n" + 
            "\t" + "<userId>" + userId + "</userId>\n" + 
            "\t" + "<userPassword>" + passwd + "</userPassword>\n" + 
            "</loginRequest>";
        byte[] messageBytes = null;
        try {
            // create UTF8 bytes from the message ---------------------------------------
            byte[] mmlByte = mml.getBytes("UTF8");
            int bufLen = mmlByte.length + 1;
            //---
            //==============================================================================
            // DEBUG
            /*
             System.out.println(
                "SENDING DATA ******************************************\n" + 
                new String(mmlByte, "UTF8") + 
                "********************************************************" );
             */
            FileUtils.toFile(
                ClientContext.getUserDirectory() + "/" + new String(MMLDate.getDateTime() + "SND.txt").replaceAll(":", ""),
                new String(mmlByte, "UTF8")
            );
            try { Thread.sleep(1000); } catch (Exception e) {}
            //==============================================================================
            //---
            messageBytes = new byte[bufLen];
            System.arraycopy(mmlByte, 0, messageBytes, 0, mmlByte.length);
            messageBytes[mmlByte.length] = EOT;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Establish connection
        AskOneQuestion qObj = new AskOneQuestion(host, port);
        // Send <loginRequest>
        if (qObj != null &&
            qObj.getClient() != null && 
            qObj.getClient().isConnected() == true) {
            qObj.sendMessage(messageBytes);
            //============================================
            // BLOCKING BY WAITING FOR RESULT 
            //============================================
            // Receive <loginResult>
            String loginResult = qObj.getResultString();
            //System.out.println("LOGIN RESULT ***** " + loginResult);
            if (loginResult.equals("succeeded")) {
                // keep the socket be opened instead of closing it.
                // qObj.closeSocket();
                questionObj = qObj;
                
                // DEBUG
                // Do more with the server...
                
                return true;
            } else {
                return false;
            }
        } else {
            System.out.println("SOMETHING WRONG WITH CONNECTION");
            // just discard the obj.
            // I don't care what's going on *heehee*
            questionObj = null;
            return false;
        }
    }
    
    public void SendQuery(String mml) {
        byte[] messageBytes = null;
        try {
            // create UTF8 bytes from the message ---------------------------------------
            byte[] mmlByte = mml.getBytes("UTF8");
            
            int bufLen = mmlByte.length + 1;
            messageBytes = new byte[bufLen];
            System.arraycopy(mmlByte, 0, messageBytes, 0, mmlByte.length);
            messageBytes[mmlByte.length] = EOT;

            //---
             /*
              System.out.println(
                "SENDING DATA ******************************************\n" + 
                new String(mmlByte, "UTF8") + 
                "********************************************************" );
              */
            //---
            //***********************************************************************************
            // DEBUG
            /*
            FileUtils.toFile(
                new String("/dummy/" + MMLDate.getDateTime() + "SND.txt").replaceAll(":", ""),
                new String(mmlByte, "UTF8")
            );
             */
            FileUtils.toFile(
                ClientContext.getUserDirectory() + "/" + new String(MMLDate.getDateTime() + "SND.txt").replaceAll(":", ""),
                new String(mmlByte, "UTF8")
            );
            try { Thread.sleep(1000); } catch (Exception e) {}
            //***********************************************************************************
        
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }        

        questionObj.sendMessage(messageBytes);
    }
}
