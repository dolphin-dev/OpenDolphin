package open.dolphin.client;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import open.dolphin.impl.labrcv.NLaboImportSummary;

/**
 *
 * @author Kazushi Minagawa.
 */
public interface LabResultParser {

    public static final String MIHOKOKU = java.util.ResourceBundle.getBundle("open/dolphin/client/resources/LabResultParser").getString("text.unReported");
    public static final String NO_RESULT = java.util.ResourceBundle.getBundle("open/dolphin/client/resources/LabResultParser").getString("text.noResultValue");
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_8 = "yyyyMMdd";
    public static final String DATE_FORMAT_10 = "yyyy-MM-dd";
    public static final String CSV_DELIM = "\\s*,\\s*";
    
    public List<NLaboImportSummary> parse(Path path) throws IOException;
}
