package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class ErrorResponse extends AbstractResponse {

	public ErrorResponse(int status, String message) {
		setStatus(status);
		setMessage(message);
	}

	public ErrorResponse() {
	}
}
