package com.gof.process;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gof.dao.IrCurveSpotDao;
import com.gof.entity.IrCurveSpot;
import com.gof.entity.IrParamSw;
import com.gof.entity.IrSprdCurve;
import com.gof.entity.IrSprdLp;
import com.gof.enums.EJob;
import com.gof.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Esg230_LpSprd extends Process {	
	
	public static final Esg230_LpSprd INSTANCE = new Esg230_LpSprd();
	public static final String jobId = INSTANCE.getClass().getSimpleName().toUpperCase().substring(0, ENTITY_LENGTH);
	
	public static List<IrSprdLp> setLpFromSwMap(String bssd, String applBizDv, Map<String, Map<Integer, IrParamSw>> paramSwMap) {
		
		List<IrSprdLp> rst = new ArrayList<IrSprdLp>();
		
		for(Map.Entry<String, Map<Integer, IrParamSw>> curveSwMap : paramSwMap.entrySet()) {
			
			List<String> tenorList = IrCurveSpotDao.getIrCurveTenorList(bssd, curveSwMap.getKey());
			if(tenorList.isEmpty()) {
				log.error("No IR Curve Data [IR_CURVE_ID: {}] in [{}] for [{}]", curveSwMap.getKey(), toPhysicalName(IrCurveSpot.class.getSimpleName()), bssd);
				continue;
			}
//			log.info("irCurveId: {}, {}", curveSwMap.getKey(), IrCurveSpotDao.getIrCurveTenorList(bssd, curveSwMap.getKey()));
			
			for(Map.Entry<Integer, IrParamSw> swSce : curveSwMap.getValue().entrySet()) {
				
//				log.info("paramSw: {}, {}, {}", curveSwMap.getKey(), swSce.getKey(), swSce.getValue());				
//				if(curveSwMap.getKey().equals("1010000")) break;
				
				int llp = StringUtil.objectToPrimitive(swSce.getValue().getLlp(), 20);
				for(String tenor : tenorList) {					
//					log.info("tenor: {}, {}, {}", tenor, tenor.substring(1), swSce.getValue().getLlp());					

					if(Integer.valueOf(tenor.substring(1)) <=  llp * MONTH_IN_YEAR) {						
						
						IrSprdLp lp1 = new IrSprdLp();
						
						lp1.setBaseYymm(bssd);
						lp1.setDcntApplModelCd("BU1");
						lp1.setApplBizDv(applBizDv);
						lp1.setIrCurveId(curveSwMap.getKey());
						lp1.setIrCurveSceNo(swSce.getKey());
						lp1.setMatCd(tenor);
						lp1.setLiqPrem(swSce.getValue().getLiqPrem());
						lp1.setLastModifiedBy(jobId);						
						lp1.setLastUpdateDate(LocalDateTime.now());
						
						rst.add(lp1);
					}					
				}
			}
		}
		log.info("{}({}) creates [{}] results of [{}] (from SW Param). They are inserted into [{}] Table", jobId, EJob.valueOf(jobId).getJobName(), rst.size(), applBizDv, toPhysicalName(IrSprdLp.class.getSimpleName()));
		
		return rst;
	}
	
	
	public static List<IrSprdLp> setLpFromCrdSprd(String bssd, String applBizDv, Map<String, Map<Integer, IrParamSw>> paramSwMap, String lpCurveId) {
		
		List<IrSprdLp> rst = new ArrayList<IrSprdLp>();
		
		for(Map.Entry<String, Map<Integer, IrParamSw>> curveSwMap : paramSwMap.entrySet()) {			
			
			List<String> tenorList = IrCurveSpotDao.getIrCurveTenorList(bssd, curveSwMap.getKey());
			if(tenorList.isEmpty()) {
				log.error("No IR Curve Data [IR_CURVE_ID: {}] in [{}] for [{}]", curveSwMap.getKey(), toPhysicalName(IrCurveSpot.class.getSimpleName()), bssd);
				continue;
			}
			
			for(Map.Entry<Integer, IrParamSw> swSce : curveSwMap.getValue().entrySet()) {

				int llp = StringUtil.objectToPrimitive(swSce.getValue().getLlp(), 20);				
				for(IrSprdCurve lpCrv : IrCurveSpotDao.getIrSprdCurve(bssd, lpCurveId)) {
					if(Integer.valueOf(lpCrv.getMatCd().substring(1)) <= llp * MONTH_IN_YEAR) {
						
						IrSprdLp lp2 = new IrSprdLp();
						
						lp2.setBaseYymm(bssd);
						lp2.setDcntApplModelCd("BU2");
						lp2.setApplBizDv(applBizDv);
						lp2.setIrCurveId(curveSwMap.getKey());
						lp2.setIrCurveSceNo(swSce.getKey());
						lp2.setMatCd(lpCrv.getMatCd());
						lp2.setLiqPrem(lpCrv.getCrdSprd());
						lp2.setLastModifiedBy(jobId);						
						lp2.setLastUpdateDate(LocalDateTime.now());
						
						rst.add(lp2);
					}					
				}
			}
		}
		log.info("{}({}) creates [{}] results of [{}] (from Credit Spread). They are inserted into [{}] Table", jobId, EJob.valueOf(jobId).getJobName(), rst.size(), applBizDv, toPhysicalName(IrSprdLp.class.getSimpleName()));
		
		return rst;
	}		

}

