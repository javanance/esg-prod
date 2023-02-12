package com.gof.process;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.IrCurveSpotDao;
import com.gof.dao._LiqPremiumDao;
import com.gof.entity.IrSprdLpBiz;
import com.gof.entity.IrSprdLpUsr;
import com.gof.entity.IrDcntRateBu;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveSpot;
import com.gof.entity.IrSprdLp;
import com.gof.util.EsgConstant;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> IFRS 17 의 BottomUp 방법론에 의한 할인율 산출 모형의 실행         
 *  <p> 시장에서 관측되는 무위험 금리를 기반으로 보험 부채의 비유동성 측면을 반영하여 보험부채에 적용할 할인율 산출함.
 *  <p>    1. 기산출된 무위험 금리 및 유동성 프리미엄 추출   
 *  <p>    2. 기준월의 무위험 시장금리 + 유동성 스프레를 적용하여 기간구조 생성
 *  <p>    3. Smith-Wilson 방법론( {@link SmithWilsonModel} 으로 보간/ 보외를 적용하여 전체 구간의 할인율 산출함.
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class _Job17_LiqAdjBottomUp {

//	private static Map<String, Double> lpMap = new HashMap<String, Double>();
	
	public static Stream<IrDcntRateBu> createBottomUpAddLiqPremium(String bssd, IrCurve curveMst) {
		List<IrDcntRateBu> rst = new ArrayList<IrDcntRateBu>();

//		Map<String, Double> lpMap = getAppliedLiqMap(bssd, curveMst, modelId);
		Map<String, Double> lpMap = _LiqPremiumDao.getBizLiqPremium(bssd, curveMst.getApplBizDv()).stream()
											.collect(toMap(IrSprdLpBiz::getMatCd, IrSprdLpBiz::getLiqPrem));
		
		
		List<IrCurveSpot> curveHisList = IrCurveSpotDao.getIrCurveSpot(bssd, curveMst.getRefCurveId()).stream()
													.filter(s->EsgConstant.getTenorList().contains(s.getMatCd()))
													.collect(toList())
													;
		log.info("{}", curveHisList);
		log.info("aaa : {},{}", lpMap.size(), curveHisList.size());
		//무위험 금리가 존재하지 않으면 Error 임  
		if(curveHisList.isEmpty()) {
			log.error("Risk Adjust Int Rate Error :  Historical Data of {} is not found at {} ", curveMst.getRefCurveId(), bssd );
			System.exit(0);
		}
		
		return curveHisList.stream().map(s-> addLiqPremium(bssd, curveMst.getIrCurveId(), s, lpMap));
	}
	
	private static IrDcntRateBu addLiqPremium(String bssd, String curveId, IrCurveSpot curveHis, Map<String, Double> lpMap) {
		double liqPremium = lpMap.getOrDefault(curveHis.getMatCd(), 0.0	);
		log.info("aaa aaaa: {},{},{}", curveId, curveHis, liqPremium);
		IrDcntRateBu temp;
		
		temp = new IrDcntRateBu();
		temp.setBaseYymm(bssd);
		temp.setIrCurveId(curveId);
//		temp.setSceNo("0");
		temp.setMatCd(curveHis.getMatCd());
		temp.setSpotRateDisc(curveHis.getSpotRate());
		temp.setLiqPrem(liqPremium);
		temp.setAdjSpotRateDisc(curveHis.getSpotRate() + liqPremium);
		temp.setAdjSpotRateCont(0.0);
		
		temp.setLastModifiedBy("ESG");
		temp.setLastUpdateDate(LocalDateTime.now());
		
		return temp;
	}

//	private static Map<String, Double> getAppliedLiqMap(String bssd, IrCurve curveMst, String modelId) {
//		if(lpMap.isEmpty()) {
//			List<LiqPremium> liqPremList    = LiqPremiumDao.getLiqPremium(bssd, modelId);
//			List<BizLiqPremiumUd> lpUserRst = LiqPremiumDao.getLiqPremiumUd(bssd);
//			
//			if(lpUserRst.isEmpty()) {
//				lpMap = liqPremList.stream().collect(toMap(LiqPremium::getMatCd, LiqPremium::getLiqPrem));
//			}
//			else{
//				lpMap =lpUserRst.stream().collect(toMap(BizLiqPremiumUd::getMatCd, BizLiqPremiumUd::getApplyLiqPrem));
//			}
//			
//			List<String> tenorList = EsgConstant.getTenorList();
//			Collections.reverse(tenorList);
//			
//			double prevLiq =0.0;
//			
//			for(String aa : tenorList) {
//				if(lpMap.containsKey(aa)) {
//					prevLiq = lpMap.get(aa);
//				}
//				else {
//					lpMap.put(aa, prevLiq);
//				}
//			}
//		}
//		return lpMap;
//	}
	
	
//	private static Map<String, Double> getBizLiqPrem(String bssd, String bizDv) {
//		if(lpMap.isEmpty()) {
//			lpMap = LiqPremiumDao.getBizLiqPremium(bssd, bizDv).stream().collect(toMap(BizLiqPremium::getMatCd, BizLiqPremium::getApplyLiqPrem));
//		}
//		return lpMap;
//	}
	
	
	
}
