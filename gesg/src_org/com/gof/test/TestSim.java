package com.gof.test;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;

import com.gof.entity.IrParamHwBiz;
import com.gof.model.Hw1fSimulationKics;
import com.gof.model.IrModel;
import com.gof.model.entity.Hw1fCalibParas;
import com.gof.model.entity.IrModelSce;
import com.gof.model.entity.SmithWilsonRslt;
import com.gof.entity.IrCurveSpot;
import com.gof.util.DateUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class TestSim extends IrModel {
	
	protected int[]         monthSeq;
	protected double[]      timeFactor;
	protected String[]      matCd;
	protected double[]      spotContBase;
	protected double[]      spotDiscBase;
	protected double[]      fwdContBase;
	protected double[]      fwdDiscBase;	
	protected double[]      dcntFactorBase;
	
	protected int           scenNum;
	protected double        dt;	
	
	protected double[]      alpha;
	protected double[]      sigma;
	protected double[]      theta;
	protected int[]         alphaPiece;
	protected int[]         sigmaPiece;	
	protected String        mode;
	protected boolean       priceAdj;

	protected double[][]    randNum;
	protected double[][]    sRateScen;
	protected double[][]    spotContScen;
	protected double[][]    spotDiscScen;
	protected double[][]    fwdContScen;
	protected double[][]    fwdDiscScen;	
	protected double[][]    dcntFactorScen;
	
	protected double[]      sRateMean;
	protected double[]      spotContMean;
	protected double[]      spotDiscMean;	
	protected double[]      dcntFactorMean;
	protected double[]      fwdContMean;
	protected double[]      fwdDiscMean;	
	
	protected double        ltfr;	
	protected int           ltfrT;
	protected int           prjYear;
	protected int           prjMonth;	
	protected int           prjInterval;	
	protected double        lastLiquidPoint;
	
	protected int           duraMonth;
	protected double[][]    bondYieldCont;	
	protected double[][]    bondYieldDisc;
	protected double[]      bondYieldContMean;
	protected double[]      bondYieldDiscMean;	
	
	protected List<SmithWilsonRslt> swRsltList = new ArrayList<SmithWilsonRslt>();
	protected TreeMap<String, Double> alphaMap   = new TreeMap<String, Double>();
	protected TreeMap<String, Double> sigmaMap   = new TreeMap<String, Double>();
	
			
	public static void main(String[] args) throws Exception {		
		
		char       cmpdType    = CMPD_MTD_DISC;
		int        prjYear     = 100;
		double     ltfr        = 4.401688541677426;	
		int        ltfrT       = 60;
		boolean    real        = false;
		String bssd 			="201712";
		LocalDate baseDate 	= DateUtil.convertFrom(bssd);
	    
		String   mode      = "DUAL";   // SIGMA, DUAL, CONST      
		boolean  priceAdj  = false;
	    
		
		String[]   matCd       = new String[] {"M0003", "M0006", "M0009", "M0012", "M0018", "M0024", "M0030", "M0036", "M0060", "M0084", "M0120", "M0180", "M0240"};
		double[]   baseRate    = new double[] {1.258, 1.278, 1.327, 1.333, 1.338, 1.361, 1.373, 1.351, 1.466, 1.603, 1.673, 1.686, 1.701};    //FY2019 RF_CONT
		
		List<IrCurveSpot> curveList = new ArrayList<IrCurveSpot>();		
		for(int i=0; i<matCd.length; i++) {
			IrCurveSpot curve = new IrCurveSpot();			
			curve.setMatCd(matCd[i]);
			curve.setSpotRate(baseRate[i]);
			curveList.add(curve);
		}
		
		List<IrParamHwBiz> bizParamList = setBizParam(bssd);
		
		bizParamList.forEach(s-> log.info("aaa : {},{},{}", s.getParamTypCd(), s.getMatCd(), s.getParamVal()));
		
		int[] alphaPiece = bizParamList.stream().filter(s->s.getParamTypCd().equals("ALPHA")).mapToInt(s-> Integer.valueOf(s.getMatCd().split("M")[1])).toArray();
		int[] sigmaPiece = bizParamList.stream().filter(s->s.getParamTypCd().equals("SIGMA")).mapToInt(s-> Integer.valueOf(s.getMatCd().split("M")[1])).toArray();
		
		
		List<Hw1fCalibParas> hwParasList = Hw1fCalibParas.convertFrom(bizParamList);
	
		Hw1fSimulationKics hwRslt        = new Hw1fSimulationKics(baseDate, curveList, hwParasList, alphaPiece, sigmaPiece, mode, priceAdj, 1000, cmpdType, real, ltfr, ltfrT, prjYear, DCB_MON_DIF, 2);		
		List<IrModelSce>   hwRsltList    = hwRslt.getIrModelHw1fList();		
		
		hwRsltList.stream().forEach(s-> log.info("aaaaa :  {}, {},{},{}", s.getBaseDate(), s.getSceNo(), s.getMatCd(), s.getSpotRateDisc()));
		
		log.info("__________{}_________________\n", mode);		
	}	
	
	private static List<IrParamHwBiz> setBizParam(String bssd){
	
//		정보: BBBB : 20171231_ALPHA_M0120_1.0000000425215018E-5
//		정보: BBBB : 20171231_ALPHA_M0240_0.036252624150937655
//
//		정보: BBBB : 20171231_SIGMA_M0012_0.004635956471377259
//		정보: BBBB : 20171231_SIGMA_M0024_0.005171071718669859
//		정보: BBBB : 20171231_SIGMA_M0036_0.005726712822669287
//		정보: BBBB : 20171231_SIGMA_M0060_0.005212117052149407
//		정보: BBBB : 20171231_SIGMA_M0084_0.003962474289118655
//		정보: BBBB : 20171231_SIGMA_M0120_0.0047416671670727
//		정보: BBBB : 20171231_SIGMA_M0240_0.0047416671670727
		
		List<IrParamHwBiz> bizParamList = new ArrayList<IrParamHwBiz>();
		
		IrParamHwBiz alpha1= new IrParamHwBiz();
		IrParamHwBiz alpha2= new IrParamHwBiz();
		IrParamHwBiz sigma1= new IrParamHwBiz();
		IrParamHwBiz sigma2= new IrParamHwBiz();
		IrParamHwBiz sigma3= new IrParamHwBiz();
		IrParamHwBiz sigma4= new IrParamHwBiz();
		IrParamHwBiz sigma5= new IrParamHwBiz();
		IrParamHwBiz sigma6= new IrParamHwBiz();
		IrParamHwBiz sigma7= new IrParamHwBiz();
		IrParamHwBiz sigma8= new IrParamHwBiz();
		
		alpha1.setBaseYymm(bssd);
		alpha1.setApplBizDv("I");
		alpha1.setParamTypCd("ALPHA");
		alpha1.setIrModelId("4");
		alpha1.setMatCd("M0240");
		alpha1.setParamVal(1.0000000425215018E-5);
		
		alpha2.setBaseYymm(bssd);
		alpha2.setApplBizDv("I");
		alpha2.setParamTypCd("ALPHA");
		alpha2.setIrModelId("4");
		alpha2.setMatCd("M1200");
		alpha2.setParamVal(0.036252624150937655);
		
		
		sigma1.setBaseYymm(bssd);
		sigma1.setApplBizDv("I");
		sigma1.setParamTypCd("SIGMA");
		sigma1.setIrModelId("4");
		sigma1.setMatCd("M0012");
		sigma1.setParamVal(0.004635956471377259);
		
		sigma2.setBaseYymm(bssd);
		sigma2.setApplBizDv("I");
		sigma2.setParamTypCd("SIGMA");
		sigma2.setIrModelId("4");
		sigma2.setMatCd("M0024");
		sigma2.setParamVal(0.005171071718669859);
		
		sigma3.setBaseYymm(bssd);
		sigma3.setApplBizDv("I");
		sigma3.setParamTypCd("SIGMA");
		sigma3.setIrModelId("4");
		sigma3.setMatCd("M0036");
		sigma3.setParamVal(0.005726712822669287);
		
		sigma4.setBaseYymm(bssd);
		sigma4.setApplBizDv("I");
		sigma4.setParamTypCd("SIGMA");
		sigma4.setIrModelId("4");
		sigma4.setMatCd("M0060");
		sigma4.setParamVal(0.005212117052149407);
		
		sigma5.setBaseYymm(bssd);
		sigma5.setApplBizDv("I");
		sigma5.setParamTypCd("SIGMA");
		sigma5.setIrModelId("4");
		sigma5.setMatCd("M0084");
		sigma5.setParamVal(0.003962474289118655);
		
		sigma6.setBaseYymm(bssd);
		sigma6.setApplBizDv("I");
		sigma6.setParamTypCd("SIGMA");
		sigma6.setIrModelId("4");
		sigma6.setMatCd("M0120");
		sigma6.setParamVal(0.0047416671670727);
		
		sigma7.setBaseYymm(bssd);
		sigma7.setApplBizDv("I");
		sigma7.setParamTypCd("SIGMA");
		sigma7.setIrModelId("4");
		sigma7.setMatCd("M0240");
		sigma7.setParamVal(0.0047416671670727);
		
		sigma8.setBaseYymm(bssd);
		sigma8.setApplBizDv("I");
		sigma8.setParamTypCd("SIGMA");
		sigma8.setIrModelId("4");
		sigma8.setMatCd("M1200");
		sigma8.setParamVal(0.0047416671670727);
		
		bizParamList.add(alpha1);
		bizParamList.add(alpha2);
		bizParamList.add(sigma1);
		bizParamList.add(sigma2);
		bizParamList.add(sigma3);
		bizParamList.add(sigma4);
		bizParamList.add(sigma5);
		bizParamList.add(sigma6);
		bizParamList.add(sigma7);
		bizParamList.add(sigma8);
		
		return bizParamList;
	}
}	