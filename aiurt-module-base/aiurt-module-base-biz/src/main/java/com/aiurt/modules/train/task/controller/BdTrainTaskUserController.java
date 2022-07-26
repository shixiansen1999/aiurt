package com.aiurt.modules.train.task.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;
import com.aiurt.modules.train.task.mapper.BdTrainTaskUserMapper;
import com.aiurt.modules.train.task.service.IBdTrainTaskUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

 /**
 * @Description: 培训任务人员
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
@Api(tags="培训任务人员")
@RestController
@RequestMapping("/feedback/bdTrainTaskUser")
@Slf4j
public class BdTrainTaskUserController extends BaseController<BdTrainTaskUser, IBdTrainTaskUserService> {
	@Autowired
	private IBdTrainTaskUserService bdTrainTaskUserService;
	 @Autowired
	 private BdTrainTaskUserMapper bdTrainTaskUserMapper;
	/**
	 * 分页列表查询
	 *
	 * @param bdTrainTaskUser
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "培训任务人员-分页列表查询")
	@ApiOperation(value="培训任务人员-分页列表查询", notes="培训任务人员-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BdTrainTaskUser bdTrainTaskUser,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BdTrainTaskUser> queryWrapper = QueryGenerator.initQueryWrapper(bdTrainTaskUser, req.getParameterMap());
		Page<BdTrainTaskUser> page = new Page<BdTrainTaskUser>(pageNo, pageSize);
		IPage<BdTrainTaskUser> pageList = bdTrainTaskUserService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 培训任务人员-根据任务id查询
	  * @param taskId
	  * @autor lkj
	  * @return
	  */
	 @AutoLog(value = "培训任务人员-根据任务id查询")
	 @ApiOperation(value="培训任务人员-根据任务id查询", notes="培训任务人员-根据任务id查询")
	 @GetMapping(value = "/getUserListById")
	 public Result<?> getUserListById(@RequestParam(name="taskId",required=true)String taskId) {
		 List<BdTrainTaskUser> userListById = bdTrainTaskUserMapper.getUserListById(taskId);
		 return Result.OK(userListById);
	 }
	/**
	 *   添加
	 *
	 * @param bdTrainTaskUser
	 * @return
	 */
	@AutoLog(value = "培训任务人员-添加")
	@ApiOperation(value="培训任务人员-添加", notes="培训任务人员-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdTrainTaskUser bdTrainTaskUser) {
		bdTrainTaskUserService.save(bdTrainTaskUser);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bdTrainTaskUser
	 * @return
	 */
	@AutoLog(value = "培训任务人员-编辑")
	@ApiOperation(value="培训任务人员-编辑", notes="培训任务人员-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdTrainTaskUser bdTrainTaskUser) {
		bdTrainTaskUserService.updateById(bdTrainTaskUser);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "培训任务人员-通过id删除")
	@ApiOperation(value="培训任务人员-通过id删除", notes="培训任务人员-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdTrainTaskUserService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "培训任务人员-批量删除")
	@ApiOperation(value="培训任务人员-批量删除", notes="培训任务人员-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdTrainTaskUserService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "培训任务人员-通过id查询")
	@ApiOperation(value="培训任务人员-通过id查询", notes="培训任务人员-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdTrainTaskUser bdTrainTaskUser = bdTrainTaskUserService.getById(id);
		if(bdTrainTaskUser==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdTrainTaskUser);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bdTrainTaskUser
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdTrainTaskUser bdTrainTaskUser) {
        return super.exportXls(request, bdTrainTaskUser, BdTrainTaskUser.class, "培训任务人员");
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
        return super.importExcel(request, response, BdTrainTaskUser.class);
    }

}
