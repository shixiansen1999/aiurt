package com.aiurt.boot.team.controller;

import com.aiurt.boot.team.constant.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingProgram;
import com.aiurt.boot.team.service.IEmergencyTrainingProgramService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: emergency_training_program
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="应急队伍训练计划")
@RestController
@RequestMapping("/emergency/emergencyTrainingProgram")
@Slf4j
public class EmergencyTrainingProgramController extends BaseController<EmergencyTrainingProgram, IEmergencyTrainingProgramService> {
	@Autowired
	private IEmergencyTrainingProgramService emergencyTrainingProgramService;
	@Resource
	private ISysBaseAPI sysBaseApi;
	/**
	 * 分页列表查询
	 *
	 * @param emergencyTrainingProgramDTO
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="应急队伍训练计划-分页列表查询", notes="应急队伍训练计划-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyTrainingProgram>> queryPageList(EmergencyTrainingProgramDTO emergencyTrainingProgramDTO,
																 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
																 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
																 HttpServletRequest req) {
		IPage<EmergencyTrainingProgram> pageList = emergencyTrainingProgramService.queryPageList(emergencyTrainingProgramDTO,pageNo,pageSize);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyTrainingProgram
	 * @return
	 */
	@AutoLog(value = "应急队伍训练计划-添加")
	@ApiOperation(value="应急队伍训练计划-添加", notes="应急队伍训练计划-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyTrainingProgram emergencyTrainingProgram) {
		emergencyTrainingProgramService.add(emergencyTrainingProgram);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyTrainingProgram
	 * @return
	 */
	@AutoLog(value = "应急队伍训练计划-编辑")
	@ApiOperation(value="应急队伍训练计划-编辑", notes="应急队伍训练计划-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT})
	public Result<String> edit(@RequestBody EmergencyTrainingProgram emergencyTrainingProgram) {
		return emergencyTrainingProgramService.edit(emergencyTrainingProgram);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "应急队伍训练计划-通过id删除")
	@ApiOperation(value="应急队伍训练计划-通过id删除", notes="应急队伍训练计划-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		EmergencyTrainingProgram program = emergencyTrainingProgramService.getById(id);
		if(program==null) {
			return Result.error("未找到对应数据");
		}
		emergencyTrainingProgramService.delete(program);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "应急队伍训练计划-批量删除")
	@ApiOperation(value="应急队伍训练计划-批量删除", notes="应急队伍训练计划-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> list = Arrays.asList(ids.split(","));
		for (String s : list) {
			EmergencyTrainingProgram program = emergencyTrainingProgramService.getById(s);
			if(program==null) {
				return Result.error("未找到对应数据");
			}
			emergencyTrainingProgramService.delete(program);
		}
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="应急队伍训练计划-通过id查询", notes="应急队伍训练计划-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyTrainingProgram> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyTrainingProgram emergencyTrainingProgram = emergencyTrainingProgramService.getById(id);
		if(emergencyTrainingProgram==null) {
			return Result.error("未找到对应数据");
		}
		return emergencyTrainingProgramService.queryById(emergencyTrainingProgram);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyTrainingProgram
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyTrainingProgram emergencyTrainingProgram) {
        return super.exportXls(request, emergencyTrainingProgram, EmergencyTrainingProgram.class, "emergency_training_program");
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
        return super.importExcel(request, response, EmergencyTrainingProgram.class);
    }

	 /**
	  * 自动生成计划编号
	  *
	  * @return
	  */
	 @ApiOperation(value="应急队伍训练计划-自动生成计划编号", notes="应急队伍训练计划-自动生成计划编号")
	 @GetMapping(value = "/getTrainPlanCode")
	 public String getTrainPlanCode() {
		 return  emergencyTrainingProgramService.getTrainPlanCode();
	 }

	/**
	 *  编辑
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "应急队伍训练计划-下发")
	@ApiOperation(value="应急队伍训练计划-下发", notes="应急队伍训练计划-下发")
	@RequestMapping(value = "/publish", method = {RequestMethod.GET})
	public Result<String> publish(@RequestParam(name="id",required=true) String id) {
		EmergencyTrainingProgram program = emergencyTrainingProgramService.getById(id);
		program.setStatus(TeamConstant.WAIT_COMPLETE);
		emergencyTrainingProgramService.updateById(program);
		emergencyTrainingProgramService.publish(program);
		return Result.OK("发布成功!");
	}
}
