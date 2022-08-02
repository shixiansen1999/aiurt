package com.aiurt.modules.modeler.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.modeler.entity.ActCustomButton;
import com.aiurt.modules.modeler.service.IActCustomButtonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 流程按钮
 * @Author: aiurt
 * @Date:   2022-08-01
 * @Version: V1.0
 */
@Api(tags="流程按钮")
@RestController
@RequestMapping("/modeler/actCustomButton")
@Slf4j
public class ActCustomButtonController extends BaseController<ActCustomButton, IActCustomButtonService> {


	@Autowired
	private IActCustomButtonService actCustomButtonService;

	/**
	 * 流程按钮查询
	 *
	 * @return list
	 */
	@ApiOperation(value="流程按钮-分页列表查询", notes="流程按钮-分页列表查询")
	@GetMapping(value = "/list")
	public Result<List<ActCustomButton>> queryPageList() {
		List<ActCustomButton> buttonList = actCustomButtonService.list();
		return Result.OK(buttonList);
	}

	/**
	 *   添加
	 *
	 * @param actCustomButton
	 * @return
	 */
	@AutoLog(value = "流程按钮-添加")
	@ApiOperation(value="流程按钮-添加", notes="流程按钮-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ActCustomButton actCustomButton) {
		actCustomButtonService.save(actCustomButton);
		return Result.OK("添加成功！");
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="流程按钮-通过id查询", notes="流程按钮-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ActCustomButton> queryById(@RequestParam(name="id",required=true) String id) {
		ActCustomButton actCustomButton = actCustomButtonService.getById(id);
		if(actCustomButton==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(actCustomButton);
	}
}
