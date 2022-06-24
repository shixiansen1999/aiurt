package com.aiurt.modules.faulttype.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faulttype.entity.FaultType;
import com.aiurt.modules.faulttype.service.IFaultTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.util.List;

 /**
 * @Description: fault_type
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Api(tags="故障管理-故障基础数据-故障分类")
@RestController
@RequestMapping("/faulttype/faultType")
@Slf4j
public class FaultTypeController extends BaseController<FaultType, IFaultTypeService> {
	@Autowired
	private IFaultTypeService faultTypeService;
	@Autowired
	private IFaultService faultService;

	/**
	 * 分页列表查询
	 *
	 * @param faultType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "fault_type-分页列表查询")
	@ApiOperation(value="fault_type-分页列表查询", notes="fault_type-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultType>> queryPageList(FaultType faultType,
												  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												  HttpServletRequest req) {
		QueryWrapper<FaultType> queryWrapper = QueryGenerator.initQueryWrapper(faultType, req.getParameterMap());
		Page<FaultType> page = new Page<FaultType>(pageNo, pageSize);
		IPage<FaultType> pageList = faultTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param faultType
	 * @return
	 */
	@AutoLog(value = "fault_type-添加")
	@ApiOperation(value="fault_type-添加", notes="fault_type-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody FaultType faultType) {
		return faultTypeService.add(faultType);
	}

	/**
	 *  编辑
	 *
	 * @param faultType
	 * @return
	 */
	@AutoLog(value = "fault_type-编辑")
	@ApiOperation(value="fault_type-编辑", notes="fault_type-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody FaultType faultType) {
		return faultTypeService.update(faultType);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "fault_type-通过id删除")
	@ApiOperation(value="fault_type-通过id删除", notes="fault_type-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		FaultType faultType = faultTypeService.getById(id);
		//判断故障上报是否使用
		LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(Fault::getFaultTypeCode, faultType.getCode());
		List<Fault> list = faultService.list(queryWrapper);
		if (!list.isEmpty()) {
			return Result.error("故障分类编码已被故障报修单使用，不能删除！");
		}
		faultType.setDelFlag(1);
		faultTypeService.updateById(faultType);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
/*	@AutoLog(value = "fault_type-批量删除")
	@ApiOperation(value="fault_type-批量删除", notes="fault_type-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.faultTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}*/

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "fault_type-通过id查询")
	@ApiOperation(value="fault_type-通过id查询", notes="fault_type-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultType> queryById(@RequestParam(name="id",required=true) String id) {
		FaultType faultType = faultTypeService.getById(id);
		if(faultType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultType);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultType
    */
  /*  @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FaultType faultType) {
        return super.exportXls(request, faultType, FaultType.class, "fault_type");
    }*/

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    /*@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, FaultType.class);
    }*/

	 /**
	  * 写列表
	  * @param majorCode
	  * @return
	  */
	public Result<List<SelectTable>> selectFaultTypeByMajorCode(String majorCode) {
		LambdaQueryWrapper<FaultType> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(FaultType::getMajorCode, majorCode);
		return null;
	}
}
