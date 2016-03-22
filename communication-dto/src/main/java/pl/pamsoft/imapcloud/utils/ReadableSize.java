package pl.pamsoft.imapcloud.utils;

import java.text.DecimalFormat;

public class ReadableSize {

	private static final int KIB = 1024;
	private static final String[] UNITS = new String[]{"B", "kiB", "MiB", "GiB", "TiB"};

	public static String getReadableFileSize(Long size) {
		if (null == size || size <= 0) {
			return "0";
		}
		int digitGroups = (int) (Math.log10(size) / Math.log10(KIB));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(KIB, digitGroups)) + " " + UNITS[digitGroups];
	}
}
