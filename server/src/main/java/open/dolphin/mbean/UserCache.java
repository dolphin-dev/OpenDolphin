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

    private final Map<String, String> map = new ConcurrentHashMap<>();

    public Map<String, String> getMap() {
        return map;
    }
}
