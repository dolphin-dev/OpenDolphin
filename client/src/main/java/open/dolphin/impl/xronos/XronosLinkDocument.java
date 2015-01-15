/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.impl.xronos;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.client.GUIConst;
import open.dolphin.project.Project;

/**
 * Xronos連携
 * @author Life Sciences Computing Corporation.
 */
public class XronosLinkDocument extends AbstractChartDocument {

    private static final String TITLE = "Xronos";
    private static final String APPLICATION_NAME = "dolphin";
    private static final String KEY_TITLE = "xronos.browser.title";
    public static final String KEY_XRONOSBROWSER_LINK = "xronos.browser.link";
    public static final String KEY_XRONOSBROWSER_STUDY = "xronos.browser.study";
    public static final String KEY_XRONOSBROWSER_SERIES = "xronos.browser.series";
    public static final String KEY_XRONOSBROWSER_IMAGE = "xronos.browser.image";
    public static final String KEY_XRONOSBROWSER_MODALITY_TITLE = "xronos.browser.modality.title";
    public static final String KEY_XRONOSBROWSER_MODALITY_KEY = "xronos.browser.modality.key";
    private XronosLinkPanel xronosPane;
    
    public XronosLinkDocument() {
        String title = Project.getString(KEY_TITLE);
        if(title == null || title.length() <= 0) {
            setTitle(TITLE);
        }else{
            setTitle(title);
        }
    }
    
    @Override
    public void start() {
        initialize();
    }

    @Override
    public void stop() {
    }
    
    @Override
    public void enter() {
        super.enter();
        getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, false);
    }
    
    private void initialize() {
        xronosPane = new XronosLinkPanel(this.getContext());
        xronosPane.createXronosPanel(Project.getUserId(), getContext().getPatient().getPatientId(), APPLICATION_NAME);
        getUI().setLayout(new BorderLayout());
        getUI().add(xronosPane, BorderLayout.CENTER);
        getUI().setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        xronosPane.reDividerLocation(getUI().getHeight());
    }
}
