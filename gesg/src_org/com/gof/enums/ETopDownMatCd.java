package com.gof.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ETopDownMatCd {
	   
	   M0003 (0.25)
	 , M0006 (0.5)
	 , M0009 (0.75)
	 , M0012 (1.0)
	 , M0018 (1.5)
	 , M0024 (2.0)
	 , M0036 (3.0)
	 , M0048 (4.0)
	 , M0060 (5.0)
	 , M0084 (7.0)
	 , M0120 (10.0)
	 , M0240 (20.0)
	 ;
	
	
	private double yearFrac;

	private ETopDownMatCd(double yearFrac) {
		this.yearFrac = yearFrac;
	}

	public double getYearFrac() {
		return yearFrac;
	}

	public static List<String> names(){
		return Arrays.stream(values()).map(s->s.name()).collect(Collectors.toList());
	}

	public static double[] yearFracs(){
//		return Arrays.stream(values()).map(s->s.getYearFrac()).toArray(Double[]::new);
		return Arrays.stream(values()).mapToDouble(s->s.getYearFrac()).toArray();
		
	}
	
	
	public static String getBaseMatCd(String matCd) {
		return ETopDownMatCd.getBaseMatCdEnum(matCd).name();
	}
	
	/*public static ETopDownMatCd getBaseMatCdEnum(String matCd) {
		double matYearFrac = Double.parseDouble(matCd.split("M")[1]) /12  ;
		
		if(matYearFrac >= 20.0) {
			return ETopDownMatCd.M0240;
		}

//		ETopDownMatCd rst = ETopDownMatCd.M0120;
		ETopDownMatCd rst = ETopDownMatCd.M0003;
		for(ETopDownMatCd aa : ETopDownMatCd.values()) {
			if( aa.getYearFrac() <=  matYearFrac) {
				continue;
			}
			else {
				return aa ;
			}
		}
		return  rst;
	}*/
	
	public static ETopDownMatCd getBaseMatCdEnum(String matCd) {
		double matYearFrac = Double.parseDouble(matCd.split("M")[1]) /12  ;
		
		if(matYearFrac<=0.25) {
			return ETopDownMatCd.M0003;
		}

//		ETopDownMatCd rst = ETopDownMatCd.M0120;
		ETopDownMatCd rst = ETopDownMatCd.M0240;
		for(ETopDownMatCd aa : ETopDownMatCd.values()) {
			if( aa.getYearFrac() <  matYearFrac) {
				continue;
			}
			else {
				return aa ;
			}
		}
		return  rst;
	}
	
	public static ETopDownMatCd getBaseMatCdEnum(int ordinal) {
		for(ETopDownMatCd aa : ETopDownMatCd.values()) {
			if( aa.ordinal()==ordinal) {
				return aa;
			}
		}
//		return ETopDownMatCd.M0120;
		return ETopDownMatCd.M0240;

	}
}
