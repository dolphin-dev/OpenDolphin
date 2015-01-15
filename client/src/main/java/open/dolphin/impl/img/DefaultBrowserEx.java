package open.dolphin.impl.img;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.TableColumn;
import open.dolphin.client.Chart;
import open.dolphin.utilities.utility.OtherProcessLink;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.client.ImageEntry;
import open.dolphin.impl.server.PVTKanaToAscii;
import open.dolphin.impl.server.PVTReceptionLink;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.project.Project;
import open.dolphin.util.Log;

/**
 * 他プロセス連携
 * @author Life Sciences Computing Corporation.
 */
public class DefaultBrowserEx extends AbstractBrowser {

    private static final String TITLE = "PDF・画像";
    private static final String SETTING_FILE_NAME = "defaultex.properties";
    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String KEY_UPPER_FOLDER = "%UPPERFOLDER%";
    private static final String KEY_PATIENT_ID = "%PATIENTID%";
    private static final String KEY_PATIENT_ID_0SUP = "%PATIENTID0SUP%";
    private static final String KEY_STUDY_DATE = "%STUDYDATE%";
    private static final String KEY_TODAY = "%TODAY%";
    private static final String KEY_PATIENT_KANJI = "%PATIENTKANJI%";               // 患者氏名(漢字)
    private static final String KEY_PATIENT_KANA = "%PATIENTKANA%";                 // 患者氏名(半角ｶﾅ)
    private static final String KEY_PATIENT_ZENKAKUKANA = "%PATIENTZENKAKUKANA%";   // 患者氏名(全角カナ)
    private static final String KEY_PATIENT_ASCII = "%PATIENTASCII%";               // 患者氏名(ASCII)
    private static final String KEY_PATIENT_KANJITRIM = "%PATIENTKANJITRIM%";               // 患者氏名(漢字)空白削除
    private static final String KEY_PATIENT_KANATRIM = "%PATIENTKANATRIM%";                 // 患者氏名(半角ｶﾅ)空白削除
    private static final String KEY_PATIENT_ZENKAKUKANATRIM = "%PATIENTZENKAKUKANATRIM%";   // 患者氏名(全角カナ)空白削除
    private static final String KEY_PATIENT_ASCIITRIM = "%PATIENTASCIITRIM%";               // 患者氏名(ASCII)空白削除
    private static final String KEY_PATIENT_SEX = "%PATIENTSEX%";                   // 患者性別
    private static final String KEY_PATIENT_BIRTH = "%PATIENTBIRTH%";               // 患者誕生日
    private static final String KEY_PVTDATE = "%PVTDATE%";                          // 受付日時
    private static final String KEY_PATIENT_ZIPCODE = "%PATIENTZIPCODE%";           // 患者郵便番号
    private static final String KEY_PATIENT_ADDRESS = "%PATIENTADDRESS%";           // 患者住所
    private static final String KEY_PATIENT_PHONE = "%PATIENTPHONE%";               // 患者電話番号
    private static final String KEY_PATIENT_TELEPHONE = "%PATIENTTELEPHONE%";       // 患者電話
    private static final String KEY_PATIENT_MOBILEPHONE = "%PATIENTMOBILEPHONE%";   // 患者携帯電話
//s.oh^ 2014/08/01 受付連携
    private static final String KEY_DEPT_CODE = "%DEPTCODE%";                       // 診療科コード
    private static final String KEY_DEPT_NAME = "%DEPTNAME%";                       // 診療科
    private static final String KEY_ATTENDING_KANJI = "%ATTENDINGKANJI%";           // 担当医(漢字)
    private static final String KEY_ATTENDING_ID = "%ATTENDINGID%";                 // 担当医(ID)
    private static final String KEY_INSURANCE_FIRST = "%INSURANCEFIRST%";           // 保険
//s.oh$
    private static final String KEY_USER_ID = "%USERID%";                           // ログインID
    private static final String KEY_USER_PASS = "%USERPASS%";                       // ログインパスワード
    private static final String TAG_EXE = "[EXE]";
    private static final String TAG_URL = "[URL]";
    private static final String TAG_FILE = "[FILE]";
    private static final String TAG_LINK = "[LINK]";
    private static final String TAG_TCP_EXE = "[TCP:EXE]";
    private static final String TAG_TCP_FILE = "[TCP:FILE]";
    private static final String TAG_ORDER_FILE = "[ORDER:FILE]";
    
    private static final String[][] KANA = { {"ア", "ｱ"},  {"イ", "ｲ"},  {"ウ", "ｳ"},  {"エ", "ｴ"},  {"オ", "ｵ"},
                                             {"カ", "ｶ"},  {"キ", "ｷ"},  {"ク", "ｸ"},  {"ケ", "ｹ"},  {"コ", "ｺ"},
                                             {"サ", "ｻ"},  {"シ", "ｼ"},  {"ス", "ｽ"},  {"セ", "ｾ"},  {"ソ", "ｿ"},
                                             {"タ", "ﾀ"},  {"チ", "ﾁ"},  {"ツ", "ﾂ"},  {"テ", "ﾃ"},  {"ト", "ﾄ"},
                                             {"ナ", "ﾅ"},  {"ニ", "ﾆ"},  {"ヌ", "ﾇ"},  {"ネ", "ﾈ"},  {"ノ", "ﾉ"},
                                             {"ハ", "ﾊ"},  {"ヒ", "ﾋ"},  {"フ", "ﾌ"},  {"ヘ", "ﾍ"},  {"ホ", "ﾎ"},
                                             {"マ", "ﾏ"},  {"ミ", "ﾐ"},  {"ム", "ﾑ"},  {"メ", "ﾒ"},  {"モ", "ﾓ"},
                                             {"ヤ", "ﾔ"},  {"ユ", "ﾕ"},  {"ヨ", "ﾖ"},
                                             {"ラ", "ﾗ"},  {"リ", "ﾘ"},  {"ル", "ﾙ"},  {"レ", "ﾚ"},  {"ロ", "ﾛ"},
                                             {"ワ", "ﾜ"},  {"ヲ", "ｦ"},  {"ン", "ﾝ"},
                                             {"ァ", "ｧ"},  {"ィ", "ｨ"},  {"ゥ", "ｩ"},  {"ェ", "ｪ"},  {"ォ", "ｫ"},
                                             {"ッ", "ｯ"},
                                             {"ャ", "ｬ"},  {"ュ", "ｭ"},  {"ョ", "ｮ"},
                                             {"ガ", "ｶﾞ"}, {"ギ", "ｷﾞ"}, {"グ", "ｸﾞ"}, {"ゲ", "ｹﾞ"}, {"ゴ", "ｺﾞ"},
                                             {"ザ", "ｻﾞ"}, {"ジ", "ｼﾞ"}, {"ズ", "ｽﾞ"}, {"ゼ", "ｾﾞ"}, {"ゾ", "ｿﾞ"},
                                             {"ダ", "ﾀﾞ"}, {"ヂ", "ﾁﾞ"}, {"ヅ", "ﾂﾞ"}, {"デ", "ﾃﾞ"}, {"ド", "ﾄﾞ"},
                                             {"バ", "ﾊﾞ"}, {"ビ", "ﾋﾞ"}, {"ブ", "ﾌﾞ"}, {"ベ", "ﾍﾞ"}, {"ボ", "ﾎﾞ"},
                                             {"パ", "ﾊﾟ"}, {"ピ", "ﾋﾟ"}, {"プ", "ﾌﾟ"}, {"ペ", "ﾍﾟ"}, {"ポ", "ﾎﾟ"},
                                             {"　", " "},  {"ー", "-"},
                                             {"０", "0"},  {"１", "1"},  {"２", "2"},  {"３", "3"},  {"４", "4"},  {"５", "5"}, {"６", "6"}, {"７", "7"}, {"８", "8"}, {"９", "9"},
                                             {"Ａ", "A"},  {"Ｂ", "B"},  {"Ｃ", "C"},  {"Ｄ", "D"},  {"Ｅ", "E"},  {"Ｆ", "F"}, {"Ｇ", "G"}, {"Ｈ", "H"}, {"Ｉ", "I"}, {"Ｊ", "J"},
                                             {"Ｋ", "K"},  {"Ｌ", "L"},  {"Ｍ", "M"},  {"Ｎ", "N"},  {"Ｏ", "O"},  {"Ｐ", "P"}, {"Ｑ", "Q"}, {"Ｒ", "R"}, {"Ｓ", "S"}, {"Ｔ", "T"},
                                             {"Ｕ", "U"},  {"Ｖ", "V"},  {"Ｗ", "W"},  {"Ｘ", "X"},  {"Ｙ", "Y"},  {"Ｚ", "Z"},
                                             {"ａ", "a"},  {"ｂ", "b"},  {"ｃ", "c"},  {"ｄ", "d"},  {"ｅ", "e"},  {"ｆ", "f"}, {"ｇ", "g"}, {"ｈ", "h"}, {"ｉ", "i"}, {"ｊ", "j"},
                                             {"ｋ", "k"},  {"ｌ", "l"},  {"ｍ", "m"},  {"ｎ", "n"},  {"ｏ", "o"},  {"ｐ", "p"}, {"ｑ", "q"}, {"ｒ", "r"}, {"ｓ", "s"}, {"ｔ", "t"},
                                             {"ｕ", "u"},  {"ｖ", "v"},  {"ｗ", "w"},  {"ｘ", "x"},  {"ｙ", "y"},  {"ｚ", "z"} };
    private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
    private static final String DEFAULT_CSV_ENCODING = "Shift_JIS";

