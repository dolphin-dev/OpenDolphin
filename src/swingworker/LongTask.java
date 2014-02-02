package swingworker;

/** Uses a SwingWorker to perform a time-consuming (and utterly fake) task. */

public class LongTask {
    private int lengthOfTask;
    private int current = 0;
    private String statMessage;

    LongTask() {
        //Compute length of task...
        //In a real program, this would figure out
        //the number of bytes to read or whatever.
        lengthOfTask = 1000;
    }

    /**
     * Called from ProgressBarDemo to start the task.
     */
    void go() {
        current = 0;
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new ActualTask();
            }
        };
        worker.start();
    }

    /**
     * Called from ProgressBarDemo to find out how much work needs
     * to be done.
     */
    int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    int getCurrent() {
        return current;
    }

    void stop() {
        current = lengthOfTask;
    }

    /**
     * Called from ProgressBarDemo to find out if the task has completed.
     */
    boolean done() {
        if (current >= lengthOfTask)
            return true;
        else
            return false;
    }

    String getMessage() {
        return statMessage;
    }

    /**
     * The actual long running task.  This runs in a SwingWorker thread.
     */
    class ActualTask {
        ActualTask () {
            //Fake a long task,
            //making a random amount of progress every second.
            while (current < lengthOfTask) {
                try {
                    Thread.sleep(1000); //sleep for a second
                    current += Math.random() * 100; //make some progress
                    if (current > lengthOfTask) {
                        current = lengthOfTask;
                    }
                    statMessage = "Completed " + current +
                                  " out of " + lengthOfTask + ".";
                } catch (InterruptedException e) {}
            }
        }
    }
}
