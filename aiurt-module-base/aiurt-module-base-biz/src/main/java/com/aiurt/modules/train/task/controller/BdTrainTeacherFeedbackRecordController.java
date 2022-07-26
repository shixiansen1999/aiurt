package com.aiurt.modules.train.task.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedback;
import com.aiurt.modules.train.task.dto.BdTeacherFeedBackDTO;
import com.aiurt.modules.train.task.entity.BdTrainTask;
import com.aiurt.modules.train.task.entity.BdTrainTeacherFeedbackRecord;
import com.aiurt.modules.train.task.mapper.BdTrainTaskMapper;
import com.aiurt.modules.train.task.service.IBdTrainTeacherFeedbackRecordService;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 讲师反馈记录表
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
@Api(tags="讲师反馈记录表")
@RestController
@RequestMapping("/feedback/bdTrainTeacherFeedbackRecord")
@Slf4j
public class BdTrainTeacherFeedbackRecordController extends BaseController<BdTrainTeacherFeedbackRecord, IBdTrainTeacherFeedbackRecordService> {
	@Autowired
	private IBdTrainTeacherFeedbackRecordService bdTrainTeacherFeedbackRecordService;

	@Autowired
	private BdTrainTaskMapper bdTrainTaskMapper;
	/**
	 * 分页列表查询
	 *
	 * @param bdTrainTeacherFeedbackRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "讲师反馈记录表-分页列表查询")
	@ApiOperation(value="讲师反馈记录表-分页列表查询", notes="讲师反馈记录表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BdTrainTeacherFeedbackRecord bdTrainTeacherFeedbackRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BdTrainTeacherFeedbackRecord> queryWrapper = QueryGenerator.initQueryWrapper(bdTrainTeacherFeedbackRecord, req.getParameterMap());
		Page<BdTrainTeacherFeedbackRecord> page = new Page<BdTrainTeacherFeedbackRecord>(pageNo, pageSize);
		IPage<BdTrainTeacherFeedbackRecord> pageList = bdTrainTeacherFeedbackRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param bdTrainTeacherFeedbackRecord
	 * @return
	 */
	@AutoLog(value = "讲师反馈记录表-添加")
	@ApiOperation(value="讲师反馈记录表-添加", notes="讲师反馈记录表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdTrainTeacherFeedbackRecord bdTrainTeacherFeedbackRecord) {
		bdTrainTeacherFeedbackRecordService.save(bdTrainTeacherFeedbackRecord);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param bdTrainTeacherFeedbackRecord
	 * @return
	 */
	@AutoLog(value = "讲师反馈记录表-编辑")
	@ApiOperation(value="讲师反馈记录表-编辑", notes="讲师反馈记录表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdTrainTeacherFeedbackRecord bdTrainTeacherFeedbackRecord) {
		bdTrainTeacherFeedbackRecordService.updateById(bdTrainTeacherFeedbackRecord);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "讲师反馈记录表-通过id删除")
	@ApiOperation(value="讲师反馈记录表-通过id删除", notes="讲师反馈记录表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdTrainTeacherFeedbackRecordService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "讲师反馈记录表-批量删除")
	@ApiOperation(value="讲师反馈记录表-批量删除", notes="讲师反馈记录表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdTrainTeacherFeedbackRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "讲师反馈记录表-通过id查询")
	@ApiOperation(value="讲师反馈记录表-通过id查询", notes="讲师反馈记录表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdTrainTeacherFeedbackRecord bdTrainTeacherFeedbackRecord = bdTrainTeacherFeedbackRecordService.getById(id);
		if(bdTrainTeacherFeedbackRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdTrainTeacherFeedbackRecord);
	}




	 /**
    * 导出excel
    *
    * @param request
    * @param bdTrainTeacherFeedbackRecord
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdTrainTeacherFeedbackRecord bdTrainTeacherFeedbackRecord) {
        return super.exportXls(request, bdTrainTeacherFeedbackRecord, BdTrainTeacherFeedbackRecord.class, "讲师反馈记录表");
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
        return super.importExcel(request, response, BdTrainTeacherFeedbackRecord.class);
    }

	 /**
	  * 讲师评估查询
	  * @param taskId
	  * @return
	  * @autor lkj
	  * */
	 @AutoLog(value = "讲师授课任务-评估查询")
	 @ApiOperation(value="讲师授课任务-评估查询", notes="讲师授课任务-评估查询")
	 @GetMapping(value = "/getTeacherFeedbackRecord")
	 public Result<?> getTeacherFeedbackRecord(@RequestParam(name="taskId",required=true)String taskId ) {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
	 	 BdTrainQuestionFeedback teacherFeedbackRecord = bdTrainTeacherFeedbackRecordService.getTeacherFeedbackRecord(sysUser.getId(), taskId);
		 return Result.OK(teacherFeedbackRecord);
	 }
	/**
	 * 讲师评估查询
	 * @param taskId
	 * @return
	 * @autor lkj
	 * */
	@AutoLog(value = "讲师授课任务-评估查询-PC")
	@ApiOperation(value="讲师授课任务-评估查询", notes="讲师授课任务-评估查询")
	@GetMapping(value = "/getTeacherFeedbackRecordPc")
	public Result<?> getTeacherFeedbackRecordPc(@RequestParam(name="taskId",required=true)String taskId, @RequestParam(name="userId",required = true) String userId){
		BdTrainQuestionFeedback teacherFeedbackRecord = bdTrainTeacherFeedbackRecordService.getTeacherFeedbackRecord(userId, taskId);
		return Result.OK(teacherFeedbackRecord);
	}
	 /**
	  * 讲师评估保存
	  * @param bdTeacherFeedBackDTO
	  * @return
	  * @autor lkj
	  * */
	 @AutoLog(value = "讲师授课任务-评估保存")
	 @ApiOperation(value="讲师授课任务-评估保存", notes="讲师授课任务-评估保存")
	 @PostMapping(value = "/saveTeacherFeedbackRecord")
	 public Result<?> saveTeacherFeedbackRecord(@RequestBody BdTeacherFeedBackDTO bdTeacherFeedBackDTO) {
		 List<BdTrainTeacherFeedbackRecord> bdTrainTeacherFeedbackRecords = bdTeacherFeedBackDTO.getBdTrainTeacherFeedbackRecords();
		 for (BdTrainTeacherFeedbackRecord bdTrainTeacherFeedbackRecord:bdTrainTeacherFeedbackRecords) {
			 if (StringUtils.isEmpty(bdTrainTeacherFeedbackRecord.getId())) {
				 bdTrainTeacherFeedbackRecordService.save(bdTrainTeacherFeedbackRecord);
			 }
			 bdTrainTeacherFeedbackRecordService.updateById(bdTrainTeacherFeedbackRecord);
		 }
		 return Result.OK("保存成功！");
	 }

	 /**
	  * 讲师评估提交
	  * @param bdTeacherFeedBackDTO
	  * @return
	  * @autor lkj
	  * */
	 @AutoLog(value = "讲师授课任务-评估提交")
	 @ApiOperation(value="讲师授课任务-评估提交", notes="讲师授课任务-评估提交")
	 @PostMapping(value = "/submitTeacherFeedbackRecord")
	 public Result<?> submitTeacherFeedbackRecord(@RequestBody BdTeacherFeedBackDTO bdTeacherFeedBackDTO) {
		 List<BdTrainTeacherFeedbackRecord> bdTrainTeacherFeedbackRecords = bdTeacherFeedBackDTO.getBdTrainTeacherFeedbackRecords();
		 for (BdTrainTeacherFeedbackRecord bdTrainTeacherFeedbackRecord:bdTrainTeacherFeedbackRecords) {
			 if (StringUtils.isEmpty(bdTrainTeacherFeedbackRecord.getId())) {
				 bdTrainTeacherFeedbackRecordService.save(bdTrainTeacherFeedbackRecord);
				 //更新状态为已完成
				 BdTrainTask bdTrainTask = bdTrainTaskMapper.selectById(bdTrainTeacherFeedbackRecord.getTrainTaskId());
				 bdTrainTask.setTaskState(7);
				 bdTrainTaskMapper.updateById(bdTrainTask);
			 }
			 bdTrainTeacherFeedbackRecordService.updateById(bdTrainTeacherFeedbackRecord);
			 //更新状态为已完成
			 BdTrainTask bdTrainTask = bdTrainTaskMapper.selectById(bdTrainTeacherFeedbackRecord.getTrainTaskId());
			 bdTrainTask.setTaskState(7);
			 bdTrainTaskMapper.updateById(bdTrainTask);
		 }
		 return Result.OK("提交成功！");
	 }




	/**
	 * 讲师授课任务-已关闭-评估查询
	 * @param taskId
	 * @return
	 * @autor hlq
	 * */
	@AutoLog(value = "讲师授课任务-已关闭-评估查询")
	@ApiOperation(value="讲师授课任务-已关闭-评估查询", notes="讲师授课任务-已关闭-评估查询")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = BdTrainTeacherFeedbackRecord.class),
	})
	@GetMapping(value = "/queryTeacherTaskFeedbackEvaluate")
	public Result<?> queryTeacherTaskFeedbackEvaluate(@RequestParam(name="taskId",required=true)String taskId ) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		BdTrainTeacherFeedbackRecord teacherFeedbackRecord = bdTrainTeacherFeedbackRecordService.queryTeacherTaskFeedbackEvaluate(sysUser.getId(), taskId);
		return Result.OK(teacherFeedbackRecord);
	}

 }
