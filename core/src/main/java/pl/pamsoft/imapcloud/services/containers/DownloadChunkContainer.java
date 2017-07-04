package pl.pamsoft.imapcloud.services.containers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang.StringUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;

@Immutable
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN", "NM_SAME_SIMPLE_NAME_AS_INTERFACE"})
public class DownloadChunkContainer implements pl.pamsoft.imapcloud.api.containers.DownloadChunkContainer {
	public static final DownloadChunkContainer EMPTY = new DownloadChunkContainer(StringUtils.EMPTY, null, null, null, null);

	private final String taskId;
	private final FileChunk chunkToDownload;
	private final FileDto destinationDir;
	private final String expectedChunkHash;
	private final String expectedFileHash;
	@SuppressFBWarnings("EI_EXPOSE_REP")
	private final byte[] data;
	private final String chunkHash;
	private final String fileHash;
	//TODO: add chunk size (and file size?)

	public DownloadChunkContainer(String taskId, FileChunk chunkToDownload, FileDto destinationDir, String expectedChunkHash, String expectedFileHash) {
		this(taskId, chunkToDownload, destinationDir, expectedChunkHash, expectedFileHash, new byte[0], null, null);
	}

	//CSOFF: ParameterNumberCheck
	private DownloadChunkContainer(String taskId, FileChunk chunkToDownload, FileDto destinationDir, String expectedChunkHash, String expectedFileHash, byte[] data, String chunkHash, String fileHash) {
		this.taskId = taskId;
		this.chunkToDownload = chunkToDownload;
		this.destinationDir = destinationDir;
		this.expectedChunkHash = expectedChunkHash;
		this.expectedFileHash = expectedFileHash;
		this.data = Arrays.copyOf(data, data.length);
		this.chunkHash = chunkHash;
		this.fileHash = fileHash;
	}
	//CSON

	public static DownloadChunkContainer addData(DownloadChunkContainer dcc, byte[] data) {
		return new DownloadChunkContainer(dcc.getTaskId(), dcc.getChunkToDownload(), dcc.getDestinationDir(), dcc.getExpectedChunkHash(), dcc.getExpectedFileHash(), data, dcc.getChunkHash(), dcc.getFileHash());
	}

	public static DownloadChunkContainer addChunkHash(DownloadChunkContainer dcc, String chunkHash) {
		return new DownloadChunkContainer(dcc.getTaskId(), dcc.getChunkToDownload(), dcc.getDestinationDir(), dcc.getExpectedChunkHash(), dcc.getExpectedFileHash(), dcc.getData(), chunkHash, dcc.getFileHash());
	}

	public static DownloadChunkContainer addFileHash(DownloadChunkContainer dcc, String fileHash) {
		return new DownloadChunkContainer(dcc.getTaskId(), dcc.getChunkToDownload(), dcc.getDestinationDir(), dcc.getExpectedChunkHash(), dcc.getExpectedFileHash(), dcc.getData(), dcc.getChunkHash(), fileHash);
	}

	@Override
	public String getTaskId() {
		return this.taskId;
	}

	public FileChunk getChunkToDownload() {
		return this.chunkToDownload;
	}

	public FileDto getDestinationDir() {
		return this.destinationDir;
	}

	@Override
	public String getExpectedChunkHash() {
		return expectedChunkHash;
	}

	@Override
	public String getExpectedFileHash() {
		return expectedFileHash;
	}

	@Override
	public String getStorageChunkId() {
		return chunkToDownload.getMessageId();
	}

	@Override
	public byte[] getData() {
		return this.data;
	}

	@Override
	public String getChunkHash() {
		return this.chunkHash;
	}

	@Override
	public String getFileHash() {
		return this.fileHash;
	}
}
