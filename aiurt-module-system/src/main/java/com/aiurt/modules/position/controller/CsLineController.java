package com.aiurt.modules.position.controller;


import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.service.ICsLineService;
import com.aiurt.modules.position.service.ICsStationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: cs_line
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="系统管理-基础数据-位置管理-一级")
@RestController
@RequestMapping("/position/csLine")
@Slf4j
public class CsLineController extends BaseController<CsLine, ICsLineService> {
	@Autowired
	private ICsLineService csLineService;
	@Autowired
	private ICsStationService csStationService;
	@Autowired
	private IDeviceService deviceService;
	/**
	 * 分页列表查询
	 *
	 * @param csLine
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="cs_line-分页列表查询", notes="cs_line-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CsLine>> queryPageList(CsLine csLine,
											   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											   HttpServletRequest req) {
		QueryWrapper<CsLine> queryWrapper = QueryGenerator.initQueryWrapper(csLine, req.getParameterMap());
		Page<CsLine> page = new Page<CsLine>(pageNo, pageSize);
		IPage<CsLine> pageList = csLineService.page(page, queryWrapper.lambda().eq(CsLine::getDelFlag,0));
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param position
	 * @return
	 */
	@AutoLog(value = "cs_line-添加")
	@ApiOperation(value="cs_line-添加", notes="cs_line-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CsStationPosition position) {
		CsLine csLine = entityChange(position);
		return csLineService.add(csLine);
	}

	/**
	 *  编辑
	 *
	 * @param position
	 * @return
	 */
	@AutoLog(value = "cs_line-编辑")
	@ApiOperation(value="cs_line-编辑", notes="cs_line-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody CsStationPosition position) {
		CsLine csLine = entityChange(position);
		return csLineService.update(csLine);
	}

	 /**
	  * position转换成csLine
	  * @param position
	  * @return
	  */
    public CsLine entityChange(CsStationPosition position){
		CsLine csLine = new CsLine();
		csLine.setId(position.getId());
		csLine.setLineType(position.getPositionType());
		csLine.setLineCode(position.getPositionCode());
		csLine.setLineName(position.getPositionName());
		csLine.setSort(position.getSort());
		csLine.setLevel(position.getLevel());
		return csLine;
	}
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "cs_line-通过id删除")
	@ApiOperation(value="cs_line-通过id删除", notes="cs_line-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		CsLine csLine = csLineService.getById(id);
		//判断二级是否使用
		LambdaQueryWrapper<CsStation> wrapper =  new LambdaQueryWrapper<CsStation>();
		wrapper.eq(CsStation::getLineCode,csLine.getLineCode());
		wrapper.eq(CsStation::getDelFlag,0);
		List<CsStation> list = csStationService.list(wrapper);
		if(!list.isEmpty()){
			return Result.error("该位置信息正在使用中，无法删除");
		}
		//判断设备主数据是否使用
		LambdaQueryWrapper<Device> deviceWrapper =  new LambdaQueryWrapper<Device>();
		deviceWrapper.eq(Device::getPositionCode,csLine.getLineCode());
		deviceWrapper.eq(Device::getDelFlag,0);
		List<Device> deviceList = deviceService.list(deviceWrapper);
		if(!deviceList.isEmpty()){
			return Result.error("该位置信息被设备主数据使用中，无法删除");
		}
		csLine.setDelFlag(1);
		csLineService.updateById(csLine);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="cs_line-通过id查询", notes="cs_line-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CsLine> queryById(@RequestParam(name="id",required=true) String id) {
		CsLine csLine = csLineService.getById(id);
		if(csLine==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csLine);
	}

}
