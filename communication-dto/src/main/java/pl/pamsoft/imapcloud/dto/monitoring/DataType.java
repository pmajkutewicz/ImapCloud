package pl.pamsoft.imapcloud.dto.monitoring;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum DataType {
	MIN, MAX, AVG, EVENT;

	public static final List<DataType> AGG_TYPES = ImmutableList.of(MIN, MAX, AVG);
}
