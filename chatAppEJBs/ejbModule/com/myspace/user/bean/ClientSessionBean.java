package com.myspace.user.bean;

import com.myspace.model.ChatMessage;
import com.myspace.model.ChatMessages;
import com.myspace.helper.Constants;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import javax.ejb.Stateful;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;

/**
 * Stateful Session Bean that maintains state for each chat client and provides business logic.
 * State is stored in the session instance field values
 * e.g. this will maintain state of 
 * 1. mapping between each < user->ChatMessages >
 * 2. contacts of each user
 * 3. presence of each user
 * 4. websocket session of each user
 * @author Vineet Sandhu
 */


@Stateful
public class ClientSessionBean implements IClientSessionBeanLocal {

	final private QueueConnection connection;
	final private QueueSession session;
	
	
	//JMS queues
	final private Queue chatQueue;
	final private Queue contactQueue;
	final private Queue presenceQueue;
	
	
	private String user;
    private int userPresence = Constants.ONLINE;
    private ChatMessages messages;
   
    //Mapping between user & chatMessages
    private HashMap<String, ChatMessages> contacts;

    //Websocket Session for clients
    private javax.websocket.Session webSocketsession;
    
    
	public ClientSessionBean() throws Exception {
		InitialContext initCtx = new InitialContext();
		QueueConnectionFactory connectionFactory = (QueueConnectionFactory)initCtx.lookup("jms/ConnectionFactory");
		connection = connectionFactory.createQueueConnection();
		connection.start();
		session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//These queues need to be define in glassfish resources. 
		chatQueue = (Queue)initCtx.lookup("jms/chatQueue");
		contactQueue = (Queue)initCtx.lookup("jms/contactQueue");
		presenceQueue = (Queue)initCtx.lookup("jms/presenceQueue");
	}
	
	
    /**
     * Send ChatMessage to JMS chat queue.
     */
    @Override
    public void sendMessageRequest(String message, String fromUser, String toFriend) {
                
        try {
    
        	
        	System.out.println("========message=========" +message);
            ChatMessage msgDetails = new ChatMessage();
            msgDetails.setFromUser(fromUser);
            msgDetails.setToFriend(toFriend);
            msgDetails.setMessageType("chat");
            msgDetails.setMessageInfo(message);
            msgDetails.setTimeStamp(Calendar.getInstance());
                      
            System.out.println("___Chat Message Reached here...for putting in jMS queue___" +msgDetails.getMessageInfo());
            try {
				session.createProducer(chatQueue).send(session.createObjectMessage(msgDetails));
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}           
        } catch (JMSRuntimeException ex) {
            ex.printStackTrace();
            System.out.println("Error during the delivery of chat message");
        }
        
    }
    
    
    /**
     * Send ChatMessage including fromUser - toFriend  to JMS contacts queue.
     */
    @Override
    public void sendContactRequest(String friend) {
        try {

        	System.out.println("user in userSessionBean" +user);
            ChatMessage msgDetails = new ChatMessage();
            msgDetails.setFromUser(user);
            msgDetails.setToFriend(friend);
            msgDetails.setMessageType("contact");
                        
            session.createProducer(contactQueue).send(session.createObjectMessage(msgDetails));        
        } catch (JMSException ex) {
            ex.printStackTrace();
            System.out.println("Error during the delivery of friend request");
        }
        
    }
    
    
    /**Send message comprising from user, friends' list, presence to JMS presence queue.*/
    @Override
    public void sendPresenceRequest(int presence) {
        
        try {
            ChatMessage msgDetails;
            //Proceed only if this user has friends
            if(!contacts.isEmpty()) {
            
            	//mapping between user -> ChatMessage(toFriend), basically to find out to which contact Presence status need to updated.
                ChatMessages friends = new ChatMessages(presence);
                Iterator<String> friendNames = contacts.keySet().iterator();
                
                while(friendNames.hasNext()) {
                

                	String to = friendNames.next();
                	
                	// depict the contact list of user here,  as plugin is not there in client right now...
                	// We can look into glassfish logs for Contact list of user
                	System.out.println("Contact List for user " + user + " is  " +to);
                	
                    msgDetails = new ChatMessage();
                    msgDetails.setFromUser(user);
                    msgDetails.setToFriend(to);
                    
                    friends.addMessage(msgDetails);

                }
                // set the presence status of the user.
                setUserPresence(presence);
                
                // send the messages(each message will have a "friend") to the presenceQueue
                session.createProducer(presenceQueue).send(session.createObjectMessage(friends));                          
            }
        } catch (JMSException ex) {
            ex.printStackTrace();
            System.out.println("Error during the delivery of presence request");
        }        
        
    }
    
    //Initialize properites
    @Override
    public void setUser(String user) {
        this.user = user;
        contacts = new HashMap<String, ChatMessages>();
        System.out.println("set user:" + this.user);
    }

    //returns user
    @Override
    public String getUser() {
        return this.user;
    }

    //Create an entry for friend to accomodate chat messages and presence
    @Override
    public void addContact(String friend) {
        //Entry needs to be created only once
        if(!contacts.containsKey(friend)) {
            messages = new ChatMessages(Constants.ONLINE);
            contacts.put(friend, messages);
        }
        
    }

    //Add a new chat message to user
    @Override
    public void addMessage(ChatMessage msgInfo) {
    	for (String contact : contacts.keySet()){
    		System.out.println("contacts in user session contact list" +contact );
    	}
    	contacts.get(msgInfo.getFromUser()).addMessage(msgInfo);
    }

    /**
     * @return the contacts
     */
    public HashMap<String, ChatMessages> getContacts() {
        return contacts;
    }

    //Obtain friend information and update its presence
    @Override
    public void updateFriendPresence(String friend, int presence) {
        contacts.get(friend).setPresence(presence);
    }

    /**
     * @return the userPresence
     */
   public int getUserPresence() {
        return userPresence;
    }

    /**
     * @param userPresence the userPresence to set
     */
    public void setUserPresence(int userPresence) {
        this.userPresence = userPresence;
    }
    
    @Override
    public void setWebSocketSession(javax.websocket.Session session){
    	this.webSocketsession = session;
    }
    
    @Override
    public javax.websocket.Session getWebSocketSession(){
    	return webSocketsession;
    }
}
