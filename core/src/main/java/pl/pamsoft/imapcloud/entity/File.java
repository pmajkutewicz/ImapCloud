package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@NoArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class File {
	@Id
	private String id;
	private Integer version;

	private String fileUniqueId;
	private String name;
	private String absolutePath;
	private Long size;
	private String fileHash;
	private boolean completed;
	private Account ownerAccount;
}
