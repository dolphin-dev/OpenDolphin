package open.dolphin.client.impl;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import open.dolphin.client.*;

/**
 * LaboTestGraph
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc
 */
public class LaboTestGraph extends JPanel implements ComponentListener {

    private static final long serialVersionUID = 7472151201436824606L;
    public static final int TT_ABSOLUTE_GRAPH = 0;
    public static final int TT_RELATIVE_GRAPH = 1;
    static final String UPPER_LIMIT_STRING = "上限値";
    static final String LOWER_LIMIT_STRING = "下限値";
    static final float UPPER_LIMIT_VALUE = 1.0F;
    static final float LOWER_LIMIT_VALUE = 0.0F;
    static final float Y_SCALE_UNIT = 0.2F;
    static final String RELATIVE_VALUE_STRING = "相対値";
    static final int OFFSET_LEFT = 12;
    static final int OFFSET_RIGHT = 12;
    static final int OFFSET_TOP = 12;
    static final int OFFSET_BOTTOM = 20;
    static final float SCALE_FRAME_SPACE = 5.0F;
    static final float EXAMPLE_FRAME_SPACE = 50.0F;
    static final float EXAMPLE_SPACE = 30.0F;
    static final float DUMMY_VALUE = -9999.99F;

    // 検査データ
    String[] sampleTime; // 検体採取日
    String[] itemName; // テスト項目名
    String[] itemUnit; // 検査値の単位
    float[][] data; // 検査値
    float[] upperLimit; // 上限値
    float[] lowerLimit; // 下限値
    // String[][] normal; // 異常値フラグ
    float[] maxValue; // 検査値の最大値
    float[] minValue; // 検査値の最小値
    boolean validData; // データセットが有効化かどうかのフラグ

    // 描画領域
    float canvasWidth;
    float canvasHeight;
    int offsetTop;
    int offsetLeft;
    int offsetRight;
    int offsetBottom;
    float xMin;
    float xMax;
    float yMin;
    float yMax;
    float pixelsPerX;
    float pixelsPerY;

    // 描画用パラメータ
    Color background = new Color(245, 245, 245);
    float plotCircleRadius = 10.0F;
    Color[] plotColors = new Color[]{Color.red, Color.blue, Color.pink,
        Color.cyan, Color.orange, Color.green, Color.magenta, Color.yellow
    };
    boolean bValueCircle = true;
    boolean antiAliasing = true;
    boolean textAntiAliasing = false;
    Color scaleLineColor = Color.lightGray;
    float scaleLineWidth = 1.0F;
    Color scaleColor = Color.black;
    Font scaleFont = new Font("Dialig", Font.PLAIN, 12);
    float[] yScaleDashPattern = {2.0F, 2.0F};
    Color xTitleColor = Color.black;
    Font xTitleFont = new Font("Dialig", Font.PLAIN, 12);
    Color yTitleColor = Color.black;
    Font yTitleFont = new Font("Dialig", Font.PLAIN, 12);
    Font itemNameFont = new Font("Dialig", Font.PLAIN, 12);
    float valueLineWidth = 1.7F;
    Color upperLimitColor = new Color(255, 64, 183);
    Color normalAreaColor = new Color(127, 250, 107);
    Color lowerLimitColor = new Color(19, 100, 250);
    float valueAreaAlpha = 0.5F;

    // 相対表示の時 true、絶対表示の時 false;
    int mode = 1;
    private Object[] yScales;
    // paint
    boolean hasOffset;
    boolean hasPixelsPerValue;

    /** Creates a new instance of LaboTestGraph */
    public LaboTestGraph() {
        this.setBackground(background);
        this.addComponentListener(this);
    }

    public void componentHidden(java.awt.event.ComponentEvent componentEvent) {
    }

    public void componentMoved(java.awt.event.ComponentEvent componentEvent) {
    }

    public void componentResized(java.awt.event.ComponentEvent componentEvent) {
        int width = this.getWidth();
        int height = this.getHeight();
        System.out.println("( " + width + " , " + height + " )");
    }

