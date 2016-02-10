package pl.pamsoft.imapcloud.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.dto.FileDto;

import java.io.File;
import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FileSplitter implements Function<FileDto, Stream<byte[]>> {

	private static final Logger LOG = LoggerFactory.getLogger(FileSplitter.class);

	private int maxChunkSizeMB;
	private int deviationInPercent;
	private boolean variableSize;

	public FileSplitter(int maxChunkSizeMB) {
		this.maxChunkSizeMB = maxChunkSizeMB;
	}

	public FileSplitter(int maxChunkSizeMB, int deviationInPercent) {
		this.maxChunkSizeMB = maxChunkSizeMB;
		this.deviationInPercent = deviationInPercent;
		this.variableSize = true;
	}

	@Override
	public Stream<byte[]> apply(FileDto fileDto) {
		LOG.debug("Processing: {}", fileDto.getAbsolutePath());
		int maxSize = calculateMaxSize(toBytes(maxChunkSizeMB));
		FileChunkIterator fileChunkIterator = variableSize ? new FileChunkIterator(maxSize, xPercent(maxSize)) : new FileChunkIterator(maxSize);
		try {
			fileChunkIterator.process(new File(fileDto.getAbsolutePath()));
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(fileChunkIterator, Spliterator.ORDERED), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Stream.empty();
	}

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
}
