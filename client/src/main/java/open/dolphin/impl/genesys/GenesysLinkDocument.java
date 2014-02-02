/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.impl.genesys;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.project.Project;

/**
 * ジェネシス連携
 * @author Life Sciences Computing Corporation.
 */
public class GenesysLinkDocument extends AbstractChartDocument {

    private static final String TITLE = "Genesys";
    private static final String APPLICATION_NAME = "dolphin";
    private static final String KEY_TITLE = "genesys.browser.title";
    private static final String KEY_SERVER = "genesys.browser.server";
    public static final String KEY_GENESYSBROWSER = "genesys.browser";
    public static final String VAL_GENESYS = "genesys";
    private GenesysLinkPanel splitPane;
    
    public GenesysLinkDocument() {
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
    
    private void initialize() {
        splitPane = new GenesysLinkPanel(Project.getString(KEY_SERVER), Project.getUserId(), getContext().getPatient().getPatientId(), APPLICATION_NAME);
        getUI().setLayout(new BorderLayout());
        getUI().add(splitPane, BorderLayout.CENTER);
        getUI().setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        splitPane.reDividerLocation(getUI().getHeight());
    }
}
