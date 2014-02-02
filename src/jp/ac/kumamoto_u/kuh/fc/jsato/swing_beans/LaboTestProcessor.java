/*
 * LaboTestProcessor.java
 *
 * Created on 2001/11/04, 17:36
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.xml.parsers.* ;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import jp.ac.kumamoto_u.kuh.fc.jsato.math_mml.*;

import netscape.ldap.*;
//import netscape.ldap.beans.*;
/**
 *
 * @author  Junzo SATO
 * @copyright   Copyright (c) 2001, Junzo SATO. All rights reserved.
 */

public class LaboTestProcessor {
    private StatusBean status = null;
    private LDAPLoginBean ldap = null;
    private String srcDir = null;// src directory
    private String extRefsDir = null;// dst directory for extRefs files
    
    /** Creates new LaboTestProcessor */
    public LaboTestProcessor(StatusBean status) {
        this.status = status;
    }

    private void printlnStatus(String s) {
        if (status != null) {
            status.printlnStatus(s);
        } else {
            System.out.println(s);
        }
    }
    
    private void printStatus(String s) {
        if (status != null) {
            status.printStatus(s);
        } else {
            System.out.print(s);
        }
    }
    
    String laboTestDN = null;
    public void processFile(File file, LDAPLoginBean ldap, String srcDir, String extRefsDir) {
        this.ldap = ldap;
        this.srcDir = srcDir;
        this.extRefsDir = extRefsDir;
        
        //------------------------------------------
        /* 
        // Authenticate
        // update properties in the connection panel
        ldap.updateProperties();

        // suthenticate using properties in the panel
        LDAPSimpleAuth auth = new LDAPSimpleAuth();
        auth.setHost(ldap.getHost());
        auth.setPort(ldap.getPort());
        auth.setAuthDN(ldap.getBindDN());
        auth.setAuthPassword(ldap.getPassword());
        String res = auth.authenticate();
        if (res == "N") {
            printlnStatus("*** Authenticaton failed while processing file: " + file.getName());
            return;
        }
        */
        
        //------------------------------------------
        // Check the existence of the LaboTest directory in LDAP.
        // LaboTest
        laboTestDN = searchLaboTest();
        if (laboTestDN == null) {
            // directory was not found. then create it:-)
            if (false == createLaboTest()) {
                return;
            }
            
            // directory was created.
            // laboTestDN holds this new entry
        }
        if (laboTestDN == null) {
            printlnStatus("*** Check LaboTest directory in LDAP server.");
            return;
        }
        //------------------------------------------
        
        if (false == readMML(file)) {
            return;
        }
        
        handleMmlTree();
        // disconnectLDAP();
    }
    
