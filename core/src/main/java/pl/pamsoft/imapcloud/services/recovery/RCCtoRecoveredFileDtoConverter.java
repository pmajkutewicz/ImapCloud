package pl.pamsoft.imapcloud.services.recovery;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import pl.pamsoft.imapcloud.dto.RecoveredFileDto;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.containers.RecoveryChunkContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.bouncycastle.pqc.math.linearalgebra.ByteUtils.fromHexString;
import static pl.pamsoft.imapcloud.dto.FileDto.FileType.FILE;

public class RCCtoRecoveredFileDtoConverter implements Function<RecoveryChunkContainer, List<RecoveredFileDto>> {

	private CryptoService cs;

	public RCCtoRecoveredFileDtoConverter(CryptoService cryptoService) {
		this.cs = cryptoService;
	}

	@Override
	public List<RecoveredFileDto> apply(RecoveryChunkContainer rcc) {
		PaddedBufferedBlockCipher key = cs.getDecryptingCipher(fromHexString(rcc.getAccount().getCryptoKey()));

		List<RecoveredFileDto> result = new ArrayList<>(rcc.getFileMap().size());
		for (File f : rcc.getFileMap().values()) {
			try {
				String name = new String(cs.decryptHex(key, f.getName()), UTF_8);
				String path = new String(cs.decryptHex(key, f.getAbsolutePath()), UTF_8);
				result.add(new RecoveredFileDto(name, path, FILE, f.getSize(), f.getFileUniqueId(), f.isCompleted()));
			} catch (IOException | InvalidCipherTextException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
