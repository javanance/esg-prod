package com.gof.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;

import com.gof.entity.IrSprdAfnsBiz;
import com.gof.entity.IrSprdLp;
import com.gof.entity.IrSprdLpBiz;
import com.gof.entity.IrSprdLpUsr;
import com.gof.util.HibernateUtil;

public class IrSprdDao extends DaoUtil {
	
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<IrSprdLpUsr> getIrSprdLpUsrList(String bssd) {
		
		String query = " select a from IrSprdLpUsr a "
				 	 + "  where 1=1 "
				 	 + "    and :bssd between a.applStYymm and a.applEdYymm "
				 	 ;		
		
		return session.createQuery(query, IrSprdLpUsr.class)
				      .setParameter("bssd", bssd)
				      .getResultList();
	}	
	

	public static List<IrSprdLpUsr> getIrSprdLpUsrList(String bssd, List<String> irCurveIdList) {
		
		String query = " select a from IrSprdLpUsr a "
				 	 + "  where 1=1 " 
				 	 + "    and :bssd between a.applStYymm and a.applEdYymm "
				 	 + "    and a.irCurveId in (:irCurveIdList) "
				 	 ;		
		
		return session.createQuery(query, IrSprdLpUsr.class)
				      .setParameter("bssd", bssd)
				      .setParameterList("irCurveIdList", irCurveIdList)
				      .getResultList();
	}	

	
	public static List<IrSprdLpBiz> getIrSprdLpUsrToBizList(String bssd) {		
		return getIrSprdLpUsrList(bssd).stream().map(s -> s.convert(bssd)).collect(Collectors.toList());
	}
	
	
	public static List<IrSprdLp> getIrSprdLpList(String bssd) {
		
		String query = " select a from IrSprdLp a "
				 	 + "  where 1=1 "
				 	 + "    and baseYymm = :bssd "
				 	 ;		
		
		return session.createQuery(query, IrSprdLp.class)
				      .setParameter("bssd", bssd)
				      .getResultList();
	}	

	
	public static List<IrSprdLp> getIrSprdLpList(String bssd, List<String> irCurveIdList) {
		
		String query = " select a from IrSprdLp a "
				 	 + "  where 1=1 " 
				 	 + "    and baseYymm = :bssd "
				 	 ;		
		
		return session.createQuery(query, IrSprdLp.class)
				      .setParameter("bssd", bssd)
				      .setParameterList("irCurveIdList", irCurveIdList)
				      .getResultList();
	}		
	
	
	public static List<IrSprdLpBiz> getIrSprdLpBiz(String bssd, String applBizDv, String irCurveId, Integer irCurveSceNo){
		
		String query = "select a from IrSprdLpBiz a "
					 + " where 1=1 "
					 + "   and a.baseYymm     = :bssd         "
					 + "   and a.applBizDv    = :applBizDv    "
					 + "   and a.irCurveId    = :irCurveId    "
					 + "   and a.irCurveSceNo = :irCurveSceNo "
					 ;
		
		return session.createQuery(query, IrSprdLpBiz.class)
			      	  .setParameter("bssd", bssd)
			      	  .setParameter("applBizDv", applBizDv)
			      	  .setParameter("irCurveId", irCurveId)			      	  
			      	  .setParameter("irCurveSceNo", irCurveSceNo)
					  .getResultList();
	}	
	

	public static List<IrSprdAfnsBiz> getIrSprdAfnsBiz(String bssd, String irModelId, String irCurveId, Integer irCurveSceNo){
		
		String query = "select a from IrSprdAfnsBiz a "
					 + " where 1=1 "
					 + "   and a.baseYymm     = :bssd         "
					 + "   and a.irModelId    = :irModelId    "
					 + "   and a.irCurveId    = :irCurveId    "
					 + "   and a.irCurveSceNo = :irCurveSceNo "
					 ;
		
		return session.createQuery(query, IrSprdAfnsBiz.class)
			      	  .setParameter("bssd", bssd)
			      	  .setParameter("irModelId", irModelId)
			      	  .setParameter("irCurveId", irCurveId)			      	  
			      	  .setParameter("irCurveSceNo", irCurveSceNo)
					  .getResultList();
	}	
	
}
