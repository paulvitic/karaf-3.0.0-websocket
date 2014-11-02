package net.vitic.karaf.websocket;

import com.hazelcast.config.Config;
import com.hazelcast.core.*;
import com.hazelcast.instance.GroupProperties;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("serial")
public class KarafWebSocketServlet extends WebSocketServlet {

    // node hazelcast
    private static HazelcastInstance hazelcastNode;

    // topic
    private static ITopic<Object> hazelcastTopic;

    private static final Set<Session> clientSessions    = Collections.synchronizedSet(new HashSet<Session>());

    public static final long          WEBSOCKET_TIMEOUT = -1;
  
    @Override
    public void configure(WebSocketServletFactory factory) {
    factory.register(KarafWebSocket.class);
    }

    @Override
    public void init() throws javax.servlet.ServletException {
        super.init();
        System.out.println("******************************** WebsocketActivator STARTUP **********************************");

        Config config = new Config();
        config.setProperty(GroupProperties.PROP_VERSION_CHECK_ENABLED, "false");
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);

        hazelcastNode = Hazelcast.newHazelcastInstance(config);
        hazelcastTopic = hazelcastNode.getTopic("cloud");
        hazelcastTopic.addMessageListener(new MessageListener<Object>() {

            public void onMessage(Message<Object> arg0) {
                publishToAll(arg0.getMessageObject().toString());
            }
        });
    }

    @Override
    public void destroy() {
        hazelcastNode.shutdown();
        System.out.println("******************************** WebsocketActivator SHUTDOWN **********************************");
        super.destroy();
    }


    public static void onMessage(String message, Session client) {
        hazelcastTopic.publish(message);
    }

    public static void registerConnection(Session session) {
        session.setIdleTimeout(KarafWebSocketServlet.WEBSOCKET_TIMEOUT);
        clientSessions.add(session);

    }

    public static void unregisterConnection(Session session) {
        clientSessions.remove(session);
    }

    /**
     * méthode qui envoie le message à tous les clients connectés
     *
     * @param message
     */
    public void publishToAll(String message) {
        StringBuffer buf = new StringBuffer(message);
        synchronized (clientSessions) {
            System.out.println("send message '" + message + "' to " + clientSessions.size() + " clients");

            Iterator<Session> it = clientSessions.iterator();
            while (it.hasNext()) {
                Session session = it.next();
                try {
                    session.getRemote().sendString(buf.toString());
                }
                catch (IOException exception) {
                    clientSessions.remove(session);
                    it = clientSessions.iterator();
                    try {
                        session.close();
                    }
                    catch (Exception e) {
                    }
                }
            }
        }

    }
}