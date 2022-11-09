package com.aiurt.modules.material.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.service.ICsManufactorService;
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
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	@Autowired
	private ICsMajorService csMajorService;
	@Autowired
	private ICsSubsystemService csSubsystemService;
	@Autowired
	private IMaterialBaseTypeService materialBaseTypeService;
	@Autowired
	private ICsManufactorService csManufactorService;

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
		//物资类型层级
		String baseTypeCodeCc = materialBase.getBaseTypeCodeCc()==null?"":materialBase.getBaseTypeCodeCc();
		String baseTypeCodeCcName = "";
		if(baseTypeCodeCc.contains(CommonConstant.SYSTEM_SPLIT_STR)){
			List<String> strings = Arrays.asList(baseTypeCodeCc.split(CommonConstant.SYSTEM_SPLIT_STR));
			for(String typecode : strings){
				MaterialBaseType materialBaseType = materialBaseTypeMapper.selectOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",typecode));
				baseTypeCodeCcName += materialBaseType==null?"":materialBaseType.getBaseTypeName()+"/";
			}
		}else{
			MaterialBaseType materialBaseType = materialBaseTypeMapper.selectOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",baseTypeCodeCc));
			baseTypeCodeCcName = materialBaseType==null?"":materialBaseType.getBaseTypeName()+CommonConstant.SYSTEM_SPLIT_STR;
		}
		if(baseTypeCodeCcName.contains(CommonConstant.SYSTEM_SPLIT_STR)){
			baseTypeCodeCcName = baseTypeCodeCcName.substring(0,baseTypeCodeCcName.length()-1);
		}
		materialBase.setBaseTypeCodeCcName(baseTypeCodeCcName);
		return materialBase;
	}

	@Override
	public Result importExcelMaterial(MultipartFile file, ImportParams params) throws Exception {
		List<MaterialBase> listMaterial = ExcelImportUtil.importExcel(file.getInputStream(), MaterialBase.class, params);
		List<String> errorStrs = new ArrayList<>();
		// 去掉 sql 中的重复数据
		Integer errorLines=0;
		Integer successLines=0;
		for (int i = 0; i < listMaterial.size(); i++) {
			try {
				MaterialBase materialBase = listMaterial.get(i);
				String finalstr = "";
				//专业
				String majorCodeName = materialBase.getMajorCodeName()==null?"":materialBase.getMajorCodeName();
				if("".equals(majorCodeName)){
					errorStrs.add("第 " + i + " 行：专业名称为空，忽略导入。");
					continue;
				}
				CsMajor csMajor = csMajorService.getOne(new QueryWrapper<CsMajor>().eq("major_name",majorCodeName).eq("del_flag",0));
				if(csMajor == null){
					errorStrs.add("第 " + i + " 行：无法根据专业名称找到对应数据，忽略导入。");
					continue;
				}else{
					materialBase.setMajorCode(csMajor.getMajorCode());
					//子系统
					String systemCodeName = materialBase.getSystemCodeName()==null?"":materialBase.getSystemCodeName();
					CsSubsystem csSubsystem = csSubsystemService.getOne(new QueryWrapper<CsSubsystem>().eq("major_code",csMajor.getMajorCode()).eq("system_name",systemCodeName).eq("del_flag",0));
					if(!"".equals(systemCodeName) && csSubsystem == null){
						errorStrs.add("第 " + i + " 行：无法根据子系统名称找到对应数据，忽略导入。");
						continue;
					}else{
						if(csSubsystem != null){
							materialBase.setSystemCode(csSubsystem.getSystemCode());
						}
						//物资分类
						String baseTypeCodeName = materialBase.getBaseTypeCodeName()==null?"":materialBase.getBaseTypeCodeName();
						if("".equals(baseTypeCodeName)){
							errorStrs.add("第 " + i + " 行：物资分类编码为空，忽略导入。");
							continue;
						}
						QueryWrapper<MaterialBaseType> queryWrapper = new QueryWrapper<MaterialBaseType>();
						queryWrapper.eq("major_code",csMajor.getMajorCode()).eq("base_type_name",baseTypeCodeName).eq("del_flag",0);
						if(!"".equals(systemCodeName)){
							queryWrapper.eq("system_code",csSubsystem.getSystemCode());
						}else{
							queryWrapper.apply(" (system_code = '' or system_code is null) ");
						}
						MaterialBaseType materialBaseType = materialBaseTypeService.getOne(queryWrapper);
						if(materialBaseType == null){
							errorStrs.add("第 " + i + " 行：无法根据物资分类名称找到对应数据，忽略导入。");
							continue;
						}else{
							materialBase.setBaseTypeCode(materialBaseType.getBaseTypeCode());
						}
						MaterialBaseType materialBaseTypefinal = materialBaseTypeService.getOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",materialBaseType.getBaseTypeCode()));
						String typeCodeCc = materialBaseTypeService.getCcStr(materialBaseTypefinal);
						materialBase.setBaseTypeCodeCc(typeCodeCc);
					}
				}
				//物资名称
				String name = materialBase.getName()==null?"":materialBase.getName();
				if ("".equals(name)) {
					errorStrs.add("第 " + i + " 行：物资名称为空，忽略导入。");
					continue;
				}
				String type = materialBase.getType()==null?"":materialBase.getType();
				if ("".equals(type)) {
					errorStrs.add("第 " + i + " 行：物资类型为空，忽略导入。");
					continue;
				}else {
					if (type.equals("通用类")){
						materialBase.setType("2");
					}else if (type.equals("专用类")){
							materialBase.setType("1");
						}else if (type.equals("AFC类")){
							materialBase.setType("3");
						}else {
						errorStrs.add("第 " + i + " 行：物资类型不存在，忽略导入。");
						continue;
					}
				}

				//生产厂商
				String manufactorCodeName = materialBase.getManufactorCodeName()==null?"":materialBase.getManufactorCodeName();
				CsManufactor csManufactor = csManufactorService.getOne(new QueryWrapper<CsManufactor>().eq("name",manufactorCodeName).eq("del_flag",0));
				if(!"".equals(manufactorCodeName) && csManufactor == null){
					errorStrs.add("第 " + i + " 行：无法根据生产厂商名称找到对应数据，忽略导入。");
					continue;
				}else{
					materialBase.setManufactorCode(csManufactor.getCode());
				}
				//单位
				String unit = materialBase.getUnit()==null?"":materialBase.getUnit();
				if(!"".equals(unit)){
					List<DictModel> materianUnit = sysBaseApi.queryDictItemsByCode("materian_unit");
					List<DictModel> collect = materianUnit.stream().filter(m -> m.getText().equals(unit)).collect(Collectors.toList());
					if(collect != null && collect.size()>0){
						materialBase.setUnit(collect.get(0).getValue());
					}else{
						errorStrs.add("第 " + i + " 行：无法根据物资单位找到对应数据，忽略导入。");
						continue;
					}
				}
				LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
				materialBase.setSysOrgCode(user.getOrgCode());
				int save = materialBaseMapper.insert(materialBase);
				if(save<=0){
					throw new Exception(CommonConstant.SQL_INDEX_UNIQ_MATERIAL_BASE_CODE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		errorLines+=errorStrs.size();
		successLines+=(listMaterial.size()-errorLines);
		return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorStrs);
	}

	@Override
	public String getCodeByCc(String baseTypeCodeCc) {
		String baseTypeCode = "";
		if(!"".equals(baseTypeCodeCc)){
			if(baseTypeCodeCc.contains(CommonConstant.SYSTEM_SPLIT_STR)){
				String[] split = baseTypeCodeCc.split(CommonConstant.SYSTEM_SPLIT_STR);
				baseTypeCode = split[split.length-1];
			}else{
				baseTypeCode = baseTypeCodeCc;
			}
		}
		return baseTypeCode;
	}

}