    public void componentShown(java.awt.event.ComponentEvent componentEvent) {
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int newMode) {

        int old = mode;

        if (newMode == 0 || newMode == 1) {
            this.mode = newMode;
        }

        if (newMode != old) {
            hasPixelsPerValue = false;
            repaint();
        }
    }

    public void setTestValue(String[] sampleTime, ArrayList list, int mode) {

        if (data != null) {
            itemName = null;
            itemUnit = null;
            data = null;
            upperLimit = null;
            lowerLimit = null;
            // normal = null;
            maxValue = null;
            minValue = null;
            hasPixelsPerValue = false;
        }

        this.mode = mode;

        this.sampleTime = sampleTime;

        // サンプリング数
        int sampleCount = sampleTime.length;

        // テスト項目数
        int itemCount = list.size();

        // [項目数][サンプリング数] の配列を生成する
        itemName = new String[itemCount];
        itemUnit = new String[itemCount];
        data = new float[itemCount][sampleCount];
        upperLimit = new float[itemCount];
        lowerLimit = new float[itemCount];
        // normal = new String[itemCount][sampleCount];
        maxValue = new float[itemCount];
        minValue = new float[itemCount];

        float value = 0.0F;

        // 検査値の最大と最小値
        float maxUp = 0.0F;
        float maxVal = 0.0F;

        // 上限値の最大値
        float minVal = DUMMY_VALUE;

        // 下限値の最小値
        float minLow = DUMMY_VALUE;

        String strVal = null;

        // テスト項目数分繰り返す
        for (int i = 0; i < itemCount; i++) {

            ArrayList al = (ArrayList) list.get(i);
            int size = al.size();
            boolean hasName = false;
            boolean hasUnit = false;
            boolean firstValue = true;
            boolean firstUp = true;
            boolean firstLow = true;

            // サンプリング数繰り返す
            for (int j = 0; j < size; j++) {

                SimpleLaboTestItem test = (SimpleLaboTestItem) al.get(j);

                if (test == null) {
                    data[i][j] = DUMMY_VALUE;
                    continue;
                }

                try {
                    value = Float.parseFloat(test.getItemValue());
                    data[i][j] = value;

                    if (firstValue) {
                        maxVal = value;
                        minVal = value;
                        firstValue = false;

                    } else {
                        maxVal = Math.max(value, maxVal);
                        minVal = Math.min(value, minVal);
                    }

                } catch (Exception e) {
                    data[i][j] = DUMMY_VALUE;
                }

                try {
                    value = Float.parseFloat(test.getUp());

                    if (firstUp) {
                        maxUp = value;
                        firstUp = false;
                    } else {
                        maxUp = Math.max(value, maxUp);
                    }

                } catch (Exception e) {

                }

                try {
                    value = Float.parseFloat(test.getLow());

                    if (firstLow) {
                        minLow = value;
                        firstLow = false;
                    } else {
                        minLow = Math.min(value, minLow);
                    }

                } catch (Exception e) {

                }

                if (!hasName) {
                    strVal = test.getItemName();
                    if (strVal != null) {
                        itemName[i] = strVal;
                        hasName = true;
                    }
                }

                if (!hasUnit) {
                    strVal = test.getItemUnit();
                    if (strVal != null) {
                        itemUnit[i] = strVal;
                        hasUnit = true;
                    }
                }

            /*
             * strVal = test.getNormal(); if (strVal != null) { normal[i][j] =
             * strVal; } else { normal[i][j] = null; }
             */
            }

            // このテスト項目のそれぞれの値を格納する
            maxValue[i] = maxVal; // 検査値の最大値
            minValue[i] = minVal; // 検査値の最小値
            upperLimit[i] = maxUp; // 上限値の最大値
            lowerLimit[i] = minLow; // 下限値の最大値
        }

        // 検査データを表示させる
        repaint();
    }

    protected float getCanvasWidth() {
        return getWidth() - offsetLeft - offsetRight;
    }

    protected float getCanvasHeight() {
        return getHeight() - offsetTop - offsetBottom;
    }

    protected float getPixelsForSample(int j) {
        return ((float) j + 0.5F) * pixelsPerX;
    }

    protected float getPixelsForSampleBound(int j) {
        return (float) j * pixelsPerX;
    }

