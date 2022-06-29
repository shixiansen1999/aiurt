package com.aiurt.modules.position.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.mapper.CsLineMapper;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.aiurt.modules.position.service.ICsLineService;
import com.aiurt.modules.position.service.ICsStationPositionService;
import com.aiurt.modules.position.service.ICsStationService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: cs_station_position
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="系统管理-基础数据-位置管理-三级")
@RestController
@RequestMapping("/position")
@Slf4j
public class CsStationPositionController  {
	@Autowired
	private ICsStationPositionService csStationPositionService;
	@Autowired
	private ICsStationService csStationService;
	@Autowired
	private ICsLineService csLineService;
	@Autowired
	private CsLineMapper csLineMapper;
	@Autowired
	private CsStationMapper csStationMapper;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	@Autowired
	private IDeviceService deviceService;
	 /**
	  * 位置管理树
	  *
	  * @return
	  */
	 @AutoLog(value = "位置管理树")
	 @ApiOperation(value="位置管理树", notes="位置管理树")
	 @GetMapping(value = "/treeList")
	 public Result<?> queryTreeList() {
	 	 //查询所有一级
		 List<CsLine> lineList = csLineService.list(new LambdaQueryWrapper<CsLine>().eq(CsLine::getDelFlag,0).orderByAsc(CsLine::getSort));
		 //查询所有二级
		 List<CsStation> stationList = csStationService.list(new LambdaQueryWrapper<CsStation>().eq(CsStation::getDelFlag,0).orderByAsc(CsStation::getSort));
		 //查询所有三级
		 List<CsStationPosition> positionList = csStationPositionService.list(new LambdaQueryWrapper<CsStationPosition>().eq(CsStationPosition::getDelFlag,0).orderByAsc(CsStationPosition::getSort));
		 List<CsStationPosition> newList = new ArrayList<>();
		 //循环一级
		 lineList.forEach(line -> {
			 CsStationPosition onePosition = setEntity(line.getId(),1,line.getSort(),line.getLineCode(),line.getLineName());
			 List<CsStation> twoStationList = stationList.stream().filter(station-> station.getLineCode().equals(line.getLineCode())).collect(Collectors.toList());
			 List<CsStationPosition> twoList = new ArrayList<>();
			 //循环二级
			 twoStationList.forEach(two->{
				 CsStationPosition twoPosition = setEntity(two.getId(),2,two.getSort(),two.getStationCode(),two.getStationName());
				 List<CsStationPosition> threeStationList = positionList.stream().filter(position-> position.getStaionCode().equals(two.getStationCode())).collect(Collectors.toList());
				 List<CsStationPosition> threeList = new ArrayList<>();
				 //循环三级
				 threeStationList.forEach(three->{
					 CsStationPosition threePosition = setEntity(three.getId(),3,three.getSort(),three.getPositionCode(),three.getPositionName());
					 threeList.add(threePosition);
				 });
				 twoPosition.setChildren(threeList);
				 twoList.add(twoPosition);
			 });
			 //二级放入一级的子节点里
			 onePosition.setChildren(twoList);
			 newList.add(onePosition);
		 });

		 return Result.OK(newList);
	 }

