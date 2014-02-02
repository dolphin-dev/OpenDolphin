package open.dolphin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.project.Project;

/**
 * ORCA に登録してある病名を検索するクラス。
 *
 * @author Minagawa, Kazushi
 */
public class SqlOrcaView extends SqlDaoBean {
    
    private static final String DRIVER = "org.postgresql.Driver";
    private static final int PORT = 5432;
    private static final String DATABASE = "orca";
    private static final String USER = "orca";
    private static final String PASSWD = "";
    private SimpleDateFormat sdf;
    
    /**
     * Creates a new instance of SqlOrcaSetDao
     */
    public SqlOrcaView() {
        this.setDriver(DRIVER);
        this.setHost(Project.getString(Project.CLAIM_ADDRESS));
        this.setPort(PORT) ;
        this.setDatabase(DATABASE);
        this.setUser(USER);
        this.setPasswd(PASSWD);
        sdf = new SimpleDateFormat();
    }
    
    /**
     * ORCA に登録してある病名を検索する。
     * @return RegisteredDiagnosisModelのリスト
     */
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
