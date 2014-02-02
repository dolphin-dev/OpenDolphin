package open.dolphin.client;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.text.Position;
import open.dolphin.infomodel.AttachmentModel;

/**
 * AttachmentModelをkartePaneに表示するホルダークラス。
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class AttachmentHolder extends AbstractComponentHolder implements ComponentHolder {
    
    private AttachmentModel attachment;
    private KartePane kartePane;
    private Position start;
    private Position end;
    private boolean selected;
    
    public AttachmentHolder(KartePane kartePane, AttachmentModel attachment) {
        this.kartePane = kartePane;
        this.attachment = attachment;
        this.setText(constractText(attachment));
        this.setIcon(attachment.getIcon());
    }
    
    // Lable のテキストを編集する: title (mime type)
    private String constractText(AttachmentModel attachment) {
        StringBuilder sb = new StringBuilder();
        sb.append(attachment.getTitle());
        sb.append(" (").append(attachment.getContentType()).append(")");
        return sb.toString();
    }
    
    public AttachmentModel getAttachment() {
        return attachment;
    }

    @Override
    public void edit() {
        FileOutputStream outputFile;
        try {
            // Contentを表示する
            byte[] data = attachment.getBytes();
            ByteBuffer buf = ByteBuffer.wrap(data);
            
            //File out = new File(ClientContext.getTempDirectory(), attachment.getUri());
            
            String name = attachment.getFileName();
            int index = name.indexOf(attachment.getExtension());
            name = name.substring(0, index);
            File tmpDir = new File(ClientContext.getTempDirectory());
//s.oh^ jdk7
            //File out = File.createTempFile(name, "."+attachment.getExtension(), tmpDir);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            File out = File.createTempFile(uuid, "."+attachment.getExtension(), tmpDir);
//s.oh$
            out.deleteOnExit();
            
            outputFile = new FileOutputStream(out);
            FileChannel outChannel = outputFile.getChannel();
            outChannel.write(buf);
            outChannel.close();
            
            // 表示
            URI uri = out.toURI();
            Desktop.getDesktop().browse(uri);
            
        } catch (IOException ex) {
            Logger.getLogger(AttachmentHolder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void mabeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popup = new JPopupMenu();
            popup.setFocusable(false);
            ChartMediator mediator = kartePane.getMediator();
            popup.add(mediator.getAction(GUIConst.ACTION_CUT));
            popup.add(mediator.getAction(GUIConst.ACTION_COPY));
            popup.add(mediator.getAction(GUIConst.ACTION_PASTE));
            popup.addSeparator();
            
            // タイトル編集
            AbstractAction titleAction = new AbstractAction("タイトル編集") {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    changeTitle();
                }
            };
            popup.add(titleAction);
            
            popup.addSeparator();
            
            // 表示
            AbstractAction action = new AbstractAction("開く") {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    edit();
                }
            };
            popup.add(action);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    private void changeTitle() {
        String title = JOptionPane.showInputDialog(this, "タイトル", attachment.getTitle());
        if (!title.trim().equals("")) {
            attachment.setTitle(title);
            this.setText(constractText(attachment));
            this.revalidate();
            this.repaint();
        }
    }

    @Override
    public int getContentType() {
        return TT_ATTACHENT;
    }

    @Override
    public KartePane getKartePane() {
        return kartePane;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean b) {
        boolean old = this.selected;
        this.selected = b;
        if (old != this.selected) {
            if (this.selected) {
                this.setBorder(BorderFactory.createLineBorder(SELECTED_BORDER));
            } else {
                this.setBorder(BorderFactory.createLineBorder(kartePane.getTextPane().getBackground()));
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEntry(Position start, Position end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public int getStartPos() {
        return start.getOffset();
    }
    
    @Override
    public int getEndPos() {
        return end.getOffset();
    }
}
