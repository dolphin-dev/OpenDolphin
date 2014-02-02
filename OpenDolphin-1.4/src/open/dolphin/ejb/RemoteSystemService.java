package open.dolphin.ejb;

import java.util.Collection;

import open.dolphin.infomodel.UserModel;

/**
 * RemoteSystemService
 *
 * @author Minagawa, Kazushi
 */
public interface RemoteSystemService {
    
    /**
     * Ÿ‚ÌOID‚ğæ“¾‚·‚éB
     *
     * @return OID
     */
    public String helloDolphin();
    
    /**
     * {İ‚ÆŠÇ—Òî•ñ‚ğ“o˜^‚·‚éB
     * @param user {İŠÇ—Ò
     */
    public void addFacilityAdmin(UserModel user);
    
}
