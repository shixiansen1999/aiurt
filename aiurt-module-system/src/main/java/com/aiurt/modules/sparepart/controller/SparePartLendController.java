package com.aiurt.modules.sparepart.controller;

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
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.sparepart.entity.SparePartLend;
import com.aiurt.modules.sparepart.service.ISparePartLendService;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.todo.dto.TodoDTO;
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
import java.util.*;
import java.util.stream.Collectors;

 /**
 * @Description: spare_part_lend
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Api(tags="备件管理-备件借入管理")
@RestController
@RequestMapping("/sparepart/sparePartLend")
@Slf4j
public class SparePartLendController extends BaseController<SparePartLend, ISparePartLendService> {
	 @Autowired
	 private ISparePartLendService sparePartLendService;
	 @Autowired
	 private ISysDepartService sysDepartService;
	 @Autowired
	 private ISysBaseAPI sysBaseApi;
	 @Autowired
	 private ISysParamAPI iSysParamAPI;
	 @Autowired
	 private ISTodoBaseAPI isTodoBaseAPI;
	/**
	 * 分页列表查询
	 *
	 * @param sparePartLend
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询备件借入",permissionUrl = "/sparepart/sparePartLend/list")
	@ApiOperation(value="spare_part_lend-分页列表查询", notes="spare_part_lend-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent = "sparePartsFor/SparePartLendList")
	public Result<IPage<SparePartLend>> queryPageList(SparePartLend sparePartLend,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<SparePartLend> page = new Page<SparePartLend>(pageNo, pageSize);
		List<SparePartLend> list = sparePartLendService.selectList(page, sparePartLend);
		list = list.stream().distinct().collect(Collectors.toList());
		page.setRecords(list);
		return Result.OK(page);
	}
	 /**
	  * 备件借出-获取仓库查询条件
	  *
	  * @param sparePartLend
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件借出-获取仓库查询条件",permissionUrl = "/sparepart/sparePartLend/list")
	 @ApiOperation(value="备件借出-获取仓库查询条件", notes="备件借出-获取仓库查询条件")
	 @GetMapping(value = "/selectList")
	 @PermissionData(pageComponent = "sparePartsFor/SparePartLendList")
	 public Result<?> selectList(SparePartLend sparePartLend, HttpServletRequest req) {
		 List<SparePartLend> list = sparePartLendService.selectList(null, sparePartLend);
		 List<String> backList = list.stream().map(SparePartLend::getBackWarehouseName).collect(Collectors.toList());
		 List<String> lendList = list.stream().map(SparePartLend::getLendWarehouseName).collect(Collectors.toList());
		 List<String> newList = new ArrayList<>();
		 newList.addAll(backList);
		 newList.addAll(lendList);
		 newList = newList.stream().distinct().collect(Collectors.toList());
		 newList.remove(null);
		 return Result.OK(newList);
	 }
	/**
	 *   添加
	 *
	 * @param sparePartLend
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加备件借入",permissionUrl = "/sparepart/sparePartLend/list")
	@ApiOperation(value="spare_part_lend-添加", notes="spare_part_lend-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SparePartLend sparePartLend) {
		return sparePartLendService.add(sparePartLend);
	}

	 /**
	  *   验证
	  * @param
	  * @return
	  */
	 @AutoLog(value = "校验",operateType = 3,operateTypeAlias = "添加备件校验",permissionUrl = "/sparepart/sparePartLend/list")
	 @ApiOperation(value="spare_part_lend-添加 -校验", notes="spare_part_lend-添加-校验")
	 @PostMapping(value = "/check")
	 public Result<?> check() {
		 return sparePartLendService.check();
	 }
	/**
	 *  借出确认
	 *
	 * @param sparePartLend
	 * @return
	 */
	@AutoLog(value = "借出确认",operateType = 3,operateTypeAlias = "备件借入借出确认",permissionUrl = "/sparepart/sparePartLend/list")
	@ApiOperation(value="spare_part_lend-借出确认", notes="spare_part_lend-借出确认")
	@RequestMapping(value = "/lendConfirm", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> lendConfirm(@RequestBody SparePartLend sparePartLend) {
		return sparePartLendService.lendConfirm(sparePartLend);
	}

	 /**
	  *  归还
	  *
	  * @param sparePartLend
	  * @return
	  */
	 @AutoLog(value = "归还",operateType = 3,operateTypeAlias = "备件借入的归还",permissionUrl = "/sparepart/sparePartLend/list")
	 @ApiOperation(value="spare_part_lend-归还", notes="spare_part_lend-归还")
	 @RequestMapping(value = "/back", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<?> back(@RequestBody SparePartLend sparePartLend) {
		 LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 sparePartLend.setBackPerson(user.getUsername());
		 sparePartLend.setBackTime(new Date());
		 sparePartLendService.updateById(sparePartLend);
		 SparePartLend one = sparePartLendService.getById(sparePartLend.getId());
		 try {
			 //根据仓库编号获取仓库组织机构code
			 String orgCode = sysBaseApi.getDepartByWarehouseCode(one.getBackWarehouseCode());
			 String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(Collections.singletonList(orgCode), Collections.singletonList(RoleConstant.FOREMAN));

			 //发送通知
			 MessageDTO messageDTO = new MessageDTO(user.getUsername(),userName, "备件归还申请" + DateUtil.today(), null);

			 //构建消息模板
			 HashMap<String, Object> map = new HashMap<>();
			 map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, one.getId());
			 map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_LEND_RETURN.getType());
			 map.put("materialCode",one.getMaterialCode());
			 String materialName= sysBaseApi.getMaterialNameByCode(one.getMaterialCode());
			 map.put("name",materialName);
			 map.put("backNum",one.getBackNum());
			 String warehouseName= sysBaseApi.getWarehouseNameByCode(one.getBackWarehouseCode());
			 map.put("warehouseName",warehouseName);

			 /*messageDTO.setData(map);
			 //业务类型，消息类型，消息模板编码，摘要，发布内容
			 messageDTO.setTemplateCode(CommonConstant.SPAREPARTBACK_SERVICE_NOTICE);
			 SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
			 messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
			 messageDTO.setMsgAbstract("备件归还申请");
			 messageDTO.setPublishingContent("备件归还申请，请确认");
			 messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
			 sysBaseApi.sendTemplateMessage(messageDTO);*/
			 //发送待办
			 TodoDTO todoDTO = new TodoDTO();
			 todoDTO.setData(map);
			 SysParamModel sysParamModelTodo = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE_PROCESS);
			 todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModelTodo) ? sysParamModelTodo.getValue() : "");
			 todoDTO.setTitle("备件归还申请" + DateUtil.today());
			 todoDTO.setMsgAbstract("备件归还申请");
			 todoDTO.setPublishingContent("备件归还申请，请确认");
			 todoDTO.setCurrentUserName(userName);
			 todoDTO.setBusinessKey(sparePartLend.getId());
			 todoDTO.setBusinessType(TodoBusinessTypeEnum.SPAREPART_LEND_RETURN.getType());
			 todoDTO.setCurrentUserName(userName);
			 todoDTO.setTaskType(TodoTaskTypeEnum.SPARE_PART.getType());
			 todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
			 todoDTO.setTemplateCode(CommonConstant.SPAREPARTBACK_SERVICE_NOTICE);

			 isTodoBaseAPI.createTodoTask(todoDTO);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 return Result.OK("编辑成功！");
	 }
	 /**
	  *  归还确认
	  *
	  * @param sparePartLend
	  * @return
	  */
	 @AutoLog(value = "归还确认",operateType = 3,operateTypeAlias = "备件借入归还确认",permissionUrl = "/sparepart/sparePartLend/list")
	 @ApiOperation(value="spare_part_lend-归还确认", notes="spare_part_lend-归还确认")
	 @RequestMapping(value = "/backConfirm", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<?> backConfirm(@RequestBody SparePartLend sparePartLend) {
		 return sparePartLendService.backConfirm(sparePartLend);
	 }
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "通过id删除备件借入",permissionUrl = "/sparepart/sparePartLend/list")
	@ApiOperation(value="spare_part_lend-通过id删除", notes="spare_part_lend-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sparePartLendService.removeById(id);
		return Result.OK("删除成功!");
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询备件借入",permissionUrl = "/sparepart/sparePartLend/list")
	@ApiOperation(value="spare_part_lend-通过id查询", notes="spare_part_lend-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartLend> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartLend sparePartLend = sparePartLendService.getById(id);
		if(sparePartLend==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartLend);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param
    */
	@AutoLog(value = "导出",operateType = 6,operateTypeAlias = "导出备件借入",permissionUrl = "/sparepart/sparePartLend/list")
    @RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(@ApiParam(value = "行数据ids" ,required = true) @RequestParam("ids") String ids, HttpServletRequest request, HttpServletResponse response) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		SparePartLend sparePartLend = new SparePartLend();
		sparePartLend.setIds(Arrays.asList(ids.split(",")));
		List<SparePartLend> list = sparePartLendService.selectList(null, sparePartLend);
		list = list.stream().distinct().collect(Collectors.toList());
		for(int i=0;i<list.size();i++){
			SparePartLend lend = list.get(i);
			lend.setNumber(i+1+"");
		}
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "备件借入管理列表");
		mv.addObject(NormalExcelConstants.CLASS, SparePartLend.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件借入管理列表数据", "导出人:"+user.getRealname(), "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, list);
		return mv;
	}


}
