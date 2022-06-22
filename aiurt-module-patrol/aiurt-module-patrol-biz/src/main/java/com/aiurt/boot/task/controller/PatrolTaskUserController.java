package com.aiurt.boot.task.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.service.IPatrolTaskUserService;

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
 * @Description: patrol_task_user
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="patrol_task_user")
@RestController
@RequestMapping("/patrolTaskUser")
@Slf4j
public class PatrolTaskUserController extends BaseController<PatrolTaskUser, IPatrolTaskUserService> {
	@Autowired
	private IPatrolTaskUserService patrolTaskUserService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolTaskUser
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_task_user-分页列表查询")
	@ApiOperation(value="patrol_task_user-分页列表查询", notes="patrol_task_user-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolTaskUser>> queryPageList(PatrolTaskUser patrolTaskUser,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolTaskUser> queryWrapper = QueryGenerator.initQueryWrapper(patrolTaskUser, req.getParameterMap());
		Page<PatrolTaskUser> page = new Page<PatrolTaskUser>(pageNo, pageSize);
		IPage<PatrolTaskUser> pageList = patrolTaskUserService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param patrolTaskUser
	 * @return
	 */
	@AutoLog(value = "patrol_task_user-添加")
	@ApiOperation(value="patrol_task_user-添加", notes="patrol_task_user-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolTaskUser patrolTaskUser) {
		patrolTaskUserService.save(patrolTaskUser);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param patrolTaskUser
	 * @return
	 */
	@AutoLog(value = "patrol_task_user-编辑")
	@ApiOperation(value="patrol_task_user-编辑", notes="patrol_task_user-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolTaskUser patrolTaskUser) {
		patrolTaskUserService.updateById(patrolTaskUser);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "patrol_task_user-通过id删除")
	@ApiOperation(value="patrol_task_user-通过id删除", notes="patrol_task_user-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolTaskUserService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "patrol_task_user-批量删除")
	@ApiOperation(value="patrol_task_user-批量删除", notes="patrol_task_user-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolTaskUserService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "patrol_task_user-通过id查询")
	@ApiOperation(value="patrol_task_user-通过id查询", notes="patrol_task_user-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolTaskUser> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolTaskUser patrolTaskUser = patrolTaskUserService.getById(id);
		if(patrolTaskUser==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolTaskUser);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolTaskUser
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolTaskUser patrolTaskUser) {
        return super.exportXls(request, patrolTaskUser, PatrolTaskUser.class, "patrol_task_user");
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
        return super.importExcel(request, response, PatrolTaskUser.class);
    }

}