    protected float getPixelsForSample(float j) {
        return j * pixelsPerX;
    }

    protected float getPixelsForValue(float value) {
        return canvasHeight - (value - yMin) * pixelsPerY;
    }

    protected Rectangle2D getGraphRect() {
        // left, top, width, heifgt
        float left = getPixelsForSample(xMin);
        float top = getPixelsForValue(yMax);
        float right = getPixelsForSample(xMax);
        float bottom = getPixelsForValue(yMin);

        return new Rectangle2D.Float(left, top, right - left, bottom - top);
    }

    protected Rectangle2D getUpperLimitRect() {

        float left = getPixelsForSample(xMin);
        float top = getPixelsForValue(yMax);
        float right = getPixelsForSample(xMax);
        float bottom = getPixelsForValue(UPPER_LIMIT_VALUE);

        return new Rectangle2D.Float(left, top, right - left, bottom - top);
    }

    protected Rectangle2D getLowerLimitRect() {

        float left = getPixelsForSample(xMin);
        float top = getPixelsForValue(LOWER_LIMIT_VALUE);
        float right = getPixelsForSample(xMax);
        float bottom = getPixelsForValue(yMin);

        return new Rectangle2D.Float(left, top, right - left, bottom - top);
    }

    protected Rectangle2D getNormalValueRect() {

        float left = getPixelsForSample(xMin);
        float top = getPixelsForValue(UPPER_LIMIT_VALUE);
        float right = getPixelsForSample(xMax);
        float bottom = getPixelsForValue(LOWER_LIMIT_VALUE);

        return new Rectangle2D.Float(left, top, right - left, bottom - top);
    }

    /**
     * プロット用のサークル 2D オブジェクトを返す。
     */
    protected Ellipse2D getPlotCircle2D(Point2D point) {
        return new Ellipse2D.Float((float) point.getX() - plotCircleRadius / 2.0F, (float) point.getY() - plotCircleRadius / 2.0F,
                plotCircleRadius, plotCircleRadius);
    }

    protected Color getPlotColor(int index) {

        Color color = null;

        color = (index >= plotColors.length)
                ? plotColors[(index % plotColors.length)]
                : plotColors[index];

        return color;
    }

