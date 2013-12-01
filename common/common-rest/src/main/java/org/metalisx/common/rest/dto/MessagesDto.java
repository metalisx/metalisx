package org.metalisx.common.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class MessagesDto {

    private List<MessageDto> messages = new ArrayList<MessageDto>();
    
    public MessagesDto() {
    }
    
    public MessagesDto(MessageDto messageDto) {
        this.messages.add(messageDto);
    }

    public void add(MessageDto messageDto) {
        this.messages.add(messageDto);
    }

	public List<MessageDto> getMessages() {
		return messages;
	}

	public MessagesDto addSuccess(String message) {
		messages.add(new MessageDto(message, MessageLevel.SUCCESS_LEVEL));
		return this;
	}

	public MessagesDto addSuccess(String message, String detail) {
		messages.add(new MessageDto(message, detail, MessageLevel.SUCCESS_LEVEL));
		return this;
	}

	public MessagesDto addInfo(String message) {
		messages.add(new MessageDto(message, MessageLevel.INFO_LEVEL));
		return this;
	}

	public MessagesDto addInfo(String message, String detail) {
		messages.add(new MessageDto(message, detail, MessageLevel.INFO_LEVEL));
		return this;
	}

	public MessagesDto addError(String message) {
		messages.add(new MessageDto(message, MessageLevel.ERROR_LEVEL));
		return this;
	}

	public MessagesDto addError(String message, String detail) {
		messages.add(new MessageDto(message, detail, MessageLevel.ERROR_LEVEL));
		return this;
	}

}
