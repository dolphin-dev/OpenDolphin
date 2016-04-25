package open.dolphin.project;

import static open.dolphin.project.Project.CLAIM_SENDER;

/**
 *
 * @author Kazushi Minagawa. Lab
 */
public class StubI18N extends ProjectStub {
    
    private final String BASE_URI = "http://localhost";
    
    private final String CONTEXT_ROOT = "/openDolphin/resources";
    
    private String baseURI;

    @Override
    public String getBaseURI() {
        if (baseURI==null) {
            baseURI = createBaseURI(BASE_URI, CONTEXT_ROOT);
        }
        return baseURI;
    }
    
    @Override
    public String getServerURI() {
        return getServer();
    }

    @Override
    public void setServerURI(String val) {
    }
    
    @Override
    public String getSchema() {
        return "http";
    }
    
    @Override
    public String getServer() {
        return "test.open.dolphin";
    }
    
    @Override
    public String getPort() {
        return "8080";
    }
    
    @Override
    public boolean isTester() {
        return true;
    }
    
    @Override
    public boolean claimSenderIsClient() {
        String test = getString(CLAIM_SENDER);
        return (test!=null && test.equals("client"));
    }
    
    @Override
    public boolean claimSenderIsServer() {
        return false;
    }
    
    @Override
    public boolean canAccessToOrca() {
        // In case of orca connetion is client = valid address else false
        return claimSenderIsClient() ? claimAddressIsValid() : false;
    }
    
    @Override
    public boolean canSearchMaster() {
        return claimSenderIsClient() ? claimAddressIsValid() : true;
    }
    
    @Override
    public boolean canGlobalPublish() {
        return false;
    }
}
