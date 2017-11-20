package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.communication.event;

import android.util.Log;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.ConfigService;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.datamapper.DataMapper;
import com.robertnorthard.dtbs.server.common.dto.TaxiLocationEventDto;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Observable;

/**
 * Bidirectional network event bus for taxi location event propagation.
 *
 * @author robertnorthard
 */
public class MessageEventBus extends Observable{

    // TAG used for logging.
    private static final String TAG = MessageEventBus.class.getName();

    private static MessageEventBus messageEventBus;
    private WebSocketClient webSocketClient;

    private MessageEventBus(){
        initiateWebSocket();
    }

    /**
     * @return a single instance of MessageEventBus. If null create new.
     */
    public static MessageEventBus getInstance(){
        //String gridReference
        if(MessageEventBus.messageEventBus == null){
            synchronized(MessageEventBus.class){
                MessageEventBus.messageEventBus = new MessageEventBus();
            }
        }
        return MessageEventBus.messageEventBus;
    }

    /**
     * Open communication channel.
     */
    public void open(){
        this.initiateWebSocket();
    }

    private void initiateWebSocket() {

        URI uri;
        try {
            uri = new URI(ConfigService.getProperty("dtbs.endpoint.taxi.location.subscribe.grid"));
        } catch (URISyntaxException e) {
            Log.d(TAG, e.getMessage());
            return;
        }

        // Websocket draft 17 compatible with server as Sec-WebSocket-Version is 13.
        this.webSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("WebSocket", "Socket Opened");
            }

            @Override
            public void onMessage(final String message) {
                Log.i(TAG, message);
                TaxiLocationEventDto e = DataMapper.getInstance().readObject(message, TaxiLocationEventDto.class);
                setChanged();
                notifyObservers(e);
            }

            @Override
            public void onClose(int i, String message, boolean b) {
                Log.i(TAG, message);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, e.getMessage());
            }
        };

        this.webSocketClient.connect();
    }

    public void sendData(Object object){
        try {
            this.webSocketClient.send(DataMapper.getInstance().getObjectAsJson(object));
        }catch(Exception ex){
            Log.d("WebSocket", "Error sending message.");
        }
    }

    /**
     * Close communication channel.
     *
     * @return true if communication channel closed, else false.
     */
    public synchronized boolean close(){
        if(MessageEventBus.messageEventBus != null){
            this.webSocketClient.close();
            return true;
        }
        return false;
    }
}
