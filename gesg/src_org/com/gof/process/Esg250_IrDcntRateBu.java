package com.gof.process;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.gof.dao.IrCurveSpotDao;
import com.gof.dao.IrSprdDao;
import com.gof.entity.IrCurveSpot;
import com.gof.entity.IrDcntRateBu;
import com.gof.entity.IrParamSw;
import com.gof.entity.IrSprdAfnsBiz;
import com.gof.entity.IrSprdLpBiz;
import com.gof.enums.EJob;
import com.gof.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Esg250_IrDcntRateBu extends Process {	
	
	public static final Esg250_IrDcntRateBu INSTANCE = new Esg250_IrDcntRateBu();
	public static final String jobId = INSTANCE.getClass().getSimpleName().toUpperCase().substring(0, ENTITY_LENGTH);	
	
	public static List<IrDcntRateBu> setIrDcntRateBu(String bssd, String irModelId, String applBizDv, Map<String, Map<Integer, IrParamSw>> paramSwMap) {	
		
		List<IrDcntRateBu> rst = new ArrayList<IrDcntRateBu>();
		
		for(Map.Entry<String, Map<Integer, IrParamSw>> curveSwMap : paramSwMap.entrySet()) {		
			
			List<IrCurveSpot> spotList = IrCurveSpotDao.getIrCurveSpot(bssd, curveSwMap.getKey());
			TreeMap<String, Double> spotMap = spotList.stream().collect(Collectors.toMap(IrCurveSpot::getMatCd, IrCurveSpot::getSpotRate, (k, v) -> k, TreeMap::new));
			
			if(spotList.isEmpty()) {
				log.error("No IR Curve Spot Data [BIZ: [{}], IR_CURVE_ID: {}] in [{}] for [{}]", applBizDv, curveSwMap.getKey(), toPhysicalName(IrCurveSpot.class.getSimpleName()), bssd);
				continue;
			}

			for(Map.Entry<Integer, IrParamSw> swSce : curveSwMap.getValue().entrySet()) {
				
				Map<String, Double> irSprdLpMap = IrSprdDao.getIrSprdLpBiz(bssd, applBizDv, curveSwMap.getKey(), swSce.getKey()).stream()
						                                   .collect(Collectors.toMap(IrSprdLpBiz::getMatCd, IrSprdLpBiz::getLiqPrem));

				Map<String, Double> irSprdShkMap = IrSprdDao.getIrSprdAfnsBiz(bssd, irModelId, curveSwMap.getKey(), swSce.getKey()).stream()
                        						            .collect(Collectors.toMap(IrSprdAfnsBiz::getMatCd, IrSprdAfnsBiz::getShkSprdCont));				
				

				List<IrCurveSpot> spotSceList = spotList;				
				String fwdMatCd = StringUtil.objectToPrimitive(swSce.getValue().getFwdMatCd(), "M0000");				
				
				if(spotMap.containsKey(fwdMatCd)) {					
					Map<String, Double> fwdSpotMap = irSpotDiscToFwdMap(bssd, spotMap, fwdMatCd);					
//					spotSceList.stream().forEach(s -> s.setSpotRate(fwdSpotMap.get(s.getMatCd())));					
				}				

				String pvtMatCd = StringUtil.objectToPrimitive(swSce.getValue().getPvtRateMatCd() , "M0000");
				double pvtRate  = StringUtil.objectToPrimitive(spotMap.getOrDefault(pvtMatCd, 0.0), 0.0    );				
				double pvtMult  = StringUtil.objectToPrimitive(swSce.getValue().getMultPvtRate()  , 0.0    );				
				double intMult  = StringUtil.objectToPrimitive(swSce.getValue().getMultIntRate()  , 1.0    );
				double addSprd  = StringUtil.objectToPrimitive(swSce.getValue().getAddSprd()      , 0.0    );
				int    llp      = StringUtil.objectToPrimitive(swSce.getValue().getLlp()          , 20     );				
				
//				log.info("{}, {}, {}, {}, {}, {}, {}, {}, {}", applBizDv, curveSwMap.getKey(), swSce.getKey(), pvtMatCd, pvtRate, pvtMult, intMult, addSprd, llp);
				for(IrCurveSpot spot : spotSceList) {				
					if(Integer.valueOf(spot.getMatCd().substring(1)) <=  llp * MONTH_IN_YEAR) {
						
						IrDcntRateBu dcntRateBu = new IrDcntRateBu();						
						
						double baseSpot = intMult * (StringUtil.objectToPrimitive(spot.getSpotRate()) + pvtMult * pvtRate) + addSprd;						
						double baseSpotCont = irDiscToCont(baseSpot);						
						if(swSce.getValue().getApplBizDv().equals("SAAS")) {
//							log.info("{}, {}, {}, {}, {}, {}, {}", spot.getMatCd(), spot.getSpotRate(), pvtVal, addSprd, intMult, baseSpot, baseSpotCont);
						}		
						
//						double shkCont      = irSprdShkMap.getOrDefault(spot.getMatCd(), 0.0);
						double shkCont      = applBizDv.equals("KICS") ? irSprdShkMap.getOrDefault(spot.getMatCd(), 0.0) : 0.0;
						double lpDisc       = irSprdLpMap.getOrDefault(spot.getMatCd(), 0.0);
						
						double spotCont     = baseSpotCont + shkCont;
						double spotDisc     = irContToDisc(spotCont);						
						double adjSpotDisc  = spotDisc + lpDisc;
						double adjSpotCont  = irDiscToCont(adjSpotDisc);						
						
						dcntRateBu.setBaseYymm(bssd);
						dcntRateBu.setApplBizDv(applBizDv);
						dcntRateBu.setIrCurveId(curveSwMap.getKey());
						dcntRateBu.setIrCurveSceNo(swSce.getKey());
						dcntRateBu.setMatCd(spot.getMatCd());						
						dcntRateBu.setSpotRateDisc(spotDisc);
						dcntRateBu.setSpotRateCont(spotCont);
						dcntRateBu.setLiqPrem(lpDisc);
						dcntRateBu.setAdjSpotRateDisc(adjSpotDisc);
						dcntRateBu.setAdjSpotRateCont(adjSpotCont);
						dcntRateBu.setAddSprd(addSprd);
						dcntRateBu.setLastModifiedBy(jobId);						
						dcntRateBu.setLastUpdateDate(LocalDateTime.now());						
						
						rst.add(dcntRateBu);
					}					
				}
			}
		}		
		log.info("{}({}) creates [{}] results of [{}]. They are inserted into [{}] Table", jobId, EJob.valueOf(jobId).getJobName(), rst.size(), applBizDv, toPhysicalName(IrDcntRateBu.class.getSimpleName()));
		
		return rst;		
	}		

}

