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
@Table(name ="E_IR_PARAM_SW")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class IrParamSw implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = -4175243759288891655L;

	@Id
	private String baseYymm;

	@Id
	private String applBizDv;	

	@Id	
	private String irCurveId;
	
	@Id	
	private Integer irCurveSceNo;
	
	private String curCd;
	private Integer freq;
	private Integer llp;
	private Double ltfr;
	private Integer ltfrCp;
	private Double liqPrem;
	private String liqPremApplDv;	
	private Double swAlphaYtm;		
	private String stoSceGenYn;	
	private String fwdMatCd;	
	private Double multIntRate;
	private Double addSprd;
	private String pvtRateMatCd;	
	private Double multPvtRate;	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;	

}


