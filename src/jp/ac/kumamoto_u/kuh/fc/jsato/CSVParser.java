/*
 * CSVParser.java
 *
 * Created on 2002/12/01, 16:49
 */

package jp.ac.kumamoto_u.kuh.fc.jsato;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author  Administrator
 */
public class CSVParser {
    
    /** Creates a new instance of CSVParser */
    private CSVParser() {
    }
    
    //==========================================================================
    static public void printlnStatus(String s) {
        System.out.println(s);
    }
    
    static public void parseCSV(String filepath) {
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
    
    //==========================================================================
    // This is the better version of parseCSV.
    // It can detect comma inside an item.
    //
    // Reference: 
    // Yoshiro Yokoi, Standard XML & JAVA Programming, Shuwa Systems, 2001
    //
    static protected String stOver, stBegin, stElement ;
    static protected int nQtLeft = 0, nQtRight, nComma = 0, nRtCm, nBeginLen ;
    static protected String strCm = ",", strQt = "\"" ;
    static protected boolean blLastCm ; 

    static private void pickupText() {
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

    static public void parse(String filepath) {
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
    
    static public Hashtable parseAsHashtable(String filepath) {
        // for 2 columns csv
        
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

    static public Hashtable parseAsHashtableFromStream(InputStream s) {
        // for 2 columns csv
        
        Hashtable table = new Hashtable();

        try {
            // read file and create data
            InputStreamReader instream = null;
            BufferedReader reader = null;
            instream = new InputStreamReader(s);
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

    static public Hashtable getInverseTable(Hashtable ht) {
        // key and value are inversed
        
        Hashtable inverse = new Hashtable();
        String k,v;
        for (Enumeration e = ht.keys(); e.hasMoreElements(); ) {
            k = (String)e.nextElement();
            v = (String)ht.get(k);
            // put inversed set
            inverse.put(new String(v), new String(k));
            //System.out.println("key: " + v + ", value: " + k);
        }
        return inverse;
    }
    
    static public Vector parseAsValueVector(String filepath) {
        // for 2 columns csv
        
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
    
    static public Vector parseAsValueVectorFromStream(InputStream s) {
        // for 2 columns csv
        
        //Hashtable table = new Hashtable();
        Vector values = new Vector();

        try {
            // read file and create data
            InputStreamReader instream = null;
            BufferedReader reader = null;
            instream = new InputStreamReader(s) ;
            
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
