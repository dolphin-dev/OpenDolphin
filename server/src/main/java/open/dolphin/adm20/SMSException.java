/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.adm20;

/**
 *
 * @author kazushi
 */
public class SMSException extends RuntimeException {
    
    public SMSException() {
        super();
    }

    public SMSException(String msg) {        
        super(msg);
    }
    
    public SMSException(java.lang.Throwable t) {        
        super(t);
    } 
    
    public SMSException(String s, java.lang.Throwable t) {        
        super(s, t);
    }   
}
