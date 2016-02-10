package pl.pamsoft.imapcloud.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class FileChunkIterator implements Iterator<byte[]> {

	private static final Logger LOG = LoggerFactory.getLogger(FileChunkIterator.class);

	private FileChannel inChannel;
	private long maxSize;
	private long currentPosition = 0;
	private int fetchSize;

	private boolean variableChunksMode;
	private int maxIncrease;
	private int minFetchSize;

	public FileChunkIterator(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public FileChunkIterator(int fetchSize, int deviation) {
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
		this.fetchSize = ThreadLocalRandom.current().nextInt(minFetchSize, minFetchSize + maxIncrease);
	}

	public void close() throws IOException {
		inChannel.close();
	}

	@Override
	public boolean hasNext() {
		return currentPosition < maxSize;
	}

	@Override
	public byte[] next() {
		try {
			if (currentPosition + fetchSize > maxSize) {
				this.fetchSize = Math.toIntExact(maxSize - currentPosition);
			}
			byte[] data = new byte[fetchSize];
			MappedByteBuffer mapped = inChannel.map(FileChannel.MapMode.READ_ONLY, currentPosition, fetchSize);
			currentPosition += fetchSize;
			LOG.debug("Returning buffer of {} bytes", fetchSize);
			if (variableChunksMode) {
				generateNextFetchSize();
			}
			mapped.get(data);
			return data;
		} catch (IOException e) {
			return null;
		}
	}
}
