package com.aiurt.boot.task.controller;

import com.aiurt.boot.task.entity.PatrolTaskFault;
import com.aiurt.boot.task.service.IPatrolTaskFaultService;
import com.aiurt.common.system.base.controller.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: patrol_task_fault
 * @Author: aiurt
 * @Date:   2022-08-08
 * @Version: V1.0
 */
@Api(tags="patrol_task_fault")
@RestController
@RequestMapping("/patrolTaskFault")
@Slf4j
public class PatrolTaskFaultController extends BaseController<PatrolTaskFault, IPatrolTaskFaultService> {
	@Autowired
	private IPatrolTaskFaultService patrolTaskFaultService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolTaskFault
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_task_fault-分页列表查询")
//	@ApiOperation(value="patrol_task_fault-分页列表查询", notes="patrol_task_fault-分页列表查询")
//	@GetMapping(value = "/list")
//	public Result<IPage<PatrolTaskFault>> queryPageList(PatrolTaskFault patrolTaskFault,
//								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
//								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
//								   HttpServletRequest req) {
//		QueryWrapper<PatrolTaskFault> queryWrapper = QueryGenerator.initQueryWrapper(patrolTaskFault, req.getParameterMap());
//		Page<PatrolTaskFault> page = new Page<PatrolTaskFault>(pageNo, pageSize);
//		IPage<PatrolTaskFault> pageList = patrolTaskFaultService.page(page, queryWrapper);
//		return Result.OK(pageList);
//	}

	/**
	 *   添加
	 *
	 * @param patrolTaskFault
	 * @return
	 */
//	@AutoLog(value = "patrol_task_fault-添加")
//	@ApiOperation(value="patrol_task_fault-添加", notes="patrol_task_fault-添加")
//	@PostMapping(value = "/add")
//	public Result<String> add(@RequestBody PatrolTaskFault patrolTaskFault) {
//		patrolTaskFaultService.save(patrolTaskFault);
//		return Result.OK("添加成功！");
//	}

	/**
	 *  编辑
	 *
	 * @param patrolTaskFault
	 * @return
	 */
//	@AutoLog(value = "patrol_task_fault-编辑")
//	@ApiOperation(value="patrol_task_fault-编辑", notes="patrol_task_fault-编辑")
//	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
//	public Result<String> edit(@RequestBody PatrolTaskFault patrolTaskFault) {
//		patrolTaskFaultService.updateById(patrolTaskFault);
//		return Result.OK("编辑成功!");
//	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
//	@AutoLog(value = "patrol_task_fault-通过id删除")
//	@ApiOperation(value="patrol_task_fault-通过id删除", notes="patrol_task_fault-通过id删除")
//	@DeleteMapping(value = "/delete")
//	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
//		patrolTaskFaultService.removeById(id);
//		return Result.OK("删除成功!");
//	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
//	@AutoLog(value = "patrol_task_fault-批量删除")
//	@ApiOperation(value="patrol_task_fault-批量删除", notes="patrol_task_fault-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.patrolTaskFaultService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("批量删除成功!");
//	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "patrol_task_fault-通过id查询")
//	@ApiOperation(value="patrol_task_fault-通过id查询", notes="patrol_task_fault-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<PatrolTaskFault> queryById(@RequestParam(name="id",required=true) String id) {
//		PatrolTaskFault patrolTaskFault = patrolTaskFaultService.getById(id);
//		if(patrolTaskFault==null) {
//			return Result.error("未找到对应数据");
//		}
//		return Result.OK(patrolTaskFault);
//	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolTaskFault
    */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, PatrolTaskFault patrolTaskFault) {
//        return super.exportXls(request, patrolTaskFault, PatrolTaskFault.class, "patrol_task_fault");
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
//        return super.importExcel(request, response, PatrolTaskFault.class);
//    }

}
