package open.dolphin.adm10.rest;

import java.beans.XMLDecoder;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author kazushi Minagawa, Digital Globe, Inc.
 */
public class AbstractResource {
    
    protected static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    protected static final String RESOURCE_START = "<mmlTouch version=\"1.0\">";
    protected static final String RESOURCE_END = "</mmlTouch>";
    protected static final String PROPERTY_START = "<property>";
    protected static final String PROPERTY_END = "</property>";
    protected static final String STRING_START = "<string>";
    protected static final String STRING_END = "</string>";

    protected static final String XML_LT = "&lt;";
    protected static final String XML_GT = "&gt;";
    protected static final String XML_AND = "&amp;";
    protected static final String XML_QUOT = "&quot;";
    protected static final String XML_APOS = "&apos;";

    protected static final String STRING_LT = "<";
    protected static final String STRING_GT = ">";
    protected static final String STRING_AND = "&";
    protected static final String STRING_QUOT = "\"";
    protected static final String STRING_APOS = "'";
    
    protected static final String DOLPHIN_ASP_OID = "1.3.6.1.4.1.9414.";
    protected static final String USER_START = "<user>";
    protected static final String USER_END = "</user>";
    protected static final String FACILITY_START = "<facility>";
    protected static final String FACILITY_END = "</facility>";
    protected static final String ELEMENT_USER_ID = "userId";
    protected static final String ELEMENT_COMMON_NAME = "commonName";
//minagawa^ VisitTouch追加    
    protected static final String ELEMENT_USER_PK = "pk";
    protected static final String ELEMENT_LICENSE = "license";
    protected static final String ELEMENT_LICENSE_DESC = "licenseDesc";
    protected static final String ELEMENT_DEPARTMENT = "department";
    protected static final String ELEMENT_DEPARTMENT_DESC = "departmentDesc";
    protected static final String ELEMENT_ORCA_ID = "orcaId";
//minagawa$    
    protected static final String ELEMENT_FACILITY_ID = "facilityId";
    protected static final String ELEMENT_FACILITY_NAME = "facilityName";
//minagawa^ VisitTouch追加   
    protected static final String ELEMENT_FACILITY_ZIP = "zipCode";
    protected static final String ELEMENT_FACILITY_ADDRESS = "address";
    protected static final String ELEMENT_FACILITY_TELEPHONE = "telephone";
    protected static final String ELEMENT_FACILITY_FAX = "facsimile";
//minagawa$ 
    protected static final String PATIENT_START = "<patient>";
    protected static final String PATIENT_END = "</patient>";
    protected static final String ELEMENT_PK = "pk";
    protected static final String ELEMENT_PATIENT_ID = "patientId";
    protected static final String ELEMENT_NAME = "name";
    protected static final String ELEMENT_KANA = "kana";
    protected static final String ELEMENT_SEX = "sex";
    protected static final String ELEMENT_BIRTHDAY = "birthday";
    protected static final String ELEMENT_FIRST_VISIT = "firstVisit";
    protected static final String ELEMENT_VISITED = "visited";

    protected static final String ELEMENT_DISEASE = "disease";

//    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//    protected static SimpleDateFormat fullDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    protected static SimpleDateFormat mmlDf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    protected static final String[] MALES = {"m", "M", "男"};
    protected static final String[] FEMALES = {"f", "F", "女"};
    protected static final String MALE = "M";
    protected static final String FEMALE = "F";
    protected static final String UNKNOWN = "U";

    protected static final String ENTITY_MED_ORDER = "medOrder";
    protected static final String ENTITY_BASE_CHARGE_ORDER = "baseChargeOrder";
    protected static final String ENTITY_INSTRACTION_CHARGE_ORDER = "instractionChargeOrder";
    protected static final String ENTITY_INJECTION_ORDER = "injectionOrder";
    protected static final String ENTITY_TREATMENT_ORDER = "treatmentOrder";
    protected static final String ENTITY_SURGERY_ORDER = "surgeryOrder";
    protected static final String ENTITY_BACTERIA_ORDER = "bacteriaOrder";
    protected static final String ENTITY_PHYSIOLOGY_ORDER = "physiologyOrder";
    protected static final String ENTITY_TEST_ORDER = "testOrder";
    protected static final String ENTITY_RADIOLOGY_ORDER = "radiologyOrder";
    protected static final String ENTITY_OTHER_ORDER = "otherOrder";
    protected static final String ENTITY_GENERAL_ORDER = "generalOrder";

