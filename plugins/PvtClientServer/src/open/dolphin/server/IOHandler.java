package open.dolphin.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import open.dolphin.client.ModelSender;
import open.dolphin.infomodel.PatientVisitModel;
import org.apache.log4j.Logger;

public class IOHandler implements Handler {
    
    private final static int BUFFER_SIZE = 8192;
    
    private static final int EOT = 0x04;
    
    private static final int ACK = 0x06;

    private byte[] data;
    
    private int length;
    
    private Logger logger;
    
    
    public IOHandler(Logger logger) {
        this.logger = logger;
        data = new byte[BUFFER_SIZE*10];
    }
    
    public void handle(SelectionKey key) throws ClosedChannelException, IOException {
        
        if (key.isReadable()) {
            read(key);
        }
    }

    private void read(SelectionKey key) throws ClosedChannelException, IOException {
        
        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        buffer.clear();
        
        int len = channel.read(buffer);

        if (len < 0) {
            
            channel.close();
            
            String pvtXml = new String(data, 0, length -1, "UTF8");
            logger.debug(pvtXml);
            
            BufferedReader r = new BufferedReader(new StringReader(pvtXml));
            PVTBuilder builder = new PVTBuilder();
            builder.setLogger(logger);
            builder.parse(r);
            PatientVisitModel model = builder.getProduct();

            logger.info("—ˆ‰@î•ñ‚ð Queue ‚Ö“o˜^‚µ‚Ü‚µ‚½");
            ModelSender.getInstance().offer(model);

        } else {
            
            buffer.flip();
            buffer.get(data, length, len);
            length += len;
            
            if (buffer.get(len -1) == EOT) {
                logger.debug("Received EOT");
                ByteBuffer ackBuf2 = ByteBuffer.wrap(new byte[]{ACK});
                channel.write(ackBuf2);
                logger.info("ACK ‚ð•Ô‚µ‚µ‚Ü‚µ‚½");
            }
        }
    }
}
