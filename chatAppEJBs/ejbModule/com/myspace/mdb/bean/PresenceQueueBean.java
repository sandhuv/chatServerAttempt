package com.myspace.mdb.bean;

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.myspace.model.ChatMessage;
import com.myspace.model.ChatMessages;
import com.myspace.user.bean.IClientSessionBeanLocal;
import com.myspace.user.bean.IClientSessionBeanProviderLocal;

/**
 * This class is a Message Driven Bean that receives messages from presence
 * queue. This is used to accept user's presence status and ultimately updates
 * status to all of its friends.
 */
@MessageDriven(
	    activationConfig = {
	      @ActivationConfigProperty(propertyName = "destinationType",propertyValue = "javax.jms.Queue"),
	      @ActivationConfigProperty(propertyName = "destination",propertyValue = "presenceQueue")
	    },mappedName = "jms/presenceQueue")
public class PresenceQueueBean implements MessageListener {
    
	@EJB
    private IClientSessionBeanProviderLocal clientSessionProvider;
 
    public PresenceQueueBean() {
    }
    
    //Process the message containing friends list,presence status to send
    //presence status to all friends.
    public void onMessage(Message message) {

        ObjectMessage objMsg = null;
        ChatMessages msgFriends = null;

        try {
            System.out.println("presence queue");
            if (message instanceof ObjectMessage) {
                objMsg = (ObjectMessage) message;
                msgFriends = (ChatMessages)objMsg.getObject();
                processMessage(msgFriends);
                
            } else {
                System.out.println("Message of wrong type at presence queue: " +
                        message.getClass().getName());
            }

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    //Call friends' stateful bean and update friends' presence status    
    private void processMessage(ChatMessages msgInfo) {
        
        List<ChatMessage> messages = msgInfo.getMessage();
        for(int i=0; i<messages.size(); i++) {
        	
        	
        	// get ChatMessage, which each contain contacts to update the status
        	ChatMessage chatMessageContainingContactsToSendPresenceStatus = messages.get(i);
        	
        	// get Session bean for each client we want to update the status
        	IClientSessionBeanLocal usb = clientSessionProvider.getUser(chatMessageContainingContactsToSendPresenceStatus.getToFriend());
        	
        	// update status of statefullsession bean... 
        	usb.updateFriendPresence(messages.get(i).getFromUser(), msgInfo.getPresence());                    
        }
                
    }
    
}
