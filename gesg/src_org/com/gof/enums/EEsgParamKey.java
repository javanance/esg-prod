package com.gof.enums;

public enum EEsgParamKey {
	  
	
	 jobList ("JOB")
	,outputDir("OUTPUT_DIR")
	,batchNum ("BATCH_NUM")
	,dnsVolAdjust("DNS_VOL_ADJUST") 

	,dnsErrorTolerance("DNS_ERROR_TOLERANCE") 
	,hwErrorTolerance ("HW_ERROR_TOLERANCE")
	,hw2ErrorTolerance ("HW2_ERROR_TOLERANCE")

	,irSceCurrency("IR_SCE_CURRENCY")
	;
	
	
	private String alias;

	private EEsgParamKey(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}
	
}
