package pl.pamsoft.imapcloud.services.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.common.TasksProgressService;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import java.io.FileNotFoundException;
import java.util.function.Consumer;

public class FileWithProgressBinder implements Consumer<UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(FileWithProgressBinder.class);

	private FileServices fileServices;
	private TasksProgressService tasksProgressService;

	public FileWithProgressBinder(FileServices fileServices, TasksProgressService tasksProgressService) {
		this.fileServices = fileServices;
		this.tasksProgressService = tasksProgressService;
	}

	@Override
	public void accept(UploadChunkContainer ucc) {
		LOG.debug("Binding file with taskprogress for {}", ucc.getFileDto().getName());
		try {
			File savedFile = fileServices.getFileByUniqueId(ucc.getFileUniqueId());
			tasksProgressService.bindWithFile(ucc.getTaskId(), savedFile.getAbsolutePath(), savedFile.getFileUniqueId());
		} catch (FileNotFoundException ignored) {
		}
	}
}
