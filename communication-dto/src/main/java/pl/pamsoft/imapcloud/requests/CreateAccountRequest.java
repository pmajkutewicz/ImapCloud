package pl.pamsoft.imapcloud.requests;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.pamsoft.imapcloud.dto.EmailProviderInfo;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class CreateAccountRequest implements AccountRequest {
	private String username;
	private String password;
	private EmailProviderInfo selectedEmailProvider;
}
