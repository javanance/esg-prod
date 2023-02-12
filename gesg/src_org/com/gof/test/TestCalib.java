package com.gof.test;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.gof.entity.IrCurveSpot;
import com.gof.model.Hw1fCalibrationKics;
import com.gof.model.IrModel;
import com.gof.model.entity.SmithWilsonRslt;
import com.gof.model.entity.SwpnVolInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class TestCalib {	
	
	protected int[]         swpnMat;
	protected int[]         swapTenor;
	protected double[][]    swpnVolMkt;
	
	protected int[][]       swapMatTenor;	
	protected double[][]    swapDfSum;
	protected double[][]    swapRate;
	protected double[][]    swpnPriceAtm;
	protected double[][]    swpnPriceHw;
	
	protected double[]      initParas;	
	protected double[]      optParas;
	protected int[]         alphaPiece;
	protected int[]         sigmaPiece;		

	protected int[]         pmtIdxBaseRate;
	protected double[]      priceBaseRate;
	protected double[]      spotContBaseRate;
	protected double[]      fwdContBaseRate;		

	protected int           freq;
	protected double        notional;	
	protected double        accuracy;
	protected int           itrMax;	
		                        
	protected double        ltfr;	
	protected int           ltfrT;                      
	protected int           prjYear;
	protected int           prjInterval;	
	protected double        lastLiquidPoint;
	
	protected List<SmithWilsonRslt> swRsltList = new ArrayList<SmithWilsonRslt>();
	
			
	public static void main(String[] args) throws Exception {		
		
//		char       cmpdType    = CMPD_MTD_DISC;
//		int        prjYear     = 100;		
//		double     ltfr        = 4.5;
//		boolean    real        = false;
//		LocalDate  baseDate    = LocalDate.of(2017, 12, 31);
//
//	    int[]      swpnMat     = new int[] {1,2,3,5,7,10};
//	    int[]      swapTenor   = new int[] {1,2,3,5,7,10};
//		double[][] swpnVolMkt  = new double[][] {{18.09, 18.99, 19.64, 20.39, 20.61, 21.13}, 
//										         {21.08, 21.31, 20.74, 20.57, 20.48, 20.58},
//										         {21.55, 21.12, 20.65, 20.46, 20.2 , 20.34},
//										         {21.55, 21.16, 20.58, 19.99, 19.42, 19.47},
//										         {21.25, 20.59, 19.92, 18.7 , 18.52, 18.65},
//										         {20.07, 19.17, 18.74, 18.33, 18.43, 18.6 }};	    
//
//		String[]   matCd       = new String[] {"M0003", "M0006", "M0009", "M0012", "M0018", "M0024", "M0030", "M0036", "M0048", "M0060", "M0084", "M0120", "M0240"};
//		double[]   baseRate    = new double[] {1.52, 1.66, 1.79, 1.87, 2.01, 2.09, 2.15, 2.15, 2.31, 2.38, 2.44, 2.46, 2.44};                             //FY2017 RF_DISC
	
	
//		char       cmpdType    = CMPD_MTD_CONT;
		char       cmpdType    = IrModel.CMPD_MTD_DISC;				
		boolean    real        = false;
		LocalDate  baseDate    = LocalDate.of(2017, 12, 31);

	    int[]      swpnMat     = new int[] {1,2,3,5,7,10};
	    int[]      swapTenor   = new int[] {1,2,3,5,7,10};	    
		double[][] swpnVolMkt  = new double[][] {{18.095, 18.995, 19.64 , 20.39 , 20.615, 21.135}, 
										         {20.2  , 20.8  , 19.8  , 19.9  , 19.9  , 20.58 },
										         {20.8  , 21.2  , 20.1  , 19.7  , 20.2  , 20.33 },
										         {21.6  , 21.8  , 20.1  , 19.99 , 19.42 , 19.47 },
										         {21.4  , 20.6  , 19.92 , 18.7  , 18.52 , 18.64 },
										         {20.02 , 19.17 , 18.72 , 18.33 , 18.42 , 18.6  }};
	    
		String[]   matCd       = new String[] {"M0003", "M0006", "M0009", "M0012", "M0018", "M0024", "M0030", "M0036", "M0060", "M0084", "M0120", "M0180", "M0240"};
		double[]   baseRate    = new double[] {1.512, 1.625, 1.78 , 1.87 , 2.004, 2.081, 2.144, 2.152, 2.35 , 2.444, 2.473, 2.458, 2.46 };    //FY2017 RF_CONT
		
		List<SwpnVolInfo> volList = new ArrayList<SwpnVolInfo>();
		for(int i=0; i<swpnMat.length; i++) {
			for(int j=0; j<swapTenor.length; j++) {
				SwpnVolInfo swpnVol = new SwpnVolInfo();
				swpnVol.setBaseYymm(baseDate.format(DateTimeFormatter.BASIC_ISO_DATE));
				swpnVol.setSwpnMat(swpnMat[i]);
				swpnVol.setSwapTenor(swapTenor[j]);
				swpnVol.setVol(swpnVolMkt[i][j]);
				volList.add(swpnVol);
			}
		}
		
		List<IrCurveSpot> curveList = new ArrayList<IrCurveSpot>();		
		for(int i=0; i<matCd.length; i++) {
			IrCurveSpot curve = new IrCurveSpot();			
			curve.setMatCd(matCd[i]);
			curve.setSpotRate(baseRate[i]);
			curveList.add(curve);
		}		
		
		double[] initParas = new double[] {0.03, 0.06, 0.007, 0.006, 0.005, 0.004, 0.005, 0.006};
		
		Hw1fCalibrationKics calib = new Hw1fCalibrationKics(baseDate, curveList, volList, new int[] {10}, new int[] {1, 2, 3, 5, 7, 10}
		                                                  , initParas, 2, 100, cmpdType, real, 6, IrModel.DCB_MON_DIF, 10, 1e-10);
		
//		calib.getHw1fCalibrationResultList().stream().forEach(s -> log.info("AAAA :  {}", s.toString()));
		
		String irModelType = "4";
		String paramCalcCd = "SIGMA_LOCAL_CALIB";
		
		calib.getHw1fCalibrationResultList().stream().map(s->s.convert(irModelType, paramCalcCd)).flatMap(s-> s.stream()).forEach(s -> log.info("BBBB : {}", s.toString()));
		
		log.info("_____________________________\n");
	}	

}		
	