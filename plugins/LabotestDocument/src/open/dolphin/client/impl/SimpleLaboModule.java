/*
 * SimpleLaboModule.java
 *
 * Created on 2003/07/30, 12:29
 */

package open.dolphin.client.impl;

import java.util.*;

/**
 * SimpleLaboModule
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SimpleLaboModule {
    
    private String sampleTime;
    private String registTime;
    private String reportTime;
    private String mmlConfirmDate;
    private String reportStatus;
    private String repMemo;
    private String repMemoCodeName;
    private String repMemoCode;
    private String repMemoCodeId;
    private String repFreeMemo;
    private String testCenterName;
    private String set;
    
    private ArrayList simpleLaboTests;
    
    /** Creates a new instance of SimpleLaboModule */
    public SimpleLaboModule() {
    }
    
    public String getSampleTime() {
        return sampleTime;
    }
    
    public void setSampleTime(String val) {
        sampleTime = val;
    }
    
    public String getRegistTime() {
        return registTime;
    }
    
    public void setRegistTime(String val) {
        registTime = val;
    }
    
    public String getReportTime() {
        return reportTime;
    }
    
    public void setReportTime(String val) {
        reportTime = val;
    }
    
    public String getMmlConfirmDate() {
        return mmlConfirmDate;
    }
    
    public void setMmlConfirmDate(String val) {
        mmlConfirmDate = val;
    }
    
    public String getReportStatus() {
        return reportStatus;
    }
    
    public void setReportStatus(String val) {
        reportStatus = val;
    }
    
    public String getRepMemo() {
        return repMemo;
    }
    
    public void setRepMemo(String val) {
        repMemo = val;
    } 
    
    public String getRepMemoCodeName() {
        return repMemoCodeName;
    }
    
    public void setRepMemoCodeName(String val) {
        repMemoCodeName = val;
    }
    
    public String getRepMemoCode() {
        return repMemoCode;
    }
    
    public void setRepMemoCode(String val) {
        repMemoCode = val;
    } 
    
    public String getRepMemoCodeId() {
        return repMemoCodeId;
    }
    
    public void setRepMemoCodeId(String val) {
        repMemoCodeId = val;
    }
    
    public String getRepFreeMemo() {
        return repFreeMemo;
    }
    
    public void setRepFreeMemo(String val) {
        repFreeMemo = val;
    }
    
    public String getTestCenterName() {
        return testCenterName;
    }
    
    public void setTestCenterName(String val) {
        testCenterName = val;
    } 
    
    public String getSet() {
        return set;
    }
    
    public void setSet(String val) {
        set = val;
    }   
    
    public ArrayList getSimpleLaboTest() {
        return simpleLaboTests;
    }
    
    public void setSimpleLaboTest(ArrayList list) {
        simpleLaboTests = list;
    }
    
    @SuppressWarnings("unchecked")
	public void addSimpleLaboTest(SimpleLaboTest val) {
        if (simpleLaboTests == null) {
            simpleLaboTests = new ArrayList();
        }
        simpleLaboTests.add(val);
    }  
    
    public String getHeader() {
        StringBuilder buf = new StringBuilder("採取: ");
        if (sampleTime != null) {
            int index = sampleTime.indexOf('T');
            if (index > 0) {
                String val = sampleTime.substring(0, index);
                buf.append(val);
            } else {
                buf.append(sampleTime);
            }
        }
        
        return buf.toString();                   
    }
    
    public void fillNormaliedData(Object[][] obj, int col, AllLaboTest allTests) {
        
        if (simpleLaboTests == null || simpleLaboTests.size() == 0) {
            return;
        }
        
        int row = 0;
        
        /*if (registTime != null) {
            obj[row++][col] = registTime;
        }
        
        if (reportTime != null) {
            obj[row++][col] = reportTime;
        } 
        
        if (reportStatus != null) {
            obj[row++][col] = reportStatus;
        }
        
        if (testCenterName != null) {
            obj[row++][col] = testCenterName;
        }
        
        if (set != null) {
            obj[row++][col] = set;
        }*/

        
        /*if (reportTime != null) {
            obj[row++][col] = "報告: " + reportTime;
        } else {
            obj[row++][col] = "報告: ";
        }
        
        if (reportStatus != null) {
            obj[row++][col] = "ステータス: " + reportStatus;
        } else {
            obj[row++][col] = "";
        }
        
        if (testCenterName != null) {
            obj[row++][col] = testCenterName;
        } else {
            obj[row++][col] = "";
        }
        
        if (set != null) {
            obj[row++][col] = "セット名: " + set;
        } else {
            obj[row++][col] = "";
        }*/
        
        // このモジュールに含まれる LaboTest(検体とその検体に対するテスト項目セット）の数
        int size = simpleLaboTests.size();
        SimpleLaboSpecimen specimen = null;
        SimpleLaboTest test = null;
        boolean hasSpecimen = false;
        //StringBuffer buf = null;
        
        /** この検索の全検査項目 */
        Iterator iter = allTests.getAllTests().keySet().iterator();
        
        while (iter.hasNext()) {
            
            specimen = (SimpleLaboSpecimen)iter.next();
            //obj[row++][col] = "";
            //obj[row++][col] = specimen.getSpecimenName();
            obj[row++][col] = specimen;
            
            hasSpecimen = false;
            for (int i = 0; i < size; i++) {
                test = (SimpleLaboTest)simpleLaboTests.get(i);
                if (test.isSpecimen(specimen)) {
                    hasSpecimen = true;
                    break;
                }
            }
            
            TreeSet ts = (TreeSet)allTests.getAllTests().get(specimen);
            Iterator i = ts.iterator();
            while (i.hasNext()) {
                
                LaboTestItemID testID = (LaboTestItemID)i.next();
                
                if (hasSpecimen) {
                    //obj[row++][col] = test.getTestValue(testID);
                    obj[row++][col] = test.getTestItem(testID);
                
                } else {
                    //obj[row++][col] = testID.getItemName();
                    obj[row++][col] = null;
                }
            }
        }
        
        obj[row++][col] = "";
        
        if (registTime != null) {
            obj[row++][col] = registTime;
        } else {
            obj[row++][col] = "";
        }
        
        if (reportTime != null) {
            obj[row++][col] = reportTime;
        } else {
            obj[row++][col] = "";
        }
        
        if (reportStatus != null) {
            obj[row++][col] = reportStatus;
        } else {
            obj[row++][col] = "";
        }
        
        if (testCenterName != null) {
            obj[row++][col] = testCenterName;
        } else {
            obj[row++][col] = "";
        }
        
        if (set != null) {
            obj[row++][col] = set;
        } else {
            obj[row++][col] = "";
        }
    }
    
    public void normalize(AllLaboTest allTests) {
         
        if (simpleLaboTests == null || simpleLaboTests.size() == 0) {
            return;
        }
        
        int size = simpleLaboTests.size();
        SimpleLaboSpecimen specimen = null;
        StringBuffer buf = null;
        SimpleLaboTest test = null;
        boolean hasSpecimen = false;
        
        Iterator iter = allTests.getAllTests().keySet().iterator();
        
        while (iter.hasNext()) {

            buf = new StringBuffer();
            
            specimen = (SimpleLaboSpecimen)iter.next();
            buf.append((specimen.getSpecimenName()));
            buf.append("\n");
           
            hasSpecimen = false; 
            for (int i = 0; i < size; i++) {
                test = (SimpleLaboTest)simpleLaboTests.get(i);
                if (test.isSpecimen(specimen)) {
                    hasSpecimen = true;
                    break;
                }
            }
            
            TreeSet ts = (TreeSet)allTests.getAllTests().get(specimen);
            Iterator i = ts.iterator();
            while (i.hasNext()) {
                
                LaboTestItemID testID = (LaboTestItemID)i.next();
                
                if (hasSpecimen) {
                    buf.append(test.getTestValue(testID));
                
                } else {
                    buf.append(testID.getItemName());
                }
                buf.append("\n");
            }
            
            System.out.println(buf.toString());
        }
    }
    
    public String toString() {
        
        StringBuffer buf = new StringBuffer();
        
        if (sampleTime != null) {
            buf.append(sampleTime);
            buf.append("\n");
        }
        
        if (registTime != null) {
            buf.append(registTime);
            buf.append("\n");
        }
        
        if (reportTime != null) {
            buf.append(reportTime);
            buf.append("\n");
        }
        
        if (mmlConfirmDate != null) {
            buf.append(mmlConfirmDate);
            buf.append("\n");
        }
        
        if (reportStatus != null) {
            buf.append(reportStatus);
            buf.append("\n");
        }
        
        if (testCenterName != null) {
            buf.append(testCenterName);
            buf.append("\n");
        }
        
        if (set != null) {
            buf.append(set);
            buf.append("\n");
        }
        
        if (simpleLaboTests != null) {
            
            for (int i = 0; i < simpleLaboTests.size(); i++) {
                SimpleLaboTest test = (SimpleLaboTest)simpleLaboTests.get(i);
                
                buf.append(test.toString());
            }
        }
        
        return buf.toString();
    }
}