    // MML Parser
    MMLDirector dr = null;
    MMLVisitor v = null;
    public boolean readMML(File file) {
        String path = file.getPath();
        if (path == null) {
            printlnStatus("Couldn't get path for file: " + file);
            return false;
        }
        printlnStatus("Processing file: " + file.getPath());

        // it is assumed that this xml file is MML
        try {
            // create parser
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            // we need to detect namespaces by SAX2.
            // this setting should be called explicitely for jdk1.4 or later
            saxFactory.setNamespaceAware(true);
            SAXParser parser = saxFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            
            if ( dr != null ) {
                dr.releaseDirector();
                dr = null;
            }                        
            dr = new MMLDirector(status);
            
            reader.setContentHandler(dr);
            // parse xml
            reader.parse(path);

            // create new file
            printlnStatus("Parsing done...\n");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /*
     public void writeMML(String path) {
        try {
            printlnStatus("Creating xml...");
            Writer w = new FileWriter(path);
            PrintWriter pw = new PrintWriter(w);
            // create visitor
            v = new MMLVisitor(pw);
            
            // get root object
            MMLObject obj = (MMLObject)dr.getMMLBuilder().getMmlTree().firstElement();
            if (obj == null) {
                printlnStatus("*** COULDN'T GET OBJECT");
            } else {
                v.visitMMLObject(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */

    String patientId = null;
    String patientDN = null;
    public void handleMmlTree() {
        if (getMml() == null) {
            printlnStatus("*** Mml object is null.");
            return;
        }
        // check Mml version
        if (getMml().getVersion() != null &&
            getMml().getVersion().equals("2.3") == false ) {
            printlnStatus("*** Unsupported MML version. The Dolphin Project is based on MML ver 2.3");
            return;
        }
        //------------
        // <MmlHeader>
        //------------
        if (getMml().getMmlHeader() == null) {
            printlnStatus("*** MmlHeader object is null.");
            return;
        }
        // mmlCiCreatorInfo -------------------------------------------
        // we don't detect creator info in the header
        
        // masterId -------------------------------------------------
        if (getMml().getMmlHeader().getMasterId() == null) {
            printlnStatus("*** masterId object is null.");
            return;
        }
        
        mmlCmId id = getMml().getMmlHeader().getMasterId().getId();
        if (id == null) {
            printlnStatus("*** id object is null.");
            return;
        }
        
        String type = " ";
        if (id.getMmlCmtype() != null) {
            type = id.getMmlCmtype();
        }
        printlnStatus("type: " + type);
        
        // checkDigitSchema
        String cdschema = " ";
        if (id.getMmlCmcheckDigitSchema() != null) {
            cdschema = id.getMmlCmcheckDigitSchema();
        }
        printlnStatus("cdschema: " + cdschema);
         
        // checkDigit
        String cdigit = " ";
        if (id.getMmlCmcheckDigit() != null) {
            cdigit = id.getMmlCmcheckDigit();
        }
        printlnStatus("cdigit: " + cdigit);
        
        // tableId
        String tableId = " ";
        if (id.getMmlCmtableId() != null) {
            tableId = id.getMmlCmtableId();
        }
        printlnStatus("tableId: " + tableId);
        
        // get the body of mmlCm:Id
        patientId = id.getText();
        if (patientId == null) {
            printlnStatus("*** Missing patient ID.");
            return;
        }
        patientId = patientId.trim();
        printlnStatus("patientId: " + patientId);
        
        // toc -------------------------------------------------------
        // scopePeriod -----------------------------------------------
        // encryptInfo -----------------------------------------------

        //////////////////////////////////////////////////////////////
        // LDAP
        /////////
        // if the patient entry doesn't exist, create new one
        patientDN = searchPatient(type);
        if (patientDN == null) {
            createPatient(type, tableId, cdschema, cdigit);// object laboPatient
        }

        if (patientDN == null) {
            printlnStatus("*** Check patient directory under LaboTest in LDAP server.");
            return;
        }
        /////////
        //////////////////////////////////////////////////////////////
        
        //-----------  
        // <MmlBody>
        //-----------
        // get the list of MmlModuleItem
        Vector v = getModules(getMml());
        if (v == null) {
            printlnStatus("*** Couldn't get any modules.");
            return;
        }
        printlnStatus("Number of Modules in MML instance: " + String.valueOf( v.size() ));
        
        int numTests = countSpecifiedModules(v, "test");
        printlnStatus("Number of LaboTest module: " + String.valueOf(numTests));
        if (numTests == 0) {
            printlnStatus("Processing ended because of no TestModule existence.");
            return;
        }
        
        // handle each modules
        handleTestModules(v);
    }
    
    //--------------------------------------------------------------------------
    // tools for deriving attributes and bodies
    public Mml getMml() {
        // get root object
        MMLObject obj = (MMLObject)dr.getMMLBuilder().getMmlTree().firstElement();
        if (obj == null) {
            printlnStatus("*** COULDN'T GET MML OBJECT");
            return null;
        } else {
            if (obj.getQName().equals("Mml")) {
                return (Mml)obj;
            } else {
                printlnStatus("*** ROOT OF MMLTREE IS NOT Mml");
                return null;
            }
        }
    }
    
    public Vector getModules(Mml obj) {
        if (obj == null) return null;
        
        // get the list of MmlModuleItem
        MmlBody body = obj.getMmlBody();
        if (body == null) {
            printlnStatus("*** MmlBody object is null.");
            return null;
        } else {
            return body.getMmlModuleItem();
        }
    }
    
    public String toPersonName(mmlNmName nm) {
        if (nm == null) {
            return "";
        }
        
        if (nm.getFullname() != null &&
            nm.getFullname().getText() != null) {
            return (nm.getFullname().getText().trim());
        } else if (nm.getFamily() != null &&
                    nm.getFamily().getText() != null &&
                    nm.getGiven() != null &&
                    nm.getGiven().getText() != null){
            return (nm.getFamily().getText().trim() + " " + nm.getGiven().getText().trim());
        } else {
            return "";
        }
    }
    
    public String toPhoneNumber(mmlPhPhone ph) {
        if (ph == null) {
            return "";
        }
        
        String telephoneNumber = "";
        // hey, hey, only one string for the phone number is enough:-)

        //String type = ph.getMmlPhtelEquipType(); // ignored (^^;;
        
        mmlPharea areaObj = ph.getArea();
        mmlPhcity cityObj = ph.getCity();
        //mmlPhcountry countryObj = ph.getCountry(); // ignored (^^;;
        mmlPhextension extensionObj = ph.getExtension();
        mmlPhmemo memoObj = ph.getMemo();
        mmlPhnumber numberObj = ph.getNumber();
        
        String area = "";
        if (areaObj != null && areaObj.getText() != null) {
            area = areaObj.getText().trim();
        }
        
        String city = "";
        if (cityObj != null && cityObj.getText() != null) {
            city = cityObj.getText().trim();
        }
        
        /*
         String country = "";
        if (countryObj != null && countryObj.getText() != null) {
            country = countryObj.getText().trim();
        }
         */
            
        String extension = "";
        if (extensionObj != null && extensionObj.getText() != null) {
            extension = extensionObj.getText().trim();
        }
        
        String memo = "";
        if (memoObj != null && memoObj.getText() != null) {
            memo = memoObj.getText().trim();
        }
        
        String number = "";
        if (numberObj != null && numberObj.getText() != null) {
            number = numberObj.getText().trim();
        }
        
        if (extension.equals("")) {
            telephoneNumber = area + " - " + city + " - " + number;
        } else {
            telephoneNumber = area + " - " + city + " - " + number + " Ext. " + extension;
        }
        
        if (memo.equals("") == false) {
            telephoneNumber = telephoneNumber + " " + memo;
        }
        
        return telephoneNumber;
    }
    
    public String toAddress( mmlAdAddress ad ) {
        if (ad == null) {
            return "";
        }
        
        // one string is enough to express the address. hey, hey!
        String address = "";
        
        String repCode = ad.getMmlAdrepCode();
        String addressClass = ad.getMmlAdaddressClass();
        String tableId = ad.getMmlAdtableId();

        mmlAdfull fullObj = ad.getFull();
        if (fullObj != null && fullObj.getText() != null) {
            String full = "";
            full = fullObj.getText().trim();
            address = full;
        } else {
            String prefecture = "";
            String city = "";
            String town = "";
            String homeNumber = "";
            
            mmlAdprefecture prefectureObj = ad.getPrefecture();
            if (prefectureObj != null && prefectureObj.getText() != null) {
                prefecture = prefectureObj.getText().trim();
            }
            
            mmlAdcity cityObj = ad.getCity();
            if (cityObj != null && cityObj.getText() != null) {
                city = cityObj.getText().trim();
            }
            
            mmlAdtown townObj = ad.getTown();
            if (townObj != null && townObj.getText() != null) {
                town = townObj.getText().trim();
            }
            
            mmlAdhomeNumber homeNumberObj = ad.getHomeNumber();
            if (homeNumberObj != null && homeNumberObj.getText() != null) {
                homeNumber = homeNumberObj.getText().trim();
            }
            
            address = prefecture + city + town + homeNumber;
        }
        
        String zip = "";
        mmlAdzip zipObj = ad.getZip();
        if (zipObj != null && zipObj.getText() != null) {
            zip = zipObj.getText().trim();
        }
        if (zip.equals("") == false) {
            address = zip + " " + address;
        }
        //mmlAdcountryCode countryCodeObj = ad.getCountryCode();// ignored:-)
        
        return address;
    }
    
    //--------------------------------------------------------------------------
    public int countSpecifiedModules(Vector v, String modulename) {
        int numModules = 0;
        Enumeration e = v.elements();
        while (e.hasMoreElements()) {
            // get MmlModuleItem
            MmlModuleItem item = (MmlModuleItem)e.nextElement();
            // get contentModuleType
            if (item.getDocInfo().getContentModuleType().equals(modulename) ) {
                ++numModules;
            }
        }
        return numModules;
    }

    String moduleDN = null;
    String laboTestResultDN = null;
    String laboItemDN = null;
    
    String checkString = null;
    public void handleTestModules(Vector v) {
        Enumeration e = v.elements();
        while (e.hasMoreElements()) {
            //--------------
            // MmlModuleItem
            //--------------
            MmlModuleItem item = (MmlModuleItem)e.nextElement();
            if (item == null) continue;
            
            // type ---------------------------------------------
            
            //----------
            // docInfo 
            //---------- 
            if (item.getDocInfo() == null) {
                printlnStatus("*** docInfo object is null.");
                continue;
            }
            
            // contentModuleType --------------------------------
            if (item.getDocInfo().getContentModuleType() == null) {
                printlnStatus("*** contentModuleType object is null.");
                continue;
            }
            if ( item.getDocInfo().getContentModuleType().equals("test") == false ) {
                // this module is not a lobo test
                printlnStatus("*** this module is not a labo test.");
                continue;
            }
            
            // moduleVersion ------------------------------------
            
            // securityLevel ------------------------------------
            
            // title --------------------------------------------
            String title = " ";
            if (item.getDocInfo().getTitle() != null) {
                if (item.getDocInfo().getTitle().getText() != null ) {
                    title = item.getDocInfo().getTitle().getText().trim();
                }
                printlnStatus("title: " + title);
                
                // generationPurpose ----------------------
                /* generationPurpose should be set to "reportTest" in MML0007 */
                if ( item.getDocInfo().getTitle().getGenerationPurpose() != null ) {
                    printlnStatus("generationPurpose: " + item.getDocInfo().getTitle().getGenerationPurpose());
                    if ( item.getDocInfo().getTitle().getGenerationPurpose().equals("reportTest") == false ) {
                        printlnStatus("*** generationPurpose doesn't equal to \"reportTest\".");
                    }
                } else {
                    printlnStatus("*** generationPurpose is not set.");
                }
            }
            //-------
            // docId
            //-------
            if (item.getDocInfo().getDocId() == null) {
                printlnStatus("*** docId object is null");
                continue;
            }
            
            // uid -------------------------------------------------------------
            String uniqueId = " ";
            if (item.getDocInfo().getDocId().getUid() != null &&
                item.getDocInfo().getDocId().getUid().getText() != null) {
                uniqueId = item.getDocInfo().getDocId().getUid().getText().trim();
            }
            printlnStatus("uid: " + uniqueId);
            // parentId --------------------------------------------------------
            
            // groupId ---------------------------------------------------------
            String gID = " ";
            Vector gid = item.getDocInfo().getDocId().getGroupId();
            if ( gid != null && gid.size() > 0) {
                printlnStatus("this module is a part of the group.");
                
                // currently only one groupId is derived...
                groupId g = (groupId)gid.firstElement();
                gID = g.getText().trim();
                printlnStatus("groupId: " + gID);
                if (g.getGroupClass() != null) {
                    printlnStatus("groupClass: " + g.getGroupClass());
                } else {
                    printlnStatus("*** groupClass is unknown");
                }
            } else {
                //printlnStatus("this module doesn't have any groupId.");
            }

            // confirm date
            String confirmDate = " ";
            if (item.getDocInfo().getConfirmDate() != null &&
                item.getDocInfo().getConfirmDate().getText() != null) {
                confirmDate = item.getDocInfo().getConfirmDate().getText().trim();
            }
            printlnStatus("confirm date: " + confirmDate);
            
            /* confirmDate should be the same as reportTime in the module */
            /* laboTest never uses attributes start and end. */
            // start ----------------------------------
            // end ------------------------------------
            /* Keep confirmDate for comparing this to reportTime */
            if (checkString != null) checkString = null;
            checkString = new String(confirmDate);

            //-------------------
            // mmlCiCreatorInfo
            //-------------------
            mmlCiCreatorInfo ci = item.getDocInfo().getCreatorInfo();
            if (ci == null) {
                printlnStatus("***creator info object is null.");
                continue;
            }
            //-----------------------
            // mmlPsiPersonalizedInfo
            //-----------------------
            mmlPsiPersonalizedInfo pi = ci.getPersonalizedInfo();
            String reporterName = " ";
            String facilityName = " ";
            String facilityCode = " ";
            String departmentName = " ";
            String departmentCode = " ";
            String addresses[] = null;
            String emails[] = null;
            String phones[] = null;
            if (pi != null) {
                // mmlCmId-------------------------------

                // mmlPsipersonName----------------------
                // person name at the labo test center
                if (pi.getPersonName() != null && 
                    pi.getPersonName().getName() != null) {
                    reporterName = toPersonName(
                        (mmlNmName)pi.getPersonName().getName().firstElement()
                    );
                }
                printlnStatus("person's name" + reporterName);

                // mmlFcFacility ------------------------
                if (pi.getFacility() != null) {
                    // mmlFcname
                    // name of the labo test center
                    if (pi.getFacility().getName() != null && 
                        pi.getFacility().getName().firstElement() != null) {
                        facilityName = ((mmlFcname)pi.getFacility().getName().firstElement()).getText().trim();
                    }
                    printlnStatus("facility name: " + facilityName);
                    // repCode (I|A|P)
                    // tableId 

                    // mmlCmId-------------------------------
                    if (pi.getFacility().getId() != null &&
                        pi.getFacility().getId().getText() != null) {
                        facilityCode = pi.getFacility().getId().getText().trim();
                    }
                    printlnStatus("facility code: " + facilityCode);
                }
                
                // mmlDpDepartment-----------------------
                if (pi.getDepartment() != null) {
                    // mmlDpname
                    // name of the department
                    if (pi.getDepartment().getName() != null &&
                        pi.getDepartment().getName().firstElement() != null &&
                        ((mmlDpname)pi.getDepartment().getName().firstElement()).getText() != null) {
                        departmentName = ((mmlDpname)pi.getDepartment().getName().firstElement()).getText().trim();
                    }
                    printlnStatus("department name: " + departmentName);
                    // repCode (I|A|P)
                    // tableId 

                    // mmlCmId
                    if (pi.getDepartment().getId() != null &&
                        pi.getDepartment().getId().getText() != null) {
                        departmentCode = pi.getDepartment().getId().getText().trim();
                    }
                    printlnStatus("department code: " + departmentCode);
                }
                
                // mmlPsiaddresses-----------------------
                mmlPsiaddresses addressesObj = pi.getAddresses();
                if (addressesObj != null) {
                    Vector addressesVector = addressesObj.getAddress();
                    if (addressesVector != null && addressesVector.size() > 0) {
                        addresses = new String[addressesVector.size()];
                        for (int k = 0; k < addressesVector.size(); ++k) {
                            String s = toAddress((mmlAdAddress)addressesVector.elementAt(k));
                            printlnStatus("address: " + s);
                            if (s.equals("")) s = " ";
                            addresses[k] = s;
                        }
                    }
                }            

                // mmlPsiemailAddresses------------------
                mmlPsiemailAddresses addrsObj = pi.getEmailAddresses();
                if (addrsObj != null) {
                    Vector addrsVector = addrsObj.getEmail();
                    if (addrsVector != null && addrsVector.size() > 0) { 
                        emails = new String[addrsVector.size()];
                        for (int k = 0; k < addrsVector.size(); ++k) {
                            if ( ((mmlCmemail)addrsVector.elementAt(k)).getText() != null) {
                                printlnStatus("email: " + ((mmlCmemail)addrsVector.elementAt(k)).getText().trim());
                                emails[k] = ((mmlCmemail)addrsVector.elementAt(k)).getText().trim();
                            }
                        }
                    }
                }

                // mmlPsiphones--------------------------
                mmlPsiphones phonesObj = pi.getPhones();
                if (phonesObj != null) {
                    Vector phonesVector = phonesObj.getPhone();
                    if (phonesVector != null && phonesVector.size() > 0) {
                        phones = new String[phonesVector.size()];
                        for (int k = 0; k < phonesVector.size(); ++k) {
                            String s = toPhoneNumber((mmlPhPhone)phonesVector.elementAt(k));
                            printlnStatus("phone: " + s);
                            if (s.equals("")) s = " ";
                            phones[k] = s;
                        }
                    }
                }
            }
            
            //----------------------
            // mmlCicreatorLicense 
            //----------------------
            String licenses[] = null;
            Vector licenseV = ci.getCreatorLicense();
            if (licenseV != null && licenseV.size() > 0) {
                licenses = new String[licenseV.size()];
                for (int k = 0; k < licenseV.size(); ++k) {
                    mmlCicreatorLicense cl = (mmlCicreatorLicense)licenseV.elementAt(k);
                    if (cl != null) {
                        String tid = "";
                        if (cl.getMmlCitableId() != null) {
                            tid = cl.getMmlCitableId();
                        }
                        String ls = "";
                        if (cl.getText() != null) {
                            ls = cl.getText().trim();
                        }
                        licenses[k] = tid + "__" + ls;
                        printlnStatus("license: " + licenses[k]);
                    }
                }
            }
            
            //////////////////////////////////////////////////////
            
            //------------------------------------------------------------------
            // <extRefs>
            //
            extRefs refs = item.getDocInfo().getExtRefs();
            Vector exts = null;
            if ( refs != null ) {
                exts = refs.getExtRef();
            }
            //
            //------------------------------------------------------------------
            
            //-------------
            // content 
            //-------------
            if (item.getContent() == null) {
                printlnStatus("*** content object is null");
                continue;
            }
            mmlLbTestModule test = item.getContent().getTestModule();
            if (test == null) {
                printlnStatus("*** testModule was not found in content.");
                continue;
            } 
            
            //-----------------
            // mmlLbTestModule
            //-----------------
            // information
            mmlLbinformation info = test.getInformation();
            if (info == null) {
                printlnStatus("*** information object is null.");
                continue;
            }
            String registId = " ";
            if (info.getMmlLbregistId() != null) {
                registId = info.getMmlLbregistId();
                printlnStatus("registId: " + registId);
            }
            
            String registTime = " ";
            if (info.getMmlLbregistTime() != null) {
                registTime = info.getMmlLbregistTime();
                printlnStatus("registTime: " + registTime);
            }
            
            String reportTime = " ";
            if (info.getMmlLbreportTime() != null) {
                reportTime = info.getMmlLbreportTime();
                printlnStatus("reportTime: " + reportTime);
                
                /* check reportTime with confirmDate in docInfo */
                // cut off time from dateTime format
                String reportDate = new String(reportTime);
                if ( reportDate.indexOf("T") > 0 ) {
                    reportDate = reportDate.substring(0, reportDate.indexOf("T"));
                }
                if ( checkString.equals(reportDate) == false ) {
                    printlnStatus("*** reportTime doesn't equal to confirmDate: " + checkString);
                }
            }
            
            String sampleTime = " ";
            if (info.getMmlLbsampleTime() != null) {
                sampleTime = info.getMmlLbsampleTime();
                printlnStatus("sampleTime: " + sampleTime);
            }

            // reportStatus---------------------------
            mmlLbreportStatus repStObj = info.getReportStatus();
            String reportStatusCode = " ";
            String reportStatusCodeId = " ";
            String reportStatus = " ";
            if (repStObj != null) {
                if (repStObj.getMmlLbstatusCode() != null) {
                     reportStatusCode = repStObj.getMmlLbstatusCode();
                     printlnStatus("reportStatusCode: " + reportStatusCode);
                }
                if (repStObj.getMmlLbstatusCodeId() != null) {
                     reportStatusCodeId = repStObj.getMmlLbstatusCodeId();
                     printlnStatus("reportStatusCodeId: " + reportStatusCodeId);
                }
                if (repStObj != null && repStObj.getText() != null) {
                     reportStatus = repStObj.getText().trim();
                     printlnStatus("reportStatus: " + reportStatus);
                }
            }
            // set------------------------------------
            mmlLbset setObj = info.getSet();
            String setCode = " ";
            String setCodeId = " ";
            String set = " ";
            if (setObj != null) {
                if (setObj.getMmlLbsetCode() != null) {
                     setCode = setObj.getMmlLbsetCode();
                     printlnStatus("setCode: " + setCode);
                }
                if (setObj.getMmlLbsetCodeId() != null) {
                     setCodeId = setObj.getMmlLbsetCodeId();
                     printlnStatus("setCodeId: " + setCodeId);
                }
                if (setObj != null && setObj.getText() != null) {
                     set = setObj.getText().trim();
                     printlnStatus("set: " + set);
                }
            }
            
            // CLIENT facility-------------------------------
            mmlLbfacility clientFacilityObj = info.getFacility();
            String clientFacilityCode = " ";
            String clientFacilityCodeId = " ";
            String clientFacility = " ";
            if (clientFacilityObj != null) {
                if (clientFacilityObj.getMmlLbfacilityCode() != null) {
                     clientFacilityCode = clientFacilityObj.getMmlLbfacilityCode();
                     printlnStatus("client facilityCode: " + clientFacilityCode);
                }
                if (clientFacilityObj.getMmlLbfacilityCodeId() != null) {
                     clientFacilityCodeId = clientFacilityObj.getMmlLbfacilityCodeId();
                     printlnStatus("client facilityCodeId: " + clientFacilityCodeId);
                }
                if (clientFacilityObj != null && clientFacilityObj.getText() != null) {
                     clientFacility = clientFacilityObj.getText().trim();
                     printlnStatus("client facility: " + clientFacility);
                }
            }
            
            // CLIENT department-----------------------------
            mmlLbdepartment clientDepartmentObj = info.getDepartment();
            String clientDepartmentCode = " ";
            String clientDepartmentCodeId = " ";
            String clientDepartment = " ";
            if (clientDepartmentObj != null) {
                if (clientDepartmentObj.getMmlLbdepCode() != null) {
                     clientDepartmentCode = clientDepartmentObj.getMmlLbdepCode();
                     printlnStatus("client departmentCode: " + clientDepartmentCode);
                }
                if (clientDepartmentObj.getMmlLbdepCodeId() != null) {
                     clientDepartmentCodeId = clientDepartmentObj.getMmlLbdepCodeId();
                     printlnStatus("client departmentCodeId: " + clientDepartmentCodeId);
                }
                if (clientDepartmentObj != null && clientDepartmentObj.getText() != null) {
                     clientDepartment = clientDepartmentObj.getText().trim();
                     printlnStatus("client department: " + clientDepartment);
                }
            }
            
            // CLIENT ward-----------------------------------
            mmlLbward clientWardObj = info.getWard();
            String clientWardCode = " ";
            String clientWardCodeId = " ";
            String clientWard = " ";
            if (clientWardObj != null) {
                if (clientWardObj.getMmlLbwardCode() != null) {
                     clientWardCode = clientWardObj.getMmlLbwardCode();
                     printlnStatus("client wardCode: " + clientWardCode);
                }
                if (clientWardObj.getMmlLbwardCodeId() != null) {
                     clientWardCodeId = clientWardObj.getMmlLbwardCodeId();
                     printlnStatus("client wardCodeId: " + clientWardCodeId);
                }
                if (clientWardObj != null && clientWardObj.getText() != null) {
                     clientWard = clientWardObj.getText().trim();
                     printlnStatus("client ward: " + clientWard);
                }
            }
            
            // client---------------------------------
            mmlLbclient clientObj = info.getClient();
            String clientCode = " ";
            String clientCodeId = " ";
            String client = " ";
            if (clientObj != null) {
                if (clientObj.getMmlLbclientCode() != null) {
                     clientCode = clientObj.getMmlLbclientCode();
                     printlnStatus("client Code: " + clientCode);
                }
                if (clientObj.getMmlLbclientCodeId() != null) {
                     clientCodeId = clientObj.getMmlLbclientCodeId();
                     printlnStatus("client CodeId: " + clientCodeId);
                }
                if (clientObj != null && clientObj.getText() != null) {
                     client = clientObj.getText().trim();
                     printlnStatus("client: " + client);
                }
            }
            
            // laboratoryCenter-----------------------
            mmlLblaboratoryCenter laboratoryCenterObj = info.getLaboratoryCenter();
            String laboratoryCenterCode = " ";
            String laboratoryCenterCodeId = " ";
            String laboratoryCenter = " ";
            if (laboratoryCenterObj != null) {
                if (laboratoryCenterObj.getMmlLbcenterCode() != null) {
                     laboratoryCenterCode = laboratoryCenterObj.getMmlLbcenterCode();
                     printlnStatus("laboratoryCenter Code: " + laboratoryCenterCode);
                }
                if (laboratoryCenterObj.getMmlLbcenterCodeId() != null) {
                     laboratoryCenterCodeId = laboratoryCenterObj.getMmlLbcenterCodeId();
                     printlnStatus("laboratoryCenter CodeId: " + laboratoryCenterCodeId);
                }
                if (laboratoryCenterObj != null && laboratoryCenterObj.getText() != null) {
                     laboratoryCenter = laboratoryCenterObj.getText().trim();
                     printlnStatus("laboratoryCenter: " + laboratoryCenter);
                }
            }
            
            // technician-----------------------------
            mmlLbtechnician technicianObj = info.getTechnician();
            String technicianCode = " ";
            String technicianCodeId = " ";
            String technician = " ";
            if (technicianObj != null) {
                if (technicianObj.getMmlLbtechCode() != null) {
                     technicianCode = technicianObj.getMmlLbtechCode();
                     printlnStatus("technician Code: " + technicianCode);
                }
                if (technicianObj.getMmlLbtechCodeId() != null) {
                     technicianCodeId = technicianObj.getMmlLbtechCodeId();
                     printlnStatus("technician CodeId: " + technicianCodeId);
                }
                if (technicianObj != null && technicianObj.getText() != null) {
                     technician = technicianObj.getText().trim();
                     printlnStatus("technician: " + technician);
                }
            }
            
            // repMemo*--------------------------------
            String repMemos[] = null;
            Vector repV = info.getRepMemo();
            if (repV != null && repV.size() > 0) { 
                repMemos = new String[repV.size()];
                for (int k = 0; k < repV.size(); ++k) {
                    mmlLbrepMemo repM = (mmlLbrepMemo)repV.elementAt(k);

                    if (repM == null) {
                        repMemos[k] = "";
                        continue;
                    }

                    String repCodeName = "";
                    if (repM.getMmlLbrepCodeName() != null) {
                        repCodeName = repM.getMmlLbrepCodeName().trim();
                    }
                    String repCode = "";
                    if (repM.getMmlLbrepCode() != null) {
                        repCode = repM.getMmlLbrepCode().trim();
                    }
                    String repCodeId = "";
                    if (repM.getMmlLbrepCodeId() != null) {
                        repCodeId = repM.getMmlLbrepCodeId().trim();
                    }
                    String repbody = "";
                    if (repM.getText() != null) {
                        repbody = repM.getText().trim();
                    }

                    repMemos[k] = String.valueOf((int)(k+1)) + "__" + repCodeName + "__" + repCode + "__" + repCodeId + "__" + repbody;
                    printlnStatus("report memo: " +  repMemos[k]);
                }
            }

            // repMemoF -------------------------
            String repMemoF = " ";
            mmlLbrepMemoF repmemoFObj = info.getRepMemoF();
            if (repmemoFObj != null && repmemoFObj.getText() != null) {
                repMemoF = repmemoFObj.getText().trim();
                printlnStatus("report free memo: " + repMemoF);
            }
            
            //////////////////////////////////////////////////
            // LDAP //////////////////////////////////////////
            // create laboModule directory
            moduleDN = createModule(
                title, uniqueId, gID, confirmDate, 
                reporterName,
                facilityCode, facilityName, 
                departmentCode, departmentName,
                addresses, emails, phones, licenses,
                registId, 
                registTime, reportTime, sampleTime, 
                reportStatusCode, reportStatusCodeId, reportStatus,
                setCode, setCodeId, set, 
                clientFacilityCode, clientFacilityCodeId, clientFacility, 
                clientDepartmentCode, clientDepartmentCodeId, clientDepartment, 
                clientWardCode, clientWardCodeId, clientWard, 
                clientCode, clientCodeId, client, 
                laboratoryCenterCode, laboratoryCenterCodeId, laboratoryCenter, 
                technicianCode, technicianCodeId, technician, 
                repMemoF, repMemos );
            if (moduleDN == null) {
                printlnStatus("*** Couldn't create laboModule.");
                continue;
            }
            //----------------------------
            // append entries for extRefs
            if (exts != null && exts.size() > 0) {
                appendExtRefs(exts);
            }
            //----------------------------
            //////////////////////////////////////////////////

            Vector laboTests = test.getLaboTest();
            printlnStatus("number of laboTests: " + laboTests.size());

            Enumeration etest = laboTests.elements();
            while (etest.hasMoreElements()) {
                mmlLblaboTest lab = (mmlLblaboTest)etest.nextElement();
                if (lab == null) continue;
                
                // laboTest specimen
                if (lab.getSpecimen() == null) {
                    printlnStatus("***specimen object is null.");
                    continue;
                }
                
                // spacimenName
                String specimen = " ";
                String spCode = " ";
                String spCodeId = " ";
                if (lab.getSpecimen().getSpecimenName() != null) {
                    if (lab.getSpecimen().getSpecimenName().getText() != null) {
                        specimen = lab.getSpecimen().getSpecimenName().getText().trim();
                    }
                    if ( lab.getSpecimen().getSpecimenName().getMmlLbspCode() != null ) {
                        spCode = lab.getSpecimen().getSpecimenName().getMmlLbspCode();
                    }
                    if ( lab.getSpecimen().getSpecimenName().getMmlLbspCodeId() != null ) {
                        spCodeId = lab.getSpecimen().getSpecimenName().getMmlLbspCodeId();
                    }
                }
                printlnStatus("-- specimen: " + specimen);
                printlnStatus("-- specimen code: " + spCode);
                printlnStatus("-- specimen code id: " + spCodeId);
                
                // spcMemo -------------------------
                String spcMemos[] = null;
                Vector spV = lab.getSpecimen().getSpcMemo();
                if (spV != null && spV.size() > 0) { 
                    spcMemos = new String[spV.size()];
                    for (int k = 0; k < spV.size(); ++k) {
                        mmlLbspcMemo spcm = (mmlLbspcMemo)spV.elementAt(k);
                        if (spcm == null) {
                            spcMemos[k] = "";
                            continue;
                        }

                        String smCodeName = "";
                        if (spcm.getMmlLbsmCodeName() != null) {
                            smCodeName = spcm.getMmlLbsmCodeName().trim();
                        }
                        String smCode = "";
                        if (spcm.getMmlLbsmCode() != null) {
                            smCode = spcm.getMmlLbsmCode().trim();
                        }
                        String smCodeId = "";
                        if (spcm.getMmlLbsmCodeId() != null) {
                            smCodeId = spcm.getMmlLbsmCodeId().trim();
                        }
                        String spcbody = "";
                        if (spcm.getText() != null) {
                            spcbody = spcm.getText().trim();
                        }

                        spcMemos[k] = String.valueOf((int)(k+1)) + "__" + smCodeName + "__" + smCode + "__" + smCodeId + "__" + spcbody;
                        printlnStatus("specimen memo: " +  spcMemos[k]);
                    }
                }

                // spcMemoF -------------------------
                String spcMemoF = " ";
                mmlLbspcMemoF memoF = lab.getSpecimen().getSpcMemoF();
                if (memoF != null && memoF.getText() != null) {
                    spcMemoF = memoF.getText().trim();
                    printlnStatus("specimen free memo: " + spcMemoF);
                }
                
                // LDAP //////////////////////////////////////////////
                laboTestResultDN = createLaboTest(specimen, spCode, spCodeId, spcMemoF, spcMemos);
                if (laboTestResultDN == null) {
                    printlnStatus("*** Couldn't create laboTest for specimen: " + specimen);
                    continue;
                }
                //////////////////////////////////////////////////////

                // laboTest item
                handleLaboItems(lab.getItem());
            }
        }
    }
    
    public void handleLaboItems(Vector items) {
        Enumeration e = items.elements();
        printlnStatus("number of items in this specimen: " + String.valueOf(items.size()));
        
        while (e.hasMoreElements()) {
            mmlLbitem item = (mmlLbitem)e.nextElement();
            if (item == null) {
                printlnStatus("*** item object is null.");
                continue;
            }
            
            if (item.getItemName() == null) {
                printlnStatus("*** item name object is null.");
                continue;
            }
            // get item name (ex. GOT) and value
            String itemName = " ";
            if (item.getItemName().getText() != null ) {
                itemName = item.getItemName().getText().trim();
            }
            printlnStatus("itemName: " + itemName);
            
            String value = " ";
            if (item.getValue() != null &&
                item.getValue().getText() != null) {
                value = item.getValue().getText().trim();
            }
            printlnStatus(itemName + ": " + value);
            
            // itemCode, itemCodeId
            String itemCode = " ";
            if (item.getItemName().getMmlLbitCode() != null) {
                itemCode = item.getItemName().getMmlLbitCode();
            }
            
            String itemCodeId = " ";
            if (item.getItemName().getMmlLbitCodeId() != null) {
                itemCode = item.getItemName().getMmlLbitCodeId();
            }
            
            // Acode,Icode,Scode,Mcode,Rcode
            String Acode = " ";
            String Icode = " ";
            String Scode = " ";
            String Mcode = " ";
            String Rcode = " ";
            if (item.getItemName().getMmlLbAcode() != null) {
                Acode = item.getItemName().getMmlLbAcode();
            }
            if (item.getItemName().getMmlLbIcode() != null) {
                Icode = item.getItemName().getMmlLbIcode();
            }
            if (item.getItemName().getMmlLbScode() != null) {
                Scode = item.getItemName().getMmlLbScode();
            }
            if (item.getItemName().getMmlLbMcode() != null) {
                Mcode = item.getItemName().getMmlLbMcode();
            }
            if (item.getItemName().getMmlLbRcode() != null) {
                Rcode = item.getItemName().getMmlLbRcode();
            }

            // unit 
            mmlLbunit unit = item.getUnit();
            String u = " ";
            if (unit != null && unit.getText() != null) {
                u = unit.getText().trim();
            }
            printlnStatus("Unit:" + u);
            
            // uCode
            String uCode = " ";
            if (unit != null && unit.getMmlLbuCode() != null) {
                uCode = unit.getMmlLbuCode();
                printlnStatus("unit code:" + uCode);
            }
            // uCodeId
            String uCodeId = " ";
            if (unit != null && unit.getMmlLbuCodeId() != null) {
                uCodeId = unit.getMmlLbuCodeId();
            }
            printlnStatus("unit code Id:" + uCodeId);
            
            // numerical value
            mmlLbnumValue nval = item.getNumValue();
            String low = " ";
            String up = " ";
            String normal = " ";
            String out = " ";
            if (nval != null) {
                //printlnStatus("---numValue");
                
                if (nval.getMmlLblow() != null) {
                    low = nval.getMmlLblow();
                    printlnStatus("[low:" + low + "] ");
                }
                
                if (nval.getMmlLbup() != null) {
                    up = nval.getMmlLbup();
                    printlnStatus("[up:" + up + "] ");
                }
                
                if (nval.getMmlLbnormal() != null) {
                    normal = nval.getMmlLbnormal();
                    printlnStatus("[normal:" + normal + "] ");
                }
                
                if (nval.getMmlLbout() != null) {
                    out = nval.getMmlLbout();
                    printlnStatus("[out:" + out + "] ");
                }
                
            } else {
                //printlnStatus("---no numValue");
            }
            
            // extRef in  referenceInfo
            // ***** extRef has been inported while the parsing docInfo
            // ***** we should also keep extRef appeared here to know which file is for this item
            String extHrefs[] = null;
            mmlLbreferenceInfo referenceInfoObj = item.getReferenceInfo();
            if (referenceInfoObj != null) {
                Vector exts = referenceInfoObj.getExtRef();
                if (exts != null && exts.size() > 0) {
                    extHrefs = new String[exts.size()];
                    for ( int i = 0; i < exts.size(); ++i ) {
                        mmlCmextRef ref = (mmlCmextRef)exts.elementAt(i);
                        if ( ref == null ) continue;
                        
                        /*
                         String type = " ";
                        if ( ref.getMmlCmcontentType() != null ) {
                            type = ref.getMmlCmcontentType();
                            printlnStatus("contentType: " + type);
                        }

                        String role = " ";
                        if ( ref.getMmlCmmedicalRole() != null ) {
                            role = ref.getMmlCmmedicalRole();
                            printlnStatus("medicalRole: " + role);
                        }

                        String title = " ";
                        if ( ref.getMmlCmtitle() != null ) {
                            title = ref.getMmlCmtitle();
                            printlnStatus("title: " + title);
                        }
                        */

                        String href = " ";
                        if ( ref.getMmlCmhref() != null ) {
                            href = ref.getMmlCmhref();
                            printlnStatus("href: " + href);
                        }
                        extHrefs[i] = href;
                    }
                }
            }
            
            // itemMemo -------------------------
            String itemMemos[] = null;
            Vector itV = item.getItemMemo();
            if (itV != null && itV.size() > 0) { 
                itemMemos = new String[itV.size()];
                for (int k = 0; k < itV.size(); ++k) {
                    mmlLbitemMemo itM = (mmlLbitemMemo)itV.elementAt(k);

                    if (itM == null) {
                        itemMemos[k] = "";
                        continue;
                    }

                    String imCodeName = "";
                    if (itM.getMmlLbimCodeName() != null) {
                        imCodeName = itM.getMmlLbimCodeName().trim();
                    }
                    String imCode = "";
                    if (itM.getMmlLbimCode() != null) {
                        imCode = itM.getMmlLbimCode().trim();
                    }
                    String imCodeId = "";
                    if (itM.getMmlLbimCodeId() != null) {
                        imCodeId = itM.getMmlLbimCodeId().trim();
                    }
                    String body = "";
                    if (itM.getText() != null) {
                        body = itM.getText().trim();
                    }

                    itemMemos[k] = String.valueOf((int)(k+1)) + "__" + imCodeName + "__" + imCode + "__" + imCodeId + "__" + body;
                    printlnStatus("item memo: " +  itemMemos[k]);
                }
            }

            // itemMemoF -------------------------
            String itemMemoF = " ";
            mmlLbitemMemoF memoF = item.getItemMemoF();
            if (memoF != null && memoF.getText() != null) {
                itemMemoF = memoF.getText().trim();
                printlnStatus("item free memo: " + itemMemoF);
            }

            String laboItemDN = createLaboItem(
                itemName, itemCode, itemCodeId, 
                Acode, Icode, Scode, Mcode, Rcode,
                value, 
                u, uCode, uCodeId,
                low, up, normal, out,
                extHrefs,
                itemMemoF, itemMemos );
            if (laboItemDN == null) {
                printlnStatus("*** Couldn't create laboItem of: " + itemName);
                continue;
            }
        }
    }
    //--------------------------------------------------------------------------
    public String searchDN(String dn, String filter) {
        // get connection
        LDAPConnection ld = getConnection();
        if (ld == null) {
            printlnStatus("*** Couldn't search DN. Connection is null.");
            return null;
        }
        
        String[] attrs = null;
        LDAPSearchResults results = null;
        try {
            results = ld.search(dn, netscape.ldap.LDAPConnection.SCOPE_ONE, filter, attrs, false);
        } catch (LDAPException e) {
            printlnStatus("*** Directory was not found.");
            return null;
        }
        
        if ( results.hasMoreElements() == false ) {
            printlnStatus("*** Directory was not found.");
            return null;
        }
        
        // get first entry we found
        LDAPEntry entry = null;
        try {
            entry = (LDAPEntry)results.next();
        } catch (LDAPException e) {
            printlnStatus("*** Couldn't get entry of search result.");
            return null;
        }
        return entry.getDN();
    }
    
    // LDAP Tools
    
    public String searchLaboTest() {
        ldap.updateProperties();
        String dn = ldap.getBaseDN();
        String filter = "(cn=LaboTest)";
        return searchDN(dn, filter);
    }
    
    private LDAPConnection conn = null;
    public synchronized LDAPConnection getConnection() {
        if (conn != null && conn.isConnected()) {
            return conn;
        }
        
        try {
            conn = new LDAPConnection();
            ldap.updateProperties();
            conn.connect(
                ldap.getHost(),
                ldap.getPort(),
                ldap.getBindDN(),
                ldap.getPassword());
            return conn;
        } catch (LDAPException e) {
            printlnStatus(e.toString());
            return null;
        }
    }
    
    public synchronized void disconnectLDAP() {
        if (conn != null && conn.isConnected()) {
            try {
                conn.disconnect();
                conn = null;
            } catch (LDAPException e) {
                printlnStatus(e.toString());
            }
        }
    }
    
    public boolean createLaboTest() {
        try {
            LDAPAttributeSet attrs = new LDAPAttributeSet();
            attrs.add(new LDAPAttribute("objectclass", "mmlContainer"));
            attrs.add(new LDAPAttribute("cn", "LaboTest"));
            attrs.add(new LDAPAttribute("description", "Laboratory test for this clinic."));
            
            ldap.updateProperties();
            String dn = "cn=LaboTest," + ldap.getBaseDN();
            LDAPEntry entry = new LDAPEntry(dn, attrs);
            
            LDAPConnection ld = getConnection();
            if (ld == null) {
                printlnStatus("*** Couldn't create LaboTest directory.");
                return false;
            }
            
            ld.add(entry);
            Thread.sleep(100);
            
            laboTestDN = dn;
            printlnStatus("*** LaboTest directory was created at " + dn);
            return true;
        } catch(Exception e) {
            printlnStatus("*** Couldn't create LaboTest directory.");
            e.printStackTrace();
            return false;
        }        
    }
    
    //--------------------------------------------------------------------------
    public String searchPatient(String type) {
        String dn = laboTestDN;
        
        // type should be "facility" or "local"
        String pid = type + "__" + patientId.replaceAll(",","");
        String filter = "(mmlPid=" + pid + ")";
        filter = filter.replaceAll("-","");
        filter = filter.replaceAll(":","");
        return searchDN(dn, filter);
    }
    
    public boolean createPatient(
        String type, 
        String tableId,
        String cdschema,
        String cdigit ) {
            
        String pid = type + "__" + patientId.replaceAll(",","");
        String dn = "mmlPid=" + pid + "," + laboTestDN;
        dn = dn.replaceAll("-","");
        dn = dn.replaceAll(":","");
        
        try {
            LDAPAttributeSet attrs = new LDAPAttributeSet();
            attrs.add(new LDAPAttribute("objectclass", "laboPatient"));
            
            if (patientId.equals("")) patientId = " ";
            attrs.add(new LDAPAttribute("mmlPid", pid));
            
            if (type.equals("")) type = " ";
            attrs.add(new LDAPAttribute("laboPatientType", type));
            
            if (tableId.equals("")) tableId = " ";
            attrs.add(new LDAPAttribute("laboPatientTableId", tableId));
            
            if (cdschema.equals("")) cdschema = " ";
            attrs.add(new LDAPAttribute("laboPatientCheckDigitSchema", cdschema));
            
            if (cdigit.equals("")) cdigit = " ";
            attrs.add(new LDAPAttribute("laboPatientCheckDigit", cdigit));
            
            attrs.add(new LDAPAttribute("description", "laboPatient is created for each patient in this clinic."));
            
            LDAPEntry entry = new LDAPEntry(dn, attrs);

            LDAPConnection ld = getConnection();
            if (ld == null) {
                printlnStatus("*** Couldn't create LaboTest directory.");
                return false;
            }
            ld.add(entry);
            Thread.sleep(100);
                        
            patientDN = dn;
            printlnStatus("*** laboPatient directory was created at " + dn);
            return true;
        } catch(Exception e) {
            printlnStatus("*** Couldn't create Patient directory.");
            e.printStackTrace();
            return false;
        }        
    }
    
    //--------------------------------------------------------------------------
    public String generateUUIDWindows() {
        /*
        String[] cmd = new String[3];
        cmd[0] = "command.com";
        cmd[1] = "/C" ;
        cmd[2] = "uuidgen";
        */
        
        String cmd = "uuidgen";
        
        try {
            // invoke command and get result...
            // output strings are stored in the vector
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(cmd);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );
            String line = null;
            Vector v = new Vector();
            while( (line = br.readLine()) != null ) {
                v.add(line);
                //System.out.println( line );
            }
            p.waitFor();

             // get result
            if (v.size() > 0) {
                String s = (String)v.firstElement();
                s=s.trim();
                return s;
            } else {
                return "";
            }
        } catch( IOException ex ) {
            System.out.println(
                "*** An IO exception occurred while executing the cmd <" + cmd + ">:" + ex.getMessage() 
            );
            ex.printStackTrace();
            return "";
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }
    }
    
    public String generateUUIDLinux() {
        String cmd = "uuidgen -t";
        
        try {
            // invoke command and get result...
            // output strings are stored in the vector
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(cmd);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );
            String line = null;
            Vector v = new Vector();
            while( (line = br.readLine()) != null ) {
                v.add(line);
                //System.out.println( line );
            }
            p.waitFor();

             // get result
            if (v.size() >= 0) {
                String s = (String)v.firstElement();
                s=s.trim();
                return s;
            } else {
                return "";
            }
        } catch( IOException ex ) {
            System.out.println(
                "*** An IO exception occurred while executing the cmd <" + cmd + ">:" + ex.getMessage() 
            );
            ex.printStackTrace();
            return "";
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }
    }

