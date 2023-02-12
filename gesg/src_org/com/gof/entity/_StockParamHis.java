package com.gof.entity;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Entity
@Table( name ="EAS_ST_PARAM_CALC_HIS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Slf4j
public class _StockParamHis implements Serializable{

	private static final long serialVersionUID = 2360652480675748510L;

	@Id	private String baseYymm;
	@Id	private String stdAsstCd;
	@Id	private String paramCalcCd;	
	@Id	private String paramTypCd;	
//	@Column(name = "MAT_CD")
//	@Id	private String matCd;
	
	@Id private Integer matDayNum;	
	
	
	private Double paramVal;

	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public boolean isLocalVol() {
		return "LOCAL_VOL".equals(paramCalcCd) ? true: false;
	}
	
	public _BizStockParam convert(String bizDiv) {
		return _BizStockParam.builder()
					.baseYymm(this.getBaseYymm())
					.applBizDv(bizDiv)
					.stdAsstCd(this.getStdAsstCd())
					.paramTypCd(this.getParamTypCd())
					.matDayNum(this.getMatDayNum())
					.applParamVal(this.getParamVal())
					.lastModifiedBy("ESG")
					.lastUpdateDate(LocalDateTime.now())
					.build()
					;
	}
	
		
}


