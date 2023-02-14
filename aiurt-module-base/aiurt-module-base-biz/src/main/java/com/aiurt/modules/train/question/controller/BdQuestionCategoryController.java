package com.aiurt.modules.train.question.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.question.entity.BdQuestionCategory;
import com.aiurt.modules.train.question.service.IBdQuestionCategoryService;
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
 * @Description: bd_question_category
 * @Author: jeecg-boot
 * @Date:   2022-04-15
 * @Version: V1.0
 */
@Api(tags="习题类别")
@RestController
@RequestMapping("/exam/bdQuestionCategory")
@Slf4j
public class BdQuestionCategoryController extends BaseController<BdQuestionCategory, IBdQuestionCategoryService> {
	@Autowired
	private IBdQuestionCategoryService bdQuestionCategoryService;

	 /**
	  * 查询习题类别树
	  *
	  * @return
	  */
	 @AutoLog(value = "查询习题类别树")
	 @ApiOperation(value = "查询习题类别树", notes = "查询习题类别树")
	 @GetMapping(value = "/rootList")
	 public Result<?> queryPageList(String name) {
		 return Result.OK( bdQuestionCategoryService.queryPageList(name));
	 }

	 /**
      * 获取子数据
      * @param bdQuestionCategory
      * @param req
      * @return
      */
	@AutoLog(value = "习题类别-获取子数据")
	@ApiOperation(value="习题类别-获取子数据", notes="习题类别-获取子数据")
	@GetMapping(value = "/childList")
	public Result<?> queryPageList(BdQuestionCategory bdQuestionCategory,HttpServletRequest req) {
		QueryWrapper<BdQuestionCategory> queryWrapper = QueryGenerator.initQueryWrapper(bdQuestionCategory, req.getParameterMap());
		List<BdQuestionCategory> list = bdQuestionCategoryService.list(queryWrapper);
		IPage<BdQuestionCategory> pageList = new Page<>(1, 10, list.size());
        pageList.setRecords(list);
		return Result.OK(pageList);
	}

    /**
      * 批量查询子节点
      * @param parentIds 父ID（多个采用半角逗号分割）
      * @return 返回 IPage
      * @param parentIds
      * @return
      */
	@AutoLog(value = "习题类别-批量获取子数据")
    @ApiOperation(value="习题类别-批量获取子数据", notes="习题类别-批量获取子数据")
    @GetMapping("/getChildListBatch")
    public Result<?> getChildListBatch(@RequestParam("parentIds") String parentIds) {
        try {
            QueryWrapper<BdQuestionCategory> queryWrapper = new QueryWrapper<>();
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            queryWrapper.in("pid", parentIdList);
            List<BdQuestionCategory> list = bdQuestionCategoryService.list(queryWrapper);
            IPage<BdQuestionCategory> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("批量查询子节点失败：" + e.getMessage());
        }
    }

	/**
	 *   添加
	 *
	 * @param bdQuestionCategory
	 * @return
	 */
	@AutoLog(value = "习题类别-添加")
	@ApiOperation(value="习题类别-添加", notes="习题类别-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdQuestionCategory bdQuestionCategory) {
		bdQuestionCategoryService.addBdQuestionCategory(bdQuestionCategory);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param bdQuestionCategory
	 * @return
	 */
	@AutoLog(value = "习题类别-编辑")
	@ApiOperation(value="习题类别-编辑", notes="习题类别-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdQuestionCategory bdQuestionCategory) {
		bdQuestionCategoryService.updateBdQuestionCategory(bdQuestionCategory);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "习题类别-通过id删除")
	@ApiOperation(value="习题类别-通过id删除", notes="习题类别-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdQuestionCategoryService.deleteBdQuestionCategory(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "习题类别-批量删除")
	@ApiOperation(value="习题类别-批量删除", notes="习题类别-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdQuestionCategoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "习题类别-通过id查询")
	@ApiOperation(value="习题类别-通过id查询", notes="习题类别-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdQuestionCategory bdQuestionCategory = bdQuestionCategoryService.getById(id);
		return Result.OK(bdQuestionCategory);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bdQuestionCategory
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdQuestionCategory bdQuestionCategory) {
		return super.exportXls(request, bdQuestionCategory, BdQuestionCategory.class, "bd_question_category");
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
		return super.importExcel(request, response, BdQuestionCategory.class);
    }

}
