package open.dolphin.impl.scheam;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Properties;
import javax.swing.ImageIcon;
import open.dolphin.impl.scheam.schemahelper.SchemaUtils;
import open.dolphin.project.Project;

/**
 * SchemaEditor で使われる変数を一括管理する
 * @author pns
 */
public class SchemaEditorProperties {

//masuda^   Preferences -> Propertiesに変更
    //private static Preferences prefs;
    private static final String SETTING_FILE_NAME = "schema-editor.properties";
    private static Properties props;
//masuda$
    
    public static final String DEFAULT_ROLE = "参考図";
    public static final String DEFAULT_TITLE = "参考画像";
    public static final Dimension SHAPEICON_SIZE = new Dimension(48,8);
    public static final Dimension CPALETTE_SIZE = new Dimension(16,16);
    
    // デフォルト値（DEFAULT_OOO）とプロパティー名（PN_OOO）
    private static final String PN_LINE_WIDTH = "lineWidth";
    private static final float DEFAULT_LINE_WIDTH = 2.5f;
    private static final String PN_FILL_COLOR = "fillColor";
    private static final Color DEFAULT_FILL_COLOR = Color.RED;
    private static final String PN_ALPHA = "alpha";
    private static final float DEFAULT_ALPHA = 0.5f;
    private static final String PN_TBUTTON_NUMBER = "toolButton";
    private static final int DEFAULT_TBUTTON_NUMBER = 0;
    private static final String PN_FONT_SIZE = "textSize";
    private static final int DEFAULT_FONT_SIZE = 24;

    private static final String PN_CANVAS_VIEW_RECT = "canvasViewRect";
    private static final String PN_TOOL_VIEW_RECT = "toolViewRect";

    private static final String DEFAULT_FONT_NAME = "Dialog";

    // 描画に使われる変数
    // プロパティーファイルに load/save する変数
    private float lineWidth = DEFAULT_LINE_WIDTH;
    private Color fillColor = DEFAULT_FILL_COLOR;
    private float alpha = DEFAULT_ALPHA;
    private int selectedTButtonNumber = DEFAULT_TBUTTON_NUMBER;
    private Rectangle canvasViewRect;
    private Rectangle toolViewRect;
    private int fontSize = DEFAULT_FONT_SIZE;
    
    // プロパティーファイル非対応
    private String fontName = DEFAULT_FONT_NAME;
    private int fontStyle = Font.BOLD;
    private boolean fill = true;
    private boolean isRightRotation;

    // アイコン
    public static final ImageIcon ICON_ERASER = new ImageIcon(SchemaEditorImpl.class.getResource(
            "/open/dolphin/impl/scheam/resources/eraserCursor.gif"));

    public SchemaEditorProperties() {
//masuda^
        //prefs = Preferences.userNodeForPackage(SchemaEditorImpl.class);
        if (props == null) {
            props = new Properties();
            Project.loadProperties(props, SETTING_FILE_NAME);
        }
//masuda$
    }
    
