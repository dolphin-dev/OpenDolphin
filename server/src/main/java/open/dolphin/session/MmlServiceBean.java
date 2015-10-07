package open.dolphin.session;

import java.beans.XMLDecoder;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.*;
import open.dolphin.touch.converter.IPatientModel;
import open.dolphin.msg.MMLHelper;
import open.dolphin.msg.PatientHelper;
import open.dolphin.msg.VelocityHelper;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author kazushi
 */
@Named
@Stateless
public class MmlServiceBean {
    
    // parameter
    private static final String KARTE_ID = "karteId";
    private static final String ID = "id";
    private static final String FID = "fid";
    private static final String PK = "pk";
    
    private static final String QUERY_DIAGNOSIS_BY_KARTE = "from RegisteredDiagnosisModel r where r.karte.id=:karteId";
    private static final String QUERY_KARTE = "from KarteBean k where k.patient.id=:pk";
    private static final String QUERY_MODULE_BY_DOC_ID = "from ModuleModel m where m.document.id=:id";
    private static final String QUERY_SCHEMA_BY_DOC_ID = "from SchemaModel i where i.document.id=:id";
    private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id";
    
    private static final String QUERY_INSURANCE_BY_PATIENT_PK = "from HealthInsuranceModel h where h.patient.id=:pk";
    
    private static final String QUERY_ITEM_BY_MID = "from NLaboItem l where l.laboModule.id=:mid order by groupCode,parentCode,itemCode";
    private static final String QUERY_ITEM_BY_MID_ORDERBY_SORTKEY = "from NLaboItem l where l.laboModule.id=:mid order by l.sortKey";
    //private static final String QUERY_ITEM_BY_FIDPID_ITEMCODE = "from NLaboItem l where l.patientId=:fidPid and l.itemCode=:itemCode order by l.sampleDate desc";
    private static final String MID = "mid";
    //private static final String ITEM_CODE = "itemCode";
    private static final String WOLF = "WOLF";
    
    //private static final String QUERY_LETTER_BY_KARTE_ID = "from LetterModule l where l.karte.id=:karteId";
    //private static final String QUERY_LETTER_BY_ID = "from LetterModule l where l.id=:id";
    private static final String QUERY_ITEM_BY_ID = "from LetterItem l where l.module.id=:id";
    private static final String QUERY_TEXT_BY_ID = "from LetterText l where l.module.id=:id";
    private static final String QUERY_DATE_BY_ID = "from LetterDate l where l.module.id=:id";
    
    
    private static final String PATIENT_HELPER_OBJECT = "patientHelper";
    private static final String PATIENT_HELPER_TEMPLATE = "patientHelper.vm";
    private static final String PATIENT_HELPER_ENCODING = "SHIFT_JIS";
    private static final String MML_HELPER_OBJECT = "mmlHelper";
    private static final String MML_HELPER_TEMPLATE = "mml2.3Helper.vm";
    private static final String MML_HELPER_ENCODING = "SHIFT_JIS";
    
    @PersistenceContext
    private EntityManager em;
    
