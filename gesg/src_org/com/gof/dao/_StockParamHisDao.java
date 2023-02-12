package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.IrDcntRateBu;
import com.gof.entity._StockParamHis;
import com.gof.util.HibernateUtil;

public class _StockParamHisDao {
	
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<_StockParamHis> getStockParamHis(String bssd) {
		 String sql = "select a from StockParamHis a where baseYymm =:bssd";
		 
	    List<_StockParamHis> rst = session.createQuery(sql, _StockParamHis.class)
	    							.setParameter("bssd", bssd)
	    							.getResultList();
		return rst;
	}
	
	public static List<_StockParamHis> getStockParamHis(String bssd, String stdAsstCd) {
		 String sql = "select a from StockParamHis a where baseYymm =:bssd and a.stdAsstCd = :stdAsstCd";
		 
	    List<_StockParamHis> rst = session.createQuery(sql, _StockParamHis.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("stdAsstCd", stdAsstCd)
	    							.getResultList();
		return rst;
	}
	

}	
