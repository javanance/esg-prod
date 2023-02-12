package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;
import com.gof.interfaces.IIntRate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="EAS_IR_SCE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class _IrSce implements Serializable, EntityIdentifier, IIntRate {

	private static final long serialVersionUID = 4458482460359847563L;

	@Id	private String baseDate;
    @Id	private String irModelId;
    @Id private String irCurveId;	
    @Id	private String matCd;
    @Id	private String sceNo;

	private Double rfRate;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
//	public IrSce() {}

	public IrCurveSpot convertTo() {
		IrCurveSpot rst = new IrCurveSpot(this.baseDate, this.irCurveId, this.matCd, this.sceNo, this.rfRate);
		
		return rst;
	}
	
	@Override
	public Double getSpread() {
		return 0.0;
	}
	@Override
	public String getBaseYymm() {
		return getBaseDate().substring(0,6);
	}

	@Override
	public Double getIntRate() {
		return getRfRate();
	}
	
//	@Transient
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
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseDate).append(delimeter)
			   .append(irModelId).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(sceNo).append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(rfRate).append(delimeter)
			   .append("0.0").append(delimeter)
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate);
		
		return builder.append("\n").toString();
	}

}


