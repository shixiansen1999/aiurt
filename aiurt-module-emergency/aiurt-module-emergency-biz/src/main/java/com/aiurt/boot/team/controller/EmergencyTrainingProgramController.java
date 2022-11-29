package com.aiurt.boot.team.controller;

import com.aiurt.boot.team.service.IEmergencyTrainingProgramService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.team.entity.EmergencyTrainingProgram;
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
 * @Description: emergency_training_program
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_training_program")
@RestController
@RequestMapping("/emergency/emergencyTrainingProgram")
@Slf4j
public class EmergencyTrainingProgramController extends BaseController<EmergencyTrainingProgram, IEmergencyTrainingProgramService> {
	@Autowired
	private IEmergencyTrainingProgramService emergencyTrainingProgramService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyTrainingProgram
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_training_program-分页列表查询")
	@ApiOperation(value="emergency_training_program-分页列表查询", notes="emergency_training_program-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyTrainingProgram>> queryPageList(EmergencyTrainingProgram emergencyTrainingProgram,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyTrainingProgram> queryWrapper = QueryGenerator.initQueryWrapper(emergencyTrainingProgram, req.getParameterMap());
		Page<EmergencyTrainingProgram> page = new Page<EmergencyTrainingProgram>(pageNo, pageSize);
		IPage<EmergencyTrainingProgram> pageList = emergencyTrainingProgramService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyTrainingProgram
	 * @return
	 */
	@AutoLog(value = "emergency_training_program-添加")
	@ApiOperation(value="emergency_training_program-添加", notes="emergency_training_program-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyTrainingProgram emergencyTrainingProgram) {
		emergencyTrainingProgramService.save(emergencyTrainingProgram);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyTrainingProgram
	 * @return
	 */
	@AutoLog(value = "emergency_training_program-编辑")
	@ApiOperation(value="emergency_training_program-编辑", notes="emergency_training_program-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyTrainingProgram emergencyTrainingProgram) {
		emergencyTrainingProgramService.updateById(emergencyTrainingProgram);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_training_program-通过id删除")
	@ApiOperation(value="emergency_training_program-通过id删除", notes="emergency_training_program-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyTrainingProgramService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_training_program-批量删除")
	@ApiOperation(value="emergency_training_program-批量删除", notes="emergency_training_program-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyTrainingProgramService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_training_program-通过id查询")
	@ApiOperation(value="emergency_training_program-通过id查询", notes="emergency_training_program-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyTrainingProgram> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyTrainingProgram emergencyTrainingProgram = emergencyTrainingProgramService.getById(id);
		if(emergencyTrainingProgram==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyTrainingProgram);
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

}
