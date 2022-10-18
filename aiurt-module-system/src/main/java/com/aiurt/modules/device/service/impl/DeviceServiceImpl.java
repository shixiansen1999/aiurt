package com.aiurt.modules.device.service.impl;

import cn.hutool.core.util.StrUtil;
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
		String positionCodeCc = lineCode ;
		if(stationCode!= null && !"".equals(stationCode)){
			positionCodeCc += CommonConstant.SYSTEM_SPLIT_STR + stationCode;
		}

		if (!"".equals(positionCode) && positionCode != null) {
			positionCodeCc += CommonConstant.SYSTEM_SPLIT_STR + positionCode;
		}
		String positionCodeCcName = lineCodeName ;
		if(stationCodeName != null && !"".equals(stationCodeName)){
			positionCodeCcName +=  CommonConstant.SYSTEM_SPLIT_STR + stationCodeName  ;
		}
		if(!"".equals(positionCodeName) && positionCodeName != null){
			positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + positionCodeName;
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

	@Override
	public QueryWrapper<Device> getQueryWrapper(String stationCode,String positionCodeCc, String temporary, String majorCode, String systemCode, String deviceTypeCode, String code, String name, String status) {
		QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
		if(majorCode != null && !"".equals(majorCode)){
			queryWrapper.eq("major_code", majorCode);
		}
		if(temporary != null && !"".equals(temporary)){
			queryWrapper.eq("temporary", temporary);
		}
		if(systemCode != null && !"".equals(systemCode)){
			queryWrapper.eq("system_code", systemCode);
		}
		if(deviceTypeCode != null && !"".equals(deviceTypeCode)){
			queryWrapper.apply(" FIND_IN_SET ( '"+deviceTypeCode+"' , REPLACE(device_type_code_cc,'/',',')) ");
		}
		if(positionCodeCc != null && !"".equals(positionCodeCc)){
			if(positionCodeCc.contains(CommonConstant.SYSTEM_SPLIT_STR)){
				String[] split = positionCodeCc.split(CommonConstant.SYSTEM_SPLIT_STR);
				int length = split.length;
				switch (length){
					case 2:
						queryWrapper.eq("line_code", split[0]);
						queryWrapper.eq("station_code", split[1]);
						break;
					case 3:
						queryWrapper.eq("line_code", split[0]);
						queryWrapper.eq("station_code", split[1]);
						queryWrapper.eq("position_code", split[2]);
						break;
					default:
						queryWrapper.eq("line_code", split[0]);
				}
			}else{
				queryWrapper.eq("line_code", positionCodeCc);
			}
		}
		if(code != null && !"".equals(code)){
			queryWrapper.like("code", code);
		}
		if(name != null && !"".equals(name)){
			queryWrapper.like("name", name);
		}
		if(status != null && !"".equals(status)){
			queryWrapper.eq("status", status);
		}
        // 多个已逗号分割
		if(StrUtil.isNotEmpty(stationCode)){
			queryWrapper.in("station_code", StrUtil.split(stationCode,','));
		}
		queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		return queryWrapper;
	}

}