    private ImageTableRenderer imageRenderer;
    private int cellWidth = MAX_IMAGE_SIZE + CELL_WIDTH_MARGIN;
    private int cellHeight = MAX_IMAGE_SIZE + CELL_HEIGHT_MARGIN;

    private DefaultBrowserViewEx view;
    private String otherProcess1;
    private String otherProcess2;
    private String otherProcess3;
  
    public DefaultBrowserEx() {
        
        String title = Project.getString("defaultex.title.name");
        if(valueIsNullOrEmpty(title)) {
            setTitle(TITLE);
        }else{
            setTitle(title);
        }
        
        properties = getProperties();
        
        otherProcess1 = Project.getString("otherprocess1.link.name");
        otherProcess2 = Project.getString("otherprocess2.link.name");
        otherProcess3 = Project.getString("otherprocess3.link.name");

        // Convert the old properties
        Properties old = Project.loadPropertiesAsObject("imageBrowserProp2.xml");
        if (old!=null) {
            Enumeration e = old.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String)e.nextElement();
                String val = old.getProperty(key);
                properties.setProperty(key, val);
            }
            Project.storeProperties(properties, SETTING_FILE_NAME);
            Project.deleteSettingFile("imageBrowserProp2.xml");
            
        } else {
            Project.loadProperties(properties, SETTING_FILE_NAME);
        }

        // Base directory
        String value = properties.getProperty(PROP_BASE_DIR);
        imageBase = valueIsNotNullNorEmpty(value) ? value : null;
    }
      
    @Override
    protected String getImgLocation() {
        
        if (getContext()==null) {
            view.getDirLbl().setText("");
            return null;
        }

        if (valueIsNullOrEmpty(getImageBase())) {
            view.getDirLbl().setText("画像ディレクトリが指定されていません。");
            return null;
        }

        String pid = getContext().getPatient().getPatientId();
        StringBuilder sb = new StringBuilder();
        sb.append(getImageBase());
        if (!getImageBase().endsWith(File.separator)) {
            sb.append(File.separator);
        }

        sb.append(pid);
        String loc = sb.toString();
        nowLocation = loc;
        view.getDirLbl().setText(createLocationText(nowLocation));

        return loc;
    }
    
    private String createLocationText(String loc) {
        StringBuilder sb = new StringBuilder();
        if (loc.length() > 33) {
            sb.append(loc.substring(0, 15));
            sb.append("...");
            int pos = loc.length() - 15;
            sb.append(loc.substring(pos));
        } else {
            return loc;
        }
        
        return sb.toString();
    }

    private ActionMap getActionMap(ResourceBundle resource) {

        ActionMap ret = new ActionMap();

//minagawa^ Icon Server        
        //ImageIcon icon = ClientContext.getImageIcon("ref_16.gif");
        ImageIcon icon = ClientContext.getImageIconArias("icon_refresh_small");
//minagawa$        
        AbstractAction refresh = new AbstractAction("更新",icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {
//s.oh^ 2014/05/07 PDF・画像タブの改善
                if(isScanning(getContext().getFrame(), "更新できません。")) return;
//s.oh$
                scan(getImgLocation());
                nowLocation = getImgLocation();
                view.getDirLbl().setText(createLocationText(nowLocation));
            }
        };
        ret.put("refresh", refresh);

//minagawa^ Icon Server        
        //icon = ClientContext.getImageIcon("confg_16.gif");
        icon = ClientContext.getImageIconArias("icon_setting_small");
//minagawa$        
        AbstractAction doSetting = new AbstractAction("設定",icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {
//s.oh^ 2014/05/07 PDF・画像タブの改善
                if(isScanning(getContext().getFrame(), "設定を変更できません。")) return;
//s.oh$

                // 現在のパラメータを保存し、Setting dialog を開始する
                int oldCount = columnCount();
                boolean oldShow = showFilename();
                boolean oldDisplayIsFilename = displayIsFilename();
                boolean oldSortIsLastModified = sortIsLastModified();
                boolean oldSortIsDescending = sortIsDescending();
                String oldBase = properties.getProperty(PROP_BASE_DIR);
                oldBase = valueIsNotNullNorEmpty(oldBase) ? oldBase : "";

                // 設定ダイアログを起動する
                DefaultSettingEx setting = new DefaultSettingEx(DefaultBrowserEx.this, getUI());
                setting.start();

                // 結果は properties にセットされて返ってくるので save する
                Project.storeProperties(properties, SETTING_FILE_NAME);

                // 新たに設定された値を読む
                int newCount = columnCount();
                boolean newShow = showFilename();
                boolean newDisplayIsFilename = displayIsFilename();
                boolean newSortIsLastModified = sortIsLastModified();
                boolean newSortIsDescending = sortIsDescending();
                String newBase = properties.getProperty(PROP_BASE_DIR);
                newBase = valueIsNotNullNorEmpty(newBase) ? newBase : "";

                // 更新ボタンの enabled
                boolean canRefresh = true;
                canRefresh = canRefresh && (!newBase.equals(""));
                view.getRefreshBtn().setEnabled(canRefresh);

                boolean needsRefresh = false;

                // カラム数変更
                if (newCount != oldCount) {
                    needsRefresh = true;
                    tableModel = new ImageTableModel(null, newCount);
                    table.setModel(tableModel);
                    TableColumn column;
                    for (int i = 0; i < newCount; i++) {
                        column = table.getColumnModel().getColumn(i);
                        column.setPreferredWidth(cellWidth);
                    }
                    table.setRowHeight(cellHeight);
                }

                needsRefresh = (needsRefresh ||
                                (newShow!=oldShow) ||
                                (newDisplayIsFilename!=oldDisplayIsFilename) ||
                                (newSortIsLastModified!=oldSortIsLastModified) ||
                                (newSortIsDescending!=oldSortIsDescending));

                // ベースディレクトリ
                if (!newBase.equals(oldBase)) {
                    setImageBase(newBase);
                } else if (needsRefresh) {
                    scan(getImgLocation());
                    nowLocation = getImgLocation();
                    view.getDirLbl().setText(createLocationText(nowLocation));
                }
            }
        };
        ret.put("doSetting", doSetting);
         
        if(otherProcess1 != null && otherProcess1.length() > 0) {
            AbstractAction process1 = new AbstractAction(otherProcess1) {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    linkOtherProcess1();
                }
            };
            ret.put("process1", process1);
        }

        if(otherProcess2 != null && otherProcess2.length() > 0) {
            AbstractAction process2 = new AbstractAction(otherProcess2) {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    linkOtherProcess2();
                }
            };
            ret.put("process2", process2);
        }

        if(otherProcess3 != null && otherProcess3.length() > 0) {
            AbstractAction process3 = new AbstractAction(otherProcess3) {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    linkOtherProcess3();
                }
            };
            ret.put("process3", process3);
        }
        
