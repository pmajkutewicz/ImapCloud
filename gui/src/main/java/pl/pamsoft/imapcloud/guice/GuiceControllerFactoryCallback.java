package pl.pamsoft.imapcloud.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import javafx.util.Callback;

public class GuiceControllerFactoryCallback implements Callback<Class<?>, Object> {
	private final Injector injector;

	public GuiceControllerFactoryCallback(Module module) {
		injector = Guice.createInjector(module);
	}

	@Override
	public Object call(Class<?> clazz) {
		return injector.getInstance(clazz);
	}
}