    protected void drawData(Graphics2D g2) {

        Color color = null;

        g2.setFont(scaleFont);
        FontMetrics fm = g2.getFontMetrics(scaleFont);
        BasicStroke lineStroke = new BasicStroke(valueLineWidth);
        BasicStroke dashStroke = new BasicStroke(valueLineWidth,
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0F,
                yScaleDashPattern, 0);

        for (int i = 0; i < data.length; i++) {

            color = getPlotColor(i);

            g2.setColor(color);
            g2.setPaint(color);

            if (mode == TT_ABSOLUTE_GRAPH) {
                plotValue(g2, fm, lineStroke, dashStroke, i, bValueCircle);

            } else {
                plotRelativeValue(g2, fm, lineStroke, dashStroke, i,
                        bValueCircle);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (data == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        Object aliasing = antiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
        Object textAl = textAntiAliasing ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aliasing);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, textAl);

        if (mode == TT_ABSOLUTE_GRAPH) {
            setupWorld(g2);
            drawCoordinate(g2);
            drawData(g2);

        } else {
            setupRelativeWorld(g2);
            drawRelativeCoordinate(g2);
            drawData(g2);
        }
    }

    // ---------------------------------------------------------------------------------------------
    protected void setupRelativeWorld(Graphics2D g2) {

        if (!hasOffset) {

            // 凡例の最大の長さを求める
            g2.setFont(scaleFont);
            FontMetrics fm = g2.getFontMetrics(scaleFont);

            int maxWidth = fm.stringWidth(itemName[0]);
            for (int i = 1; i < itemName.length; i++) {
                maxWidth = Math.max(fm.stringWidth(itemName[i]), maxWidth);
            }

            // オフセットを求める
            offsetLeft = maxWidth + OFFSET_LEFT + (int) EXAMPLE_FRAME_SPACE;
            // offsetRight = OFFSET_RIGHT + fm.stringWidth(UPPER_LIMIT_STRING) +
            // 2;
            offsetRight = OFFSET_RIGHT;
            offsetTop = OFFSET_TOP;
            offsetBottom = OFFSET_BOTTOM;

            hasOffset = true;
        }

        // if (! hasPixelsPerValue) {

        xMin = 0.0F;
        xMax = (float) sampleTime.length;

        yMax = 0.0F;
        yMin = 0.0F;

        for (int i = 0; i < data.length; i++) {

            float maxVal = (maxValue[i] - lowerLimit[i]) / (upperLimit[i] - lowerLimit[i]);
            float minVal = (minValue[i] - lowerLimit[i]) / (upperLimit[i] - lowerLimit[i]);

            yMax = i == 0 ? maxVal : Math.max(maxVal, yMax);
            yMin = i == 0 ? minVal : Math.min(minVal, yMin);
        }

        yMax = yMax > UPPER_LIMIT_VALUE ? yMax : UPPER_LIMIT_VALUE;
        yMin = yMin < LOWER_LIMIT_VALUE ? yMin : LOWER_LIMIT_VALUE;

        int plusCountount = 1;
        while (Y_SCALE_UNIT * (float) plusCountount <= yMax) {
            plusCountount++;
        }

        int minusCountount = 1;
        while (-Y_SCALE_UNIT * (float) minusCountount > yMin) {
            minusCountount++;
        }

        yMax = Y_SCALE_UNIT * (float) plusCountount;
        yMin = -Y_SCALE_UNIT * (float) minusCountount;

        hasPixelsPerValue = true;
        // }

        // pixelsPerValue を設定する
        canvasWidth = getCanvasWidth();
        canvasHeight = getCanvasHeight();
        pixelsPerX = canvasWidth / (xMax - xMin);
        pixelsPerY = canvasHeight / (yMax - yMin);

        // オフセット分移動する
        g2.translate(offsetLeft, offsetTop);
    }

    protected void drawRelativeCoordinate(Graphics2D g2) {

        // 現在のフォントを保存する
        // Font oldFont = g2.getFont();

        // スケールの描画色を得る
        g2.setColor(scaleLineColor);
        g2.setStroke(new BasicStroke(scaleLineWidth));

        // 枠で囲む
        g2.draw(getGraphRect());

        // 目盛り線用のフォントとカラー設定
        g2.setFont(scaleFont);
        FontMetrics fm = g2.getFontMetrics(scaleFont);
        g2.setColor(scaleColor);

        // Plot 用の変数
        float xp = 0.0F;
        float yp = 0.0F;
        String str = null;

        // X 軸(サンプル日)の境界を書く
        g2.setStroke(new BasicStroke(scaleLineWidth, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0F, yScaleDashPattern, 0));
        for (int j = 1; j < sampleTime.length; j++) {

            xp = getPixelsForSampleBound(j);
            g2.draw(new Line2D.Float(new Point2D.Float(xp, 0.0F),
                    new Point2D.Float(xp, canvasHeight)));
        }

        // X 軸(サンプル日)を書く
        for (int j = 0; j < sampleTime.length; j++) {

            xp = getPixelsForSample(j);

            str = sampleTime[j];
            xp = xp - fm.stringWidth(str) / 2.0F;
            yp = canvasHeight + fm.getAscent() + fm.getDescent();
            g2.drawString(str, xp, yp);
        }

        // X 軸のタイトル(検体採集日) を書く
        // g2.setFont(xTitleFont);
        // g2.setColor(xTitleColor);
        // fm = g2.getFontMetrics(xTitleFont);
        // str = "検体採取日";
        // xp = ( getPixelsForSample(xMax) - getPixelsForSample(xMin) -
        // fm.stringWidth(str) ) / 2;
        // yp = getPixelsForValue(yMin) + 2*(fm.getAscent() + fm.getDescent());
        // g2.drawString(str, xp, yp);

        // Y 軸のスケールを入れる
        // float[] dashPattern = {2.0F,2.0F};
        // g2.setStroke(new BasicStroke(scaleLineWidth,
        // BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0F,yScaleDashPattern,0));

        float start = yMin + Y_SCALE_UNIT;

        while (start < yMax) {

            xp = getPixelsForSample(xMin);
            yp = getPixelsForValue(start);
            Point2D from = new Point2D.Float(xp, yp);

            xp = getPixelsForSample(xMax);
            Point2D to = new Point2D.Float(xp, yp);

            g2.draw(new Line2D.Float(from, to));

            start = start + Y_SCALE_UNIT;
        }

        // 上限値
        str = UPPER_LIMIT_STRING;
        xp = getPixelsForSample(xMax) - fm.stringWidth(str) - SCALE_FRAME_SPACE;
        yp = getPixelsForValue(UPPER_LIMIT_VALUE) - fm.getDescent();
        g2.drawString(str, xp, yp);

        // 下限値
        str = LOWER_LIMIT_STRING;
        xp = getPixelsForSample(xMax) - fm.stringWidth(str) - SCALE_FRAME_SPACE;
        yp = getPixelsForValue(LOWER_LIMIT_VALUE) + fm.getAscent(); // fm.getDescent();
        g2.drawString(str, xp, yp);

        // max
        str = String.valueOf(yMax);
        xp = getPixelsForSample(xMin) - fm.stringWidth(str) - SCALE_FRAME_SPACE;
        yp = getPixelsForValue(yMax) + fm.getDescent();
        g2.drawString(str, xp, yp);

        // 相対値
        str = RELATIVE_VALUE_STRING;
        xp = xp - fm.stringWidth(str) - SCALE_FRAME_SPACE;
        // yp = getPixelsForValue(yMax) + fm.getDescent();
        g2.drawString(str, xp, yp);

        // 1.0
        str = String.valueOf(UPPER_LIMIT_VALUE);
        xp = getPixelsForSample(xMin) - fm.stringWidth(str) - SCALE_FRAME_SPACE;
        yp = getPixelsForValue(UPPER_LIMIT_VALUE) + fm.getDescent();
        g2.drawString(str, xp, yp);

        // 0.0
        str = String.valueOf(LOWER_LIMIT_VALUE);
        xp = getPixelsForSample(xMin) - fm.stringWidth(str) - SCALE_FRAME_SPACE;
        yp = getPixelsForValue(LOWER_LIMIT_VALUE) + fm.getDescent();
        g2.drawString(str, xp, yp);

        // min
        str = String.valueOf(yMin);
        xp = getPixelsForSample(xMin) - fm.stringWidth(str) - SCALE_FRAME_SPACE;
        yp = getPixelsForValue(yMin) + fm.getDescent();
        g2.drawString(str, xp, yp);

        // 上限値エリアを塗りつぶす
        Rectangle2D rect = getUpperLimitRect();
        g2.setPaint(upperLimitColor);
        int rule = AlphaComposite.SRC_OVER;
        g2.setComposite(AlphaComposite.getInstance(rule, valueAreaAlpha));
        g2.fill(rect);

        // 正常値エリアを塗りつぶす
        rect = getNormalValueRect();
        g2.setPaint(normalAreaColor);
        g2.fill(rect);

        // 上限値エリアを塗りつぶす
        rect = getLowerLimitRect();
        g2.setPaint(lowerLimitColor);
        g2.fill(rect);

    }

    protected void plotRelativeValue(Graphics2D g2, FontMetrics fm,
            BasicStroke lineStroke, BasicStroke dashStroke, int index,
            boolean circle) {

        int sampleCount = sampleTime.length;

        float up = upperLimit[index];
        float low = lowerLimit[index];

        // 有効な上限及び下限値でない時はグラフ表示しない
        if (up == DUMMY_VALUE || low == DUMMY_VALUE) {
            return;
        }

        // 上限値 - 下限値 で規格かする
        float scale = up - low;

        // ループ中で使用する変数
        float x = 0.0F;
        float y = 0.0F;
        Point2D.Float fromP = null;
        Point2D.Float toP = null;
        boolean firstValue = true;

        // 凡例を書く
        String str = itemName[index];
        float xe = getPixelsForSample(xMin) - fm.stringWidth(str) - EXAMPLE_FRAME_SPACE;
        float space = fm.getAscent() + fm.getDescent() + fm.getLeading();
        float ye = getPixelsForValue(UPPER_LIMIT_VALUE) + (float) (index) * space;
        // float ye = getPixelsForValue(UPPER_LIMIT_VALUE) + (float)(index) *
        // EXAMPLE_SPACE;
        g2.drawString(str, xe, ye);
        xe = xe + fm.stringWidth(str);

        // サンプル数 -1 だけ繰り返す
        for (int j = 0; j < sampleCount - 1; j++) {

            // 対象の点が null でない時
            if (data[index][j] != DUMMY_VALUE) {

                x = getPixelsForSample(j);
                y = (data[index][j] - low) / scale;
                y = getPixelsForValue(y);

                // 凡例と最初の値を点線で結ぶ
                if (firstValue) {
                    fromP = new Point2D.Float(xe, ye);
                    toP = new Point2D.Float(x, y);

                    g2.setStroke(dashStroke);
                    g2.draw(new Line2D.Float(fromP, toP));
                    g2.setStroke(lineStroke);
                    firstValue = false;
                }

                // プロットの始点
                fromP = new Point2D.Float(x, y);

                if (circle) {
                    g2.fill(getPlotCircle2D(fromP));
                }

                // プロットの終点も nul でない時、線で結ぶ
                if (data[index][j + 1] != DUMMY_VALUE) {
                    x = getPixelsForSample(j + 1);
                    y = (data[index][j + 1] - low) / scale;
                    y = getPixelsForValue(y);
                    toP = new Point2D.Float(x, y);

                    g2.draw(new Line2D.Float(fromP, toP));

                // 次の点が null の時はプロットのみ
                } else {

                    if (circle) {
                        g2.fill(getPlotCircle2D(fromP));
                    }
                }
            }
        }

        // 最後のサンプル点の処理
        if (data[index][sampleCount - 1] != DUMMY_VALUE && circle) {
            x = getPixelsForSample(sampleCount - 1);
            y = (data[index][sampleCount - 1] - low) / scale;
            y = getPixelsForValue(y);
            fromP = new Point2D.Float(x, y);
            g2.fill(getPlotCircle2D(fromP));
        }
    }

    // ------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    protected Object[] getScale(float max, float min) {

        float value = max - min;

        double[] base = new double[]{1.0D, 2.0D, 5.0D, 10.0D};

        int keta = getFigureNumber(max - min);
        double k = Math.pow(10.0D, (double) (keta - 2));

        double nearestToTen = 0.0D;
        double val = 0.0D;
        int index = 0;

        for (int i = 0; i < base.length; i++) {

            val = (double) value / (k * base[i]);

            if (i == 0) {
                nearestToTen = Math.abs(val - 10.0D);
                index = 0;

            } else {
                if (Math.abs(val - 10.0D) < nearestToTen) {
                    nearestToTen = Math.abs(val - 10.0D);
                    index = i;
                }
            }
        }

        // スケールの増分単位
        double unit = k * base[index];

        ArrayList ret = new ArrayList();
        double start = 0.0D;

        if (min < 0.0F) {
            // マイナス側へ min より小さくなるまで
            while (start > (double) min) {
                start = start - unit;
            }
        }

        while (start < (double) max) {
            ret.add(String.valueOf((float) start));
            start = start + unit;
        }
        ret.add(String.valueOf((float) start));
        return ret.toArray();
    }

