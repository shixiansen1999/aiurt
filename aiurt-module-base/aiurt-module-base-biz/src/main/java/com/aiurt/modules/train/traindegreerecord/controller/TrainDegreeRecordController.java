package com.aiurt.modules.train.traindegreerecord.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.traindegreerecord.entity.TrainDegreeRecord;
import com.aiurt.modules.train.traindegreerecord.service.ITrainDegreeRecordService;
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
 * @Description: train_degree_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
@Api(tags="train_degree_record")
@RestController
@RequestMapping("/traindegreerecord/trainDegreeRecord")
@Slf4j
public class TrainDegreeRecordController extends BaseController<TrainDegreeRecord, ITrainDegreeRecordService> {
	@Autowired
	private ITrainDegreeRecordService trainDegreeRecordService;

	/**
	 * 分页列表查询
	 *
	 * @param trainDegreeRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "train_degree_record-分页列表查询")
	@ApiOperation(value="train_degree_record-分页列表查询", notes="train_degree_record-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TrainDegreeRecord>> queryPageList(TrainDegreeRecord trainDegreeRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TrainDegreeRecord> queryWrapper = QueryGenerator.initQueryWrapper(trainDegreeRecord, req.getParameterMap());
		Page<TrainDegreeRecord> page = new Page<TrainDegreeRecord>(pageNo, pageSize);
		IPage<TrainDegreeRecord> pageList = trainDegreeRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param trainDegreeRecord
	 * @return
	 */
	@AutoLog(value = "train_degree_record-添加")
	@ApiOperation(value="train_degree_record-添加", notes="train_degree_record-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TrainDegreeRecord trainDegreeRecord) {
		trainDegreeRecordService.save(trainDegreeRecord);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param trainDegreeRecord
	 * @return
	 */
	@AutoLog(value = "train_degree_record-编辑")
	@ApiOperation(value="train_degree_record-编辑", notes="train_degree_record-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TrainDegreeRecord trainDegreeRecord) {
		trainDegreeRecordService.updateById(trainDegreeRecord);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "train_degree_record-通过id删除")
	@ApiOperation(value="train_degree_record-通过id删除", notes="train_degree_record-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		trainDegreeRecordService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "train_degree_record-批量删除")
	@ApiOperation(value="train_degree_record-批量删除", notes="train_degree_record-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.trainDegreeRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "train_degree_record-通过id查询")
	@ApiOperation(value="train_degree_record-通过id查询", notes="train_degree_record-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TrainDegreeRecord> queryById(@RequestParam(name="id",required=true) String id) {
		TrainDegreeRecord trainDegreeRecord = trainDegreeRecordService.getById(id);
		if(trainDegreeRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(trainDegreeRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param trainDegreeRecord
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TrainDegreeRecord trainDegreeRecord) {
        return super.exportXls(request, trainDegreeRecord, TrainDegreeRecord.class, "train_degree_record");
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
        return super.importExcel(request, response, TrainDegreeRecord.class);
    }

}
