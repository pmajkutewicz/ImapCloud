package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.pamsoft.imapcloud.dto.AccountDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class ListAccountResponse implements AccountResponse {
	List<AccountDto> accountDtos;
}
