package com.aiurt.modules.sparepart.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.mapper.SparePartStockInfoMapper;
import com.aiurt.modules.sparepart.service.ISparePartReturnOrderService;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

 /**
 * @Description: spare_part_return_order
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Api(tags="备件管理-备件退库")
@RestController
@RequestMapping("/sparepart/sparePartReturnOrder")
@Slf4j
public class SparePartReturnOrderController extends BaseController<SparePartReturnOrder, ISparePartReturnOrderService> {
	@Autowired
	private ISparePartReturnOrderService sparePartReturnOrderService;
	@Autowired
	private SparePartStockInfoMapper sparePartStockInfoMapper;
	 @Autowired
	 private ISysParamAPI iSysParamAPI;
	 @Autowired
	 private ISysBaseAPI sysBaseApi;
	 @Autowired
	 private ISTodoBaseAPI isTodoBaseAPI;
	/**
	 * 分页列表查询
	 *
	 * @param sparePartReturnOrder
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件退库-分页列表查询",permissionUrl = "/sparepart/sparePartReturnOrder/list")
	@ApiOperation(value="备件退库-分页列表查询", notes="备件退库-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent = "sparePartsFor/back")
	public Result<IPage<SparePartReturnOrder>> queryPageList(SparePartReturnOrder sparePartReturnOrder,
															 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
															 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
															 HttpServletRequest req) {
		Page<SparePartReturnOrder> page = new Page<SparePartReturnOrder>(pageNo, pageSize);
		List<SparePartReturnOrder> list = sparePartReturnOrderService.selectList(page, sparePartReturnOrder);
		page.setRecords(list);
		return Result.OK(page);
	}
	 /**
	  * 备件退库-获取退入仓库查询条件
	  *
	  * @param sparePartReturnOrder
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件退库-获取退入仓库查询条件",permissionUrl = "/sparepart/sparePartReturnOrder/list")
	 @ApiOperation(value="备件退库-获取退入仓库查询条件", notes="备件退库-获取退入仓库查询条件")
	 @GetMapping(value = "/selectList")
	 @PermissionData(pageComponent = "sparePartsFor/back")
	 public Result<?> selectList(SparePartReturnOrder sparePartReturnOrder, HttpServletRequest req) {
		 List<SparePartReturnOrder> list = sparePartReturnOrderService.selectList(null, sparePartReturnOrder);
		 List<String> newList = list.stream().map(SparePartReturnOrder::getWarehouseName).collect(Collectors.toList());
		 newList = newList.stream().distinct().collect(Collectors.toList());
		 newList.remove(null);
		 return Result.OK(newList);
	 }
	/**
	 *   添加
	 *
	 * @param sparePartReturnOrder
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加备件退库",permissionUrl = "/sparepart/sparePartReturnOrder/list")
	@ApiOperation(value="spare_part_return_order-添加", notes="spare_part_return_order-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SparePartReturnOrder sparePartReturnOrder) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(SparePartStockInfo::getWarehouseCode,sparePartReturnOrder.getMaterialCode());
		wrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
		SparePartStockInfo stockInfo = sparePartStockInfoMapper.selectOne(wrapper);
		if(null!=stockInfo){
			sparePartReturnOrder.setOrgId(stockInfo.getOrganizationId());
		}
		sparePartReturnOrder.setSysOrgCode(user.getOrgCode());
		sparePartReturnOrder.setUserId(user.getUsername());
		sparePartReturnOrderService.save(sparePartReturnOrder);


		try {
			//根据仓库编号获取仓库组织机构code
			String orgCode = sysBaseApi.getDepartByWarehouseCode(sparePartReturnOrder.getWarehouseCode());
			String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(Collections.singletonList(orgCode), Collections.singletonList(RoleConstant.FOREMAN));

			//发送通知
			MessageDTO messageDTO = new MessageDTO(user.getUsername(),userName, "备件退库-确认" + DateUtil.today(), null);

			//构建消息模板
			HashMap<String, Object> map = new HashMap<>();
			map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, sparePartReturnOrder.getId());
			map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_BACK.getType());
			map.put("materialCode",sparePartReturnOrder.getMaterialCode());
			String materialName= sysBaseApi.getMaterialNameByCode(sparePartReturnOrder.getMaterialCode());
			map.put("name",materialName);
			map.put("num",sparePartReturnOrder.getNum());
			String warehouseName= sysBaseApi.getWarehouseNameByCode(sparePartReturnOrder.getWarehouseCode());
			map.put("warehouseName",warehouseName);
			map.put("realName",user.getRealname());

			messageDTO.setData(map);
			//业务类型，消息类型，消息模板编码，摘要，发布内容
			/*messageDTO.setTemplateCode(CommonConstant.SPAREPARTRETURN_SERVICE_NOTICE);
			SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
			messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
			messageDTO.setMsgAbstract("备件退库申请");
			messageDTO.setPublishingContent("备件退库申请，请确认");
			messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
			sysBaseApi.sendTemplateMessage(messageDTO);*/
			//发送待办
			TodoDTO todoDTO = new TodoDTO();
			todoDTO.setData(map);
			SysParamModel sysParamModelTodo = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE_PROCESS);
			todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModelTodo) ? sysParamModelTodo.getValue() : "");
			todoDTO.setTitle("备件退库-确认" + DateUtil.today());
			todoDTO.setMsgAbstract("备件退库申请");
			todoDTO.setPublishingContent("备件退库申请，请确认");
			todoDTO.setCurrentUserName(userName);
			todoDTO.setBusinessKey(sparePartReturnOrder.getId());
			todoDTO.setBusinessType(TodoBusinessTypeEnum.SPAREPART_BACK.getType());
			todoDTO.setCurrentUserName(userName);
			todoDTO.setTaskType(TodoBusinessTypeEnum.SPAREPART_BACK.getType());
			todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
			todoDTO.setTemplateCode(CommonConstant.SPAREPARTRETURN_SERVICE_NOTICE);

			isTodoBaseAPI.createTodoTask(todoDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.OK("添加成功！");

	}

	/**
	 *  编辑
	 *
	 * @param sparePartReturnOrder
	 * @return
	 */
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑备件退库",permissionUrl = "/sparepart/sparePartReturnOrder/list")
	@ApiOperation(value="spare_part_return_order-编辑", notes="spare_part_return_order-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SparePartReturnOrder sparePartReturnOrder) {
		SparePartReturnOrder one = sparePartReturnOrderService.getById(sparePartReturnOrder.getId());
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		try {
			LoginUser userById = sysBaseApi.getUserByName(one.getUserId());

			//发送通知
			MessageDTO messageDTO = new MessageDTO(user.getUsername(),userById.getUsername(), "备件退库成功" + DateUtil.today(), null);

			//构建消息模板
			HashMap<String, Object> map = new HashMap<>();
			map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, one.getId());
			map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_BACK.getType());
			map.put("materialCode",one.getMaterialCode());
			String materialName= sysBaseApi.getMaterialNameByCode(one.getMaterialCode());
			map.put("name",materialName);
			map.put("num",one.getNum());
			String warehouseName= sysBaseApi.getWarehouseNameByCode(one.getWarehouseCode());
			map.put("warehouseName",warehouseName);
			map.put("realName",userById.getRealname());

			messageDTO.setData(map);
			//业务类型，消息类型，消息模板编码，摘要，发布内容
			messageDTO.setTemplateCode(CommonConstant.SPAREPARTRETURN_SERVICE_NOTICE);
			SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
			messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
			messageDTO.setMsgAbstract("备件出库申请通过");
			messageDTO.setPublishingContent("备件退库申请通过");
			messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
			sysBaseApi.sendTemplateMessage(messageDTO);
			// 更新待办
			isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.SPAREPART_BACK.getType(), one.getId(), user.getUsername(), "1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sparePartReturnOrderService.update(sparePartReturnOrder);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "通过id删除备件退库",permissionUrl = "/sparepart/sparePartReturnOrder/list")
	@ApiOperation(value="spare_part_return_order-通过id删除", notes="spare_part_return_order-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		SparePartReturnOrder sparePartReturnOrder = sparePartReturnOrderService.getById(id);
		sparePartReturnOrder.setDelFlag(CommonConstant.DEL_FLAG_1);
		sparePartReturnOrderService.updateById(sparePartReturnOrder);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询备件退库",permissionUrl = "/sparepart/sparePartReturnOrder/list")
	@ApiOperation(value="spare_part_return_order-通过id查询", notes="spare_part_return_order-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartReturnOrder> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartReturnOrder sparePartReturnOrder = sparePartReturnOrderService.getById(id);
		List<SparePartReturnOrder> list = sparePartReturnOrderService.selectListById(sparePartReturnOrder);
		list = list.stream().filter(sparePartReturnOrder1 -> sparePartReturnOrder1.getId().equals(id)).distinct().collect(Collectors.toList());
		if(CollUtil.isNotEmpty(list)){
			for (SparePartReturnOrder partReturnOrder : list) {
				sparePartReturnOrder = partReturnOrder;
			}
		}
		if(sparePartReturnOrder==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartReturnOrder);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param
    */
	@AutoLog(value = "导出",operateType = 6,operateTypeAlias = "导出备件退库",permissionUrl = "/sparepart/sparePartReturnOrder/list")
    @RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(@ApiParam(value = "行数据ids" ,required = true) @RequestParam("ids") String ids, HttpServletRequest request, HttpServletResponse response) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		SparePartReturnOrder sparePartReturnOrder = new SparePartReturnOrder();
		sparePartReturnOrder.setIds(Arrays.asList(ids.split(",")));
		List<SparePartReturnOrder> list = sparePartReturnOrderService.selectList(null, sparePartReturnOrder);
		list = list.stream().distinct().collect(Collectors.toList());
		for(int i=0;i<list.size();i++){
			SparePartReturnOrder order = list.get(i);
			order.setNumber(i+1+"");
		}
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "备件退库管理列表");
		mv.addObject(NormalExcelConstants.CLASS, SparePartReturnOrder.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件退库管理列表数据", "导出人:"+user.getRealname(), "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, list);
		return mv;
	}


}
