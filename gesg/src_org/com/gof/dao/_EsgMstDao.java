package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.IrParamModel;
import com.gof.enums.EBoolean;
import com.gof.util.HibernateUtil;

public class _EsgMstDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	private static String baseQuery = "select a from EsgMst a where 1=1 ";

	public static List<IrParamModel> getEntities() {
		return session.createQuery(baseQuery, IrParamModel.class).getResultList();
	}

	
	public static List<IrParamModel> getEsgMst(EBoolean useYn) {
		String q = "select a from EsgMst a where a.useYn = :param";
		
		return session.createQuery(q, IrParamModel.class)
				.setParameter("param", useYn)
				.getResultList();
	}
}
