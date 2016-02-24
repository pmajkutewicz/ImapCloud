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
	@SuppressFBWarnings("EI_EXPOSE_REP")
	private final byte[] data;
	private final int chunkNumber;
	private final String chunkHash;
	private final String fileChunkUniqueId;
	private final String messageId;

	public UploadChunkContainer(String taskId, FileDto fileDto) {
		this(taskId, fileDto, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, 0, null, 0, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	private UploadChunkContainer(String taskId, FileDto fileDto, String fileHash, String savedFileId, String fileUniqueId, long chunkSize,
	                             byte[] data, int chunkNumber, String chunkHash, String fileChunkUniqueId, String messageId) {
		this.taskId = taskId;
		this.fileDto = fileDto;
		this.fileHash = fileHash;
		this.savedFileId = savedFileId;
		this.fileUniqueId = fileUniqueId;
		this.chunkSize = chunkSize;
		this.data = data;
		this.chunkNumber = chunkNumber;
		this.chunkHash = chunkHash;
		this.fileChunkUniqueId = fileChunkUniqueId;
		this.messageId = messageId;
	}

	public static UploadChunkContainer addFileDto(UploadChunkContainer ucc, FileDto file) {
		return new UploadChunkContainer(ucc.getTaskId(), file, ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getChunkSize(),
			ucc.getData(), ucc.getChunkNumber(), ucc.getChunkHash(), ucc.getFileChunkUniqueId(), ucc.getMessageId());
	}

	public static UploadChunkContainer addFileHash(UploadChunkContainer ucc, String fileHash) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), fileHash, ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getChunkSize(),
			ucc.getData(), ucc.getChunkNumber(), ucc.getChunkHash(), ucc.getFileChunkUniqueId(), ucc.getMessageId());
	}

	public static UploadChunkContainer addIds(UploadChunkContainer ucc, String savedFileId, String fileUniqueId) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), ucc.getFileHash(), savedFileId, fileUniqueId, ucc.getChunkSize(),
			ucc.getData(), ucc.getChunkNumber(), ucc.getChunkHash(), ucc.getFileChunkUniqueId(), ucc.getMessageId());
	}

	public static UploadChunkContainer addChunk(UploadChunkContainer ucc, long chunkSize, byte[] data, int chunkNumber) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), chunkSize,
			data, chunkNumber, ucc.getChunkHash(), ucc.getFileChunkUniqueId(), ucc.getMessageId());
	}

	public static UploadChunkContainer addChunkHash(UploadChunkContainer ucc, String chunkHash) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getChunkSize(),
			ucc.getData(), ucc.getChunkNumber(), chunkHash, ucc.getFileChunkUniqueId(), ucc.getMessageId());
	}

	public static UploadChunkContainer addEncryptedData(UploadChunkContainer ucc, byte[] encoded) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getChunkSize(),
			encoded, ucc.getChunkNumber(), ucc.getChunkHash(), ucc.getFileChunkUniqueId(), ucc.getMessageId());
	}

	public static UploadChunkContainer addChunkId(UploadChunkContainer ucc, String fileChunkUniqueId) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getChunkSize(),
			ucc.getData(), ucc.getChunkNumber(), ucc.getChunkHash(), fileChunkUniqueId, ucc.getMessageId());
	}

	public static UploadChunkContainer addMessageId(UploadChunkContainer ucc, String messageId) {
		return new UploadChunkContainer(ucc.getTaskId(), ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getChunkSize(),
			ucc.getData(), ucc.getChunkNumber(), ucc.getChunkHash(), ucc.getFileUniqueId(), messageId);
	}

	@Override
	public String toString() {
		return String.format("Chunk %s for file %s (%s bytes)", chunkNumber, fileDto.getAbsolutePath(), data.length);
	}


}
