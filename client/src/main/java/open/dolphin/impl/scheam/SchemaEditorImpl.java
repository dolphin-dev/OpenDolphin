package open.dolphin.impl.scheam;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import open.dolphin.client.SchemaEditor;
import open.dolphin.impl.scheam.holder.DrawingHolder;
import open.dolphin.impl.scheam.schemahelper.SchemaUtils;
import open.dolphin.impl.scheam.schemahelper.ShapeIconMaker;
import open.dolphin.infomodel.ExtRefModel;
import open.dolphin.infomodel.SchemaModel;

/**
 * SchemaEditorImpl ここではコンポネントを作る
 *    SchemaEditorProperties: 描画のための変数を扱う
 *    StateMgr: ボタンで state を切り替える
 *    State: 実際の描画をする部分
 * @author kazm, pns
 */
public class SchemaEditorImpl implements SchemaEditor {

    public static final String TITLE = "シェーマエディタ";

    // ここに絵を描く（JComponent の子で paintComponent を Override している）
    private SchemaCanvas canvas;
    // 重なっている DrowingHolder を順番に保持する
    private ArrayList<DrawingHolder> drawingList;
    // schema の infomodel
    private SchemaModel model;
    // Matisse で作った画像編集フレーム(JFrame)
    private SchemaCanvasView canvasView;
    // Matisse で作ったツールパレット(JFrame)
    private SchemaToolView toolView;
    // KartePane との通信用
    private PropertyChangeSupport boundSupport;

    private SchemaEditorProperties properties;
    private StateMgr stateMgr;
    private UndoMgr undoMgr;
    private boolean editable;
        
    //ボタン
    private JButton[] cPaletteBtn = new JButton[12];
    private JButton cancelBtn;
    private JButton clearBtn;
    private JButton colorBtn;
    private JToggleButton eraserBtn;
    private JToggleButton lineBtn;
    private JToggleButton[] lineWidthBtn = new JToggleButton[4];
    private static final float[] lineWidthValue = { 1.5f, 2.5f, 3.5f, 4.5f }; // なぜか 3.0f でアンチエリアスがかからない
    private JButton okBtn;
    private JToggleButton ovalBtn;
    private JToggleButton ovalFillBtn;
    private JToggleButton pencilBtn;
    private JToggleButton polyBtn;
    private JToggleButton polyFillBtn;
    private JToggleButton rectBtn;
    private JToggleButton rectFillBtn;
    private JComboBox roleCombo;
    private JToggleButton selectBtn;
    private JToggleButton textBtn;
    private JTextField titleFld;
    private JButton undoBtn;
    private JButton redoBtn;

    private JButton rotateRightBtn;
    private JButton rotateLeftBtn;
    private JButton expandBtn;
    private JToggleButton clippingBtn;
    private JToggleButton dotsSparseBtn;
    private JToggleButton dotsMediumBtn;
    private JToggleButton dotsDenseBtn;
    private JToggleButton netSparseBtn;
    private JToggleButton netMediumBtn;
    private JToggleButton netDenseBtn;

    private ButtonGroup toolBg = new ButtonGroup();
    private ButtonGroup lineWidthBg = new ButtonGroup();

    // アルファ値
    private JSlider alphaSlider;
    private JTextField alphaField;
    
    // Line Width スライダー
    private JSlider widthSlider;
    private JTextField widthField;
    
    // カーソル
    //private Cursor defaultCursor;
    private Cursor crossHairCursor;
    private Cursor moveCursor;
    private Cursor eraserCursor;
    private Cursor textCursor;
    
    public SchemaEditorImpl() {}

    @Override
    public void start() {
        initComponents(editable);
    }

    // getter and setters
    @Override
    public void setSchema(SchemaModel model) {
        this.model = model;
    }
    @Override
    public void setEditable(boolean b) {
        this.editable = b;
    }
    public SchemaCanvas getCanvas() {
        return canvas;
    }
    public ArrayList<DrawingHolder> getDrawingList() {
        return drawingList;
    }
    public SchemaEditorProperties getProperties() {
        return properties;
    }
    public SchemaCanvasView getCanvasView() {
        return canvasView;
    }
    public SchemaToolView getToolView() {
        return toolView;
    }
    public StateMgr getStateMgr() {
        return stateMgr;
    }
    public UndoMgr getUndoMgr() {
        return undoMgr;
    }
    
