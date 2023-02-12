package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.RcCorpTm;
import com.gof.entity._TransitionMatrixUd;
import com.gof.util.EsgConstant;
import com.gof.util.HibernateUtil;

/**
 *  <p> ������� ������ �����ϴ� DAO
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class _TransitionMatrixDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<_TransitionMatrixUd> getTMUd(String bssd) {
//		 String tmType = ParamUtil.getParamMap().getOrDefault("tmType", "STM1");
		 String tmType =EsgConstant.getStrConstant().getOrDefault("tmType", "STM1");
		 String sql = "select a from TransitionMatrixUd a "
	    			+ "where a.tmType = :param "
	    			+ "and a.baseYyyy = :time" 
	    			;
	    
	    List<_TransitionMatrixUd> rst = session.createQuery(sql, _TransitionMatrixUd.class)
	    							.setParameter("param", tmType)
	    							.setParameter("time",bssd.substring(0,4))
	    							.getResultList();

	    
		return rst;
	}
	
	
	public static List<_TransitionMatrixUd> getDefaultRateUd(String bssd) {
//		String tmType = ParamUtil.getParamMap().getOrDefault("tmType", "STM1");
		String tmType =EsgConstant.getStrConstant().getOrDefault("tmType", "STM1");
		
		
		String sql = "select a from TransitionMatrixUd a "
	    			+ "where a.tmType = :param "
	    			+ "and a.toGrade  = :param2 "
	    			+ "and a.baseYyyy = :time" 
	    			;
	    
	    List<_TransitionMatrixUd> rst = session.createQuery(sql, _TransitionMatrixUd.class)
	    							.setParameter("param", tmType)
	    							.setParameter("param2", "D")
	    							.setParameter("time",bssd.substring(0,4))
	    							.getResultList();
		return rst;
	}
	
	
	
	public static List<RcCorpTm> getTM(String bssd) {
//		 String tmType = ParamUtil.getParamMap().getOrDefault("tmType", "STM1");
		 String tmType =EsgConstant.getStrConstant().getOrDefault("tmType", "STM1");
		 String sql = "select a from TransitionMatrix a "
	    			+ "where a.tmType = :param "
	    			+ "and a.baseYyyy = :time" 
	    			;
	    
	    List<RcCorpTm> rst = session.createQuery(sql, RcCorpTm.class)
	    							.setParameter("param", tmType)
	    							.setParameter("time",bssd.substring(0,4))
	    							.getResultList();

	    
		return rst;
	}
	
	public static List<RcCorpTm> getDefaultRate(String bssd) {
//		String tmType = ParamUtil.getParamMap().getOrDefault("tmType", "STM1");
		String tmType =EsgConstant.getStrConstant().getOrDefault("tmType", "STM1");
		
		
		String sql = "select a from TransitionMatrix a "
	    			+ "where a.tmType = :param "
	    			+ "and a.toGrade  = :param2 "
	    			+ "and a.baseYyyy = :time" 
	    			;
	    
	    List<RcCorpTm> rst = session.createQuery(sql, RcCorpTm.class)
	    							.setParameter("param", tmType)
	    							.setParameter("param2", "D")
	    							.setParameter("time",bssd.substring(0,4))
	    							.getResultList();
		return rst;
	}
}
