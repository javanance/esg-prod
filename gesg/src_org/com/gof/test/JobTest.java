package com.gof.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.hibernate.Session;

import com.gof.enums.EJob;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobTest {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
			
	public static void main(String[] args) {
		List<String> jobList = new ArrayList<String>();
		
		String gocId ="aoabboaaaz";
		Function<String, Stream<? extends Object>> func = s ->   aaa(s);
		
		job1(gocId, aaa(gocId));
		job2(gocId, func);
		
		
		for(EJob job : EJob.values()){
			if(jobList.contains(job.ordinal())) {
				job1(gocId, aaa(gocId));
//				job2(gocId, func);
			}
			
		}
		
	}
	private static void job1(String gocId, Stream<? extends Object> stream) {
		session.beginTransaction();
//		JobLog jobLog = startJogLog(EJob.ESG2);
		try {
			
			stream.forEach(s-> log.info("zzz : {}", s));
			
//			completJob("SUCCESS", jobLog);
		} catch (Exception e) {
//			completJob("ERROR", jobLog);
		}
		
//		session.saveOrUpdate(jobLog);
		session.getTransaction().commit();
		
}
	private static void job2(String gocId, Function<String, Stream<? extends Object>> func) {
			session.beginTransaction();
//			JobLog jobLog = startJogLog(EJob.ESG2);
			try {
				
				func.apply(gocId).forEach(s-> log.info("zzz : {}", s));
				
//				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
//				completJob("ERROR", jobLog);
			}
			
//			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
			
	}
		
	private static Stream<String> aaa(String gocId){
		return Arrays.stream(gocId.split("o"));
	}
	
	
	

	
	
}
