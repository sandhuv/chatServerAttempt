package com.myspace.user.bean;

import java.util.HashMap;

import javax.ejb.Local;
import javax.websocket.Session;

import com.myspace.model.*;

/**
 * @author Vineet Sandhu
 */


@Local
public interface IClientSessionBeanLocal {

	

    void setWebSocketSession(Session session);
    
    Session getWebSocketSession();

    void setUser(String user);
    
    String getUser();

    void addContact(String friend);

    void sendContactRequest(String friend);

    void addMessage(ChatMessage msgInfo);
    
    HashMap<String, ChatMessages> getContacts();

    void sendPresenceRequest(int presence);

    void updateFriendPresence(String friend, int presence);
    
    int getUserPresence();
    
    void sendMessageRequest(String message, String fromUser, String toFriend);
}
