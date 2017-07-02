package pl.pamsoft.imapcloud.api.containers;

import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;

import java.util.List;
import java.util.Map;

public interface RecoveryChunkContainer {
	String getTaskId();

	Account getAccount();

	Map<String, File> getFileMap();

	Map<String, List<FileChunk>> getFileChunkMap();
}
