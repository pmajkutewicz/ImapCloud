package pl.pamsoft.imapcloud.services;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import pl.pamsoft.imapcloud.dto.FileDto;

import javax.annotation.concurrent.Immutable;

@Immutable
@Getter
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class UploadChunkContainer {
	public static final UploadChunkContainer EMPTY = new UploadChunkContainer(StringUtils.EMPTY, null);

	private final String taskId;
	private final FileDto fileDto;
	private final String fileHash;
	private final String savedFileId;
	private final String fileUniqueId;
	private final long chunkSize;
	private final long currentFileChunkCumulativeSize;
	@SuppressFBWarnings("EI_EXPOSE_REP")
	private final byte[] data;
	private final boolean encrypted;
	private final int chunkNumber;
	private final boolean lastChunk;
	private final String chunkHash;
	private final String messageId;

	public UploadChunkContainer(String taskId, FileDto fileDto) {
		this(taskId, fileDto, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, 0, 0, null, false, 0, false, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	//CSOFF: ParameterNumberCheck
	private UploadChunkContainer(String taskId, FileDto fileDto, String fileHash, String savedFileId, String fileUniqueId, long chunkSize, long currentFileChunkCumulativeSize,
	                             byte[] data, boolean encrypted, int chunkNumber, boolean lastChunk, String chunkHash, String messageId) {
		this.taskId = taskId;
		this.fileDto = fileDto;
		this.fileHash = fileHash;
		this.savedFileId = savedFileId;
		this.fileUniqueId = fileUniqueId;
		this.chunkSize = chunkSize;
		this.currentFileChunkCumulativeSize = currentFileChunkCumulativeSize;
		this.data = data;
		this.encrypted = encrypted;
		this.chunkNumber = chunkNumber;
		this.lastChunk = lastChunk;
		this.chunkHash = chunkHash;
		this.messageId = messageId;
	}
	//CSON

	public static UploadChunkContainer addFileDto(UploadChunkContainer ucc, FileDto file) {
		return new UploadChunkContainer(ucc.getTaskId(), file, ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getChunkSize(), ucc.getCurrentFileChunkCumulativeSize(),
			ucc.getData(), ucc.isEncrypted(), ucc.getChunkNumber(), ucc.isLastChunk(), ucc.getChunkHash(), ucc.getMessageId());
	}

	public static UploadChunkContainer addFileHash(UploadChunkContainer ucc, String fileHash) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), fileHash, ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getChunkSize(), ucc.getCurrentFileChunkCumulativeSize(),
			ucc.getData(), ucc.isEncrypted(), ucc.getChunkNumber(), ucc.isLastChunk(), ucc.getChunkHash(), ucc.getMessageId());
	}

	public static UploadChunkContainer addIds(UploadChunkContainer ucc, String savedFileId, String fileUniqueId) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), ucc.getFileHash(), savedFileId, fileUniqueId, ucc.getChunkSize(), ucc.getCurrentFileChunkCumulativeSize(),
			ucc.getData(), ucc.isEncrypted(), ucc.getChunkNumber(), ucc.isLastChunk(), ucc.getChunkHash(), ucc.getMessageId());
	}

	public static UploadChunkContainer addChunk(UploadChunkContainer ucc, long chunkSize, long currentFileChunkCumulativeSize, byte[] data, int chunkNumber, boolean lastChunk) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), chunkSize, currentFileChunkCumulativeSize,
			data, ucc.isEncrypted(), chunkNumber, lastChunk, ucc.getChunkHash(), ucc.getMessageId());
	}

	public static UploadChunkContainer addChunkHash(UploadChunkContainer ucc, String chunkHash) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getChunkSize(), ucc.getCurrentFileChunkCumulativeSize(),
			ucc.getData(), ucc.isEncrypted(), ucc.getChunkNumber(), ucc.isLastChunk(), chunkHash, ucc.getMessageId());
	}

	public static UploadChunkContainer addEncryptedData(UploadChunkContainer ucc, byte[] encrypted) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getChunkSize(), ucc.getCurrentFileChunkCumulativeSize(),
			encrypted, true, ucc.getChunkNumber(), ucc.isLastChunk(), ucc.getChunkHash(), ucc.getMessageId());
	}

	public static UploadChunkContainer addMessageId(UploadChunkContainer ucc, String messageId) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getChunkSize(), ucc.getCurrentFileChunkCumulativeSize(),
			ucc.getData(), ucc.isEncrypted(), ucc.getChunkNumber(), ucc.isLastChunk(), ucc.getChunkHash(), messageId);
	}

	@Override
	public String toString() {
		return String.format("Chunk %s for file %s (%s bytes)", chunkNumber, fileDto.getAbsolutePath(), data.length);
	}

	public String getFileChunkUniqueId() {
		return String.format("%s.%04d", getFileUniqueId(), getChunkNumber());
	}
}
