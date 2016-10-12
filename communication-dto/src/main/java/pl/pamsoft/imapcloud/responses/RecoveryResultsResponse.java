package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.RecoveredFileDto;

import java.util.List;
import java.util.Map;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class RecoveryResultsResponse extends AbstractResponse {
	private Map<String, List<RecoveredFileDto>> recoveredFiles;

	public RecoveryResultsResponse(Map<String, List<RecoveredFileDto>> recoveredFiles) {
		this.recoveredFiles = recoveredFiles;
	}

	public RecoveryResultsResponse() {
	}

	public Map<String, List<RecoveredFileDto>> getRecoveredFiles() {
		return this.recoveredFiles;
	}

	public void setRecoveredFiles(Map<String, List<RecoveredFileDto>> recoveredFiles) {
		this.recoveredFiles = recoveredFiles;
	}
}
