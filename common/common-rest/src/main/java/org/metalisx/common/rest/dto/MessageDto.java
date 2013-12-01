package org.metalisx.common.rest.dto;

public class MessageDto {

	private String id;
	
	private String message;
	
	private String level;

	private String detail;
	
	public MessageDto(String message, String level) {
		this(null, message, null, level);
	}

	public MessageDto(String message, String detail, String level) {
		this(null, message, detail, level);
	}

	public MessageDto(String id, String message, String detail, String level) {
		this.id = id;
		this.message = message;
		this.detail = detail;
		this.level = level;
	}
	
	public String getId() {
		return id;
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
