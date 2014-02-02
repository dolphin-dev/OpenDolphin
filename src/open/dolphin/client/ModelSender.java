package open.dolphin.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.delegater.PVTDelegater;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.PatientVisitModel;
import org.apache.log4j.Logger;

/**
 *
 * @author kazm
 */
public class ModelSender  {
    
    private static ModelSender instance = new ModelSender();
    
    private LinkedBlockingQueue queue;
    
    private Thread sender;
    
    private ModelConsumer consumer;
    
    private Logger logger;
    
    public static ModelSender getInstance() {
        return instance;
    }

    private ModelSender() {
        logger = ClientContext.getBootLogger();
        queue = new LinkedBlockingQueue();
        consumer = new ModelConsumer();
        sender = new Thread(consumer);
        sender.setPriority(Thread.NORM_PRIORITY);
        sender.start();
    }
            
    public void offer(Object model) {
        queue.offer(model);
    }
    
    public Object take() {
        
        try {
            return queue.take();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public void stop() {
        Thread moribound = sender;
        sender = null;
        moribound.interrupt();
    }
    
    private Callable<Long> getDocumentTask(final DocumentModel model) { 
        
        Callable<Long> c = new Callable() {
            public Long call() {
                DocumentDelegater ddl = new DocumentDelegater();
                long result = ddl.putKarte(model);
                return new Long(result);
            }
        };
        return c;
    }
    
    private Callable<Integer> getPvtTask(final PatientVisitModel model) {
        
        Callable<Integer> c = new Callable() {
            public Integer call() {
                PVTDelegater pdl = new PVTDelegater();
                int result = pdl.addPvt(model);
                return new Integer(result);
            }
        };
        return c;
    }
    
    class ModelConsumer implements Runnable {
        
        public void run() {
            
            logger.debug("sender started");
            
             while (true) {
                 
                 final Object model = take();
                 logger.debug("took object");
                 
                 if (model != null) {
                     
                    try {
                        FutureTask task = null;
                        if (model instanceof PatientVisitModel) {
                            task = new FutureTask(getPvtTask((PatientVisitModel) model));
                            logger.debug("created pvt FutureTask");
                        }
                        else if (model instanceof DocumentModel) {
                            task = new FutureTask(getDocumentTask((DocumentModel) model));
                            logger.debug("created document FutureTask");
                        }
                        logger.debug("start FutureTask");
                        new Thread(task).start();
                        task.get(120, TimeUnit.SECONDS);
                        logger.debug("got result within timeout");
                        
                    } catch (InterruptedException ex) {
                        logger.warn(ex);
                    } catch (ExecutionException ex) {
                        logger.warn(ex);
                    } catch (TimeoutException ex) {
                        logger.warn(ex);
                        offer(model);
                    }
                 }
             }
        }
    }
}
