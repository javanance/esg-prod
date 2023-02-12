package com.gof.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class _SmithWilsonResult {
	
	private String baseYymm;
	private String sceNo;
	private String irCurveId;
	private String matCd;
	
	private double  timeFactor;
	private int		monthNum;
	private double  spotCont;
	private double	spotAnnual;
	private double	discountFactor;
	private double	fwdCont;				//1M forward Rate 
	private double	fwdAnnual;
	private int		fwdMonthNum;			//1M forward Rate 

	public _SmithWilsonResult() {
	}

	
	@Override
	public String toString() {
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(timeFactor).append(delimeter)
			   .append(monthNum).append(delimeter)
			   .append(spotCont).append(delimeter)
			   .append(spotAnnual).append(delimeter)
			   .append(discountFactor).append(delimeter)
			   .append(fwdCont).append(delimeter)
			   .append(fwdAnnual).append(delimeter)
			   .append(fwdMonthNum).append("\n")
			   ;
		return builder.toString();
	}
	
	public IrCurveSpot convertToIrCurveHis() {
		IrCurveSpot rst = new IrCurveSpot();
		
		rst.setBaseDate(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
//		rst.setSceNo(this.sceNo);
		rst.setMatCd(this.matCd);
		rst.setSpotRate(this.spotAnnual);
		
		return rst;
	}
	
	
	public IrDcntRateBiz convertToBizIrCurveHis(String bizDv) {
		IrDcntRateBiz rst = new IrDcntRateBiz();
		
		rst.setBaseYymm(this.baseYymm);
		rst.setApplBizDv(bizDv);
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);
		rst.setSpotRate(this.spotAnnual);
		rst.setFwdRate(this.fwdAnnual);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	
	public IrDcntRateBu convertToBottomUp(Map<String, Double> lpMap) {
		IrDcntRateBu rst = new IrDcntRateBu();
		double liqPremium = lpMap.getOrDefault(this.matCd, 0.0);
		
		rst.setBaseYymm(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);

		rst.setSpotRateDisc(this.spotAnnual - liqPremium);
		rst.setLiqPrem(liqPremium);
		rst.setAdjSpotRateDisc(this.spotAnnual);
		rst.setAdjSpotRateCont(this.fwdAnnual);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public IrDcntRate convertToBizDiscountRate(Map<String, Double> lpMap) {
		IrDcntRate rst = new IrDcntRate();
		double liqPremium = lpMap.getOrDefault(this.matCd, 0.0);
		
		rst.setBaseYymm(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);

		rst.setSpotRate(this.spotAnnual - liqPremium);
//		rst.setLiqPrem(liqPremium);
		rst.setAdjSpotRate(this.spotAnnual);
		rst.setAdjFwdRate(this.fwdAnnual);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public IrDcntRateBu convertToKicsTermStructure(Map<String, Double> lpMap) {
		IrDcntRateBu rst = new IrDcntRateBu();
		double liqPremium = lpMap.getOrDefault(this.matCd, 0.0);
		
		rst.setBaseYymm(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);

		rst.setSpotRateDisc(this.spotAnnual - liqPremium);
		rst.setLiqPrem(liqPremium);
		rst.setAdjSpotRateDisc(this.spotAnnual);
		rst.setAdjSpotRateCont(this.fwdAnnual);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
		
	public IrDcntRate convertToBizDiscountRate(String bizDv, Map<String, Double> lpMap) {
		IrDcntRate rst = new IrDcntRate();
		double liqPremium = lpMap.getOrDefault(this.matCd, 0.0);
		
		rst.setBaseYymm(this.baseYymm);
		rst.setApplBizDv(bizDv);
		
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);
		
		
		rst.setSpotRate(this.spotAnnual - liqPremium);
//		rst.setLiqPrem(liqPremium);
		rst.setAdjSpotRate(this.spotAnnual);
		rst.setAdjFwdRate(this.fwdAnnual);
		
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public IrDcntRate convertToBizDiscountRate(String bizDv) {
//		BizDiscountRate rst = new BizDiscountRate();
//		
//		rst.setBaseYymm(this.baseYymm);
//		rst.setApplyBizDv(bizDv);
//		
//		rst.setIrCurveId(this.irCurveId);
//		rst.setMatCd(this.matCd);
//		
//		
//		rst.setRfRate(this.spotAnnual );
//		rst.setLiqPrem(0.0);
//		rst.setRiskAdjRfRate(this.spotAnnual);
//		rst.setRiskAdjRfFwdRate(this.fwdAnnual);
//		
//		return rst;
		return convertToBizDiscountRate(bizDv, new HashMap<String, Double>());
	}
	
	
	public IrDcntSceStoBiz convertToBizDiscountRateSce(String bizDv, Map<String, Double> lpMap) {
		IrDcntSceStoBiz rst = new IrDcntSceStoBiz();
		
		double liqPremium = lpMap.getOrDefault(this.matCd, 0.0);
		
		rst.setBaseYymm(this.baseYymm);
		rst.setApplBizDv(bizDv);
		
		rst.setIrCurveId(this.irCurveId);
		rst.setSceNo(this.sceNo);
		rst.setMatCd(this.matCd);
		
		rst.setSpotRate(this.spotAnnual - liqPremium);
		rst.setFwdRate(this.fwdAnnual);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public IrDcntSceStoBiz convertToBizDiscountRateSce(String bizDv, double spread) {
		IrDcntSceStoBiz rst = new IrDcntSceStoBiz();
		
		double liqPremium = spread;
		
		rst.setBaseYymm(this.baseYymm);
		rst.setApplBizDv(bizDv);
		
		rst.setIrCurveId(this.irCurveId);
		rst.setSceNo(this.sceNo);
		rst.setMatCd(this.matCd);
		
		rst.setSpotRate(this.spotAnnual - liqPremium);
		rst.setFwdRate(this.fwdAnnual);
		
		return rst;
	}
	public IrDcntSceStoBiz convertToBizDiscountRateSce(String bizDv) {
		return convertToBizDiscountRateSce(bizDv, new HashMap<String, Double>());
	}
}
