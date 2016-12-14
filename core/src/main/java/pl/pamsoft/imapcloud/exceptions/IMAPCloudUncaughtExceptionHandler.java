package pl.pamsoft.imapcloud.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class IMAPCloudUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler, Function<Throwable, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(IMAPCloudUncaughtExceptionHandler.class);

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		LOG.error("Caught exception from executor service", e);
	}

	@Override
	public Void apply(Throwable throwable) {
		uncaughtException(Thread.currentThread(), throwable);
		return null;
	}
}
