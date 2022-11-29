package com.aiurt.boot.team.controller;

import com.aiurt.boot.team.entity.EmergencyTrainingRecordCrew;
import com.aiurt.boot.team.service.IEmergencyTrainingRecordCrewService;
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
 * @Description: emergency_training_record_crew
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_training_record_crew")
@RestController
@RequestMapping("/emergency/emergencyTrainingRecordCrew")
@Slf4j
public class EmergencyTrainingRecordCrewController extends BaseController<EmergencyTrainingRecordCrew, IEmergencyTrainingRecordCrewService> {
	@Autowired
	private IEmergencyTrainingRecordCrewService emergencyTrainingRecordCrewService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyTrainingRecordCrew
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_training_record_crew-分页列表查询")
	@ApiOperation(value="emergency_training_record_crew-分页列表查询", notes="emergency_training_record_crew-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyTrainingRecordCrew>> queryPageList(EmergencyTrainingRecordCrew emergencyTrainingRecordCrew,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyTrainingRecordCrew> queryWrapper = QueryGenerator.initQueryWrapper(emergencyTrainingRecordCrew, req.getParameterMap());
		Page<EmergencyTrainingRecordCrew> page = new Page<EmergencyTrainingRecordCrew>(pageNo, pageSize);
		IPage<EmergencyTrainingRecordCrew> pageList = emergencyTrainingRecordCrewService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyTrainingRecordCrew
	 * @return
	 */
	@AutoLog(value = "emergency_training_record_crew-添加")
	@ApiOperation(value="emergency_training_record_crew-添加", notes="emergency_training_record_crew-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyTrainingRecordCrew emergencyTrainingRecordCrew) {
		emergencyTrainingRecordCrewService.save(emergencyTrainingRecordCrew);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyTrainingRecordCrew
	 * @return
	 */
	@AutoLog(value = "emergency_training_record_crew-编辑")
	@ApiOperation(value="emergency_training_record_crew-编辑", notes="emergency_training_record_crew-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyTrainingRecordCrew emergencyTrainingRecordCrew) {
		emergencyTrainingRecordCrewService.updateById(emergencyTrainingRecordCrew);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_training_record_crew-通过id删除")
	@ApiOperation(value="emergency_training_record_crew-通过id删除", notes="emergency_training_record_crew-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyTrainingRecordCrewService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_training_record_crew-批量删除")
	@ApiOperation(value="emergency_training_record_crew-批量删除", notes="emergency_training_record_crew-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyTrainingRecordCrewService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_training_record_crew-通过id查询")
	@ApiOperation(value="emergency_training_record_crew-通过id查询", notes="emergency_training_record_crew-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyTrainingRecordCrew> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyTrainingRecordCrew emergencyTrainingRecordCrew = emergencyTrainingRecordCrewService.getById(id);
		if(emergencyTrainingRecordCrew==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyTrainingRecordCrew);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyTrainingRecordCrew
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyTrainingRecordCrew emergencyTrainingRecordCrew) {
        return super.exportXls(request, emergencyTrainingRecordCrew, EmergencyTrainingRecordCrew.class, "emergency_training_record_crew");
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
        return super.importExcel(request, response, EmergencyTrainingRecordCrew.class);
    }

}
