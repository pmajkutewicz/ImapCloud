package pl.pamsoft.imapcloud.converters;

import javafx.util.StringConverter;
import pl.pamsoft.imapcloud.dto.AccountDto;

public class AccountDtoConverter extends StringConverter<AccountDto> {
	@Override
	public String toString(AccountDto object) {
		return object.getEmail();
	}

	@Override
	public AccountDto fromString(String string) {
		return null;
	}
}
