package com.aiurt.modules.position.controller;


import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.service.ICsStationPositionService;
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
 * @Description: cs_station
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="系统管理-基础数据-位置管理-二级")
@RestController
@RequestMapping("/position/csStation")
@Slf4j
public class CsStationController extends BaseController<CsStation, ICsStationService> {
	@Autowired
	private ICsStationService csStationService;
	@Autowired
	private ICsStationPositionService csStationPositionService;
	 @Autowired
	 private IDeviceService deviceService;

	/**
	 *   添加
	 *
	 * @param position
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加二级位置",permissionUrl = "/position/list")
	@ApiOperation(value="cs_station-添加", notes="cs_station-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CsStationPosition position) {
        CsStation csStation = entityChange(position);
        return csStationService.add(csStation);
	}

	/**
	 *  编辑
	 *
	 * @param position
	 * @return
	 */
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑二级位置",permissionUrl = "/position/list")
	@ApiOperation(value="cs_station-编辑", notes="cs_station-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody CsStationPosition position) {
        CsStation csStation = entityChange(position);
        return csStationService.update(csStation);
	}

     /**
      * position转换成csLine
      * @param position
      * @return
      */
     public CsStation entityChange(CsStationPosition position){
         CsStation csStation = new CsStation();
		 csStation.setId(position.getId());
         csStation.setStationType(position.getPositionType());
         csStation.setStationCode(position.getPositionCode());
         csStation.setStationName(position.getPositionName());
         csStation.setLineCode(position.getLineCode());
         csStation.setLineName(position.getLineName());
         csStation.setSort(position.getSort());
         csStation.setLevel(position.getLevel());
		 csStation.setPhoneNum(position.getPhoneNum());
         return csStation;
     }

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "通过id删除二级位置",permissionUrl = "/position/list")
	@ApiOperation(value="cs_station-通过id删除", notes="cs_station-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		CsStation csStation = csStationService.getById(id);
		//判断三级是否使用
		LambdaQueryWrapper<CsStationPosition> wrapper =  new LambdaQueryWrapper<CsStationPosition>();
		wrapper.eq(CsStationPosition::getStaionCode,csStation.getStationCode());
		wrapper.eq(CsStationPosition::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<CsStationPosition> list = csStationPositionService.list(wrapper);
		if(!list.isEmpty()){
			return Result.error("该位置信息正在使用中，无法删除");
		}
		//判断设备主数据是否使用
		LambdaQueryWrapper<Device> deviceWrapper =  new LambdaQueryWrapper<Device>();
		deviceWrapper.eq(Device::getPositionCode,csStation.getStationCode());
		deviceWrapper.eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<Device> deviceList = deviceService.list(deviceWrapper);
		if(!deviceList.isEmpty()){
			return Result.error("该位置信息被设备主数据使用中，无法删除");
		}
		csStation.setDelFlag(CommonConstant.DEL_FLAG_1);
		csStationService.updateById(csStation);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询二级位置",permissionUrl = "/position/list")
	@ApiOperation(value="cs_station-通过id查询", notes="cs_station-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CsStation> queryById(@RequestParam(name="id",required=true) String id) {
		CsStation csStation = csStationService.getById(id);
		if(csStation==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csStation);
	}

	 /**
	  * 站所列表查询
	  * @param
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "站所列表查询",permissionUrl = "/position/list")
	 @ApiOperation(value="站所列表查询", notes="站所列表查询")
	 @GetMapping(value = "/selectList")
	 public Result<?> selectList() {
		 LambdaQueryWrapper<CsStation> queryWrapper = new LambdaQueryWrapper<>();
		 List<CsStation> list = csStationService.list(queryWrapper.eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0));
		 return Result.OK(list);
	 }
}
