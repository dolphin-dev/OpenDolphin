package open.orca.rest;

import java.beans.XMLEncoder;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.*;
import open.dolphin.infomodel.*;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
@Singleton
@Path("/orca")
public class OrcaResource {
    
    private static final String RP_KBN_START = "2";
    private static final String SHINRYO_KBN_START = ".";
    private static final int SHINRYO_KBN_LENGTH = 3;
    private static final int DEFAULT_BUNDLE_NUMBER = 1;
    private static final String KBN_RP = "220";
    private static final String KBN_RAD = "700";
    private static final String KBN_GENERAL = "999";
    
    //masuda^   ORCA 4.6対応など
    private static final String ORCA_DB_VER45 = "040500-1";
    private static final String ORCA_DB_VER46 = "040600-1";
    private static final String ORCA_DB_VER47 = "040700-1";
    
    private static int HOSP_NUM;
    private static String DB_VERSION;
    
    private static boolean RP_OUT = true;
    
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
    
    private static final String QUERY_TENSU_BY_TEN2
            = "select srycd,name,kananame,taniname,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn,ykzkbn,yakkakjncd,yukostymd,yukoedymd from tbl_tensu where ten = ? and yukostymd<= ? and yukoedymd>=?";

    private static final String QUERY_GENERAL_NAME_BY_CODE
            = "select b.srycd,genericname from tbl_tensu b,tbl_genericname c where b.srycd=? and substring(b.yakkakjncd from 1 for 9)=c.yakkakjncd order by b.yukoedymd desc";
    
//    private static final String QUERY_DICEASE_BY_NAME
//            = "select byomeicd, byomei, byomeikana, icd10, haisiymd from tbl_byomei where (byomei ~ ? or byomeikana ~?) and haisiymd >= ?";

    private static final String QUERY_DICEASE_BY_NAME_46
            = "select byomeicd, byomei, byomeikana, icd10_1, haisiymd from tbl_byomei where (byomei ~ ? or byomeikana ~?) and haisiymd >= ?";
    
    private static final String CAMMA = ",";
    
    @Resource(mappedName="java:jboss/datasources/OrcaDS")
    private DataSource ds;
    
    private boolean DEBUG;
    
    //masuda^
    //ORCAのデータベースバージョンとhospNumを取得する
    @PostConstruct
    public void setupParams() {
        
        DEBUG = Logger.getLogger("open.dolphin").getLevel().equals(java.util.logging.Level.FINE);
        log("OrcaResource: setupParams");
        
        Connection con1 = null;
        java.sql.Statement st1 = null;
        Connection con2 = null;
        java.sql.Statement st2 = null;
        HOSP_NUM = 1;
        
        try {
            // custom.properties から JMARI_CODEを読む
            Properties config = new Properties();

            // コンフィグファイルを読み込む
            StringBuilder sb = new StringBuilder();
            sb.append(System.getProperty("jboss.home.dir"));
            sb.append(File.separator);
            sb.append("custom.properties");
            File f = new File(sb.toString());
            FileInputStream fin = new FileInputStream(f);
            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
            config.load(r);
            r.close();
            
//minagawa^ Client-ORCA接続の場合
            String conn = config.getProperty("claim.conn");
            if (conn==null || conn.equals("client")) {
                return;
            }
//minagawa$            
            // JMARI code
            String jmari = config.getProperty("jamri.code");
            
            // デフォルトの院内院外処方
            String test = config.getProperty("rp.default.inout");
            RP_OUT = (test!=null && test.equals("out"));

            // 病院番号検索　JMARI<->HospNum
            sb = new StringBuilder();
            sb.append("select hospnum, kanritbl from tbl_syskanri where kanricd='1001' and kanritbl like '%");
            sb.append(jmari);
            sb.append("%'");
            String sql = sb.toString();

            con1 = getConnection();
            st1 = con1.createStatement();
            ResultSet rs = st1.executeQuery(sql);
            if (rs.next()) {
                HOSP_NUM = rs.getInt(1);
            }

            // Version 検索
            sql = "select version from tbl_dbkanri where kanricd='ORCADB00'";

            con2 = getConnection();
            st2 = con2.createStatement();
            ResultSet rs2 = st2.executeQuery(sql);
//minagawa^ BUG            
            if (rs2.next()) {
                DB_VERSION = rs2.getString(1);
            }
//minagawa$  
            log("ORCA 病院番号="+HOSP_NUM);
            log("ORCA Version="+DB_VERSION);
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            
        } finally {
            closeConnection(con1);
            closeStatement(st1);
            closeConnection(con2);
            closeStatement(st2);
        }
    }
    //masuda$
    
