package open.dolphin.touch;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import open.dolphin.infomodel.*;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author kazushi Minagawa.
 */
public class JSONStampBuilder {
    
    public String build(InfoModel model) {
        
        if (model==null) {
            return null;
        }
        
        if (model instanceof BundleDolphin) {
            return createBundleJSON(model);
            
        } else if (model instanceof RegisteredDiagnosisModel) {
            return createDiagnosisJSON(model);
            
        } else if (model instanceof TextStampModel) {
            return createTextStampJSON(model);
        }
        
        return null;  
    }
    
    private String createBundleJSON(InfoModel model) {
        
        BundleDolphin bundle = (BundleDolphin)model;
        String className = bundle.getClassName();           // 診療行為名
        String classCode = bundle.getClassCode();           // 診療行為コード
        String classCodeSystem = bundle.getClassCodeSystem();     // コード体系
        String admin = bundle.getAdmin();                   // 用法
        String adminCode = bundle.getAdminCode();           // 用法コード
        String adminCodeSystem = bundle.getAdminCodeSystem();     // 用法コード体系
        String adminMemo = bundle.getAdminMemo();           // 用法メモ
        String bundleNumber = bundle.getBundleNumber();     // バンドル数
        ClaimItem[] items = bundle.getClaimItem();          // バンドル構成品目
        String memo = bundle.getMemo();                     // メモ
        String insurance = bundle.getInsurance();           // 保険種別
        String orderName = bundle.getOrderName();
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        stringProp("className",className,sb,true);
        stringProp("classCode",classCode,sb,true);
        stringProp("classCodeSystem",classCodeSystem,sb,true);
        stringProp("admin",admin,sb,true);
        stringProp("adminCode",adminCode,sb,true);
        stringProp("adminCodeSystem",adminCodeSystem,sb,true);
        stringProp("adminMemo",adminMemo,sb,true);
        stringProp("bundleNumber",bundleNumber,sb,true);
        stringProp("memo",memo,sb,true);
        stringProp("insurance",insurance,sb,true);
        stringProp("orderName",orderName,sb,true);
        
        sb.append(addQouteColon("claimItem")).append("[");
        
        for (int i=0; i<items.length;i++) {
            ClaimItem ci = items[i];
            sb.append("{");
            stringProp("name",ci.getName(),sb,true);
            stringProp("code",ci.getCode(),sb,true);
            stringProp("codeSystem",ci.getCodeSystem(),sb,true);
            stringProp("classCode",ci.getClassCode(),sb,true);
            stringProp("classCodeSystem",ci.getClassCodeSystem(),sb,true);
            stringProp("number",ci.getNumber(),sb,true);
            stringProp("unit",ci.getUnit(),sb,true);
            stringProp("numberCode",ci.getNumberCode(),sb,true);
            stringProp("numberCodeSystem",ci.getNumberCodeSystem(),sb,true);
            stringProp("memo",ci.getMemo(),sb,true);
            stringProp("ykzKbn",ci.getYkzKbn(),sb,false);
//s.oh^ 予防医学
            if(sb.toString().lastIndexOf(",") == sb.toString().length() - 1) {
                sb = sb.deleteCharAt(sb.length() - 1);
            }
//s.oh$
            sb.append("}");
            if (!(i==items.length-1)) {
                sb.append(",");
            }
        }
        sb.append("]");
        sb.append("}");
        
        return sb.toString();
    }
    
    private String createDiagnosisJSON(InfoModel model) {
        
        RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel)model;
        String diagnosis = rd.getDiagnosis();
        String diagnosisCode = rd.getDiagnosisCode();
        String diagnosisCodeSystem= rd.getDiagnosisCodeSystem();
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        stringProp("diagnosis",diagnosis,sb,true);
        stringProp("diagnosisCode",diagnosisCode,sb,true);
        stringProp("diagnosisCodeSystem",diagnosisCodeSystem,sb,false);
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private String createTextStampJSON(InfoModel model) {
        try {
            TextStampModel tx = (TextStampModel)model;
//minagawa^               
            StringWriter sw = new StringWriter();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(sw, tx);
            String json = sw.toString();
            sw.close();
            //System.err.println(json);
            return json;
//minagawa$        
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    private void stringProp(String prop, String val, StringBuilder sb, boolean camma) {
        if (prop!=null && val!=null) {
            sb.append(addQoute(prop)).append(":").append(addQoute(val));
            if (camma) {
                sb.append(",");
            }
        }
    }
    
    private String addQouteColon(String val) {
        if (val!=null) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"").append(val).append("\":");
            return sb.toString();
        }
        return null;
    }
    
    private String addQoute(String val) {
        if (val!=null) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"").append(val).append("\"");
            return sb.toString();
        }
        return null;
    }
}
