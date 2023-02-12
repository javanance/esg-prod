package com.gof.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gof.entity._SmithWilsonParamHis;

public class EsgConstant {
	
	private static Map<String, String> strConstant = new HashMap<String, String>();
	private static Map<String, Double> numConstant = new HashMap<String, Double>();
	private static Map<String, _SmithWilsonParamHis> smParam = new HashMap<String, _SmithWilsonParamHis>();
	private static List<String> tenorList = new ArrayList<String>();	
	
	public static Map<String, String> getStrConstant() {
		return strConstant;
	}

	public static Map<String, Double> getNumConstant() {
		return numConstant;
	}

	public static Map<String, _SmithWilsonParamHis> getSmParam() {
		return smParam;
	}

	public static void setSmParam(Map<String, _SmithWilsonParamHis> smParam) {
		EsgConstant.smParam = smParam;
	}
	
	public static List<String> getTenorList(){
		return tenorList;
	}
	
//	public static List<String> getReverseTenorList(){
//		tenorList.sort(String::com);
//		Collections.sort(tenorList, String ::compareTo);
//		return reversTenorList;
//	}
	
	public static void setTenorList(List<String> tenorStr){
		tenorList =tenorStr;
	}
}
