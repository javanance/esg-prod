package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="EAS_PARAM_SMITH_WILSON_HIS")
@Getter
@Setter
@EqualsAndHashCode
public class _SmithWilsonParamHis implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id
	private String applStYymm;
	
	@Id
	private String applEdYymm;
	
	@Id
	private String curCd;	
	
	@Column(name ="LLP")
	private Double llp;
	
	@Column(name ="UFR")
	private Double ufr;

	@Column(name ="UFR_T")
	private Double ufrT;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;

	
	public _SmithWilsonParamHis() {
		super();
	}
	
	public _SmithWilsonParamHis(String curCd, double ufr, double ufrT) {
		super();
		this.curCd = curCd;
		this.ufr = ufr;
		this.ufrT = ufrT;
	}

	
	
	
	
}


