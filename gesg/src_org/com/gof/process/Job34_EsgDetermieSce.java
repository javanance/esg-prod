package com.gof.process;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao._BizDiscountRateDao;
import com.gof.entity.IrDcntRate;
import com.gof.entity.IrDcntSceStoBiz;
import com.gof.entity._BizStockYield;
import com.gof.entity.IrCurve;
import com.gof.entity.StdAsst;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job34_EsgDetermieSce {
	
	public static Stream<_BizStockYield> createDetermineSce(String bssd, IrCurve curveMst, List<StdAsst> asstCdList) {
		
		return _BizDiscountRateDao.getTermStructure(bssd, curveMst.getApplBizDv(), curveMst.getIrCurveId()).stream()
								
								.flatMap(s->build(bssd, s, asstCdList))
								;
	}
	
	public static Stream<_BizStockYield> createDetermineSce1(String bssd, IrCurve curveMst, List<StdAsst> asstCdList) {
		
		List<_BizStockYield> rstList = new ArrayList<_BizStockYield>();
		String bizDv = curveMst.getApplBizDv();
		
		Map<String, IrDcntRate> bizRateMap = _BizDiscountRateDao.getTermStructure(bssd, bizDv, curveMst.getIrCurveId()).stream()
												.collect(toMap(IrDcntRate::getMatCd, Function.identity()));
		
		
		for(StdAsst asstCd : asstCdList) {
			int durationMonth =1;
			if(asstCd.getStdAsstCd().contains("1Y")){
				durationMonth =  12;
			}
			else if(asstCd.getStdAsstCd().contains("3Y")){
				durationMonth =  36;
			}
			else if(asstCd.getStdAsstCd().contains("5Y")){
				durationMonth =  60;
			}
			
//			int durationMonth =  asstCd.getStdAsstTypCd().contains("BOND")? 36	: 1;
			
			for(int i=0 ; i< bizRateMap.size(); i++) {
				List<IrDcntRate> bizRateList = new ArrayList<IrDcntRate>();
				String matCd = "M"+ String.format("%04d", i+1);
				int maxIndex = Math.min(bizRateMap.size(), i + durationMonth);
				
				for(int j=i  ; j < maxIndex ; j++) {
					String tempMatCd = "M"+ String.format("%04d", j+1);
//					log.info("aaaa: {},{}", j, tempMatCd);
					bizRateList.add(bizRateMap.get(tempMatCd));
				}
//				rstList.add(build(bssd, bizDv, matCd, bizRateList, asstCd, durationMonth));
				
				
				Map<Integer, Double> bizFwdRateMap  = new HashMap<Integer, Double>();
				for(int j=i  ; j < i+durationMonth ; j++) {
					int minIndex = Math.min(bizRateMap.size(), j+1);
					String tempMatCd = "M"+ String.format("%04d", minIndex);
					log.info("aaaa: {},{}", j, tempMatCd);
					bizFwdRateMap.put(j, bizRateMap.get(tempMatCd).getAdjFwdRate());
				}
				
				
				rstList.add(build(bssd, bizDv, matCd, bizFwdRateMap, asstCd, durationMonth));
				log.info("aaaaaa : {},{},{},{},{}", asstCd.getStdAsstCd(),   bizRateList.size());
			}
		}
		
		return rstList.stream();
	}

	private static _BizStockYield build(String bssd, String bizDv, String matCd, List<IrDcntRate> bizRateList, StdAsst asstMst, int durationMonth){
		
		double df =1.0;
		
		for(IrDcntRate aa : bizRateList) {
			df = df * Math.pow(1 + aa.getAdjFwdRate(), -1.0 /12.0);
		}
		double ytm = Math.pow(df, -12.0 / durationMonth) -1;
//		log.info("aaaaaa : {},{},{},{},{}", asstMst.getStdAsstCd(),  matCd, bizRateList.size(), df, ytm);
		return _BizStockYield.builder()
								.baseYymm(bssd)
								.applBizDv(bizDv)
								.stdAsstCd(asstMst.getStdAsstCd())
								.fwdMatCd(matCd)
								.asstYield(Math.pow(1+ ytm, 1.0/12.0) -1)
								.lastModifiedBy("ESG_34")
								.lastUpdateDate(LocalDateTime.now())
								.build()
						;
		

	}
	
	private static _BizStockYield build(String bssd, String bizDv, String matCd, Map<Integer,Double> bizFwdRateMap, StdAsst asstMst, int durationMonth){
		
		double df =1.0;
		
		for(Map.Entry<Integer, Double> entry : bizFwdRateMap.entrySet()) {
			df = df * Math.pow(1 + entry.getValue(), -1.0 /12.0);
		}
		double ytm = Math.pow(df, -12.0 / durationMonth) -1;
//		log.info("aaaaaa : {},{},{},{},{}", asstMst.getStdAsstCd(),  matCd, bizRateList.size(), df, ytm);
		return _BizStockYield.builder()
								.baseYymm(bssd)
								.applBizDv(bizDv)
								.stdAsstCd(asstMst.getStdAsstCd())
								.fwdMatCd(matCd)
								.asstYield(Math.pow(1+ ytm, 1.0/12.0) -1)
								.lastModifiedBy("ESG_34")
								.lastUpdateDate(LocalDateTime.now())
								.build()
						;
		

	}
	
	private static Stream<_BizStockYield> build(String bssd, IrDcntRate bottomupRate, List<StdAsst> asstCdList){
		List<_BizStockYield> rstList = new ArrayList<_BizStockYield>();
		for(StdAsst asst  : asstCdList) {
			rstList.add(_BizStockYield.builder()
								.baseYymm(bssd)
								.applBizDv(bottomupRate.getApplBizDv())
								.stdAsstCd(asst.getStdAsstCd())
								.fwdMatCd(bottomupRate.getMatCd())
								.asstYield(Math.pow(1+bottomupRate.getAdjFwdRate(), 1.0/12.0) -1)
								.lastModifiedBy("ESG_34")
								.lastUpdateDate(LocalDateTime.now())
								.build()
						);
		}
		return rstList.stream();
	}
}


