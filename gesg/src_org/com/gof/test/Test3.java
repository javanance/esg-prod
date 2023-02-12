package com.gof.test;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.util.FileUtils;

public class Test3 {
	private final static Logger logger = LoggerFactory.getLogger("DAO");

			
	public static void main(String[] args) {
		int core = Runtime.getRuntime().availableProcessors();
		
		IntStream.range(0,5).forEach(s -> logger.info("ss :{}", s));

		logger.info("core : {},{}", core, Thread.currentThread().getName());
		double rst = 0.0;
		
		Double[] input = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0,10.0,11.0, 12.0, 13.0, 14.0, 15.0};
//		Double[] input = {1.0, 2.0, 3.0, 4.0, 5.0};
//		Double[] input = {1.0, 2.0, 3.0};
		
		Stream<Double> in =Stream.of(input);
//		int poolSize = Math.max(core, input.length);
		int poolSize = 5;
		ExecutorService exe = Executors.newFixedThreadPool(poolSize, new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				
				Thread t = new Thread(r);
//				System.out.println("thread Name : "+ t.getName());
				t.setDaemon(true);
				return t;
			}
		});
		
		aaa(in, exe).stream().forEach(s -> logger.info("aaa : {}", s));
		
				
//		aaa(Stream.of(input), Math.max(core, input.length)).stream().forEach(s -> logger.info("bbb : {}", s));
		
//		aaa(in,3).stream().forEach(s -> logger.info("aaa : {}", s));
//		aaa(in).stream().forEach(s -> logger.info("aaa : {}", s));
		
		
//		kkk();
//		zzz();
		logger.info("rst : {}", rst);
	}
	
	
	
	private static List<Double> aaa(Stream<Double> in, ExecutorService exe){
		 List<CompletableFuture<Double>> rstFuture =
					in.map(aa -> CompletableFuture.supplyAsync(() -> doAsync(aa), exe))
//					in.map(aa -> CompletableFuture.supplyAsync(Test2::doAsync, exe))
					.collect(Collectors.toList());


		 return rstFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());
	}
	
	private static List<Double> aaa(Stream<Double> in, int poolSize){
		ExecutorService exe = Executors.newFixedThreadPool(poolSize, new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}
		});
		
		 List<CompletableFuture<Double>> rstFuture =
					in.map(aa -> CompletableFuture.supplyAsync(() -> doAsync(aa), exe))