    public void dumpPatientDiagnosisToMML(String facility, int index, long pk) {
        
        String query = "from PatientModel p where p.id=:pk";
        PatientModel  pm = (PatientModel)em.createQuery(query)
                .setParameter(PK, pk)
                .getSingleResult();
            
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("----------------------------").append("\n");
            sb.append("処理番号 = ").append(index+1).append("\n");
            sb.append("患者ID = ").append(pm.getPatientId()).append("\n");
            sb.append("患者氏名 = ").append(pm.getFullName()).append("\n");

            // 健康保険を取得する
            List<HealthInsuranceModel> insurances
                    = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                    .setParameter(PK, pm.getId()).getResultList();

            // PVTHealthInsurance
            for (HealthInsuranceModel hm: insurances) {
                PVTHealthInsuranceModel pvtH = (PVTHealthInsuranceModel)xmlDecode(hm.getBeanBytes());
                pm.addPvtHealthInsurance(pvtH);
                sb.append("健康保険 = ").append(pvtH.getInsuranceClass()).append("\n");
            }

            // 患者のカルテを取得する
            List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                                .setParameter(PK, pm.getId())
                                .getResultList();
            KarteBean karte = kartes.get(0);
            long karteId = karte.getId();

            // 病名を取得する
            List<RegisteredDiagnosisModel> diagList = (List<RegisteredDiagnosisModel>)em.createQuery(QUERY_DIAGNOSIS_BY_KARTE)
                    .setParameter(KARTE_ID, karteId)
                    .getResultList();

            for (RegisteredDiagnosisModel dm : diagList) {
                sb.append("病名 = ").append(dm.getDiagnosis()).append("\n");
            }

            log(sb.toString());

            PatientHelper helper = new PatientHelper();
            helper.setPatient(pm);
            helper.setDiagnosisList(diagList);
            helper.setFacility(facility);

            // Create MML instance
            VelocityContext context = VelocityHelper.getContext();
            context.put(PATIENT_HELPER_OBJECT, helper);
            StringWriter sw = new StringWriter();
            try (BufferedWriter bw = new BufferedWriter(sw)) {
                Velocity.mergeTemplate(PATIENT_HELPER_TEMPLATE, PATIENT_HELPER_ENCODING, context, bw);
                bw.flush();
            }
            String mml = sw.toString();
            if (false) {
                log(mml);
            }

            // Byte data
            byte[] data = mml.getBytes("UTF-8");
            ByteBuffer buf = ByteBuffer.allocate(data.length);
            buf.clear();
            buf.put(data);
            buf.flip();

            // MML File
            File f = getPatientMmlFile(pm.getPatientId());
            FileOutputStream fout = new FileOutputStream(f);

            try ( // Channel
                    FileChannel outChanel = fout.getChannel()) {
                while(buf.hasRemaining()) {
                    outChanel.write(buf);
                }
            }

        } catch (IOException | ResourceNotFoundException | ParseErrorException | MethodInvocationException e) {
            e.printStackTrace(System.err);
            StringBuilder sb = new StringBuilder();
            sb.append(index).append(" は例外が発生しました");
            Logger.getLogger("open.dolphin").fine(sb.toString());
        }
    }    
    
    public Long getFacilityPatientCount(String facilityId) {

        Long ret = (Long)em.createQuery("select count(*) from PatientModel p where p.facilityId=:fid")
            .setParameter(FID, facilityId)
            .getSingleResult();

        StringBuilder sb = new StringBuilder();
        sb.append(facilityId).append(" の患者数 = ").append(String.valueOf(ret));
        log(sb.toString());
        
        return ret;
    }
    
    public void dumpPatientDiagnosisToMML(String facilityId, int index) {
        
        FacilityModel facility = (FacilityModel)em.createQuery("from FacilityModel f where f.facilityId=:fid")
                                   .setParameter(FID, facilityId)
                                   .getSingleResult();
        
        StringBuilder sb = new StringBuilder();
        sb.append("from PatientModel p where p.facilityId=:fid order by patientId");
        String query = sb.toString();
        
        List<PatientModel>  list = em.createQuery(query)
                .setParameter(FID, facilityId)
                .setFirstResult(index)
                .setMaxResults(1)
                .getResultList();
        
        if (list.isEmpty()) {
            return;
        }
        
        for (PatientModel pm : list) {
            
            try {
            
                sb = new StringBuilder();
                sb.append("\n");
                sb.append("----------------------------").append("\n");
                sb.append("処理番号 = ").append(index+1).append("\n");
                sb.append("患者ID = ").append(pm.getPatientId()).append("\n");
                sb.append("患者氏名 = ").append(pm.getFullName()).append("\n");

                // 健康保険を取得する
                List<HealthInsuranceModel> insurances
                        = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                        .setParameter(PK, pm.getId()).getResultList();

                // PVTHealthInsurance
                for (HealthInsuranceModel hm: insurances) {
                    PVTHealthInsuranceModel pvtH = (PVTHealthInsuranceModel)xmlDecode(hm.getBeanBytes());
                    pm.addPvtHealthInsurance(pvtH);
                    sb.append("健康保険 = ").append(pvtH.getInsuranceClass()).append("\n");
                }

                // 患者のカルテを取得する
                List<KarteBean> kartes = em.createQuery(QUERY_KARTE)
                                    .setParameter(PK, pm.getId())
                                    .getResultList();
                KarteBean karte = kartes.get(0);
                long karteId = karte.getId();

                // 病名を取得する
                List<RegisteredDiagnosisModel> diagList = (List<RegisteredDiagnosisModel>)em.createQuery(QUERY_DIAGNOSIS_BY_KARTE)
                        .setParameter(KARTE_ID, karteId)
                        .getResultList();

                for (RegisteredDiagnosisModel dm : diagList) {
                    sb.append("病名 = ").append(dm.getDiagnosis()).append("\n");
                }

                log(sb.toString());

                PatientHelper helper = new PatientHelper();
                helper.setPatient(pm);
                helper.setDiagnosisList(diagList);
                helper.setFacility(facility.getFacilityId());

                // Create MML instance
                VelocityContext context = VelocityHelper.getContext();
                context.put(PATIENT_HELPER_OBJECT, helper);
                StringWriter sw = new StringWriter();
                try (BufferedWriter bw = new BufferedWriter(sw)) {
                    Velocity.mergeTemplate(PATIENT_HELPER_TEMPLATE, PATIENT_HELPER_ENCODING, context, bw);
                    bw.flush();
                }
                String mml = sw.toString();
                if (false) {
                    log(mml);
                }
                
                // Byte data
                byte[] data = mml.getBytes("UTF-8");
                ByteBuffer buf = ByteBuffer.allocate(data.length);
                buf.clear();
                buf.put(data);
                buf.flip();
                
                // MML File
                File f = getPatientMmlFile(pm.getPatientId());
                FileOutputStream fout = new FileOutputStream(f);
                
                try ( // Channel
                        FileChannel outChanel = fout.getChannel()) {
                    while(buf.hasRemaining()) {
                        outChanel.write(buf);
                    }
                }
                
            } catch (IOException | ResourceNotFoundException | ParseErrorException | MethodInvocationException e) {
                e.printStackTrace(System.err);
                sb = new StringBuilder();
                sb.append(index).append(" は例外が発生しました");
                Logger.getLogger("open.dolphin").fine(sb.toString());
            }
        }
    }
    
    public List<Long> getFacilityDocumentList(String fid) {
        
        String query = "from DocumentModel d where d.creator.userId like :fid";
        List<DocumentModel>  list = em.createQuery(query)
                .setParameter(FID, fid+":%")
                .getResultList();
        
        StringBuilder sb = new StringBuilder();
        sb.append(fid).append(" のカルテ件数 = ").append(String.valueOf(list.size()));
        log(sb.toString());
        
        List<Long> ret = new ArrayList<>();
        for (DocumentModel dm : list) {
            ret.add(dm.getId());
        }
        
        return ret;
    }
       
    public void dumpDocumentToMML(int index, long pk) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("from DocumentModel d where d.id=:pk");
        String query = sb.toString();
        
        DocumentModel dm = (DocumentModel)em.createQuery(query)
                .setParameter(PK, pk)
                .getSingleResult();
        
        try {

            dm.toDetuch();

            // ModuleBean を取得する
            List modules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
            .setParameter(ID, dm.getId())
            .getResultList();

            // decode
            for (Iterator iter = modules.iterator();iter.hasNext();) {
                ModuleModel mm = (ModuleModel)iter.next();
                mm.setModel((IInfoModel)this.xmlDecode(mm.getBeanBytes()));
            }

            dm.setModules(modules);

            // SchemaModel を取得する
            List images = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
            .setParameter(ID, dm.getId())
            .getResultList();
            dm.setSchema(images);

            // Helper
            MMLHelper helper = new MMLHelper();
            helper.setDocument(dm);
            helper.buildText();

            //------------------------------------------------------------------
            sb = new StringBuilder();
            sb.append("\n");
            sb.append("----------------------------").append("\n");
            sb.append("処理番号 = ").append(index+1).append("\n");
            sb.append("患者ID = ").append(dm.getKarteBean().getPatientModel().getPatientId()).append("\n");
            sb.append("担当医ID = ").append(dm.getUserModel().getUserId()).append("\n");
            sb.append("Doc ID = ").append(dm.getDocInfoModel().getDocId()).append("\n");
            sb.append("最初の確定日 = ").append(dm.getDocInfoModel().getFirstConfirmDate()).append("\n");
            sb.append("確定日 = ").append(dm.getDocInfoModel().getConfirmDate()).append("\n");
            sb.append("文書 status = ").append(dm.getStatus()).append("\n");
            int bCount = helper.getClaimBundle()!=null ? helper.getClaimBundle().size() : 0;
            sb.append("バンドル数 = ").append(bCount).append("\n");
            int scCount = dm.getSchema()!=null ? dm.getSchema().size() : 0;
            sb.append("シェーマ数 = ").append(scCount);
            log(sb.toString());
            //------------------------------------------------------------------

            // Create MML instance
            VelocityContext context = VelocityHelper.getContext();
            context.put(MML_HELPER_OBJECT, helper);
            StringWriter sw = new StringWriter();
            try (BufferedWriter bw = new BufferedWriter(sw)) {
                Velocity.mergeTemplate(MML_HELPER_TEMPLATE, MML_HELPER_ENCODING, context, bw);
                bw.flush();
            }
            String mml = sw.toString();
            if (false) {
                log(mml);
            }

            // Byte data
            byte[] data = mml.getBytes("UTF-8");
            ByteBuffer buf = ByteBuffer.allocate(data.length);
            buf.clear();
            buf.put(data);
            buf.flip();

            File f = getKarteMmlFile(dm.getKarteBean().getPatientModel().getPatientId(),dm.getDocInfoModel().getDocId());
            FileOutputStream fout = new FileOutputStream(f);

            try ( // Channel
                    FileChannel outChanel = fout.getChannel()) {
                while(buf.hasRemaining()) {
                    outChanel.write(buf);
                }
            }

            // Scheam file
            if (dm.getSchema()!=null && dm.getSchema().size()>0) {

                for (SchemaModel sm : dm.getSchema()) {

                    // ByteBuf
                    ByteBuffer scbuf = ByteBuffer.allocate(sm.getJpegByte().length);
                    scbuf.clear();
                    scbuf.put(sm.getJpegByte());
                    scbuf.flip();

                    // File
                    File scf = getSchemaFile(dm.getKarteBean().getPatientModel().getPatientId(), sm.getExtRefModel().getHref());
                    FileOutputStream scfout = new FileOutputStream(scf);

                    try ( // Channel
                            FileChannel scoutChanel = scfout.getChannel()) {
                        while(scbuf.hasRemaining()) {
                            scoutChanel.write(scbuf);
                        }
                    }
                }
            }

        } catch (IOException | ResourceNotFoundException | ParseErrorException | MethodInvocationException e) {
            e.printStackTrace(System.err);
            sb = new StringBuilder();
            sb.append(index).append(" は例外が発生しました");
            Logger.getLogger("open.dolphin").fine(sb.toString());
        }
    }
        
    public Long getFacilityDocumentCount(String facilityId) {

        Long ret = (Long)em.createQuery("select count(*) from DocumentModel d where d.creator.userId like :fid")
            .setParameter(FID, facilityId+":%")
            .getSingleResult();

        StringBuilder sb = new StringBuilder();
        sb.append(facilityId).append(" のカルテ件数 = ").append(String.valueOf(ret));
        log(sb.toString());
        
        return ret;
    }
    
    public void dumpDocumentToMML(String facilityId, int index) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("from DocumentModel d where d.creator.userId like :fid order by id");
        String query = sb.toString();
        
        List<DocumentModel>  list = em.createQuery(query)
                .setParameter(FID, facilityId+":%")
                .setFirstResult(index)
                .setMaxResults(1)
                .getResultList();
        
        if (list.isEmpty()) {
            return;
        }
        
        // ループする
        for (DocumentModel dm : list) {
            
            try {
            
                dm.toDetuch();
                
                // ModuleBean を取得する
                List modules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
                .setParameter(ID, dm.getId())
                .getResultList();
                
                // decode
                for (Iterator iter = modules.iterator();iter.hasNext();) {
                    ModuleModel mm = (ModuleModel)iter.next();
                    mm.setModel((IInfoModel)this.xmlDecode(mm.getBeanBytes()));
                }
                
                dm.setModules(modules);

                // SchemaModel を取得する
                List images = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
                .setParameter(ID, dm.getId())
                .getResultList();
                dm.setSchema(images);

                // Helper
                MMLHelper helper = new MMLHelper();
                helper.setDocument(dm);
                helper.buildText();
                
                //------------------------------------------------------------------
                sb = new StringBuilder();
                sb.append("\n");
                sb.append("----------------------------").append("\n");
                sb.append("処理番号 = ").append(index+1).append("\n");
                sb.append("患者ID = ").append(dm.getKarteBean().getPatientModel().getPatientId()).append("\n");
                sb.append("担当医ID = ").append(dm.getUserModel().getUserId()).append("\n");
                sb.append("Doc ID = ").append(dm.getDocInfoModel().getDocId()).append("\n");
                sb.append("最初の確定日 = ").append(dm.getDocInfoModel().getFirstConfirmDate()).append("\n");
                sb.append("確定日 = ").append(dm.getDocInfoModel().getConfirmDate()).append("\n");
                sb.append("文書 status = ").append(dm.getStatus()).append("\n");
                int bCount = helper.getClaimBundle()!=null ? helper.getClaimBundle().size() : 0;
                sb.append("バンドル数 = ").append(bCount).append("\n");
                int scCount = dm.getSchema()!=null ? dm.getSchema().size() : 0;
                sb.append("シェーマ数 = ").append(scCount);
                log(sb.toString());
                //------------------------------------------------------------------

                // Create MML instance
                VelocityContext context = VelocityHelper.getContext();
                context.put(MML_HELPER_OBJECT, helper);
                StringWriter sw = new StringWriter();
                try (BufferedWriter bw = new BufferedWriter(sw)) {
                    Velocity.mergeTemplate(MML_HELPER_TEMPLATE, MML_HELPER_ENCODING, context, bw);
                    bw.flush();
                }
                String mml = sw.toString();
                if (false) {
                    log(mml);
                }
                
                // Byte data
                byte[] data = mml.getBytes("UTF-8");
                ByteBuffer buf = ByteBuffer.allocate(data.length);
                buf.clear();
                buf.put(data);
                buf.flip();
                
                File f = getKarteMmlFile(dm.getKarteBean().getPatientModel().getPatientId(),dm.getDocInfoModel().getDocId());
                FileOutputStream fout = new FileOutputStream(f);
                
                try ( // Channel
                        FileChannel outChanel = fout.getChannel()) {
                    while(buf.hasRemaining()) {
                        outChanel.write(buf);
                    }
                }
                
                // Scheam file
                if (dm.getSchema()!=null && dm.getSchema().size()>0) {
                    
                    for (SchemaModel sm : dm.getSchema()) {
                        
                        // ByteBuf
                        ByteBuffer scbuf = ByteBuffer.allocate(sm.getJpegByte().length);
                        scbuf.clear();
                        scbuf.put(sm.getJpegByte());
                        scbuf.flip();
                        
                        // File
                        File scf = getSchemaFile(dm.getKarteBean().getPatientModel().getPatientId(), sm.getExtRefModel().getHref());
                        FileOutputStream scfout = new FileOutputStream(scf);

                        try ( // Channel
                                FileChannel scoutChanel = scfout.getChannel()) {
                            while(scbuf.hasRemaining()) {
                                scoutChanel.write(scbuf);
                            }
                        }
                    }
                }
                
            } catch (IOException | ResourceNotFoundException | ParseErrorException | MethodInvocationException e) {
                e.printStackTrace(System.err);
                sb = new StringBuilder();
                sb.append(index).append(" は例外が発生しました");
                Logger.getLogger("open.dolphin").fine(sb.toString());
            }
        }
    }
    
    private File getPatientMmlFile(String pid) {
        // Directory
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir")).append("/mml/patient/");
        String pDir = sb.toString();
        File patientDir = new File(pDir);
        boolean test = patientDir.mkdirs();
        
        // XML file
        sb = new StringBuilder();
        sb.append(pid).append(".xml");
        File f = new File(patientDir, sb.toString());
        return f;
    }
    
    private File getKarteMmlFile(String pid, String docId) {
        // Directory
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir")).append("/mml/karte/");
        sb.append(pid);
        String pDir = sb.toString();
        File patientDir = new File(pDir);
        boolean test = patientDir.mkdirs();

        // XML file
        sb = new StringBuilder();
        sb.append(docId).append(".xml");
        File f = new File(patientDir, sb.toString());
        return f;
    }
    
    private File getSchemaFile(String pid, String href) {
        // Directory
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir")).append("/mml/karte/");
        sb.append(pid);
        String pDir = sb.toString();
        File patientDir = new File(pDir);
        boolean test = patientDir.mkdirs();

        // XML file
        File f = new File(patientDir, href);
        return f;
    }
    
    private Object xmlDecode(byte[] bytes)  {

        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                new ByteArrayInputStream(bytes)));

        return d.readObject();
    }
    
    private void log(String msg) {
        Logger.getLogger("open.dolphin").info(msg);
    }
    
    
    //-----------------------------------------------------------------------
    
    public void patientToJSON(int index, long pk) {
        
        String query = "from PatientModel p where p.id=:pk";
        PatientModel  pm = (PatientModel)em.createQuery(query)
                .setParameter(PK, pk)
                .getSingleResult();
            
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("----------------------------").append("\n");
            sb.append("処理番号 = ").append(index+1).append("\n");
            sb.append("患者ID = ").append(pm.getPatientId()).append("\n");
            sb.append("患者氏名 = ").append(pm.getFullName()).append("\n");
            log(sb.toString());

            // 健康保険を取得する
            List<HealthInsuranceModel> insurances
                    = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                    .setParameter(PK, pm.getId()).getResultList();
            pm.setHealthInsurances(insurances);
            
            // Converter
            IPatientModel conv = new IPatientModel();
            conv.setModel(pm);

            // JSON
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(conv);
            
            byte[] data = json.getBytes("UTF-8");
            ByteBuffer buf = ByteBuffer.allocate(data.length);
            buf.clear();
            buf.put(data);
            buf.flip();
            
            // MML File
            File f = getPatientMmlFile(pm.getPatientId());
            FileOutputStream fout = new FileOutputStream(f);

            try ( // Channel
                    FileChannel outChanel = fout.getChannel()) {
                while(buf.hasRemaining()) {
                    outChanel.write(buf);
                }
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
            StringBuilder sb = new StringBuilder();
            sb.append(index).append(" は例外が発生しました");
            Logger.getLogger("open.dolphin").fine(sb.toString());
        }
    }
    
    //--------------------------------------------------------------------------
    // Patient JSON
    //--------------------------------------------------------------------------
    public List<Long> getFacilityPatientList(String fid) {
        
        String query = "from PatientModel m where m.facilityId=:fid";
        List<PatientModel>  list = em.createQuery(query)
                .setParameter(FID, fid)
                .getResultList();
        
        StringBuilder sb = new StringBuilder();
        sb.append(fid).append(":患者総数 = ").append(String.valueOf(list.size()));
        log(sb.toString());
        
        List<Long> ret = new ArrayList<>();
        for (PatientModel pm : list) {
            ret.add(pm.getId());
        }
        
        return ret;
    }
    
    public PatientModel getPatientByPK(long pk) {
        String query = "from PatientModel m where m.id=:pk";
        PatientModel  pm = (PatientModel)em.createQuery(query)
                .setParameter(PK, pk)
                .getSingleResult();
        List<HealthInsuranceModel> insurances
                        = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                        .setParameter(PK, pm.getId()).getResultList();
        pm.setHealthInsurances(insurances);
        return pm;
    }
    
    //--------------------------------------------------------------------------
    // Disease JSON
    //--------------------------------------------------------------------------
    public List<Long> getFacilityDiseaseList(String fid) {
        
        String query = "from RegisteredDiagnosisModel m where m.creator.userId like :fid";
        List<RegisteredDiagnosisModel>  list = em.createQuery(query)
                .setParameter(FID, fid+":%")
                .getResultList();
        
        StringBuilder sb = new StringBuilder();
        sb.append(fid).append(":病名総数 = ").append(String.valueOf(list.size()));
        log(sb.toString());
        
        List<Long> ret = new ArrayList<>();
        for (RegisteredDiagnosisModel m : list) {
            ret.add(m.getId());
        }
        
        return ret;
    }
    
    public RegisteredDiagnosisModel getDiseaseByPK(long pk) {
        String query = "from RegisteredDiagnosisModel m where m.id=:pk";
        RegisteredDiagnosisModel  ret = (RegisteredDiagnosisModel)em.createQuery(query)
                .setParameter(PK, pk)
                .getSingleResult();
        return ret;
    }
    
    //--------------------------------------------------------------------------
    // Memo JSON
    //--------------------------------------------------------------------------
    public List<Long> getFacilityMemoList(String fid) {
        
        String query = "from PatientMemoModel m where m.creator.userId like :fid";
        List<PatientMemoModel>  list = em.createQuery(query)
                .setParameter(FID, fid+":%")
                .getResultList();
        
        StringBuilder sb = new StringBuilder();
        sb.append(fid).append(":メモ総数 = ").append(String.valueOf(list.size()));
        log(sb.toString());
        
        List<Long> ret = new ArrayList<>();
        for (PatientMemoModel m : list) {
            ret.add(m.getId());
        }
        
        return ret;
    }
    
    public PatientMemoModel getMemoByPK(long pk) {
        String query = "from PatientMemoModel m where m.id=:pk";
        PatientMemoModel  ret = (PatientMemoModel)em.createQuery(query)
                .setParameter(PK, pk)
                .getSingleResult();
        return ret;
    }
    
    //--------------------------------------------------------------------------
    // Observation JSON
    //--------------------------------------------------------------------------
    public List<Long> getFacilityObservationList(String fid) {
        
        String query = "from ObservationModel m where m.creator.userId like :fid";
        List<ObservationModel>  list = em.createQuery(query)
                .setParameter(FID, fid+":%")
                .getResultList();
        
        StringBuilder sb = new StringBuilder();
        sb.append(fid).append(":オブザべーション総数 = ").append(String.valueOf(list.size()));
        log(sb.toString());
        
        List<Long> ret = new ArrayList<>();
        for (ObservationModel m : list) {
            ret.add(m.getId());
        }
        
        return ret;
    }
    
    public ObservationModel getObservationByPK(long pk) {
        String query = "from ObservationModel m where m.id=:pk";
        ObservationModel  ret = (ObservationModel)em.createQuery(query)
                .setParameter(PK, pk)
                .getSingleResult();
        return ret;
    }
    
    //--------------------------------------------------------------------------
    // Karte JSON
    //--------------------------------------------------------------------------
    public List<Long> getFacilityKarteList(String fid) {
        
        String query = "from DocumentModel m where m.creator.userId like :fid";
        List<DocumentModel>  list = em.createQuery(query)
                .setParameter(FID, fid+":%")
                .getResultList();
        
        StringBuilder sb = new StringBuilder();
        sb.append(fid).append(":カルテ総数 = ").append(String.valueOf(list.size()));
        log(sb.toString());
        
        List<Long> ret = new ArrayList<>();
        for (DocumentModel m : list) {
            ret.add(m.getId());
        }
        
        return ret;
    }
    
    public DocumentModel getKarteByPK(long pk) {
        String query = "from DocumentModel m where m.id=:pk";
        DocumentModel  ret = (DocumentModel)em.createQuery(query)
                .setParameter(PK, pk)
                .getSingleResult();
        // ModuleBean を取得する
        List modules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
        .setParameter(ID, ret.getId())
        .getResultList();
        ret.setModules(modules);

        // SchemaModel を取得する
        List images = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
        .setParameter(ID, ret.getId())
        .getResultList();
        ret.setSchema(images);

        // AttachmentModel を取得する
        List attachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
        .setParameter(ID, ret.getId())
        .getResultList();
        ret.setAttachment(attachments);
        
        return ret;
    }
    
