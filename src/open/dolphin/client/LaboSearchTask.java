/*
 * LaboTestSearchTask.java
 *
 * Created on 2003/01/27
 *
 * Last updated on 2003/02/28
 *
 */

package open.dolphin.client;

import java.util.*;

import open.dolphin.delegater.LaboDelegater;
import open.dolphin.dto.LaboSearchSpec;
import open.dolphin.infomodel.LaboItemValue;
import open.dolphin.infomodel.LaboModuleValue;
import open.dolphin.infomodel.LaboSpecimenValue;

/**
 * LaboSearchTask
 * 
 * @author  kazushi,Minagawa
 * 
 */
public class LaboSearchTask extends AbstractInfiniteTask {
    
    private LaboSearchSpec spec;
    private LaboDelegater ldl;
    
    private AllLaboTest allLaboTests;
    private Vector<SimpleLaboModule> moduleVec;

    /** Creates new LaboTestSearchTask */
    public LaboSearchTask(LaboSearchSpec spec, LaboDelegater ldl, int taskLength) {
        this.spec = spec;
        this.ldl = ldl;
        setTaskLength(taskLength);
    }
    
	public AllLaboTest getAllLaboTest() {
        return allLaboTests;
    }
    
	public Vector getLaboModuleColumns() {
        return moduleVec;
    }

    @SuppressWarnings("unchecked")
	protected void doTask() {
    	
    	// データベースを検索し LaboModuleValue のリストを得る
        this.setMessage("検索しています...");
        List<LaboModuleValue> results = (List<LaboModuleValue>) ldl.getLaboModules(spec);
        
        if (results == null || results.size() == 0) {
        	setMessage("検索が終了しました");
        	setDone(true);
        	return;
        }
        
        // 検査結果テーブルのカラムを生成するためのベクトル
        // 一カラムが一モジュールに対応する
        moduleVec = new Vector<SimpleLaboModule>();
        
        // LaboModuleValueをイテレートし、テーブルへ表示できるデータに分解する
        for (LaboModuleValue moduleValue : results) {
        
        	// LaboModuleValuの簡易版オブジェクトを生成しベクトルに加える
        	SimpleLaboModule simpleLaboModule = new SimpleLaboModule();
            moduleVec.add(simpleLaboModule);
       
            // 簡易版に値を設定する
            simpleLaboModule.setSampleTime(moduleValue.getSampleTime());
            simpleLaboModule.setRegistTime(moduleValue.getRegistTime());
            simpleLaboModule.setReportTime(moduleValue.getReportTime());
            simpleLaboModule.setMmlConfirmDate(moduleValue.getConfirmDate());            
            simpleLaboModule.setReportStatus(moduleValue.getReportStatus());
            simpleLaboModule.setTestCenterName(moduleValue.getLaboratoryCenter());            
            simpleLaboModule.setSet(moduleValue.getSetName()); 
            
            // Module に含まれる標本をイテレートする
            Collection<LaboSpecimenValue> specimens = moduleValue.getLaboSpecimens();
            
            if (specimens != null) {
            	
            	for (LaboSpecimenValue bean : specimens) {
            		
            		// 簡易版ラボテストオブジェクトを生成し簡易版のモジュールへ加える
            		SimpleLaboTest laboTest = new SimpleLaboTest();
                    simpleLaboModule.addSimpleLaboTest(laboTest);
                    SimpleLaboSpecimen specimen = new SimpleLaboSpecimen();
                    laboTest.setSimpleSpecimen(specimen);
                    
                    specimen.setSpecimenCodeID(bean.getSpecimenCodeId());
                    specimen.setSpecimenCode(bean.getSpecimenCode());
                    specimen.setSpecimenName(bean.getSpecimenName());
                    
                    // 検索期間に含まれる全ての検査を保持するオブジェクト - allLaboTestsを生成する
                    if (allLaboTests == null) {
                        allLaboTests = new AllLaboTest();
                    }
                    // 標本をキーとして登録する
                    allLaboTests.addSpecimen(specimen);
                    
                    // Specimenに含まれる Item をイテレートする
                    Collection<LaboItemValue> items = bean.getLaboItems();
                    
                    if (items != null) {

                    	for (LaboItemValue itemBean : items) {
                    		
                    		// 検索項目を標本キーの値(TreeSet)として登録する
                    		SimpleLaboTestItem testItem = new SimpleLaboTestItem();
                            LaboTestItemID testItemID = new LaboTestItemID();
                            
                            testItem.setItemCodeID(itemBean.getItemCodeId());
                            testItemID.setItemCodeID(itemBean.getItemCodeId());
                            
                            testItem.setItemCode(itemBean.getItemCode());
                            testItemID.setItemCode(itemBean.getItemCode());
                            
                            testItem.setItemName(trimJSpace(itemBean.getItemName()));
                            testItemID.setItemName(trimJSpace(itemBean.getItemName()));
                            
                            allLaboTests.addTestItem(specimen, testItemID);
                            
                            testItem.setItemValue(itemBean.getItemValue());
                            testItem.setItemUnit(itemBean.getUnit());
                            testItem.setLow(itemBean.getLow());
                            testItem.setUp(itemBean.getUp());
                            testItem.setNormal(itemBean.getNormal());
                            testItem.setOut(itemBean.getNout());
                            
                            laboTest.addSimpleLaboTestItem(testItem);
                    	}
                    }
            	}
            }
        }
        
        setMessage("検索が終了しました");
        setDone(true);
    }
    
    private String trimJSpace(String str) {
        String ret = null;
        if (str != null) {
            int index = str.indexOf("　");
            ret = index > 0 ? str.substring(0, index) : str;
        }
        return ret;
    }
}
