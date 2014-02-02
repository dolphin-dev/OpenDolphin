/*
 * CSVTester.java
 *
 * Created on 2001/09/16, 7:11
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.util.*;

import jp.ac.kumamoto_u.kuh.fc.jsato.*;

/**
 *
 * @author  Junzo SATO
 * @copyright   Copyright (c) 2001, Junzo SATO. All rights reserved.
 */
public class CSVTester extends javax.swing.JFrame {
    static String  copyright() {
        return "Copyright 2001, Junzo Sato. All rights reserved.";
    }
    
    Vector foundFiles = new Vector();
    
    private void watchDirectory() {
        // Firstly, check the existence of the target directory to watch.
        File targetDir = new File(tfPath.getText());
        if (targetDir.exists() == false) {
            statusBean1.printlnStatus("Target directory doesn't exist.");
            // Create new directory with specified name.
            if (targetDir.mkdirs() == true) {
                statusBean1.printlnStatus("Target directory is created.");
            }
            // Check the readability of the directory
            if (targetDir.isDirectory() == false || targetDir.canRead() == false) {
                statusBean1.printlnStatus("Can't read target directory.");
            }
            // Bail out so far, because this directory has no file in it.
            return;
        }

        // Traverse target directory to find files in it.
        String flist[] = targetDir.list();
        //statusBean1.printlnStatus("Num of objects: " + flist.length);
        for (int i=0; i < flist.length; ++i) {
            if (flist[i].endsWith("csv") == false) {
                // name of the target object does not end with "csv"
                continue;
            }
            
            File file = new File(targetDir.getPath(), flist[i]);
            if (file.isFile() == false) {
                // target object is not a file
                continue;
            }
            
            //statusBean1.printlnStatus(file.getPath());
            // Append the file to the queue.
            foundFiles.addElement(file);
        }
    }
    
    private void processFiles() {
        if (foundFiles.size() <= 0) return;
        
        // Process Files
        Hashtable ht = null;
        while (foundFiles.size() > 0) {
            File f = (File)foundFiles.elementAt(0);
            
            statusBean1.printlnStatus("---------------------------------------------------------------------");
            statusBean1.printlnStatus(f.toString());
            
            //parseCSV(f.getPath());
            //new CSVParser().parse(f.getPath());
            ht = new CSVParser().parseAsHashtable(f.getPath());
            
            printlnStatus("GOT TABLE: size = " + String.valueOf(ht.size()));
            foundFiles.removeElementAt(0);
            
            String tablename = f.getName().substring(0, f.getName().length() - 4);
            System.out.println("TABLE NAME: " + tablename);
            System.out.println("TABLE PURPOSE: " + (String)ht.get(tablename));
            String k,v;
            for (Enumeration e = ht.keys(); e.hasMoreElements(); ) {
                k = (String)e.nextElement();
                v = (String)ht.get(k);
                
                System.out.println("GOT key: " + k + ", value: " + v);
            }
        }
    }
    
    private void printlnStatus(String s) {
        statusBean1.printlnStatus(s);
    }
    
