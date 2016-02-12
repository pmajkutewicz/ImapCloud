package pl.pamsoft.imapcloud;

import org.junit.Test;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Streamtest {

	@FunctionalInterface
	public interface Funct_WithExceptions<T, R, E extends Exception> {
		R apply(T t) throws E;
	}

	@Test
	public void test() {
		IntUnaryOperator intUnaryOperator = a ->{
			if (a == 3) throw new RuntimeException("asdasda");
			return a;
		};

		try {

			IntStream.range(1,1000)
				.map(intUnaryOperator)
				.forEach(System.out::println);
		}catch (RuntimeException e) {

		}

		System.out.println("ok");
	}
}