//s.oh^ 2014/05/07 PDF・画像タブの改善
        icon = ClientContext.getImageIconArias("icon_delete");
        AbstractAction delete = new AbstractAction("削除", icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(isScanning(getContext().getFrame(), "ファイル/フォルダを削除できません。")) return;
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if(row < 0 || col < 0) return;
                ImageEntry entry = getEntryAt(row, col);
                if(entry == null) return;
                //String yes = "はい";
                //String no = "いいえ";
                //Object[] options = new Object[]{yes, no};
                //if (ClientContext.isMac()) {
                //    options = new Object[]{no, yes};
                //}else{
                //    options = new Object[]{yes, no};
                //}
                String msg = "「" + entry.getPath() + "」を削除しますか？";
                //int select = JOptionPane.showOptionDialog(getContext().getFrame(), msg, "削除", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, no);
                int option = JOptionPane.showConfirmDialog(getContext().getFrame(), msg, "削除", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(option == JOptionPane.OK_OPTION) {
                    delete(new File(entry.getPath()));
                    if(tableModel != null && tableModel.getImageList() != null) {
                        tableModel.getImageList().remove(entry);
                    }
                    ArrayList<ImageEntry> imageList = new ArrayList<ImageEntry>();
                    for(Object obj : tableModel.getImageList()) {
                        imageList.add(((ImageEntry)obj).copy());
                    }
                    tableModel.setImageList(imageList);
                    //scan(nowLocation);
                }
            }
        };
        ret.put("delete", delete);
        
        AbstractAction rename = new AbstractAction("名前の変更") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(isScanning(getContext().getFrame(), "名前の変更はできません。")) return;
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if(row < 0 || col < 0) return;
                ImageEntry entry = getEntryAt(row, col);
                if(entry == null) return;
                String newName = JOptionPane.showInputDialog(getContext().getFrame(), "名前を入力してください。", entry.getFileName());
                ImageEntry newEntry = rename(newName, entry);
                if(newEntry != null) {
                    ArrayList<ImageEntry> imageList = new ArrayList<ImageEntry>();
                    for(Object obj : tableModel.getImageList()) {
                        if(((ImageEntry)obj).getFileName().equals(entry.getFileName())) {
                            imageList.add(newEntry);
                        }else{
                            imageList.add(((ImageEntry)obj).copy());
                        }
                    }
                    tableModel.setImageList(imageList);
                }
            }
        };
        ret.put("rename", rename);
        
        AbstractAction newdir = new AbstractAction("新規") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(isScanning(getContext().getFrame(), "フォルダを作成できません。")) return;
//s.oh^ 2014/07/29 PDF・画像タブの改善
                String name = JOptionPane.showInputDialog(getContext().getFrame(), "フォルダ名を入力してください。", checkDirName(nowLocation, "新規フォルダ"));
//s.oh$
                newdir(name);
            }
        };
        ret.put("newdir", newdir);
//s.oh$
        
//s.oh^ 2014/05/30 PDF・画像タブの改善
        AbstractAction backdir = new AbstractAction("戻る") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                backdir();
            }
        };
        ret.put("backdir", backdir);
//s.oh$

        return ret;
    }
    
//s.oh^ 2014/05/07 PDF・画像タブの改善
    private void delete(File file) {
        if(!file.exists()) {
            return;
        }
        if(file.isFile()) {
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, file.getPath());
            file.delete();
        }else if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(int i = 0; i < files.length; i++) {
                delete(files[i]);
            }
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, file.getPath());
            file.delete();
        }
    }
    
    private ImageEntry rename(String name, ImageEntry entry) {
        if(name != null && name.length() >= 0 && entry != null) {
            File newFile = new File(nowLocation, name);
            if(newFile.exists()) {
                String name2 = JOptionPane.showInputDialog(getContext().getFrame(), "同じ名前が既に存在します。別の名前を入力してください。", name);
                return rename(name2, entry);
            }else if(name.startsWith("__")) {
                String name2 = JOptionPane.showInputDialog(getContext().getFrame(), "__から始まる名前を使用できません。別の名前を入力してください。", name);
                return rename(name2, entry);
            }else{
                ImageEntry newEntry = entry.copy();
                try {
                    entry.setUrl(newFile.toURI().toURL().toString());
                } catch (MalformedURLException ex) {
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_ERROR, "ImageEntryのURL作成失敗", ex.getMessage());
                }
                newEntry.setFileName(name);
                newEntry.setPath(newFile.getPath());
                File oldFile = new File(nowLocation, entry.getFileName());
                oldFile.renameTo(newFile);
                return newEntry;
            }
        }
        return null;
    }
    
    private void newdir(String name) {
        if(name != null && name.length() >= 0) {
            File dir = new File(nowLocation);
            if(!dir.exists()) {
                dir.mkdirs();
            }else if((dir.exists()) && (!dir.isDirectory())) {
                return;
            }
            File[] files = dir.listFiles();
            if(files == null) return;
            for(File file : files) {
                if(file.getName().equals(name)) {
                    String name2 = JOptionPane.showInputDialog(getContext().getFrame(), "同じ名前が既に存在します。別の名前を入力してください。", name);
                    newdir(name2);
                    return;
                }
            }
            name = nowLocation + File.separator + name;
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, name);
            File folder = new File(name);
            folder.mkdir();
            
            ImageEntry entry = new ImageEntry();
            try {
                entry.setUrl(folder.toURI().toURL().toString());
            } catch (MalformedURLException ex) {
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_ERROR, "ImageEntryのURL作成失敗", ex.getMessage());
            }
            entry.setPath(folder.getPath());
            entry.setFileName(folder.getName());
            entry.setLastModified(folder.lastModified());
            entry.setImageIcon(ClientContext.getImageIconArias("icon_foldr"));
            entry.setDirectrory(true);
            ArrayList<ImageEntry> imageList = new ArrayList<ImageEntry>();
            imageList.add(entry);
            if(tableModel.getImageList() != null) {
                for(Object obj : tableModel.getImageList()) {
                    imageList.add(((ImageEntry)obj).copy());
                }
            }
            tableModel.setImageList(imageList);
            //scan(nowLocation);
        }
    }
