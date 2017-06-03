package pl.pamsoft.imapcloud;

import net.openhft.hashing.LongHashFunction;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.testng.annotations.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toMap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@State(Scope.Benchmark)
public class JHMHashSpeedBenchmark {

	private static final int SIZE_100MB = 100 * 1024 * 1024;
	private MessageDigest md5;
	private MessageDigest keccak512;
	private MessageDigest sha3512;
	private MessageDigest blake2b512;

	private byte[] testData = new byte[SIZE_100MB];
	private Random random = new SecureRandom();

	@Setup(Level.Trial)
	public void classSetup() throws NoSuchAlgorithmException {
		Security.addProvider(new BouncyCastleProvider());
		md5 = MessageDigest.getInstance("MD5");
		keccak512 = MessageDigest.getInstance("KECCAK-512");
		sha3512 = MessageDigest.getInstance("SHA3-512");
		blake2b512 = MessageDigest.getInstance("BLAKE2B-512");
	}

	@Setup(Level.Invocation)
	public void init() throws NoSuchAlgorithmException {
		random.nextBytes(testData);
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public void xx64HashTimeOf100MiB(Blackhole blackhole) {
		blackhole.consume(LongHashFunction.xx().hashBytes(testData));
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public void keccak512HashTimeOf100MiB(Blackhole blackhole) {
		blackhole.consume(keccak512.digest(testData));
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public void blake2b512HashTimeOf100MiB(Blackhole blackhole) {
		blackhole.consume(blake2b512.digest(testData));
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public void sha3512HashTimeOf100MiB(Blackhole blackhole) {
		blackhole.consume(sha3512.digest(testData));
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public void md5HashTimeOf100MiB(Blackhole blackhole) {
		blackhole.consume(md5.digest(testData));
	}

	@Test
	public void startBenchmark() throws RunnerException, InterruptedException {
		Options opt = new OptionsBuilder()
			.include(".*" + JHMHashSpeedBenchmark.class.getSimpleName() + ".*")
			.warmupIterations(5)
			.measurementIterations(10)
			.forks(1)
			.build();

		Collection<RunResult> results = new Runner(opt).run();
		Map<String, Double> resultMap = results.stream()
			.map(RunResult::getPrimaryResult)
			.collect(toMap(Result::getLabel, Result::getScore));

		assertEquals(results.size(), 5);
		assertTrue(resultMap.get("md5HashTimeOf100MiB") > resultMap.get("xx64HashTimeOf100MiB"));
	}
}
