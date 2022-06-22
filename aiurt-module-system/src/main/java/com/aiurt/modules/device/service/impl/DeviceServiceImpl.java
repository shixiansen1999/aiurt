package com.aiurt.modules.device.service.impl;

import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.mapper.DeviceAssemblyMapper;
import com.aiurt.modules.device.mapper.DeviceMapper;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	private DeviceMapper deviceMapper;

	@Autowired
	private DeviceAssemblyMapper deviceAssemblyMapper;

	@Autowired
	private SysBaseApiImpl sysBaseApi;

	@Override
	public Result<Device> queryDetailById(String deviceId) {
    	Device device = deviceMapper.getById(deviceId);
		String deviceLevel = device.getDeviceLevel()==null?"":device.getDeviceLevel();
		String status = device.getStatus()==null?"":device.getStatus().toString();
		String reuseType = device.getReuseType()==null?"":device.getReuseType();
		String temporary = device.getTemporary()==null?"":device.getTemporary();
		String scrapFlag = device.getScrapFlag()==null?"":device.getScrapFlag().toString();
		String delFlag = device.getDelFlag()==null?"":device.getDelFlag().toString();
		device.setDeviceLevelName(sysBaseApi.translateDict("device_level",deviceLevel)==null?"":sysBaseApi.translateDict("device_level",deviceLevel));
		device.setStatusDesc(sysBaseApi.translateDict("device_status",status)==null?"":sysBaseApi.translateDict("device_status",status));
		device.setReuseTypeName(sysBaseApi.translateDict("device_reuse_type",reuseType)==null?"":sysBaseApi.translateDict("device_reuse_type",reuseType));
		device.setTemporaryName(sysBaseApi.translateDict("device_temporary",temporary)==null?"":sysBaseApi.translateDict("device_temporary",temporary));
		device.setScrapFlagName(sysBaseApi.translateDict("device_scrap_flag",scrapFlag)==null?"":sysBaseApi.translateDict("device_scrap_flag",scrapFlag));
		device.setDelFlagName(sysBaseApi.translateDict("device_del_flag",delFlag)==null?"":sysBaseApi.translateDict("device_del_flag",delFlag));
		//设备组件
		List<DeviceAssembly> deviceAssemblyList = deviceAssemblyMapper.selectList(new QueryWrapper<DeviceAssembly>().eq("device_code", device.getCode()));
		for(DeviceAssembly deviceAssembly : deviceAssemblyList){
			String statusAssembly = deviceAssembly.getStatus()==null?"":deviceAssembly.getStatus();
			String baseTypeCode = deviceAssembly.getBaseTypeCode()==null?"":deviceAssembly.getBaseTypeCode();
			deviceAssembly.setStatusName(sysBaseApi.translateDict("device_assembly_status",statusAssembly)==null?"":sysBaseApi.translateDict("device_assembly_status",statusAssembly));
			deviceAssembly.setBaseTypeCodeName(sysBaseApi.translateDict("device_base_type_code",baseTypeCode)==null?"":sysBaseApi.translateDict("device_base_type_code",baseTypeCode));
		}
		device.setDeviceAssemblyList(deviceAssemblyList);
		return Result.ok(device);
	}

	@Override
	public Integer getDeviceNum(Map map) {
		return this.baseMapper.getDeviceNum(map);
	}



	@Override
	public List<Device> queryDeviceByStationCodeAndSystemCode(String stationCode, String systemCode) {
		return this.baseMapper.queryDeviceByStationCodeAndSystemCode(stationCode,systemCode);
	}
}
