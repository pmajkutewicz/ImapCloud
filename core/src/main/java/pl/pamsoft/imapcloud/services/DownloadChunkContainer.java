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
	private final byte[] data;
	private final String chunkHash;
	private final String fileHash;

	public DownloadChunkContainer(String taskId, FileChunk chunkToDownload, FileDto destinationDir) {
		this(taskId, chunkToDownload, destinationDir, null, null, null);
	}

	//CSOFF: ParameterNumberCheck
	private DownloadChunkContainer(String taskId, FileChunk chunkToDownload, FileDto destinationDir, byte[] data, String chunkHash, String fileHash) {
		this.taskId = taskId;
		this.chunkToDownload = chunkToDownload;
		this.destinationDir = destinationDir;
		this.data = data;
		this.chunkHash = chunkHash;
		this.fileHash = fileHash;
	}
	//CSON

	public static DownloadChunkContainer addData(DownloadChunkContainer dcc, byte[] data) {
		return new DownloadChunkContainer(dcc.getTaskId(), dcc.getChunkToDownload(), dcc.getDestinationDir(), data, dcc.getChunkHash(), dcc.getFileHash());
	}

	public static DownloadChunkContainer addChunkHash(DownloadChunkContainer dcc, String chunkHash) {
		return new DownloadChunkContainer(dcc.getTaskId(), dcc.getChunkToDownload(), dcc.getDestinationDir(), dcc.getData(), chunkHash, dcc.getFileHash());
	}

	public static DownloadChunkContainer addFileHash(DownloadChunkContainer dcc, String fileHash) {
		return new DownloadChunkContainer(dcc.getTaskId(), dcc.getChunkToDownload(), dcc.getDestinationDir(), dcc.getData(), dcc.getChunkHash(), fileHash);
	}
}