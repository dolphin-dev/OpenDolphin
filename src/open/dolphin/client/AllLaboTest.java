/*
 * NormalizedLaboTest.java
 *
 * Created on 2003/08/01, 9:26
 */

package open.dolphin.client;

import java.util.*;

/**
 * AllLaboTest
 * 検索した期間に含まれる全ての検査を保持するオブジェクト。
 * 標本をキーにし、その標本に含まれる検査項目のTreeSetを値とする。
 * ex.  血清 キー
 * 　　　上記標本(血清)に関する全ての検査項目を TreeSet に保持する。
 * 
 * @author   Kazushi Minagawa, Digital Globe, Inc.
 */
public class AllLaboTest {
    
    private TreeMap<SimpleLaboSpecimen, TreeSet<LaboTestItemID>> allTests = new TreeMap<SimpleLaboSpecimen, TreeSet<LaboTestItemID>>();
    
    /** Creates a new instance of NormalizedLaboTest */
    public AllLaboTest() {
    }
    
    public void clear() {
        allTests.clear();
    }
    
    /**
     * 標本を追加する。
     * 標本をきーとした TreeSet がマップに追加される。
     * @param specimen 標本
     */
    @SuppressWarnings("unchecked")
	public void addSpecimen(SimpleLaboSpecimen specimen) {
        if (! allTests.containsKey(specimen)) {
            allTests.put(specimen, new TreeSet());
        }
    }
    
    /**
     * 標本に検査項目を追加する。
     * @param specimen 標本
     * @param testItem 検査項目
     */
    @SuppressWarnings("unchecked")
	public void addTestItem(SimpleLaboSpecimen specimen, LaboTestItemID testItem) {
        
    	// 標本の TreeSet を得る
        TreeSet treeSet = allTests.get(specimen);
        
        if (treeSet != null) {
            treeSet.add(testItem);
        }
    }
    
    public TreeMap getAllTests() {
        return allTests;
    }
    
    /**
     * テーブルに表示する場合に必要な行数を返す。
     * これは標本の数と各標本に含まれる検査項目の合計となる。
     * @return 標本の数+各標本に含まれる検査項目の合計
     */
    public int getRowCount() {
        
        int count = 0;
        
        Iterator iter = allTests.keySet().iterator();
        
        while (iter.hasNext()) {
            
            SimpleLaboSpecimen sp = (SimpleLaboSpecimen) iter.next();
            count++;
            
            Iterator it = allTests.get(sp).iterator();
            
            while(it.hasNext()) {
                it.next();
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * テーブルのデータ配列に検査データ名を設定する。見出しを作成するためのメソッド。
     * @param laboData テーブルのデータ配列
     * @param startRow 値の設定を開始する行
     * @param col 値を設定するカラム
     */
    public void fillRow(Object[][] laboData, int startRow, int col) {
        
        Iterator iter = allTests.keySet().iterator();
        
        while (iter.hasNext()) {
            
            SimpleLaboSpecimen sp = (SimpleLaboSpecimen) iter.next();
            
            // 開始行は標本名とする
            laboData[startRow++][col] = sp;
            
            // 以降の行はテスト項目名とする
            Iterator it = allTests.get(sp).iterator();
            
            while(it.hasNext()) {
                
                LaboTestItemID id = (LaboTestItemID) it.next();
                
                laboData[startRow++][col] = id.getItemName();
            }
        }
    }
    
    public String toString() {
                
        StringBuilder buf = new StringBuilder();
        
        Iterator iter = allTests.keySet().iterator();
        
        while (iter.hasNext()) {
            
            SimpleLaboSpecimen sp = (SimpleLaboSpecimen) iter.next();
            
            buf.append("\n");
            buf.append(sp.getSpecimenName());
            buf.append("\n");
            
            Iterator it = allTests.get(sp).iterator();
            
            while (it.hasNext()) {
                
                LaboTestItemID id = (LaboTestItemID)it.next();
                
                buf.append(id.getItemName());
                buf.append("\n");
            }
        }
        
        return buf.toString();
    }
}
