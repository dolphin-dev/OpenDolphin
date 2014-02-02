/*
 * NormalizedLaboTest.java
 *
 * Created on 2003/08/01, 9:26
 */

package mirrorI.dolphin.client;

import java.util.*;

/**
 *
 * @author   Kazushi Minagawa, Digital Globe, Inc.
 */
public class AllLaboTest {
    
    private TreeMap allTests = new TreeMap();
    
    /** Creates a new instance of NormalizedLaboTest */
    public AllLaboTest() {
    }
    
    public void clear() {
        allTests.clear();
    }
    
    public void addSpecimen(SimpleLaboSpecimen specimen) {
        
        if (! allTests.containsKey(specimen)) {
            allTests.put(specimen, new TreeSet());
        }
    }
    
    public void addTestItem(SimpleLaboSpecimen specimen, LaboTestItemID testItem) {
        
        TreeSet treeSet = (TreeSet)allTests.get(specimen);
        
        if (treeSet != null) {
            treeSet.add(testItem);
        }
    }
    
    public TreeMap getAllTests() {
        return allTests;
    }
    
    public int getRowCount() {
        
        int count = 0;
        
        Iterator iter = allTests.keySet().iterator();
        
        while (iter.hasNext()) {
            
            SimpleLaboSpecimen sp = (SimpleLaboSpecimen)iter.next();
           
            //count++;
            count++;
            
            Iterator it = ((TreeSet)allTests.get(sp)).iterator();
            
            while(it.hasNext()) {
                
                it.next();
                count++;
            }
        }
        
        return count;
    }
    
    public void fillRow(Object[][] laboData, int startRow, int col) {
        
        Iterator iter = allTests.keySet().iterator();
        
        while (iter.hasNext()) {
            
            SimpleLaboSpecimen sp = (SimpleLaboSpecimen)iter.next();
            
            //laboData[startRow++][col] = null;
            laboData[startRow++][col] = sp;
            
            Iterator it = ((TreeSet)allTests.get(sp)).iterator();
            
            while(it.hasNext()) {
                
                LaboTestItemID id = (LaboTestItemID)it.next();
                
                laboData[startRow++][col] = id.getItemName();
            }
        }
    }
    
    public String toString() {
                
        StringBuffer buf = new StringBuffer();
        
        Iterator iter = allTests.keySet().iterator();
        
        while (iter.hasNext()) {
            
            SimpleLaboSpecimen sp = (SimpleLaboSpecimen)iter.next();
            
            buf.append("\n");
            buf.append(sp.getSpecimenName());
            buf.append("\n");
            
            Iterator it = ((TreeSet)allTests.get(sp)).iterator();
            
            while(it.hasNext()) {
                
                LaboTestItemID id = (LaboTestItemID)it.next();
                
                buf.append(id.getItemName());
                buf.append("\n");
            }
        }
        
        return buf.toString();
    }
}
