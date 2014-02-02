package open.dolphin.mbean;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Singleton;
import javax.servlet.AsyncContext;
import open.dolphin.infomodel.PatientVisitModel;

/**
 * サーブレットの諸情報を保持するクラス
 * @author masuda, Masuda Naika
 */
@Singleton
public class ServletContextHolder {

    // 今日と明日
    private GregorianCalendar today;
    private GregorianCalendar tomorrow;

    // AsyncContextのリスト
    private final List<AsyncContext> acList = new ArrayList<AsyncContext>();
    
    // facilityIdとpvtListのマップ
    private Map<String, List<PatientVisitModel>> pvtListMap 
            = new ConcurrentHashMap<String, List<PatientVisitModel>>();
    
    // サーバーのUUID
    private String serverUUID;

    public List<AsyncContext> getAsyncContextList() {
        return acList;
    }

    public void addAsyncContext(AsyncContext ac) {
        synchronized (acList) {
            acList.add(ac);
        }
    }

    public void removeAsyncContext(AsyncContext ac) {
        synchronized (acList) {
            acList.remove(ac);
        }
    }
    
    public String getServerUUID() {
        return serverUUID;
    }
    
    public void setServerUUID(String uuid) {
        serverUUID = uuid;
    }

    public Map<String, List<PatientVisitModel>> getPvtListMap() {
        return pvtListMap;
    }
    
    public List<PatientVisitModel> getPvtList(String fid) {
        List<PatientVisitModel> pvtList = pvtListMap.get(fid);
        if (pvtList == null) {
            pvtList = new CopyOnWriteArrayList<PatientVisitModel>();
            pvtListMap.put(fid, pvtList);
        }
        return pvtList;
    }

    // 今日と明日を設定する
    public void setToday() {
        today= new GregorianCalendar();
        int year = today.get(GregorianCalendar.YEAR);
        int month = today.get(GregorianCalendar.MONTH);
        int date = today.get(GregorianCalendar.DAY_OF_MONTH);
        today.clear();
        today.set(year, month, date);

        tomorrow = new GregorianCalendar();
        tomorrow.setTime(today.getTime());
        tomorrow.add(GregorianCalendar.DAY_OF_MONTH, 1);
    }
    
    public GregorianCalendar getToday() {
        return today;
    }
    public GregorianCalendar getTomorrow() {
        return tomorrow;
    }
}