    /**
     * KartePane に返す BufferedImage を作る
     * @return
     */
    private BufferedImage createImage() {
        BufferedImage baseImage = canvas.getBaseImage();

        int width = baseImage.getWidth();
        int height = baseImage.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics2D g2 = result.createGraphics();
        // まず全部白く塗る
        Rectangle2D bounds = new Rectangle2D.Double(0, 0, width, height);
        g2.setPaint(Color.WHITE);
        g2.fill(bounds);
        // これを入れないと，図形の縁がギザギザになる
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(baseImage, null, 0, 0);
        for (DrawingHolder d : drawingList) d.draw(g2);
        return result;
    }

    /**
     * KartePane に SchemaModel を返す
     * 　「カルテに展開」ボタン：　createImage で作った BufferedImage を持ってくる
     * 　「破棄」ボタン　　　　：　null を持ってくる
     * @param image
     */
    private void firePropertyChange(BufferedImage image) {
        // カルテに展開
        if (image != null) {
            ImageIcon icon = new ImageIcon(image);
            model.setIcon(icon);
            String text = canvasView.getTitleFld().getText().trim();
            if (text.equals("")) {
                text = SchemaEditorProperties.DEFAULT_TITLE;
            }
            model.getExtRefModel().setTitle(text);
            model.getExtRefModel().setMedicalRole((String) canvasView.getRoleCombo().getSelectedItem());
            boundSupport.firePropertyChange("imageProp", null, model);

        // キャンセル
        } else {
            boundSupport.firePropertyChange("imageProp", model, null);
        }
    }

