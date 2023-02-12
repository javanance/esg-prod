package com.gof.enums;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ERunSettings {
		JOB						( "STRING_ARRAY", 	"")
	,	TENOR_LIST				( "STRING_ARRAY",	"M0003,M0006,M0009,M0012,M0024,M0036,M0060,M0084,M0120,M0240")
	,	IR_CURVE_CURRENCY		( "STRING_ARRAY",	"KRW, USD")
	,	ESG_RF_KRW_ID			( "STRING_ARRAY",	"1010000")
	,	FLUSH_SIZE				( "INT",			"10000")

	,	IR_SCE_CUR_CD			( "STRING",			"KRW")
	, 	LP_FIT_MODEL			( "STRING",			"POLY_FITTING")			// POLY_FITTING,  SMITH_WILSON FITTING
	
	, 	IFRS_IR_MODEL_ID 		( "STRING", 		"HW1_SURFACE")			//TODO :NM
	,	KICS_IR_MODEL_ID 		( "STRING",			"HW1_SURFACE")
	
	, 	IFRS_LIQ_MODEL_NM 		( "STRING", 		"COVERED_BOND_DIFF")
	,	KICS_LIQ_MODEL_NM  		( "STRING",			"KICS_VOL_ADJ")
	
	,	LIQ_KTB_CURVE_NM  		( "STRING",			"1010000")
	,	LIQ_KDB_CURVE_NM  		( "STRING",			"5010110")
	,	LIQ_AVG_NUM		  		( "INT",			"-36")
	,	KICS_VOL_ADJ			( "DOUBLE",			"0.0032")

	, 	ALPHA_OUTER_AVG_NUM 	( "INT", 			"-36")
	, 	SIGMA_OUTER_AVG_NUM 	( "INT", 			"-36")
	, 	ALPHA_OUTER_MAT_CD		( "STRING", 		"M0240")
	, 	SIGMA_OUTER_MAT_CD 		( "STRING", 		"M0120")
	  
	,	UFR_MAP 				( "DOUBLE_MAP",		"KRW:0.052, USD:0.042")
	,	UFRT_MAP				( "INT_MAP"	  , 	"KRW:60, USD: 60")	

	,	BATCH_NUM				( "INT",			"10")
	,	PROJECTION_YEAR			( "INT",			"101")	
	
	,	DECAY_FACTOR			( "DOUBLE",			"0.97")
	,	BOND_YIELD_TGT_DURATION	( "DOUBLE",			"4.5")

	,	SPOT_SW_ALPHA			( "DOUBLE",			"0.1")
	,	SPOT_SW_LLP				( "INT",			"20")
	,	SPOT_SW_LIQ_PREM		( "DOUBLE",			"0")
	,	SPOT_SW_FREQ			( "INT",			"2")
	
	,	AFNS_MODE				( "STRING",			"AFNS")
	,	AFNS_START_DATE			( "STRING",			"20100101")
	,	AFNS_HIST_CURVE_ID		( "STRING",			"AFNS_KRW")
	,	AFNS_BASE_CURVE_ID		( "STRING",			"1010000")
	,	AFNS_REAL_INT_RATE		( "BOOLEAN",		"Y")
	,	AFNS_CMPD_TYPE			( "STRING",			"DISC")
	,	AFNS_REAL_NUMBER_BL		( "BOOLEAN",		"Y")
	,	AFNS_DT_DENOM			( "DOUBLE",			"52.0")
	,	AFNS_ERROR_TOL			( "DOUBLE",			"1E-10")
	,	AFNS_KALMAN_ITR_MAX		( "INT",			"100")
	,	AFNS_CONF_INTERVAL		( "DOUBLE",			"0.995")
	,	AFNS_SIGMA_INIT			( "DOUBLE",			"0.05")
	,	AFNS_LAMBDA_MIN			( "DOUBLE",			"0.05")
	,	AFNS_LAMBDA_MAX			( "DOUBLE",			"2")
	,	AFNS_EPSILON_INIT		( "DOUBLE",			"0.001")
	,	AFNS_DAY_COUNT_BASIS	( "INT",			"1")
	,	AFNS_PROJ_YEAR			( "INT",			"140")
	,	AFNS_LTFR_INSU			( "DOUBLE",			"0.045")
	,	AFNS_LTFR_ASSET			( "DOUBLE",			"0.045")
	,	AFNS_LTFR_TERM			( "INT",			"60")
	,	AFNS_KICS_VOL_ADJ		( "DOUBLE",			"0.0032")
	,	AFNS_TENOR_LIST			( "STRING_ARRAY",	"M0003,M0006,M0009,M0012,M0018,M0024,M0030,M0036,M0048,M0060,M0084,M0120,M0240")
	,	AFNS_INIT_PARAM			( "DOUBLE_ARRAY",	"0.01, 0.02")
	,	AFNS_INIT_LCS			( "DOUBLE_ARRAY",	"0.01, 0.02")
	;
	
	
	private String type;
	private String value;

	private ERunSettings(String type, String value) {
		this.type 	= type;
		this.value 	= value;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStringValue() {
		if(type.equals("STRING")) {
			return value;
		}
		return value;
	}
	
	public int getIntValue() {
		if(type.equals("INT")) {
			return Integer.valueOf(value);
		}
		else {
			log.error("Data Type Error in ERunSettings : {}", this.name());
			System.exit(1);
		}
		return 0;
		
	}
	public boolean getBooleanValue() {
		if(type.equals("BOOLEAN")) {
			return value=="Y" ? true: false;
		}
		else {
			log.error("Data Type Error in ERunSettings : {}", this.name());
			System.exit(1);
		}
		return false;
	}
	
	public double  getDoubleValue() {
		if(type.equals("DOUBLE")) {
			return Double.valueOf(value);
		}
		else {
			log.error("Data Type Error in ERunSettings : {}", this.name());
			System.exit(1);
		}
		return 0.0;
	}
	
	public List<String> getStringList(){
		if(type.equals("STRING_ARRAY")) {
			return Arrays.stream(value.split(",")).map(s -> s.trim()).collect(Collectors.toList());
		}
		else {
			log.error("Data Type Error in ERunSettings : {}", this.name());
			System.exit(1);
		}
		return new ArrayList<String>();
	}
	
	public List<Double> getDoubleList(){
		if(type.equals("DOUBLE_ARRAY")) {
			return Arrays.stream(value.split(",")).map(s -> s.trim()).mapToDouble(s->Double.valueOf(s)).boxed().collect(toList());
		}
		else {
			log.error("Data Type Error in ERunSettings : {}", this.name());
			System.exit(1);
		}
		return new ArrayList<Double>();
	}
	
	public Map<String, Double> getDoubleMap(){
		
		if(type.equals("DOUBLE_MAP")) {
			return Arrays.stream(value.split(",")).collect(toMap(s-> s.split(":")[0].trim(), s->Double.valueOf(s.split(":")[1].trim())));
		}
		else {
			log.error("Data Type Error in ERunSettings : {}", this.name());
			System.exit(1);
		}
		return new HashMap<String, Double>();
	}
	
	public Map<String, Integer> getIntMap(){
		
		if(type.equals("INT_MAP")) {
			return Arrays.stream(value.split(",")).collect(toMap(s-> s.split(":")[0].trim(), s->Integer.valueOf(s.split(":")[1].trim())));
		}
		else {
			log.error("Data Type Error in ERunSettings : {}", this.name());
			System.exit(1);
		}
		return new HashMap<String, Integer>();
	}
}
