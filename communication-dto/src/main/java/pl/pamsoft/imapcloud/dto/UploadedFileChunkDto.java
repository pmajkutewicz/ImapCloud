package pl.pamsoft.imapcloud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.pamsoft.imapcloud.utils.ReadableSize;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({"readableFileSize"})
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class UploadedFileChunkDto {

	@Getter @Setter
	private String fileChunkUniqueId;
	@Getter @Setter
	private int chunkNumber;
	@Getter @Setter
	private String chunkHash;
	@Getter @Setter
	private Long size;
	@Getter @Setter
	private String messageId;

	public String getReadableFileSize() {
		return ReadableSize.getReadableFileSize(size);
	}
}
