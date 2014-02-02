package open.dolphin.labrcv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class NLabParser implements LabResultParser {

    private static final String MIHOKOKU = "未報告";
    private static final String NO_RESULT = "結果値無し";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_FORMAT_8 = "yyyyMMdd";
    private static final String CSV_DELIM = "\\s*,\\s*";

    private String encoding = "SHIFT-JIS";

    private Boolean DEBUG;

    private Logger logger;

    public NLabParser() {
        logger = ClientContext.getLaboTestLogger();
        DEBUG = (logger.getLevel() == Level.DEBUG) ? true : false;
    }
    

    /**
     * 入力ストリームの検査結果をパースする。
     */
    @Override
    public List<NLaboImportSummary> parse(File labFile) throws IOException {

        String line = null;
        String curKey = null;
        NLaboModule curModule = null;
        List<NLaboModule> allModules = new ArrayList<NLaboModule>();
        List<NLaboImportSummary> retList = new ArrayList<NLaboImportSummary>();

        SimpleDateFormat defaultDF = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        SimpleDateFormat df8 = new SimpleDateFormat(DATE_FORMAT_8);

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(labFile),getEncoding()));

        while ((line = reader.readLine()) != null) {

            String[] data = line.split(CSV_DELIM);    // CSV

            String lboCode = data[0];
            String patientId = data[1];
            String sampleDate = data[2];
            String patientName = data[3];
            String patientSex = data[4];

            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("------------------------------------------").append("\n");
                sb.append(lboCode).append(" ");
                sb.append(patientId).append(" ");
                sb.append(sampleDate).append(" ");
                sb.append(patientName).append(" ");
                sb.append(patientSex);
                logger.debug(sb.toString());
            }

            sampleDate = sampleDate.replaceAll("/", "-");
            Date date = null;

            // YYYYMMDDの場合
            try {
                if (sampleDate.length()==8) {
                    date = df8.parse(sampleDate);
                } else {
                    date = defaultDF.parse(sampleDate);
                }
            } catch (Exception e) {
                logger.warn(e);
                throw new IOException(e.getMessage());
            }

            // key for the parse line
            StringBuilder buf = new StringBuilder();
            buf.append(patientId);
            buf.append(".");
            buf.append(sampleDate);
            buf.append(".");
            buf.append(lboCode);
            String testKey = buf.toString();

            if (!testKey.equals(curKey)) {

                // 新しい current module を生成する
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

                String val = data[index];

                if (val != null) {
                    val = val.trim();
                }

                if (val == null || val.equals("")) {
                    index++;
                    continue;
                }

                switch (index) {

                    case 5:
                        debug("乳ビ", val);
                        item.setLipemia(val);       // 乳ビ
                        break;

                    case 6:
                        debug("溶血", val);
                        item.setHemolysis(val);     // 溶血
                        break;

                    case 7:
                        debug("透析", val);
                        item.setDialysis(val);      // 透析
                        break;

                    case 8:
                        debug("報告状況", val);
                        item.setReportStatus(val);  // 報告状況
                        break;

                    case 9:
                        debug("グループコード", val);
                        item.setGroupCode(val);     // グループコード
                        break;

                    case 10:
                        debug("グループ名称", val);
                        item.setGroupName(val);     // グループ名称
                        break;

                    case 11:
                        debug("検査項目コード・親", val);
                        item.setParentCode(val);    // 検査項目コード・親
                        break;

                    case 12:
                        debug("検査項目コード", val);
                        item.setItemCode(val);      // 検査項目コード
                        break;

                    case 13:
                        debug("MEDISコード", val);
                        item.setMedisCode(val);     // MEDIS コード
                        break;

                    case 14:
                        debug("検査項目名", val);
                        item.setItemName(val);      // 検査項目名
                        break;

                    case 15:
                        debug("異常区分", val);
                        item.setAbnormalFlg(val);   // 異常区分
                        break;

                    case 16:
                        debug("基準値", val);
                        item.setNormalValue(val);   // 基準値
                        break;

                    case 17:
                        debug("検査結果", val);
                        item.setValue(val);         // 検査結果
                        break;

                    case 18:
                        debug("単位", val);
                        item.setUnit(val);          // 単位
                        break;

                    case 19:
                        debug("検体材料コード", val);
                        item.setSpecimenCode(val);   // 検体材料コード
                        break;

                    case 20:
                        debug("検体材料名称", val);
                        item.setSpecimenName(val);   // 検体材料名称
                        break;

                    case 21:
                        debug("コメントコード1", val);
                        item.setCommentCode1(val);  // コメントコード1
                        break;

                    case 22:
                        debug("コメント1", val);
                        item.setComment1(val);      // コメント1
                        break;

                    case 23:
                        debug("コメントコード2", val);
                        item.setCommentCode2(val);  // コメントコード2
                        break;

                    case 24:
                        debug("コメント2", val);
                        item.setComment2(val);     // コメント2
                        break;
                }

                index++;
            }

            // 検査結果値がない場合
            if (item.getValue() == null || item.getValue().equals("")) {

                String resultValue = MIHOKOKU;
                
                // 三菱化学メディエンスの場合
                if (lboCode.equals("M")) {
                    resultValue = NO_RESULT;
                }

                item.setValue(resultValue);
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

    private void debug(String item, String value) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append(item);
            sb.append("=");
            sb.append(value);
            logger.debug(sb.toString());
        }
    }
}
