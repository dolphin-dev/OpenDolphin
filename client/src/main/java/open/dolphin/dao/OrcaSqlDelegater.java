package open.dolphin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import open.dolphin.client.ClientContext;
import open.dolphin.delegater.OrcaDelegater;
import open.dolphin.infomodel.*;
import open.dolphin.order.MMLTable;
import open.dolphin.project.Project;
import open.dolphin.util.StringTool;

/**
 * OrcaSqlDelegater
 *
 * @author Kazushi Minagawa
 */
public final class OrcaSqlDelegater extends SqlDaoBean implements OrcaDelegater {
    
    private static final String RP_KBN_START        = "2";
    private static final String SHINRYO_KBN_START   = ".";
    private static final int SHINRYO_KBN_LENGTH     = 3;
    private static final int DEFAULT_BUNDLE_NUMBER  = 1;
    private static final String KBN_RP              = "220";
    private static final String KBN_RAD             = "700";
    private static final String KBN_GENERAL         = "999";
    
    private static final String QUERY_FACILITYID_BY_1001
            ="select kanritbl from tbl_syskanri where kanricd='1001'";

    private static final String QUERY_TENSU_BY_SHINKU
            = "select srycd,name,kananame,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn,yukostymd,yukoedymd from tbl_tensu where srysyukbn ~ ? and yukostymd<= ? and yukoedymd>=?";

    private static final String QUERY_TENSU_BY_NAME
            = "select srycd,name,kananame,taniname,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn,ykzkbn,yakkakjncd,yukostymd,yukoedymd from tbl_tensu where (name ~ ? or kananame ~ ?) and yukostymd<= ? and yukoedymd>=?";

    private static final String QUERY_TENSU_BY_1_NAME
            = "select srycd,name,kananame,taniname,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn,ykzkbn,yakkakjncd,yukostymd,yukoedymd from tbl_tensu where (name = ? or kananame = ?) and yukostymd<= ? and yukoedymd>=?";

    private static final String QUERY_TENSU_BY_CODE
            = "select srycd,name,kananame,taniname,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn,ykzkbn,yakkakjncd,yukostymd,yukoedymd from tbl_tensu where srycd ~ ? and yukostymd<= ? and yukoedymd>=?";

    private static final String QUERY_TENSU_BY_TEN
            = "select srycd,name,kananame,taniname,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn,ykzkbn,yakkakjncd,yukostymd,yukoedymd from tbl_tensu where ten >= ? and ten <= ? and yukostymd<= ? and yukoedymd>=?";

    private static final String QUERY_DICEASE_BY_NAME
            = "select byomeicd, byomei, byomeikana, icd10, haisiymd from tbl_byomei where (byomei ~ ? or byomeikana ~?) and haisiymd >= ?";

    private static final String QUERY_DICEASE_BY_NAME_46
            = "select byomeicd, byomei, byomeikana, icd10_1, haisiymd from tbl_byomei where (byomei ~ ? or byomeikana ~?) and haisiymd >= ?";
    
    private static final String QUERY_GENERAL_NAME_BY_CODE
            = "select b.srycd,genericname from tbl_tensu b,tbl_genericname c where b.srycd=? and substring(b.yakkakjncd from 1 for 9)=c.yakkakjncd order by b.yukoedymd desc";

    /**
     * Creates a new instance of OrcaSqlDelegater
     */
    public OrcaSqlDelegater() {
    }
    
    //-------------------------------------------------------------------------
    // 保険医療機関コードとJMARIコード by 1001
    //-------------------------------------------------------------------------
    @Override
    public String getFacilityCodeBy1001() throws Exception {
        // SQL 文
        StringBuilder buf = new StringBuilder();
        buf.append(QUERY_FACILITYID_BY_1001);
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps;
        
        StringBuilder ret = new StringBuilder();

        try
        {
            con = getConnection();
            ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String line = rs.getString(1);
                
                // 保険医療機関コード 10桁
                ret.append(line.substring(0, 10));
                
                // JMARIコード JPN+12桁 (total 15)
                int index = line.indexOf("JPN");
                if (index>0) {
                    ret.append(line.substring(index, index+15));
                }
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;

        } finally {
            closeConnection(con);
        }

        return ret.toString();
    } 
    
