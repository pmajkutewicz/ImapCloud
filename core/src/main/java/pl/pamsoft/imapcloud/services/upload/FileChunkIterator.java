package pl.pamsoft.imapcloud.services.upload;

import com.jamonapi.Monitor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
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
	private MonitoringHelper monitoringHelper;
	private int fetchSize;
	private int currentChunkNumber = 1;
	private long chunkSizeCumulative = 0;

	private boolean variableChunksMode;
	private int maxIncrease;
	private int minFetchSize;

	public FileChunkIterator(UploadChunkContainer ucc, int fetchSize, MonitoringHelper monitoringHelper) {
		this.ucc = ucc;
		this.fetchSize = fetchSize;
		this.monitoringHelper = monitoringHelper;
	}

	public FileChunkIterator(UploadChunkContainer ucc, int fetchSize, int deviation, MonitoringHelper monitoringHelper) {
		this.ucc = ucc;
		this.monitoringHelper = monitoringHelper;
		this.minFetchSize = fetchSize - deviation;
		this.maxIncrease = 2 * deviation;
		this.variableChunksMode = true;
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
			Monitor monitor = monitoringHelper.start(Keys.UL_FILE_CHUNK_CREATOR);
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
			chunkSizeCumulative += data.length;
			UploadChunkContainer uploadChunkContainer = UploadChunkContainer.addChunk(ucc, data.length, chunkSizeCumulative, data, currentChunkNumber++, !hasNext());
			double lastVal = monitoringHelper.stop(monitor);
			LOG.debug("Chunk of {} for file {} created in {}", uploadChunkContainer.getData().length, uploadChunkContainer.getFileDto().getAbsolutePath(), lastVal);
			return uploadChunkContainer;
		} catch (IOException e) {
			LOG.warn("Returning EMPTY from FileChunkIterator");
			return UploadChunkContainer.EMPTY;
		}
	}
}
