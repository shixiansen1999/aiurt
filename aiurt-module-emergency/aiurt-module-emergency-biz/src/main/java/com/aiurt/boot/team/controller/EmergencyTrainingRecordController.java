package com.aiurt.boot.team.controller;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.team.constant.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.dto.EmergencyTrainingRecordDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingProgram;
import com.aiurt.boot.team.entity.EmergencyTrainingRecord;
import com.aiurt.boot.team.service.IEmergencyTrainingRecordService;
import com.aiurt.boot.team.vo.EmergencyTrainingRecordVO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

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
	@ApiOperation(value="应急队伍训练记录-分页列表查询", notes="应急队伍训练记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyTrainingRecordVO>> queryPageList(EmergencyTrainingRecordDTO emergencyTrainingRecordDTO,
																@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
																@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
																HttpServletRequest req) {
		IPage<EmergencyTrainingRecordVO> pageList = emergencyTrainingRecordService.queryPageList(emergencyTrainingRecordDTO, pageNo, pageSize);
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
	public Result<String> add(@RequestBody   @Validated(EmergencyTrainingRecord.Save.class) EmergencyTrainingRecord emergencyTrainingRecord) {
		return emergencyTrainingRecordService.add(emergencyTrainingRecord);
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
	public Result<String> edit(@RequestBody @Validated(EmergencyTrainingRecord.Update.class)EmergencyTrainingRecord emergencyTrainingRecord) {
		emergencyTrainingRecordService.edit(emergencyTrainingRecord);
		return Result.OK("编辑成功!");
	}
	 /**
	  * 提交
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "应急队伍训练记录-提交")
	 @ApiOperation(value="应急队伍训练记录-提交", notes="应急队伍训练记录-提交")
	 @DeleteMapping(value = "/recordSubmit")
	 @Transactional(rollbackFor = Exception.class)
	 public Result<String> recordSubmit(@RequestParam(name="id",required=true) String id) {
		 EmergencyTrainingRecord record = emergencyTrainingRecordService.getById(id);
		 if (TeamConstant.SUBMITTED.equals(record.getStatus())) {
			 return Result.error("当前记录已提交,不能重复提交");
		 }
		 //如果是提交，判断是否所有内容填写完整
		 Result<EmergencyTrainingRecordVO> result = this.queryById(id);
		 if (CollUtil.isEmpty(result.getResult().getTrainingCrews()) || CollUtil.isEmpty(result.getResult().getProcessRecords()) ||CollUtil.isEmpty(result.getResult().getRecordAtts())) {
			 return Result.error("还有内容没有填写，不能提交");
		 }
		 record.setStatus(TeamConstant.SUBMITTED);
		 emergencyTrainingRecordService.updateById(record);
		 emergencyTrainingRecordService.submit(record);
		 return Result.OK("提交成功!");
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
		EmergencyTrainingRecord program = emergencyTrainingRecordService.getById(id);
		if(program==null) {
			return Result.error("未找到对应数据");
		}
		if (TeamConstant.SUBMITTED.equals(program.getStatus())) {
			return Result.error("当前记录已提交，不可删除");
		}
		emergencyTrainingRecordService.delete(id);
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
		List<String> idList = Arrays.asList(ids.split(","));
		for (String id : idList) {
			EmergencyTrainingRecord program = emergencyTrainingRecordService.getById(id);
			if(program==null) {
				return Result.error("未找到对应数据");
			}
			if (TeamConstant.SUBMITTED.equals(program.getStatus())) {
				return Result.error("当前记录已提交，不可删除");
			}
			emergencyTrainingRecordService.delete(id);
		}
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="应急队伍训练记录-通过id查询", notes="应急队伍训练记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyTrainingRecordVO> queryById(@RequestParam(name="id",required=true) String id) {
		return emergencyTrainingRecordService.queryById(id);
	}

	/**
	 * 根据应急队伍选择训练计划
	 *
	 * @param emergencyTrainingProgramDTO
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="应急队伍训练记录-根据应急队伍选择训练计划", notes="应急队伍训练记录-根据应急队伍选择训练计划")
	@PostMapping(value = "/getTrainingProgram")
	public Result<IPage<EmergencyTrainingProgram>> getTrainingProgram(@RequestBody EmergencyTrainingProgramDTO emergencyTrainingProgramDTO,
																	  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
																	  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
																	  HttpServletRequest req) {
		IPage<EmergencyTrainingProgram> trainingProgram = emergencyTrainingRecordService.getTrainingProgram(emergencyTrainingProgramDTO, pageNo, pageSize);
		return Result.OK(trainingProgram);
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
        return 	emergencyTrainingRecordService.importExcel(request,response);
    }

}
