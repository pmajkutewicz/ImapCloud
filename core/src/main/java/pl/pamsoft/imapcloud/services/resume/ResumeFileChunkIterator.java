package pl.pamsoft.imapcloud.services.resume;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.upload.FileChunkIterator;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class ResumeFileChunkIterator extends FileChunkIterator {

	private Queue<Long> chunkSizeQueue;

	public ResumeFileChunkIterator(UploadChunkContainer ucc, int fetchSize, List<FileChunk> existingFileChunks, MonitoringHelper monitoringHelper) {
		super(ucc, fetchSize, monitoringHelper);
		mapToChunkList(existingFileChunks);
	}

	@SuppressFBWarnings("PCOA_PARTIALLY_CONSTRUCTED_OBJECT_ACCESS")
	public ResumeFileChunkIterator(UploadChunkContainer ucc, int fetchSize, int deviation, List<FileChunk> existingFileChunks, MonitoringHelper monitoringHelper) {
		super(ucc, fetchSize, deviation, monitoringHelper);
		mapToChunkList(existingFileChunks);
		generateNextFetchSize();
	}

	private void mapToChunkList(List<FileChunk> existingFileChunks) {
		this.chunkSizeQueue = new LinkedList<>(existingFileChunks.stream().map(FileChunk::getOrgSize).collect(Collectors.toList()));
	}

	@Override
	protected void generateNextFetchSize() {
		if (null != chunkSizeQueue) {
			Long poll = chunkSizeQueue.poll();
			if (null != poll) {
				setFetchSize(poll.intValue());
				return;
			}
		}
		super.generateNextFetchSize();
	}
}
