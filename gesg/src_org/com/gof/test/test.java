package com.gof.test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.gof.dao.IrCurveSpotDao;
import com.gof.entity.IrCurveSpot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class test {

	public static void main(String[] args) {
//		String matCd1 ="M0038";
//		String matCd2 = "M0036";
//		
//		log.info("zzz :  {}", matCd1.compareTo(matCd2));
		
//		aaa();
//		bbb();
//		ccc();
		long aa = 10;
		int bb= 5;
		log.info("aaa : {}", bb-aa);
//		IrCurveHisDao.getEomTimeSeries("201812", "1010000", "M0084", -2).forEach(s-> log.info("aaa : {}", s.getMatCd()));
		log.info("aaa : {}",LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		log.info("aaa : {}",LocalDateTime.now().format(DateTimeFormatter.ISO_ORDINAL_DATE));
		log.info("aaa : {}",LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss")));
		System.out.println("KTB3Y".substring(3, 4));
	}
	
	private static void aaa() {
//		Map<String, String> eom = IrCurveHisDao.getEomMap("201812", "1010000");
//		eom.entrySet().forEach(s-> log.info("zzz : {},{}", s.getKey(), s.getValue()));
		
		List<IrCurveSpot> rst = IrCurveSpotDao.getEomTimeSeries("201812", "1010000", "M0084", -24);
		rst.forEach(s-> log.info("aaa : {}", s.getBaseDate()));
	}

	
	private static void bbb() {
//		
//		Job35_DiscRateFwdGenNew.getTimeSeries("201812", "1010000", "M0084", -24).entrySet().forEach(s-> log.info("zzz : {},{}", s.getKey(), s.getValue()));
		
//		Job35_DiscRateFwdGenNew.getTimeSeriesScenario("201812", "1010000", "M0084", "1", -24).entrySet().forEach(s-> log.info("zzz : {},{}", s.getKey(), s.getValue()));
		
		
	}
	private static void ccc() {
		
//		Job35_DiscRateFwdGenNew.createBizDiscRateFwdSce("201812", "I", "1010000", "2","M0084", -24).forEach(s-> log.info("zzz : {},{}", s.toString()));
		
		
	}
}
