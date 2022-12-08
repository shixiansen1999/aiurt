package com.aiurt.modules.material.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
import com.aiurt.modules.schedule.entity.ScheduleItem;
import com.aiurt.modules.schedule.service.impl.ScheduleServiceImpl;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
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
	@Value("${jeecg.path.upload}")
	private String upLoadPath;
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
				MaterialBaseType materialBaseType = materialBaseTypeMapper.selectOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",typecode).eq("major_code",materialBase.getMajorCode()));
				baseTypeCodeCcName += materialBaseType==null?"":materialBaseType.getBaseTypeName()+"/";
			}
		}else{
			MaterialBaseType materialBaseType = materialBaseTypeMapper.selectOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",baseTypeCodeCc).eq("major_code",materialBase.getMajorCode()));
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

		List<MaterialBase> list = new ArrayList<>();
		for (int i = 0; i < listMaterial.size(); i++) {
			try {
				MaterialBase materialBase = listMaterial.get(i);
				String finalstr = "";
				//专业
				String majorCodeName = materialBase.getMajorCodeName()==null?"":materialBase.getMajorCodeName();
				if("".equals(majorCodeName)){
					errorStrs.add("第 " + i + " 行：专业名称为空，忽略导入。");
					materialBase.setText("专业名称为空，忽略导入");
					list.add(materialBase);
					continue;
				}
				CsMajor csMajor = csMajorService.getOne(new QueryWrapper<CsMajor>().eq("major_name",majorCodeName).eq("del_flag",0));
				if(csMajor == null){
					errorStrs.add("第 " + i + " 行：无法根据专业名称找到对应数据，忽略导入。");
					materialBase.setText("无法根据专业名称找到对应数据，忽略导入");
					list.add(materialBase);
					continue;
				}else{
					materialBase.setMajorCode(csMajor.getMajorCode());
					//子系统
					String systemCodeName = materialBase.getSystemCodeName()==null?"":materialBase.getSystemCodeName();
					CsSubsystem csSubsystem = csSubsystemService.getOne(new QueryWrapper<CsSubsystem>().eq("major_code",csMajor.getMajorCode()).eq("system_name",systemCodeName).eq("del_flag",0));
					if(!"".equals(systemCodeName) && csSubsystem == null){
						errorStrs.add("第 " + i + " 行：无法根据子系统名称找到对应数据，忽略导入。");
						materialBase.setText("无法根据子系统名称找到对应数据，忽略导入");
						list.add(materialBase);
						continue;
					}else{
						if(csSubsystem != null){
							materialBase.setSystemCode(csSubsystem.getSystemCode());
						}
						//物资分类
						String baseTypeCodeName = materialBase.getBaseTypeCodeName()==null?"":materialBase.getBaseTypeCodeName();
						if("".equals(baseTypeCodeName)){
							errorStrs.add("第 " + i + " 行：物资分类编码为空，忽略导入。");
							materialBase.setText("物资分类编码为空，忽略导入");
							list.add(materialBase);
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
							materialBase.setText("无法根据物资分类名称找到对应数据，忽略导入");
							list.add(materialBase);
							continue;
						}else{
							materialBase.setBaseTypeCode(materialBaseType.getBaseTypeCode());
						}
						if (StrUtil.isNotEmpty(materialBase.getCode())){
							List<MaterialBase> materialBase1 = materialBaseMapper.selectList(new LambdaQueryWrapper<MaterialBase>()
									.eq(MaterialBase::getCode,materialBase.getCode())
									.eq(MaterialBase::getMajorCode,materialBase.getMajorCode())
									.eq(MaterialBase::getDelFlag,0));
							if (materialBase1.size()>0){
								errorStrs.add("第 " + i + " 行：在同一专业下相同的物资编号，忽略导入。");
								materialBase.setText("在同一专业下相同的物资编号，忽略导入");
								list.add(materialBase);
								continue;
							}
						}
						MaterialBaseType materialBaseTypefinal = materialBaseTypeService.getOne(new QueryWrapper<MaterialBaseType>().lambda()
								                             .eq(MaterialBaseType::getBaseTypeCode,materialBaseType.getBaseTypeCode())
								                             .eq(MaterialBaseType::getMajorCode,materialBaseType.getMajorCode())
								                             .eq(MaterialBaseType::getDelFlag,0));
						String typeCodeCc = materialBaseTypeService.getCcStr(materialBaseTypefinal);
						materialBase.setBaseTypeCodeCc(typeCodeCc);
					}
				}
				//物资名称
				String name = materialBase.getName()==null?"":materialBase.getName();
				if ("".equals(name)) {
					errorStrs.add("第 " + i + " 行：物资名称为空，忽略导入。");
					materialBase.setText("物资名称为空，忽略导入");
					list.add(materialBase);
					continue;
				}
				String type = materialBase.getType()==null?"":materialBase.getType();
				if ("".equals(type)) {
					errorStrs.add("第 " + i + " 行：物资类型为空，忽略导入。");
					materialBase.setText("物资类型为空，忽略导入");
					list.add(materialBase);
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
						materialBase.setText("物资类型不存在，忽略导入");
						list.add(materialBase);
						continue;
					}
				}
                if (StrUtil.isEmpty(materialBase.getManufactorCodeName())){
					errorStrs.add("第 " + i + " 行：生产厂商名称未输入，忽略导入。");
					materialBase.setText("生产厂商名称未输入，忽略导入");
					list.add(materialBase);
					continue;
				}
				//生产厂商
				String manufactorCodeName = materialBase.getManufactorCodeName()==null?"":materialBase.getManufactorCodeName();
				CsManufactor csManufactor = csManufactorService.getOne(new QueryWrapper<CsManufactor>().eq("name",manufactorCodeName).eq("del_flag",0).last("limit 1"));
				if(!"".equals(manufactorCodeName) && csManufactor == null){
					errorStrs.add("第 " + i + " 行：无法根据生产厂商名称找到对应数据，忽略导入。");
					materialBase.setText("无法根据生产厂商名称找到对应数据，忽略导入");
					list.add(materialBase);
					continue;
				}else{
					materialBase.setManufactorCode(csManufactor.getId());
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
						materialBase.setText("无法根据物资单位找到对应数据，忽略导入");
						list.add(materialBase);
						continue;
					}
				}
				if(StrUtil.isNotEmpty(materialBase.getConsumablesName())){
					//是否为易耗品 默认为否 在为是的时候修改状态
					if ("是".equals(materialBase.getConsumablesName())){
						materialBase.setConsumablesType(1);
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
		if (list.size()>0){
			//创建导入失败错误报告,进行模板导出
			Resource resource = new ClassPathResource("templates\\materialBaseError.xlsx");
			InputStream resourceAsStream = resource.getInputStream();
			//2.获取临时文件
			File fileTemp= new File("templates\\materialBaseError.xlsx");
			try {
				//将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
				FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			String path = fileTemp.getAbsolutePath();
			TemplateExportParams exportParams = new TemplateExportParams(path);
			List<Map<String, Object>> mapList = new ArrayList<>();
			list.forEach(l->{
				Map<String, Object> lm = new HashMap<String, Object>();
				lm.put("majorCodeName",l.getMajorCodeName());
				lm.put("systemCodeName",l.getSystemCodeName());
				lm.put("baseTypeCodeName",l.getBaseTypeCodeName());
				lm.put("code",l.getCode());
				lm.put("name",l.getName());
				lm.put("type",l.getType());
				lm.put("manufactorCodeName",l.getManufactorCodeName());
				lm.put("specifications",l.getSpecifications());
				lm.put("unitName",l.getUnitName());
				lm.put("price",l.getPrice());
				lm.put("consumablesName",l.getConsumablesName());
				lm.put("text",l.getText());
				mapList.add(lm);
			});
			Map<String, Object> errorMap = new HashMap<String, Object>();
			errorMap.put("maplist", mapList);
					Workbook workbook = ExcelExportUtil.exportExcel(exportParams,errorMap);
						String fileName = "物资主数据错误模板"+"_" + System.currentTimeMillis()+".xlsx";
						FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+fileName);
						String  url = fileName;
						workbook.write(out);
						errorLines+=errorStrs.size();
						successLines+=(listMaterial.size()-errorLines);
					return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorStrs,url);
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
