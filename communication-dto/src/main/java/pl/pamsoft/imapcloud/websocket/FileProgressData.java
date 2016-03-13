package pl.pamsoft.imapcloud.websocket;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class FileProgressData {
	private String absolutePath;
	private long size;
	private long progress;

	public FileProgressData(String absolutePath, long size) {
		this.absolutePath = absolutePath;
		this.size = size;
	}
}
