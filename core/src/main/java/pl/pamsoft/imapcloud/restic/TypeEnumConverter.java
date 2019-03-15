package pl.pamsoft.imapcloud.restic;

import org.springframework.core.convert.converter.Converter;

public class TypeEnumConverter implements Converter<String, ResticType> {
	@Override
	public ResticType convert(String s) {
		return ResticType.valueOf(s.toUpperCase());
	}
}
