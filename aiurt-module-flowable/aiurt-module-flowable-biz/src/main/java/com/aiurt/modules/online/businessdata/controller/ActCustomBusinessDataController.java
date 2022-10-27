package com.aiurt.modules.online.businessdata.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.online.businessdata.entity.ActCustomBusinessData;
import com.aiurt.modules.online.businessdata.service.IActCustomBusinessDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 流程中间业务数据
 * @Author: aiurt
 * @Date:   2022-10-27
 * @Version: V1.0
 */
@Api(tags="流程中间业务数据")
@RestController
@RequestMapping("/business/data/actCustomBusinessData")
@Slf4j
public class ActCustomBusinessDataController extends BaseController<ActCustomBusinessData, IActCustomBusinessDataService> {
	@Autowired
	private IActCustomBusinessDataService actCustomBusinessDataService;

	/**
	 *   添加
	 *
	 * @param actCustomBusinessData
	 * @return
	 */
	@AutoLog(value = "流程中间业务数据-添加")
	@ApiOperation(value="流程中间业务数据-添加", notes="流程中间业务数据-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ActCustomBusinessData actCustomBusinessData) {
		actCustomBusinessDataService.save(actCustomBusinessData);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param actCustomBusinessData
	 * @return
	 */
	@AutoLog(value = "流程中间业务数据-编辑")
	@ApiOperation(value="流程中间业务数据-编辑", notes="流程中间业务数据-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ActCustomBusinessData actCustomBusinessData) {
		actCustomBusinessDataService.updateById(actCustomBusinessData);
		return Result.OK("编辑成功!");
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="流程中间业务数据-通过id查询", notes="流程中间业务数据-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ActCustomBusinessData> queryById(@RequestParam(name="id",required=true) String id) {
		ActCustomBusinessData actCustomBusinessData = actCustomBusinessDataService.getById(id);
		if(actCustomBusinessData==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(actCustomBusinessData);
	}


}