    //--------------------------------------------------------------------------
    public void parseCSV(String filepath) {
    // this is faster, but cannot handle an element in form of "***,**"
    // however, this algorithm is effective for the simplest CSV file to import faster:-)
        FileReader instream = null;
        BufferedReader reader = null;

        try{
            instream = new FileReader(filepath) ;
        } catch (FileNotFoundException e) {
            printlnStatus("file not found") ;
            return;
        }

        reader = new BufferedReader(instream);

        String line = null;
        try {
            while(reader.ready() == true) {
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    continue;
                }
                // process current line
                if (line != null) {
                    //printlnStatus("line = " + line);
                    
                    int len = line.length();
                    
                    //printlnStatus("length = " + len);

                    int j = 0;
                    String s;// s[0]...s[len-1]
                    for (int i=0; i<len; i++) {
                        // get the comma separeted value
                        if (line.charAt(i) == ',') {
                            // get token 
                            s = line.substring(j,i);// from s[j] to s[i-1]

                            j=i+1;// keep char position next to ','

                            // if the s starts with '"', drop both ends.
                            // it's assumed that the substring which starts with '"' has 
                            // '"' at the end of it.
                            //
                            // "012345" ---> 012345
                            if (s.startsWith("\"")) {
                                s = s.substring(1,s.length()-1);
                            }
                            printlnStatus(s);
                        }
                    }
                    // last element
                    s = line.substring(j,len);
                    if (s.startsWith("\"")) {
                        s = s.substring(1,s.length()-1);
                    }
                    printlnStatus(s);
                }
            }
        } catch (IOException e) {

        }
    }
    //--------------------------------------------------------------------------
    // This is the better version of parseCSV.
    // It can detect comma inside an item.
    //
    // Reference: 
    // Yoshiro Yokoi, Standard XML & JAVA Programming, Shuwa Systems, 2001
    //
    public class CSVParser {
        protected String stOver, stBegin, stElement ;
        protected int nQtLeft = 0, nQtRight, nComma = 0, nRtCm, nBeginLen ;
        protected String strCm = ",", strQt = "\"" ;
        protected boolean blLastCm ; 

        private void pickupText() {
            nBeginLen = stOver.length() ;
            blLastCm = false ;
            do {
                if(nQtLeft < 0 && nComma > 0) {
                    stElement = stOver.substring(0, nComma) ;
                    stOver = stOver.substring(nComma + 1) ;
                    blLastCm = (stOver.length() == 0) ;
                    break ;
                }

                if(nQtLeft >= 0 && nComma > 0) {
                    if(nComma < nQtLeft && nComma >= 0) {
                        stElement = stOver.substring(0, nComma) ;
                        stOver = stOver.substring(nComma + 1) ; 
                        break ;
                    }
                    nQtRight = stOver.indexOf(strQt,  nQtLeft + 1) ;
                    nRtCm = stOver.indexOf(strCm, nQtRight) ;

                    if(nRtCm >= 0) { 
                    stElement = stOver.substring(0, nRtCm) ;
                    stOver = stOver.substring(nRtCm + 1) ;
                    blLastCm = (stOver.length() == 0) ;
                    break ;
                    }
                    if(nRtCm < 0) {
                    stElement = stOver ;
                    stOver = "" ;
                    break ;
                    }
                }

                if(nComma < 0) {
                    stElement = stOver ;
                    stOver = "" ;
                    break ;
                }

                if(nComma == 0) {
                    stElement = "" ;
                    stOver = stOver.substring(1) ;
                    blLastCm = (stOver.length() == 0) ;
                }
            } while(false);

            if(nBeginLen == stOver.length()) {
                printlnStatus(stBegin + " cannot be parsed.");
                return;
            }

            nQtLeft = stOver.indexOf(strQt) ;
            nComma = stOver.indexOf(strCm) ;
        } 

        public void parse(String filepath) {
            try {
                // read file and create data
                FileReader instream = null;
                BufferedReader reader = null;
                try{
                    instream = new FileReader(filepath) ;
                } catch (FileNotFoundException e) {
                    printlnStatus("File not found.");
                    return;
                }

                reader = new BufferedReader(instream);
                String line = null;
                try {
                    while(reader.ready() == true) {
                        try {
                            line = reader.readLine();
                        } catch (IOException e) {
                            continue;
                        }

                        if (line != null) {
                            //printlnStatus("line = " + line);
                            int len = line.length();
                            //printlnStatus("length = " + len);

                            // process this line
                            stBegin = stOver = line;
                            stElement = "" ;
                            nQtLeft = stOver.indexOf(strQt) ;
                            nComma = stOver.indexOf(strCm) ;

                            while(nQtLeft >= 0 || nComma >= 0 || stOver.length() > 0) {
                                stElement = "";
                                pickupText();
                                if(stElement.length() > 0) {
                                    // "12345" --> 12345
                                    if (stElement.startsWith("\"")) {
                                        stElement = stElement.substring(1,stElement.length()-1);
                                    }
                                    
                                    // print the item
                                    printlnStatus(stElement);
                                } else {
                                    // null element
                                }

                                if (blLastCm) {
                                    // in case that the string lasts with comma
                                }
                            }
                        }
                    }
                } catch (IOException e) {

                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        
        public Hashtable parseAsHashtable(String filepath) {
            Hashtable table = new Hashtable();
            
            try {
                // read file and create data
                FileReader instream = null;
                BufferedReader reader = null;
                try{
                    instream = new FileReader(filepath) ;
                } catch (FileNotFoundException e) {
                    printlnStatus("File not found.");
                    return null;
                }

                reader = new BufferedReader(instream);
                String line = null;
                try {
                    while(reader.ready() == true) {
                        try {
                            line = reader.readLine();
                        } catch (IOException e) {
                            continue;
                        }

                        if (line != null) {
                            //printlnStatus("line = " + line);
                            int len = line.length();
                            //printlnStatus("length = " + len);

                            // process this line
                            stBegin = stOver = line;
                            stElement = "" ;
                            nQtLeft = stOver.indexOf(strQt) ;
                            nComma = stOver.indexOf(strCm) ;
                            
                            int cnt = 0;
                            String key = null;
                            while(nQtLeft >= 0 || nComma >= 0 || stOver.length() > 0) {
                                stElement = "";
                                pickupText();
                                if(stElement.length() > 0) {
                                    // "12345" --> 12345
                                    if (stElement.startsWith("\"")) {
                                        stElement = stElement.substring(1,stElement.length()-1);
                                    }
                                    
                                    ++cnt;
                                    
                                    //printlnStatus("cnt = " + String.valueOf(cnt));
                                    // print the item
                                    //printlnStatus(stElement);
                                    
                                    if (cnt == 1) {
                                        // hashtable key
                                        key = new String(stElement);
                                    } else {
                                        // hashtable value
                                        table.put( key, new String(stElement) );
                                        printlnStatus("added key: " + key + ", value: " + new String(stElement));
                                        key = null;
                                    }
                                } else {
                                    // null element
                                }

                                if (blLastCm) {
                                    // in case that the string lasts with comma
                                }
                            }
                        }
                    }
                } catch (IOException e) {

                }
            } catch (Exception err) {
                err.printStackTrace();
            }
            
            return table;
        }
        
        public Vector parseAsValueVector(String filepath) {
            //Hashtable table = new Hashtable();
            Vector values = new Vector();
            
            try {
                // read file and create data
                FileReader instream = null;
                BufferedReader reader = null;
                try{
                    instream = new FileReader(filepath) ;
                } catch (FileNotFoundException e) {
                    printlnStatus("File not found.");
                    return null;
                }

                reader = new BufferedReader(instream);
                String line = null;
                try {
                    while(reader.ready() == true) {
                        try {
                            line = reader.readLine();
                        } catch (IOException e) {
                            continue;
                        }

                        if (line != null) {
                            //printlnStatus("line = " + line);
                            int len = line.length();
                            //printlnStatus("length = " + len);

                            // process this line
                            stBegin = stOver = line;
                            stElement = "" ;
                            nQtLeft = stOver.indexOf(strQt) ;
                            nComma = stOver.indexOf(strCm) ;
                            
                            int cnt = 0;
                            String key = null;
                            while(nQtLeft >= 0 || nComma >= 0 || stOver.length() > 0) {
                                stElement = "";
                                pickupText();
                                if(stElement.length() > 0) {
                                    // "12345" --> 12345
                                    if (stElement.startsWith("\"")) {
                                        stElement = stElement.substring(1,stElement.length()-1);
                                    }
                                    
                                    ++cnt;
                                    
                                    //printlnStatus("cnt = " + String.valueOf(cnt));
                                    // print the item
                                    //printlnStatus(stElement);
                                    
                                    if (cnt == 1) {
                                        // hashtable key
                                        key = new String(stElement);
                                    } else {
                                        // hashtable value
                                        //table.put( key, new String(stElement) );
                                        values.addElement(new String(stElement));
                                        printlnStatus("added key: " + key + ", value: " + new String(stElement));
                                        key = null;
                                    }
                                } else {
                                    // null element
                                }

                                if (blLastCm) {
                                    // in case that the string lasts with comma
                                }
                            }
                        }
                    }
                } catch (IOException e) {

                }
            } catch (Exception err) {
                err.printStackTrace();
            }
            
            //return table;
            return values;
        }
    }
    //--------------------------------------------------------------------------

    /** Creates new form CSVTester */
    public CSVTester() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tfPath = new javax.swing.JTextField();
        btnChoose = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        statusBean1 = new jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean();
        btnParse = new javax.swing.JButton();

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 40));
        jPanel1.setMinimumSize(new java.awt.Dimension(163, 40));
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 40));
        jLabel1.setText("Directory");
        jPanel1.add(jLabel1);

        tfPath.setText("c:\\\\MMLTable");
        tfPath.setPreferredSize(new java.awt.Dimension(400, 21));
        jPanel1.add(tfPath);

        btnChoose.setText("Choose...");
        btnChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseActionPerformed(evt);
            }
        });

        jPanel1.add(btnChoose);

        getContentPane().add(jPanel1);

        jPanel2.setMinimumSize(new java.awt.Dimension(79, 40));
        jPanel2.setPreferredSize(new java.awt.Dimension(600, 500));
        statusBean1.setPreferredSize(new java.awt.Dimension(600, 460));
        jPanel2.add(statusBean1);

        btnParse.setText("Parse");
        btnParse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnParseActionPerformed(evt);
            }
        });

        jPanel2.add(btnParse);

        getContentPane().add(jPanel2);

        pack();
    }//GEN-END:initComponents

    private void btnParseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnParseActionPerformed
        // Add your handling code here:
        watchDirectory();
        processFiles();
        //test();
    }//GEN-LAST:event_btnParseActionPerformed

    private void btnChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseActionPerformed
        // Add your handling code here:
        JFileChooser chooser = new JFileChooser("./");
        if (chooser == null) return;
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        String path;
        int selected = chooser.showOpenDialog(this.getContentPane());
        if (selected == JFileChooser.APPROVE_OPTION) {
            // selected
            File file = chooser.getSelectedFile();

            path = file.getPath();
            tfPath.setText(path);
            statusBean1.printlnStatus("Selected: " + path);
            return;
        } else if (selected == JFileChooser.CANCEL_OPTION) {
                // canceled
                return;
        }
    }//GEN-LAST:event_btnChooseActionPerformed

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    public void test() {
        File f = new File(
            CSVTester.class.getResource(
                "/jp/ac/kumamoto_u/kuh/fc/jsato/mml_table/MML0033.csv"
            ).getPath()
        );
        Hashtable ht = new CSVParser().parseAsHashtable(f.getPath());
        //
        String tablename = f.getName().substring(0, f.getName().length() - 4);
        System.out.println("TABLE NAME: " + tablename);
        System.out.println("TABLE PURPOSE: " + (String)ht.get(tablename));
        String k,v;
        for (Enumeration e = ht.keys(); e.hasMoreElements(); ) {
            k = (String)e.nextElement();
            v = (String)ht.get(k);
            System.out.println("GOT key: " + k + ", value: " + v);
        }
    }
    
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new CSVTester().show();
        
        /*
        System.out.println(
            FileUtils.fromFile(
                CSVTester.class.getResource(
                    "/jp/ac/kumamoto_u/kuh/fc/jsato/mml_table/MML0033.csv"
                ).getPath()
            )
        );
         */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton btnParse;
    private javax.swing.JTextField tfPath;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton btnChoose;
    private jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean statusBean1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
