package pl.pamsoft.imapcloud.services.recovery;

import pl.pamsoft.imapcloud.dto.RecoveredFileDto;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pl.pamsoft.imapcloud.dto.FileDto.FileType.FILE;

public class RCCtoRecoveredFileDtoConverter implements Function<RecoveryChunkContainer, List<RecoveredFileDto>> {

	Function<File, RecoveredFileDto> toRecoveredFileDto = f ->
		new RecoveredFileDto(f.getName(), f.getAbsolutePath(), FILE, f.getSize(), f.getFileUniqueId(), f.isCompleted());

	@Override
	public List<RecoveredFileDto> apply(RecoveryChunkContainer rcc) {
		return rcc.getFileMap().values()
			.stream()
			.map(toRecoveredFileDto)
			.collect(Collectors.toList());
	}
}
