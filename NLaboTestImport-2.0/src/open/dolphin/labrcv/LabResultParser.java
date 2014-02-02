package open.dolphin.labrcv;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Kazushi Minagawa.
 */
public interface LabResultParser {

    public List<NLaboImportSummary> parse(File file) throws IOException;

}
