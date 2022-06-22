package com.aiurt.modules.fault.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.service.IFaultService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: fault
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="故障管理")
@RestController
@RequestMapping("/fault/")
@Slf4j
public class FaultController extends BaseController<Fault, IFaultService> {
	@Autowired
	private IFaultService faultService;

	/**
	 * 分页列表查询
	 *
	 * @param fault
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "故障分页列表查询")
	@ApiOperation(value="分页列表查询", notes="fault-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Fault>> queryPageList(Fault fault,
											  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											  HttpServletRequest req) {
		QueryWrapper<Fault> queryWrapper = QueryGenerator.initQueryWrapper(fault, req.getParameterMap());
		Page<Fault> page = new Page<>(pageNo, pageSize);
		IPage<Fault> pageList = faultService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param fault
	 * @return
	 */
	@AutoLog(value = "故障上报")
	@ApiOperation(value="故障上报", notes="故障上报")
	@PostMapping(value = "/add")
	public Result<?> add(@Validated @RequestBody Fault fault) {
		faultService.add(fault);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param fault
	 * @return
	 */
	@AutoLog(value = "编辑故障单")
	@ApiOperation(value="fault-编辑", notes="fault-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Fault fault) {
		faultService.updateById(fault);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "fault-通过id删除")
	@ApiOperation(value="fault-通过id删除", notes="fault-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		faultService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查看故障详情")
	@ApiOperation(value="fault-通过id查询", notes="fault-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Fault> queryById(@RequestParam(name="id",required=true) String id) {
		Fault fault = faultService.getById(id);
		if(fault==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(fault);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param fault
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Fault fault) {
        return super.exportXls(request, fault, Fault.class, "fault");
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
        return super.importExcel(request, response, Fault.class);
    }

}
