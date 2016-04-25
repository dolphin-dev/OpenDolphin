package open.dolphin.project;

/**
 *
 * @author Kazushi Minagawa. Lab
 */
public class StubASP extends ProjectStub {
    
    private final String BASE_URI = "http://localhost:8080";
    
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
        return "https";
    }
    
    @Override
    public String getServer() {
        return "cloud.open.dolphin";
    }
    
    @Override
    public String getPort() {
        return "443";
    }
    
    @Override
    public boolean isTester() {
        return false;
    }
    
    @Override
    public boolean claimSenderIsClient() {
        return true;
    }
    
    @Override
    public boolean claimSenderIsServer() {
        return false;
    }
    
    @Override
    public boolean canAccessToOrca() {
        return claimAddressIsValid();
    }
    
    @Override
    public boolean canSearchMaster() {
        return claimAddressIsValid();
    }
    
    @Override
    public boolean canGlobalPublish() {
        return true;
    }
}
