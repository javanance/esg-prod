package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="E_IR_DCNT_RATE_USR")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class IrDcntRateUsr implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = -4252300668894647002L;
	
	@Id	
	private String baseYymm;
	
	@Id
	private String applBizDv;
	
	@Id	
	private String irCurveId;

	@Id	
	private Integer irCurveSceNo;

	@Id	
	private String matCd;
	
	private Double spotRate;
	private Double fwdRate;
	private Double adjSpotRate;
	private Double adjFwdRate;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
}