package com.gof.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.IrParamHwBiz;
import com.gof.entity.IrParamHwUsr;
import com.gof.entity.IrParamModel;
import com.gof.entity.IrParamHwCalc;
import com.gof.enums.EBoolean;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class _EsgParamDao {
	
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<IrParamHwCalc> getFullLocalCalibParamCalHis(String bssd , int monthNum, String paramType, String matCd) {
		String q = "select a from ParamCalcHis a "
				+ " where 1=1" 
				+ " and a.baseYymm between :stDate and :endDate "
				+ " and a.paramTypCd =  :paramType"
				+ " and a.paramCalcCd = :paramCalcCd"
				+ " and a.matCd = :matCd" ;
		
		return session.createQuery(q, IrParamHwCalc.class)
		.setParameter("stDate", FinUtils.addMonth(bssd, monthNum))
		.setParameter("endDate", bssd)
		.setParameter("paramType", paramType)
		.setParameter("matCd", matCd)
		.setParameter("paramCalcCd", "FULL_LOCAL_CALIB")
		.list()
		;

	}

	public static List<IrParamHwCalc> getSigmaLocalCalibParamCalHis(String bssd , int monthNum, String paramType, String matCd) {
		String q = "select a from ParamCalcHis a "
				+ " where 1=1" 
//				+ " and a.baseYymm between :stDate and :endDate "
				+ " and a.baseYymm > :stDate and a.baseYymm <= :endDate "
				+ " and a.paramTypCd =  :paramType"
				+ " and a.paramCalcCd = :paramCalcCd"
				+ " and a.matCd = :matCd" ;
		
		return session.createQuery(q, IrParamHwCalc.class)
		.setParameter("stDate", FinUtils.addMonth(bssd, monthNum))
		.setParameter("endDate", bssd)
		.setParameter("paramType", paramType)
		.setParameter("matCd", matCd)
		.setParameter("paramCalcCd", "SIGMA_LOCAL_CALIB")
		.list()
		;

	}
	
	public static List<IrParamHwCalc> getParamCalHis(String bssd , String irModelTyp, String paramCalcCd) {
		String q = "select a from ParamCalcHis a "
				+ " where 1=1" 
				+ " and a.baseYymm =:bssd "
				+ " and a.irModelTyp =  :irModelTyp"
				+ " and a.paramCalcCd = :paramCalcCd"
				;
		
		return session.createQuery(q, IrParamHwCalc.class)
		.setParameter("bssd", bssd)
		.setParameter("irModelTyp", irModelTyp)
		.setParameter("paramCalcCd", paramCalcCd)
		.list()
		;

	}

	public static List<IrParamHwUsr> getBizEsgParamUd(String bssd) {
		
		String irModelId ="HW_1F";
		List<IrParamModel> esgMstList = _EsgMstDao.getEsgMst(EBoolean.Y);
		if(!esgMstList.isEmpty()) {
			irModelId = esgMstList.get(0).getIrModelId();
		}		
		
		String q = "select a from IrParamHwUsr a "
				+ " where 1=1" 
				+ " and a.baseYymm      = :bssd "
				+ " and a.applyBizDv 	= :bizDv "
				+ " and a.irModelId 	= :irModelId "
				;
		
		return session.createQuery(q, IrParamHwUsr.class)
				.setParameter("bssd", getAppliedDate(bssd))
				.setParameter("bizDv", "I")
				.setParameter("irModelId", irModelId)
				.list()
		;

	}

	public static List<IrParamHwBiz> getBizEsgParam(String bssd, String irModelId ) {
		String q = "select a from BizEsgParam a "
				+ " where 1=1" 
				+ " and a.baseYymm = :bssd "
				+ " and a.irModelId 	= :irModelId "
				;
		
		return session.createQuery(q, IrParamHwBiz.class)
				.setParameter("bssd", bssd)
				.setParameter("irModelId", irModelId)
				.list()
		;

	}
	private static String getAppliedDate(String bssd) {
		
		String irModelId ="HW_1F";
		List<IrParamModel> esgMstList = _EsgMstDao.getEsgMst(EBoolean.Y);
		if(!esgMstList.isEmpty()) {
			irModelId = esgMstList.get(0).getIrModelId();
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append("select max(a.applyStartYymm) from BizEsgParamUd a "
				+ "		where 1=1"
				+ "		and a.applyBizDv = :bizDv"
				+ "		and a.irModelId  = :irModelId"
				+ "		and a.applyStartYymm <= :bssd"
				+ "		and a.applyEndYymm >=  :bssd"
				)
		;
		Query<String> q = session.createQuery(builder.toString(), String.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", "I");
		q.setParameter("irModelId", irModelId);
		;
		
		log.info("apply Date for Biz Applied Parameter : {}", q.getSingleResult());
		return q.getSingleResult();
	}
}