    //-------------------------------------------------------------------------
    // 併用禁忌チェック
    // masuda 先生の SqlMiscDao からcheckInteractionポーティング。
    //-------------------------------------------------------------------------
    @Override
    public List<DrugInteractionModel> checkInteraction(Collection<String> drug1, Collection<String> drug2) throws Exception {
        // 引数はdrugcdの配列ｘ２ 

        if (drug1 == null || drug1.isEmpty() || drug2 == null || drug2.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder sb = new StringBuilder();
        List<DrugInteractionModel> ret = new ArrayList<DrugInteractionModel>();

        // SQL文を作成
        sb.append("select drugcd, drugcd2, TI.syojyoucd, syojyou ");
        sb.append("from tbl_interact TI inner join tbl_sskijyo TS on TI.syojyoucd = TS.syojyoucd ");
        sb.append("where (drugcd in (");
        sb.append(getCodes(drug1));
        sb.append(") and drugcd2 in (");
        sb.append(getCodes(drug2));
        sb.append("))");
        String sql = sb.toString();

        Connection con = null;
        Statement st = null;

        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                ret.add(new DrugInteractionModel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
            rs.close();
            closeStatement(st);
            closeConnection(con);
        } catch (Exception e) {
            processError(e);
            closeStatement(st);
            closeConnection(con);
        }

        return ret;
    }

    //-------------------------------------------------------------------------
    // 点数マスター検索
    //-------------------------------------------------------------------------
    @Override
    public List<TensuMaster> getTensuMasterByShinku(String shinku, String now) throws Exception {

        // 結果を格納するリスト
        ArrayList<TensuMaster> ret = new ArrayList<TensuMaster>();

        // SQL 文
        StringBuilder buf = new StringBuilder();
        buf.append(QUERY_TENSU_BY_SHINKU);
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps;

        try
        {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, shinku);
            ps.setString(2, now);
            ps.setString(3, now);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                TensuMaster t = new TensuMaster();
                t.setSrycd(rs.getString(1));
                t.setName(rs.getString(2));
                t.setKananame(rs.getString(3));
                t.setTensikibetu(rs.getString(4));
                t.setTen(rs.getString(5));
                t.setNyugaitekkbn(rs.getString(6));
                t.setRoutekkbn(rs.getString(7));
                t.setSrysyukbn(rs.getString(8));
                t.setHospsrykbn(rs.getString(9));
                t.setYukostymd(rs.getString(10));
                t.setYukoedymd(rs.getString(11));
                ret.add(t);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
            //processError(e);
            //closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;
    }

    @Override
    public List<TensuMaster> getTensuMasterByName(String name, String now, boolean partialMatch) throws Exception {

        // 結果を格納するリスト
        ArrayList<TensuMaster> ret = new ArrayList<TensuMaster>();

        // 半角英数字を全角へ変換する
        name = StringTool.toZenkakuUpperLower(name);

        // SQL 文
        boolean one = name.length()==1 ? true : false;
        StringBuilder buf = new StringBuilder();
        if (one) {
            buf.append(QUERY_TENSU_BY_1_NAME);
        } else {
            buf.append(QUERY_TENSU_BY_NAME);
            if (!partialMatch) {
                name = "^" + name;
            }
        }
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps;

        try
        {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, name);
            ps.setString(3, now);
            ps.setString(4, now);
            //System.err.println(ps);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                TensuMaster t = new TensuMaster();
                t.setSrycd(rs.getString(1));
                t.setName(rs.getString(2));
                t.setKananame(rs.getString(3));
                t.setTaniname(rs.getString(4));
                t.setTensikibetu(rs.getString(5));
                t.setTen(rs.getString(6));
                t.setNyugaitekkbn(rs.getString(7));
                t.setRoutekkbn(rs.getString(8));
                t.setSrysyukbn(rs.getString(9));
                t.setHospsrykbn(rs.getString(10));
                t.setYkzkbn(rs.getString(11));
                t.setYakkakjncd(rs.getString(12));
                t.setYukostymd(rs.getString(13));
                t.setYukoedymd(rs.getString(14));
                ret.add(t);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
            //processError(e);
            //closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;
    }

    @Override
    public List<TensuMaster> getTensuMasterByCode(String regExp, String now) throws Exception {

        // 結果を格納するリスト
        ArrayList<TensuMaster> ret = new ArrayList<TensuMaster>();

        // SQL 文
        StringBuilder buf = new StringBuilder();
        buf.append(QUERY_TENSU_BY_CODE);
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps;

        try
        {
            con = getConnection();
            ps = con.prepareStatement(sql);
            // 増田内科 コール側で ^ をとる
            ps.setString(1, "^"+regExp);
            ps.setString(2, now);
            ps.setString(3, now);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                TensuMaster t = new TensuMaster();
                t.setSrycd(rs.getString(1));
                t.setName(rs.getString(2));
                t.setKananame(rs.getString(3));
                t.setTaniname(rs.getString(4));
                t.setTensikibetu(rs.getString(5));
                t.setTen(rs.getString(6));
                t.setNyugaitekkbn(rs.getString(7));
                t.setRoutekkbn(rs.getString(8));
                t.setSrysyukbn(rs.getString(9));
                t.setHospsrykbn(rs.getString(10));
                t.setYkzkbn(rs.getString(11));
                t.setYakkakjncd(rs.getString(12));
                t.setYukostymd(rs.getString(13));
                t.setYukoedymd(rs.getString(14));
                ret.add(t);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
            //processError(e);
            //closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;
    }


    @Override
    public List<TensuMaster> getTensuMasterByTen(String ten, String now) throws Exception {

        // 結果を格納するリスト
        ArrayList<TensuMaster> ret = new ArrayList<TensuMaster>();

        // SQL 文
        StringBuilder buf = new StringBuilder();
        buf.append(QUERY_TENSU_BY_TEN);
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps;

        try
        {
            con = getConnection();
            ps = con.prepareStatement(sql);
            String[] params = ten.split("-");
            if (params.length > 1) {
                ps.setFloat(1, Float.parseFloat(params[0]));
                ps.setFloat(2, Float.parseFloat(params[1]));
            } else {
                ps.setFloat(1, Float.parseFloat(params[0]));
                ps.setFloat(2, Float.parseFloat(params[0]));
            }
            
            ps.setString(3, now);
            ps.setString(4, now);
            System.err.println(ps);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                TensuMaster t = new TensuMaster();
                t.setSrycd(rs.getString(1));
                t.setName(rs.getString(2));
                t.setKananame(rs.getString(3));
                t.setTaniname(rs.getString(4));
                t.setTensikibetu(rs.getString(5));
                t.setTen(rs.getString(6));
                t.setNyugaitekkbn(rs.getString(7));
                t.setRoutekkbn(rs.getString(8));
                t.setSrysyukbn(rs.getString(9));
                t.setHospsrykbn(rs.getString(10));
                t.setYkzkbn(rs.getString(11));
                t.setYakkakjncd(rs.getString(12));
                t.setYukostymd(rs.getString(13));
                t.setYukoedymd(rs.getString(14));
                ret.add(t);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
            //processError(e);
            //closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;
    }

    @Override
    public List<DiseaseEntry> getDiseaseByName(String name, String now, boolean partialMatch) throws Exception {

        // 結果を格納するリスト
        ArrayList<DiseaseEntry> ret = new ArrayList<DiseaseEntry>();

        // SQL 文
        StringBuilder buf = new StringBuilder();
//minagawa^ 4.7対応         
        //masuda^ Version46 対応
//        if (ORCA_DB_VER46.equals(getOrcaDbVersion())) {
//            buf.append(QUERY_DICEASE_BY_NAME_46);
//        } else {
//            buf.append(QUERY_DICEASE_BY_NAME);
//        }
        //masuda$       
        String curentVersion = getOrcaDbVersion();
        if (curentVersion.compareTo(ORCA_DB_VER46)>=0) {
            buf.append(QUERY_DICEASE_BY_NAME_46);
        } else {
            buf.append(QUERY_DICEASE_BY_NAME);
        }
//minagawa$        
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps;

        if (!partialMatch) {
            name = "^"+name;
        }

        try
        {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, name);
            ps.setString(3, now);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                DiseaseEntry de = new DiseaseEntry();
                de.setCode(rs.getString(1));        // Code
                de.setName(rs.getString(2));        // Name
                de.setKana(rs.getString(3));         // Kana
                de.setIcdTen(rs.getString(4));      // IcdTen
                de.setDisUseDate(rs.getString(5));  // DisUseDate
                ret.add(de);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
            //processError(e);
            //closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;

    }
    
    //--------------------------------------------------------------------------
    // 一般名を検索する
    //--------------------------------------------------------------------------
    @Override
    public String getGeneralName(String param) {
        
        Connection con = null;
        PreparedStatement ps;
        String gname = null;
        //String srycd = param;

        StringBuilder sb = new StringBuilder();
        sb.append(QUERY_GENERAL_NAME_BY_CODE);
        String sql = sb.toString();
        debug(sql);
        
        try {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, param);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                gname = rs.getString(2);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            processError(e);
            closeConnection(con);
        } finally {
            closeConnection(con);
        }
        
        return gname;
    }
    
    //--------------------------------------------------------------------------
    //  ORCA 入力セット検索
    //--------------------------------------------------------------------------
        
    /**
     * ORCA の入力セットコード（約束処方、診療セット）を返す。
     * @return 入力セットコード(OrcaInputCd)の昇順リスト
     */
    @Override
    public ArrayList<OrcaInputCd> getOrcaInputSet() {
         
        debug("getOrcaInputSet()");
        Connection con = null;
        ArrayList<OrcaInputCd> collection;
        Statement st = null;
        String sql;

        StringBuilder sb = new StringBuilder();
        sb.append("select * from tbl_inputcd where ");
        if (true) {
            int hospnum = getHospNum();
            sb.append("hospnum=");
            sb.append(hospnum);
            sb.append(" and ");
        } 
        sb.append("inputcd like 'P%' or inputcd like 'S%' order by inputcd");
        
        sql = sb.toString();
        debug(sql);
        
        boolean v4 = true;  //Project.getOrcaVersion().startsWith("4") ? true : false;
        
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            collection = new ArrayList<OrcaInputCd>();
            
            while (rs.next()) {
                
                debug("got from tbl_inputcd");
                
                OrcaInputCd inputCd = new OrcaInputCd();
                
                if (!v4) {
                    inputCd.setHospId(rs.getString(1));
                    inputCd.setCdsyu(rs.getString(2));
                    inputCd.setInputCd(rs.getString(3));
                    inputCd.setSryKbn(rs.getString(4));
                    inputCd.setSryCd(rs.getString(5));
                    inputCd.setDspSeq(rs.getInt(6));
                    inputCd.setDspName(rs.getString(7));
                    inputCd.setTermId(rs.getString(8));
                    inputCd.setOpId(rs.getString(9));
                    inputCd.setCreYmd(rs.getString(10));
                    inputCd.setUpYmd(rs.getString(11));
                    inputCd.setUpHms(rs.getString(12));
                    
                    String cd = inputCd.getInputCd();
                    if (cd.length() > 6) {
                        cd = cd.substring(0, 6);
                        inputCd.setInputCd(cd);
                    }
                    
                } else {
                    inputCd.setCdsyu(rs.getString(1));
                    inputCd.setInputCd(rs.getString(2));
                    inputCd.setSryKbn(rs.getString(3));
                    inputCd.setSryCd(rs.getString(4));
                    inputCd.setDspSeq(rs.getInt(5));
                    inputCd.setDspName(rs.getString(6));
                    inputCd.setTermId(rs.getString(7));
                    inputCd.setOpId(rs.getString(8));
                    inputCd.setCreYmd(rs.getString(9));
                    inputCd.setUpYmd(rs.getString(10));
                    inputCd.setUpHms(rs.getString(11));
                    
                    String cd = inputCd.getInputCd();
                    if (cd.length() > 6) {
                        cd = cd.substring(0, 6);
                        inputCd.setInputCd(cd);
                    }
                    
                    debug("getCdsyu = " + inputCd.getCdsyu());
                    debug("getInputCd = " + inputCd.getInputCd());
                    debug("getSryKbn = " + inputCd.getSryKbn());
                    debug("getSryCd = " + inputCd.getSryCd());
                    debug("getDspSeq = " + String.valueOf(inputCd.getDspSeq()));
                    debug("getDspName = " + inputCd.getDspName());
                    debug("getTermId = " + inputCd.getTermId());
                    debug("getOpId " + inputCd.getOpId());
                    debug("getCreYmd " + inputCd.getCreYmd());
                    debug("getUpYmd " + inputCd.getUpYmd());
                    debug("getUpHms " + inputCd.getUpHms());
                    
                    ModuleInfoBean info = inputCd.getStampInfo();
                    debug("getStampName = " + info.getStampName());
                    debug("getStampRole = " + info.getStampRole());
                    debug("getEntity = " + info.getEntity());
                    debug("getStampId = " + info.getStampId());
                }
                
                collection.add(inputCd);
            }
            
            rs.close();
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
     * 指定された入力セットコードから診療セットを Stamp にして返す。
     * @param inputSetInfo 入力セットの StampInfo
     * @return 入力セットのStampリスト
     */    
    @Override
    public ArrayList<ModuleModel> getStamp(ModuleInfoBean inputSetInfo) {
        
        String setCd = inputSetInfo.getStampId(); // stampId=setCd; セットコード
        String stampName = inputSetInfo.getStampName();
        debug("getStamp()");
        debug("setCd = " + setCd);
        debug("stampName = " + stampName);
        
        
        int hospnum = -1;
        if (true) {
            hospnum = getHospNum();
        }
        
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        String sql1;
        String sql2;
        
        StringBuilder sb1 = new StringBuilder();
        if (true) {
            sb1.append("select inputcd,suryo1,kaisu from tbl_inputset where hospnum=? and setcd=? order by setseq");
            sql1 = sb1.toString();
        } else {
            sb1.append("select inputcd,suryo1,kaisu from tbl_inputset where setcd=? order by setseq");
            sql1 = sb1.toString();
        }
        
        // order by yukoedymd desc を追加 ^
        StringBuilder sb2 = new StringBuilder();
        if (true) {
            sb2.append("select srysyukbn,name,taniname,ykzkbn from tbl_tensu where hospnum=? and srycd=? order by yukoedymd desc");
            sql2 = sb2.toString();
        } else {
            sb2.append("select srysyukbn,name,taniname,ykzkbn from tbl_tensu where srycd=?  order by yukoedymd desc");
            sql2 = sb2.toString();
        }
        
        ArrayList<ModuleModel> retSet = new ArrayList<ModuleModel>();
        
        try {
            //
            // setCd を検索する
            //
            con = getConnection();
            ps1 = con.prepareStatement(sql1);
            if (hospnum > 0) {
                ps1.setInt(1, hospnum);
                ps1.setString(2, setCd);
            } else {
                ps1.setString(1, setCd);
            }
            debug(ps1.toString());
            
            ResultSet rs = ps1.executeQuery();
            
            ArrayList<OrcaInputSet> list = new ArrayList<OrcaInputSet>();

            while (rs.next()) {
               
                debug("got from tbl_inputset");
                OrcaInputSet inputSet = new OrcaInputSet();
                //inputSet.setHospId(rs.getString(1));
                //inputSet.setSetCd(rs.getString(2));         // P01001 ...
                //inputSet.setYukostYmd(rs.getString(3));
                //inputSet.setYukoedYmd(rs.getString(4));
                //inputSet.setSetSeq(rs.getInt(5));           // 1, 2, ...
                inputSet.setInputCd(rs.getString(1));       // .210 616130532 ...
                inputSet.setSuryo1(rs.getFloat(2));         // item の個数
                //inputSet.setSuryo2(rs.getFloat(8));
                inputSet.setKaisu(rs.getInt(3));            // バンドル数
                //inputSet.setComment(rs.getString(10));
                //inputSet.setAtai1(rs.getString(11));
                //inputSet.setAtai2(rs.getString(12));
                //inputSet.setAtai3(rs.getString(13));
                //inputSet.setAtai4(rs.getString(14));
                //inputSet.setTermId(rs.getString(15));
                //inputSet.setOpId(rs.getString(16));
                //inputSet.setCreYmd(rs.getString(17));
                //inputSet.setUpYmd(rs.getString(18));
                //inputSet.setUpHms(rs.getString(19));
                
                debug("getInputCd = " + inputSet.getInputCd());
                debug("getSuryo1 = " + String.valueOf(inputSet.getSuryo1()));
                debug("getKaisu = " + String.valueOf(inputSet.getKaisu()));
                
                list.add(inputSet);
            }
            
            rs.close();
            closeStatement(ps1);
            
            ModuleModel stamp;
            BundleDolphin bundle = null;
            ps2 = con.prepareStatement(sql2);
            
            if (list != null && list.size() > 0) {
                
                for (OrcaInputSet inputSet : list) {
                    
                    String inputcd = inputSet.getInputCd();
                    debug("inputcd = " + inputcd);
                    
                    if (inputcd.startsWith(SHINRYO_KBN_START)) {
                        
                        //---------------------------------------
                        //
                        //---------------------------------------
                        stamp = createStamp(stampName, inputcd);
                        if (stamp != null) {
                            bundle = (BundleDolphin) stamp.getModel();
                            retSet.add(stamp);
                        }
                        debug("created stamp " + inputcd);
                        
                    } else {
                        
                        if (hospnum > 0) {
                            ps2.setInt(1, hospnum);
                            ps2.setString(2, inputcd);
                        } else {
                            ps2.setString(1, inputcd);
                        }
                        debug(ps2.toString());
                    
                        ResultSet rs2 = ps2.executeQuery();
                        
                        if (rs2.next()) {
                            
                            debug("got from tbl_tensu");
                            String code = inputcd;
                            String kbn = rs2.getString(1);
                            String name = rs2.getString(2);
                            String number = String.valueOf(inputSet.getSuryo1());
                            String unit = rs2.getString(3);
                            
                            debug("code = " + code);
                            debug("kbn = " + kbn);
                            debug("name = " + name);
                            debug("number = " + number);
                            debug("unit = " + unit);
                            
                            ClaimItem item = new ClaimItem();
                            item.setCode(code);
                            item.setName(name);
                            item.setNumber(number);
                            item.setClassCodeSystem(ClaimConst.SUBCLASS_CODE_ID);
                            
                            if (code.startsWith(ClaimConst.SYUGI_CODE_START)) {
                                //
                                // 手技の場合
                                //
                                debug("item is tech");
                                item.setClassCode(String.valueOf(ClaimConst.SYUGI));
                                
                                if (bundle == null) {
                                    stamp = createStamp(stampName, kbn);
                                    if (stamp != null) {
                                        bundle = (BundleDolphin) stamp.getModel();
                                        retSet.add(stamp);
                                    }
                                }
                                
                                if (bundle != null) {
                                    bundle.addClaimItem(item);
                                } 
                            
                            } else if (code.startsWith(ClaimConst.YAKUZAI_CODE_START)) {
                                //
                                // 薬剤の場合
                                //
                                debug("item is medicine");
                                item.setClassCode(String.valueOf(ClaimConst.YAKUZAI));
                                item.setNumberCode(ClaimConst.YAKUZAI_TOYORYO);
                                item.setNumberCodeSystem(ClaimConst.NUMBER_CODE_ID);
                                item.setUnit(unit);
                                
                                if (bundle == null) {
                                    String receiptCode = rs2.getString(4).equals(ClaimConst.YKZ_KBN_NAIYO)
                                            ? ClaimConst.RECEIPT_CODE_NAIYO 
                                            : ClaimConst.RECEIPT_CODE_GAIYO;
                                    stamp = createStamp(stampName, receiptCode);
                                    if (stamp != null) {
                                        bundle = (BundleDolphin) stamp.getModel();
                                        retSet.add(stamp);
                                    }
                                }
                                
                                if (bundle != null) {
                                    bundle.addClaimItem(item);
                                }
                                
                            } else if (code.startsWith(ClaimConst.ZAIRYO_CODE_START)) {
                                //
                                // 材料の場合
                                //
                                debug("item is material");
                                item.setClassCode(String.valueOf(ClaimConst.ZAIRYO));
                                item.setNumberCode(ClaimConst.ZAIRYO_KOSU);
                                item.setNumberCodeSystem(ClaimConst.NUMBER_CODE_ID);
                                item.setUnit(unit);
                                
                                if (bundle == null) {
                                    stamp = createStamp(stampName, KBN_GENERAL);
                                    if (stamp != null) {
                                        bundle = (BundleDolphin) stamp.getModel();
                                        retSet.add(stamp);
                                    }
                                }
                                
                                if (bundle != null) {
                                    bundle.addClaimItem(item);
                                }
                                
                            
                            } else if (code.startsWith(ClaimConst.ADMIN_CODE_START)) {
                                //
                                // 用法の場合
                                //
                                debug("item is administration");
                                if (bundle == null) {
                                    stamp = createStamp(stampName, KBN_RP);
                                    if (stamp != null) {
                                        bundle = (BundleDolphin) stamp.getModel();
                                        retSet.add(stamp);
                                    }
                                }
                                
                                if (bundle != null) {
                                    if (bundle instanceof BundleMed) {
                                        debug("cur bundle is BundleMed");
                                        bundle.setAdmin(name);
                                        bundle.setAdminCode(code);
                                        bundle.setBundleNumber(String.valueOf(inputSet.getKaisu()));
                                    } else {
                                        debug("cur bundle is ! BundleMed");
                                        bundle.addClaimItem(item);
                                    }
                                }
                            
                            } else if (inputcd.startsWith(ClaimConst.RBUI_CODE_START)) {
                                //
                                // 放射線部位の場合
                                //
                                debug("item is rad loc.");
                                item.setClassCode(String.valueOf(ClaimConst.SYUGI));
                                
                                if (bundle == null) {
                                    stamp = createStamp(stampName, KBN_RAD);
                                    if (stamp != null) {
                                        bundle = (BundleDolphin) stamp.getModel();
                                        retSet.add(stamp);
                                    }
                                }
                                
                                if (bundle != null) {
                                    bundle.addClaimItem(item);
                                }

                            } else {

                                debug("item is other");
                                if (bundle==null) {
                                    stamp = createStamp(stampName, KBN_GENERAL);
                                    if (stamp != null) {
                                        bundle = (BundleDolphin) stamp.getModel();
                                        retSet.add(stamp);
                                    }
                                }
                                if (bundle != null) {
                                    bundle.addClaimItem(item);
                                }
                            }
                        }
                    }
                }
                
                closeStatement(ps2);
            }
            
            closeConnection(con);
            
        } catch (Exception e) {
            processError(e);
            closeConnection(con);
            closeStatement(ps1);
            closeStatement(ps2);
        }
        
        return retSet; 
    }
    
    /**
     * Stampを生成する。
     * @param stampName Stamp名
     * @param code 診療区分コード
     * @return Stamp
     */
    private ModuleModel createStamp(String stampName, String code) {
        
        ModuleModel stamp = null;
        
        if (code != null) {
            
            if (code.startsWith(SHINRYO_KBN_START)) {
                code = code.substring(1);
            }
            
            if (code.length() > SHINRYO_KBN_LENGTH) {
                code = code.substring(0, SHINRYO_KBN_LENGTH);
            }
            
            stamp = new ModuleModel();
            ModuleInfoBean stampInfo = stamp.getModuleInfoBean();
            stampInfo.setStampName(stampName);
            stampInfo.setStampRole(IInfoModel.ROLE_P);  // ROLE_ORCA -> EOLE_P
            //stampInfo.setStampMemo(code);
            BundleDolphin bundle;
                
            if (code.startsWith(RP_KBN_START)) {
                
                bundle = new BundleMed();
                stamp.setModel(bundle);
                
                String inOut = Project.getBoolean(Project.RP_OUT, true)
                               ? ClaimConst.EXT_MEDICINE
                               : ClaimConst.IN_MEDICINE;
                bundle.setMemo(inOut);
                
            } else {
                
                bundle = new BundleDolphin();
                stamp.setModel(bundle);
            }
            
            bundle.setClassCode(code);
            bundle.setClassCodeSystem(ClaimConst.CLASS_CODE_ID);
            bundle.setClassName(MMLTable.getClaimClassCodeName(code));
            bundle.setBundleNumber(String.valueOf(DEFAULT_BUNDLE_NUMBER));

            String[] entityOrder = getEntityOrderName(code);
            if (entityOrder != null) {
                stampInfo.setEntity(entityOrder[0]);
                bundle.setOrderName(entityOrder[1]);
            }
        } 
        
        return stamp;
    }
    
    private String[] getEntityOrderName(String receiptCode) {
        
        try {
            int number = Integer.parseInt(receiptCode);
            
            if (number >= 110 && number <= 125) {
                return new String[]{IInfoModel.ENTITY_BASE_CHARGE_ORDER, "診断料"};
            
            } else if (number >= 130 && number <= 150) {
                return new String[]{IInfoModel.ENTITY_INSTRACTION_CHARGE_ORDER, "指導・在宅"};
                
            } else if (number >= 200 && number <= 299) {
                return new String[]{IInfoModel.ENTITY_MED_ORDER, "RP"};
            
            } else if (number >= 300 && number <= 352) {
                return new String[]{IInfoModel.ENTITY_INJECTION_ORDER, "注 射"};
            
            } else if (number >= 400 && number <= 499) {
                return new String[]{IInfoModel.ENTITY_TREATMENT, "処 置"};
            
            } else if (number >= 500 && number <= 599) {
                return new String[]{IInfoModel.ENTITY_SURGERY_ORDER, "手術"};
            
            } else if (number >= 600 && number <= 699) {
                return new String[]{IInfoModel.ENTITY_LABO_TEST, "検査"};
            
            } else if (number >= 700 && number <= 799) {
                return new String[]{IInfoModel.ENTITY_RADIOLOGY_ORDER, "放射線"};
            
            } else if (number >= 800 && number <= 899) {
                return new String[]{IInfoModel.ENTITY_OTHER_ORDER, "その他"};
                
            } else {
                return new String[]{IInfoModel.ENTITY_GENERAL_ORDER, "汎 用"};
            }
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
        return null;
    }

    @Override
    protected void debug(String msg) {
        //System.out.println(msg);
    }
    
    
    //--------------------------------------------------------------------------
    //  病名インポート
    //--------------------------------------------------------------------------
        
    /**
     * ORCA に登録してある病名を検索する。
     * @return RegisteredDiagnosisModelのリスト
     */
    @Override
    public ArrayList<RegisteredDiagnosisModel> getOrcaDisease(String patientId, String from, String to, Boolean ascend) {
        
        Connection con = null;
        ArrayList<RegisteredDiagnosisModel> collection;
        PreparedStatement pt = null;
        String sql;
        String ptid = null;
        int hospNum = getHospNum(); //-1;
        
        StringBuilder sb = new StringBuilder();
        sb.append("select ptid, ptnum from tbl_ptnum where hospnum=? and ptnum=?");
        sql = sb.toString();
        ClientContext.getBootLogger().debug(sql);
        
        try {
            con = getConnection();
            pt = con.prepareStatement(sql);
            pt.setInt(1, hospNum);
            pt.setString(2, patientId);
            
            ResultSet rs = pt.executeQuery();
            if (rs.next()) {
                ptid = rs.getString(1);
            }
            closeConnection(con);
            closeStatement(pt);
            
        }  catch (Exception e) {
            ClientContext.getBootLogger().warn(e.getMessage());
            processError(e);
            closeConnection(con);
            closeStatement(pt);
        }
        
        if (ptid == null) {
            ClientContext.getBootLogger().warn("ptid=null");
            return null;
        }
        
        sb = new StringBuilder();
        sb.append("select sryymd,khnbyomeicd,utagaiflg,syubyoflg,tenkikbn,tenkiymd,byomei from tbl_ptbyomei where ");
        if (ascend.booleanValue()) {
            if (hospNum > 0) {
                sb.append("hospnum=? and ptid=? and sryymd >= ? and sryymd <= ? and dltflg!=? order by sryymd");
            } else {
                sb.append("ptid=? and sryymd >= ? and sryymd <= ? and dltflg!=?  order by sryymd");
            }
        } else {
            if (hospNum > 0) {
                sb.append("hospnum=? and ptid=? and sryymd >= ? and sryymd <= ? and dltflg!=?  order by sryymd desc");
            } else {
                sb.append("ptid=? and sryymd >= ? and sryymd <= ? and dltflg!=?  order by sryymd desc");
            }
        }

        sql = sb.toString();
        ClientContext.getBootLogger().debug(sql);
        
        try {
            con = getConnection();
            pt = con.prepareStatement(sql);
            if (hospNum > 0) {
                pt.setInt(1, hospNum);
                pt.setInt(2, Integer.parseInt(ptid));   // 元町皮膚科
                pt.setString(3, from);
                pt.setString(4, to);
                pt.setString(5, "1");
            } else {
                pt.setInt(1, Integer.parseInt(ptid));   // 元町皮膚科
                pt.setString(2, from);
                pt.setString(3, to);
                pt.setString(4, "1");
            }
            ResultSet rs = pt.executeQuery();
            collection = new ArrayList<RegisteredDiagnosisModel>();
            
            while (rs.next()) {
                
                RegisteredDiagnosisModel ord = new RegisteredDiagnosisModel();
                
                // 疾患開始日
                ord.setStartDate(toDolphinDateStr(rs.getString(1)));
                
                // 病名コード
                ord.setDiagnosisCode(rs.getString(2));

                // 疑いフラグ
                storeSuspectedDiagnosis(ord, rs.getString(3));

                // 主病名フラグ
                storeMainDiagnosis(ord, rs.getString(4));

                // 転帰
                storeOutcome(ord, rs.getString(5));
                
                // 疾患終了日（転帰）
                ord.setEndDate(toDolphinDateStr(rs.getString(6)));
                
                // 疾患名
                ord.setDiagnosis(rs.getString(7));
                
                // 制御のための Status
                ord.setStatus("ORCA");
                
                collection.add(ord);
            }
            
            rs.close();
            closeStatement(pt);
            closeConnection(con);
            
            return collection;
            
        } catch (Exception e) {
            ClientContext.getBootLogger().warn(e.getMessage());
            processError(e);
            closeConnection(con);
            closeStatement(pt);
        }
        
        return null;
    }


    /**
     * ORCA に登録してある直近の病名を検索する。
     * @return RegisteredDiagnosisModelのリスト
     */
    @Override
    public ArrayList<RegisteredDiagnosisModel> getActiveOrcaDisease(String patientId, boolean asc) {

        Connection con = null;
        ArrayList<RegisteredDiagnosisModel> collection;
        PreparedStatement pt = null;
        String sql;
        String ptid = null;
        int hospNum = getHospNum(); //-1;

        StringBuilder sb = new StringBuilder();
        sb.append("select ptid, ptnum from tbl_ptnum where hospnum=? and ptnum=?");
        sql = sb.toString();
        ClientContext.getBootLogger().debug(sql);

        try {
            con = getConnection();
            pt = con.prepareStatement(sql);
            pt.setInt(1, hospNum);
            pt.setString(2, patientId);

            ResultSet rs = pt.executeQuery();
            if (rs.next()) {
                ptid = rs.getString(1);
            }
            closeConnection(con);
            closeStatement(pt);

        }  catch (Exception e) {
            ClientContext.getBootLogger().warn(e.getMessage());
            processError(e);
            closeConnection(con);
            closeStatement(pt);
        }

        if (ptid == null) {
            ClientContext.getBootLogger().warn("ptid=null");
            return null;
        }

        sb = new StringBuilder();
        sb.append("select sryymd,khnbyomeicd,utagaiflg,syubyoflg,tenkikbn,tenkiymd,byomei from tbl_ptbyomei where ");
        if (hospNum > 0) {
            sb.append("hospnum=? and ptid=? and tenkikbn=? and dltflg!=? order by sryymd");
        } else {
            sb.append("ptid=? and tenkikbn=? and dltflg!=? order by sryymd");
        }
        if (!asc) {
            sb.append(" desc");
        }

        sql = sb.toString();
        ClientContext.getBootLogger().debug(sql);

        try {
            con = getConnection();
            pt = con.prepareStatement(sql);
            if (hospNum > 0) {
                pt.setInt(1, hospNum);
                pt.setInt(2, Integer.parseInt(ptid));   // 元町皮膚科
                pt.setString(3, " ");
                pt.setString(4, "1");
            } else {
                pt.setInt(1, Integer.parseInt(ptid));   // 元町皮膚科
                pt.setString(2, " ");
                pt.setString(3, "1");
            }
            ResultSet rs = pt.executeQuery();
            collection = new ArrayList<RegisteredDiagnosisModel>();

            while (rs.next()) {

                RegisteredDiagnosisModel ord = new RegisteredDiagnosisModel();

                // 疾患開始日
                ord.setStartDate(toDolphinDateStr(rs.getString(1)));

                // 病名コード
                ord.setDiagnosisCode(rs.getString(2));

                // 疑いフラグ
                storeSuspectedDiagnosis(ord, rs.getString(3));

                // 主病名フラグ
                storeMainDiagnosis(ord, rs.getString(4));

                // 転帰
                storeOutcome(ord, rs.getString(5));

                // 疾患終了日（転帰）
                ord.setEndDate(toDolphinDateStr(rs.getString(6)));

                // 疾患名
                ord.setDiagnosis(rs.getString(7));

                // 制御のための Status
                ord.setStatus("ORCA");

                collection.add(ord);
            }

            rs.close();
            closeStatement(pt);
            closeConnection(con);

            return collection;

        } catch (Exception e) {
            ClientContext.getBootLogger().warn(e.getMessage());
            processError(e);
            closeConnection(con);
            closeStatement(pt);
        }

        return null;
    }
    
    // ORCA カテゴリ
    private void storeSuspectedDiagnosis(RegisteredDiagnosisModel rdm, String test) {
        if (test!=null) {
            if (test.equals("1")) {
                rdm.setCategory("suspectedDiagnosis");
                rdm.setCategoryDesc("疑い病名");
                rdm.setCategoryCodeSys("MML0015");

            } else if (test.equals("2")) {
//                rdm.setCategory("suspectedDiagnosis");
//                rdm.setCategoryDesc("急性");
//                rdm.setCategoryCodeSys("MML0012");

            } else if (test.equals("3")) {
                rdm.setCategory("suspectedDiagnosis");
                rdm.setCategoryDesc("疑い病名");
                rdm.setCategoryCodeSys("MML0015");
            }
        }
    }
    
    private void storeMainDiagnosis(RegisteredDiagnosisModel rdm, String test) {
        if (test!=null && test.equals("1")) {
            rdm.setCategory("mainDiagnosis");
            rdm.setCategoryDesc("主病名");
            rdm.setCategoryCodeSys("MML0012");
        }
    }

    // ORCA 転帰
    private void storeOutcome(RegisteredDiagnosisModel rdm, String data) {
        if (data != null) {
            if (data.equals("1")) {
                rdm.setOutcome("fullyRecovered");
                rdm.setOutcomeDesc("全治");
                rdm.setOutcomeCodeSys("MML0016");

            } else if (data.equals("2")) {
                rdm.setOutcome("died");
                rdm.setOutcomeDesc("死亡");
                rdm.setOutcomeCodeSys("MML0016");

            } else if (data.equals("3")) {
                rdm.setOutcome("pause");
                rdm.setOutcomeDesc("中止");
                rdm.setOutcomeCodeSys("MML0016");

            } else if (data.equals("8")) {
                rdm.setOutcome("transfer");
                rdm.setOutcomeDesc("転医");
                rdm.setOutcomeCodeSys("MML0016");
            }
        }
    }

    private String toDolphinDateStr(String orcaDate) {
        if (orcaDate==null || orcaDate.equals("")) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("yyyyMMdd");
            Date orca = sdf.parse(orcaDate);
            sdf.applyPattern("yyyy-MM-dd");
            String ret = sdf.format(orca);
            return ret;
        } catch (ParseException ex) {
            //ex.printStackTrace(System.err);
        }

        return null;
    }
}