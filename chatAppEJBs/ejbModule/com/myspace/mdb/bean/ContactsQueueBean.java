package com.myspace.mdb.bean;

import com.myspace.model.ChatMessage;
import com.myspace.user.bean.IClientSessionBeanProviderLocal;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;



/**
 * Message Driven Bean as consumer for jms/contactQueue
 * It will consume messages from contact queue, & invoke corresponding session beans for user & contacts
 * @author Vineet Sandhu
 */


@MessageDriven(
	    activationConfig = {
	      @ActivationConfigProperty(propertyName = "destinationType",
	            propertyValue = "javax.jms.Queue"),
	      @ActivationConfigProperty(propertyName = "destination",
	            propertyValue = "contactQueue")
	    },
	    mappedName = "jms/contactQueue")
public class ContactsQueueBean implements MessageListener {
    
    @EJB
    private IClientSessionBeanProviderLocal clientSessionProvider;

    public ContactsQueueBean() {
    }
    
    //Process the message containing user,friend to add each other as contact.
    public void onMessage(Message message) {
        
        ObjectMessage objMsg = null;
        ChatMessage msgDetails = null;
        
        try {
            System.out.println("contacts queue");
            if (message instanceof ObjectMessage) {
                objMsg = (ObjectMessage) message;
                msgDetails = (ChatMessage) objMsg.getObject();
                
                System.out.println("Received Message from contacts queue: " +
                        msgDetails.getFromUser() + " sent friend request to  " +
                        msgDetails.getToFriend());
                
                processMessage(msgDetails);
                
            } else {
                System.out.println("Message of wrong type at contacts queue: " +
                        message.getClass().getName());
            }
            
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    //Call both user and friend stateful beans and update their contact lists
    //with friend information
    private void processMessage(ChatMessage msgInfo) {
    	
        //friend adding user
    	clientSessionProvider.getUser(msgInfo.getToFriend()).addContact(msgInfo.getFromUser());

        //user adding friend
    	clientSessionProvider.getUser(msgInfo.getFromUser()).addContact(msgInfo.getToFriend());
        
    }

    
}
