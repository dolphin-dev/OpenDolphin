/*
 * FacilityID.java
 *
 * Created on 2003/03/24
 *
 * Last updated on 2003/03/24
 * Revised on 2003/03/25 'getFacilityID' is retrieved for other than user 'lasmanager'.
 *
 */
package mirrorI.dolphin.server;


import netscape.ldap.*;
import java.util.*;
import java.util.logging.*;
import mirrorI.dolphin.data.*;

/**
 *
 * This class retrieves Facilty ID from DolphinUser-Facility ID
 *
 * @author  Aniruddha, Mirror - I
 */
public class FacilityID {

    public static final String searchKey = "ou=DolphinUsers,o=Dolphin";

    private static Logger logger;

    LDAPConnection conLDAP;

    /** Creates new FacilityID */
    public FacilityID(Logger l) {

        logger = l;

        ConnectingToLDAP instance  = ConnectingToLDAP.getInstance();

        conLDAP = instance.acquireLDAPConnection();

        if(conLDAP == null) {
                logger.warning("Could not connect to LDAP");
        }
    }

    /**
     *
     * getFacilityID(), retrieves and sends facility ID from DolphinUsers<br>
     *
     * This method is called from MasterUpdate.getUpdate()<br>
     *
     */
    String getFacilityID(String dolphinUser) {
		
        //logger.finer("Method Entry");
	String facilityID = null;
        
	try{
			
            LDAPSearchResults result = conLDAP.search(searchKey, LDAPv2.SCOPE_SUB,"uid=*" ,(new String[]{"uid","facilityId"}), false);

            LDAPEntry entry =null;
            String user = null;
			
            while(result.hasMoreElements()){
				
                entry = result.next();
		Enumeration enumVals;
		LDAPAttribute attr = entry.getAttribute("uid");
				
                if (attr != null) {
                    enumVals = attr.getStringValues();
                    if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        
                        user = (String)enumVals.nextElement();
                        
                        Enumeration enumVals1;
                                
                        LDAPAttribute attr1 = entry.getAttribute("facilityId");

                        if (attr1 != null) {
                            enumVals1 = attr1.getStringValues();
                            if ( (enumVals1 != null) && enumVals1.hasMoreElements() ) {
                                facilityID = (String)enumVals1.nextElement();
                                //return  facilityID;
                                
                                if (! user.equals(dolphinUser)) {
                                    // Found regstered user
                                    break;
                                }
                            }
                        }
                            
                        
                        /*if(!((String)enumVals.nextElement()).equals(dolphinUser)){
                            Enumeration enumVals1;
                                
                            LDAPAttribute attr1 = entry.getAttribute("facilityId");
                                    
                            if (attr1 != null) {
                                enumVals1 = attr1.getStringValues();
                                if ( (enumVals1 != null) && enumVals1.hasMoreElements() ) {
                                        facilityID = (String)enumVals1.nextElement();
                                        return  facilityID;
                                }
                            }
                        }*/
                        
                        
                    }
                }
            }
        }
        catch (LDAPException lde) {
            logger.warning("LDAPException while getting facility ID");
            logger.warning( "Exception details:"  + lde );
            facilityID = null;
        }
        catch (Exception e) {
            logger.warning("Exception while getting facility ID");
            logger.warning( "Exception details:"  + e );
            facilityID = null;
        }
		
        finally{
            
            try{
                if(conLDAP != null) {
                    conLDAP.disconnect();
                    conLDAP=null;
                }
            }
            catch (LDAPException lde) {
                logger.warning("LDAPException while closing LDAP conenction");
                logger.warning( "Exception details:"  + lde );
            }
            catch (Exception e) {
                logger.warning("Exception while closing LDAP conenction");
                logger.warning( "Exception details:"  + e );
            }
        }
        //logger.finer("Method Exit");
        return  facilityID;
    }
}