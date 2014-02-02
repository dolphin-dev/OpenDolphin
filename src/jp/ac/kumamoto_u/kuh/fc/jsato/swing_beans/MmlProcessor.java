/*
 * MmlProcessor.java
 *
 * Created on 2001/12/01, 19:48
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

/**
 *
 * @author  Junzo SATO
 * @version 
 */

import jp.ac.kumamoto_u.kuh.fc.jsato.math_mml.*;
import java.util.*;
import java.text.*;

public class MmlProcessor {
    Vector allExtRefs = null;
    
    /** Creates new MmlProcessor */
    public MmlProcessor() {
        allExtRefs = new Vector();
    }

    public Vector getAllExtRefs() {
        return allExtRefs;
    }
    
    private void printlnStatus(String s) {
        System.out.println(s);
    }
    
    private void printStatus(String s) {
        System.out.print(s);
    }

    public void processMml(Mml mml) {
        //------------------
        if (mml == null) {
            printlnStatus("*** Mml object is null.");
            return;
        }
        // check Mml version
        if (mml.getVersion() != null &&
            mml.getVersion().equals("2.3") == false ) {
            printlnStatus("*** Unsupported MML version. The Dolphin Project is based on MML ver 2.3");
            return;
        }
        //------------
        // <MmlHeader>
        //------------
        if (mml.getMmlHeader() == null) {
            printlnStatus("*** MmlHeader object is null.");
            return;
        }
        // mmlCiCreatorInfo -------------------------------------------
        // we don't detect creator info in the header
        
        // masterId -------------------------------------------------
        if (mml.getMmlHeader().getMasterId() == null) {
            printlnStatus("*** masterId object is null.");
            return;
        }
        
        mmlCmId id = mml.getMmlHeader().getMasterId().getId();
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
        String patientId = id.getText();
        if (patientId == null) {
            printlnStatus("*** Missing patient ID.");
            return;
        }
        patientId = patientId.trim();
        printlnStatus("patientId: " + patientId);
        
        // toc -------------------------------------------------------
        // scopePeriod -----------------------------------------------
        // encryptInfo -----------------------------------------------
        
        //-----------  
        // <MmlBody>
        //-----------
        // get the list of MmlModuleItem
        Vector v = getModules(mml);
        if (v == null) {
            printlnStatus("*** Couldn't get any modules.");
            return;
        }
        printlnStatus("Number of Modules in MML instance: " + String.valueOf( v.size() ));
        
        /*
         int numTests = countSpecifiedModules(v, "test");
        printlnStatus("Number of LaboTest module: " + String.valueOf(numTests));
        if (numTests == 0) {
            printlnStatus("Processing ended because of no TestModule existence.");
            return;
        }
         */
        
        // handle each modules
        processModules(v);
    }
    
    // MmlModuleItem+ in MmlBody
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
    
    //--------------------------------------------------------------------------
    
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
    
