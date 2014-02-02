package open.dolphin.labrcv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class NLabParser {

    private static final String MIHOKOKU = "未報告";

    private String encoding = "SHIFT-JIS";
    

    /**
     * 入力ストリームの検査結果をパースする。
     */
    public List<NLaboImportSummary> parse(File labFile) throws IOException, Exception {

        String line = null;
        String curKey = null;
        NLaboModule curModule = null;
        List<NLaboModule> allModules = new ArrayList<NLaboModule>();
        List<NLaboImportSummary> retList = new ArrayList<NLaboImportSummary>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(labFile),getEncoding()));

        while ((line = reader.readLine()) != null) {

            String[] data = line.split(",");    // CSV

            String lboCode = data[0];
            String patientId = data[1];
            String sampleDate = data[2];
            String patientName = data[3];
            String patientSex = data[4];

            sampleDate = sampleDate.replaceAll("/", "-");

            StringBuffer buf = new StringBuffer();
            buf.append(patientId);
            buf.append(".");
            buf.append(sampleDate);
            buf.append(".");
            buf.append(lboCode);
            String testKey = buf.toString();

            if (!testKey.equals(curKey)) {

                curModule = new NLaboModule();
                curModule.setLaboCenterCode(lboCode);
                curModule.setPatientId(patientId);
                curModule.setPatientName(patientName);
                curModule.setPatientSex(patientSex);
                curModule.setSampleDate(sampleDate);
                allModules.add(curModule);

                curKey = testKey;
            }

            NLaboItem item = new NLaboItem();

            item.setPatientId(patientId);   // カルテ番号
            item.setSampleDate(sampleDate); // 検体採取日

            int index = 5;
            while (index < data.length) {

                switch (index) {

                    case 5:
                        item.setLipemia(data[index]);       // 乳ビ
                        break;

                    case 6:
                        item.setHemolysis(data[index]);     // 溶血
                        break;

                    case 7:
                        item.setDialysis(data[index]);      // 透析
                        break;

                    case 8:
                        item.setReportStatus(data[index]);  // 報告状況
                        break;

                    case 9:
                        item.setGroupCode(data[index]);     // グループコード
                        break;

                    case 10:
                        item.setGroupName(data[index]);     // グループ名称
                        break;

                    case 11:
                        item.setParentCode(data[index]);    // 検査項目コード・親
                        break;

                    case 12:
                        item.setItemCode(data[index]);      // 検査項目コード
                        break;

                    case 13:
                        item.setMedisCode(data[index]);     // MEDIS コード
                        break;

                    case 14:
                        item.setItemName(data[index]);      // 検査項目名
                        break;

                    case 15:
                        item.setAbnormalFlg(data[index]);   // 異常区分
                        break;

                    case 16:
                        item.setNormalValue(data[index]);   // 基準値
                        break;

                    case 17:
                        item.setValue(data[index]);         // 検査結果
                        break;

                    case 18:
                        item.setUnit(data[index]);          // 単位
                        break;

                    case 19:
                        item.setSpecimenCode(data[index]);   // 検体材料コード
                        break;

                    case 20:
                        item.setSpecimenName(data[index]);   // 検体材料名称
                        break;

                    case 21:
                        item.setCommentCode1(data[index]);  // コメントコード1
                        break;

                    case 22:
                        item.setComment1(data[index]);      // コメント1
                        break;

                    case 23:
                        item.setCommentCode2(data[index]);  // コメントコード2
                        break;

                    case 24:
                        item.setComment2(data[index]);     // コメント2
                        break;
                }

                index+=1;
            }

            // 検査結果値がない場合
            if (item.getValue() == null || item.getValue().equals("")) {
                item.setValue(MIHOKOKU);
            }

            // 関係を構築する
            curModule.addItem(item);
            item.setLaboModule(curModule);
        }

        reader.close();

        // サマリを生成する
        for (NLaboModule module : allModules) {

            NLaboImportSummary summary = new NLaboImportSummary();
            summary.setLaboCode(module.getLaboCenterCode());
            summary.setPatientId(module.getPatientId());
            summary.setPatientName(module.getPatientName());
            summary.setPatientSex(module.getPatientSex());
            summary.setSampleDate(module.getSampleDate());
            summary.setNumOfTestItems(String.valueOf(module.getItems().size()));
            summary.setModule(module);
            retList.add(summary);
        }
        
        return retList;
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
