package open.dolphin.client;

//import com.ning.http.client.Response;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import open.dolphin.delegater.ChartEventDelegater;
import open.dolphin.infomodel.*;
import open.dolphin.project.Project;
import open.dolphin.util.BeanUtils;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * カルテオープンなどの状態の変化をまとめて管理する
 * @author masuda, Masuda Naika
 * 
 * modified minagawa
 * オリジナル Git Hub Masuda-Naika OpenDolphin-2.3.8m
 * PropertyChange systemへ変更
 */
public class ChartEventHandler  {
    
    public static final String CHART_EVENT_PROP = "chartEventModelProp";
    
    // プロパティ
    private ChartEventModel model;
    
     // このクライアントのパラメーター類
    private String clientUUID;
    private String orcaId;
    private String deptCode;
    private String departmentDesc;
    private String doctorName;
    private String userId;
    private String jmariCode;
    private String facilityId;
    
    // スレッド
    private EventListenTask listenTask;
    private Thread thread;
    
    // 状態変化を各listenerに通知するタスク
    private Executor exec;
    
    private PropertyChangeSupport boundSupport;
    
    private static final ChartEventHandler instance = new ChartEventHandler();

    private ChartEventHandler() {
        init();
     }

    public static ChartEventHandler getInstance() {
        return instance;
    }
    
