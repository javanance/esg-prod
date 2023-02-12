package com.gof.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.IrDcntRate;
import com.gof.entity.IrDcntSceStoBiz;
import com.gof.entity.IrDcntRateUsr;
import com.gof.entity.IrDcntRateBu;
import com.gof.entity.IrCurveSpotWeek;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link IrDcntRateBu} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class _BizDiscountRateDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	
	
	public static List<IrDcntRate> getTermStructure(String bssd, String bizDv, String irCurveId){
		StringBuilder builder = new StringBuilder();
		builder.append("select a from BizDiscountRate a "
				+ "where a.irCurveId = :irCurveId "
				+ "and a.baseYymm  = :bssd "
				+ "and a.applyBizDv  = :bizDv "
				)
		;
		
		Query<IrDcntRate> q = session.createQuery(builder.toString(), IrDcntRate.class);
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		q.setParameter("irCurveId", irCurveId);
		;
		log.debug("Term Sructure param : {},{},{}", bssd, bizDv, irCurveId);
		
		return q.getResultList();
	}
	
	
	public static List<IrDcntRate> getTermStructure(String bssd, String bizDv, String irCurveId, List<String> tenorList){
		StringBuilder builder = new StringBuilder();
		builder.append("select a from BizDiscountRate a "
				+ "where a.irCurveId = :irCurveId "
				+ "and a.baseYymm  = :bssd "
				+ "and a.applyBizDv  = :bizDv "
				+ "and a.matCd in (:matCdList)"
				)
		;
		
		Query<IrDcntRate> q = session.createQuery(builder.toString(), IrDcntRate.class);
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		q.setParameter("irCurveId", irCurveId);
		q.setParameterList("matCdList", tenorList)
		;
		log.debug("Term Sructure param : {},{},{}, {}", bssd, bizDv, irCurveId, tenorList);
		
		return q.getResultList();
	}
	
	
	public static List<IrDcntSceStoBiz> getTermStructureBySceNo(String bssd, String bizDv, String irCurveId,  String sceNo){
		StringBuilder builder = new StringBuilder();
		builder.append("select a from BizDiscountRateSce a "
					 + "where 1=1 "
					 + "and a.baseYymm  = :bssd "
					 + "and a.applyBizDv = :bizDv "
					 + "and a.irCurveId = :irCurveId "
					 + "and a.sceNo  = :sceNo "
					)
		;
		
		Query<IrDcntSceStoBiz> q = session.createQuery(builder.toString(), IrDcntSceStoBiz.class);
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		q.setParameter("irCurveId", irCurveId);
		q.setParameter("sceNo", sceNo);
		;
		
		return q.getResultList();
	}


	public static List<IrDcntSceStoBiz> getTermStructureAllScenario(String bssd, String bizDv, String irCurveId){
			StringBuilder builder = new StringBuilder();
			builder.append("select a from BizDiscountRateSce a "
					+ "where a.baseYymm  = :bssd "
					+ "and a.applyBizDv = :bizDv "
					+ "and a.irCurveId = :irCurveId "
	//				+ "and a.sceNo  = :sceNo"
					)
			;
			
			Query<IrDcntSceStoBiz> q = session.createQuery(builder.toString(), IrDcntSceStoBiz.class);
			q.setParameter("bssd", bssd);
			q.setParameter("bizDv", bizDv);
			q.setParameter("irCurveId", irCurveId);
	//		q.setParameter("sceNo", sceNo);
			;
			
			return q.getResultList();
		}


	public static List<IrDcntRate> getBizPrecedingByMaturity(String bssd, int monNum, String irCurveId, String matCd){
		String query ="select a from BizDiscountRate a "
					+ "where a.irCurveId = :irCurveId "
					+ "and a.baseYymm > :stDate "
					+ "and a.baseYymm <= :endDate "
					+ "and a.matCd=:matCd"
					;
		
		Query<IrDcntRate> q = session.createQuery(query, IrDcntRate.class);
		q.setParameter("stDate", FinUtils.addMonth(bssd,  monNum));
		q.setParameter("endDate", bssd);
		q.setParameter("irCurveId", irCurveId);
		q.setParameter("matCd", matCd);
		;
		
		return q.getResultList();
	}
	
	public static List<IrDcntRate> getTimeSeries(String bssd, String bizDv, String irCurveId, String matCd, int monNum){
		StringBuilder builder = new StringBuilder();
		builder.append("select a from BizDiscountRate a "
				+ "where a.baseYymm <= :bssd "
				+ "and a.baseYymm  >=  :stBssd "
				+ "and a.applyBizDv  = :bizDv "
				+ "and a.irCurveId   = :irCurveId "
				+ "and a.matCd = :matCd"
				+ "order by a.baseYymm desc"
				)
		;
		
		Query<IrDcntRate> q = session.createQuery(builder.toString(), IrDcntRate.class);
		q.setParameter("bssd", bssd);
		q.setParameter("stBssd", FinUtils.addMonth(bssd, monNum));
		q.setParameter("bizDv", bizDv);
		q.setParameter("irCurveId", irCurveId);
		q.setParameter("matCd", matCd);
		;
		
		return q.getResultList();
	}
	
	public static List<IrDcntRate> getTimeSeries(String bssd, String bizDv, String irCurveId, int monNum){
		StringBuilder builder = new StringBuilder();
		builder.append("select a from BizDiscountRate a "
				+ "where a.baseYymm <= :bssd "
				+ "and a.baseYymm  >=  :stBssd "
				+ "and a.applyBizDv = :bizDv "
				+ "and a.irCurveId = :irCurveId "
//				+ "and a.matCd = :matCd "
				
				)
		;
		
		Query<IrDcntRate> q = session.createQuery(builder.toString(), IrDcntRate.class);
		q.setParameter("bssd", bssd);
		q.setParameter("stBssd", FinUtils.addMonth(bssd,  monNum));
		q.setParameter("bizDv", bizDv);
		q.setParameter("irCurveId", irCurveId);
//		q.setParameter("matCd", matCd);
		;
		
		return q.getResultList();
	}

	public static List<IrDcntRateUsr> getBizDiscountRateUd(String bssd, String bizDv){
		String query ="select a from BizDiscountRateUd a "
					+ "where a.applyBizDv = :bizDv "
					+ "and a.baseYymm = :bssd "
		;
		
		Query<IrDcntRateUsr> q = session.createQuery(query, IrDcntRateUsr.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		
		return q.getResultList();
	}
}