    protected int getFigureNumber(float value) {

        if (value >= 1.0F) {
            int keta = 1;
            while (Math.pow(10.0D, keta) < (double) value) {
                keta++;
            }
            return keta;

        } else {
            int keta = 1;
            return keta;
        }
    }

    protected void setupWorld(Graphics2D g2) {

        if (!hasOffset) {
            // 凡例の最大の長さを求める
            g2.setFont(scaleFont);
            FontMetrics fm = g2.getFontMetrics(scaleFont);

            int maxWidth = fm.stringWidth(itemName[0]);
            for (int i = 1; i < itemName.length; i++) {
                maxWidth = Math.max(fm.stringWidth(itemName[i]), maxWidth);
            }

            // オフセットを求める
            offsetLeft = maxWidth + OFFSET_LEFT + (int) EXAMPLE_FRAME_SPACE;
            offsetRight = OFFSET_RIGHT; // + fm.stringWidth(UPPER_LIMIT_STRING)
            // + 2;
            offsetTop = OFFSET_TOP;
            offsetBottom = OFFSET_BOTTOM;

            hasOffset = true;
        }

        xMin = 0.0F;
        xMax = (float) sampleTime.length;

        // グラフの最大値 -> 全ての検査値及び上限値のなかで最大のもの
        yMax = 0.0F;

        // ラフの最小値 -> 全ての検査値及び下限値のなかで最小のもの
        yMin = 0.0F;

        // 全てのテスト項目をスキャンする
        for (int i = 0; i < data.length; i++) {

            float maxVal = maxValue[i];
            float minVal = minValue[i];

            maxVal = upperLimit[i] != DUMMY_VALUE ? Math.max(maxVal, upperLimit[i]) : maxVal;
            minVal = lowerLimit[i] != DUMMY_VALUE ? Math.min(minVal, lowerLimit[i]) : minVal;

            yMax = i == 0 ? maxVal : Math.max(maxVal, yMax);
            yMin = i == 0 ? minVal : Math.min(minVal, yMin);
        }

        // 最小値がゼロ以上の時はゼロをグラフの最小値にする
        yMin = yMin >= 0.0F ? 0.0F : yMin;

        // 実際の最大値及び最小値はスケールの最大値と最小値
        yScales = getScale(yMax, yMin);
        yMax = Float.parseFloat((String) yScales[yScales.length - 1]);
        yMin = Float.parseFloat((String) yScales[0]);

        hasPixelsPerValue = true;

        // pixelsPerValue を設定する
        canvasWidth = getCanvasWidth();
        canvasHeight = getCanvasHeight();
        pixelsPerX = canvasWidth / (xMax - xMin);
        pixelsPerY = canvasHeight / (yMax - yMin);

        // オフセット分移動する
        g2.translate(offsetLeft, offsetTop);
    }

