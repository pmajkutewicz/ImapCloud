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
	private final String fileUniqueId;

	public UploadChunkContainer(FileDto fileDto) {
		this(fileDto, EMPTY, null, 0, EMPTY);
	}

	private UploadChunkContainer(FileDto fileDto, String fileUniqueId, byte[] data, int chunkNumber, String chunkHash) {
		this.fileDto = fileDto;
		this.data = data;
		this.chunkNumber = chunkNumber;
		this.chunkHash = chunkHash;
		this.fileUniqueId = fileUniqueId;
	}

	public static UploadChunkContainer addFileUniqueId(UploadChunkContainer ucc, String fileUniqueId) {
		return new UploadChunkContainer(ucc.getFileDto(), fileUniqueId, null, 0, EMPTY);
	}

	public static UploadChunkContainer addChunk(UploadChunkContainer ucc, byte[] data, int chunkNumber) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.getFileUniqueId(), data, chunkNumber, EMPTY);
	}

	public static UploadChunkContainer addChunkHash(UploadChunkContainer ucc, String chunkHash) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.fileUniqueId, ucc.getData(), ucc.getChunkNumber(), chunkHash);
	}

	public static UploadChunkContainer addEncryptedData(UploadChunkContainer ucc, byte[] encoded) {
		return new UploadChunkContainer(ucc.getFileDto(), ucc.fileUniqueId, encoded, ucc.getChunkNumber(), ucc.getChunkHash());
	}

	@Override
	public String toString() {
		return String.format("Chunk %s for file %s (%s bytes)", chunkNumber, fileDto.getAbsolutePath(), data.length);
	}
}
