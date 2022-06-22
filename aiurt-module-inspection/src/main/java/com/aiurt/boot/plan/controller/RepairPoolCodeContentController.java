package com.aiurt.boot.plan.controller;

import com.aiurt.boot.entity.inspection.plan.RepairPoolCodeContent;
import com.aiurt.boot.plan.service.IRepairPoolCodeContentService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
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

 /**
 * @Description: repair_pool_code_content
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="repair_pool_code_content")
@RestController
@RequestMapping("/plan/repairPoolCodeContent")
@Slf4j
public class RepairPoolCodeContentController extends BaseController<RepairPoolCodeContent, IRepairPoolCodeContentService> {
	@Autowired
	private IRepairPoolCodeContentService repairPoolCodeContentService;

	/**
	 * 分页列表查询
	 *
	 * @param repairPoolCodeContent
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "repair_pool_code_content-分页列表查询")
	@ApiOperation(value="repair_pool_code_content-分页列表查询", notes="repair_pool_code_content-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RepairPoolCodeContent>> queryPageList(RepairPoolCodeContent repairPoolCodeContent,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<RepairPoolCodeContent> queryWrapper = QueryGenerator.initQueryWrapper(repairPoolCodeContent, req.getParameterMap());
		Page<RepairPoolCodeContent> page = new Page<RepairPoolCodeContent>(pageNo, pageSize);
		IPage<RepairPoolCodeContent> pageList = repairPoolCodeContentService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param repairPoolCodeContent
	 * @return
	 */
	@AutoLog(value = "repair_pool_code_content-添加")
	@ApiOperation(value="repair_pool_code_content-添加", notes="repair_pool_code_content-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RepairPoolCodeContent repairPoolCodeContent) {
		repairPoolCodeContentService.save(repairPoolCodeContent);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param repairPoolCodeContent
	 * @return
	 */
	@AutoLog(value = "repair_pool_code_content-编辑")
	@ApiOperation(value="repair_pool_code_content-编辑", notes="repair_pool_code_content-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RepairPoolCodeContent repairPoolCodeContent) {
		repairPoolCodeContentService.updateById(repairPoolCodeContent);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "repair_pool_code_content-通过id删除")
	@ApiOperation(value="repair_pool_code_content-通过id删除", notes="repair_pool_code_content-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		repairPoolCodeContentService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "repair_pool_code_content-批量删除")
	@ApiOperation(value="repair_pool_code_content-批量删除", notes="repair_pool_code_content-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.repairPoolCodeContentService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "repair_pool_code_content-通过id查询")
	@ApiOperation(value="repair_pool_code_content-通过id查询", notes="repair_pool_code_content-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RepairPoolCodeContent> queryById(@RequestParam(name="id",required=true) String id) {
		RepairPoolCodeContent repairPoolCodeContent = repairPoolCodeContentService.getById(id);
		if(repairPoolCodeContent==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(repairPoolCodeContent);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param repairPoolCodeContent
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RepairPoolCodeContent repairPoolCodeContent) {
        return super.exportXls(request, repairPoolCodeContent, RepairPoolCodeContent.class, "repair_pool_code_content");
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
        return super.importExcel(request, response, RepairPoolCodeContent.class);
    }

}
