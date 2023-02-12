package com.gof.enums;

public enum EMatCd {
	  time ( "TIME")
	, job ( "JOB")
	, properties ( "PROPERTIES")
	;
	
	
	private String alias;

	private EMatCd(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}
	
}