//s.oh$
    
    private void linkOtherProcess1() {
        otherProcess(Project.getString("otherprocess1.link.path"),
                     Project.getString("otherprocess1.link.param"),
                     null);
    }
    
    private void linkOtherProcess2() {
        otherProcess(Project.getString("otherprocess2.link.path"),
                     Project.getString("otherprocess2.link.param"),
                     null);
    }
    
    private void linkOtherProcess3() {
        otherProcess(Project.getString("otherprocess3.link.path"),
                     Project.getString("otherprocess3.link.param"),
                     null);
    }
    
    private void linkOtherProcessThumbnail(ImageEntry entry) {
        String date = null;
        if(entry.getFileName().length() >= DATE_FORMAT.length()) {
            date = entry.getFileName().substring(0, DATE_FORMAT.length());
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            try {
                sdf.parse(date);
                if(!otherProcess(Project.getString("otherprocess.link.path"),
                                 Project.getString("otherprocess.link.param"),
                                 date)) {
                    openImage(entry);
                }
            } catch (ParseException ex) {
                Logger.getLogger(DefaultBrowserEx.class.getName()).log(Level.SEVERE, null, ex);
                openImage(entry);
            }
        }else{
            openImage(entry);
        }
    }
    
    private boolean otherProcess(String processPath, String processParam, String imageDate) {
//        if(processPath == null || processPath.length() <= 0) return false;
//        
//        OtherProcessLink opl = new OtherProcessLink();
//        
//        if(processPath.startsWith(TAG_URL)) {
//            // URL
//            String url = processPath.substring(processPath.indexOf("]") + 1);
//            DocInfoModel[] selectModel = getContext().getDocumentHistory().getSelectedHistories();
//            String dateSelect = null;
//            String dateToday = null;
//            if(imageDate == null) {
//                if(selectModel != null && selectModel.length == 1) {
//                    dateSelect = ModelUtils.getDateAsFormatString(selectModel[0].getFirstConfirmDate(), DATE_FORMAT);
//                }
//            }else{
//                dateSelect = imageDate;
//            }
//            Date today = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//            dateToday = sdf.format(today);
//            url = url.replaceAll(KEY_PATIENT_ID, getContext().getPatient().getPatientId());
//            url = url.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(getContext().getPatient().getPatientId()));
//            if(dateSelect != null) {
//                url = url.replaceAll(KEY_STUDY_DATE, dateSelect);
//            }else{
//                url = url.replaceAll(KEY_STUDY_DATE, dateToday);
//            }
//            url = url.replaceAll(KEY_TODAY, dateToday);
//            
//            opl.linkURL(url);
//        }else if(processPath.startsWith(TAG_EXE)) {
//            // Exe
//            String path = processPath.substring(processPath.indexOf("]") + 1);
//            String param = processParam;
//            String command = null;
//            if(param != null && param.length() > 0) {
//                DocInfoModel[] selectModel = getContext().getDocumentHistory().getSelectedHistories();
//                String dateSelect = null;
//                String dateToday = null;
//                if(imageDate == null) {
//                    if(selectModel != null && selectModel.length == 1) {
//                        dateSelect = ModelUtils.getDateAsFormatString(selectModel[0].getFirstConfirmDate(), DATE_FORMAT);
//                    }
//                }else{
//                    dateSelect = imageDate;
//                }
//                Date today = new Date();
//                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//                dateToday = sdf.format(today);
//                param = param.replaceAll(KEY_PATIENT_ID, getContext().getPatient().getPatientId());
//                param = param.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(getContext().getPatient().getPatientId()));
//                if(dateSelect != null) {
//                    param = param.replaceAll(KEY_STUDY_DATE, dateSelect);
//                }else{
//                    param = param.replaceAll(KEY_STUDY_DATE, dateToday);
//                }
//                param = param.replaceAll(KEY_TODAY, dateToday);
//                if(path.indexOf(" ") >= 0) {
//                    command = "\"" + path + "\" " + param;
//                }else{
//                    command = path + " " + param;
//                }
//            }else{
//                if(path.indexOf(" ") >= 0) {
//                    command = "\"" + path + "\"";
//                }else{
//                    command = path;
//                }
//            }
//            
//            opl.linkFile(command);
//        }else if(processPath.startsWith(TAG_FILE)) {
//            // File
//            String path = processPath.substring(processPath.indexOf("]") + 1);
//            String param = processParam;
//            if(param != null && param.length() > 0) {
//                DocInfoModel[] selectModel = getContext().getDocumentHistory().getSelectedHistories();
//                String dateSelect = null;
//                String dateToday = null;
//                if(imageDate == null) {
//                    if(selectModel != null && selectModel.length == 1) {
//                        dateSelect = ModelUtils.getDateAsFormatString(selectModel[0].getFirstConfirmDate(), DATE_FORMAT);
//                    }
//                }else{
//                    dateSelect = imageDate;
//                }
//                Date today = new Date();
//                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//                dateToday = sdf.format(today);
//                param = param.replaceAll(KEY_PATIENT_ID, getContext().getPatient().getPatientId());
//                param = param.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(getContext().getPatient().getPatientId()));
//                if(dateSelect != null) {
//                    param = param.replaceAll(KEY_STUDY_DATE, dateSelect);
//                }else{
//                    param = param.replaceAll(KEY_STUDY_DATE, dateToday);
//                }
//                param = param.replaceAll(KEY_TODAY, dateToday);
//            }
//            
//            File file = new File(path);
//            BufferedWriter bw;
//            try {
//                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
//                bw.write(param);
//                bw.newLine();
//                bw.close();
//            } catch (IOException ex) {
//                Logger.getLogger(DefaultBrowserEx.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }else if(processPath.startsWith(TAG_LINK)) {
//            // link
//            // image.browser.name=defaultex
//            // otherprocessN.link.name=name
//            // otherprocessN.link.path=[LINK]
//            // otherprocessN.link.param=1
//            // otherprocess1.link.num=1
//            // otherprocess1.link1.〜
//            String param = processParam;
//            if(param != null && param.length() > 0) {
//                final String KEY_DEF = "otherprocess" + param + ".link";
//                int num = Project.getInt("otherprocess" + param + ".link.num", 1);
//                for(int i = 1; i < num+1; i++) {
//                    PatientModel patientModel = getContext().getPatient();
//                    if(patientModel == null) return false;
//                    PVTKanaToAscii kana2Ascii = new PVTKanaToAscii();
//                    String format = Project.getString(KEY_DEF + String.valueOf(i) + ".format");
//                    if(format == null || format.length() <= 0) return false;
//                    String dir = Project.getString(KEY_DEF + String.valueOf(i) + ".dir");
//                    if(dir == null || dir.length() <= 0) return false;
//                    String birth = (patientModel.getBirthday() != null) ? patientModel.getBirthday().replaceAll("-", "").replaceAll("/", "") : "";
//                    SimpleDateFormat sdf1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
//                    String birthFormat = Project.getString(KEY_DEF + String.valueOf(i) + ".birthformat", DEFAULT_DATE_FORMAT);
//                    SimpleDateFormat sdf2 = new SimpleDateFormat(birthFormat);
//                    Date date = sdf1.parse(birth, new ParsePosition(0));
//                    String sexFormats = Project.getString(KEY_DEF + String.valueOf(i) + ".malefemale", "m,f");
//                    String[] sexFormat = sexFormats.split(",");
//                    String sex = (patientModel.getGender() != null) ? (patientModel.getGender().toLowerCase().startsWith("m") ? sexFormat[0] : sexFormat[1]) : "";
//                    String encoding = Project.getString(KEY_DEF + String.valueOf(i) + ".encoding", DEFAULT_CSV_ENCODING);
//                    format = format.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
//                    format = format.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
//                    format = format.replaceAll(KEY_PATIENT_KANJI, patientModel.getFullName());
//                    format = format.replaceAll(KEY_PATIENT_KANA, fullKanaToHalfKana(patientModel.getKanaName()));
//                    format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, patientModel.getKanaName());
//                    format = format.replaceAll(KEY_PATIENT_ASCII, kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), ""));
//                    format = format.replaceAll(KEY_PATIENT_SEX, sex);
//                    format = format.replaceAll(KEY_PATIENT_BIRTH, sdf2.format(date));
//                    if(patientModel.getAddress() != null) {
//                        if(patientModel.getAddress().getZipCode() != null) {
//                            format = format.replaceAll(KEY_PATIENT_ZIPCODE, patientModel.getAddress().getZipCode());
//                        }else{
//                            format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
//                        }
//                        if(patientModel.getAddress().getAddress() != null) {
//                            format = format.replaceAll(KEY_PATIENT_ADDRESS, patientModel.getAddress().getAddress());
//                        }else{
//                            format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
//                        }
//                    }else{
//                        format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
//                        format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
//                    }
//                    if(patientModel.getTelephone() != null) {
//                        format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getTelephone());
//                    }else if(patientModel.getMobilePhone() != null) {
//                        format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getMobilePhone());
//                    }else{
//                        format = format.replaceAll(KEY_PATIENT_PHONE, "");
//                    }
//                    if(patientModel.getTelephone() != null) {
//                        format = format.replaceAll(KEY_PATIENT_TELEPHONE, patientModel.getTelephone());
//                    }else{
//                        format = format.replaceAll(KEY_PATIENT_TELEPHONE, "");
//                    }
//                    if(patientModel.getMobilePhone() != null) {
//                        format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, patientModel.getMobilePhone());
//                    }else{
//                        format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, "");
//                    }
//                    String file = Project.getString(KEY_DEF + String.valueOf(i) + ".filename", "ID_%PATIENTID%");
//                    String ext = Project.getString(KEY_DEF + String.valueOf(i) + ".ext", ".txt");
//                    String fDateFormat = Project.getString(KEY_DEF + String.valueOf(i) + ".filename.dateformat", DEFAULT_DATE_FORMAT);
//                    SimpleDateFormat sdf3 = new SimpleDateFormat(fDateFormat);
//                    Date today = new Date();
//                    file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
//                    file = file.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
//                    file = file.replaceAll(KEY_TODAY, sdf3.format(today));
//                    if(processPath.indexOf("[TCP]") >= TAG_LINK.length()) {
//                        String path = processPath.substring(processPath.lastIndexOf("]") + 1) + ext;
//                        String[] data = path.split(",");
//                        if(data.length < 3) return false;
//                        opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
//                    }else if(processPath.indexOf("[EXE]") >= TAG_LINK.length()) {
//                        String path = processPath.substring(processPath.lastIndexOf("]") + 1);
//                        String command = null;
//                        if(path.indexOf(" ") >= 0) {
//                            command = "\"" + path + "\" " + param;
//                        }else{
//                            command = path + " " + format;
//                        }
//
//                        opl.linkFile(command);
//                    }else{
//                        String path = dir + File.separator + file;
//                        try {
//                            if(Project.getBoolean(KEY_DEF + String.valueOf(i) + ".rename", false)) {
//                                File tmp = new File(path);
//                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
//                                bw.write(format);
//                                bw.newLine();
//                                bw.close();
//                                File csv = new File(path + ext);
//                                tmp.renameTo(csv);
//                            }else{
//                                File tmp = new File(path + ext);
//                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
//                                bw.write(format);
//                                bw.newLine();
//                                bw.close();
//                            }
//                        } catch (IOException ex) {
//                            Logger.getLogger(DefaultBrowserEx.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//                        }
//                    }
//                }
//            }
//        }else if(processPath.startsWith(TAG_TCP_EXE)) {
//            // TCP/IP:Exe
//            String tcp = processPath.substring(processPath.indexOf("]") + 1);
//            String[] data = tcp.split(",");
//            if(data.length < 2) return false;
//            String val = processParam;
//            val = val.replaceAll(KEY_PATIENT_ID, getContext().getPatient().getPatientId());
//            val = val.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(getContext().getPatient().getPatientId()));
//            opl.linkTCPToExe(val, data[0], Integer.valueOf(data[1]));
//        }else if(processPath.startsWith(TAG_TCP_FILE)) {
//            // TCP/IP:File
//            String tcp = processPath.substring(processPath.indexOf("]") + 1);
//            String[] data = tcp.split(",");
//            if(data.length < 3) return false;
//            String val = processParam;
//            val = val.replaceAll(KEY_PATIENT_ID, getContext().getPatient().getPatientId());
//            val = val.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(getContext().getPatient().getPatientId()));
//            opl.linkTCPToFile(val, data[0], Integer.valueOf(data[1]), data[2]);
//        }else if(processPath.startsWith(TAG_ORDER_FILE)) {
//            // Order:File
//            final String KEY_DEF = processParam;
//            if(KEY_DEF != null && KEY_DEF.length() > 0) {
//                PatientVisitModel pvtModel = getContext().getPatientVisit();
//                PatientModel patientModel = getContext().getPatient();
//                if(patientModel == null) return false;
//                PVTKanaToAscii kana2Ascii = new PVTKanaToAscii();
//                String format = Project.getString(KEY_DEF + ".format");
//                if(format == null || format.length() <= 0) return false;
//                String dir = Project.getString(KEY_DEF + ".dir");
//                if(dir == null || dir.length() <= 0) return false;
//                String birth = (patientModel.getBirthday() != null) ? patientModel.getBirthday().replaceAll("-", "").replaceAll("/", "") : "";
//                SimpleDateFormat sdf1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
//                String birthFormat = Project.getString(KEY_DEF + ".birthformat", DEFAULT_DATE_FORMAT);
//                SimpleDateFormat sdf2 = new SimpleDateFormat(birthFormat);
//                Date date = sdf1.parse(birth, new ParsePosition(0));
//                String sexFormats = Project.getString(KEY_DEF + ".malefemale", "m,f");
//                String[] sexFormat = sexFormats.split(",");
//                String sex = (patientModel.getGender() != null) ? (patientModel.getGender().toLowerCase().startsWith("m") ? sexFormat[0] : sexFormat[1]) : "";
//                String encoding = Project.getString(KEY_DEF + ".encoding", DEFAULT_CSV_ENCODING);
//                format = format.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
//                format = format.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
//                format = format.replaceAll(KEY_PATIENT_KANJI, patientModel.getFullName());
//                format = format.replaceAll(KEY_PATIENT_KANA, fullKanaToHalfKana(patientModel.getKanaName()));
//                format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, patientModel.getKanaName());
//                format = format.replaceAll(KEY_PATIENT_ASCII, kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), ""));
//                format = format.replaceAll(KEY_PATIENT_SEX, sex);
//                format = format.replaceAll(KEY_PATIENT_BIRTH, sdf2.format(date));
//                format = format.replaceAll(KEY_PVTDATE, pvtModel.getPvtDate());
//                if(patientModel.getAddress() != null) {
//                    if(patientModel.getAddress().getZipCode() != null) {
//                        format = format.replaceAll(KEY_PATIENT_ZIPCODE, patientModel.getAddress().getZipCode());
//                    }else{
//                        format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
//                    }
//                    if(patientModel.getAddress().getAddress() != null) {
//                        format = format.replaceAll(KEY_PATIENT_ADDRESS, patientModel.getAddress().getAddress());
//                    }else{
//                        format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
//                    }
//                }else{
//                    format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
//                    format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
//                }
//                if(patientModel.getTelephone() != null) {
//                    format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getTelephone());
//                }else if(patientModel.getMobilePhone() != null) {
//                    format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getMobilePhone());
//                }else{
//                    format = format.replaceAll(KEY_PATIENT_PHONE, "");
//                }
//                if(patientModel.getTelephone() != null) {
//                    format = format.replaceAll(KEY_PATIENT_TELEPHONE, patientModel.getTelephone());
//                }else{
//                    format = format.replaceAll(KEY_PATIENT_TELEPHONE, "");
//                }
//                if(patientModel.getMobilePhone() != null) {
//                    format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, patientModel.getMobilePhone());
//                }else{
//                    format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, "");
//                }
//                String file = Project.getString(KEY_DEF + ".filename", "ID_%PATIENTID%");
//                String ext = Project.getString(KEY_DEF + ".ext", ".txt");
//                String fDateFormat = Project.getString(KEY_DEF + ".filename.dateformat", DEFAULT_DATE_FORMAT);
//                SimpleDateFormat sdf3 = new SimpleDateFormat(fDateFormat);
//                Date today = new Date();
//                file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
//                file = file.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
//                file = file.replaceAll(KEY_TODAY, sdf3.format(today));
//                String path = dir + File.separator + file;
//                if(path.startsWith("[TCP]")) {
//                    path = path.substring(path.indexOf("]") + 1) + ext;
//                    String[] data = path.split(",");
//                    if(data.length < 3) return false;
//                    opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
//                }else{
//                    try {
//                        if(Project.getBoolean(KEY_DEF + ".rename", false)) {
//                            File tmp = new File(path);
//                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
//                            bw.write(format);
//                            bw.newLine();
//                            bw.close();
//                            File csv = new File(path + ext);
//                            tmp.renameTo(csv);
//                        }else{
//                            File tmp = new File(path + ext);
//                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
//                            bw.write(format);
//                            bw.newLine();
//                            bw.close();
//                        }
//                    } catch (IOException ex) {
//                        Logger.getLogger(DefaultBrowserEx.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//                    }
//                }
//            }
//        }
//        
//        return true;
        String KEY_DEF = null;
        if(processParam != null) {
            if(processPath.startsWith(TAG_ORDER_FILE)) {
                KEY_DEF = processParam;
            }else{
                KEY_DEF = "otherprocess" + processParam + ".link";
            }
        }
        return otherProcess(KEY_DEF, getContext(), processPath, processParam, imageDate);
    }
    
