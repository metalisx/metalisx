package org.metalisx.common.rest.dto;

public class MessageDto {

	private String message;
	
	private String level;
	
	public MessageDto(String message, String level) {
		this.message = message;
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public String getLevel() {
		return level;
	}

}
