package pl.pamsoft.imapcloud.services.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.entity.EntryProgress;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.function.Consumer;

public class FileWithProgressBinder implements Consumer<UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(FileWithProgressBinder.class);

	private FileServices fileServices;
	private Map<String, TaskProgress> taskProgressMap;

	public FileWithProgressBinder(FileServices fileServices, Map<String, TaskProgress> taskProgressMap) {
		this.fileServices = fileServices;
		this.taskProgressMap = taskProgressMap;
	}

	@Override
	@SuppressWarnings("PMD.EmptyCatchBlock")
	public void accept(UploadChunkContainer ucc) {
		LOG.debug("Binding file with taskprogress for {}", ucc.getFileDto().getName());
		try {
			TaskProgress taskProgress = taskProgressMap.get(ucc.getTaskId());
			EntryProgress entryProgress = taskProgress.getProgressMap().get(ucc.getFileDto().getAbsolutePath());
			File savedFile = fileServices.getFileByUniqueId(ucc.getFileUniqueId());
			entryProgress.setFile(savedFile);
		} catch (FileNotFoundException ignored) {
		}
	}
}
