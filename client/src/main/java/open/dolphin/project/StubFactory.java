package open.dolphin.project;

/**
 *
 * @author Kazushi Minagawa. Lab
 */
public class StubFactory {
    
    public static ProjectStub create(String name) {
        
        if ("dolphin".equals(name)) {
            return new StubDolphin();
        
        } else if ("asp".equals(name)) {
            return new StubASP();
        
        } else if ("i18n".equals(name)) {
            return new StubI18N();
        }
        
        throw new RuntimeException("Stub is not avalibale: " + name);
        
    }
}
