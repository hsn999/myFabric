package com.myfabric.myutil;


import javax.websocket.Session;

public interface WebSocket {
	
	public void onClose();
	public void onMessage(String message, Session session);
	
}
