package com.gof.process;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.CorrelatedRandomVectorGenerator;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.NormalizedRandomGenerator;

import com.gof.dao._BizStockParamDao;
import com.gof.dao._EsgRandomDao;
import com.gof.dao.IrCurveSpotDao;
import com.gof.dao._StdAssetDao;
import com.gof.entity.IrParamHwBiz;
import com.gof.entity.IrDcntRateBiz;
import com.gof.entity._BizStockParam;
import com.gof.entity.StdAsstIrSceSto;
import com.gof.entity.IrParamModel;
import com.gof.entity.IrParamHwRnd;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveSpot;
import com.gof.entity._IrSce;
import com.gof.entity.MvCorr;
import com.gof.entity.StdAsst;
import com.gof.enums.EIrModelType;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job30_EsgStockScenario {
	
	public static NormalizedRandomGenerator createRandom(String bssd) {
		NormalizedRandomGenerator generator  = new GaussianRandomGenerator(new MersenneTwister(Long.valueOf(bssd)));
		return generator;
	}
	
	public static CorrelatedRandomVectorGenerator createRandom(String bssd, Map<String, Map<String, Double>> corrMap) {
		int rowIdx =0;
		int colIdx =0;

		List<String> entryList = new ArrayList<String>();
		entryList.addAll(corrMap.keySet());
		
		RealMatrix cov = new Array2DRowRealMatrix(entryList.size(), entryList.size());
		
		for(String key1  : entryList) {
			for(String key2  : entryList) {
				cov.setEntry(rowIdx, colIdx, corrMap.get(key1).get(key2));
//				log.info("aaaa : {},{}, {},{},{}", rowIdx, colIdx, key1, key2, corrMap.get(key1).get(key2));
				colIdx ++;
			}
			colIdx =0;
			rowIdx ++; 
		}
		
		NormalizedRandomGenerator generator  = new GaussianRandomGenerator(new MersenneTwister(Long.valueOf(bssd)));
		CorrelatedRandomVectorGenerator gen = new CorrelatedRandomVectorGenerator(cov, 0.0001, generator);

		return gen;
	}
	
	public static Map<Integer, double[]> createDrift(String bssd, List<String> entryList,  Map<String, IrCurve> rfCurveMap, int projectionYear){
		Map<Integer, double[]> rst = new HashMap<Integer, double[]>();
		
		Map<String, String> assetCurCd = _StdAssetDao.getStdAssetMst().stream().collect(toMap(StdAsst::getStdAsstCd, StdAsst::getCurCd));
//		assetCurCd.entrySet().forEach(s-> log.info("cur : {},{}", s.getKey(), s.getValue()));
		
		Map<String, Map<Integer, Double>> bizIrHisMap = new HashMap<String, Map<Integer, Double>>();		//�넻�솕蹂� 留뚭린蹂� 湲덈━

		for(Map.Entry<String, IrCurve> entry : rfCurveMap.entrySet()) {
			Map<Integer, Double> tempMap = IrCurveSpotDao.getBizIrCurveHis(bssd, "A", entry.getValue().getIrCurveId()).stream()
															.collect(toMap(IrDcntRateBiz::getMatNum, IrDcntRateBiz::getDf));
			
//			log.info("irCurve : {},{}", entry.getKey(), tempMap.size());
			Map<Integer, Double> driftMap = new HashMap<Integer,Double>();		//drift 瑜� �쐞�븳 湲덈━
			
			double logReturn=0.0;
			for(int i= 0; i<tempMap.size(); i++) {
				if(i==0) {
					logReturn = Math.log( 1.0 / tempMap.get(i+1));
				}
				else {
					logReturn= Math.log( tempMap.get(i)/ tempMap.get(i+1) );
					
				}
				driftMap.put(i+1, logReturn);
//				log.info("aaaaa : {}", logReturn);
			}
//			driftMap.entrySet().forEach(s-> log.info("curvezzzz : {},{}", s.getKey(), s.getValue()));
			
			bizIrHisMap.put(entry.getKey(), driftMap);
		}
//		bizIrHisMap.entrySet().forEach(s-> log.info("curve : {},{}", s.getKey(), s.getValue()));
		
		for(int matNum= 1; matNum <= projectionYear * 12 ; matNum++) {
			int idx =0;
			double[] drift = new double[entryList.size()];
			for(String aa : entryList) {
				String curCd = assetCurCd.get(aa);
//				log.info("entry : {},{},{}", aa, curCd, matNum);
				drift[idx] = bizIrHisMap.get(curCd).get(matNum);
				idx++;
			}
			rst.put(matNum, drift);
		}	
		return rst; 
	}
	
	
	public static Map<Integer, Map<String, Double>> createDrift1(String bssd, List<String> entryList,  Map<String, IrCurve> rfCurveMap, int projectionYear){
		Map<Integer, Map<String, Double>> rst = new HashMap<Integer, Map<String, Double>>();
		
		Map<String, String> assetCurCd = _StdAssetDao.getStdAssetMst().stream().collect(toMap(StdAsst::getStdAsstCd, StdAsst::getCurCd));
//		assetCurCd.entrySet().forEach(s-> log.info("cur : {},{}", s.getKey(), s.getValue()));
		
		Map<String, Map<Integer, Double>> bizIrHisMap = new HashMap<String, Map<Integer, Double>>();		//�넻�솕蹂� 留뚭린蹂� 湲덈━

		for(Map.Entry<String, IrCurve> entry : rfCurveMap.entrySet()) {
			Map<Integer, Double> tempMap = IrCurveSpotDao.getBizIrCurveHis(bssd, "A", entry.getValue().getIrCurveId()).stream()
															.collect(toMap(IrDcntRateBiz::getMatNum, IrDcntRateBiz::getDf));
			
//			log.info("irCurve : {},{}", entry.getKey(), tempMap.size());
			Map<Integer, Double> driftMap = new HashMap<Integer,Double>();		//drift 瑜� �쐞�븳 留뚭린蹂� 湲덈━
			
			double logReturn=0.0;
			for(int i= 0; i<tempMap.size(); i++) {
				if(i==0) {
					logReturn = Math.log( 1.0 / tempMap.get(i+1));
				}
				else {
					logReturn= Math.log( tempMap.get(i)/ tempMap.get(i+1) );
					
				}
				driftMap.put(i+1, logReturn);
//				log.info("aaaaa : {}", logReturn);
			}
//			driftMap.entrySet().forEach(s-> log.info("curvezzzz : {},{}", s.getKey(), s.getValue()));
			
			bizIrHisMap.put(entry.getKey(), driftMap);
		}
//		bizIrHisMap.entrySet().forEach(s-> log.info("curve : {},{}", s.getKey(), s.getValue()));
		
		for(int matNum= 1; matNum <= projectionYear * 12 ; matNum++) {
			Map<String, Double> assetDriftMap = new HashMap<String,Double>();		//drift 瑜� �쐞�븳 醫낅ぉ蹂� �닔�씡瑜�
			for(String aa : entryList) {
				String curCd = assetCurCd.get(aa);
				assetDriftMap.put(aa, bizIrHisMap.get(curCd).get(matNum));
//				log.info("entry : {},{},{}", aa, curCd, matNum);
			}
			rst.put(matNum, assetDriftMap);
		}	
		return rst; 
	}
	
	public static Map<String, Map<Integer, Double>> createDrift2(String bssd, List<String> entryList,  Map<String, IrCurve> rfCurveMap, int projectionYear){
		Map<String, Map<Integer, Double>> rst = new HashMap<String, Map<Integer, Double>>();
		
		Map<String, String> assetCurCd = _StdAssetDao.getStdAssetMst().stream().collect(toMap(StdAsst::getStdAsstCd, StdAsst::getCurCd));
//		assetCurCd.entrySet().forEach(s-> log.info("cur : {},{}", s.getKey(), s.getValue()));
		
		for(String aa : entryList) {
			String curCd = assetCurCd.get(aa);
			
			Map<Integer, Double> tempMap = IrCurveSpotDao.getBizIrCurveHis(bssd, "A", rfCurveMap.get(curCd).getIrCurveId()).stream()
											.collect(toMap(IrDcntRateBiz::getMatNum, IrDcntRateBiz::getDf));
			
			Map<Integer, Double> assetDriftMap = new HashMap<Integer,Double>();		//drift 瑜� �쐞�븳 留뚭린蹂�  �닔�씡瑜�
			
			double logReturn=0.0;
			double discYield=0.0;
			for(int matNum= 1; matNum <= projectionYear * 12 ; matNum++) {
				if(tempMap.containsKey(matNum)) {
					if(matNum==1) {
						logReturn = Math.log( 1.0 / tempMap.get(matNum));
						discYield = Math.pow(tempMap.get(matNum), -12) -1;
					}
					else {
						logReturn= Math.log( tempMap.get(matNum-1)/ tempMap.get(matNum) );
						
						discYield = Math.pow(tempMap.get(matNum-1) / tempMap.get(matNum), 12) -1;
					}
				}
//				assetDriftMap.put(matNum, logReturn);
				assetDriftMap.put(matNum, discYield);
			}
			
			rst.put(aa, assetDriftMap);
		}
		return rst; 
	}
	
//	Forward Rate �쓣 drift 濡� �쟻�슜�븿.
	public static Map<String, Map<Integer, Double>> createDrift3(String bssd, String bizDiv, List<String> entryList, Map<String, IrCurve> rfCurveMap, int projectionYear){
		Map<String, Map<Integer, Double>> rst = new HashMap<String, Map<Integer, Double>>();
		
		Map<String, String> assetCurCd = _StdAssetDao.getStdAssetMst().stream().collect(toMap(StdAsst::getStdAsstCd, StdAsst::getCurCd));
		assetCurCd.entrySet().forEach(s-> log.info("cur : {},{}", s.getKey(), s.getValue()));
		
		for(String aa : entryList) {
			String curCd = assetCurCd.get(aa);
			Map<Integer, Double> tempMap = IrCurveSpotDao.getBizIrCurveHis(bssd, bizDiv, rfCurveMap.get(curCd).getIrCurveId()).stream()
											.collect(toMap(IrDcntRateBiz::getMatNum, IrDcntRateBiz::getContForwardRate));   //TODO :: Continuous ?? Discrete...
			log.info("qqqqqqq : {},{}", aa, tempMap.size());
			rst.put(aa, tempMap);
		}
		return rst; 
	}
	
	
	public static Map<Integer, double[]> createSigma(String bssd, String bizDv, List<String> entryList,  int projectionYear){
		Map<Integer, double[]> rst = new HashMap<Integer, double[]>();
		
		Map<String, List<_BizStockParam>> bizStockParamMap = _BizStockParamDao.getBizStockParam(bssd, bizDv, "SIGMA").stream()
															.collect(groupingBy(_BizStockParam::getStdAsstCd, toList()));				// 援ш컙蹂� sigma 
		
		for(int matNum= 1; matNum <= projectionYear * 12 ; matNum++) {
			int idx =0;
			double[] sigma = new double[entryList.size()];
			for(String aa : entryList) {
				long dayNum = Math.round(matNum*365/12);
//				log.info("entry : {},{},{}", aa, curCd, matNum);
				sigma[idx] = interplationVol(bizStockParamMap.get(aa), dayNum);
				idx++;
			}
			rst.put(matNum, sigma);
		}	
		return rst; 
	}
	
	
	public static Map<Integer,  Map<String, Double>> createSigma1(String bssd, String bizDv, List<String> entryList,  int projectionYear){
		Map<Integer, Map<String, Double>> rst = new HashMap<Integer,  Map<String, Double>>();
		
		Map<String, List<_BizStockParam>> bizStockParamMap = _BizStockParamDao.getBizStockParam(bssd, bizDv, "SIGMA").stream()
															.collect(groupingBy(_BizStockParam::getStdAsstCd, toList()));				// 援ш컙蹂� sigma 
		
		for(int matNum= 1; matNum <= projectionYear * 12 ; matNum++) {
			Map<String, Double> assetSigmaMap = new HashMap<String,Double>();		//sigma 瑜� �쐞�븳 醫낅ぉ蹂� 蹂��룞�꽦
		
			for(String aa : entryList) {
				long dayNum = Math.round(matNum*365/12);
				
				assetSigmaMap.put(aa, interplationVol(bizStockParamMap.get(aa), dayNum));
//				log.info("entry : {},{},{}", aa, curCd, matNum);
			}	
			rst.put(matNum, assetSigmaMap);
		}	
		return rst; 
	}
	
	public static Map<String,  Map<Integer, Double>> createSigma2(String bssd, String bizDv, List<String> entryList,  int projectionYear){
		Map<String, Map<Integer, Double>> rst = new HashMap<String,  Map<Integer, Double>>();
		
		List<_BizStockParam> bizStockParam = _BizStockParamDao.getBizStockParam(bssd, bizDv, "SIGMA");
		
		long currDayNum =0; 
		long prevDayNum =0;
		for(String aa : entryList) {
			Map<Integer, Double> assetSigmaMap = new HashMap<Integer,Double>();							//sigma 瑜� �쐞�븳 留뚭린蹂� 蹂��룞�꽦
			
			List<_BizStockParam> filterdBizStockParam =bizStockParam.stream().filter(s->s.getStdAsstCd().equals(aa)).collect(toList());
			for(int matNum= 1; matNum <= projectionYear * 12 ; matNum++) {
				currDayNum = Math.round(matNum*365/12);
				
//				assetSigmaMap.put(matNum, interplationVol(filterdBizStockParam, currDayNum));
				assetSigmaMap.put(matNum, calcForwardVol(filterdBizStockParam, prevDayNum,  currDayNum));
				
//				log.info("entry : {},{},{},{}", aa, matNum, interplationVol(filterdBizStockParam, currDayNum), calcForwardVol(filterdBizStockParam, prevDayNum,  currDayNum));
				
				prevDayNum = currDayNum;
			}	
			rst.put(aa, assetSigmaMap);
		}	
		return rst; 
	}
	
	public static List<StdAsstIrSceSto> createEsgScenario(String bssd, String bizDv, List<String> assetList, Map<String, Double> asstHisMap
						, Map<Integer, double[]> driftMap, Map<Integer, double[]> sigmaMap, CorrelatedRandomVectorGenerator gen, int sceNo, int projectionYear) {
		
			List<StdAsstIrSceSto> sceList = new ArrayList<StdAsstIrSceSto>();
			double prevStockSce=0.0;
			double currStockSce=0.0;
			double yieldSce =0.0;
			int k=0;
			double dt = 1/12.0;
			for(String asset : assetList) {
				prevStockSce = asstHisMap.get(asset);
				k=0;
				for(int j=1 ; j<= projectionYear * 12; j++) {
					double[] sce   = gen.nextVector();
					double[] drift = driftMap.get(j);
					double[] sigma = sigmaMap.get(j);
				
					currStockSce = prevStockSce * Math.exp( ( drift[k] -  0.5* sigma[k] * sigma[k] ) * dt   + sigma[k] * Math.sqrt(dt) * sce[k]);
//					currStockSce = prevStockSce * Math.exp( drift[k] * dt   + sigma[k] * Math.sqrt(dt) * sce[k]);
					
					yieldSce = currStockSce / prevStockSce - 1.0 ;
//					yieldSce = Math.pow(currStockSce / prevStockSce, 1/ dt)  - 1.0 ;
					
					prevStockSce = currStockSce;
					
					sceList.add( StdAsstIrSceSto.builder()
											.baseYymm(bssd)
											.applBizDv(bizDv)
											.sceNo(String.valueOf(sceNo))
											.matCd( "M" + String.format("%04d", j))
											.stdAsstCd(asset)
											.asstYield(yieldSce)
											.lastModifiedBy("ESG")
											.lastUpdateDate(LocalDateTime.now())
											.build()
								);
				}
				k++;	

			}

			return sceList;
	}
	
	
	public static List<StdAsstIrSceSto> createEsgScenario1(String bssd, String bizDv, List<String> assetList, Map<String, Double> asstHisMap
			, Map<Integer, Map<String, Double>> driftMap, Map<Integer, Map<String, Double>> sigmaMap, CorrelatedRandomVectorGenerator gen, int sceNo, int projectionYear) {

		List<StdAsstIrSceSto> sceList = new ArrayList<StdAsstIrSceSto>();
		double prevStockSce=0.0;
		double currStockSce=0.0;
		double yieldSce =0.0;
		int k=0;
		double dt = 1/12.0;
		for(int j=1 ; j<= projectionYear * 12; j++) {
			double[] sce   = gen.nextVector();

			k=0;
			for(String asset : assetList) {
				prevStockSce = asstHisMap.get(asset);

				double drift = driftMap.get(j).get(asset);
				double sigma = sigmaMap.get(j).get(asset);
			
				currStockSce = prevStockSce * Math.exp( ( drift -  0.5* sigma * sigma ) * dt   + sigma * Math.sqrt(dt) * sce[k]);
		//		currStockSce = prevStockSce * Math.exp( drift * dt   + sigma * Math.sqrt(dt) * sce[k]);
				
//				yieldSce = currStockSce / prevStockSce - 1.0 ;
//				yieldSce = Math.pow(currStockSce / prevStockSce, 1/ dt)  - 1.0 ;
				
				yieldSce =  (drift -  0.5* sigma * sigma ) * dt   + sigma * Math.sqrt(dt) * sce[k];
				prevStockSce = currStockSce;
				
				sceList.add( StdAsstIrSceSto.builder()
										.baseYymm(bssd)
										.applBizDv(bizDv)
										.sceNo(String.valueOf(sceNo))
										.matCd( "M" + String.format("%04d", j))
										.stdAsstCd(asset)
										.asstYield(yieldSce)
										.lastModifiedBy("ESG")
										.lastUpdateDate(LocalDateTime.now())
										.build()
							);
				k++;	
			}
		}
		
		return sceList;
	}
	
	public static List<StdAsstIrSceSto> createEsgScenario2(String bssd, String bizDv, List<String> assetList, Map<String, Double> asstHisMap
			, Map<String, Map<Integer, Double>> driftMap, Map<String, Map<Integer, Double>> sigmaMap, CorrelatedRandomVectorGenerator gen, int sceNo, int projectionYear) {

		List<StdAsstIrSceSto> sceList = new ArrayList<StdAsstIrSceSto>();
		double prevStockSce=0.0;
		double currStockSce=0.0;
		double yieldSce =0.0;
		int k=0;
		double dt = 1/12.0;
		
		for(int j=1 ; j<= projectionYear * 12; j++) {
			double[] sce   = gen.nextVector();

			k=0;
			for(String asset : assetList) {
				prevStockSce = asstHisMap.get(asset);

				double drift = driftMap.get(asset).get(j);
				double sigma = sigmaMap.get(asset).get(j);
			
				currStockSce = prevStockSce * Math.exp( ( drift -  0.5* sigma * sigma ) * dt   + sigma * Math.sqrt(dt) * sce[k]);
		//		currStockSce = prevStockSce * Math.exp( drift * dt   + sigma * Math.sqrt(dt) * sce[k]);
				
//				yieldSce = currStockSce / prevStockSce - 1.0 ;
//				yieldSce = Math.pow(currStockSce / prevStockSce, 1/ dt)  - 1.0 ;
				
//				yieldSce =  ( drift -  0.5* sigma * sigma ) * dt   + sigma * Math.sqrt(dt) * sce[k];
				yieldSce =  ( drift ) * dt   + sigma * Math.sqrt(dt) * sce[k];
				prevStockSce = currStockSce;
				
				sceList.add( StdAsstIrSceSto.builder()
										.baseYymm(bssd)
										.applBizDv(bizDv)
										.sceNo(String.valueOf(sceNo))
										.matCd( "M" + String.format("%04d", j))
										.stdAsstCd(asset)
//										.asstYield(yieldSce)
										.asstYield(Math.exp(yieldSce)-1)
										.lastModifiedBy("ESG")
										.lastUpdateDate(LocalDateTime.now())
										.build()
							);
				k++;	
			}
		}
		
//		log.info("zzzzz : {}", sceList.size());
		return sceList;
	}
	
	
	

	private static double interplationVol(List<_BizStockParam> paramList, long dayNum) {
		double weightVar=0.0;
		long leftDayNum =0;
		long effectiveDayNum =0;
		paramList.sort((s1,s2)-> s1.getMatDayNum()- s2.getMatDayNum());
		
		if(dayNum ==0) {
			return 0.0;
		}
		
		if(paramList.size()==1) {
			effectiveDayNum =dayNum;
			weightVar =  Math.pow(paramList.get(0).getApplParamVal(), 2) * effectiveDayNum;
		}
		else  {
			for(_BizStockParam aa : paramList) {
				if(aa.getMatDayNum() <= dayNum) {
					effectiveDayNum = aa.getMatDayNum();
					weightVar = weightVar + (effectiveDayNum -leftDayNum )  * Math.pow(aa.getApplParamVal(), 2);
//					log.info("interpolVol : {},{},{},{},{}", dayNum, effectiveDayNum-leftDayNum , effectiveDayNum, aa.getApplParamVal(), weightVar);
					leftDayNum = aa.getMatDayNum();
				}
				else {
					effectiveDayNum = dayNum;
					weightVar = weightVar + (effectiveDayNum - leftDayNum ) * Math.pow(aa.getApplParamVal(), 2) ;
//					log.info("interpolVol1 : {},{},{},{},{}", dayNum, effectiveDayNum - leftDayNum, effectiveDayNum, aa.getApplParamVal(),weightVar);
					break;
				}
			}
		}
		return Math.sqrt(weightVar / effectiveDayNum ) ;
	}
	
	private static double calcForwardVol(List<_BizStockParam> paramList, long prevDayNum, long currDayNum) {
	
		paramList.sort((s1,s2)-> s1.getMatDayNum()- s2.getMatDayNum());
		long dt = currDayNum - prevDayNum;

		double farVol  = interplationVol(paramList, currDayNum);
		double nearVol = interplationVol(paramList, prevDayNum);
		double weightVar = currDayNum * farVol*farVol  - prevDayNum * nearVol*nearVol;
				
		return Math.sqrt( weightVar / dt ) ;
	}
	
}

