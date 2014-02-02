package open.dolphin.dao;

import java.sql.*;
import java.util.*;
import open.dolphin.infomodel.AdminEntry;

import open.dolphin.infomodel.DiseaseEntry;
import open.dolphin.infomodel.MedicineEntry;
import open.dolphin.infomodel.ToolMaterialEntry;
import open.dolphin.infomodel.TreatmentEntry;
import open.dolphin.project.Project;
import open.dolphin.util.*;

/**
 * SqlMasterDao
 *
 * @author Kazushi Minagawa
 */
public final class SqlMasterDao extends SqlDaoBean {
    
    private static final String DISEASE_MASTER = "disease";
    private static final String MEDICAL_SUPLLIES = "medicine";
    private static final String ADMIN_MASTER = "admin";
    private static final String MEDICINE_CODE = "20";
    private static final String TREATMENT_MASTER = "treatment";
    private static final String TOOL_MATERIAL_MASTER = "tool_material";
    private static final String YKZKBN = "4";	// 薬剤区分
    
    private int totalCount;
    
    /** Creates a new instance of SqlMasterDao */
    public SqlMasterDao() {
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    /**
     * マスタを項目の名前で検索する。
     * @param master            検索対象のマスタ
     * @param name		項目の名称
     * @param startsWith	前方一致の時 TRUE
     * @param serchClassCode	診療行為マスタ検索の場合の点数集計先コード
     * @param sortBy            ソートするカラム
     * @param order             昇順または降順
     * @return                  マスタ項目のリスト
     */
    public ArrayList getByName(String master, String name, boolean startsWith, String serchClassCode,
            String sortBy, String order) {
        
        // 戻り値のリストを用意する
        ArrayList results = null;
        
        // 半角のローマ字(ユーザ入力)を全角のローマ字(マスタで使用)に変換する
        String zenkakuRoman = StringTool.toZenkakuUpperLower(name);
        
        if (master.equals(DISEASE_MASTER)) {
            // 傷病名マスタを検索する
            results = getDiseaseByName(name, startsWith, sortBy, order);
            
        } else if (master.equals(MEDICAL_SUPLLIES)) {
            // 医薬品マスタを検索する
            if (serchClassCode.equals(MEDICINE_CODE)) {
                // 薬剤の検索を行う
                results = getMedicineByName(zenkakuRoman, startsWith, sortBy, order);
                
            } else {
                // 注射薬の検索を行う
                results = getInjectionByName(zenkakuRoman, startsWith, sortBy, order);
            }
            
        } else if (master.equals(TREATMENT_MASTER)) {
            // 診療行為マスタを検索する
            results = getTreatmentByName(zenkakuRoman, startsWith, serchClassCode, sortBy, order);
            
        } else if (master.equals(TOOL_MATERIAL_MASTER)) {
            // 特定機材マスタを検索する
            results = getToolMaterialByName(zenkakuRoman, startsWith, sortBy, order);
            
        } else if (master.equals(ADMIN_MASTER)) {
            // 用法マスタを検索する
            results = getAdminByName(zenkakuRoman, startsWith, sortBy, order);
            
        } else {
            throw new RuntimeException("Unsupported master: " + master);
        }
        
        return results;
    }
    
    
    /**
     * 病名検索を行う。
     * @param text	検索キーワード
     * @param startsWith 前方一致の時 true
     * @param sortBy ソートするカラム
     * @param order 昇順降順の別
     * @return 病名リスト
     */
    private ArrayList<DiseaseEntry> getDiseaseByName(String text, boolean startsWith, String sortBy, String order) {
        
        // 前方一致検索を行う
        String sql = getDiseaseql(text, sortBy, order, true);
        ArrayList<DiseaseEntry> ret = getDiseaseCollection(sql);
        
        // NoError で結果がないとき部分一致検索を行う
        if (isNoError() && (ret == null || ret.size() == 0) ) {
            sql = getDiseaseql(text, sortBy, order, false);
            ret = getDiseaseCollection(sql);
        }
        
        return ret;
    }
    private String getDiseaseql(String text, String sortBy, String order, boolean forward)  {
        
        String word = null;
        StringBuilder buf = new StringBuilder();
        
        buf.append("select byomeicd, byomei, byomeikana, icd10, haisiymd from tbl_byomei where ");
        
        // 全て数字の場合はコードを検索する
        if (StringTool.isAllDigit(text)) {
            word = text;
            buf.append("byomeicd ~ ");
            
        } else {
            // それ以外は名称を検索する
            word = text;
            buf.append("byomei ~ ");
        }
        
        if (forward) {
            buf.append(addSingleQuote("^" + word));
        } else {
            buf.append(addSingleQuote(word));
        }
        
        String orderBy = getOrderBy(sortBy, order);
        if (orderBy == null) {
            orderBy = " order by byomeicd";
        }
        buf.append(orderBy);
        
        String sql = buf.toString();
        debug(sql);
        
        return sql;
    }
    private ArrayList<DiseaseEntry> getDiseaseCollection(String sql) {
        
        Connection con = null;
        ArrayList<DiseaseEntry> collection = null;
        ArrayList<DiseaseEntry> outUse = null;
        Statement st = null;
        
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            // ValueObject
            DiseaseEntry de = null;
            collection = new ArrayList<DiseaseEntry>();
            outUse = new ArrayList<DiseaseEntry>();
            
            while (rs.next()) {
                de = new DiseaseEntry();
                de.setCode(rs.getString(1));        // Code
                de.setName(rs.getString(2));        // Name
                de.setKana(rs.getString(3));         // Kana
                de.setIcdTen(rs.getString(4));      // IcdTen
                de.setDisUseDate(rs.getString(5));  // DisUseDate
                
                if (de.isInUse()) {
                    collection.add(de);
                } else {
                    outUse.add(de);
                }
            }
            rs.close();
            collection.addAll(outUse);
            
            closeStatement(st);
            closeConnection(con);
            return collection;
            
        } catch (Exception e) {
            processError(e);
            closeConnection(con);
            closeStatement(st);
        }
        return null;
    }
    
