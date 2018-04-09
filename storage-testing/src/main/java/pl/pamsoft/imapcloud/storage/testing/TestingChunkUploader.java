package pl.pamsoft.imapcloud.storage.testing;

import pl.pamsoft.imapcloud.api.accounts.ChunkUploader;
import pl.pamsoft.imapcloud.api.containers.UploadChunkContainer;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestingChunkUploader implements ChunkUploader {

	private static AtomicInteger counter = new AtomicInteger(0);
	@Override
	public String upload(UploadChunkContainer dataChunk, Map<String, String> metadata) {
		counter.incrementAndGet();
		try {
			Thread.sleep(TimeUnit.MINUTES.toMillis(1));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "";
	}

	public AtomicInteger getCounter() {
		return counter;
	}
}
