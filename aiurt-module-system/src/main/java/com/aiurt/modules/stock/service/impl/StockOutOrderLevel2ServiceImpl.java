package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsExport;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.service.ISparePartApplyService;
import com.aiurt.modules.stock.entity.*;
import com.aiurt.modules.stock.mapper.StockOutOrderLevel2Mapper;
import com.aiurt.modules.stock.service.IStockOutOrderLevel2Service;
import com.aiurt.modules.stock.service.IStockIncomingMaterialsService;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.aiurt.modules.stock.service.IStockOutboundMaterialsService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
import com.aiurt.modules.stock.entity.StockOutOrderLevel2;
import com.aiurt.modules.stock.mapper.StockOutOrderLevel2Mapper;
import com.aiurt.modules.stock.service.IStockOutOrderLevel2Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


/**
 * @Description: stock_out_order_level2
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
@Service
public class StockOutOrderLevel2ServiceImpl extends ServiceImpl<StockOutOrderLevel2Mapper, StockOutOrderLevel2> implements IStockOutOrderLevel2Service {

	@Autowired
	private ISparePartApplyService iSparePartApplyService;
	@Autowired
	private IStockOutboundMaterialsService iStockOutboundMaterialsService;

	@Override
	public IPage<StockOutOrderLevel2> pageList(Page<StockOutOrderLevel2> page, StockOutOrderLevel2 stockOutOrderLevel2) {
		List<StockOutOrderLevel2> baseList = baseMapper.pageList(page, stockOutOrderLevel2);
		page.setRecords(baseList);
		return page;
	}

	@Override
	public SparePartApply getList(String id) {
		StockOutOrderLevel2 stockOutOrderLevel2 = this.getById(id);
		String applyCode = stockOutOrderLevel2.getApplyCode();
		String orderCode = stockOutOrderLevel2.getOrderCode();
		SparePartApply sparePartApply = iSparePartApplyService.getOne(new QueryWrapper<SparePartApply>().eq("code",applyCode).eq("del_flag", CommonConstant.DEL_FLAG_0));
		List<StockOutboundMaterials> stockOutboundMaterials = iStockOutboundMaterialsService.list(new QueryWrapper<StockOutboundMaterials>().eq("out_order_code",orderCode).eq("del_flag", CommonConstant.DEL_FLAG_0));
		int count = 0;
		if(stockOutboundMaterials != null && stockOutboundMaterials.size()>0){
			for(StockOutboundMaterials materials : stockOutboundMaterials){
				materials = iStockOutboundMaterialsService.translate(materials);
				count += materials.getActualOutput()==null?0:materials.getActualOutput();
			}
		}
		sparePartApply.setUserId(stockOutOrderLevel2.getUserId());
		sparePartApply.setOutTime(stockOutOrderLevel2.getOutTime());
		sparePartApply.setTotalCount(count);
		sparePartApply.setStockOutboundMaterialsList(stockOutboundMaterials);
		return sparePartApply;
	}

	@Override
	public void confirmOutOrder(SparePartApply sparePartApply) {
		//1. 修改二级库出库表的信息（出库时间、出库操作用户）stock_out_order_level2
		//2. 修改二级库出库物资表信息（实际出库数量）stock_outbound_materials
		//3. 修改备件申领表信息（申领状态-已确认、出库时间）spare_part_apply
		//4. 修改备件申领物资表信息（实际出库数量）spare_part_apply_material
		//5. 备件入库表插入数据
	}


}
