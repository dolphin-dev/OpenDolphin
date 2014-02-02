/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import open.dolphin.client.ClientContext;
import open.dolphin.impl.login.AbstractLoginDialog;
import open.dolphin.utilities.common.CommonDefImpl;
import open.dolphin.utilities.utility.HttpConnect;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Life Sciences Computing Corporation.
 */
public class RssReaderPane {
    
    private static final int CONTROL_WIDTH = 600;
    private static final int CONTROL_HEIGHT = 100;
    
    public RssReaderPane() {}
    
    public JScrollPane createRssPane(String rssURL) {
        HttpConnect http = new HttpConnect();
        String xml = null;
        try {
            xml = http.httpGET(rssURL, CommonDefImpl.REQUESTMETHOD_GET);
        }
        catch (MalformedURLException ex) {
            Logger.getLogger(AbstractLoginDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AbstractLoginDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // 全体のパネル
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        contentPane.setOpaque(true);
        
        if(xml != null && xml.length() > 0) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
                Document document = builder.parse(is);
                Element root = document.getDocumentElement();
                NodeList channel = root.getElementsByTagName("channel");
                NodeList title = ((Element)channel.item(0)).getElementsByTagName("title");
                NodeList link = ((Element)channel.item(0)).getElementsByTagName("link");
                NodeList language = ((Element)channel.item(0)).getElementsByTagName("language");
                NodeList copyright = ((Element)channel.item(0)).getElementsByTagName("copyright");
                contentPane.add("North", createMainPane(title.item(0).getFirstChild().getNodeValue(),
                                                        link.item(0).getFirstChild().getNodeValue(),
                                                        language.item(0).getFirstChild().getNodeValue(),
                                                        copyright.item(0).getFirstChild().getNodeValue()));
                
                NodeList itemList = root.getElementsByTagName("item");
                // RSS内容のパネル
                JPanel infoPane = new JPanel(new GridLayout(itemList.getLength(), 1));
                for(int i = 0; i < itemList.getLength(); i++) {
                    Element element = (Element)itemList.item(i);
                    NodeList itemTitle = element.getElementsByTagName("title");
                    NodeList itemLink = element.getElementsByTagName("link");
                    NodeList itemPubDate = element.getElementsByTagName("pubDate");
                    NodeList itemCategory = element.getElementsByTagName("category");
                    NodeList itemDescription = element.getElementsByTagName("description");
                    Color color = (i % 2 != 0) ? Color.CYAN : Color.WHITE;
                    infoPane.add(createItemPane4Html(itemTitle.item(0).getFirstChild().getNodeValue(),
                                                itemLink.item(0).getFirstChild().getNodeValue(),
                                                itemPubDate.item(0).getFirstChild().getNodeValue(),
                                                itemCategory.item(0).getFirstChild().getNodeValue(),
                                                itemDescription.item(0).getFirstChild().getNodeValue(),
                                                color));
                }
                contentPane.add("South", infoPane);
            } catch (SAXException ex) {
                Logger.getLogger(AbstractLoginDialog.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AbstractLoginDialog.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(AbstractLoginDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // 全体のスクロールパネル
        JScrollPane scroll = new JScrollPane(contentPane);
        
        return scroll;
    }
    
    private JPanel createMainPane(String title, final String link, String language, String copyright) {
        JPanel titleArea = new JPanel();
        JLabel titleLabel = new JLabel(title);
        Font font = new Font("Serif", Font.BOLD, 20);
        titleLabel.setFont(font);
        //titleLabel.setForeground(Color.CYAN);
        titleArea.add(titleLabel);
        
        JPanel copyrightArea = new JPanel();
        JLabel copyrightLabel = new JLabel(copyright);
        font = new Font("Serif", Font.BOLD, 10);
        copyrightLabel.setFont(font);
        copyrightArea.add(copyrightLabel);
        
        JPanel pane = new JPanel(new BorderLayout());
        titleArea.setBackground(Color.LIGHT_GRAY);
        copyrightArea.setBackground(Color.LIGHT_GRAY);
        pane.add("North", titleArea);
        pane.add("South", copyrightArea);
        RssMouseListener rml = new RssMouseListener();
        rml.setURL(link);
        pane.addMouseListener(rml);
        
        return pane;
    }
    
    private JPanel createItemPane(String title, final String link, String pubDate, String category, String description, Color color) {
        JPanel titleArea = new JPanel();
        JLabel titleLabel = new JLabel(title);
        Font font = new Font("Serif", Font.BOLD, 16);
        titleLabel.setFont(font);
        titleLabel.setForeground(Color.BLUE);
        titleArea.add(titleLabel);
        
        JPanel pubDateArea = new JPanel();
        JLabel pubDateLabel = new JLabel(pubDate);
        font = new Font("Serif", Font.BOLD, 10);
        pubDateLabel.setFont(font);
        pubDateArea.add(pubDateLabel);
        
        JPanel categoryArea = new JPanel();
        JLabel categoryLabel = new JLabel(category);
        categoryArea.add(categoryLabel);
        
        JPanel descriptionArea = new JPanel();
        JLabel descriptionLabel = new JLabel(description);
        descriptionArea.add(descriptionLabel);
        
        JPanel pane = new JPanel(new GridLayout(3, 1));
        if(color != null) {
            titleArea.setBackground(color);
            pubDateArea.setBackground(color);
            categoryArea.setBackground(color);
            descriptionArea.setBackground(color);
        }
        pane.add(titleArea);
        pane.add(pubDateArea);
        pane.add(descriptionArea);
        RssMouseListener rml = new RssMouseListener();
        rml.setURL(link);
        pane.addMouseListener(rml);
        
        return pane;
    }
    
    private JPanel createItemPane4Html(String title, final String link, String pubDate, String category, String description, Color color) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<font size=\"5\" color=\"#0000ff\"").append("<u>").append(title).append("</u>").append("</font>").append("<br>");
        sb.append(pubDate).append("<br>");
        sb.append(description).append("<br>");
        sb.append("</html>");
        
        JLabel label = new JLabel(sb.toString());
        label.setPreferredSize(new Dimension(CONTROL_WIDTH - 20, CONTROL_HEIGHT - 20));
        
        JPanel pane = new JPanel();
        pane.setPreferredSize(new Dimension(CONTROL_WIDTH, CONTROL_HEIGHT));
        pane.setBackground(color);
        pane.add(label);
        RssMouseListener rml = new RssMouseListener();
        rml.setURL(link);
        pane.addMouseListener(rml);
        
        return pane;
    }
    
    class RssMouseListener implements MouseListener {
        
        String url;
        
        public void setURL(String url) { this.url = url; }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(url));
                } catch (URISyntaxException ex) {
                    Logger.getLogger(AbstractLoginDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AbstractLoginDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}
