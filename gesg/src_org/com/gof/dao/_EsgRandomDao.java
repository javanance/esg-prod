package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.IrParamHwRnd;
import com.gof.util.HibernateUtil;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class _EsgRandomDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<IrParamHwRnd> getEsgRandom(String bssd, String volCalcId, String stdAsstCd) {
		 String sql = "select a from EsgRandom a "
		 		+ "	where baseYymm= :bssd"
		 		+ " and volCalcId = :volCalcId"
		 		+ " and stdAsstCd = :stdAsstCd"
		 		;
		 
	    return  session.createQuery(sql, IrParamHwRnd.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("volCalcId", volCalcId)
	    							.setParameter("stdAsstCd", stdAsstCd)
	    							.getResultList();

	}
	
	
	public static List<IrParamHwRnd> getEsgRandom(String bssd, String volCalcId, String stdAsstCd, int matNum) {
		 String sql = "select a from EsgRandom a "
		 		+ "	where a.baseYymm= :bssd"
		 		+ " and a.volCalcId = :volCalcId"
		 		+ " and a.stdAsstCd = :stdAsstCd"
		 		+ " and a.matNum = :matNum"
		 		;
		 
	    return  session.createQuery(sql, IrParamHwRnd.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("volCalcId", volCalcId)
	    							.setParameter("stdAsstCd", stdAsstCd)
	    							.setParameter("matNum", matNum)
	    							.getResultList();

	}
	
}	