    private void init() {
        clientUUID = Dolphin.getInstance().getClientUUID();
        orcaId = Project.getUserModel().getOrcaId();
        deptCode = Project.getUserModel().getDepartmentModel().getDepartment();
        departmentDesc = Project.getUserModel().getDepartmentModel().getDepartmentDesc();
        doctorName = Project.getUserModel().getCommonName();
        userId = Project.getUserModel().getUserId();
        jmariCode = Project.getString(Project.JMARI_CODE);
        facilityId = Project.getFacilityId();
        boundSupport = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(CHART_EVENT_PROP,l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(CHART_EVENT_PROP,l);
    }
    
    public void setChartEventModel(ChartEventModel evt) {
        ChartEventModel old = this.model;
        this.model = evt;
        boundSupport.firePropertyChange(CHART_EVENT_PROP, old, this.model);
    }
    
    private void logAndSetChartEventModel(boolean notify, ChartEventModel evt) {
        if (false) {
            String notifyOrReceived = notify ? "通知" : "受信";
            System.err.println("----------------------------------------");
            StringBuilder sb = new StringBuilder();
            sb.append(notifyOrReceived).append("\n");
            sb.append("evtType=").append(evt.getEventType()).append("\n");
            sb.append("pvtPK=").append(evt.getPvtPk()).append("\n");
            sb.append("pvtState=").append(evt.getState()).append("\n");
            sb.append("ptPK=").append(evt.getPtPk()).append("\n");
            sb.append("owner=" ).append(evt.getOwnerUUID()).append("\n");
            sb.append("bitOpen=").append(evt.getOwnerUUID()!=null).append("\n");
            System.err.println(sb.toString());
        }
        setChartEventModel(evt);
    }
    
    public String getClientUUID() {
        return clientUUID;
    }
    
    // 状態変更処理の共通入り口
    private void publish(ChartEventModel evt) {
        exec.execute(new LocalOnEventTask(evt));
    }
    
    public void publishPvtDelete(PatientVisitModel pvt) {
        
        ChartEventModel evt = new ChartEventModel(clientUUID);
        evt.setParamFromPvt(pvt);
        evt.setEventType(ChartEventModel.PVT_DELETE);
        
        publish(evt);
    }
    
    public void publishPvtState(PatientVisitModel pvt) {
        
        ChartEventModel evt = new ChartEventModel(clientUUID);
        evt.setParamFromPvt(pvt);
        evt.setEventType(ChartEventModel.PVT_STATE);
        
        publish(evt);
    }
    
    public void publishKarteOpened(PatientVisitModel pvt) {
        
        // 閲覧のみの処理、ええい！面倒だ！
        if (!clientUUID.equals(pvt.getPatientModel().getOwnerUUID())) {
            return;
        }

        // PatientVisitModel.BIT_OPENを立てる
        pvt.setStateBit(PatientVisitModel.BIT_OPEN, true);
        // ChartStateListenerに通知する
        ChartEventModel evt = new ChartEventModel(clientUUID);
        evt.setParamFromPvt(pvt);
        evt.setEventType(ChartEventModel.PVT_STATE);
        
        publish(evt);
    }
    
    public void publishKarteClosed(PatientVisitModel pvt) {
        
        // 閲覧のみの処理、ええい！面倒だ！
        if (!clientUUID.equals(pvt.getPatientModel().getOwnerUUID())) {
            return;
        }
        
        // PatientVisitModel.BIT_OPENとownerUUIDをリセットする
        pvt.setStateBit(PatientVisitModel.BIT_OPEN, false);
        pvt.getPatientModel().setOwnerUUID(null);
        
        // ChartStateListenerに通知する
        ChartEventModel evt = new ChartEventModel(clientUUID);
        evt.setParamFromPvt(pvt);
        evt.setEventType(ChartEventModel.PVT_STATE);
        
        publish(evt);
    }

    public void start() {

        exec = Executors.newSingleThreadExecutor();
        listenTask = new EventListenTask();
        thread = new Thread(listenTask, "ChartEvent Listen Task");
//minagawa^ 念のため        
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    public void stop() {
        
        listenTask.stop();
        thread.interrupt();
        thread = null;
    }

    // Commetでサーバーと同期するスレッド
    private class EventListenTask implements Runnable {
        
        //private Future<Response> future;
        private Future<HttpResponse> future;
        
        private boolean isRunning;
        
        private EventListenTask() {
            isRunning = true;
        }

        private void stop() {
            isRunning = false;
            if (future != null) {
                future.cancel(true);
            }
        }
        
        @Override
        public void run() {
            
            //long t1 = System.currentTimeMillis();
            
            while (isRunning) {
                try {
                    future = ChartEventDelegater.getInstance().subscribe();
                    //System.out.println("time = " + String.valueOf(System.currentTimeMillis() - t1));
                    //Response response = future.get();
                    HttpResponse response = future.get();
                    //t1 = System.currentTimeMillis();
                    if (response != null) {
                        exec.execute(new RemoteOnEventTask2(response));
                    }
                    //System.out.println("ChartEvent= " + json);
                } catch (Exception e) {
                    System.err.print("future exception");
                    System.out.println(e.toString());
                }
            }
        }
    }
    
    // 自クライアントの状態変更後、サーバーに通知するタスク
    private class LocalOnEventTask implements Runnable {
        
        private ChartEventModel evt;
        
        private LocalOnEventTask(ChartEventModel evt) {
            this.evt = evt;
        }

        @Override
        public void run() {
            // まずは自クライアントを更新
            logAndSetChartEventModel(true,evt);
            // サーバーに更新を通知
            try {
                ChartEventDelegater del = ChartEventDelegater.getInstance();
                del.putChartEvent(evt);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }
    
//    // 状態変化通知メッセージをデシリアライズし各リスナに処理を分配する
//    private class RemoteOnEventTask implements Runnable {
//        
//        private Response response;
//        
//        private RemoteOnEventTask(Response response) {
//            this.response = response;
//        }
//
//        @Override
//        public void run() {
//
//            try {
//                
//                ChartEventModel evt = responseToEvent(response);
//                
//                if (evt!=null) {
//                    // PatientModelが乗っかってきている場合は保険をデコード
//                    PatientModel pm = evt.getPatientModel();
//                    if (pm != null) {
//                        decodeHealthInsurance(pm);
//                    }
//                    PatientVisitModel pvt = evt.getPatientVisitModel();
//                    if (pvt != null) {
//                        decodeHealthInsurance(pvt.getPatientModel());
//                    }
//                }
//                logAndSetChartEventModel(false,evt);
//                
//            } catch (Exception e) {
//                e.printStackTrace(System.err);
//            }         
//        }        
//    }
    
    private class RemoteOnEventTask2 implements Runnable {
        
        private HttpResponse response;
        
        private RemoteOnEventTask2(HttpResponse response) {
            this.response = response;
        }

        @Override
        public void run() {

            try {
                
                ChartEventModel evt = responseToEvent2(response);
                
                if (evt!=null) {
                    // PatientModelが乗っかってきている場合は保険をデコード
                    PatientModel pm = evt.getPatientModel();
                    if (pm != null) {
                        decodeHealthInsurance(pm);
                    }
                    PatientVisitModel pvt = evt.getPatientVisitModel();
                    if (pvt != null) {
                        decodeHealthInsurance(pvt.getPatientModel());
                    }
                }
                logAndSetChartEventModel(false,evt);
                
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }         
        }        
    }
    
//    private ChartEventModel responseToEvent(Response response) throws Exception {
//        InputStream in = response.getResponseBodyAsStream();
//        InputStreamReader ir = new InputStreamReader(in, "UTF-8");
//        BufferedReader br = new BufferedReader(ir);
//        ObjectMapper mapper = new ObjectMapper();
//        ChartEventModel evt = mapper.readValue(br, ChartEventModel.class);
//        br.close();
//        return evt;
//    }
    
    private ChartEventModel responseToEvent2(HttpResponse response) throws Exception {
        InputStream in = response.getEntity().getContent();
        InputStreamReader ir = new InputStreamReader(in, "UTF-8");
        BufferedReader br = new BufferedReader(ir);
        ObjectMapper mapper = new ObjectMapper();
        ChartEventModel evt = mapper.readValue(br, ChartEventModel.class);
        br.close();
        return evt;
    }
    
    /**
     * バイナリの健康保険データをオブジェクトにデコードする。
     *
     * @param patient 患者モデル
     */
    private void decodeHealthInsurance(PatientModel patient) {

        // Health Insurance を変換をする beanXML2PVT
        Collection<HealthInsuranceModel> c = patient.getHealthInsurances();

        if (c != null && !c.isEmpty()) {

            List<PVTHealthInsuranceModel> list = new ArrayList<PVTHealthInsuranceModel>(c.size());

            for (HealthInsuranceModel hm : c) {
                try {
                    // byte[] を XMLDecord
                    PVTHealthInsuranceModel hModel = (PVTHealthInsuranceModel) 
                            BeanUtils.xmlDecode(hm.getBeanBytes());
                    list.add(hModel);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }

            patient.setPvtHealthInsurances(list);
            patient.getHealthInsurances().clear();
            patient.setHealthInsurances(null);
        }
    }
    
    // FakePatientVisitModelを作る
    public PatientVisitModel createFakePvt(PatientModel pm) {

        // 来院情報を生成する
        PatientVisitModel pvt = new PatientVisitModel();
        pvt.setId(0L);
        pvt.setPatientModel(pm);
        pvt.setFacilityId(facilityId);

        //--------------------------------------------------------
        // 受け付けを通していないのでログイン情報及び設定ファイルを使用する
        // 診療科名、診療科コード、医師名、医師コード、JMARI
        // 2.0
        //---------------------------------------------------------
        pvt.setDeptName(departmentDesc);
        pvt.setDeptCode(deptCode);
        pvt.setDoctorName(doctorName);
        if (orcaId != null) {
            pvt.setDoctorId(orcaId);
        } else {
            pvt.setDoctorId(userId);
        }
        pvt.setJmariNumber(jmariCode);
        
        // 来院日
        pvt.setPvtDate(ModelUtils.getDateTimeAsString(new Date()));
        return pvt;
    }
}
