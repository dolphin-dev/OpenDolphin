package open.dolphin.server;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import open.dolphin.infomodel.AddressModel;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTClaim;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.PVTPublicInsuranceItemModel;
import open.dolphin.infomodel.SimpleAddressModel;
import open.dolphin.infomodel.TelephoneModel;
import org.apache.log4j.Level;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.Namespace;

/**
 * PVTBuilder
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PVTBuilder {

    private static final Namespace mmlCm = Namespace.getNamespace("mmlCm","http://www.medxml.net/MML/SharedComponent/Common/1.0");
    private static final Namespace mmlNm = Namespace.getNamespace("mmlNm","http://www.medxml.net/MML/SharedComponent/Name/1.0");
    private static final Namespace mmlFc = Namespace.getNamespace("mmlFc","http://www.medxml.net/MML/SharedComponent/Facility/1.0");
    private static final Namespace mmlDp = Namespace.getNamespace("mmlDp","http://www.medxml.net/MML/SharedComponent/Department/1.0");
    private static final Namespace mmlAd = Namespace.getNamespace("mmlAd","http://www.medxml.net/MML/SharedComponent/Address/1.0");
    private static final Namespace mmlPh = Namespace.getNamespace("mmlPh","http://www.medxml.net/MML/SharedComponent/Phone/1.0");
    private static final Namespace mmlPsi = Namespace.getNamespace("mmlPsi","http://www.medxml.net/MML/SharedComponent/PersonalizedInfo/1.0");
    private static final Namespace mmlCi = Namespace.getNamespace("mmlCi","http://www.medxml.net/MML/SharedComponent/CreatorInfo/1.0");
    private static final Namespace mmlPi = Namespace.getNamespace("mmlPi","http://www.medxml.net/MML/ContentModule/PatientInfo/1.0");
    private static final Namespace mmlHi = Namespace.getNamespace("mmlHi","http://www.medxml.net/MML/ContentModule/HealthInsurance/1.1");
    private static final Namespace mmlSc = Namespace.getNamespace("mmlSc","http://www.medxml.net/MML/SharedComponent/Security/1.0");
    private static final Namespace claim = Namespace.getNamespace("claim","http://www.medxml.net/claim/claimModule/2.1");
    
    private static final String MmlBody = "MmlBody";
    private static final String MmlModuleItem = "MmlModuleItem";
    private static final String docInfo = "docInfo";
    private static final String content = "content";
    private static final String contentModuleType = "contentModuleType";
    private static final String patientInfo = "patientInfo";
    private static final String healthInsurance = "healthInsurance";
    private static final String docId = "docId";
    private static final String uid = "uid";
    private static final String e_claim = "claim";
    private static final String mmlCm_Id = "mmlCm:Id";
    private static final String mmlNm_Name = "mmlNm:Name";
    private static final String repCode = "repCode";
    private static final String tableId = "tableId";
    private static final String mmlNm_family = "mmlNm:family";
    private static final String P = "P";
    private static final String I = "I";
    private static final String A = "A";
    private static final String mmlNm_given = "mmlNm:given";
    private static final String mmlNm_fullname = "mmlNm:fullname";
    private static final String mmlPi_birthday = "mmlPi:birthday";
    private static final String mmlPi_sex = "mmlPi:sex";
    private static final String mmlAd_Address = "mmlAd:Address";
    private static final String addressClass = "addressClass";
    private static final String mmlAd_full = "mmlAd:full";
    private static final String mmlAd_zip = "mmlAd:zip";
    private static final String mmlPh_Phone = "mmlPh:Phone";
    private static final String mmlPh_area = "mmlPh:area";
    private static final String mmlPh_city = "mmlPh:city";
    private static final String mmlPh_number = "mmlPh:number";
    private static final String mmlPh_memo = "mmlPh:memo";
    private static final String HealthInsuranceModule = "HealthInsuranceModule";
    private static final String insuranceClass = "insuranceClass";
    private static final String ClassCode = "ClassCode";
    private static final String insuranceNumber = "insuranceNumber";
    private static final String clientId = "clientId";
    private static final String group = "group";
    private static final String number = "number";
    private static final String familyClass = "familyClass";
    private static final String startDate = "startDate";
    private static final String expiredDate = "expiredDate";
    private static final String paymentInRatio = "paymentInRatio";
    private static final String paymentOutRatio = "paymentOutRatio";
    private static final String publicInsurance = "publicInsurance";
    private static final String priority = "priority";
    private static final String providerName = "providerName";
    private static final String provider = "provider";
    private static final String recipient = "recipient";
    private static final String paymentRatio = "paymentRatio";
    private static final String ratioType = "ratioType";
    private static final String CreatorInfo = "CreatorInfo";
    private static final String PersonalizedInfo = "PersonalizedInfo";
    private static final String Id = "Id";
    private static final String personName = "personName";
    private static final String Name = "Name";
    private static final String fullname = "fullname";
    private static final String Facility = "Facility";
    private static final String Department = "Department";
    private static final String name = "name";
    private static final String ClaimModule = "ClaimModule";
    private static final String information = "information";
    private static final String status = "status";
    private static final String registTime = "registTime";
    private static final String admitFlag = "admitFlag";
    private static final String insuranceUid = "insuranceUid";

    private static final char FULL_SPACE = '　';
    private static final char HALF_SPACE = ' ';
    
    private PatientModel patientModel;
    
    private AddressModel curAddress;
    
    private TelephoneModel curTelephone;
    
    private ArrayList<PVTHealthInsuranceModel> pvtInsurnaces;
    
    private PVTHealthInsuranceModel curInsurance;
    
    private PVTPublicInsuranceItemModel curPublicItem;
    
    private PVTClaim pvtClaim;
    
    private String curRepCode;
    
    private Logger logger;

    private boolean DEBUG;
    
    public void setLogger(Logger logger) {
        this.logger = logger;
        DEBUG = (logger != null && logger.getLevel() == Level.DEBUG) ? true : false;
    }
    
    /**
     * CLAIM モジュールをパースする。
     *
     * @param reader CLAIM モジュールへの Reader
     */
    public void parse(BufferedReader reader) {
        
        try {
            SAXBuilder docBuilder = new SAXBuilder();
            Document doc = docBuilder.build(reader);
            Element root = doc.getRootElement();
            parseBody(root.getChild(MmlBody));
            reader.close();
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            logger.warn(e.getMessage());
        }
    }
    
    /**
     * CLAIM モジュールをパースして得た PatientVisitModel オブジェクトを返す。
     *
     * @return パース結果の PatientVisitModel
     */
    public PatientVisitModel getProduct() {
        
        // PatientVisitModelを生成する
        PatientVisitModel model = new PatientVisitModel();
        
        // 患者モデルを設定する
        if (patientModel != null) {
            
            model.setPatientModel(patientModel);
            
            // ORCA CLAIM 特有の処理を行う
            // 全角のスペースを半角スペースに変換する
            String fullName = patientModel.getFullName();
            fullName = fullName.replace(FULL_SPACE, HALF_SPACE);
            patientModel.setFullName(fullName);
            
            // FamilyName と GivenName を設定する
            int index = fullName.indexOf(HALF_SPACE);
            if (patientModel.getFamilyName() == null && index > 0) {
                patientModel.setFamilyName(fullName.substring(0, index));
            }
            if (patientModel.getGivenName() == null && index > 0) {
                patientModel.setGivenName(fullName.substring(index+1));
            }
            
            String kana = patientModel.getKanaName();
            if (kana != null) {
                kana = kana.replace(FULL_SPACE, HALF_SPACE);
                patientModel.setKanaName(kana);
                int index2 = kana.indexOf(HALF_SPACE);
                if (patientModel.getKanaFamilyName() == null && index2 > 0) {
                    patientModel.setKanaFamilyName(kana.substring(0, index2));
                }
                if (patientModel.getKanaGivenName() == null && index2 > 0) {
                    patientModel.setKanaGivenName(kana.substring(index2+1));
                }
            }
            
            // 住所をEmbedded に変換する
            Collection<AddressModel> addresses = patientModel.getAddresses();
            if (addresses != null && addresses.size() > 0) {
                for (AddressModel bean : addresses) {
                    String addr = bean.getAddress().replace(FULL_SPACE, HALF_SPACE);
                    addr = addr.replace('ー', '-');
                    SimpleAddressModel simple = new SimpleAddressModel();
                    simple.setZipCode(bean.getZipCode());
                    simple.setAddress(addr);
                    patientModel.setSimpleAddressModel(simple);
                    break;  // TODO
                }
            }
            
            // 電話をフィールドにする
            Collection<TelephoneModel> telephones = patientModel.getTelephones();
            if (telephones != null) {
                for (TelephoneModel bean : telephones) {
                    // MEMO へ設定
                    patientModel.setTelephone(bean.getMemo());
                }
            }
            
            // 健康保険モジュールを設定する
            if (pvtInsurnaces != null && pvtInsurnaces.size() > 0) {
                for (PVTHealthInsuranceModel bean : pvtInsurnaces) {
                    // 健康保険モジュールの BeanXml byte を生成し、
                    // 永続化のためのフォルダ HealthInsuranceModelに変換し
                    // それを患者属性に追加する
                    HealthInsuranceModel insModel = new HealthInsuranceModel();
                    insModel.setBeanBytes(getXMLBytes(bean));
                    // EJB 3.0 の関連を設定する
                    patientModel.addHealthInsurance(insModel);
                    insModel.setPatient(patientModel);
                }
            }
        }
        
        //------------------------------------------------------
        // 受付情報を設定する
        // status=info ありだって、ヤレヤレ...
        //------------------------------------------------------
        if (pvtClaim != null) {

            // 2.0 から
            model.setDeptCode(pvtClaim.getClaimDeptCode());         // 診療科コード
            model.setDeptName(pvtClaim.getClaimDeptName());         // 診療科名
            model.setDoctorId(pvtClaim.getAssignedDoctorId());      // 担当医コード
            model.setDoctorName(pvtClaim.getAssignedDoctorName());  // 担当医名
            model.setJmariNumber(pvtClaim.getJmariCode());          // JMARI
            model.setPvtDate(pvtClaim.getClaimRegistTime());        // 受付登録日時
            model.setInsuranceUid(pvtClaim.getInsuranceUid());      // UUID
            if (pvtInsurnaces != null && pvtInsurnaces.size() > 0) {
                PVTHealthInsuranceModel bean = pvtInsurnaces.get(0);    // 受付た保険情報
                model.setFirstInsurance(bean.toString());
            }
        }
        
        return model;
    }
    
    /**
     * MmlBody 要素をパースする。
     *
     * @param current 要素
     */
    public void parseBody(Element body) {
        
        // MmlModuleItem のリストを得る
        List children = body.getChildren(MmlModuleItem);
        
        //----------------------------
        // それをイテレートする
        //----------------------------
        for (Iterator iterator = children.iterator(); iterator.hasNext();) {
            
            Element moduleItem = (Element) iterator.next();
            
            //---------------------------------------------------
            // ModuleItem = docInfo + content なので夫々の要素を得る
            //---------------------------------------------------
            Element docInfoEle = moduleItem.getChild(docInfo);
            Element contentEle = moduleItem.getChild(content);
            
            // docInfo の contentModuleType を調べる
            String attr = docInfoEle.getAttributeValue(contentModuleType);
            
            //------------------------------
            // contentModuleTypeで分岐する
            //------------------------------
            if (attr.equals(patientInfo)) {
                //-----------------------
                // 患者モジュールをパースする
                //-----------------------
                if (DEBUG) {
                    logger.debug("patientInfo　をパース中");
                }
                patientModel = new PatientModel();
                parsePatientInfo(docInfoEle, contentEle);
                
            } else if (attr.equals(healthInsurance)) {
                //------------------------------
                // 健康保険モジュールをパースする
                //------------------------------
                String uuid = docInfoEle.getChild(docId).getChildTextTrim(uid);
                if (DEBUG) {
                    logger.debug("healthInsurance　をパース中");
                    logger.debug("HealthInsurance UUID = " + uuid);
                }

                if (pvtInsurnaces == null) {
                    pvtInsurnaces = new ArrayList<PVTHealthInsuranceModel>();
                }
                curInsurance = new PVTHealthInsuranceModel();
                curInsurance.setGUID(uuid);
                pvtInsurnaces.add(curInsurance);
                parseHealthInsurance(docInfoEle, contentEle);
                
            } else if (attr.equals(e_claim)) {
                //------------------------------
                // 受付情報をパースする
                //------------------------------
                if (DEBUG) {
                    logger.debug("claim　をパース中");
                }
                pvtClaim = new PVTClaim();
                parseClaim(docInfoEle, contentEle);
                
            } else {
                logger.warn("Unknown attribute value : " + attr);
            }
        }
    }
    
    /**
     * 患者モジュールをパースする。
     *
     * @param content 患者要素
     */
    private void parsePatientInfo(Element docInfo, Element content) {
        
        //-------------------------------------
        // 患者モジュールの要素をイテレートする
        //-------------------------------------
        List children = content.getChildren();

        for (Iterator iterator = children.iterator(); iterator.hasNext();) {
            
            Element child = (Element) iterator.next();
            String qname = child.getQualifiedName();
            
            if (qname.equals(mmlCm_Id)) {
                String pid = child.getTextTrim();
                patientModel.setPatientId(pid);
                if (DEBUG) {
                    logger.debug("patientId = " + pid);
                }
                
            } else if (qname.equals(mmlNm_Name)) {
                List attrs = child.getAttributes();
                for (Iterator iter = attrs.iterator(); iter.hasNext(); ) {
                    Attribute attr = (Attribute) iter.next();
                    if (attr.getName().equals(repCode)) {
                        curRepCode = attr.getValue();
                        if (DEBUG) {
                            logger.debug("curRepCode = " + attr.getValue());
                        }
                    } else if (attr.getName().equals(tableId)) {
                        if (DEBUG) {
                            logger.debug("tableId = " + attr.getValue());
                        }
                    }
                }
                
            } else if (qname.equals(mmlNm_family)) {
                if (curRepCode.equals(P)) {
                    patientModel.setKanaFamilyName(child.getTextTrim());
                } else if (curRepCode.equals(I)) {
                    patientModel.setFamilyName(child.getTextTrim());
                } else if (curRepCode.equals(A)) {
                    patientModel.setRomanFamilyName(child.getTextTrim());
                }
                if (DEBUG) {
                    logger.debug("family = " + child.getTextTrim());
                }
                
            } else if (qname.equals(mmlNm_given)) {
                if (curRepCode.equals(P)) {
                    patientModel.setKanaGivenName(child.getTextTrim());
                } else if (curRepCode.equals(I)) {
                    patientModel.setGivenName(child.getTextTrim());
                } else if (curRepCode.equals(A)) {
                    patientModel.setRomanGivenName(child.getTextTrim());
                }
                if (DEBUG) {
                    logger.debug("given = " + child.getTextTrim());
                }
                
            } else if (qname.equals(mmlNm_fullname)) {
                if (curRepCode.equals(P)) {
                    patientModel.setKanaName(child.getTextTrim());
                } else if (curRepCode.equals(I)) {
                    patientModel.setFullName(child.getTextTrim());
                } else if (curRepCode.equals(A)) {
                    patientModel.setRomanName(child.getTextTrim());
                }
                if (DEBUG) {
                    logger.debug("fullName = " + child.getTextTrim());
                }
                
            } else if (qname.equals(mmlPi_birthday)) {
                patientModel.setBirthday(child.getTextTrim());
                if (DEBUG) {
                    logger.debug("birthday = " + child.getTextTrim());
                }
                
            } else if (qname.equals(mmlPi_sex)) {
                patientModel.setGender(child.getTextTrim());
                if (DEBUG) {
                    logger.debug("gender = " + child.getTextTrim());
                }
                
            } else if (qname.equals(mmlAd_Address)) {
                curAddress = new AddressModel();
                patientModel.addAddress(curAddress);
                
                List attrs = child.getAttributes();
                for (Iterator iter = attrs.iterator(); iter.hasNext(); ) {
                    Attribute attr = (Attribute) iter.next();
                    if (attr.getName().equals(addressClass)) {
                        curRepCode = attr.getValue();
                        curAddress.setAddressType(attr.getValue());
                        if (DEBUG) {
                            logger.debug("addressClass = " + attr.getValue());
                        }
                    } else if (attr.getName().equals(tableId)) {
                        curAddress.setAddressTypeCodeSys(attr.getValue());
                        if (DEBUG) {
                            logger.debug("tableId = " + attr.getValue());
                        }
                    }
                }
                
            } else if (qname.equals(mmlAd_full)) {
                curAddress.setAddress(child.getTextTrim());
                if (DEBUG) {
                    logger.debug("address = " + child.getTextTrim());
                }
                
            } else if (qname.equals(mmlAd_zip)) {
                curAddress.setZipCode(child.getTextTrim());
                if (DEBUG) {
                    logger.debug("zip = " + child.getTextTrim());
                }
                
            } else if (qname.equals(mmlPh_Phone)) {
                curTelephone = new TelephoneModel();
                patientModel.addTelephone(curTelephone);
                
            } else if (qname.equals(mmlPh_area)) {
                String val = child.getTextTrim();
                //val = ZenkakuUtils.utf8Replace(val);
                curTelephone.setArea(val);
                if (DEBUG) {
                    logger.debug("area = " + val);
                }
                
            } else if (qname.equals(mmlPh_city)) {
                String val = child.getTextTrim();
                //val = ZenkakuUtils.utf8Replace(val);
                curTelephone.setCity(val);
                if (DEBUG) {
                    logger.debug("city = " + val);
                }
                
            } else if (qname.equals(mmlPh_number)) {
                String val = child.getTextTrim();
                //val = ZenkakuUtils.utf8Replace(val);
                curTelephone.setNumber(val);
                if (DEBUG) {
                    logger.debug("number = " + val);
                }
                
            } else if (qname.equals(mmlPh_memo)) {
                // ORCA
                curTelephone.setMemo(child.getTextTrim());
                if (DEBUG) {
                    logger.debug("memo = " + child.getTextTrim());
                }
            }
            
            parsePatientInfo(docInfo, child);
        }
    }
    
    /**
     * 健康保険モジュールをパースする。
     *
     * @param content 健康保険要素
     */
    private void parseHealthInsurance(Element docInfo, Element content) {
        
        // HealthInsuranceModule を得る
        Element hModule = content.getChild(HealthInsuranceModule, mmlHi);
        if (hModule == null) {
            logger.debug("No HealthInsuranceModule");
            return;
        }
        
        // InsuranceClass を解析する
        Element insuranceClassEle = hModule.getChild(insuranceClass, mmlHi);
        if (insuranceClass != null) {
            curInsurance.setInsuranceClass(insuranceClassEle.getTextTrim());
            if (insuranceClassEle.getAttribute(ClassCode, mmlHi) != null) {
                curInsurance.setInsuranceClassCode(insuranceClassEle.getAttributeValue(ClassCode, mmlHi));
            }
            if (insuranceClassEle.getAttribute(tableId, mmlHi) != null) {
                curInsurance.setInsuranceClassCodeSys(insuranceClassEle.getAttributeValue(tableId, mmlHi));
            }
        }
        
        // insurance Number を得る
        if (hModule.getChildTextTrim(insuranceNumber, mmlHi) != null) {
            curInsurance.setInsuranceNumber(hModule.getChildTextTrim(insuranceNumber, mmlHi));
        }
        
        // clientId を得る
        Element clientIdEle = hModule.getChild(clientId, mmlHi);
        if (clientIdEle != null) {
            if (clientIdEle.getChild(group,mmlHi) != null) {
                curInsurance.setClientGroup(clientIdEle.getChildTextTrim(group,mmlHi));
            }
            if (clientIdEle.getChild(number,mmlHi) != null) {
                curInsurance.setClientNumber(clientIdEle.getChildTextTrim(number,mmlHi));
            }
        }
        
        // familyClass を得る
        if (hModule.getChild(familyClass, mmlHi) != null) {
            curInsurance.setFamilyClass(hModule.getChildTextTrim(familyClass, mmlHi));
        }
        
        // startDateを得る
        if (hModule.getChild(startDate, mmlHi) != null) {
            curInsurance.setStartDate(hModule.getChildTextTrim(startDate, mmlHi));
        }
        
        // expiredDateを得る
        if (hModule.getChild(expiredDate, mmlHi) != null) {
            curInsurance.setExpiredDate(hModule.getChildTextTrim(expiredDate, mmlHi));
        }
        
        // payInRatio を得る
        if (hModule.getChild(paymentInRatio, mmlHi) != null) {
            curInsurance.setPayInRatio(hModule.getChildTextTrim(paymentInRatio, mmlHi));
        }
        
        // payOutRatio を得る
        if (hModule.getChild(paymentOutRatio, mmlHi) != null) {
            curInsurance.setPayOutRatio(hModule.getChildTextTrim(paymentOutRatio, mmlHi));
        }

        if (DEBUG) {
            logger.debug("insuranceClass = " + curInsurance.getInsuranceClass());
            logger.debug("insurance ClassCode = " + curInsurance.getInsuranceClassCode());
            logger.debug("insurance tableId = " + curInsurance.getInsuranceClassCodeSys());
            logger.debug("insuranceNumber = " + curInsurance.getInsuranceNumber());
            logger.debug("group = " + curInsurance.getClientGroup());
            logger.debug("number = " + curInsurance.getClientNumber());
            logger.debug("familyClass = " + curInsurance.getFamilyClass());
            logger.debug("startDate = " + curInsurance.getStartDate());
            logger.debug("expiredDate = " + curInsurance.getExpiredDate());
            logger.debug("paymentInRatio = " + curInsurance.getPayInRatio());
            logger.debug("paymentOutRatio = " + curInsurance.getPayOutRatio());
        }
        
        //--------------------------------
        // publicInsurance をパースする
        //--------------------------------
        Element publicInsuranceEle = hModule.getChild(publicInsurance, mmlHi);
        if (publicInsuranceEle != null) {
            
            List children = publicInsuranceEle.getChildren();
            
            for (Iterator iterator = children.iterator(); iterator.hasNext();) {
                
                // publicInsuranceItem を得る
                Element publicInsuranceItem = (Element) iterator.next();
                
                curPublicItem = new PVTPublicInsuranceItemModel();
                curInsurance.addPvtPublicInsuranceItem(curPublicItem);
            
                // priority
                if (publicInsuranceItem.getAttribute(priority, mmlHi) != null) {
                    curPublicItem.setPriority(publicInsuranceItem.getAttributeValue(priority, mmlHi));
                }
                
                // providerName
                if (publicInsuranceItem.getChild(providerName, mmlHi) != null) {
                    curPublicItem.setProviderName(publicInsuranceItem.getChildTextTrim(providerName, mmlHi));
                }
                
                // provider
                if (publicInsuranceItem.getChild(provider, mmlHi) != null) {
                    curPublicItem.setProvider(publicInsuranceItem.getChildTextTrim(provider, mmlHi));
                }
                
                // recipient
                if (publicInsuranceItem.getChild(recipient, mmlHi) != null) {
                    curPublicItem.setRecipient(publicInsuranceItem.getChildTextTrim(recipient, mmlHi));
                }
                
                // startDate
                if (publicInsuranceItem.getChild(startDate, mmlHi) != null) {
                    curPublicItem.setStartDate(publicInsuranceItem.getChildTextTrim(startDate, mmlHi));
                }
                
                // expiredDate
                if (publicInsuranceItem.getChild(expiredDate, mmlHi) != null) {
                    curPublicItem.setExpiredDate(publicInsuranceItem.getChildTextTrim(expiredDate, mmlHi));
                }
                
                // paymentRatio
                Element paymentRatioEle = publicInsuranceItem.getChild(paymentRatio, mmlHi);
                if (paymentRatioEle != null) {
                    curPublicItem.setPaymentRatio(paymentRatioEle.getTextTrim());
                    if (paymentRatioEle.getAttribute(ratioType, mmlHi) != null) {
                        curPublicItem.setPaymentRatioType(paymentRatioEle.getAttributeValue(ratioType, mmlHi));
                    }
                }

                if (DEBUG) {
                    logger.debug("priority = " + curPublicItem.getPriority());
                    logger.debug("providerName = " + curPublicItem.getProviderName());
                    logger.debug("provider = " + curPublicItem.getProvider());
                    logger.debug("recipient = " + curPublicItem.getRecipient());
                    logger.debug("startDate = " + curPublicItem.getStartDate());
                    logger.debug("expiredDate = " + curPublicItem.getExpiredDate());
                    logger.debug("paymentRatio = " + curPublicItem.getPaymentRatio());
                    logger.debug("paymentRatioType = " + curPublicItem.getPaymentRatioType());
                }
            }
        }
    }
    
    /**
     * 受付情報をパースする。
     *
     * @param content 受付情報要素
     */
    private void parseClaim(Element docInfo, Element content) {
        
        //-------------------------------------------------------
        // ClaimModule の DocInfo に含まれる診療科と担当医を抽出する
        //-------------------------------------------------------
        Element creatorInfo = docInfo.getChild(CreatorInfo, mmlCi);
        Element psiInfo = creatorInfo.getChild(PersonalizedInfo, mmlPsi);
        
        // 担当医ID
        pvtClaim.setAssignedDoctorId(psiInfo.getChildTextTrim(Id, mmlCm));
        
        // 担当医名
        Element personNameEle = psiInfo.getChild(personName, mmlPsi);
        Element nameEle = personNameEle.getChild(Name, mmlNm);
        if (nameEle != null) {
            Element fullName = nameEle.getChild(fullname, mmlNm);
            if (fullName != null) {
                pvtClaim.setAssignedDoctorName(fullName.getTextTrim());
            }
        }
        
        // 施設情報 JMARI 4.0 から
        Element facility = psiInfo.getChild(Facility, mmlFc);
        pvtClaim.setJmariCode(facility.getChildTextTrim(Id, mmlCm));
        
        // 診療科情報
        Element dept = psiInfo.getChild(Department, mmlDp);
        pvtClaim.setClaimDeptName(dept.getChildTextTrim(name, mmlDp));
        pvtClaim.setClaimDeptCode(dept.getChildTextTrim(Id, mmlCm));
        
        // ClaimInfoを解析する
        Element claimModule = content.getChild(ClaimModule, claim);
        Element claimInfo = claimModule.getChild(information, claim);
        
        // status
        pvtClaim.setClaimStatus(claimInfo.getAttributeValue(status, claim));
        
        // registTime
        pvtClaim.setClaimRegistTime(claimInfo.getAttributeValue(registTime, claim));
        
        // admitFlag
        pvtClaim.setClaimAdmitFlag(claimInfo.getAttributeValue(admitFlag, claim));
        
        // insuranceUid
        pvtClaim.setInsuranceUid(claimInfo.getAttributeValue(insuranceUid, claim));
        
        // DEBUG 出力
        if (DEBUG) {
            logger.debug("担当医ID = " + pvtClaim.getAssignedDoctorId());
            logger.debug("担当医名 = " + pvtClaim.getAssignedDoctorName());
            logger.debug("JMARI コード = " + pvtClaim.getJmariCode());
            logger.debug("診療科名 = " + pvtClaim.getClaimDeptName());
            logger.debug("診療科コード = " + pvtClaim.getClaimDeptCode());
            logger.debug("status = " + pvtClaim.getClaimStatus());
            logger.debug("registTime = " + pvtClaim.getClaimRegistTime());
            logger.debug("admitFlag = " + pvtClaim.getClaimAdmitFlag());
            logger.debug("insuranceUid = " + pvtClaim.getInsuranceUid());
        }
    }
    
    protected byte[] getXMLBytes(Object bean) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));
        e.writeObject(bean);
        e.close();
        return bo.toByteArray();
    }
}
