package com.aiurt.modules.device.service.impl;

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
		//数据字典部分翻译
		//设备等级
		String deviceLevel = device.getDeviceLevel()==null?"":device.getDeviceLevel();
		//设备状态
		String status = device.getStatus()==null?"":device.getStatus().toString();
		//设备复用类型
		String reuseType = device.getReuseType()==null?"":device.getReuseType();
		//是否临时设备
		String temporary = device.getTemporary()==null?"":device.getTemporary();
		//设备报废状态
		String scrapFlag = device.getScrapFlag()==null?"":device.getScrapFlag().toString();
		device.setDeviceLevelName(sysBaseApi.translateDict("device_level",deviceLevel)==null?"":sysBaseApi.translateDict("device_level",deviceLevel));
		device.setStatusDesc(sysBaseApi.translateDict("device_status",status)==null?"":sysBaseApi.translateDict("device_status",status));
		String reuseTypeName = "";
		if(!"".equals(reuseType) && reuseType.contains(",")){
			String[] split = reuseType.split(",");
			for(String s : split){
				reuseTypeName += sysBaseApi.translateDict("device_reuse_type",reuseType)==null?"":sysBaseApi.translateDict("device_reuse_type",s) + ",";
			}
			reuseTypeName = reuseTypeName.substring(0,reuseTypeName.length()-1);
		}else{
			reuseTypeName = sysBaseApi.translateDict("device_reuse_type",reuseType)==null?"":sysBaseApi.translateDict("device_reuse_type",reuseType);
		}
		device.setReuseTypeName(reuseTypeName);
		device.setTemporaryName(sysBaseApi.translateDict("device_temporary",temporary)==null?"":sysBaseApi.translateDict("device_temporary",temporary));
		device.setScrapFlagName(sysBaseApi.translateDict("device_scrap_flag",scrapFlag)==null?"":sysBaseApi.translateDict("device_scrap_flag",scrapFlag));
		//表部分翻译
		//所属专业
		String majorCode = device.getMajorCode()==null?"":device.getMajorCode();
		//子系统
		String systemCode = device.getSystemCode()==null?"":device.getSystemCode();
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
		//管理员
		String manageUserName = device.getManageUserName()==null?"":device.getManageUserName();
		//班组
		String orgCode = device.getOrgCode()==null?"":device.getOrgCode();
		//厂商
		String manufactorCode = device.getManufactorCode()==null?"":device.getManufactorCode();
		String majorCodeName = sysBaseApi.translateDictFromTable("cs_major", "major_name", "major_code", majorCode);
		String systemCodeName = sysBaseApi.translateDictFromTable("cs_subsystem", "system_name", "system_code", systemCode);
		String deviceTypeCodeName = sysBaseApi.translateDictFromTable("device_type", "name", "code", deviceTypeCode);
		String lineCodeName = sysBaseApi.translateDictFromTable("cs_line", "line_name", "line_code", lineCode);
		String stationCodeName = sysBaseApi.translateDictFromTable("cs_station", "station_name", "station_code", stationCode);
		String positionCodeName = sysBaseApi.translateDictFromTable("cs_station_position", "position_name", "position_code", positionCode);
		String manageUserNameName = sysBaseApi.translateDictFromTable("sys_user", "realname", "username", manageUserName);
		String orgCodeName = sysBaseApi.translateDictFromTable("sys_depart", "depart_name", "org_code", orgCode);
		String manufactorCodeName = sysBaseApi.translateDictFromTable("cs_manufactor", "name", "code", manufactorCode);
		String deviceTypeCodeCcName = "";
		if(deviceTypeCodeCc.contains("/")){
			List<String> strings = Arrays.asList(deviceTypeCodeCc.split("/"));
			for(String typecode : strings){
				DeviceType deviceType = deviceTypeMapper.selectOne(new QueryWrapper<DeviceType>().eq("code",typecode));
				deviceTypeCodeCcName += deviceType==null?"":deviceType.getName()+"/";
			}
		}else{
			DeviceType deviceType = deviceTypeMapper.selectOne(new QueryWrapper<DeviceType>().eq("code",deviceTypeCodeCc));
			deviceTypeCodeCcName += deviceType==null?"":deviceType.getName()+"/";
		}
		if(deviceTypeCodeCcName.contains("/")){
			deviceTypeCodeCcName = deviceTypeCodeCcName.substring(0,deviceTypeCodeCcName.length()-1);
		}
		device.setMajorCodeName(majorCodeName);
		device.setSystemCodeName(systemCodeName);
		device.setDeviceTypeCodeName(deviceTypeCodeName);
		device.setDeviceTypeCodeCcName(deviceTypeCodeCcName);
		device.setLineCodeName(lineCodeName);
		device.setStationCodeName(stationCodeName);
		device.setPositionCodeName(positionCodeName);
		device.setManageUserNameName(manageUserNameName);
		device.setOrgCodeName(orgCodeName);
		device.setManufactorCodeName(manufactorCodeName);
		return device;
	}

}
