package open.dolphin.ejb;

import javax.ejb.SessionContext;

import open.dolphin.exception.SecurityException;
import open.dolphin.infomodel.IInfoModel;

/**
 * DolphinService
 *
 * @author Minagawa,Kazushi
 *
 */
public class DolphinService implements IInfoModel {
    
    private static final long serialVersionUID = 5771634594361964587L;
    
    public String checkIdAsComposite(SessionContext ctx, String checkId) {
        
        // テストする ID の施設ID 部を得る
        int index = checkId.indexOf(COMPOSITE_KEY_MAKER);
        if (index < 0) {
            throw new SecurityException(checkId);
        }
        String facilityId = checkId.substring(0, index);
        
        // Caller の施設ID部を得る
        String callerId = ctx.getCallerPrincipal().getName();
        index = callerId.indexOf(COMPOSITE_KEY_MAKER);
        String callersFacilityId = callerId.substring(0, index);
        
        // 両者が一致しているかどうかを調べる
        if (!facilityId.equals(callersFacilityId)) {
            throw new SecurityException(checkId);
        }
        
        // 複合キーと見なせる
        return checkId;
    }
    
    public String checkFacility(SessionContext ctx, String checkId) {
        String callerKey = getCallersFacilityId(ctx);
        String requestKey = getRequestsFacilityId(checkId);
        if (! callerKey.equals(requestKey)) {
            throw new SecurityException(requestKey);
        }
        return checkId;
    }
    
    public String getIdAsComposite(SessionContext ctx, String testId) {
        StringBuilder sb = new StringBuilder();
        sb.append(getCallersFacilityId(ctx));
        sb.append(COMPOSITE_KEY_MAKER);
        sb.append(getIdAsLocal(testId));
        return sb.toString();
    }
    
    public String getCallersFacilityId(SessionContext ctx) {
        String callerId = ctx.getCallerPrincipal().getName();
        int index = callerId.indexOf(COMPOSITE_KEY_MAKER);
        return callerId.substring(0, index);
    }
    
    public String getIdAsQualified(String facilityId, String localIdd) {
        StringBuilder buf = new StringBuilder();
        buf.append(facilityId);
        buf.append(COMPOSITE_KEY_MAKER);
        buf.append(localIdd);
        return buf.toString();
    }
    
    public String getIdAsLocal(String testId) {
        int index = testId.indexOf(COMPOSITE_KEY_MAKER);
        return index > 0 ? testId.substring(index + 1) : testId;
    }
    
    private String getRequestsFacilityId(String checkId) {
        int index = checkId.indexOf(COMPOSITE_KEY_MAKER);
        return index > 0 ? checkId.substring(0, index) : checkId;
    }
}
