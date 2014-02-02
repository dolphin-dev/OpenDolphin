package open.dolphin.impl.labrcv;

import open.dolphin.client.LabResultParser;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class LabParserFactory {

    public static LabResultParser getParser(String key) {
        try {
            String clsName = null;
            if (key.toLowerCase().endsWith(".dat")) {
                clsName = "open.dolphin.impl.labrcv.NLabParser";

            } else if (key.toLowerCase().endsWith(".dat2")) {
                clsName = "open.dolphin.impl.labrcv.Dat2Parser";

            } else if (key.toLowerCase().endsWith(".hl7")) {
                clsName = "open.dolphin.impl.falco.HL7Falco";

            } else if (key.toLowerCase().endsWith(".txt")) {
                clsName = "open.dolphin.impl.labrcv.WolfParser";
            
            } else if (key.toLowerCase().endsWith(".csv")) {
                clsName = "open.dolphin.impl.labrcv.Dat2Parser";
            }
            
            LabResultParser ret = (LabResultParser) Class.forName(clsName).newInstance();
            return ret;
            
        } catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
}
