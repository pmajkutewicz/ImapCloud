package pl.pamsoft.imapcloud.restic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static org.springframework.http.MediaType.asMediaType;
import static org.springframework.util.MimeType.valueOf;

@Component
public class ResticApiResponseWriter implements HttpMessageConverter<Object> {

	private ImmutableList<MediaType> supportedMediaTypes = of(asMediaType(valueOf(ResticUtils.API_V1)), asMediaType(valueOf(ResticUtils.API_V2)));

	@Autowired
	private ObjectMapper mapper;

	@Override
	public boolean canRead(Class<?> aClass, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> aClass, MediaType mediaType) {
		return supportedMediaTypes.contains(mediaType) && Collection.class.isAssignableFrom(aClass);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return supportedMediaTypes;
	}

	@Override
	public Object read(Class<?> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	public void write(Object object, MediaType mediaType, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
		mapper.writeValue(httpOutputMessage.getBody(), object);
	}

}
