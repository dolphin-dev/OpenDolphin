/*
 * PVTHealthInsurance.java
 *
 * Created on 2001/10/10, 13:23
 *
 * Last updated on 2002/12/31.
 * Revised on 2003/01/06 for Null Pointer Exception at item.toString() (publicInsurances).
 * Revised on 2003/01/07 for initializing publicInsurances object first time.
 * Revised on 2003/01/08 renamed pvtPublicInsuranceItem from publicInsurances.
 *                                    added 'insuranceClassCodeTableId' in toString()
 *
 */
// Mirror-i        -start
//package open.dolphin.server;
package mirrorI.dolphin.server;
// Mirror-i        -end

import java.util.logging.*;

/**
 * Health-Insurance class to be parsed.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 *
 * Modified by Mirror-I corp for adding 'PvtPublicInsuranceItem' and related function to store/get PvtPublicInsuranceItem name
 */
public class PVTHealthInsurance{

    private String moduleUid;
    private String insuranceClass;
    private String insuranceClassCode;
    private String insuranceClassCodeTableId;
    private String insuranceNumber;
    private String insuranceClientGroup;
    private String insuranceClientNumber;
    private String insuranceFamilyClass;
    private String insuranceStartDate;
    private String insuranceExpiredDate;
    private String[] insuranceDisease;
    private String insurancePayInRatio;
    private String insurancePayOutRatio;

    private PvtPublicInsuranceItem[] pvtPublicInsuranceItem;

	private static Logger logger = Logger.getLogger(PVTServer.loggerLocation);

    /** Creates new PVTHealthInsurance */
    public PVTHealthInsurance() {
        super();
    }

    public String getModuleUid() {
        return moduleUid;
    }

    public void setModuleUid(String val) {
        moduleUid = val;
    }

    public String getInsuranceClass() {
        return insuranceClass;
    }

    public void setInsuranceClass(String val) {
        insuranceClass = val;
    }

    public String getInsuranceClassCode() {
        return insuranceClassCode;
    }

    public void setInsuranceClassCode(String val) {
        insuranceClassCode = val;
    }

    public String getInsuranceClassCodeTableId() {
        return insuranceClassCodeTableId;
    }

    public void setInsuranceClassCodeTableId(String val) {
        insuranceClassCodeTableId = val;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String val) {
        insuranceNumber = val;
    }

    public String getInsuranceClientGroup() {
        return insuranceClientGroup;
    }

    public void setInsuranceClientGroup(String val) {
        insuranceClientGroup = val;
    }

    public String getInsuranceClientNumber() {
        return insuranceClientNumber;
    }

    public void setInsuranceClientNumber(String val) {
        insuranceClientNumber = val;
    }

    public String getInsuranceFamilyClass() {
        return insuranceFamilyClass;
    }

    public void setInsuranceFamilyClass(String val) {
        insuranceFamilyClass = val;
    }

    public String getInsuranceStartDate() {
        return insuranceStartDate;
    }

    public void setInsuranceStartDate(String val) {
        insuranceStartDate = val;
    }

    public String getInsuranceExpiredDate() {
        return insuranceExpiredDate;
    }

    public void setInsuranceExpiredDate(String val) {
        insuranceExpiredDate = val;
    }

    public String[] getInsuranceDisease() {
        return insuranceDisease;
    }

    public void setInsuranceDisease(String[] val) {
        insuranceDisease = val;
    }

    public void addInsuranceDisease(String val) {

        int len = 0;

        if (insuranceDisease == null) {
            insuranceDisease = new String[1];
        }
        else {
            len = insuranceDisease.length;
            String[] dest = new String[len + 1];
            System.arraycopy(insuranceDisease, 0, dest, 0, len);
            insuranceDisease = dest;
        }
        insuranceDisease[len] = val;
    }

    public String getInsurancePayInRatio() {
        return insurancePayInRatio;
    }

    public void setInsurancePayInRatio(String val) {
        insurancePayInRatio = val;
    }

    public String getInsurancePayOutRatio() {
        return insurancePayOutRatio;
    }

    public void setInsurancePayOutRatio(String val) {
        insurancePayOutRatio = val;
    }


