package pl.pamsoft.imapcloud.services.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FileSplitter implements Function<UploadChunkContainer, Stream<UploadChunkContainer>> {

	private static final Logger LOG = LoggerFactory.getLogger(FileSplitter.class);

	private int maxChunkSizeInMB;
	private Statistics statistics;
	private final PerformanceDataService performanceDataService;
	private int deviationInPercent;
	private boolean variableSize;

	public FileSplitter(int maxChunkSizeMB, Statistics statistics, PerformanceDataService performanceDataService) {
		this.maxChunkSizeInMB = maxChunkSizeMB;
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
	}

	public FileSplitter(int maxChunkSizeMB, int deviationInPercent, Statistics statistics, PerformanceDataService performanceDataService) {
		this.maxChunkSizeInMB = maxChunkSizeMB;
		this.deviationInPercent = deviationInPercent;
		this.statistics = statistics;
		this.variableSize = true;

		this.performanceDataService = performanceDataService;
	}

	@Override
	public Stream<UploadChunkContainer> apply(UploadChunkContainer ucc) {
		FileDto fileDto = ucc.getFileDto();
		LOG.debug("Processing: {}", fileDto.getAbsolutePath());
		int maxSize = calculateMaxSize(toBytes(maxChunkSizeInMB));
		FileChunkIterator fileChunkIterator = variableSize ?
			new FileChunkIterator(ucc, maxSize, xPercent(maxSize), statistics, performanceDataService) :
			new FileChunkIterator(ucc, maxSize, statistics, performanceDataService);
		try {
			fileChunkIterator.process();
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(fileChunkIterator, Spliterator.ORDERED), false);
		} catch (IOException e) {
			LOG.error(String.format("Can't chop file %s into chunks.", fileDto.getAbsolutePath()), e);
		}
		return Stream.empty();
	}

	//CSOFF: MagicNumber
	private int toBytes(int maxChunkSizeMB) {
		return maxChunkSizeMB * 1024 * 1024;
	}

	private int calculateMaxSize(int maxSize) {
		// https://en.wikipedia.org/wiki/Base64
		// bytes = (string_length(encoded_string) - 814) / 1.37
		double result = (maxSize - 814) / 1.37;
		return (int) minus5Percent(result);
	}

	private double minus5Percent(double value) {
		return value - ((value * 5) / 100);
	}

	private int xPercent(double val) {
		return (int) (val * deviationInPercent) / 100;
	}
	//CSON: MagicNumber
}
