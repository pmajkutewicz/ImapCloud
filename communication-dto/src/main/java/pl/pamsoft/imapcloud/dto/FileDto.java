package pl.pamsoft.imapcloud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.utils.ReadableSize;

@JsonIgnoreProperties({"readableFileSize"})
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class FileDto {

	private String name;
	private String absolutePath;
	private FileType type;
	private Long size;

	public FileDto(String name, String absolutePath, FileType type, Long size) {
		this.name = name;
		this.absolutePath = absolutePath;
		this.type = type;
		this.size = size;
	}

	public FileDto() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbsolutePath() {
		return this.absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public FileType getType() {
		return this.type;
	}

	public void setType(FileType type) {
		this.type = type;
	}

	public Long getSize() {
		return this.size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getReadableFileSize() {
		if (FileType.DIRECTORY == type) {
			return "";
		}
		return ReadableSize.getReadableFileSize(size);
	}

	public enum FileType {FILE, DIRECTORY}
}
