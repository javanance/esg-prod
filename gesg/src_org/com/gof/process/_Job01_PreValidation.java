package com.gof.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.dao._EsgMstDao;
import com.gof.dao.IrCurveSpotDao;
import com.gof.dao.IrVolSwpnDao;
import com.gof.dao._TransitionMatrixDao;
import com.gof.entity.IrParamModel;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveSpot;
import com.gof.entity.IrVolSwpn;
import com.gof.entity._TransitionMatrixUd;
import com.gof.enums.EBoolean;

public class _Job01_PreValidation {
	
	private final static Logger logger = LoggerFactory.getLogger(_Job01_PreValidation.class);
	
	/**
	 *  ��õ���� �Լ��� �ݸ� �������� ���ռ� ����
	 *  <p>�ݸ� �Ⱓ������ Time Bucket �� �����ϰ� �ԷµǾ����� ����
	 *  <p>Time Bucket �� �ݸ��� ����/������ ������ ���� ���ؿ� �ִ��� Ȯ���� (  0.9~1.1 �� ���� ������ ������. )
	 *  @param bssd ���س��
	*/
	public static void validateIrCurve(String bssd) {
		logger.info("Validation Rule 1 : IrCurve Bucket size & Bucket rate ratio");
		
		List<IrCurve> curveMst = IrCurveSpotDao.getIrCurveByCrdGrdCd("000");
		double ratio;
		String curveDate ; 
		for( IrCurve aa : curveMst) {
			curveDate = IrCurveSpotDao.getMaxBaseDate(bssd, aa.getIrCurveId());
			
			List<IrCurveSpot> currentCurveRst = IrCurveSpotDao.getIrCurveSpot(bssd, aa.getIrCurveId());
			List<IrCurveSpot> beforeCurveRst  = IrCurveSpotDao.getIrCurveSpot(bssd, aa.getIrCurveId());
			
			
			Map<String, List<IrCurveSpot>> currentMap = currentCurveRst.stream().collect(Collectors.groupingBy(s->s.getIrCurveId(), Collectors.toList())); 
			Map<String, List<IrCurveSpot>> beforetMap = beforeCurveRst.stream().collect(Collectors.groupingBy(s->s.getIrCurveId(), Collectors.toList()));
			Map<String, IrCurveSpot> beforeMaturityMap ; 
			
			
			for(Map.Entry<String, List<IrCurveSpot>> entry : currentMap.entrySet()) {
				logger.info("Bucket Size of IrCurve {} at {} : Current Month {}, Previous Month : {}. Two size may be the same. "
											, entry.getKey() , curveDate
											, entry.getValue().size(), beforetMap.getOrDefault(entry.getKey(), new ArrayList<IrCurveSpot>()).size());
				
				beforeMaturityMap = beforetMap.getOrDefault(entry.getKey(), new ArrayList<IrCurveSpot>())
											  .stream().collect(Collectors.toMap(s->s.getMatCd(), Function.identity()));
				
				for(IrCurveSpot bb : entry.getValue()) {
					ratio = beforeMaturityMap.getOrDefault(bb.getMatCd(), new IrCurveSpot(bssd, bb.getMatCd(), 0.0)).getSpotRate() / bb.getSpotRate();
					if(ratio <=0.9 || ratio >= 1.1) {
						logger.warn("The ratio {} before Ir Rate to current Ir rate of maturity {} is too big or too small  ", ratio, bb.getMatCd());
					}
				}
				
				entry.getValue().forEach(s -> logger.debug("Current Curve : {}", s.toString()));
			}
			
		}
	}

	
	public static void validateBottomUpIrCurve() {
//		public static void validateBottomUpIrCurve(String bssd) {
		logger.info("Validation Rule 2 : Reference Rate for BottomUp Curve");
		
		List<IrCurve> curveMst = IrCurveSpotDao.getBottomUpIrCurve();
		if( curveMst.size() ==0) {
			logger.warn("There are no BottomUp Curve Info to generate BottomUp Discount Rate, Look up table EAS_IR_CURVE and check USE_YN column");
		}else {
			curveMst.stream().filter(s -> s.getRefCurveId()==null)
					.forEach(s -> logger.warn("Ref Curve is not set for BottomUp Curve {}. Check REF_CURVE_ID column in table EAS_IR_CURVE ", s.getIrCurveId()));
			
		}
		
		curveMst.stream().forEach(s -> logger.debug("Bottom Up Discount Rate for {} will be created using RF Curve {}",s.getCurCd(),  s.getRefCurveId()  ));
		
	}
	
	
	public static void validatePrecedingSwaptionVol(String bssd) {
		List<IrVolSwpn> volRst = IrVolSwpnDao.getSwpnVol(bssd, -36);

		Map<String, List<IrVolSwpn>> volBssdMap = volRst.stream().collect(Collectors.groupingBy(s->s.getBaseYymm(), Collectors.toList()));
		
		logger.info("ESG parameter need Swaption Vol 6*6 Data ( total 36 Data) for past 36 Months");
		volBssdMap.entrySet().stream()
							 .filter(s->s.getValue().size()==36)
							 .forEach(s-> logger.debug("Swaption Vol at {} has {} Data", s.getKey(), s.getValue().size()));

		volBssdMap.entrySet().stream()
							 .filter(s->s.getValue().size()!=36)
							 .forEach(s-> logger.warn("Swaption Vol Size at {} doesn't have  {} not 36 Data", s.getKey(), s.getValue().size()));
		
	}
	
