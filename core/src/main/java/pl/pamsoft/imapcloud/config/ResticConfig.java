package pl.pamsoft.imapcloud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import pl.pamsoft.imapcloud.restic.ResticApiResponseWriter;
import pl.pamsoft.imapcloud.restic.TypeEnumConverter;

import java.util.List;

@Configuration
public class ResticConfig extends WebMvcConfigurationSupport {
	@Autowired
	private ResticApiResponseWriter converter;

	@Override
	public FormattingConversionService mvcConversionService() {
		FormattingConversionService f = super.mvcConversionService();
		f.addConverter(new TypeEnumConverter());
		return f;
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(converter);
	}
}
