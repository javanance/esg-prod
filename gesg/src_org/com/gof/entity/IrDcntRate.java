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
@Table(name ="E_IR_DCNT_RATE")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class IrDcntRate implements Serializable, EntityIdentifier {

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
	

	//TODO: to be deleted!!!
	public IrCurveSpot convert() {
		
		IrCurveSpot rst = new IrCurveSpot();

		rst.setBaseDate(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);
		rst.setSpotRate(this.getAdjSpotRate());
		
		return rst;
	}	
	
	
	public IrDcntRateBiz convertAdj() {
		
		IrDcntRateBiz adjDcnt = new IrDcntRateBiz();
		
		adjDcnt.setBaseYymm(this.baseYymm);		
		adjDcnt.setApplBizDv(this.applBizDv  + "_L");
		adjDcnt.setIrCurveId(this.irCurveId);
		adjDcnt.setIrCurveSceNo(this.irCurveSceNo);
		adjDcnt.setMatCd(this.matCd);			
		adjDcnt.setSpotRate(this.adjSpotRate);
		adjDcnt.setFwdRate(this.adjFwdRate);
		adjDcnt.setLastModifiedBy("GESG_" + this.getClass().getSimpleName());
		adjDcnt.setLastUpdateDate(LocalDateTime.now());
		
		return adjDcnt;
	}
	
	
	public IrDcntRateBiz convertBase() {
		
		IrDcntRateBiz baseDcnt = new IrDcntRateBiz();
		
		baseDcnt.setBaseYymm(this.baseYymm);		
		baseDcnt.setApplBizDv(this.applBizDv + "_A");
		baseDcnt.setIrCurveId(this.irCurveId);
		baseDcnt.setIrCurveSceNo(this.irCurveSceNo);		
		baseDcnt.setMatCd(this.matCd);			
		baseDcnt.setSpotRate(this.spotRate);
		baseDcnt.setFwdRate(this.fwdRate);
		baseDcnt.setLastModifiedBy("GESG_" + this.getClass().getSimpleName());
		baseDcnt.setLastUpdateDate(LocalDateTime.now());
		
		return baseDcnt;
	}	
	

}