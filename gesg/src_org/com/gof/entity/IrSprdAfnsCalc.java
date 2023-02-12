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
import lombok.ToString;

@Entity
@Table(name ="E_IR_SPRD_AFNS_CALC")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class IrSprdAfnsCalc implements Serializable, EntityIdentifier {	
	
	private static final long serialVersionUID = 6332350473680597191L;

	@Id
	private String baseYymm; 
	
	@Id
	private String irModelId;
	
	@Id
	private String irCurveId;	
	
	@Id	
	private Integer irCurveSceNo;
	
	@Id
	private String matCd;	
	
	private Double shkSprdCont;	
	private String lastModifiedBy;	
	private LocalDateTime lastUpdateDate;	
	
}
