package com.aiurt.boot.team.controller;

import com.aiurt.boot.team.dto.EmergencyTrainingRecordDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingRecord;
import com.aiurt.boot.team.service.IEmergencyTrainingRecordService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: emergency_training_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="应急队伍训练记录")
@RestController
@RequestMapping("/emergency/emergencyTrainingRecord")
@Slf4j
public class EmergencyTrainingRecordController extends BaseController<EmergencyTrainingRecord, IEmergencyTrainingRecordService> {
	@Autowired
	private IEmergencyTrainingRecordService emergencyTrainingRecordService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyTrainingRecordDTO
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_training_record-分页列表查询")
	@ApiOperation(value="应急队伍训练记录-分页列表查询", notes="应急队伍训练记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyTrainingRecord>> queryPageList(EmergencyTrainingRecordDTO emergencyTrainingRecordDTO,
																@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
																@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
																HttpServletRequest req) {
		IPage<EmergencyTrainingRecord> pageList = emergencyTrainingRecordService.queryPageList(emergencyTrainingRecordDTO, pageNo, pageSize);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyTrainingRecord
	 * @return
	 */
	@AutoLog(value = "应急队伍训练记录-添加")
	@ApiOperation(value="应急队伍训练记录-添加", notes="应急队伍训练记录-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyTrainingRecord emergencyTrainingRecord) {
		emergencyTrainingRecordService.save(emergencyTrainingRecord);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyTrainingRecord
	 * @return
	 */
	@AutoLog(value = "应急队伍训练记录-编辑")
	@ApiOperation(value="应急队伍训练记录-编辑", notes="应急队伍训练记录-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyTrainingRecord emergencyTrainingRecord) {
		emergencyTrainingRecordService.updateById(emergencyTrainingRecord);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "应急队伍训练记录-通过id删除")
	@ApiOperation(value="应急队伍训练记录-通过id删除", notes="应急队伍训练记录-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyTrainingRecordService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "应急队伍训练记录-批量删除")
	@ApiOperation(value="应急队伍训练记录-批量删除", notes="应急队伍训练记录-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyTrainingRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_training_record-通过id查询")
	@ApiOperation(value="应急队伍训练记录-通过id查询", notes="应急队伍训练记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyTrainingRecord> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyTrainingRecord emergencyTrainingRecord = emergencyTrainingRecordService.getById(id);
		if(emergencyTrainingRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyTrainingRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyTrainingRecord
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyTrainingRecord emergencyTrainingRecord) {
        return super.exportXls(request, emergencyTrainingRecord, EmergencyTrainingRecord.class, "emergency_training_record");
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
        return super.importExcel(request, response, EmergencyTrainingRecord.class);
    }

}
