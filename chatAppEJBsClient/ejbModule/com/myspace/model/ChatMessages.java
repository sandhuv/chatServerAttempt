package com.myspace.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class maintains a list of message related information. This acts as a
 * transfer object among various beans.
 */
public class ChatMessages implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<ChatMessage> message;
    private int presence;
    
    //Initialize properites
    public ChatMessages() {
        message = new ArrayList<>();
    }    

    //Initialize properites
    public ChatMessages(int present) {
        message = new ArrayList<>();
        this.presence = present;
    }    

    //Return message list
    public List<ChatMessage> getMessage() {
        return message;
    }
    
    //Add another message to the existing list
    public boolean addMessage(ChatMessage msgDetails) {
        return message.add(msgDetails);
    }

    /**
     * @return the presence
     */
    public int getPresence() {
        return presence;
    }

    /**
     * @param presence the presence to set
     */
    public void setPresence(int presence) {
        this.presence = presence;
    }
    
}
