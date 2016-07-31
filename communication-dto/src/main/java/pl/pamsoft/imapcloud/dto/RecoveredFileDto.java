package pl.pamsoft.imapcloud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({"readableFileSize"})
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class RecoveredFileDto extends FileDto {

	@Getter
	private String fileUniqueId;

	@Getter
	private boolean completed;

	@Getter
	@Setter
	private boolean inDb;

	@Getter
	@Setter
	private Boolean completedInDb;

	public RecoveredFileDto(String name, String absolutePath, FileType type, Long size,
	                        String fileUniqueId, boolean completed) {
		super(name, absolutePath, type, size);
		this.fileUniqueId = fileUniqueId;
		this.completed = completed;
	}
}
