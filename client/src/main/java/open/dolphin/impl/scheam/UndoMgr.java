package open.dolphin.impl.scheam;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import open.dolphin.impl.scheam.holder.DrawingHolder;

/**
 *
 * @author pns
 */
public class UndoMgr {

    /** shape の描画・削除操作 */
    public static final int DRAW = 1;
    /** canvas の回転操作 */
    public static final int ROTATE = 2;
    /** DrawingHolder の移動操作 */
    public static final int MOVE = 3;
    /** DrawingHolder の拡大縮小 */
    public static final int EXPAND = 4;
    /** DrawingHolder の切り抜き */
    public static final int CLIP = 5;

    private SchemaEditorImpl context;
    private Deque<Model> undoQueue;
    private Deque<Model> redoQueue;

    public UndoMgr(SchemaEditorImpl context) {
        this.context = context;
        undoQueue = new LinkedList<Model>();
        redoQueue = new LinkedList<Model>();
    }

    /**
     * Draw 系の操作をした場合はここを呼ぶ
     */
    public void storeDraw() {
        Model model = new Model(DRAW);
        undoQueue.offerLast(model);
        // undo のあと新たに編集したら，それまでの redo は忘れる
        // 忘れないようにコーディングするのは難しくてできなかった
        redoQueue.clear();
        controlBtn();
    }

    /**
     * Rotate 系の操作をした場合はここを呼ぶ
     * @param theta
     */
    public void storeRotate(double theta) {
        Model model = new Model(ROTATE);
        model.setBaseImage();
        model.setTheta(theta);
        undoQueue.offerLast(model);
        redoQueue.clear();
        controlBtn();
    }

    /**
     * Move 系の操作をした場合はここを呼ぶ
     * @param holder
     * @param x
     * @param y
     */
    public void storeMove(DrawingHolder holder, double x, double y) {
        Model model = new Model(MOVE);
        model.setHolder(holder);
        model.setMove(x, y);
        undoQueue.offerLast(model);
        redoQueue.clear();
        controlBtn();
    }

    /**
     * 拡大縮小系の操作をした場合はここを呼ぶ
     * @param ratio
     */
    public void storeExpand(double ratio) {
        Model model = new Model(EXPAND);
        model.setBaseImage();
        model.setScale(ratio);
        undoQueue.offerLast(model);
        redoQueue.clear();
        controlBtn();
    }

    /**
     * 切り抜きの操作をした場合はここを呼ぶ
     * @param x
     * @param y
     */
    public void storeClipping(double x, double y) {
        Model model = new Model(CLIP);
        model.setBaseImage();
        model.setMove(x, y);
        undoQueue.offerLast(model);
        redoQueue.clear();
        controlBtn();
    }

    public void undo() {
        if (! undoQueue.isEmpty()) restore(undoQueue, redoQueue);
        controlBtn();
    }

    public void redo() {
        if (! redoQueue.isEmpty()) restore(redoQueue, undoQueue);
        controlBtn();
    }

    /**
     * Undo/Redo ボタンの　enable/disable 制御
     */
    private void controlBtn() {
        JButton undoButton = context.getToolView().getUndoBtn();
        if (undoQueue.isEmpty()) undoButton.setEnabled(false);
        else undoButton.setEnabled(true);
        JButton redoButton = context.getToolView().getRedoBtn();
        if (redoQueue.isEmpty()) redoButton.setEnabled(false);
        else redoButton.setEnabled(true);
    }

    /**
     * recover queue から model をとりだして復活させる
     * present model は store queue に入れる
     * @param recover
     * @param store
     */
    private void restore(Deque<Model> recover, Deque<Model> store) {
        Model model = recover.pollLast();
        Model present;

        switch(model.getCode()) {
            case DRAW:
                present = new Model(DRAW);
                store.offerLast(present);
                restoreDraw(model);
                break;

            case ROTATE:
                present = new Model(ROTATE);
                // present には戻したのをまた戻す情報をセットするので，theta は符号を変える
                present.setTheta( - model.getTheta());
                present.setBaseImage();
                store.offerLast(present);
                restoreRotate(model);
                break;

            case MOVE:
                present = new Model(MOVE);
                present.setMove( - model.getMove().x,  - model.getMove().y);
                present.setHolder(model.getHolder());
                store.offerLast(present);
                restoreMove(model);
                break;
                
            case EXPAND:
                present = new Model(EXPAND);
                present.setScale( 1/model.getScale());
                present.setBaseImage();
                store.offerLast(present);
                restoreExpand(model);
                break;

            case CLIP:
                present = new Model(CLIP);
                present.setMove( - model.getMove().x,  - model.getMove().y);
                present.setBaseImage();
                store.offerLast(present);
                restoreClipping(model);
                break;
        }
    }

