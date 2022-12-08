package com.aiurt.boot.materials.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.entity.EmergencyMaterialsUsage;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.materials.service.IEmergencyMaterialsUsageService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: emergency_materials_usage
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="应急物资使用记录")
@RestController
@RequestMapping("/emergency/emergencyMaterialsUsage")
@Slf4j
public class EmergencyMaterialsUsageController extends BaseController<EmergencyMaterialsUsage, IEmergencyMaterialsUsageService> {
	@Autowired
	private IEmergencyMaterialsUsageService emergencyMaterialsUsageService;


	@AutoLog(value = "应急物资使用记录-分页列表查询", operateType =  1, operateTypeAlias = "应急物资使用记录-分页列表查询")
	@ApiOperation(value="应急物资使用记录-分页列表查询", notes="应急物资使用记录-分页列表查询")
	@GetMapping(value = "/queryPageList")
	public Result<IPage<EmergencyMaterialsUsage>> queryPageList(EmergencyMaterialsUsage condition,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
								   ) {
		Page<EmergencyMaterialsUsage> pageList = new Page<>(pageNo, pageSize);
		Page<EmergencyMaterialsUsage> usageRecordList = emergencyMaterialsUsageService.getUsageRecordList(pageList, condition);
		return Result.OK(usageRecordList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyMaterialsUsage
	 * @return
	 */
	@AutoLog(value = "应急物资使用记录-添加")
	@ApiOperation(value="应急物资使用记录-添加", notes="应急物资使用记录-添加")
	@PostMapping(value = "/add")
	@Transactional(rollbackFor = Exception.class)
	public Result<String> add(@RequestBody EmergencyMaterialsUsage emergencyMaterialsUsage) {
		emergencyMaterialsUsageService.save(emergencyMaterialsUsage);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyMaterialsUsage
	 * @return
	 */
	@AutoLog(value = "应急物资使用记录-编辑")
	@ApiOperation(value="应急物资使用记录-编辑", notes="应急物资使用记录-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	@Transactional(rollbackFor = Exception.class)
	public Result<String> edit(@RequestBody EmergencyMaterialsUsage emergencyMaterialsUsage) {
		emergencyMaterialsUsageService.updateById(emergencyMaterialsUsage);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "应急物资使用记录-通过id删除")
	@ApiOperation(value="应急物资使用记录-通过id删除", notes="应急物资使用记录-通过id删除")
	@DeleteMapping(value = "/delete")
	@Transactional(rollbackFor = Exception.class)
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		EmergencyMaterialsUsage emergencyMaterialsUsage = new EmergencyMaterialsUsage();
		if (StrUtil.isNotBlank(id)){
			emergencyMaterialsUsage.setId(id);
			emergencyMaterialsUsage.setDelFlag(1);
		}else {
			return Result.OK("删除失败，id为空或不存在!");
		}
		emergencyMaterialsUsageService.updateById(emergencyMaterialsUsage);
		return Result.OK("删除成功!");
	}

//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "emergency_materials_usage-批量删除")
//	@ApiOperation(value="emergency_materials_usage-批量删除", notes="emergency_materials_usage-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.emergencyMaterialsUsageService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("批量删除成功!");
//	}

	 @AutoLog(value = "应急物资使用记录-提交")
	 @ApiOperation(value="应急物资使用记录-提交", notes="应急物资使用记录-提交")
	 @PostMapping(value = "/getSubmitMaterialRecord")
	 @Transactional(rollbackFor = Exception.class)
	 public Result<?> getSubmitMaterialRecord(@RequestParam(name="ids",required=true) String ids){
		 List<EmergencyMaterialsUsage> list = new ArrayList<>();
		 List<String> stringList = Arrays.asList(ids.split(","));
		     stringList.forEach(e->{
				 EmergencyMaterialsUsage emergencyMaterialsUsage = new EmergencyMaterialsUsage();
				 emergencyMaterialsUsage.setId(e);
				 emergencyMaterialsUsage.setStatus(1);
				 list.add(emergencyMaterialsUsage);
			 });
		 emergencyMaterialsUsageService.updateBatchById(list);
		 return Result.OK("提交成功!");
	 }

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "应急物资使用记录-通过id查询")
	@ApiOperation(value="应急物资使用记录-通过id查询", notes="应急物资使用记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyMaterialsUsage> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyMaterialsUsage emergencyMaterialsUsage = emergencyMaterialsUsageService.getById(id);
		if(emergencyMaterialsUsage==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyMaterialsUsage);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyMaterialsUsage
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyMaterialsUsage emergencyMaterialsUsage) {
        return super.exportXls(request, emergencyMaterialsUsage, EmergencyMaterialsUsage.class, "emergency_materials_usage");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, EmergencyMaterialsUsage.class);
    }

}
