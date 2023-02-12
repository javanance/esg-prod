package com.gof.process;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gof.dao.IrSprdDao;
import com.gof.entity.IrSprdLpBiz;
import com.gof.entity.IrSprdLpUsr;
import com.gof.enums.EJob;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Esg240_BizLpSprd extends Process {	
	
	public static final Esg240_BizLpSprd INSTANCE = new Esg240_BizLpSprd();
	public static final String jobId = INSTANCE.getClass().getSimpleName().toUpperCase().substring(0, ENTITY_LENGTH);
	
	public static List<IrSprdLpBiz> setLpSprdBiz(String bssd, String applBizDv, List<IrSprdLpUsr> irSprdLpUsrList) {
		
		List<IrSprdLpBiz> rst = new ArrayList<IrSprdLpBiz>();
		
//		Map<String, Map<Integer, IrSprdLpUsr>> sprdLpUsrMap = new TreeMap<String, Map<Integer, IrSprdLpUsr>>();
//		
//		sprdLpUsrMap = irSprdLpUsrList.stream().filter(s -> s.getApplBizDv().equals(applBizDv))
//					                        .collect(Collectors.groupingBy(IrSprdLpUsr::getIrCurveId, TreeMap::new, Collectors.toMap(IrSprdLpUsr::getIrCurveSceNo, Function.identity())));				

//		irSprdLpUsrList.stream().map(s -> s.convert(bssd)).forEach(s -> session.);

		//일단 SWparam을 무조건 인수로 받아야한다. 굳이 DB에서 가져올필요는 없고, xxxxParamSwMap을 인수로 받아서 처리한다.
		if(irSprdLpUsrList == null || irSprdLpUsrList.size() == 0) {
			log.info("There is no Liquidity Premium Data in [{}] from [{}] Table.", bssd, toPhysicalName(IrSprdLpUsr.class.getSimpleName()));			
			
			rst = IrSprdDao.getIrSprdLpList(bssd).stream().filter(s -> s.getDcntApplModelCd().equals("BU1") && s.getApplBizDv().equals(applBizDv))
					                               			.map(s -> s.convert()).collect(Collectors.toList());			
		}
		
		log.info("{}({}) creates [{}] results of {}. They are inserted into [{}] Table", jobId, EJob.valueOf(jobId).getJobName(), rst.size(), applBizDv, toPhysicalName(IrSprdLpBiz.class.getSimpleName()));
		
		return rst;		
	}	

}

