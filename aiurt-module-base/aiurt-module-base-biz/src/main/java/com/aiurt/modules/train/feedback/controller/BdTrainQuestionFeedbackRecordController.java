package com.aiurt.modules.train.feedback.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackRecord;
import com.aiurt.modules.train.feedback.service.IBdTrainQuestionFeedbackRecordService;
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
 * @Description: bd_train_question_feedback_record
 * @Author: jeecg-boot
 * @Date:   2022-05-23
 * @Version: V1.0
 */
@Api(tags="bd_train_question_feedback_record")
@RestController
@RequestMapping("/feedback/bdTrainQuestionFeedbackRecord")
@Slf4j
public class BdTrainQuestionFeedbackRecordController extends BaseController<BdTrainQuestionFeedbackRecord, IBdTrainQuestionFeedbackRecordService> {
	@Autowired
	private IBdTrainQuestionFeedbackRecordService bdTrainQuestionFeedbackRecordService;
	
	/**
	 * 分页列表查询
	 *
	 * @param bdTrainQuestionFeedbackRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "bd_train_question_feedback_record-分页列表查询")
	@ApiOperation(value="bd_train_question_feedback_record-分页列表查询", notes="bd_train_question_feedback_record-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BdTrainQuestionFeedbackRecord bdTrainQuestionFeedbackRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BdTrainQuestionFeedbackRecord> queryWrapper = QueryGenerator.initQueryWrapper(bdTrainQuestionFeedbackRecord, req.getParameterMap());
		Page<BdTrainQuestionFeedbackRecord> page = new Page<BdTrainQuestionFeedbackRecord>(pageNo, pageSize);
		IPage<BdTrainQuestionFeedbackRecord> pageList = bdTrainQuestionFeedbackRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param bdTrainQuestionFeedbackRecord
	 * @return
	 */
	@AutoLog(value = "bd_train_question_feedback_record-添加")
	@ApiOperation(value="bd_train_question_feedback_record-添加", notes="bd_train_question_feedback_record-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdTrainQuestionFeedbackRecord bdTrainQuestionFeedbackRecord) {
		bdTrainQuestionFeedbackRecordService.save(bdTrainQuestionFeedbackRecord);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bdTrainQuestionFeedbackRecord
	 * @return
	 */
	@AutoLog(value = "bd_train_question_feedback_record-编辑")
	@ApiOperation(value="bd_train_question_feedback_record-编辑", notes="bd_train_question_feedback_record-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdTrainQuestionFeedbackRecord bdTrainQuestionFeedbackRecord) {
		bdTrainQuestionFeedbackRecordService.updateById(bdTrainQuestionFeedbackRecord);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bd_train_question_feedback_record-通过id删除")
	@ApiOperation(value="bd_train_question_feedback_record-通过id删除", notes="bd_train_question_feedback_record-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdTrainQuestionFeedbackRecordService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "bd_train_question_feedback_record-批量删除")
	@ApiOperation(value="bd_train_question_feedback_record-批量删除", notes="bd_train_question_feedback_record-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdTrainQuestionFeedbackRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bd_train_question_feedback_record-通过id查询")
	@ApiOperation(value="bd_train_question_feedback_record-通过id查询", notes="bd_train_question_feedback_record-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdTrainQuestionFeedbackRecord bdTrainQuestionFeedbackRecord = bdTrainQuestionFeedbackRecordService.getById(id);
		if(bdTrainQuestionFeedbackRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdTrainQuestionFeedbackRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bdTrainQuestionFeedbackRecord
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdTrainQuestionFeedbackRecord bdTrainQuestionFeedbackRecord) {
        return super.exportXls(request, bdTrainQuestionFeedbackRecord, BdTrainQuestionFeedbackRecord.class, "bd_train_question_feedback_record");
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
        return super.importExcel(request, response, BdTrainQuestionFeedbackRecord.class);
    }

}
