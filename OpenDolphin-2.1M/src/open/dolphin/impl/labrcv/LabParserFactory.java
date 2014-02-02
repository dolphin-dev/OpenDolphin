package open.dolphin.impl.labrcv;

import open.dolphin.client.LabResultParser;
import open.dolphin.impl.falco.HL7Falco;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class LabParserFactory {

    public static LabResultParser getParser(String key) {

        // key == filename
        
        if (key.toLowerCase().endsWith(".dat")) {
            return new NLabParser();

        } else if (key.toLowerCase().endsWith(".dat2")) {
            return new Dat2Parser();

        } else if (key.toLowerCase().endsWith(".hl7")) {
            return new HL7Falco();

        } else if (key.toLowerCase().endsWith(".txt")) {
            return new WolfParser();
        }

        return null;
    }
}
