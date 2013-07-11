package org.metalisx.common.rest.dto;

public class MessageDto {

	private String message;
	
	private String level;

	private String detail;
	
	public MessageDto(String message, String level) {
		this(message, null, level);
	}

	public MessageDto(String message, String detail, String level) {
		this.message = message;
		this.detail = detail;
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public String getLevel() {
		return level;
	}

	public String getDetail() {
		return detail;
	}
	
}
