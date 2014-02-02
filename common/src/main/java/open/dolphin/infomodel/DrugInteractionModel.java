package open.dolphin.infomodel;

/**
 * 薬剤相互作用のモデル
 *
 * @author masuda, Masuda Naika
 */
public class DrugInteractionModel extends InfoModel implements java.io.Serializable {

    private String srycd1;
    private String srycd2;
    private String sskijo;
    private String syojyoucd;
    
    public DrugInteractionModel() {
    }

    public DrugInteractionModel(String srycd1, String srycd2, String sskijo, String syojyoucd){
        this();
        this.srycd1 = srycd1;
        this.srycd2 = srycd2;
        this.sskijo = sskijo;
        this.syojyoucd = syojyoucd;
    }

    public String getSrycd1() {
        return srycd1;
    }

    public void setSrycd1(String srycd1) {
        this.srycd1 = srycd1;
    }

    public String getSrycd2() {
        return srycd2;
    }

    public void setSrycd2(String srycd2) {
        this.srycd2 = srycd2;
    }

    public String getSskijo() {
        return sskijo;
    }

    public void setSskijo(String sskijo) {
        this.sskijo = sskijo;
    }

    public String getSyojyoucd() {
        return syojyoucd;
    }

    public void setSyojyoucd(String syojyoucd) {
        this.syojyoucd = syojyoucd;
    }
}
