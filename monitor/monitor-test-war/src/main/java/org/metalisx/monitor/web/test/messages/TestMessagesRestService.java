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
    @Path("/testMessagesDefaultContainer")
    @Produces(MediaType.APPLICATION_JSON)
    public MessagesDto testMessagesDefaultContainer() {
        MessagesDto messagesDto = new MessagesDto();
        messagesDto.add(new MessageDto("hello error", null, "error"));
        messagesDto.add(new MessageDto("hello info", null, "info"));
        messagesDto.add(new MessageDto("hello success", null, "success"));
        return messagesDto;
    }

    @GET
    @Path("/testSingleMessageNamedContainer")
    @Produces(MediaType.APPLICATION_JSON)
    public MessageDto testSingleMessageNamedContainer() {
        return new MessageDto("singleMessageContainer", "hello info", null, "info");
    }

    @GET
    @Path("/testMessagesNamedContainer")
    @Produces(MediaType.APPLICATION_JSON)
    public MessagesDto testMessagesNamedContainer() {
        MessagesDto messagesDto = new MessagesDto();
        messagesDto.add(new MessageDto("messagesNamedContainer", "hello error", null, "error"));
        messagesDto.add(new MessageDto("messagesNamedContainer", "hello info", null, "info"));
        messagesDto.add(new MessageDto("messagesNamedContainer", "hello success", null, "success"));
        return messagesDto;
    }

    @GET
    @Path("/testMessagesMultipleNamedContainers")
    @Produces(MediaType.APPLICATION_JSON)
    public MessagesDto testMessagesMultipleNamedContainers() {
        MessagesDto messagesDto = new MessagesDto();
        messagesDto.add(new MessageDto("messagesNamedContainerA", "hello error for container A", null, "error"));
        messagesDto.add(new MessageDto("messagesNamedContainerB", "hello info for containber B", null, "info"));
        messagesDto.add(new MessageDto("messagesNamedContainerA", "hello success for container A", null, "success"));
        return messagesDto;
    }

}
