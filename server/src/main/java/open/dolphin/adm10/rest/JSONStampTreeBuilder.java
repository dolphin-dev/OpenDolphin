package open.dolphin.adm10.rest;


/**
 * StampTree Builder クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class JSONStampTreeBuilder extends AbstractStampTreeBuilder {
    
    /** XML文書で置換が必要な文字 */
    private static final String[] REPLACES = new String[] {"<", ">", "&", "'" ,"\""};
    
    /** 置換文字 */
    private static final String[] MATCHES = new String[] {"&lt;", "&gt;", "&amp;", "&apos;", "&quot;"};
    
    private static final String[] TREE_NAMES = new String[]{"diagnosis",
        "baseChargeOrder", "instractionChargeOrder", "medOrder", "injectionOrder", "treatmentOrder", 
        "surgeryOrder", "testOrder", "physiologyOrder", "bacteriaOrder", "radiologyOrder", "otherOrder", "generalOrder", "path", "text"};
    
    /** Logger */
    private boolean DEBUG;
    
    // 
    private StringBuilder sb;
    
    private boolean firstRoot;
    
    private boolean firstInfo;
    
    /** 
     * Creates new DefaultStampTreeBuilder 
     */
    public JSONStampTreeBuilder() {
    }
    
    /**
     * Returns the product of this builder
     * @return vector that contains StampTree instances
     */
    @Override
    public String getProduct() {
        return sb !=null ? sb.toString() : null;
    }
    
    /**
     * build を開始する。
     */
    @Override
    public void buildStart() {
        sb = new StringBuilder();
        sb.append("{").append("\n");
        sb.append(addQuoteColon("stampTreeList")).append("[").append("\n");
        firstRoot = true;
    }
    
    /**
     * Root を生成する。
     * @param name root名
     * @param Stamptree の Entity
     */
    @Override
    public void buildRoot(String name, String entity) {
        
        if (firstRoot) {
            firstRoot = false;
        } else {
            sb.append(",").append("\n");
        }
        
        sb.append("{").append("\n");
        
        sb.append(addQuoteColon("treeName")).append(addQuote(name)).append(",").append("\n");
        
        sb.append(addQuoteColon("entity")).append(addQuote(entity)).append(",").append("\n");
        
        for (int i=0; i < TREE_NAMES.length; i++) {
            if (TREE_NAMES[i].equals(entity)) {
                String num = String.valueOf(i);
                if (i<10) {
                    num = "0"+num;
                }
                sb.append(addQuoteColon("treeOrder")).append(addQuote(num)).append(",").append("\n");
                break;
            }
        }
        
        sb.append(addQuoteColon("stampList")).append("[").append("\n");
        
        firstInfo = true;
    }
    
    /**
     * ノードを生成する。
     * @param name ノード名
     */
    @Override
    public void buildNode(String name) {
//        // S.Oh 2014/02/06 iPadのFreeText対応 Add Start
//        if (firstInfo) {
//            firstInfo = false;
//        } else {
//            sb.append(",").append("\n");
//        }
//        sb.append("{").append("\n");
//        sb.append(addQuoteColon("name")).append(addQuote(toXmlText(name))).append(",").append("\n");
//        sb.append(addQuoteColon("role")).append(addQuote("fs")).append(",").append("\n");
//        sb.append(addQuoteColon("entity")).append(addQuote("")).append(",").append("\n");
//        sb.append(addQuoteColon("memo")).append(addQuote(toXmlText(""))).append(",").append("\n");
//        sb.append(addQuoteColon("stampId")).append(addQuote("")).append("\n");
//        sb.append("}").append("\n");
//        // S.Oh 2014/02/06 Add End
    }
    
    /**
     * StampInfo を UserObject にするノードを生成する。
     * @param name ノード名
     * @param entity エンティティ
     * @param editable 編集可能かどうかのフラグ
     * @param memo メモ
     * @param id DB key
     */
    @Override
    public void buildStampInfo(String name,
            String role,
            String entity,
            String editable,
            String memo,
            String id) {
        
        if (DEBUG) {
            StringBuilder bb = new StringBuilder();
            bb.append(name);
            bb.append(",");
            bb.append(role);
            bb.append(",");
            bb.append(entity);
            bb.append(",");
            bb.append(editable);
            bb.append(",");
            bb.append(memo);
            bb.append(",");
            bb.append(id);
            System.err.println(sb.toString());
        }
        
        //------------------------------------
        // エディタから発行、stampIdのないもの
        //------------------------------------
        if (id==null) {
            return;
        }
        
        if (firstInfo) {
            firstInfo = false;
        } else {
            sb.append(",").append("\n");
        }
        
        sb.append("{").append("\n");
        
        sb.append(addQuoteColon("name")).append(addQuote(toXmlText(name))).append(",").append("\n");
        
        sb.append(addQuoteColon("role")).append(addQuote(role)).append(",").append("\n");
        
        sb.append(addQuoteColon("entity")).append(addQuote(entity)).append(",").append("\n");
        
        if (memo!=null) {
            sb.append(addQuoteColon("memo")).append(addQuote(toXmlText(memo))).append(",").append("\n");
        }
        
        sb.append(addQuoteColon("stampId")).append(addQuote(id)).append("\n");
        
        sb.append("}").append("\n");
    }
    
    /**
     * Node の生成を終了する。
     */
    @Override
    public void buildNodeEnd() {
//        // S.Oh 2014/02/06 iPadのFreeText対応 Add Start
//        if (firstInfo) {
//            firstInfo = false;
//        } else {
//            sb.append(",").append("\n");
//        }
//        sb.append("{").append("\n");
//        sb.append(addQuoteColon("name")).append(addQuote("")).append(",").append("\n");
//        sb.append(addQuoteColon("role")).append(addQuote("fe")).append(",").append("\n");
//        sb.append(addQuoteColon("entity")).append(addQuote("")).append(",").append("\n");
//        sb.append(addQuoteColon("memo")).append(addQuote(toXmlText(""))).append(",").append("\n");
//        sb.append(addQuoteColon("stampId")).append(addQuote("")).append("\n");
//        sb.append("}").append("\n");
//        // S.Oh 2014/02/06 Add End
    }
    
    /**
     * Root Node の生成を終了する。 
     */
    @Override
    public void buildRootEnd() {
        sb.append("]").append("\n");
        sb.append("}").append("\n");
    }
    
    /**
     * build を終了する。
     */
    @Override
    public void buildEnd() {
        sb.append("]").append("\n");
        sb.append("}").append("\n");
    }
     
    /**
     * 特殊文字を変換する。
     */
    private String toXmlText(String text) {
        for (int i = 0; i < REPLACES.length; i++) {
            text = text.replaceAll(MATCHES[i], REPLACES[i]);
        }
        return text;
    }
    
    private String addQuote(String val) {
        StringBuilder buf = new StringBuilder();
        buf.append("\"").append(val).append("\"");
        return buf.toString();
    }
    
    private String addQuoteColon(String val) {
        StringBuilder buf = new StringBuilder();
        buf.append("\"").append(val).append("\"").append(":");
        return buf.toString();
    }
}