    protected void drawCoordinate(Graphics2D g2) {

        // スケールの描画色を得る
        g2.setColor(scaleLineColor);
        g2.setStroke(new BasicStroke(scaleLineWidth));

        // 枠で囲む
        g2.draw(getGraphRect());

        // 目盛り線用のフォントとカラー設定
        g2.setFont(scaleFont);
        FontMetrics fm = g2.getFontMetrics(scaleFont);
        g2.setColor(scaleColor);

        // Plot 用の変数
        float xp = 0.0F;
        float yp = 0.0F;
        String str = null;

        // X 軸(サンプル日)の境界を書く
        g2.setStroke(new BasicStroke(scaleLineWidth, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0F, yScaleDashPattern, 0));
        for (int j = 1; j < sampleTime.length; j++) {

            xp = getPixelsForSampleBound(j);
            g2.draw(new Line2D.Float(new Point2D.Float(xp, 0.0F),
                    new Point2D.Float(xp, canvasHeight)));
        }

        // X 軸(サンプル日)を書く
        for (int j = 0; j < sampleTime.length; j++) {

            xp = getPixelsForSample(j);

            str = sampleTime[j];
            xp = xp - fm.stringWidth(str) / 2.0F;
            yp = canvasHeight + fm.getAscent() + fm.getDescent();
            g2.drawString(str, xp, yp);
        }

        for (int i = 0; i < yScales.length; i++) {
            // System.out.println((String)yScales[i]);

            yp = Float.parseFloat((String) yScales[i]);
            yp = getPixelsForValue(yp);
            g2.draw(new Line2D.Float(new Point2D.Float(0.0F, yp),
                    new Point2D.Float(canvasWidth, yp)));

            str = (String) yScales[i];
            xp = 0.0F - fm.stringWidth(str) - 2.0F;
            yp = yp + fm.getDescent();
            // y = (y + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(str, xp, yp);
        }
    }

