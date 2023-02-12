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
	
	public IrCurveSpot convertSimple() {
		
		IrCurveSpot spot = new IrCurveSpot();
		
		spot.setBaseDate(this.baseDate);		
		spot.setIrCurveId(this.irCurveId);		
		spot.setMatCd(this.matCd);
		spot.setSpotRate(this.ytm);		
		spot.setLastModifiedBy("GESG_" + this.getClass().getSimpleName());
		spot.setLastUpdateDate(LocalDateTime.now());
		
		return spot;
	}		
	
	
	public IrCurveYtm addSpread(double spread) {
		
		IrCurveYtm addYtm = new IrCurveYtm();
		
		addYtm.setBaseDate(this.baseDate);		
		addYtm.setIrCurveId(this.irCurveId);		
		addYtm.setMatCd(this.matCd);
		addYtm.setYtm(this.ytm+spread);		
		addYtm.setLastModifiedBy("GESG_" + this.getClass().getSimpleName());
		addYtm.setLastUpdateDate(LocalDateTime.now());
		
		return addYtm;
	}		
}