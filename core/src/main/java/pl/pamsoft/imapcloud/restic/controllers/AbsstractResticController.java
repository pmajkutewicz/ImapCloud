package pl.pamsoft.imapcloud.restic.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class AbsstractResticController {

	@Autowired
	private ObjectMapper mapper;

	protected String toJson(Object o) throws JsonProcessingException {
		return mapper.writeValueAsString(o);
	}
}
