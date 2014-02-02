
package cancer.document;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.CalendarCardPanel;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.client.KartePane;
import open.dolphin.client.KartePaneDumper_2;
import open.dolphin.client.KarteStyledDocument;
import open.dolphin.client.SOATransferHandler;
import open.dolphin.delegater.SetaDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.CompositeImageModel;
import open.dolphin.infomodel.FirstEncounter2Model;
import open.dolphin.infomodel.FirstEncounterModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.project.Project;

/**
 * 瀬田クリニックグループ初診時情報２のフォームクラス。
 */
public class FirstEncounter2 extends AbstractChartDocument {

    private static final String TITLE = "初診時記録2";
    private static final String DOC_TYPE = "SETA_2";
    private FirstEncounter2Model model;
    private List<CompositeImageModel> imageList;
    private FirstEncounter2View view;
    private StateMgr stateMgr;
    private KartePane soaPane;
    private boolean empty;

    /** Creates a new instance of FirstEncounter2 */
    public FirstEncounter2() {
        setTitle(TITLE);
    }

    private void initComponents() {
        
        this.view = new FirstEncounter2View();
        
        soaPane = new KartePane();
        soaPane.setTextPane(view.getPhysioObjectiveNotes());
        soaPane.setParent(this);
        soaPane.setRole(IInfoModel.ROLE_SOA);
        soaPane.getTextPane().setTransferHandler(new SOATransferHandler(soaPane));
        
        JScrollPane scroller = new JScrollPane(this.view);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller, BorderLayout.CENTER);
    }

    @Override
    public void start() {

        // GUI を初期化する
        initComponents();
        
        // StateContext を生成する
        super.enter();
        stateMgr = new StateMgr();
        
        // 患者の初診時2記録を検索する
        final long pk = getContext().getKarte().getId();
        
        DBTask task = new DBTask<Boolean, Void>(getContext()) {
            
            @Override
            protected Boolean doInBackground() throws Exception {
                logger.debug("FirstEncounter2 GetTask doInBackground");
                SetaDelegater ddl = new SetaDelegater();
                List<FirstEncounterModel> list =  ddl.getFirstEncounter(pk, DOC_TYPE);
                //if (ddl.isNoError()) {
                    if (list != null && list.size() > 0) {
                        model = (FirstEncounter2Model) list.get(0);
                    }
                //} else {
                    //logger.debug(ddl.getErrorMessage());
                //}
                return true;
            }
            
            @Override
            protected void succeeded(Boolean result) {
                logger.debug("FirstEncounter2 GetTask succeeded");
                if (result && model != null) {
                    empty = false;
                    display(model);
                } else {
                    empty = true;
                }
                stateMgr.start();
            }
        };
        
        task.execute();
    }
    
    @Override
    public void enter() {
        super.enter();
        if (stateMgr != null) {
            stateMgr.enter();
        }
    }
    
    @Override
    public boolean isDirty() {
        if (stateMgr != null) {
            return stateMgr.isDirtyState();
        } else {
            return super.isDirty();
        }
    }
    
    @Override
    public void setDirty(boolean b) {
        logger.debug("setDirty " + b);
    }

    @Override
    public void stop() {
    }

    @Override
    @org.jdesktop.application.Action
    public void save() {

        long pk = 0L;
        if (model != null && model.getId() != 0L) {
            pk = model.getId();
        }
        model = new FirstEncounter2Model();
        model.setId(pk);
        restore(model);
        
        DBTask task = new DBTask<Long, Void>(getContext()) {
            
            @Override
            protected Long doInBackground() throws Exception {
                logger.debug("FirstEncounter2 save doInBackground");
                SetaDelegater ddl = new SetaDelegater();
                long result = ddl.saveOrUpdateFirstEncounter(model, imageList);
                //if (!ddl.isNoError()) {
                    //System.err.println(ddl.getErrorMessage());
                //}
                return new Long(result);
            }
            
            @Override
            protected void succeeded(Long result) {
                logger.debug("FirstEncounter2 save succeeded");
                if (result.longValue() != 0L) {
                    model.setId(result);
                    stateMgr.processSavedEvent();
                }
            }
        };
        
        task.execute();
    }
    
    public void modifyKarte() {
        stateMgr.processModifyEvent();
    }

    private String getFieldValue(JTextField tf) {
        String test = tf.getText().trim();
        return test.equals("") ? null : test;
    }

    private String getAreaValue(JTextArea ta) {
        String test = ta.getText().trim();
        return test.equals("") ? null : test;
    }
    
    private String getPaneValue(JTextPane tp) {
        String test = tp.getText().trim();
        return test.equals("") ? null : test;
    }

    private String getRadioValue(JRadioButton[] btns) {
        String ret = null;
        for (JRadioButton btn : btns) {
            if (btn.isSelected()) {
                ret = btn.getText();
                break;
            }
        }
        return ret;
    }

    private void setFieldValue(JTextField tf, String value) {
        if (value != null) {
            tf.setText(value);
        }
    }

    private void setAreaValue(JTextArea ta, String value) {
        if (value != null) {
            ta.setText(value);
        }
    }
    
    private void setPaneValue(JTextPane ta, String value) {
        if (value != null) {
            ta.setText(value);
        }
    }

    private void selectRadio(JRadioButton[] btns, String value) {
        for (JRadioButton b : btns) {
            if (b.getText().equals(value)) {
                b.setSelected(true);
                break;
            }
        }
    }

    protected void restore(FirstEncounter2Model model) {

        if (model == null) {
            return;
        }

        // 記録日
        Date d = new Date();
        model.setConfirmed(d);
        model.setStarted(d);
        model.setRecorded(d);
        model.setStatus(IInfoModel.STATUS_FINAL);
        model.setKarteBean(getContext().getKarte());
        model.setUserModel(Project.getUserModel());
        
        // 前治療
        model.setPrevCare(getRadioValue(new JRadioButton[]{view.getZenChiryoNashi(), view.getZenChiryoAri()}));

        // 手術
        model.setSurgeryDate1(getFieldValue(view.getSurgeryDate1()));
        model.setSurgeryDate2(getFieldValue(view.getSurgeryDate2()));
        model.setSurgeryDate3(getFieldValue(view.getSurgeryDate3()));
        model.setSurgery1(getFieldValue(view.getSurgery1()));
        model.setSurgery2(getFieldValue(view.getSurgery2()));
        model.setSurgery3(getFieldValue(view.getSurgery3()));

        // 放射線
        model.setRadDate1(getFieldValue(view.getRadDate1()));
        model.setRadDate2(getFieldValue(view.getRadDate2()));
        model.setRadDate3(getFieldValue(view.getRadDate3()));
        model.setRad1(getFieldValue(view.getRad1()));
        model.setRad2(getFieldValue(view.getRad2()));
        model.setRad3(getFieldValue(view.getRad3()));

        // その他
        model.setOtherDate1(getFieldValue(view.getOtherDate1()));
        model.setOtherDate2(getFieldValue(view.getOtherDate2()));
        model.setOtherDate3(getFieldValue(view.getOtherDate3()));
        model.setOtherDate4(getFieldValue(view.getOtherDate4()));
        model.setOtherDate5(getFieldValue(view.getOtherDate5()));
        model.setOther1(getFieldValue(view.getOther1()));
        model.setOther2(getFieldValue(view.getOther2()));
        model.setOther3(getFieldValue(view.getOther3()));
        model.setOther4(getFieldValue(view.getOther4()));
        model.setOther5(getFieldValue(view.getOther5()));

        // 化学療法
        model.setChemicalFrom1(getFieldValue(view.getChemotherapyFrom1()));
        model.setChemicalFrom2(getFieldValue(view.getChemotherapyFrom2()));
        model.setChemicalFrom3(getFieldValue(view.getChemotherapyFrom3()));
        model.setChemicalFrom4(getFieldValue(view.getChemotherapyFrom4()));
        model.setChemicalFrom5(getFieldValue(view.getChemotherapyFrom5()));
        model.setChemicalFrom6(getFieldValue(view.getChemotherapyFrom6()));
        model.setChemicalTo1(getFieldValue(view.getChemotherapyTo1()));
        model.setChemicalTo2(getFieldValue(view.getChemotherapyTo2()));
        model.setChemicalTo3(getFieldValue(view.getChemotherapyTo3()));
        model.setChemicalTo4(getFieldValue(view.getChemotherapyTo4()));
        model.setChemicalTo5(getFieldValue(view.getChemotherapyTo5()));
        model.setChemicalTo6(getFieldValue(view.getChemotherapyTo6()));
        model.setChemotherapy1(getFieldValue(view.getChemotherapy1()));
        model.setChemotherapy2(getFieldValue(view.getChemotherapy2()));
        model.setChemotherapy3(getFieldValue(view.getChemotherapy3()));
        model.setChemotherapy4(getFieldValue(view.getChemotherapy4()));
        model.setChemotherapy5(getFieldValue(view.getChemotherapy5()));
        model.setChemotherapy6(getFieldValue(view.getChemotherapy6()));

        // 評価可能病変
        model.setEvalSpecimen(getRadioValue(new JRadioButton[]{view.getEvalNashi(), view.getEvalAri()}));

        // 撮影日（部位）
        model.setSatueiDate1(getFieldValue(view.getSatueiDate1()));
        model.setSatueiDate2(getFieldValue(view.getSatueiDate2()));
        model.setLocation1(getFieldValue(view.getLocation1()));
        model.setLocation2(getFieldValue(view.getLocation2()));

        // 画像種類
        model.setImageType1(getRadioValue(new JRadioButton[]{view.getCt1(), view.getMri1(), view.getXp1()}));
        model.setOtherImage1(getFieldValue(view.getOtherImage1()));
        model.setImageType2(getRadioValue(new JRadioButton[]{view.getCt2(), view.getMri2(), view.getXp2()}));
        model.setOtherImage2(getFieldValue(view.getOtherImage2()));

        // 原発臓器
        model.setGenpatuType(getRadioValue(new JRadioButton[]{view.getGenpatuNashi(), view.getGenpatuTanpatu(), view.getGenpatuTahatu()}));
        model.setGennpatuSize1(getFieldValue(view.getGennpatuSize1()));
        model.setGennpatuSize2(getFieldValue(view.getGennpatuSize2()));

        // 他臓器
        model.setTazokiType(getRadioValue(new JRadioButton[]{view.getTazokiNashi(), view.getTazokiTanpatu(), view.getTazokiTahatu()}));
        model.setTazokiSize1(getFieldValue(view.getTazokiSize1()));
        model.setTazokiSize2(getFieldValue(view.getTazokiSize2()));

        // リンパ節
        model.setLymphType(getRadioValue(new JRadioButton[]{view.getLymphNashi(), view.getLymphTanpatu(), view.getLymphTahatu()}));
        model.setLymphSize1(getFieldValue(view.getLymphSize1()));
        model.setLymphSize2(getFieldValue(view.getLymphSize2()));

        // 非標的病変
        model.setNoneTarget(getRadioValue(new JRadioButton[]{view.getHyotekiNashi(), view.getHyotekiAri()}));

        // 測定可能病変計
        model.setMesuableSum(getFieldValue(view.getMesuableSum()));

        // 測定不能病変  unmesuableNote
        model.setUnmesuableType(getRadioValue(new JRadioButton[]{view.getUnmesuableNashi(), view.getUnmesuableAri()}));
        model.setUnmesuableNote(getFieldValue(view.getUnmesuableNote()));

        // 画像所見
        model.setImageObjectiveNotes(getAreaValue(view.getImageObjectiveNotes()));

        // 特記事項
        model.setTokkiJiko(getAreaValue(view.getTokkiJiko()));

        // 併用治療
        model.setHeiyoType(getRadioValue(new JRadioButton[]{view.getHeiyoNashi(), view.getHeiyoAri()}));
        model.setHeiyoChiryo(getFieldValue(view.getHeiyoChiryo()));

        // 治療方針
        model.setCarePolicy(getAreaValue(view.getCarePolicy()));
        
        // 理学的所見
        KartePaneDumper_2 dumper = new KartePaneDumper_2();
        KarteStyledDocument doc = (KarteStyledDocument) soaPane.getTextPane().getDocument();
        dumper.dump(doc);
        model.setPhysioObjectiveNotes(dumper.getSpec());
        
        // 
        // Schema を追加する
        //      
        int maxImageWidth = ClientContext.getInt("image.max.width");
        int maxImageHeight = ClientContext.getInt("image.max.height");
        Dimension maxSImageSize = new Dimension(maxImageWidth, maxImageHeight);
        SchemaModel[] schemas = dumper.getSchema();
        if (schemas != null && schemas.length > 0) {
            imageList = new ArrayList<CompositeImageModel>();
            int number = 0;
            for (SchemaModel schema : schemas) {
                ImageIcon icon = schema.getIcon();
                icon = adjustImageSize(icon, maxSImageSize);
                byte[] jpegByte = getJPEGByte(icon.getImage());
                schema.setJpegByte(jpegByte);
                schema.setIcon(null);
                
                CompositeImageModel cim = new CompositeImageModel();
                cim.setConfirmed(model.getConfirmed());
                cim.setStarted(model.getStarted());
                cim.setRecorded(model.getRecorded());
                cim.setKarteBean(model.getKarteBean());
                cim.setUserModel(model.getUserModel());
                cim.setStatus(model.getStatus());
                
                cim.setContentType("image/jpg");
                cim.setMedicalRole("参考図");
                cim.setTitle("シェーマ画像");
                cim.setHref(number + ".jpg");
                cim.setImageNumber(number++);
                cim.setJpegByte(schema.getJpegByte());
                
                imageList.add(cim);
            }
        }
    }

    protected void display(FirstEncounter2Model model) {
        
        logger.debug("display");

        if (model == null || model.getId() == 0L) {
            return;
        }

        // 前治療
        selectRadio(new JRadioButton[]{view.getZenChiryoNashi(), view.getZenChiryoAri()}, model.getPrevCare());

        // 手術
        setFieldValue(view.getSurgeryDate1(), model.getSurgeryDate1());
        setFieldValue(view.getSurgeryDate2(), model.getSurgeryDate2());
        setFieldValue(view.getSurgeryDate3(), model.getSurgeryDate3());
        setFieldValue(view.getSurgery1(), model.getSurgery1());
        setFieldValue(view.getSurgery2(), model.getSurgery2());
        setFieldValue(view.getSurgery3(), model.getSurgery3());

        // 放射線
        setFieldValue(view.getRad1(), model.getRadDate1());
        setFieldValue(view.getRad2(), model.getRadDate2());
        setFieldValue(view.getRad3(), model.getRadDate3());
        setFieldValue(view.getRad1(), model.getRad1());
        setFieldValue(view.getRad2(), model.getRad2());
        setFieldValue(view.getRad3(), model.getRad3());

        // その他
        setFieldValue(view.getOtherDate1(), model.getOtherDate1());
        setFieldValue(view.getOtherDate2(), model.getOtherDate2());
        setFieldValue(view.getOtherDate3(), model.getOtherDate3());
        setFieldValue(view.getOtherDate4(), model.getOtherDate4());
        setFieldValue(view.getOtherDate5(), model.getOtherDate5());
        setFieldValue(view.getOther1(), model.getOther1());
        setFieldValue(view.getOther2(), model.getOther2());
        setFieldValue(view.getOther3(), model.getOther3());
        setFieldValue(view.getOther4(), model.getOther4());
        setFieldValue(view.getOther5(), model.getOther5());

        // 化学療法
        setFieldValue(view.getChemotherapyFrom1(), model.getChemicalFrom1());
        setFieldValue(view.getChemotherapyFrom2(), model.getChemicalFrom2());
        setFieldValue(view.getChemotherapyFrom3(), model.getChemicalFrom3());
        setFieldValue(view.getChemotherapyFrom4(), model.getChemicalFrom4());
        setFieldValue(view.getChemotherapyFrom5(), model.getChemicalFrom5());
        setFieldValue(view.getChemotherapyFrom6(), model.getChemicalFrom6());
        setFieldValue(view.getChemotherapyTo1(), model.getChemicalTo1());
        setFieldValue(view.getChemotherapyTo2(), model.getChemicalTo2());
        setFieldValue(view.getChemotherapyTo3(), model.getChemicalTo3());
        setFieldValue(view.getChemotherapyTo4(), model.getChemicalTo4());
        setFieldValue(view.getChemotherapyTo5(), model.getChemicalTo5());
        setFieldValue(view.getChemotherapyTo6(), model.getChemicalTo6());
        setFieldValue(view.getChemotherapy1(), model.getChemotherapy1());
        setFieldValue(view.getChemotherapy2(), model.getChemotherapy2());
        setFieldValue(view.getChemotherapy3(), model.getChemotherapy3());
        setFieldValue(view.getChemotherapy4(), model.getChemotherapy4());
        setFieldValue(view.getChemotherapy5(), model.getChemotherapy5());
        setFieldValue(view.getChemotherapy6(), model.getChemotherapy6());

        // 評価可能病変
        selectRadio(new JRadioButton[]{view.getEvalNashi(), view.getEvalAri()}, model.getEvalSpecimen());

        // 撮影日（部位）
        setFieldValue(view.getSatueiDate1(), model.getSatueiDate1());
        setFieldValue(view.getSatueiDate2(), model.getSatueiDate2());
        setFieldValue(view.getLocation1(), model.getLocation1());
        setFieldValue(view.getLocation2(), model.getLocation2());

        // 画像種類
        selectRadio(new JRadioButton[]{view.getCt1(), view.getMri1(), view.getXp1()}, model.getImageType1());
        selectRadio(new JRadioButton[]{view.getCt2(), view.getMri2(), view.getXp2()}, model.getImageType2());
        setFieldValue(view.getOtherImage1(), model.getOtherImage1());
        setFieldValue(view.getOtherImage2(), model.getOtherImage2());

        // 原発臓器
        selectRadio(new JRadioButton[]{view.getGenpatuNashi(), view.getGenpatuTanpatu(), view.getGenpatuTahatu()}, model.getGenpatuType());
        setFieldValue(view.getGennpatuSize1(), model.getGennpatuSize1());
        setFieldValue(view.getGennpatuSize2(), model.getGennpatuSize2());

        // 他臓器
        selectRadio(new JRadioButton[]{view.getTazokiNashi(), view.getTazokiTanpatu(), view.getTazokiTahatu()}, model.getTazokiType());
        setFieldValue(view.getTazokiSize1(), model.getTazokiSize1());
        setFieldValue(view.getTazokiSize2(), model.getTazokiSize2());

        // リンパ節
        selectRadio(new JRadioButton[]{view.getLymphNashi(), view.getLymphTanpatu(), view.getLymphTahatu()}, model.getLymphType());
        setFieldValue(view.getLymphSize1(), model.getLymphSize1());
        setFieldValue(view.getLymphSize2(), model.getLymphSize2());

        // 非標的病変
        selectRadio(new JRadioButton[]{view.getHeiyoNashi(), view.getHyotekiAri()}, model.getNoneTarget());

        // 測定可能病変計
        setFieldValue(view.getMesuableSum(), model.getMesuableSum());

        // 測定不能病変  unmesuableNote
        selectRadio(new JRadioButton[]{view.getUnmesuableNashi(), view.getUnmesuableAri()}, model.getUnmesuableType());
        setFieldValue(view.getUnmesuableNote(), model.getUnmesuableNote());

        // 画像所見
        setAreaValue(view.getImageObjectiveNotes(), model.getImageObjectiveNotes());

        // 特記事項
        setAreaValue(view.getTokkiJiko(), model.getTokkiJiko());

        // 併用治療
        selectRadio(new JRadioButton[]{view.getHeiyoNashi(), view.getHeiyoAri()}, model.getHeiyoType());
        setFieldValue(view.getHeiyoChiryo(), model.getHeiyoChiryo());

        // 治療方針
        setAreaValue(view.getCarePolicy(), model.getCarePolicy());
        
        // 理学的所見
        String physioSpec = model.getPhysioObjectiveNotes();
        if (physioSpec != null && (!physioSpec.equals(""))) {
            Collection<CompositeImageModel> schemas = model.getCompositeImages();
            List<SchemaModel> iList = null;
            if (schemas != null) {
                iList = new ArrayList<SchemaModel>(1);
                for (CompositeImageModel cim : schemas) {
                    ImageIcon icon = new ImageIcon(cim.getJpegByte());
                    SchemaModel sm = new SchemaModel();
                    sm.setIcon(icon);
                    cim.setJpegByte(null);
                    iList.add(sm);
                }
            }
            PhysioPaneRenderer renderer = new PhysioPaneRenderer(soaPane);
            renderer.renderPane(physioSpec, iList);
        }
    }
    
    private void setEditables(boolean b) {
        
        logger.debug("setEditables " + b);
        
        view.getZenChiryoNashi().setEnabled(b);
        view.getZenChiryoAri().setEnabled(b);
        
        view.getSurgery1().setEditable(b);
        view.getSurgery2().setEditable(b);
        view.getSurgery3().setEditable(b);
        view.getSurgeryDate1().setEditable(b);
        view.getSurgeryDate2().setEditable(b);
        view.getSurgeryDate3().setEditable(b);
        
        view.getRad1().setEditable(b);
        view.getRad2().setEditable(b);
        view.getRad3().setEditable(b);
        view.getRadDate1().setEditable(b);
        view.getRadDate2().setEditable(b);
        view.getRadDate3().setEditable(b);
        
        view.getOther1().setEditable(b);
        view.getOther2().setEditable(b);
        view.getOther3().setEditable(b);
        view.getOther4().setEditable(b);
        view.getOther5().setEditable(b);
        view.getOtherDate1().setEditable(b);
        view.getOtherDate2().setEditable(b);
        view.getOtherDate3().setEditable(b);
        view.getOtherDate4().setEditable(b);
        view.getOtherDate5().setEditable(b);
        
        view.getChemotherapy1().setEditable(b);
        view.getChemotherapy2().setEditable(b);
        view.getChemotherapy3().setEditable(b);
        view.getChemotherapy4().setEditable(b);
        view.getChemotherapy5().setEditable(b);
        view.getChemotherapy6().setEditable(b);
        view.getChemotherapyFrom1().setEditable(b);
        view.getChemotherapyFrom2().setEditable(b);
        view.getChemotherapyFrom3().setEditable(b);
        view.getChemotherapyFrom4().setEditable(b);
        view.getChemotherapyFrom5().setEditable(b);
        view.getChemotherapyFrom6().setEditable(b);
        view.getChemotherapyTo1().setEditable(b);
        view.getChemotherapyTo2().setEditable(b);
        view.getChemotherapyTo3().setEditable(b);
        view.getChemotherapyTo4().setEditable(b);
        view.getChemotherapyTo5().setEditable(b);
        view.getChemotherapyTo6().setEditable(b);
        
        view.getEvalNashi().setEnabled(b);
        view.getEvalAri().setEnabled(b);
        
        view.getSatueiDate1().setEditable(b);
        view.getSatueiDate2().setEditable(b);
        view.getLocation1().setEditable(b);
        view.getLocation2().setEditable(b);
        
        view.getCt1().setEnabled(b);
        view.getMri1().setEnabled(b);
        view.getXp1().setEnabled(b);
        view.getOtherImage1().setEditable(b);
        view.getCt2().setEnabled(b);
        view.getMri2().setEnabled(b);
        view.getXp2().setEnabled(b);
        view.getOtherImage2().setEditable(b);
        
        view.getGenpatuNashi().setEnabled(b);
        view.getGenpatuTanpatu().setEnabled(b);
        view.getGenpatuTahatu().setEnabled(b);
        view.getGennpatuSize1().setEditable(b);
        view.getGennpatuSize2().setEditable(b);
        
        view.getTazokiNashi().setEnabled(b);
        view.getTazokiTanpatu().setEnabled(b);
        view.getTazokiTahatu().setEnabled(b);
        view.getTazokiSize1().setEditable(b);
        view.getTazokiSize2().setEditable(b);
        
        view.getLymphNashi().setEnabled(b);
        view.getLymphTanpatu().setEnabled(b);
        view.getLymphTahatu().setEnabled(b);
        view.getLymphSize1().setEditable(b);
        view.getLymphSize2().setEditable(b);

        view.getHyotekiNashi().setEnabled(b);
        view.getHyotekiAri().setEnabled(b);
        
        view.getMesuableSum().setEditable(b);
        
        view.getUnmesuableNashi().setEnabled(b);
        view.getUnmesuableAri().setEnabled(b);
        view.getUnmesuableNote().setEditable(b);
        
        //view.getPhysioObjectiveNotes().setEditable(b);
        soaPane.setEditableProp(b);
        
        view.getImageObjectiveNotes().setEditable(b);
        
        view.getTokkiJiko().setEditable(b);
        
        view.getHeiyoNashi().setEnabled(b);
        view.getHeiyoAri().setEnabled(b);
        view.getHeiyoChiryo().setEditable(b);
        
        view.getCarePolicy().setEditable(b);
    }
   
    private void addListeners() {
        
        logger.debug("addListeners");
        
        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }
        };
        
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.processDirtyEvent();
            }
        };
        
        view.getZenChiryoNashi().addActionListener(al);
        view.getZenChiryoAri().addActionListener(al);
        
        view.getSurgery1().getDocument().addDocumentListener(dl);
        view.getSurgery2().getDocument().addDocumentListener(dl);
        view.getSurgery3().getDocument().addDocumentListener(dl);
        view.getSurgeryDate1().getDocument().addDocumentListener(dl);
        view.getSurgeryDate2().getDocument().addDocumentListener(dl);
        view.getSurgeryDate3().getDocument().addDocumentListener(dl);
        
        view.getRad1().getDocument().addDocumentListener(dl);
        view.getRad2().getDocument().addDocumentListener(dl);
        view.getRad3().getDocument().addDocumentListener(dl);
        view.getRadDate1().getDocument().addDocumentListener(dl);
        view.getRadDate2().getDocument().addDocumentListener(dl);
        view.getRadDate3().getDocument().addDocumentListener(dl);
        
        view.getOther1().getDocument().addDocumentListener(dl);
        view.getOther2().getDocument().addDocumentListener(dl);
        view.getOther3().getDocument().addDocumentListener(dl);
        view.getOther4().getDocument().addDocumentListener(dl);
        view.getOther5().getDocument().addDocumentListener(dl);
        view.getOtherDate1().getDocument().addDocumentListener(dl);
        view.getOtherDate2().getDocument().addDocumentListener(dl);
        view.getOtherDate3().getDocument().addDocumentListener(dl);
        view.getOtherDate4().getDocument().addDocumentListener(dl);
        view.getOtherDate5().getDocument().addDocumentListener(dl);
        
        view.getChemotherapy1().getDocument().addDocumentListener(dl);
        view.getChemotherapy2().getDocument().addDocumentListener(dl);
        view.getChemotherapy3().getDocument().addDocumentListener(dl);
        view.getChemotherapy4().getDocument().addDocumentListener(dl);
        view.getChemotherapy5().getDocument().addDocumentListener(dl);
        view.getChemotherapy6().getDocument().addDocumentListener(dl);
        view.getChemotherapyFrom1().getDocument().addDocumentListener(dl);
        view.getChemotherapyFrom2().getDocument().addDocumentListener(dl);
        view.getChemotherapyFrom3().getDocument().addDocumentListener(dl);
        view.getChemotherapyFrom4().getDocument().addDocumentListener(dl);
        view.getChemotherapyFrom5().getDocument().addDocumentListener(dl);
        view.getChemotherapyFrom6().getDocument().addDocumentListener(dl);
        view.getChemotherapyTo1().getDocument().addDocumentListener(dl);
        view.getChemotherapyTo2().getDocument().addDocumentListener(dl);
        view.getChemotherapyTo3().getDocument().addDocumentListener(dl);
        view.getChemotherapyTo4().getDocument().addDocumentListener(dl);
        view.getChemotherapyTo5().getDocument().addDocumentListener(dl);
        view.getChemotherapyTo6().getDocument().addDocumentListener(dl);
        
        view.getEvalNashi().addActionListener(al);
        view.getEvalAri().addActionListener(al);
        
        view.getSatueiDate1().getDocument().addDocumentListener(dl);
        view.getSatueiDate2().getDocument().addDocumentListener(dl);
        view.getLocation1().getDocument().addDocumentListener(dl);
        view.getLocation2().getDocument().addDocumentListener(dl);
        
        view.getCt1().addActionListener(al);
        view.getMri1().addActionListener(al);
        view.getXp1().addActionListener(al);
        view.getOtherImage1().getDocument().addDocumentListener(dl);
        view.getCt2().addActionListener(al);
        view.getMri2().addActionListener(al);
        view.getXp2().addActionListener(al);
        view.getOtherImage2().getDocument().addDocumentListener(dl);
        
        view.getGenpatuNashi().addActionListener(al);
        view.getGenpatuTanpatu().addActionListener(al);
        view.getGenpatuTahatu().addActionListener(al);
        view.getGennpatuSize1().getDocument().addDocumentListener(dl);
        view.getGennpatuSize2().getDocument().addDocumentListener(dl);
        
        view.getTazokiNashi().addActionListener(al);
        view.getTazokiTanpatu().addActionListener(al);
        view.getTazokiTahatu().addActionListener(al);
        view.getTazokiSize1().getDocument().addDocumentListener(dl);
        view.getTazokiSize2().getDocument().addDocumentListener(dl);
        
        view.getLymphNashi().addActionListener(al);
        view.getLymphTanpatu().addActionListener(al);
        view.getLymphTahatu().addActionListener(al);
        view.getLymphSize1().getDocument().addDocumentListener(dl);
        view.getLymphSize2().getDocument().addDocumentListener(dl);

        view.getHyotekiNashi().addActionListener(al);
        view.getHyotekiAri().addActionListener(al);
        
        view.getMesuableSum().getDocument().addDocumentListener(dl);
        
        view.getUnmesuableNashi().addActionListener(al);
        view.getUnmesuableAri().addActionListener(al);
        view.getUnmesuableNote().getDocument().addDocumentListener(dl);
        
        //view.getPhysioObjectiveNotes().getDocument().addDocumentListener(dl);
        
        view.getImageObjectiveNotes().getDocument().addDocumentListener(dl);
        
        view.getTokkiJiko().getDocument().addDocumentListener(dl);
        
        view.getHeiyoNashi().addActionListener(al);
        view.getHeiyoAri().addActionListener(al);
        view.getHeiyoChiryo().getDocument().addDocumentListener(dl);
        
        view.getCarePolicy().getDocument().addDocumentListener(dl);
        
        // Focus
        view.getSurgery1().addFocusListener(AutoKanjiListener.getInstance());
        view.getSurgery2().addFocusListener(AutoKanjiListener.getInstance());
        view.getSurgery3().addFocusListener(AutoKanjiListener.getInstance());
        view.getSurgeryDate1().addFocusListener(AutoRomanListener.getInstance());
        view.getSurgeryDate2().addFocusListener(AutoRomanListener.getInstance());
        view.getSurgeryDate3().addFocusListener(AutoRomanListener.getInstance());
        
        view.getRad1().addFocusListener(AutoKanjiListener.getInstance());
        view.getRad2().addFocusListener(AutoKanjiListener.getInstance());
        view.getRad3().addFocusListener(AutoKanjiListener.getInstance());
        view.getRadDate1().addFocusListener(AutoRomanListener.getInstance());
        view.getRadDate2().addFocusListener(AutoRomanListener.getInstance());
        view.getRadDate3().addFocusListener(AutoRomanListener.getInstance());
        
        view.getOther1().addFocusListener(AutoKanjiListener.getInstance());
        view.getOther2().addFocusListener(AutoKanjiListener.getInstance());
        view.getOther3().addFocusListener(AutoKanjiListener.getInstance());
        view.getOther4().addFocusListener(AutoKanjiListener.getInstance());
        view.getOther5().addFocusListener(AutoKanjiListener.getInstance());
        view.getOtherDate1().addFocusListener(AutoRomanListener.getInstance());
        view.getOtherDate2().addFocusListener(AutoRomanListener.getInstance());
        view.getOtherDate3().addFocusListener(AutoRomanListener.getInstance());
        view.getOtherDate4().addFocusListener(AutoRomanListener.getInstance());
        view.getOtherDate5().addFocusListener(AutoRomanListener.getInstance());
        
        view.getChemotherapy1().addFocusListener(AutoKanjiListener.getInstance());
        view.getChemotherapy2().addFocusListener(AutoKanjiListener.getInstance());
        view.getChemotherapy3().addFocusListener(AutoKanjiListener.getInstance());
        view.getChemotherapy4().addFocusListener(AutoKanjiListener.getInstance());
        view.getChemotherapy5().addFocusListener(AutoKanjiListener.getInstance());
        view.getChemotherapy6().addFocusListener(AutoKanjiListener.getInstance());
        view.getChemotherapyFrom1().addFocusListener(AutoRomanListener.getInstance());
        view.getChemotherapyFrom2().addFocusListener(AutoRomanListener.getInstance());
        view.getChemotherapyFrom3().addFocusListener(AutoRomanListener.getInstance());
        view.getChemotherapyFrom4().addFocusListener(AutoRomanListener.getInstance());
        view.getChemotherapyFrom5().addFocusListener(AutoRomanListener.getInstance());
        view.getChemotherapyFrom6().addFocusListener(AutoRomanListener.getInstance());
        view.getChemotherapyTo1().addFocusListener(AutoRomanListener.getInstance());
        view.getChemotherapyTo2().addFocusListener(AutoRomanListener.getInstance());
        view.getChemotherapyTo3().addFocusListener(AutoRomanListener.getInstance());
        view.getChemotherapyTo4().addFocusListener(AutoRomanListener.getInstance());
        view.getChemotherapyTo5().addFocusListener(AutoRomanListener.getInstance());
        view.getChemotherapyTo6().addFocusListener(AutoRomanListener.getInstance());
        
        view.getSatueiDate1().addFocusListener(AutoRomanListener.getInstance());
        view.getSatueiDate2().addFocusListener(AutoRomanListener.getInstance());
        view.getLocation1().addFocusListener(AutoKanjiListener.getInstance());
        view.getLocation2().addFocusListener(AutoKanjiListener.getInstance());
        
        view.getOtherImage1().addFocusListener(AutoKanjiListener.getInstance());
        view.getOtherImage2().addFocusListener(AutoKanjiListener.getInstance());
        
        view.getGennpatuSize1().addFocusListener(AutoRomanListener.getInstance());
        view.getGennpatuSize2().addFocusListener(AutoRomanListener.getInstance());
        
        view.getTazokiSize1().addFocusListener(AutoRomanListener.getInstance());
        view.getTazokiSize2().addFocusListener(AutoRomanListener.getInstance());
        
        view.getLymphSize1().addFocusListener(AutoRomanListener.getInstance());
        view.getLymphSize2().addFocusListener(AutoRomanListener.getInstance());
        
        view.getMesuableSum().addFocusListener(AutoRomanListener.getInstance());
        
        view.getUnmesuableNote().addFocusListener(AutoKanjiListener.getInstance());
        
        //view.getPhysioObjectiveNotes().addFocusListener(AutoKanjiListener.getInstance());
        
        view.getImageObjectiveNotes().addFocusListener(AutoKanjiListener.getInstance());
        
        view.getTokkiJiko().addFocusListener(AutoKanjiListener.getInstance());
        
        view.getHeiyoChiryo().addFocusListener(AutoKanjiListener.getInstance());
        
        view.getCarePolicy().addFocusListener(AutoKanjiListener.getInstance());
        
        // カレンダ
        PopupListener p1 = new PopupListener(view.getSurgeryDate1());
        PopupListener p2 = new PopupListener(view.getSurgeryDate2());
        PopupListener p3 = new PopupListener(view.getSurgeryDate3());
        PopupListener p4 = new PopupListener(view.getRadDate1());
        PopupListener p5 = new PopupListener(view.getRadDate2());
        PopupListener p6 = new PopupListener(view.getRadDate3());
        PopupListener p7 = new PopupListener(view.getOtherDate1());
        PopupListener p8 = new PopupListener(view.getOtherDate2());
        PopupListener p9 = new PopupListener(view.getOtherDate3());
        PopupListener p10 = new PopupListener(view.getOtherDate4());
        PopupListener p11 = new PopupListener(view.getOtherDate5());
        PopupListener p12 = new PopupListener(view.getChemotherapyFrom1());
        PopupListener p13 = new PopupListener(view.getChemotherapyFrom2());
        PopupListener p14 = new PopupListener(view.getChemotherapyFrom3());
        PopupListener p15 = new PopupListener(view.getChemotherapyFrom4());
        PopupListener p16 = new PopupListener(view.getChemotherapyFrom5());
        PopupListener p17 = new PopupListener(view.getChemotherapyFrom6());
        PopupListener p18 = new PopupListener(view.getChemotherapyTo1());
        PopupListener p19 = new PopupListener(view.getChemotherapyTo2());
        PopupListener p20 = new PopupListener(view.getChemotherapyTo3());
        PopupListener p21 = new PopupListener(view.getChemotherapyTo4());
        PopupListener p22 = new PopupListener(view.getChemotherapyTo5());
        PopupListener p23 = new PopupListener(view.getChemotherapyTo6());
        PopupListener p24 = new PopupListener(view.getSatueiDate1());
        PopupListener p25 = new PopupListener(view.getSatueiDate2());
    }
    
        
    protected abstract class State {
        
        public abstract void enter();
    }
    
    class EmptyState extends State {
        
        @Override
        public void enter() {
            logger.debug("enter EmptyState");
            setEditables(true);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }
    
    class CleanState extends State {
        
        @Override
        public void enter() {
            logger.debug("enter cleanState");
            setEditables(false);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, true); // OK
        }
    }
    
    class DirtyState extends State {
        
        @Override
        public void enter() {
            logger.debug("enter DirtyState");
            getContext().enabledAction(GUIConst.ACTION_SAVE, true);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }
    
    class StartEditing extends State {
        
        @Override
        public void enter() {
            logger.debug("enter StartEditingState");
            setEditables(true);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }
    
    /**
     * StateContext クラス。
     */
    class StateMgr {
        
        private EmptyState emptyState = new EmptyState();
        private CleanState cleanState = new CleanState();
        private DirtyState dirtyState = new DirtyState();
        private StartEditing startEditing = new StartEditing();
        private State curState;
        
        public StateMgr() {
        }
        
        public void start() {
            if (empty) {
                processEmptyEvent();
            } else {
                processCleanEvent();
            }
            addListeners();
        }
        
        public void enter() {
            curState.enter();
        }
        
        public void processEmptyEvent() {
            logger.debug("processEmptyEvent");
            if (curState != emptyState) {
                curState = emptyState;
                enter();
            }
        }
        
        public void processCleanEvent() {
            logger.debug("processCleanEvent");
            if (curState != cleanState) {
                curState = cleanState;
                enter();
            }
        }
        
        public void processDirtyEvent() {
            logger.debug("processDirtyEvent");
            boolean newDirty = getAreaValue(view.getCarePolicy()) != null 
                             ? true
                             : false;
            if (isDirtyState() != newDirty) {
                curState = newDirty ? dirtyState : curState;
                enter();
            }
        }
        
        public void processSavedEvent() {
            logger.debug("processSavedEvent");
            curState = cleanState;
            enter();
        }
        
        public void processModifyEvent() {
            logger.debug("processModifyEvent");
            curState = startEditing;
            enter();
        }
        
        public boolean isDirtyState() {
            return curState == dirtyState ? true : false;
        }
    }
    
    class PopupListener extends MouseAdapter implements PropertyChangeListener {
        
        private JPopupMenu popup;
        
        private JTextField tf;
        
        public PopupListener(JTextField tf) {
            this.tf = tf;
            tf.addMouseListener(PopupListener.this);
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        private void maybeShowPopup(MouseEvent e) {
            
            if (e.isPopupTrigger()) {
                popup = new JPopupMenu();
                CalendarCardPanel cc = new CalendarCardPanel(ClientContext.getEventColorTable());
                cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
                cc.setCalendarRange(new int[] { -12, 0 });
                popup.insert(cc, 0);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(CalendarCardPanel.PICKED_DATE)) {
                SimpleDate sd = (SimpleDate) e.getNewValue();
                String mmldate = SimpleDate.simpleDateToMmldate(sd);     
                tf.setText(mmldate);
                popup.setVisible(false);
                popup = null;
            }
        }
    }
    
    /**
     * Courtesy of Junzo SATO
     */
    private byte[] getJPEGByte(Image image) {

        byte[] ret = null;
        BufferedOutputStream writer = null;

        try {

            JPanel myPanel = getUI();
            Dimension d = new Dimension(image.getWidth(myPanel), image.getHeight(myPanel));
            BufferedImage bf = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
            Graphics g = bf.getGraphics();
            g.setColor(Color.white);
            g.drawImage(image, 0, 0, d.width, d.height, myPanel);

            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            writer = new BufferedOutputStream(bo);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(writer);
            encoder.encode(bf);
            writer.flush();
            writer.close();
            ret = bo.toByteArray();

        } catch (IOException e) {
            e.printStackTrace(System.err);
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e2) {
                }
            }
        }
        return ret;
    }

    private ImageIcon adjustImageSize(ImageIcon icon, Dimension dim) {

        if ((icon.getIconHeight() > dim.height) ||
                (icon.getIconWidth() > dim.width)) {
            Image img = icon.getImage();
            float hRatio = (float) icon.getIconHeight() / dim.height;
            float wRatio = (float) icon.getIconWidth() / dim.width;
            int h,
             w;
            if (hRatio > wRatio) {
                h = dim.height;
                w = (int) (icon.getIconWidth() / hRatio);
            } else {
                w = dim.width;
                h = (int) (icon.getIconHeight() / wRatio);
            }
            img = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            return icon;
        }
    }
}

