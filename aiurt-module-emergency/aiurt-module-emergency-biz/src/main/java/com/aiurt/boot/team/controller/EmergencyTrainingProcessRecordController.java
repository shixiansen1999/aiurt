package com.aiurt.boot.team.controller;

import com.aiurt.boot.team.entity.EmergencyTrainingProcessRecord;
import com.aiurt.boot.team.service.IEmergencyTrainingProcessRecordService;
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
 * @Description: emergency_training_process_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_training_process_record")
@RestController
@RequestMapping("/emergency/emergencyTrainingProcessRecord")
@Slf4j
public class EmergencyTrainingProcessRecordController extends BaseController<EmergencyTrainingProcessRecord, IEmergencyTrainingProcessRecordService> {
	@Autowired
	private IEmergencyTrainingProcessRecordService emergencyTrainingProcessRecordService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyTrainingProcessRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_training_process_record-分页列表查询")
	@ApiOperation(value="emergency_training_process_record-分页列表查询", notes="emergency_training_process_record-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyTrainingProcessRecord>> queryPageList(EmergencyTrainingProcessRecord emergencyTrainingProcessRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyTrainingProcessRecord> queryWrapper = QueryGenerator.initQueryWrapper(emergencyTrainingProcessRecord, req.getParameterMap());
		Page<EmergencyTrainingProcessRecord> page = new Page<EmergencyTrainingProcessRecord>(pageNo, pageSize);
		IPage<EmergencyTrainingProcessRecord> pageList = emergencyTrainingProcessRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyTrainingProcessRecord
	 * @return
	 */
	@AutoLog(value = "emergency_training_process_record-添加")
	@ApiOperation(value="emergency_training_process_record-添加", notes="emergency_training_process_record-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyTrainingProcessRecord emergencyTrainingProcessRecord) {
		emergencyTrainingProcessRecordService.save(emergencyTrainingProcessRecord);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyTrainingProcessRecord
	 * @return
	 */
	@AutoLog(value = "emergency_training_process_record-编辑")
	@ApiOperation(value="emergency_training_process_record-编辑", notes="emergency_training_process_record-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyTrainingProcessRecord emergencyTrainingProcessRecord) {
		emergencyTrainingProcessRecordService.updateById(emergencyTrainingProcessRecord);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_training_process_record-通过id删除")
	@ApiOperation(value="emergency_training_process_record-通过id删除", notes="emergency_training_process_record-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyTrainingProcessRecordService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_training_process_record-批量删除")
	@ApiOperation(value="emergency_training_process_record-批量删除", notes="emergency_training_process_record-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyTrainingProcessRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_training_process_record-通过id查询")
	@ApiOperation(value="emergency_training_process_record-通过id查询", notes="emergency_training_process_record-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyTrainingProcessRecord> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyTrainingProcessRecord emergencyTrainingProcessRecord = emergencyTrainingProcessRecordService.getById(id);
		if(emergencyTrainingProcessRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyTrainingProcessRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyTrainingProcessRecord
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyTrainingProcessRecord emergencyTrainingProcessRecord) {
        return super.exportXls(request, emergencyTrainingProcessRecord, EmergencyTrainingProcessRecord.class, "emergency_training_process_record");
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
        return super.importExcel(request, response, EmergencyTrainingProcessRecord.class);
    }

}