//					in.map(aa -> CompletableFuture.supplyAsync(() -> test2::doAsync, exe))
					.collect(Collectors.toList());
		 
		 
		 return rstFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());
	}
	
	private static List<Double> aaa(Stream<Double> in){
		
		 List<CompletableFuture<Double>> rstFuture =
					in.map(aa -> CompletableFuture.supplyAsync(() -> doAsync(aa)))
					.collect(Collectors.toList());
		 
		 return rstFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());
	}
	
	private static void zzz() {
		String product = "aaa";
		
		double rst = getPrice(product);
		rst = doSomething(rst);
	}
	
	private static void kkk() {
		String product = "aaa";
		
		Future<Double> futurePrice1 = getPriceAsnc("AAA");
		Future<Double> futurePrice2 = getPriceAsnc("BBB");
		Future<Double> futurePrice3 = getPriceAsnc("CCC");
		
		double rst = 0.0;
		
		rst = doSomething(rst);
		
		try {
			logger.info("Wait for resutl : {}" );
			double price = futurePrice1.get();
			double price1 = futurePrice2.get();
			double price2 = futurePrice3.get();
			logger.info("Price : {},{},{}", price, price1, price2);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}


	public static Double doAsync(Double rst) {
		logger.info("start Something : {},{}", rst, Thread.currentThread().getName());
		double rstNew =0.0;
		Path irScePath = Paths.get("D:\\Dev\\ESG\\zzz"+rst+".csv");
		FileUtils.writeHeader(irScePath, "AAA"+rst +"_"+ Thread.currentThread().getName());
		
		
		try (BufferedWriter writer = Files.newBufferedWriter(irScePath, Charset.defaultCharset(), StandardOpenOption.APPEND))	{
			logger.info("start Something : {},{}", rst, Thread.currentThread().getName() );
			for (int t = 1; t < rst ; t++) {
				for (int j = 1; j < 100000; j++) {
	
					for (int i = 0; i < 10000; i++) {
	//					rstNew = rstNew + i * 1.0 + j * 1.0 + t;
						rstNew = rstNew + i    ;
					}
	
	//				synchronized (writer) {
						writer.append("a_"+t+"_"+j+"_"+rstNew).append("\n");
	//				}
				}
				writer.flush();
			}
			writer.close();
		}
		catch(Exception e)
		{
			logger.info("Error start Something : {},{}", rst, Thread.currentThread().getName() );
			logger.error("Error in IR Scenario result writing : {}", e);
		}
		
			
		
		logger.info("End Something : {}", rst);
		return rstNew;
	}
	
/*	public static Double doAsync(Double rst) {
		logger.info("start Something : {},{}", rst, Thread.currentThread().getName());
		double rstNew =0.0;
		Path irScePath = Paths.get("D:\\Dev\\ESG\\zzz"+rst+".csv");
		FileUtils.writeHeader(irScePath, "AAA"+rst +"_"+ Thread.currentThread().getName());
		
		
		for (int t = 1; t < rst ; t++) {
			for (int j = 1; j < 100000; j++) {

				for (int i = 0; i < 10000; i++) {
//					rstNew = rstNew + i * 1.0 + j * 1.0 + t;
					rstNew = rstNew + i    ;
				}

				logger.info("start Something : {},{}", rst, Thread.currentThread().getName() );
				try (BufferedWriter writer = Files.newBufferedWriter(irScePath, Charset.defaultCharset(), StandardOpenOption.APPEND))	{
//				synchronized (writer) {
					writer.append("a_"+rstNew).append("\n");
//					writer.close();
//				}
				}
				catch(Exception e)
				{
					logger.info("Error start Something : {},{}", rst, Thread.currentThread().getName() );
					logger.error("Error in IR Scenario result writing : {}", e);
				}
			}
			
			
		}
		
			
		
		logger.info("End Something : {}", rst);
		return rstNew;
	}*/
	private static double doSomething(double rst) {
		for (int i = 0; i < 10000; i++) {
			for (int j = 0; j < 10000; j++) {

				for (int t = 0; t <100; t++) {
					rst = rst + i * 1.0 + j * 1.0 + t;
				}

			}
		}
		logger.info("End Something");
		return rst;
	}

	public static Future<Double> getPriceAsnc(String product) {
		CompletableFuture<Double> futurePrice = new CompletableFuture<>();
		new Thread(() -> {
			try {
				double price = calculatePrice(product);
				futurePrice.complete(price);

			} catch (Exception e) {
				futurePrice.completeExceptionally(e);
			}
		}).start();
		logger.info("ascn start");
		return futurePrice;
	}

	public static double getPrice(String product) {
		logger.info("start price job");
		return calculatePrice(product);
	}

	private static double calculatePrice(String product) {
		delay();
		Random random = new Random();
		logger.info("End Job");
		Path irScePath = Paths.get("D:\\Dev\\ESG\\"+product+".csv");

		
		FileUtils.writeHeader(irScePath, product);

		
		return random.nextDouble() * product.charAt(0) + product.charAt(1);
//		return doSomething(0.0);
	}

	public static void delay() {
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static long iterativeSum(long n) {
		long result = 0;
		for (long i = 1L; i <= n; i++) {
			result += i;
		}
		return result;
	}

	public long measureSumPerf(Function<Long, Long> adder, long n) {
		long fastest = Long.MAX_VALUE;
		for (int i = 0; i < 10; i++) {
			long start = System.nanoTime();
			long sum = adder.apply(n);
			long duration = (System.nanoTime() - start) / 1_000_000;
			System.out.println("Result: " + sum);
			if (duration < fastest)
				fastest = duration;
		}
		return fastest;
	}

	public static long parallelRangedSum(long n) {
		return LongStream.rangeClosed(1, n).parallel().reduce(0L, Long::sum);
	}

}
