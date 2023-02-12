package com.gof.model.entity;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.gof.entity.IrParamHwBiz;
import com.gof.entity.IrParamHwCalc;

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
public class Hw1fCalibParas implements Serializable {
	
	private static final long serialVersionUID = -3028479667862393941L;

	private String baseDate;
	
	private Integer monthSeq;
	
	private String matCd;
	
	private Double alpha;
	
	private Double sigma;	
	
	public List<IrParamHwCalc> convert(String irModelId, String irCurveId) {
		
		List<IrParamHwCalc> rstList = new ArrayList<IrParamHwCalc>();
		
		IrParamHwCalc alphaRst  = new IrParamHwCalc();
		if(matCd.equals("M0120") || matCd.equals("M0240")) {
			alphaRst.setBaseYymm(this.baseDate.substring(0,6));
			alphaRst.setIrModelId(irModelId);
			alphaRst.setIrCurveId(irCurveId);
			alphaRst.setMatCd(this.matCd);
			alphaRst.setParamTypCd("ALPHA");			
			alphaRst.setParamVal(this.alpha);
			rstList.add(alphaRst);
		}
		
		IrParamHwCalc sigmaRst  = new IrParamHwCalc();		
		sigmaRst.setBaseYymm(baseDate.substring(0, 6));
		sigmaRst.setIrModelId(irModelId);
		sigmaRst.setIrCurveId(irCurveId);
		sigmaRst.setMatCd(this.matCd);
		sigmaRst.setParamTypCd("SIGMA");
		sigmaRst.setParamVal(this.sigma);				
		rstList.add(sigmaRst);
		
		return rstList;
	}

	
	public static List<Hw1fCalibParas> convertFrom(List<IrParamHwBiz> bizParam) {
		
		List<Hw1fCalibParas> rstList = new ArrayList<Hw1fCalibParas>();
		
		double alpha1  = bizParam.stream().filter(s->s.getMatCd().equals("M0240") && s.getParamTypCd().equals("ALPHA")).mapToDouble(IrParamHwBiz::getParamVal).sum();
		double alpha2  = bizParam.stream().filter(s->s.getMatCd().equals("M1200") && s.getParamTypCd().equals("ALPHA")).mapToDouble(IrParamHwBiz::getParamVal).sum();		
		double sigmaLt = bizParam.stream().filter(s->s.getMatCd().equals("M1200") && s.getParamTypCd().equals("SIGMA")).mapToDouble(IrParamHwBiz::getParamVal).sum();
		
		List<IrParamHwBiz> sigmaList = bizParam.stream().filter(s-> s.getParamTypCd().equals("SIGMA")).collect(toList());				
		
		for(IrParamHwBiz aa : sigmaList) {
			Hw1fCalibParas temp = new Hw1fCalibParas();
			temp.baseDate= aa.getBaseYymm();
			temp.matCd = aa.getMatCd();
			temp.monthSeq = Integer.valueOf(aa.getMatCd().split("M")[1]);
			temp.sigma = aa.getParamVal();

			temp.alpha = temp.monthSeq <= 240 ? alpha1: alpha2;
			
			rstList.add(temp);
		}		
		
		List<IrParamHwBiz> alphaList = bizParam.stream().filter(s-> s.getParamTypCd().equals("ALPHA")).collect(toList());
		
		for(IrParamHwBiz bb : alphaList) {
			for(IrParamHwBiz aa : sigmaList) {
				
				if(!bb.getMatCd().equals(aa.getMatCd())) {
					Hw1fCalibParas temp2 = new Hw1fCalibParas();
					temp2.baseDate= bb.getBaseYymm();
					temp2.matCd = bb.getMatCd();
					temp2.monthSeq = Integer.valueOf(bb.getMatCd().split("M")[1]);
					temp2.sigma = sigmaLt;

					temp2.alpha = temp2.monthSeq <= 240 ? alpha1: alpha2;
					
					rstList.add(temp2);					
				}
			}
		}		
		return rstList;
	}
}