    protected void plotValue(Graphics2D g2, FontMetrics fm,
            BasicStroke lineStroke, BasicStroke dashStroke, int index,
            boolean circle) {

        int sampleCount = sampleTime.length;

        // ループ中で使用する変数
        float x = 0.0F;
        float y = 0.0F;
        Point2D.Float fromP = null;
        Point2D.Float toP = null;
        boolean firstValue = true;

        // 凡例を書く
        String str = itemName[index];
        float xe = getPixelsForSample(xMin) - fm.stringWidth(str) - EXAMPLE_FRAME_SPACE;
        float space = fm.getAscent() + fm.getDescent() + fm.getLeading();
        float ye = getPixelsForValue(2.0F * yMax / 3.0F) + (float) (index) * space;
        // float ye = getPixelsForValue(2.0F * yMax / 3.0F) + (float)(index) *
        // EXAMPLE_SPACE;
        g2.drawString(str, xe, ye);
        xe = xe + fm.stringWidth(str);

        // サンプル数 -1 だけ繰り返す
        for (int j = 0; j < sampleCount - 1; j++) {

            // 対象の点が null でない時
            if (data[index][j] != DUMMY_VALUE) {

                x = getPixelsForSample(j);
                y = data[index][j];
                y = getPixelsForValue(y);

                // 凡例と最初の値を点線で結ぶ
                if (firstValue) {
                    fromP = new Point2D.Float(xe, ye);
                    toP = new Point2D.Float(x, y);

                    g2.setStroke(dashStroke);
                    g2.draw(new Line2D.Float(fromP, toP));
                    g2.setStroke(lineStroke);
                    firstValue = false;
                }

                // プロットの始点
                fromP = new Point2D.Float(x, y);

                if (circle) {
                    g2.fill(getPlotCircle2D(fromP));
                }

                // プロットの終点も nul でない時、線で結ぶ
                if (data[index][j + 1] != DUMMY_VALUE) {
                    x = getPixelsForSample(j + 1);
                    y = data[index][j + 1];
                    y = getPixelsForValue(y);
                    toP = new Point2D.Float(x, y);

                    g2.draw(new Line2D.Float(fromP, toP));

                // 次の点が null の時はプロットのみ
                } else {

                    if (circle) {
                        g2.fill(getPlotCircle2D(fromP));
                    }
                }
            }
        }

        // 最後のサンプル点の処理
        if (data[index][sampleCount - 1] != DUMMY_VALUE && circle) {
            x = getPixelsForSample(sampleCount - 1);
            y = data[index][sampleCount - 1];
            y = getPixelsForValue(y);
            fromP = new Point2D.Float(x, y);
            g2.fill(getPlotCircle2D(fromP));
        }

        // テスト項目数が１の時は上限・下限を表示する
        if (data.length == 1) {
            drawUpperLowerLine(g2, fm);
        }
    }

