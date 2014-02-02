/*
 * SimpleLaboTest.java
 *
 * Copyright (C) 2003-2004 Digital Globe, Inc. All rights reserved.
 */

package mirrorI.dolphin.client;

import java.util.*;

/**
 * １検体につき、テストされた項目の TreeSet を保持するクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SimpleLaboTest implements Comparable {
    
    /** 検体 */
    private SimpleLaboSpecimen specimen;
    
    /** テスト項目の TreeSet 要素は SimpleLaboTestItem */
    private TreeSet testItemTreeSet;
    
    /** Creates a new instance of SimpleLaboTest */
    public SimpleLaboTest() {
    }
    
    public SimpleLaboSpecimen getSimpleSpecimen() {
        return specimen;
    }
    
    public void setSimpleSpecimen(SimpleLaboSpecimen val) {
        specimen = val;
    }
    
    public TreeSet getTestItemTreeSet() {
        return testItemTreeSet;
    }
    
    public void setTestItemTreeSet(TreeSet val) {
        testItemTreeSet = val;
    }
    
    public void addSimpleLaboTestItem(SimpleLaboTestItem val) {
        
        if (testItemTreeSet == null) {
            testItemTreeSet = new TreeSet();
        }
        
        testItemTreeSet.add(val);
    }
    
    public int compareTo(Object obj) {
        
        SimpleLaboTest other = (SimpleLaboTest)obj;
        
        String str1 = specimen.getSpecimenName();
        String str2 = other.getSimpleSpecimen().getSpecimenName();
        
        return str1.compareTo(str2);
    }
    
    public boolean isSpecimen(SimpleLaboSpecimen other) {
        
        return specimen.equals(other);
    }
    
    public Object getTestItem(LaboTestItemID testID) {
        
        Object ret = null;
        
        if (testItemTreeSet == null) {
            return ret;
        }
        
        Iterator iter = testItemTreeSet.iterator();
        SimpleLaboTestItem testItem;
        
        while (iter.hasNext()) {
            
            testItem = (SimpleLaboTestItem)iter.next();
            if (testItem.isTest(testID)) {
                ret = (Object)testItem;
                break;
            }
        }
        
        //if (ret == null) {
            //ret = testID.getItemName();
        //}
        
        return ret;
    }
    
    public String getTestValue(LaboTestItemID testID) {
        
        String ret = null;
        
        if (testItemTreeSet == null) {
            return ret;
        }
        
        Iterator iter = testItemTreeSet.iterator();
        boolean hasTest = false;
        SimpleLaboTestItem testItem = null;
        
        while (iter.hasNext()) {
            
            testItem = (SimpleLaboTestItem)iter.next();
            if (testItem.isTest(testID)) {
                hasTest = true;
                break;
            }
        }
        
        if (hasTest) {
            ret = testItem.toString();
        } else {
            ret = testID.getItemName();
        }
        
        return ret;
    }
    
	public ArrayList getSimpleLaboTestItem() {
        
		ArrayList ret = null;
    
		if (testItemTreeSet == null) {
			return ret;
		}
    
		Iterator iter = testItemTreeSet.iterator();
		SimpleLaboTestItem testItem;
		ret = new ArrayList();
    
		while (iter.hasNext()) {
        
			ret.add((SimpleLaboTestItem)iter.next());
		}
		
		return ret;
	}
    
    public String toString() {
        
        StringBuffer buf = new StringBuffer();
        
        buf.append(" ");
        buf.append("\n");
        buf.append(specimen.toString());
        buf.append("\n");
        
        if (testItemTreeSet != null) {
            
            Iterator iter = testItemTreeSet.iterator();
            while (iter.hasNext()) {
                
                SimpleLaboTestItem item = (SimpleLaboTestItem)iter.next();
                
                buf.append(item.toString());
                buf.append("\n");
            }
        }
        
        return buf.toString();
        
    }
}
