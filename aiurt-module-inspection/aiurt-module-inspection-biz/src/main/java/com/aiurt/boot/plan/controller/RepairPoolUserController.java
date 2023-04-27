package com.aiurt.boot.plan.controller;

import com.aiurt.boot.plan.entity.RepairPoolUser;
import com.aiurt.boot.plan.service.IRepairPoolUserService;
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
import java.util.List;

/**
 * @Description: repair_pool_user
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="repair_pool_user")
@RestController
@RequestMapping("/plan/repairPoolUser")
@Slf4j
public class RepairPoolUserController extends BaseController<RepairPoolUser, IRepairPoolUserService> {
	@Autowired
	private IRepairPoolUserService repairPoolUserService;

	@GetMapping(value = "/findAll")
	public Result<List<RepairPoolUser>> findAll() {
        List<RepairPoolUser> repairPoolUser = repairPoolUserService.findAll();
		if(repairPoolUser==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(repairPoolUser);
	}


}
