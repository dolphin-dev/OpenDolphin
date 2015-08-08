/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.delegater;

/**
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public final class  ServerInfoDelegater extends BusinessDelegater {
    
    private static final String BASE_RESOURCE = "/serverinfo/";
    
    public boolean isCloudZero() throws Exception {
        
        // PATH
        String path = BASE_RESOURCE + "cloud/zero";
        
        // GET
        String ret = getEasyText(path, String.class);
        
        return (ret.equals("true"));
    }
    
    public String getJamri() throws Exception {
        
        // PATH
        String path = BASE_RESOURCE + "jamri";
        
        // GET
        String ret = getEasyText(path, String.class);
                
        return ret;
    }
}
