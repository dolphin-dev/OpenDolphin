package open.dolphin.project;

import java.awt.*;

import open.dolphin.client.*;
import open.dolphin.infomodel.ID;

/**
 * プロジェクトに依存するオブジェクトを生成するファクトリクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractProjectFactory {

    private static DolphinFactory dolphin;
    
    /** Creates new ProjectFactory */
    public AbstractProjectFactory() {
    }
    
    public static AbstractProjectFactory getProjectFactory(String proj) {
        
        if (dolphin == null) {
            dolphin = new DolphinFactory();
        }
        return dolphin;
    }
    
    public abstract String createCSGWPath(String uploaderAddress, String share, String facilityId);
    
    public abstract Object createAboutDialog();
    
    public abstract ID createMasterId(String pid, String facilityId);
    
    public abstract Object createSaveDialog(Window parent, SaveParams params);
}