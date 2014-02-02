/*
 * LaboModuleBuilder.java
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import open.dolphin.client.ClientContext;
import open.dolphin.delegater.LaboDelegater;
import open.dolphin.infomodel.LaboImportSummary;
import open.dolphin.infomodel.LaboItemValue;
import open.dolphin.infomodel.LaboModuleValue;
import open.dolphin.infomodel.LaboSpecimenValue;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.project.Project;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 * LaboModuleBuilder
 *
 * @author Kazushi Minagawa
 */
public class LaboModuleBuilder {
    
    private String patientId;
    private String patientIdType;
    private String patientIdTypeTableId;
    private String moduleUUID;
    private String confirmDate;
    private ArrayList<LaboModuleValue> allModules;
    private LaboModuleValue laboModule;
    private LaboSpecimenValue laboSpecimen;
    private LaboItemValue laboItem;
    private boolean masterId;
    private List<File> parseFiles;
    private String encoding;
    private LaboDelegater laboDelegater;
    // != null
    private Logger logger;
    private boolean DEBUG = false;
    
    /** LaboModuleBuilder を生成する。 */
    public LaboModuleBuilder() {
    }
    
    public void setLogger(Logger l) {
        this.logger = l;
    }
    
    public String getEncoding() {
        return encoding;
    }
    
    public void setEncoding(String enc) {
        encoding = enc;
    }
    
    public LaboDelegater getLaboDelegater() {
        return laboDelegater;
    }
    
    public void setLaboDelegater(LaboDelegater laboDelegater) {
        this.laboDelegater = laboDelegater;
    }
    
    public List<LaboModuleValue> getProduct() {
        return allModules != null ? allModules : null;
    }
    
