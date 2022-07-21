package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.aiurt.modules.stock.entity.StockInOrderLevel2;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.mapper.StockInOrderLevel2Mapper;
import com.aiurt.modules.stock.service.IStockIncomingMaterialsService;
import com.aiurt.modules.stock.service.IStockInOrderLevel2Service;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockInOrderLevel2ServiceImpl extends ServiceImpl<StockInOrderLevel2Mapper, StockInOrderLevel2> implements IStockInOrderLevel2Service {

	@Autowired
	private StockInOrderLevel2Mapper stockInOrderLevel2Mapper;
	@Autowired
	private IStockIncomingMaterialsService stockIncomingMaterialsService;
	@Autowired
	private IMaterialBaseService materialBaseService;
	@Autowired
	private IStockLevel2Service stockLevel2Service;

	@Override
	public StockInOrderLevel2 getSubmitPlanCode() {
		QueryWrapper<StockInOrderLevel2> queryWrapper = new QueryWrapper<>();
		String str = "TBJH";
		Date date = new Date("yyyy-MM-dd");
		str += date.toString();
		queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		queryWrapper.likeRight("create_time",str);
		queryWrapper.orderByDesc("create_time");
		queryWrapper.last("limit 1");
		StockInOrderLevel2 stockInOrderLevel2 = stockInOrderLevel2Mapper.selectOne(queryWrapper);
		String format = "";
		if(stockInOrderLevel2 != null){
			String code = stockInOrderLevel2.getOrderCode();
			String numstr = code.substring(code.length()-3);
			format = String.format("%03d", Long.parseLong(numstr) + 1);
		}else{
			format = "001";
		}
		StockInOrderLevel2 stockInOrderLevel2res = new StockInOrderLevel2();
		stockInOrderLevel2res.setOrderCode(str + format);
		Date dateres = new Date("yyyy-MM-dd HH:mm");
		stockInOrderLevel2res.setSubmitTime(dateres);
		return stockInOrderLevel2res;
	}

	@Override
	public void add(StockInOrderLevel2 stockInOrderLevel2) {
		List<StockIncomingMaterials> stockIncomingMaterialsList = stockInOrderLevel2.getStockIncomingMaterialsList();
		if(stockIncomingMaterialsList != null && stockIncomingMaterialsList.size()>0){
			stockIncomingMaterialsService.saveBatch(stockIncomingMaterialsList);
		}
		this.save(stockInOrderLevel2);
	}

	@Override
	public boolean edit(StockInOrderLevel2 stockInOrderLevel2) {
		String code = stockInOrderLevel2.getOrderCode();
		QueryWrapper<StockIncomingMaterials> queryWrapper = new QueryWrapper<StockIncomingMaterials>();
		queryWrapper.eq("in_order_code",code);
		stockIncomingMaterialsService.remove(queryWrapper);
		List<StockIncomingMaterials> stockIncomingMaterialsList = stockInOrderLevel2.getStockIncomingMaterialsList();
		if(stockIncomingMaterialsList != null && stockIncomingMaterialsList.size()>0){
			stockIncomingMaterialsService.saveBatch(stockIncomingMaterialsList);
		}
		boolean ok = this.updateById(stockInOrderLevel2);
		return ok;
	}

	@Override
	public boolean submitPlan(Integer status, String code) {
		StockInOrderLevel2 stockInOrderLevel2 = this.getOne(new QueryWrapper<StockInOrderLevel2>().eq("code",code));
		stockInOrderLevel2.setStatus(status);
		String warehouseCode = stockInOrderLevel2.getWarehouseCode();//仓库编号
		Date stockInTime = stockInOrderLevel2.getStockInTime();
		List<StockIncomingMaterials> stockIncomingMaterialsList = stockIncomingMaterialsService.list(new QueryWrapper<StockIncomingMaterials>().eq("in_order_code",code).eq("del_flag", CommonConstant.DEL_FLAG_0));
		if(stockIncomingMaterialsList != null && stockIncomingMaterialsList.size()>0){
			for(StockIncomingMaterials stockIncomingMaterials : stockIncomingMaterialsList){
				String materialCode = stockIncomingMaterials.getMaterialCode();//物资编号
				Integer number = stockIncomingMaterials.getNumber();//数量
				StockLevel2 stockLevel2 = stockLevel2Service.getOne(new QueryWrapper<StockLevel2>().eq("material_code",materialCode).eq("warehouse_code",warehouseCode).eq("del_flag", CommonConstant.DEL_FLAG_0));
				if(stockLevel2 != null){
					Integer num = stockLevel2.getNum();
					stockLevel2.setNum(num + number);
					stockLevel2.setStockInTime(stockInTime);
					stockLevel2Service.updateById(stockLevel2);
				}else{
					MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",materialCode));
					StockLevel2 stockLevel2new = new StockLevel2();
					stockLevel2new.setMaterialCode(materialCode);
					stockLevel2new.setBaseTypeCode(materialBase.getBaseTypeCodeCc());
					stockLevel2new.setWarehouseCode(warehouseCode);
					stockLevel2new.setNum(number);
					stockLevel2new.setMajorCode(materialBase.getMajorCode());
					stockLevel2new.setSystemCode(materialBase.getSystemCode());
					stockLevel2new.setStockInTime(stockInTime);
					stockLevel2Service.save(stockLevel2new);
				}
			}
		}
		boolean ok = this.updateById(stockInOrderLevel2);
		return ok;
	}
}
