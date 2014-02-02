/*
 * SqlOrcaSetDao.java
 * Copyright (C) 2007 Digital Globe, Inc. All rights reserved.
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
        
        StringBuilder sb = new StringBuilder();
        sb.append("select ptid, ptnum from tbl_ptnum where ptnum=?");
        sql = sb.toString();
        try {
            con = getConnection();
            pt = con.prepareStatement(sql);
            pt.setString(1, patientId);
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
            sb.append("select sryymd, khnbyomeicd, tenkikbn, tenkiymd, byomei from tbl_ptbyomei where ptid=? and sryymd >= ? and sryymd < ? order by sryymd asc");
        } else {
            sb.append("select sryymd, khnbyomeicd, tenkikbn, tenkiymd, byomei from tbl_ptbyomei where ptid=? and sryymd >= ? and sryymd < ? order by sryymd desc");
        }
        sql = sb.toString();
        
        try {
            con = getConnection();
            pt = con.prepareStatement(sql);
            pt.setString(1, ptid);
            pt.setString(2, from);
            pt.setString(3, to);
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
                out.setOutcomeDesc(toDolphinOutcome(rs.getString(3)));
                
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
            } else if (orcaOutcome.equals("4")) {
                outcomeDesc = IInfoModel.ORCA_OUTCOME_TRANSFERED;
            }
            return outcomeDesc;
        }
        return null;
    }
}
