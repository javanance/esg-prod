package com.gof.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.util.FileUtils;

public class Test2 {
	private final static Logger logger = LoggerFactory.getLogger("DAO");

			
	public static void main(String[] args) {
		int core = Runtime.getRuntime().availableProcessors();

		logger.info("core : {},{}", core, Thread.currentThread().getName());
		double rst = 0.0;
		
		Double[] input = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0,10.0
				,11.0, 12.0, 13.0, 14.0, 15.0, 16.0,17.0, 18.0, 19.0,20.0
//				,21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0,30.0
//				,31.0, 32.0, 33.0, 34.0, 35.0, 36.0, 37.0, 38.0, 39.0,40.0
				};
//		Double[] input = {1.0, 2.0, 3.0, 4.0, 5.0};
//		Double[] input = {1.0, 2.0, 3.0};
		
		Stream<Double> in =Stream.of(input);
		int poolSize = Math.max(core, input.length);
//		int poolSize = 5;
		ExecutorService exe = Executors.newFixedThreadPool(poolSize, new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				
				Thread t = new Thread(r);
//				System.out.println("thread Name : "+ t.getName());
				t.setDaemon(true);
				return t;
			}
		});
//		Path irScePath = Paths.get("D:\\Dev\\ESG\\zzz"+rst+".csv");
//		aaa(in, exe).stream().forEach(s -> FileUtils.writeHeader(irScePath, s));
		
		aaa(in, exe).stream().forEach(s -> logger.info("aaa : {}", "bb"));
		
		
		logger.info("rst : {}", rst);
	}
	
	
	
	private static List<String> aaa(Stream<Double> in, ExecutorService exe){
		 List<CompletableFuture<String>> rstFuture =
					in.map(aa -> CompletableFuture.supplyAsync(() -> doAsync(aa), exe))
					.collect(Collectors.toList());
//		 List<String> rst =rstFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());
		 
		 
		 /*Stream<CompletableFuture<String>> rstFuture =
					in.map(aa -> CompletableFuture.supplyAsync(() -> doAsync(aa), exe))
					;*/
//		 List<String> rst =rstFuture.map(CompletableFuture::join).collect(Collectors.toList());
		 
		 Path irScePath = Paths.get("D:\\Dev\\ESG\\zzz.csv");
//		 FileUtils.writeHeader(irScePath, "AAA"+rst +"_");
		 FileUtils.writeHeader(irScePath, "AAA");
		 try {
//				logger.info("Current Thread 2:  {},{}", Thread.currentThread().getId(),Thread.currentThread().getName());
				Files.write(irScePath, (Iterable<String>)rstFuture.stream().map(CompletableFuture::join).collect(Collectors.toList())::iterator,StandardOpenOption.APPEND);
			} catch (Exception e) {
				logger.error("Error in IR Scenario result writing : {}", e);
			}
		 
		 return new ArrayList<>();
 
	}
	


	public static String doAsync(Double rst) {
		logger.info("start Something : {},{}", rst, Thread.currentThread().getName());
		double rstNew =0.0;
		
		Path irScePath = Paths.get("D:\\Dev\\ESG\\zzz"+rst+".csv");
		
		StringBuilder builder = new StringBuilder();
		
			for (int t = 1; t < rst ; t++) {
				for (int j = 1; j < 100000; j++) {
	
					for (int i = 0; i < 10000; i++) {
	//					rstNew = rstNew + i * 1.0 + j * 1.0 + t;
						rstNew = rstNew + i    ;
					}
	
					builder.append(rstNew).append("\n");
	//				}
				}
			}
		
		FileUtils.writeHeader(irScePath, "AAA"+rst +"_"+builder.toString());
		logger.info("End Something : {}", rst);
		return builder.toString();
	}
	
}
