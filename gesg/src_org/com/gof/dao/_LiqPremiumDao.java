package com.gof.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.IrSprdLpBiz;
import com.gof.entity.IrSprdLpUsr;
import com.gof.entity.IrDcntRateBu;
import com.gof.entity.IrSprdLp;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link IrDcntRateBu} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class _LiqPremiumDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	private static String  getApplyDateLiqPremiumUd(String bssd, String bizDv){
		String maxQuery = "select max(a.applyStartYymm) from BizLiqPremiumUd a "
				+ "		where 1=1"
				+ "		and a.applyBizDv = :bizDv"
				+ "		and a.applyStartYymm <= :bssd"
				+ "		and a.applyEndYymm >=  :bssd"
		;
		
		Query<String> q = session.createQuery(maxQuery, String.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		;
		
		log.info("Applied Date for User Input Liq premium : {}", q.getSingleResult());
		return q.getSingleResult();
	}
	
	public static List<IrSprdLpUsr> getLiqPremiumUd(String bssd){
		
		String query = "select a from BizLiqPremiumUd a "
				+ "		where 1=1"
				+ "		and a.applyBizDv = :bizDv"
				+ "		and a.applyStartYymm = :stDate"
		;
		
		Query<IrSprdLpUsr> q = session.createQuery(query, IrSprdLpUsr.class);
		
		q.setParameter("stDate", getApplyDateLiqPremiumUd(bssd, "I"));
		q.setParameter("bizDv", "I");
		
		
		return q.getResultList();
	}
	
	public static List<IrSprdLpUsr> getLiqPremiumUd(String bssd,String bizDv){
		
		String query = "select a from BizLiqPremiumUd a "
				+ "		where 1=1"
				+ "		and a.applyBizDv = :bizDv"
				+ "		and a.applyStartYymm = :stDate"
		;
		
		Query<IrSprdLpUsr> q = session.createQuery(query, IrSprdLpUsr.class);
		
		q.setParameter("stDate", getApplyDateLiqPremiumUd(bssd, bizDv));
		q.setParameter("bizDv", bizDv);
		
		
		return q.getResultList();
	}

	public static List<IrSprdLp> getLiqPremium(String bssd, String modelId, String order){
		
		String query = "select a from LiqPremium a "
				+ "where a.modelId = :modelId "
				+ "and a.baseYymm = :bssd "
				+ "order by a.matCd :desc"
		;
		
		Query<IrSprdLp> q = session.createQuery(query, IrSprdLp.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("modelId", modelId);
		q.setParameter("desc", order);
		
		return q.getResultList();
	}
	
	public static List<IrSprdLp> getLiqPremium(String bssd, String modelId){
		
		String query = "select a from LiqPremium a "
				+ "where a.modelId = :modelId "
				+ "and a.baseYymm = :bssd "
				
		;
		
		Query<IrSprdLp> q = session.createQuery(query, IrSprdLp.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("modelId", modelId);
		
		return q.getResultList();
	}

	public static List<IrSprdLpBiz> getBizLiqPremium(String bssd){
		
		String query = "select a from BizLiqPremium a "
					+  "where a.applyBizDv = :bizDv "
					+  "and a.baseYymm = :bssd"
					;
		
		Query<IrSprdLpBiz> q = session.createQuery(query, IrSprdLpBiz.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", "I");
		
		return q.getResultList();
	}
	
	public static List<IrSprdLpBiz> getBizLiqPremium(String bssd, String bizDv){
		
		String query = "select a from BizLiqPremium a "
					+  "where a.applyBizDv = :bizDv "
					+  "and a.baseYymm = :bssd"
					;
		
		Query<IrSprdLpBiz> q = session.createQuery(query, IrSprdLpBiz.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		
		return q.getResultList();
	}
}
