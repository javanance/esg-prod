package com.gof.process;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gof.dao._LiqPremiumDao;
import com.gof.entity.IrSprdLpBiz;
import com.gof.entity.IrSprdLpUsr;
import com.gof.entity.IrSprdLp;
import com.gof.util.EsgConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class _Job16_BizLiquidPremium {
	
	public static List<IrSprdLpBiz> createBizLiqPremium(String bssd, String bizDv, String modelId) {
		List<IrSprdLpBiz> rstList = new ArrayList<IrSprdLpBiz>();
		
		List<IrSprdLp> liqPremList    = _LiqPremiumDao.getLiqPremium(bssd, modelId);
		List<IrSprdLpUsr> lpUserRst = _LiqPremiumDao.getLiqPremiumUd(bssd, bizDv);
	
//		log.info("zzzz :{},{},{}", liqPremList.size(), modelId, bssd);
//		liqPremList.forEach(s->log.info("zzz : {}", s.getModelId()));
		
//		if(lpUserRst.isEmpty()) {
//			rstList = liqPremList.stream().map(s->s.convertTo(bizDv)).collect(toList());
//		}
//		else{
//			rstList = lpUserRst.stream().map(s->s.convertToBizLiqPremium(bssd)).collect(toList());
//		}
		log.info("aaaa : {}", lpUserRst.size());
		Map<String, Double> lqMap = new HashMap<String, Double>();
		if(lpUserRst.isEmpty()) {
			lqMap = liqPremList.stream().collect(toMap(IrSprdLp::getMatCd, IrSprdLp::getLiqPrem));
		}
		else{
			lqMap = lpUserRst.stream().collect(toMap(IrSprdLpUsr::getMatCd, IrSprdLpUsr::getLiqPrem));
			
		}
		
		List<String> tenorList = EsgConstant.getTenorList();
		Collections.reverse(tenorList);
		
		double prevLiq =0.0;
		
		for(String aa : tenorList) {
			if(lqMap.containsKey(aa)) {
				prevLiq = lqMap.get(aa);
			}
			rstList.add(build(bssd, bizDv, aa, prevLiq));
		}
		
		return rstList;
	}
	
	
	private static IrSprdLpBiz build(String bssd, String bizDv, String matCd, double liqPrem) {
		IrSprdLpBiz rst = new IrSprdLpBiz();
		rst.setBaseYymm(bssd);
		rst.setApplBizDv(bizDv);
		rst.setMatCd(matCd);
		rst.setLiqPrem(liqPrem);
		rst.setLiqPrem(liqPrem);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
}
