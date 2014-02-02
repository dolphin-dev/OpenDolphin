
package open.stamp.seed;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.StampModel;
import open.dolphin.msg.GUIDGenerator;

/**
 * アカウント作成時にシード元のStampTreeをコピーする builder
 *
 * based on DefaultStampTreeBuilder.java
 * @author Kazushi Minagawa.
 */
public class CopyStampTreeBuilder {
    
    private BufferedWriter writer;
    private StringWriter stringWriter;
    
    // seed元のStampModel id
    private List<String> seedStampList;
    
    // copyして保存すべきStampModel
    private List<StampModel> listToPersist;

    private boolean DEBUG = false;
    
    // Creates new CopyStampTreeBuilder
    public CopyStampTreeBuilder() {
    }

    public String getStampTreeXML() {
        String ret = stringWriter.toString();
        debug(ret);
        return ret;
    }
    
    public List<String> getSeedStampList() {
        return seedStampList;
    }
    
    public List<StampModel> getStampModelToPersist() {
        return listToPersist;
    }

    //build を開始する。
    public void buildStart() throws IOException {
        seedStampList = new ArrayList<String>();
        listToPersist = new ArrayList<StampModel>();
        stringWriter = new StringWriter();
        writer = new BufferedWriter(stringWriter);
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        writer.write("<stampBox project=\"open.dolphin\" version=\"1.0\">\n");
    }

    /**
     * Root を生成する。
     * @param name root名
     * @param Stamptree の Entity
     */
    public void buildRoot(String name, String entity) throws IOException {
        writer.write("<root name=");
        writer.write(addQuote(name));
        writer.write(" entity=");
        writer.write(addQuote(entity));
        writer.write(">");
        writer.write("\n");
    }

    /**
     * ノードを生成する。
     * @param name ノード名
     */
    public void buildNode(String name) throws IOException {
        writer.write("<node name=");
        writer.write(addQuote(name));
        writer.write(">");
        writer.write("\n");
    }

    /**
     * StampInfo を UserObject にするノードを生成する。
     * @param name ノード名
     * @param entity エンティティ
     * @param editable 編集可能かどうかのフラグ
     * @param memo メモ
     * @param seedID DB key
     */
    public void buildStampInfo(String name,
            String role,
            String entity,
            String editable,
            String memo,
            String seedID) throws IOException {

        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(",");
            sb.append(role);
            sb.append(",");
            sb.append(entity);
            sb.append(",");
            sb.append(editable);
            sb.append(",");
            sb.append(memo);
            sb.append(",");
            sb.append(seedID);
            sb.append(",");
            debug(sb.toString());
        }
        
        writer.write("<stampInfo name=");
        writer.write(addQuote(name));
        writer.write(" role=");
        writer.write(addQuote(role));
        writer.write(" entity=");
        writer.write(addQuote(entity));
        if (editable!=null) {
            writer.write(" editable=");
            writer.write(addQuote(editable));
        }
        if (memo!=null) {
            writer.write(" memo=");
            writer.write(addQuote(memo));
        }
        
        if (seedID!=null) {
            
            // seedになる(保存されている)StampModel id
            seedStampList.add(seedID);
            
            // ここで作成したStampModelがpersitされる
            // stampBytesはseedIDで検索したものがsetされる
            // userIdはpersit側で設定する
            StampModel model = new StampModel();
            String stampId = GUIDGenerator.generate(model);
            model.setId(stampId);
            model.setEntity(entity);
            //byte[] stampBytes = getStamp(seedID);
            //model.setStampBytes(stampBytes);
            listToPersist.add(model);
            
            writer.write(" stampId=");
            writer.write(addQuote(stampId));
        }
        writer.write("/>");
        writer.write("\n");
    }

    // Node の生成を終了する。
    public void buildNodeEnd() throws IOException {
        writer.write("</node>");
        writer.write("\n");
    }

    // Root Node の生成を終了する。
    public void buildRootEnd() throws IOException {
        writer.write("</root>");
        writer.write("\n");
    }

    // build を終了する。
    public void buildEnd() throws IOException {
        writer.write("</stampBox>");
        writer.write("\n");
        writer.flush();
    }
    
    private String addQuote(String s) {
        StringBuilder buf = new StringBuilder();
        buf.append("\"");
        buf.append(s);
        buf.append("\"");
        return buf.toString();
    }
    
    private void debug(String str) {
        if (DEBUG) {
            System.err.print(str);
        }
    }
}
