package net.vitic.karaf.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class KarafWebSocket {
  
  @OnWebSocketConnect
  public void onOpen(Session session) {
    KarafWebSocketServlet.registerConnection(session);
  }
  
  @OnWebSocketClose
  public void onClose(Session session, int statusCode, String reason) {
      KarafWebSocketServlet.unregisterConnection(session);
  }
  
  @OnWebSocketMessage
  public void onText(Session session, String message) {
      KarafWebSocketServlet.onMessage(message, session);
  }
}
