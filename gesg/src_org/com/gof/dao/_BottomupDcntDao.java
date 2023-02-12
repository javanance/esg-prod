package com.gof.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.IrDcntRateBu;
import com.gof.entity.IrCurveSpot;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link IrDcntRateBu} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class _BottomupDcntDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	/** 
	*  BottomUp �������� �ݸ��Ⱓ������ ������   
	*  @param bssd 	   ���س��
	*  @param irCurveId  ������ �ݸ�� ID 
	*  @return        Bottom Up �������� �Ⱓ����   
	*/ 
	
	public static List<IrDcntRateBu> getTermStructure(String bssd, String irCurveId){
		StringBuilder builder = new StringBuilder();
		builder.append("select a from BottomupDcnt a "
				+ "where a.irCurveId = :irCurveId "
				+ "and a.baseYymm  = :bssd "
				)
		;
		
		Query<IrDcntRateBu> q = session.createQuery(builder.toString(), IrDcntRateBu.class);
		q.setParameter("bssd", bssd);
		q.setParameter("irCurveId", irCurveId);
		;
		log.debug("test : {}", bssd);
		
		return q.getResultList();
	}	
	
	/** 
	*  BottomUp ��������  ���� �̷� ������ ����
	*  <p> �������� ���� ���� �� �������� �̵� ����� �ʿ��� �� ����ϴ� Method ��   
	*  @param bssd 	   ���س��
	*  @param monNum  ���س�� ���� ���� 
	*  @return        Bottom Up ������  
	*/ 
	
	public static List<IrDcntRateBu> getPrecedingByMaturity(String bssd, int monNum, String irCurveId, String matCd){
		String query ="select a from BottomupDcnt a "
					+ "where a.irCurveId = :irCurveId "
					+ "and a.baseYymm > :stDate "
					+ "and a.baseYymm <= :endDate "
					+ "and a.matCd=:matCd"
					;
		
		Query<IrDcntRateBu> q = session.createQuery(query, IrDcntRateBu.class);
		q.setParameter("stDate", FinUtils.addMonth(bssd,  monNum));
		q.setParameter("endDate", bssd);
		q.setParameter("irCurveId", irCurveId);
		q.setParameter("matCd", matCd);
		;
		
		return q.getResultList();
	}
	
	public static IrDcntRateBu getShortRateHis(String bssd, String curveId){
		String query = "select a from BottomupDcnt a "
				+ "where 1=1 "
				+ "and a.baseYymm = :bssd	"
				+ "and a.irCurveId =:param1 "
				+ "and a.matCd = :matCd "
				+ "order by a.baseYymm"
				;
		
		IrDcntRateBu curveRst =  session.createQuery(query, IrDcntRateBu.class)
				.setParameter("param1", curveId)
				.setParameter("bssd", bssd)
				.setParameter("matCd", "M0003")
				.getSingleResult()
				;		
		

		return curveRst;
	}
	
		
//	public static List<BizDiscountRateUd> getBizDiscountRateUd(String bssd, String bizDv){
//		String query ="select a from BizDiscountRateUd a "
//					+ "where a.applyBizDv = :bizDv "
//					+ "and a.baseYymm = :bssd "
//		;
//		
//		Query<BizDiscountRateUd> q = session.createQuery(query, BizDiscountRateUd.class);
//		
//		q.setParameter("bssd", bssd);
//		q.setParameter("bizDv", bizDv);
//		
//		return q.getResultList();
//	}
}
