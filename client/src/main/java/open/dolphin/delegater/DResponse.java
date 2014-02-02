/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.delegater;

import java.io.InputStream;

/**
 * 2013/08/29
 * @author kazushi
 */
public class DResponse {
    
    private InputStream body;

    public DResponse(InputStream body) {
        this.body = body;
    }

    public InputStream getBody() {
        return body;
    }
}
