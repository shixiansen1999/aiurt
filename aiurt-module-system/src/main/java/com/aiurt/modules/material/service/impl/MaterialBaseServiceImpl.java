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
		String format = "";
				MaterialBase materialBase = materialBaseMapper.selectOne(new LambdaQueryWrapper<MaterialBase>().likeRight(MaterialBase::getCode, finalstr)
				.eq(MaterialBase::getDelFlag, 0).orderByDesc(MaterialBase::getCreateTime).last("limit 1"));
		if(materialBase != null){
			String code = materialBase.getCode();
			String numstr = code.substring(code.length()-5);
			format = String.format("%05d", Long.parseLong(numstr) + 1);
		}else{
			format = "00001";
		}
		return finalstr + format;
	}

	@Override
	public MaterialBase translate(MaterialBase materialBase) {
		//物资类型最小
		String baseTypeCode = materialBase.getBaseTypeCode()==null?"":materialBase.getBaseTypeCode();
		//物资类型层级
		String baseTypeCodeCc = materialBase.getBaseTypeCodeCc()==null?"":materialBase.getBaseTypeCodeCc();
		String baseTypeCodeCcName = "";
		if(baseTypeCodeCc.contains("/")){
			List<String> strings = Arrays.asList(baseTypeCodeCc.split("/"));
			for(String typecode : strings){
				MaterialBaseType materialBaseType = materialBaseTypeMapper.selectOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",typecode));
				baseTypeCodeCcName += materialBaseType==null?"":materialBaseType.getBaseTypeName()+"/";
			}
		}else{
			MaterialBaseType materialBaseType = materialBaseTypeMapper.selectOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",baseTypeCodeCc));
			baseTypeCodeCcName = materialBaseType==null?"":materialBaseType.getBaseTypeName()+"/";
		}
		if(baseTypeCodeCcName.contains("/")){
			baseTypeCodeCcName = baseTypeCodeCcName.substring(0,baseTypeCodeCcName.length()-1);
		}
		materialBase.setBaseTypeCodeCcName(baseTypeCodeCcName);
		//专业
		String majorCode = materialBase.getMajorCode()==null?"":materialBase.getMajorCode();
		//子系统
		String systemCode = materialBase.getSystemCode()==null?"":materialBase.getSystemCode();
		//厂商
		String manufactorCode = materialBase.getManufactorCode()==null?"":materialBase.getManufactorCode();
		//所属部门
		String sysOrgCode = materialBase.getSysOrgCode()==null?"":materialBase.getSysOrgCode();
		String baseTypeCodeName = sysBaseApi.translateDictFromTable("material_base_type", "base_type_name", "base_type_code", baseTypeCode);
		String majorCodeName = sysBaseApi.translateDictFromTable("cs_major", "major_name", "major_code", majorCode);
		String systemCodeName = sysBaseApi.translateDictFromTable("cs_subsystem", "system_name", "system_code", systemCode);
		String manufactorCodeName = sysBaseApi.translateDictFromTable("cs_manufactor", "name", "code", manufactorCode);
		String sysOrgCodeName = sysBaseApi.translateDictFromTable("sys_depart", "depart_name", "org_code", sysOrgCode);
		materialBase.setBaseTypeCodeName(baseTypeCodeName);
		materialBase.setMajorCodeName(majorCodeName);
		materialBase.setSystemCodeName(systemCodeName);
		materialBase.setManufactorCodeName(manufactorCodeName);
		materialBase.setSysOrgCodeName(sysOrgCodeName);
		return materialBase;
	}
}
