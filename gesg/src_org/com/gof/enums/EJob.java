package com.gof.enums;

public enum EJob {
	
	  ESG100 ("Pre-Process")
	, ESG101 ("Set Smith-Wilson Attribute")
//	, ESG102 ("Set Swaption Volatility from USER")
	, ESG102 ("test")
	
	, ESG110 ("YTM to SPOT by Smith-Wilson Method")
	, ESG111 ("YTM to SPOT by Smith-Wilson Method Migration")

	, ESG210 ("AFNS Weekly Input TermStructure Setup")
	, ESG220 ("AFNS Shock Spread")
	
	, ESG230 ("Set Liquidity Premium")	
	, ESG240 ("Biz Applied Liquidity Premium")
	
	, ESG250 ("BottomUp Risk Free TermStructure with Liquidity Premium")	
	, ESG260 ("Interpolated TermStructure by SW")
	, ESG270 ("Biz Applied TermStructure by SW")
//	, ESG280 ("TermStructure Forecast by Forward")
	, ESG280 ("TEMPLATE")
	
	, ESG310 ("Calibration of HW1F Model Parameter")
	, ESG320 ("Validation of Calibration Result of HW1F Model")
	, ESG330 ("Biz Applied HW1F Model Parameter")		
	
	, ESG340 ("Stochastic Scenario of Biz TermStructure")	
	, ESG350 ("Validation for Random Number of Stochastic Scenario")
	, ESG360 ("Validation for Market Consistency of Stochastic Scenario")
	, ESG370 ("Bond Yield Scenario of Biz TermStructure")
	, ESG380 ("Validation for Market Consistency of Bond Yield Scenario")

	, ESG810 ("Probability of Default from Transition Matrix")
	
	, ESG4	("Historical Vol")
	, ESG5	("Histocical Correlation")
	, ESG6	("Stock Parameter Creation")
	, ESG7	("Biz Stock Param")	

	, ESG33	("Std Asset Yield Scenario AVG")
	, ESG34	("Std Asset Yield Deterministic Scenario")
	, ESG35	("Std Asset Yield Scenario from irCurve_IFRS")
	, ESG36	("Std Asset Yield Scenario from irCurve_KICS")	

	
	, ESG82	("IFRSCorpPd")
	, ESG83	("KICSCorpPd")
	
	, ESG86	("LGD")
	, ESG87	("IFRSLGD")
	, ESG88	("KICSLGD")
	;
	
	private String jobName;

	private EJob(String jobName) {
		this.jobName = jobName;
	}

	public String getJobName() {
		return jobName;
	}
	
}
