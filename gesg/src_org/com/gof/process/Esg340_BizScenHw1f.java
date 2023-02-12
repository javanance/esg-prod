package com.gof.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.gof.dao.IrDcntRateDao;
import com.gof.dao.IrParamHwDao;
import com.gof.entity.IrCurveSpot;
import com.gof.entity.IrDcntRateBu;
import com.gof.entity.IrDcntSceStoBiz;
import com.gof.entity.IrParamHwBiz;
import com.gof.entity.IrParamHwRnd;
import com.gof.entity.IrParamModel;
import com.gof.entity.IrParamSw;
import com.gof.enums.EJob;
import com.gof.model.Hw1fSimulationKics;
import com.gof.model.entity.Hw1fCalibParas;
import com.gof.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Esg340_BizScenHw1f extends Process {
	
	public static final Esg340_BizScenHw1f INSTANCE = new Esg340_BizScenHw1f();
	public static final String jobId = INSTANCE.getClass().getSimpleName().toUpperCase().substring(0, ENTITY_LENGTH);

	public static Map<String, List<?>> createScenHw1f(String bssd, String applBizDv, String irModelId, String irCurveId, Integer irCurveSceNo, Map<String, Map<Integer, IrParamSw>> paramSwMap, Map<String, IrParamModel> modelMstMap, Integer projectionYear) {
		
		Map<String, List<?>>  rst     = new TreeMap<String, List<?>>();
		List<IrDcntSceStoBiz> sceRst  = new ArrayList<IrDcntSceStoBiz>();
		List<IrParamHwRnd>    randRst = new ArrayList<IrParamHwRnd>();
		
		for(Map.Entry<String, Map<Integer, IrParamSw>> curveSwMap : paramSwMap.entrySet()) {
			for(Map.Entry<Integer, IrParamSw> swSce : curveSwMap.getValue().entrySet()) {
//				
				if(!StringUtil.objectToPrimitive(swSce.getValue().getStoSceGenYn(), "N").toUpperCase().equals("Y")) continue;
				
				if(!curveSwMap.getKey().equals(irCurveId) || !swSce.getKey().equals(irCurveSceNo)) continue;				
//				log.info("IR_CURVE_ID: [{}], IR_CURVE_SCE_NO: [{}]", curveSwMap.getKey(), swSce.getKey());
				
				if(!modelMstMap.containsKey(curveSwMap.getKey())) {
					log.error("No Model Attribute of [{}] for [{}] in [{}] Table", irModelId, curveSwMap.getKey(), Process.toPhysicalName(IrParamModel.class.getSimpleName()));
					continue;
				}
				
				List<IrCurveSpot> adjSpotRate = IrDcntRateDao.getIrDcntRateBuToAdjSpotList(bssd, applBizDv, curveSwMap.getKey(), swSce.getKey());				
//				List<IrCurveSpot> adjSpotRate = IrDcntRateDao.getIrDcntRateBuToBaseSpotList(bssd, applBizDv, curveSwMap.getKey(), swSce.getKey());
				if(adjSpotRate.isEmpty()) {
					log.error("No Spot Rate Data [ID: {}, SCE_NO: {}] for [{}] in [{}] Table", curveSwMap.getKey(), swSce.getKey(), bssd, Process.toPhysicalName(IrDcntRateBu.class.getSimpleName()));
					continue;
				}				
									
				List<IrParamHwBiz> paramHw = IrParamHwDao.getIrParamHwBizList(bssd, applBizDv, irModelId, curveSwMap.getKey());					
				if(paramHw.isEmpty()) {
					log.error("No HW1F Model Parameter exist in [MODEL: {}] [IR_CURVE_ID: {}] in [{}] Table", irModelId, curveSwMap.getKey(), Process.toPhysicalName(IrParamHwBiz.class.getSimpleName()));
					continue;
				}
				List<Hw1fCalibParas> hwParasList = Hw1fCalibParas.convertFrom(paramHw);
//				log.info("{}, {}", hwParasList);
				
				int[] alphaPiece = paramHw.stream().filter(s->s.getParamTypCd().equals("ALPHA") && s.getMatCd().equals("M0240"))
										  	       .mapToInt(s-> Integer.valueOf(s.getMatCd().split("M")[1])/12).toArray();
				int[] sigmaPiece = paramHw.stream().filter(s->s.getParamTypCd().equals("SIGMA") && !s.getMatCd().equals("M1200") && !s.getMatCd().equals("M0240"))
												   .mapToInt(s-> Integer.valueOf(s.getMatCd().split("M")[1])/12).toArray();	
	//			log.info("{}, {}", alphaPiece, sigmaPiece);				
				
				boolean priceAdj      = false;
				int     randomGenType = 1;
				int     sceNum        = Integer.parseInt(modelMstMap.get(curveSwMap.getKey()).getTotalSceNo());						
				int     seedNum       = Integer.parseInt(modelMstMap.get(curveSwMap.getKey()).getRndSeed());
				double  ltfr          = paramSwMap.get(curveSwMap.getKey()).get(swSce.getKey()).getLtfr();
				int     ltfrCp        = paramSwMap.get(curveSwMap.getKey()).get(swSce.getKey()).getLtfrCp();
//				log.info("seedNum: {}, {}", seedNum, bssd);		

				Hw1fSimulationKics hw1f = new Hw1fSimulationKics(bssd, adjSpotRate, hwParasList, alphaPiece, sigmaPiece, priceAdj, sceNum, ltfr, ltfrCp, projectionYear, randomGenType, seedNum);
				List<IrDcntSceStoBiz> stoBizList  = hw1f.getIrModelHw1fList().stream().map(s -> s.convert(applBizDv, irModelId, curveSwMap.getKey(), swSce.getKey(), jobId)).collect(Collectors.toList());
				List<IrParamHwRnd>    randNumList = new ArrayList<IrParamHwRnd>();
				
				//TODO:
				if(applBizDv.equals("1KICS") && swSce.getKey().equals(1)) {
					
					String pathDir = "C:/Users/NHfire.DESKTOP-J5J0BJV/Desktop/";
					String path0 = pathDir + "SW_FWD_"        + curveSwMap.getKey() + "_" + swSce.getKey() + ".csv";
					String path1 = pathDir + "HW_FWD_DISC_"   + curveSwMap.getKey() + "_" + swSce.getKey() + ".csv";
					String path2 = pathDir + "HW_RANDOM_"     + curveSwMap.getKey() + "_" + swSce.getKey() + ".csv";
					String path3 = pathDir + "HW_YIELD_DISC_" + curveSwMap.getKey() + "_" + swSce.getKey() + ".csv";
					
					try {
						double[][] sw = new double[hw1f.getFwdDiscBase().length][3];
						for(int i=0; i<sw.length; i++) {
							sw[i][0] = i+1;
							sw[i][1] = hw1f.getSpotDiscBase()[i];
							sw[i][2] = hw1f.getFwdDiscBase()[i];
						}			
						writeArraytoCSV(sw, path0);  //matTranspose(sw)
						writeArraytoCSV(hw1f.getFwdDiscScen(), path1);
						if(swSce.getKey().equals(1)) writeArraytoCSV(hw1f.getRandNum(), path2);
						
						hw1f.getIrModelHw1fBondYield(hw1f.getIrModelHw1fList(), 3.0);
						writeArraytoCSV(hw1f.getBondYieldDisc(), path3);
											
					} catch (Exception e) {
						e.printStackTrace();
					}					
				}
				
				if(swSce.getKey().equals(1)) {
					randNumList = hw1f.getRandomScenList().stream().map(s -> s.setKeys(irModelId, curveSwMap.getKey(), jobId)).collect(Collectors.toList());	
				}				
				sceRst.addAll(stoBizList);
				randRst.addAll(randNumList);
			}
		}
		rst.put("SCE", sceRst);
		rst.put("RND", randRst);			

		log.info("{}({}) creates [{}] results of [{}] [ID: {}, SCE: {}]. They are inserted into [{}] Table", jobId, EJob.valueOf(jobId).getJobName(), rst.get("SCE").size(), applBizDv, irCurveId, irCurveSceNo, toPhysicalName(IrDcntSceStoBiz.class.getSimpleName()));
		if(applBizDv.equals("KICS") && rst.get("RND").size() > 0) {
			log.info("{}({}) creates [{}] results of [{}] [ID: {}, SCE: {}]. They are inserted into [{}] Table", jobId, EJob.valueOf(jobId).getJobName(), rst.get("RND").size(), applBizDv, irCurveId, irCurveSceNo, toPhysicalName(IrParamHwRnd.class.getSimpleName()));	
		}		
		
		return rst;		
	}		

}