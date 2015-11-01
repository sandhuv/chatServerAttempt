package com.myspace.model;

import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ChatMessageEncoder implements Encoder.Text<ChatMessage> {
		@Override
	public String encode(final ChatMessage chatMessage) throws EncodeException {
		System.out.println("encoder is called when " +chatMessage.getFromUser());
		return Json.createObjectBuilder()
				.add("message", chatMessage.getMessageInfo())
				.add("fromUser", chatMessage.getFromUser())
				.add("toFriend", chatMessage.getToFriend())
				.add("messageType", chatMessage.getMessageType())
				.add("received", chatMessage.getTimeStamp().toString()).build()
				.toString();
	}
		@Override
		public void init(final EndpointConfig config) {
		}

		@Override
		public void destroy() {
		}


}

