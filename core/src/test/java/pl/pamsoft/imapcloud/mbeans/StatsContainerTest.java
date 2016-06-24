package pl.pamsoft.imapcloud.mbeans;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class StatsContainerTest {

	@DataProvider(name = "shouldFormatData")
	public static Object[][] primeNumbers() {
		long value = 1024;
		return new Object[][] {
			{1, 1, 1, "{min=1.000 ns, max=1.000 ns, avg=1.000 ns}"},
			{value, value, value, "{min=1.024 μs, max=1.024 μs, avg=1.024 μs}"}
		};
	}

	@Test(dataProvider = "shouldFormatData")
	public void shouldFormatData(long min, long max, long avg, String expected) {
		assertEquals(new StatsContainer(min, max, avg).toString(), expected);
	}

}
