package open.dolphin.impl.scheam;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import open.dolphin.impl.scheam.schemahelper.SchemaTitleBorder;

/**
 *
 * @author  pns
 */
public class SchemaToolView extends javax.swing.JFrame {

    private SchemaCanvasView canvasView;
    private SchemaToolView toolView;

    public SchemaToolView() {
        initComponents();
        initTitlePanel();

        alphaSlider.putClientProperty("JComponent.sizeVariant", "small");
        widthSlider.putClientProperty("JComponent.sizeVariant", "small");
    }

    /**
     * titlePanel をつかんで移動できるようにする
     */
    private void initTitlePanel() {
        toolView = this;
        TitlePanelListener l = new TitlePanelListener();
        titlePanel.addMouseListener(l);
        titlePanel.addMouseMotionListener(l);
        titlePanel.setBorder(new SchemaTitleBorder());
    }

    /**
     * canvasView に近づいたらくっつける動作をする
     */
    private class TitlePanelListener extends MouseAdapter {
        private Point from;
        private int THRESHOLD = 16;

        @Override
        public void mousePressed(MouseEvent e) {
            from = e.getLocationOnScreen();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point to = e.getLocationOnScreen();
            int dx = to.x - from.x;
            int dy = to.y - from.y;

            Rectangle toolRect = toolView.getBounds();
            Rectangle canvasRect = canvasView.getBounds();
            int dxTcL = toolRect.x + toolRect.width - canvasRect.x;
            int dxTcR = canvasRect.x + canvasRect.width - toolRect.x;

            // 近づいたらくっつける動作
            if ((-THRESHOLD < dxTcL && dxTcL < THRESHOLD) || (-THRESHOLD < dxTcR && dxTcR < THRESHOLD)) {

                // 近接していた場合 mouse 距離が一定以上離れるまで x 方向には動かさない
                if (-THRESHOLD-3 < dx && dx < THRESHOLD+3) {
                    // まだマウスが十分動いていない場合
                    if (toolRect.x < canvasRect.x) {
                        // toolView が左にある場合
                        toolRect.x = canvasRect.x - toolRect.width - 2;
                    } else {
                        // toolView が右にある場合
                        toolRect.x = canvasRect.x + canvasRect.width + 2;
                    }
                    toolRect.y += dy;
                    from.y = to.y;
                } else {
                    // マウスが十分動いた場合は普通に描画
                    toolRect.x += dx; toolRect.y += dy;
                    from.x = to.x; from.y = to.y;
                }
            } else {
                // 離れている場合は普通に描画
                toolRect.x += dx; toolRect.y += dy;
                from.x = to.x; from.y = to.y;
            }
            toolView.setBounds(toolRect.x, toolRect.y, toolRect.width, toolRect.height);
        }
    }
    public void setSchemaCanvasView(SchemaCanvasView view) {
        canvasView = view;
    }
    public javax.swing.JButton getClearBtn() {
        return clearBtn;
    }
    public javax.swing.JButton getColorBtn() {
        return colorBtn;
    }
    public javax.swing.JToggleButton getLineBtn() {
        return lineBtn;
    }
    public javax.swing.JToggleButton getPencilBtn() {
        return pencilBtn;
    }
    public javax.swing.JToggleButton getEraserBtn() {
        return eraserBtn;
    }
    public javax.swing.JToggleButton getLineWidthBtn(int i) {
        switch (i) {
            case 0:
                return lineWidth1;
            case 1:
                return lineWidth2;
            case 2:
                return lineWidth3;
            default:
                return lineWidth4;
        }
    }
    public javax.swing.JButton getCPaletteBtn(int i) {
        switch (i) {
            case 0:
                return cPalette1;
            case 1:
                return cPalette2;
            case 2:
                return cPalette3;
            case 3:
                return cPalette4;
            case 4:
                return cPalette5;
            case 5:
                return cPalette6;
            case 6:
                return cPalette7;
            case 7:
                return cPalette8;
            case 8:
                return cPalette9;
            case 9:
                return cPalette10;
            case 10:
                return cPalette11;
            default:
                return cPalette12;
        }
    }
    public javax.swing.JToggleButton getOvalBtn() {
        return ovalBtn;
    }
    public javax.swing.JToggleButton getOvalFillBtn() {
        return ovalFillBtn;
    }
    public javax.swing.JToggleButton getPolyBtn() {
        return polyBtn;
    }
    public javax.swing.JToggleButton getPolyFillBtn() {
        return polyFillBtn;
    }
    public javax.swing.JToggleButton getRectBtn() {
        return rectBtn;
    }
    public javax.swing.JToggleButton getRectFillBtn() {
        return rectFillBtn;
    }
    public javax.swing.JToggleButton getSelectBtn() {
        return selectBtn;
    }
    public javax.swing.JToggleButton getTextBtn() {
        return textBtn;
    }
    public javax.swing.JButton getUndoBtn() {
        return undoBtn;
    }

