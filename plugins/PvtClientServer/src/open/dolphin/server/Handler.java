package open.dolphin.server;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

public interface Handler {
    public void handle(SelectionKey key) throws ClosedChannelException, IOException;
}