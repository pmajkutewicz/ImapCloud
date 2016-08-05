package pl.pamsoft.imapcloud.services.common;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.openhft.hashing.Access;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

public class DataInputStreamAccess extends Access<DataInputStream> {
	@Override
	@SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
	public int getByte(DataInputStream in, long l) {
		try {
			return in.readByte();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteOrder byteOrder(DataInputStream in) {
		return ByteOrder.nativeOrder();
	}

	@Override
	@SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
	public long getLong(DataInputStream in, long offset) {
		try {
			return in.readLong();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
	public int getInt(DataInputStream input, long offset) {
		try {
			return input.readInt();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
