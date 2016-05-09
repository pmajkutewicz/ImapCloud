package pl.pamsoft.imapcloud.services;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;

import javax.annotation.concurrent.Immutable;

@Immutable
@Getter
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class DownloadChunkContainer {
	public static final DownloadChunkContainer EMPTY = new DownloadChunkContainer(StringUtils.EMPTY, null, null);

	private final String taskId;
	private final FileChunk chunkToDownload;
	private final FileDto destinationDir;
	private final byte[] chunkData;

	public DownloadChunkContainer(String taskId, FileChunk chunkToDownload, FileDto destinationDir) {
		this(taskId, chunkToDownload, destinationDir, null);
	}

	//CSOFF: ParameterNumberCheck
	private DownloadChunkContainer(String taskId, FileChunk chunkToDownload, FileDto destinationDir, byte[] chunkData) {
		this.taskId = taskId;
		this.chunkToDownload = chunkToDownload;
		this.destinationDir = destinationDir;
		this.chunkData = chunkData;
	}
	//CSON

	public static DownloadChunkContainer addChunkData(DownloadChunkContainer dcc, byte[] chunkData) {
		return new DownloadChunkContainer(dcc.getTaskId(), dcc.getChunkToDownload(), dcc.getDestinationDir(), chunkData);
	}
}
