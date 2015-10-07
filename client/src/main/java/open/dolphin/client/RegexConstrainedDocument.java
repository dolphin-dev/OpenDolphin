package open.dolphin.client;
import java.awt.Toolkit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * RegexConstrainedDocument
 */
public final class RegexConstrainedDocument extends PlainDocument {

    private static final long serialVersionUID = 4066321190740323979L;
    
    boolean beep;
    boolean debug;
	
    Pattern pattern;
    Matcher matcher;
    
//s.oh^ 2013/09/12 PDF印刷文字サイズ
    int textLength;
//s.oh$
    
    public RegexConstrainedDocument () { 
    	super(); 
    }
    
    public RegexConstrainedDocument (AbstractDocument.Content c) {
    	super(c); 
    }
    
    public RegexConstrainedDocument (AbstractDocument.Content c, String p) {
        super (c);
        setPatternByString (p);
    }
    
    public RegexConstrainedDocument (String p) {
        super();
        setPatternByString (p);
    }
    
//s.oh^ 2013/09/12 PDF印刷文字サイズ
    public RegexConstrainedDocument (String p, int length) {
        super();
        setPatternByString (p);
        textLength = length;
    }
//s.oh$

    public void setPatternByString (String p) {
        Pattern lpattern = Pattern.compile (p);
        // check the document against the new pattern
        // and removes the content if it no longer matches
        try {
            matcher = lpattern.matcher (getText(0, getLength()));
            debug("matcher reset to " + getText (0, getLength()));
            if (! matcher.matches()) {
                debug ("does not match");
                remove (0, getLength());
            }
        } catch (BadLocationException ble) {
            ble.printStackTrace(System.err); // impossible?
        }
    }

    public Pattern getPattern() { 
    	return pattern; 
    }

    @Override
    public void insertString (int offs, String s, AttributeSet a) throws BadLocationException {
        
        String proposedInsert = getText (0, getLength()) + s ;
        debug("proposing to change to: " + proposedInsert);
        if (matcher != null) {
            matcher.reset (proposedInsert);
            debug("matcher reset");
            if (! matcher.matches()) {
            	beep();
                debug("insert doesn't match");
                return;
            }
        }
//s.oh^ 2013/09/12 PDF印刷文字サイズ
        //super.insertString (offs, s, a);
        if(textLength <= 0) {
            super.insertString (offs, s, a);
        }else{
            if(this.getLength() + s.length() <= textLength) {
                super.insertString (offs, s, a);
            }
        }
//s.oh$
    }
    
    private void beep() {
    	if (beep) {
    		Toolkit.getDefaultToolkit().beep();
    	}
    }
    
    private void debug(String msg) {
    	if (debug) {
    		System.out.println(msg);
    	}
    }

}
