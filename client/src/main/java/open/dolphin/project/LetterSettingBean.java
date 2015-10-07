package open.dolphin.project;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import open.dolphin.client.ClientContext;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author s.oh
 */
public final class LetterSettingBean extends AbstractSettingBean {

    private String title;
    private boolean includeGreetings;
    private boolean printName;
    private String pdfDirectory;
    private String fontSize;
    
    // Output telephone number
    private boolean telephoneOutputPdf;
    
    private final Map<String, String[]> tagMap = new HashMap<>(5, 0.75f);
    
    public LetterSettingBean() {
        ResourceBundle bundle = ClientContext.getMyBundle(this.getClass());
        String ab1 = bundle.getString("gokika");
        String ab2 = bundle.getString("onjishi");
        String n3 = bundle.getString("none");
        tagMap.put("title", new String[]{ab1, ab2, n3});
        String small = bundle.getString("small");
        String large = bundle.getString("large");
        tagMap.put("fontSize", new String[]{small, large});
    }
    
    @Override
    public String[] propertyOrder() {
        return new String[]{
            "title", "includeGreetings", "telephoneOutputPdf", "printName", "fontSize", "pdfDirectory"
        };
    }
    
    @Override
    public boolean isTagProperty(String property) {
        return tagMap.get(property)!=null;
    }
    
    @Override
    public String[] getTags(String property) {
        String[] ret = tagMap.get(property);
        return ret;
    }
    
    @Override
    public boolean isDirectoryProperty(String property) {
        return property.equals("pdfDirectory");
    }
    
    @Override
    public boolean isValidBean() {
        String dir = getPdfDirectory();
        return (dir!=null && !"".equals(dir));
    }

    @Override
    public void populate() {
        ProjectStub stub = Project.getProjectStub();
        setTitle(stub.getString(Project.LETTER_ATESAKI_TITLE));
        
        setIncludeGreetings(stub.getBoolean(Project.LETTER_INCLUDE_GREETINGS));
        
        setPrintName(stub.getBoolean(Project.PLAIN_PRINT_PATIENT_NAME));
        
        String test = stub.getString(Project.LOCATION_PDF);
        if (!notEmpty(test)) {
            test = ClientContext.getPDFDirectory();
        }
        setPdfDirectory(test);
        
        // Font Size small|large
        test = stub.getString(Project.SHINDANSYO_FONT_SIZE);
        int index = findIndex(test, new String[]{"small", "large"});
        String value = getTags("fontSize")[index];
        setFontSize(value);
        
        setTelephoneOutputPdf(stub.getBoolean(Project.LETTER_TELEPHONE_OUTPUTPDF, true));
    }

    @Override
    public void store() {
        ProjectStub stub = Project.getProjectStub();
        
        // 宛先敬称
        stub.setString(Project.LETTER_ATESAKI_TITLE, getTitle());

        // PDF印刷時に挨拶文を含めるかどうか
        stub.setBoolean(Project.LETTER_INCLUDE_GREETINGS, isIncludeGreetings());

        // PLAIN文書印刷で患者氏名を印刷するかどうか
        stub.setBoolean(Project.PLAIN_PRINT_PATIENT_NAME, isPrintName());

        // PDFの出力ディレクトリー
        if (notEmpty(getPdfDirectory())) {
            stub.setString(Project.LOCATION_PDF, getPdfDirectory());
        }

        // 診断書のフォントサイズ
        int index = findIndex(getFontSize(), getTags("fontSize"));
        String value = index==0 ? "small" : "large";
        stub.setString(Project.SHINDANSYO_FONT_SIZE, value);

        stub.setBoolean(Project.LETTER_TELEPHONE_OUTPUTPDF, isTelephoneOutputPdf());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isIncludeGreetings() {
        return includeGreetings;
    }

    public void setIncludeGreetings(boolean includeGreetings) {
        this.includeGreetings = includeGreetings;
    }

    public boolean isPrintName() {
        return printName;
    }

    public void setPrintName(boolean printName) {
        this.printName = printName;
    }

    public String getPdfDirectory() {
        return pdfDirectory;
    }

    public void setPdfDirectory(String dir) {
        this.pdfDirectory = dir;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }
    
    public boolean isTelephoneOutputPdf() {
        return telephoneOutputPdf;
    }

    public void setTelephoneOutputPdf(boolean output) {
        this.telephoneOutputPdf = output;
    }
}
