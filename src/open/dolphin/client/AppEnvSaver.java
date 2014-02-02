/*
 * AppEnvSaver.java
 *
 * Created on 2007/04/27, 20:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package open.dolphin.client;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import open.dolphin.plugin.ILongTask;
import open.dolphin.plugin.helper.AbstractLongTask;
import open.dolphin.plugin.helper.TaskManager;
import org.apache.log4j.Logger;

/**
 * アプリケーションの環境保存クラス。
 *
 * @author Kazushi Minagawa
 */
public class AppEnvSaver {
    
    /** 環境保存ステートプロパティ */
    public static final String SAVE_ENV_PROP = "saveEnvProp";
    
    /** 環境が正常に保存された */
    public static final int SAVE_DONE   = 1;
    
    /** 環境の保存に失敗した */
    public static final int SAVE_ERROR  = 2;
    
    /** 環境保存の条件が整っていない */
    public static final int NO_SAVE_CONDITION = 3;
    
    /** 環境保存ステート属性 */
    private int state;
    
    /** エラーを起こしているタスク */
    private ILongTask errorTask;
    
    /** 環境保存ステートの束縛サポート*/
    private PropertyChangeSupport boundSupport;
    
    // TimerTask
    private javax.swing.Timer taskTimer;
    
    /** Creates a new instance of AppEnvSaver */
    public AppEnvSaver() {
        boundSupport = new PropertyChangeSupport(this);
    }
    
    /**
     * 保存ステートを設定しリスナへ通知する。
     */
    public void setSaveEnvState(int state) {
        int old = this.state;
        this.state = state;
        boundSupport.firePropertyChange(SAVE_ENV_PROP, old, this.state);
    }
    
    public ILongTask getErrorTask() {
        return errorTask;
    }
    
    public void setErrorTask(ILongTask task) {
        errorTask = task;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(SAVE_ENV_PROP, l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(SAVE_ENV_PROP, l);
    }
    
    /**
     * 終了処理を行う。
     */
    public void save(Hashtable activeChildren) {
        
        final Logger bootLogger = ClientContext.getLogger("boot");
        
        // 未保存のカルテがある場合は警告しリターンする
        // カルテを保存または破棄してから再度実行する
        boolean dirty = false;
        
        // Chart を調べる
        ArrayList<ChartPlugin> allChart = ChartPlugin.getAllChart();
        if (allChart != null && allChart.size() > 0) {
            for (ChartPlugin chart : allChart) {
                if (chart.isDirty()) {
                    dirty = true;
                    break;
                }
            }
        }
        
        // 保存してないものがあればリターンする
        if (dirty) {
            alertDirty();
            setSaveEnvState(NO_SAVE_CONDITION);
            return;
        }
        
        // EditorFrameのチェックを行う
        java.util.List<IChart> allEditorFrames = EditorFrame.getAllEditorFrames();
        if (allEditorFrames != null && allEditorFrames.size() > 0) {
            for(IChart chart : allEditorFrames) {
                if (chart.isDirty()) {
                    dirty = true;
                    break;
                }
            }
        }
        
        if (dirty) {
            alertDirty();
            setSaveEnvState(NO_SAVE_CONDITION);
            return;
        }
        
        
        //
        // StoppingTask を集める
        //
        Vector<ILongTask> stoppingTasks = new Vector<ILongTask>();
        ILongTask task = null;
        
        try {
            Hashtable cloneMap = null;
            synchronized (activeChildren) {
                cloneMap = (Hashtable) activeChildren.clone();
            }
            Iterator iter = cloneMap.values().iterator();
            while (iter != null && iter.hasNext()) {
                IMainWindowPlugin pl = (IMainWindowPlugin) iter.next();
                task = pl.getStoppingTask();
                if (task != null) {
                    stoppingTasks.add(task);
                }
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            bootLogger.warn(ex.toString());
        }
        
        //
        // StoppingTask を一つのタイマ及び Progress Monitor で実行する
        // 全てのタスクが終了したらアプリケーションの終了処理に移る
        //
        int cnt = stoppingTasks.size();
        
        if (cnt == 0) {
            setSaveEnvState(SAVE_DONE);
            return; // Never come back
            
        } else {
            bootLogger.info(cnt + " 個の StoppingTask があります");
        }
        
        // 一括して実行するためのTaskManagerを生成する
        ILongTask[] longs = new AbstractLongTask[cnt];
        for (int i = 0; i < cnt; i++) {
            longs[i] = stoppingTasks.get(i);
        }
        final TaskManager taskMgr = new TaskManager(longs);
        
        // Progress Monitor を生成する
        String exittingNote = ClientContext.getString("mainWindow.progressNote.exitting");
        final ProgressMonitor monitor = new ProgressMonitor(null, null, exittingNote, 0, taskMgr.getLength());
        
        // 実行 Timer を生成する
        taskTimer = new javax.swing.Timer(taskMgr.getDelay(), new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                if (taskMgr.isDone()) {
                    
                    // 終了処理を行う
                    taskTimer.stop();
                    monitor.close();
                    
                    //
                    // 実行結果を得る
                    //
                    if (!taskMgr.getResult()) {
                        //
                        // 保存処理がエラーの時
                        //
                        setErrorTask(taskMgr.getCurTask());
                        bootLogger.warn("StoppingTask にエラーがあります");
                        setSaveEnvState(SAVE_ERROR);
                        
                    } else {
                        //
                        // エラーなし
                        //
                        bootLogger.info("StoppingTask が終了しました");
                        setSaveEnvState(SAVE_DONE);
                    }
                    
                } else {
                    // 現在値を更新する
                    monitor.setProgress(taskMgr.getCurrent());
                }
            }
        });
        taskMgr.start();
        taskTimer.start();
    }
    
    /**
     * 未保存のドキュメントがある場合の警告を表示する。
     */
    private void alertDirty() {
        String msg0 = "未保存のドキュメントがあります。";
        String msg1 = "保存または破棄した後に再度実行してください。";
        String taskTitle = ClientContext.getString("mainWindow.exit.taskTitle");
        JOptionPane.showMessageDialog(
                        (Component) null,
                        new Object[]{msg0, msg1},
                        ClientContext.getFrameTitle(taskTitle),
                        JOptionPane.INFORMATION_MESSAGE
                        );
    }
}
