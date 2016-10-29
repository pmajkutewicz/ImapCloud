package pl.pamsoft.imapcloud.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamonapi.JAMonBufferListener;
import com.jamonapi.JAMonDetailValue;
import com.jamonapi.JAMonListener;
import com.jamonapi.Monitor;
import com.jamonapi.utils.BufferList;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.dto.monitoring.EventData;
import pl.pamsoft.imapcloud.dto.monitoring.MonitorData;
import pl.pamsoft.imapcloud.dto.monitoring.MonitorDescription;
import pl.pamsoft.imapcloud.responses.MonitoringResponse;
import pl.pamsoft.imapcloud.services.common.MonitoringService;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("monitoring")
@SuppressFBWarnings("CLI_CONSTANT_LIST_INDEX")
public class MonitoringRestController {

	private static final int INDEX_VALUE = 1;
	private static final int INDEX_DATE = 3;

	@Autowired
	private MonitoringService monitoringService;

	private Function<JAMonDetailValue, EventData> convertEvent = i -> {
		Object[] objects = i.toArray();
		return new EventData(((Date)objects[INDEX_DATE]).getTime(), (double)objects[INDEX_VALUE]);
	};

	@SuppressWarnings("unchecked")
	private class Converter implements Function<Monitor, MonitorData> {
		private long eventsAfter;

		Converter(long eventsAfter) {
			this.eventsAfter = eventsAfter;
		}

		@Override
		public MonitorData apply(Monitor i) {
			JAMonListener value = i.getListenerType("value").getListener();
			List<EventData> events = emptyList();
			if (value instanceof JAMonBufferListener) {
				BufferList bufferList = ((JAMonBufferListener) value).getBufferList();
				List<JAMonDetailValue> collection = bufferList.getCollection();
				events = collection.stream().map(convertEvent).filter(e -> e.getTimestamp() > eventsAfter).collect(toList());
			}
			return new MonitorData(i.getMin(), i.getMax(), i.getAvg(), i.getTotal(), i.getHits(),
				(MonitorDescription) i.getMonKey().getDetails(), i.getMonKey().getLabel(),
				i.getUnits(), events);
		}
	}

	@ApiOperation("Returns all application monitors")
	@RequestMapping(method = RequestMethod.GET)
	public MonitoringResponse getMonitors() throws JsonProcessingException {
		long millis = new DateTime().getMillis();
		List<MonitorData> data = monitoringService.getAllMonitors().stream().map(new Converter(0)).collect(toList());
		return new MonitoringResponse(millis, data);
	}

	@ApiOperation("Returns all application monitors, with events after given date")
	@RequestMapping(value = "filter/events/after", method = RequestMethod.GET)
	public MonitoringResponse getMonitorsAfter(@RequestParam("timestamp") long eventsAfter) throws JsonProcessingException {
		long millis = new DateTime().getMillis();
		List<MonitorData> data = monitoringService.getAllMonitors().stream().map(new Converter(eventsAfter)).collect(toList());
		return new MonitoringResponse(millis, data);
	}
}
