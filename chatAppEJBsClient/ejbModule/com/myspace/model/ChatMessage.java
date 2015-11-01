package com.myspace.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 
 * class holds the serializable Chatmessage which can pass among the beans.
 * @author Vineet Sandhu
 *
 */
public class ChatMessage implements Serializable {
    
	
	private static final long serialVersionUID = 1L;
	private String fromUser;
    private String ToFriend;
    private String messageInfo;
    private Calendar timeStamp;
    private String messageType;

    
    public String getMessageType(){
    	return messageType;
    }

    public void setMessageType(String type){
    	this.messageType = type;
    }
    /**
     * @return the timeStamp
     */
    public Calendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(Calendar timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the messageInfo
     */
    public String getMessageInfo() {
        return messageInfo;
    }

    /**
     * @param messageInfo the messageInfo to set
     */
    public void setMessageInfo(String messageInfo) {
        this.messageInfo = messageInfo;
    }

    /**
     * @return the ToFriend
     */
    public String getToFriend() {
        return ToFriend;
    }

    /**
     * @param ToFriend the ToFriend to set
     */
    public void setToFriend(String ToFriend) {
        this.ToFriend = ToFriend;
    }    

    /**
     * @return the fromUser
     */
    public String getFromUser() {
        return fromUser;
    }

    /**
     * @param fromUser the fromUser to set
     */
    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }
    
}
