package org.metalisx.monitor.web.test.messages;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.metalisx.common.rest.dto.MessageDto;
import org.metalisx.common.rest.dto.MessagesDto;


@Path("/test")
public class TestMessagesRestService {

    @GET
    @Path("/testAlertDefaultContainer")
    @Produces(MediaType.APPLICATION_JSON)
    public MessagesDto testAlertDefaultContainer() {
        MessagesDto messagesDto = new MessagesDto();
        messagesDto.add(new MessageDto("hello error", null, "error"));
        messagesDto.add(new MessageDto("hello info", null, "info"));
        messagesDto.add(new MessageDto("hello success", null, "success"));
        return messagesDto;
    }

    @GET
    @Path("/testSingleAlertTargetContainer")
    @Produces(MediaType.APPLICATION_JSON)
    public MessageDtoWrapper testSingleAlertTargetContainer() {
    	MessageDto messageDto = new MessageDto("singleAlertContainer", "hello info", null, "info"); 
        return new MessageDtoWrapper(messageDto);
    }

    @GET
    @Path("/testAlertTargetContainer")
    @Produces(MediaType.APPLICATION_JSON)
    public MessagesDto testAlertTargetContainer() {
        MessagesDto messagesDto = new MessagesDto();
        messagesDto.add(new MessageDto("alertContainer", "hello error", null, "error"));
        messagesDto.add(new MessageDto("alertContainer", "hello info", null, "info"));
        messagesDto.add(new MessageDto("alertContainer", "hello success", null, "success"));
        return messagesDto;
    }

    @GET
    @Path("/testAlertMultipleTargetContainers")
    @Produces(MediaType.APPLICATION_JSON)
    public MessagesDto testAlertMultipleTargetContainers() {
        MessagesDto messagesDto = new MessagesDto();
        messagesDto.add(new MessageDto("alertContainerA", "hello error for container A", null, "error"));
        messagesDto.add(new MessageDto("alertContainerB", "hello info for containber B", null, "info"));
        messagesDto.add(new MessageDto("alertContainerA", "hello success for container A", null, "success"));
        return messagesDto;
    }

    public class MessageDtoWrapper {
    	
    	private MessageDto message;
    	
    	public MessageDtoWrapper(MessageDto messageDto) {
    		this.message = messageDto;
    	}
    	
    	public MessageDto getMessage() {
    		return message;
    	}
    	
    }
    
}
