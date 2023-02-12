package test;

import java.util.List;

import org.hibernate.Session;

import com.gof.dao.IrCurveDao;
import com.gof.entity.IrCurve;
import com.gof.enums.EBoolean;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class dbtest {
	
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	public static void main(String[] args) {
		
		
		
		String q = " select a from IrCurve a "
				 + "  where 1=1              " 
				 + "    and a.useYn = :useYn "
				 ;		
		
		session.createQuery(q, IrCurve.class)
				.setParameter("useYn"     , EBoolean.Y)
				.getResultList().forEach(s->log.info("aaa : ", s.toString()));
		
		
//		List<IrCurve> irList= IrCurveDao.getIrCurveList();
//		
//		for( IrCurve aa : irList) {
//			System.out.println("aaa :" + aa.getIrCurveId());
//		}

	}

}
