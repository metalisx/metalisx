package org.metalisx.monitor.web.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.metalisx.common.rest.dto.ItemDto;
import org.metalisx.common.rest.dto.MessagesDto;
import org.metalisx.monitor.domain.service.MonitorLogService;
import org.metalisx.monitor.file.parser.FileParser;
import org.metalisx.monitor.profiler.interceptor.Profile;
import org.metalisx.monitor.web.producer.property.Value;
import org.metalisx.monitor.web.service.dto.FilenameDto;


/**
 * REST service for loading a log file.
 * 
 * @author Stefan Oude Nijhuis
 */
@Profile
@Path("/load")
public class LogFileLoadRestService {

	@Inject
	@Value("MONITOR_WEB_APPLICATION_LOAD_LOG_FILE")
	private String logFileLLoad;

    @EJB
    MonitorLogService monitorLogService;

    @Inject
    private FileParser fileParser;

    @GET
    @Path("/model")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDto get() {
        FilenameDto filenameDto = new FilenameDto();
        filenameDto.setFilename(logFileLLoad);
        return new ItemDto(filenameDto);
    }

    @POST
    @Path("/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MessagesDto load(FilenameDto filenameDto) {
        MessagesDto messagesDto = new MessagesDto();
        try {
            if (filenameDto != null && filenameDto.getFilename() != null
                    && !"".equals(filenameDto.getFilename().trim())) {
                File file = new File(filenameDto.getFilename());
                if (!file.isFile()) {
                	messagesDto.addError("File does not exists.");
                } else if (!file.canRead()) {
                	messagesDto.addError("File is not readable.");
                } else {
                    fileParser.parse(filenameDto.getFilename());
                    messagesDto.addInfo("Monitor log loaded.");
                }
            } else {
            	messagesDto.addError("Filename is required.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            messagesDto.addError(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            messagesDto.addError(e.getMessage());
        }
        return messagesDto;
    }

}