    private void drawUpperLowerLine(Graphics2D g2, FontMetrics fm) {

        float up = upperLimit[0];
        float low = lowerLimit[0];

        float xp = 0.0F;
        float yp = 0.0F;
        Point2D.Float from = null;
        Point2D.Float to = null;

        // 有効な上限値及び下限値がある場合のみ表示する
        if (up != DUMMY_VALUE) {

            xp = getPixelsForSample(xMin);
            yp = getPixelsForValue(up);
            from = new Point2D.Float(xp, yp);

            xp = getPixelsForSample(xMax);
            to = new Point2D.Float(xp, yp);

            g2.setColor(upperLimitColor);
            g2.draw(new Line2D.Float(from, to));
        }

        if (low != DUMMY_VALUE) {

            xp = getPixelsForSample(xMin);
            yp = getPixelsForValue(low);
            from = new Point2D.Float(xp, yp);

            xp = getPixelsForSample(xMax);
            to = new Point2D.Float(xp, yp);

            g2.setColor(lowerLimitColor);
            g2.draw(new Line2D.Float(from, to));
        }

        String unit = itemUnit[0];

        if (unit != null) {

            xp = getPixelsForSample(xMin) - fm.stringWidth(unit) - fm.stringWidth((String) yScales[yScales.length - 1]) - 5.0F;
            yp = getPixelsForValue(yMax) + fm.getDescent();

            g2.setColor(scaleColor);
            g2.drawString(unit, xp, yp);
        }
    }
}
