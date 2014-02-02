package open.dolphin.client;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.JLabel;
import javax.swing.text.*;

/**
 *
 * @author  Kazushi Minagawa.
 */
public class TextPaneDumpBuilder {

    // Control flags to dump.

    static final int TT_SECTION = 0;
    static final int TT_PARAGRAPH = 1;
    static final int TT_CONTENT = 2;
    static final int TT_ICON = 3;
    static final int TT_COMPONENT = 4;

    /** Creates a new instance of TextPaneDumpBuilder */
    public TextPaneDumpBuilder() {
    }

    public String build(DefaultStyledDocument doc) {

        StringWriter sw = new StringWriter();
        BufferedWriter w = new BufferedWriter(sw);

        try {
            //w.write("<?xml version=¥"1.0¥" encoding=¥"UTF-8¥" ?>¥n");
            javax.swing.text.Element root = (javax.swing.text.Element) doc.getDefaultRootElement();
            writeElemnt(root, w);

            w.flush();
            w.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        return sw.toString();
    }

    String attsDump(AttributeSet atts) {
        if (atts != null) {
            StringBuilder retBuffer = new StringBuilder();
            Enumeration names = atts.getAttributeNames();
            while (names.hasMoreElements()) {
                Object nextName = names.nextElement();
                if (nextName != StyleConstants.ResolveAttribute) {
                    retBuffer.append(" ");
                    retBuffer.append(nextName);
                    retBuffer.append("=");
                    Object attObject = atts.getAttribute(nextName);
                    retBuffer.append(addQuote(attObject.toString()));
                }
            }
            return retBuffer.toString();
        }
        return null;
    }

    void writeElemnt0(javax.swing.text.Element element, Writer writer) throws IOException, BadLocationException {

        // 開始及び終了のオフセット値
        int start = element.getStartOffset();
        int end = element.getEndOffset();

        // このエレメントの属性セット
        AttributeSet atts = element.getAttributes().copyAttributes();

        // 属性値の文字列表現
        String asString = "";

        if (atts != null) {

            StringBuilder retBuffer = new StringBuilder();

            // 全ての属性を列挙する
            Enumeration names = atts.getAttributeNames();

            while (names.hasMoreElements()) {

                Object nextName = names.nextElement();

                if (nextName != StyleConstants.ResolveAttribute) {

                    // $enameは除外する
                    if (nextName.toString().startsWith("$")) {
                        continue;
                    }

                    retBuffer.append(" ");
                    retBuffer.append(nextName);
                    retBuffer.append("=");

                    // foreground 属性の場合は再構築の際に利用しやすい形に分解する
                    if (nextName.toString().equals("foreground")) {
                        Color c = (Color) atts.getAttribute(StyleConstants.Foreground);
                        StringBuilder buf = new StringBuilder();
                        buf.append(String.valueOf(c.getRed()));
                        buf.append(",");
                        buf.append(String.valueOf(c.getGreen()));
                        buf.append(",");
                        buf.append(String.valueOf(c.getBlue()));
                        retBuffer.append(addQuote(buf.toString()));

                    } else {
                        Object attObject = atts.getAttribute(nextName);

                        // スタンプ及びシェーマの判定をする
                        if (attObject instanceof JLabel) {
                            String str = ((JLabel) attObject).getText();
                            retBuffer.append(addQuote(str));

                        } else {
                            // それ以外の属性についてはそのまま記録する
                            retBuffer.append(addQuote(attObject.toString()));
                        }
                    }
                }
            }
            asString = retBuffer.toString();
        }

        writer.write("<");
        writer.write(element.getName());
        writer.write(" start=");
        writer.write(addQuote(start));
        writer.write(" end=");
        writer.write(addQuote(end));
        writer.write(asString);
        writer.write(">\n");

        // content要素の場合はテキストを抽出する
        if (element.getName().equals("content")) {
            writer.write("<text>");
            int len = end - start;
            String text = element.getDocument().getText(start, len).trim();

            text.replaceAll("<", "&lt;");
            text.replaceAll(">", "&gt;");
            text.replaceAll("&", "&amp;");

            writer.write(text);
            writer.write("</text>\n");
        }

        // 子要素について再帰する
        int children = element.getElementCount();
        for (int i = 0; i < children; i++) {
            writeElemnt0(element.getElement(i), writer);
        }

        writer.write("</");
        writer.write(element.getName());
        writer.write(">\n");
    }

    void writeElemnt(javax.swing.text.Element e, Writer w) throws IOException, BadLocationException {

        String elementName = e.getName();
        int start = e.getStartOffset();
        int end = e.getEndOffset();
        AttributeSet atts = e.getAttributes();
        int elementType = -1;

        if (elementName.equals(AbstractDocument.ParagraphElementName)) {
            startParagraph(w, start, end, atts);
            elementType = TT_PARAGRAPH;

        } else if (elementName.equals(AbstractDocument.ContentElementName)) {
            startContent(w, start, end, e, atts);
            elementType = TT_CONTENT;

        } else if (elementName.equals("icon")) {
            elementType = TT_ICON;
            startIcon(w, start, end, e, atts);

        } else if (elementName.equals("component")) {
            elementType = TT_COMPONENT;
            startComponent(w, start, end, e, atts);

        } else if (elementName.equals("section")) {
            elementType = TT_SECTION;
            startSection(w);
        }

        int children = e.getElementCount();
        for (int i = 0; i < children; i++) {
            writeElemnt(e.getElement(i), w);
        }

        // このメソッドの出口で endXXX をコールする
        switch (elementType) {

            case TT_PARAGRAPH:
                endParagraph(w);
                break;

            case TT_CONTENT:
                endContent(w);
                break;

            case TT_ICON:
                endIcon(w);
                break;

            case TT_COMPONENT:
                endComponent(w);
                break;

            case TT_SECTION:
                endSection(w);
                break;
        }
    }

    void startSection(Writer w) throws IOException {
        w.write("<section>\n");
    }

    void endSection(Writer w) throws IOException {
        w.write("</section>\n");
    }

    void startParagraph(Writer w, int start, int end, AttributeSet atts) throws IOException {

        // 論理スタイル
        String name = (String) atts.getAttribute(StyleConstants.NameAttribute);

        indent(w, 1);
        w.write("<paragraph");
        w.write(" start=");
        w.write(addQuote(start));
        w.write(" end=");
        w.write(addQuote(end));

        if (name != null) {
            w.write(" logicalStyle=");
            w.write(addQuote(name));
        }

        if (atts != null) {
            StringBuilder retBuffer = new StringBuilder();
            Enumeration names = atts.getAttributeNames();
            while (names.hasMoreElements()) {
                Object nextName = names.nextElement();
                if (nextName != StyleConstants.ResolveAttribute) {
                    retBuffer.append(" ");
                    retBuffer.append(nextName);
                    retBuffer.append("=");
                    Object attObject = atts.getAttribute(nextName);
                    retBuffer.append(addQuote(attObject.toString()));
                }
            }
            w.write(retBuffer.toString());
        }

        w.write(">\n");
    }

    void endParagraph(Writer w) throws IOException {
        indent(w, 1);
        w.write("</paragraph>\n");
    }

    void startContent(Writer w, int start, int end, javax.swing.text.Element e, AttributeSet atts)
            throws IOException, BadLocationException {

        indent(w, 2);
        w.write("<content");
        w.write(" start=");
        w.write(addQuote(start));
        w.write(" end=");
        w.write(addQuote(end));

        if (atts != null) {
            StringBuilder retBuffer = new StringBuilder();
            Enumeration names = atts.getAttributeNames();
            while (names.hasMoreElements()) {

                Object nextName = names.nextElement();
                if (nextName != StyleConstants.ResolveAttribute) {
                    retBuffer.append(" ");
                    retBuffer.append(nextName);
                    retBuffer.append("=");
                    if (nextName.toString().equals("foreground")) {
                        StringBuilder buf = new StringBuilder();
                        Color c = (Color) atts.getAttribute(StyleConstants.Foreground);
                        buf.append(String.valueOf(c.getRed()));
                        buf.append(",");
                        buf.append(String.valueOf(c.getGreen()));
                        buf.append(",");
                        buf.append(String.valueOf(c.getBlue()));
                        retBuffer.append(addQuote(buf.toString()));
                    } else {
                        Object attObject = atts.getAttribute(nextName);
                        retBuffer.append(addQuote(attObject.toString()));
                    }
                }
            }
            w.write(retBuffer.toString());
        }
        w.write(">\n");

        indent(w, 3);
        w.write("<text>");
        int len = end - start;
        String text = e.getDocument().getText(start, len).trim();

        text.replaceAll("<", "&lt;");
        text.replaceAll(">", "&gt;");
        text.replaceAll("&", "&amp;");

        w.write(text);
        //indent(w, 2);

        w.write("</text>\n");
    }

    void endContent(Writer w) throws IOException {
        indent(w, 2);
        w.write("</content>\n");
    }

    void startComponent(Writer w, int start, int end, javax.swing.text.Element e, AttributeSet atts) throws IOException {

        indent(w, 2);
        w.write("<component");
        w.write(" start=");
        w.write(addQuote(start));
        w.write(" end=");
        w.write(addQuote(end));

        if (atts != null) {
            StringBuilder retBuffer = new StringBuilder();
            Enumeration names = atts.getAttributeNames();
            while (names.hasMoreElements()) {
                Object nextName = names.nextElement();
                if (nextName != StyleConstants.ResolveAttribute) {
                    if (nextName.toString().startsWith("$")) {
                        continue;
                    }
                    retBuffer.append(" ");
                    retBuffer.append(nextName);
                    retBuffer.append("=");
                    if (nextName.toString().equals("component")) {
                        JLabel l = (JLabel) atts.getAttribute(nextName);
                        retBuffer.append(addQuote(l.getText()));
                    } else {
                        Object attObject = atts.getAttribute(nextName);
                        retBuffer.append(addQuote(attObject.toString()));
                    }
                }
            }
            w.write(retBuffer.toString());
        }
        w.write(">\n");
    }

    void endComponent(Writer w) throws IOException {
        indent(w, 2);
        w.write("</component>\n");
    }

    void startIcon(Writer w, int start, int end, javax.swing.text.Element e, AttributeSet a) throws IOException {

        indent(w, 2);
        w.write("<icon");
        w.write(" start=");
        w.write(addQuote(start));
        w.write(" end=");
        w.write(addQuote(end));
        w.write(">\n");

        Enumeration enums = a.getAttributeNames();

        while (enums.hasMoreElements()) {

            Object o = enums.nextElement();
            String ename = o.toString();
            String value = null;

            // $ename 
            if (ename.startsWith("$")) {
                continue;
            } else if (ename.equals("icon")) {
                value = a.getAttribute(o).getClass().getName();
            } else {
                value = a.getAttribute(o).toString();
            }

            indent(w, 3);
            w.write("<");
            w.write(ename);
            w.write(">");
            w.write(value);
            //indent(w, 2);
            w.write("</");
            w.write(ename);
            w.write(">\n");
        }
    }

    void endIcon(Writer w) throws IOException {
        indent(w, 2);
        w.write("</icon>\n");
    }

    void indent(Writer w, int depth) throws IOException {
        for (int i = 0; i < depth; i++) {
            w.write("    ");
        }
    }

    String addQuote(String str) {
        StringBuilder buf = new StringBuilder();
        buf.append("\"");
        buf.append(str);
        buf.append("\"");
        return buf.toString();
    }

    String addQuote(int str) {
        StringBuilder buf = new StringBuilder();
        buf.append("\"");
        buf.append(str);
        buf.append("\"");
        return buf.toString();
    }
}