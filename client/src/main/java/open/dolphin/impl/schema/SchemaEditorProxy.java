package open.dolphin.impl.schema;

import java.beans.PropertyChangeListener;
import open.dolphin.client.SchemaEditor;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.project.Project;

/**
 *
 * @author kazushi
 */
public class SchemaEditorProxy implements SchemaEditor {
    
    private SchemaEditor editor;;

    @Override
    public void setEditable(boolean b) {
        getEditor().setEditable(b);
    }

    @Override
    public void setSchema(SchemaModel model) {
        getEditor().setSchema(model);
    }

    @Override
    public void start() {
        getEditor().start();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        getEditor().addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        getEditor().removePropertyChangeListener(l);
    }
    
    // Factory
    private SchemaEditor getEditor() {
        if (editor==null) {
            // Projectに指定されているブラウザを生成する
            String name = Project.getString("schema.editor.name");
            
            if (name==null || name.equals("cool")) {
                editor = (SchemaEditor)create("open.dolphin.impl.scheam.SchemaEditorImpl"); // Scheam

            } else {
                editor = (SchemaEditor)create("open.dolphin.impl.schema.SchemaEditorImpl"); // Schema
            }
        }
        return editor;
    }
    
    private Object create(String clsName) {
        try {
            return Class.forName(clsName).newInstance();
        } catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }   
}
