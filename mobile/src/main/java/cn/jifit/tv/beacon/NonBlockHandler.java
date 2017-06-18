package cn.jifit.tv.beacon;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * Created by addler on 2017/6/7.
 * Create a selector to monitor all non block request.
 * And only One running loop to Simple handle these request.
 * All complex Operation, Create a work to put in Single Thread Queue to Handle
 */

public class NonBlockHandler implements Runnable{

    private Selector selector;
    private boolean started = false;
    //read buffer size
    private static final int BUFFER_SIZE = 1500;
    //for reader,
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);


    // Singleton
    private NonBlockHandler(){
        selector = NonBlockHandler.createSelector();
    }
    private static class SingletonHelper{
        private static final NonBlockHandler INSTANCE = new NonBlockHandler();
    }
    public static NonBlockHandler getInstance(){
        return SingletonHelper.INSTANCE;
    }

    // Create a blank selector
    private static Selector createSelector(){
        Selector selector = null;
        try {
            selector = SelectorProvider.provider().openSelector();
        }catch (IOException io){
            if (selector != null){
                try {
                    selector.close();
                    selector = null;
                } catch (IOException e) {
                    //Do nothing
                }
            }
        }

        return selector;
    }

    public Selector getSelector(){
        return selector;
    }
    public void shutdown(){
        this.started = false;
    }
    @Override
    public void run(){
        this.started = true;

        while (true) {
            if (!started) {
                break;
            }
            try {
                // Wait for an event one of the registered channels
                this.selector.select();

                // Iterate over the set of keys for which events are available
                Iterator selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    // Check what event is available and deal with it
                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void read(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int read = 0;
        this.byteBuffer.clear();
        try {
            read = socketChannel.read(this.byteBuffer);
            if (read > 0){
                this.byteBuffer.flip();
                byte[] buf = new byte[read];
                this.byteBuffer.get(buf);
                String input = new String(buf, StandardCharsets.UTF_8);
                Callable task = WorkFactory.getInstance().getCallable(WorkFactory.TYPE.MI_BEACON_JSON_WORK, input);
                SingleThreadQueue.getInstance().submit(task);
                this.byteBuffer.clear();
            }
        } catch (IOException e) {
            //Do nothing
        }
        if (read == -1){
            try {
                socketChannel.close();
            } catch (IOException e) {
                //Do nothing
            }
            key.cancel();

        }
    }


    private void accept(SelectionKey key) throws IOException {
        // For an accept to be pending the channel must be a server socket channel.
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();
//        Socket socket = socketChannel.socket(); // socket
        socketChannel.configureBlocking(false);

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's byteBuffer waiting to be read
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }
}
