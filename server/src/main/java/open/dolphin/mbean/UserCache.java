package open.dolphin.mbean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Singleton;

/**
 * ユーザーのキャッシュ
 * @author masuda, Masuda Naika
 */
@Singleton
public class UserCache {

    private Map<String, String> map = new ConcurrentHashMap<String, String>();

    public Map<String, String> getMap() {
        return map;
    }
}
