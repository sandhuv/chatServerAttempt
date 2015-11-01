package com.myspace.model;

import java.io.StringReader;
import java.util.Calendar;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * simple decoder to decode the messages received on websocket to appropriate chatMessage
 * after converting them to JsonObject
 * @author Vineet Sandhu
 */

public class ChatMessageDecoder implements Decoder.Text<ChatMessage> {
	
	@Override
	public ChatMessage decode(final String textMessage) throws DecodeException {
		ChatMessage chatMessage = new ChatMessage();
		JsonObject obj = Json.createReader(new StringReader(textMessage)).readObject();
		chatMessage.setMessageInfo(obj.getString("message"));
		chatMessage.setFromUser(obj.getString("fromUser"));
		chatMessage.setToFriend(obj.getString("toFriend"));
		chatMessage.setMessageType(obj.getString("messageType"));
		chatMessage.setTimeStamp(Calendar.getInstance());
		return chatMessage;
	}

	@Override
	public boolean willDecode(final String s) {
		return true;
	}
	@Override
	public void init(final EndpointConfig config) {}

	@Override
	public void destroy() {}


}
