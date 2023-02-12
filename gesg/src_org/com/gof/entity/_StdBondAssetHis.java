package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.Pricable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="EAS_STD_ASST_KTB_HIS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
//@Setter(value = AccessLevel.PROTECTED)
//@ToString(callSuper = false)
//@EqualsAndHashCode(of= {"irModelId", "irModelNm", "irModelTyp"}, callSuper = false)

public class _StdBondAssetHis implements Serializable, Pricable{
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id	private String baseDate;
	@Id	private String stdAsstCd;
	
	private Double ktbYield;
	private Double intRate;
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public double getTenor() {
		return Double.parseDouble(stdAsstCd.substring(3, 4));
	}
	
	@Override
	public double getPrice() {
		return 1.0 / Math.pow(1+ktbYield/100.0, getTenor());
	}
}