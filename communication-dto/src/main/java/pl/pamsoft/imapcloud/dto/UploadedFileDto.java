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
@ToString(callSuper = true)
@JsonIgnoreProperties({"readableFileSize"})
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class UploadedFileDto extends FileDto {

	@Getter @Setter
	private Boolean completed;

	@Getter @Setter
	private String fileUniqueId;


	public static UploadedFileDto folder(String name){
		UploadedFileDto uploadedFileDto = new UploadedFileDto();
		uploadedFileDto.setName(name);
		uploadedFileDto.setType(FileType.DIRECTORY);
		return uploadedFileDto;
	}
}
