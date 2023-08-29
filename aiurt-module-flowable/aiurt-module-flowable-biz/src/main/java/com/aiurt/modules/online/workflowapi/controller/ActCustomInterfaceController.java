package com.aiurt.modules.online.workflowapi.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.online.workflowapi.entity.ActCustomInterface;
import com.aiurt.modules.online.workflowapi.service.IActCustomInterfaceService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

 /**
 * @Description: act_custom_interface
 * @Author: wgp
 * @Date:   2023-07-25
 * @Version: V1.0
 */
@Api(tags="自定义接口")
@RestController
@RequestMapping("/workflowapi/actCustomInterface")
@Slf4j
public class ActCustomInterfaceController extends BaseController<ActCustomInterface, IActCustomInterfaceService> {
	@Autowired
	private IActCustomInterfaceService actCustomInterfaceService;

	/**
	 * 分页列表查询
	 *
	 * @param actCustomInterface
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "分页列表自定义接口查询")
	@ApiOperation(value="分页列表自定义接口查询", notes="分页列表自定义接口查询")
	@GetMapping(value = "/list")
	public Result<IPage<ActCustomInterface>> queryPageList(ActCustomInterface actCustomInterface,
														   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														   HttpServletRequest req) {
		QueryWrapper<ActCustomInterface> queryWrapper = QueryGenerator.initQueryWrapper(actCustomInterface, req.getParameterMap());
		if(ObjectUtil.isNotEmpty(actCustomInterface.getType())){
			queryWrapper.eq("type",actCustomInterface.getType());
		}
		if(ObjectUtil.isNotEmpty(actCustomInterface.getModule())){
			queryWrapper.eq("module",actCustomInterface.getModule());
		}
		queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		Page<ActCustomInterface> page = new Page<>(pageNo, pageSize);
		IPage<ActCustomInterface> pageList = actCustomInterfaceService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加自定义接口
	 *
	 * @param actCustomInterface
	 * @return
	 */
	@AutoLog(value = "添加自定义接口")
	@ApiOperation(value="添加自定义接口", notes="添加自定义接口")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ActCustomInterface actCustomInterface) {
		// 检查数据库中是否已存在具有相同name的记录
		if (actCustomInterfaceService.isNameExists(actCustomInterface.getName(),null)) {
			return Result.error("名称已存在，请使用其他名称！");
		}
		actCustomInterface.setMark(String.format("%s%s","interface",System.currentTimeMillis()));
		actCustomInterfaceService.save(actCustomInterface);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑自定义接口
	 *
	 * @param actCustomInterface
	 * @return
	 */
	@AutoLog(value = "编辑自定义接口")
	@ApiOperation(value="编辑自定义接口", notes="编辑自定义接口")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ActCustomInterface actCustomInterface) {
		// 检查数据库中是否已存在具有相同name的记录
		if (actCustomInterfaceService.isNameExists(actCustomInterface.getName(), actCustomInterface.getId())) {
			return Result.error("名称已存在，请使用其他名称！");
		}
		actCustomInterfaceService.updateById(actCustomInterface);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删自定义接口
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "通过id删自定义接口")
	@ApiOperation(value="通过id删自定义接口", notes="通过id删自定义接口")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		actCustomInterfaceService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除删自定义接口
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "批量删除删自定义接口")
	@ApiOperation(value="批量删除删自定义接口", notes="批量删除删自定义接口")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.actCustomInterfaceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询自定义接口
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="通过id查询自定义接口", notes="通过id查询自定义接口")
	@GetMapping(value = "/queryById")
	public Result<ActCustomInterface> queryById(@RequestParam(name="id",required=true) String id) {
		ActCustomInterface actCustomInterface = actCustomInterfaceService.getById(id);
		return Result.OK(actCustomInterface);
	}

}
