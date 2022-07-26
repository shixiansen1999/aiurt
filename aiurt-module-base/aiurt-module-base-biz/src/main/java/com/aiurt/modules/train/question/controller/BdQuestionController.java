package com.aiurt.modules.train.question.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.aiurt.modules.train.question.service.IBdQuestionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

 /**
 * @Description: bd_question
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Api(tags="考卷习题")
@RestController
@RequestMapping("/question/bdQuestion")
@Slf4j
public class BdQuestionController extends BaseController<BdQuestion, IBdQuestionService> {
	@Autowired
	private IBdQuestionService bdQuestionService;
	
	/**
	 * 分页列表查询
	 *
	 * @param condition
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "考卷习题-分页列表查询")
	@ApiOperation(value="考卷习题-分页列表查询", notes="考卷习题-分页列表查询")
	@PostMapping(value = "/questionList")
	public Result<?> queryPageList(@RequestBody BdQuestion condition,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Page<BdQuestion> pageList = new Page<>(pageNo, pageSize);
		Page<BdQuestion> bdQuestionPage = bdQuestionService.queryPageList(pageList, condition);
		return Result.OK(bdQuestionPage);
	}

	/**
	 *   添加
	 *
	 * @param bdQuestion
	 * @return
	 */
	@AutoLog(value = "考卷习题-添加")
	@ApiOperation(value="考卷习题-添加", notes="考卷习题-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdQuestion bdQuestion) {
		bdQuestionService.addBdQuestion(bdQuestion);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bdQuestion
	 * @return
	 */
	@AutoLog(value = "考卷习题-编辑")
	@ApiOperation(value="考卷习题-编辑", notes="考卷习题-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdQuestion bdQuestion) {
		bdQuestionService.updateBdQuestion(bdQuestion);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "考卷习题-通过id删除")
	@ApiOperation(value="考卷习题-通过id删除", notes="考卷习题-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdQuestionService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "考卷习题-批量删除")
	@ApiOperation(value="考卷习题-批量删除", notes="考卷习题-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdQuestionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "考卷习题-通过id查询")
	@ApiOperation(value="考卷习题-通过id查询", notes="考卷习题-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdQuestion bdQuestion = bdQuestionService.getById(id);
		if(bdQuestion==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdQuestion);
	}


	 /**
	  * 通过id查询列表
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "考卷习题-通过id查询列表")
	 @ApiOperation(value="考卷习题-通过id查询列表", notes="考卷习题-通过id查询列表")
	 @GetMapping(value = "/queryByInlist")
	 public Result<?> queryByInlist(@RequestParam(name="id",required=true) String id) {
		 BdQuestion bdQuestion = bdQuestionService.bdQuestion(id);
		 if(bdQuestion==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(bdQuestion);
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param bdQuestion
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdQuestion bdQuestion) {
        return super.exportXls(request, bdQuestion, BdQuestion.class, "bd_question");
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
        return super.importExcel(request, response, BdQuestion.class);
    }

	 /**
	  * 查看学习资料
	  * @param paperId 试卷id
	  * @autor lkj
	  */
	 @AutoLog(value = "查看学习资料")
	 @ApiOperation(value="查看学习资料-详情", notes="查看学习资料")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = BdQuestion.class)
	 })
	 @GetMapping(value = "/getLearningMaterials")
		 public Result<?> getLearningMaterials(@RequestParam String paperId) {
		 List<BdQuestion> learningMaterials = bdQuestionService.getLearningMaterials(paperId);
		 return Result.OK(learningMaterials);
	 }
}
