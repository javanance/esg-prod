package com.gof.process;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Session;

import com.gof.dao.CoEsgMetaDao;
import com.gof.dao.DaoUtil;
import com.gof.dao.IrCurveDao;
import com.gof.dao.IrCurveSpotDao;
import com.gof.dao.IrCurveSpotWeekDao;
import com.gof.dao.IrCurveYtmDao;
import com.gof.dao.IrDcntRateDao;
import com.gof.dao.IrParamSwDao;
import com.gof.dao._SmithWilsonDao;
import com.gof.dao._StdAssetDao;
import com.gof.dao.IrVolSwpnDao;
import com.gof.dao.RcCorpPdDao;
import com.gof.entity.IrDcntRate;
import com.gof.entity.IrDcntRateBiz;
import com.gof.entity.IrDcntRateBu;
import com.gof.entity.IrDcntSceStoBiz;
import com.gof.entity.IrParamAfnsCalc;
import com.gof.entity.IrParamHwBiz;
import com.gof.entity.IrParamHwCalc;
import com.gof.entity.StdAsstIrSceSto;
import com.gof.entity.IrParamModel;
import com.gof.entity.IrParamSw;
import com.gof.entity.IrSprdAfnsCalc;
import com.gof.entity.IrSprdLp;
import com.gof.entity.IrSprdLpBiz;
import com.gof.entity.IrParamHwRnd;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveSpot;
import com.gof.entity.IrCurveSpotWeek;
import com.gof.entity.IrCurveYtm;
import com.gof.entity.CoJobInfo;
import com.gof.entity._SmithWilsonParamHis;
import com.gof.entity.StdAsst;
import com.gof.entity.IrVolSwpn;
import com.gof.entity.IrVolSwpnUsr;
import com.gof.entity.RcCorpPd;
import com.gof.enums.EBoolean;
import com.gof.enums.EJob;
import com.gof.enums.ERunArgument;
import com.gof.model.LogNormal4j;
import com.gof.model.entity.TransMat;
import com.gof.util.DateUtil;
import com.gof.util.EsgConstant;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
	
	private static Map<ERunArgument, String> argInputMap = new HashMap<>();
	private static Map<String, String> argInDBMap = new HashMap<>();
	
	private static String    filePath;
	private static String    bssd;
	
	private static int       projectionYear              = 100;	
	private static int       batchNum                    = 100;	                                                     
	private static long      cnt                         = 0;            
	private static String    jobString;	                 
	private static int       flushSize                   = 10000;
	private static int       logSize                     = 100000;
	private static Session   session;
	private static String    schema                      = "RSKI";
	                                                     
	private static List<String>         irCurveIdList    = new ArrayList<String>();	
	private static Map<String, IrCurve> irCurveMap       = new TreeMap<String, IrCurve>();
	
	//TODO: to be deleted
	private static Map<String, IrCurve> ifrsCurveMap     = new TreeMap<String, IrCurve>();	
	private static Map<String, IrCurve> kicsCurveMap     = new TreeMap<String, IrCurve>();
	
	private static Map<String, IrParamSw> irCurveSwMap   = new TreeMap<String, IrParamSw>();	
	private static Map<String, IrParamSw> irCurveSwMap2  = new TreeMap<String, IrParamSw>();
	private static Map<String, Map<Integer, IrParamSw>> kicsSwMap = new TreeMap<String, Map<Integer, IrParamSw>>();
	private static Map<String, Map<Integer, IrParamSw>> ifrsSwMap = new TreeMap<String, Map<Integer, IrParamSw>>();
	private static Map<String, Map<Integer, IrParamSw>> ibizSwMap = new TreeMap<String, Map<Integer, IrParamSw>>();
	private static Map<String, Map<Integer, IrParamSw>> saasSwMap = new TreeMap<String, Map<Integer, IrParamSw>>();
	                                                     
	private static List<String> jobList 		         = new ArrayList<String>();	

	private static double[]  hw1fCalibInitParam          = new double[] {0.03, 0.06, 0.007, 0.006, 0.005, 0.004, 0.005, 0.006};
	private static int       randGenMode                 = 1;	
	private static int[]     randSeedBond                = new int[] {245};
	private static int[]     randSeedAsst                = new int[] {701};
	private static double    targetDuration              = 3.0;
	private static int[]     hwAlphaPiece                = new int[] {10};
	private static int[]     hwSigmaPiece                = new int[] {1, 2, 3, 5, 7, 10};
	
	private static String stBssd;	
	private static int    weekDay;    // MON = 1, ... FRI = 5
	
	//TODO: to be deleted
	private static String afnsTenorListStr = "M0003";
	private static List<String> afnsTenorList = new ArrayList<String>();		
	
	public static void main(String[] args) {		

// ****************************************************************** Run Argument &  Common Data Setting ********************************
		
		init(args);		// Input Data Setting
		
// ****************************************************************** Pre-Process and Input Setting      ********************************	

		job100();       // Job 100: Pre-process
		job101();       // Job 101: IrParamSwUsr to IrParamSw
		job102();       // Job 101: IrVolSwpnUsr to IrVolSwpn
		
		job110();       // Job 110: YTM to SPOT by Smith-Wilson Method
		job111();       // Job 111: YTM to SPOT by Smith-Wilson Method Migration for 201912~202106
		
// ****************************************************************** Deterministic Scenario with LP      ********************************		

		job210();       // Job 210: AFNS Weekly Input TermStructure Setup
		job220();       // Job 220: AFNS Shock Spread		
		
		job230();		// job 230: Set Liquidity Premium
		job240();		// job 240: Biz Liquidity Premium
		
		job250();		// job 250: BottomUp Risk Free TermStructure with Liquidity Premium
		job260();		// job 260: Interpolated TermStructure by SW		
		
		job270();		// job 270: Biz TermStructure by SW
		
		job280();       // Job 280: TermStructure Forecast by Forward
		
// ****************************************************************** Stochastic Scenario with LP         ********************************	
		
		job310();		// Job 310: Calibration of HW1F Model Parameter
		job320();		// job 320: Validation of Calibration Result of HW1F Model		
		job330();       // job 330: Biz Applied HW1F Model Parameter
		
		job340();       // job 340: Stochastic Scenario of Biz TermStructure		
		job370();       // job 370: Bond Yield Scenario of Biz TermStructure
		
// ****************************************************************** RC Job                              ********************************

		job810();		// Job 810: TM
		job82();		// Job 82 : IFRS Corp PD
		job83();		// Job 83 : KICS Corp PD
		
		
// ****************************************************************** ESG Stock Simulation Job            ********************************
		
		job34();		// Std Asset Yield Deterministic Scenario from irCurve
		job34A();		// Std Asset Yield Deterministic Scenario from irCurve_JAVA
		
		job35();		// Std Asset Yield Scenaio for KOSPI200_IFRS
		job36();		// Std Asset Yield Scenaio for KOSPI200_KICS		


// ****************************************************************** End Job                             ********************************
		
		hold();
		HibernateUtil.shutdown();
		System.exit(0);		
		
//		try {
//			log.warn("try: {}", HibernateUtil.sessionFactory.isClosed());
//			HibernateUtil.shutdown();
//		} catch (Exception e) {
//			log.warn("Error in Close Session", e);
//			log.warn("Catch: {}", HibernateUtil.sessionFactory.isClosed());
//			e.printStackTrace();
//			System.exit(1);
//		}
		
	}
	

	private static void init(String[] args) {
		
		for (String aa : args) {
			log.info("Input Arguments : {}", aa);
			for (ERunArgument bb : ERunArgument.values()) {
				if (aa.split("=")[0].toLowerCase().contains(bb.name())) {
					argInputMap.put(bb, aa.split("=")[1]);
					break;
				}
			}
		}
		
		try {
			bssd = argInputMap.get(ERunArgument.time).replace("-", "").replace("/", "").substring(0, 6);
		} catch (Exception e) {
			log.error("Argument error : -Dtime" );
			System.exit(0);
		}
		
		Properties properties = new Properties();
		try {
			FileInputStream fis = new FileInputStream(argInputMap.get(ERunArgument.properties));
			properties.load(new BufferedInputStream(fis));

		} catch (Exception e) {
			log.warn("Error in Properties Loading : {}", e);
			System.exit(1);
		}		
		session = HibernateUtil.getSessionFactory(properties).openSession();
		log.info("End of session call");
		
		argInDBMap = CoEsgMetaDao.getCoEsgMeta("PROPERTIES").stream().collect(toMap(s->s.getParamKey(), s->s.getParamValue()));		
		log.info("argInDBMap: {}", argInDBMap);		
		
		
		for(Map.Entry<String, String> entry : EsgConstant.getStrConstant().entrySet()) {
//			log.info("entry: {}", entry.toString());
			try {
				EsgConstant.getNumConstant().put(entry.getKey(), Double.parseDouble(entry.getValue()));
			} catch (Exception e) {
//				log.info("entry not Number: {}", entry.toString());
				continue;
			}
		}	
		
		argInDBMap. entrySet().forEach(s -> log.info("Effective Arguments in DB : [{}], [{}]", s.getKey(), s.getValue()));
		argInputMap.entrySet().forEach(s -> log.info("Effective Arguments Input : [{}], [{}]", s.getKey(), s.getValue()));		
		
		filePath                         = argInDBMap.getOrDefault("OUTPUT_DIR", 	 properties.getOrDefault("OUTPUT_DIR", "C:\\Dev\\ESG\\").toString()) ;
		jobString 		                 = argInDBMap.getOrDefault("JOB", "N/A").toString();
		jobList 	                     = Arrays.stream(jobString .split(",")).map(s -> s.trim()).collect(Collectors.toList());

		try {
			String hw1fCalibInitParamStr = argInDBMap.getOrDefault("HW1F_CALIB_INIT_PARAM", "0.03, 0.06, 0.007, 0.006, 0.005, 0.004, 0.005, 0.006").toString();
			hw1fCalibInitParam           = Arrays.stream(hw1fCalibInitParamStr.split(",")).map(s->s.trim()).map(Double::parseDouble).mapToDouble(Double::doubleValue).toArray();			
			if(hw1fCalibInitParam.length !=8) hw1fCalibInitParam = new double[] {0.03, 0.06, 0.007, 0.006, 0.005, 0.004, 0.005, 0.006};			
		} catch (Exception e) {
			hw1fCalibInitParam           = new double[] {0.03, 0.06, 0.007, 0.006, 0.005, 0.004, 0.005, 0.006};			
		}		
//		log.info("hw1fCalibInitParam: {}", hw1fCalibInitParam);
		
		projectionYear 	                 = Integer.parseInt(argInDBMap.getOrDefault("PROJECTION_YEAR", "100").toString());
		randGenMode                      = Integer.parseInt(argInDBMap.getOrDefault("RAND_GEN_MODE"  , "1"  ).toString());

		String randSeedBondStr           = argInDBMap.getOrDefault("RAND_SEED_BOND_DEF", "245").toString();  
		randSeedBond                     = Arrays.stream(randSeedBondStr.split(",")).map(s->s.trim()).map(Integer::parseInt).mapToInt(Integer::intValue).toArray();				
		String randSeedAsstStr           = argInDBMap.getOrDefault("RAND_SEED_ASST_DEF", "701").toString();  
		randSeedAsst                     = Arrays.stream(randSeedAsstStr.split(",")).map(s->s.trim()).map(Integer::parseInt).mapToInt(Integer::intValue).toArray();
		
		targetDuration	                 = Double.parseDouble(argInDBMap.getOrDefault("BOND_YIELD_TGT_DURATION", "3.0").toString());
		
//		jobList.stream().forEach(s -> log.info("Effective Job List : {}", s));
		log.info("Effective Job List : [{}]", jobList.toString());

//////////////////////////////////////////////////////////
		
		String hwAlphaPieceStr = argInDBMap.getOrDefault("HW1F_ALPHA_PIECE", "10").toString();
		String hwSigmaPieceStr = argInDBMap.getOrDefault("HW1F_SIGMA_PIECE", "1, 2, 3, 5, 7, 10").toString();
		hwAlphaPiece           = Arrays.stream(hwAlphaPieceStr.split(",")).map(s -> s.trim()).map(Integer::parseInt).mapToInt(Integer::intValue).toArray();
		hwSigmaPiece           = Arrays.stream(hwSigmaPieceStr.split(",")).map(s -> s.trim()).map(Integer::parseInt).mapToInt(Integer::intValue).toArray();				
		
		stBssd           = argInDBMap.getOrDefault("AFNS_START_DATE", "20100101").toString().trim().toUpperCase();
		afnsTenorListStr = argInDBMap.getOrDefault("AFNS_TENOR_LIST", "M0003,M0006,M0009,M0012,M0018,M0024,M0030,M0036,M0048,M0060,M0084,M0120,M0180,M0240").toString();
		afnsTenorList    = Arrays.stream(afnsTenorListStr.split(",")).map(s -> s.trim()).collect(Collectors.toList());
	}	

		
	private static void job100() {
		if (true) {			
			log.info("Job 0 : Pre-Process start !!!");
			session.beginTransaction();			

//			Job01_PreValidation.validateSwaptionVol(bssd);
//			Job01_PreValidation.validateUsedEsgModel();			
//			Job01_PreValidation.validateBottomUpIrCurve();			
//			Job01_PreValidation.validTransitionMatrix(bssd);
//			Job01_PreValidation.validTransitionMatrixSumEqualOne(bssd);	
			
			session.getTransaction().commit();
			log.info("Job 000: Validation  is Completed !!!");
		}		
	}	
	
	
	private static void job101() {
//		if (jobList.contains("101")) {
		if(true) {
			session.beginTransaction();		
			CoJobInfo jobLog = startJogLog(EJob.ESG101);
			
			try {
				irCurveMap    = IrCurveDao.getIrCurveList().stream().collect(Collectors.toMap(s->s.getIrCurveId(), Function.identity()));
				irCurveIdList = irCurveMap.keySet().stream().collect(Collectors.toList());				

				if(irCurveIdList.isEmpty()) {
					log.error("No Active Interest Rate Curve in [{}] Table", Process.toPhysicalName(IrCurve.class.getSimpleName()));
					System.exit(0);
				}
				else {
//					activeCurveList.forEach(s -> log.info("Active Interest Rate Curve: [{}]", s));  
					log.info("Active Interest Rate Curve: [{}]", irCurveIdList);
				}			
				
				int delNum = session.createQuery("delete IrParamSw a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				log.info("[{}] has been Deleted in Job:[{}] [BASE_YYMM: {}, COUNT: {}]", Process.toPhysicalName(IrParamSw.class.getSimpleName()), jobLog.getJobId(), bssd, delNum);				
				
				//TODO: IrParamSwDao의 getAppliedDate 는 applBizDv, irCurveId, irCurveSceNo가 모두 있어야 의미가 있다. 무결성에러가 리턴이 될것이니...그냥 현재처럼 구현해두자.
				List<IrParamSw> paramSwList = IrParamSwDao.getIrParamSwList(bssd, irCurveIdList);				
				paramSwList.stream().forEach(s -> session.saveOrUpdate(s));
//				paramSwList.stream().forEach(s -> log.info("{}", s));

				irCurveSwMap  = paramSwList.stream().filter(s -> s.getIrCurveSceNo().equals(1) && s.getApplBizDv().equals("KICS"))
						                            .collect(Collectors.toMap(IrParamSw::getIrCurveId, Function.identity()));

				for(IrParamSw irParamSw : paramSwList.stream().filter(s -> s.getIrCurveSceNo().equals(1) && !s.getApplBizDv().equals("KICS")).collect(Collectors.toList())) {
					irCurveSwMap.putIfAbsent(irParamSw.getIrCurveId(), irParamSw);
				}
//				log.info("irCurveSwMap: {}", irCurveSwMap);				
				
				if(irCurveSwMap.isEmpty()) {
					log.error("Check Smith-Wilson Attribute in [{}] Table", Process.toPhysicalName(IrParamSw.class.getSimpleName()));
					System.exit(0);
				}
				
				kicsSwMap = paramSwList.stream().filter(s -> s.getApplBizDv().equals("KICS"))
												.collect(Collectors.groupingBy(IrParamSw::getIrCurveId, TreeMap::new, Collectors.toMap(IrParamSw::getIrCurveSceNo, Function.identity(), (k, v) -> k, TreeMap::new)));
				
				ifrsSwMap = paramSwList.stream().filter(s -> s.getApplBizDv().equals("IFRS"))
	                                            .collect(Collectors.groupingBy(IrParamSw::getIrCurveId, TreeMap::new, Collectors.toMap(IrParamSw::getIrCurveSceNo, Function.identity(), (k, v) -> k, TreeMap::new)));
				
				ibizSwMap = paramSwList.stream().filter(s -> s.getApplBizDv().equals("IBIZ"))
												.collect(Collectors.groupingBy(IrParamSw::getIrCurveId, TreeMap::new, Collectors.toMap(IrParamSw::getIrCurveSceNo, Function.identity(), (k, v) -> k, TreeMap::new)));
				
				saasSwMap = paramSwList.stream().filter(s -> s.getApplBizDv().equals("SAAS"))
												.collect(Collectors.groupingBy(IrParamSw::getIrCurveId, TreeMap::new, Collectors.toMap(IrParamSw::getIrCurveSceNo, Function.identity(), (k, v) -> k, TreeMap::new)));				
				
//				log.info("kicsSwMap: {}", kicsSwMap);
//				log.info("saasSwMap: {}", saasSwMap);
				
//				if(kicsSwMap instanceof HashMap) log.info("HashMap: {}");
//				if(kicsSwMap instanceof TreeMap) log.info("TreeMap: {}");
//				if(kicsSwMap instanceof Map) log.info("Map: {}");				
//				for(Map.Entry<String, Map<String, IrParamSw>> curveSwMap : kicsSwMap.entrySet()) {
//					log.info("{}", curveSwMap);
//					if(curveSwMap.getValue() instanceof HashMap) log.info("HashMap: {}", curveSwMap.getKey());
//					if(curveSwMap.getValue() instanceof TreeMap) log.info("TreeMap: {}", curveSwMap.getKey());
//					if(curveSwMap.getValue() instanceof Map) log.info("Map: {}", curveSwMap.getKey());
//					
//					for(Map.Entry<String, IrParamSw> swSce : curveSwMap.getValue().entrySet()) {
//						log.info("{}", swSce.getClass());
//					}
//				}				
					
				session.flush();
				session.clear();				
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();			
		}
	}
	
	
	private static void job102() {
		if (jobList.contains("102")) {		
			session.beginTransaction();		
			CoJobInfo jobLog = startJogLog(EJob.ESG102);
			
			try {				
				for(Map.Entry<String, IrCurve> irCrv : irCurveMap.entrySet()) {					
				
					if(!irCurveSwMap.containsKey(irCrv.getKey())) {
						log.info("No Ir Curve Data [{}] in Smith-Wilson Map for [{}]", irCrv.getKey(), bssd);						
						continue;
					}

//					int delNum = session.createQuery("delete IrVolSwpn a where a.baseYymm = :param1 and a.irCurveId = :param2")
//										.setParameter("param1", bssd)				
//										.setParameter("param2", irCrv.getKey()).executeUpdate();
//		
//					log.info("[{}] has been Deleted in Job:[{}] [COUNT: {}]", Process.toPhysicalName(IrVolSwpn.class.getSimpleName()), jobLog.getJobId(), delNum);
					
//					List<IrVolSwpnUsr> swpnVolUsr = IrVolSwpnDao.getSwpnVolUsr(bssd, irCrv.getKey());
//					swpnVolUsr.stream().forEach(s -> log.info("{}", s.toString()));
//					
//					log.info("{}", hwSigmaPiece);
				}
				
				session.flush();
				session.clear();
				completeJob("SUCCESS", jobLog);
	
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}	
	

	//TODO: 110번이 결국 IrCurveSpot에 저장하는 부분인데... SAAS에 대한 중복되지 않는 금융채ID도 여기에서 같이 처리를 해주어야 한다. 
	//다만, 금융채 1번 시나리오는 일단 뽑아놓고 가야할듯함...결국 spot까지 만들고 transform한다고 보는게 맞음
	private static void job110() {
		if (jobList.contains("110")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG110);			
			
			try {
				for(Map.Entry<String, IrCurve> irCrv : irCurveMap.entrySet()) {
					if(!irCurveSwMap.containsKey(irCrv.getKey())) {
						log.info("No Ir Curve Data [{}] in Smith-Wilson Map for [{}]", irCrv.getKey(), bssd);						
						continue;
					}				
					
					//TODO: IrCurveYtmDao method중에서 ytm is not null 구문 나중에 지우기...!!!(사실 그냥 두어도 문제는 없고, 오히려 안전할 수 도 있음)
					IrCurveSpotDao.deleteIrCurveSpotMonth(bssd, irCrv.getKey());
					List<IrCurveYtm> ytmRstList = IrCurveYtmDao.getIrCurveYtmMonth(bssd, irCrv.getKey());					
					if(ytmRstList.size()==0) {
						log.warn("No Historical YTM Data exist for [{}, {}]", bssd, irCrv.getKey());
						continue;
					}					
					
					TreeMap<String, List<IrCurveYtm>> ytmRstMap = new TreeMap<String, List<IrCurveYtm>>();
					ytmRstMap = ytmRstList.stream().collect(Collectors.groupingBy(s -> s.getBaseDate(), TreeMap::new, Collectors.toList()));					
					
					for(Map.Entry<String, List<IrCurveYtm>> ytmRst : ytmRstMap.entrySet()) {						
						
//						log.info("ytmRst: {}, {}, {}, {}, {}, {}", ytmRst.getKey(), irCrv.getKey(), irCurveSwMap.get(irCrv.getKey()).getSwAlphaYtm(), irCurveSwMap.get(irCrv.getKey()).getFreq(), ytmRst.getValue(), ytmRst);
//						Esg110_YtmToSpotSw.createIrCurveSpot(ytmRst.getKey(), irCrv.getKey(), ytmRst.getValue()).forEach(s -> session.save(s));
						Esg110_YtmToSpotSw.createIrCurveSpot(ytmRst.getKey(), irCrv.getKey(), ytmRst.getValue(), 
								                             irCurveSwMap.get(irCrv.getKey()).getSwAlphaYtm(), irCurveSwMap.get(irCrv.getKey()).getFreq()).forEach(s -> session.save(s));
					}
					session.flush();
					session.clear();
				}
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);
			}			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();		
		}
	}
	

	private static void job111() {
		if (jobList.contains("111")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG111);			
			
			for(String irCrv : irCurveIdList) {
				try {				
					String stBssd = "200912";
					String edBssd = "202106";
					LocalDate sttDate = DateUtil.convertFrom(DateUtil.toEndOfMonth(stBssd));
					LocalDate endDate = DateUtil.convertFrom(DateUtil.toEndOfMonth(edBssd));
					int monthDiff = DateUtil.monthBetween(stBssd, edBssd);
					
					log.info("{}. {}, monthDiff: {}", sttDate, endDate, monthDiff);
					TreeSet<LocalDate> bssdList = new TreeSet<LocalDate>(); 
					
					for(int i=0; i<monthDiff+1; i++) {
						LocalDate curDate = sttDate.plusMonths(i);					
						bssdList.add(curDate);
					}
					
					log.info("{}", bssdList);				
					
					for(LocalDate date : bssdList) {
						
						String bssd1 = DateUtil.dateToString(date).substring(0, 6);
						IrCurveSpotDao.deleteIrCurveSpotMonth(bssd1, irCrv);		
						
						List<IrCurveYtm> ytmRstList = IrCurveYtmDao.getIrCurveYtmMonth(bssd1, irCrv);
						
						if(ytmRstList.size()==0) {
							log.warn("No Historical YTM Data exist for [{}, {}]", bssd, irCrv);
							continue;
						}
						
						TreeMap<String, List<IrCurveYtm>> ytmRstMap = new TreeMap<String, List<IrCurveYtm>>();
						ytmRstMap = ytmRstList.stream().collect(Collectors.groupingBy(s -> s.getBaseDate(), TreeMap::new, Collectors.toList()));
								
						for(Map.Entry<String, List<IrCurveYtm>> ytmRst : ytmRstMap.entrySet()) {
							Esg110_YtmToSpotSw.createIrCurveSpot(ytmRst.getKey(), irCrv, ytmRst.getValue()).forEach(s -> session.save(s));
						}				
					}				
					session.flush();
					session.clear();					
					completeJob("SUCCESS", jobLog);
					
				} catch (Exception e) {
					log.error("ERROR: {}", e);
					completeJob("ERROR", jobLog);
				}
			}			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();			
		}
	}	
	

	private static void job210() {
		if (jobList.contains("210")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG210);			

			try {
				for(Map.Entry<String, IrCurve> irCrv : irCurveMap.entrySet()) {
					if(!irCurveSwMap.containsKey(irCrv.getKey())) {
						log.info("No Ir Curve Data [{}] in Smith-Wilson Map for [{}]", irCrv.getKey(), bssd);						
						continue;
					}				

					//for manual TenorList, use afnsTenorList in global variable
					List<String> tenorList = IrCurveSpotDao.getIrCurveTenorList(bssd, irCrv.getKey(), irCurveSwMap.get(irCrv.getKey()).getLlp());
					log.info("TenorList in [{}]: ID: [{}], llp: [{}], matCd: {}", jobLog.getJobId(), irCrv.getKey(), irCurveSwMap.get(irCrv.getKey()).getLlp(), tenorList);
					
					if(tenorList.isEmpty()) {
						log.info("No Ir Curve Data [{}] at [{}] in Table [{}]", irCrv.getKey(), bssd, Process.toPhysicalName(IrCurveSpot.class.getSimpleName()));						
						continue;						
					}
					
					int delNum = session.createQuery("delete IrCurveSpotWeek a where a.baseDate >= :param1 and a.baseDate <= :param2 and a.irCurveId = :param3")
										.setParameter("param1", stBssd)
										.setParameter("param2", bssd+31)
							            .setParameter("param3", irCrv.getKey()).executeUpdate();				
					
					log.info("[{}] has been Deleted in Job:[{}] [IR_CURVE_ID: {}, COUNT: {}]", Process.toPhysicalName(IrCurveSpotWeek.class.getSimpleName()), jobLog.getJobId(), irCrv.getKey(), delNum);

					List<IrCurveSpotWeek> spotWeek = Esg210_SpotWeek.setupIrCurveSpotWeek(bssd, stBssd, irCrv.getKey(), tenorList);
					spotWeek.stream().forEach(s -> session.saveOrUpdate(s));					
					//TODO: 적당히 끊어서 flush clear 해서 속도 테스트 해보자!!!
					
					session.flush();
					session.clear();					
				}
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);
			}			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();			
		}		
	}	
	

    //TODO: 모수입력이냐 산출이냐에서 출발해야하나??? 일단 그렇게 하자...귀찮아도 그렇게 해야 나중에 손이 덜간다.
	private static void job220() {
		if (jobList.contains("220")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG220);			

			String model           = argInDBMap.getOrDefault("AFNS_MODE"         , "AFNS"    ).trim().toUpperCase();			
			double dt              = 1.0 /  Double. valueOf((String) argInDBMap.getOrDefault("AFNS_DT_DENOM" , "52"));			
			int    weekDay         = Integer.valueOf((String) argInDBMap.getOrDefault("AFNS_WEEK_DAY"        , "5"));
			double errorTolerance  = Double. valueOf((String) argInDBMap.getOrDefault("AFNS_ERROR_TOL"       , "0.00000001"));
			int    kalmanItrMax    = Integer.valueOf((String) argInDBMap.getOrDefault("AFNS_KALMAN_ITR_MAX"  , "100"));
			double confInterval    = Double. valueOf((String) argInDBMap.getOrDefault("AFNS_CONF_INTERVAL"   , "0.995"));
			double sigmaInit       = Double. valueOf((String) argInDBMap.getOrDefault("AFNS_SIGMA_INIT"      , "0.05"));
			double epsilonInit     = Double. valueOf((String) argInDBMap.getOrDefault("AFNS_EPSILON_INIT"    , "0.001"));			
			
			try {
				for(Map.Entry<String, IrCurve> irCrv : irCurveMap.entrySet()) {
					if(!irCurveSwMap.containsKey(irCrv.getKey())) {
						log.info("No Ir Curve Data [{}] in Smith-Wilson Map for [{}]", irCrv.getKey(), bssd);
						continue;
					}					
					
					log.info("AFNS Shock Spread (Cont) for [{}({}, {})]", irCrv.getKey(), irCrv.getValue().getIrCurveNm(), irCrv.getValue().getCurCd());
					
					List<String> tenorList = IrCurveSpotDao.getIrCurveTenorList(bssd, irCrv.getKey(), irCurveSwMap.get(irCrv.getKey()).getLlp());					
					log.info("TenorList in [{}]: ID: [{}], llp: [{}], matCd: {}", jobLog.getJobId(), irCrv.getKey(), irCurveSwMap.get(irCrv.getKey()).getLlp(), tenorList);
					if(tenorList.isEmpty()) {
						log.error("No Spot Rate Data [ID: {}] for [{}]", irCrv.getKey(), bssd);
						continue;
					}

					int delNum1 = session.createQuery("delete IrParamAfnsCalc a where baseYymm=:param1 and a.irModelId=:param2 and a.irCurveId=:param3")
		                     			 .setParameter("param1", bssd) 
		                     			 .setParameter("param2", model)
		                     			 .setParameter("param3", irCrv.getKey())
		                     			 .executeUpdate();
					
					log.info("[{}] has been Deleted in Job:[{}] [IR_CURVE_ID: {}, COUNT: {}]", Process.toPhysicalName(IrParamAfnsCalc.class.getSimpleName()), jobLog.getJobId(), irCrv.getKey(), delNum1);
					
					int delNum2 = session.createQuery("delete IrSprdAfnsCalc a where baseYymm=:param1 and a.irModelId=:param2 and a.irCurveId=:param3")
					                     .setParameter("param1", bssd) 
								  		 .setParameter("param2", model)
										 .setParameter("param3", irCrv.getKey())
										 .executeUpdate();					

					log.info("[{}] has been Deleted in Job:[{}] [IR_CURVE_ID: {}, COUNT: {}]", Process.toPhysicalName(IrSprdAfnsCalc.class.getSimpleName()), jobLog.getJobId(), irCrv.getKey(), delNum2);
					
					List<IrCurveSpotWeek> weekHisList = IrCurveSpotWeekDao.getIrCurveSpotWeekHis(bssd, stBssd, irCrv.getKey(), tenorList, weekDay, false);					
					log.info("weekHisList: {}, {}", irCrv.getKey(), weekHisList.size());					

					if(weekHisList.size() < 100) {
						log.error("Weekly SpotRate Data is not Enough [ID: {}, SIZE: {}] for [{}]", irCrv.getKey(), weekHisList.size(), bssd);
						continue;
					}					
					
					//TODO: 20110701 기준으로 수렴에 이슈사항이 발생함. 20120701 등으로 바꿔보면 문제가 없기에, 데이터의 문제로 판단됨. constraint를 포함한 objective function을 구성하니 해결은 되었음
					List<IrCurveSpot> curveHisList = weekHisList.stream().map(s->s.convertToHis()).collect(toList());
//					curveHisList = curveHisList.stream().filter(s -> Integer.valueOf(s.getBaseDate()) >= 20110701).collect(toList());					
					
//					List<IrDcntRate> irDcntRate = IrDcntRateDao.getIrDcntRateList(bssd, "KICS", irCrv.getKey(), "1", afnsTenorList);
//					List<IrCurveSpot> curveBaseList = irDcntRate.stream().map(s -> s.convert()).collect(toList());
					
					//TODO: IrDcntRate는 나중에 호출되므로 안전하게 대안을 찾는다. base spot rate를 넣어도 충격스프레드를 산출하는데는 영향을 미치지 않는다. 우선 이렇게 처리해두자.
//					List<IrCurveSpot> curveBaseList = IrDcntRateDao.getIrDcntRateList(bssd, "KICS", irCrv.getKey(), "1", afnsTenorList).stream().map(s -> s.convert()).collect(toList());
					List<IrCurveSpot> curveBaseList = IrCurveSpotDao.getIrCurveHis(bssd, irCrv.getKey(), tenorList);					
					
					if(curveBaseList.size()==0) {
						log.error("No IR Curve Data [IR_CURVE_ID: {}] for [{}]", irCrv.getKey(), bssd);
						continue;
					}
					
					Map<String, List<?>> irShockSenario = new TreeMap<String, List<?>>();					
					
					irShockSenario = Esg220_ShkSprdAfns.createAfnsShockScenario(FinUtils.toEndOfMonth(bssd), model, curveHisList, curveBaseList, tenorList, dt, sigmaInit
														                        , irCurveSwMap.get(irCrv.getKey()).getLtfr()
														                        , irCurveSwMap.get(irCrv.getKey()).getLtfrCp()
														                        , projectionYear
														                        , errorTolerance
														                        , kalmanItrMax
														                        , confInterval
														                        , epsilonInit);				

//					//for input Paras(currenty null)					
//					irShockSenario = Esg220_AfnsShkSprd.createAfnsShockScenarioByParam(FinUtils.toEndOfMonth(bssd), model, null, curveBaseList, afnsTenorList, dt, sigmaInit
//																                       , irCurveSwMap.get(irCrv.getKey()).getLtfr()
//																                       , irCurveSwMap.get(irCrv.getKey()).getLtfrCp()
//																                       , projectionYear
//																                       , errorTolerance
//																                       , kalmanItrMax
//																                       , confInterval
//																                       , epsilonInit);					
				
					for(Map.Entry<String, List<?>> rslt : irShockSenario.entrySet()) {						
//						if(!rslt.getKey().equals("CURVE")) rslt.getValue().forEach(s -> log.info("{}, {}", s.toString()));						
						rslt.getValue().forEach(s -> session.save(s));

						session.flush();
						session.clear();
					}
				}
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);
			}			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}	
	

	private static void job230() {
		if (jobList.contains("230")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG230);
			
			try {
				int delNum = session.createQuery("delete IrSprdLp a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				log.info("[{}] has been Deleted in Job:[{}] [BASE_YYMM: {}, COUNT: {}]", Process.toPhysicalName(IrSprdLp.class.getSimpleName()), jobLog.getJobId(), bssd, delNum);

				List<IrSprdLp> kicsSpread1 = Esg230_LpSprd.setLpFromSwMap(bssd, "KICS", kicsSwMap);
				kicsSpread1.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrSprdLp> ifrsSpread1 = Esg230_LpSprd.setLpFromSwMap(bssd, "IFRS", ifrsSwMap);
				ifrsSpread1.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrSprdLp> ibizSpread1 = Esg230_LpSprd.setLpFromSwMap(bssd, "IBIZ", ibizSwMap);
				ibizSpread1.stream().forEach(s -> session.saveOrUpdate(s));				
				
				List<IrSprdLp> saasSpread1 = Esg230_LpSprd.setLpFromSwMap(bssd, "SAAS", saasSwMap);
				saasSpread1.stream().forEach(s -> session.saveOrUpdate(s));
				
				String lpCurveId = argInDBMap.getOrDefault("LP_CURVE_ID", "5010110");
				
				List<IrSprdLp> kicsSpread2 = Esg230_LpSprd.setLpFromCrdSprd(bssd, "KICS", kicsSwMap, lpCurveId);
				kicsSpread2.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrSprdLp> ifrsSpread2 = Esg230_LpSprd.setLpFromCrdSprd(bssd, "IFRS", ifrsSwMap, lpCurveId);
				ifrsSpread2.stream().forEach(s -> session.saveOrUpdate(s));

				List<IrSprdLp> ibizSpread2 = Esg230_LpSprd.setLpFromCrdSprd(bssd, "IBIZ", ibizSwMap, lpCurveId);
				ibizSpread2.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrSprdLp> saasSpread2 = Esg230_LpSprd.setLpFromCrdSprd(bssd, "SAAS", saasSwMap, lpCurveId);
				saasSpread2.stream().forEach(s -> session.saveOrUpdate(s));
				
				
				session.flush();
				session.clear();
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);					
			}			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();			
		}			
	}	
		

	private static void job240() {
		if (jobList.contains("240")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG240);
			
			try {
				int delNum = session.createQuery("delete IrSprdLpBiz a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				log.info("[{}] has been Deleted in Job:[{}] [BASE_YYMM: {}, COUNT: {}]", Process.toPhysicalName(IrSprdLpBiz.class.getSimpleName()), jobLog.getJobId(), bssd, delNum);

				
				List<IrSprdLpBiz> kicsSpread = Esg240_BizLpSprd.setLpSprdBiz(bssd, "KICS", null);
				kicsSpread.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrSprdLpBiz> ifrsSpread = Esg240_BizLpSprd.setLpSprdBiz(bssd, "IFRS", null);
				ifrsSpread.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrSprdLpBiz> ibizSpread = Esg240_BizLpSprd.setLpSprdBiz(bssd, "IBIZ", null);
				ibizSpread.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrSprdLpBiz> saasSpread = Esg240_BizLpSprd.setLpSprdBiz(bssd, "SAAS", null);
				saasSpread.stream().forEach(s -> session.saveOrUpdate(s));
				
				
				session.flush();
				session.clear();
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);					
			}				
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();			
		}			
	}	

	//TODO: forward Curve Generation in Esg250
	private static void job250() {
		if (jobList.contains("250")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG250);
			
			try {
				int delNum = session.createQuery("delete IrDcntRateBu a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();				
				log.info("[{}] has been Deleted in Job:[{}] [BASE_YYMM: {}, COUNT: {}]", Process.toPhysicalName(IrDcntRateBu.class.getSimpleName()), jobLog.getJobId(), bssd, delNum);
				
				String irModelId = "AFNS";				

				List<IrDcntRateBu> kicsDcntRateBu = Esg250_IrDcntRateBu.setIrDcntRateBu(bssd, irModelId, "KICS",  kicsSwMap);				
				kicsDcntRateBu.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrDcntRateBu> ifrsDcntRateBu = Esg250_IrDcntRateBu.setIrDcntRateBu(bssd, irModelId, "IFRS", ifrsSwMap);
				ifrsDcntRateBu.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrDcntRateBu> ibizDcntRateBu = Esg250_IrDcntRateBu.setIrDcntRateBu(bssd, irModelId, "IBIZ", ibizSwMap);
				ibizDcntRateBu.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrDcntRateBu> saasDcntRateBu = Esg250_IrDcntRateBu.setIrDcntRateBu(bssd, irModelId, "SAAS", saasSwMap);
				saasDcntRateBu.stream().forEach(s -> session.saveOrUpdate(s));				
				
				session.flush();
				session.clear();
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);					
			}				
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();			
		}			
	}	


	private static void job260() {
		if (jobList.contains("260")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG260);
			
			try {
				int delNum = session.createQuery("delete IrDcntRate a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();				
				log.info("[{}] has been Deleted in Job:[{}] [BASE_YYMM: {}, COUNT: {}]", Process.toPhysicalName(IrDcntRate.class.getSimpleName()), jobLog.getJobId(), bssd, delNum);				
				
				List<IrDcntRate> kicsDcntRate = Esg260_IrDcntRate.createIrDcntRate(bssd, "KICS", kicsSwMap, projectionYear);				
				kicsDcntRate.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrDcntRate> ifrsDcntRate = Esg260_IrDcntRate.createIrDcntRate(bssd, "IFRS", ifrsSwMap, projectionYear);
				ifrsDcntRate.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrDcntRate> ibizDcntRate = Esg260_IrDcntRate.createIrDcntRate(bssd, "IBIZ", ibizSwMap, projectionYear);
				ibizDcntRate.stream().forEach(s -> session.saveOrUpdate(s));
				
				List<IrDcntRate> saasDcntRate = Esg260_IrDcntRate.createIrDcntRate(bssd, "SAAS", saasSwMap, projectionYear);
				saasDcntRate.stream().forEach(s -> session.saveOrUpdate(s));				
				
				session.flush();
				session.clear();
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);					
			}				
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();			
		}		
	}	
	
	//TODO: 여기서 변액자산수익률도 뽑아놓는다. 테이블은 IrDcntRateBiz일 필요는 없는데 그냥 그게 나을듯하기도 하고...일단 값검증후에 추후에 생각해보자.(ID는 KICS_V 정도로)
	private static void job270() {
		if (jobList.contains("270")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG270);
			
			try {
				int delNum = session.createQuery("delete IrDcntRateBiz a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();				
				log.info("[{}] has been Deleted in Job:[{}] [BASE_YYMM: {}, COUNT: {}]", Process.toPhysicalName(IrDcntRateBiz.class.getSimpleName()), jobLog.getJobId(), bssd, delNum);				
				
				//TODO:IrDcntRateUsr에 있는 값을 최우선적으로 적재하고, 없는 irCurveId에 대해 한정하여 아래의 명령을 실행한다.
				
				IrDcntRateDao.getIrDcntRateBizAdjSpotList (bssd, "KICS").forEach(s -> session.saveOrUpdate(s));
				IrDcntRateDao.getIrDcntRateBizBaseSpotList(bssd, "KICS").forEach(s -> session.saveOrUpdate(s));
				
				IrDcntRateDao.getIrDcntRateBizAdjSpotList (bssd, "IFRS").forEach(s -> session.saveOrUpdate(s));
				IrDcntRateDao.getIrDcntRateBizBaseSpotList(bssd, "IFRS").forEach(s -> session.saveOrUpdate(s));

				IrDcntRateDao.getIrDcntRateBizAdjSpotList (bssd, "IBIZ").forEach(s -> session.saveOrUpdate(s));
				IrDcntRateDao.getIrDcntRateBizBaseSpotList(bssd, "IBIZ").forEach(s -> session.saveOrUpdate(s));
				
				IrDcntRateDao.getIrDcntRateBizAdjSpotList (bssd, "SAAS").forEach(s -> session.saveOrUpdate(s));
				IrDcntRateDao.getIrDcntRateBizBaseSpotList(bssd, "SAAS").forEach(s -> session.saveOrUpdate(s));				
				
				session.flush();
				session.clear();
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);					
			}				
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();			
		}		
	}	
	
	//TODO: Blank JOB
	private static void job280() {
		if (jobList.contains("280")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG280);
			try {				
//				List<String> tenorList = IrCurveSpotDao.getIrCurveTenorList(bssd, "1010000", 20);				
//				List<IrVolSwpn> swpnVolList = IrVolSwpnDao.getSwpnVol(bssd, "1010000");
//				List<IrCurveSpot> adjSpotRate = IrDcntRateDao.getIrDcntRateBuToAdjSpotList(bssd, "KICS", "1010000", "1");
//				List<IrParamHwBiz> paramHw = IrParamHwDao.getIrParamHwBizList(bssd, "KICS", "HW1F", "1");
				
				session.flush();
				session.clear();
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();			
		}	
	}		
	
 
	private static void job310() {
		if (jobList.contains("310")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG310);
			
			String mode = argInDBMap.getOrDefault("HW_MODE", "HW1F").trim().toUpperCase();			
			
			try {
				for(Map.Entry<String, IrCurve> irCrv : irCurveMap.entrySet()) {
					if(!irCurveSwMap.containsKey(irCrv.getKey())) {
						log.info("No Ir Curve Data [{}] in Smith-Wilson Map for [{}]", irCrv.getKey(), bssd);					
						continue;
					}				
										
					List<String> tenorList = IrCurveSpotDao.getIrCurveTenorList(bssd, irCrv.getKey(), irCurveSwMap.get(irCrv.getKey()).getLlp());					
					log.info("TenorList in [{}]: ID: [{}], llp: [{}], matCd: {}", jobLog.getJobId(), irCrv.getKey(), irCurveSwMap.get(irCrv.getKey()).getLlp(), tenorList);					
					if(tenorList.isEmpty()) {
						log.error("No Spot Rate Data [ID: {}] for [{}]", irCrv.getKey(), bssd);
						continue;
					}
					
					int delNum = session.createQuery("delete IrParamHwCalc a where baseYymm=:param1 and a.irModelId=:param2 and a.irCurveId=:param3")
										.setParameter("param1", bssd) 
		                     			.setParameter("param2", mode)
		                     			.setParameter("param3", irCrv.getKey())
		                     			.executeUpdate();
					
					log.info("[{}] has been Deleted in Job:[{}] [IR_CURVE_ID: {}, COUNT: {}]", Process.toPhysicalName(IrParamHwCalc.class.getSimpleName()), jobLog.getJobId(), irCrv.getKey(), delNum);					

					List<IrCurveSpot> spotList = IrCurveSpotDao.getIrCurveHis(bssd, irCrv.getKey(), tenorList);
					
					log.info("SPOT RATE: [ID: {}], [SIZE: {}]", irCrv.getKey(), spotList.size());					
					if(spotList.size()==0) {
						log.error("No IR Curve Data [IR_CURVE_ID: {}] for [{}]", irCrv.getKey(), bssd);
						continue;
//						throw new Exception("No IR Curve Data [IR_CURVE_ID: " + irCrv.getKey() + "]");
					}

					List<IrVolSwpn> swpnVolList = IrVolSwpnDao.getSwpnVol(bssd, irCrv.getKey());
					
					log.info("SWAPNTION VOL: [ID: {}], [SIZE: {}]", irCrv.getKey(), swpnVolList.size());
					if(swpnVolList.size()==0) {
						log.error("No SWAPTION VOL Data [IR_CURVE_ID: {}] for [{}]", irCrv.getKey(), bssd);
						continue;
//						throw new Exception("No SWAPTION VOL Data [IR_CURVE_ID: " + irCrv.getKey() + "]");
					}

					//TODO: 초기모수 조정, 스왑션변동성 조정등의 여지를 남겨두자. 결국 건드려야할 것은 irModelId 뿐이다. HW1F_IV1, HW1F_VP1 ...
					//hw1fCalibrationKics는 full constructor는 freq 와 prjInterval 두개다 입력되게 되어있다. 빌드패턴으로 정리하면서 이것도 함께 처리하자.
					//Cost Value도 COST라는 param으로 찍어두자...그리고 유효성 검증할때도 이것저것 하기는 해야 한다.
					Integer freq = irCurveSwMap.get(irCrv.getKey()).getFreq();
					log.info("freq: {}", freq);
					double errTol = 1e-8;
					double[] hwInitParam = hw1fCalibInitParam;

					Esg310_ParamHw1f.createParamHw1f(bssd, mode, irCrv.getKey(), spotList, swpnVolList, hwInitParam, freq, errTol, hwAlphaPiece, hwSigmaPiece).forEach(s -> session.save(s));
//					Esg310_CalcHw1fParam.createParamHw1f(bssd, mode, irCrv.getKey(), spotList, swpnVolList, hwInitParam, freq, errTol).forEach(s -> session.save(s));

					session.flush();
					session.clear();	
				}
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);				
			}			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}			
	}
	
	//TODO: validation slot!!!
	private static void job320() {
		if (jobList.contains("320")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG320);
			try {				
//				List<String> tenorList = IrCurveSpotDao.getIrCurveTenorList(bssd, "1010000", 20);				
//				List<IrVolSwpn> swpnVolList = IrVolSwpnDao.getSwpnVol(bssd, "1010000");
//				List<IrCurveSpot> adjSpotRate = IrDcntRateDao.getIrDcntRateBuToAdjSpotList(bssd, "KICS", "1010000", "1");
//				List<IrParamHwBiz> paramHw = IrParamHwDao.getIrParamHwBizList(bssd, "KICS", "HW1F", "1");
				
				session.flush();
				session.clear();
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();			
		}	
	}	

	//TODO: 일단 동일한 내용을 KICS IFRS IBIZ에 넣는다. KICS만 10년평균으로 하고, 나머지는 CALC를 그대로 사용할 수 도있으나, alpha가 작아서 현실적으로 의미가 없다. 
	private static void job330() {
		if (jobList.contains("330")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG330);
			
			String model = argInDBMap.getOrDefault("HW_MODE", "HW1F").trim().toUpperCase();			
			
			try {
				for(Map.Entry<String, IrCurve> irCrv : irCurveMap.entrySet()) {
					if(!irCurveSwMap.containsKey(irCrv.getKey())) {
						log.info("No Ir Curve Data [{}] in Smith-Wilson Map for [{}]", irCrv.getKey(), bssd);
						continue;
					}				
					
					int delNum = session.createQuery("delete IrParamHwBiz a where baseYymm=:param1 and a.irModelId=:param2 and a.irCurveId=:param3")
										.setParameter("param1", bssd) 
		                     			.setParameter("param2", model)
		                     			.setParameter("param3", irCrv.getKey())
		                     			.executeUpdate();
					
					log.info("[{}] has been Deleted in Job:[{}] [IR_CURVE_ID: {}, COUNT: {}]", Process.toPhysicalName(IrParamHwBiz.class.getSimpleName()), jobLog.getJobId(), irCrv.getKey(), delNum);	
					
					int hwAlphaAvgNum = -1 * Integer.parseInt(argInDBMap.getOrDefault("HW_ALPHA_AVG_NUM", "120").toString());
					int hwSigmaAvgNum = -1 * Integer.parseInt(argInDBMap.getOrDefault("HW_SIGMA_AVG_NUM", "120").toString());
					
					String hwAlphaAvgMatCd = argInDBMap.getOrDefault("HW_ALPHA_AVG_MAT_CD", "M0240").trim().toUpperCase();
					String hwSigmaAvgMatCd = argInDBMap.getOrDefault("HW_SIGMA_AVG_MAT_CD", "M0120").trim().toUpperCase();
					
					Esg330_BizParamHw1f.createBizHw1fParam(bssd, "KICS", model, irCrv.getKey(), hwAlphaAvgNum, hwAlphaAvgMatCd, hwSigmaAvgNum, hwSigmaAvgMatCd).forEach(s -> session.save(s));
					Esg330_BizParamHw1f.createBizHw1fParam(bssd, "IFRS", model, irCrv.getKey(), hwAlphaAvgNum, hwAlphaAvgMatCd, hwSigmaAvgNum, hwSigmaAvgMatCd).forEach(s -> session.save(s));
					Esg330_BizParamHw1f.createBizHw1fParam(bssd, "IBIZ", model, irCrv.getKey(), hwAlphaAvgNum, hwAlphaAvgMatCd, hwSigmaAvgNum, hwSigmaAvgMatCd).forEach(s -> session.save(s));
					Esg330_BizParamHw1f.createBizHw1fParam(bssd, "SAAS", model, irCrv.getKey(), hwAlphaAvgNum, hwAlphaAvgMatCd, hwSigmaAvgNum, hwSigmaAvgMatCd).forEach(s -> session.save(s));

					session.flush();
					session.clear();					
				}				
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);
			}				
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}	
	}	
	

	private static void job340() {
		if (jobList.contains("340")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG340);			
			
			String irModelId = argInDBMap.getOrDefault("HW_MODE", "HW1F").trim().toUpperCase();
			
			Map<String, Object> param = new HashMap<String, Object>();		
			param.put("irModelId", irModelId);
			param.put("useYn", EBoolean.Y);			

			List<IrParamModel> modelMst = DaoUtil.getEntities(IrParamModel.class, param);
			Map<String, IrParamModel> modelMstMap = modelMst.stream().collect(Collectors.toMap(IrParamModel::getIrCurveId, Function.identity()));				
			log.info("{}", modelMstMap.toString());
			
			Map<String, Map<String, Map<Integer, IrParamSw>>> totalSwMap = new HashMap<String, Map<String, Map<Integer, IrParamSw>>>();
			totalSwMap.put("KICS",  kicsSwMap);
			totalSwMap.put("IFRS",  ifrsSwMap);
			totalSwMap.put("IBIZ",  ibizSwMap);
			totalSwMap.put("SAAS",  saasSwMap);
										
			try {				
				int delNum = session.createQuery("delete IrDcntSceStoBiz a where a.baseYymm=:param1 and a.irModelId=:param2")						
									.setParameter("param1", bssd) 
									.setParameter("param2", irModelId)
									.executeUpdate();

//				String query = " delete " + schema + ".E_IR_DCNT_SCE_STO_BIZ partition (PT_E" + bssd + ") " 
//						     + "  where BASE_YYMM=:param1 and IR_MODEL_ID=:param2 "
//				             ;
//				
//				int delNum = session.createNativeQuery(query)
//									.setParameter("param1", bssd) 
//									.setParameter("param2", irModelId)
//									.executeUpdate();								

				log.info("[{}] has been Deleted in Job:[{}] [COUNT: {}]", Process.toPhysicalName(IrDcntSceStoBiz.class.getSimpleName()), jobLog.getJobId(), delNum);


				int delNum2 = session.createQuery("delete IrParamHwRnd a where baseYymm=:param1 and a.irModelId=:param2")						
									 .setParameter("param1", bssd) 
									 .setParameter("param2", irModelId)
									 .executeUpdate();					

//				String query2 = " delete " + schema + ".E_IR_PARAM_HW_RND partition (PT_E" + bssd + ") " 
//							  + "  where BASE_YYMM=:param1 and IR_MODEL_ID=:param2 "
//							  ;
//				
//				int delNum2 = session.createNativeQuery(query2)
//									 .setParameter("param1", bssd) 
//									 .setParameter("param2", irModelId)
//									 .executeUpdate();								

				log.info("[{}] has been Deleted in Job:[{}] [COUNT: {}]", Process.toPhysicalName(IrParamHwRnd.class.getSimpleName()), jobLog.getJobId(), delNum2);

				for(Map.Entry<String, Map<String, Map<Integer, IrParamSw>>> biz : totalSwMap.entrySet()) {
					
					for(Map.Entry<String, Map<Integer, IrParamSw>> curveSwMap : biz.getValue().entrySet()) {
						for(Map.Entry<Integer, IrParamSw> swSce : curveSwMap.getValue().entrySet()) {
							
//							if(!biz.getKey().equals("KICS") || !swSce.getKey().equals(1)) continue;
							log.info("[{}] BIZ: [{}], IR_CURVE_ID: [{}], IR_CURVE_SCE_NO: [{}]", jobLog.getJobId(), biz.getKey(), curveSwMap.getKey(), swSce.getKey());
							Map<String, List<?>> hw1fResult = Esg340_BizScenHw1f.createScenHw1f(bssd, biz.getKey(), irModelId, curveSwMap.getKey(), swSce.getKey(), biz.getValue(), modelMstMap, projectionYear);
						
							@SuppressWarnings("unchecked")
							List<IrDcntSceStoBiz> stoBizList = (List<IrDcntSceStoBiz>) hw1fResult.get("SCE");				
							@SuppressWarnings("unchecked")
							List<IrParamHwRnd>    randHwList = (List<IrParamHwRnd>) hw1fResult.get("RND");				
							
							int sceCnt = 1;
							for (IrDcntSceStoBiz sce : stoBizList) {						
								session.save(sce);
								if (sceCnt % 50 == 0) {
									session.flush();
									session.clear();
								}
								if (sceCnt % logSize == 0) {
									log.info("Stochastic TermStructure of [{}] [BIZ: {}, ID: {}, SCE: {}] is processed {}/{} in Job:[{}]", irModelId, biz.getKey(), curveSwMap.getKey(), swSce.getKey(), sceCnt, stoBizList.size(), jobLog.getJobId());
								}
								sceCnt++;
							}					
							
							if(biz.getKey().equals("KICS")) {
								int rndCnt = 1;
								for (IrParamHwRnd rnd : randHwList) {
									session.save(rnd);
									if (rndCnt % 50 == 0) {
										session.flush();
										session.clear();
									}
									if (rndCnt % logSize == 0) {
										log.info("Stochastic Random Number of [{}] [BIZ: {}, ID: {}, SCE: {}] is processed {}/{} in Job:[{}]", irModelId, biz.getKey(), curveSwMap.getKey(), swSce.getKey(), rndCnt, randHwList.size(), jobLog.getJobId());
									}
									rndCnt++;
								}
							}
							
							session.flush();
							session.clear();								
						}
					}
				}
				completeJob("SUCCESS", jobLog);	
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);
			}			
			session.saveOrUpdate(jobLog);					
			session.getTransaction().commit();
		}	
	}	
	
	
	private static void job370() {
		if (jobList.contains("370")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG370);			
			
			String irModelId = argInDBMap.getOrDefault("HW_MODE", "HW1F").trim().toUpperCase();
			
			Map<String, Object> param = new HashMap<String, Object>();		
			param.put("irModelId", irModelId);
			param.put("useYn", EBoolean.Y);			

			List<IrParamModel> modelMst = DaoUtil.getEntities(IrParamModel.class, param);
			Map<String, IrParamModel> modelMstMap = modelMst.stream().collect(Collectors.toMap(IrParamModel::getIrCurveId, Function.identity()));				
			log.info("{}", modelMstMap.toString());
			
			Map<String, Map<String, Map<Integer, IrParamSw>>> totalSwMap = new HashMap<String, Map<String, Map<Integer, IrParamSw>>>();
			totalSwMap.put("KICS",  kicsSwMap);
			totalSwMap.put("IFRS",  ifrsSwMap);
			totalSwMap.put("IBIZ",  ibizSwMap);
			totalSwMap.put("SAAS",  saasSwMap);

			try {				
				int delNum = session.createQuery("delete StdAsstIrSceSto a where a.baseYymm=:param1")						
									.setParameter("param1", bssd)
									.executeUpdate();

				log.info("[{}] has been Deleted in Job:[{}] [COUNT: {}]", Process.toPhysicalName(StdAsstIrSceSto.class.getSimpleName()), jobLog.getJobId(), delNum);

				for(Map.Entry<String, Map<String, Map<Integer, IrParamSw>>> biz : totalSwMap.entrySet()) {
					
					for(Map.Entry<String, Map<Integer, IrParamSw>> curveSwMap : biz.getValue().entrySet()) {
						for(Map.Entry<Integer, IrParamSw> swSce : curveSwMap.getValue().entrySet()) {
							
							log.info("[{}] BIZ: [{}], IR_CURVE_ID: [{}], IR_CURVE_SCE_NO: [{}]", jobLog.getJobId(), biz.getKey(), curveSwMap.getKey(), swSce.getKey());
							List<StdAsstIrSceSto> bondYieldList = Esg370_BizBondYieldHw1f.createBondYieldHw1f(bssd, biz.getKey(), irModelId, curveSwMap.getKey(), swSce.getKey(), biz.getValue(), modelMstMap, projectionYear, targetDuration);									
						
							int sceCnt = 1;
							for (StdAsstIrSceSto sce : bondYieldList) {						
								session.save(sce);
								if (sceCnt % 50 == 0) {
									session.flush();
									session.clear();
								}
								if (sceCnt % logSize == 0) {
									log.info("Stochastic Bond Yield of [{}] [BIZ: {}, ASST: {}, SCE: {}] is processed {}/{} in Job:[{}]", irModelId, biz.getKey(), curveSwMap.getKey(), swSce.getKey(), sceCnt, bondYieldList.size(), jobLog.getJobId());
								}
								sceCnt++;
							}							
							
							session.flush();
							session.clear();								
						}
					}
				}
				completeJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);					
			session.getTransaction().commit();
		}	
	}	

	
	private static void job810() {
		if (jobList.contains("810")) {		
			session.beginTransaction();		
			CoJobInfo jobLog = startJogLog(EJob.ESG810);
			
			try {
				int delNum = session.createQuery("delete RcCorpPd a where a.baseYymm = :param1")
									.setParameter("param1", bssd).executeUpdate();										
	
				log.info("[{}] has been Deleted in Job:[{}] [COUNT: {}]", Process.toPhysicalName(RcCorpPd.class.getSimpleName()), jobLog.getJobId(), delNum);
				
//				List<RcCorpTm> tmList = RcCorpPdDao.getRcCorpTm(bssd);
//				tmList.stream().forEach(s -> log.info("{}", s.toString()));				
							
				List<TransMat> tm  = RcCorpPdDao.getRcCorpTm(bssd).stream().map(s-> TransMat.convertFrom(s)).collect(toList());			
				
				TreeMap<Integer, Map<Integer, Double>> tmMap = new TreeMap<Integer, Map<Integer, Double>>();
				tmMap = tm.stream().collect(Collectors.groupingBy(s -> s.getFromGrdOrder(), TreeMap::new, Collectors.toMap(TransMat::getToGrdOrder, TransMat::getTransProb, (k, v) -> k, TreeMap::new)));								
				log.info("{}", tmMap);

				int[] fromGrd = tmMap.keySet().stream().mapToInt(Integer::intValue).toArray();						
				int[] toGrd   = tmMap.firstEntry().getValue().keySet().stream().mapToInt(Integer::intValue).toArray();
				
				
//					
//					this.swpnMat      = volMap.keySet().stream().mapToInt(Integer::intValue).toArray();
//					this.swapTenor    = volMap.firstEntry().getValue().keySet().stream().mapToInt(Integer::intValue).toArray();
//					this.swapMatTenor = new int[this.swpnMat.length][this.swapTenor.length];
//					this.swpnVolMkt   = new double[this.swpnMat.length][this.swapTenor.length];
//					
//					int mat = 0;
//					for(Map.Entry<Integer, Map<Integer, Double>> volArg : volMap.entrySet()) {
//						int ten = 0;			
//						for(Map.Entry<Integer, Double> arg : volArg.getValue().entrySet()) {
//							this.swapMatTenor[mat][ten] = volArg.getKey() + arg.getKey();
//							this.swpnVolMkt[mat][ten] = arg.getValue();				
//							ten++;
//						}
//						mat++;
//					}						
				
				session.flush();
				session.clear();
				completeJob("SUCCESS", jobLog);
	
			} catch (Exception e) {
				log.error("ERROR: {}", e);
				completeJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}		
	
	private static void job81() {
		cnt = 0;
		if (jobList.contains("81")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG810);
			try {
				session.createQuery("delete CorpCumPd a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				session.createQuery("delete CorpCrdGrdPd a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				Job81_CorporatePd.createCorpPd(bssd).stream().forEach(s -> session.save(s));
				Job81_CorporatePd.createCorpCumPd(bssd).stream().forEach(s -> session.save(s));
				completeJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completeJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void job82() {
		cnt = 0;
		if (jobList.contains("82")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG82);
			try {
				session.createQuery("delete BizCorpPd a where a.baseYymm=:param and a.applyBizDv=:param2").setParameter("param", bssd).setParameter("param2", "I").executeUpdate();
				
				Job82_BizCorpPd.getBizCorpPdFromCumPd(bssd, "I").stream().forEach(s ->session.saveOrUpdate(s));
				completeJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completeJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void job83() {
		if (jobList.contains("83")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG83);
			try {
				session.createQuery("delete BizCorpPd a where a.baseYymm=:param and a.applyBizDv=:param2").setParameter("param", bssd).setParameter("param2", "K").executeUpdate();
				Job82_BizCorpPd.getBizCorpPdFromCumPd(bssd, "K").stream().forEach(s ->session.saveOrUpdate(s));
				completeJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completeJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void job33() {
		if (jobList.contains("33")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG33);
			try {
				String bizDv ="I";
				
				_StdAssetDao.getStdStockYield(bssd, bizDv).forEach(s->saveOrUpdate(s));
				
				completeJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completeJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job34() {
		if (jobList.contains("34")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG34);
			try {
				session.createQuery("delete BizStockYield a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				
//				List<String> assetList = StdAssetDao.getStdAssetMst().stream()
//													.filter(s->s.getCurCd().equals("KRW"))
//													.map(StdAssetMst::getStdAsstCd).collect(toList());
				
//				Map<String, List<String>> assetMap = StdAssetDao.getStdAssetMst().stream()
//															.collect(groupingBy(StdAssetMst::getCurCd, mapping(StdAssetMst::getStdAsstCd, toList())));
				Map<String, List<StdAsst>> assetMap = _StdAssetDao.getStdAssetMst().stream()
																.collect(groupingBy(StdAsst::getCurCd, toList()));
															
//													
				
				ifrsCurveMap.values().stream().flatMap(s-> Job34_EsgDetermieSce.createDetermineSce(bssd, s, assetMap.get(s.getCurCd()))).forEach(s->saveOrUpdate(s));
				kicsCurveMap.values().stream().flatMap(s-> Job34_EsgDetermieSce.createDetermineSce(bssd, s, assetMap.get(s.getCurCd()))).forEach(s->saveOrUpdate(s));
				
				completeJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completeJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void job34A() {
		if (jobList.contains("34A")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG34);
			try {
				session.createQuery("delete BizStockYield a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				
//				List<String> assetList = StdAssetDao.getStdAssetMst().stream()
//													.filter(s->s.getCurCd().equals("KRW"))
//													.map(StdAssetMst::getStdAsstCd).collect(toList());
				
//				Map<String, List<String>> assetMap = StdAssetDao.getStdAssetMst().stream()
//															.collect(groupingBy(StdAssetMst::getCurCd, mapping(StdAssetMst::getStdAsstCd, toList())));
				Map<String, List<StdAsst>> assetMap = _StdAssetDao.getStdAssetMst().stream()
																.collect(groupingBy(StdAsst::getCurCd, toList()));
															
//													
				
				ifrsCurveMap.values().stream().flatMap(s-> Job34_EsgDetermieSce.createDetermineSce1(bssd,  s, assetMap.get(s.getCurCd()))).forEach(s->saveOrUpdate(s));
				kicsCurveMap.values().stream().flatMap(s-> Job34_EsgDetermieSce.createDetermineSce1(bssd, s, assetMap.get(s.getCurCd()))).forEach(s->saveOrUpdate(s));
				
				completeJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completeJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void job35() {
		if (jobList.contains("35")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG35);
			try {
				String bizDv ="I";
				List<String> entryList = new ArrayList<String>();
				entryList.add("KOSPI200");
				
				int deleteNum = session.createQuery("delete BizStockSce a where a.baseYymm=:param and a.applBizDv=:bizDv and a.stdAsstCd=:stdAsstCd")
										.setParameter("param", bssd)
										.setParameter("bizDv", bizDv)
										.setParameter("stdAsstCd", "KOSPI200")
										.executeUpdate();
				
				log.info("Delete Data : {} for Job 35", deleteNum );
				
				Map<String, Map<Integer, Double>> driftMap = Job30_EsgStockScenario.createDrift3(bssd, bizDv, entryList, ifrsCurveMap, projectionYear);
				Map<String, Map<Integer, Double>> sigmaMap = Job30_EsgStockScenario.createSigma2(bssd, bizDv, entryList, projectionYear);

				Map<String, Double> asstHisMap = new HashMap<String, Double>();									// 산출기준일 종가 S0
				List<StdAsst> assetList = _StdAssetDao.getStdAssetMst().stream()
														 .filter(s->s.getStdAsstCd().equals("KOSPI200"))
														 .collect(toList());
				
//				log.info("assetList: {}, {}, {}", assetList.size(), assetList.get(0).getStdAsstCd(), assetList.get(0).getStdAsstTypCd());
				log.info("zzzz :  {},{}", driftMap.get("KOSPI200").size(), sigmaMap.get("KOSPI200").size());
//				driftMap.entrySet().forEach(s-> log.info("aaa : {},{}", s.getKey(), s.getValue()));
				
				double prevStockSce=0.0;
				double currStockSce=0.0;
				double yieldSce =0.0;
				int k=0;
				double dt = 1/12.0;
				
				for(StdAsst asset : assetList) {
					String mvId =asset.getStdAsstCd();
					if(asset.getStdAsstTypCd().equals("STOCK")){
//						log.info("{}, {}", asset.getStdAsstCd());
//						log.info("{}, {}", driftMap);
//						log.info("{}, {}", sigmaMap);
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), _StdAssetDao.getStdStockAssetHis(bssd,mvId).getStdAsstPrice());
					}else if(asset.getStdAsstTypCd().equals("BOND") && !asset.getStdAsstCd().equals("KTB")){
						log.info("aaaa : {}", asset.getStdAsstCd());
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), _StdAssetDao.getStdBondAssetHis(bssd, mvId).getIntRate());
					}
					else if(asset.getStdAsstTypCd().equals("SHORT_RATE")) {
//						asstHisMap.putIfAbsent(asset.getStdAsstCd(), IrCurveHisDao.getShortRateHis(bssd, "1010000").getIntRate());
//						asstHisMap.putIfAbsent(asset.getStdAsstCd(), _BottomupDcntDao.getShortRateHis(bssd, "RF_KRW_KICS").getIntRate());
					}
				}
				
				log.info("{}", asstHisMap);
				
				String assetCd = "KOSPI200";
				
				if(!driftMap.containsKey(assetCd)) {
					log.error("Error: No Drift Parameter for {}", assetCd);
					System.exit(1);
				}
				if(!sigmaMap.containsKey(assetCd)) {
					log.error("Error: No Sigma Parameter for {}", assetCd);
					System.exit(1);
				}				
				
				int seedNum = randSeedAsst[0];
				log.info("seedNum: {}", seedNum);
				
//				LogNormal4j logNormal = new LogNormal4j(bssd, driftMap.get(assetCd), sigmaMap.get(assetCd), projectionYear*12, batchNum);
				LogNormal4j logNormal = new LogNormal4j(bssd, driftMap.get(assetCd), sigmaMap.get(assetCd), projectionYear*12, batchNum, seedNum);
				
				session.createQuery("delete EsgRandom a where a.baseYymm=:param  and a.stdAsstCd=:type and a.volCalcId=:id")
 			           .setParameter("param", bssd).setParameter("type", assetCd).setParameter("id", assetCd).executeUpdate();
				
				List<IrParamHwRnd> rnd = logNormal.getRandomScenList();
			
				for(IrParamHwRnd r : rnd) {
					r.setIrModelId(assetCd);
					r.setIrCurveId(assetCd);
				}				
				
//				List<CompletableFuture<List<StdAsstIrSceSto>>> 	sceJobFutures =	
//						IntStream.rangeClosed(1,batchNum)
//						.mapToObj(batch-> CompletableFuture.supplyAsync(() ->  logNormal.getBizStockScenario(bssd, assetCd , bizDv, batch), exe))
//						.collect(Collectors.toList());
//				
//				List<StdAsstIrSceSto> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
				
				List<StdAsstIrSceSto> rst = null;
				
				log.info("rst Size :  {},{}", rst.size());
				
				int sceCnt = 1;
				for (StdAsstIrSceSto bb :rst) {
//					log.info("zzzz : {}", bb.toString());
					session.save(bb);
					if (sceCnt % 50 == 0) {
						session.flush();
						session.clear();
					}
					if (sceCnt % flushSize == 0) {
//						log.info("Biz Bottom Scenario for {}  is processed {}/{} in Job 24A {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						log.info("BizStockSce Scenario for {}  is processed {}/{} in Job 35 {}", sceCnt, batchNum * 100 * projectionYear * 12);
					}
					sceCnt = sceCnt + 1;
				}				
				
				int sceCnt2 = 1;
				for (IrParamHwRnd rr :rnd) {
					session.save(rr);
					if (sceCnt2 % 50 == 0) {
						session.flush();
						session.clear();
					}
					if (sceCnt2 % flushSize == 0) {
						log.info("ESG Random Number for {}  is processed {}/{} in Job 35 {}", sceCnt2, batchNum * 100 * projectionYear * 12);
					}
					sceCnt2 = sceCnt2 + 1;
				}					

				completeJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				e.printStackTrace();
				log.warn("Error : {}", e);
				completeJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
		
	private static void job36() {
		if (jobList.contains("36")) {
			session.beginTransaction();
			CoJobInfo jobLog = startJogLog(EJob.ESG36);
			try {
				String bizDv ="K";
				List<String> entryList = new ArrayList<String>();
				entryList.add("KOSPI200");
				
				int deleteNum = session.createQuery("delete BizStockSce a where a.baseYymm=:param and a.applBizDv=:bizDv and a.stdAsstCd=:stdAsstCd")
										.setParameter("param", bssd)
										.setParameter("bizDv", bizDv)
										.setParameter("stdAsstCd", "KOSPI200")
										.executeUpdate();
				
				log.info("Delete Data : {} for Job 36", deleteNum );
				
//				Map<String, Map<Integer, Double>> driftMap = Job30_EsgStockScenario.createDrift3(bssd, bizDv, entryList, bottomUpMap, projectionYear);
				Map<String, Map<Integer, Double>> driftMap = Job30_EsgStockScenario.createDrift3(bssd, bizDv, entryList, kicsCurveMap, projectionYear);
				Map<String, Map<Integer, Double>> sigmaMap = Job30_EsgStockScenario.createSigma2(bssd, bizDv, entryList, projectionYear);

				Map<String, Double> asstHisMap = new HashMap<String, Double>();									// 산출기준일 종가 S0
				List<StdAsst> assetList = _StdAssetDao.getStdAssetMst().stream()
														 .filter(s->s.getStdAsstCd().equals("KOSPI200"))
														 .collect(toList());
				
				log.info("zzzz :  {},{}", driftMap.get("KOSPI200").size(), sigmaMap.get("KOSPI200").size());
//				driftMap.entrySet().forEach(s-> log.info("aaa : {},{}", s.getKey(), s.getValue()));
				
				double prevStockSce=0.0;
				double currStockSce=0.0;
				double yieldSce =0.0;
				int k=0;
				double dt = 1/12.0;
				
				for(StdAsst asset : assetList) {
					String mvId =asset.getStdAsstCd();
					if(asset.getStdAsstTypCd().equals("STOCK")){							
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), _StdAssetDao.getStdStockAssetHis(bssd,mvId).getStdAsstPrice());
					}else if(asset.getStdAsstTypCd().equals("BOND") && !asset.getStdAsstCd().equals("KTB")){
						log.info("aaaa : {}", asset.getStdAsstCd());
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), _StdAssetDao.getStdBondAssetHis(bssd, mvId).getIntRate());
					}
					else if(asset.getStdAsstTypCd().equals("SHORT_RATE")) {
//						asstHisMap.putIfAbsent(asset.getStdAsstCd(), IrCurveHisDao.getShortRateHis(bssd, "1010000").getIntRate());
//						asstHisMap.putIfAbsent(asset.getStdAsstCd(), _BottomupDcntDao.getShortRateHis(bssd, "RF_KRW_KICS").getIntRate());
					}
				}
				
				
				String assetCd = "KOSPI200";
				
				if(!driftMap.containsKey(assetCd)) {
					log.error("Error: No Drift Parameter for {}", assetCd);
					System.exit(1);
				}
				if(!sigmaMap.containsKey(assetCd)) {
					log.error("Error: No Sigma Parameter for {}", assetCd);
					System.exit(1);
				}
				
				int seedNum = randSeedAsst[0];
				log.info("seedNum: {}", seedNum);
				
//				LogNormal4j logNormal = new LogNormal4j(bssd, driftMap.get(assetCd), sigmaMap.get(assetCd), projectionYear*12, batchNum);
				LogNormal4j logNormal = new LogNormal4j(bssd, driftMap.get(assetCd), sigmaMap.get(assetCd), projectionYear*12, batchNum, seedNum);
				
//				session.createQuery("delete EsgRandom a where a.baseYymm=:param  and a.stdAsstCd=:type and a.volCalcId=:id")
// 			           .setParameter("param", bssd).setParameter("type", assetCd).setParameter("id", assetCd).executeUpdate();
//				
//				List<EsgRandom> rnd = logNormal.getRandomScenList();
//			
//				for(EsgRandom r : rnd) {
//					r.setStdAsstCd(assetCd);
//					r.setVolCalcId(assetCd);
//				}							
				
//				List<CompletableFuture<List<StdAsstIrSceSto>>> 	sceJobFutures =	
//						IntStream.rangeClosed(1,batchNum)
//						.mapToObj(batch-> CompletableFuture.supplyAsync(() ->  logNormal.getBizStockScenario(bssd, assetCd , bizDv, batch), exe))
//						.collect(Collectors.toList());
//				
//				List<StdAsstIrSceSto> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
				
				List<StdAsstIrSceSto> rst = null;
				
				log.info("rst Size :  {},{}", rst.size());
				
				int sceCnt = 1;
				for (StdAsstIrSceSto bb :rst) {
//					log.info("zzzz : {}", bb.toString());
					session.save(bb);
					if (sceCnt % 50 == 0) {
						session.flush();
						session.clear();
					}
					if (sceCnt % flushSize == 0) {
//						log.info("Biz Bottom Scenario for {}  is processed {}/{} in Job 24A {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						log.info("BizStockSce Scenario for {}  is processed {}/{} in Job 36 {}", sceCnt, batchNum * 100 * projectionYear * 12);
					}
					sceCnt = sceCnt + 1;
				}
				
//				int sceCnt2 = 1;
//				for (EsgRandom rr :rnd) {
//					session.save(rr);
//					if (sceCnt2 % 50 == 0) {
//						session.flush();
//						session.clear();
//					}
//					if (sceCnt2 % flushSize == 0) {
//						log.info("ESG Random Number for {}  is processed {}/{} in Job 36 {}", sceCnt2, batchNum * 100 * projectionYear * 12);
//					}
//					sceCnt2 = sceCnt2 + 1;
//				}				
					
				completeJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completeJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void saveOrUpdate(Object item) {
		session.saveOrUpdate(item);

		if(cnt % flushSize ==0) {
			session.flush();
			session.clear();
			log.info("in the flush : {}", cnt);
		}
		cnt = cnt+1;
	}
	

//	private static void save(Object item) {
//		session.save(item);
//			
//		if(cnt % flushSize ==0) {
//			session.flush();
//			session.clear();
//			log.info("in the flush : {}", cnt);
//		}
//		cnt = cnt+1;
//	}

	
	private static CoJobInfo startJogLog(EJob job) {
		CoJobInfo jobLog = new CoJobInfo();
		jobLog.setJobStart(LocalDateTime.now());
		
		jobLog.setJobId(job.name());
		jobLog.setCalcStart(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
		jobLog.setBaseYymm(bssd);
		jobLog.setCalcDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")));
		jobLog.setJobNm(job.getJobName());
		
		log.info("{}({}): Job Start !!! " , job.name(), job.getJobName());
		
		return jobLog;
	}
	
	private static void completeJob(String successDiv, CoJobInfo jobLog) {		

		long timeElapse = Duration.between(jobLog.getJobStart(), LocalDateTime.now()).getSeconds();
		
//		log.info("timeElapse: {}, jobStart: {}", timeElapse, jobLog.getJobStart());
		jobLog.setCalcEnd(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
		jobLog.setCalcScd(successDiv);
		jobLog.setCalcElps(String.valueOf(timeElapse));
		jobLog.setLastModifiedBy(jobLog.getJobId());
		jobLog.setLastUpdateDate(LocalDateTime.now());
		
		log.info("{}({}): Job Completed with {} !!!", jobLog.getJobId(), jobLog.getJobNm(), successDiv);
	}		
	
	
	private static void hold() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.error("ERROR: {}", e);
		}		
	}
	
}
