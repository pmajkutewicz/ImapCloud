
package pl.pamsoft.imapcloud.imap;

import com.jamonapi.Monitor;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.monitoring.MonHelper;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.search.HeaderTerm;
import java.util.function.Function;

public class FileDeleter implements Function<File, Boolean> {

	private static final Logger LOG = LoggerFactory.getLogger(FileDeleter.class);

	private final GenericObjectPool<Store> connectionPool;
	private final PerformanceDataService performanceDataService;

	public FileDeleter(GenericObjectPool<Store> connectionPool, PerformanceDataService performanceDataService) {
		this.connectionPool = connectionPool;
		this.performanceDataService = performanceDataService;
	}

	@Override
	public Boolean apply(File fileToDelete) {
		Store store = null;
		try {
			LOG.info("Deleting file {}", fileToDelete.getName());
			Monitor monitor = MonHelper.start(MonHelper.DE_FILE_DELETER);
			store = connectionPool.borrowObject();
			String folderName = IMAPUtils.createFolderName(fileToDelete);
			Folder folder = store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME).getFolder(folderName);
			folder.open(Folder.READ_WRITE);

			Message[] messages = folder.search(new HeaderTerm(MessageHeaders.FileId.toString(), fileToDelete.getFileUniqueId()));
			for (Message message : messages) {
				message.setFlag(Flags.Flag.DELETED, true);
			}
			folder.close(IMAPUtils.EXPUNGE);
			double lastVal = MonHelper.stop(monitor);
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.FILE_DELETER, lastVal));
			LOG.debug("File deleted in {}", lastVal);
			return Boolean.TRUE;
		} catch (Exception e) {
			LOG.error("Error in stream", e);
			try {
				connectionPool.invalidateObject(store);
			} catch (Exception e1) {
				LOG.error("Error invalidating", e1);
			}
		} finally {
			if (null != store) {
				connectionPool.returnObject(store);
			}
		}
		return Boolean.FALSE;
	}

}
