package com.aiurt.modules.faultlevel.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultlevel.entity.FaultLevel;
import com.aiurt.modules.faultlevel.service.IFaultLevelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

 /**
 * @Description: 故障等级
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Api(tags="故障管理-故障基础数据-故障等级")
@RestController
@RequestMapping("/faultlevel/faultLevel")
@Slf4j
public class FaultLevelController extends BaseController<FaultLevel, IFaultLevelService> {
	@Autowired
	private IFaultLevelService faultLevelService;
	 @Autowired
	 private IFaultService faultService;

	/**
	 * 分页列表查询
	 *
	 * @param faultLevel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "故障等级-分页列表查询")
	@ApiOperation(value="故障等级-分页列表查询", notes="故障等级-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultLevel>> queryPageList(FaultLevel faultLevel,
												   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												   HttpServletRequest req) {
		QueryWrapper<FaultLevel> queryWrapper = QueryGenerator.initQueryWrapper(faultLevel, req.getParameterMap());
		Page<FaultLevel> page = new Page<FaultLevel>(pageNo, pageSize);
		IPage<FaultLevel> pageList = faultLevelService.page(page, queryWrapper.lambda().eq(FaultLevel::getDelFlag,0));
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param faultLevel
	 * @return
	 */
	@AutoLog(value = "故障等级-添加")
	@ApiOperation(value="故障等级-添加", notes="故障等级-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody FaultLevel faultLevel) {
		return faultLevelService.add(faultLevel);
	}

	/**
	 *  编辑
	 *
	 * @param faultLevel
	 * @return
	 */
	@AutoLog(value = "故障等级-编辑")
	@ApiOperation(value="故障等级-编辑", notes="故障等级-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody FaultLevel faultLevel) {
		return faultLevelService.update(faultLevel);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障等级-通过id删除")
	@ApiOperation(value="故障等级-通过id删除", notes="故障等级-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		FaultLevel faultLevel = faultLevelService.getById(id);
		//判断故障上报是否使用 todo
		/*LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(Fault::getFaultTypeCode, faultLevel.getCode());
		List<Fault> list = faultService.list(queryWrapper);
		if (!list.isEmpty()) {
			return Result.error("故障分类编码已被故障报修单使用，不能删除！");
		}*/
		faultLevel.setDelFlag(1);
		faultLevelService.updateById(faultLevel);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	/*@AutoLog(value = "故障等级-批量删除")
	@ApiOperation(value="故障等级-批量删除", notes="故障等级-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.faultLevelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}*/

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "故障等级-通过id查询")
	@ApiOperation(value="故障等级-通过id查询", notes="故障等级-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultLevel> queryById(@RequestParam(name="id",required=true) String id) {
		FaultLevel faultLevel = faultLevelService.getById(id);
		if(faultLevel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultLevel);
	}



	 /**
	  * 根据专业编码查询故障等级
	  * @param majorCode
	  * @return
	  */
	 @GetMapping("/queryFaultLevelByMajorCode")
	 @ApiOperation("根据专业编码查询故障等级")
	 @ApiImplicitParams({
			 @ApiImplicitParam(name = "majorCode", value = "专业编码", required = true, paramType = "query"),
	 })
	 public Result<List<SelectTable>> queryFaultLevelByMajorCode(@RequestParam(value = "majorCode") String majorCode) {
		 LambdaQueryWrapper<FaultLevel> wrapper = new LambdaQueryWrapper<>();
		 wrapper.eq(FaultLevel::getMajorCode, majorCode);
		 List<FaultLevel> typeList = faultLevelService.getBaseMapper().selectList(wrapper);
		 List<SelectTable> tableList = typeList.stream().map(faultLevel -> {
			 SelectTable table = new SelectTable();
			 table.setLabel(faultLevel.getName());
			 table.setValue(faultLevel.getCode());
			 return table;
		 }).collect(Collectors.toList());
		 return Result.OK(tableList);
	 }
}
