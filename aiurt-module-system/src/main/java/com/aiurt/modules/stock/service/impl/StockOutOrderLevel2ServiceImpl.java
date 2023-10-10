package com.aiurt.modules.stock.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.CodeGenerateUtils;
import com.aiurt.modules.material.constant.MaterialRequisitionConstant;
import com.aiurt.modules.material.dto.MaterialRequisitionInfoDTO;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.aiurt.modules.material.service.IMaterialRequisitionDetailService;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.mapper.SparePartStockInfoMapper;
import com.aiurt.modules.sparepart.service.ISparePartApplyMaterialService;
import com.aiurt.modules.sparepart.service.ISparePartApplyService;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.stock.dto.MaterialOutRequisitionDTO;
import com.aiurt.modules.stock.entity.*;
import com.aiurt.modules.stock.mapper.StockOutOrderLevel2Mapper;
import com.aiurt.modules.stock.service.*;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ICsUserDepartService;
import com.aiurt.modules.system.service.ISysDepartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: stock_out_order_level2
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
@Slf4j
@Service
public class StockOutOrderLevel2ServiceImpl extends ServiceImpl<StockOutOrderLevel2Mapper, StockOutOrderLevel2> implements IStockOutOrderLevel2Service {

	@Autowired
	private ISparePartApplyService iSparePartApplyService;
	@Autowired
	private IStockOutboundMaterialsService iStockOutboundMaterialsService;
	@Autowired
	private IMaterialStockOutInRecordService materialStockOutInRecordService;
	@Autowired
	private ISparePartApplyMaterialService iSparePartApplyMaterialService;
	@Autowired
	private ISparePartInOrderService iSparePartInOrderService;
    @Autowired
    private IStockLevel2CheckService iStockLevel2CheckService;
    @Autowired
    private IStockLevel2CheckDetailService iStockLevel2CheckDetailService;
    @Autowired
    private IStockLevel2Service stockLevel2Service;
    @Autowired
    private SparePartStockInfoMapper sparePartStockInfoMapper;
	@Autowired
	private ISysDepartService iSysDepartService;
	@Autowired
	private ISysBaseAPI sysBaseApi;
	@Autowired
	private ICsUserDepartService csUserDepartService;
	@Autowired
	private IMaterialRequisitionService materialRequisitionService;
	@Autowired
	private IMaterialRequisitionDetailService materialRequisitionDetailService;

