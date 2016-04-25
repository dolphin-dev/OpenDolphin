package open.dolphin.project;

import static open.dolphin.project.Project.CLAIM_SENDER;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class StubDolphin extends ProjectStub {
    
    // ASP & Pro
    private final String CONTEXT_ROOT = "/openDolphin/resources";
    
    // Docker
    //private final String CONTEXT_ROOT = "/dolphin/openSource";
    
    private String baseURI;
    
    private String[] spec;
    
    /** Creates new Project */
    public StubDolphin() {
    }
    
    @Override
    public String getBaseURI() {
        if (baseURI==null) {
            StringBuilder sb = new StringBuilder();
            String test = getServerURI();
            if (test != null) {
                if (test.endsWith("/")) {
                    int len = test.length();
                    test = test.substring(0, len-1);
                }
                sb.append(test);
                sb.append(CONTEXT_ROOT);
                baseURI = sb.toString();
            }
            //createSpec(baseURI);
        }
        return baseURI;
    }
    
    private void createSpec(String test) {
       spec = new String[3];
        if (test==null || "".equals(test)) {
            spec[0] = "http";
            spec[1] = null;
            spec[2] = "8080";
        } else {
            try {
                String[] comp = test.split("://");
                spec[0] = comp[0];
                String[] comp2 = comp[1].split(":");
                spec[1] = comp2[0];
                spec[2] = comp2[1];
            } catch (RuntimeException e) {
                spec[0] = "http";
                spec[1] = null;
                spec[2] = "8080";
            }
        }
    }
    
    @Override
    public String getServerURI() {
        return getString(Project.SERVER_URI, null);
    }

    @Override
    public void setServerURI(String val) {
        setString(Project.SERVER_URI, val);
        baseURI = null;
        spec = null;
    }
    
    @Override
    public String getSchema() {
        if (spec==null) {
            createSpec(getServerURI());
        }
        return spec[0];
    }
    
    @Override
    public String getServer() {
        if (spec==null) {
            createSpec(getServerURI());
        }
        return spec[1];
    }
    
    @Override
    public String getPort() {
        if (spec==null) {
            createSpec(getServerURI());
        }
        return spec[2];
    }
    
    @Override
    public boolean isTester() {
        return false;
    }
    
    @Override
    public boolean claimSenderIsClient() {
        String test = getString(CLAIM_SENDER);
        return (test!=null && test.equals("client"));
    }
    
    @Override
    public boolean claimSenderIsServer() {
        String test = getString(CLAIM_SENDER);
        return (test!=null && test.equals("server"));
    }
    
    @Override
    public boolean canAccessToOrca() {
        // Always true when connection is server
        return claimSenderIsClient() ? claimAddressIsValid() : true;
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