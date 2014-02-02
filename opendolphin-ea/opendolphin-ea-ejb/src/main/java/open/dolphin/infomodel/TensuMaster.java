package open.dolphin.infomodel;

import java.io.Serializable;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class TensuMaster implements Serializable {

    private Integer hospnum;

    private String srycd;

    private String yukostymd;

    private String yukoedymd;

    private String name;

    private String kananame;

    private String taniname;

    private String tensikibetu;

    private String ten;

    private String ykzkbn;

    private String yakkakjncd;

    private String nyugaitekkbn;

    private String routekkbn;

    private String srysyukbn;

    private String hospsrykbn;

    /**
     * @return the hospnum
     */
    public Integer getHospnum() {
        return hospnum;
    }

    /**
     * @param hospnum the hospnum to set
     */
    public void setHospnum(Integer hospnum) {
        this.hospnum = hospnum;
    }

    /**
     * @return the srycd
     */
    public String getSrycd() {
        return srycd;
    }

    /**
     * @param srycd the srycd to set
     */
    public void setSrycd(String srycd) {
        this.srycd = srycd;
    }

    /**
     * @return the yukostymd
     */
    public String getYukostymd() {
        return yukostymd;
    }

    /**
     * @param yukostymd the yukostymd to set
     */
    public void setYukostymd(String yukostymd) {
        this.yukostymd = yukostymd;
    }

    /**
     * @return the yukoedymd
     */
    public String getYukoedymd() {
        return yukoedymd;
    }

    /**
     * @param yukoedymd the yukoedymd to set
     */
    public void setYukoedymd(String yukoedymd) {
        this.yukoedymd = yukoedymd;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the kananame
     */
    public String getKananame() {
        return kananame;
    }

    /**
     * @param kananame the kananame to set
     */
    public void setKananame(String kananame) {
        this.kananame = kananame;
    }

    /**
     * @return the taniname
     */
    public String getTaniname() {
        return taniname;
    }

    /**
     * @param taniname the taniname to set
     */
    public void setTaniname(String taniname) {
        this.taniname = taniname;
    }

    /**
     * @return the tensikibetu
     */
    public String getTensikibetu() {
        return tensikibetu;
    }

    /**
     * @param tensikibetu the tensikibetu to set
     */
    public void setTensikibetu(String tensikibetu) {
        this.tensikibetu = tensikibetu;
    }

    /**
     * @return the ten
     */
    public String getTen() {
        return ten;
    }

    /**
     * @param ten the ten to set
     */
    public void setTen(String ten) {
        this.ten = ten;
    }

    /**
     * @return the ykzkbn
     */
    public String getYkzkbn() {
        return ykzkbn;
    }

    /**
     * @param ykzkbn the ykzkbn to set
     */
    public void setYkzkbn(String ykzkbn) {
        this.ykzkbn = ykzkbn;
    }

    /**
     * @return the yakkakjncd
     */
    public String getYakkakjncd() {
        return yakkakjncd;
    }

    /**
     * @param yakkakjncd the yakkakjncd to set
     */
    public void setYakkakjncd(String yakkakjncd) {
        this.yakkakjncd = yakkakjncd;
    }

    /**
     * @return the nyugaitekkbn
     */
    public String getNyugaitekkbn() {
        return nyugaitekkbn;
    }

    /**
     * @param nyugaitekkbn the nyugaitekkbn to set
     */
    public void setNyugaitekkbn(String nyugaitekkbn) {
        this.nyugaitekkbn = nyugaitekkbn;
    }

    /**
     * @return the routekkbn
     */
    public String getRoutekkbn() {
        return routekkbn;
    }

    /**
     * @param routekkbn the routekkbn to set
     */
    public void setRoutekkbn(String routekkbn) {
        this.routekkbn = routekkbn;
    }

    /**
     * @return the srysyukbn
     */
    public String getSrysyukbn() {
        return srysyukbn;
    }

    /**
     * @param srysyukbn the srysyukbn to set
     */
    public void setSrysyukbn(String srysyukbn) {
        this.srysyukbn = srysyukbn;
    }

    /**
     * @return the hospsrykbn
     */
    public String getHospsrykbn() {
        return hospsrykbn;
    }

    /**
     * @param hospsrykbn the hospsrykbn to set
     */
    public void setHospsrykbn(String hospsrykbn) {
        this.hospsrykbn = hospsrykbn;
    }

    public String getSlot() {

        if (srycd==null) {
            return null;
        }

        String ret = null;

        if (srycd.startsWith(ClaimConst.SYUGI_CODE_START)) {
            ret = ClaimConst.SLOT_SYUGI;

        } else if (srycd.startsWith(ClaimConst.YAKUZAI_CODE_START)) {
            //内用1、外用6、注射薬4
            if (ykzkbn.equals(ClaimConst.YKZ_KBN_NAIYO)) {
                ret = ClaimConst.SLOT_NAIYO_YAKU;

            } else if (ykzkbn.equals(ClaimConst.YKZ_KBN_INJECTION)) {
                ret = ClaimConst.SLOT_TYUSHYA_YAKU;

            } else if (ykzkbn.equals(ClaimConst.YKZ_KBN_GAIYO)) {
                ret = ClaimConst.SLOT_GAIYO_YAKU;

            } else {
                ret = ClaimConst.SLOT_YAKUZAI;
            }

        } else if (srycd.startsWith(ClaimConst.ZAIRYO_CODE_START)) {
            ret = ClaimConst.SLOT_ZAIRYO;

        } else if (srycd.startsWith(ClaimConst.ADMIN_CODE_START)) {
            ret = ClaimConst.SLOT_YOHO;

        } else if (srycd.startsWith(ClaimConst.RBUI_CODE_START)) {
            ret = ClaimConst.SLOT_BUI;

        } else {
            ret = ClaimConst.SLOT_OTHER;
        }

        return ret;
    }
}























