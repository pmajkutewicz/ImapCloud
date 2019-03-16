package pl.pamsoft.imapcloud.restic;

import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Component
public class ResticTypeConverter extends PropertyEditorSupport {

	public void setAsText(final String text) throws IllegalArgumentException {
		setValue(ResticType.valueOf(text.toUpperCase()));
	}

}
