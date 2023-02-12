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
@Table(name ="E_IR_VALID_PARAM_HW")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class IrValidParamHw implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = -7621117603455851091L;

	@Id
	private String baseYymm;
	
    @Id
	private String irModelId;	
	
	@Id
	private String irCurveId;

	@Id
	private Double swpnMatNum;
	
	@Id
	private Double swapTenNum;
	
	@Id
	private String validDv;
	
	private String validVal1;
	private String validVal2;
	private String validVal3;
	private String validVal4;	
	private String validVal5;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;	
	
}