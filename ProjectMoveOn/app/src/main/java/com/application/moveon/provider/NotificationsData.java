package com.application.moveon.provider;

public class NotificationsData {

	public String id;
	public String type;
	public String id_user;
	public String id_sender;
	
	public NotificationsData(String id, String type, String id_user, String id_sender) {
		this.id = id;
		this.type = type;
		this.id_user = id_user;
		this.id_sender = id_sender;
	}
}
