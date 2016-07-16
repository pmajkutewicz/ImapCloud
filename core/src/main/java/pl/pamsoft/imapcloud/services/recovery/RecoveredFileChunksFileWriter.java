package pl.pamsoft.imapcloud.services.recovery;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RecoveredFileChunksFileWriter implements Function<RecoveryChunkContainer, RecoveryChunkContainer> {

	private static final int INDENT_FACTOR = 2;
	private final String recoveries;
	private final FilesIOService filesIOService;

	public RecoveredFileChunksFileWriter(FilesIOService filesIOService, String recoveries) {
		this.filesIOService = filesIOService;
		this.recoveries = recoveries;
	}

	@Override
	public RecoveryChunkContainer apply(RecoveryChunkContainer rcc) {
		try {
			JSONObject jsonObject = new JSONObject(rcc);
			String fileName = String.format("%s.%s", rcc.getTaskId(), "ic");
			String data = jsonObject.toString(INDENT_FACTOR);
			OutputStream os = filesIOService.getOutputStream(Paths.get(recoveries, fileName + ".zip"));
			ZipOutputStream zos = new ZipOutputStream(os);
			zos.setLevel(9);
			ZipEntry entry = new ZipEntry(fileName);
			entry.setSize(data.length());
			entry.setCrc(getCrc(data));
			zos.putNextEntry(entry);
			IOUtils.write(data, zos, StandardCharsets.UTF_8);
			zos.closeEntry();
			IOUtils.closeQuietly(zos);
			return rcc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return RecoveryChunkContainer.EMPTY;
	}

	private long getCrc(String data) {
		CRC32 crc32 = new CRC32();
		crc32.update(data.getBytes(StandardCharsets.UTF_8));
		return crc32.getValue();
	}
}
