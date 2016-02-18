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
	public static final UploadChunkContainer EMPTY = new UploadChunkContainer(null);

	private final FileDto fileDto;
	private final String fileHash;
	private final String savedFileId;
	private final String fileUniqueId;
	@SuppressFBWarnings("EI_EXPOSE_REP")
	private final byte[] data;
	private final int chunkNumber;
	private final String chunkHash;
	private final String fileChunkUniqueId;
	private final String messageId;

	public UploadChunkContainer(FileDto fileDto) {
		this(fileDto, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, 0, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	private UploadChunkContainer(FileDto fileDto, String fileHash, String savedFileId, String fileUniqueId,
	                             byte[] data, int chunkNumber, String chunkHash, String fileChunkUniqueId, String messageId) {
		this.fileDto = fileDto;
		this.fileHash = fileHash;
		this.savedFileId = savedFileId;
		this.fileUniqueId = fileUniqueId;
		this.data = data;
		this.chunkNumber = chunkNumber;
		this.chunkHash = chunkHash;
		this.fileChunkUniqueId = fileChunkUniqueId;
		this.messageId = messageId;
	}

	public static UploadChunkContainer addFileHash(UploadChunkContainer ucc, String fileHash) {
		return new UploadChunkContainer(ucc.getFileDto(), fileHash, ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getData(), ucc.getChunkNumber(), ucc.getChunkHash(), ucc.getFileChunkUniqueId(), ucc.getMessageId());
	}

	public static UploadChunkContainer addIds(UploadChunkContainer ucc, String savedFileId, String fileUniqueId) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.getFileHash(), savedFileId, fileUniqueId, ucc.getData(), ucc.getChunkNumber(), ucc.getChunkHash(), ucc.getFileChunkUniqueId(), ucc.getMessageId());
	}

	public static UploadChunkContainer addChunk(UploadChunkContainer ucc, byte[] data, int chunkNumber) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), data, chunkNumber, ucc.getChunkHash(), ucc.getFileChunkUniqueId(), ucc.getMessageId());
	}

	public static UploadChunkContainer addChunkHash(UploadChunkContainer ucc, String chunkHash) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getData(), ucc.getChunkNumber(), chunkHash, ucc.getFileChunkUniqueId(), ucc.getMessageId());
	}

	public static UploadChunkContainer addEncryptedData(UploadChunkContainer ucc, byte[] encoded) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), encoded, ucc.getChunkNumber(), ucc.getChunkHash(), ucc.getFileChunkUniqueId(), ucc.getMessageId());
	}

	public static UploadChunkContainer addChunkId(UploadChunkContainer ucc, String fileChunkUniqueId) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getData(), ucc.getChunkNumber(), ucc.getChunkHash(), fileChunkUniqueId, ucc.getMessageId());
	}

	public static UploadChunkContainer addMessageId(UploadChunkContainer ucc, String messageId) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getData(), ucc.getChunkNumber(), ucc.getChunkHash(), ucc.getFileUniqueId(), messageId);
	}

	@Override
	public String toString() {
		return String.format("Chunk %s for file %s (%s bytes)", chunkNumber, fileDto.getAbsolutePath(), data.length);
	}


}
