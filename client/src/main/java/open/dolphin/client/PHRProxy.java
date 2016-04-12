package open.dolphin.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.swing.SwingWorker;
import javax.websocket.ClientEndpoint;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import open.dolphin.delegater.PHRDelegater;
import open.dolphin.util.ZenkakuUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 *
 * @author kazushi Minagawa
 */
@ClientEndpoint(subprotocols = {"layer-1.0"})
public final class PHRProxy implements MainService {
    
    private static final String LAYER_ROOT_URI = "https://api.layer.com";
    private static final String LAER_APP_ID = "layer:///apps/staging/3a031e94-5c3b-11e5-b0e1-e9979f007fc5";
    private static final String IDENTITY_SERVER_URI = "https://i18n.opendolphin.com:443/openDolphin/resources/20/adm/phr/identityToken";
    // 患者毎にユニーク
    //private static final String CONVERSATION_UUID = "e0878218-2258-4778-8c9f-772c3b8d065e";
    private static final String USER_ID = "kazushi";
    private MainWindow context;
    private Session webSocketSession;
    private String session_token;
    
    public PHRProxy() {
    }
    
    private String getIdentityToken(String nonce, String userId) {
        
        String json = Json.createObjectBuilder()
            .add("nonce", nonce)
            .add("user", userId)
            .build()
            .toString();

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(IDENTITY_SERVER_URI);
        
        Response response = target.request().post(Entity.text(json));
        int status = response.getStatus();
        String entity = response.readEntity(String.class);
        log(status, entity);
        
        return entity;
    }
    
    private ResteasyWebTarget getWebTarget(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(LAYER_ROOT_URI);
        if (path.startsWith("/")) {
            sb.append(path);
        } else {
            sb.append("/").append(path);
        }
        String uri = sb.toString();
        
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register((ClientRequestFilter) (ClientRequestContext crc) -> {
            crc.getHeaders().add("Accept", "application/vnd.layer+json; version=1.0");
            crc.getHeaders().add("Content-type", "application/json");
        });
        
        return client.target(uri);
    }
    
    private ResteasyWebTarget getWebTarget(String path, final String sessionToken) {
        StringBuilder sb = new StringBuilder();
        sb.append(LAYER_ROOT_URI);
        if (path.startsWith("/")) {
            sb.append(path);
        } else {
            sb.append("/").append(path);
        }
        String uri = sb.toString();
        
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register((ClientRequestFilter) (ClientRequestContext crc) -> {
            StringBuilder sb1 = new StringBuilder();
            sb1.append("Layer session-token=");
            sb1.append("'").append(sessionToken).append("'");
            String token = sb1.toString();
            crc.getHeaders().add("Accept", "application/vnd.layer+json; version=1.0");
            crc.getHeaders().add("Content-type", "application/json");
            crc.getHeaders().add("Authorization", token);
        });
        
        return client.target(uri);
    }
    
    private Response postEasy(String path, String json) {
        getLogger().log(Level.INFO, "json={0}", json);
        ResteasyWebTarget target = getWebTarget(path);
        return  target.request().post(Entity.text(json));
    }
    
    private Response postEasy(String path, String sessionToken, String json) {
        getLogger().log(Level.INFO, "json={0}", json);
        ResteasyWebTarget target = getWebTarget(path, sessionToken);
        return  target.request().post(Entity.text(json));
    }
    
    private Response getEasy(String path, String sessionToken) {
        ResteasyWebTarget target = getWebTarget(path, sessionToken);
        return target.request().get();
    }
    
    private String getNonce() {
        String path = "/nonces";
        Response response = postEasy(path, "");
        int status = response.getStatus();
        String entity = response.readEntity(String.class);
        log(status, entity);
        
        if (status/100 == 2) {
            JsonObject jso = getJsonObject(entity);
            return jso.getString("nonce");
        }
        return null;
    }
    
    private String getSessionToken(String identityToken) {
        
        String path = "/sessions";
        
        String json = Json.createObjectBuilder()
            .add("identity_token", identityToken)
            .add("app_id", LAER_APP_ID)
            .build()
            .toString();
        
        Response response = postEasy(path, json);
        int status = response.getStatus();
        String entity = response.readEntity(String.class);
        log(status, entity);
        response.close();
        
        if (status/100 == 2) {
            JsonObject jso = getJsonObject(entity);
            return jso.getString("session_token");
            
        } else if (status==401) {
            return authenticationChallenge(entity);
            
        } else {
            throw new RuntimeException("Unknown HTTP error: " + status);
        }
    }
    
