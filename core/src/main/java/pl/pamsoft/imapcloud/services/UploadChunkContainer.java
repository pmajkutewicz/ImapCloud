package pl.pamsoft.imapcloud.services;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import pl.pamsoft.imapcloud.dto.FileDto;

import javax.annotation.concurrent.Immutable;

import static org.apache.commons.lang.StringUtils.EMPTY;

@Immutable
@Getter
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class UploadChunkContainer {
	private final FileDto fileDto;
	@SuppressFBWarnings("EI_EXPOSE_REP")
	private final byte[] data;
	private final int chunkNumber;
	private final String chunkHash;
	private final String savedFileId;
	private final String fileUniqueId;
	private final String fileHash;

	public UploadChunkContainer(FileDto fileDto) {
		this(fileDto, EMPTY, EMPTY, EMPTY, null, 0, EMPTY);
	}

	private UploadChunkContainer(FileDto fileDto, String fileHash, String savedFileId, String fileUniqueId, byte[] data, int chunkNumber, String chunkHash) {
		this.fileDto = fileDto;
		this.fileHash = fileHash;
		this.savedFileId = savedFileId;
		this.fileUniqueId = fileUniqueId;
		this.data = data;
		this.chunkNumber = chunkNumber;
		this.chunkHash = chunkHash;
	}

	public static UploadChunkContainer addFileHash(UploadChunkContainer ucc, String fileHash) {
		return new UploadChunkContainer(ucc.getFileDto(), fileHash, ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getData(), ucc.getChunkNumber(), ucc.getChunkHash());
	}

	public static UploadChunkContainer addIds(UploadChunkContainer ucc, String savedFileId, String fileUniqueId) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.getFileHash(), savedFileId, fileUniqueId, ucc.getData(), ucc.getChunkNumber(), ucc.getChunkHash());
	}

	public static UploadChunkContainer addChunk(UploadChunkContainer ucc, byte[] data, int chunkNumber) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), data, chunkNumber, ucc.getChunkHash());
	}

	public static UploadChunkContainer addChunkHash(UploadChunkContainer ucc, String chunkHash) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), ucc.getData(), ucc.getChunkNumber(), chunkHash);
	}

	public static UploadChunkContainer addEncryptedData(UploadChunkContainer ucc, byte[] encoded) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.getFileHash(), ucc.getSavedFileId(), ucc.getFileUniqueId(), encoded, ucc.getChunkNumber(), ucc.getChunkHash());
	}

	@Override
	public String toString() {
		return String.format("Chunk %s for file %s (%s bytes)", chunkNumber, fileDto.getAbsolutePath(), data.length);
	}
}