//s.oh^ 他プロセス連携(アイコン) 2014/05/09
    public static boolean otherProcess(String KEY_DEF, Chart chart, String processPath, String processParam, String imageDate) {
        if(processPath == null || processPath.length() <= 0) return false;
        
        OtherProcessLink opl = new OtherProcessLink();
        
        if(processPath.startsWith(TAG_URL)) {
            // URL
            String url = processPath.substring(processPath.indexOf("]") + 1);
            String birthFormat = Project.getString(KEY_DEF + ".birthformat", DEFAULT_DATE_FORMAT);
            String sexFormats = Project.getString(KEY_DEF + ".malefemale", "m,f");
            String urlEncode = Project.getString(KEY_DEF + ".urlencode", "utf-8");
            url = createURLFormat(chart, url, birthFormat, sexFormats, urlEncode, imageDate);
            
            opl.linkURL(url);
        }else if(processPath.startsWith(TAG_EXE)) {
            // Exe
            String path = processPath.substring(processPath.indexOf("]") + 1);
            String param = processParam;
            String command = null;
            if(param != null && param.length() > 0) {
                DocInfoModel[] selectModel = chart.getDocumentHistory().getSelectedHistories();
                String dateSelect = null;
                String dateToday = null;
                if(imageDate == null) {
                    if(selectModel != null && selectModel.length == 1) {
                        dateSelect = ModelUtils.getDateAsFormatString(selectModel[0].getFirstConfirmDate(), DATE_FORMAT);
                    }
                }else{
                    dateSelect = imageDate;
                }
                Date today = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                dateToday = sdf.format(today);
                param = param.replaceAll(KEY_PATIENT_ID, chart.getPatient().getPatientId());
                param = param.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(chart.getPatient().getPatientId()));
                if(dateSelect != null) {
                    param = param.replaceAll(KEY_STUDY_DATE, dateSelect);
                }else{
                    param = param.replaceAll(KEY_STUDY_DATE, dateToday);
                }
                param = param.replaceAll(KEY_TODAY, dateToday);
                if(path.indexOf(" ") >= 0) {
                    command = "\"" + path + "\" " + param;
                }else{
                    command = path + " " + param;
                }
            }else{
                if(path.indexOf(" ") >= 0) {
                    command = "\"" + path + "\"";
                }else{
                    command = path;
                }
            }
            
            opl.linkFile(command);
        }else if(processPath.startsWith(TAG_LINK)) {
            // link
            String param = processParam;
            if(param != null && param.length() > 0) {
                //final String KEY_DEF = "otherprocess" + param + ".link";
                int num = Project.getInt(KEY_DEF + ".num", 1);
                for(int i = 1; i < num+1; i++) {
//                    PatientModel patientModel = chart.getPatient();
//                    if(patientModel == null) return false;
//                    PVTKanaToAscii kana2Ascii = new PVTKanaToAscii();
                    String format = Project.getString(KEY_DEF + String.valueOf(i) + ".format");
                    if(format == null || format.length() <= 0) return false;
                    String dir = Project.getString(KEY_DEF + String.valueOf(i) + ".dir");
                    if(dir == null || dir.length() <= 0) return false;
//                    String birth = (patientModel.getBirthday() != null) ? patientModel.getBirthday().replaceAll("-", "").replaceAll("/", "") : "";
//                    SimpleDateFormat sdf1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
//                    String birthFormat = Project.getString(KEY_DEF + String.valueOf(i) + ".birthformat", DEFAULT_DATE_FORMAT);
//                    SimpleDateFormat sdf2 = new SimpleDateFormat(birthFormat);
//                    Date date = sdf1.parse(birth, new ParsePosition(0));
//                    String sexFormats = Project.getString(KEY_DEF + String.valueOf(i) + ".malefemale", "m,f");
//                    String[] sexFormat = sexFormats.split(",");
//                    String sex = (patientModel.getGender() != null) ? (patientModel.getGender().toLowerCase().startsWith("m") ? sexFormat[0] : sexFormat[1]) : "";
                    String encoding = Project.getString(KEY_DEF + String.valueOf(i) + ".encoding", DEFAULT_CSV_ENCODING);
//                    format = format.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
//                    format = format.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
//                    format = format.replaceAll(KEY_PATIENT_KANJI, patientModel.getFullName());
//                    format = format.replaceAll(KEY_PATIENT_KANA, fullKanaToHalfKana(patientModel.getKanaName()));
//                    format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, patientModel.getKanaName());
//                    format = format.replaceAll(KEY_PATIENT_ASCII, kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), ""));
//                    format = format.replaceAll(KEY_PATIENT_SEX, sex);
//                    format = format.replaceAll(KEY_PATIENT_BIRTH, sdf2.format(date));
//                    if(patientModel.getAddress() != null) {
//                        if(patientModel.getAddress().getZipCode() != null) {
//                            format = format.replaceAll(KEY_PATIENT_ZIPCODE, patientModel.getAddress().getZipCode());
//                        }else{
//                            format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
//                        }
//                        if(patientModel.getAddress().getAddress() != null) {
//                            format = format.replaceAll(KEY_PATIENT_ADDRESS, patientModel.getAddress().getAddress());
//                        }else{
//                            format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
//                        }
//                    }else{
//                        format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
//                        format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
//                    }
//                    if(patientModel.getTelephone() != null) {
//                        format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getTelephone());
//                    }else if(patientModel.getMobilePhone() != null) {
//                        format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getMobilePhone());
//                    }else{
//                        format = format.replaceAll(KEY_PATIENT_PHONE, "");
//                    }
//                    if(patientModel.getTelephone() != null) {
//                        format = format.replaceAll(KEY_PATIENT_TELEPHONE, patientModel.getTelephone());
//                    }else{
//                        format = format.replaceAll(KEY_PATIENT_TELEPHONE, "");
//                    }
//                    if(patientModel.getMobilePhone() != null) {
//                        format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, patientModel.getMobilePhone());
//                    }else{
//                        format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, "");
//                    }
                    format = createFormat(chart, KEY_DEF + String.valueOf(i), format);
                    String file = Project.getString(KEY_DEF + String.valueOf(i) + ".filename", "ID_%PATIENTID%");
                    String ext = Project.getString(KEY_DEF + String.valueOf(i) + ".ext", ".txt");
                    String fDateFormat = Project.getString(KEY_DEF + String.valueOf(i) + ".filename.dateformat", DEFAULT_DATE_FORMAT);
                    SimpleDateFormat sdf3 = new SimpleDateFormat(fDateFormat);
                    Date today = new Date();
                    if(chart != null && chart.getPatient() != null) {
                        file = file.replaceAll(KEY_PATIENT_ID, chart.getPatient().getPatientId());
                        file = file.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(chart.getPatient().getPatientId()));
                    }
                    file = file.replaceAll(KEY_TODAY, sdf3.format(today));
                    if(processPath.indexOf("[TCP]") >= TAG_LINK.length()) {
                        String path = processPath.substring(processPath.lastIndexOf("]") + 1) + ext;
                        String[] data = path.split(",");
                        if(data.length < 3) return false;
                        opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
                    }else if(processPath.indexOf("[EXE]") >= TAG_LINK.length()) {
                        String path = processPath.substring(processPath.lastIndexOf("]") + 1);
                        String command = null;
                        if(path.indexOf(" ") >= 0) {
                            command = "\"" + path + "\" " + format;
                        }else{
                            command = path + " " + format;
                        }

                        opl.linkFile(command);
                    }else{
                        String path = dir + File.separator + file;
                        try {
                            if(Project.getBoolean(KEY_DEF + String.valueOf(i) + ".rename", false)) {
                                File tmp = new File(path);
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                                bw.write(format);
                                bw.newLine();
                                bw.close();
                                File csv = new File(path + ext);
                                tmp.renameTo(csv);
                            }else{
                                File tmp = new File(path + ext);
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                                bw.write(format);
                                bw.newLine();
                                bw.close();
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(DefaultBrowserEx.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }else if(processPath.startsWith(TAG_FILE)) {
            // File
            String path = processPath.substring(processPath.indexOf("]") + 1);
            String param = processParam;
            if(param != null && param.length() > 0) {
                DocInfoModel[] selectModel = chart.getDocumentHistory().getSelectedHistories();
                String dateSelect = null;
                String dateToday = null;
                if(imageDate == null) {
                    if(selectModel != null && selectModel.length == 1) {
                        dateSelect = ModelUtils.getDateAsFormatString(selectModel[0].getFirstConfirmDate(), DATE_FORMAT);
                    }
                }else{
                    dateSelect = imageDate;
                }
                Date today = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                dateToday = sdf.format(today);
                param = param.replaceAll(KEY_PATIENT_ID, chart.getPatient().getPatientId());
                param = param.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(chart.getPatient().getPatientId()));
                if(dateSelect != null) {
                    param = param.replaceAll(KEY_STUDY_DATE, dateSelect);
                }else{
                    param = param.replaceAll(KEY_STUDY_DATE, dateToday);
                }
                param = param.replaceAll(KEY_TODAY, dateToday);
            }
            
            File file = new File(path);
            BufferedWriter bw;
            try {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                bw.write(param);
                bw.newLine();
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(DefaultBrowserEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(processPath.startsWith(TAG_TCP_EXE)) {
            // TCP/IP:Exe
            String tcp = processPath.substring(processPath.indexOf("]") + 1);
            String[] data = tcp.split(",");
            if(data.length < 2) return false;
            String val = processParam;
            val = val.replaceAll(KEY_PATIENT_ID, chart.getPatient().getPatientId());
            val = val.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(chart.getPatient().getPatientId()));
            opl.linkTCPToExe(val, data[0], Integer.valueOf(data[1]));
        }else if(processPath.startsWith(TAG_TCP_FILE)) {
            // TCP/IP:File
            String tcp = processPath.substring(processPath.indexOf("]") + 1);
            String[] data = tcp.split(",");
            if(data.length < 3) return false;
            String val = processParam;
            val = val.replaceAll(KEY_PATIENT_ID, chart.getPatient().getPatientId());
            val = val.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(chart.getPatient().getPatientId()));
            opl.linkTCPToFile(val, data[0], Integer.valueOf(data[1]), data[2]);
        }else if(processPath.startsWith(TAG_ORDER_FILE)) {
            // Order:File
            //final String KEY_DEF = processParam;
            if(KEY_DEF != null && KEY_DEF.length() > 0) {
                String format = Project.getString(KEY_DEF + ".format");
                if(format == null || format.length() <= 0) return false;
                String dir = Project.getString(KEY_DEF + ".dir");
                if(dir == null || dir.length() <= 0) return false;
                String encoding = Project.getString(KEY_DEF + ".encoding", DEFAULT_CSV_ENCODING);
                format = createFormat(chart, KEY_DEF, format);
                String file = Project.getString(KEY_DEF + ".filename", "ID_%PATIENTID%");
                String ext = Project.getString(KEY_DEF + ".ext", ".txt");
                String fDateFormat = Project.getString(KEY_DEF + ".filename.dateformat", DEFAULT_DATE_FORMAT);
                SimpleDateFormat sdf3 = new SimpleDateFormat(fDateFormat);
                Date today = new Date();
                if(chart != null && chart.getPatient() != null) {
                    file = file.replaceAll(KEY_PATIENT_ID, chart.getPatient().getPatientId());
                    file = file.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(chart.getPatient().getPatientId()));
                }
                file = file.replaceAll(KEY_TODAY, sdf3.format(today));
                String path = dir + File.separator + file;
                if(path.startsWith("[TCP]")) {
                    path = path.substring(path.indexOf("]") + 1) + ext;
                    String[] data = path.split(",");
                    if(data.length < 3) return false;
                    opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
                }else{
                    try {
                        if(Project.getBoolean(KEY_DEF + ".rename", false)) {
                            File tmp = new File(path);
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                            bw.write(format);
                            bw.newLine();
                            bw.close();
                            File csv = new File(path + ext);
                            tmp.renameTo(csv);
                        }else{
                            File tmp = new File(path + ext);
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                            bw.write(format);
                            bw.newLine();
                            bw.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DefaultBrowserEx.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        return true;
    }
    
//s.oh^ 2014/08/01 受付連携
    private static String createFormat(Chart chart, String KEY_DEF, String tmp) {
        String format = tmp;
        format = format.replaceAll(KEY_USER_ID, Project.getUserId());
        format = format.replaceAll(KEY_USER_PASS, Project.getUserModel().getPassword());
        PatientVisitModel pvtModel = chart.getPatientVisit();
        PatientModel patientModel = chart.getPatient();
        if(patientModel == null) return format;
        PVTKanaToAscii kana2Ascii = new PVTKanaToAscii();
        String birth = (patientModel.getBirthday() != null) ? patientModel.getBirthday().replaceAll("-", "").replaceAll("/", "") : "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        String birthFormat = Project.getString(KEY_DEF + ".birthformat", DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf2 = new SimpleDateFormat(birthFormat);
        Date date = sdf1.parse(birth, new ParsePosition(0));
        String sexFormats = Project.getString(KEY_DEF + ".malefemale", "m,f");
        String[] sexFormat = sexFormats.split(",");
        String sex = (patientModel.getGender() != null) ? (patientModel.getGender().toLowerCase().startsWith("m") ? sexFormat[0] : sexFormat[1]) : "";
        format = format.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        format = format.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        format = format.replaceAll(KEY_PATIENT_KANJI, patientModel.getFullName());
        format = format.replaceAll(KEY_PATIENT_KANA, fullKanaToHalfKana(patientModel.getKanaName()));
        format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, patientModel.getKanaName());
        format = format.replaceAll(KEY_PATIENT_ASCII, kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), ""));
        format = format.replaceAll(KEY_PATIENT_SEX, sex);
        format = format.replaceAll(KEY_PATIENT_BIRTH, sdf2.format(date));
        if(pvtModel != null) {
            format = format.replaceAll(KEY_PVTDATE, pvtModel.getPvtDate());
            format = format.replaceAll(KEY_DEPT_CODE, pvtModel.getDeptCode());
            format = format.replaceAll(KEY_DEPT_NAME, pvtModel.getDeptName());
            format = format.replaceAll(KEY_ATTENDING_KANJI, pvtModel.getDoctorName());
            format = format.replaceAll(KEY_ATTENDING_ID, pvtModel.getDoctorId());
            format = format.replaceAll(KEY_INSURANCE_FIRST, pvtModel.getFirstInsurance());
        }
        if(patientModel.getAddress() != null) {
            if(patientModel.getAddress().getZipCode() != null) {
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, patientModel.getAddress().getZipCode());
            }else{
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            }
            if(patientModel.getAddress().getAddress() != null) {
                format = format.replaceAll(KEY_PATIENT_ADDRESS, patientModel.getAddress().getAddress());
            }else{
                format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
            }
        }else{
            format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getTelephone());
        }else if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_PHONE, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, patientModel.getTelephone());
        }else{
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, "");
        }
        if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, "");
        }
        
        return format;
    }
