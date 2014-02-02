package open.dolphin.client;

import swingworker.SwingWorker;

/**
 * AbstractInfiniteTask
 *
 * @author Minagawa,Kazushi
 *
 */
public abstract class AbstractInfiniteTask {
	
	protected int taskLength;
    
    protected int current;
    
    protected String message;
    
    protected boolean done;
    
    public AbstractInfiniteTask() {
    }
    
    
    public void start() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new ActualTask();
            }
        };
        worker.start();
    }
    
    public int getTaskLength() {
        return taskLength;
    }
    
    public void setTaskLength(int taskLength) {
        this.taskLength = taskLength;
    }
    
    public int getCurrent() {
        return current++;
    }
    
    protected void setCurrent(int current) {
        this.current = current;
    }
    
    public boolean isDone() {
        return done;
    }
    
    protected void setDone(boolean done) {
        this.done = done;
    }
    
    public boolean isTimeOver() {
        return current < taskLength ? false : true;
    }
    
    public String getMessage() {
        return message;
    }
    
    protected void setMessage(String message) {
        this.message = message;
    }
    
    private class ActualTask {
        
        ActualTask() {
            doTask();
        }
    }
    
    protected abstract void doTask();
}
