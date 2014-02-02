package open.dolphin.impl.labrcv;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import open.dolphin.client.ClientContext;
import open.dolphin.client.LabResultParser;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;

/**
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class WolfParser implements LabResultParser {

    private static final String WOLF = "WOLF";

    private String encoding = "SHIFT-JIS";

    private boolean DEBUG=false;

    public WolfParser() {
    }
    
    /**
     * 入力ストリームの検査結果をパースする。
     */
    @Override
    public List<NLaboImportSummary> parse(Path labFile) throws IOException {

        String line;
        String curKey = null;
        NLaboModule curModule = null;
        List<NLaboModule> allModules = new ArrayList<NLaboModule>();
        List<NLaboImportSummary> retList = new ArrayList<NLaboImportSummary>();

        SimpleDateFormat defaultDF = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        SimpleDateFormat df8 = new SimpleDateFormat(DATE_FORMAT_8);

        BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                        Files.newInputStream(labFile), getEncoding()));

        int number=-1;
        while ((line = reader.readLine()) != null) {

            number++;

            line=line.replaceAll("\"", "");
            //System.err.println(number + "=" + line);
            if (line.equals("") || line.length()==0) {
                continue;
            }

            String[] data = line.split(CSV_DELIM);    // CSV
            if (data.length==0) {
                continue;
            }
            //System.err.println(number + "=" + data.length);

            String labCode = data[0];
            String sampleDate = data[1];
            String orderId = data[2];
            String patientId = data[3];
            String karteNo = data[4];
            String patientName = data[5];   // HALF
            String birthday = data[6];
            String patientSex = data[7];

            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("------------------------------------------").append("\n");
                sb.append(labCode).append(" ");
                sb.append(sampleDate).append(" ");
                sb.append(orderId).append(" ");
                sb.append(patientId).append(" ");
                sb.append(karteNo).append(" ");
                sb.append(patientName).append(" ");
                sb.append(birthday).append(" ");
                sb.append(patientSex);
                System.err.println(sb.toString());
            }

            Date date;

            // YYYYMMDD
            try {
                date = df8.parse(sampleDate);
                sampleDate = defaultDF.format(date);

            } catch (Exception e) {
                ClientContext.getLaboTestLogger().warn(e);
                throw new IOException(e.getMessage());
            }

            // key for the parse line
            // spect=sampleDate+orderId  dolphin=pid+sampleDate+labId
            StringBuilder key = new StringBuilder();
            key.append(sampleDate);
            key.append(orderId);
            String testKey = key.toString();

            if (!testKey.equals(curKey)) {
                curKey = testKey;
                // 新しい current module を生成する
                curModule = new NLaboModule();
                curModule.setLaboCenterCode(labCode);
                if (patientId!=null && karteNo!=null) {
                    if ((!patientId.equals("")) && (!karteNo.equals(""))) {
                        curModule.setPatientId(patientId);
                    }
                }
                if (curModule.getPatientId()==null) {
                    curModule.setPatientId(karteNo);
                }
                curModule.setPatientName(patientName);
                curModule.setPatientSex(patientSex);
                curModule.setSampleDate(sampleDate);
                curModule.setReportFormat(WOLF);
                curModule.setModuleKey(curKey);
                allModules.add(curModule);
            }

            NLaboItem item = new NLaboItem();

            //item.setPatientId(patientId);   // カルテ番号
            item.setPatientId(curModule.getPatientId());   // カルテ番号
            item.setSampleDate(sampleDate); // 検体採取日

            int index = 8;
            String resultType = null;
            StringBuilder sortKey = new StringBuilder();
            StringBuilder comment1 = new StringBuilder();
            StringBuilder comment2 = new StringBuilder();

            while (index < data.length) {

                String val = data[index];

                if (val != null) {
                    val = val.trim();
                }

                if (val == null || val.equals("")) {
                    index++;
                    continue;
                }

                //----------------------------------------------------
                // 患者ID fid:Pid
                // ラボコード
                // 患者氏名
                // 患者性別
                // 検体採取日または検査受付日時
                // この検査モジュールに含まれている検査項目の数
                // module key = 患者ID+検体採取日+ラボコード
                // -- module-key を追加
                // -- report_format を追加
                //-----------------------------------------------------
                // 乳ビ                 item.setLipemia(val)
                // 溶血                 item.setHemolysis(val)
                // 透析                 item.setDialysis(val)        ○
                // 報告状況              item.setReportStatus(val)
                // グループコード        item.setGroupCode(val)
                // グループ名称          item.setGroupName(val)
                // 検査項目コード・親     item.setParentCode(val)
                // 検査項目コード        item.setItemCode(val)        ○
                // MEDIS コード         item.setMedisCode(val)
                // 検査項目名            item.setItemName(val)       ○
                // 異常区分             item.setAbnormalFlg(val)     ○（異常値区分）
                // 基準値               item.setNormalValue(val)     ○（表示基準値）
                // 検査結果             item.setValue(val)　　　　　　 ○ +（結果値形態）
                // 単位                 item.setUnit(val)　　　　　　 ○
                // 検体材料コード        item.setSpecimenCode(val)
                // 検体材料名称          item.setSpecimenName(val)
                // コメントコード1       item.setCommentCode1(val)
                // コメント1            item.setComment1(val)
                // コメントコード2       item.setCommentCode2(val)
                // コメント2            item.setComment2(val)
                //------------------------------------------------------
                // sort key = グループコード+検査項目コード・親+検査項目コード
                //            MEDIS分析物コード+材料コード+負荷情報
                //------------------------------------------------------
                // -- sort-key を追加
                // -- 食事区分,妊娠週数,入外区分,項目区分,再検区分,正常値区分,正常値1,正常値2
                // -- フリーコメント,補助コメント1,補助コメント2,基準値コメント --> 集約

                switch (index) {

                    case 8:
                        // 1:透析前 2:透析後
                        debug("透析区分", val);
                        item.setDialysis(val);
                        break;

                    case 9:
                        debug("食事区分", val);
                        break;

                    case 10:
                        debug("妊娠週数", val);
                        break;

                    case 11:
                        // yyyymmdd
                        debug("採取日", val);
                        break;

                    case 12:
                        // 1:入院 2:外来
                        debug("入外区分", val);
                        break;

                    case 13:
                        debug("結果フリーコメント", val);
                        comment1.append(val);
                        break;

                    case 14:
                        debug("検査項目コード", val);
                        item.setItemCode(val);
                        break;

                    case 15:
                        debug("検査項目名称", val);
                        item.setItemName(val);
                        break;

                    case 16:
                        // 1:単項目 2:分画親項目 3:分画子項目
                        debug("項目区分", val);
                        break;

                    case 17:
                        // ソートのみに利用する sort=MEDIS分析物コード(5桁) + 材料コード(3桁) + 負荷情報(3桁)
                        debug("MEDIS 分析物コート", val);
                        sortKey.append(val);
                        break;

                    case 18:
                        // ソートのみに利用する sort=MEDIS分析物コード(5桁) + 材料コード(3桁) + 負荷情報(3桁)
                        debug("材料コート", val);
                        sortKey.append(val);
                        break;

                    case 19:
                        // WOLF 未使用
                        debug("材料名", val);
                        break;

                    case 20:
                        // ソートのみに利用する sort=MEDIS分析物コード(5桁) + 材料コード(3桁) + 負荷情報(3桁)
                        debug("負荷情報", val);
                        sortKey.append(val);
                        break;

                    case 21:
                        // 0:初回検査 1:再検中 2:再検済み 3:再々検済み
                        debug("再検区分", val);
                        break;

                    case 22:
                        // H:高値 L:低値 !:異常値
                        debug("異常値区分", val);
                        item.setAbnormalFlg(val);
                        break;

                    case 23:
                        // E:以下 L:未満 U:以上
                        debug("結果値形態", val);
                        resultType = val;
                        break;

                    case 24:
                        debug("検査結果", val);
                        item.setValue(val);
                        break;

                    case 25:
                        debug("単位", val);
                        item.setUnit(val);
                        break;

                    case 26:
                        debug("表示基準値", val);
                        item.setNormalValue(val);
                        break;

                    case 27:
                        // -:範囲 E:以下 L:未満 U:以上
                        debug("正常値区分", val);
                        break;

                    case 28:
                        debug("正常値1", val);
                        break;

                    case 29:
                        debug("正常値2", val);
                        break;

                    case 30:
                        // WOLF 未使用
                        debug("結果補助コメント1コート", val);
                        break;

                    case 31:
                        debug("結果補助コメント1内容", val);
                        comment2.append(val);
                        break;

                    case 32:
                        // WOLF 未使用
                        debug("結果補助コメント2コート", val);
                        break;

                    case 33:
                        debug("結果補助コメント2内容", val);
                        if (comment2.length()>0) {
                            comment2.append(":");
                        }
                        comment2.append(val);
                        break;

                    case 34:
                        // WOLF 未使用
                        debug("基準値コメントコート", val);
                        break;

                    case 35:
                        debug("基準値コメント内容", val);
                        if (comment2.length()>0) {
                            comment2.append(":");
                        }
                        comment2.append(val);
                        break;
                }

                index++;
            }

            // sort-key
            if (sortKey.length()>0) {
                item.setSortKey(sortKey.toString());
            }

            // 検査結果値を表現する
            if (item.getValue() == null || item.getValue().equals("")) {
                String resultValue = NO_RESULT;
                item.setValue(resultValue);

            } else {
                if (resultType!=null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(item.getValue()).append(" ").append(resultType);
                    item.setValue(sb.toString());
                }
            }

            // cmmonet1
            if (comment1.length()>0) {
                item.setComment1(comment1.toString());
            }

            // cmmonet2
            if (comment2.length()>0) {
                item.setComment1(comment2.toString());
            }

            // GropuCode, GroupName,parentCode
            item.setGroupCode(curModule.getLaboCenterCode());
            item.setGroupName(curModule.getLaboCenterCode());
            item.setParentCode(item.getItemCode());

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
            System.err.println(sb.toString());
        }
    }
}
