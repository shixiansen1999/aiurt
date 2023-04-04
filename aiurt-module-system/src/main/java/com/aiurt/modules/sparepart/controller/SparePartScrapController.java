package com.aiurt.modules.sparepart.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.service.ISparePartScrapService;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
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
import org.jeecg.common.system.vo.SysDepartModel;
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
 * @Description: spare_part_scrap
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Api(tags="备件管理-备件处置管理")
@RestController
@RequestMapping("/sparepart/sparePartScrap")
@Slf4j
public class SparePartScrapController extends BaseController<SparePartScrap, ISparePartScrapService> {
	@Autowired
	private ISparePartScrapService sparePartScrapService;
	 @Autowired
	 private ISysParamAPI iSysParamAPI;
	 @Autowired
	 private ISysBaseAPI sysBaseApi;
	 @Autowired
	 private ISTodoBaseAPI isTodoBaseAPI;

	@Autowired
	private ISysDepartService sysDepartService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartScrap
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件报废分页列表查询",permissionUrl = "/sparepart/sparePartScrap/list")
	@ApiOperation(value="spare_part_scrap-分页列表查询", notes="spare_part_scrap-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent = "sparePartsFor/SparePartScrap")
	public Result<IPage<SparePartScrap>> queryPageList(SparePartScrap sparePartScrap,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<SparePartScrap> page = new Page<SparePartScrap>(pageNo, pageSize);
		List<SparePartScrap> list = sparePartScrapService.selectList(page, sparePartScrap);
		page.setRecords(list);
		return Result.OK(page);
	}

	/**
	 *   添加
	 *
	 * @param sparePartScrap
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加备件报废",permissionUrl = "/sparepart/sparePartScrap/list")
	@ApiOperation(value="spare_part_scrap-添加", notes="spare_part_scrap-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SparePartScrap sparePartScrap) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		sparePartScrap.setSysOrgCode(user.getOrgCode());
		sparePartScrap.setStatus(CommonConstant.SPARE_PART_SCRAP_STATUS_1);
		sparePartScrapService.save(sparePartScrap);
		try {
			String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(Collections.singletonList(user.getOrgCode()), Collections.singletonList(RoleConstant.FOREMAN));

			//发送通知
			MessageDTO messageDTO = new MessageDTO(user.getUsername(),userName, "备件报废申请-确认" + DateUtil.today(), null);

			//构建消息模板
			HashMap<String, Object> map = new HashMap<>();
			map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, sparePartScrap.getId());
			map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_LEND.getType());
			map.put("materialCode",sparePartScrap.getMaterialCode());
			String materialName= sysBaseApi.getMaterialNameByCode(sparePartScrap.getMaterialCode());
			map.put("name",materialName);
			map.put("num",sparePartScrap.getNum());
			LoginUser userByName = sysBaseApi.getUserByName(sparePartScrap.getCreateBy());
			map.put("realName",userByName.getRealname());
			map.put("scrapTime", DateUtil.format(sparePartScrap.getScrapTime(),"yyyy-MM-dd HH:mm:ss"));

			messageDTO.setData(map);
			//业务类型，消息类型，消息模板编码，摘要，发布内容
			/*messageDTO.setTemplateCode(CommonConstant.SPAREPARTSCRAP_SERVICE_NOTICE);
			SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
			messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
			messageDTO.setMsgAbstract("备件报废申请");
			messageDTO.setPublishingContent("备件报废申请，请确认");
			messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
			sysBaseApi.sendTemplateMessage(messageDTO);*/
			//发送待办
			TodoDTO todoDTO = new TodoDTO();
			todoDTO.setData(map);
			SysParamModel sysParamModelTodo = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE_PROCESS);
			todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModelTodo) ? sysParamModelTodo.getValue() : "");
			todoDTO.setTitle("备件报废申请-确认" + DateUtil.today());
			todoDTO.setMsgAbstract("备件报废申请");
			todoDTO.setPublishingContent("备件报废申请，请确认");
			todoDTO.setCurrentUserName(userName);
			todoDTO.setBusinessKey(sparePartScrap.getId());
			todoDTO.setBusinessType(TodoBusinessTypeEnum.SPAREPART_SCRAP.getType());
			todoDTO.setCurrentUserName(userName);
			todoDTO.setTaskType(TodoBusinessTypeEnum.SPAREPART_SCRAP.getType());
			todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
			todoDTO.setTemplateCode(CommonConstant.SPAREPARTSCRAP_SERVICE_NOTICE);

			isTodoBaseAPI.createTodoTask(todoDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Result.OK("添加成功！");
	}


	 /**
	  * 查询出所有的存放位置，生成存放位置规则：组织机构名称+材料库
	  * @param id
	  * @return
	  */
	@AutoLog(value = "查询存放位置", operateType = 1, operateTypeAlias = "添加备件报废-查询存放位置", permissionUrl = "/sparepart/sparePartScrap/list")
	@ApiOperation(value = "spare_part_scrap-查询存放位置", notes = "spare_part_scrap-查询存放位置")
	@GetMapping(value = "/queryAllLocation")
	public Result<List<String>> queryAllLocation(@RequestParam(name = "id", required = false) String id) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		Result<List<String>> result = new Result<>();
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.orderByAsc(SysDepart::getOrgCode);
		if(oConvertUtils.isNotEmpty(id)){
			String[] arr = id.split(",");
			query.in(SysDepart::getId,arr);
		}
		List<SysDepart> ls = this.sysDepartService.list(query);
		String temp = "材料库";
		String local = user.getOrgName() + temp;
		List<String> collect = ls.stream().filter(t -> StrUtil.isNotBlank(t.getDepartName())).map(t -> t.getDepartName() + temp)
				.filter(t -> !StrUtil.equals(t, local))
				.collect(Collectors.toList());
		// 将登录用户所属组织机构材料库默认放在第一个索引位置
		collect.remove(local);
		collect.add(0,local);
		result.setSuccess(true);
		result.setResult(collect);
		return result;
	}

	/**
	 *  编辑
	 *
	 * @param sparePartScrap
	 * @return
	 */
	@AutoLog(value = "处置",operateType = 3,operateTypeAlias = "编辑备件报废",permissionUrl = "/sparepart/sparePartScrap/list")
	@ApiOperation(value="spare_part_scrap-处置", notes="spare_part_scrap-处置")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SparePartScrap sparePartScrap) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		sparePartScrap.setSysOrgCode(user.getOrgCode());
		if (ObjectUtil.isNotEmpty(sparePartScrap.getHandleWay()) && ObjectUtil.isNotEmpty(sparePartScrap.getId())
				&& CommonConstant.SPARE_PART_SCRAP_STATUS_1.equals(sparePartScrap.getStatus())) {
			if (CommonConstant.SPARE_PART_SCRAP_HANDLE_WAY_0.equals(sparePartScrap.getHandleWay())) {
				sparePartScrap.setStatus(CommonConstant.SPARE_PART_SCRAP_STATUS_3);
			}
			if (CommonConstant.SPARE_PART_SCRAP_HANDLE_WAY_1.equals(sparePartScrap.getHandleWay())) {
				sparePartScrap.setStatus(CommonConstant.SPARE_PART_SCRAP_STATUS_2);
			}
			return sparePartScrapService.update(sparePartScrap);

		}else {
			return Result.error("操作失败");
		}
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "删除备件报废",permissionUrl = "/sparepart/sparePartScrap/list")
	@ApiOperation(value="spare_part_scrap-通过id删除", notes="spare_part_scrap-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		SparePartScrap sparePartScrap = sparePartScrapService.getById(id);
		sparePartScrap.setDelFlag(CommonConstant.DEL_FLAG_1);
		sparePartScrapService.updateById(sparePartScrap);
		return Result.OK("删除成功!");
	}



	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询备件报废",permissionUrl = "/sparepart/sparePartScrap/list")
	@ApiOperation(value="spare_part_scrap-通过id查询", notes="spare_part_scrap-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartScrap> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartScrap sparePartScrap = sparePartScrapService.getById(id);
		List<SparePartScrap> list = sparePartScrapService.selectListById(sparePartScrap);
		list = list.stream().filter(sparePartScrap1 -> sparePartScrap1.getId().equals(id)).distinct().collect(Collectors.toList());
		if(CollUtil.isNotEmpty(list)){
			for (SparePartScrap partScrap : list) {
				sparePartScrap = partScrap;
			}
		}
		if(sparePartScrap==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartScrap);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param  ids
    */
	@AutoLog(value = "导出",operateType = 6,operateTypeAlias = "导出备件报废",permissionUrl = "/sparepart/sparePartScrap/list")
    @RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(@ApiParam(value = "行数据ids" ,required = true) @RequestParam("ids") String ids, HttpServletRequest request, HttpServletResponse response) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		SparePartScrap sparePartScrap = new SparePartScrap();
		sparePartScrap.setIds(Arrays.asList(ids.split(",")));
		List<SparePartScrap> list = sparePartScrapService.selectList(null, sparePartScrap);
		list = list.stream().distinct().collect(Collectors.toList());
		for(int i=0;i<list.size();i++){
			SparePartScrap order = list.get(i);
			order.setNumber(i+1+"");
		}
		// 根据配置决定是否需要使用专用导出
		SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPARTSCRAP_SPECIAL_EXPORT);
		boolean equals = "1".equals(sysParamModel.getValue());
		if (equals) {
			SysDepartModel depart = sysBaseApi.getDepartByOrgCode(user.getOrgCode());
			DateTime date = DateUtil.date();
			String title = depart.getDepartName() + DateUtil.year(date) + "年度非固定资产（生产类专用物资）报废申请表";
			//导出文件名称
			mv.addObject(NormalExcelConstants.FILE_NAME, "备件报废管理列表");
			mv.addObject(NormalExcelConstants.CLASS, SparePartScrap.class);
			mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(title, "经办人:" + user.getRealname() + "     作成日期：" + date, "导出信息"));
			//自定义导出字段，为null值时导出全部 例子:exportField="realname,username"
			String exportField = "typeName,name,specifications,unit,num,sysOrgCode,location,lifeCycle,usageTime,reason,remarks";
			//自定义导出字段
			mv.addObject(NormalExcelConstants.EXPORT_FIELDS,exportField);
			mv.addObject(NormalExcelConstants.DATA_LIST, list);
			return mv;
		} else {
			//导出文件名称
			mv.addObject(NormalExcelConstants.FILE_NAME, "备件报废管理列表");
			mv.addObject(NormalExcelConstants.CLASS, SparePartScrap.class);
			mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件报废管理列表数据", "导出人:"+user.getRealname(), "导出信息"));
			mv.addObject(NormalExcelConstants.DATA_LIST, list);
			return mv;
		}
	}

	 /**
	  * 委外备件送修-查询
	  * 初始查询所有状态为“已报损”且“故障单号"不为空的数据
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "委外备件送修-查询",permissionUrl = "/sparepart/sparePartScrap/list")
	 @ApiOperation(value="委外备件送修-查询", notes="委外备件送修-查询")
	 @GetMapping(value = "/queryAllScrapForRepair")
	 public Result<IPage<SparePartScrap>> queryAllScrapForRepair(SparePartScrap sparePartScrap,
																 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
																 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
																 HttpServletRequest req) {
		 Page<SparePartScrap> page = new Page<>(pageNo, pageSize);
		 IPage<SparePartScrap> pageList = sparePartScrapService.queryAllScrapForRepair(page, sparePartScrap);
		 return Result.ok(pageList);
	 }


	 @AutoLog(value = "返修",operateType = 3, operateTypeAlias = "返修",permissionUrl = "/sparepart/sparePartScrap/list")
	 @ApiOperation(value="委外备件送修-返修", notes="委外备件送修-返修")
	 @RequestMapping(value = "/scrapToRepair", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<?> scrapToRepair(@RequestBody SparePartScrap sparePartScrap) {
		 if (StrUtil.isNotEmpty(sparePartScrap.getId()) && ObjectUtil.equal(sparePartScrap.getRepairStatus(), CommonConstant.SPARE_PART_SCRAP_REPAIR_STATUS_1)) {
			 sparePartScrap.setRepairStatus(CommonConstant.SPARE_PART_SCRAP_REPAIR_STATUS_2);
			 sparePartScrapService.updateById(sparePartScrap);
			 return Result.ok("操作成功");
		 } else {
			 return Result.error("操作失败");
		 }
	 }

	 @AutoLog(value = "验收", operateType = 3, operateTypeAlias = "验收", permissionUrl = "/sparepart/sparePartScrap/list")
	 @ApiOperation(value="委外备件送修-验收", notes="委外备件送修-验收")
	 @PostMapping(value = "/scrapRepairAcceptance")
	 public Result<?> scrapRepairAcceptance(@RequestBody SparePartScrap sparePartScrap) {
		 if (StrUtil.isNotEmpty(sparePartScrap.getId()) && ObjectUtil.equal(sparePartScrap.getRepairStatus(), CommonConstant.SPARE_PART_SCRAP_REPAIR_STATUS_2)) {
			 sparePartScrapService.scrapRepairAcceptance(sparePartScrap);
			 return Result.ok("操作成功");
		 } else {
			 return Result.error("操作失败");
		 }

	 }

}
