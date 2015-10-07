package open.dolphin.client;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

/**
 * 
 * @author Kazushi Minagawa
 */
public class CustomVelocityLogger implements LogChute {

    @Override
    public void init(RuntimeServices rs) throws Exception {
    }

    @Override
    public void log(int i, String string) {
    }

    @Override
    public void log(int i, String string, Throwable thrwbl) {
    }

    @Override
    public boolean isLevelEnabled(int i) {
        return false;
    }
}
