package open.dolphin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import open.dolphin.infomodel.DiagnosisOutcomeModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.project.Project;

/**
 * ORCA の入力セットマスタを検索するクラス。
 *
 * @author Minagawa, Kazushi
 */
public class SqlOrcaView extends SqlDaoBean {
    
    private static final String DRIVER = "org.postgresql.Driver";
    private static final int PORT = 5432;
    private static final String DATABASE = "orca";
    private static final String USER = "orca";
    private static final String PASSWD = "";
    
    
    /**
     * Creates a new instance of SqlOrcaSetDao
     */
    public SqlOrcaView() {
        this.setDriver(DRIVER);
        this.setHost(Project.getClaimAddress());
        this.setPort(PORT) ;
        this.setDatabase(DATABASE);
        this.setUser(USER);
        this.setPasswd(PASSWD);
    }
    
    /**
     * ORCA の入力セットコード（約束処方、診療セット）を返す。
     * @return 入力セットコード(OrcaInputCd)の昇順リスト
     */
    public ArrayList<RegisteredDiagnosisModel> getOrcaDisease(String patientId, String from, String to, Boolean ascend) {
        
        Connection con = null;
        ArrayList<RegisteredDiagnosisModel> collection = null;
        PreparedStatement pt = null;
        String sql = null;
        String ptid = null;
        int hospNum = -1;
        
        StringBuilder sb = new StringBuilder();
        sb.append("select ptid, ptnum from tbl_ptnum where ");
        if (Project.getOrcaVersion().startsWith("4")) {
            hospNum = getHospNum();
            sb.append("hospnum=? and ptnum=?");
        } else {
            sb.append("ptnum=?");
        }
        sql = sb.toString();
        
        try {
            con = getConnection();
            pt = con.prepareStatement(sql);
            if (hospNum > 0) {
                pt.setInt(1, hospNum);
                pt.setString(2, patientId);
            } else {
                pt.setString(1, patientId);
            }
            ResultSet rs = pt.executeQuery();
            if (rs.next()) {
                ptid = rs.getString(1);
            }
            closeConnection(con);
            closeStatement(pt);
            
        }  catch (Exception e) {
            processError(e);
            closeConnection(con);
            closeStatement(pt);
        }
        
        if (ptid == null) {
            return null;
        }
        
        sb = new StringBuilder();
        
        if (ascend.booleanValue()) {
            if (hospNum > 0) {
                sb.append("select sryymd,khnbyomeicd,tenkikbn,tenkiymd,byomei from tbl_ptbyomei where hospnum=? and ptid=? and sryymd >= ? and sryymd <= ? order by sryymd asc");
            } else {
                sb.append("select sryymd,khnbyomeicd,tenkikbn,tenkiymd,byomei from tbl_ptbyomei where ptid=? and sryymd >= ? and sryymd <= ? order by sryymd asc");
            }
        } else {
            if (hospNum > 0) {
                sb.append("select sryymd,khnbyomeicd,tenkikbn,tenkiymd,byomei from tbl_ptbyomei where hospnum=? and ptid=? and sryymd >= ? and sryymd <= ? order by sryymd desc");
            } else {
                sb.append("select sryymd,khnbyomeicd,tenkikbn,tenkiymd,byomei from tbl_ptbyomei where ptid=? and sryymd >= ? and sryymd <= ? order by sryymd desc");
            }
        }
        sql = sb.toString();
        
        try {
            con = getConnection();
            pt = con.prepareStatement(sql);
            if (hospNum > 0) {
                pt.setInt(1, hospNum);
                pt.setString(2, ptid);
                pt.setString(3, from);
                pt.setString(4, to);
            } else {
                pt.setString(1, ptid);
                pt.setString(2, from);
                pt.setString(3, to);
            }
            //System.out.println(pt.toString());
            ResultSet rs = pt.executeQuery();
            collection = new ArrayList<RegisteredDiagnosisModel>();
            
            while (rs.next()) {
                
                RegisteredDiagnosisModel ord = new RegisteredDiagnosisModel();
                
                // 疾患開始日
                ord.setStartDate(toDolphinDateStr(rs.getString(1)));
                
                // 病名コード
                ord.setDiagnosisCode(rs.getString(2));
                
                // 転帰
                DiagnosisOutcomeModel out = new DiagnosisOutcomeModel();
                ord.setDiagnosisOutcomeModel(out);
                String data = rs.getString(3);
                out.setOutcomeDesc(toDolphinOutcome(data));
                
                // 疾患終了日（転帰）
                ord.setEndDate(toDolphinDateStr(rs.getString(4)));
                
                // 疾患名
                ord.setDiagnosis(rs.getString(5));
                
                // 制御のための Status
                ord.setStatus("ORCA");
                
                collection.add(ord);
            }
            
            rs.close();
            closeStatement(pt);
            closeConnection(con);
            
            return collection;
            
        } catch (Exception e) {
            processError(e);
            closeConnection(con);
            closeStatement(pt);
        }
        
        return null;
    }
    
    private String toDolphinDateStr(String orcaDate) {
        if (orcaDate != null) {
            boolean digit = true;
            for (int i = 0; i < orcaDate.length(); i++) {
                if(!Character.isDigit(orcaDate.charAt(i))) {
                    digit = false;
                    break;
                }
            }
            if (!digit) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(orcaDate.substring(0, 4));
            sb.append("-");
            sb.append(orcaDate.substring(4, 6));
            sb.append("-");
            sb.append(orcaDate.substring(6));
            return sb.toString();
        }
        return null;
    }
    
    private String toDolphinOutcome(String orcaOutcome) {
        if (orcaOutcome != null) {
            String outcomeDesc = null;
            if (orcaOutcome.equals("1")) {
                outcomeDesc = IInfoModel.ORCA_OUTCOME_RECOVERED;
            } else if (orcaOutcome.equals("2")) {
                outcomeDesc = IInfoModel.ORCA_OUTCOME_DIED;
            } else if (orcaOutcome.equals("3")) {
                outcomeDesc = IInfoModel.ORCA_OUTCOME_END;
            } else if (orcaOutcome.equals("8")) {
                outcomeDesc = IInfoModel.ORCA_OUTCOME_TRANSFERED;
            }
            return outcomeDesc;
        }
        return null;
    }
}
