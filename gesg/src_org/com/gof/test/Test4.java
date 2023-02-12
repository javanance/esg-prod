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

import com.gof.dao._IrCurveSceDao;
import com.gof.util.FileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test4 {
	private final static Logger logger = LoggerFactory.getLogger("DAO");

			
	public static void main(String[] args) {
		int core = Runtime.getRuntime().availableProcessors();
		int cnt =1;
//		IrCurveSceDao.getIrCurveSce("201812", "1010000").forEach(s->log.info("zz : {},{}", s.getSceNo(), s.getMatCd(), s.getBaseYymm()));
		_IrCurveSceDao.getBizIrCurveSceStream("201812", "A", "1010000").forEach(s->log.info("zz : {},{}", s.getSceNo(), s.getMatCd(), s.getBaseYymm()));
		
	}
	
	

}
