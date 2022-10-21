package com.aiurt.modules.train.question.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.question.entity.BdQuestionOptions;
import com.aiurt.modules.train.question.service.IBdQuestionOptionsService;
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
 * @Description: bd_question_options
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Api(tags="习题选项")
@RestController
@RequestMapping("/questionoptions/bdQuestionOptions")
@Slf4j
public class BdQuestionOptionsController extends BaseController<BdQuestionOptions, IBdQuestionOptionsService> {
	@Autowired
	private IBdQuestionOptionsService bdQuestionOptionsService;

	/**
	 * 分页列表查询
	 *
	 * @param bdQuestionOptions
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "bd_question_options-分页列表查询")
	@ApiOperation(value="bd_question_options-分页列表查询", notes="bd_question_options-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BdQuestionOptions bdQuestionOptions,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BdQuestionOptions> queryWrapper = QueryGenerator.initQueryWrapper(bdQuestionOptions, req.getParameterMap());
		Page<BdQuestionOptions> page = new Page<BdQuestionOptions>(pageNo, pageSize);
		IPage<BdQuestionOptions> pageList = bdQuestionOptionsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param bdQuestionOptions
	 * @return
	 */
	@AutoLog(value = "bd_question_options-添加")
	@ApiOperation(value="bd_question_options-添加", notes="bd_question_options-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdQuestionOptions bdQuestionOptions) {
		bdQuestionOptionsService.save(bdQuestionOptions);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param bdQuestionOptions
	 * @return
	 */
	@AutoLog(value = "bd_question_options-编辑")
	@ApiOperation(value="bd_question_options-编辑", notes="bd_question_options-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdQuestionOptions bdQuestionOptions) {
		bdQuestionOptionsService.updateById(bdQuestionOptions);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bd_question_options-通过id删除")
	@ApiOperation(value="bd_question_options-通过id删除", notes="bd_question_options-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdQuestionOptionsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "bd_question_options-批量删除")
	@ApiOperation(value="bd_question_options-批量删除", notes="bd_question_options-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdQuestionOptionsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bd_question_options-通过id查询")
	@ApiOperation(value="bd_question_options-通过id查询", notes="bd_question_options-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdQuestionOptions bdQuestionOptions = bdQuestionOptionsService.getById(id);
		if(bdQuestionOptions==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdQuestionOptions);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bdQuestionOptions
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdQuestionOptions bdQuestionOptions) {
        return super.exportXls(request, bdQuestionOptions, BdQuestionOptions.class, "bd_question_options");
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
        return super.importExcel(request, response, BdQuestionOptions.class);
    }

}
