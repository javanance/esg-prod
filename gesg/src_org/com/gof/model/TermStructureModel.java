package com.gof.model;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.gof.entity.IrDcntRate;
import com.gof.entity.IrDcntRateBu;
import com.gof.entity.IrCurve;
import com.gof.interfaces.IIntRate;
import com.gof.util.EsgConstant;

public class TermStructureModel {
	
	public static List<IrDcntRateBu> createTermStructure(String bssd, String irCurveId, String sceNo, Map<String, ? extends IIntRate> curveMap,Map<String, Double> lpMap){
		List<IrDcntRateBu> rstList = new ArrayList<IrDcntRateBu>();
		IrDcntRateBu temp;
		double beforeIntFactor =1.0;
		double currentIntFactor=1.0;
		double fwdRate=0.0;
		double spotRate =0.0;
		double beforeRate =0.0;
		double beforeTimeFactor =0.0;
		double currentTimeFactor =0.0;
		String matCd ="";
		
		for(int i=0; i< curveMap.size(); i++) {
			matCd= "M" + String.format("%04d", i+1);
			if(curveMap.containsKey(matCd)){
				beforeIntFactor = Math.pow(1+beforeRate, beforeTimeFactor);
				
				currentTimeFactor = (i+1) /12.0;
				spotRate = curveMap.get(matCd).getIntRate() + lpMap.getOrDefault(matCd, 0.0);
				
				currentIntFactor = Math.pow(1+spotRate, currentTimeFactor);
				fwdRate = Math.pow(currentIntFactor/beforeIntFactor, 1.0 / (currentTimeFactor - beforeTimeFactor))-1.0;
						
				beforeTimeFactor = currentTimeFactor;
				beforeRate  = spotRate;
				
				temp = new IrDcntRateBu();
				temp.setBaseYymm(bssd);
				temp.setIrCurveId(irCurveId);
//				temp.setSceNo(sceNo);
				temp.setMatCd(curveMap.get(matCd).getMatCd());
				temp.setSpotRateDisc(curveMap.get(matCd).getIntRate());
				temp.setLiqPrem(lpMap.getOrDefault(matCd, 0.0));
				temp.setAdjSpotRateDisc(spotRate);
				temp.setAdjSpotRateCont(fwdRate);
				
				temp.setLastModifiedBy("ESG");
				temp.setLastUpdateDate(LocalDateTime.now());
				
				rstList.add(temp);
			}
		}
		
		return rstList;
	}
	
	
	public static List<IrDcntRate> createForward(String bssd, IrCurve irCurve, String sceNo, List<IrDcntRateBu> rfBottomUp, List<IrDcntRateBu> rfAdjBottomUp){
		List<IrDcntRate> rstList = new ArrayList<IrDcntRate>();
		IrDcntRate temp;
		Map<String, IrDcntRateBu> rfMap = rfBottomUp.stream().collect(toMap(IrDcntRateBu::getMatCd, Function.identity()));
		Map<String, IrDcntRateBu> rfAdjMap = rfAdjBottomUp.stream().collect(toMap(IrDcntRateBu::getMatCd, Function.identity()));
		
		double beforeIntFactor =1.0;
		double currentIntFactor=1.0;
		double rfBeforeIntFactor =1.0;
		double rfCurrentIntFactor=1.0;
		
		double spotRate =0.0;
		double beforeRate =0.0;
		double fwdRate=0.0;
		
		double rfSpotRate =0.0;
		double rfBeforeRate =0.0;
		double rfFwdRate =0.0;
		
		double beforeTimeFactor =0.0;
		double currentTimeFactor =0.0;
		String matCd ="";
		
		for(int i=0; i< rfAdjMap.size(); i++) {
			matCd= "M" + String.format("%04d", i+1);
			if(rfAdjMap.containsKey(matCd)){
				beforeIntFactor = Math.pow(1+beforeRate, beforeTimeFactor);
				rfBeforeIntFactor = Math.pow(1+rfBeforeRate, beforeTimeFactor);
				
				currentTimeFactor = (i+1) /12.0;
				spotRate = rfAdjMap.get(matCd).getAdjSpotRateDisc();
				rfSpotRate = rfMap.get(matCd).getSpotRateDisc();
				
				currentIntFactor = Math.pow(1+spotRate, currentTimeFactor);
				rfCurrentIntFactor = Math.pow(1+rfSpotRate, currentTimeFactor);
				
				fwdRate = Math.pow(currentIntFactor/beforeIntFactor, 1.0 / (currentTimeFactor - beforeTimeFactor))-1.0;
				rfFwdRate = Math.pow(rfCurrentIntFactor/rfBeforeIntFactor, 1.0 / (currentTimeFactor - beforeTimeFactor))-1.0;
						
				beforeTimeFactor = currentTimeFactor;
				beforeRate  = spotRate;
				rfBeforeRate  = rfSpotRate;
				
				
				
				temp = new IrDcntRate();
				temp.setBaseYymm(bssd);
				temp.setIrCurveId(irCurve.getIrCurveId());
				temp.setApplBizDv(irCurve.getApplBizDv());
				temp.setMatCd(rfAdjMap.get(matCd).getMatCd());
				temp.setSpotRate(rfSpotRate);
				temp.setFwdRate(rfFwdRate);
//				temp.setLiqPrem(spotRate- rfSpotRate);
//				temp.setCrdSpread(fwdRate- rfFwdRate);
				temp.setAdjSpotRate(spotRate);
				temp.setAdjFwdRate(fwdRate);
				temp.setLastModifiedBy("ESG");
				temp.setLastUpdateDate(LocalDateTime.now());
				
				rstList.add(temp);
			}
		}
		
		return rstList;
	}
	
	public static List<IrDcntRateBu> createTermStructure(String bssd, String irCurveId, String sceNo, Map<String, ? extends IIntRate> curveMap, double spread){
		Map<String, Double> lpMap = new HashMap<String, Double>();
		String llp = EsgConstant.getStrConstant().getOrDefault("llp", "M0240");
		
		for(Map.Entry<String, ? extends IIntRate> entry : curveMap.entrySet()) {
			if(entry.getKey().compareTo(llp) <= 0) {
				lpMap.put(entry.getKey(), spread);
			}
		}
		return createTermStructure(bssd, irCurveId, sceNo, curveMap, lpMap);
			
	}

	public static List<IrDcntRateBu> createTermStructure(String bssd, String irCurveId, String sceNo, List<? extends IIntRate> curveList, Map<String, Double> lpMap){
		Map<String, ? extends IIntRate> curveMap = curveList.stream().collect(toMap(s->s.getMatCd(), Function.identity()));
		return createTermStructure(bssd, irCurveId, sceNo, curveMap, lpMap);
	}
	
	public static List<IrDcntRateBu> createTermStructure(String bssd, String irCurveId, String sceNo, List<? extends IIntRate> curveList, double spread){
		Map<String, ? extends IIntRate> curveMap = curveList.stream().collect(toMap(s->s.getMatCd(), Function.identity()));
		return createTermStructure(bssd, irCurveId, sceNo, curveMap, spread);
	}
}
