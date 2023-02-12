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
@Table(name ="EAS_BIZ_APLY_STD_ASST_YIELD")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class _BizStockYield implements Serializable {

	private static final long serialVersionUID = 4458482460359847563L;

	@Id	private String baseYymm;
    @Id	private String applBizDv;
    @Id private String stdAsstCd;	
    @Id	private String fwdMatCd;

	private Double asstYield;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public _BizStockYield(String baseYymm, String applBizDv, String stdAsstCd, String fwdMatCd, Double asstYield) {
		this.baseYymm = baseYymm;
		this.applBizDv = applBizDv;
		this.stdAsstCd = stdAsstCd;
		this.fwdMatCd = fwdMatCd;
		this.asstYield = asstYield;
		this.lastModifiedBy = "ESG";
		this.lastUpdateDate = LocalDateTime.now();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		return builder.append(baseYymm).append(",")
				.append(applBizDv).append(",")
				.append(stdAsstCd).append(",")
				.append(fwdMatCd).append(",")
				.toString();
		
	}
	
}


