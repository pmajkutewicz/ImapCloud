package pl.pamsoft.imapcloud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.utils.ReadableSize;

@JsonIgnoreProperties({"readableFileSize"})
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class AccountDto {
	private Long id;
	private String email;
	private String cryptoKey;
	private Long usedSpace;

	public AccountDto(Long id, String email, String cryptoKey, Long usedSpace) {
		this.id = id;
		this.email = email;
		this.cryptoKey = cryptoKey;
		this.usedSpace = usedSpace;
	}

	public AccountDto() {
	}

	// used as javafx property
	public String getReadableFileSize() {
		return ReadableSize.getReadableFileSize(usedSpace);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCryptoKey() {
		return this.cryptoKey;
	}

	public void setCryptoKey(String cryptoKey) {
		this.cryptoKey = cryptoKey;
	}

	public Long getUsedSpace() {
		return this.usedSpace;
	}

	public void setUsedSpace(Long usedSpace) {
		this.usedSpace = usedSpace;
	}
}