    public String generateUUIDMacOSX() {
        String cmd = "uuidgen";
        
        try {
            // invoke command and get result...
            // output strings are stored in the vector
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(cmd);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );
            String line = null;
            Vector v = new Vector();
            while( (line = br.readLine()) != null ) {
                v.add(line);
                //System.out.println( line );
            }
            p.waitFor();

             // get result
            if (v.size() >= 0) {
                String s = (String)v.firstElement();
                s=s.trim();
                return s;
            } else {
                return "";
            }
        } catch( IOException ex ) {
            System.out.println(
                "*** An IO exception occurred while executing the cmd <" + cmd + ">:" + ex.getMessage() 
            );
            ex.printStackTrace();
            return "";
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }
    }
    
    public String generateUUID() {
        String osName = System.getProperty("os.name");
        if ( osName.equals("Windows 2000") ) {
            // it is assummed that the platform is WIndows2000
            return generateUUIDWindows();
        } else if ( osName.equals("Linux") ) {
            return generateUUIDLinux();
        } else if ( osName.equals("Mac OS X") ) {
            return generateUUIDMacOSX();
        } else {
            return "";
        }
    }

    // hahaha:-) this function's arguments are very long. but never mind.
    // I accepted the clarity of arguments rather than the effiency of coding.
    public String createModule(   String title, // title of module
                                    String uniqueId,// uid of module
                                    String gID,// groupId of module
                                    String confirmDate, 
                                    String reporterName,// reporter
                                    String facilityCode, String facilityName,
                                    String departmentCode, String departmentName,
                                    String[] addresses, String[] emails, String[] phones, String[] licenses,
                                    String registId, String registTime, String reportTime, String sampleTime, 
                                    String reportStatusCode, String reportStatusCodeId, String reportStatus,
                                    String setCode, String setCodeId, String set,
                                    String clientFacilityCode, String clientFacilityCodeId, String clientFacility,
                                    String clientDepartmentCode, String clientDepartmentCodeId, String clientDepartment,
                                    String clientWardCode, String clientWardCodeId, String clientWard,
                                    String clientCode, String clientCodeId, String client,
                                    String laboratoryCenterCode, String laboratoryCenterCodeId, String laboratoryCenter,
                                    String technicianCode, String technicianCodeId, String technician,
                                    String repMemoF, String[] repMemos ) {
            
        String uid = sampleTime.replaceAll(",","") + "__" + facilityCode + "__" + generateUUID();
        String dn = "uid=" + uid + "," + patientDN;
        dn = dn.replaceAll("-","");
        dn = dn.replaceAll(":","");
        
        try {
            LDAPAttributeSet attrs = new LDAPAttributeSet();
            attrs.add(new LDAPAttribute("objectclass", "laboModule"));
            
            if (uid.equals("")) uid = " ";
            attrs.add(new LDAPAttribute("uid", uid));
            
            if (title.equals("")) title = " ";
            attrs.add(new LDAPAttribute("laboModuleTitle", title));
            
            if (uniqueId.equals("")) uniqueId = " ";
            attrs.add(new LDAPAttribute("mmlUid", uniqueId));
            
            if (gID.equals("")) gID = " ";
            attrs.add(new LDAPAttribute("mmlGroupId", gID));
            
            if (confirmDate.equals("")) confirmDate = " ";
            attrs.add(new LDAPAttribute("mmlConfirmDate", confirmDate));
            
            
            if (reporterName.equals("")) reporterName = " ";
            attrs.add(new LDAPAttribute("laboTestReporter", reporterName));

            
            if (facilityCode.equals("")) facilityCode = " ";
            attrs.add(new LDAPAttribute("laboTestCenterId", facilityCode));
            if (facilityName.equals("")) facilityName = " ";
            attrs.add(new LDAPAttribute("laboTestCenterName", facilityName));
            
            
            if (departmentCode.equals("")) departmentCode = " ";
            attrs.add(new LDAPAttribute("laboTestDepartmentId", departmentCode));
            if (departmentName.equals("")) departmentName = " ";
            attrs.add(new LDAPAttribute("laboTestDepartmentName", departmentName));

            
            if (addresses != null && addresses.length > 0) {
                attrs.add(new LDAPAttribute("laboAddress", addresses)); 
            }

            if (emails != null && emails.length > 0) {
                attrs.add(new LDAPAttribute("mail", emails)); 
            }
            
            if (phones != null && phones.length > 0) {
                attrs.add(new LDAPAttribute("laboPhone", phones)); 
            }
            
            if (licenses != null && licenses.length > 0) {
                attrs.add(new LDAPAttribute("laboCreatorLicense", licenses)); 
            }
            
            if (registId.equals("")) registId = " ";
            attrs.add(new LDAPAttribute("laboRegistId", registId));
            if (registTime.equals("")) registTime = " ";
            attrs.add(new LDAPAttribute("laboRegistTime", registTime));
            if (reportTime.equals("")) reportTime = " ";
            attrs.add(new LDAPAttribute("laboReportTime", reportTime));
            if (sampleTime.equals("")) sampleTime = " ";
            attrs.add(new LDAPAttribute("laboSampleTime", sampleTime));
            
            if (reportStatusCode.equals("")) reportStatusCode = " ";
            attrs.add(new LDAPAttribute("laboReportStatusCode", reportStatusCode));
            if (reportStatusCodeId.equals("")) reportStatusCodeId = " ";
            attrs.add(new LDAPAttribute("laboReportStatusCodeId", reportStatusCodeId));
            if (reportStatus.equals("")) reportStatus = " ";
            attrs.add(new LDAPAttribute("laboReportStatus", reportStatus));
            
            
            if (setCode.equals("")) setCode = " ";
            attrs.add(new LDAPAttribute("laboSetCode", setCode));
            if (setCodeId.equals("")) setCodeId = " ";
            attrs.add(new LDAPAttribute("laboSetCodeId", setCodeId));
            if (set.equals("")) set = " ";
            attrs.add(new LDAPAttribute("laboSet", set));

            
            if (clientFacilityCode.equals("")) clientFacilityCode = " ";
            attrs.add(new LDAPAttribute("laboClientFacilityCode", clientFacilityCode));
            if (clientFacilityCodeId.equals("")) clientFacilityCodeId = " ";
            attrs.add(new LDAPAttribute("laboClientFacilityCodeId", clientFacilityCodeId));
            if (clientFacility.equals("")) clientFacility = " ";
            attrs.add(new LDAPAttribute("laboClientFacility", clientFacility));

        
            if (clientDepartmentCode.equals("")) clientDepartmentCode = " ";
            attrs.add(new LDAPAttribute("laboClientDepartmentCode", clientDepartmentCode));
            if (clientDepartmentCodeId.equals("")) clientDepartmentCodeId = " ";
            attrs.add(new LDAPAttribute("laboClientDepartmentCodeId", clientDepartmentCodeId));
            if (clientDepartment.equals("")) clientDepartment = " ";
            attrs.add(new LDAPAttribute("laboClientDepartment", clientDepartment));

            
            if (clientWardCode.equals("")) clientWardCode = " ";
            attrs.add(new LDAPAttribute("laboClientWardCode", clientWardCode));
            if (clientWardCodeId.equals("")) clientWardCodeId = " ";
            attrs.add(new LDAPAttribute("laboClientWardCodeId", clientWardCodeId));
            if (clientWard.equals("")) clientWard = " ";
            attrs.add(new LDAPAttribute("laboClientWard", clientWard));

            
            if (clientCode.equals("")) clientCode = " ";
            attrs.add(new LDAPAttribute("laboClientCode", clientCode));
            if (clientCodeId.equals("")) clientCodeId = " ";
            attrs.add(new LDAPAttribute("laboClientCodeId", clientCodeId));
            if (client.equals("")) client = " ";
            attrs.add(new LDAPAttribute("laboClient", client));

            
            if (laboratoryCenterCode.equals("")) laboratoryCenterCode = " ";
            attrs.add(new LDAPAttribute("laboLaboratoryCenterCode", laboratoryCenterCode));
            if (laboratoryCenterCodeId.equals("")) laboratoryCenterCodeId = " ";
            attrs.add(new LDAPAttribute("laboLaboratoryCenterCodeId", laboratoryCenterCodeId));
            if (laboratoryCenter.equals("")) laboratoryCenter = " ";
            attrs.add(new LDAPAttribute("laboLaboratoryCenter", laboratoryCenter));

            
            if (technicianCode.equals("")) technicianCode = " ";
            attrs.add(new LDAPAttribute("laboTechnicianCode", technicianCode));
            if (technicianCodeId.equals("")) technicianCodeId = " ";
            attrs.add(new LDAPAttribute("laboTechnicianCodeId", technicianCodeId));
            if (technician.equals("")) technician = " ";
            attrs.add(new LDAPAttribute("laboTechnician", technician));


            if (repMemoF.equals("")) repMemoF = " ";
            attrs.add(new LDAPAttribute("laboReportFreeMemo", repMemoF));
            
            if (repMemos != null && repMemos.length > 0) {
                attrs.add(new LDAPAttribute("laboReportMemo", repMemos)); 
            }

            attrs.add(new LDAPAttribute("description", "laboTest is stored."));
            
            LDAPEntry entry = new LDAPEntry(dn, attrs);
            
            LDAPConnection ld = getConnection();
            if (ld == null) {
                printlnStatus("*** Couldn't create laboModule directory.");
                return null;
            }
            ld.add(entry);
            Thread.sleep(100);
            
            return dn;
        } catch(Exception e) {
            printlnStatus("*** error in createModule");
            e.printStackTrace();
            return null;
        }        
    }

    public void appendExtRefs(Vector exts) {
        for ( int i = 0; i < exts.size(); ++i ) {
            mmlCmextRef ref = (mmlCmextRef)exts.elementAt(i);
            if ( ref == null ) continue;
            
            String type = " ";
            if ( ref.getMmlCmcontentType() != null ) {
                type = ref.getMmlCmcontentType();
                printlnStatus("contentType: " + type);
            }
            
            String role = " ";
            if ( ref.getMmlCmmedicalRole() != null ) {
                role = ref.getMmlCmmedicalRole();
                printlnStatus("medicalRole: " + role);
            }
            
            String title = " ";
            if ( ref.getMmlCmtitle() != null ) {
                title = ref.getMmlCmtitle();
                printlnStatus("title: " + title);
            }
            
            String href = " ";
            if ( ref.getMmlCmhref() != null ) {
                href = ref.getMmlCmhref();
                printlnStatus("href: " + href);
            }
            
            createLaboExtRef(type, role, title, href);
            
            //====================================================
            // move this extRef file to local directory
            moveFile(href);
            //====================================================
        }
    }

    public void moveFile(String href) {
        // it is assummed that href is just a filename
        File src = new File( srcDir + href );
        File dst = new File( extRefsDir + href );
        
        // move file from srcDir to extRefsDir
        try {
            boolean result = src.renameTo(dst);
            if (result == false) {
                printlnStatus("*** Couldn't move extRef file: " + href);
            }
        } catch (Exception e) {
            e.printStackTrace();
            printlnStatus("*** Couldn't move extRef file: " + href);
        }
    }

    public void createLaboExtRef(String contentType, 
                                    String medicalRole, 
                                    String title, 
                                    String href ) {
        String cn = "extRef__" + generateUUID();
        String dn = "cn=" + cn + "," + moduleDN;
        dn = dn.replaceAll("-","");
        dn = dn.replaceAll(":","");        
        
        try {
            LDAPAttributeSet attrs = new LDAPAttributeSet();
            attrs.add(new LDAPAttribute("objectclass", "laboExtRef"));
            
            if (cn.equals("")) cn = " ";
            attrs.add(new LDAPAttribute("cn", cn));
            
            if (contentType.equals("")) contentType = " ";
            attrs.add(new LDAPAttribute("laboExtRefContentType", contentType));
            
            if (medicalRole.equals("")) medicalRole = " ";
            attrs.add(new LDAPAttribute("laboExtRefMedicalRole", medicalRole));
            
            if (title.equals("")) title = " ";
            attrs.add(new LDAPAttribute("laboExtRefTitle", title));
            
            if (href.equals("")) href = " ";
            attrs.add(new LDAPAttribute("laboExtRefHref", href));

            attrs.add(new LDAPAttribute("description", "extRef for labo test module"));
            
            LDAPEntry entry = new LDAPEntry(dn, attrs);

            LDAPConnection ld = getConnection();
            if (ld == null) {
                printlnStatus("*** Couldn't create laboExtRef directory.");
                return;
            }
            ld.add(entry);
            Thread.sleep(100);

        } catch(Exception e) {
            printlnStatus("*** error in createLaboExtRef");
            e.printStackTrace();
            return;
        }    
    }

    public String createLaboTest(   String specimen, 
                                      String spCode, 
                                      String spCodeId,
                                      String spcMemoF,
                                      String[] spcMemos ) {
                                          
        String uid = specimen.replaceAll(",","") + "__" + generateUUID();
        String dn = "uid=" + uid + "," + moduleDN;
        dn = dn.replaceAll("-","");
        dn = dn.replaceAll(":","");
        
        try {
            LDAPAttributeSet attrs = new LDAPAttributeSet();
            attrs.add(new LDAPAttribute("objectclass", "laboTest"));
            
            if (uid.equals("")) uid = " ";
            attrs.add(new LDAPAttribute("uid", uid));
            
            if (specimen.equals("")) specimen = " ";
            attrs.add(new LDAPAttribute("laboSpecimenName", specimen));
            
            if (spCode.equals("")) spCode = " ";
            attrs.add(new LDAPAttribute("laboSpecimenCode", spCode));
            
            if (spCodeId.equals("")) spCodeId = " ";
            attrs.add(new LDAPAttribute("laboSpecimenCodeId", spCodeId));
            
            if (spcMemoF.equals("")) spcMemoF = " ";
            attrs.add(new LDAPAttribute("laboSpecimenFreeMemo", spcMemoF));
            
            if (spcMemos != null && spcMemos.length > 0) {
                attrs.add(new LDAPAttribute("laboSpecimenMemo", spcMemos)); 
            }
            
            attrs.add(new LDAPAttribute("description", "laboTest for laboModule"));
            
            LDAPEntry entry = new LDAPEntry(dn, attrs);

            LDAPConnection ld = getConnection();
            if (ld == null) {
                printlnStatus("*** Couldn't create LaboTest directory.");
                return null;
            }
            ld.add(entry);
            Thread.sleep(100);

            return dn;
        } catch(Exception e) {
            printlnStatus("*** error in createLaboTest");
            e.printStackTrace();
            return null;
        }        
    }
    
    public String createLaboItem( String itemName, String itemCode, String itemCodeId,
                                    String Acode, String Icode, String Scode, String Mcode, String Rcode,
                                    String value,
                                    String unit, String uCode, String uCodeId, 
                                    String low, String up, String normal, String out,
                                    String[] extHrefs,
                                    String itemMemoF, String[] itemMemos ) {
                                        
        String uid = itemName.replaceAll(",","") + "__" + generateUUID();
        String dn = "uid=" + uid + "," + laboTestResultDN;
        dn = dn.replaceAll("-","");
        dn = dn.replaceAll(":","");
        
        try {
            LDAPAttributeSet attrs = new LDAPAttributeSet();
            attrs.add(new LDAPAttribute("objectclass", "laboItem"));
            
            if (uid.equals("")) uid = " ";
            attrs.add(new LDAPAttribute("uid", uid));
            
            if (itemName.equals("")) itemName = " ";
            attrs.add(new LDAPAttribute("laboItemName", itemName));
            
            if (itemCode.equals("")) itemCode = " ";
            attrs.add(new LDAPAttribute("laboItemCode", itemCode));
            if (itemCodeId.equals("")) itemCodeId = " ";
            attrs.add(new LDAPAttribute("laboItemCodeId", itemCodeId));
            
            if (Acode.equals("")) Acode = " ";
            attrs.add(new LDAPAttribute("laboItemAcode", Acode));
            if (Icode.equals("")) Icode = " ";
            attrs.add(new LDAPAttribute("laboItemIcode", Icode));
            if (Scode.equals("")) Scode = " ";
            attrs.add(new LDAPAttribute("laboItemScode", Scode));
            if (Mcode.equals("")) Mcode = " ";
            attrs.add(new LDAPAttribute("laboItemMcode", Mcode));
            if (Rcode.equals("")) Rcode = " ";
            attrs.add(new LDAPAttribute("laboItemRcode", Rcode));
            
            if (value.equals("")) value = " ";
            attrs.add(new LDAPAttribute("laboValue", value));
            
            if (unit.equals("")) unit = " ";
            attrs.add(new LDAPAttribute("laboUnit", unit));
            if (uCode.equals("")) uCode = " ";
            attrs.add(new LDAPAttribute("laboUnitCode", uCode));
            if (uCodeId.equals("")) uCodeId = " ";
            attrs.add(new LDAPAttribute("laboUnitCodeId", uCodeId));
            
            if (low.equals("")) low = " ";
            attrs.add(new LDAPAttribute("laboLow", low));
            if (up.equals("")) up = " ";
            attrs.add(new LDAPAttribute("laboUp", up));
            if (normal.equals("")) normal = " ";
            attrs.add(new LDAPAttribute("laboNormal", normal));
            if (out.equals("")) out = " ";
            attrs.add(new LDAPAttribute("laboOut", out));
            
            if (extHrefs != null && extHrefs.length > 0) {
                attrs.add(new LDAPAttribute("laboExtRefHref", extHrefs)); 
            }
            
            if (itemMemoF.equals("")) itemMemoF = " ";
            attrs.add(new LDAPAttribute("laboItemFreeMemo", itemMemoF));
            
            if (itemMemos != null && itemMemos.length > 0) {
                attrs.add(new LDAPAttribute("laboItemMemo", itemMemos)); 
            }
            
            attrs.add(new LDAPAttribute("description", "laboItem for laboTest"));
            
            LDAPEntry entry = new LDAPEntry(dn, attrs);

            LDAPConnection ld = getConnection();
            if (ld == null) {
                printlnStatus("*** Couldn't create laboItem directory.");
                return null;
            }
            ld.add(entry);
            Thread.sleep(100);
            
            return dn;
        } catch(Exception e) {
            printlnStatus("*** error in createLaboItem()");
            e.printStackTrace();
            return null;
        }        
    }
}
