package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.IrVolSwpn;
import com.gof.entity.IrVolSwpnUsr;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IrVolSwpnDao extends DaoUtil {
	
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<IrVolSwpnUsr> getSwpnVolUsr(String bssd, String irCurveId) {
		
		String endOfMonth = getMaxBaseDate(bssd, irCurveId);		
		
		String query = " select a from IrVolSwpnUsr a    " 
		 		 	 + "  where a.baseDate = :endOfMonth "
		 		 	 + "    and a.irCurveId = :irCurveId "
		 		 	 + "  order by a.swpnMat             "		 		 
		 		 	 ;
		
		return session.createQuery(query, IrVolSwpnUsr.class)
					  .setParameter("endOfMonth", endOfMonth)
					  .setParameter("irCurveId", irCurveId)
					  .getResultList()
					  ;
	}
	
	
	public static String getMaxBaseDate(String bssd, String irCurveId) {
		
		String query = "select max(a.baseDate) "
					 + "from IrVolSwpnUsr a "
					 + "where 1=1 "
					 + "and a.baseDate <= :bssd	"
					 + "and a.irCurveId = :irCurveId "					 
					 ;		
		
		Object maxDate = session.createQuery(query)					
								.setParameter("bssd", FinUtils.toEndOfMonth(bssd))
				 			 	.setParameter("irCurveId", irCurveId)
								.uniqueResult();
		
		if(maxDate == null) {
			log.warn("IR Vol Swaption Date is not found {} at {}!!!" , irCurveId, FinUtils.toEndOfMonth(bssd));
			return bssd;
		}		
		
		return maxDate.toString();
	}	
	
	
	public static List<IrVolSwpn> getSwpnVol(String bssd, String irCurveId) {
		
		String query = " select a from IrVolSwpn a       	  " 
		 		 	 + "  where a.baseYymm = :bssd       	  "
		 		 	 + "    and a.irCurveId = :irCurveId      "
		 		 	 + "  order by a.swpnMatNum, a.swapTenNum "		 		 
		 		 	 ;
		
		return session.createQuery(query, IrVolSwpn.class)
					  .setParameter("bssd", bssd)
					  .setParameter("irCurveId", irCurveId)
					  .getResultList()
					  ;
	}
	
	
	public static List<IrVolSwpn> getSwpnVol(String bssd) {
		
		String query = " select a from IrVolSwpn a       	  " 
		 		 	 + "  where a.baseYymm = :bssd       	  "		 		 	 
		 		 	 + "  order by a.swpnMatNum, a.swapTenNum "		 		 
		 		 	 ;
		
		return session.createQuery(query, IrVolSwpn.class)
					  .setParameter("bssd", bssd)					  
					  .getResultList()
					  ;
	}
	
	
	public static List<IrVolSwpn> getSwpnVol(String bssd, int monthNum, String irCurveId) {
		
		String query = " select a from IrVolSwpn a            "
				 	 + "  where a.baseYymm = :bssd            "
		 		 	 + "    and a.irCurveId = :irCurveId      "
		 		 	 + "  order by a.swpnMatNum, a.swapTenNum "				
				 	 ;
		
		return session.createQuery(query, IrVolSwpn.class)
					  .setParameter("bssd", FinUtils.addMonth(bssd, monthNum))
					  .setParameter("irCurveId", irCurveId)
					  .getResultList()
					  ;
	}
	
	
	public static List<IrVolSwpn> getSwpnVol(String bssd, int monthNum) {
		
		String query = " select a from IrVolSwpn a            "
				 	 + "  where a.baseYymm = :bssd            "
		 		 	 + "  order by a.swpnMatNum, a.swapTenNum "				
				 	 ;
		
		return session.createQuery(query, IrVolSwpn.class)
					  .setParameter("bssd", FinUtils.addMonth(bssd, monthNum))
					  .getResultList()
					  ;
	}	
	

	
	
}