	public static void validateSwaptionVol(String bssd) {
		List<IrVolSwpn> volRst = IrVolSwpnDao.getSwpnVol(bssd);

		Map<String, List<IrVolSwpn>> volBssdMap = volRst.stream().collect(Collectors.groupingBy(s->s.getBaseYymm(), Collectors.toList()));
		
		logger.info("ESG parameter need Swaption Vol 6*6 Data ( total 36 Data) for given date");
		
		volBssdMap.entrySet().stream()
							 .filter(s->s.getValue().size()==36)
							 .forEach(s-> logger.debug("Swaption Vol at {} has {} Data", s.getKey(), s.getValue().size()));

		volBssdMap.entrySet().stream()
							 .filter(s->s.getValue().size()!=36)
							 .forEach(s-> logger.warn("Swaption Vol Size at {} doesn't have {} not 36 Data", s.getKey(), s.getValue().size()));
		
	}
	
	public static void validateUsedEsgModel() {
	
		List<IrParamModel> esgMstList = _EsgMstDao.getEsgMst(EBoolean.Y);			//TODO : irCurveId �� filtering ó�� ���???
		
		
//		No ESG Model
		if(esgMstList.size()==0) {
			logger.error("ESG Model used to generate scenario are not found. Check if the column USE_YN has no \"Y\" record in the table  ");
			System.exit(1);
		}
//		Multi ESE Model !!!
		else if( esgMstList.size() > 1) {
			logger.error("ESG Model used to generate scenario are {} Model ", esgMstList.size());
			logger.error("Please check if the column USE_YN has only one \"Y\" record in the table EAS_ESG_MST table");
			System.exit(1);
		}
//		ESG Info
		esgMstList.forEach(s-> logger.info("Active ESG Models is {}. You can change active Model to switch the USE_YN Column in the table EAS_ESG_MST", s.getIrModelId()));
	}	
	
	
	public static void validTransitionMatrix(String bssd) {
		List<_TransitionMatrixUd> tmList = _TransitionMatrixDao.getTMUd(bssd);
		Map<String , List<_TransitionMatrixUd>> tmMap = tmList.stream().collect(Collectors.groupingBy(s-> s.getFromGrade(), Collectors.toList()));
		
//		int tmSize =0;
		for(Map.Entry<String, List<_TransitionMatrixUd>> entry : tmMap.entrySet()) {
			if(entry.getValue().size() != tmMap.keySet().size() + 1) {
				logger.warn("Transition Element of {} should be {} but input Data is only {}.", entry.getKey(), tmMap.keySet().size() + 1, entry.getValue().size() );
			}
		}
	}
	
	public static void validTransitionMatrixSumEqualOne(String bssd) {
		List<_TransitionMatrixUd> tmList = _TransitionMatrixDao.getTMUd(bssd);
		Map<String , List<_TransitionMatrixUd>> tmMap = tmList.stream().collect(Collectors.groupingBy(s-> s.getFromGrade(), Collectors.toList()));
		
		for(Map.Entry<String, List<_TransitionMatrixUd>> entry : tmMap.entrySet()) {
			double tmRate =0.0;
			for(_TransitionMatrixUd  tm : entry.getValue()) {
				tmRate = tmRate + tm.getTmRate();
			}
			if(tmRate !=1.0) {
				logger.warn("Sum of Transition Rate of Credit Grade {} is not equal to 1.0. It is {}", entry.getKey(), tmRate );
			}
		}
	}
}