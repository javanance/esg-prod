package com.gof.process;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gof.dao._CorpCrdGrdPdDao;
import com.gof.entity.RcCorpPdBiz;
import com.gof.entity._CorpCumPd;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Job82_BizCorpPd {
	public static List<RcCorpPdBiz> getBizCorpPdFromCumPd(String bssd, String bizDv) {
		List<RcCorpPdBiz> rstList = new ArrayList<RcCorpPdBiz>();
		RcCorpPdBiz tempPd;
		
		List<_CorpCumPd> corpPdList = _CorpCrdGrdPdDao.getCorpCumPd(bssd);
		
		for (_CorpCumPd aa : corpPdList) {
			tempPd = new RcCorpPdBiz();

			tempPd.setBaseYymm(bssd);
			tempPd.setApplBizDv(bizDv);
			tempPd.setCrdGrdCd(aa.getGradeCode());
			tempPd.setMatCd(aa.getMatCd());
			
//			tempPd.setPd( aa.getMatCd().compareTo("M0012") < 1  ? aa.getCumPd(): 0.0);
			tempPd.setCumPd(aa.getCumPd());
			tempPd.setFwdPd(aa.getFwdPd());
			
			tempPd.setLastModifiedBy("ESG");
			tempPd.setLastUpdateDate(LocalDateTime.now());
			
			rstList.add(tempPd);
			
		}
		log.info("Job (Biz {}  Applied Cumulative Corporate Pd ) create {} result.  They are inserted into EAS_BIZ_APLY_CORP_PD Table ", bizDv, rstList.size());
		return rstList;
	}
	

//	public static List<BizSegLgd> getBizSegLgd(String bssd) {
//		List<BizSegLgd> rstList = new ArrayList<BizSegLgd>();
//		BizSegLgd tempPd;
//		
//		List<SegLgd> lgdList = SegLgdDao.getSegLgd(bssd);   //占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 LGD
//		
//		for (SegLgd aa : lgdList) {
//			tempPd = new BizSegLgd();
//
//			tempPd.setBaseYymm(bssd);
//			tempPd.setApplyBizDv("I");
//			tempPd.setSegId(aa.getSegId());
//			tempPd.setLgdCalcTypCd(aa.getLgdCalcTypCd());
//			
//			tempPd.setApplyLgd(aa.getLgd());
//			tempPd.setVol(0.0);
//			
//			tempPd.setLastModifiedBy("ESG");
//			tempPd.setLastUpdateDate(LocalDateTime.now());
//			
//			rstList.add(tempPd);
//			
//		}
//		log.info("Job 63 (IFRS17 Applied Segment LGD ) create {} result.  They are inserted into EAS_BIZ_APPL_SEG_LGD Table ", rstList.size());
//		return rstList;
//	}
	
//	public static List<BizCrdSpread> getBizCrdSpread(String bssd) {
//		List<BizCrdSpread> rstList = new ArrayList<BizCrdSpread>();
//		BizCrdSpread tempPd =new BizCrdSpread();
//		Map<String, Object> param = new HashMap<>();
//		param.put("baseYymm", bssd);
//		
//		List<CreditSpread> spreadList = DaoUtil.getEntities(CreditSpread.class, param);
//		
//		for (CreditSpread aa : spreadList) {
//			
//			tempPd = new BizCrdSpread();
//			tempPd.setBaseYymm(bssd);
////			tempPd.setApplyStYymm(bssd);
////			tempPd.setApplyEndYymm(bssd);
//			tempPd.setApplyBizDv("I");
//			
//			tempPd.setCrdGrdCd(aa.getCrdGrdCd());
//			tempPd.setMatCd(aa.getMatCd());
//			
//			tempPd.setApplyCrdSpread(aa.getCrdSpread());
//			tempPd.setVol(0.0);
//			
//			tempPd.setLastModifiedBy("ESG");
//			tempPd.setLastUpdateDate(LocalDateTime.now());
//			rstList.add(tempPd);
//		}
//		
//		log.info("Job 63 (IFRS17 Applied Credit Spread) create {} result.  They are inserted into EAS_BIZ_APLY_CRD_SPREAD Table ", rstList.size());
//		return rstList;
//	}

//	public static List<BizInflation> getBizInflation(String bssd) {
//		List<BizInflation> rstList = new ArrayList<BizInflation>();
//		BizInflation tempPd =new BizInflation();
//		
//		int inflationAvgMonNum = Integer.parseInt(ParamUtil.getParamMap().getOrDefault("inflationAvgMonNum", "-36"));
//		
//		List<Inflation> infList = InflationDao.getPrecedingInflation(bssd, inflationAvgMonNum);          //占쏙옙占쏙옙 3占쏙옙 inflation 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙臼占� 占쏙옙占쏙옙占쏙옙.
//		
//		double avgInflation =0.0;
//		for (Inflation aa : infList) {
//			avgInflation = avgInflation + aa.getInflation();
//			tempPd = new BizInflation();
//			
//			tempPd.setBaseYymm(bssd);
//			tempPd.setApplyBizDv("I");
//			tempPd.setInflationId(aa.getInflationId());
//			tempPd.setInflationIndex(aa.getInflationIndex());
//			
//			tempPd.setInflation(avgInflation/infList.size());
//			
//			tempPd.setMgmtTargetLowerVal(aa.getMgmtTargetLowerVal());
//			tempPd.setMgmtTargetUpperVal(aa.getMgmtTargetUpperVal());
//			
//			tempPd.setLastModifiedBy("ESG");
//			tempPd.setLastUpdateDate(LocalDateTime.now());
//		}
//		
//		rstList.add(tempPd);
//		log.info("Job 63 (IFRS17 Applied Inflation) create {} result.  They are inserted into EAS_BIZ_APPL_INFLATION Table ", rstList.size());
//		return rstList;
//	}
//	
	
//	public static List<BizInflation> getBizInflationMA(String bssd) {
//		List<BizInflation> rstList = new ArrayList<BizInflation>();
//		BizInflation tempPd =new BizInflation();
//		
//		int avgNum = Integer.parseInt(ParamUtil.getParamMap().getOrDefault("inflationAvgNum", "-36"));
//		
//		List<InflationUd> infUdList = InflationDao.getInflationUd(bssd);	//CURRENT USER INPUT 
//		List<Inflation> infList = InflationDao.getPrecedingInflation(bssd, avgNum); 
//		
//		tempPd = new BizInflation();
//		tempPd.setBaseYymm(bssd);
//		tempPd.setApplyBizDv("I");
//
//		double avgSum =0.0;
//		int   avgCnt =0;
//		if( infUdList.size() > 0 && infUdList.get(0).getIfrsTgtIndex() != null && infUdList.get(0).getIfrsTgtIndex() != 0.0  ) { 
//			InflationUd infUd = infUdList.get(0);
//			
//			tempPd = new BizInflation();
//			tempPd.setBaseYymm(bssd);
//			tempPd.setApplyBizDv("I");
//			tempPd.setInflationId("TGT");
////			tempPd.setInflationIndex(infUd.getIfrsTgtIndex());   
//			tempPd.setInflation(infUd.getIfrsTgtIndex());			//TODO : �뀒�씠釉� 移쇰읆 異붽�
//			
//			tempPd.setMgmtTargetLowerVal(0.0);
//			tempPd.setMgmtTargetUpperVal(0.0);
////			tempPd.setVol(0.0);
//			
//			tempPd.setLastModifiedBy("ESG");
//			tempPd.setLastUpdateDate(LocalDateTime.now());
//			rstList.add(tempPd);
//		}
//		else if(infList.size()>0){
//			for (Inflation zz : infList) {
//				avgSum = avgSum + zz.getInflation();
//				avgCnt  = avgCnt +1;
//				
//			}
////			logger.info("zzz1 : {},{},{},{},{}", bssd, avgCnt, avgSum);
//			
//			Inflation inf = infList.get(0);
//			
//			tempPd = new BizInflation();
//			tempPd.setBaseYymm(bssd);
//			tempPd.setApplyBizDv("I");
//			tempPd.setInflationId(inf.getInflationId());
//			tempPd.setInflationIndex(inf.getInflationIndex());
////			tempPd.setInflation(inf.getInflation());
//			tempPd.setInflation(avgSum/avgCnt);
//			
//			tempPd.setMgmtTargetLowerVal(0.0);
//			tempPd.setMgmtTargetUpperVal(0.0);
////			tempPd.setVol(0.0);
//			
//			tempPd.setLastModifiedBy("ESG");
//			tempPd.setLastUpdateDate(LocalDateTime.now());
//
//			rstList.add(tempPd);
//		}
//		else {
//			
//		}
//		log.info("Job 63 (IFRS17 Applied Inflation) create {} result.  They are inserted into EAS_BIZ_APPL_INFLATION Table ", rstList.size());
//		return rstList;
//	}
	
}
