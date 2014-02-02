package open.dolphin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import open.dolphin.infomodel.BundleDolphin;
import open.dolphin.infomodel.BundleMed;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.OrcaInputCd;
import open.dolphin.infomodel.OrcaInputSet;
import open.dolphin.infomodel.ClaimConst;
import open.dolphin.order.MMLTable;
import open.dolphin.project.Project;

/**
 * ORCA の入力セットマスタを検索するクラス。
 *
 * @author Minagawa, Kazushi
 */
public class SqlOrcaSetDao extends SqlDaoBean {
    
    private static final String DRIVER = "org.postgresql.Driver";
    private static final int PORT = 5432;
    private static final String DATABASE = "orca";
    private static final String USER = "orca";
    private static final String PASSWD = "";
    //private static final String S_SET = "S";
    //private static final String P_SET = "P";
    private static final String RP_KBN_START = "2";
    private static final String SHINRYO_KBN_START = ".";
    private static final int SHINRYO_KBN_LENGTH = 3;
    private static final int DEFAULT_BUNDLE_NUMBER = 1;
    private static final String KBN_RP = "220";
    private static final String KBN_RAD = "700";
    private static final String KBN_GENERAL = "999";
    
    /** 
     * Creates a new instance of SqlOrcaSetDao 
     */
    public SqlOrcaSetDao() {
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
    public ArrayList<OrcaInputCd> getOrcaInputSet() {
         
        debug("getOrcaInputSet()");
        Connection con = null;
        ArrayList<OrcaInputCd> collection = null;
        Statement st = null;
        String sql = null;

        StringBuilder sb = new StringBuilder();
        sb.append("select * from tbl_inputcd where ");
        if (Project.getOrcaVersion().startsWith("4")) {
            int hospnum = getHospNum();
            sb.append("hospnum=");
            sb.append(hospnum);
            sb.append(" and ");
        } 
        sb.append("inputcd like 'P%' or inputcd like 'S%' order by inputcd");
        
        sql = sb.toString();
        debug(sql);
        
        boolean v4 = Project.getOrcaVersion().startsWith("4") ? true : false;
        
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
    public ArrayList<ModuleModel> getStamp(ModuleInfoBean inputSetInfo) {
        
        String setCd = inputSetInfo.getStampId(); // stampId=setCd; セットコード
        String stampName = inputSetInfo.getStampName();
        debug("getStamp()");
        debug("setCd = " + setCd);
        debug("stampName = " + stampName);
        
        
        int hospnum = -1;
        if (Project.getOrcaVersion().startsWith("4")) {
            hospnum = getHospNum();
        }
        
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        String sql1 = null;
        String sql2 = null;
        
        StringBuilder sb1 = new StringBuilder();
        if (Project.getOrcaVersion().startsWith("4")) {
            sb1.append("select inputcd,suryo1,kaisu from tbl_inputset where hospnum=? and setcd=? order by setseq");
            sql1 = sb1.toString();
        } else {
            sb1.append("select inputcd,suryo1,kaisu from tbl_inputset where setcd=? order by setseq");
            sql1 = sb1.toString();
        }
        
        StringBuilder sb2 = new StringBuilder();
        if (Project.getOrcaVersion().startsWith("4")) {
            sb2.append("select srysyukbn,name,taniname,ykzkbn from tbl_tensu where hospnum=? and srycd=?");
            sql2 = sb2.toString();
        } else {
            sb2.append("select srysyukbn,name,taniname,ykzkbn from tbl_tensu where srycd=?");
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
            
            ModuleModel stamp = null;
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
                                if (bundle == null) {
                                    stamp = createStamp(stampName, KBN_RP);
                                    if (stamp != null) {
                                        bundle = (BundleDolphin) stamp.getModel();
                                        retSet.add(stamp);
                                    }
                                }
                                
                                if (bundle != null) {
                                    bundle.setAdmin(name);
                                    bundle.setAdminCode(code);
                                    bundle.setBundleNumber(String.valueOf(inputSet.getKaisu()));
                                }
                            
                            } else if (inputcd.startsWith(ClaimConst.RBUI_CODE_START)) {
                                //
                                // 放射線部位の場合
                                //
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
            BundleDolphin bundle = null;
                
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
            
            } else if (number >= 130 && number <= 140) {
                return new String[]{IInfoModel.ENTITY_INSTRACTION_CHARGE_ORDER, "指導・在宅"};
                
            } else if (number >= 200 && number <= 299) {
                return new String[]{IInfoModel.ENTITY_MED_ORDER, "RP"};
            
            } else if (number >= 300 && number <= 331) {
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
}
