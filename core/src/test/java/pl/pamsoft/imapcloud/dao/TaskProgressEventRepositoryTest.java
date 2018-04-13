package pl.pamsoft.imapcloud.dao;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.SpringTestConfig;
import pl.pamsoft.imapcloud.config.AccountsSettings;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.io.IOException;

import static com.google.common.collect.ImmutableList.of;
import static org.testng.Assert.assertEquals;

@ContextConfiguration(
	initializers = ConfigFileApplicationContextInitializer.class,
	classes = {PropertyPlaceholderAutoConfiguration.class, SpringTestConfig.class, AccountsSettings.class, GraphProperties.class},
	loader=AnnotationConfigContextLoader.class)
public class TaskProgressEventRepositoryTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private TaskProgressRepository eventRepository;

	@Test(enabled = false)
	public void test() throws IOException {
		int startSize = eventRepository.findAll().size();
		FileDto f = new FileDto(RandomStringUtils.randomAlphabetic(5), "/a/b/c/file.txt", FileDto.FileType.FILE, 1000L);
		TaskProgress event = eventRepository.create(TaskType.VERIFY, "test", 1000, of(f));
		assertEquals(eventRepository.findAll().size(), startSize + 1);

		eventRepository.save(event);
		assertEquals(eventRepository.findAll().size(), startSize + 1); // no new entries

		eventRepository.save(event);
		assertEquals(eventRepository.findAll().size(), startSize + 1); // no new entries
	}
}