//    public void getKarteByPK(int index, long pk) {
//        String query = "from DocumentModel m where m.id=:pk";
//        DocumentModel  ret = (DocumentModel)em.createQuery(query)
//                .setParameter(PK, pk)
//                .getSingleResult();
//        // ModuleBean を取得する
//        List modules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
//        .setParameter(ID, ret.getId())
//        .getResultList();
//        ret.setModules(modules);
//
//        // SchemaModel を取得する
//        List images = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
//        .setParameter(ID, ret.getId())
//        .getResultList();
//        ret.setSchema(images);
//
//        // AttachmentModel を取得する
//        List attachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
//        .setParameter(ID, ret.getId())
//        .getResultList();
//        ret.setAttachment(attachments);
//        
//        ret.toDetuch();
//        
//        // Converter
//        DocumentModelConverter conv = new DocumentModelConverter();
//        conv.setModel(ret);
//        
//        // JSON
//        try {
//            String pid = "001";    //ret.getKarteBean().getPatientModel().getPatientId();
//            String docId = ret.getDocInfoModel().getDocId();
//            File f = getKarteFile(pid, docId);
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.writeValue(f, conv);
//            StringBuilder sb = new StringBuilder();
//            sb.append("---------------------\n");
//            sb.append("No. = ").append(index).append("\n");
//            sb.append("DocId = ").append(docId);
//            log(sb.toString());
//            
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//            StringBuilder sb = new StringBuilder();
//            sb.append("No. = ").append(index).append(": exception");
//            log(sb.toString());
//        }
//    }
    
    private File getKarteFile(String pid, String docId) {
        // Directory
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir")).append("/mml/karte/");
        sb.append(pid);
        String pDir = sb.toString();
        File patientDir = new File(pDir);
        boolean test = patientDir.mkdirs();

        // XML file
        sb = new StringBuilder();
        sb.append(docId).append(".txt");
        File f = new File(patientDir, sb.toString());
        return f;
    }
    
    //--------------------------------------------------------------------------
    // Letter JSON
    //--------------------------------------------------------------------------
    public List<Long> getFacilityLetterList(String fid) {
        
        String query = "from LetterModule m where m.creator.userId like :fid";
        List<LetterModule>  list = em.createQuery(query)
                .setParameter(FID, fid+":%")
                .getResultList();
        
        StringBuilder sb = new StringBuilder();
        sb.append(fid).append(":紹介状総数 = ").append(String.valueOf(list.size()));
        log(sb.toString());
        
        List<Long> ret = new ArrayList<>();
        for (LetterModule m : list) {
            ret.add(m.getId());
        }
        
        return ret;
    }
    
    public LetterModule getLetterByPK(long pk) {
        String query = "from LetterModule m where m.id=:pk";
        LetterModule  ret = (LetterModule)em.createQuery(query)
                .setParameter(PK, pk)
                .getSingleResult();
        // item
        List<LetterItem> items = (List<LetterItem>)
                 em.createQuery(QUERY_ITEM_BY_ID)
                   .setParameter(ID, ret.getId())
                   .getResultList();
        ret.setLetterItems(items);

        // text
        List<LetterText> texts = (List<LetterText>)
                 em.createQuery(QUERY_TEXT_BY_ID)
                   .setParameter(ID, ret.getId())
                   .getResultList();
        ret.setLetterTexts(texts);

        // date
        List<LetterDate> dates = (List<LetterDate>)
                 em.createQuery(QUERY_DATE_BY_ID)
                   .setParameter(ID, ret.getId())
                   .getResultList();
        ret.setLetterDates(dates);
        return ret;
    } 
    
    //--------------------------------------------------------------------------
    // Labtest JSON
    //--------------------------------------------------------------------------
    public List<Long> getFacilityLabtestList(String fid) {
        
        String query = "from NLaboModule m where m.creator.userId like :fid";
        List<NLaboModule>  list = em.createQuery(query)
                .setParameter(FID, fid+":%")
                .getResultList();
        
        StringBuilder sb = new StringBuilder();
        sb.append(fid).append(":検査総数 = ").append(String.valueOf(list.size()));
        log(sb.toString());
        
        List<Long> ret = new ArrayList<>();
        for (NLaboModule m : list) {
            ret.add(m.getId());
        }
        
        return ret;
    }
    
    public NLaboModule getLabtestByPK(long pk) {
        String query = "from NLaboModule m where m.id=:pk";
        NLaboModule  ret = (NLaboModule)em.createQuery(query)
                .setParameter(PK, pk)
                .getSingleResult();
        if (ret.getReportFormat() != null && ret.getReportFormat().equals(WOLF)) {
            List<NLaboItem> items = (List<NLaboItem>) em.createQuery(QUERY_ITEM_BY_MID_ORDERBY_SORTKEY).setParameter(MID, ret.getId()).getResultList();
            ret.setItems(items);

        } else {
            List<NLaboItem> items = (List<NLaboItem>) em.createQuery(QUERY_ITEM_BY_MID).setParameter(MID, ret.getId()).getResultList();
            ret.setItems(items);
        }
        return ret;
    } 
}
