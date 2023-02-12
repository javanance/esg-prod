package com.gof.process;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao._BottomupDcntDao;
import com.gof.entity.IrDcntRate;
import com.gof.entity.IrDcntSceStoBiz;
import com.gof.entity.IrDcntRateBu;
import com.gof.entity.IrCurve;
import com.gof.entity._SmithWilsonResult;
import com.gof.model.IrModel;
import com.gof.model.SmithWilsonKics;
import com.gof.model.TermStructureModel;
import com.gof.model.entity.SmithWilsonRslt;
import com.gof.util.DateUtil;
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
public class Job18_BizDiscountRate {
	
	public static Stream<IrDcntRate> createBizBottomUpCurveNew(String bssd, IrCurve curveMst, int projectionYear) {
		
		List<String> tenorList = EsgConstant.getTenorList();
		
		List<IrDcntRateBu> swRfAdjBottomUp = new ArrayList<IrDcntRateBu>();
		List<IrDcntRateBu> swRfBottomUp = new ArrayList<IrDcntRateBu>();
		
		Map<String, Double> lpMap = new HashMap<String, Double>();
		Map<String, Double> curveRfMap = _BottomupDcntDao.getTermStructure(bssd, curveMst.getIrCurveId()).stream()
														.filter(s-> tenorList.contains(s.getMatCd()))
														.collect(toMap(IrDcntRateBu::getMatCd, IrDcntRateBu::getSpotRateDisc));
		
//		log.info("curveRfMap: {}", curveRfMap);
		
		swRfBottomUp = createCurveBySwNew(bssd, "0", curveMst, curveRfMap, projectionYear);
		
		Map<String, Double> curveRfAdjMap = _BottomupDcntDao.getTermStructure(bssd, curveMst.getIrCurveId()).stream()
														.filter(s-> tenorList.contains(s.getMatCd()))
														.collect(toMap(IrDcntRateBu::getMatCd, IrDcntRateBu::getAdjSpotRateDisc));
		
//		log.info("curveRfAdjMap: {}", curveRfAdjMap);
		
		swRfAdjBottomUp = createCurveBySwNew(bssd, "0", curveMst, curveRfAdjMap, projectionYear);		
		
		
		log.info("Job18(Biz Bottom Up Ir Rate Calculation) creates {} results for {}. inserted into EAS_BOTTOMUP_DCNT", curveMst.getIrCurveId());
		return  TermStructureModel.createForward(bssd, curveMst, "0", swRfBottomUp, swRfAdjBottomUp).stream();		
		
	}
	
	
	private static List<IrDcntRateBu> createCurveBySwNew(String bssd, String sceNo, IrCurve curveMst, Map<String, Double> curveMap, int projectionYear) {
		
		Map<String, Double> liqMap = new HashMap<String, Double>();
		
		double ufr  = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfr();
		double ufrt = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfrT();
		
//		SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, curveMst.getIrCurveId(), sceNo, curveMap, ufr, ufrt, projectionYear);
//		List<SmithWilsonResult> rst = sw.getSmithWilsionResult();
		
		Map<Double, Double> ts = new TreeMap<Double, Double>();
		
		for(Map.Entry<String, Double> crv : curveMap.entrySet()) {
			ts.put(Double.parseDouble(crv.getKey().substring(1, 5)) / IrModel.MONTH_IN_YEAR, crv.getValue());
		}
//		log.info("ts:{}, ltfr:{}, ltfrT:{}, prjYear:{}, {}", ts, ufr, (int)ufrt, projectionYear + 1, DateUtil.convertFrom(bssd).with(TemporalAdjusters.lastDayOfMonth()));
	
		SmithWilsonKics sw  = new SmithWilsonKics(DateUtil.convertFrom(bssd).with(TemporalAdjusters.lastDayOfMonth()), ts, 'D', true, ufr, (int) ufrt, projectionYear + 1, IrModel.DCB_MON_DIF);

		List<SmithWilsonRslt> swRsltList = new ArrayList<SmithWilsonRslt>();
		swRsltList = sw.getSmithWilsonResultList();				
		List<_SmithWilsonResult> rst = swRsltList.stream().map(s -> s.convert(curveMst.getIrCurveId())).collect(toList());				
		
		return rst.stream().map(s->s.convertToBottomUp(liqMap)).collect(toList());
	}
}