    private String authenticationChallenge(String json) {
        JsonObject jso = getJsonObject(json);
        printJson(jso);
        JsonObject data = jso.getJsonObject("data");
        String nonce = data.getString("nonce");
        String token = getIdentityToken(nonce, USER_ID);
        String session = getSessionToken(token);
        return session;
    }
    
    private void createConversation(String[] participants, boolean distinct) {
        
        JsonArrayBuilder arr = Json.createArrayBuilder();
        for (String p : participants) {
            arr.add(p);
        }
        String json = Json.createObjectBuilder()
            .add("participants", arr)
            .add("distinct", distinct)
            .build()
            .toString();
        
        String path = "/conversations";
        Response response = postEasy(path, session_token, json);
        int status = response.getStatus();
        String entity = response.readEntity(String.class);
        log(status, entity);
        response.close();
        
        if (status==401) {
            session_token = authenticationChallenge(entity);
            createConversation(participants, distinct);
        }
    }
    
    private void getConversation(String uuid) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("/conversations/").append(uuid);
        String path = sb.toString();
        
        Response response = getEasy(path, session_token);
        int status = response.getStatus();
        String entity = response.readEntity(String.class);
        log(status, entity);
        response.close();
        
        if (status/100==2) {
            JsonObject jsonObject = getJsonObject(entity);
            printJson(jsonObject);
            
        } else if (status==401) {
            session_token = authenticationChallenge(entity);
            getConversation(uuid);
        }
    }
    
    private void getAllMessages(String uuid) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("/conversations/").append(uuid).append("/messages");
        String path = sb.toString();
        
        Response response = getEasy(path, session_token);
        int status = response.getStatus();
        String entity = response.readEntity(String.class);
        log(status, entity);
        response.close();
        
        if (status/100 ==2) {
            javax.json.JsonArray array = getJsonArray(entity);
            for (JsonValue value : array) {
                JsonObject jso = (JsonObject)value;
                JsonArray parts = jso.getJsonArray("parts");
                JsonObject part = (JsonObject)parts.getJsonObject(0);
                if ("text/plain".equals(part.getString("mime_type"))) {
                    getLogger().log(Level.INFO, "body={0}", part.getString("body"));
                }
            }
        }
        else if (status==401) {
            session_token = authenticationChallenge(entity);
            getAllMessages(uuid);
        }
    }
    
    private void getMessage(String uuid) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("/messages/").append(uuid);
        String path = sb.toString();
        
        Response response = getEasy(path, session_token);
        int status = response.getStatus();
        String entity = response.readEntity(String.class);
        log(status, entity);
        response.close();
        
        if (status/100 ==2) {
            JsonObject jsonObject = getJsonObject(entity);
            printJson(jsonObject);
        }
        else if (status==401) {
            session_token = authenticationChallenge(entity);
            getMessage(uuid);
        }
    }
    
    private void sendTextMessage(String conversation_UUID, String text) {
        
        JsonObjectBuilder msgObject = Json.createObjectBuilder();
        msgObject.add("body", text);
        msgObject.add("mime_type", "text/plain");
            
        JsonArrayBuilder arr = Json.createArrayBuilder();
        arr.add(msgObject);
        
        String json = Json.createObjectBuilder().add("parts", arr).build().toString();
        
        StringBuilder sb = new StringBuilder();
        sb.append("/conversations/").append(conversation_UUID).append("/messages");
        String path = sb.toString();
        
        Response response = postEasy(path, session_token, json);
        int status = response.getStatus();
        String entity = response.readEntity(String.class);
        log(status, entity);
        response.close();
        
        if (status/100 ==2) {
            //
        }
        else if (status==401) {
            session_token = authenticationChallenge(entity);
            sendTextMessage(conversation_UUID, text);
        }
    }
    
    private String createTextMessage(String conversation_UUID, String text) {
        
        JsonObjectBuilder part = Json.createObjectBuilder();
        part.add("body", text);
        part.add("mime_type", "text/plain");
        
        JsonArrayBuilder parts = Json.createArrayBuilder();
        parts.add(part);
        
        JsonObjectBuilder data = Json.createObjectBuilder();
        data.add("parts", parts);
        
        JsonObjectBuilder body = Json.createObjectBuilder();
        body.add("method", "Message.create");
        body.add("request_id", UUID.randomUUID().toString()); // uuid
        body.add("object_id", conversation_UUID);
        body.add("data", data);
        
        JsonObjectBuilder msgObject = Json.createObjectBuilder();
        msgObject.add("type", "request");
        msgObject.add("body", body);
       
        JsonObject test = msgObject.build();
        printJson(test);
        
        return test.toString();
    }
    
    private JsonObject getJsonObject(String jsonStr) {
        JsonObject jsonObject;
        try (StringReader sr = new StringReader(jsonStr); JsonReader jsonReader = Json.createReader(sr)) {
            jsonObject = jsonReader.readObject();
        }
        return jsonObject;
    }
    
    private void printJson(JsonObject jso) {
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        StringWriter sw = new StringWriter();    
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        try (JsonWriter jsonWriter = writerFactory.createWriter(sw)) {
            jsonWriter.writeObject(jso);
            getLogger().log(Level.INFO, "json={0}", sw.toString());
        }
    }
    
    private JsonArray getJsonArray(String jsonStr) {
        JsonArray jsonArray;
        try (StringReader sr = new StringReader(jsonStr); JsonReader jsonReader = Json.createReader(sr)) {
            jsonArray = jsonReader.readArray();
        }
        return jsonArray;
    }
    
    private Logger getLogger() {
        return Logger.getLogger(this.getClass().getName());
    }
    
    private void log(int status, String entity) {
        getLogger().log(Level.INFO, "status={0}", status);
        getLogger().log(Level.INFO, "entity={0}", entity);
    }
    
    private void sendPHRRequest(final String conversation_uuid, String sender, String dataType) {
        
        final String pid = sender.equals("shiho") ? "000008" : null;
        if (pid==null) {
            return;
        }
        
        SwingWorker worker;
        worker = new SwingWorker<Void, Void>() {
            
            @Override
            protected Void doInBackground() throws Exception {
                PHRDelegater phrd = new PHRDelegater();
                String text = phrd.getPHRdata(pid, dataType);
                text = ZenkakuUtils.toHalfNumber(text);
                sendTextMessage(conversation_uuid, text);
                return null;
            }
        };
        
        worker.execute();
    }
    
    @OnOpen
    public void onOpen(Session session) {
        getLogger().log(Level.INFO, "onOpen");
    }

    @OnMessage
    public void onMessage(String message) {
        
        getLogger().log(Level.INFO, "onMessage");
        JsonObject jso = getJsonObject(message);
        printJson(jso);
        
        // type
        String type = jso.getString("type");            // change, request, response, signal
        JsonObject body = jso.getJsonObject("body");
        String timestamp = jso.getString("timestamp");
        int counter = jso.getInt("counter");
        
        if (!"change".equals(type)) {
            return;
        }
        if (!"create".equals(body.getString("operation"))) {
            // create, delete, update
            return;
        }
        if (!"Message".equals(body.getJsonObject("object").getString("type"))) {
            // Conversation, Message
            return;
        }
       
        JsonArray parts = body.getJsonObject("data").getJsonArray("parts");
        JsonObject part0 = parts.getJsonObject(0);
        if (!"text/plain".equals(part0.getString("mime_type"))) {
            return;
        }
        
        String sender = body.getJsonObject("data").getJsonObject("sender").getString("user_id");
        if (USER_ID.equals(sender)) {
            return;
        }
        
        // Conversatuin ID
        String conversation_uuid = body.getJsonObject("data").getJsonObject("conversation").getString("id");
        int index = conversation_uuid.lastIndexOf("/");
        if (index <= 0) {
            return;
        }
        conversation_uuid = conversation_uuid.substring(index + 1);
        
        String text = part0.getString("body");
        if (text.equals("処方") ||
                text.equals("検査") ||
                text.equals("病名") ||
                text.equals("アレルギー")) {
            
            sendPHRRequest(conversation_uuid, sender, text);
        }
    }

    @OnError
    public void onError(Throwable t) {
        getLogger().log(Level.SEVERE, null, t);
    }

    @OnClose
    public void onClose(Session session) {
        getLogger().log(Level.INFO, "onClose");
    }

    @Override
    public String getName() {
        return "PHR Proxy";
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public MainWindow getContext() {
        return context;
    }

    @Override
    public void setContext(MainWindow context) {
        this.context = context;
    }

    @Override
    public void start() {
        String nonce = this.getNonce();
        String token = this.getIdentityToken(nonce, USER_ID);
        session_token = this.getSessionToken(token);
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = URI.create("wss://api.layer.com/websocket?session_token="+session_token);

            webSocketSession = container.connectToServer(this, uri);
            
        } catch (DeploymentException | IOException ex) {
            Logger.getLogger(PHRProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void stop() {
        try {
            if (webSocketSession!=null) {
                webSocketSession.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(PHRProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