    @GET
    @Path("/facilitycode")
    @Produces(MediaType.TEXT_PLAIN)
    public String getFacilityCodeBy1001() {
       
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
            processError(e);

        } finally {
            closeConnection(con);
        }

        return ret.toString();        
    }
    
    @GET
    @Path("/tensu/shinku/{param}/")
    @Produces(MediaType.APPLICATION_JSON)
    public TensuListConverter getTensutensuByShinku(@PathParam("param") String param) {

        // パラメーターを取得する
        String[] params = param.split(CAMMA);
        String shinku = params[0];
        String now = params[1];
        
        if (!shinku.startsWith("^")) {
            shinku = "^" + shinku;
        }
        //System.err.println(shinku);
        
        // 結果を格納するリスト
        ArrayList<TensuMaster> list = new ArrayList<TensuMaster>();

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
                list.add(t);
            }

            rs.close();
            ps.close();
            
            // Wrapper
            TensuList wrapper = new TensuList();
            wrapper.setList(list);
            
            // Converter
            TensuListConverter conv = new TensuListConverter();
            conv.setModel(wrapper);
            
            // JSON
            return conv;
            
        } catch (Exception e) {
            processError(e);

        } finally {
            closeConnection(con);
        }

        return null;
    }

    @GET
    @Path("/tensu/name/{param}/")
    @Produces(MediaType.APPLICATION_JSON)
    public TensuListConverter getTensuMasterByName(@PathParam("param") String param) {
        
        // パラメーターを取得する
        String[] params = param.split(CAMMA);
        String name = params[0];
        String now = params[1];
        boolean partialMatch = Boolean.parseBoolean(params[2]);

        // 結果を格納するリスト
        ArrayList<TensuMaster> list = new ArrayList<TensuMaster>();

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
                list.add(t);
            }

            rs.close();
            ps.close();
            
            // Wrapper
            TensuList wrapper = new TensuList();
            wrapper.setList(list);
            
            // Converter
            TensuListConverter conv = new TensuListConverter();
            conv.setModel(wrapper);
            
            // JSON
            return conv;

        } catch (Exception e) {
            processError(e);

        } finally {
            closeConnection(con);
        }

        return null;
    }

    @GET
    @Path("/tensu/code/{param}/")
    @Produces(MediaType.APPLICATION_JSON)
    public TensuListConverter getTensuMasterByCode(@PathParam("param") String param) {
        
        // パラメーターを取得する
        String[] params = param.split(CAMMA);
        String regExp = params[0];
        String now = params[1];

        // 結果を格納するリスト
        ArrayList<TensuMaster> list = new ArrayList<TensuMaster>();

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
                list.add(t);
            }

            rs.close();
            ps.close();
            
            // Wrapper
            TensuList wrapper = new TensuList();
            wrapper.setList(list);
            
            // Converter
            TensuListConverter conv = new TensuListConverter();
            conv.setModel(wrapper);
            
            // JSON
            return conv;

        } catch (Exception e) {
            processError(e);

        } finally {
            closeConnection(con);
        }

        return null;
    }

    @GET
    @Path("/tensu/ten/{param}/")
    @Produces(MediaType.APPLICATION_JSON)
    public TensuListConverter getTensuMasterByTen(@PathParam("param") String param) {
        
        // パラメーターを取得する
        String[] params = param.split(CAMMA);
        String ten = params[0];
        String now = params[1];

        // 結果を格納するリスト
        ArrayList<TensuMaster> list = new ArrayList<TensuMaster>();

        // SQL 文
        int type;
        StringBuilder buf = new StringBuilder();
        if (ten.indexOf("-") > 0) {
            buf.append(QUERY_TENSU_BY_TEN);
            type = 1;
        } else {
            buf.append(QUERY_TENSU_BY_TEN2);
            type = 2;
        }
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps;

        try
        {
            con = getConnection();
            ps = con.prepareStatement(sql);
            if (type==1) {
                String[] ten_params = ten.split("-");
                ps.setFloat(1, Float.parseFloat(ten_params[0]));
                ps.setFloat(2, Float.parseFloat(ten_params[1]));
                ps.setString(3, now);
                ps.setString(4, now);
            } else {
                ps.setFloat(1, Float.parseFloat(ten));
                ps.setString(2, now);
                ps.setString(3, now);
            }
            
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
                list.add(t);
            }

            rs.close();
            ps.close();
            
            // Wrapper
            TensuList wrapper = new TensuList();
            wrapper.setList(list);
            
            // Converter
            TensuListConverter conv = new TensuListConverter();
            conv.setModel(wrapper);
            
            // JSON
            return conv;

        } catch (Exception e) {
            processError(e);

        } finally {
            closeConnection(con);
        }

        return null;
    }

    @GET
    @Path("/disease/name/{param}/")
    @Produces(MediaType.APPLICATION_JSON)
    public DiseaseListConverter getDiseaseByName(@PathParam("param") String param) {
        
        // パラメーターを取得する
        String[] params = param.split(CAMMA);
        String name = params[0];
        String now = params[1];
        boolean partialMatch = Boolean.parseBoolean(params[2]);

        // 結果を格納するリスト
        ArrayList<DiseaseEntry> list = new ArrayList<DiseaseEntry>();
        
        // 戻り値
        String retXml = null;

        // SQL 文
        StringBuilder buf = new StringBuilder();
        
//        //masuda^ Version46 対応
//        if (ORCA_DB_VER46.equals(getOrcaDbVersion())) {
//            buf.append(QUERY_DICEASE_BY_NAME_46);
//        } else {
//            buf.append(QUERY_DICEASE_BY_NAME);
//        }
//        //masuda$
        buf.append(QUERY_DICEASE_BY_NAME_46);
        
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
                list.add(de);
            }

            rs.close();
            ps.close();
            
            // Wrapper
            DiseaseList wrapper = new DiseaseList();
            wrapper.setList(list);
            
            // Converter
            DiseaseListConverter conv = new DiseaseListConverter();
            conv.setModel(wrapper);
            
            // JSON
            return conv;

        } catch (Exception e) {
            processError(e);

        } finally {
            closeConnection(con);
        }

        return null;
    }
    
