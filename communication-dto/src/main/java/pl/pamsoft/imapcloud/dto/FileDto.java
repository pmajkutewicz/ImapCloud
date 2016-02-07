package pl.pamsoft.imapcloud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.text.DecimalFormat;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({ "readableFileSize"})
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class FileDto {

	private static final int KIB = 1024;
	private final String[] units = new String[]{"B", "kiB", "MiB", "GiB", "TiB"};
	public enum Type {FILE, DIRECTORY}

	@Getter @Setter
	private String name;
	@Getter @Setter
	private String absolutePath;
	@Getter @Setter
	private Type type;
	@Getter @Setter
	private Long size;

	public String getReadableFileSize() {
		if (null == size || size <= 0) {
			return "0";
		}
		int digitGroups = (int) (Math.log10(size) / Math.log10(KIB));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(KIB, digitGroups)) + " " + units[digitGroups];
	}
}
