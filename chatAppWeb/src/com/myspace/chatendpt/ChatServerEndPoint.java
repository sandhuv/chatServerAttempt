package com.myspace.chatendpt;

import javax.annotation.*;
import javax.ejb.Asynchronous;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.Session;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.myspace.model.ChatMessageDecoder;
import com.myspace.model.ChatMessageEncoder;
import com.myspace.user.bean.IClientSessionBeanProviderLocal;
import com.myspace.helper.References;
import com.myspace.model.ChatMessage;
import com.myspace.helper.Constants;
import com.myspace.helper.InBackground;

import javax.enterprise.event.Observes;


/**
 * WebSocket Endpoint 
 * @onOpen : when client opens session
 * @OnMessage : client sends message (chatmessage, login, contact request, presence)
 * 
 * ChatMessageDecoder is applied when message is received. 
 * @author Vineet Sandhu
 *
 */

@Stateless
@Asynchronous
@ServerEndpoint(value = "/chat", decoders = ChatMessageDecoder.class, encoders=ChatMessageEncoder.class)
public class ChatServerEndPoint implements Serializable{
	
	private static final long serialVersionUID = 1L;

	//Instance of UserAccessBean which maintains users Stateful Sessions
	IClientSessionBeanProviderLocal clientSessionProviderLocal = new References().lookupClientSessionBeanProviderLocal();
	
	private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
	
	@PostConstruct
	public void acquire() {
		System.out.printf("%s.acquire() called in thread: [%s]\n",
				getClass().getSimpleName(), 
				Thread.currentThread().getName());
	}

	@PreDestroy
	public void release() {
		System.out.printf("%s.release() called\n", getClass().getSimpleName());
	}

	@OnOpen
	public void open(Session session) {
		System.out.printf("%s.open() called session=%s\n", getClass().getSimpleName(), session );
		sessions.add(session);
	}

	@OnClose
	public void close(Session session) {
		try {
			session.getBasicRemote().sendText("WebSocket Session closed");
			sessions.remove(session);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		System.out.printf("%s.close() called session=%s\n", getClass().getSimpleName(), session);
	}


	@OnMessage
	public void onMessage(final Session session, final ChatMessage chatMessage) {
		
		System.out.println("________on message received_____________" +chatMessage.getMessageInfo());
		
		switch(chatMessage.getMessageType()){
			case Constants.SEND_FRIEND_REQUEST:
				System.out.println("__Constants.SEND_FRIEND_REQUEST:__" + " from User : "+ chatMessage.getFromUser() +" toFriend : " +chatMessage.getToFriend());
				System.out.println("Websocket Session SEND_FRIEND_REQUEST" +session);
				
				clientSessionProviderLocal.getUser(chatMessage.getFromUser()).setWebSocketSession(session);
				clientSessionProviderLocal.getUser(chatMessage.getFromUser()).
									sendContactRequest(chatMessage.getToFriend());
				break;
				
			case Constants.SEND_MESSAGE_REQUEST:
				clientSessionProviderLocal.getUser(chatMessage.getFromUser()).
                					sendMessageRequest(chatMessage.getMessageInfo(),chatMessage.getFromUser(),chatMessage.getToFriend());
                break;
                
			case Constants.SEND_PRESENCESTATUS_REQUEST:
				// hardcoded for online status (forgot to add in message, tried to show the support with presence JMS queues)
				// Need to send the status information to all the connected clients in contact list
				clientSessionProviderLocal.getUser(chatMessage.getFromUser()).sendPresenceRequest(1);
                break;
                
			case Constants.SEND_USER_LOGIN_REQUEST:
				System.out.println("Websocket Session SEND_USER_LOGIN_REQUEST" +session);
				//userAccessBeanLocal.getUser(chatMessage.getFromUser()).setWebSocketSession(session);
				clientSessionProviderLocal.getUser(chatMessage.getFromUser());
				break;
				
			default: 
				System.err.println("Invalid Message type  __ " +chatMessage.getMessageType());
            	break; 
		}
	}

	
	 public void onJMSMessage(@Observes @InBackground ChatMessage msg) {
	        Logger.getLogger(ChatServerEndPoint.class.getName()).log(Level.INFO, "Got JMS Message at WebSocket!");
	        System.out.println("___Got JMS Message at WebSocket to send further....." +msg.getFromUser());
	        try {
	        	if(clientSessionProviderLocal.getUser(msg.getToFriend()).getWebSocketSession() !=null){
	        		Session wsSession = clientSessionProviderLocal.getUser(msg.getToFriend()).getWebSocketSession();
	        		wsSession.getBasicRemote().sendObject(new ChatMessageEncoder().encode(msg));
	        	}
			} catch (IOException | EncodeException  | EJBException e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "onJMSMessage failed", e.getMessage());
			}
	 }
	 
}