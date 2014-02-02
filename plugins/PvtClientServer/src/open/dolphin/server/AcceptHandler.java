package open.dolphin.server;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.apache.log4j.Logger;

public class AcceptHandler implements Handler {
    
    private Logger logger;
    
    public AcceptHandler(Logger logger) {
        this.logger = logger;
    }
    
    public void handle(SelectionKey key) throws ClosedChannelException, IOException {
        
        ServerSocketChannel serverChannel
            = (ServerSocketChannel) key.channel();
        
        SocketChannel channel = serverChannel.accept();
        logger.info(channel.socket().getInetAddress() + " connected");
        channel.configureBlocking(false);

        IOHandler handler = new IOHandler(logger);
        channel.register(key.selector(), SelectionKey.OP_READ, handler);
    }
}
