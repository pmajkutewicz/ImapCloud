package pl.pamsoft.imapcloud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pamsoft.imapcloud.utils.ReadableSize;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties({"readableFileSize"})
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class AccountDto {
	private String id;
	private String email;
	private String cryptoKey;
	private Long usedSpace;

	// used as javafx property
	public String getReadableFileSize() {
		return ReadableSize.getReadableFileSize(usedSpace);
	}
}
