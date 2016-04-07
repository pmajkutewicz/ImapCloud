package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@NoArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class FileChunk {
	@Id
	private String id;
	private Integer version;

	private String fileChunkUniqueId;
	private int chunkNumber;
	private String chunkHash;
	private Long size;
	private File ownerFile;
	private String messageId;
	private Long lastVerifiedAt;
	private Boolean chunkExists;

}