    // Document, Module, Item, TextItem, SChema, ClaimItem
    protected static final String ELEMENT_PAGE_INFO_START = "<pageInfo>";
    protected static final String ELEMENT_PAGE_INFO_END = "</pageInfo>";
    protected static final String ELEMENT_NUM_RECORDS = "numRecords";
    protected static final String ELEMENT_DOCUMENT_START = "<document>";
    protected static final String ELEMENT_DOCUMENT_END = "</document>";
    protected static final String ELEMENT_MODULE_START = "<module>";
    protected static final String ELEMENT_MODULE_END = "</module>";
    protected static final String ELEMENT_ITEM_START = "<item>";
    protected static final String ELEMENT_ITEM_END = "</item>";
    protected static final String ELEMENT_TEXT_ITEM_START = "<textItem>";
    protected static final String ELEMENT_TEXT_ITEM_END = "</textItem>";
    protected static final String ELEMENT_CLAIM_ITEM_START = "<claimItem>";
    protected static final String ELEMENT_CLAIM_ITEM_END = "</claimItem>";

    // entity, name, startDate, code, quantity, unit
    protected static final String ELEMENT_ENTITY = "entity";
    protected static final String ELEMENT_START_DATE = "startDate";
    protected static final String ELEMENT_CODE = "code";
    protected static final String ELEMENT_VALUE = "value";
    protected static final String ELEMENT_QUANTITY = "quantity";
    protected static final String ELEMENT_UNIT = "unit";

    // ProgressCourse parse
    protected static final String COMPONENT_ELEMENT_NAME = "component";
    //protected static final String STAMP_HOLDER = "stampHolder";
    //protected static final String SCHEMA_HOLDER = "schemaHolder";
    protected static final int TT_SECTION = 0;
    protected static final int TT_PARAGRAPH = 1;
    protected static final int TT_CONTENT = 2;
    protected static final int TT_ICON = 3;
    protected static final int TT_COMPONENT = 4;
    protected static final int TT_PROGRESS_COURSE = 5;
    protected static final String SECTION_NAME = "section";
    protected static final String PARAGRAPH_NAME = "paragraph";
    protected static final String CONTENT_NAME = "content";
    protected static final String COMPONENT_NAME = "component";
    protected static final String ICON_NAME = "icon";
    protected static final String ALIGNMENT_NAME = "Alignment";
    protected static final String FOREGROUND_NAME = "foreground";
    protected static final String SIZE_NAME = "size";
    protected static final String BOLD_NAME = "bold";
    protected static final String ITALIC_NAME = "italic";
    protected static final String UNDERLINE_NAME = "underline";
    protected static final String TEXT_NAME = "text";
    protected static final String NAME_NAME = "name";
    protected static final String LOGICAL_STYLE_NAME = "logicalStyle";
    protected static final String PROGRESS_COURSE_NAME = "kartePane";
    protected static final String NAME_STAMP_HOLDER = "name=\"stampHolder\"";

    protected boolean DEBUG;

