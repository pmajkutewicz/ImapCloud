package pl.pamsoft.imapcloud.dao;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import pl.pamsoft.imapcloud.SpringTestConfig;
import pl.pamsoft.imapcloud.config.AccountsSettings;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.config.ff4j.FF4JConfiguration;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.io.IOException;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(
	initializers = ConfigFileApplicationContextInitializer.class,
	classes = {PropertyPlaceholderAutoConfiguration.class, SpringTestConfig.class, AccountsSettings.class, GraphProperties.class, FF4JConfiguration.class},
	loader=AnnotationConfigContextLoader.class)
@ExtendWith(SpringExtension.class)
@Disabled
class TaskProgressEventRepositoryTest {

	@Autowired
	private TaskProgressRepository eventRepository;

	@Test
	void test() throws IOException {
		int startSize = eventRepository.findAll().size();
		FileDto f = new FileDto(RandomStringUtils.randomAlphabetic(5), "/a/b/c/file.txt", FileDto.FileType.FILE, 1000L);
		TaskProgress event = eventRepository.create(TaskType.VERIFY, "test", 1000, of(f));
		assertEquals(startSize + 1, eventRepository.findAll().size());

		eventRepository.save(event);
		assertEquals(startSize + 1, eventRepository.findAll().size()); // no new entries

		eventRepository.save(event);
		assertEquals(startSize + 1, eventRepository.findAll().size()); // no new entries
	}
}
