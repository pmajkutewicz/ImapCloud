package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@NoArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class FileProgress {
	@Id
	private String id;
	private String absolutePath;
	private long size;
	private long progress;

	public FileProgress(String absolutePath, long size) {
		this.absolutePath = absolutePath;
		this.size = size;
	}

}
