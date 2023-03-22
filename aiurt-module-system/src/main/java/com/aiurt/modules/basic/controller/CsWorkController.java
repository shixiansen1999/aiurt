package com.aiurt.modules.basic.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.basic.service.ICsWorkService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 作业类型
 * @Author: aiurt
 * @Date:   2022-07-04
 * @Version: V1.0
 */
@Api(tags="作业类型")
@RestController
@RequestMapping("/manage/work")
@Slf4j
public class CsWorkController extends BaseController<CsWork, ICsWorkService> {
	@Autowired
	private ICsWorkService csWorkService;

	/**
	 * 分页列表查询
	 *
	 * @param csWork
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="作业类型-分页列表查询", notes="作业类型-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CsWork>> queryPageList(CsWork csWork,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CsWork> queryWrapper = QueryGenerator.initQueryWrapper(csWork, req.getParameterMap());
		Page<CsWork> page = new Page<>(pageNo, pageSize);
		IPage<CsWork> pageList = csWorkService.page(page, queryWrapper);
		List<CsWork> records = pageList.getRecords();
		records.stream().forEach(entity->entity.setIsPaln(entity.getIsPlan()));
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param csWork
	 * @return
	 */
	@AutoLog(value = "作业类型-添加")
	@ApiOperation(value="作业类型-添加", notes="作业类型-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CsWork csWork) {
		csWork.setIsPlan(csWork.getIsPaln());
		csWork.setWorkCode(csWork.getWorkName());
		csWorkService.save(csWork);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param csWork
	 * @return
	 */
	@AutoLog(value = "作业类型-编辑")
	@ApiOperation(value="作业类型-编辑", notes="作业类型-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CsWork csWork) {
		csWork.setIsPlan(csWork.getIsPaln());
		csWork.setWorkCode(csWork.getWorkName());
		csWorkService.updateById(csWork);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "作业类型-通过id删除", operateType = 4, operateTypeAlias = "通过id删除", permissionUrl = "/manage/WorkList")
	@ApiOperation(value="作业类型-通过id删除", notes="作业类型-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		csWorkService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "作业类型-批量删除", operateType = 4, operateTypeAlias = "批量删除", permissionUrl = "/manage/WorkList")
	@ApiOperation(value="作业类型-批量删除", notes="作业类型-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.csWorkService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="作业类型-通过id查询", notes="作业类型-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CsWork> queryById(@RequestParam(name="id",required=true) String id) {
		CsWork csWork = csWorkService.getById(id);
		if(csWork==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csWork);
	}

}
