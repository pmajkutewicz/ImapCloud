package pl.pamsoft.imapcloud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.utils.ReadableSize;

@JsonIgnoreProperties({"readableFileSize"})
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class UploadedFileChunkDto {

	private String fileChunkUniqueId;
	private int chunkNumber;
	private String chunkHash;
	private Long size;
	private String messageId;
	private Long lastVerifiedAt;
	private Boolean chunkExists;

	public UploadedFileChunkDto(String fileChunkUniqueId, int chunkNumber, String chunkHash, Long size, String messageId, Long lastVerifiedAt, Boolean chunkExists) {
		this.fileChunkUniqueId = fileChunkUniqueId;
		this.chunkNumber = chunkNumber;
		this.chunkHash = chunkHash;
		this.size = size;
		this.messageId = messageId;
		this.lastVerifiedAt = lastVerifiedAt;
		this.chunkExists = chunkExists;
	}

	public UploadedFileChunkDto() {
	}

	// used as javafx property
	public String getReadableFileSize() {
		return ReadableSize.getReadableFileSize(size);
	}

	public String getFileChunkUniqueId() {
		return this.fileChunkUniqueId;
	}

	public void setFileChunkUniqueId(String fileChunkUniqueId) {
		this.fileChunkUniqueId = fileChunkUniqueId;
	}

	public int getChunkNumber() {
		return this.chunkNumber;
	}

	public void setChunkNumber(int chunkNumber) {
		this.chunkNumber = chunkNumber;
	}

	public String getChunkHash() {
		return this.chunkHash;
	}

	public void setChunkHash(String chunkHash) {
		this.chunkHash = chunkHash;
	}

	public Long getSize() {
		return this.size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getMessageId() {
		return this.messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Long getLastVerifiedAt() {
		return this.lastVerifiedAt;
	}

	public void setLastVerifiedAt(Long lastVerifiedAt) {
		this.lastVerifiedAt = lastVerifiedAt;
	}

	public Boolean getChunkExists() {
		return this.chunkExists;
	}

	public void setChunkExists(Boolean chunkExists) {
		this.chunkExists = chunkExists;
	}
}
