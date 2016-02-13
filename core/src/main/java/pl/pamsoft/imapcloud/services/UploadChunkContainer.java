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
	private final FileDto fileDto;
	@SuppressFBWarnings("EI_EXPOSE_REP")
	private final byte[] data;
	private final int chunkNumber;
	private final String chunkHash;

	public UploadChunkContainer(FileDto fileDto, byte[] data, int chunkNumber) {
		this(fileDto, data, chunkNumber, StringUtils.EMPTY);
	}

	public UploadChunkContainer(FileDto fileDto, byte[] data, int chunkNumber, String chunkHash) {
		this.fileDto = fileDto;
		this.data = data;
		this.chunkNumber = chunkNumber;
		this.chunkHash = chunkHash;
	}

	@Override
	public String toString() {
		return String.format("Chunk %s for file %s (%s bytes)", chunkNumber, fileDto.getAbsolutePath(), data.length);
	}
}
