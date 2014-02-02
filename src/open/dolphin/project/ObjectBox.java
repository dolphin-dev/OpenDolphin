/*
 * Created on 2005/05/31
 *
 */
package open.dolphin.project;

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class ObjectBox {
    
    private String name;
    private String className;
    
    private boolean alive;
    
    public ObjectBox() {
    }
    
    public ObjectBox(String name, String className) {
        this();
        setName(name);
        setClassName(className);
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public String toString() {
        return name;
    }
    
}
