package com.aiurt.modules.position.controller;


import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.service.ICsLineService;
import com.aiurt.modules.position.service.ICsStationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	 *   添加
	 *
	 * @param position
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加一级位置",permissionUrl = "/position/list")
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
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑一级位置",permissionUrl = "/position/list")
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
		csLine.setLongitude(position.getLongitude());
		csLine.setLatitude(position.getLatitude());
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
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "删除一级位置",permissionUrl = "/position/list")
	@ApiOperation(value="cs_line-通过id删除", notes="cs_line-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		CsLine csLine = csLineService.getById(id);
		//判断二级是否使用
		LambdaQueryWrapper<CsStation> wrapper =  new LambdaQueryWrapper<CsStation>();
		wrapper.eq(CsStation::getLineCode,csLine.getLineCode());
		wrapper.eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<CsStation> list = csStationService.list(wrapper);
		if(!list.isEmpty()){
			return Result.error("该位置信息正在使用中，无法删除");
		}
		//判断设备主数据是否使用
		LambdaQueryWrapper<Device> deviceWrapper =  new LambdaQueryWrapper<Device>();
		deviceWrapper.eq(Device::getPositionCode,csLine.getLineCode());
		deviceWrapper.eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<Device> deviceList = deviceService.list(deviceWrapper);
		if(!deviceList.isEmpty()){
			return Result.error("该位置信息被设备主数据使用中，无法删除");
		}
		csLine.setDelFlag(CommonConstant.DEL_FLAG_1);
		csLineService.updateById(csLine);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询一级位置",permissionUrl = "/position/list")
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
