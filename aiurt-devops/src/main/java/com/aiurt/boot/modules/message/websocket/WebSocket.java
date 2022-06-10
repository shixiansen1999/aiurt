package com.aiurt.boot.modules.message.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}")
//此注解相当于设置访问URL
public class WebSocket {

    private Session session;

    private String userId;

    private static CopyOnWriteArraySet<WebSocket> webSockets =new CopyOnWriteArraySet<>();
    private static Map<String,Session> sessionPool = new HashMap<String,Session>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value="userId")String userId) {
        try {
            if (userId != null || !StringUtils.equals(userId,"null")) {
                //if (sessionPool.get(userId)!=null){
                //    sessionPool.get(userId).close();
                //}
                this.userId = userId;
                this.session = session;
                webSockets.add(this);

                sessionPool.put(userId, session);
                log.info("【websocket消息】有新的连接，总数为:" + webSockets.size() + "  id:" + userId);
            }
		} catch (Exception e) {
		}
    }

    @OnClose
    public void onClose() {
        try {
            sessionPool.remove(this.userId);
			webSockets.remove(this);
			log.info("【websocket消息】连接断开，总数为:"+webSockets.size());
		} catch (Exception e) {
		}
    }

    @OnMessage
    public void onMessage(String message) {
    	log.info("【websocket消息】收到客户端消息:"+message);
    	JSONObject obj = new JSONObject();
    	obj.put("code", "200");//业务类型
    	obj.put("msg", "心跳响应");//消息内容
        obj.put("req",message);
    	session.getAsyncRemote().sendText(obj.toJSONString());
    }

    // 此为广播消息
    public void sendAllMessage(String message) {
    	log.info("【websocket消息】广播消息:"+message);
        for(WebSocket webSocket : webSockets) {
            try {
            	if(webSocket.session.isOpen()) {
            		webSocket.session.getAsyncRemote().sendText(message);
            	}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息
    public void sendOneMessage(String userId, String message) {
        Session session = sessionPool.get(userId);
        if (session != null&&session.isOpen()) {
            try {
            	log.info("【websocket消息】 单点消息:"+message +" userId:" + userId);
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息(多人)
    public void sendMoreMessage(String[] userIds, String message) {
    	for(String userId:userIds) {
    		Session session = sessionPool.get(userId);
            if (session != null&&session.isOpen()) {
                try {
                	log.info("【websocket消息】 单点消息:"+message);
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    	}
    }

    public void getSize(){
        sessionPool.remove("null");
        log.info("当前在线个数: {} Id: {} ", webSockets.size(),StringUtils.join(sessionPool.keySet()));
    }

}
