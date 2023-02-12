package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity._BizStockParam;
import com.gof.entity._BizStockParamUd;
import com.gof.entity.IrDcntRateBu;
import com.gof.util.HibernateUtil;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class _BizStockParamDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<_BizStockParam> getBizStockParam(String bssd, String bizDv) {
		 String sql = "select a from BizStockParam a where baseYymm =:bssd and a.applBizDv = :bizDv ";
		 
	    List<_BizStockParam> rst = session.createQuery(sql, _BizStockParam.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("bizDv", bizDv)
	    							.getResultList();
		return rst;
	}
	
	
	public static List<_BizStockParam> getBizStockParam(String bssd, String bizDv, String paramTypCd) {
		 String sql = "select a from BizStockParam a where baseYymm =:bssd and a.applBizDv = :bizDv and a.paramTypCd =:paramTypCd ";
		 
	    List<_BizStockParam> rst = session.createQuery(sql, _BizStockParam.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("bizDv", bizDv)
	    							.setParameter("paramTypCd", paramTypCd)
	    							.getResultList();
		return rst;
	}
	
	public static List<_BizStockParamUd> getBizStockParamUd(String bssd, String bizDv) {
		 String sql = "select a from BizStockParamUd a where baseYymm =:bssd and a.applBizDv = :bizDv ";
		 
	    List<_BizStockParamUd> rst = session.createQuery(sql, _BizStockParamUd.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("bizDv", bizDv)
	    							.getResultList();
		return rst;
	}
}	
