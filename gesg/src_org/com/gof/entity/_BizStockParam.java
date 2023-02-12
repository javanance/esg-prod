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
@Table( name ="EAS_BIZ_APLY_ST_PARAM")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Slf4j
public class _BizStockParam implements Serializable{

	private static final long serialVersionUID = 2360652480675748510L;

	@Id	private String baseYymm;
	@Id	private String applBizDv;
	@Id	private String stdAsstCd;
	@Id	private String paramTypCd;	
	@Id private Integer matDayNum;	
	
	
	private Double applParamVal;

	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
}


