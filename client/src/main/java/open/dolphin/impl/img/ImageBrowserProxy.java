package open.dolphin.impl.img;

import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import open.dolphin.client.Chart;
import open.dolphin.client.ChartDocument;
import open.dolphin.client.ClientContext;
import open.dolphin.project.Project;

/**
 * ImageBrowser plugin の proxy class.
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class ImageBrowserProxy implements ChartDocument {
    
    private ChartDocument browser;

    public ImageBrowserProxy() {
    }

    @Override
    public void start() {
        getBrowser().start();
        // menu control
        enter();
    }

    @Override
    public void stop() {
        getBrowser().stop();
    }

    @Override
    public String getTitle() {
        return getBrowser().getTitle();
    }

    @Override
    public void setTitle(String title) {
        getBrowser().setTitle(title);
    }

    @Override
    public ImageIcon getIconInfo(Chart ctx) {
        return getBrowser().getIconInfo(ctx);
    }

    @Override
    public Chart getContext() {
        return getBrowser().getContext();
    }

    @Override
    public void setContext(Chart ctx) {
        getBrowser().setContext(ctx);
    }

    @Override
    public JPanel getUI() {
        return getBrowser().getUI();
    }

    @Override
    public void enter() {
        getBrowser().enter();
    }

    @Override
    public void save() {
        getBrowser().save();
    }

    @Override
    public void print() {
        getBrowser().print();
    }

    @Override
    public boolean isDirty() {
        return getBrowser().isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        getBrowser().setDirty(dirty);
    }
    
    private ChartDocument getBrowser() {
        if (browser==null) {
            // Projectに指定されているブラウザを生成する
            String name = Project.getString("image.browser.name");
            boolean win = ClientContext.isWin();
            //win = true; // test with Mac

            if (win && name!=null && name.equals("genesys")) {
                browser = (ChartDocument)create("open.dolphin.impl.img.GenesysBrowser");

            } else if (win && name!=null && name.equals("unitea")) {
                browser = (ChartDocument)create("open.dolphin.impl.img.UniteaBrowser");

            } else if (win && name!=null && name.equals("tfs")) {
                browser = (ChartDocument)create("open.dolphin.impl.img.TFSBrowser");

//s.oh^ FCR連携/他プロセス連携/ジェネシス連携
            // FCR連携
            } else if (win && name!=null && name.equals("fcr")) {
                browser = (ChartDocument)create("open.dolphin.impl.img.FCRBrowser");
                
            // 他プロセス連携
            } else if (name != null && name.equals("defaultex")) {
                browser = (ChartDocument)create("open.dolphin.impl.img.DefaultBrowserEx");
                
            // ジェネシス連携
            } else if (win && name != null && name.equals("genephin")) {
                browser = (ChartDocument)create("open.dolphin.impl.genesys.GenesysLinkDocument");
                
//s.oh$
            } else {
                browser = (ChartDocument)create("open.dolphin.impl.img.DefaultBrowser");
            }
        }
        return browser;
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

    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        getBrowser().addPropertyChangeListener(prop, l);
    }

    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        getBrowser().removePropertyChangeListener(prop, l);
    }

    @Override
    public boolean isChartDocDidSave() {
        return getBrowser().isChartDocDidSave();
    }

    @Override
    public void setChartDocDidSave(boolean b) {
        getBrowser().setChartDocDidSave(b);
    }
}
