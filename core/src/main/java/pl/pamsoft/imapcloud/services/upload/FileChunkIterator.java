package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class FileChunkIterator implements Iterator<UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(FileChunkIterator.class);

	private FileChannel inChannel;
	private long maxSize;
	private long currentPosition = 0;
	private UploadChunkContainer ucc;
	private int fetchSize;
	private Statistics statistics;
	private int currentChunkNumber = 1;

	private boolean variableChunksMode;
	private int maxIncrease;
	private int minFetchSize;

	public FileChunkIterator(UploadChunkContainer ucc, int fetchSize, Statistics statistics) {
		this.ucc = ucc;
		this.fetchSize = fetchSize;
		this.statistics = statistics;
	}

	public FileChunkIterator(UploadChunkContainer ucc, int fetchSize, int deviation, Statistics statistics) {
		this.ucc = ucc;
		this.minFetchSize = fetchSize - deviation;
		this.maxIncrease = 2 * deviation;
		this.variableChunksMode = true;
		this.statistics = statistics;
		generateNextFetchSize();
	}

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	public void process() throws IOException {
		// i guess it should be FileInputStream
		inChannel = new RandomAccessFile(new File(ucc.getFileDto().getAbsolutePath()), "r").getChannel();
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
	public UploadChunkContainer next() {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			if (currentPosition + fetchSize > maxSize) {
				this.fetchSize = Math.toIntExact(maxSize - currentPosition);
			}
			byte[] data = new byte[fetchSize];
			MappedByteBuffer mapped = inChannel.map(FileChannel.MapMode.READ_ONLY, currentPosition, fetchSize);
			currentPosition += fetchSize;

			if (variableChunksMode) {
				generateNextFetchSize();
			}
			mapped.get(data);
			UploadChunkContainer uploadChunkContainer = UploadChunkContainer.addChunk(ucc, data, currentChunkNumber++);
			statistics.add(StatisticType.CHUNK_ENCODER, stopwatch.stop());
			LOG.debug("Chunk of {} for file {} created in {}", uploadChunkContainer.getData().length, uploadChunkContainer.getFileDto().getAbsolutePath(), stopwatch);
			return uploadChunkContainer;
		} catch (IOException e) {
			LOG.warn("Returning EMPTY from FileChunkIterator");
			return UploadChunkContainer.EMPTY;
		}
	}
}