//s.oh$
    
    private static String createURLFormat(Chart chart, String format, String birthFormat, String sexFormats, String urlencode, String imageDate) {
        format = format.replaceAll(KEY_USER_ID, Project.getUserId());
        format = format.replaceAll(KEY_USER_PASS, Project.getUserModel().getPassword());
        if(chart == null) return format;
        PatientVisitModel pvtModel = chart.getPatientVisit();
        PatientModel patientModel = chart.getPatient();
        if(patientModel == null) return format;
        PVTKanaToAscii kana2Ascii = new PVTKanaToAscii();
        if(format == null || format.length() <= 0) return format;
        String birth = (patientModel.getBirthday() != null) ? patientModel.getBirthday().replaceAll("-", "").replaceAll("/", "") : "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf2 = new SimpleDateFormat(birthFormat);
        Date date = sdf1.parse(birth, new ParsePosition(0));
        String[] sexFormat = sexFormats.split(",");
        String sex = (patientModel.getGender() != null) ? (patientModel.getGender().toLowerCase().startsWith("m") ? sexFormat[0] : sexFormat[1]) : "";
        format = format.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        format = format.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        if(urlencode != null) {
           try {
               String tmp = URLEncoder.encode(patientModel.getFullName(), urlencode);
               format = format.replaceAll(KEY_PATIENT_KANJI, tmp.replace("+", "%20"));
               tmp = URLEncoder.encode(fullKanaToHalfKana(patientModel.getKanaName()), urlencode);
               format = format.replaceAll(KEY_PATIENT_KANA, tmp.replace("+", "%20"));
               tmp = URLEncoder.encode(patientModel.getKanaName(), urlencode);
               format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, tmp.replace("+", "%20"));
           } catch (UnsupportedEncodingException ex) {
               Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, ex.toString(), format, urlencode);
           }
        }else{
            format = format.replaceAll(KEY_PATIENT_KANJI, patientModel.getFullName());
            format = format.replaceAll(KEY_PATIENT_KANA, fullKanaToHalfKana(patientModel.getKanaName()));
            format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, patientModel.getKanaName());
        }
        format = format.replaceAll(KEY_PATIENT_ASCII, kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), ""));
        format = format.replaceAll(KEY_PATIENT_KANJITRIM, trimSpace(patientModel.getFullName()));
        format = format.replaceAll(KEY_PATIENT_KANATRIM, trimSpace(fullKanaToHalfKana(patientModel.getKanaName())));
        format = format.replaceAll(KEY_PATIENT_ZENKAKUKANATRIM, trimSpace(patientModel.getKanaName()));
        format = format.replaceAll(KEY_PATIENT_ASCIITRIM, trimSpace(kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), "")));
        format = format.replaceAll(KEY_PATIENT_SEX, sex);
        format = format.replaceAll(KEY_PATIENT_BIRTH, sdf2.format(date));
        format = format.replaceAll(KEY_PVTDATE, (pvtModel != null) ? pvtModel.getPvtDate() : "");
        if(patientModel.getAddress() != null) {
            if(patientModel.getAddress().getZipCode() != null) {
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, patientModel.getAddress().getZipCode());
            }else{
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            }
            if(patientModel.getAddress().getAddress() != null) {
                format = format.replaceAll(KEY_PATIENT_ADDRESS, patientModel.getAddress().getAddress());
            }else{
                format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
            }
        }else{
            format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getTelephone());
        }else if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_PHONE, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, patientModel.getTelephone());
        }else{
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, "");
        }
        if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, "");
        }
        
        DocInfoModel[] selectModel = (chart != null) ? chart.getDocumentHistory().getSelectedHistories() : null;
        String dateSelect = null;
        String dateToday = null;
        if(imageDate == null) {
            if(selectModel != null && selectModel.length == 1) {
                dateSelect = ModelUtils.getDateAsFormatString(selectModel[0].getFirstConfirmDate(), DATE_FORMAT);
            }
        }else{
            dateSelect = imageDate;
        }
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        dateToday = sdf.format(today);
        if(dateSelect != null) {
            format = format.replaceAll(KEY_STUDY_DATE, dateSelect);
        }else{
            format = format.replaceAll(KEY_STUDY_DATE, dateToday);
        }
        format = format.replaceAll(KEY_TODAY, dateToday);
        
        return format;
    }
