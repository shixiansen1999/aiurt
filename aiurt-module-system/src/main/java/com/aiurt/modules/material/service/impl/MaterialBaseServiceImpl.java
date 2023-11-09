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
import com.aiurt.common.util.XlsUtil;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
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
	@Value("${jeecg.path.errorExcelUpload}")
	private String errorExcelUpload;
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<MaterialBase> materialBaseClass) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		List<String> errorMessage = new ArrayList<>();
		int successLines = 0;
		// 错误信息
		int  errorLines = 0;
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			String type = FilenameUtils.getExtension(file.getOriginalFilename());
			if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
				return XlsUtil.importReturnRes(errorLines, successLines, errorMessage, false, null);
			}
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<MaterialBase> list = ExcelImportUtil.importExcel(file.getInputStream(), MaterialBase.class, params);

				errorLines = check(list, errorLines);

				if (errorLines > 0) {
					//存在错误，导出错误清单
					return getErrorExcel(errorLines, errorMessage, list, successLines, null, type);
				}
				this.saveBatch(list);
				return XlsUtil.importReturnRes(errorLines, list.size(), errorMessage, true, null);
			} catch (Exception e) {
				//update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
				String msg = e.getMessage();
				log.error(msg, e);
				if(msg!=null && msg.contains("Duplicate entry")){
					return Result.error("文件导入失败:有重复数据！");
				}else{
					return Result.error("文件导入失败:" + e.getMessage());
				}
				//update-end-author:taoyan date:20211124 for: 导入数据重复增加提示
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Result.error("文件导入失败！");
	}
	
	/**
	 * 检验导入数据
	 * @param list 导入数据
	 * @param errorLines 错误行数
	 * @return 返回错误行数
	 */
	private int check(List<MaterialBase> list, int errorLines) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		// 专业map
		Map<String, CsMajor> csMajorMap = csMajorService.list(new QueryWrapper<CsMajor>().lambda().eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0)).stream().collect(Collectors.toMap(CsMajor::getMajorName, Function.identity()));
		// 单位map
		Map<String, String> materianUnitMap = sysBaseApi.queryDictItemsByCode("materian_unit").stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue));
		// 导入数据的code集合，检验重复编码
		Set<String> codes = new HashSet<>();
		// 遍历
		for (MaterialBase materialBase : list) {
			StringBuilder error = new StringBuilder();
			// 物资编码
			String code = materialBase.getCode();
			if (StrUtil.isBlank(code)) {
				error.append("物资编码不能为空;");
			} else {
				if (codes.contains(code)) {
					error.append("物资编码重复;");
				} else {
					codes.add(code);
				}
			}
			// 专业
			String majorCodeName = materialBase.getMajorCodeName();
			if (StrUtil.isBlank(majorCodeName)) {
				error.append("专业类型不能为空;");
			}
			// 物资系统类别
			String baseTypeCodeName = materialBase.getBaseTypeCodeName();
			if (StrUtil.isBlank(baseTypeCodeName)) {
				error.append("物资系统类别不能为空;");
			}
			// 验证编码、专业、分类
			if (StrUtil.isNotBlank(majorCodeName)) {
				CsMajor csMajor = csMajorMap.get(majorCodeName);
				if (ObjectUtil.isNull(csMajor)) {
					error.append("系统中不存在该专业类型;");
				} else {
					materialBase.setMajorCode(csMajor.getMajorCode());
					//编码
					if (StrUtil.isNotBlank(code)) {
						List<MaterialBase> materialBase1 = materialBaseMapper.selectList(new LambdaQueryWrapper<MaterialBase>()
								.eq(MaterialBase::getCode, code)
								.eq(MaterialBase::getMajorCode, materialBase.getMajorCode())
								.eq(MaterialBase::getDelFlag, 0));
						if (materialBase1.size() > 0) {
							error.append("系统中该专业下已存在相同的物资编码;");
						}
					}
					//物资分类（包括子系统）
					if (StrUtil.isNotBlank(baseTypeCodeName)) {
						QueryWrapper<MaterialBaseType> queryWrapper = new QueryWrapper<MaterialBaseType>();
						queryWrapper.lambda().eq(MaterialBaseType::getMajorCode, csMajor.getMajorCode())
								.eq(MaterialBaseType::getBaseTypeName, baseTypeCodeName)
								.eq(MaterialBaseType::getDelFlag, 0);
						MaterialBaseType materialBaseType = materialBaseTypeService.getOne(queryWrapper, false);
						if (ObjectUtil.isNull(materialBaseType)) {
							error.append("该专业下不存在该物资分类;");
						} else {
							materialBase.setBaseTypeCode(materialBaseType.getBaseTypeCode());
							materialBase.setSystemCode(materialBaseType.getSystemCode());
							MaterialBaseType materialBaseTypefinal = materialBaseTypeService.getOne(new QueryWrapper<MaterialBaseType>().lambda()
									.eq(MaterialBaseType::getBaseTypeCode, materialBaseType.getBaseTypeCode())
									.eq(MaterialBaseType::getMajorCode, materialBaseType.getMajorCode())
									.eq(MaterialBaseType::getDelFlag, CommonConstant.DEL_FLAG_0));
							String typeCodeCc = materialBaseTypeService.getCcStr(materialBaseTypefinal);
							materialBase.setBaseTypeCodeCc(typeCodeCc);
						}
					}
				}
			}
			//物资名称
			String name = materialBase.getName();
			if (StrUtil.isBlank(name)) {
				error.append("物资名称不能为空;");
			}
			//厂家/品牌
			String manufactorCodeName = materialBase.getManufactorCodeName();
			if (StrUtil.isNotBlank(manufactorCodeName)) {
				CsManufactor csManufactor = csManufactorService.getOne(new QueryWrapper<CsManufactor>().lambda()
						.eq(CsManufactor::getName, manufactorCodeName)
						.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0), false);
				if (ObjectUtil.isNull(csManufactor)) {
					error.append("系统中不存在该厂家/品牌;");
				} else {
					materialBase.setManufactorCode(csManufactor.getId());
				}
			}
			//单位
			String unitName = materialBase.getUnitName();
			if (StrUtil.isNotBlank(unitName)) {
				if (materianUnitMap.containsKey(unitName)) {
					materialBase.setUnit(materianUnitMap.get(unitName));
				} else {
					error.append("系统中不存在该单位;");
				}
			}

			// 是否易耗品:默认否
			materialBase.setConsumablesType(0);
			materialBase.setSysOrgCode(user.getOrgCode());
			if (ObjectUtil.isNotEmpty(error)) {
				errorLines++;
				materialBase.setText(error.toString());
			}
		}
		return errorLines;
	}


	private Result<?> getErrorExcel(int errorLines, List<String> errorMessage, List<MaterialBase> list, int successLines, String url, String type) {
		try {
			TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/materialBaseError.xlsx");
			Map<String, Object> errorMap = new HashMap<>(16);

			List<Map<String, String>> listMap = new ArrayList<>();
			for (MaterialBase materialBase : list) {
				Map<String, String> lm = new HashMap<>(11);
				lm.put("code",materialBase.getCode());
				lm.put("majorCodeName",materialBase.getMajorCodeName());
				lm.put("baseTypeCodeName",materialBase.getBaseTypeCodeName());
				lm.put("name",materialBase.getName());
				lm.put("manufactorCodeName",materialBase.getManufactorCodeName());
				lm.put("specifications",materialBase.getSpecifications());
				lm.put("technicalParameter", materialBase.getTechnicalParameter());
				lm.put("unitName",materialBase.getUnitName());
				lm.put("price",materialBase.getPrice());
				lm.put("remark", materialBase.getRemark());
				lm.put("text",materialBase.getText());
				listMap.add(lm);
			}
			errorMap.put("maplist", listMap);
			Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(1);
			sheetsMap.put(0, errorMap);
			Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);

			String fileName = "物资主数据导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
			FileOutputStream out = new FileOutputStream(errorExcelUpload+ File.separator+fileName);
			url = File.separator+"errorExcelFiles"+ File.separator+fileName;
			workbook.write(out);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return XlsUtil.importReturnRes(errorLines, successLines, errorMessage,true,url);
	}
}