    /**
     * Draw 系の操作のリストア
     * @param model
     */
    private void restoreDraw(Model model) {
        List<DrawingHolder> list = context.getDrawingList();
        list.clear();
        for (DrawingHolder h : model.getDrawingList()) list.add(h);
    }

    /**
     * Rotate 系の操作のリストア
     * @param model
     */
    private void restoreRotate(Model model) {
        List<DrawingHolder> list = context.getDrawingList();
        // 戻すので theta は符号を変える
        double theta = - model.getTheta();
        BufferedImage baseImage = model.getBaseImage();

        //DrawingHolder の回転
        for (DrawingHolder h : list) {
            if (theta > 0) {
                // 原点中心に 90度右回転 → 右方向に幅の分移動
                h.rotate(Math.PI/2);
                h.translate(baseImage.getWidth(), 0);
            } else {
                // 原点中心に 90度左回転 → 下方向に高さ分移動
                h.rotate(-Math.PI/2);
                h.translate(0,baseImage.getHeight());
            }
        }
        context.getCanvas().setBaseImage(baseImage);
        context.recomputeViewBounds(baseImage);
    }

    /**
     * Move 系の操作のリストア
     * @param model
     */
    private void restoreMove(Model model) {
        List<DrawingHolder> list = context.getDrawingList();
        int hash = model.getHolder().hashCode();

        for (DrawingHolder h : list) {
            // Holder を hashCode で比較して，一致したものを undo する
            if (h.hashCode() == hash) {
                h.translate( - model.getMove().x, - model.getMove().y);
                break;
            }
        }
    }

    private void restoreExpand(Model model) {
        List<DrawingHolder> list = context.getDrawingList();
        BufferedImage baseImage = model.getBaseImage();
        double ratio = 1/model.getScale();

        for (DrawingHolder h : list) h.expand(ratio, ratio);

        context.getCanvas().setBaseImage(baseImage);
        context.recomputeViewBounds(baseImage);
    }

    private void restoreClipping(Model model) {
        List<DrawingHolder> list = context.getDrawingList();
        BufferedImage baseImage = model.getBaseImage();

        for (DrawingHolder h : list) h.translate( -model.getMove().x, -model.getMove().y);

        context.getCanvas().setBaseImage(baseImage);
        context.recomputeViewBounds(baseImage);
    }

    /**
     * undo 情報を入れておくための model
     * drawingList だけは必ず取っておく。その他は必要に応じてセット
     */
    private class Model {
        private int code;
        private List<DrawingHolder> drawingList;
        private BufferedImage baseImage;
        private double theta;
        private double x;
        private double y;
        private DrawingHolder holder;
        private double scale;

        public Model(int code) {
            this.code = code;
            drawingList = new ArrayList<DrawingHolder>();
            for (DrawingHolder h : context.getDrawingList()) drawingList.add(h);
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public List<DrawingHolder> getDrawingList() {
            return drawingList;
        }

        public void setBaseImage() {
            BufferedImage image = context.getCanvas().getBaseImage();
            baseImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            Graphics2D g = baseImage.createGraphics();
            g.drawImage(image, null, 0, 0);
            g.dispose();
        }

        public BufferedImage getBaseImage() {
            return baseImage;
        }

        public void setTheta(double theta) {
            this.theta = theta;
        }

        public double getTheta() {
            return theta;
        }

        public void setMove(double x, double y) {
            this.x = x; this.y = y;
        }

        public Point.Double getMove() {
            return new Point.Double(x,y);
        }

        public void setHolder(DrawingHolder holder) {
            this.holder = holder;
        }

        public DrawingHolder getHolder() {
            return holder;
        }

        public void setScale(double scale) {
            this.scale = scale;
        }

        public double getScale() {
            return scale;
        }
    }
}