    /**
     * このリスナは KartePane の propertyChanged を呼び出す
     * @param l
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(l);
    }
    
    private void initComponents(boolean editable) {
        properties = new SchemaEditorProperties();
        drawingList = new ArrayList<DrawingHolder>(5);
        undoMgr = new UndoMgr(this);

        // SchemaCanvas の設定
        canvas = new SchemaCanvas(this);
        canvas.setBorder(BorderFactory.createEmptyBorder());

        // 持ってきた SchemaModel の Image を BufferedImage に変換してセット
        BufferedImage srcImage = SchemaUtils.imageToBufferedImage(model.getIcon());
        canvas.setBaseImage(srcImage);

        // canvas と tool の View (JFrame) を作る
        canvasView = new SchemaCanvasView();
        toolView = new SchemaToolView();
        // お互いに連絡するために登録する
        canvasView.setSchemaToolView(toolView);
        toolView.setSchemaCanvasView(canvasView);

        // canvasView に canvas を登録
        JPanel canvasPanel = canvasView.getCanvasPanel();
        canvasPanel.setBorder(BorderFactory.createEmptyBorder());
        canvasPanel.setBackground(canvasView.getBackground());
        canvasPanel.add(canvas);

        stateMgr = new StateMgr(this);
        
        // カーソル作成
        // defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        moveCursor = new Cursor(Cursor.MOVE_CURSOR);
        textCursor = new Cursor(Cursor.TEXT_CURSOR);
        crossHairCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        eraserCursor = ShapeIconMaker.createIconCursor(SchemaEditorProperties.ICON_ERASER);

        // ボタン設定
        // ツールボタン
        pencilBtn = toolView.getPencilBtn();
        eraserBtn = toolView.getEraserBtn();
        ovalBtn = toolView.getOvalBtn();
        ovalFillBtn = toolView.getOvalFillBtn();
        rectBtn = toolView.getRectBtn();
        rectFillBtn = toolView.getRectFillBtn();
        polyBtn = toolView.getPolyBtn();
        polyFillBtn = toolView.getPolyFillBtn();
        textBtn = toolView.getTextBtn();
        lineBtn = toolView.getLineBtn();
        selectBtn = toolView.getSelectBtn();

        clippingBtn = toolView.getClippingBtn();
        netSparseBtn = toolView.getNetSparseBtn();
        netMediumBtn = toolView.getNetMediumBtn();
        netDenseBtn = toolView.getNetDenseBtn();
        dotsSparseBtn = toolView.getDotsSparseBtn();
        dotsMediumBtn = toolView.getDotsMediumBtn();
        dotsDenseBtn = toolView.getDotsDenseBtn();

        JToggleButton[] toolButtons = {
            pencilBtn, eraserBtn, ovalBtn, ovalFillBtn, rectBtn, rectFillBtn, polyBtn, polyFillBtn, textBtn, lineBtn,
            selectBtn, clippingBtn, dotsSparseBtn, dotsMediumBtn, dotsDenseBtn, netSparseBtn, netMediumBtn, netDenseBtn
        };

        for (JToggleButton b : toolButtons) {
            toolBg.add(b);
            b.setEnabled(editable);
        }

        // 線の太さボタン
        for (int i=0; i<lineWidthValue.length; i++) {
            lineWidthBtn[i] = toolView.getLineWidthBtn(i);
            lineWidthBg.add(lineWidthBtn[i]);
            lineWidthBtn[i].setEnabled(editable);
        }

        // カラーボタンと，カラーパレット
        colorBtn = toolView.getColorBtn();  colorBtn.setEnabled(editable);
        ImageIcon cPaletteIcon[] = new ImageIcon[12];
        for (int i=0; i<12; i++) {
            cPaletteBtn[i] = toolView.getCPaletteBtn(i);
            cPaletteIcon[i] = ShapeIconMaker.createRectFillIcon(
                    cPaletteBtn[i].getForeground(), SchemaEditorProperties.CPALETTE_SIZE);
            cPaletteBtn[i].setIcon(cPaletteIcon[i]);
            cPaletteBtn[i].setEnabled(editable);
        }
        
        // その他のボタン
        undoBtn = toolView.getUndoBtn();    undoBtn.setEnabled(false);
        redoBtn = toolView.getRedoBtn();    redoBtn.setEnabled(false);
        clearBtn = toolView.getClearBtn();  clearBtn.setEnabled(editable);
        rotateRightBtn = toolView.getRotateRightBtn(); rotateLeftBtn = toolView.getRotateLeftBtn();
        expandBtn = toolView.getExpandBtn();
        cancelBtn = canvasView.getCancelBtn(); 
        okBtn = canvasView.getOkBtn(); okBtn.setSelected(editable);
        
        // アルファ値スライダー
        alphaSlider = toolView.getAlphaSlider();
        alphaField = toolView.getAlphaField();
        alphaField.setEditable(false);
        alphaSlider.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                float val = (float)alphaSlider.getValue() / 100;
                properties.setAlpha(val);
                alphaField.setText(String.format("%.2f", val));
            }
        });
        toolView.getAlphaLabel().addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                properties.setAlpha(0.5f);
                alphaSlider.setValue(50);
                alphaField.setText("0.50");
            }
        });
        
        // Line Width スライダー
        widthSlider = toolView.getWidthSlider();
        widthField = toolView.getWidthField();
        widthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                float val = (float)widthSlider.getValue() / 10;
                properties.setLineWidth(val);
                
                setLineWidthGUI();
            }
        });

        // ショートカット登録
        ShortcutKey.register(canvasView, undoBtn, KeyEvent.VK_Z, InputEvent.META_DOWN_MASK, "undo");
        ShortcutKey.register(canvasView, redoBtn, KeyEvent.VK_Z, InputEvent.META_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, "redo");
        ShortcutKey.register(canvasView, cancelBtn, KeyEvent.VK_ESCAPE, 0, "escape");
        ShortcutKey.register(canvasView, okBtn, KeyEvent.VK_ENTER, 0, "enter");
        ShortcutKey.register(canvasView, lineWidthBtn[0], KeyEvent.VK_1, 0, "line1");
        ShortcutKey.register(canvasView, lineWidthBtn[1], KeyEvent.VK_2, 0, "line2");
        ShortcutKey.register(canvasView, lineWidthBtn[2], KeyEvent.VK_3, 0, "line3");
        ShortcutKey.register(canvasView, lineWidthBtn[3], KeyEvent.VK_4, 0, "line4");
        ShortcutKey.register(canvasView, pencilBtn, KeyEvent.VK_B, 0, "pencil");
        ShortcutKey.register(canvasView, clippingBtn, KeyEvent.VK_C, 0, "clipping");
        ShortcutKey.register(canvasView, eraserBtn, KeyEvent.VK_E, 0, "eraser");
        ShortcutKey.register(canvasView, textBtn, KeyEvent.VK_T, 0, "text");
        ShortcutKey.register(canvasView, expandBtn, KeyEvent.VK_Z, 0, "expand");
        ShortcutKey.register(canvasView, lineBtn, KeyEvent.VK_U, 0, "line");
        ShortcutKey.register(canvasView, ovalFillBtn, KeyEvent.VK_O, 0, "oval");
        ShortcutKey.register(canvasView, rectFillBtn, KeyEvent.VK_I, 0, "rect");
        ShortcutKey.register(canvasView, polyFillBtn, KeyEvent.VK_P, 0, "poly");
        ShortcutKey.register(canvasView, selectBtn, KeyEvent.VK_S, 0, "select");
        ShortcutKey.register(canvasView, rotateRightBtn, KeyEvent.VK_R, 0, "rotateRight");
        ShortcutKey.register(canvasView, rotateLeftBtn, KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK, "rotateLeft");
        
        // マウスリスナ　StateMgr で切り替えた State を呼び出す
        AtokAvoidableMouseListener atokAvoider = new AtokAvoidableMouseListener(stateMgr);
        canvas.addMouseListener(atokAvoider);
        canvas.addMouseMotionListener(atokAvoider);

        // ボタンアクション登録  stateMgr.文字列() が呼ばれる
        ButtonAction buttonAction = new ButtonAction(this);
        // クリックした後，マウスで絵を描いたりするボタン
        buttonAction.register(selectBtn, "startSelect", moveCursor);
        buttonAction.register(lineBtn, "startLine", crossHairCursor);
        buttonAction.register(rectBtn, "startRect", crossHairCursor);
        buttonAction.register(ovalBtn, "startEllipse", crossHairCursor);
        buttonAction.register(polyBtn, "startPolygon", crossHairCursor);
        buttonAction.register(rectFillBtn, "startRectFill", crossHairCursor);
        buttonAction.register(ovalFillBtn, "startEllipseFill", crossHairCursor);
        buttonAction.register(polyFillBtn, "startPolygonFill", crossHairCursor);
        buttonAction.register(textBtn, "startText", textCursor);
        buttonAction.register(pencilBtn, "startPencil", crossHairCursor);
        buttonAction.register(eraserBtn, "startEraser", eraserCursor);
        buttonAction.register(clippingBtn, "startClipping", crossHairCursor);
        buttonAction.register(netSparseBtn, "startNetSparse", crossHairCursor);
        buttonAction.register(netMediumBtn, "startNetMedium", crossHairCursor);
        buttonAction.register(netDenseBtn, "startNetDense", crossHairCursor);
        buttonAction.register(dotsSparseBtn, "startDotsSparse", crossHairCursor);
        buttonAction.register(dotsMediumBtn, "startDotsMedium", crossHairCursor);
        buttonAction.register(dotsDenseBtn, "startDotsDense", crossHairCursor);

        // クリックだけで作業完了するボタン（カーソル変更は不要）
        buttonAction.register(undoBtn, "undo", null);
        buttonAction.register(redoBtn, "redo", null);
        buttonAction.register(clearBtn, "clear", null);
        buttonAction.register(rotateRightBtn, "rotateRight", null);
        buttonAction.register(rotateLeftBtn, "rotateLeft", null);
        buttonAction.register(expandBtn, "expand", null);

        // 線の太さにアクションを設定
        for (int i=0; i<lineWidthValue.length; i++) {
            final int final_i = i;
            lineWidthBtn[i].addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    float lw = lineWidthValue[final_i];
                    if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) lw *= 2;
                    properties.setLineWidth(lw);
                    widthSlider.setValue((int)(lw*10));
                    widthField.setText(String.format("%.2f", lw));
                }
            });
        }
        // カラーパレットボタンにアクションを設定
        for (int i=0; i<12; i++) {
            final int final_i = i;
            cPaletteBtn[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Color c = cPaletteBtn[final_i].getForeground();
                    properties.setFillColor(c);
                    colorBtn.setIcon(ShapeIconMaker.createRectFillIcon(c, SchemaEditorProperties.SHAPEICON_SIZE));
                }
            });
        }
        // 選択された色を表示するボタン　押すと colorChooser が起動
        colorBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseColor();
            }
        });
        okBtn.setEnabled(editable);
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
                firePropertyChange(createImage());
            }
        });
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
                firePropertyChange(null);
            }
        });

        titleFld = canvasView.getTitleFld();
        titleFld.setText(SchemaEditorProperties.DEFAULT_TITLE);
        titleFld.setEnabled(editable);

        roleCombo = canvasView.getRoleCombo();
        roleCombo.setSelectedItem(SchemaEditorProperties.DEFAULT_ROLE);
        roleCombo.setEnabled(editable);
        
        // これらのボタンがフォーカスを取ってしまうとショートカットが効かなくなる
        okBtn.setFocusable(false);
        cancelBtn.setFocusable(false);
        roleCombo.setFocusable(false);

        ExtRefModel extRef = model.getExtRefModel();
        if (extRef != null) {
            String text = extRef.getTitle();
            if (text != null && (!text.equals(""))) {
                titleFld.setText(text);
            }
            text = extRef.getMedicalRole();
            if (text != null && (!text.equals(""))) {
                roleCombo.setSelectedItem(text);
            }
        }

        // プロパティーファイルの値によりボタンの初期値をセット
        properties.load();

        // 線の太さ
        setLineWidthGUI();
        
        // 色
        colorBtn.setIcon(ShapeIconMaker.createRectFillIcon(properties.getFillColor(), SchemaEditorProperties.SHAPEICON_SIZE));
        // ツールボタン
        int btnNo = Math.min(properties.getSelectedTButtonNumber(), toolButtons.length-1);
        toolButtons[btnNo].doClick();
        // アルファスライダー
        alphaSlider.setValue((int) (properties.getAlpha() * 100));
        alphaField.setText(String.format("%.2f", properties.getAlpha()));
        
        // baseImage から，view の必要 width, height を計算（自動ではうまくいかない）
        properties.computeViewBounds(canvasView, toolView, srcImage);

        if (editable) toolView.setVisible(true); // editable でない場合はツールパネルを出さない
        canvasView.setVisible(true);
    }
    
    private void setLineWidthGUI() {
        lineWidthBg.clearSelection();
        
        float lw = properties.getLineWidth();
        if (lw == 1.5f) lineWidthBtn[0].doClick();
        else if (lw == 2.5f) lineWidthBtn[1].doClick();
        else if (lw == 3.5f) lineWidthBtn[2].doClick();
        else if (lw == 4.5f) lineWidthBtn[3].doClick();
        
        widthSlider.setValue((int)(lw*10));
        widthField.setText(String.format("%.2f", lw));        
    }
    
    private void close() {
        // 選択されているツールボタンの番号を調べる
        int btnNo = 0;
        for (Enumeration e = toolBg.getElements(); e.hasMoreElements();) {
            if(((JToggleButton) e.nextElement()).isSelected()) break;
            btnNo++;
        }
        properties.setSelectedTButtonNumber(btnNo);
        properties.setSchemaViewRect(canvasView, toolView);

        properties.save(); // プロパティーファイルに書き込み

        canvasView.setVisible(false);
        canvasView.dispose();
        toolView.setVisible(false);
        toolView.dispose();
    }
    /**
     * カラー表示しているボタンを押したら ColorChooser を出す
     */
    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(canvasView, "塗りつぶしカラー選択", properties.getFillColor());
        if (newColor != null) {
            properties.setFillColor(newColor);
            ImageIcon icon = ShapeIconMaker.createRectFillIcon(properties.getFillColor(), SchemaEditorProperties.SHAPEICON_SIZE);
            toolView.getColorBtn().setIcon(icon);
        }
    }

    /**
     * baseImage が変わった場合，SchemaCanvas を描画し直す
     * @param srcImage
     */
    public void recomputeViewBounds(BufferedImage baseImage) {
        properties.recomputeViewBounds(canvasView, toolView, baseImage);
        canvasView.repaint(); // 明示的に描いておかないと，正方形の画像だったら repaint されない
    }
}
