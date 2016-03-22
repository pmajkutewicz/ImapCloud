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
public class FileDto {

	public enum FileType {FILE, DIRECTORY}

	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private String absolutePath;
	@Getter
	@Setter
	private FileType type;
	@Getter
	@Setter
	private Long size;

	public String getReadableFileSize() {
		if (FileType.DIRECTORY == type) {
			return "";
		}
		return ReadableSize.getReadableFileSize(size);
	}
}
