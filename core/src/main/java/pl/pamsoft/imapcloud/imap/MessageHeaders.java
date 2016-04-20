package pl.pamsoft.imapcloud.imap;

public enum MessageHeaders {
	ChunkNumber("IC-ChunkNumber"),
	ChunkId("IC-ChunkId"),
	FileId("IC-FileId"),
	FileName("IC-FileName"),
	FilePath("IC-FilePath"),
	FileHash("IC-FileHash");

	private String headerName;

	MessageHeaders(String name) {
		headerName = name;
	}


	@Override
	public String toString() {
		return headerName;
	}
}
