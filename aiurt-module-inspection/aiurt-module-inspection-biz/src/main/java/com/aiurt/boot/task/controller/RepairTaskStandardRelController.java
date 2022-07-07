//package com.aiurt.boot.task.controller;
//
//import com.aiurt.boot.task.entity.RepairTaskStandardRel;
//import com.aiurt.boot.task.service.IRepairTaskStandardRelService;
//import com.aiurt.common.aspect.annotation.AutoLog;
//import com.aiurt.common.system.base.controller.BaseController;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.jeecg.common.api.vo.Result;
//import org.jeecg.common.system.query.QueryGenerator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Arrays;
//
// /**
// * @Description: repair_task_standard_rel
// * @Author: aiurt
// * @Date:   2022-06-22
// * @Version: V1.0
// */
////@Api(tags="repair_task_standard_rel")
//@RestController
//@RequestMapping("/task/repairTaskStandardRel")
//@Slf4j
//public class RepairTaskStandardRelController extends BaseController<RepairTaskStandardRel, IRepairTaskStandardRelService> {
//	@Autowired
//	private IRepairTaskStandardRelService repairTaskStandardRelService;
//
//	/**
//	 * 分页列表查询
//	 *
//	 * @param repairTaskStandardRel
//	 * @param pageNo
//	 * @param pageSize
//	 * @param req
//	 * @return
//	 */
//	//@AutoLog(value = "repair_task_standard_rel-分页列表查询")
//	@ApiOperation(value="repair_task_standard_rel-分页列表查询", notes="repair_task_standard_rel-分页列表查询")
//	@GetMapping(value = "/list")
//	public Result<IPage<RepairTaskStandardRel>> queryPageList(RepairTaskStandardRel repairTaskStandardRel,
//								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
//								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
//								   HttpServletRequest req) {
//		QueryWrapper<RepairTaskStandardRel> queryWrapper = QueryGenerator.initQueryWrapper(repairTaskStandardRel, req.getParameterMap());
//		Page<RepairTaskStandardRel> page = new Page<RepairTaskStandardRel>(pageNo, pageSize);
//		IPage<RepairTaskStandardRel> pageList = repairTaskStandardRelService.page(page, queryWrapper);
//		return Result.OK(pageList);
//	}
//
//	/**
//	 *   添加
//	 *
//	 * @param repairTaskStandardRel
//	 * @return
//	 */
//	@AutoLog(value = "repair_task_standard_rel-添加")
//	@ApiOperation(value="repair_task_standard_rel-添加", notes="repair_task_standard_rel-添加")
//	@PostMapping(value = "/add")
//	public Result<String> add(@RequestBody RepairTaskStandardRel repairTaskStandardRel) {
//		repairTaskStandardRelService.save(repairTaskStandardRel);
//		return Result.OK("添加成功！");
//	}
//
//	/**
//	 *  编辑
//	 *
//	 * @param repairTaskStandardRel
//	 * @return
//	 */
//	@AutoLog(value = "repair_task_standard_rel-编辑")
//	@ApiOperation(value="repair_task_standard_rel-编辑", notes="repair_task_standard_rel-编辑")
//	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
//	public Result<String> edit(@RequestBody RepairTaskStandardRel repairTaskStandardRel) {
//		repairTaskStandardRelService.updateById(repairTaskStandardRel);
//		return Result.OK("编辑成功!");
//	}
//
//	/**
//	 *   通过id删除
//	 *
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "repair_task_standard_rel-通过id删除")
//	@ApiOperation(value="repair_task_standard_rel-通过id删除", notes="repair_task_standard_rel-通过id删除")
//	@DeleteMapping(value = "/delete")
//	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
//		repairTaskStandardRelService.removeById(id);
//		return Result.OK("删除成功!");
//	}
//
//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "repair_task_standard_rel-批量删除")
//	@ApiOperation(value="repair_task_standard_rel-批量删除", notes="repair_task_standard_rel-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.repairTaskStandardRelService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("批量删除成功!");
//	}
//
//	/**
//	 * 通过id查询
//	 *
//	 * @param id
//	 * @return
//	 */
//	//@AutoLog(value = "repair_task_standard_rel-通过id查询")
//	@ApiOperation(value="repair_task_standard_rel-通过id查询", notes="repair_task_standard_rel-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<RepairTaskStandardRel> queryById(@RequestParam(name="id",required=true) String id) {
//		RepairTaskStandardRel repairTaskStandardRel = repairTaskStandardRelService.getById(id);
//		if(repairTaskStandardRel==null) {
//			return Result.error("未找到对应数据");
//		}
//		return Result.OK(repairTaskStandardRel);
//	}
//
//    /**
//    * 导出excel
//    *
//    * @param request
//    * @param repairTaskStandardRel
//    */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, RepairTaskStandardRel repairTaskStandardRel) {
//        return super.exportXls(request, repairTaskStandardRel, RepairTaskStandardRel.class, "repair_task_standard_rel");
//    }
//
//    /**
//      * 通过excel导入数据
//    *
//    * @param request
//    * @param response
//    * @return
//    */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, RepairTaskStandardRel.class);
//    }
//
//}