    /**
     * 引数のMML検査結果ファイルをパースしその中に含まれる
     * 検査結果モジュールのリストを返す。
     * @param file MML検査結果ファイル
     * @return パースしたモジュール LaboModuleValue のリスト
     */
    public List<LaboModuleValue> build(File file) {
        
        if (logger == null) {
            setLogger(ClientContext.getLogger("laboTest"));
        }
        
        if (file == null ) {
            return null;
        }
        
        try {
            String name = file.getName();
            logger.info(name + " のパースを開始します");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding));
            parse(reader);
            reader.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Exception while building LaboModules" + e.toString());
        }
        
        return getProduct();
    }
    
    /**
     * MML検査結果ファイルをパースする。
     * @param files MML検査結果ファイルの配列
     */
    public List<LaboImportSummary> build(List<File> files) {
        
        if (logger == null) {
            setLogger(ClientContext.getLogger("laboTest"));
        }
        
        parseFiles = files;
        if (parseFiles == null || parseFiles.size() == 0) {
            logger.warn("パースするファイルがありません");
            return null;
        }
        if (laboDelegater == null) {
            logger.warn("ラボテスト用のデリゲータが設定されていません");
            return null;
        }
        if (encoding == null) {
            encoding = "UTF-8";
            logger.debug("デフォルトのエンコーディング" + encoding + "を使用します");
        } else {
            logger.debug("エンコーディングは" + encoding + "が指定されています");
        }
        
        // パース及び登録に成功したデータの情報リストを生成する
        // このメソッドのリターン値
        List<LaboImportSummary> ret = new ArrayList<LaboImportSummary>(files.size());
        
        // ファイルをイテレートする
        for (File file : parseFiles) {
            
            try {
                // ファイル名を出力する
                String name = file.getName();
                logger.info(name + " のパースを開始します");
                
                // 一つのファイルに含まれる全LaboModuleのリストを生成する
                // パース結果のLaboModuleValueを格納するリストである
                if (allModules == null) {
                    allModules = new ArrayList<LaboModuleValue>(1);
                } else {
                    allModules.clear();
                }
                
                // 入力ストリームを生成しパースする
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
                parse(reader);
                reader.close();
                
                // パースの例外をここで全てキャッチする
            } catch (Exception pe) {
                pe.printStackTrace();
                logger.warn("パース中に例外が生じました。");
                if (pe.getCause() != null) {
                    logger.warn("原因: " + pe.getCause());
                }
                if (pe.getMessage() != null) {
                    logger.warn("内容: " + pe.getMessage());
                }
                continue;
            }
            
            // パース後データベースへ登録する
            for (LaboModuleValue module : allModules) {
                
                LaboImportSummary summary = new LaboImportSummary();
                summary.setPatientId(module.getPatientId());
                if (module.getSetName() != null) {
                    summary.setSetName(module.getSetName());
                } else {
                    Collection<LaboSpecimenValue> c = module.getLaboSpecimens();
                    for (LaboSpecimenValue specimen : c) {
                        summary.setSetName(specimen.getSpecimenName());
                    }
                }
                summary.setSampleTime(module.getSampleTime());
                summary.setReportTime(module.getReportTime());
                summary.setLaboratoryCenter(module.getLaboratoryCenter());
                summary.setReportStatus(module.getReportStatus());
                
                PatientModel reply = laboDelegater.putLaboModule(module);
                
                if (laboDelegater.isNoError()) {
                    summary.setPatient(reply);
                    summary.setResult("成功");
                    logger.info("LaboModuleを登録しました。患者ID :" + module.getPatientId());
                    
                    ret.add(summary);
                    
                } else {
                    logger.warn("LaboModule を登録できませんでした。患者ID :" + module.getPatientId());
                    logger.warn(laboDelegater.getErrorMessage());
                    summary.setResult("エラー");
                }
            }
        }
        
        return ret;
    }
    
    /**
     * 入力ストリームの検査結果をパースする。
     */
    public void parse(BufferedReader reader) throws IOException, Exception {
        
        SAXBuilder docBuilder = new SAXBuilder();
        Document doc = docBuilder.build(reader);
        Element root = doc.getRootElement();
        
        // Headerをパースする
        parseHeader(root.getChild("MmlHeader"));
        
        // Bodyをパースする
        parseBody(root.getChild("MmlBody"));
    }
    
    /**
     * MMLヘッダーをパースする。
     * 取得するのは MasterIdの mmlCm:Id 患者IDのみ。
     * @param header ヘッダー要素
     */
    private void parseHeader(Element header) {
        
        // 子要素を列挙する
        List children = header.getChildren();
        Iterator iterator = children.iterator();
        
        while (iterator.hasNext()) {
            
            Element child = (Element) iterator.next();
            String qname = child.getQualifiedName();
            String ename = child.getName();
            Namespace ns = child.getNamespace();
            debug(child.toString());
            
            // MasterIdを取得し患者IDを得る
            if (ename.equals("masterId")) {
                logger.debug("masterId　をパース中");
                masterId = true;
                
            } else if (masterId && qname.equals("mmlCm:Id")) {
                
                // patientId
                patientId = child.getTextTrim();
                logger.debug("patientId = " + patientId);
                
                // type
                patientIdType = child.getAttributeValue("type", ns);
                logger.debug("type = " + patientIdType);
                
                // tableId
                patientIdTypeTableId = child.getAttributeValue("tableId", ns);
                logger.debug("tableId = " + patientIdTypeTableId);
            }
            // 再帰する
            parseHeader(child);
        }
    }
    
    /**
     * MML Bodyをパースする。
     * ModuleItemのDocInfoの uuid, confirmdateを取得する。
     * @param body Body要素
     */
    private void parseBody(Element body) {
        
        // 子供を列挙する
        List children = body.getChildren();
        Iterator iterator = children.iterator();
        
        while (iterator.hasNext()) {
            
            Element child = (Element) iterator.next();
            //String qname = child.getQualifiedName();
            String ename = child.getName();
            //Namespace ns = child.getNamespace();
            debug(child.toString());
            
            if (ename.equals("MmlModuleItem")) {
                logger.debug("MmlModuleItem　をパース中");
                
            } else if (ename.equals("docInfo")) {
                String val = child.getAttributeValue("contentModuleType");
                logger.debug("contentModuleType = " + val);
                
            } else if (ename.equals("docId")) {
                
            } else if (ename.equals("uid")) {
                // 文書のUUIDを取得する
                moduleUUID = child.getTextTrim();
                logger.debug("uid = " + moduleUUID);
                
            } else if (ename.equals("confirmDate")) {
                // 確定日を取得する ModuleIte-DocInfo
                confirmDate = child.getTextTrim();
                logger.debug("confirmDate = " + confirmDate);
                
            } else if (ename.equals("content")) {
                // content要素をパースする
                parseContent(child);
            }
            
            parseBody(child);
        }
    }
    
    /**
     * Content要素をパースする。
     * クライアント情報、ラボセンター情報、検体情報、検査項目情報を取得する。
     * @param content 検査結果があるコンテント要素
     */
    private void parseContent(Element content) {
        
        // コンテント要素の子を列挙する
        List children = content.getChildren();
        Iterator iterator = children.iterator();
        
        while (iterator.hasNext()) {
            
            Element child = (Element) iterator.next();
            //String ename = child.getName();
            String qname = child.getQualifiedName();
            Namespace ns = child.getNamespace();
            debug(child.toString());
            String val = null;
            
            if (qname.equals("mmlLb:TestModule")) {
                // 解析するモジュールはmmlLb:TestModule
                logger.debug("TestModule　をパース中");
                
                // LaboModuleValueオブジェクトを生成する
                // このオブジェクトの属性にMMLの内容が設定される
                laboModule = new LaboModuleValue();
                allModules.add(laboModule);
                
                // これまでに取得した基本情報を設定する
                // 患者ID、ModuleUUID、確定日を設定する
                laboModule.setCreator(Project.getUserModel());
                laboModule.setPatientId(patientId);
                laboModule.setPatientIdType(patientIdType);
                laboModule.setPatientIdTypeCodeSys(patientIdTypeTableId);
                laboModule.setDocId(moduleUUID);
                
                // 確定日、適合開始日、記録日を設定する
                Date confirmed = ModelUtils.getDateTimeAsObject(confirmDate);
                laboModule.setConfirmed(confirmed);
                laboModule.setStarted(confirmed);
                laboModule.setRecorded(new Date());
                laboModule.setStatus("F");
                
            } else if (qname.equals("mmlLb:information")) {
                // mmlLb:information要素をパースする
                logger.debug("infomation　をパース中");
                
                // 登録ID属性を取得する
                val = child.getAttributeValue("registId", ns);
                logger.debug("registId = " + val);
                laboModule.setRegistId(val);
                
                // サンプルタイム属性を取得する
                val = child.getAttributeValue("sampleTime", ns);
                logger.debug("sampleTime = " + val);
                laboModule.setSampleTime(val);
                
                // 登録時刻属性を取得する
                val = child.getAttributeValue("registTime", ns);
                logger.debug("registTime = " + val);
                laboModule.setRegistTime(val);
                
                // 報告時間属性を取得する
                val = child.getAttributeValue("reportTime", ns);
                logger.debug("reportTime = " + val);
                laboModule.setReportTime(val);
                
                
            } else if (qname.equals("mmlLb:reportStatus")) {
                // mmlLb:reportStatus要素をパースする
                logger.info("reportStatus　をパース中");
                
                // レポートステータスを取得する
                val = child.getTextTrim();
                logger.debug("reportStatus = " + val);
                laboModule.setReportStatus(val);
                
                // statusCodeを取得する
                val = child.getAttributeValue("statusCode", ns);
                logger.debug("statusCode = " + val);
                laboModule.setReportStatusCode(val);
                
                // statusCodeIdを取得する
                val = child.getAttributeValue("statusCodeId", ns);
                logger.debug("statusCodeId = " + val);
                laboModule.setReportStatusCodeId(val);
                
            } else if (qname.equals("mmlLb:facility")) {
                // クライアント施設情報をパースする
                logger.debug("facility　をパース中");
                
                // 施設を取得する
                val = child.getTextTrim();
                logger.debug("facility = " + val);
                laboModule.setClientFacility(val);
                
                // 施設コード属性を取得する
                val = child.getAttributeValue("facilityCode", ns);
                logger.debug("facilityCode = " + val);
                laboModule.setClientFacilityCode(val);
                
                // 施設コード体系を登録する
                val = child.getAttributeValue("facilityCodeId", ns);
                logger.debug("facilityCodeId = " + val);
                laboModule.setClientFacilityCodeId(val);
                
            } else if (qname.equals("mmlLb:laboratoryCenter")) {
                // ラボセンター情報をパースする
                logger.debug("laboratoryCenter　をパース中");
                
                // ラボセンターを取得する
                val = child.getTextTrim();
                logger.debug("laboratoryCenter = " + val);
                laboModule.setLaboratoryCenter(val);
                
                // ラボコードを取得する
                val = child.getAttributeValue("centerCode", ns);
                logger.debug("centerCode = " + val);
                laboModule.setLaboratoryCenterCode(val);
                
                // ラボコード体系を取得する
                val = child.getAttributeValue("centerCodeId", ns);
                logger.debug("centerCodeId = " + val);
                laboModule.setLaboratoryCenterCodeId(val);
                
            } else if (qname.equals("mmlLb:labotest")) {
                // labotest要素をパースする
                logger.debug("labotest　をパース中");
                
            } else if (qname.equals("mmlLb:specimen")) {
                // 検体情報をパースする
                logger.debug("specimen　をパース中");
                laboSpecimen = new LaboSpecimenValue();
                //laboSpecimen.setId(GUIDGenerator.generate(laboSpecimen)); // EJB3.0で変更
                laboModule.addLaboSpecimen(laboSpecimen);
                laboSpecimen.setLaboModule(laboModule);	// 関係を設定する
                
            } else if (qname.equals("mmlLb:specimenName")) {
                // 検体名を取得する
                val = child.getTextTrim();
                logger.debug("specimenName = " + val);
                laboSpecimen.setSpecimenName(val);
                
                // spCodeを取得する
                val = child.getAttributeValue("spCode", ns);
                logger.debug("spCode = " + val);
                laboSpecimen.setSpecimenCode(val);
                
                // spCodeIdを取得する
                val = child.getAttributeValue("spCodeId", ns);
                logger.debug("spCodeId = " + val);
                laboSpecimen.setSpecimenCodeId(val);
                
            } else if (qname.equals("mmlLb:item")) {
                // 検査項目をパースする
                logger.debug("item　をパース中");
                laboItem = new LaboItemValue();
                //laboItem.setId(GUIDGenerator.generate(laboItem)); // EJB3.0で変更
                laboSpecimen.addLaboItem(laboItem);
                laboItem.setLaboSpecimen(laboSpecimen);	// 関係を設定する
                
            } else if (qname.equals("mmlLb:itemName")) {
                // 検査項目名をパースする
                logger.debug("itemName　をパース中");
                
                // 検査項目名を取得する
                val = child.getTextTrim();
                logger.debug("itemName = " + val);
                laboItem.setItemName(val);
                
                // 項目コードを取得する
                val = child.getAttributeValue("itCode", ns);
                logger.debug("itCode = " + val);
                laboItem.setItemCode(val);
                
                // 項目コード体系を取得する
                val = child.getAttributeValue("itCodeId", ns);
                logger.debug("itCodeId = " + val);
                laboItem.setItemCodeId(val);
                
            } else if (qname.equals("mmlLb:value")) {
                // 検査値をパースする
                logger.debug("value　をパース中");
                
                // 値を取得する
                val = child.getTextTrim();
                logger.debug("value = " + val);
                laboItem.setItemValue(val);
                
            } else if (qname.equals("mmlLb:numValue")) {
                // 数値要素をパースする
                logger.debug("value　をパース中");
                
                // 値を取得する
                val = child.getTextTrim();
                logger.debug("numValue = " + val);
                // TODO laboItem.setValue()***************************
                
                // up
                val = child.getAttributeValue("up", ns);
                logger.debug("up = " + val);
                laboItem.setUp(val);
                
                // low
                val = child.getAttributeValue("low", ns);
                logger.debug("low = " + val);
                laboItem.setLow(val);
                
                // normal
                val = child.getAttributeValue("normal", ns);
                logger.debug("low = " + val);
                laboItem.setNormal(val);
                
                // out
                val = child.getAttributeValue("out", ns);
                logger.debug("out = " + val);
                laboItem.setNout(val);
                
            } else if (qname.equals("mmlLb:unit")) {
                // 単位情報を取得する
                logger.debug("unit　をパース中");
                
                // value
                val = child.getTextTrim();
                logger.debug("unit = " + val);
                laboItem.setUnit(val);
                
                // uCode
                val = child.getAttributeValue("uCode", ns);
                logger.debug("uCode = " + val);
                laboItem.setUnitCode(val);
                
                // uCodeId
                val = child.getAttributeValue("uCodeId", ns);
                logger.debug("uCodeId = " + val);
                laboItem.setUnitCodeId(val);
            }
            
            parseContent(child);
        }
    }
    
    private void debug(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }
}
