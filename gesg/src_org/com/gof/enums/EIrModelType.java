package com.gof.enums;

public enum EIrModelType {
	   MERTON    ("1")
	 , VASICEK 	 ("2")
	 , HOLEE 	 ("3")
	 , HULLWHITE ("4")
	 , CIR	     ("5")
	 , HW2       ("6")
;
	
	private String legacyCode;

	private EIrModelType(String legacy) {
		this.legacyCode = legacy;
	}

	public String getLegacyCode() {
		return legacyCode;
	}

	public static EIrModelType getEIrModelType(String legacyCode) {
		for(EIrModelType aa : EIrModelType.values()) {
			if(aa.getLegacyCode().equals(legacyCode)) {
				return aa;
			}
		}
		return HULLWHITE;
	}

}