    /**
     * 線の太さを返す
     * @return
     */
    public float getLineWidth() {
        return lineWidth;
    }
    /**
     * 線の太さを設定する
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
    /**
     * 色を返す
     * @return
     */
    public Color getFillColor() {
        return fillColor;
    }
    /**
     * 色を設定する
     * @param fillColor
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }
    /**
     * アルファ値を返す
     * @return
     */
    public float getAlpha() {
        return alpha;
    }
    /**
     * アルファ値を設定する
     * @param alpha
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
    /**
     * テキストサイズを返す
     * @return
     */
    public int getTextSize() {
        return fontSize;
    }
    /**
     * テキストサイズを設定する
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.fontSize = textSize;
    }
    /**
     * 塗るかどうかを返す
     * @return
     */
    public boolean isFill() {
        return fill;
    }
    /**
     * 塗りかどうか設定
     * @param fill
     */
    public void setIsFill(boolean fill) {
        this.fill = fill;
    }
    /**
     * フォント名を返す
     * @return
     */
    public String getFontName() {
        return fontName;
    }
    /**
     * フォント名を設定する
     * @param fontName
     */
    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
    /**
     * フォントスタイルを返す
     * @return
     */
    public int getFontStyle() {
        return fontStyle;
    }
    /**
     * フォントスタイルを設定する
     * @param fontStyle
     */
    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }
    /**
     * フォントサイズを返す
     * @return
     */
    public int getFontSize() {
        return fontSize;
    }
    /**
     * フォントサイズを設定する
     * @param size
     */
    public void setFontSize(int size) {
        fontSize = size;
    }
    /**
     * 選択されたツールボタンの番号を返す
     * @return
     */
    public int getSelectedTButtonNumber() {
        return this.selectedTButtonNumber;
    }
    /**
     * 選択されたツールボタンの番号を設定する
     * @param n
     */
    public void setSelectedTButtonNumber(int n) {
        this.selectedTButtonNumber = n;
    }
    /**
     * 線のストロークを返す
     * @return
     */
    public Stroke getStroke() {
        return new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }
    /**
     * 消しゴムのストロークを返す
     * @return
     */
    public Stroke getEraserStroke() {
        return new BasicStroke(16.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }
    /**
     * 1ドット幅の stroke
     * @return
     */
    public Stroke getOutlineStroke() {
        return new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }
    /**
     * アルファコンポジットを返す
     * @return
     */
    public AlphaComposite getAlphaComposite() {
        return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
    }
    /**
     * テキストのアルファコンポジットを返す
     * @return
     */
    public AlphaComposite getTextComposite() {
        return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTextAlpha());
    }
    /**
     * テキストのフォントを返す
     * @return
     */
    public Font getFont() {
        return new Font(fontName, fontStyle, fontSize);
    }
    /**
     * テキストの色を返す
     * @return
     */
    public Color getTextColor() {
        return fillColor;
    }
    /**
     * テキストの色を設定する
     * @param textColor
     */
    public void setTextColor(Color textColor) {
        //this.textColor = textColor;
    }
    /**
     * テキストのアルファ値を返す
     * @return
     */
    public float getTextAlpha() {
        return alpha;
    }
    /**
     * テキストのアルファ値を設定する
     * @param textAlpha
     */
    public void setTextAlpha(float textAlpha) {
        //this.textAlpha = textAlpha;
    }
    /**
     * テキストの outline を書くときの stroke
     * @return
     */
    public Stroke getTextStroke() {
        return new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }
    /**
     * G2D 描画に必要な情報をセットする (stroke, alpha, color)
     * @param g2d
     */
    public void setGraphicsState(Graphics2D g2d) {
        g2d.setStroke(getStroke());
        g2d.setComposite(getAlphaComposite());
        g2d.setPaint(fillColor);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    /**
     * SchemaView の bounds をセットする
     * @param r
     */
    public void setSchemaViewRect(SchemaCanvasView cv, SchemaToolView tv) {
        canvasViewRect = cv.getBounds();
        toolViewRect = tv.getBounds();
    }
    /**
     * 回転変換の右左をセットする
     * @param b
     */
    public void setIsRightRotation(boolean b) {
        isRightRotation = b;
    }
    /**
     * 回転変換の方向を返す
     * @return
     */
    public boolean isRightRotation() {
        return isRightRotation;
    }

    /**
     * プロパティーファイルから読み込む
     */
    public void load() {
//masuda^   Preferences -> Propertiesに変更
        lineWidth = Float.valueOf(props.getProperty(PN_LINE_WIDTH, String.valueOf(DEFAULT_LINE_WIDTH)));
        alpha = Float.valueOf(props.getProperty(PN_ALPHA, String.valueOf(DEFAULT_ALPHA)));
        selectedTButtonNumber = Integer.valueOf(props.getProperty(PN_TBUTTON_NUMBER, "0"));
        fillColor = SchemaUtils.stringToColor(props.getProperty(PN_FILL_COLOR, "255,0,0"));
        canvasViewRect = SchemaUtils.stringToRectangle(props.getProperty(PN_CANVAS_VIEW_RECT, null));
        toolViewRect = SchemaUtils.stringToRectangle(props.getProperty(PN_TOOL_VIEW_RECT, null));
        fontSize = Integer.valueOf(props.getProperty(PN_FONT_SIZE, String.valueOf(DEFAULT_FONT_SIZE)));
/*
        lineWidth = prefs.getFloat(PN_LINE_WIDTH, DEFAULT_LINE_WIDTH);
        alpha = prefs.getFloat(PN_ALPHA, DEFAULT_ALPHA);
        selectedTButtonNumber = prefs.getInt(PN_TBUTTON_NUMBER, 0);
        fillColor = SchemaUtils.stringToColor(prefs.get(PN_FILL_COLOR, "255,0,0"));
        canvasViewRect = SchemaUtils.stringToRectangle(prefs.get(PN_CANVAS_VIEW_RECT, null));
        toolViewRect = SchemaUtils.stringToRectangle(prefs.get(PN_TOOL_VIEW_RECT, null));
        fontSize = prefs.getInt(PN_FONT_SIZE, DEFAULT_FONT_SIZE);
*/
//masuda$
    }
    /**
     * プロパティーファイルに保存する
     */
    public void save() {
//masuda^   Preferences -> Propertiesに変更
        props.setProperty(PN_LINE_WIDTH, String.valueOf(lineWidth));
        props.setProperty(PN_ALPHA, String.valueOf(alpha));
        props.setProperty(PN_TBUTTON_NUMBER, String.valueOf(selectedTButtonNumber));
        props.setProperty(PN_FILL_COLOR, SchemaUtils.colorToString(fillColor));
        props.setProperty(PN_CANVAS_VIEW_RECT, SchemaUtils.rectangleToString(canvasViewRect));
        props.setProperty(PN_TOOL_VIEW_RECT, SchemaUtils.rectangleToString(toolViewRect));
        props.setProperty(PN_FONT_SIZE, String.valueOf(fontSize));
        Project.storeProperties(props, SETTING_FILE_NAME);
/*
        prefs.putFloat(PN_LINE_WIDTH, lineWidth);
        prefs.putFloat(PN_ALPHA, alpha);
        prefs.putInt(PN_TBUTTON_NUMBER, selectedTButtonNumber);
        prefs.put(PN_FILL_COLOR, SchemaUtils.colorToString(fillColor));
        prefs.put(PN_CANVAS_VIEW_RECT, SchemaUtils.rectangleToString(canvasViewRect));
        prefs.put(PN_TOOL_VIEW_RECT, SchemaUtils.rectangleToString(toolViewRect));
        prefs.putInt(PN_FONT_SIZE, fontSize);
*/
//masuda$
    }
    /**
     * canvasView, toolView の表示位置を計算して設定する
     * 設定前の値として preferences の値を使うバージョン
     * @param srcImage
     * @return
     */
    public void computeViewBounds(SchemaCanvasView cv, SchemaToolView tv, BufferedImage baseImage) {
        computeViewBounds(cv, tv, baseImage, canvasViewRect, toolViewRect);
    }
    /**
     * canvasView, toolView の表示位置を計算して設定する
     * 設定前の値として現在の bounds を使うバージョン
     * @param cv
     * @param tv
     * @param baseImage
     */
    public void recomputeViewBounds(SchemaCanvasView cv, SchemaToolView tv, BufferedImage baseImage) {
        computeViewBounds(cv, tv, baseImage, cv.getBounds(), tv.getBounds());
    }
    /**
     * canvasView, toolView の表示位置を計算する
     * 　canvasView は pack() とかしても自動でいい大きさになってくれないので
     * @param cv SchemaCanvasView
     * @param tv ToolCanvasView
     * @param baseImage
     * @param pCvRect 設定する前の canvasView の bounds
     * @param pTvRect 設定する前の toolView の bounds
     */
    public void computeViewBounds(SchemaCanvasView cv, SchemaToolView tv, BufferedImage baseImage, Rectangle pCvRect, Rectangle pTvRect) {

        Rectangle cvRect = getCanvasViewRectangle(baseImage);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (pCvRect == null) {
            // 以前の値が保存されてなければ画面中央に
            cvRect.x = (screenSize.width - cvRect.width) / 2;
            cvRect.y = (screenSize.height - cvRect.height) / 2;
        } else {
            cvRect.x = pCvRect.x;
            cvRect.y = pCvRect.y;

            // はみ出ていたり変な値だったら適当に修正
            if (cvRect.x < 0) cvRect.x = 0;
            else if (cvRect.x > screenSize.width - cvRect.width - 100) cvRect.x = screenSize.width - cvRect.width -100;
            if (cvRect.y < 24) cvRect.y = 24;
            else if (cvRect.y > screenSize.height - cvRect.height) cvRect.y = screenSize.height - cvRect.height;
        }
        cv.setBounds(cvRect);
        cv.getCanvasPanel().revalidate(); // 小さく cut 後に zoom したとき対応

        Rectangle tvRect = tv.getBounds();
        tvRect.y = cvRect.y;

        if (pTvRect == null) {
            // プロパティー値が保存されていない場合，左にくっつける
            tvRect.x = cvRect.x - tvRect.width - 2;            
        } else {
            // 以前の値が右にくっついている場合はくっつくように調整
            if (pTvRect.x == pCvRect.x + pCvRect.width +2) {
                tvRect.x = cvRect.x + cvRect.width + 2;
            // それ以外は以前の値をそのまま設定
            } else {
                tvRect.x = pTvRect.x;
            }
        }
        tv.setBounds(tvRect);
    }
    /**
     * baseImage から必要な view の大きさを計算する（試行錯誤で見つけ出した値）
     * @param srcImage
     * @return
     */
    public Rectangle getCanvasViewRectangle(BufferedImage baseImage) {

        // width は最低 550 (ボタン配置などで 500 + 絵の margin 25x2)
        int width = baseImage.getWidth();
        width = (width > 500)? width: 500;
        width += 50;

        // height は最低 284 (ボタン，タイトルバーなどで 144 + margin 120)
        int height = baseImage.getHeight();
        height = (height > 144)? height: 144;
        height += 120;

        return new Rectangle(0, 0, width, height);
    }

}
