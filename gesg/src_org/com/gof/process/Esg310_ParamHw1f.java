package com.gof.process;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;

import com.gof.entity.IrCurveSpot;
import com.gof.entity.IrParamHwCalc;
import com.gof.entity.IrVolSwpn;
import com.gof.enums.EJob;
import com.gof.model.Hw1fCalibrationKics;
import com.gof.model.entity.SwpnVolInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Esg310_ParamHw1f extends Process {
	
	public static final Esg310_ParamHw1f INSTANCE = new Esg310_ParamHw1f();
	public static final String jobId = INSTANCE.getClass().getSimpleName().toUpperCase().substring(0, ENTITY_LENGTH);

	public static List<IrParamHwCalc> createParamHw1f(String bssd, String irModelId, String irCurveId, List<IrCurveSpot> spotList, List<IrVolSwpn> swpnVolList, double[] initParas, Integer freq, double errTol, int[] alphaPiece, int[] sigmaPiece) {			
	
		freq = Math.max(freq, 1);		
		List<SwpnVolInfo> volInfo  = swpnVolList.stream().map(s-> SwpnVolInfo.convertFrom(s)).collect(toList());		
		
		Hw1fCalibrationKics calib = new Hw1fCalibrationKics(bssd, spotList, volInfo, alphaPiece, sigmaPiece, initParas, freq, errTol);
		List<IrParamHwCalc> rst   = calib.getHw1fCalibrationResultList().stream().map(s->s.convert(irModelId, irCurveId))
																			     .flatMap(s-> s.stream())
																			     .collect(toList());

		rst.stream().forEach(s -> s.setLastModifiedBy(jobId));
		rst.stream().forEach(s -> s.setLastUpdateDate(LocalDateTime.now()));
		rst.stream().forEach(s-> log.info("Calibration Result: {}", s.toString()));
		
		log.info("{}({}) creates {} results. They are inserted into [{}] Table", jobId, EJob.valueOf(jobId).getJobName(), rst.size(), toPhysicalName(IrParamHwCalc.class.getSimpleName()));
		
		return rst;
	}	
	

	public static List<IrParamHwCalc> createParamHw1f(String bssd, String irModelId, String irCurveId, List<IrCurveSpot> spotList, List<IrVolSwpn> swpnVolList, double[] initParas, Integer freq, double errTol) {
		return createParamHw1f(bssd, irModelId, irCurveId, spotList, swpnVolList, initParas, freq, errTol, new int[] {10}, new int[] {1, 2, 3, 5, 7, 10});
	}	
	
	
	public static List<IrParamHwCalc> createParamHw1f(String bssd, String irModelId, String irCurveId, List<IrCurveSpot> spotList , List<IrVolSwpn> swpnVolList, double errTol) {				
		return createParamHw1f(bssd, irModelId, irCurveId, spotList, swpnVolList, new double[] {0.03, 0.06, 0.007, 0.006, 0.005, 0.004, 0.005, 0.006}, 2, errTol, new int[] {10}, new int[] {1, 2, 3, 5, 7, 10});
	}
	
	
//	//ADD : 20210629(But no job currently)
//	public static List<ParamCalcHis> createHwKicsParamCalcHisAsyncFromYtm(String bssd, List<IrCurveYtmHis> ytmRst , List<SwaptionVol> swapVolRst, double ufr, double ufrt, double errorTolerance) {
//
//		int projYear = 100;
//		String irModelType = "4";
//		String paramCalcCd = "SIGMA_LOCAL_CALIB";
//
//		int[]    alphaPiece = new int[] {10} ;
//		int[]    sigmaPiece = new int[] {1, 2, 3, 5, 7, 10};
//		double[] initParas  = new double[] {0.03, 0.06, 0.007, 0.006, 0.005, 0.004, 0.005, 0.006};		
//		
//		List<SwpnVolInfo> volRst  = swapVolRst.stream().map(s-> SwpnVolInfo.convertFrom(s)).collect(toList());
//		
//		SmithWilsonKicsBts swBts = new SmithWilsonKicsBts(DateUtil.convertFrom(bssd), ytmRst);		
//		swBts.getSpotBtsRslt().forEach(s -> log.info("{}, {}", s.getMatCd(), s.getIntRate()));		
//		
//		Hw1fCalibrationKics calib = new Hw1fCalibrationKics(bssd, swBts.getSpotBtsRslt(), volRst, alphaPiece, sigmaPiece, initParas, projYear, errorTolerance);		
//		List<ParamCalcHis> rst = calib.getHw1fCalibrationResultList().stream().map(s->s.convert(irModelType, paramCalcCd))
//																			  .flatMap(s-> s.stream())
//																			  .collect(toList());
//
//		rst.forEach(s-> log.info("aa :  {}", s.toString()));
//		
//		log.info("Job11 (Historical Hull White Parameter for Kics) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
//		
//		return rst;
//	}	


}