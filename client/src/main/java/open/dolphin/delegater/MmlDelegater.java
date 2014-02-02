package open.dolphin.delegater;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 *
 * @author kazushi
 */
public class MmlDelegater extends BusinessDelegater {
    
    public void dumpPatientDiagnosisToMML(String fid) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/patient/").append(fid);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);
        
        checkStatus(response);
    }
    
    public void dumpDocumentToMML(String fid) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/document/").append(fid);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);
        
        checkStatus(response);
    }
    
    //-------------------------------------------------------------------------
    
    public void dumpAllCollection(String fid) throws RuntimeException {
        
        try {
            //dumpPatientToJSON(fid);
            //dumpMemoToJSON(fid);
            //dumpDiseaseToJSON(fid);
            //dumpObservationToJSON(fid);
            dumpKarteToJSON(fid);
            //dumpLetterToJSON(fid);
            //dumpLabtestToJSON(fid);
            
        } catch (Exception e) {
           e.printStackTrace(System.err);
        }
    }
    
    //-------------------------------------------------------------------------
    // Patient
    //-------------------------------------------------------------------------
    public void dumpPatientToJSON(String fid) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/patient/list/").append(fid);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);
        
        String[] pks = getString(response).split(",");
        
        for (String str : pks) {
            
            sb = new StringBuilder();
            sb.append("/mml/patient/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);
            
            String json = getString(response);
            System.err.println(json);
        }
    }
    
    //-------------------------------------------------------------------------
    // Disease
    //-------------------------------------------------------------------------
    public void dumpDiseaseToJSON(String fid) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/disease/list/").append(fid);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);
        
        String[] pks = getString(response).split(",");
        
        for (String str : pks) {
            
            sb = new StringBuilder();
            sb.append("/mml/disease/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);
            
            String json = getString(response);
            System.err.println(json);
        }
    }
    
    //-------------------------------------------------------------------------
    // Memo
    //-------------------------------------------------------------------------
    public void dumpMemoToJSON(String fid) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/memo/list/").append(fid);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);
        
        String[] pks = getString(response).split(",");
        
        for (String str : pks) {
            
            sb = new StringBuilder();
            sb.append("/mml/memo/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);
            
            String json = getString(response);
            System.err.println(json);
        }
    }
    
    //-------------------------------------------------------------------------
    // Observation
    //-------------------------------------------------------------------------
    public void dumpObservationToJSON(String fid) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/observation/list/").append(fid);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);
        
        String[] pks = getString(response).split(",");
        
        for (String str : pks) {
            
            sb = new StringBuilder();
            sb.append("/mml/observation/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);
            
            String json = getString(response);
            System.err.println(json);
        }
    }
    
    //-------------------------------------------------------------------------
    // Karte
    //-------------------------------------------------------------------------
    public void dumpKarteToJSON(String fid) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/karte/list/").append(fid);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);
        
        checkStatus(response);
        
        String[] pks = getString(response).split(",");
        System.err.println("doc count = " + pks.length);

        int cnt = 1;
        for (String str : pks) {

            sb = new StringBuilder();
            sb.append("/mml/karte/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);

            String json = getString(response);
            sb = new StringBuilder();
            sb.append(cnt++).append(":").append(json);
            System.err.println(sb.toString());
            if (cnt==10) {
                break;
            }
        }
    }
    
    //-------------------------------------------------------------------------
    // Letter
    //-------------------------------------------------------------------------
    public void dumpLetterToJSON(String fid) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/letter/list/").append(fid);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);
        
        String[] pks = getString(response).split(",");
        
        for (String str : pks) {
            
            sb = new StringBuilder();
            sb.append("/mml/letter/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);
            
            String json = getString(response);
            System.err.println(json);
        }
    }
    
    //-------------------------------------------------------------------------
    // Labtest
    //-------------------------------------------------------------------------
    public void dumpLabtestToJSON(String fid) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/labtest/list/").append(fid);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);
        
        String[] pks = getString(response).split(",");
        
        for (String str : pks) {
            
            sb = new StringBuilder();
            sb.append("/mml/labtest/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);
            
            String json = getString(response);
            System.err.println(json);
        }
    }
}
