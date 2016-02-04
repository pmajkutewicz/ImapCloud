package pl.pamsoft.imapcloud.services;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class FileChunkIterator implements Iterator<MappedByteBuffer> {

	private FileChannel inChannel;
	private long maxSize;
	private long currentPosition = 0;
	private long fetchSize;

	private boolean variableChunksMode;
	private long maxIncrease;
	private long minFetchSize;

	public FileChunkIterator(long fetchSize) {
		this.fetchSize = fetchSize;
	}

	public FileChunkIterator(long fetchSize, long deviation) {
		this.minFetchSize = fetchSize - deviation;
		this.maxIncrease = 2 * deviation;
		this.variableChunksMode = true;
		generateNextFetchSize();
	}

	public void process(File file) throws IOException {
		// i guess it should be FileInputStream
		inChannel = new RandomAccessFile(file, "r").getChannel();
		maxSize = inChannel.size();
	}

	private void generateNextFetchSize() {
		this.fetchSize = ThreadLocalRandom.current().nextLong(minFetchSize, minFetchSize + maxIncrease);
	}

	public void close() throws IOException {
		inChannel.close();
	}

	@Override
	public boolean hasNext() {
		return currentPosition < maxSize;
	}

	@Override
	public MappedByteBuffer next() {
		try {
			if (currentPosition + fetchSize > maxSize) {
				this.fetchSize = maxSize - currentPosition;
			}
			MappedByteBuffer mapped = inChannel.map(FileChannel.MapMode.READ_ONLY, currentPosition, fetchSize);
			currentPosition += fetchSize;
			if (variableChunksMode) {
				generateNextFetchSize();
			}
			return mapped;
		} catch (IOException e) {
			return null;
		}
	}
}
