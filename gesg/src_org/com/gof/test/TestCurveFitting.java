package com.gof.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import com.gof.dao._LiqPremiumDao;
import com.gof.entity.IrSprdLp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestCurveFitting {
	public static void main(String[] args) {
		
		List<WeightedObservedPoint> obsList= new ArrayList<WeightedObservedPoint>();

		double weight = 1.0/5.0;
		
		obsList.add(new WeightedObservedPoint(weight,0.0254623, 531026.0));
		obsList.add(new WeightedObservedPoint(weight,4.03128248, 984167.0));
		obsList.add(new WeightedObservedPoint(weight,4.03839603, 1887233.0));
		obsList.add(new WeightedObservedPoint(weight,4.04421621, 2687152.0));
		obsList.add(new WeightedObservedPoint(weight,4.05132976, 3461228.0));
		
		
		WeightedObservedPoints obs= new WeightedObservedPoints();
		obs.add(0.0254623, 531026.0);
		obs.add(4.03128248, 984167.0);
		obs.add(4.03839603, 1887233.0);
		obs.add(4.04421621, 2687152.0);
		obs.add(4.05132976, 3461228.0);
		obs.add(4.05326982, 3580526.0);
		obs.add(4.05779662, 3439750.0);
		obs.add(4.0636168,  2877648.0);
		
		log.info("aaa :");
//		GaussianCurveFitter fit= GaussianCurveFitter.create().
		
//		double[] param = GaussianCurveFitter.create()
//								.withMaxIterations(10000000)
//								.fit(obs.toList());
//		
//		Arrays.stream(param).forEach(s -> log.info("zzz : {}", s));
		
				
		List<IrSprdLp> liqPremList    = _LiqPremiumDao.getLiqPremium("201812", "COVERED_BOND_KDB");
		
		WeightedObservedPoints obs1= new WeightedObservedPoints();
		
		for(IrSprdLp aa : liqPremList) {
			if(Integer.parseInt(aa.getMatCd().substring(1))< 240) {
				log.info("liq : {},{}", Integer.parseInt(aa.getMatCd().substring(1)), aa.getLiqPrem());
				obs1.add((double)Integer.parseInt(aa.getMatCd().substring(1)) /12.0, aa.getLiqPrem());
			}
		}
		
		obs1.add(0.0, 0.0);
		obs1.add(20.0, 0.0);
		
		double[] param1 = PolynomialCurveFitter.create(4)
				.withMaxIterations(100)
				.fit(obs1.toList());

		Arrays.stream(param1).forEach(s -> log.info("qqq : {}", s));
		
		PolynomialFunction polFn = new PolynomialFunction(param1);
		
		for(int i =0; i<= 240; i++) {
			log.info("zzz  : {},{}", i/12.0 ,polFn.value((double)i/12.0));
		}
		
		
	}
}