    //===========================================
    // Helper Methods
    //===========================================
    protected static String simpleFormat(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    protected static Date simpleParse(String dateStr) {
        try {
            return (Date) new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    protected static Date fullParse(String dateStr) {
        try {
            return (Date) new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    protected static Date mmlDfParse(String dateStr) {
        try {
            return (Date) new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateStr);
        } catch (ParseException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    protected static void propertyString(String prop, String strValue, StringBuilder sb) {

        if (prop != null && strValue != null) {

            strValue = strValue.replaceAll(STRING_LT, XML_LT);
            strValue = strValue.replaceAll(STRING_GT, XML_GT);
            strValue = strValue.replaceAll(STRING_AND, XML_AND);
            strValue = strValue.replaceAll(STRING_QUOT, XML_QUOT);
            strValue = strValue.replaceAll(STRING_APOS, XML_APOS);

            sb.append(PROPERTY_START);
            sb.append(prop);
            sb.append(PROPERTY_END);
            sb.append(STRING_START);
            sb.append(strValue);
            sb.append(STRING_END);
        }
    }

    protected static String entityToName(String order) {

        if (order == null) {
            return null;
        }

        String ret = null;

        if (order.equals(ENTITY_MED_ORDER)) {
            ret = "RP";

        } else if (order.equals(ENTITY_BASE_CHARGE_ORDER)) {
            ret = "診断料";

        } else if (order.equals(ENTITY_INSTRACTION_CHARGE_ORDER)) {
            ret = "指導・在宅";

        } else if (order.equals(ENTITY_INJECTION_ORDER)) {
            ret = "注 射";

        } else if (order.equals(ENTITY_TREATMENT_ORDER)) {
            ret = "処 置";

        } else if (order.equals(ENTITY_SURGERY_ORDER)) {
            ret = "手 術";

        } else if (order.equals(ENTITY_BACTERIA_ORDER)) {
            ret = "細菌検査";

        } else if (order.equals(ENTITY_PHYSIOLOGY_ORDER)) {
            ret = "生体検査";

        } else if (order.equals(ENTITY_TEST_ORDER)) {
            ret = "検体検査";

        } else if (order.equals(ENTITY_RADIOLOGY_ORDER)) {
            ret = "放射線";

        } else if (order.equals(ENTITY_OTHER_ORDER)) {
            ret = "その他";

        } else if (order.equals(ENTITY_GENERAL_ORDER)) {
            ret = "汎 用";
        }

        return ret;
    }


    protected void debug(String str) {
        if (DEBUG) {
            System.err.println(str);
            System.err.println("----------------------------");
        }
    }

    protected static Object xmlDecode(byte[] bytes)  {

        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                new ByteArrayInputStream(bytes)));

        return d.readObject();
    }

    protected static String sexValueToDesc(String code) {

        for (int i=0; i < MALES.length; i++) {
            if (code.startsWith(MALES[i])) {
                return MALE;
            }
        }

        for (int i=0; i < FEMALES.length; i++) {
            if (code.startsWith(FEMALES[i])) {
                return FEMALE;
            }
        }

        return UNKNOWN;
    }

        /**
     * OpenDolphinの textPane データをパースする。
     * @param sb  contentのテキストを集積するためのStringBuilder
     * @param xml textPane データ
     */
    protected void renderPane(StringBuilder sb, String xml) {

        debug(xml);

        SAXBuilder docBuilder = new SAXBuilder();

        try {
            StringReader sr = new StringReader(xml);
            Document doc = docBuilder.build(new BufferedReader(sr));
            org.jdom.Element root = (org.jdom.Element) doc.getRootElement();

            writeChildren(sb, root);

        } catch (JDOMException e) {
            e.printStackTrace(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * 要素を再帰的に解析する。
     * @param sb contentのテキストを集積するためのStringBuilder
     * @param current XML要素
     */
    protected void writeChildren(StringBuilder sb, org.jdom.Element current) {

        int eType = -1;
        String eName = current.getName();

        if (eName.equals(PARAGRAPH_NAME)) {
            eType = TT_PARAGRAPH;
            startParagraph(sb,
                    current.getAttributeValue(LOGICAL_STYLE_NAME),
                    current.getAttributeValue(ALIGNMENT_NAME));

        } else if (eName.equals(CONTENT_NAME) && (current.getChild(TEXT_NAME) != null)) {
            eType = TT_CONTENT;
            startContent(sb,
                    current.getAttributeValue(FOREGROUND_NAME),
                    current.getAttributeValue(SIZE_NAME),
                    current.getAttributeValue(BOLD_NAME),
                    current.getAttributeValue(ITALIC_NAME),
                    current.getAttributeValue(UNDERLINE_NAME),
                    current.getChildText(TEXT_NAME));

        } else if (eName.equals(COMPONENT_NAME)) {
            eType = TT_COMPONENT;
            startComponent(sb,
                    current.getAttributeValue(NAME_NAME), // compoenet=number
                    current.getAttributeValue(COMPONENT_ELEMENT_NAME));

        } else if (eName.equals(ICON_NAME)) {
            eType = TT_ICON;
            startIcon(sb, current);

        } else if (eName.equals(PROGRESS_COURSE_NAME)) {
            eType = TT_PROGRESS_COURSE;
            startProgressCourse(sb);

        } else if (eName.equals(SECTION_NAME)) {
            eType = TT_SECTION;
            startSection(sb);

        } else {
            debug("Other element:" + eName);
        }

        //
        // 再帰: 子を探索するのはパラグフとトップ要素のみ
        //
        if (eType == TT_PARAGRAPH || eType == TT_PROGRESS_COURSE || eType == TT_SECTION) {

            java.util.List children = (java.util.List) current.getChildren();
            Iterator iterator = children.iterator();

            while (iterator.hasNext()) {
                org.jdom.Element child = (org.jdom.Element) iterator.next();
                writeChildren(sb, child);
            }
        }

        switch (eType) {

            case TT_PARAGRAPH:
                endParagraph(sb);
                break;

            case TT_CONTENT:
                endContent(sb);
                break;

            case TT_ICON:
                endIcon(sb);
                break;

            case TT_COMPONENT:
                endComponent(sb);
                break;

            case TT_PROGRESS_COURSE:
                endProgressCourse(sb);
                break;

            case TT_SECTION:
                endSection(sb);
                break;
        }
    }

    protected void startSection(StringBuilder sb) {
        debug("startSection");
    }

    protected void endSection(StringBuilder sb) {
        debug("endSection");
    }

    protected void startProgressCourse(StringBuilder sb) {
        debug("startProgressCourse");
    }

    protected void endProgressCourse(StringBuilder sb) {
        debug("endProgressCourse");
    }

    protected void startParagraph(StringBuilder sb, String lStyle, String alignStr) {
        debug("startParagraph");
        debug("lStyle: " + lStyle);
        debug("alignStr: " + alignStr);
    }

    protected void endParagraph(StringBuilder sb) {
        debug("endParagraph");
        // パラグラフを生成する
        sb.append("\n");
    }

    protected void startContent(StringBuilder sb,
            String foreground,
            String size, String bold,
            String italic, String underline, String text) {

        debug("startContent");
        debug("foreground: " + foreground);
        debug("size: " + size);
        debug("bold: " + bold);
        debug("italic: " + italic);
        debug("underline: " + underline);
        debug("text: " + text);
        // ゴミ除去
        sb.append(text.trim());
    }

    protected void endContent(StringBuilder sb) {
    }

    protected void startComponent(StringBuilder sb, String name, String number) {
        debug("startComponent");
        debug("name: " + name);
        debug("number: " + number);
    }

    protected void endComponent(StringBuilder sb) {
        debug("endComponent");
    }

    protected void startIcon(StringBuilder sb, org.jdom.Element current) {

        String name = current.getChildTextTrim("name");

        if (name != null) {
            debug(name);
        }
    }

    protected void endIcon(StringBuilder sb) {
    }

    protected String toIsoBirtday(String birthday) {

        String[] spec = birthday.split("/");
        StringBuilder sb = new StringBuilder();
        sb.append(spec[0]);
        sb.append("-");
        if (spec[1].length() == 1) {
            sb.append("0");
        }
        sb.append(spec[1]);
        sb.append("-");
        if (spec[2].length() == 1) {
            sb.append("0");
        }
        sb.append(spec[2]);
        return sb.toString();
    }
}
