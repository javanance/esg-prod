package com.gof.process;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gof.dao.IrCurveYtmDao;
import com.gof.dao.IrDcntRateDao;
import com.gof.entity.IrCurveSpot;
import com.gof.entity.IrCurveYtm;
import com.gof.entity.IrDcntRate;
import com.gof.entity.IrDcntRateBu;
import com.gof.entity.IrParamSw;
import com.gof.enums.EJob;
import com.gof.model.SmithWilsonKics;
import com.gof.model.SmithWilsonKicsBts;
import com.gof.model.entity.SmithWilsonRslt;
import com.gof.util.DateUtil;
import com.gof.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Esg270_IrDcntRate extends Process {	
	
	public static final Esg270_IrDcntRate INSTANCE = new Esg270_IrDcntRate();
	public static final String jobId = INSTANCE.getClass().getSimpleName().toUpperCase().substring(0, ENTITY_LENGTH);	
	
	public static List<IrDcntRate> createIrDcntRate(String bssd, String applBizDv, Map<String, Map<Integer, IrParamSw>> paramSwMap, Integer projectionYear) {	
		
		List<IrDcntRate> rst = new ArrayList<IrDcntRate>();
		
		for(Map.Entry<String, Map<Integer, IrParamSw>> curveSwMap : paramSwMap.entrySet()) {			

			Map<String, IrDcntRate> adjRateSce1Map       = new TreeMap<String, IrDcntRate>();
			Map<String, SmithWilsonRslt> baseRateSce1Map = new TreeMap<String, SmithWilsonRslt>();  			//for using SmithWilsonKicsBts not SmithWilsonKics
//			List<IrDcntRate> adjRateSce1List             = new ArrayList<IrDcntRate>();			                //hereafter for KICS SCE_NO 7 and 8(totalShift)
//			double ltfr1 = 0.0;
//			double shift = 0.0;
			
			for(Map.Entry<Integer, IrParamSw> swSce : curveSwMap.getValue().entrySet()) {				
				
				log.info("BIZ: [{}], IR_CURVE_ID: [{}], IR_CURVE_SCE_NO: [{}]", applBizDv, curveSwMap.getKey(), swSce.getKey());
				List<IrCurveSpot> irCurveSpotList = IrDcntRateDao.getIrDcntRateBuToAdjSpotList(bssd, applBizDv, curveSwMap.getKey(), swSce.getKey());
				
				if(irCurveSpotList.size()==0) {
					log.warn("No IR Dcnt Rate Data [BIZ: {}, IR_CURVE_ID: {}, IR_CURVE_SCE_NO: {}] in [{}] for [{}]", applBizDv, curveSwMap.getKey(), swSce.getKey(), toPhysicalName(IrDcntRateBu.class.getSimpleName()), bssd);
					continue;
				}				
				
				LocalDate baseDate = DateUtil.convertFrom(bssd).with(TemporalAdjusters.lastDayOfMonth());
//				log.info("{}, {}, {}", swSce.getValue().getLtfr(), swSce.getValue().getLtfrCp(), projectionYear);
								
				SmithWilsonKics swKics = new SmithWilsonKics(baseDate, irCurveSpotList, CMPD_MTD_DISC, true, swSce.getValue().getLtfr(), swSce.getValue().getLtfrCp(), projectionYear, 1, 100, DCB_MON_DIF);				
				List<IrDcntRate> adjRateList = swKics.getSmithWilsonResultList().stream().map(s -> s.convert()).collect(Collectors.toList());
				
				
				
				Map<String, IrDcntRate> adjRateMap = adjRateList.stream().collect(Collectors.toMap(IrDcntRate::getMatCd, Function.identity(), (k, v) -> k, TreeMap::new));				
				TreeSet<Double> tenorList = adjRateList.stream().map(s -> Double.valueOf(1.0 * Integer.valueOf(s.getMatCd().substring(1)) / MONTH_IN_YEAR)).collect(Collectors.toCollection(TreeSet::new));
				double[] prjTenor = tenorList.stream().mapToDouble(Double::doubleValue).toArray();				
				
				if(applBizDv.equals("KICS")) {
					if( swSce.getKey()==1 || swSce.getKey() > 6) {
						adjRateSce1Map = adjRateList.stream().collect(Collectors.toMap(IrDcntRate::getMatCd, Function.identity(), (k, v) -> k, TreeMap::new));										
						
						List<IrCurveYtm> ytmList = IrCurveYtmDao.getIrCurveYtm(bssd, curveSwMap.getKey());
						if(ytmList.size()==0) {
							log.warn("No Historical YTM Data exist for [{}, {}] in [{}]", bssd, curveSwMap.getKey(), jobId);
							continue;
						}				
						
						SmithWilsonKicsBts swBts = SmithWilsonKicsBts.of()
								.baseDate(baseDate)					
								.ytmCurveHisList(ytmList)
								.alphaApplied(StringUtil.objectToPrimitive(swSce.getValue().getSwAlphaYtm(), 0.1))													 
								.freq(StringUtil.objectToPrimitive(swSce.getValue().getFreq(), 2))
								.build();						
						
						baseRateSce1Map = swBts.getSmithWilsonResultList(prjTenor).stream().collect(Collectors.toMap(SmithWilsonRslt::getMatCd, Function.identity()));
						
						for(IrDcntRate rslt : adjRateList) {						
							rslt.setSpotRate(baseRateSce1Map.get(rslt.getMatCd()).getSpotDisc());
							rslt.setFwdRate (baseRateSce1Map.get(rslt.getMatCd()).getFwdDisc());
						}					
						
					}
					
					//for KICS: Asset Discount Rate Scenario after scen#1 is generated from Above Insurance Discount Rate + Difference Rate of Insurance - Asset at SCE#1
					else if(swSce.getKey() <= 6) {
						TreeMap<String, Double> spotRateMap = new TreeMap<String, Double>();
						TreeMap<String, Double> fwdRateMap  = new TreeMap<String, Double>();
						
						for(IrDcntRate rslt : adjRateList) {						
							String matCd   = rslt.getMatCd();						
							double adjRate = adjRateMap.get(matCd).getAdjSpotRate();
							double adjDiff = baseRateSce1Map.get(matCd).getSpotDisc() - adjRateSce1Map.get(matCd).getAdjSpotRate();
							
							rslt.setSpotRate(adjRate + adjDiff);						
							spotRateMap.put(matCd, adjRate + adjDiff);
						}					
						fwdRateMap = irSpotDiscToFwdM1Map(spotRateMap);					

						for(IrDcntRate rslt : adjRateList) {
							rslt.setFwdRate(fwdRateMap.get(rslt.getMatCd()).doubleValue());
						}		
					}
					
				}
				
//				else if(!applBizDv.equals("KICS")) {
				else  {
					
					adjRateSce1Map = adjRateList.stream().collect(Collectors.toMap(IrDcntRate::getMatCd, Function.identity(), (k, v) -> k, TreeMap::new));		
					
					List<IrCurveYtm> ytmList = IrDcntRateDao.getIrDcntRateBuToBaseSpotList(bssd, applBizDv, curveSwMap.getKey(), swSce.getKey()).stream().map(s -> s.convertSimpleYtm()).collect(Collectors.toList());					
					if(ytmList.size()==0) {
						log.warn("No IR Dcnt Rate Data [BIZ: {}, IR_CURVE_ID: {}, IR_CURVE_SCE_NO: {}] in [{}] for [{}]", applBizDv, curveSwMap.getKey(), swSce.getKey(), toPhysicalName(IrDcntRateBu.class.getSimpleName()), bssd);
						continue;
					}
					
					SmithWilsonKicsBts swBts = SmithWilsonKicsBts.of()
							 									 .baseDate(baseDate)					
							 									 .ytmCurveHisList(ytmList)
							 									 .alphaApplied(StringUtil.objectToPrimitive(swSce.getValue().getSwAlphaYtm(), 0.1))													 
							 									 .freq(0)
							 									 .build();						

//				    swBts.getSmithWilsonResultList(prjTenor).stream().filter(s -> Double.parseDouble(s.getMatCd().substring(1, 5)) <= 240).forEach(s -> log.info("{}, {}, {}", s.getMatCd(), s.getSpotDisc(), s.getFwdDisc()));
					baseRateSce1Map = swBts.getSmithWilsonResultList(prjTenor).stream().collect(Collectors.toMap(SmithWilsonRslt::getMatCd, Function.identity()));

					for(IrDcntRate rslt : adjRateList) {						
						rslt.setSpotRate(baseRateSce1Map.get(rslt.getMatCd()).getSpotDisc());
						rslt.setFwdRate (baseRateSce1Map.get(rslt.getMatCd()).getFwdDisc());
					}					
				}

				for(IrDcntRate rslt : adjRateList) {
					rslt.setBaseYymm(bssd);
					rslt.setApplBizDv(applBizDv);
					rslt.setIrCurveId(curveSwMap.getKey());
					rslt.setIrCurveSceNo(swSce.getKey());
					rslt.setLastModifiedBy(jobId);
					rslt.setLastUpdateDate(LocalDateTime.now());
				}				
				
				
				for(IrDcntRate dcnt : adjRateList) {
					if(dcnt.getSpotRate().isNaN() || dcnt.getSpotRate().isInfinite() || dcnt.getAdjSpotRate().isNaN() || dcnt.getAdjSpotRate().isInfinite()) {
//						log.info("{}, {}, {}", curveSwMap.getKey(), swSce.getKey(), dcnt);
						log.error("Smith-Wilson Interpolation is failed. Check Shock Spread Data in [{}] Table for [BIZ: {}, IR_CURVE_ID: {}, IR_CURVE_SCE_NO: {}] in [{}]", Process.toPhysicalName(IrCurveYtm.class.getSimpleName()), applBizDv, curveSwMap.getKey(), swSce.getKey(), bssd);
						try {
							throw new Exception();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						return new ArrayList<IrDcntRate>();
					}
				}
				
				rst.addAll(adjRateList);
			}
		}		
		log.info("{}({}) creates [{}] results of [{}] {}. They are inserted into [{}] Table", jobId, EJob.valueOf(jobId).getJobName(), rst.size(), applBizDv, paramSwMap.keySet(), toPhysicalName(IrDcntRate.class.getSimpleName()));
		
		return rst;		
	}	

}

