package open.dolphin.delegater;

import java.util.Collection;

import java.util.List;
import javax.naming.NamingException;

import open.dolphin.dto.LaboSearchSpec;
import open.dolphin.ejb.RemoteLaboService;
import open.dolphin.ejb.RemoteNLaboService;
import open.dolphin.infomodel.LaboModuleValue;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;
import open.dolphin.infomodel.PatientModel;

/**
 * Labo 関連の Delegater クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LaboDelegater extends BusinessDelegater {
    
    /**
     * LaboModule を保存する。
     * @param laboModuleValue
     * @return LaboImportReply
     */
    public PatientModel putLaboModule(LaboModuleValue value) {
        
        try {
            return getService().putLaboModule(value);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return null;
    }
    
    public Collection getLaboModules(LaboSearchSpec spec) {
        
        Collection c = null;
        
        try {
            c = getService().getLaboModuless(spec);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return c;
    }

    private RemoteLaboService getService() throws NamingException {
        return (RemoteLaboService) getService("RemoteLaboService");
    }

    //=========================================================
    // 新 LabMozule
    //=========================================================
    
    /**
     * 検査結果を追加する。
     * @param value 追加する検査モジュール
     * @return      患者オブジェクト
     */
    public PatientModel putNLaboModule(NLaboModule value) {

        try {
            return getNService().create(value);

        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }

        return null;
    }

    /**
     * ラボモジュールを検索する。
     * @param patientId     対象患者のID
     * @param firstResult   取得結果リストの最初の番号
     * @param maxResult     取得する件数の最大値
     * @return              ラボモジュールを採取日で降順に格納したリスト
     */
    public List<NLaboModule> getLaboTest(String patientId, int firstResult, int maxResult) {
        
        try {
            return getNService().getLaboTest(patientId, firstResult, maxResult);

        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }

        return null;
    }

    /**
     * 指定された検査コードの結果を取得する。
     * @param patientId     対象患者
     * @param firstResult   全件数のなかで最初に返す番号
     * @param maxResult     戻す最大件数
     * @param itemCode      検索する検査コード
     * @return              検査結果項目を採取日で降順に格納したリスト
     */
    public List<NLaboItem> getLaboTestItem(String patientId, int firstResult, int maxResult, String itemCode) {
        
        try {
            return getNService().getLaboTestItem(patientId, firstResult, maxResult, itemCode);

        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }

        return null;
    }

    //
    // RemoteNLaboServiceを返す。
    //
    private RemoteNLaboService getNService() throws NamingException {
        return (RemoteNLaboService) getService("RemoteNLaboService");
    }
}
