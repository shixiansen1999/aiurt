package com.aiurt.modules.train.task.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedback;
import com.aiurt.modules.train.task.dto.BdStudentFeedBackDTO;
import com.aiurt.modules.train.task.dto.StudentFeedbackRecordDTO;
import com.aiurt.modules.train.task.entity.BdTrainStudentFeedbackRecord;
import com.aiurt.modules.train.task.mapper.BdTrainStudentFeedbackRecordMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskUserMapper;
import com.aiurt.modules.train.task.service.IBdTrainStudentFeedbackRecordService;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * @Description: 学员反馈记录
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
@Api(tags="学员反馈记录")
@RestController
@RequestMapping("/feedback/bdTrainStudentFeedbackRecord")
@Slf4j
public class BdTrainStudentFeedbackRecordController extends BaseController<BdTrainStudentFeedbackRecord, IBdTrainStudentFeedbackRecordService> {
	@Autowired
	private IBdTrainStudentFeedbackRecordService bdTrainStudentFeedbackRecordService;
	@Autowired
	private BdTrainTaskUserMapper bdTrainTaskUserMapper;
	@Autowired
	private BdTrainStudentFeedbackRecordMapper bdTrainStudentFeedbackRecordMapper;
	/**
	 * 分页列表查询
	 *
	 * @param bdTrainStudentFeedbackRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "学员反馈记录-分页列表查询")
	@ApiOperation(value="学员反馈记录-分页列表查询", notes="学员反馈记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BdTrainStudentFeedbackRecord bdTrainStudentFeedbackRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BdTrainStudentFeedbackRecord> queryWrapper = QueryGenerator.initQueryWrapper(bdTrainStudentFeedbackRecord, req.getParameterMap());
		Page<BdTrainStudentFeedbackRecord> page = new Page<BdTrainStudentFeedbackRecord>(pageNo, pageSize);
		IPage<BdTrainStudentFeedbackRecord> pageList = bdTrainStudentFeedbackRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param bdTrainStudentFeedbackRecord
	 * @return
	 */
	@AutoLog(value = "学员反馈记录-添加")
	@ApiOperation(value="学员反馈记录-添加", notes="学员反馈记录-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdTrainStudentFeedbackRecord bdTrainStudentFeedbackRecord) {
		bdTrainStudentFeedbackRecordService.save(bdTrainStudentFeedbackRecord);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bdTrainStudentFeedbackRecord
	 * @return
	 */
	@AutoLog(value = "学员反馈记录-编辑")
	@ApiOperation(value="学员反馈记录-编辑", notes="学员反馈记录-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdTrainStudentFeedbackRecord bdTrainStudentFeedbackRecord) {
		bdTrainStudentFeedbackRecordService.updateById(bdTrainStudentFeedbackRecord);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "学员反馈记录-通过id删除")
	@ApiOperation(value="学员反馈记录-通过id删除", notes="学员反馈记录-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdTrainStudentFeedbackRecordService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "学员反馈记录-批量删除")
	@ApiOperation(value="学员反馈记录-批量删除", notes="学员反馈记录-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdTrainStudentFeedbackRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "学员反馈记录-通过id查询")
	@ApiOperation(value="学员反馈记录-通过id查询", notes="学员反馈记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdTrainStudentFeedbackRecord bdTrainStudentFeedbackRecord = bdTrainStudentFeedbackRecordService.getById(id);
		if(bdTrainStudentFeedbackRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdTrainStudentFeedbackRecord);
	}

	 /**
	  * 学生评估查询app
	  * @param taskId
	  * @return
	  * @autor lkj
	  * */
	 @AutoLog(value = "学生培训任务-反馈查询app")
	 @ApiOperation(value="学生培训任务-反馈查询app", notes="学生培训任务-反馈查询app")
	 @GetMapping(value = "/getTrainStudentFeedbackRecordById")
	 public Result<?> getTrainStudentFeedbackRecordById(@RequestParam(name="taskId",required=true)String taskId,@RequestParam(name="userId",required=false) String userId ) {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 if (ObjectUtil.isNotNull(userId)) {
			 BdTrainQuestionFeedback studentFeedbackRecord = bdTrainStudentFeedbackRecordService.getStudentFeedbackRecordById(userId, taskId);
			 return Result.OK(studentFeedbackRecord);
		 } else {
			 BdTrainQuestionFeedback studentFeedbackRecord = bdTrainStudentFeedbackRecordService.getStudentFeedbackRecordById(sysUser.getId(), taskId);
			 return Result.OK(studentFeedbackRecord);
		 }
	 }

	 /**
	  * 学员反馈查询web(暂停使用)
	  * @param taskId
	  * @return
	  * @autor lkj
	  * */
	 @AutoLog(value = "学生培训任务web-反馈查询web")
	 @ApiOperation(value="学生培训任务web-反馈查询web", notes="学生培训任务web-反馈查询web")
	 @GetMapping(value = "/getTrainStudentFeedbackRecord")
	 public Result<?> getTrainStudentFeedbackRecord(@RequestParam(name="taskId",required=true)String taskId ,@RequestParam(name="userId",required=true) String userId ) {
		 List<StudentFeedbackRecordDTO> studentFeedbackRecord = bdTrainStudentFeedbackRecordService.getStudentFeedbackRecord(userId, taskId);
		 return Result.OK(studentFeedbackRecord);
	 }

	 /**
	  * 学生评估保存
	  * @parambd bdStudentFeedBackDTO
	  * @return
	  * @autor lkj
	  */
	 @AutoLog(value = "学生培训任务-反馈保存")
	 @ApiOperation(value = "学生培训任务-反馈保存", notes = "学生培训任务-反馈保存")
	 @PostMapping(value = "/addTrainStudentFeedbackRecord")
	 public Result<?> addTrainStudentFeedbackRecord(@RequestBody BdStudentFeedBackDTO bdStudentFeedBackDTO ) {
		 List<BdTrainStudentFeedbackRecord> bdTrainStudentFeedbackRecords = bdStudentFeedBackDTO.getBdTrainStudentFeedbackRecords();
		 for (BdTrainStudentFeedbackRecord bdTrainStudentFeedbackRecord:bdTrainStudentFeedbackRecords) {
			 if (StringUtils.isEmpty(bdTrainStudentFeedbackRecord.getId())) {
				 bdTrainStudentFeedbackRecordService.save(bdTrainStudentFeedbackRecord);
			 }
			 bdTrainStudentFeedbackRecordService.updateById(bdTrainStudentFeedbackRecord);
		 }
		 return Result.OK("保存成功！");
	 }

	 /**
	  * 学员反馈提交
	  * @param bdStudentFeedBackDTO
	  * @return
	  * @autor lkj
	  * */
	 @AutoLog(value = "学员培训任务-评估提交")
	 @ApiOperation(value="学员培训任务-评估提交", notes="学员培训任务-评估提交")
	 @PostMapping(value = "/submitStudentFeedbackRecord")
	 public Result<?> submitTeacherFeedbackRecord(@RequestBody BdStudentFeedBackDTO bdStudentFeedBackDTO) {
		 List<BdTrainStudentFeedbackRecord> bdTrainStudentFeedbackRecords = bdStudentFeedBackDTO.getBdTrainStudentFeedbackRecords();
		 for (BdTrainStudentFeedbackRecord bdTrainStudentFeedbackRecord:bdTrainStudentFeedbackRecords) {
			 if (StringUtils.isEmpty(bdTrainStudentFeedbackRecord.getId())) {
				 bdTrainStudentFeedbackRecordService.save(bdTrainStudentFeedbackRecord);
				 //更新状态为已反馈
				 bdTrainTaskUserMapper.updateFeedState(bdTrainStudentFeedbackRecord.getUserId(), bdTrainStudentFeedbackRecord.getTrainTaskId());
			 }
			 bdTrainStudentFeedbackRecordService.updateById(bdTrainStudentFeedbackRecord);
			 //更新状态为已反馈
			 bdTrainTaskUserMapper.updateFeedState(bdTrainStudentFeedbackRecord.getUserId(), bdTrainStudentFeedbackRecord.getTrainTaskId());
		 }
		 return Result.OK("提交成功！");
	 }


    /**
    * 导出excel
    *
    * @param request
    * @param bdTrainStudentFeedbackRecord
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdTrainStudentFeedbackRecord bdTrainStudentFeedbackRecord) {
        return super.exportXls(request, bdTrainStudentFeedbackRecord, BdTrainStudentFeedbackRecord.class, "学员反馈记录");
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
        return super.importExcel(request, response, BdTrainStudentFeedbackRecord.class);
    }

}
