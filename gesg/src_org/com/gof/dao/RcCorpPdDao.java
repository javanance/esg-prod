package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.RcCorpTm;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RcCorpPdDao extends DaoUtil {
	
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<RcCorpTm> getRcCorpTm(String bssd) {
		
		String baseYymm = getMaxBaseYymm(bssd);		
		
		String query = " select a from RcCorpTm a      " 
		 		 	 + "  where a.baseYymm = :baseYymm "		 		 	 		 		 
		 		 	 ;
		
		return session.createQuery(query, RcCorpTm.class)
					  .setParameter("baseYymm", baseYymm)					  
					  .getResultList()
					  ;
	}
	
	
	public static String getMaxBaseYymm(String bssd) {
		
		String query = "select max(a.baseYymm) "
					 + "from RcCorpTm a "
					 + "where 1=1 "
					 + "and a.baseYymm <= :bssd	"					 					 
					 ;		
		
		Object maxDate = session.createQuery(query)					
								.setParameter("bssd", bssd)				 			 	
								.uniqueResult();
		
		if(maxDate == null) {
			log.warn("Corporate Transition Matrix is not found {} at {}!!!", bssd);
			return bssd;
		}		
		
		return maxDate.toString();
	}	
		
}
