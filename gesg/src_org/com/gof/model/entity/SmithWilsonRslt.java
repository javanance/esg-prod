package com.gof.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.gof.entity.IrDcntRate;
import com.gof.entity._SmithWilsonResult;
import com.gof.model.IrModel;
import com.gof.util.DateUtil;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class SmithWilsonRslt implements Serializable {
	
	private static final long serialVersionUID = 3248965187822308189L;

	//@Id
	private String baseDate;
	
	private String resultType;
	
	private String scenType;
	
	private String matCd;
	
	private Double matTerm;
	
//	private LocalDate matDate;
	
	private Double dcntFactor;
	
	private Double spotCont;
	
	private Double spotDisc;
	
	private Double fwdCont;
	
	private Double fwdDisc;
	
	private String lastModifiedBy;	
	private LocalDateTime lastUpdateDate;
		
		
	public IrDcntRate convert() {

		IrDcntRate rslt = new IrDcntRate();		
				
		rslt.setMatCd(this.matCd);			
		rslt.setAdjSpotRate(this.spotDisc);
		rslt.setAdjFwdRate(this.fwdDisc);		
		rslt.setLastModifiedBy("GESG_" + this.getClass().getSimpleName());
		rslt.setLastUpdateDate(LocalDateTime.now());
		
		return rslt;		
	}
	
	
	public _SmithWilsonResult convert(String irCurveId) {
		
		_SmithWilsonResult rst = new _SmithWilsonResult();		
		
		rst.setBaseYymm(IrModel.dateToString(DateUtil.convertFrom(baseDate)).substring(0,6));		
		rst.setSceNo("0");
		rst.setIrCurveId(irCurveId);		
		rst.setMatCd(this.matCd);		
		rst.setTimeFactor(this.matTerm);
		rst.setMonthNum(Integer.valueOf(this.matCd.substring(1,5)));
		rst.setSpotCont(this.spotCont);
		rst.setSpotAnnual(this.spotDisc);
		rst.setDiscountFactor(this.dcntFactor);
		rst.setFwdCont(this.fwdCont);
		rst.setFwdAnnual(this.fwdDisc);
		rst.setFwdMonthNum(1);
		
		return rst;
	}
	
}