    /**
     * 医薬品マスタを検索する。
     * @param text	検索キーワード
     * @param startsWith	noUse
     * @param sortBy	ソートララム
     * @param order	昇順降順
     * @return	医薬品リスト
     */
    private ArrayList<MedicineEntry> getMedicineByName(String text, boolean startsWith, String sortBy, String order) {
        // 前方一致検索を行う
        String sql = getMedicineSql(text, sortBy, order, true);
        ArrayList<MedicineEntry> ret = getMedicineCollection(sql);
        
        // NoError で結果がないとき部分一致検索を行う
        if (isNoError() && (ret == null || ret.size() == 0) ) {
            sql = getMedicineSql(text, sortBy, order, false);
            ret = getMedicineCollection(sql);
        }
        
        return ret;
    }
    private String getMedicineSql(String text, String sortBy, String order, boolean forward) {
        
        //
        // 点数マスタが6で始まり、薬剤区分が注射薬 4 でないものを検索する
        //
        String word = null;
        StringBuilder buf = new StringBuilder();
        
        buf.append("select srycd,name,kananame,taniname,tensikibetu,ten,ykzkbn,yakkakjncd,yukostymd,yukoedymd from tbl_tensu where ");
        if (Project.getOrcaVersion().startsWith("4")) {
            int hospnum = getHospNum();
            buf.append("hospnum=");
            buf.append(hospnum);
            buf.append(" and ");
        }
        buf.append("srycd ~ '^6' and ");
        
        // 全て数字であればコードを検索し、それ以外は名称を検索する
        if (StringTool.isAllDigit(text)) {
            word = text;
            buf.append("srycd ~ ");
            
        } else {
            word = text;
            buf.append("name ~ ");
        }
        
        if (forward) {
            buf.append(addSingleQuote("^" + word));
        } else {
            buf.append(addSingleQuote(word));
        }
        
        //
        // 注射薬でない
        // 
        buf.append(" and ykzkbn != ");
        buf.append(addSingleQuote(YKZKBN));
        
        String orderBy = getOrderBy(sortBy, order);
        if (orderBy == null) {
            orderBy = " order by srycd";
        }
        buf.append(orderBy);
        
        String sql = buf.toString();
        printTrace(sql);
        
        return sql;
    }
    private ArrayList<MedicineEntry> getMedicineCollection(String sql) {
        
        Connection con = null;
        ArrayList<MedicineEntry> collection = null;
        ArrayList<MedicineEntry> outUse = null;
        Statement st = null;
        
        // 前方一致を試みる
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            // ValueObject
            MedicineEntry me = null;
            collection = new ArrayList<MedicineEntry>();
            outUse = new ArrayList<MedicineEntry>();
            
            while (rs.next()) {
                me = new MedicineEntry();
                me.setCode(rs.getString(1));        // Code
                me.setName(rs.getString(2));        // Name
                me.setKana(rs.getString(3));        // Name
                me.setUnit(rs.getString(4));        // Unit
                me.setCostFlag(rs.getString(5));    // Cost flag
                me.setCost(rs.getString(6));        // Cost
                me.setYkzKbn(rs.getString(7));      // 薬剤区分
                me.setJNCD(rs.getString(8));        // JNCD
                me.setStartDate(rs.getString(9));  // startDate
                me.setEndDate(rs.getString(10));    // endDate
                
                if (me.isInUse()) {
                    collection.add(me);
                } else {
                    outUse.add(me);
                }
            }
            rs.close();
            collection.addAll(outUse);
            
            closeStatement(st);
            closeConnection(con);
            return collection;
            
        } catch (Exception e) {
            processError(e);
            closeStatement(st);
            closeConnection(con);
        }
        return null;
    }
    
    /**
     * 注射薬を検索する。
     * @param text	検索キーワード
     * @param startsWith no use
     * @param sortBy	ソートするカラム
     * @param order	昇順降順
     * @return		注射薬のリスト
     */
    private ArrayList<MedicineEntry> getInjectionByName(String text, boolean startsWith, String sortBy, String order) {
        // 前方一致検索を行う
        String sql = getInjectionSql(text, sortBy, order, true);
        ArrayList<MedicineEntry> ret = getInjectionCollection(sql);
        
        // NoError で結果がないとき部分一致検索を行う
        if (isNoError() && (ret == null || ret.size() == 0) ) {
            sql = getInjectionSql(text, sortBy, order, false);
            ret = getInjectionCollection(sql);
        }
        
        return ret;
    }
    private String getInjectionSql(String text, String sortBy, String order, boolean forward) {
        
        
        String word = null;
        StringBuilder buf = new StringBuilder();
        
        buf.append("select srycd,name,kananame,taniname,tensikibetu,ten,ykzkbn,yakkakjncd,yukostymd,yukoedymd from tbl_tensu where ");
        if (Project.getOrcaVersion().startsWith("4")) {
            int hospnum = getHospNum();
            buf.append("hospnum=");
            buf.append(hospnum);
            buf.append(" and ");
        }
        buf.append("srycd ~ '^6' and ");
        
        if (StringTool.isAllDigit(text)) {
            word = text;
            buf.append("srycd ~ ");
            
        } else {
            word = text;
            buf.append("name ~ ");
        }
        if (forward) {
            buf.append(addSingleQuote("^" + word));
        } else {
            buf.append(addSingleQuote(word));
        }
        buf.append(" and ykzkbn = ");
        buf.append(addSingleQuote(YKZKBN));
        
        String orderBy = getOrderBy(sortBy, order);
        if (orderBy == null) {
            orderBy =" order by srycd";
        }
        buf.append(orderBy);
        
        String sql = buf.toString();
        printTrace(sql);
        
        return sql;
    }
    private ArrayList<MedicineEntry> getInjectionCollection(String sql) {
        
        Connection con = null;
        ArrayList<MedicineEntry> collection = null;
        ArrayList<MedicineEntry> outUse = null;
        Statement st = null;
        
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            // ValueObject
            MedicineEntry me = null;
            collection = new ArrayList<MedicineEntry>();
            outUse = new ArrayList<MedicineEntry>();
            
            while (rs.next()) {
                me = new MedicineEntry();
                me.setCode(rs.getString(1));        // Code
                me.setName(rs.getString(2));        // Name
                me.setKana(rs.getString(3));        // Name
                me.setUnit(rs.getString(4));        // Unit
                me.setCostFlag(rs.getString(5));    // Cost flag
                me.setCost(rs.getString(6));
                me.setYkzKbn(rs.getString(7));      // 薬剤区分
                me.setJNCD(rs.getString(8));
                me.setStartDate(rs.getString(9));  // start Date
                me.setEndDate(rs.getString(10));    // end Date
                
                if (me.isInUse()) {
                    collection.add(me);
                } else {
                    outUse.add(me);
                }
            }
            rs.close();
            collection.addAll(outUse);
            
            closeStatement(st);
            closeConnection(con);
            return collection;
            
        } catch (Exception e) {
            processError(e);
            closeStatement(st);
            closeConnection(con);
        }
        return null;
    }
    
    /**
     * 診療行為マスタを検索する。
     * @param text	検索キーワード
     * @param startsWith	no use
     * @param orderClassCode	点数集計先
     * @param sortBy	ソートカラム
     * @param order	昇順降順
     * @return	診療行為リスト
     */
    private ArrayList<TreatmentEntry> getTreatmentByName(String text, boolean startsWith, String orderClassCode, String sortBy, String order) {
        // 前方一致検索を行う
        String sql = getTreatemenrSql(text, orderClassCode, sortBy, order, true);
        ArrayList<TreatmentEntry> ret = getTreatmentCollection(sql);
        
        // NoError で結果がないとき部分一致検索を行う
        if (isNoError() && (ret == null || ret.size() == 0) ) {
            sql = getTreatemenrSql(text, orderClassCode, sortBy, order, false);
            ret = getTreatmentCollection(sql);
        }
        return ret;
    }
    private String getTreatemenrSql(String text,  String orderClassCode, String sortBy, String order, boolean forward) {
        
        String word = null;
        StringBuilder buf = new StringBuilder();
        
        buf.append("select srycd,name,kananame,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn,yukostymd,yukoedymd from tbl_tensu where ");
        if (Project.getOrcaVersion().startsWith("4")) {
            int hospnum = getHospNum();
            buf.append("hospnum=");
            buf.append(hospnum);
            buf.append(" and ");
        } 
        buf.append("(srycd ~ '^1' or srycd ~ '^00') and ");
        
        if (StringTool.isAllDigit(text)) {
            word = text;
            buf.append("srycd ~ ");
            
        } else {
            word = text;
            buf.append("name ~ ");
        }
        
        if (forward) {
            buf.append(addSingleQuote("^" + word));
        } else {
            buf.append(addSingleQuote(word));
        }
        
        StringBuilder sbd = new StringBuilder();
        if (orderClassCode != null) {
            String[] cClass = new String[]{"",""};
            int index = 0;
            StringTokenizer tokenizer = new StringTokenizer(orderClassCode,"-");
            while (tokenizer.hasMoreTokens()) {
                cClass[index++] = tokenizer.nextToken();
            }
            String min = cClass[0];
            String max = cClass[1];
            
            if ( (! min.equals("")) && max.equals("") ) {
                sbd.append(" and srysyukbn = ");
                sbd.append(addSingleQuote(min));
                
            } else if ((! min.equals("")) && (! max.equals("")) ) {
                sbd.append(" and srysyukbn >= ");
                sbd.append(addSingleQuote(min));
                sbd.append(" and srysyukbn <= ");
                sbd.append(addSingleQuote(max));
            }
        }
        String sql2 = sbd.toString();
        buf.append(sql2);
        
        String orderBy = getOrderBy(sortBy, order);
        if (orderBy == null) {
            orderBy = " order by srycd";
        }
        buf.append(orderBy);
        
        String sql = buf.toString();
        printTrace(sql);
        
        return sql;
    }
    private ArrayList<TreatmentEntry> getTreatmentCollection(String sql) {
        
        Connection con = null;
        ArrayList<TreatmentEntry> collection = null;
        ArrayList<TreatmentEntry> outUse = null;
        Statement st = null;
        
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            // ValueObject
            TreatmentEntry te = null;
            collection = new ArrayList<TreatmentEntry>();
            outUse = new ArrayList<TreatmentEntry>();
            
            while (rs.next()) {
                te = new TreatmentEntry();
                te.setCode(rs.getString(1));            // srycd
                te.setName(rs.getString(2));            // name
                te.setKana(rs.getString(3));            // kana
                //te.setUnit(rs.getString(3));          // Unit
                te.setCostFlag(rs.getString(4));        // tensikibetu
                te.setCost(rs.getString(5));            // ten
                te.setInOutFlag(rs.getString(6));       // nyugaitekkbn
                te.setOldFlag(rs.getString(7));  	// routekkbn									// OldFlag
                te.setClaimClassCode(rs.getString(8));  // srysuykbn
                te.setHospitalClinicFlag(rs.getString(9)); // hospsrykbn
                te.setStartDate(rs.getString(10));     // start
                te.setEndDate(rs.getString(11));     // end
                
                if (te.isInUse()) {
                    collection.add(te);
                } else {
                    outUse.add(te);
                }
            }
            rs.close();
            collection.addAll(outUse);
            
            closeStatement(st);
            closeConnection(con);
            return collection;
            
        } catch (Exception e) {
            processError(e);
            closeStatement(st);
            closeConnection(con);
        }
        return null;
    }
    
    /**
     * 診療行為マスタを検索する。
     * @param master
     * @param claimClass 診療行為コード(点数集計先)
     * @param sortBy	ソートカラム
     * @param order	昇順降順
     * @return診療行為リスト
     */
    public ArrayList<TreatmentEntry> getByClaimClass(String master, String claimClass, String sortBy, String order) {
        
        Connection con = null;
        ArrayList<TreatmentEntry> collection = null;
        ArrayList<TreatmentEntry> outUse = null;
        Statement st = null;
        
        // 診療行為コードの範囲を分解する
        // ex. 700-799 等
        String[] cClass = new String[]{"",""};
        int index = 0;
        StringTokenizer tokenizer = new StringTokenizer(claimClass,"-");
        while (tokenizer.hasMoreTokens()) {
            cClass[index++] = tokenizer.nextToken();
        }
        String min = cClass[0];
        String max = cClass[1];
        
        StringBuffer buf = new StringBuffer();
        buf.append("select srycd,name,kananame,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn,yukostymd,yukoedymd from tbl_tensu where ");
        if (Project.getOrcaVersion().startsWith("4")) {
            int hospnum = getHospNum();
            buf.append("hospnum=");
            buf.append(hospnum);
            buf.append(" and ");
        }
        
        buf.append("srycd ~ '^1' and ");
        
        if ( (! min.equals("")) && max.equals("") ) {
            buf.append("srysyukbn = ");
            buf.append(addSingleQuote(min));
            
        } else if ((! min.equals("")) && (! max.equals("")) ) {
            buf.append("srysyukbn >= ");
            buf.append(addSingleQuote(min));
            buf.append(" and srysyukbn <= ");
            buf.append(addSingleQuote(max));
        }
        
        String orderBy = getOrderBy(sortBy, order);
        if (orderBy != null) {
            buf.append(orderBy);
        } else {
            buf.append(" order by srycd");
        }
        
        String sql = buf.toString();
        printTrace(sql);
        
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            // ValueObject
            TreatmentEntry te = null;
            collection = new ArrayList<TreatmentEntry>();
            outUse = new ArrayList<TreatmentEntry>();
            
            while (rs.next()) {
                te = new TreatmentEntry();
                te.setCode(rs.getString(1));            // srycd
                te.setName(rs.getString(2));            // name
                te.setKana(rs.getString(3));            // kana
                //te.setUnit(rs.getString(3));          // Unit
                te.setCostFlag(rs.getString(4));        // tensikibetu
                te.setCost(rs.getString(5));            // ten
                te.setInOutFlag(rs.getString(6));       // nyugaitekkbn
                te.setOldFlag(rs.getString(7));  	// routekkbn									// OldFlag
                te.setClaimClassCode(rs.getString(8));  // srysuykbn
                te.setHospitalClinicFlag(rs.getString(9)); // hospsrykbn
                te.setStartDate(rs.getString(10));     // start
                te.setEndDate(rs.getString(11));     // end
                
                if (te.isInUse()) {
                    collection.add(te);
                } else {
                    outUse.add(te);
                }
            }
            rs.close();
            collection.addAll(outUse);
            
            closeStatement(st);
            closeConnection(con);
            return collection;
            
        } catch (Exception e) {
            processError(e);
            closeStatement(st);
            closeConnection(con);
        }
        return null;
    }
    
    /**
     * 放射線撮影部位の検索を行う。
     * @param master
     * @param sortBy ソートカラム
     * @param order 昇順降順
     * @return 診療行為リスト
     */
    public ArrayList<TreatmentEntry> getRadLocation(String master, String sortBy, String order) {
        
        
        Connection con = null;
        ArrayList<TreatmentEntry> collection = null;
        ArrayList<TreatmentEntry> outUse = null;
        Statement st = null;
        
        StringBuffer buf = new StringBuffer();
        buf.append("select srycd,name,kananame,srysyukbn,yukostymd,yukoedymd from tbl_tensu where ");
        if (Project.getOrcaVersion().startsWith("4")) {
            int hospnum = getHospNum();
            buf.append("hospnum=");
            buf.append(hospnum);
            buf.append(" and ");
        } 
        
        buf.append("srycd ~ '^002'");
        String orderBy = getOrderBy(sortBy, order);
        if (orderBy != null) {
            buf.append(orderBy);
        } else {
            buf.append(" order by srycd");
        }
        String sql = buf.toString();
        printTrace(sql);
        
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            // ValueObject
            TreatmentEntry te = null;
            collection = new ArrayList<TreatmentEntry>();
            outUse = new ArrayList<TreatmentEntry>();
            
            while (rs.next()) {
                te = new TreatmentEntry();
                te.setCode(rs.getString(1));            // srycd
                te.setName(rs.getString(2));            // name
                te.setKana(rs.getString(3));            // kana								// OldFlag
                te.setClaimClassCode(rs.getString(4));  // srysuykbn
                te.setStartDate(rs.getString(5));     // start
                te.setEndDate(rs.getString(6));     // end
                
                if (te.isInUse()) {
                    collection.add(te);
                } else {
                    outUse.add(te);
                }
            }
            rs.close();
            collection.addAll(outUse);
            
            closeStatement(st);
            closeConnection(con);
            return collection;
            
        } catch (Exception e) {
            processError(e);
            closeStatement(st);
            closeConnection(con);
        }
        return null;
    }
    
    /**
     * 特定機材マスタの検索を行う。
     * @param text 検索キーワード
     * @param startsWith no use
     * @param sortBy ソートカラム
     * @param order 昇順降順
     * @return 特定機材リスト
     */
    private ArrayList<ToolMaterialEntry> getToolMaterialByName(String text, boolean startsWith, String sortBy, String order) {
        // 前方一致検索を行う
        String sql = getToolMaterialSql(text, sortBy, order, true);
        ArrayList<ToolMaterialEntry> ret = getToolMaterialCollection(sql);
        
        // NoError で結果がないとき部分一致検索を行う
        if (isNoError() && (ret == null || ret.size() == 0) ) {
            sql = getToolMaterialSql(text, sortBy, order, false);
            ret = getToolMaterialCollection(sql);
        }
        return ret;
    }
    private String getToolMaterialSql(String text, String sortBy, String order, boolean forward) {
        
        String word = null;
        StringBuilder buf = new StringBuilder();
        
        buf.append("select srycd,name,kananame,taniname,tensikibetu,ten,yukostymd,yukoedymd from tbl_tensu where ");
        if (Project.getOrcaVersion().startsWith("4")) {
            int hospnum = getHospNum();
            buf.append("hospnum=");
            buf.append(hospnum);
            buf.append(" and ");
        } 
        buf.append("srycd ~ '^7' and ");
        
        if (StringTool.isAllDigit(text)) {
            word = text;
            buf.append("srycd ~ ");
            
        } else {
            word = text;
            buf.append("name ~ ");
        }
        
        if (forward) {
            buf.append(addSingleQuote("^" + word));
        } else {
            buf.append(addSingleQuote(word));
        }
        
        String orderBy = getOrderBy(sortBy, order);
        if (orderBy == null) {
            orderBy = " order by srycd";
        }
        buf.append(orderBy);
        String sql = buf.toString();
        printTrace(sql);
        
        return sql;
    }
    private ArrayList<ToolMaterialEntry> getToolMaterialCollection(String sql) {
        
        Connection con = null;
        ArrayList<ToolMaterialEntry> collection = null;
        ArrayList<ToolMaterialEntry> outUse = null;
        Statement st = null;
        
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            // ValueObject
            ToolMaterialEntry te = null;
            collection = new ArrayList<ToolMaterialEntry>();
            outUse = new ArrayList<ToolMaterialEntry>();
            
            while (rs.next()) {
                te = new ToolMaterialEntry();
                te.setCode(rs.getString(1));        // Code
                te.setName(rs.getString(2));        // name
                te.setKana(rs.getString(3));        // kana
                te.setUnit(rs.getString(4));        // Unit
                te.setCostFlag(rs.getString(5));    // Cost flag
                te.setCost(rs.getString(6));        // Cost
                te.setStartDate(rs.getString(7));        // start
                te.setEndDate(rs.getString(8));        // end
                
                if (te.isInUse()) {
                    collection.add(te);
                } else {
                    outUse.add(te);
                }
            }
            rs.close();
            collection.addAll(outUse);
            
            closeStatement(st);
            closeConnection(con);
            return collection;
            
        } catch (Exception e) {
            processError(e);
            closeStatement(st);
            closeConnection(con);
        }
        return null;
    }
    
    
    /**
     * 用法マスタの検索を行う。
     * @param text 検索キーワード
     * @param startsWith no use
     * @param sortBy ソートカラム
     * @param order 昇順降順
     * @return 用法リスト
     */
    private ArrayList<AdminEntry> getAdminByName(String text, boolean startsWith, String sortBy, String order) {
        // 前方一致検索を行う
        String sql = getAdminByNameSql(text, sortBy, order, true);
        ArrayList<AdminEntry> ret = getAdminCollection(sql);
        
        // NoError で結果がないとき部分一致検索を行う
        if (isNoError() && (ret == null || ret.size() == 0) ) {
            sql = getAdminByNameSql(text, sortBy, order, false);
            ret = getAdminCollection(sql);
        }
        return ret;
    }
    private String getAdminByNameSql(String text, String sortBy, String order, boolean forward) {
        
        String word = null;
        StringBuilder buf = new StringBuilder();
        
        buf.append("select srycd, name from tbl_tensu where ");
        if (Project.getOrcaVersion().startsWith("4")) {
            int hospnum = getHospNum();
            buf.append("hospnum=");
            buf.append(hospnum);
            buf.append(" and ");
        } 
        buf.append("srycd ~ '^001' and ");
        
        if (StringTool.isAllDigit(text)) {
            word = text;
            buf.append("srycd ~ ");
            
        } else {
            word = text;
            buf.append("name ~ ");
        }
        
        if (forward) {
            buf.append(addSingleQuote("^" + word));
        } else {
            buf.append(addSingleQuote(word));
        }
        
        String orderBy = getOrderBy(sortBy, order);
        if (orderBy == null) {
            orderBy = " order by srycd";
        }
        buf.append(orderBy);
        String sql = buf.toString();
        printTrace(sql);
        
        return sql;
    }
    private ArrayList<AdminEntry> getAdminCollection(String sql) {
        
        Connection con = null;
        ArrayList<AdminEntry> collection = null;
        Statement st = null;
        
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            // ValueObject
            AdminEntry te = null;
            collection = new ArrayList<AdminEntry>();
            
            while (rs.next()) {
                te = new AdminEntry();
                te.setCode(rs.getString(1));        // Code
                te.setName(rs.getString(2));        // name                
                collection.add(te);
            }
            rs.close();
            
            closeStatement(st);
            closeConnection(con);
            return collection;
            
        } catch (Exception e) {
            processError(e);
            closeStatement(st);
            closeConnection(con);
        }
        return null;
    }
    
    public ArrayList<AdminEntry> getAdminByCategory(String category) {
               
        Connection con = null;
        ArrayList<AdminEntry> collection = null;
        Statement st = null;
        
        StringBuffer buf = new StringBuffer();
        buf.append("select srycd,name from tbl_tensu where ");
        if (Project.getOrcaVersion().startsWith("4")) {
            int hospnum = getHospNum();
            buf.append("hospnum=");
            buf.append(hospnum);
            buf.append(" and ");
        } 
        buf.append("srycd ~ '^");
        
        int index = category.indexOf(' ');
        if (index > 0) {
            String s1 = category.substring(0, index);
            String s2 = category.substring(index+1);
            buf.append(s1);
            buf.append("' or srycd ~ '^");
            buf.append(s2);
            buf.append("'");
            
        } else {
            buf.append(category);
            buf.append("'");
        }
        buf.append(" order by srycd");
        String sql = buf.toString();
        printTrace(sql);
        
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            // ValueObject
            AdminEntry te = null;
            collection = new ArrayList<AdminEntry>();
            
            while (rs.next()) {
                te = new AdminEntry();
                te.setCode(rs.getString(1));            // srycd
                te.setName(rs.getString(2));            // name
                collection.add(te);
                
            }
            rs.close();
            
            closeStatement(st);
            closeConnection(con);
            return collection;
            
        } catch (Exception e) {
            processError(e);
            closeStatement(st);
            closeConnection(con);
        }
        return null;
        
    }
    
    
    private String getOrderBy(String sortBy, String order) {
        
        StringBuilder buf = null;
        
        if (sortBy != null) {
            buf = new StringBuilder();
            buf.append(" order by ");
            buf.append(sortBy);
        }
        
        if (order != null) {
            buf.append(" ");
            buf.append(order);
        }
        
        return (buf != null) ? buf.toString() : null;
    }
}