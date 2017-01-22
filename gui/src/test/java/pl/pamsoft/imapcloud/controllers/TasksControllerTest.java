package pl.pamsoft.imapcloud.controllers;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.reflection.Whitebox;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.controls.TaskProgressControl;
import pl.pamsoft.imapcloud.dto.progress.FileProgressDto;
import pl.pamsoft.imapcloud.dto.progress.TaskProgressDto;
import pl.pamsoft.imapcloud.responses.TaskProgressResponse;
import pl.pamsoft.imapcloud.rest.RequestCallback;
import pl.pamsoft.imapcloud.rest.TaskProgressRestClient;
import pl.pamsoft.imapcloud.tools.TestPlatformTools;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.collect.ImmutableMap.of;
import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

public class TasksControllerTest {

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

	@BeforeClass
	public void init() throws InterruptedException, MalformedURLException {
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
		Whitebox.setInternalState(tasksController, "updateIntervalSlider", slider);
		Whitebox.setInternalState(tasksController, "tasksContainer", new VBox());
		tasksController.initialize(new URL("http://example.com"), resourceBundle);
	}

	@Test
	public void testFlow() throws InterruptedException, IOException {
		Map<String, TaskProgressControl> currentTasks = (Map<String, TaskProgressControl>) Whitebox.getInternalState(tasksController, "currentTasks");
		RequestCallback<TaskProgressResponse> lambdaToTest = captureLambda();
		TaskProgressResponse taskProgressResponse = new TaskProgressResponse();

		String FILE_1 = "f1";
		FileProgressDto f1 = fpd(FILE_1, 10, 100);
		taskProgressResponse.setTaskProgressList(Collections.singletonList(create("1", 10, 100, of(FILE_1, f1))));
		lambdaToTest.onSuccess(taskProgressResponse);
		TaskProgressControl taskProgressControl = currentTasks.get("1");
		assertEquals(taskProgressControl.getOverallProgress().getProgress(), 0.1);
		assertEquals(taskProgressControl.getFileProgressMap().get(FILE_1).longValue(), 10);

		f1.setProgress(20);
		taskProgressResponse.setTaskProgressList(Collections.singletonList(create("1", 20, 100, of(FILE_1, f1))));
		lambdaToTest.onSuccess(taskProgressResponse);
		taskProgressControl = currentTasks.get("1");
		assertEquals(taskProgressControl.getOverallProgress().getProgress(), 0.2);
		assertEquals(taskProgressControl.getFileProgressMap().get(FILE_1).longValue(), 20);
	}

	private TaskProgressDto create(String taskId, long processed, long overall, Map<String, FileProgressDto> fileProgressDtoList) {
		TaskProgressDto taskProgressDto = new TaskProgressDto();
		taskProgressDto.setTaskId(taskId);
		taskProgressDto.setType(TaskType.UPLOAD);
		taskProgressDto.setBytesOverall(overall);
		taskProgressDto.setBytesProcessed(processed);
		taskProgressDto.setFileProgressDataMap(fileProgressDtoList);
		return taskProgressDto;
	}

	private FileProgressDto fpd(String path, long progress, long size) {
		FileProgressDto fileProgressDto = new FileProgressDto();
		fileProgressDto.setAbsolutePath(path);
		fileProgressDto.setProgress(progress);
		fileProgressDto.setSize(size);
		return fileProgressDto;
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
}
