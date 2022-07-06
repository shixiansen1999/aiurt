package com.aiurt.modules.device.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.mapper.DeviceAssemblyMapper;
import com.aiurt.modules.device.mapper.DeviceMapper;
import com.aiurt.modules.device.mapper.DeviceTypeMapper;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

	@Autowired
	private DeviceTypeMapper deviceTypeMapper;

	@Override
	public Result<Device> queryDetailById(String deviceId) {
    	Device device = deviceMapper.selectById(deviceId);
    	Device devicefinal = translate(device);
		//设备组件
		List<DeviceAssembly> deviceAssemblyList = deviceAssemblyMapper.selectList(new QueryWrapper<DeviceAssembly>().eq("device_code", device.getCode()));
		for(DeviceAssembly deviceAssembly : deviceAssemblyList){
			String statusAssembly = deviceAssembly.getStatus()==null?"":deviceAssembly.getStatus();
			String baseTypeCode = deviceAssembly.getBaseTypeCode()==null?"":deviceAssembly.getBaseTypeCode();
			deviceAssembly.setStatusName(sysBaseApi.translateDict("device_assembly_status",statusAssembly)==null?"":sysBaseApi.translateDict("device_assembly_status",statusAssembly));
			deviceAssembly.setBaseTypeCodeName(sysBaseApi.translateDictFromTable("material_base_type", "base_type_name", "base_type_code", baseTypeCode));
		}
		devicefinal.setDeviceAssemblyList(deviceAssemblyList);
		return Result.ok(devicefinal);
	}

	/**
	 * 通用翻译
	 * @param device 未翻译的实体
	 * @return
	 */
	@Override
	public Device translate(Device device) {
		//设备复用类型
		String reuseType = device.getReuseType()==null?"":device.getReuseType();
		String reuseTypeName = "";
		if(!"".equals(reuseType) && reuseType.contains(",")){
			String[] split = reuseType.split(",");
			for(String s : split){
				reuseTypeName += sysBaseApi.translateDict("device_reuse_type",s)==null?"":sysBaseApi.translateDict("device_reuse_type",s) + ",";
			}
			reuseTypeName = reuseTypeName.substring(0,reuseTypeName.length()-1);
		}else{
			reuseTypeName = sysBaseApi.translateDict("device_reuse_type",reuseType)==null?"":sysBaseApi.translateDict("device_reuse_type",reuseType);
		}
		device.setReuseTypeName(reuseTypeName);
		//设备类型
		String deviceTypeCode = device.getDeviceTypeCode()==null?"":device.getDeviceTypeCode();
		//设备类型层级
		String deviceTypeCodeCc = device.getDeviceTypeCodeCc()==null?"":device.getDeviceTypeCodeCc();
		//线路
		String lineCode = device.getLineCode()==null?"":device.getLineCode();
		//站点
		String stationCode = device.getStationCode()==null?"":device.getStationCode();
		//位置
		String positionCode = device.getPositionCode()==null?"":device.getPositionCode();
		String lineCodeName = sysBaseApi.translateDictFromTable("cs_line", "line_name", "line_code", lineCode);
		String stationCodeName = sysBaseApi.translateDictFromTable("cs_station", "station_name", "station_code", stationCode);
		String positionCodeName = sysBaseApi.translateDictFromTable("cs_station_position", "position_name", "position_code", positionCode);
		String positionCodeCc = lineCode + "/" + stationCode ;
		if (!"".equals(positionCode) && positionCode != null) {
			positionCodeCc += "/" + positionCode;
		}
		String positionCodeCcName = lineCodeName + "/" + stationCodeName  ;
		if(!"".equals(positionCodeName) && positionCodeName != null){
			positionCodeCcName += "/" + positionCodeName;
		}
		String deviceTypeCodeCcName = "";
		if(deviceTypeCodeCc.contains(CommonConstant.SYSTEM_SPLIT_STR)){
			List<String> strings = Arrays.asList(deviceTypeCodeCc.split(CommonConstant.SYSTEM_SPLIT_STR));
			for(String typecode : strings){
				DeviceType deviceType = deviceTypeMapper.selectOne(new QueryWrapper<DeviceType>().eq("code",typecode));
				deviceTypeCodeCcName += deviceType==null?"":deviceType.getName()+CommonConstant.SYSTEM_SPLIT_STR;
			}
		}else{
			DeviceType deviceType = deviceTypeMapper.selectOne(new QueryWrapper<DeviceType>().eq("code",deviceTypeCodeCc));
			deviceTypeCodeCcName += deviceType==null?"":deviceType.getName()+CommonConstant.SYSTEM_SPLIT_STR;
		}
		if(deviceTypeCodeCcName.contains(CommonConstant.SYSTEM_SPLIT_STR)){
			deviceTypeCodeCcName = deviceTypeCodeCcName.substring(0,deviceTypeCodeCcName.length()-1);
		}
		device.setDeviceTypeCodeCcName(deviceTypeCodeCcName);
		device.setPositionCodeCc(positionCodeCc);
		device.setPositionCodeCcName(positionCodeCcName);
		return device;
	}

	@Override
	public String getCodeByCc(String deviceTypeCodeCc) {
		String deviceTypeCode = "";
		if(!"".equals(deviceTypeCodeCc) && deviceTypeCodeCc != null){
			if(deviceTypeCodeCc.contains(CommonConstant.SYSTEM_SPLIT_STR)){
				String[] split = deviceTypeCodeCc.split(CommonConstant.SYSTEM_SPLIT_STR);
				deviceTypeCode = split[split.length-1];
			}else{
				deviceTypeCode = deviceTypeCodeCc;
			}
		}
		return deviceTypeCode;
	}

}
