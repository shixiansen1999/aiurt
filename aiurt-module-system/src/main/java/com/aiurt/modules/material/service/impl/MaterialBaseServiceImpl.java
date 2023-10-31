package com.aiurt.modules.material.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ExcelSelectListUtil;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.service.ICsManufactorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseMapper;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
		if (Objects.isNull(materialBase)) {
			return materialBase;
		}
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
	@Transactional(rollbackFor = Exception.class)
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
				// 物质编码
				String code = materialBase.getCode();
				if (StrUtil.isBlank(code)) {
					errorStrs.add("第 " + i + " 行：物资编码为空，忽略导入。");
					materialBase.setText("物资编码为空，忽略导入");
					list.add(materialBase);
					continue;
				}
				//专业
				String majorCodeName = materialBase.getMajorCodeName();
				if(StrUtil.isBlank(majorCodeName)){
					errorStrs.add("第 " + i + " 行：专业类型为空，忽略导入。");
					materialBase.setText("专业类型为空，忽略导入");
					list.add(materialBase);
					continue;
				}
				CsMajor csMajor = csMajorService.getOne(new QueryWrapper<CsMajor>().lambda().eq(CsMajor::getMajorName,majorCodeName).eq(CsMajor::getDelFlag,CommonConstant.DEL_FLAG_0));
				if(ObjectUtil.isNull(csMajor)){
					errorStrs.add("第 " + i + " 行：无法根据专业类型找到对应数据，忽略导入。");
					materialBase.setText("无法根据专业类型找到对应数据，忽略导入");
					list.add(materialBase);
					continue;
				}else{
					materialBase.setMajorCode(csMajor.getMajorCode());
					//子系统:根据物资系统类别查询
					String baseTypeCodeName = materialBase.getBaseTypeCodeName();
					if (StrUtil.isBlank(baseTypeCodeName)) {
						errorStrs.add("第 " + i + " 行：物资系统类别为空，忽略导入。");
						materialBase.setText("物资系统类别为空，忽略导入");
						list.add(materialBase);
					} else {
						//物资分类
						QueryWrapper<MaterialBaseType> queryWrapper = new QueryWrapper<MaterialBaseType>();
						queryWrapper.lambda().eq(MaterialBaseType::getMajorCode, csMajor.getMajorCode())
								.eq(MaterialBaseType::getBaseTypeName, baseTypeCodeName)
								.eq(MaterialBaseType::getDelFlag,0);
						MaterialBaseType materialBaseType = materialBaseTypeService.getOne(queryWrapper, false);
						if(ObjectUtil.isNull(materialBaseType)){
							errorStrs.add("第 " + i + " 行：无法根据物资系统类别找到对应物资分类，忽略导入。");
							materialBase.setText("无法根据物资系统类别找到对应物资分类，忽略导入");
							list.add(materialBase);
							continue;
						}else{
							materialBase.setBaseTypeCode(materialBaseType.getBaseTypeCode());
							materialBase.setSystemCode(materialBaseType.getSystemCode());
						}
						if (StrUtil.isNotBlank(code)){
							List<MaterialBase> materialBase1 = materialBaseMapper.selectList(new LambdaQueryWrapper<MaterialBase>()
									.eq(MaterialBase::getCode, code)
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
								.eq(MaterialBaseType::getDelFlag,CommonConstant.DEL_FLAG_0));
						String typeCodeCc = materialBaseTypeService.getCcStr(materialBaseTypefinal);
						materialBase.setBaseTypeCodeCc(typeCodeCc);
					}
				}
				//物资名称
				String name = materialBase.getName();
				if (StrUtil.isBlank(name)) {
					errorStrs.add("第 " + i + " 行：物资名称为空，忽略导入。");
					materialBase.setText("物资名称为空，忽略导入");
					list.add(materialBase);
					continue;
				}
				//厂家/品牌
				String manufactorCodeName = materialBase.getManufactorCodeName();
				if (StrUtil.isNotBlank(manufactorCodeName)) {
					CsManufactor csManufactor = csManufactorService.getOne(new QueryWrapper<CsManufactor>().lambda()
							.eq(CsManufactor::getName, manufactorCodeName)
							.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0), false);
					if (ObjectUtil.isNull(csManufactor)) {
						errorStrs.add("第 " + i + " 行：无法根据厂家/品牌找到对应数据，忽略导入。");
						materialBase.setText("无法根据厂家/品牌找到对应数据，忽略导入");
						list.add(materialBase);
						continue;
					} else {
						materialBase.setManufactorCode(csManufactor.getId());
					}
				}
				//单位
				String unitName = materialBase.getUnitName();
				if (StrUtil.isNotBlank(unitName)) {
					List<DictModel> materianUnit = sysBaseApi.queryDictItemsByCode("materian_unit");
					List<DictModel> collect = materianUnit.stream().filter(m -> m.getText().equals(unitName)).collect(Collectors.toList());
					if (collect.size() > 0) {
						materialBase.setUnit(collect.get(0).getValue());
					} else {
						errorStrs.add("第 " + i + " 行：无法根据物资单位找到对应数据，忽略导入。");
						materialBase.setText("无法根据物资单位找到对应数据，忽略导入");
						list.add(materialBase);
						continue;
					}
				}

				// 是否易耗品:默认否
				materialBase.setConsumablesType(0);
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
				lm.put("code",l.getCode());
				lm.put("majorCodeName",l.getMajorCodeName());
				lm.put("baseTypeCodeName",l.getBaseTypeCodeName());
				lm.put("name",l.getName());
				lm.put("manufactorCodeName",l.getManufactorCodeName());
				lm.put("specifications",l.getSpecifications());
				lm.put("technicalParameter", l.getTechnicalParameter());
				lm.put("unitName",l.getUnitName());
				lm.put("price",l.getPrice());
				lm.put("remark", l.getRemark());
				lm.put("text",l.getText());
				mapList.add(lm);
			});
			Map<String, Object> errorMap = new HashMap<String, Object>(1);
			errorMap.put("maplist", mapList);
			Workbook workbook = ExcelExportUtil.exportExcel(exportParams, errorMap);
			String fileName = "物资主数据错误模板" + "_" + System.currentTimeMillis() + ".xlsx";
			FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
			String url = fileName;
			workbook.write(out);
			errorLines += errorStrs.size();
			successLines += (listMaterial.size() - errorLines);
			return ImportExcelUtil.imporReturnRes(errorLines, successLines, errorStrs, url);
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

	/**
	 * 根据code获取物资基础数据，包括已删除的
	 *
	 * @param code
	 * @return
	 */
	@Override
	public MaterialBase selectByCode(String code) {
		if (StrUtil.isBlank(code)) {
			return null;
		}
		return baseMapper.selectByCode(code);
	}

	@Override
	public void getImportTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
		//获取输入流，原始模板位置
		org.springframework.core.io.Resource resource = new ClassPathResource("/templates/materialBase1.xlsx");
		InputStream resourceAsStream = resource.getInputStream();
		//2.获取临时文件
		File fileTemp = new File("/templates/materialBase1.xlsx");
		try {
			//将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
			FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		String path = fileTemp.getAbsolutePath();
		cn.afterturn.easypoi.excel.entity.TemplateExportParams exportParams = new cn.afterturn.easypoi.excel.entity.TemplateExportParams(path);
		Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
		Workbook workbook = cn.afterturn.easypoi.excel.ExcelExportUtil.exportExcel(sheetsMap, exportParams);
		CommonAPI bean = SpringContextUtils.getBean(CommonAPI.class);
		List<DictModel> majorModels = bean.queryTableDictItemsByCode("cs_major", "major_name", "major_code");
		ExcelSelectListUtil.selectList(workbook, "专业类型", 3, 1, 1, majorModels);
		List<DictModel> materialBaseTypeModels = bean.queryTableDictItemsByCode("material_base_type", "base_type_name", "base_type_code");
		ExcelSelectListUtil.selectList(workbook, "物资系统类别", 3, 2, 2, materialBaseTypeModels);
		List<DictModel> csManufactorModels = bean.queryTableDictItemsByCode("cs_manufactor", "name", "code");
		ExcelSelectListUtil.selectList(workbook, "厂家品牌", 3, 4, 4, csManufactorModels);
		List<DictModel> unitModels = bean.queryDictItemsByCode("materian_unit");
		ExcelSelectListUtil.selectList(workbook, "单位", 3, 7, 7, unitModels);
		String fileName = "物资主数据导入模板.xlsx";
		try {
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), "iso8859-1"));
			response.setHeader("Content-Disposition", "attachment;filename=" + "物资主数据导入模板.xlsx");
			BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
			workbook.write(bufferedOutPut);
			bufferedOutPut.flush();
			bufferedOutPut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
