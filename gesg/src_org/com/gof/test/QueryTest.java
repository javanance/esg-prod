package com.gof.test;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import com.gof.dao.IrCurveSpotDao;
import com.gof.dao._LiqPremiumDao;
import com.gof.entity.IrCurveSpot;
import com.gof.entity._SmithWilsonResult;
import com.gof.interfaces.IIntRate;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class QueryTest {
	
	
	public static void main(String[] args) {
		aaa();
	}	
	
	
	private static void aaa() {
		String bssd = "201712";
		String sceNo ="10";
		
		
		
//		rst.stream().map(s ->s.convertToIrCurveHis()).forEach(s->log.info("zzz : {}", s.toString()));
//		rst.stream().map(s ->s.convertToBizDiscountRateSce("I", lpMap)).forEach(s->log.info("zzz : {}", s.toString()));
	}
	
	
}
