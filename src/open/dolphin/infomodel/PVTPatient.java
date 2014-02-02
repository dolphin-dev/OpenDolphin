/*
 * PVTPatient.java
 *
 * Created on 2001/10/10, 13:21
 */
package open.dolphin.infomodel;

import java.util.*;

/**
 * PVTPatient
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PVTPatient extends InfoModel {
    
    private static final long serialVersionUID = -8528910646549208765L;
	private String patientId;
    private String idType;
    private String tableId;
    private Vector repCode = new Vector(3);
    private Vector fullName = new Vector(3);
    private Vector familyName = new Vector(3);
    private Vector givenName = new Vector(3);
    private Vector middleName = new Vector(3);
    private String prefix;
    private String degree;
    private String sex;
    private String birthday;
    private String nationality;
    private String marital;
    
    private LinkedList address;
    private LinkedList phone;
    

    /** Creates new PVTPatient */
    public PVTPatient() {
        super();
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String val) {
        patientId = val;
    }
    
    public String getIdType() {
        return idType;
    }
    
    public void setIdType(String val) {
        idType = val;
    } 
    
    public String getTableId() {
        return tableId;
    }
    
    public void setTableId(String val) {
        tableId = val;
    }     
    
    public Vector getRepCode() {
        return repCode;
    }
    
    @SuppressWarnings("unchecked")
	public void addRepCode(String val) {
        repCode.add(val);
    }
    
    public Vector getFullName() {
        return fullName;
    }
    
    @SuppressWarnings("unchecked")
	public void addFullName(String val) {
        fullName.add(val);
    }
    
    public Vector getFamilyName() {
        return familyName;
    }
    
    public String getFullName(String code) {
        //System.out.println("get fullname");
        String ret = null;
        if (repCode != null) {
            String val;
            for (int i = 0; i < repCode.size(); i++) {
                val = (String)repCode.get(i);
                if (val.equals(code)) {
                    
                    if (fullName.size() > 0) {
                        //System.out.println("fullname");
                        ret = (String)fullName.get(i);
                    }
                    else if ( (familyName.size() > 0) && (givenName.size() > 0) ) {
                        //System.out.println("family & given");
                        StringBuffer buf = new StringBuffer();
                        buf.append((String)familyName.get(i));
                        buf.append(" ");
                        buf.append((String)givenName.get(i));
                        if (middleName.size() > 0) {
                            buf.append((String)middleName.get(i));
                        }
                        ret = buf.toString();
                    }
                    
                    break;
                }  
            }
        }
        return ret;
    }
    
    @SuppressWarnings("unchecked")
	public void addFamilyName(String val) {
        familyName.add(val);
    } 
    
    public Vector getGivenName() {
        return givenName;
    }
    
    @SuppressWarnings("unchecked")
	public void addGivenName(String val) {
        givenName.add(val);
    }  
    
    public Vector getMiddleName() {
        return middleName;
    }
    
    @SuppressWarnings("unchecked")
	public void addMiddleName(String val) {
        middleName.add(val);
    }    
    
    public String getSex() {
        return sex;
    }
    
    public void setSex(String val) {
        sex = val;
    }
    
    public String getBirthday() {
        return birthday;
    }
    
    public void setBirthday(String val) {
        birthday = val;
    }    
    
    public String getNationality() {
        return nationality;
    }
    
    public void setNationality(String val) {
        nationality = val;
    } 
    
    public String getMarital() {
        return marital;
    }
    
    public void setMarital(String val) {
        marital = val;
    }  
    
    public String getDegree() {
        return degree;
    }
    
    public void setDegree(String val) {
        degree = val;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String val) {
        prefix = val;
    }    
    
    @SuppressWarnings("unchecked")
	public void addAddressRepCode(String val) {
        if (address == null) {
            address = new LinkedList();
        }
        Address item = new Address();
        item.adRepCode = val;
        address.addLast(item);
    }
    
    public void addAddressClass(String val) {
        Address item = (Address)address.getLast();
        item.adClass = val;
    }
    
    public void addAddressFull(String val) {
        Address item = (Address)address.getLast();
        item.adFull = val;
    }
    
    public void addAddressPrefecture(String val) {
        Address item = (Address)address.getLast();
        item.adPrefecture = val;
    }
    
    public void addAddressCity(String val) {
        Address item = (Address)address.getLast();
        item.adCity = val;
    }    
    
    public void addAddressTown(String val) {
        Address item = (Address)address.getLast();
        item.adTown = val;
    } 
    
    public void addAddressHomeNumber(String val) {
        Address item = (Address)address.getLast();
        item.adHomeNumber = val;
    }
    
    public void addAddressZipCode(String val) {
        Address item = (Address)address.getLast();
        item.adZip = val;
    } 
    
    public void addAddressCountryCode(String val) {
        Address item = (Address)address.getLast();
        item.adCountryCode = val;
    }     
    
    // Phone
    @SuppressWarnings("unchecked")
	public void addPhoneAreaNumber(String val) {
        if (phone == null) {
            phone = new LinkedList();
        }
        Phone item = new Phone();
        item.phArea = val;
        phone.addLast(item);        
    }    
    public void addPhoneCityNumber(String val) {
        Phone item = (Phone)phone.getLast();
        item.phCity = val;
    }
    public void addPhoneNumber(String val) {
        Phone item = (Phone)phone.getLast();
        item.phNumber = val;
    }
    public String[] getPhone() {
        String[] ret = null;
        if ( (phone != null)  && (phone.size() > 0) ){
            int len = phone.size();
            ret = new String[len];
            for (int i = 0; i < len; i++) {
                ret[i] = ((Phone)phone.get(i)).toString();
                //System.out.println(ret[i].toString());
            }
        }
        return ret;
    }
    
    //
    public String getFullAddress() {
        String ret = null;
        if (address != null) {
            for (int i = 0; i < address.size(); i++) {             
                Address ad = (Address)address.get(i);
                if ( (ad.adClass != null) && 
                     (ad.adClass.equals("home")) &&
                     (ad.adRepCode != null) &&
                     (ad.adRepCode.equals("I")) ) { 
                                                 
                     if (ad.adFull != null)  {
                        ret = ad.adFull;
                     }
                     else {
                        StringBuffer buf = new StringBuffer();
                        if (ad.adPrefecture != null) {
                            buf.append(ad.adPrefecture);
                        }
                        if (ad.adCity != null) {
                            buf.append(ad.adCity);
                        }
                        if (ad.adTown != null) {
                            buf.append(ad.adTown);
                        }
                        if (ad.adHomeNumber != null) {
                            buf.append(ad.adHomeNumber);
                        }
                        ret = buf.toString();
                     }
                     break;
                }                
            }
        }
        return ret;
    }
    
    public String getZipCode() {
        String ret = null;
        if (address != null) {
            for (int i = 0; i < address.size(); i++) {             
                Address ad = (Address)address.get(i);
                if ( (ad.adClass != null) && 
                     (ad.adClass.equals("home")) &&
                     (ad.adRepCode != null) &&
                     (ad.adRepCode.equals("I")) &&
                     (ad.adZip != null) ) {

                   ret = ad.adZip;
                   break;
                }                
            }
        }
        return ret;
    }    
    
    public String toString() {
        
        StringBuffer buf = new StringBuffer();

        if (patientId != null) {
            buf.append("Patient ID: ");
            buf.append(patientId);
            buf.append("\n"); 
        }
        
        if (idType != null) {
            buf.append("ID Type: ");
            buf.append(idType);
            buf.append("\n"); 
        } 
        
        if (tableId != null) {
            buf.append("ID Table ID: ");
            buf.append(tableId);
            buf.append("\n"); 
        }         

        int len = repCode.size();
        for (int i = 0; i < len; i++) {
            buf.append("RepCode: ");
            buf.append((String)repCode.get(i));
            buf.append(" ");

            if (fullName.size() > 0) {
                buf.append("fullName: ");
                buf.append((String) fullName.get(i));
            }
            else if (familyName.size() > 0) {
                buf.append("family: ");
                buf.append((String) familyName.get(i));
                buf.append(" given: ");
                buf.append((String) givenName.get(i));
                if (middleName.size() > 0) {
                    buf.append(" middle: ");
                    buf.append((String) middleName.get(i));
                }
            }
            buf.append("\n");
        }

        if (prefix != null) {
            buf.append("Prefix: ");
            buf.append(prefix);
            buf.append("\n");  
        }

        if (degree != null) {
            buf.append("Degree: ");
            buf.append(degree);
            buf.append("\n");  
        }      

        if (sex != null) {
            buf.append("Sex: ");
            buf.append(sex);
            buf.append("\n");  
        }  

        if (birthday != null) {
            buf.append("Birthday: ");
            buf.append(birthday);
            buf.append("\n");  
        } 

        if (nationality != null) {
            buf.append("Nationality: ");
            buf.append(nationality);
            buf.append("\n");  
        } 

        if (marital != null) {
            buf.append("Marital: ");
            buf.append(marital);
            buf.append("\n");  
        }
        
        if (address != null) {
            len = address.size();
            Address a;
            for (int i = 0; i < len; i++) {
                a = (Address)address.get(i);
                buf.append(a.toString());
            }
        }
        
        return buf.toString();
    }   
    
    protected final class Phone {
        public String phArea;
        public String phCity;
        public String phNumber;
        
        public Phone() {
            super();
        }
        
        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(phArea);
            buf.append("-");
            buf.append(phCity);
            buf.append("-");
            buf.append(phNumber);
            return buf.toString();
        }
    }
    
    protected final class Address {
        
        public String adRepCode;
        public String adClass;
        public String adFull;
        public String adPrefecture;
        public String adCity;
        public String adTown;
        public String adHomeNumber;
        public String adZip;
        public String adCountryCode;
        
        public Address() {
            super();
        }
        
        public String toString() {
            
            StringBuffer buf = new StringBuffer();
            
            if (adRepCode != null) {
                buf.append("RepCode: ");
                buf.append(adRepCode);
                buf.append("\n");
            }
            
            if (adClass != null) {
                buf.append("AddressClass: ");
                buf.append(adClass);
                buf.append("\n");
            } 
            
            if (adFull != null) {
                buf.append("AddressFull: ");
                buf.append(adFull);
                buf.append("\n");
            } 
            
            if (adPrefecture != null) {
                buf.append("Prefecture: ");
                buf.append(adPrefecture);
                buf.append("\n");
            }
            
            if (adCity != null) {
                buf.append("City: ");
                buf.append(adCity);
                buf.append("\n");
            }            
            
            if (adTown != null) {
                buf.append("Town: ");
                buf.append(adTown);
                buf.append("\n");
            }
            
            if (adHomeNumber != null) {
                buf.append("HomeNumber: ");
                buf.append(adHomeNumber);
                buf.append("\n");
            } 
            
            if (adZip != null) {
                buf.append("ZipCode: ");
                buf.append(adZip);
                buf.append("\n");
            } 
            
            if (adCountryCode != null) {
                buf.append("Country Code: ");
                buf.append(adCountryCode);
                buf.append("\n");
            }             
            
            return buf.toString();
        }       
    }
}