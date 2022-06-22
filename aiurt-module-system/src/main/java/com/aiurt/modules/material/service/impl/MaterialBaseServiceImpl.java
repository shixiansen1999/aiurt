package com.aiurt.modules.material.service.impl;

import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.mapper.DeviceAssemblyMapper;
import com.aiurt.modules.device.mapper.DeviceMapper;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseMapper;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class MaterialBaseServiceImpl extends ServiceImpl<MaterialBaseMapper, MaterialBase> implements IMaterialBaseService {

	@Autowired
	private MaterialBaseMapper materialBaseMapper;
	@Autowired
	private MaterialBaseTypeMapper materialBaseTypeMapper;
	@Autowired
	private SysBaseApiImpl sysBaseApi;

	@Override
	public String getNewBaseCode(String finalstr) {
		String res = "";
		MaterialBase materialBase = materialBaseMapper.selectOne(new LambdaQueryWrapper<MaterialBase>().likeRight(MaterialBase::getCode, finalstr)
				.eq(MaterialBase::getDelFlag, 0).orderByDesc(MaterialBase::getCreateTime).last("limit 1"));
		String code = materialBase.getCode();
		String numstr = code.substring(code.length()-5);
		String format = String.format("%05d", Long.parseLong(numstr) + 1);
		return finalstr + format;
	}

	@Override
	public MaterialBase translate(MaterialBase materialBase) {
		String baseTypeCode = materialBase.getBaseTypeCode()==null?"":materialBase.getBaseTypeCode();
		String baseTypeCodeName = "";
		if(baseTypeCode.contains("/")){
			List<String> strings = Arrays.asList(baseTypeCode.split("/"));
			for(String typecode : strings){
				MaterialBaseType materialBaseType = materialBaseTypeMapper.selectOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",typecode));
				baseTypeCodeName += materialBaseType==null?"":materialBaseType.getBaseTypeName()+"/";
			}
		}else{
			MaterialBaseType materialBaseType = materialBaseTypeMapper.selectOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",baseTypeCode));
			baseTypeCodeName = materialBaseType==null?"":materialBaseType.getBaseTypeName()+"/";
		}
		if(baseTypeCodeName.contains("/")){
			baseTypeCodeName = baseTypeCodeName.substring(0,baseTypeCodeName.length()-1);
		}
		materialBase.setBaseTypeCodeName(baseTypeCodeName);
		String majorCode = materialBase.getMajorCode()==null?"":materialBase.getMajorCode();
		String systemCode = materialBase.getSystemCode()==null?"":materialBase.getSystemCode();
		String manufactorCode = materialBase.getManufactorCode()==null?"":materialBase.getManufactorCode();
		String majorCodeName = sysBaseApi.translateDictFromTable("cs_major", "major_name", "major_code", majorCode);
		String systemCodeName = sysBaseApi.translateDictFromTable("cs_subsystem", "system_name", "system_code", systemCode);
		String manufactorCodeName = sysBaseApi.translateDictFromTable("cs_manufactor", "name", "code", manufactorCode);
		materialBase.setMajorCodeName(majorCodeName);
		materialBase.setSystemCodeName(systemCodeName);
		materialBase.setManufactorCodeName(manufactorCodeName);
		return materialBase;
	}
}
