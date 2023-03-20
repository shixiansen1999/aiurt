package com.aiurt.modules.train.eaxm.controller;

import org.jeecg.common.api.vo.Result;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.exam.entity.BdExamPaper;
import com.aiurt.modules.train.eaxm.service.IBdExamPaperService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 试卷库表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Api(tags="试卷库表")
@RestController
@RequestMapping("/exampaper/bdExamPaper")
@Slf4j
public class BdExamPaperController extends BaseController<BdExamPaper, IBdExamPaperService> {
	@Autowired
	private IBdExamPaperService bdExamPaperService;

	/**
	 * 分页列表查询
	 *
	 * @param bdExamPaper
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "试卷库表-分页列表查询")
	@ApiOperation(value="试卷库表-分页列表查询", notes="试卷库表-分页列表查询")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = BdExamPaper.class),
	})
	@PostMapping(value = "/list")
	public Result<?> queryPageList(@RequestBody BdExamPaper bdExamPaper,
								   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								   @RequestParam(name = "examClassify",required = false) String examClassify,
								   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
								   @RequestParam (name = "userId", required = false)String userId,
								   HttpServletRequest req) {
		Page<BdExamPaper> pageList = new Page<>(pageNo, pageSize);
		Page<BdExamPaper> bdExamPaperPage = bdExamPaperService.queryPageList(pageList, bdExamPaper,userId,examClassify);
		return Result.OK(bdExamPaperPage);
	}

	/**
	 * 培训题库-分页列表查询
	 * @param bdExamPaper
	 * @param req
	 * @return
	 */
	@AutoLog(value = "培训题库-分页列表查询")
	@ApiOperation(value="培训题库-分页列表查询", notes="培训题库-分页列表查询")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = BdExamPaper.class),
	})
	@PostMapping(value = "/examPaperList")
	public Result<?> examPaperList(@RequestBody BdExamPaper bdExamPaper,HttpServletRequest req) {
		Page<BdExamPaper> pageList = new Page<>(bdExamPaper.getPageNo(), bdExamPaper.getPageSize());
		if (bdExamPaper.getPageNo()==null||bdExamPaper.getPageSize()==null){
			bdExamPaper.setPageNo(1);
			bdExamPaper.setPageSize(10);
		}
		Page<BdExamPaper> bdExamPaperPage = bdExamPaperService.examPaperList(pageList, bdExamPaper);
		return Result.OK(bdExamPaperPage);
	}


	@AutoLog(value = "培训题库-打印")
	@ApiOperation(value="培训题库-打印", notes="培训题库-打印")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = BdExamPaper.class),
	})
	@PostMapping(value = "/examPaperPrintList")
	public Result<?> examPaperPrintList(@RequestBody BdExamPaper bdExamPaper,
										@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
										@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
										HttpServletRequest req) {
		Page<BdExamPaper> pageList = new Page<>(pageNo, pageSize);
		Page<BdExamPaper> bdExamPaperPage = bdExamPaperService.examPaperPrintList(pageList, bdExamPaper);
		return Result.OK(bdExamPaperPage);
	}


	/**
	 * app-查看考试详情
	 * @param bdExamPaper
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "app-查看考试详情")
	@ApiOperation(value="app-查看考试详情", notes="app-查看考试详情")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = BdExamPaper.class),
	})
	@PostMapping(value = "/getStudentAppExamRecord")
	public Result<?> getStudentAppExamRecord(@RequestBody BdExamPaper bdExamPaper,
								   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
								   HttpServletRequest req) {
		Page<BdExamPaper> pageList = new Page<>(pageNo, pageSize);
		Page<BdExamPaper> bdExamPaperPage = bdExamPaperService.getStudentAppExamRecord(pageList, bdExamPaper);
		return Result.OK(bdExamPaperPage);
	}

	/**
	 * 培训题库-详情
	 * @param bdExamPaper
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "培训题库-详情")
	@ApiOperation(value="培训题库-详情", notes="培训题库-详情")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = BdExamPaper.class),
	})
	@PostMapping(value = "/examPaperDetail")
	public Result<?> examPaperDetail(@RequestBody BdExamPaper bdExamPaper,
									 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
									 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
									 HttpServletRequest req) {
		Page<BdExamPaper> pageList = new Page<>(pageNo, pageSize);
		Page<BdExamPaper> bdExamPaperPage = bdExamPaperService.examPaperDetail(pageList, bdExamPaper);
		return Result.OK(bdExamPaperPage);
	}



	/**
	  * 试题详情（台账、培训任务）
	  * @param id
	  * @autor hlq
	  */

	 @AutoLog(value = "试题详情")
	 @ApiOperation(value="试题详情", notes="试题详情")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = BdExamPaper.class),
	 })
	 @GetMapping(value = "/getTrainQuestionDetail")
	 public Result<?> getTrainQuestionDetail(@RequestParam String id) {
		 BdExamPaper examPaper =bdExamPaperService.questionDetail(id);
		 if(examPaper==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(examPaper);
	 }

	/**
	 *  培训题库-作废
	 * @param id
	 * @return
	 */
	@AutoLog(value = "试卷库表-通过id作废")
	@ApiOperation(value="试卷库表-通过id作废", notes="试卷库表-通过id作废")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = BdExamPaper.class),
	})
	@PutMapping(value = "/becomeInvalid")
	public Result<?> becomeInvalid(@RequestParam(name="id",required=true) String id){
		bdExamPaperService.updateState(id);
		return Result.OK("作废成功!");
	}

	/**
	 *   添加
	 *
	 * @param bdExamPaper
	 * @autor lkj
	 * @return
	 */
	@AutoLog(value = "试卷库表-添加")
	@ApiOperation(value="试卷库表-添加", notes="试卷库表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdExamPaper bdExamPaper) {
		bdExamPaperService.addDetail(bdExamPaper);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param bdExamPaper
	 * @return
	 */
	@AutoLog(value = "试卷库表-编辑")
	@ApiOperation(value="试卷库表-编辑", notes="试卷库表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdExamPaper bdExamPaper) {
		bdExamPaperService.updateById(bdExamPaper);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "试卷库表-通过id删除")
	@ApiOperation(value="试卷库表-通过id删除", notes="试卷库表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdExamPaperService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "试卷库表-批量删除")
	@ApiOperation(value="试卷库表-批量删除", notes="试卷库表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdExamPaperService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "试卷库表-通过id查询")
	@ApiOperation(value="试卷库表-通过id查询", notes="试卷库表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdExamPaper bdExamPaper = bdExamPaperService.getById(id);
		if(bdExamPaper==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdExamPaper);
	}



    /**
    * 导出excel
    *
    * @param request
    * @param bdExamPaper
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdExamPaper bdExamPaper) {
        return super.exportXls(request, bdExamPaper, BdExamPaper.class, "试卷库表");
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
        return super.importExcel(request, response, BdExamPaper.class);
    }

}
