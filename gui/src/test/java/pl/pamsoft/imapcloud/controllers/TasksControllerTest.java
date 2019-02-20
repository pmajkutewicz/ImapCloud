package pl.pamsoft.imapcloud.controllers;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.pamsoft.imapcloud.controls.TaskProgressControl;
import pl.pamsoft.imapcloud.dto.progress.EntryProgressDto;
import pl.pamsoft.imapcloud.dto.progress.ProgressStatus;
import pl.pamsoft.imapcloud.dto.progress.TaskProgressDto;
import pl.pamsoft.imapcloud.responses.TaskProgressResponse;
import pl.pamsoft.imapcloud.rest.RequestCallback;
import pl.pamsoft.imapcloud.rest.TaskProgressRestClient;
import pl.pamsoft.imapcloud.tools.TestPlatformTools;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TasksControllerTest {

	private TasksController tasksController;

	private TaskProgressRestClient taskProgressRestClient = mock(TaskProgressRestClient.class);
	private Slider slider;
	private ResourceBundle resourceBundle = new ResourceBundle() {
		@Override
		protected Object handleGetObject(String key) {
			return "fake";
		}

		@Override
		public Enumeration<String> getKeys() {
			return Collections.emptyEnumeration();
		}
	};

	@BeforeAll
	void init() throws InterruptedException, MalformedURLException {
		final AtomicBoolean initialized = new AtomicBoolean();
		new Thread(() -> {
			new JFXPanel(); // initializes JavaFX environment
			initialized.set(true);
		}).start();

		await().atMost(2, MINUTES).until(initialized::get, equalTo(true));
		tasksController = new TasksController();
		tasksController.setPlatformTools(new TestPlatformTools());
		tasksController.setTaskProgressRestClient(taskProgressRestClient);
		slider = new Slider();
		setInternalState(tasksController, "updateIntervalSlider", slider);
		setInternalState(tasksController, "tasksContainer", new VBox());
		tasksController.initialize(new URL("http://example.com"), resourceBundle);
	}

	@Test // DISABLE due to jenkins (won't work TasksControllerTest.lambda$init$0:60 » UnsupportedOperation Unable to open DIS)
	@Disabled
	void testFlow() throws InterruptedException, IOException {
		Map<String, TaskProgressControl> currentTasks = (Map<String, TaskProgressControl>) getInternalState(tasksController, "currentTasks");
		RequestCallback<TaskProgressResponse> lambdaToTest = captureLambda();
		TaskProgressResponse taskProgressResponse = new TaskProgressResponse();

		String FILE_1 = "f1";
		EntryProgressDto f1 = fpd(FILE_1, 10, 100);
		taskProgressResponse.setTaskProgressList(Collections.singletonList(create("1", 10, 100, of(FILE_1, f1))));
		lambdaToTest.onSuccess(taskProgressResponse);
		TaskProgressControl taskProgressControl = currentTasks.get("1");
		assertEquals(0.1, taskProgressControl.getOverallProgress().getProgress());
		assertEquals(10, taskProgressControl.getProgressMap().get(FILE_1).longValue());

		f1.setProgress(20);
		taskProgressResponse.setTaskProgressList(Collections.singletonList(create("1", 20, 100, of(FILE_1, f1))));
		lambdaToTest.onSuccess(taskProgressResponse);
		taskProgressControl = currentTasks.get("1");
		assertEquals(0.2, taskProgressControl.getOverallProgress().getProgress());
		assertEquals(20, taskProgressControl.getProgressMap().get(FILE_1).longValue());
	}

	private TaskProgressDto create(String taskId, long processed, long overall, Map<String, EntryProgressDto> progressDtoList) {
		TaskProgressDto taskProgressDto = new TaskProgressDto();
		taskProgressDto.setTaskId(taskId);
		taskProgressDto.setType(TaskType.UPLOAD);
		taskProgressDto.setBytesOverall(overall);
		taskProgressDto.setBytesProcessed(processed);
		taskProgressDto.setProgressMap(progressDtoList);
		return taskProgressDto;
	}

	private EntryProgressDto fpd(String path, long progress, long size) {
		EntryProgressDto progressDto = new EntryProgressDto();
		progressDto.setAbsolutePath(path);
		progressDto.setProgress(progress);
		progressDto.setSize(size);
		progressDto.setStatus(ProgressStatus.UPLOADED);
		return progressDto;
	}

	private RequestCallback<TaskProgressResponse> captureLambda() throws InterruptedException {
		ArgumentCaptor<RequestCallback> requestCallbackArgumentCaptor = ArgumentCaptor.forClass(RequestCallback.class);
		slider.setValueChanging(true);
		slider.setValue(1);
		slider.setValueChanging(false);
		Thread.sleep(3000);
		verify(taskProgressRestClient, atLeastOnce()).getTasksProgress(requestCallbackArgumentCaptor.capture());
		return requestCallbackArgumentCaptor.getValue();
	}

	private static Object getInternalState(Object target, String field) {
		Class<?> c = target.getClass();
		try {
			Field f = c.getDeclaredField(field);
			f.setAccessible(true);
			return f.get(target);
		} catch (Exception e) {
			throw new RuntimeException("Unable to get internal state on a private field.", e);
		}
	}

	private  static void setInternalState(Object target, String field, Object value) {
		Class<?> c = target.getClass();
		try {
			Field f = c.getDeclaredField(field);
			f.setAccessible(true);
			f.set(target, value);
		} catch (Exception e) {
			throw new RuntimeException("Unable to set internal state on a private field.", e);
		}
	}
}
