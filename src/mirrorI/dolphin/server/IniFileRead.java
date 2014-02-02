/*
 * IniFileRead.java
 *
 * Created on 2003/01/30
 *
 * Last updated on 2003/02/07
 *
 */

package mirrorI.dolphin.server;

import java.io.*;
import java.util.*;

/**
 *
 * @author Prashanth Kumar, Mirror-i Corp.
 *
 */
public class IniFileRead {

    private Properties iniFileParameter = null;

    /** Creates new IniFileRead */
    public IniFileRead() {
    }

    /**
     * readIniFile() reads INI file and stores in 'iniFileParameter' property
     *
     */
    public Properties readIniFile(){

        String iniFileName = null;
        String osName = System.getProperty("os.name");

        //Get INI file path based on OS
        if (osName.equals("Linux")) {
            iniFileName ="/usr/local/dolphin/dolphin.ini";
        }
        else {
            iniFileName ="C:\\dolphin\\Dolphin.ini";
        }

        try{
            File fileIniFile = new File(iniFileName);
            if(!fileIniFile.isFile()){
                System.out.println("INI file could not found: " +iniFileName );
                System.exit(1);
            }
            iniFileParameter = new Properties();
            FileInputStream fisIniFile = new FileInputStream(fileIniFile);
            iniFileParameter.load(fisIniFile);
        
        } catch(FileNotFoundException e)  {
            System.out.println("INI file could not found: " +iniFileName );
            iniFileParameter = null;
        } catch(IOException e) {
            System.out.println("IO Exception while reading: " +iniFileName );
            iniFileParameter = null;
        }
        return iniFileParameter;
    }
}