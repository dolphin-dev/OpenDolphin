package open.dolphin.impl.falco;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import open.dolphin.client.Chart;
import open.dolphin.client.IKarteSender;
import open.dolphin.exception.DolphinException;
import open.dolphin.infomodel.*;
import open.dolphin.project.Project;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class FalcoSender implements IKarteSender {

    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private Chart context;
    private String insuranceFacilityId;
    private String path;
    private List<BundleDolphin> sendList;
    private String orderNumber;

    private static String createOrderNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append("DL");
        sb.append(SDF.format(new Date()));
        return sb.toString();
    }

    @Override
    public Chart getContext() {
        return context;
    }

    @Override
    public void setContext(Chart context) {
        this.context = context;
    }

    @Override
    public void prepare(DocumentModel data) {

        if (data==null || (!data.getDocInfoModel().isSendLabtest())) {
            return;
        }

        // 保健医療機関コード
        insuranceFacilityId = Project.getString(Project.SEND_LABTEST_FACILITY_ID);
        if (insuranceFacilityId==null || insuranceFacilityId.length()<10) {
            throw new DolphinException("保険医療機関コードが設定されていません。");
        }
        insuranceFacilityId+="00";

        // 検査オーダーの出力先パス
        path = Project.getString(Project.SEND_LABTEST_PATH);
        if (path==null) {
            throw new DolphinException("検体検査オーダーの出力先パスが設定されていません。");
        }

        // 検体検査オーダーを抽出する
        List<ModuleModel> modules = data.getModules();

        if (modules==null || modules.isEmpty()) {
            return;
        }

        sendList = new ArrayList<BundleDolphin>();
        for (ModuleModel module : modules) {
            ModuleInfoBean info = module.getModuleInfoBean();
            if (info.getEntity().equals(IInfoModel.ENTITY_LABO_TEST)) {
                BundleDolphin send = (BundleDolphin)module.getModel();
                ClaimItem[] items = send.getClaimItem();
                if (items!=null && items.length>0) {
                    sendList.add(send);
                }
            }
        }

        // オーダー番号を docInfo へ設定する
        if (data.getDocInfoModel().getLabtestOrderNumber()==null) {
            orderNumber = createOrderNumber();
            data.getDocInfoModel().setLabtestOrderNumber(orderNumber);
        } else {
            // 修正の場合は設定されている
            orderNumber = data.getDocInfoModel().getLabtestOrderNumber();
        }
    }

    @Override
    public void send(DocumentModel data) {

        if (data==null ||
           (!data.getDocInfoModel().isSendLabtest()) ||
            sendList.isEmpty() ||
            insuranceFacilityId==null ||
            path==null) {
            return;
        }

        // 送信する
        PatientModel patient = context.getPatient();
        UserModel user = Project.getUserModel();
        
        HL7Falco falco = new HL7Falco();
        falco.order(patient, user, sendList, insuranceFacilityId, orderNumber, path);
    }
}
