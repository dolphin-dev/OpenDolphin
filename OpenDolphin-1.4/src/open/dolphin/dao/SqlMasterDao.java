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
    
    /** Creates a new instance of SqlMasterDao */
    public SqlMasterDao() {
    }

    public List<TensuMaster> getTensuMasterByShinku(String shinku, String now) {

        // åãâ Çäiî[Ç∑ÇÈÉäÉXÉg
        ArrayList<TensuMaster> ret = new ArrayList<TensuMaster>();

        // SQL ï∂
        StringBuilder buf = new StringBuilder();
        buf.append("select srycd,name,kananame,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn,yukostymd,yukoedymd from tbl_tensu ");
        buf.append("where srysyukbn ~ ? and yukostymd<= ? and yukoedymd>=?");
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
            e.printStackTrace();
            processError(e);
            closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;
    }


    public List<TensuMaster> getTensuMasterByName(String name, String now) {

        // åãâ Çäiî[Ç∑ÇÈÉäÉXÉg
        ArrayList<TensuMaster> ret = new ArrayList<TensuMaster>();

        // îºäpâpêîéöÇëSäpÇ÷ïœä∑Ç∑ÇÈ
        name = "^"+StringTool.toZenkakuUpperLower(name);

        // SQL ï∂
        StringBuilder buf = new StringBuilder();
        buf.append("select srycd,name,kananame,taniname,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn,ykzkbn,yakkakjncd,yukostymd,yukoedymd from tbl_tensu ");
        buf.append("where (name ~ ? or kananame ~ ?) and yukostymd<= ? and yukoedymd>=?");
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
            e.printStackTrace();
            processError(e);
            closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;
    }

    public List<TensuMaster> getTensuMasterByCode(String regExp, String now) {

        // åãâ Çäiî[Ç∑ÇÈÉäÉXÉg
        ArrayList<TensuMaster> ret = new ArrayList<TensuMaster>();

        // SQL ï∂
        StringBuilder buf = new StringBuilder();
        buf.append("select srycd,name,kananame,taniname,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn,ykzkbn,yakkakjncd,yukostymd,yukoedymd from tbl_tensu ");
        buf.append("where srycd ~ ? and yukostymd<= ? and yukoedymd>=?");
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps = null;

        try
        {
            con = getConnection();
            ps = con.prepareStatement(sql);
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
            e.printStackTrace();
            processError(e);
            closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;
    }

    public List<DiseaseEntry> getDiseaseByName(String name, String now) {

        // åãâ Çäiî[Ç∑ÇÈÉäÉXÉg
        ArrayList<DiseaseEntry> ret = new ArrayList<DiseaseEntry>();

        // SQL ï∂
        StringBuilder buf = new StringBuilder();
        buf.append("select byomeicd, byomei, byomeikana, icd10, haisiymd from tbl_byomei where byomei ~ ? and haisiymd >= ?");
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps = null;

        try
        {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, "^"+name);
            ps.setString(2, now);

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
            e.printStackTrace();
            processError(e);
            closeConnection(con);

        } finally {
            closeConnection(con);
        }

        return ret;

    }
}