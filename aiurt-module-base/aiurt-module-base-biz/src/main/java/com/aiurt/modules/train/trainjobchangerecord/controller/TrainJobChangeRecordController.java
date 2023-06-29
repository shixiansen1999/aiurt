package com.aiurt.modules.train.trainjobchangerecord.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.trainjobchangerecord.entity.TrainJobChangeRecord;
import com.aiurt.modules.train.trainjobchangerecord.service.ITrainJobChangeRecordService;
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
 * @Description: train_job_change_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
@Api(tags="train_job_change_record")
@RestController
@RequestMapping("/trainjobchangerecord/trainJobChangeRecord")
@Slf4j
public class TrainJobChangeRecordController extends BaseController<TrainJobChangeRecord, ITrainJobChangeRecordService> {
	@Autowired
	private ITrainJobChangeRecordService trainJobChangeRecordService;

	/**
	 * 分页列表查询
	 *
	 * @param trainJobChangeRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "train_job_change_record-分页列表查询")
	@ApiOperation(value="train_job_change_record-分页列表查询", notes="train_job_change_record-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TrainJobChangeRecord>> queryPageList(TrainJobChangeRecord trainJobChangeRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TrainJobChangeRecord> queryWrapper = QueryGenerator.initQueryWrapper(trainJobChangeRecord, req.getParameterMap());
		Page<TrainJobChangeRecord> page = new Page<TrainJobChangeRecord>(pageNo, pageSize);
		IPage<TrainJobChangeRecord> pageList = trainJobChangeRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param trainJobChangeRecord
	 * @return
	 */
	@AutoLog(value = "train_job_change_record-添加")
	@ApiOperation(value="train_job_change_record-添加", notes="train_job_change_record-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TrainJobChangeRecord trainJobChangeRecord) {
		trainJobChangeRecordService.save(trainJobChangeRecord);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param trainJobChangeRecord
	 * @return
	 */
	@AutoLog(value = "train_job_change_record-编辑")
	@ApiOperation(value="train_job_change_record-编辑", notes="train_job_change_record-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TrainJobChangeRecord trainJobChangeRecord) {
		trainJobChangeRecordService.updateById(trainJobChangeRecord);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "train_job_change_record-通过id删除")
	@ApiOperation(value="train_job_change_record-通过id删除", notes="train_job_change_record-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		trainJobChangeRecordService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "train_job_change_record-批量删除")
	@ApiOperation(value="train_job_change_record-批量删除", notes="train_job_change_record-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.trainJobChangeRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "train_job_change_record-通过id查询")
	@ApiOperation(value="train_job_change_record-通过id查询", notes="train_job_change_record-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TrainJobChangeRecord> queryById(@RequestParam(name="id",required=true) String id) {
		TrainJobChangeRecord trainJobChangeRecord = trainJobChangeRecordService.getById(id);
		if(trainJobChangeRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(trainJobChangeRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param trainJobChangeRecord
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TrainJobChangeRecord trainJobChangeRecord) {
        return super.exportXls(request, trainJobChangeRecord, TrainJobChangeRecord.class, "train_job_change_record");
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
        return super.importExcel(request, response, TrainJobChangeRecord.class);
    }

}
