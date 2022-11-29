package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.modules.plan.entity.EmergencyPlanAtt;
import com.aiurt.boot.plan.service.IEmergencyPlanAttService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: emergency_plan_att
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_plan_att")
@RestController
@RequestMapping("/emergency/emergencyPlanAtt")
@Slf4j
public class EmergencyPlanAttController extends BaseController<EmergencyPlanAtt, IEmergencyPlanAttService> {
	@Autowired
	private IEmergencyPlanAttService emergencyPlanAttService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlanAtt
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_plan_att-分页列表查询")
	@ApiOperation(value="emergency_plan_att-分页列表查询", notes="emergency_plan_att-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyPlanAtt>> queryPageList(EmergencyPlanAtt emergencyPlanAtt,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyPlanAtt> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlanAtt, req.getParameterMap());
		Page<EmergencyPlanAtt> page = new Page<EmergencyPlanAtt>(pageNo, pageSize);
		IPage<EmergencyPlanAtt> pageList = emergencyPlanAttService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyPlanAtt
	 * @return
	 */
	@AutoLog(value = "emergency_plan_att-添加")
	@ApiOperation(value="emergency_plan_att-添加", notes="emergency_plan_att-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyPlanAtt emergencyPlanAtt) {
		emergencyPlanAttService.save(emergencyPlanAtt);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyPlanAtt
	 * @return
	 */
	@AutoLog(value = "emergency_plan_att-编辑")
	@ApiOperation(value="emergency_plan_att-编辑", notes="emergency_plan_att-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyPlanAtt emergencyPlanAtt) {
		emergencyPlanAttService.updateById(emergencyPlanAtt);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_plan_att-通过id删除")
	@ApiOperation(value="emergency_plan_att-通过id删除", notes="emergency_plan_att-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanAttService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan_att-批量删除")
	@ApiOperation(value="emergency_plan_att-批量删除", notes="emergency_plan_att-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanAttService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_plan_att-通过id查询")
	@ApiOperation(value="emergency_plan_att-通过id查询", notes="emergency_plan_att-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyPlanAtt> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyPlanAtt emergencyPlanAtt = emergencyPlanAttService.getById(id);
		if(emergencyPlanAtt==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyPlanAtt);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyPlanAtt
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyPlanAtt emergencyPlanAtt) {
        return super.exportXls(request, emergencyPlanAtt, EmergencyPlanAtt.class, "emergency_plan_att");
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
        return super.importExcel(request, response, EmergencyPlanAtt.class);
    }

}