    public PvtPublicInsuranceItem[] getPvtPublicInsuranceItem() {
        return pvtPublicInsuranceItem;
    }

    public void setPvtPublicInsuranceItem(PvtPublicInsuranceItem[] val) {
        pvtPublicInsuranceItem = val;
    }
	//publicInsurances
	// Mirror-I Start

    public void addPvtPublicInsuranceItem() {

        int len = 0;

        if (pvtPublicInsuranceItem != null) {
            len = pvtPublicInsuranceItem.length;
            PvtPublicInsuranceItem[] dest = new PvtPublicInsuranceItem[len + 1];
            System.arraycopy(pvtPublicInsuranceItem, 0, dest, 0, len);
            pvtPublicInsuranceItem = dest;
        }
       pvtPublicInsuranceItem[len] = new PvtPublicInsuranceItem();
    }

	public void addPublicInsurancePriority(String val) {
		if(pvtPublicInsuranceItem == null) {
			pvtPublicInsuranceItem = new PvtPublicInsuranceItem[1];
			//Revised on 2003/01/07 for initializing publicInsurances object first time
			pvtPublicInsuranceItem[0] = new PvtPublicInsuranceItem();

			int lenPublicInsurance = getPvtPublicInsuranceItem().length;
			logger.finer("Length of PublicInsurance" + lenPublicInsurance);
		}
		else {
			addPvtPublicInsuranceItem();
			int lenPublicInsurance = getPvtPublicInsuranceItem().length;
			logger.finer("Length of PublicInsurance" + lenPublicInsurance);
		}
		int len = pvtPublicInsuranceItem.length -1;
		pvtPublicInsuranceItem[len].setPublicInsurancePriority(val);
	}

	public void addPublicInsurancePaymentRatioType(String val) {
		int len = pvtPublicInsuranceItem.length -1;
		pvtPublicInsuranceItem[len].setPublicInsurancePaymentRatioType(val);

	}

	public void addPublicInsuranceProviderName(String val) {
		int len = pvtPublicInsuranceItem.length -1;
		pvtPublicInsuranceItem[len].setPublicInsuranceProviderName(val);

	}

	public void addPublicInsuranceProvider(String val) {
		int len = pvtPublicInsuranceItem.length -1;
		pvtPublicInsuranceItem[len].setPublicInsuranceProvider(val);

	}

	public void addPublicInsuranceRecipient(String val) {
		int len = pvtPublicInsuranceItem.length -1;
		pvtPublicInsuranceItem[len].setPublicInsuranceRecipient(val);

	}

	public void addPublicInsuranceStartDate(String val) {
		int len = pvtPublicInsuranceItem.length -1;
		pvtPublicInsuranceItem[len].setPublicInsuranceStartDate(val);

	}

	public void addPublicInsuranceExpiredDate(String val) {
		int len = pvtPublicInsuranceItem.length -1;
		pvtPublicInsuranceItem[len].setPublicInsuranceExpiredDate(val);

	}

	public void addPublicInsurancePaymentRatio(String val) {
		int len = pvtPublicInsuranceItem.length -1;
		pvtPublicInsuranceItem[len].setPublicInsurancePaymentRatio(val);

	}
        
    public String toString() {
        
        StringBuffer buf = new StringBuffer();
        
        if (insuranceNumber != null && insuranceClass != null ) {
            buf.append(insuranceNumber);
            buf.append("  ");
            buf.append(insuranceClass);
        }
        
        else if (insuranceNumber != null) {
            buf.append(insuranceNumber);
        }
        
        else if (insuranceClass != null) {
            buf.append(insuranceClass);
        }
        
        else {
            buf.append("Ž©”ï");
        }
        
        if (pvtPublicInsuranceItem != null) {
            int len = pvtPublicInsuranceItem.length;
            buf.append(" Œö”ï(");
            for (int i = 0; i < len; i++) {
                PvtPublicInsuranceItem item = pvtPublicInsuranceItem[i];
                if (item != null) {
                    if (i != 0) {
                        buf.append("E");
                    }
                    buf.append(item.toString());
                }
            }
            buf.append(" )");
        }
        
        return buf.toString();
    }
}
