package open.dolphin.plugin.helper;

import java.lang.reflect.Method;

import swingworker.SwingWorker;

/**
 * CallBacksWorker
 *
 * @author Minagawa, Kazushi
 *
 */
public class CallBacksWorker {
    
    // コールバックオブジェクト
    private Object target;
    
    // コールバックメソッド名
    private String methodName;
    
    // クラス引数
    private Class[] argClasses;
    
    // オブジェクト引数
    private Object[] args;
    
    // メッセージ
    private String message;
    
    // 現在値
    private int current;
    
    // タスクが終了フラグ
    private boolean done;
    
    /**
     * CallBacksWorkerを生成する。
     * @param target      コールバックオブジェクト
     * @param methodName  コールバックメソッド名
     * @param argClasses  クラス引数
     * @param args        オブジェクト引数
     */
    public CallBacksWorker(Object target, String methodName,
            Class[] argClasses, Object[] args) {
        this.target = target;
        this.methodName = methodName;
        this.argClasses = argClasses;
        this.args = args;
    }
    
    public void start() {
        
        SwingWorker worker = new SwingWorker() {
            
            public Object construct() {
                return new CallBack();
            }
        };
        worker.start();
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getCurrent() {
        return ++current;
    }
    
    public boolean isDone() {
        return done;
    }
    
    class CallBack {
        
        CallBack() {
            
            try {
                Method method = target.getClass().getMethod(methodName, argClasses);
                method.invoke(target, args);
                done = true;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
