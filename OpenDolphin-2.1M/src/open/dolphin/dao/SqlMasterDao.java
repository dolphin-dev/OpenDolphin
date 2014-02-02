package open.dolphin.dao;

import java.sql.*;
import java.util.*;

import open.dolphin.infomodel.DiseaseEntry;
import open.dolphin.infomodel.TensuMaster;
import open.dolphin.util.*;

/**
 * SqlMasterDao
 *
 * @author Kazushi Minagawa
 */
public final class SqlMasterDao extends SqlDaoBean {

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

    /**
     * Creates a new instance of SqlMasterDao
     */
    public SqlMasterDao() {
    }

    public List<TensuMaster> getTensuMasterByShinku(String shinku, String now) {

        // 結果を格納するリスト
        ArrayList<TensuMaster> ret = new ArrayList<TensuMaster>();

        // SQL 文
        StringBuilder buf = new StringBuilder();
        buf.append(QUERY_TENSU_BY_SHINKU);
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps = null;

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
            processError(e);
            closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;
    }

    public List<TensuMaster> getTensuMasterByName(String name, String now, boolean partialMatch) {

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
        PreparedStatement ps = null;

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
            processError(e);
            closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;
    }

    public List<TensuMaster> getTensuMasterByCode(String regExp, String now) {

        // 結果を格納するリスト
        ArrayList<TensuMaster> ret = new ArrayList<TensuMaster>();

        // SQL 文
        StringBuilder buf = new StringBuilder();
        buf.append(QUERY_TENSU_BY_CODE);
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps = null;

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
            processError(e);
            closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;
    }


    public List<TensuMaster> getTensuMasterByTen(String ten, String now) {

        // 結果を格納するリスト
        ArrayList<TensuMaster> ret = new ArrayList<TensuMaster>();

        // SQL 文
        StringBuilder buf = new StringBuilder();
        buf.append(QUERY_TENSU_BY_TEN);
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps = null;

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
            processError(e);
            closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;
    }

    public List<DiseaseEntry> getDiseaseByName(String name, String now, boolean partialMatch) {

        // 結果を格納するリスト
        ArrayList<DiseaseEntry> ret = new ArrayList<DiseaseEntry>();

        // SQL 文
        StringBuilder buf = new StringBuilder();
        
        //masuda^ Version46 対応
        if (ORCA_DB_VER46.equals(getOrcaDbVersion())) {
            buf.append(QUERY_DICEASE_BY_NAME_46);
        } else {
            buf.append(QUERY_DICEASE_BY_NAME);
        }
        //masuda$
        
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps = null;

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
            processError(e);
            closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;

    }
}