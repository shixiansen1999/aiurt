package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.stock.entity.StockSubmitMaterials;
import com.aiurt.modules.stock.entity.StockSubmitPlan;
import com.aiurt.modules.stock.mapper.StockSubmitPlanMapper;
import com.aiurt.modules.stock.service.IStockSubmitMaterialsService;
import com.aiurt.modules.stock.service.IStockSubmitPlanService;
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
public class StockSubmitPlanServiceImpl extends ServiceImpl<StockSubmitPlanMapper, StockSubmitPlan> implements IStockSubmitPlanService {

	@Autowired
	private StockSubmitPlanMapper stockSubmitPlanMapper;
	@Autowired
	private IStockSubmitMaterialsService stockSubmitMaterialsService;
	@Autowired
	private SysBaseApiImpl sysBaseApi;

	@Override
	public StockSubmitPlan getSubmitPlanCode() {
		QueryWrapper<StockSubmitPlan> queryWrapper = new QueryWrapper<>();
		String str = "TBJH";
		Date date = new Date("yyyy-MM-dd");
		str += date.toString();
		queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		queryWrapper.likeRight("create_time",str);
		queryWrapper.orderByDesc("create_time");
		queryWrapper.last("limit 1");
		StockSubmitPlan stockSubmitPlan = stockSubmitPlanMapper.selectOne(queryWrapper);
		String format = "";
		if(stockSubmitPlan != null){
			String code = stockSubmitPlan.getCode();
			String numstr = code.substring(code.length()-3);
			format = String.format("%03d", Long.parseLong(numstr) + 1);
		}else{
			format = "001";
		}
		StockSubmitPlan stockSubmitPlanres = new StockSubmitPlan();
		stockSubmitPlanres.setCode(str + format);
		Date dateres = new Date("yyyy-MM-dd HH:mm");
		stockSubmitPlanres.setSubmitTime(dateres);
		return stockSubmitPlanres;
	}

	@Override
	public void add(StockSubmitPlan stockSubmitPlan) {
		List<StockSubmitMaterials> stockSubmitMaterialsList = stockSubmitPlan.getStockSubmitMaterialsList();
		if(stockSubmitMaterialsList != null && stockSubmitMaterialsList.size()>0){
			stockSubmitMaterialsService.saveBatch(stockSubmitMaterialsList);
		}
		this.save(stockSubmitPlan);
	}

	@Override
	public boolean edit(StockSubmitPlan stockSubmitPlan) {
		String code = stockSubmitPlan.getCode();
		QueryWrapper<StockSubmitMaterials> queryWrapper = new QueryWrapper<StockSubmitMaterials>();
		queryWrapper.eq("submit_plan_code",code);
		stockSubmitMaterialsService.remove(queryWrapper);
		List<StockSubmitMaterials> stockSubmitMaterialsList = stockSubmitPlan.getStockSubmitMaterialsList();
		if(stockSubmitMaterialsList != null && stockSubmitMaterialsList.size()>0){
			stockSubmitMaterialsService.saveBatch(stockSubmitMaterialsList);
		}
		boolean ok = this.updateById(stockSubmitPlan);
		return ok;
	}
}
