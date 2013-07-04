package org.metalisx.monitor.web.service;

import java.io.File;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.metalisx.common.rest.dto.ItemDto;
import org.metalisx.common.rest.dto.ItemsDto;
import org.metalisx.common.rest.dto.MessagesDto;
import org.metalisx.monitor.domain.service.MonitorLogService;
import org.metalisx.monitor.file.listener.FileListeners;
import org.metalisx.monitor.file.parser.LineParser;
import org.metalisx.monitor.profiler.interceptor.Profile;
import org.metalisx.monitor.web.producer.property.Value;
import org.metalisx.monitor.web.service.dto.FilenameDto;

/**
 * REST service for starting and stopping file listeners.
 * 
 * @author Stefan Oude Nijhuis
 */
@Profile
@Path("/listener")
public class LogFileListenerRestService {

	private static FileListeners fileListeners = new FileListeners();

	@Inject
	@Value("MONITOR_WEB_APPLICATION_LISTENER_LOG_FILE")
	private String logFileListener;

	@Inject
	private LineParser lineParser;

	@EJB
	private MonitorLogService monitorLogService;

	@GET
	@Path("/model")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemDto get() {
		FilenameDto filenameDto = new FilenameDto();
		filenameDto.setFilename(logFileListener);
		return new ItemDto(filenameDto);
	}

	@POST
	@Path("/start")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MessagesDto start(FilenameDto filenameDto) {
		MessagesDto messagesDto = new MessagesDto();
		if (filenameDto != null && filenameDto.getFilename() != null && !"".equals(filenameDto.getFilename().trim())) {
			if (fileListeners.isListening(filenameDto.getFilename())) {
				messagesDto.addError("Listerner already active for this file.");
			} else {
				File file = new File(filenameDto.getFilename());
				if (!file.isFile()) {
					messagesDto.addError("File does not exists.");
				} else if (!file.canRead()) {
					messagesDto.addError("File is not readable.");
				} else {
					fileListeners.startListening(filenameDto.getFilename(), lineParser, monitorLogService);
					messagesDto.addInfo("Listener started.");
				}
			}
		} else {
			messagesDto.addError("Filename is required.");
		}
		return messagesDto;
	}

	@POST
	@Path("/stop")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MessagesDto stop(FilenameDto filenameDto) {
		MessagesDto messagesDto = new MessagesDto();
		if (filenameDto != null && filenameDto.getFilename() != null && !"".equals(filenameDto.getFilename().trim())) {
			fileListeners.stopListening(filenameDto.getFilename());
			messagesDto.addInfo("Listener stopped.");
		} else {
			messagesDto.addError("Filename is required.");
		}
		return messagesDto;
	}

	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemsDto getList() {
		ItemsDto itemsDto = new ItemsDto();
		itemsDto.setItems(fileListeners.getListenerFilenames());
		return itemsDto;
	}

}