    public javax.swing.JButton getRotateLeftBtn() {
        return rotateLeftBtn;
    }
    public javax.swing.JButton getRotateRightBtn() {
        return rotateRightBtn;
    }
    public javax.swing.JButton getExpandBtn() {
        return expandBtn;
    }
    public javax.swing.JToggleButton getNetSparseBtn() {
        return netSparseBtn;
    }
    public javax.swing.JToggleButton getNetMediumBtn() {
        return netMediumBtn;
    }
    public javax.swing.JToggleButton getNetDenseBtn() {
        return netDenseBtn;
    }
    public javax.swing.JToggleButton getDotsSparseBtn() {
        return dotsSparseBtn;
    }
    public javax.swing.JToggleButton getDotsMediumBtn() {
        return dotsMediumBtn;
    }
    public javax.swing.JToggleButton getDotsDenseBtn() {
        return dotsDenseBtn;
    }
    public javax.swing.JSlider getAlphaSlider() {
        return alphaSlider;
    }
    public javax.swing.JTextField getAlphaField() {
        return alphaField;
    }
    public javax.swing.JButton getRedoBtn() {
        return redoBtn;
    }
    public javax.swing.JToggleButton getClippingBtn() {
        return clippingBtn;
    }
    public javax.swing.JSlider getWidthSlider() {
        return widthSlider;
    }
    public javax.swing.JTextField getWidthField() {
        return widthField;
    }
    public javax.swing.JLabel getAlphaLabel() {
        return alphaLbl;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        blankBtn1 = new javax.swing.JButton();
        blankTgl = new javax.swing.JToggleButton();
        expandtglBtn = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        toolPanel = new javax.swing.JPanel();
        pencilBtn = new javax.swing.JToggleButton();
        eraserBtn = new javax.swing.JToggleButton();
        lineBtn = new javax.swing.JToggleButton();
        textBtn = new javax.swing.JToggleButton();
        ovalBtn = new javax.swing.JToggleButton();
        ovalFillBtn = new javax.swing.JToggleButton();
        dotsSparseBtn = new javax.swing.JToggleButton();
        netSparseBtn = new javax.swing.JToggleButton();
        rectBtn = new javax.swing.JToggleButton();
        rectFillBtn = new javax.swing.JToggleButton();
        dotsMediumBtn = new javax.swing.JToggleButton();
        netMediumBtn = new javax.swing.JToggleButton();
        polyBtn = new javax.swing.JToggleButton();
        polyFillBtn = new javax.swing.JToggleButton();
        dotsDenseBtn = new javax.swing.JToggleButton();
        netDenseBtn = new javax.swing.JToggleButton();
        selectBtn = new javax.swing.JToggleButton();
        clippingBtn = new javax.swing.JToggleButton();
        expandBtn = new javax.swing.JButton();
        clearBtn = new javax.swing.JButton();
        rotateLeftBtn = new javax.swing.JButton();
        rotateRightBtn = new javax.swing.JButton();
        undoBtn = new javax.swing.JButton();
        redoBtn = new javax.swing.JButton();
        alphaPanel = new javax.swing.JPanel();
        alphaSlider = new javax.swing.JSlider();
        alphaLbl = new javax.swing.JLabel();
        alphaField = new javax.swing.JTextField();
        widthPanel = new javax.swing.JPanel();
        widthSlider = new javax.swing.JSlider();
        widthLabel = new javax.swing.JLabel();
        widthField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        colorPalettePanel = new javax.swing.JPanel();
        colorBtn = new javax.swing.JButton();
        cPalette1 = new javax.swing.JButton();
        cPalette2 = new javax.swing.JButton();
        cPalette3 = new javax.swing.JButton();
        cPalette4 = new javax.swing.JButton();
        cPalette5 = new javax.swing.JButton();
        cPalette6 = new javax.swing.JButton();
        cPalette7 = new javax.swing.JButton();
        cPalette8 = new javax.swing.JButton();
        cPalette9 = new javax.swing.JButton();
        cPalette10 = new javax.swing.JButton();
        cPalette11 = new javax.swing.JButton();
        cPalette12 = new javax.swing.JButton();
        lineWidthPanel = new javax.swing.JPanel();
        lineWidth1 = new javax.swing.JToggleButton();
        lineWidth2 = new javax.swing.JToggleButton();
        lineWidth3 = new javax.swing.JToggleButton();
        lineWidth4 = new javax.swing.JToggleButton();

        blankBtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/blank.png"))); // NOI18N
        blankBtn1.setBorderPainted(false);
        blankBtn1.setContentAreaFilled(false);
        blankBtn1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        blankBtn1.setMaximumSize(new java.awt.Dimension(32, 32));
        blankBtn1.setMinimumSize(new java.awt.Dimension(32, 32));
        blankBtn1.setPreferredSize(new java.awt.Dimension(32, 32));

        blankTgl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/blank.png"))); // NOI18N
        blankTgl.setAlignmentY(0.0F);
        blankTgl.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        blankTgl.setMargin(new java.awt.Insets(0, 0, 0, 0));
        blankTgl.setMaximumSize(new java.awt.Dimension(32, 32));
        blankTgl.setMinimumSize(new java.awt.Dimension(32, 32));
        blankTgl.setPreferredSize(new java.awt.Dimension(32, 32));
        blankTgl.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/blank.png"))); // NOI18N

        expandtglBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/expandOff.png"))); // NOI18N
        expandtglBtn.setToolTipText("拡大・縮小します");
        expandtglBtn.setBorderPainted(false);
        expandtglBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        expandtglBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        expandtglBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        expandtglBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        expandtglBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/expandOn.png"))); // NOI18N

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setFocusCycleRoot(false);
        setFocusable(false);
        setFocusableWindowState(false);
        setMaximumSize(new java.awt.Dimension(128, 2147483647));
        setResizable(false);
        setUndecorated(true);

        titleLabel.setFont(new java.awt.Font("Lucida Grande", 0, 8)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("ツール");
        titleLabel.setMaximumSize(new java.awt.Dimension(100000, 16));
        titleLabel.setMinimumSize(new java.awt.Dimension(24, 16));
        titleLabel.setPreferredSize(new java.awt.Dimension(24, 16));

        javax.swing.GroupLayout titlePanelLayout = new javax.swing.GroupLayout(titlePanel);
        titlePanel.setLayout(titlePanelLayout);
        titlePanelLayout.setHorizontalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        titlePanelLayout.setVerticalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, titlePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        toolPanel.setMaximumSize(new java.awt.Dimension(64, 161));
        toolPanel.setMinimumSize(new java.awt.Dimension(64, 161));
        toolPanel.setPreferredSize(new java.awt.Dimension(65, 161));
        toolPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        pencilBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/PencilOff.png"))); // NOI18N
        pencilBtn.setToolTipText("エンピツ (B)");
        pencilBtn.setAlignmentY(0.0F);
        pencilBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pencilBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pencilBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        pencilBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        pencilBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        pencilBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/PencilOn.png"))); // NOI18N
        toolPanel.add(pencilBtn);

        eraserBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/EraserOff.png"))); // NOI18N
        eraserBtn.setToolTipText("消しゴム (E)");
        eraserBtn.setAlignmentY(0.0F);
        eraserBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        eraserBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        eraserBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        eraserBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        eraserBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        eraserBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/EraserOn.png"))); // NOI18N
        toolPanel.add(eraserBtn);

        lineBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/LineOff.png"))); // NOI18N
        lineBtn.setToolTipText("ライン (U)");
        lineBtn.setAlignmentY(0.0F);
        lineBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lineBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lineBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        lineBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        lineBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        lineBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/LineOn.png"))); // NOI18N
        toolPanel.add(lineBtn);

        textBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/TextOff.png"))); // NOI18N
        textBtn.setToolTipText("文字 (T)");
        textBtn.setAlignmentY(0.0F);
        textBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        textBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        textBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        textBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        textBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        textBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/TextOn.png"))); // NOI18N
        toolPanel.add(textBtn);

        ovalBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/OvalOff.png"))); // NOI18N
        ovalBtn.setToolTipText("円");
        ovalBtn.setAlignmentY(0.0F);
        ovalBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        ovalBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ovalBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        ovalBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        ovalBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        ovalBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/OvalOn.png"))); // NOI18N
        toolPanel.add(ovalBtn);

        ovalFillBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/FillOvalOff.png"))); // NOI18N
        ovalFillBtn.setToolTipText("円 (O)");
        ovalFillBtn.setAlignmentY(0.0F);
        ovalFillBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        ovalFillBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ovalFillBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        ovalFillBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        ovalFillBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        ovalFillBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/FillOvalOn.png"))); // NOI18N
        toolPanel.add(ovalFillBtn);

        dotsSparseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/dotsSparseOff.png"))); // NOI18N
        dotsSparseBtn.setBorderPainted(false);
        dotsSparseBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        dotsSparseBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        dotsSparseBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        dotsSparseBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        dotsSparseBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/dotsSparseOn.png"))); // NOI18N
        toolPanel.add(dotsSparseBtn);

        netSparseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/netSparseOff.png"))); // NOI18N
        netSparseBtn.setBorderPainted(false);
        netSparseBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        netSparseBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        netSparseBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        netSparseBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        netSparseBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/netSparseOn.png"))); // NOI18N
        toolPanel.add(netSparseBtn);

        rectBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/RectOff.png"))); // NOI18N
        rectBtn.setToolTipText("長方形");
        rectBtn.setAlignmentY(0.0F);
        rectBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        rectBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rectBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        rectBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        rectBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        rectBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/RectOn.png"))); // NOI18N
        toolPanel.add(rectBtn);

        rectFillBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/FillRectOff.png"))); // NOI18N
        rectFillBtn.setToolTipText("長方形 (I)");
        rectFillBtn.setAlignmentY(0.0F);
        rectFillBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        rectFillBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rectFillBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        rectFillBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        rectFillBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        rectFillBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/FillRectOn.png"))); // NOI18N
        toolPanel.add(rectFillBtn);

        dotsMediumBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/dotsMediumOff.png"))); // NOI18N
        dotsMediumBtn.setBorderPainted(false);
        dotsMediumBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        dotsMediumBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        dotsMediumBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        dotsMediumBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        dotsMediumBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/dotsMediumOn.png"))); // NOI18N
        toolPanel.add(dotsMediumBtn);

        netMediumBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/netMediumOff.png"))); // NOI18N
        netMediumBtn.setBorderPainted(false);
        netMediumBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        netMediumBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        netMediumBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        netMediumBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        netMediumBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/netMediumOn.png"))); // NOI18N
        toolPanel.add(netMediumBtn);

        polyBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/PolyOff.png"))); // NOI18N
        polyBtn.setToolTipText("多角形");
        polyBtn.setAlignmentY(0.0F);
        polyBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        polyBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        polyBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        polyBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        polyBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        polyBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/PolyOn.png"))); // NOI18N
        toolPanel.add(polyBtn);

        polyFillBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/FillPolyOff.png"))); // NOI18N
        polyFillBtn.setToolTipText("多角形 (P)");
        polyFillBtn.setAlignmentY(0.0F);
        polyFillBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        polyFillBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        polyFillBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        polyFillBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        polyFillBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        polyFillBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/FillPolyOn.png"))); // NOI18N
        toolPanel.add(polyFillBtn);

        dotsDenseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/dotsDenseOff.png"))); // NOI18N
        dotsDenseBtn.setBorderPainted(false);
        dotsDenseBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        dotsDenseBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        dotsDenseBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        dotsDenseBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        dotsDenseBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/dotsDenseOn.png"))); // NOI18N
        toolPanel.add(dotsDenseBtn);

        netDenseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/netDenseOff.png"))); // NOI18N
        netDenseBtn.setBorderPainted(false);
        netDenseBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        netDenseBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        netDenseBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        netDenseBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        netDenseBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/netDenseOn.png"))); // NOI18N
        toolPanel.add(netDenseBtn);

        selectBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/selectOff.png"))); // NOI18N
        selectBtn.setToolTipText("選択移動 (S)");
        selectBtn.setBorderPainted(false);
        selectBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        selectBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        selectBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        selectBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        selectBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/selectOn.png"))); // NOI18N
        toolPanel.add(selectBtn);

        clippingBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/clippingOff.png"))); // NOI18N
        clippingBtn.setToolTipText("切り抜き (C)");
        clippingBtn.setAlignmentY(0.0F);
        clippingBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        clippingBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        clippingBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        clippingBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        clippingBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        clippingBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/clippingOn.png"))); // NOI18N
        toolPanel.add(clippingBtn);

        expandBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/expandOff.png"))); // NOI18N
        expandBtn.setToolTipText("ズーム (Z)");
        expandBtn.setBorderPainted(false);
        expandBtn.setContentAreaFilled(false);
        expandBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        expandBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        expandBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        expandBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        expandBtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/expandOn.png"))); // NOI18N
        expandBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/expandOn.png"))); // NOI18N
        toolPanel.add(expandBtn);

        clearBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/clearOff.png"))); // NOI18N
        clearBtn.setToolTipText("全て消去");
        clearBtn.setBorderPainted(false);
        clearBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        clearBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        clearBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        clearBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        clearBtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/clearOn.png"))); // NOI18N
        clearBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/clearOn.png"))); // NOI18N
        toolPanel.add(clearBtn);

        rotateLeftBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/rotateLeftOff.png"))); // NOI18N
        rotateLeftBtn.setToolTipText("左に90度回転");
        rotateLeftBtn.setBorderPainted(false);
        rotateLeftBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rotateLeftBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        rotateLeftBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        rotateLeftBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        rotateLeftBtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/rotateLeftOn.png"))); // NOI18N
        rotateLeftBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/rotateLeftOn.png"))); // NOI18N
        toolPanel.add(rotateLeftBtn);

        rotateRightBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/rotateRIghtOff.png"))); // NOI18N
        rotateRightBtn.setToolTipText("右に90度回転 (R)");
        rotateRightBtn.setBorderPainted(false);
        rotateRightBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rotateRightBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        rotateRightBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        rotateRightBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        rotateRightBtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/rotateRightOn.png"))); // NOI18N
        rotateRightBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/rotateRightOn.png"))); // NOI18N
        toolPanel.add(rotateRightBtn);

        undoBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/undoOff.png"))); // NOI18N
        undoBtn.setToolTipText("元に戻す");
        undoBtn.setBorderPainted(false);
        undoBtn.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/undoDis.png"))); // NOI18N
        undoBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        undoBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        undoBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        undoBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        undoBtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/undoOn.png"))); // NOI18N
        undoBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/undoOn.png"))); // NOI18N
        toolPanel.add(undoBtn);

        redoBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/redoOff.png"))); // NOI18N
        redoBtn.setToolTipText("再実行");
        redoBtn.setBorderPainted(false);
        redoBtn.setContentAreaFilled(false);
        redoBtn.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/redoDis.png"))); // NOI18N
        redoBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        redoBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        redoBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        redoBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        redoBtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/redoOn.png"))); // NOI18N
        redoBtn.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/redoOn.png"))); // NOI18N
        toolPanel.add(redoBtn);

        alphaPanel.setLayout(new java.awt.BorderLayout());

        alphaSlider.setFont(alphaSlider.getFont().deriveFont(alphaSlider.getFont().getSize()-3f));
        alphaSlider.setToolTipText("アルファ値を設定します");
        alphaSlider.setAlignmentX(0.0F);
        alphaSlider.setAlignmentY(0.0F);
        alphaSlider.setPreferredSize(new java.awt.Dimension(30, 29));
        alphaPanel.add(alphaSlider, java.awt.BorderLayout.SOUTH);

        alphaLbl.setFont(alphaLbl.getFont());
        alphaLbl.setText(" alpha");
        alphaPanel.add(alphaLbl, java.awt.BorderLayout.WEST);

        alphaField.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
        alphaField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        alphaField.setText("0.50");
        alphaPanel.add(alphaField, java.awt.BorderLayout.EAST);

        widthPanel.setLayout(new java.awt.BorderLayout());

        widthSlider.setFont(widthSlider.getFont().deriveFont(widthSlider.getFont().getSize()-3f));
        widthPanel.add(widthSlider, java.awt.BorderLayout.SOUTH);

        widthLabel.setText(" line width");
        widthPanel.add(widthLabel, java.awt.BorderLayout.WEST);

        widthField.setFont(widthField.getFont().deriveFont(widthField.getFont().getSize()-4f));
        widthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        widthField.setText("4.00");
        widthField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widthFieldActionPerformed(evt);
            }
        });
        widthPanel.add(widthField, java.awt.BorderLayout.EAST);

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        colorPalettePanel.setAlignmentX(0.0F);
        colorPalettePanel.setAlignmentY(0.0F);
        colorPalettePanel.setMaximumSize(new java.awt.Dimension(64, 72));
        colorPalettePanel.setMinimumSize(new java.awt.Dimension(64, 72));
        colorPalettePanel.setOpaque(false);
        colorPalettePanel.setPreferredSize(new java.awt.Dimension(64, 72));
        colorPalettePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        colorBtn.setToolTipText("色を選択します");
        colorBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        colorBtn.setMaximumSize(new java.awt.Dimension(32, 29));
        colorBtn.setMinimumSize(new java.awt.Dimension(32, 29));
        colorBtn.setPreferredSize(new java.awt.Dimension(64, 18));
        colorPalettePanel.add(colorBtn);

        cPalette1.putClientProperty("Quaqua.Button.style", "square");
        cPalette1.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette1.setForeground(new java.awt.Color(251, 186, 186));
        cPalette1.setAlignmentY(0.0F);
        cPalette1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette1.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette1);

        cPalette2.putClientProperty("Quaqua.Button.style", "square");
        cPalette2.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette2.setForeground(new java.awt.Color(255, 119, 119));
        cPalette2.setAlignmentY(0.0F);
        cPalette2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette2.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette2);

        cPalette3.putClientProperty("Quaqua.Button.style", "square");
        cPalette3.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette3.setForeground(new java.awt.Color(255, 0, 0));
        cPalette3.setAlignmentY(0.0F);
        cPalette3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette3.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette3);

        cPalette4.putClientProperty("Quaqua.Button.style", "square");
        cPalette4.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette4.setForeground(new java.awt.Color(179, 27, 0));
        cPalette4.setAlignmentY(0.0F);
        cPalette4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette4.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette4);

        cPalette5.putClientProperty("Quaqua.Button.style", "square");
        cPalette5.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette5.setForeground(new java.awt.Color(204, 153, 255));
        cPalette5.setAlignmentY(0.0F);
        cPalette5.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette5.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette5);

        cPalette6.putClientProperty("Quaqua.Button.style", "square");
        cPalette6.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette6.setForeground(new java.awt.Color(204, 0, 153));
        cPalette6.setAlignmentY(0.0F);
        cPalette6.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette6.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette6);

        cPalette7.putClientProperty("Quaqua.Button.style", "square");
        cPalette7.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette7.setForeground(new java.awt.Color(153, 0, 153));
        cPalette7.setAlignmentY(0.0F);
        cPalette7.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette7.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette7);

        cPalette8.putClientProperty("Quaqua.Button.style", "square");
        cPalette8.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette8.setForeground(new java.awt.Color(102, 0, 102));
        cPalette8.setAlignmentY(0.0F);
        cPalette8.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette8.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette8);

        cPalette8.putClientProperty("Quaqua.Button.style", "square");
        cPalette8.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette9.setForeground(new java.awt.Color(255, 204, 0));
        cPalette9.setAlignmentY(0.0F);
        cPalette9.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette9.setMaximumSize(new java.awt.Dimension(16, 16));
        cPalette9.setMinimumSize(new java.awt.Dimension(16, 16));
        cPalette9.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette9);

        cPalette8.putClientProperty("Quaqua.Button.style", "square");
        cPalette8.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette10.setForeground(new java.awt.Color(204, 102, 0));
        cPalette10.setAlignmentY(0.0F);
        cPalette10.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette10.setMaximumSize(new java.awt.Dimension(16, 16));
        cPalette10.setMinimumSize(new java.awt.Dimension(16, 16));
        cPalette10.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette10);

        cPalette8.putClientProperty("Quaqua.Button.style", "square");
        cPalette8.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette11.setForeground(new java.awt.Color(153, 102, 0));
        cPalette11.setAlignmentY(0.0F);
        cPalette11.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette11.setMaximumSize(new java.awt.Dimension(16, 16));
        cPalette11.setMinimumSize(new java.awt.Dimension(16, 16));
        cPalette11.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette11);

        cPalette8.putClientProperty("Quaqua.Button.style", "square");
        cPalette8.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        cPalette12.setAlignmentY(0.0F);
        cPalette12.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cPalette12.setMaximumSize(new java.awt.Dimension(16, 16));
        cPalette12.setMinimumSize(new java.awt.Dimension(16, 16));
        cPalette12.setPreferredSize(new java.awt.Dimension(16, 16));
        colorPalettePanel.add(cPalette12);

        jPanel2.add(colorPalettePanel);

        lineWidthPanel.setAlignmentX(0.0F);
        lineWidthPanel.setAlignmentY(0.0F);
        lineWidthPanel.setMaximumSize(new java.awt.Dimension(64, 72));
        lineWidthPanel.setMinimumSize(new java.awt.Dimension(64, 72));
        lineWidthPanel.setPreferredSize(new java.awt.Dimension(64, 72));
        lineWidthPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        lineWidth1.putClientProperty("JButton.buttonType", "square");
        lineWidth1.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        lineWidth1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/lineBtn1off.png"))); // NOI18N
        lineWidth1.setAlignmentY(0.0F);
        lineWidth1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lineWidth1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lineWidth1.setMaximumSize(new java.awt.Dimension(64, 18));
        lineWidth1.setMinimumSize(new java.awt.Dimension(64, 18));
        lineWidth1.setPreferredSize(new java.awt.Dimension(64, 18));
        lineWidth1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/lineBtn1on.png"))); // NOI18N
        lineWidthPanel.add(lineWidth1);

        lineWidth2.putClientProperty("JButton.buttonType", "square");
        lineWidth2.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        lineWidth2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/lineBtn2off.png"))); // NOI18N
        lineWidth2.setAlignmentY(0.0F);
        lineWidth2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lineWidth2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lineWidth2.setMaximumSize(new java.awt.Dimension(64, 18));
        lineWidth2.setMinimumSize(new java.awt.Dimension(64, 18));
        lineWidth2.setPreferredSize(new java.awt.Dimension(64, 18));
        lineWidth2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/lineBtn2on.png"))); // NOI18N
        lineWidthPanel.add(lineWidth2);

        lineWidth3.putClientProperty("JButton.buttonType", "square");
        lineWidth3.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        lineWidth3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/lineBtn3off.png"))); // NOI18N
        lineWidth3.setAlignmentY(0.0F);
        lineWidth3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lineWidth3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lineWidth3.setMaximumSize(new java.awt.Dimension(64, 18));
        lineWidth3.setMinimumSize(new java.awt.Dimension(64, 18));
        lineWidth3.setPreferredSize(new java.awt.Dimension(64, 18));
        lineWidth3.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/lineBtn3on.png"))); // NOI18N
        lineWidthPanel.add(lineWidth3);

        lineWidth4.putClientProperty("JButton.buttonType", "square");
        lineWidth4.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(0,0,0,0));
        lineWidth4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/lineBtn4off.png"))); // NOI18N
        lineWidth4.setAlignmentY(0.0F);
        lineWidth4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lineWidth4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lineWidth4.setMaximumSize(new java.awt.Dimension(64, 18));
        lineWidth4.setMinimumSize(new java.awt.Dimension(64, 18));
        lineWidth4.setPreferredSize(new java.awt.Dimension(64, 18));
        lineWidth4.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/open/dolphin/impl/scheam/resources/lineBtn4on.png"))); // NOI18N
        lineWidthPanel.add(lineWidth4);

        jPanel2.add(lineWidthPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(toolPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
            .addComponent(widthPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(alphaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(titlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toolPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alphaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(widthPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void widthFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_widthFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_widthFieldActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField alphaField;
    private javax.swing.JLabel alphaLbl;
    private javax.swing.JPanel alphaPanel;
    private javax.swing.JSlider alphaSlider;
    private javax.swing.JButton blankBtn1;
    private javax.swing.JToggleButton blankTgl;
    private javax.swing.JButton cPalette1;
    private javax.swing.JButton cPalette10;
    private javax.swing.JButton cPalette11;
    private javax.swing.JButton cPalette12;
    private javax.swing.JButton cPalette2;
    private javax.swing.JButton cPalette3;
    private javax.swing.JButton cPalette4;
    private javax.swing.JButton cPalette5;
    private javax.swing.JButton cPalette6;
    private javax.swing.JButton cPalette7;
    private javax.swing.JButton cPalette8;
    private javax.swing.JButton cPalette9;
    private javax.swing.JButton clearBtn;
    private javax.swing.JToggleButton clippingBtn;
    private javax.swing.JButton colorBtn;
    private javax.swing.JPanel colorPalettePanel;
    private javax.swing.JToggleButton dotsDenseBtn;
    private javax.swing.JToggleButton dotsMediumBtn;
    private javax.swing.JToggleButton dotsSparseBtn;
    private javax.swing.JToggleButton eraserBtn;
    private javax.swing.JButton expandBtn;
    private javax.swing.JToggleButton expandtglBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToggleButton lineBtn;
    private javax.swing.JToggleButton lineWidth1;
    private javax.swing.JToggleButton lineWidth2;
    private javax.swing.JToggleButton lineWidth3;
    private javax.swing.JToggleButton lineWidth4;
    private javax.swing.JPanel lineWidthPanel;
    private javax.swing.JToggleButton netDenseBtn;
    private javax.swing.JToggleButton netMediumBtn;
    private javax.swing.JToggleButton netSparseBtn;
    private javax.swing.JToggleButton ovalBtn;
    private javax.swing.JToggleButton ovalFillBtn;
    private javax.swing.JToggleButton pencilBtn;
    private javax.swing.JToggleButton polyBtn;
    private javax.swing.JToggleButton polyFillBtn;
    private javax.swing.JToggleButton rectBtn;
    private javax.swing.JToggleButton rectFillBtn;
    private javax.swing.JButton redoBtn;
    private javax.swing.JButton rotateLeftBtn;
    private javax.swing.JButton rotateRightBtn;
    private javax.swing.JToggleButton selectBtn;
    private javax.swing.JToggleButton textBtn;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JPanel toolPanel;
    private javax.swing.JButton undoBtn;
    private javax.swing.JTextField widthField;
    private javax.swing.JLabel widthLabel;
    private javax.swing.JPanel widthPanel;
    private javax.swing.JSlider widthSlider;
    // End of variables declaration//GEN-END:variables
}
