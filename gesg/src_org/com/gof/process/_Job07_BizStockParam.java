package com.gof.process;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.gof.dao._BizStockParamDao;
import com.gof.dao._EsgRandomDao;
import com.gof.dao.IrCurveSpotDao;
import com.gof.dao._StdAssetDao;
import com.gof.dao._StockParamHisDao;
import com.gof.entity.IrParamHwBiz;
import com.gof.entity._BizStockParam;
import com.gof.entity._BizStockParamUd;
import com.gof.entity.IrParamModel;
import com.gof.entity.IrParamHwRnd;
import com.gof.entity.IrCurveSpot;
import com.gof.entity._IrSce;
import com.gof.entity.StdAsst;
import com.gof.enums.EIrModelType;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class _Job07_BizStockParam {
	
	public static List<_BizStockParam> createBizStockParam(String bssd, String bizDv, String localVolYn) {
		List<_BizStockParamUd> bizUd  = _BizStockParamDao.getBizStockParamUd(bssd, bizDv);

		List<String> bizUdStock = bizUd.stream().map(_BizStockParamUd::getStdAsstCd).collect(toList());

		List<_BizStockParam> bizParam = bizUd.stream().map(_BizStockParamUd::convert).collect(toList());

		if(localVolYn.equals("Y")) {
			bizParam.addAll( _StockParamHisDao.getStockParamHis(bssd).stream()
											 .filter(s->s.getParamTypCd().equals("SIGMA"))
											 .filter(s->s.isLocalVol())
											 .filter(s-> !bizUdStock.contains(s.getStdAsstCd()))
											 .map(s-> s.convert(bizDv))
											 .collect(toList())
							);
			
			List<String> bizStock = bizParam.stream().map(s->s.getStdAsstCd()).collect(toList());
			
			bizParam.addAll( _StockParamHisDao.getStockParamHis(bssd).stream()
								 .filter(s->s.getParamTypCd().equals("SIGMA"))
								 .filter(s->!s.isLocalVol())
								 .filter(s-> !bizStock.contains(s.getStdAsstCd()))
								 .map(s-> s.convert(bizDv))
								 .collect(toList())
							);
		}
		else {
			bizParam.addAll( _StockParamHisDao.getStockParamHis(bssd).stream()
								 .filter(s->s.getParamTypCd().equals("SIGMA"))
								 .filter(s->!s.isLocalVol())
								 .filter(s-> !bizUdStock.contains(s.getStdAsstCd()))
								 .map(s-> s.convert(bizDv))
								 .collect(toList())
							);
		}
		return bizParam;
	}
}

