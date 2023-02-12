package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;
import com.gof.interfaces.Pricable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name ="E_IR_CURVE_SPOT")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class IrCurveSpot implements Serializable, EntityIdentifier, Pricable {	
	
	private static final long serialVersionUID = 8405894865559378104L;
	
	@Id	
	private String baseDate; 
	
	@Id	
	@Column(name ="IR_CURVE_ID")
	private String irCurveId;
	
	@Id
	private String matCd;	
	
	private Double spotRate;		
	private String lastModifiedBy;	
	private LocalDateTime lastUpdateDate;	
	
	@ManyToOne
	@JoinColumn(name ="IR_CURVE_ID", insertable=false, updatable= false)
	private IrCurve irCurve;	
	
	public IrCurveSpot(String baseDate, String irCurveId, String matCd,String sceNo, Double intRate) {
		this.baseDate = baseDate;
		this.irCurveId = irCurveId;
		this.matCd = matCd;
		this.spotRate = intRate;
	}
	public IrCurveSpot(String baseDate, String matCd, Double intRate) {
		this.baseDate = baseDate;
		this.matCd = matCd;
		this.spotRate = intRate;
	}
	
	public IrCurveSpot(String bssd, IrCurveSpot curveHis) {
		this.baseDate = curveHis.getBaseDate();
		this.irCurveId = curveHis.getIrCurveId();
		this.matCd = curveHis.getMatCd();
		this.spotRate = curveHis.getSpotRate();				
	}
	
	@Override
	public String toString() {
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseDate).append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(spotRate).append(delimeter)
			   ;

		return builder.toString();
	}
//******************************************************Biz Method**************************************
//	@Transient
	public int getMatNum() {
		return Integer.parseInt(matCd.substring(1));
	}
	public IrCurveSpot addForwardTerm(String bssd) {
		return new IrCurveSpot(bssd, this);
	}
	
	public String getBaseYymm() {
		return getBaseDate().substring(0,6);
	}
	public boolean isBaseTerm() {
		if(matCd.equals("M0003") 
				|| matCd.equals("M0006") 
				|| matCd.equals("M0009")
				|| matCd.equals("M0012")
				|| matCd.equals("M0024")
				|| matCd.equals("M0036")
				|| matCd.equals("M0060")
				|| matCd.equals("M0084")
				|| matCd.equals("M0120")
				|| matCd.equals("M0240")
				) {
			return true;
		}
		return false;	
			
	}
	
	public IrSprdLp convertTo(String modelId) {
		IrSprdLp rst = new IrSprdLp();
		rst.setBaseYymm(this.getBaseYymm());
		rst.setDcntApplModelCd(modelId);
		rst.setMatCd(this.matCd);
		rst.setLiqPrem(this.getSpotRate());
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
		
	}

	public IrCurveSpotWeek convertToWeek() {
		IrCurveSpotWeek rst = new IrCurveSpotWeek();
		
		String dayOfWeek = LocalDate.parse(baseDate, DateTimeFormatter.BASIC_ISO_DATE).getDayOfWeek().name();
		rst.setBaseDate(this.baseDate);
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);
		rst.setSpotRate(this.spotRate);
		rst.setDayOfWeek(dayOfWeek);
		rst.setBizDayType("Y");
//		rst.setIrCurve(this.irCurve);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;		
	}		
	
	@Override
	public double getPrice() {
		return spotRate;
//		return Math.pow(intRate, -1.0* getMatNum()/12.0);
	}	
	
}
