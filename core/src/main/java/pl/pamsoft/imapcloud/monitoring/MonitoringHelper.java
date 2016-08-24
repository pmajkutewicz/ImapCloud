package pl.pamsoft.imapcloud.monitoring;

import com.jamonapi.JAMonArrayBufferListener;
import com.jamonapi.JAMonListener;
import com.jamonapi.MonKey;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.BufferList;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
public class MonitoringHelper {

	private static final String LISTENER_TYPE = "value";
	private static final int BUFFER_SIZE = 1000;

	private List<MonKey> allKeys;

	public Monitor add(MonKey key, double value) {
		return MonitorFactory.add(key, value);
	}

	public Monitor start(MonKey key) {
		return MonitorFactory.start(key);
	}

	public double stop(Monitor monitor) {
		monitor.stop();
		return monitor.getLastValue();
	}

	@PostConstruct
	public void initBuffers() {
		allKeys = Arrays.asList(
			Keys.EXECUTOR_ACTIVE, Keys.EXECUTOR_QUEUE, Keys.IMAP_THROUGHPUT, //
			Keys.UL_DIRECTORY_SIZE_CALC, Keys.UL_DIRECTORY_PROCESSOR, Keys.UL_FILE_HASHER, //
			Keys.UL_FILE_CHUNK_CREATOR, Keys.UL_CHUNK_HASHER, Keys.UL_CHUNK_ENCRYPTER, Keys.UL_CHUNK_SAVER, //
			Keys.DL_CHUNK_LOADER, Keys.DL_CHUNK_DECRYPTER, //
			Keys.DL_CHUNK_HASHER, Keys.DL_CHINK_APPENDER, Keys.DL_FILE_HASHER, //
			Keys.VR_CHUNK_VERIFIER, //
			Keys.RE_CHUNK_RECOVERY, //
			Keys.DE_FILE_DELETER
		);

		allKeys.forEach(i -> {
				BufferList list = new BufferList(new String[]{"value"}, BUFFER_SIZE);
				JAMonListener listener = new JAMonArrayBufferListener(i.getLabel(), list);
				MonitorFactory.getMonitor(i).addListener(LISTENER_TYPE, listener);
			}
		);
	}

	public List<MonKey> getAllKeys() {
		return allKeys;
	}
}
