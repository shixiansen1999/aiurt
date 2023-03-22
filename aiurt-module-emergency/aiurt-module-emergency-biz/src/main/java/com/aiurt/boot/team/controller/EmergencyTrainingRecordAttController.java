package com.aiurt.boot.team.controller;

import com.aiurt.boot.team.entity.EmergencyTrainingRecordAtt;
import com.aiurt.boot.team.service.IEmergencyTrainingRecordAttService;
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
 * @Description: emergency_training_record_att
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_training_record_att")
@RestController
@RequestMapping("/emergency/emergencyTrainingRecordAtt")
@Slf4j
public class EmergencyTrainingRecordAttController extends BaseController<EmergencyTrainingRecordAtt, IEmergencyTrainingRecordAttService> {
	@Autowired
	private IEmergencyTrainingRecordAttService emergencyTrainingRecordAttService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyTrainingRecordAtt
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_training_record_att-分页列表查询")
	@ApiOperation(value="emergency_training_record_att-分页列表查询", notes="emergency_training_record_att-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyTrainingRecordAtt>> queryPageList(EmergencyTrainingRecordAtt emergencyTrainingRecordAtt,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyTrainingRecordAtt> queryWrapper = QueryGenerator.initQueryWrapper(emergencyTrainingRecordAtt, req.getParameterMap());
		Page<EmergencyTrainingRecordAtt> page = new Page<EmergencyTrainingRecordAtt>(pageNo, pageSize);
		IPage<EmergencyTrainingRecordAtt> pageList = emergencyTrainingRecordAttService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyTrainingRecordAtt
	 * @return
	 */
	@AutoLog(value = "emergency_training_record_att-添加")
	@ApiOperation(value="emergency_training_record_att-添加", notes="emergency_training_record_att-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyTrainingRecordAtt emergencyTrainingRecordAtt) {
		emergencyTrainingRecordAttService.save(emergencyTrainingRecordAtt);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyTrainingRecordAtt
	 * @return
	 */
	@AutoLog(value = "emergency_training_record_att-编辑")
	@ApiOperation(value="emergency_training_record_att-编辑", notes="emergency_training_record_att-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyTrainingRecordAtt emergencyTrainingRecordAtt) {
		emergencyTrainingRecordAttService.updateById(emergencyTrainingRecordAtt);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_training_record_att-通过id删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "")
	@ApiOperation(value="emergency_training_record_att-通过id删除", notes="emergency_training_record_att-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyTrainingRecordAttService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_training_record_att-批量删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "")
	@ApiOperation(value="emergency_training_record_att-批量删除", notes="emergency_training_record_att-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyTrainingRecordAttService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_training_record_att-通过id查询")
	@ApiOperation(value="emergency_training_record_att-通过id查询", notes="emergency_training_record_att-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyTrainingRecordAtt> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyTrainingRecordAtt emergencyTrainingRecordAtt = emergencyTrainingRecordAttService.getById(id);
		if(emergencyTrainingRecordAtt==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyTrainingRecordAtt);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyTrainingRecordAtt
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyTrainingRecordAtt emergencyTrainingRecordAtt) {
        return super.exportXls(request, emergencyTrainingRecordAtt, EmergencyTrainingRecordAtt.class, "emergency_training_record_att");
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
        return super.importExcel(request, response, EmergencyTrainingRecordAtt.class);
    }

}
