package pl.pamsoft.imapcloud.storage.imap;

public enum MessageHeaders {
	ChunkNumber("IC-ChunkNumber"),
	ChunkId("IC-ChunkId"),
	ChunkHash("IC-ChunkHash"),
	LastChunk("IC-LastChunk"),
	ChunkEncrypted("IC-ChunkEncrypted"),
	FileId("IC-FileId"),
	FileName("IC-FileName"),
	FilePath("IC-FilePath"),
	FileHash("IC-FileHash"),
	MagicNumber("IC-MagicNumber");

	private String headerName;

	MessageHeaders(String name) {
		headerName = name;
	}


	@Override
	public String toString() {
		return headerName;
	}
}