	 /**
	  * 位置管理树-转换实体
	  * @param id
	  * @param level
	  * @param sort
	  * @param positionCode
	  * @param positionName
	  * @return
	  */
	 public CsStationPosition setEntity(String id,Integer level,Integer sort,String positionCode,String positionName){
		 CsStationPosition position = new CsStationPosition();
		 position.setId(id);
		 position.setLevel(level);
		 position.setSort(sort);
		 position.setPositionCode(positionCode);
		 position.setPositionName(positionName);
         return position;
	 }
	/**
	 * 分页列表查询
	 *
	 * @param csStationPosition
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "cs_station_position-分页列表查询")
	@ApiOperation(value="位置管理分页列表查询", notes="位置管理分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CsStationPosition csStationPosition,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<CsStationPosition> page = new Page<CsStationPosition>(pageNo, pageSize);
		List<CsStationPosition> list = csStationPositionService.readAll(page,csStationPosition);
		list.forEach(position -> {
			position.setPositionType_dictText(sysBaseAPI.translateDict("station_level",position.getPositionType()+""));
		});
		/*//查询所有一级
		List<CsLine> lineList = csLineService.list(new LambdaQueryWrapper<CsLine>().eq(CsLine::getDelFlag,0).orderByAsc(CsLine::getSort));
		//查询所有二级
		List<CsStation> stationList = csStationService.list(new LambdaQueryWrapper<CsStation>().eq(CsStation::getDelFlag,0).orderByAsc(CsStation::getSort));
		//查询所有三级
		List<CsStationPosition> positionList = csStationPositionService.list(new LambdaQueryWrapper<CsStationPosition>().eq(CsStationPosition::getDelFlag,0).orderByAsc(CsStationPosition::getSort));
		List<CsStationPosition> newList = new ArrayList<>();
		//循环一级
		lineList.forEach(one -> {
			CsStationPosition onePosition = setList(one.getId(),1,one.getSort(),one.getLineCode(),one.getLineName(),one.getLineType(),"-");
			newList.add(onePosition);
		});
        //循环二级
		stationList.forEach(two -> {
			CsLine csLine = csLineMapper.selectOne(new LambdaQueryWrapper<CsLine>().eq(CsLine::getLineCode,two.getLineCode()));
			CsStationPosition twoPosition = setList(two.getId(),2,two.getSort(),two.getStationCode(),two.getStationName(),two.getStationType(),csLine.getLineName());
			newList.add(twoPosition);
		});
		//循环三级
		positionList.forEach(three -> {
			CsStation csStation = csStationMapper.selectOne(new LambdaQueryWrapper<CsStation>().eq(CsStation::getStationCode,three.getStaionCode()));
			CsStationPosition threePosition = setList(three.getId(),3,three.getSort(),three.getPositionCode(),three.getPositionName(),three.getPositionType(),csStation.getStationName());
			newList.add(threePosition);
		});
		page.setRecords(newList);
		IPage<CsStationPosition> pageList = csStationPositionService.page(page, newList);*/
		return Result.OK(list);
	}
	 /**
	  * 列表-转换实体
	  * @param id
	  * @param level
	  * @param sort
	  * @param positionCode
	  * @param positionName
	  * @return
	  */
	 public CsStationPosition setList(String id,Integer level,Integer sort,String positionCode,String positionName,Integer lineType,String pUrl){
		 CsStationPosition position = new CsStationPosition();
		 position.setId(id);
		 position.setLevel(level);
		 position.setSort(sort);
		 position.setPositionCode(positionCode);
		 position.setPositionName(positionName);
		 position.setPositionType(lineType);
		 position.setPUrl(pUrl);
		 return position;
	 }
	/**
	 * 添加
	 *
	 * @param csStationPosition
	 * @return
	 */
	@AutoLog(value = "位置管理添加")
	@ApiOperation(value="位置管理添加", notes="位置管理添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CsStationPosition csStationPosition) {
		return csStationPositionService.add(csStationPosition);
	}

	/**
	 * 编辑
	 *
	 * @param csStationPosition
	 * @return
	 */
	@AutoLog(value = "位置管理编辑")
	@ApiOperation(value="位置管理编辑", notes="位置管理编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CsStationPosition csStationPosition) {
		return csStationPositionService.update(csStationPosition);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "位置管理通过id删除")
	@ApiOperation(value="位置管理通过id删除", notes="位置管理通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		CsStationPosition csStationPosition = csStationPositionService.getById(id);
		//判断设备主数据是否使用
		LambdaQueryWrapper<Device> deviceWrapper =  new LambdaQueryWrapper<Device>();
		deviceWrapper.eq(Device::getPositionCode,csStationPosition.getPositionCode());
		deviceWrapper.eq(Device::getDelFlag,0);
		List<Device> deviceList = deviceService.list(deviceWrapper);
		if(!deviceList.isEmpty()){
			return Result.error("该位置信息被设备主数据使用中，无法删除");
		}
		csStationPosition.setDelFlag(1);
		csStationPositionService.updateById(csStationPosition);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	/*@AutoLog(value = "cs_station_position-批量删除")
	@ApiOperation(value="cs_station_position-批量删除", notes="cs_station_position-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.csStationPositionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}*/

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "cs_station_position-通过id查询")
	@ApiOperation(value="位置管理通过id查询", notes="位置管理通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CsStationPosition csStationPosition = csStationPositionService.getById(id);
		if(csStationPosition==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csStationPosition);
	}


}