    public void processModules(Vector v) {
        // Vector v is a MmlModuleItem+ in MmlBody
        Enumeration e = v.elements();
        while (e.hasMoreElements()) {
            //--------------
            // MmlModuleItem
            //--------------
            MmlModuleItem item = (MmlModuleItem)e.nextElement();
            if (item == null) continue;
            
            // type ---------------------------------------------
            if (item.getType() == null) {
                printlnStatus("*** unknown module type.");
            } else {
                printlnStatus("type: " + item.getType());
            }
            
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
                //continue;
            }
            /*
             if ( item.getDocInfo().getContentModuleType().equals("test") == false ) {
                // this module is not a lobo test
                printlnStatus("*** this module is not a labo test.");
                continue;
            }
             */
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
                    /*
                     if ( item.getDocInfo().getTitle().getGenerationPurpose().equals("reportTest") == false ) {
                        printlnStatus("*** generationPurpose doesn't equal to \"reportTest\".");
                    }
                     */
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
            
            // start ----------------------------------
            // end ------------------------------------

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
                        licenses[k] = tid + "<>" + ls;
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
            
            /*
            //----------------------------------------------------
            // keep each mmlCmextRef in allExtRefs
            //
            // NOTE: this is for getting all extRef objects in xml later.
            for ( int i = 0; i < exts.size(); ++i ) {
                mmlCmextRef ref = (mmlCmextRef)exts.elementAt(i);
                if ( ref == null ) continue;
                allExtRefs.addElement(ref);
            }
             */
            //----------------------------------------------------
            //
            processExtRefs(exts);
            //
            //------------------------------------------------------------------
            
            //-------------
            // content 
            //-------------
            if (item.getContent() == null) {
                printlnStatus("*** content object is null");
                continue;
            }
        }
    }
    
    public void processExtRefs(Vector exts) {
        if (exts == null) return;
        
        //printlnStatus("*** processExtRefs() ***");
        //printlnStatus("exts.size() is " + String.valueOf(exts.size()));
        
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
        }
        
        //printlnStatus("**************************");
    }
    
    //--------------------------------------------------------------------

    public Vector sortModules(Vector v) {
        // Vector v is a MmlModuleItem+ in MmlBody        
        Enumeration e = v.elements();
        
        String[] ioKeys = new String[v.size()];
        int keyIdx = 0;
        Vector sourceVector = new Vector();
        Vector resultVector = new Vector();
        
        while (e.hasMoreElements()) {
            //--------------
            // MmlModuleItem
            //--------------
            MmlModuleItem item = (MmlModuleItem)e.nextElement();
            if (item == null) continue;
            
            // type ---------------------------------------------
            if (item.getType() == null) {
                printlnStatus("*** unknown module type.");
            } else {
                printlnStatus("type: " + item.getType());
            }
            
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
                //continue;
            }
            /*
             if ( item.getDocInfo().getContentModuleType().equals("test") == false ) {
                // this module is not a lobo test
                printlnStatus("*** this module is not a labo test.");
                continue;
            }
             */
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
                    /*
                     if ( item.getDocInfo().getTitle().getGenerationPurpose().equals("reportTest") == false ) {
                        printlnStatus("*** generationPurpose doesn't equal to \"reportTest\".");
                    }
                     */
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
            
            // add key
            ioKeys[keyIdx] = confirmDate + "_" + String.valueOf(keyIdx);
            keyIdx++;
            sourceVector.addElement(item);
        }
        
        /////////////////////////////////////////////////////////////////
        sortKeys(ioKeys);
        
        int idx = -1;
        for (int j = 0; j < ioKeys.length; ++j) {
            idx = Integer.parseInt(
                ioKeys[j].substring(ioKeys[j].lastIndexOf("_") + 1, ioKeys[j].length())
            );
            System.out.println("Sorted: " + idx + ": " + ioKeys[j]);
            resultVector.addElement(sourceVector.elementAt(idx));
        }
        
        return resultVector;
        /////////////////////////////////////////////////////////////////       
    }
    
    public static void sortKeys(String[] ioKeys) {
        if (ioKeys == null) return;
        if (ioKeys.length <= 0) return;
        
        Collator enUSCollator = Collator.getInstance(new Locale("en","US"));
        CollationKey[] keys = new CollationKey[ioKeys.length];
        for (int k = 0; k < keys.length; k++) {
            keys[k] = enUSCollator.getCollationKey(ioKeys[k]);
        }
        sortArray(keys);
        for (int i = 0; i < keys.length; i++) {
            ioKeys[i] = keys[i].getSourceString();
        }
    }

    public static void sortArray(CollationKey[] keys) {
        CollationKey tmp;
        for (int i = 0; i < keys.length; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                 
                 /*
                 // ASCEND
                 // Compare the keys
                 if ( keys[i].compareTo(keys[j]) > 0 ) {
                    // Swap keys[i] and keys[j] 
                    tmp = keys[i];
                    keys[i] = keys[j];
                    keys[j] = tmp;
                 }
                 */
                 
                 // DESCEND
                 if ( keys[i].compareTo(keys[j]) <= 0 ) {
                    // Swap keys[i] and keys[j] 
                    tmp = keys[i];
                    keys[i] = keys[j];
                    keys[j] = tmp;
                 }
            }
        }
   }
}
