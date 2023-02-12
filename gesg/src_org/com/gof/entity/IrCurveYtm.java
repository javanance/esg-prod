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
@Table(name ="E_IR_CURVE_YTM")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class IrCurveYtm implements Serializable, EntityIdentifier {	
	
	private static final long serialVersionUID = 1340116167808300605L;

	@Id
	private String baseDate;	

	@Id
	private String irCurveId;	
	
	@Id
	private String matCd;
	
	private Double ytm;	
	private String lastModifiedBy;	
	private LocalDateTime lastUpdateDate;	
	
}