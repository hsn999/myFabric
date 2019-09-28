package com.myfabric.myutil;


import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;


 
//URIURIWebSocketServletmappingweb.xml
@Service
@Order(1)
@ServerEndpoint("/websocket")
public class MyWebSocket implements WebSocket{
    //
    private static int onlineCount = 0;
     
    //concurrentSetMyWebSocketMapKey
    public static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();
     
    //
    private Session session;
     
    /**
     * 
     * @param session  session
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);     //set
        addOnlineCount();           //1
        System.out.println("" + getOnlineCount());
    }
     
    /**
     * 
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);  //set
        subOnlineCount();           //1    
        System.out.println("" + getOnlineCount());
    }
     
    /**
     * 
     * @param message 
     * @param session 
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(":" + message);
         
        //
        for(MyWebSocket item: webSocketSet){             
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
     
    /**
     * 
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("");
        error.printStackTrace();
    }
     
    /**
     * 
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }
 
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
 
    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }
     
    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }
}
