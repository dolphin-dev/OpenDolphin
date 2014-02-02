/*
 * LaboTestDocument.java
 *
 * Created on 2001/11/27, 2:15
 *
 * Modified on 2003/01/28 by Mirror-I corp to change reference of LaboTestBean
 *
 * Last updated on 2003/02/28
 */
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// Junzo SATO

package jp.ac.kumamoto_u.kuh.fc.jsato;

import javax.swing.*;

import open.dolphin.client.*;
import open.dolphin.infomodel.ID;
import open.dolphin.project.*;

import java.awt.*;

import jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.*;
/**
 *
 * @author  Junzo SATO
 * @version
 */
public class LaboTestDocument extends DefaultChartDocument {
    /** Creates new LaboTestDocument */
    public LaboTestDocument () {
        super();
        //JComponent compo = createComponent();
        //this.add(compo);
    }

    public void start() {
        this.setLayout(new BorderLayout());
        this.add(createComponent());
    }

    private JComponent createComponent() {
        /////////////////////////////
        /////////////////////////////
        // check the prefecture...
        // Kumamoto uses patient Id of local type.
        // Miyazaki uses that of facility type.
        /////////////////////////////
        /////////////////////////////
        String patientId = null;
        boolean isLocalId = true;
        //PatientVisit pvt = context.getPatientVisit();
        if ( Project.getLocalCode() == Project.KUMAMOTO ) {
            //patientId = pvt.localId;
            isLocalId = true;
        } else {
            //patientId = pvt.id;
            isLocalId = false;
        }

        // ToDo Kumamaoto case
        ID id = Project.getMasterId(context.getPatient().getId());
        if (id != null) {
            patientId = id.getId();
        }
        
        StatusPanel statusPanel = ((ChartPlugin)context).getStatusPanel();

        /*-----Mirror-I Start--------*/
        //JPanel panel = new LaboTestBean(patientId, isLocalId);
        JPanel panel = new mirrorI.dolphin.client.LaboTestBean(patientId, isLocalId, statusPanel);
        /*-----Mirror-I end--------*/

        panel.setPreferredSize(new Dimension(680,560));
        /*
         JScrollPane scroller = new JScrollPane(panel);
        scroller.setPreferredSize(new Dimension(680,540));
        return scroller;
         */
        return panel;
    }
}
