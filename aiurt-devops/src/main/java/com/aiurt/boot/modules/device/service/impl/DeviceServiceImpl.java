package com.aiurt.boot.modules.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.device.entity.DeviceAssembly;
import com.aiurt.boot.modules.device.entity.DeviceSmallType;
import com.aiurt.boot.modules.device.entity.DeviceType;
import com.aiurt.boot.modules.device.mapper.DeviceAssemblyMapper;
import com.aiurt.boot.modules.device.mapper.DeviceMapper;
import com.aiurt.boot.modules.device.mapper.DeviceTypeMapper;
import com.aiurt.boot.modules.device.service.IDeviceService;
import com.aiurt.boot.modules.device.service.IDeviceSmallTypeService;
import com.aiurt.boot.modules.manage.entity.Line;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.ILineService;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.MaterialBase;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IMaterialBaseService;
import com.aiurt.boot.modules.statistical.vo.DeviceDataVo;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {

    @Autowired
    private ISubsystemService subsystemService;

    @Autowired
    private ILineService lineService;

	@Autowired
	private IStationService stationService;

    @Resource
    private DeviceTypeMapper deviceTypeMapper;

	@Resource
	private DeviceAssemblyMapper deviceAssemblyMapper;

	@Resource
	private IDeviceSmallTypeService deviceSmallTypeService;

	@Resource
	private IMaterialBaseService materialBaseService;

    @Override
    public void addNeedInformation(List<Device> list) {
	    Map<String, List<Device>> deviceMap = list.stream().collect(Collectors.groupingBy(Device::getTypeCode));

	    Set<String> codeSet = null;
	    Set<String> lineSet = null;
	    Set<String> stationSet = null;
	    Set<String> systemCodeSet = null;

	    for (Device device : list) {
		    if (device.getTypeCode()!=null){
				if (codeSet==null){
					codeSet=new HashSet<>();
				}
				codeSet.add(device.getTypeCode());
		    }
		    if (device.getStationCode()!=null){
			    if (stationSet==null){
				    stationSet=new HashSet<>();
			    }
			    stationSet.add(device.getStationCode());
		    }

		    if (device.getLineCode()!=null){
			    if (lineSet==null){
				    lineSet=new HashSet<>();
			    }
			    lineSet.add(device.getLineCode());
		    }
		    if (device.getSystemCode()!=null){
			    if (systemCodeSet==null){
				    systemCodeSet=new HashSet<>();
			    }
			    systemCodeSet.add(device.getSystemCode());
		    }
	    }

		if (CollectionUtils.isNotEmpty(deviceMap.keySet())) {
			List<DeviceType> types = deviceTypeMapper.selectList(new LambdaQueryWrapper<DeviceType>().in(DeviceType::getCode, deviceMap.keySet()).eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0));
			if (CollectionUtils.isNotEmpty(types)) {
				Map<String, String> codeNameMap = types.stream().collect(Collectors.toMap(DeviceType::getCode, DeviceType::getName));
				List<Station> stationList = stationService.lambdaQuery().in(Station::getStationCode, stationSet).eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0).list();
				List<Subsystem> subsystemList = subsystemService.lambdaQuery().in(Subsystem::getSystemCode, systemCodeSet).eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0).list();
				List<Line> lineList = lineService.lambdaQuery().in(Line::getLineCode, lineSet).eq(Line::getDelFlag, 0).list();

				Map<String, Station> stationMap = null;
				Map<String, Subsystem> systemMap = null;
				Map<String, Line> lineMap = null;


				if (CollectionUtils.isNotEmpty(stationList)) {
					stationMap = stationList.stream().collect(Collectors.toMap(Station::getStationCode, s -> s));
				}
				if (CollectionUtils.isNotEmpty(subsystemList)) {
					systemMap = subsystemList.stream().collect(Collectors.toMap(Subsystem::getSystemCode, s -> s));
				}
				if (CollectionUtils.isNotEmpty(lineList)) {
					lineMap = lineList.stream().collect(Collectors.toMap(Line::getLineCode, l -> l));
				}


				Map<String, Line> finalLineMap = lineMap;
				Map<String, Station> finalStationMap = stationMap;
				Map<String, Subsystem> finalSystemMap = systemMap;
				list.forEach(l -> {
					l.setTypeName(codeNameMap.get(l.getTypeCode()));
					if (finalLineMap != null) {
						Line line = finalLineMap.get(l.getLineCode());
						if (line != null) {
							l.setLineName(line.getLineName());
						}
					}
					if (finalStationMap != null) {
						Station station = finalStationMap.get(l.getStationCode());
						if (station != null) {
							l.setStationName(station.getStationName());
						}
					}
					if (finalSystemMap != null) {
						Subsystem one = finalSystemMap.get(l.getSystemCode());
						if (one != null) {
							l.setSystemName(one.getSystemName());
						}
					}
					final DeviceSmallType deviceSmallType = deviceSmallTypeService.getOne(new LambdaQueryWrapper<DeviceSmallType>()
							.eq(DeviceSmallType::getCode, l.getSmallTypeCode()).last("limit 1"));
					if (deviceSmallType != null) {
						l.setSmallTypeName(deviceSmallType.getName());
					}
					//设备组件
					final List<DeviceAssembly> deviceAssemblies = getDeviceAssemblyDetail(l);
					l.setDeviceAssemblyList(deviceAssemblies);
				});
			}
		}
    }

	@Override
	public Result<Device> queryDetailById(String deviceId) {
		final Device device = this.baseMapper.selectById(deviceId);
		device.setTypeName(deviceTypeMapper.getTypeByCode(device.getTypeCode()));
		Line lineOne = lineService.getOne(new LambdaQueryWrapper<Line>().eq(Line::getLineCode, device.getLineCode()).eq(Line::getDelFlag,0));
		if (lineOne!=null) {
			device.setLineName(lineOne.getLineName());
		}
		Station station=stationService.getOne(new LambdaQueryWrapper<Station>().eq(Station::getStationCode, device.getStationCode()).eq(Station::getDelFlag,0));
		if (station!=null) {
			device.setStationName(station.getStationName());
		}
		Subsystem one = subsystemService.getOne(new LambdaQueryWrapper<Subsystem>().eq(Subsystem::getSystemCode, device.getSystemCode()).eq(Subsystem::getDelFlag,0));
		if (one!=null) {
			device.setSystemName(one.getSystemName());
		}
		//设备组件
		final List<DeviceAssembly> list = getDeviceAssemblyDetail(device);
		device.setDeviceAssemblyList(list);
		return Result.ok(device);
	}

	@Override
	public Integer getDeviceNum(Map map) {
		return this.baseMapper.getDeviceNum(map);
	}
	@Override
	public List<DeviceDataVo> getSystemDeviceData(Map map) {
		return this.baseMapper.getSystemDeviceData(map);
	}
	@Override
	public List<DeviceDataVo> getDeviceNumByStation(Map map) {
		return this.baseMapper.getDeviceNumByStation(map);
	}


	private List<DeviceAssembly> getDeviceAssemblyDetail(Device device){
		final LambdaQueryWrapper<DeviceAssembly> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(DeviceAssembly::getDeviceCode,device.getCode());
		final List<DeviceAssembly> list = deviceAssemblyMapper.selectList(wrapper);
		if (list == null){
			return null;
		}
		if (CollectionUtils.isNotEmpty(list)) {
			Map<String, List<DeviceAssembly>> listMap = list.stream().collect(Collectors.groupingBy(DeviceAssembly::getCode));
			List<MaterialBase> baseList = materialBaseService.lambdaQuery().in(MaterialBase::getCode, listMap.keySet()).eq(MaterialBase::getDelFlag, CommonConstant.DEL_FLAG_0).list();
			if (CollectionUtils.isNotEmpty(baseList)) {
				Map<String, MaterialBase> baseMap = baseList.stream().collect(Collectors.toMap(MaterialBase::getCode, m -> m));
				for (DeviceAssembly assembly : list) {
					MaterialBase materialBase = baseMap.get(assembly.getCode());
					if (materialBase != null) {
						assembly.setMaterialName(materialBase.getName());
						assembly.setBrand(materialBase.getBrand());
						assembly.setType(materialBase.getType());
						assembly.setSpecifications(materialBase.getSpecifications());
						assembly.setSupplier(materialBase.getManufacturer());
					}
				}
			}
		}
		return list;
	}

	@Override
	public List<Device> queryDeviceByStationCodeAndSystemCode(String stationCode, String systemCode) {
		return this.baseMapper.queryDeviceByStationCodeAndSystemCode(stationCode,systemCode);
	}
}
