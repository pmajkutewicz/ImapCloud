package pl.pamsoft.imapcloud.tools;

import com.sun.javafx.application.PlatformImpl;

public class PlatformTools {

	public void runLater(Runnable runnable) {
		PlatformImpl.runLater(runnable);
	}
}
