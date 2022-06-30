package com.aiurt.modules.faulttype.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultlevel.entity.FaultLevel;
import com.aiurt.modules.faulttype.entity.FaultType;
import com.aiurt.modules.faulttype.service.IFaultTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

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
	@ApiOperation(value="fault_type-分页列表查询", notes="fault_type-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultType>> queryPageList(FaultType faultType,
												  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												  HttpServletRequest req) {
		LambdaQueryWrapper<FaultType> queryWrapper = new LambdaQueryWrapper<>();
		if(null != faultType.getName() && !"".equals(faultType.getName())){
			queryWrapper.like(FaultType::getName, faultType.getName());
		}
		if(null != faultType.getCode() && !"".equals(faultType.getCode())){
			queryWrapper.like(FaultType::getCode, faultType.getCode());
		}
		if(null != faultType.getMajorCode() && !"".equals(faultType.getMajorCode())){
			queryWrapper.eq(FaultType::getMajorCode, faultType.getMajorCode());
		}
		Page<FaultType> page = new Page<FaultType>(pageNo, pageSize);
		IPage<FaultType> pageList = faultTypeService.page(page, queryWrapper.eq(FaultType::getDelFlag,0).orderByDesc(FaultType::getCreateTime));
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
		//判断故障上报是否使用,fault表没有del_flag
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
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
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
	  * 根据专业编码查询故障类型
	  * @param majorCode
	  * @return
	  */
	 @GetMapping("/queryFaultTypeByMajorCode")
	 @ApiOperation("根据专业编码查询故障类型")
	 @ApiImplicitParams({
			 @ApiImplicitParam(name = "majorCode", value = "专业编码", required = true, paramType = "query"),
	 })
	public Result<List<SelectTable>> queryFaultTypeByMajorCode(@RequestParam(value = "majorCode") String majorCode) {
		LambdaQueryWrapper<FaultType> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(FaultType::getMajorCode, majorCode);
		wrapper.eq(FaultType::getDelFlag, 0);
		 List<FaultType> typeList = faultTypeService.getBaseMapper().selectList(wrapper);
		 List<SelectTable> tableList = typeList.stream().map(faultType -> {
			 SelectTable table = new SelectTable();
			 table.setLabel(faultType.getName());
			 table.setValue(faultType.getCode());
			 return table;
		 }).collect(Collectors.toList());
		 return Result.OK(tableList);
	}
}
