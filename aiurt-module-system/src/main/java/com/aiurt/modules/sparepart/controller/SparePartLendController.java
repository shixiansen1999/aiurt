package com.aiurt.modules.sparepart.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.modules.sparepart.entity.SparePartLend;
import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import com.aiurt.modules.sparepart.service.ISparePartLendService;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

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
