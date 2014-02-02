package open.dolphin.rest;

import javax.naming.InitialContext;
import open.dolphin.session.AppoServiceBeanLocal;
import open.dolphin.session.KarteServiceBeanLocal;
import open.dolphin.session.NLabServiceBeanLocal;
import open.dolphin.session.LetterServiceBeanLocal;
import open.dolphin.session.PVTServiceBeanLocal;
import open.dolphin.session.PatientServiceBeanLocal;
import open.dolphin.session.StampServiceBeanLocal;
import open.dolphin.session.UserServiceBeanLocal;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class EJBLocator {

    //----------------------------------------------------------------------------------------
    private static final String USER_SERVICE    = "OpenDolphin-EA-2.0/UserServiceBean/local";
    private static final String STAMP_SERVICE   = "OpenDolphin-EA-2.0/StampServiceBean/local";
    private static final String PVT_SERVICE     = "OpenDolphin-EA-2.0/PVTServiceBean/local";
    private static final String PATIENT_SERVICE = "OpenDolphin-EA-2.0/PatientServiceBean/local";
    private static final String KARTE_SERVICE   = "OpenDolphin-EA-2.0/KarteServiceBean/local";
    private static final String NLAB_SERVICE    = "OpenDolphin-EA-2.0/NLabServiceBean/local";
    private static final String APPO_SERVICE    = "OpenDolphin-EA-2.0/AppoServiceBean/local";
    private static final String LETTER_SERVICE  = "OpenDolphin-EA-2.0/LetterServiceBean/local";
    //----------------------------------------------------------------------------------------
    
    private static UserServiceBeanLocal userServiceBeanLocal;
    private static StampServiceBeanLocal stampServiceBeanLocal;
    private static PVTServiceBeanLocal pVTServiceBeanLocal;
    private static PatientServiceBeanLocal patientServiceBeanLocal;
    private static KarteServiceBeanLocal karteServiceBeanLocal;
    private static NLabServiceBeanLocal labServiceBeanLocal;
    private static AppoServiceBeanLocal appoServiceBeanLocal;
    private static LetterServiceBeanLocal letterServiceBeanLocal;

    static {
        try {
            InitialContext ic = new InitialContext();
            userServiceBeanLocal = (UserServiceBeanLocal) ic.lookup(USER_SERVICE);
            stampServiceBeanLocal = (StampServiceBeanLocal) ic.lookup(STAMP_SERVICE);
            pVTServiceBeanLocal = (PVTServiceBeanLocal) ic.lookup(PVT_SERVICE);
            patientServiceBeanLocal = (PatientServiceBeanLocal) ic.lookup(PATIENT_SERVICE);
            karteServiceBeanLocal = (KarteServiceBeanLocal) ic.lookup(KARTE_SERVICE);
            labServiceBeanLocal = (NLabServiceBeanLocal) ic.lookup(NLAB_SERVICE);
            appoServiceBeanLocal = (AppoServiceBeanLocal) ic.lookup(APPO_SERVICE);
            letterServiceBeanLocal = (LetterServiceBeanLocal) ic.lookup(LETTER_SERVICE);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static UserServiceBeanLocal getUserServiceBean() {
        return userServiceBeanLocal;
    }

    public static StampServiceBeanLocal getStampServiceBean() {
        return stampServiceBeanLocal;
    }

    public static PVTServiceBeanLocal getPVTServiceBean() {
        return pVTServiceBeanLocal;
    }

    public static PatientServiceBeanLocal getPatientServiceBean() {
        return patientServiceBeanLocal;
    }

    public static KarteServiceBeanLocal getKarteServiceBean() {
        return karteServiceBeanLocal;
    }

    public static NLabServiceBeanLocal getNLabServiceBean() {
        return labServiceBeanLocal;
    }

    public static AppoServiceBeanLocal getAppoServiceBean() {
        return appoServiceBeanLocal;
    }

    public static LetterServiceBeanLocal getLetterServiceBean() {
        return letterServiceBeanLocal;
    }
}
