/*
 * ReflectMonitor.java
 * Copyright (C) 2007 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package open.dolphin.util;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

/**
 * Reflection を使用してメソッドを実行し、ProgressMonitor を表示するクラス。
 *
 * @author Kazushi Minagawa
 */
public class ReflectMonitor implements ActionListener {
    
    public static final String STATE_PROP = "resultProp";
    
    public static final int DONE        = 0;
    public static final int TIME_OVER   = 1;
    public static final int CANCELED    = 2;
    
    public static final int DEFAULT_DELAY   = 200;
    public static final int DEFAULT_MAX     = 30 * 1000;
    public static final String DEFAULT_NOTE = "検索しています...  ";
    public static final int DEFAULT_DECIDE  = 200;
    public static final int DEFAULT_MILLIS  = 600;
    
    /** ターゲットオブジェクト */
    private Object target;
    
    /** 実行するメソッド名 */
    private String method;
    
    /** メソッド引数の Class 配列 */
    private Class[] argClasses;
    
    /** メソッドの引数 */
    private Object[] args;
    
    /** メソッドの戻り値 */
    private Object result;
    
    /** ProgressMonitor Component */
    private Component cmp;
    
    /** ProgressMonitor Message */
    private Object message;
    
    /** ProgressMonitor Note */
    private String note = DEFAULT_NOTE;
    
    /** Popup 判定時間 msec*/
    private int decideToPopup = DEFAULT_DECIDE;
    
    /** Popup する残りの時間 msec*/
    private int millisToPopup = DEFAULT_MILLIS;
    
    /** タイマーの遅延時間 msec*/
    private int delay = DEFAULT_DELAY;
    
    /** メソッド実行に要する見積もり時間 */
    private int maxEstimation = DEFAULT_MAX;
    
    /** 実行の終了状態を通知する束縛サポート */
    private PropertyChangeSupport boundSupport;
    
    /** 実行の終了状態プロパティ */
    private int state = -1;
    
    /** 実行が途中でキャンセルできるかどうかのフラグ */
    private boolean cancelOk = true;
    
    /** タイムアウトするかどうかのフラグ */
    private boolean timeoutOk = true;
    
    /** メソッドを実行するスレッド */
    private Thread exec;
    
    /** メソッドが終了しているかどうかのフラグ */
    private boolean done;
    
    // ProgressMonitor 関連
    private int min;
    private int max;
    private int current;
    private ProgressMonitor progress;
    
    /** 割り込みタイマー */
    private javax.swing.Timer timer;
    
    
    /** 
     * Creates a new instance of ReflectMonitor. 
     */
    public ReflectMonitor() {
        boundSupport = new PropertyChangeSupport(this);
    }
    
    /** 
     * Creates a new instance of ReflectMonitor.
     * @param target Reflection の対象オブジェクト
     * @param method 実行するメソッド
     * @param argClasses メソッド引数の Class 配列
     * @param args メソッドの引数
     */
    public ReflectMonitor(Object target, String method, Class[] argClasses, Object[] args) {
        this();
        setReflection(target, method, argClasses, args);
    }
    
    /**
     * 結果プロパティの束縛リスナを追加する。
     * @param l 追加する束縛リスナ
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(STATE_PROP, l);
    }
    
    /**
     * 結果プロパティの束縛リスナを削除する。
     * @param l 削除する束縛リスナ
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(STATE_PROP, l);
    }
    
    /**
     * メソッドの実行結果を返す。
     * @return メソッドの実行結果オブジェクト
     */
    public Object getResult() {
        return result;
    }
    
    /**
     * 結果 Status を返す。
     * @return 結果Status
     */
    public int getState() {
        return state;
    }
    
    /**
     * 結果 Status を設定する。
     * @param result 結果Status
     */
    public void setState(int result) {
        int old = this.state;
        this.state = result;
        boundSupport.firePropertyChange(STATE_PROP, old, this.state);
    }
    
    /**
     * Reflection のパラメータを設定する。
     * @param target Reflection の対象オブジェクト
     * @param method 実行するメソッド
     * @param argClasses メソッド引数の Class 配列
     * @param args メソッドの引数
     */
    public void setReflection(Object target, String method, Class[] argClasses, Object[] args) {
        this.target = target;
        this.method = method;
        this.argClasses = argClasses;
        this.args = args;
    }
    
