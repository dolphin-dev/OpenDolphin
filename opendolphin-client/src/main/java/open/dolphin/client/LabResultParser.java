package open.dolphin.client;

import java.io.File;
import java.io.IOException;
import java.util.List;
import open.dolphin.impl.labrcv.NLaboImportSummary;

/**
 *
 * @author Kazushi Minagawa.
 */
public interface LabResultParser {

    public static final String MIHOKOKU = "未報告";
    public static final String NO_RESULT = "結果値無し";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_8 = "yyyyMMdd";
    public static final String DATE_FORMAT_10 = "yyyy-MM-dd";
    public static final String CSV_DELIM = "\\s*,\\s*";

    public List<NLaboImportSummary> parse(File file) throws IOException;

}