//s.oh$
    
    private void backdir() {
        String path = getImageBase() + File.separator + getContext().getPatient().getPatientId();
        if(path.equals(nowLocation)) return;
        File dir = new File(nowLocation);
        nowLocation = dir.getParent();
        scan(nowLocation);
        view.getDirLbl().setText(createLocationText(nowLocation));
    }
    
    @Override
    protected void initComponents() {

        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        final ActionMap map = getActionMap(resource);

        // TableModel
        int columnCount = columnCount();
        tableModel = new ImageTableModel(null, columnCount);

        view = new DefaultBrowserViewEx();
        table = view.getTable();
        table.setModel(tableModel);
        table.putClientProperty("karteCompositor", this);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDragEnabled(true);
        table.setTransferHandler(new ImageTableTransferHandler(this));

        TableColumn column;
        for (int i = 0; i < columnCount; i++) {
            column = view.getTable().getColumnModel().getColumn(i);
            column.setPreferredWidth(cellWidth);
        }
        table.setRowHeight(cellHeight);

        // Renderer
        imageRenderer = new ImageTableRenderer(this);
        imageRenderer.setImageSize(MAX_IMAGE_SIZE);
        table.setDefaultRenderer(java.lang.Object.class, imageRenderer);

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==1) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    ImageEntry entry = getEntryAt(row, col);
                    Action copy = getContext().getChartMediator().getAction(GUIConst.ACTION_COPY);
                    copy.setEnabled(entry!=null && (!entry.isDirectrory()));
                }
                else if(e.getClickCount()==2) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    ImageEntry entry = getEntryAt(row, col);
                    if (entry!=null && (!entry.isDirectrory())) {
                        if(entry.getFileName().toUpperCase().endsWith(".JPG")) {
                            linkOtherProcessThumbnail(entry);
                        }else{
                            openImage(entry);
                        }
                    } else if (entry!=null && entry.isDirectrory()) {
                        scan(entry.getPath());
                        nowLocation = entry.getPath();
                        view.getDirLbl().setText(createLocationText(nowLocation));
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
                mabeShowPopup(me);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                mabeShowPopup(me);
            }

            private void mabeShowPopup(MouseEvent e) {

                if (!e.isPopupTrigger()) {
                    return;
                }

                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                Object entry = tableModel.getValueAt(row, col);

                if (entry==null) {
                    return;
                }

                JPopupMenu contextMenu = new JPopupMenu();
                JMenuItem micp = new JMenuItem("コピー");
                Action copy = getContext().getChartMediator().getAction(GUIConst.ACTION_COPY);
                micp.setAction(copy);
                contextMenu.add(micp);
                
//s.oh^ 2014/05/07 PDF・画像タブの改善
                JMenuItem midel = new JMenuItem("削除");
                midel.setAction(map.get("delete"));
                contextMenu.add(midel);
                
                JMenuItem mirename = new JMenuItem("名前の変更");
                mirename.setAction(map.get("rename"));
                contextMenu.add(mirename);
//s.oh$
                
                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        
        view.getSettingBtn().setAction(map.get("doSetting"));
        view.getSettingBtn().setToolTipText("画像ディレクトリ等の設定を行います。");
        view.getRefreshBtn().setAction(map.get("refresh"));
        view.getRefreshBtn().setToolTipText("表示を更新します。");
        boolean canRefresh = true;
        canRefresh = canRefresh && (valueIsNotNullNorEmpty(properties.getProperty(PROP_BASE_DIR)));
        view.getRefreshBtn().setEnabled(canRefresh);

        if(otherProcess1 != null && otherProcess1.length() > 0) {
            view.getOtherProcess1Btn().setAction(map.get("process1"));
        }else{
            view.getOtherProcess1Btn().setVisible(false);
        }
        if(otherProcess2 != null && otherProcess2.length() > 0) {
            view.getOtherProcess2Btn().setAction(map.get("process2"));
        }else{
            view.getOtherProcess2Btn().setVisible(false);
        }
        if(otherProcess3 != null && otherProcess3.length() > 0) {
            view.getOtherProcess3Btn().setAction(map.get("process3"));
        }else{
            view.getOtherProcess3Btn().setVisible(false);
        }
        
//s.oh^ 2014/05/07 PDF・画像タブの改善
        view.getNewDirBtn().setAction(map.get("newdir"));
//s.oh$
        
//s.oh^ 2014/05/30 PDF・画像タブの改善
        view.getBackDirBtn().setAction(map.get("backdir"));
//s.oh$

//minagawa^ Icon Server        
        view.getDirLbl().setIcon(ClientContext.getImageIconArias("icon_info_small"));
//minagawa$        
        view.getDirLbl().setToolTipText("画像・PDFディレクトリの場所を表示してます。");
        setUI(view);
    }
    
    /**
     * 全角カナから半角カナへの変換
     * @param kana
     * @return 
     */
    private static String fullKanaToHalfKana(String kana) {
        StringBuffer sb = new StringBuffer();
        for(String [] pair : KANA) {
            //switch('') {
            //    case '':
            //        break;
            //}
            kana = kana.replaceAll(pair[0], pair[1]);
        }
        return kana;
    }
    
    /**
     * ゼロサプレス
     * @param data
     * @return 
     */
    private static String zeroSuppress(String data) {
        String ret = null;
        Pattern ptn = java.util.regex.Pattern.compile("^0+([0-9]+.*)");
        Matcher m = ptn.matcher(data);
        if(m.matches()) {
            ret = m.group(1);
        }else{
            ret = data;
        }
        return ret;
    }
    
    /**
     * 文字列からスペースを取り除く
     * @param data
     * @return 
     */
    private static String trimSpace(String data) {
        if(data == null || data.length() <= 0) return "";
        String ret = null;
        String tmp = data.replaceAll(" ", "");
        ret = tmp.replaceAll("　", "");
        return ret;
    }
}
