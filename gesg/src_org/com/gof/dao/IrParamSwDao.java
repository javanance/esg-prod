package com.gof.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;

import com.gof.entity.IrParamSw;
import com.gof.entity.IrParamSwUsr;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IrParamSwDao extends DaoUtil {
	
	private static Session session = HibernateUtil.getSessionFactory().openSession();	
	
	public static List<IrParamSwUsr> getIrParamSwUsrList(String bssd) {
		
		String query = " select a from IrParamSwUsr a 						"
				 	 + "  where 1=1 										"
				 	 + "    and :bssd between a.applStYymm and a.applEdYymm "
				 	 + "  order by a.irCurveId, a.irCurveSceNo 				"
				 	 ;		
		
		return session.createQuery(query, IrParamSwUsr.class)
				      .setParameter("bssd", bssd)
				      .getResultList();
	}	
	

	public static List<IrParamSw> getIrParamSwList(String bssd) {		
		return getIrParamSwUsrList(bssd).stream().map(s -> s.convert(bssd)).collect(Collectors.toList());
	}		


	public static List<IrParamSwUsr> getIrParamSwUsrList(String bssd, List<String> irCurveIdList) {
		
		String query = " select a from IrParamSwUsr a 						"
				 	 + "  where 1=1 										" 
				 	 + "    and :bssd between a.applStYymm and a.applEdYymm "
				 	 + "    and a.irCurveId in (:irCurveIdList) 			"
				 	 + "  order by a.irCurveId, a.irCurveSceNo  			"
				 	 ;		
		
		return session.createQuery(query, IrParamSwUsr.class)
				      .setParameter("bssd", bssd)
				      .setParameterList("irCurveIdList", irCurveIdList)
				      .getResultList();
	}	

	//현재까지는 이 method만 사용중	
	public static List<IrParamSw> getIrParamSwList(String bssd, List<String> irCurveIdList) {
		return getIrParamSwUsrList(bssd, irCurveIdList).stream().map(s -> s.convert(bssd)).collect(Collectors.toList());
	}

	
	public static List<IrParamSwUsr> getIrParamSwUsrList(String bssd, String applBizDv, String irCurveId, Integer irCurveSceNo) {
		
		String query = " select a from IrParamSwUsr a 						"
				 	 + "  where 1=1 										" 
				 	 + "    and :bssd between a.applStYymm and a.applEdYymm "
				 	 + "    and a.applBizDv    = :applBizDv    				"
				 	 + "    and a.irCurveId    = :irCurveId    				"
				 	 + "    and a.irCurveSceNo = :irCurveSceNo 				"
				 	 + "  order by a.irCurveId, a.irCurveSceNo 				"
				 	 ;		
		
		return session.createQuery(query, IrParamSwUsr.class)
				      .setParameter("bssd", bssd)
				      .setParameter("applBizDv", applBizDv)
				      .setParameter("irCurveId", irCurveId)
				      .setParameter("irCurveSceNo", irCurveSceNo)
				      .getResultList();
	}		
	
	
	public static List<IrParamSw> getIrParamSwList(String bssd, String applBizDv, String irCurveId, Integer irCurveSceNo) {
		return getIrParamSwUsrList(bssd, applBizDv, irCurveId, irCurveSceNo).stream().map(s -> s.convert(bssd)).collect(Collectors.toList());		
	}		
	
	
	public static List<IrParamSwUsr> getIrParamSwUsr(String bssd, String applBizDv, String irCurveId, Integer irCurveSceNo) {		
		
		String query = "select a from IrParamSwUsr a          "
					 + " where 1=1                            "  
					 + "   and a.baseYymm     = :bssd         "
					 + "   and a.applyBizDv   = :applBizDv    "
					 + "   and a.irCurveId 	  = :irCurveId    "					 
					 + "   and a.irCurveSceNo = :irCurveSceNo "
					 ;
		
		return session.createQuery(query, IrParamSwUsr.class)
					  .setParameter("bssd", getAppliedDate(bssd, applBizDv, irCurveId, irCurveSceNo))
					  .setParameter("applBizDv", applBizDv)
					  .setParameter("irCurveId", irCurveId)
					  .setParameter("irCurveSceNo", irCurveSceNo)
					  .getResultList()
					  ;
	}
	

	private static String getAppliedDate(String bssd, String applBizDv, String irCurveId, Integer irCurveSceNo) {		
		
		String query = " select max(a.applStYymm) from IrParamSwUsr a 		"
			     	 + "  where 1=1											"
			     	 + "    and :bssd between a.applStYymm and a.applEdYymm "
			     	 + "	and a.applBizDv    = :applBizDv 				"
			     	 + "	and a.irCurveId    = :irCurveId 				"
			     	 + "	and a.irCurveSceNo = :irCurveId 				"
			     	 ;
	
		Object maxDate = session.createQuery(query)					
								.setParameter("bssd", FinUtils.toEndOfMonth(bssd))
								.setParameter("applBizDv"   , applBizDv)
								.setParameter("irCurveId"   , irCurveId)
								.setParameter("irCurveSceNo", irCurveSceNo)
								.uniqueResult();
	
		if(maxDate==null) {
			log.warn("Apply Date for IrParamSwUsr is not found [BIZ: {}, IR_CURVE_ID: {}, IR_CURVE_SCE_NO: {}] at {}!!!", applBizDv, irCurveId, irCurveSceNo, FinUtils.toEndOfMonth(bssd));
			return bssd;
		}		
		
		return maxDate.toString();		
	}	
		

//	public static List<_BizStockYield> getStdStockYield(String bssd, String bizDv) {
//		 String sql = "select new com.gof.entity.BizStockYield( a.baseYymm, a.applBizDv, a.stdAsstCd, a.matCd, avg(a.asstYield)) "
//		 		+ " from BizStockSce a "
//		 		+ "	where baseYymm = :bssd"
//		 		+ "	and a.applBizDv = :bizDv"
//		 		+ " group by a.baseYymm, a.applBizDv, a.stdAsstCd, a.matCd"
//		 		;
//		 
//	    List<_BizStockYield> rst = session.createQuery(sql, _BizStockYield.class)
//	    							.setParameter("bssd", bssd)
//	    							.setParameter("bizDv", bizDv)
//	    							.getResultList();
//		return rst;
//	}
	
	
//	public static List<IrParamSw> getIrParamSwList2(String bssd) {
//		
//		String q = " select new com.gof.entity.IrParamSw( a.applStYymm, a.applBizDv, a.irCurveId, a.irCurveSceNo "
//				 + "      , max(a.curCd) as curCd, max(a.freq) as freq, max(a.llp) as llp, max(a.ltfr) as ltfr "
//				 + "      , max(a.liqPrem) as liqPrem, max(a.liqPremApplDv) as liqPremApplDv, max(a.addSprd) as addSprd, max(a.swAlphaYtm) as swAlphaYtm)"
//				 + "   from IrParamSwUsr a "
//				 + "  where 1=1 " 
//  			     + "    and a.applStYymm <  :bssd "
//				 + "    and a.applEdYymm >= :bssd "
//				 + "  group by a.applStYymm, a.applBizDv, a.irCurveId, a.irCurveSceNo "
//				 ;		
//
//		return session.createQuery(q, IrParamSw.class)
//				      .setParameter("bssd", bssd)
//				      .getResultList();
//	}
	
//	public static IrParamSwUsr getIrParamSwYtmAttr(String bssd, String irCurveId) {
//		
//		String q = " select a.irCurveId, max(a.freq), max(a.swAlphaYtm) "
//				 + "   from IrParamSwUsr a "
//				 + "  where 1=1 " 
//  			     + "    and a.applStYymm <  :bssd "
//				 + "    and a.applEdYymm >= :bssd "
//				 + "    and a.irCurveId  =  :irCurveId "
//				 + "  group by a.irCurveId             "
//				 ;		
//		
//		return session.createQuery(q, IrParamSwUsr.class)
//				      .setParameter("bssd", bssd)
//				      .setParameter("irCurveId", irCurveId)
//				      .uniqueResult();
//	}
	
}
