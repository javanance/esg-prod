package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="EAS_CORP_CRD_GRD_CUM_PD")
@Getter
@Setter
public class _CorpCumPd implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	private String baseYymm;

	@Id
	@Column(name = "CRD_EVAL_AGNCY_CD")
	private String agencyCode;

	@Id
	@Column(name = "CRD_GRD_CD")
	private String gradeCode;

	@Id
	private String matCd;

	private Double cumPd;
	private Double fwdPd;
	private Double vol;
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
	
	@Override
	public String toString() {
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseYymm).append(delimeter)
			   .append(agencyCode).append(delimeter)
			   .append(gradeCode).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(cumPd).append(delimeter)
			   .append(fwdPd).append(delimeter)
			   .append(vol)
			   ;
		
		return builder.toString();
	}
		
}