    /**
     * ProgressMonitor のパラメータを設定する。
     * @param cmp ProgressMonitor の Component
     * @param message ProgressMonitor の Message
     * @param note ProgressMonitor の note
     * @param delay 遅延時間 msec
     * @param maxEstimation 見積もり時間 msec
     */
    public void setMonitor(Component cmp, Object message, String note, int delay, int maxEstimation) {
        this.cmp = cmp;
        this.message = message;
        this.note = note;
        setDelay(delay);
        setMaxEstimation(maxEstimation);
    }
    
    /**
     * 遅延時間を返す。
     * @return 遅延時間
     */
    public int getDelay() {
        return delay;
    }
    
    /**
     * 遅延時間を設定する。
     * @param delay 遅延時間
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }
    
    /**
     * 見積もり時間を返す。
     * @return 見積もり時間
     */
    public int getMaxEstimation() {
        return maxEstimation;
    }
    
    /**
     * 見積もり時間を設定する。
     * @param maxEstimation 見積もり時間
     */
    public void setMaxEstimation(int maxEstimation) {
        this.maxEstimation = maxEstimation;
    }
    
    public int getDecideToPopup() {
        return decideToPopup;
    }
    
    public void setDecideToPopup(int decideToPopup) {
        this.decideToPopup = decideToPopup;
    }
        
    public int getMillisToPopup() {
        return millisToPopup;
    }
    
    public void setMillisToPopup(int millisToPopup) {
        this.millisToPopup = millisToPopup;
    }
    
    /**
     * キャンセル可能かどうかを返す。
     * @return キャンセル可能な時 true
     */
    public boolean isCancelOk() {
        return cancelOk;
    }
    
    /**
     * キャンセル可能かどうかを設定する。
     * @param ok キャンセル可能な時 true
     */
    public void setCancelOk(boolean ok) {
        cancelOk = ok;
    }
    
    /**
     * タイムアウトするかどうかを返す。
     * @return タイムアウトする時 true
     */
    public boolean isTimeoutOk() {
        return timeoutOk;
    }
    
    /**
     * タイムアウトするかどうかを設定する。
     * @param タイムアウトする時 true
     */
    public void setTimeoutOk(boolean timeout) {
        this.timeoutOk = timeoutOk;
    }
    
    /**
     * Reflection のメソッドが終了したかどうかを返す。
     * @return 終了している時 true
     */
    public boolean isDone() {
        return done;
    }
    
    /**
     * Reflection のメソッドが終了を設定する。
     * @param 終了した時 true
     */
    public void setDone(boolean done) {
        this.done = done;
    }
    
    /**
     * メソッドの実行を開始する。
     */
    public void start() {
        
        if (target == null || method == null) {
            throw new RuntimeException("Reflection オブジェクト又はメソッドが設定されていません。");
        }
        
        // 最小値と最大値を設定する
        min = 0;
        max = getMaxEstimation() / getDelay();
        
        // ProgressMonitor を生成する
        progress = new ProgressMonitor(cmp, message, note, min, max);
        progress.setMillisToDecideToPopup(getDecideToPopup());
        progress.setMillisToPopup(getMillisToPopup());
        
        // タイマーを生成する
        timer = new Timer(getDelay(), this);
        
        //
        // Thread を生成しメソッドを実行する
        //
        Runnable r = new Runnable() {
            public void run() {
                try {
                    Method mth = target.getClass().getMethod(method, argClasses);
                    result = mth.invoke(target, args);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                setDone(true);
            }
        };
        exec = new Thread(r);
        exec.setPriority(Thread.NORM_PRIORITY);
        exec.start();
        timer.start();
    }
    
    /**
     * 終了処理を行う。
     * タイマー及びモニタをクローズする。
     */
    private void stop() {
        
        if (progress != null) {
            progress.close();
        }
        
        if (timer != null) {
            timer.stop();
        }
    }
    
    /**
     * 実行スレッドに割り込む。
     */
    private void interrupt() {
        if (exec != null) {
            exec.interrupt();
        }
    }
    
    /**
     * 進捗状況を管理する。
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        
        current++;
        
        if (progress.isCanceled()) {
            if (isCancelOk()) {
                interrupt();
                stop();
                setState(CANCELED);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
            
        } else if (isDone()) {
            stop();
            setState(DONE);
            
        } else if (current > max && isTimeoutOk()) {
            interrupt();
            stop();
            setState(TIME_OVER);
            
        } else {
            progress.setProgress(current);
        }
    }
}
