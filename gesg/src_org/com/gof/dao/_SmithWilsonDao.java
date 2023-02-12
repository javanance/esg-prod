package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity._SmithWilsonParamHis;
import com.gof.util.HibernateUtil;

public class _SmithWilsonDao {
	
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<_SmithWilsonParamHis> getParamHisList(String bssd) {
		
		String q = "select a from _SmithWilsonParamHis a "
				 + " where 1=1 "
				 + "   and a.applStYymm <  :bssd "
				 + "   and a.applEdYymm >= :bssd "
				 ;		
		
		return session.createQuery(q, _SmithWilsonParamHis.class)
								.setParameter("bssd", bssd)
								.list()
		;

	}
	
}