	@Override
	public IPage<StockOutOrderLevel2> pageList(Page<StockOutOrderLevel2> page, StockOutOrderLevel2 stockOutOrderLevel2) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (Objects.isNull(user)) {
			throw new AiurtBootException("请重新登录");
		}
		// 根据当前登陆人的管理部门权限过滤（除了管理员角色用户）
		if (!user.getRoleCodes().contains(CommonConstant.ADMIN)) {
			List<CsUserDepartModel> departByUserId = csUserDepartService.getDepartByUserId(user.getId());
			if (CollUtil.isEmpty(departByUserId)) {
				return page;
			}
			List<String> collect = departByUserId.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
			stockOutOrderLevel2.setUserOrgCodes(collect);
		}
		List<StockOutOrderLevel2> baseList = baseMapper.pageList(page, stockOutOrderLevel2);
		page.setRecords(baseList);
		return page;
	}

	@Override
	public List<StockOutOrderLevel2> selectList() {
		List<StockOutOrderLevel2> baseList = baseMapper.selectListAll();
		return baseList;
	}

	@Override
	public MaterialOutRequisitionDTO getList(String id) {
		StockOutOrderLevel2 stockOutOrderLevel2 = this.getById(id);
		String applyCode = stockOutOrderLevel2.getApplyCode();
		String orderCode = stockOutOrderLevel2.getOrderCode();
		MaterialRequisitionInfoDTO materialRequisitionInfoDTO = materialRequisitionService.queryByCode(applyCode);
		MaterialOutRequisitionDTO materialOutRequisitionDTO = new MaterialOutRequisitionDTO();
		BeanUtil.copyProperties(materialRequisitionInfoDTO, materialOutRequisitionDTO);
		List<StockOutboundMaterials> stockOutboundMaterials = iStockOutboundMaterialsService.list(new LambdaQueryWrapper<StockOutboundMaterials>()
				.eq(StockOutboundMaterials::getOutOrderCode, orderCode)
				.eq(StockOutboundMaterials::getDelFlag, CommonConstant.DEL_FLAG_0));
		int count = 0;
		int applyTotalCount = 0;
		for (StockOutboundMaterials materials : stockOutboundMaterials) {
			materials = iStockOutboundMaterialsService.translate(materials);
			count += materials.getActualOutput() == null ? 0 : materials.getActualOutput();
			applyTotalCount += materials.getApplyOutput() == null ? 0 : materials.getApplyOutput();
		}
		// 二级库出库单对应的物资的出库仓库翻译，先将所有仓库的字典值转成key是仓库code,value的仓库名称的Map
		Set<String> warehouseCodeSet = stockOutboundMaterials.stream().map(StockOutboundMaterials::getWarehouseCode)
				.collect(Collectors.toSet());
		if (CollUtil.isNotEmpty(warehouseCodeSet)) {
			Map<String, String> warehouseMap = sysBaseApi.translateDictFromTableByKeys("stock_level2_info",
					"warehouse_name", "warehouse_code", String.join(",", warehouseCodeSet))
					.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
			stockOutboundMaterials.forEach(m->m.setWarehouseName(warehouseMap.get(m.getWarehouseCode())));
		}

		materialOutRequisitionDTO.setUserId(stockOutOrderLevel2.getUserId());
		materialOutRequisitionDTO.setOutTime(stockOutOrderLevel2.getOutTime());
		materialOutRequisitionDTO.setOutOrderRemark(stockOutOrderLevel2.getRemark());
		materialOutRequisitionDTO.setApplyTotalCount(applyTotalCount);
		materialOutRequisitionDTO.setTotalCount(count);
		materialOutRequisitionDTO.setOrderCode(orderCode);
		materialOutRequisitionDTO.setStockOutboundMaterialsList(stockOutboundMaterials);
		return materialOutRequisitionDTO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void confirmOutOrder(MaterialOutRequisitionDTO materialOutRequisitionDTO, StockOutOrderLevel2 stockOutOrderLevel2) throws ParseException {
        String warehouseCode = stockOutOrderLevel2.getWarehouseCode();
        List<StockLevel2Check> stockLevel2CheckList = iStockLevel2CheckService.list(new QueryWrapper<StockLevel2Check>().eq("del_flag", CommonConstant.DEL_FLAG_0)
                .eq("warehouse_code",warehouseCode).ne("status",CommonConstant.STOCK_LEVEL2_CHECK_STATUS_5));
		String orderCode = materialOutRequisitionDTO.getOrderCode();
		List<StockOutboundMaterials> stockOutboundMaterials = materialOutRequisitionDTO.getStockOutboundMaterialsList();
		//1. 修改二级库出库表的信息（出库时间、出库操作用户、备注）stock_out_order_level2
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date parse = sdf.parse(sdf.format(new Date()));
		stockOutOrderLevel2.setOutTime(parse);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		stockOutOrderLevel2.setUserId(user.getId());
		stockOutOrderLevel2.setRemark(materialOutRequisitionDTO.getOutOrderRemark());
		this.updateById(stockOutOrderLevel2);
		//2. 修改二级库出库物资表信息（实际出库数量）stock_outbound_materials
		iStockOutboundMaterialsService.updateBatchById(stockOutboundMaterials);
		//3. 修改备件申领表信息（申领状态-已确认、出库时间）spare_part_apply
		//更新领料单状态为已完成 material_requisition
		materialRequisitionService.update(new LambdaUpdateWrapper<MaterialRequisition>()
				.set(MaterialRequisition::getStatus, MaterialRequisitionConstant.STATUS_COMPLETED)
				.eq(MaterialRequisition::getCode, materialOutRequisitionDTO.getCode()));
		//4. 修改备件申领物资表信息（实际出库数量）material_requisition_detail
		List<MaterialRequisitionDetail> materialRequisitionDetailList = materialRequisitionDetailService.list(new LambdaQueryWrapper<MaterialRequisitionDetail>()
				.eq(MaterialRequisitionDetail::getMaterialRequisitionId, materialOutRequisitionDTO.getId())
				.eq(MaterialRequisitionDetail::getDelFlag, CommonConstant.DEL_FLAG_0));
		for (MaterialRequisitionDetail materialRequisitionDetail : materialRequisitionDetailList) {
			String materialCode = materialRequisitionDetail.getMaterialsCode();
			List<StockOutboundMaterials> collect = stockOutboundMaterials.stream().filter(s -> materialCode.equals(s.getMaterialCode())).collect(Collectors.toList());
			if (collect != null && collect.size() > 0) {
				materialRequisitionDetail.setActualNum(collect.get(0).getActualOutput());
			}
			//5. 备件入库表插入数据
			SparePartInOrder sparePartInOrder = new SparePartInOrder();
			sparePartInOrder.setMaterialCode(materialCode);
			sparePartInOrder.setWarehouseCode(stockOutOrderLevel2.getCustodialWarehouseCode());
			sparePartInOrder.setNum(null != materialRequisitionDetail.getActualNum() ? materialRequisitionDetail.getActualNum() : 1);
			SparePartStockInfo sparePartStockInfo = sparePartStockInfoMapper.selectOne(new LambdaQueryWrapper<SparePartStockInfo>().eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0).eq(SparePartStockInfo::getWarehouseCode, stockOutOrderLevel2.getCustodialWarehouseCode()));
			SysDepart sysDepart = new SysDepart();
			if (ObjectUtils.isNotEmpty(sparePartStockInfo)) {
				sysDepart = iSysDepartService.getById(sparePartStockInfo.getOrganizationId());
			}

			sparePartInOrder.setOrgId(null != sysDepart ? sysDepart.getId() : null);
			sparePartInOrder.setSysOrgCode(null != sysDepart ? sysDepart.getOrgCode() : null);
			sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_CONFRM_STATUS_0);
			sparePartInOrder.setOutOrderCode(orderCode);
			sparePartInOrder.setMaterialRequisitionId(materialOutRequisitionDTO.getId());
			sparePartInOrder.setApplyCode(materialOutRequisitionDTO.getCode());
			sparePartInOrder.setOrderCode(CodeGenerateUtils.generateSingleCode("3RK", 5));
			//全新数量等于入库数量
			sparePartInOrder.setNewNum(sparePartInOrder.getNum());
			iSparePartInOrderService.save(sparePartInOrder);
			//6. 二级库库存表数量修改
			StockLevel2 stockLevel2 = stockLevel2Service.getOne(new QueryWrapper<StockLevel2>().eq("material_code", materialCode).eq("warehouse_code", warehouseCode).eq("del_flag", CommonConstant.DEL_FLAG_0));
			if (stockLevel2 != null) {
				Integer num = stockLevel2.getNum();
				int balance = num - (null != materialRequisitionDetail.getActualNum() ? materialRequisitionDetail.getActualNum() : 1);
				stockLevel2.setNum(balance);
				stockLevel2Service.updateById(stockLevel2);
				// 6.5 更改出库出库物资表的库存结余
				LambdaUpdateWrapper<StockOutboundMaterials> stockOutboundMaterialsUpdateWrapper = new LambdaUpdateWrapper<>();
				stockOutboundMaterialsUpdateWrapper.eq(StockOutboundMaterials::getDelFlag, CommonConstant.DEL_FLAG_0);
				stockOutboundMaterialsUpdateWrapper.eq(StockOutboundMaterials::getOutOrderCode, stockOutOrderLevel2.getOrderCode());
				stockOutboundMaterialsUpdateWrapper.eq(StockOutboundMaterials::getMaterialCode, materialCode);
				stockOutboundMaterialsUpdateWrapper.eq(StockOutboundMaterials::getWarehouseCode, warehouseCode);
				stockOutboundMaterialsUpdateWrapper.set(StockOutboundMaterials::getBalance, balance);
				iStockOutboundMaterialsService.update(stockOutboundMaterialsUpdateWrapper);
			}
			//7. 如果存在盘点单，对盘点物资修改
			if (stockLevel2CheckList != null && stockLevel2CheckList.size() > 0) {
				for (StockLevel2Check stockLevel2Check : stockLevel2CheckList) {
					String stockCheckCode = stockLevel2Check.getStockCheckCode();
					StockLevel2CheckDetail stockLevel2CheckDetail = iStockLevel2CheckDetailService.getOne(new QueryWrapper<StockLevel2CheckDetail>()
							.eq("material_code", materialCode).eq("warehouse_code", warehouseCode).eq("del_flag", CommonConstant.DEL_FLAG_0)
							.eq("stock_check_code", stockCheckCode));
					if (stockLevel2CheckDetail != null) {
						Integer actualNum = materialRequisitionDetail.getActualNum();
						Integer bookNumber = stockLevel2CheckDetail.getBookNumber();
						if (bookNumber >= actualNum) {
							stockLevel2CheckDetail.setBookNumber(bookNumber - actualNum);
						}
						iStockLevel2CheckDetailService.updateById(stockLevel2CheckDetail);
					}
				}
			}
		}
		materialRequisitionDetailService.updateBatchById(materialRequisitionDetailList);

		// 添加二级库出库单信息到出入库记录表
		materialStockOutInRecordService.addOutRecordOfLevel2(stockOutOrderLevel2.getId());
	}


}
