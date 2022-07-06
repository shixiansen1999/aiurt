package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.boot.plan.dto.PatrolPlanDto;
import com.aiurt.boot.plan.dto.QuerySiteDto;
import com.aiurt.modules.device.entity.Device;
import org.jeecg.common.api.vo.Result;
import com.aiurt.boot.plan.entity.PatrolPlan;
import com.aiurt.boot.plan.service.IPatrolPlanService;

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
 * @Description: patrol_plan
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="巡检计划")
@RestController
@RequestMapping("/patrolPlan")
@Slf4j
public class PatrolPlanController extends BaseController<PatrolPlan, IPatrolPlanService> {
	@Autowired
	private IPatrolPlanService patrolPlanService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolPlan
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "巡检计划表-分页列表查询")
	@ApiOperation(value="巡检计划表-分页列表查询", notes="巡检计划表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolPlanDto>> queryPageList(PatrolPlanDto patrolPlan,
													  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													  HttpServletRequest req) {
		Page<PatrolPlanDto> page = new Page<PatrolPlanDto>(pageNo, pageSize);
		IPage<PatrolPlanDto> pageList = patrolPlanService.pageList(page, patrolPlan);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param patrolPlanDto
	 * @return
	 */
	@AutoLog(value = "巡检计划表-添加")
	@ApiOperation(value="巡检计划表-添加", notes="巡检计划表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolPlanDto patrolPlanDto) {

		patrolPlanService.add(patrolPlanDto);
		return Result.OK("添加成功！");
	}
	 /**
	  *查询站点
	  * @param
	  * @return
	  */
	 @AutoLog(value = "巡检计划表-查询站点")
	 @ApiOperation(value="巡检计划表-查询站点", notes="巡检计划表-查询站点")
	 @PostMapping(value = "/querySite")
	 public List<QuerySiteDto> querySited() {
		List<QuerySiteDto> querySiteDtos = patrolPlanService.querySited();
		 return querySiteDtos;
	 }
	 /**
	  * 修改状态
	  * @param id,status
	  * @return
	  */
	 @AutoLog(value = "巡检计划表-修改生效状态")
	 @ApiOperation(value="巡检计划表-修改生效状态", notes="巡检计划表-修改生效状态")
	 @RequestMapping(value = "/modify", method = {RequestMethod.POST})
	 public Result<String> modify(@RequestParam(name = "id") String id,
								  @RequestParam(name = "status") Integer status) {
	 	PatrolPlan patrolPlan =new PatrolPlan();
	 	patrolPlan.setId(id);if(status==0){
			 patrolPlan.setStatus(1);
		 }
		 patrolPlan.setId(id);if(status==1){
			 patrolPlan.setStatus(0);
		 }
		 patrolPlanService.updateById(patrolPlan);
		 return Result.OK("修改成功！");
	 }
	/**
	 *  编辑
	 *
	 * @param patrolPlanDto
	 * @return
	 */
	@AutoLog(value = "巡检计划表-编辑")
	@ApiOperation(value="巡检计划表-编辑", notes="巡检计划表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolPlanDto patrolPlanDto) {
		patrolPlanService.updateId(patrolPlanDto);
		return Result.OK("编辑成功!");
	}
	 /**
	  *  生成巡检计划Code
	  * @param
	  * @return
	  */
	 @AutoLog(value = "生成巡检计划Code")
	 @ApiOperation(value="生成巡检计划Code", notes="生成巡检计划Code")
	 @GetMapping(value = "/generatePlanCode")
	 public Result<String> generatePlanCode() {
		 String code="XJ"+System.currentTimeMillis();
		 return Result.OK(code);
	 }
	 /**
	  *  查看设备详情
	  * @param standardId
	  * @return
	  */
	 @AutoLog(value = "巡检计划表-查看设备详情")
	 @ApiOperation(value="巡检计划表-查看设备详情", notes="巡检计划表-查看设备详情")
	 @RequestMapping(value = "/viewDetails", method = {RequestMethod.POST})
	 public List<Device> viewDetails(@RequestParam(name = "standardId")String standardId) {
		 List<Device> list =patrolPlanService.viewDetails(standardId);
		 return list;
	 }
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "巡检计划表-通过id删除")
	@ApiOperation(value="巡检计划表-通过id删除", notes="巡检计划表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolPlanService.delete(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "巡检计划表-批量删除")
	@ApiOperation(value="巡检计划表-批量删除", notes="巡检计划表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> id = Arrays.asList(ids.split(","));
		for (String id1 :id){
			this.delete(id1);
		}
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "巡检计划表-通过id查询")
	@ApiOperation(value="巡检计划表-通过id查询", notes="巡检计划表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolPlanDto> queryById(@RequestParam(name="id",required=true) String id,
										   @RequestParam(name="code",required=true) String code) {
		PatrolPlanDto patrolPlanDto = patrolPlanService.selectId(id,code);
		if(patrolPlanDto ==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolPlanDto);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolPlan
    */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, PatrolPlan patrolPlan) {
//        return super.exportXls(request, patrolPlan, PatrolPlan.class, "patrol_plan");
//    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, PatrolPlan.class);
//    }

}
