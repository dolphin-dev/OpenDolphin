package open.dolphin.impl.img;

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
    
    private AbstractBrowser browser;

    public ImageBrowserProxy() {

        // Projectに指定されているブラウザを生成する
        String name = Project.getString("image.browser.name");
        boolean win = ClientContext.isWin();
        win = true; // test with Mac

        if (win && name!=null && name.equals("genesys")) {
            browser = new GenesysBrowser();

        } else {
            browser = new DefaultBrowser();
        }
    }

    @Override
    public void start() {
        browser.start();
    }

    @Override
    public void stop() {
        browser.stop();
    }

    @Override
    public String getTitle() {
        return browser.getTitle();
    }

    @Override
    public void setTitle(String title) {
        browser.setTitle(title);
    }

    @Override
    public ImageIcon getIconInfo(Chart ctx) {
        return browser.getIconInfo(ctx);
    }

    @Override
    public Chart getContext() {
        return browser.getContext();
    }

    @Override
    public void setContext(Chart ctx) {
        browser.setContext(ctx);
    }

    @Override
    public JPanel getUI() {
        return browser.getUI();
    }

    @Override
    public void enter() {
        browser.enter();
    }

    @Override
    public void save() {
        browser.save();
    }

    @Override
    public void print() {
        browser.print();
    }

    @Override
    public boolean isDirty() {
        return browser.isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        browser.setDirty(dirty);
    }
}
