package open.dolphin.client;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;

/**
 * 2号カルテクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class KarteViewer2 extends KarteViewer {
    
    // P Pane
    private KartePane pPane;
    
    /**
     * Creates new KarteViewer
     */
    public KarteViewer2() {
    }
    
    @Override
    public int getActualHeight() {
        try {
            JTextPane pane = soaPane.getTextPane();
            int pos = pane.getDocument().getLength();
            Rectangle r = pane.modelToView(pos);
            int hsoa = r.y;
            
            pane = pPane.getTextPane();
            pos = pane.getDocument().getLength();
            r = pane.modelToView(pos);
            int hp = r.y;
            
            return Math.max(hsoa, hp);
            
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public void adjustSize() {
        int h = getActualHeight();
        int soaWidth = soaPane.getTextPane().getPreferredSize().width;
        int pWidth = pPane.getTextPane().getPreferredSize().width;
        soaPane.getTextPane().setPreferredSize(new Dimension(soaWidth, h));
        pPane.getTextPane().setPreferredSize(new Dimension(pWidth, h));
    }
    
    /**
     * P Pane を返す。
     * @return pPane
     */
    public KartePane getPPane() {
        return pPane;
    }
    
    /**
     * ２号カルテで初期化する。
     */
    private void initialize() {
        
        KartePanel2 kp2 = new KartePanel2();
        panel2 = kp2;
        
        // TimeStampLabel を生成する
        timeStampLabel = kp2.getTimeStampLabel();
        timeStampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeStampLabel.setForeground(timeStampFore);
        timeStampLabel.setFont(timeStampFont);
        
        // SOA Pane を生成する
        soaPane = new KartePane();
        soaPane.setTextPane(kp2.getSoaTextPane());
        soaPane.setRole(IInfoModel.ROLE_SOA);
        if (model != null) {
            // Schema 画像にファイル名を付けるのために必要
            String docId = model.getDocInfo().getDocId();
            soaPane.setDocId(docId);
        }
        
        // P Pane を生成する
        pPane = new KartePane();
        pPane.setTextPane(kp2.getPTextPane());
        pPane.setRole(IInfoModel.ROLE_P);
        
        setUI(kp2);
    }
    
    /**
     * プログラムを開始する。
     */
    @Override
    public void start() {
        //
        // Creates GUI
        //
        this.initialize();
        
        // Model を表示する
        if (this.getModel() != null) {
            //
            // 確定日を分かりやすい表現に変える
            //
            String timeStamp = ModelUtils.getDateAsFormatString(
                    model.getDocInfo().getFirstConfirmDate(), 
                    IInfoModel.KARTE_DATE_FORMAT);
            
            if (model.getDocInfo().getStatus().equals(IInfoModel.STATUS_TMP)) {
                StringBuilder sb = new StringBuilder();
                sb.append(timeStamp);
                sb.append(UNDER_TMP_SAVE);
                timeStamp = sb.toString();
            }
            timeStampLabel.setText(timeStamp);
            KarteRenderer_2 renderer = new KarteRenderer_2(soaPane, pPane);
            renderer.render(model);
        }
        
        // モデル表示後にリスナ等を設定する
        ChartMediator mediator = getContext().getChartMediator();
        soaPane.init(false, mediator);
        pPane.init(false, mediator);
        enter();
    }
    
    @Override
    public void stop() {
        soaPane.clear();
        pPane.clear();
    }
    
    @Override
    public void addMouseListener(MouseListener ml) {
        soaPane.getTextPane().addMouseListener(ml);
        pPane.getTextPane().addMouseListener(ml);
    }
}