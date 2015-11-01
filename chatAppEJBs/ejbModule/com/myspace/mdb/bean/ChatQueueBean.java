package com.myspace.mdb.bean;

import com.myspace.helper.InBackground;
import com.myspace.model.ChatMessage;
import com.myspace.user.bean.IClientSessionBeanProviderLocal;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * Message Driven Bean as consumer for jms/chatQueue
 * It will consume messages from chat queue, & fire CDI events to web socket end point
 * @author Vineet Sandhu
 */
@MessageDriven(
	    activationConfig = {
	      @ActivationConfigProperty(propertyName = "destinationType",propertyValue = "javax.jms.Queue"),
	      @ActivationConfigProperty(propertyName = "destination",propertyValue = "chatQueue")
	    },mappedName = "jms/chatQueue")
public class ChatQueueBean implements MessageListener {
	
    @EJB
    private IClientSessionBeanProviderLocal userAccessBean;
    
    @Inject @InBackground Event<ChatMessage> chatMsgEvent;
    
    //Process the message containing user,friend,message,timestamp to send
    //chat message to friend.
    public void onMessage(Message message) {
        System.out.println("firing cdi event");
    	
        ObjectMessage objMsg = null;
        ChatMessage msgDetails = null;
        
        try {
            System.out.println("chat queue");
            if (message instanceof ObjectMessage) {
                objMsg = (ObjectMessage) message;
                msgDetails = (ChatMessage) objMsg.getObject();
                System.out.println("Received Message from chat queue : " + msgDetails.getMessageInfo() +"from User : " 
            			+msgDetails.getFromUser() + "toUser : " +msgDetails.getToFriend()) ;
                
                chatMsgEvent.fire(msgDetails);
                
            } else {
                System.out.println("Message of wrong type at chat queue: " + 
                        message.getClass().getName());
            }
            
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }    
}