// masuda^  ORCAのptidを取得する
    private long getOrcaPtID(String patientId){

        long ptid = 0;

        final String sql = "select ptid from tbl_ptnum where hospnum = ? and ptnum = ?";
        Connection con = null;
        PreparedStatement ps;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, HOSP_NUM);
            ps.setString(2, patientId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ptid = rs.getLong(1);
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

        return ptid;
    }
    
    @PUT
    @Path("/interaction")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DragInteractionListConverter checkInteraction(String json) throws Exception {
        
        ObjectMapper mapper = new ObjectMapper();
        InteractionCodeList input = mapper.readValue(json, InteractionCodeList.class);
        
        // 相互作用モデルのリスト
        List<DrugInteractionModel> ret = new ArrayList<DrugInteractionModel>();
        
        // JSON のための wrapper list
        DrugInteractionList iList = new DrugInteractionList();
        iList.setList(ret);
        
        // Converter
        DragInteractionListConverter conv = new DragInteractionListConverter();
        conv.setModel(iList);
        
        if (input.getCodes1() == null       || 
                input.getCodes1().isEmpty() || 
                input.getCodes2() == null   || 
                input.getCodes2().isEmpty()) {
            return conv;
        }

        // SQL文を作成
        StringBuilder sb = new StringBuilder();
        sb.append("select drugcd, drugcd2, TI.syojyoucd, syojyou ");
        sb.append("from tbl_interact TI inner join tbl_sskijyo TS on TI.syojyoucd = TS.syojyoucd ");
        sb.append("where (drugcd in (");
        sb.append(getCodes(input.getCodes1()));
        sb.append(") and drugcd2 in (");
        sb.append(getCodes(input.getCodes2()));
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

        return conv;   
    }
    //masuda$
    
    //--------------------------------------------------------------------------
    // 一般名を検索する
    //--------------------------------------------------------------------------
    @GET
    @Path("/general/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public CodeNamePackConverter getGeneralName(@PathParam("param") String param) throws Exception {
        
        Connection con = null;
        PreparedStatement ps;
        String gname = null;
        CodeNamePack ret = null;

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
                ret = new CodeNamePack(param, rs.getString(2));
            }
            rs.close();
            ps.close();
            
            CodeNamePackConverter conv = new CodeNamePackConverter();
            conv.setModel(ret);
            
            return conv;
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            processError(e);
            closeConnection(con);
        } finally {
            closeConnection(con);
        }
        
        return null;
    }
    
    //--------------------------------------------------------------------------
    // ORCA 入力セット
    //--------------------------------------------------------------------------
    /**
     * ORCA の入力セットコード（約束処方、診療セット）を返す。
     * @return 入力セットコード(OrcaInputCd)の昇順リスト
     */
    @GET
    @Path("/inputset")
    @Produces(MediaType.APPLICATION_JSON)
    public OrcaInputCdListConverter getOrcaInputSet() {
         
        Connection con = null;
        ArrayList<OrcaInputCd> collection;
        Statement st = null;

        StringBuilder sb = new StringBuilder();
        sb.append("select * from tbl_inputcd where ");
        if (true) {
            sb.append("hospnum=");
            sb.append(HOSP_NUM);
            sb.append(" and ");
        } 
        sb.append("inputcd like 'P%' or inputcd like 'S%' order by inputcd");
        
        String sql = sb.toString();
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
            
            // Wrapper
            OrcaInputCdList wrapper = new OrcaInputCdList();
            wrapper.setList(collection);
            
            // Converter
            OrcaInputCdListConverter conv = new OrcaInputCdListConverter();
            conv.setModel(wrapper);
            
            return conv;
            
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
    @GET
    @Path("/stamp/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public ModuleListConverter getStamp(@PathParam("param") String param) {
        
        String[] params = param.split(CAMMA);
        String setCd = params[0]; // stampId=setCd; セットコード
        String stampName = params[1];
        debug("OrcaResource: getStamp");
        debug("setCd = " + setCd);
        debug("stampName = " + stampName);
        
        int hospnum = -1;
        if (true) {
            hospnum = HOSP_NUM;
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
            sb2.append("select srysyukbn,name,taniname,ykzkbn from tbl_tensu where srycd=? order by yukoedymd desc");
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
            
            for (ModuleModel mm : retSet) {
                byte[] bytes = getXMLBytes(mm.getModel());
                mm.setBeanBytes(bytes);
                mm.setModel(null);
            }
            
            // Warpper
            ModuleList mlist = new ModuleList();
            mlist.setList(retSet);
            
            // Converter
            ModuleListConverter conv = new ModuleListConverter();
            conv.setModel(mlist);
            
            return conv;
            
        } catch (Exception e) {
            processError(e);
            closeConnection(con);
            closeStatement(ps1);
            closeStatement(ps2);
        }
        
        return null; 
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
                
                String inOut = RP_OUT
                               ? ClaimConst.EXT_MEDICINE
                               : ClaimConst.IN_MEDICINE;
                bundle.setMemo(inOut);
                
            } else {
                
                bundle = new BundleDolphin();
                stamp.setModel(bundle);
            }
            
            bundle.setClassCode(code);
            bundle.setClassCodeSystem(ClaimConst.CLASS_CODE_ID);
            //bundle.setClassName(MMLTable.getClaimClassCodeName(code));
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
   
    
    //--------------------------------------------------------------------------
    // ORCA 病名インポート
    //--------------------------------------------------------------------------
    
    /**
     * ORCA に登録してある病名を検索する。
     * @return RegisteredDiagnosisModelのリスト
     */
    @GET
    @Path("/disease/import/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public RegisteredDiagnosisListConverter getOrcaDisease(@PathParam("param") String param) {
        
        String[] params = param.split(CAMMA);
        
        String patientId = params[0];
        String from = params[1];
        String to = params[2];
        boolean ascend = Boolean.parseBoolean(params[3]);
        
        Connection con = null;
        ArrayList<RegisteredDiagnosisModel> collection;
        PreparedStatement pt = null;
        String sql;
        String ptid = null;
        int hospNum = HOSP_NUM; //-1;
        
        StringBuilder sb = new StringBuilder();
        sb.append("select ptid, ptnum from tbl_ptnum where hospnum=? and ptnum=?");
        sql = sb.toString();
        debug(sql);
        
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
            warn(e.getMessage());
            processError(e);
            closeConnection(con);
            closeStatement(pt);
        }
        
        if (ptid == null) {
            warn("ptid=null");
            return null;
        }
        
        sb = new StringBuilder();
        sb.append("select sryymd,khnbyomeicd,utagaiflg,syubyoflg,tenkikbn,tenkiymd,byomei from tbl_ptbyomei where ");
        if (ascend) {
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
        debug(sql);
        
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
            
            // Wrapper
            RegisteredDiagnosisList rdl = new RegisteredDiagnosisList();
            rdl.setList(collection);
            
            // Converter
            RegisteredDiagnosisListConverter conv = new RegisteredDiagnosisListConverter();
            conv.setModel(rdl);
            
            return conv;
            
        } catch (Exception e) {
            warn(e.getMessage());
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
    @GET
    @Path("/disease/active/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public RegisteredDiagnosisListConverter getActiveOrcaDisease(@PathParam("param") String param) {
        
        String[] params = param.split(CAMMA);
        String patientId = params[0];
        boolean asc = Boolean.parseBoolean(params[1]);

        Connection con = null;
        ArrayList<RegisteredDiagnosisModel> collection;
        PreparedStatement pt = null;
        String sql;
        String ptid = null;
        int hospNum = HOSP_NUM; //-1;

        StringBuilder sb = new StringBuilder();
        sb.append("select ptid, ptnum from tbl_ptnum where hospnum=? and ptnum=?");
        sql = sb.toString();
        debug(sql);

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
            warn(e.getMessage());
            processError(e);
            closeConnection(con);
            closeStatement(pt);
        }

        if (ptid == null) {
            warn("ptid=null");
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
        debug(sql);

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
            
            // Wrapper
            RegisteredDiagnosisList rdl = new RegisteredDiagnosisList();
            rdl.setList(collection);

            // Converter
            RegisteredDiagnosisListConverter conv = new RegisteredDiagnosisListConverter();
            conv.setModel(rdl);

            return conv;
            
        } catch (Exception e) {
            warn(e.getMessage());
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
        }

        return null;
    }
    
    private byte[] getXMLBytes(Object bean)  {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));
        e.writeObject(bean);
        e.close();
        return bo.toByteArray();
    }
    
    // srycdのListからカンマ区切りの文字列を作る
    private String getCodes(Collection<String> srycdList){

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String srycd : srycdList){
            if (!first){
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(addSingleQuote(srycd));
        }
        return sb.toString();
    }
    
    private String addSingleQuote(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(str).append("'");
        return sb.toString();
    }
    
    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
    
    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
            }
        }
    }
    
    private void closeStatement(java.sql.Statement st) {
        if (st != null) {
            try {
                st.close();
            }
            catch (SQLException e) {
            	e.printStackTrace(System.err);
            }
        }
    }
    
    private void processError(Throwable e) {
        e.printStackTrace(System.err);
    }
    
    private void log(String msg) {
        Logger.getLogger("open.dolphin").info(msg);
    }
    
    private void warn(String msg) {
        Logger.getLogger("open.dolphin").warning(msg);
    }
    
    private void debug(String msg) {
        if (DEBUG) {
            Logger.getLogger("open.dolphin").fine(msg);
        }
    }
}
