package cn.jifit.tv.beacon;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * Created by addler on 2017/6/6.
 * MI Bracelet Beacon Server. Listen & handle json packages from beacons.
 *
 */

public class MIBeaconServer{

    //Server Port
    private static final int DEFAULT_PORT = 9988;

    private ServerSocketChannel serverChannel;

    public MIBeaconServer() {
    }

    //bind & create ACCEPT selector
    public void start(){
        Selector socketSelector = NonBlockHandler.getInstance().getSelector();
        try {
            // Create a new non-blocking server socket channel
            this.serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);

            // Bind the server socket to the specified address and port
            InetSocketAddress isa = new InetSocketAddress(DEFAULT_PORT);
            serverChannel.socket().bind(isa);

            // Register the server socket channel, indicating an interest in
            // accepting new connections
            serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
        }catch (IOException io){
            if (serverChannel != null){
                try {
                    serverChannel.close();
                } catch (IOException e) {
                    //Do nothing
                }
            }
        }

    }

    public void close(){
        try {
            serverChannel.close();
        } catch (IOException e) {
            //Do nothing
        }
    }

}
