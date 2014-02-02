/*
 * StatusPanel.java
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
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
public class PVTBuilder {
    
    //private static final Namespace xhtml = Namespace.getNamespace("xhtml","http://www.w3.org/1999/xhtml");
    private static final Namespace mmlCm = Namespace.getNamespace("mmlCm","http://www.medxml.net/MML/SharedComponent/Common/1.0");
    private static final Namespace mmlNm = Namespace.getNamespace("mmlNm","http://www.medxml.net/MML/SharedComponent/Name/1.0");
    private static final Namespace mmlFc = Namespace.getNamespace("mmlFc","http://www.medxml.net/MML/SharedComponent/Facility/1.0");
    private static final Namespace mmlDp = Namespace.getNamespace("mmlDp","http://www.medxml.net/MML/SharedComponent/Department/1.0");
    private static final Namespace mmlAd = Namespace.getNamespace("mmlAd","http://www.medxml.net/MML/SharedComponent/Address/1.0");
    private static final Namespace mmlPh = Namespace.getNamespace("mmlPh","http://www.medxml.net/MML/SharedComponent/Phone/1.0");
    private static final Namespace mmlPsi = Namespace.getNamespace("mmlPsi","http://www.medxml.net/MML/SharedComponent/PersonalizedInfo/1.0");
    private static final Namespace mmlCi = Namespace.getNamespace("mmlCi","http://www.medxml.net/MML/SharedComponent/CreatorInfo/1.0");
    private static final Namespace mmlPi = Namespace.getNamespace("mmlPi","http://www.medxml.net/MML/ContentModule/PatientInfo/1.0");
    //private static final Namespace mmlBc = Namespace.getNamespace("mmlBc","http://www.medxml.net/MML/ContentModule/BaseClinic/1.0");
    //private static final Namespace mmlFcl = Namespace.getNamespace("mmlFcl","http://www.medxml.net/MML/ContentModule/FirstClinic/1.0");
    private static final Namespace mmlHi = Namespace.getNamespace("mmlHi","http://www.medxml.net/MML/ContentModule/HealthInsurance/1.1");
    //private static final Namespace mmlLs = Namespace.getNamespace("mmlLs","http://www.medxml.net/MML/ContentModule/Lifestyle/1.0");
    //private static final Namespace mmlPc = Namespace.getNamespace("mmlPc","http://www.medxml.net/MML/ContentModule/ProgressCourse/1.0");
    //private static final Namespace mmlRd = Namespace.getNamespace("mmlRd","http://www.medxml.net/MML/ContentModule/RegisteredDiagnosis/1.0");
    //private static final Namespace mmlSg = Namespace.getNamespace("mmlSg","http://www.medxml.net/MML/ContentModule/Surgery/1.0");
    //private static final Namespace mmlSm = Namespace.getNamespace("mmlSm","http://www.medxml.net/MML/ContentModule/Summary/1.0");
    //private static final Namespace mmlLb = Namespace.getNamespace("mmlLb","http://www.medxml.net/MML/ContentModule/test/1.0");
    //private static final Namespace mmlRp = Namespace.getNamespace("mmlRp","http://www.medxml.net/MML/ContentModule/report/1.0");
    //private static final Namespace mmlRe = Namespace.getNamespace("mmlRe","http://www.medxml.net/MML/ContentModule/Referral/1.0");
    private static final Namespace mmlSc = Namespace.getNamespace("mmlSc","http://www.medxml.net/MML/SharedComponent/Security/1.0");
    private static final Namespace claim = Namespace.getNamespace("claim","http://www.medxml.net/claim/claimModule/2.1");
    //private static final Namespace claimA = Namespace.getNamespace("claimA","http://www.medxml.net/claim/claimAmountModule/2.1");
    
    private boolean DERBY;
    
    private PatientModel patientModel;
    
    private AddressModel curAddress;
    
    private TelephoneModel curTelephone;
    
    private ArrayList<PVTHealthInsuranceModel> pvtInsurnaces;
    
    private PVTHealthInsuranceModel curInsurance;
    
    private PVTPublicInsuranceItemModel curPublicItem;
    
    private PVTClaim pvtClaim;
    
    private String curRepCode;
    
    private Logger logger;
    
    public void setLogger(Logger l) {
        this.logger = l;
    }
    
    /**
     * CLAIM モジュールをパースする。
     *
     * @param reader
     *            CLAIM モジュールへの Reader
     */
    public void parse(BufferedReader reader) {
        
        try {
            SAXBuilder docBuilder = new SAXBuilder();
            Document doc = docBuilder.build(reader);
            Element root = doc.getRootElement();
            parseBody(root.getChild("MmlBody"));
            reader.close();
            
        } catch (Exception e) {
            e.printStackTrace();
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
            
            // Dery Distribution
            if (DERBY) {
                String pid = "D_" + patientModel.getPatientId();
                patientModel.setPatientId(pid);
            }
            
            model.setPatient(patientModel);
            
            // ORCA CLAIM 特有の処理を行う
            // 全角のスペースを半角スペースに変換する
            String fullName = patientModel.getFullName();
            fullName = fullName.replace('　', ' ');
            patientModel.setFullName(fullName);
            int index = fullName.indexOf(" ");
            
            // FamilyName と GivenName を設定する
            if (patientModel.getFamilyName() == null && index > 0) {
                patientModel.setFamilyName(fullName.substring(0, index));
            }
            if (patientModel.getGivenName() == null && index > 0) {
                patientModel.setGivenName(fullName.substring(index+1));
            }
            
            String kana = patientModel.getKanaName();
            if (kana != null) {
                kana = kana.replace('　', ' ');
                patientModel.setKanaName(kana);
                int index2 = kana.indexOf(" ");
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
                    String addr = bean.getAddress().replace('　', ' ');
                    addr = addr.replace('ー', '-');
                    SimpleAddressModel simple = new SimpleAddressModel();
                    simple.setZipCode(bean.getZipCode());
                    simple.setAddress(addr);
                    patientModel.setAddress(simple);
                    System.out.println(simple.getAddress());
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
        
        //
        // 受付情報を設定する
        // status=info ありだって、ヤレヤレ...
        //
        //if (pvtClaim != null && (!pvtClaim.getClaimStatus().equals("info"))) {
        if (pvtClaim != null) {
            
            // 1.3 リリースまでの暫定
            StringBuilder sb = new StringBuilder();
            sb.append(pvtClaim.getClaimDeptName());
            sb.append(",");
            sb.append(pvtClaim.getClaimDeptCode());
            sb.append(",");
            sb.append(pvtClaim.getAssignedDoctorName());
            sb.append(",");
            sb.append(pvtClaim.getAssignedDoctorId());
            sb.append(",");
            sb.append(pvtClaim.getJmariCode());
      
            model.setDepartment(sb.toString());
            model.setPvtDate(pvtClaim.getClaimRegistTime());
            model.setInsuranceUid(pvtClaim.getInsuranceUid()); // UUID
        }
        
        return model;
    }
    
    /**
     * MmlBody 要素をパースする。
     *
     * @param current
     *            要素
     */
    public void parseBody(Element body) {
        
        // MmlModuleItem のリストを得る
        List children = body.getChildren("MmlModuleItem");
        
        //
        // それをイテレートする
        //
        for (Iterator iterator = children.iterator(); iterator.hasNext();) {
            
            Element moduleItem = (Element) iterator.next();
            
            //
            // ModuleItem = docInfo + content なので夫々の要素を得る
            //
            Element docInfo = moduleItem.getChild("docInfo");
            Element content = moduleItem.getChild("content");
            
            // docInfo の contentModuleType を調べる
            String attr = docInfo.getAttributeValue("contentModuleType");
            
            //
            // contentModuleTypeで分岐する
            //
            if (attr.equals("patientInfo")) {
                //
                // 患者モジュールをパースする
                //
                logger.debug("patientInfo　をパース中");
                patientModel = new PatientModel();
                parsePatientInfo(docInfo, content);
                
            } else if (attr.equals("healthInsurance")) {
                //
                // 健康保険モジュールをパースする
                //
                String uuid = docInfo.getChild("docId").getChildTextTrim("uid");
                logger.debug("healthInsurance　をパース中");
                logger.debug("HealthInsurance UUID = " + uuid);

                if (pvtInsurnaces == null) {
                    pvtInsurnaces = new ArrayList<PVTHealthInsuranceModel>();
                }
                curInsurance = new PVTHealthInsuranceModel();
                curInsurance.setGUID(uuid);
                pvtInsurnaces.add(curInsurance);
                parseHealthInsurance(docInfo, content);
                
            } else if (attr.equals("claim")) {
                //
                // 受付情報をパースする
                //
                logger.debug("claim　をパース中");
                pvtClaim = new PVTClaim();
                parseClaim(docInfo, content);
                
            } else {
                logger.debug("Unknown attribute value : " + attr);
            }
        }
    }
    
    /**
     * 患者モジュールをパースする。
     *
     * @param content
     *            患者要素
     */
    private void parsePatientInfo(Element docInfo, Element content) {
        
        List children = content.getChildren();
        
        //
        // 患者モジュールの要素をイテレートする
        //
        for (Iterator iterator = children.iterator(); iterator.hasNext();) {
            
            Element child = (Element) iterator.next();
            String ename = child.getName();
            //System.err.println("ename=" + ename);
            String qname = child.getQualifiedName();
            // Namespace ns = child.getNamespace();
            //debug(child.toString());
            
            if (qname.equals("mmlCm:Id")) {
                String pid = child.getTextTrim();
                patientModel.setPatientId(pid);
                logger.debug("patientId = " + pid);
                
            } else if (qname.equals("mmlNm:Name")) {
                List attrs = child.getAttributes();
                for (Iterator iter = attrs.iterator(); iter.hasNext(); ) {
                    Attribute attr = (Attribute) iter.next();
                    if (attr.getName().equals("repCode")) {
                        curRepCode = attr.getValue();
                        logger.debug("curRepCode = " + attr.getValue());
                    } else if (attr.getName().equals("tableId")) {
                        logger.debug("tableId = " + attr.getValue());
                    }
                }
                
            } else if (qname.equals("mmlNm:family")) {
                if (curRepCode.equals("P")) {
                    patientModel.setKanaFamilyName(child.getTextTrim());
                } else if (curRepCode.equals("I")) {
                    patientModel.setFamilyName(child.getTextTrim());
                } else if (curRepCode.equals("A")) {
                    patientModel.setRomanFamilyName(child.getTextTrim());
                }
                logger.debug("family = " + child.getTextTrim());
                
            } else if (qname.equals("mmlNm:given")) {
                if (curRepCode.equals("P")) {
                    patientModel.setKanaGivenName(child.getTextTrim());
                } else if (curRepCode.equals("I")) {
                    patientModel.setGivenName(child.getTextTrim());
                } else if (curRepCode.equals("A")) {
                    patientModel.setRomanGivenName(child.getTextTrim());
                }
                logger.debug("given = " + child.getTextTrim());
                
            } else if (qname.equals("mmlNm:fullname")) {
                if (curRepCode.equals("P")) {
                    patientModel.setKanaName(child.getTextTrim());
                } else if (curRepCode.equals("I")) {
                    patientModel.setFullName(child.getTextTrim());
                } else if (curRepCode.equals("A")) {
                    patientModel.setRomanName(child.getTextTrim());
                }
                logger.debug("fullName = " + child.getTextTrim());
                
            } else if (qname.equals("mmlPi:birthday")) {
                patientModel.setBirthday(child.getTextTrim());
                logger.debug("birthday = " + child.getTextTrim());
                
            } else if (qname.equals("mmlPi:sex")) {
                patientModel.setGender(child.getTextTrim());
                logger.debug("gender = " + child.getTextTrim());
                
            } else if (qname.equals("mmlAd:Address")) {
                curAddress = new AddressModel();
                patientModel.addAddress(curAddress);
                
                List attrs = child.getAttributes();
                for (Iterator iter = attrs.iterator(); iter.hasNext(); ) {
                    Attribute attr = (Attribute) iter.next();
                    if (attr.getName().equals("addressClass")) {
                        curRepCode = attr.getValue();
                        curAddress.setAddressType(attr.getValue());
                        logger.debug("addressClass = " + attr.getValue());
                    } else if (attr.getName().equals("tableId")) {
                        curAddress.setAddressTypeCodeSys(attr.getValue());
                        logger.debug("tableId = " + attr.getValue());
                    }
                }
                
            } else if (qname.equals("mmlAd:full")) {
                curAddress.setAddress(child.getTextTrim());
                logger.debug("address = " + child.getTextTrim());
                
            } else if (qname.equals("mmlAd:zip")) {
                curAddress.setZipCode(child.getTextTrim());
                logger.debug("zip = " + child.getTextTrim());
                
            } else if (qname.equals("mmlPh:Phone")) {
                curTelephone = new TelephoneModel();
                patientModel.addTelephone(curTelephone);
                
            } else if (qname.equals("mmlPh:area")) {
                String val = child.getTextTrim();
                // ORCA
                if (val != null && val.startsWith("?") == false) {
                    curTelephone.setArea(child.getTextTrim());
                }
                logger.debug("area = " + val);
                
            } else if (qname.equals("mmlPh:city")) {
                String val = child.getTextTrim();
                // ORCA
                if (val != null && val.startsWith("?") == false) {
                    curTelephone.setCity(val);
                }
                logger.debug("city = " + val);
                
            } else if (qname.equals("mmlPh:number")) {
                String val = child.getTextTrim();
                // ORCA
                if (val != null && val.startsWith("?") == false) {
                    curTelephone.setNumber(val);
                }
                logger.debug("number = " + val);
                
            } else if (qname.equals("mmlPh:memo")) {
                // ORCA
                curTelephone.setMemo(child.getTextTrim());
                logger.debug("memo = " + child.getTextTrim());
            }
            
            parsePatientInfo(docInfo, child);
        }
    }
    
    /**
     * 健康保険モジュールをパースする。
     *
     * @param content
     *            健康保険要素
     */
    private void parseHealthInsurance(Element docInfo, Element content) {
        
        // HealthInsuranceModule を得る
        Element hModule = content.getChild("HealthInsuranceModule", mmlHi);
        if (hModule == null) {
            logger.debug("No HealthInsuranceModule");
            return;
        }
        
        // InsuranceClass を解析する
        Element insuranceClass = hModule.getChild("insuranceClass", mmlHi);
        if (insuranceClass != null) {
            curInsurance.setInsuranceClass(insuranceClass.getTextTrim());
            if (insuranceClass.getAttribute("ClassCode", mmlHi) != null) {
                curInsurance.setInsuranceClassCode(insuranceClass.getAttributeValue("ClassCode", mmlHi));
            }
            if (insuranceClass.getAttribute("tableId", mmlHi) != null) {
                curInsurance.setInsuranceClassCodeSys(insuranceClass.getAttributeValue("tableId", mmlHi));
            }
            logger.debug("insuranceClass = " + curInsurance.getInsuranceClass());
            logger.debug("insurance ClassCode = " + curInsurance.getInsuranceClassCode());
            logger.debug("insurance tableId = " + curInsurance.getInsuranceClassCodeSys());
        }
        
        // insurance Number を得る
        if (hModule.getChildTextTrim("insuranceNumber", mmlHi) != null) {
            curInsurance.setInsuranceNumber(hModule.getChildTextTrim("insuranceNumber", mmlHi));
            logger.debug("insuranceNumber = " + curInsurance.getInsuranceNumber());
        }
        
        // clientId を得る
        Element clientId = hModule.getChild("clientId", mmlHi);
        if (clientId != null) {
            if (clientId.getChild("group",mmlHi) != null) {
                curInsurance.setClientGroup(clientId.getChildTextTrim("group",mmlHi));
                logger.debug("group = " + curInsurance.getClientGroup());
            }
            if (clientId.getChild("number",mmlHi) != null) {
                curInsurance.setClientNumber(clientId.getChildTextTrim("number",mmlHi));
                logger.debug("number = " + curInsurance.getClientNumber());
            }
        }
        
        // familyClass を得る
        if (hModule.getChild("familyClass", mmlHi) != null) {
            curInsurance.setFamilyClass(hModule.getChildTextTrim("familyClass", mmlHi));
            logger.debug("familyClass = " + curInsurance.getFamilyClass());
        }
        
        // startDateを得る
        if (hModule.getChild("startDate", mmlHi) != null) {
            curInsurance.setStartDate(hModule.getChildTextTrim("startDate", mmlHi));
            logger.debug("startDate = " + curInsurance.getStartDate());
        }
        
        // expiredDateを得る
        if (hModule.getChild("expiredDate", mmlHi) != null) {
            curInsurance.setExpiredDate(hModule.getChildTextTrim("expiredDate", mmlHi));
            logger.debug("expiredDate = " + curInsurance.getExpiredDate());
        }
        
        // payInRatio を得る
        if (hModule.getChild("paymentInRatio", mmlHi) != null) {
            curInsurance.setPayInRatio(hModule.getChildTextTrim("paymentInRatio", mmlHi));
            logger.debug("paymentInRatio = " + curInsurance.getPayInRatio());
        }
        
        // payOutRatio を得る
        if (hModule.getChild("paymentOutRatio", mmlHi) != null) {
            curInsurance.setPayOutRatio(hModule.getChildTextTrim("paymentOutRatio", mmlHi));
            logger.debug("paymentOutRatio = " + curInsurance.getPayOutRatio());
        }
        
        //
        // publicInsurance をパースする
        //
        Element publicInsurance = hModule.getChild("publicInsurance", mmlHi);
        if (publicInsurance != null) {
            
            List children = publicInsurance.getChildren();
            
            for (Iterator iterator = children.iterator(); iterator.hasNext();) {
                
                // publicInsuranceItem を得る
                Element publicInsuranceItem = (Element) iterator.next();
                
                curPublicItem = new PVTPublicInsuranceItemModel();
                curInsurance.addPvtPublicInsuranceItem(curPublicItem);
            
                // priority
                if (publicInsuranceItem.getAttribute("priority", mmlHi) != null) {
                    curPublicItem.setPriority(publicInsuranceItem.getAttributeValue("priority", mmlHi));
                    logger.debug("priority = " + curPublicItem.getPriority());
                }
                
                // providerName
                if (publicInsuranceItem.getChild("providerName", mmlHi) != null) {
                    curPublicItem.setProviderName(publicInsuranceItem.getChildTextTrim("providerName", mmlHi));
                    logger.debug("providerName = " + curPublicItem.getProviderName());
                }
                
                // provider
                if (publicInsuranceItem.getChild("provider", mmlHi) != null) {
                    curPublicItem.setProvider(publicInsuranceItem.getChildTextTrim("provider", mmlHi));
                    logger.debug("provider = " + curPublicItem.getProvider());
                }
                
                // recipient
                if (publicInsuranceItem.getChild("recipient", mmlHi) != null) {
                    curPublicItem.setRecipient(publicInsuranceItem.getChildTextTrim("recipient", mmlHi));
                    logger.debug("recipient = " + curPublicItem.getRecipient());
                }
                
                // startDate
                if (publicInsuranceItem.getChild("startDate", mmlHi) != null) {
                    curPublicItem.setStartDate(publicInsuranceItem.getChildTextTrim("startDate", mmlHi));
                    logger.debug("startDate = " + curPublicItem.getStartDate());
                }
                
                // expiredDate
                if (publicInsuranceItem.getChild("expiredDate", mmlHi) != null) {
                    curPublicItem.setExpiredDate(publicInsuranceItem.getChildTextTrim("expiredDate", mmlHi));
                    logger.debug("expiredDate = " + curPublicItem.getExpiredDate());
                }
                
                // paymentRatio
                Element paymentRatio = publicInsuranceItem.getChild("paymentRatio", mmlHi);
                if (paymentRatio != null) {
                    curPublicItem.setPaymentRatio(paymentRatio.getTextTrim());
                    logger.debug("paymentRatio = " + curPublicItem.getPaymentRatio());
                    
                    if (paymentRatio.getAttribute("ratioType", mmlHi) != null) {
                        curPublicItem.setPaymentRatioType(paymentRatio.getAttributeValue("ratioType", mmlHi));
                        logger.debug("paymentRatioType = " + curPublicItem.getPaymentRatioType());
                    }
                }
            }
        }
    }
    
    /**
     * 受付情報をパースする。
     *
     * @param content
     *            受付情報要素
     */
    private void parseClaim(Element docInfo, Element content) {
        
        //
        // ClaimModule の DocInfo に含まれる診療科と担当医を抽出する
        //
        Element creatorInfo = docInfo.getChild("CreatorInfo", mmlCi);
        Element psiInfo = creatorInfo.getChild("PersonalizedInfo", mmlPsi);
        
        // 担当医ID
        pvtClaim.setAssignedDoctorId(psiInfo.getChildTextTrim("Id", mmlCm));
        
        // 担当医名
        Element personName = psiInfo.getChild("personName", mmlPsi);
        Element name = personName.getChild("Name", mmlNm);
        if (name != null) {
            Element fullName = name.getChild("fullname", mmlNm);
            if (fullName != null) {
                pvtClaim.setAssignedDoctorName(fullName.getTextTrim());
            }
        }
        
        // 施設情報 JMARI 4.0 から
        Element facility = psiInfo.getChild("Facility", mmlFc);
        pvtClaim.setJmariCode(facility.getChildTextTrim("Id", mmlCm));
        
        // 診療科情報
        Element dept = psiInfo.getChild("Department", mmlDp);
        pvtClaim.setClaimDeptName(dept.getChildTextTrim("name", mmlDp));
        pvtClaim.setClaimDeptCode(dept.getChildTextTrim("Id", mmlCm));
        
        // DEBUG 出力
        logger.debug("担当医ID = " + pvtClaim.getAssignedDoctorId());
        logger.debug("担当医名 = " + pvtClaim.getAssignedDoctorName());
        logger.debug("JMARI コード = " + pvtClaim.getJmariCode());
        logger.debug("診療科名 = " + pvtClaim.getClaimDeptName());
        logger.debug("診療科コード = " + pvtClaim.getClaimDeptCode());
        
        
        // ClaimInfoを解析する
        Element claimModule = content.getChild("ClaimModule", claim);
        Element claimInfo = claimModule.getChild("information", claim);
        
        // status
        pvtClaim.setClaimStatus(claimInfo.getAttributeValue("status", claim));
        
        // registTime
        pvtClaim.setClaimRegistTime(claimInfo.getAttributeValue("registTime", claim));
        
        // admitFlag
        pvtClaim.setClaimAdmitFlag(claimInfo.getAttributeValue("admitFlag", claim));
        
        // insuranceUid
        pvtClaim.setInsuranceUid(claimInfo.getAttributeValue("insuranceUid", claim));
        
        // DEBUG
        logger.debug("status = " + pvtClaim.getClaimStatus());
        logger.debug("registTime = " + pvtClaim.getClaimRegistTime());
        logger.debug("admitFlag = " + pvtClaim.getClaimAdmitFlag());
        logger.debug("insuranceUid = " + pvtClaim.getInsuranceUid());
    }
    
    protected byte[] getXMLBytes(Object bean) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));
        e.writeObject(bean);
        e.close();
        return bo.toByteArray();
    }
}
