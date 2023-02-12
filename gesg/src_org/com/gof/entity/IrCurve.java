package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.gof.enums.EBoolean;
import com.gof.interfaces.EntityIdentifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name ="E_IR_CURVE")
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class IrCurve implements Serializable, EntityIdentifier {	
	
	private static final long serialVersionUID = -7079607534247603390L;

	@Id
	private String irCurveId;	
	
	private String irCurveNm;	
	private String curCd;	
	private String applMethDv;
	private String crdGrdCd;	
	private String intpMethCd;
	
	@Transient  //to be deleted
	private String applBizDv;
	@Transient  //to be deleted
	private String refCurveId;	
	
	@Enumerated(EnumType.STRING)
	private EBoolean useYn;
	
//	@Override
//	public String toString() {
//		return toString(",");
//	}
//	
//	public String toString(String delimeter) {
//		StringBuilder builder = new StringBuilder();
//		
//		builder.append(irCurveId).append(delimeter)
//			   .append(irCurveNm).append(delimeter)
//			   .append(curCd).append(delimeter)
//			   .append(applMethDv).append(delimeter)
//			   ;
//
//		return builder.toString();
//	}
	
//	@Override
//	public String toString() {
//		return irCurveId;
//	}
	
}
