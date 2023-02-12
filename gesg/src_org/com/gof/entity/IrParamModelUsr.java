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
@Table(name ="E_IR_PARAM_MODEL_USR")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class IrParamModelUsr implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = 6515137062709711605L;

	@Id
	private String applStYymm;
	private String applEdYymm;

	@Id	
	private String irModelId;

	@Id	
	private String irCurveId;

	@Id
	private String paramTypCd;	
	
	private Double paramVal;	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;	

}


