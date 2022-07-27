package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsExport;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.entity.SparePartApplyMaterial;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.service.ISparePartApplyMaterialService;
import com.aiurt.modules.sparepart.service.ISparePartApplyService;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
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
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
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
import java.util.stream.Collectors;

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
	@Autowired
	private ISparePartApplyMaterialService iSparePartApplyMaterialService;
	@Autowired
	private ISparePartInOrderService iSparePartInOrderService;

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
	public void confirmOutOrder(SparePartApply sparePartApply) throws ParseException {
		String orderCode = sparePartApply.getOrderCode();
		List<StockOutboundMaterials> stockOutboundMaterials = sparePartApply.getStockOutboundMaterialsList();
		//1. 修改二级库出库表的信息（出库时间、出库操作用户）stock_out_order_level2
		StockOutOrderLevel2 stockOutOrderLevel2 = this.getOne(new QueryWrapper<StockOutOrderLevel2>().eq("order_code",orderCode).eq("del_flag", CommonConstant.DEL_FLAG_0));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date parse = sdf.parse(sdf.format(new Date()));
		stockOutOrderLevel2.setOutTime(parse);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		stockOutOrderLevel2.setUserId(user.getId());
		this.updateById(stockOutOrderLevel2);
		//2. 修改二级库出库物资表信息（实际出库数量）stock_outbound_materials
		iStockOutboundMaterialsService.updateBatchById(stockOutboundMaterials);
		//3. 修改备件申领表信息（申领状态-已确认、出库时间）spare_part_apply
		sparePartApply.setStatus(CommonConstant.SPARE_PART_APPLY_STATUS_3);
		sparePartApply.setStockOutTime(parse);
		iSparePartApplyService.updateById(sparePartApply);
		//4. 修改备件申领物资表信息（实际出库数量）spare_part_apply_material
		List<SparePartApplyMaterial> sparePartApplyMaterials = iSparePartApplyMaterialService.list(new QueryWrapper<SparePartApplyMaterial>().eq("del_flag", CommonConstant.DEL_FLAG_0)
				.eq("apply_id",sparePartApply.getId()).eq("apply_code",sparePartApply.getCode()));
		if(sparePartApplyMaterials != null && sparePartApplyMaterials.size()>0){
			for(SparePartApplyMaterial sparePartApplyMaterial : sparePartApplyMaterials){
				String materialCode = sparePartApplyMaterial.getMaterialCode();
				List<StockOutboundMaterials> collect = stockOutboundMaterials.stream().filter(s -> materialCode.equals(s.getMaterialCode())).collect(Collectors.toList());
				if(collect != null && collect.size()>0){
					sparePartApplyMaterial.setActualNum(collect.get(0).getActualOutput());
				}
				//5. 备件入库表插入数据
				SparePartInOrder sparePartInOrder = new SparePartInOrder();
				sparePartInOrder.setMaterialCode(materialCode);
				sparePartInOrder.setWarehouseCode(stockOutOrderLevel2.getWarehouseCode());
				sparePartInOrder.setNum(sparePartApplyMaterial.getActualNum());
				sparePartInOrder.setOrgId(user.getOrgId());
				sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_CONFRM_STATUS_0);
				sparePartInOrder.setOutOrderCode(orderCode);
				iSparePartInOrderService.save(sparePartInOrder);
			}
			iSparePartApplyMaterialService.updateBatchById(sparePartApplyMaterials);
		}
	}


}
