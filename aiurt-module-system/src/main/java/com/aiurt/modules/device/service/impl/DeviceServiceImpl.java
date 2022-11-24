package com.aiurt.modules.device.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.device.Model.DeviceAssemblyErrorModel;
import com.aiurt.modules.device.Model.DeviceAssemblyModel;
import com.aiurt.modules.device.Model.DeviceModel;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.mapper.DeviceAssemblyMapper;
import com.aiurt.modules.device.mapper.DeviceMapper;
import com.aiurt.modules.device.mapper.DeviceTypeMapper;
import com.aiurt.modules.device.service.IDeviceAssemblyService;
import com.aiurt.modules.device.service.IDeviceComposeService;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.impl.CsMajorServiceImpl;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.service.ICsManufactorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.service.impl.CsLineServiceImpl;
import com.aiurt.modules.position.service.impl.CsStationPositionServiceImpl;
import com.aiurt.modules.position.service.impl.CsStationServiceImpl;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.impl.CsSubsystemServiceImpl;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.mapper.SysDictMapper;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
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

	@Autowired
	private DeviceTypeMapper deviceTypeMapper;

	@Autowired
	private ISysBaseAPI iSysBaseAPI;
	@Autowired
	private CsSubsystemServiceImpl csSubsystemService;
	@Autowired
	@Lazy
	private IDeviceTypeService deviceTypeService;
	@Autowired
	private SysDictMapper sysDictMapper;

	@Autowired
	private ICsManufactorService csManufactorService;

	@Autowired
	@Lazy
	private IDeviceAssemblyService iDeviceAssemblyService;

	@Autowired
	private IDeviceComposeService iDeviceCompostService;

	@Autowired
	private CsMajorServiceImpl csMajorService;
	@Autowired
	private CsLineServiceImpl csLineService;
	@Autowired
	private CsStationServiceImpl csStationService;
	@Autowired
	private CsStationPositionServiceImpl csStationPositionService;
	@Autowired
	private ISysDepartService sysDepartService;

	@Autowired
	private IMaterialBaseService iMaterialBaseService;

	@Value("${jeecg.path.upload}")
	private String upLoadPath;

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
		if(!"".equals(reuseType) && reuseType.contains(SymbolConstant.COMMA)){
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

	@Override
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

		List<String> errorMessage = new ArrayList<>();
		int successLines = 0;
		String url = null;
		// 错误信息
		int  errorLines = 0;
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			String type = FilenameUtils.getExtension(file.getOriginalFilename());
			if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
				return imporReturnRes(errorLines, successLines, errorMessage, false, null);
			}
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(2);
			params.setNeedSave(true);

			List<DeviceAssemblyErrorModel> deviceAssemblyErrorModels = new ArrayList<>();
			List<Device> deviceList = new ArrayList<Device>();
			try {
				List<DeviceModel> list = ExcelImportUtil.importExcel(file.getInputStream(), DeviceModel.class, params);
				Map<String, String> duplicateData = new HashMap<>();

				for (DeviceModel deviceModel : list) {
					//数据重复校验
					Device device = new Device();
					if (ObjectUtil.isNotEmpty(deviceModel)) {
						StringBuilder stringBuilder = new StringBuilder();
						//基础信息数据校验
						baseMassageCheck(deviceModel, device, stringBuilder);

						//详细信息数据校验
						detailMassageCheck(deviceModel, device, stringBuilder);

						//辅助信息数据校验
						auxiliaryMassageCheck(deviceModel, device, stringBuilder);

						//重复数据校验
						String s = duplicateData.get(deviceModel.getCode());
						if (StrUtil.isEmpty(s)) {
							duplicateData.put(deviceModel.getCode(), deviceModel.getName());
						} else {
							stringBuilder.append("该数据存在相同数据,");
						}

						QueryWrapper<Device> queryWrapper = this.getQueryWrapper(null,null,null,device.getMajorCode(),device.getSystemCode(),device.getDeviceTypeCode(),deviceModel.getCode(),deviceModel.getName(), String.valueOf(device.getStatus()));
						Device one = this.getOne(queryWrapper);
						if (ObjectUtil.isNotEmpty(one)) {
							stringBuilder.append("数据库已存在该数据,");
						}

						//组件数据校验
						List<DeviceAssemblyModel> deviceAssemblyModels = deviceAssemblyCheck(deviceModel, errorLines);

						if (stringBuilder.length() > 0) {
							// 截取字符
							stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
							deviceModel.setDeviceMistake(stringBuilder.toString());
							errorLines++;
						}
						List<DeviceAssembly> deviceAssemblies = new ArrayList<>();
						if (CollUtil.isNotEmpty(deviceAssemblyModels)) {
							for (DeviceAssemblyModel deviceAssemblyModel : deviceAssemblyModels) {
								if (errorLines > 0) {
									//生成错误信息
									DeviceAssemblyErrorModel deviceAssemblyErrorModel = new DeviceAssemblyErrorModel();
									BeanUtil.copyProperties(deviceModel, deviceAssemblyErrorModel);
									BeanUtil.copyProperties(deviceAssemblyModel, deviceAssemblyErrorModel);
									deviceAssemblyErrorModels.add(deviceAssemblyErrorModel);
								} else {
									//生成添加的组件信息
									DeviceAssembly deviceAssembly = new DeviceAssembly();
									BeanUtil.copyProperties(deviceAssemblyModel, deviceAssembly);
									deviceAssembly.setCode(deviceAssemblyModel.getAssemblyCode());
									deviceAssembly.setStatus(deviceAssemblyModel.getAssemblyStatus());
									deviceAssemblies.add(deviceAssembly);
								}
							}
						}
						if (errorLines == 0) {
							//生成添加的设备信息
							String[] str = {"status","temporary","deviceLevel","deviceTypeCodeCc"};
							BeanUtil.copyProperties(deviceModel,device,str);
							device.setDeviceAssemblyList(deviceAssemblies);
							deviceList.add(device);
						}
					}
				}
				if (errorLines > 0) {
					//错误报告下载
					return getErrorExcel(errorLines,list,deviceAssemblyErrorModels,errorMessage,successLines,url, type);
				}

				for (Device device : deviceList) {
					this.add(device);
					List<DeviceAssembly> deviceAssemblyList = device.getDeviceAssemblyList();
					if (CollUtil.isNotEmpty(deviceAssemblyList)) {
						iDeviceAssemblyService.saveBatch(deviceAssemblyList);
					}
				}
				return Result.ok("文件导入成功！");

			} catch (Exception e) {
				String msg = e.getMessage();
				log.error(msg, e);
				if(msg!=null && msg.contains("Duplicate entry")){
					return Result.error("文件导入失败:有重复数据！");
				}else{
					return Result.error("文件导入失败:" + e.getMessage());
				}
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return Result.ok("文件导入失败！");
	}

	@Override
	public Result<Device> add(Device device){
		Result<Device> result = new Result<Device>();
		try {
			String deviceTypeCodeCc = device.getDeviceTypeCodeCc()==null?"":device.getDeviceTypeCodeCc();
			String deviceTypeCode = this.getCodeByCc(deviceTypeCodeCc);
			device.setDeviceTypeCode(deviceTypeCode);
			String positionCodeCc = device.getPositionCodeCc()==null?"":device.getPositionCodeCc();
			if(!"".equals(positionCodeCc)){
				if(positionCodeCc.contains(CommonConstant.SYSTEM_SPLIT_STR)){
					String[] split = positionCodeCc.split(CommonConstant.SYSTEM_SPLIT_STR);
					int length = split.length;
					switch (length){
						case 2:
							device.setLineCode(split[0]);
							device.setStationCode(split[1]);
							break;
						case 3:
							device.setLineCode(split[0]);
							device.setStationCode(split[1]);
							device.setPositionCode(split[2]);
							break;
						default:
							device.setLineCode(positionCodeCc);
							device.setStationCode("");
							device.setPositionCode("");
					}
				}else{
					device.setLineCode(positionCodeCc);
				}
			}
			this.save(device);
			List<DeviceCompose> deviceComposeList = iDeviceCompostService.list(new QueryWrapper<DeviceCompose>().eq("device_type_code",deviceTypeCode));
			if(deviceComposeList != null && deviceComposeList.size()>0){
				for(DeviceCompose deviceCompose : deviceComposeList){
					DeviceAssembly deviceAssemblyOld = iDeviceAssemblyService.getOne(new LambdaQueryWrapper<DeviceAssembly>().likeRight(DeviceAssembly::getCode, deviceCompose.getMaterialCode())
							.eq(DeviceAssembly::getDelFlag, 0).orderByDesc(DeviceAssembly::getCreateTime).last("limit 1"));
					String code = deviceCompose.getMaterialCode();
					String format = "";
					if(deviceAssemblyOld != null){
						String codeold = deviceAssemblyOld.getCode();
						String numstr = codeold.substring(codeold.length()-3);
						format = String.format("%03d", Long.parseLong(numstr) + 1);
					}else{
						format = "001";
					}
					DeviceAssembly deviceAssembly = new DeviceAssembly();
					deviceAssembly.setDeviceCode(device.getCode());
					deviceAssembly.setMaterialCode(deviceCompose.getMaterialCode());
					deviceAssembly.setCode(code + format);
					deviceAssembly.setMaterialName(deviceCompose.getMaterialName());
					deviceAssembly.setBaseTypeCode(deviceCompose.getBaseTypeCode());
					deviceAssembly.setSpecifications(deviceCompose.getSpecifications());
					deviceAssembly.setUnit(deviceCompose.getUnit()==null?"":deviceCompose.getUnit());
					deviceAssembly.setManufactorCode(deviceCompose.getManufacturer()==null?"":deviceCompose.getManufacturer());
					deviceAssembly.setPrice(deviceCompose.getPrice()==null?null:deviceCompose.getPrice().toString());
					deviceAssembly.setDeviceTypeCode(deviceCompose.getDeviceTypeCode());
					iDeviceAssemblyService.save(deviceAssembly);
				}
			}
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

	@Override
	public void exportXls(Device device, HttpServletRequest request, HttpServletResponse response) {
		// Step.1 组装查询条件
		QueryWrapper<Device> queryWrapper = QueryGenerator.initQueryWrapper(device, request.getParameterMap());
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		// Step.2 获取导出数据
		List<Device> pageList = this.list(queryWrapper);

		List<Device> exportList = null;
		List<Device> deviceList = new ArrayList<>();

		// 过滤选中数据
		String selections = request.getParameter("selections");
		if (oConvertUtils.isNotEmpty(selections)) {
			List<String> selectionList = Arrays.asList(selections.split(","));
			exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		} else {
			exportList = pageList;
		}

		if (CollUtil.isNotEmpty(exportList)) {
			for (Device d : exportList) {
				Device devicefinal = translate(d);
				//设备组件
				List<DeviceAssembly> deviceAssemblyList = deviceAssemblyMapper.selectList(new QueryWrapper<DeviceAssembly>().eq("device_code", d.getCode()));
				for(DeviceAssembly deviceAssembly : deviceAssemblyList){
					String statusAssembly = deviceAssembly.getStatus()==null?"":deviceAssembly.getStatus();
					String baseTypeCode = deviceAssembly.getBaseTypeCode()==null?"":deviceAssembly.getBaseTypeCode();
					deviceAssembly.setStatusName(sysBaseApi.translateDict("device_assembly_status",statusAssembly)==null?"":sysBaseApi.translateDict("device_assembly_status",statusAssembly));
					deviceAssembly.setBaseTypeCodeName(sysBaseApi.translateDictFromTable("material_base_type", "base_type_name", "base_type_code", baseTypeCode));
				}
				devicefinal.setDeviceAssemblyList(deviceAssemblyList);
				deviceList.add(devicefinal);
			}
		}
		String title = "设备主数据";
		ExportParams exportParams=new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), ExcelType.XSSF);
		//调用ExcelExportUtil.exportExcel方法生成workbook
		Workbook wb = ExcelExportUtil.exportExcel(exportParams,Device.class,deviceList);
		String fileName = "设备主数据";
		try {
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
			//xlsx格式设置
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
			wb.write(bufferedOutPut);
			bufferedOutPut.flush();
			bufferedOutPut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void baseMassageCheck(DeviceModel deviceModel,Device device,StringBuilder stringBuilder) {
		String majorCodeName = deviceModel.getMajorCodeName();
		String systemCodeName = deviceModel.getSystemCodeName();
		String deviceTypeCodeName = deviceModel.getDeviceTypeCodeName();
		String code = deviceModel.getCode();
		String name = deviceModel.getName();
		String status = deviceModel.getStatus();
		if (StrUtil.isNotEmpty(majorCodeName) && StrUtil.isNotEmpty(systemCodeName) && StrUtil.isNotEmpty(deviceTypeCodeName) && StrUtil.isNotEmpty(code) && StrUtil.isNotEmpty(name) && StrUtil.isNotEmpty(status)) {
			LambdaQueryWrapper<CsMajor> csMajorWrapper = new LambdaQueryWrapper<>();
			csMajorWrapper.eq(CsMajor::getMajorName, majorCodeName).eq(CsMajor::getDelFlag, 0);
			CsMajor major = csMajorService.getOne(csMajorWrapper);
			if (ObjectUtil.isNotEmpty(major)) {
				QueryWrapper<DeviceType> deviceTypeQueryWrapper = new QueryWrapper<DeviceType>();
				deviceTypeQueryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
				deviceTypeQueryWrapper.eq("major_code", major.getMajorCode());
				device.setMajorCode(major.getMajorCode());

				LambdaQueryWrapper<CsSubsystem> wrapper = new LambdaQueryWrapper<>();
				wrapper.eq(CsSubsystem::getSystemName, systemCodeName).eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
				CsSubsystem subsystem = csSubsystemService.getOne(wrapper);
				if (ObjectUtil.isNotEmpty(subsystem)) {
					device.setSystemCode(subsystem.getSystemCode());
					deviceTypeQueryWrapper.eq("system_code", subsystem.getSystemCode());
				} else {
					stringBuilder.append("系统不存在该专业下的子系统，");
				}

				List<DeviceType> deviceTypeList = deviceTypeService.list(deviceTypeQueryWrapper);
				if (CollUtil.isNotEmpty(deviceTypeList)) {
					DeviceType type = deviceTypeList.stream().filter(deviceType -> deviceType.getName().equals(deviceTypeCodeName)).findFirst().orElse(null);
					if (type != null) {
						device.setDeviceTypeCode(type.getCode());
						String codeCc = this.getCodeByCc(type.getCode());
						String str = major.getMajorCode() + subsystem.getSystemCode() + codeCc;
						Device device1 = this.getOne(new LambdaQueryWrapper<Device>().likeRight(Device::getCode, str)
								.eq(Device::getDelFlag, 0).orderByDesc(Device::getCreateTime).last("limit 1"));
						String format = "";
						if (device1 != null) {
							String code1 = device1.getCode();
							String numstr = code1.substring(code1.length() - 5);
							format = String.format("%05d", Long.parseLong(numstr) + 1);
						} else {
							format = "00001";
						}
						String deviceCode = str + format;
						if (!deviceCode.equals(code)) {
							stringBuilder.append("该设备类型的设备编号不符合规范，");
						}
						device.setDeviceTypeCodeCc(type.getCodeCc());
					} else {
						stringBuilder.append("系统不存在该专业子系统的设备类型，设备编号，");
					}
				} else {
					stringBuilder.append("系统不存在该专业子系统的设备类型，设备编号，");
				}

			} else {
				stringBuilder.append("系统不存在该专业，");
			}

			List<DictModel> deviceStatus = sysDictMapper.queryDictItemsByCode("device_status");
			DictModel model = Optional.ofNullable(deviceStatus).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(status)).findFirst().orElse(null);
			if (model != null) {
				device.setStatus(Convert.toInt(model.getValue()));
			} else {
				stringBuilder.append("系统不存在该设备状态，");

			}

		} else {
			stringBuilder.append("所属专业，子系统，设备类型，设备编号，设备名称，设备状态不能为空，");
		}
	}

	private void detailMassageCheck(DeviceModel deviceModel,Device device,StringBuilder stringBuilder) {
		String lineCodeName = deviceModel.getLineCodeName();
		String stationCodeName = deviceModel.getStationCodeName();
		String positionCodeName = deviceModel.getPositionCodeName();
		String manageUserName = deviceModel.getManageUserName();
		String deviceLevel = deviceModel.getDeviceLevel();
		String temporary = deviceModel.getTemporary();
		if (StrUtil.isNotEmpty(lineCodeName)) {
			LambdaQueryWrapper<CsLine> lineWrapper = new LambdaQueryWrapper<>();
			lineWrapper.eq(CsLine::getLineName, lineCodeName).eq(CsLine::getDelFlag, 0);
			CsLine one = csLineService.getOne(lineWrapper);
			if (ObjectUtil.isEmpty(one)) {
				stringBuilder.append("系统不存在该线路，");
			} else {
				device.setLineCode(one.getLineCode());
			}
		}else {
			stringBuilder.append("设备位置不能为空，");
		}
		if (StrUtil.isNotEmpty(stationCodeName)) {
			LambdaQueryWrapper<CsStation> csStationWrapper = new LambdaQueryWrapper<>();
			csStationWrapper.eq(CsStation::getStationName, stationCodeName).eq(CsStation::getDelFlag, 0);
			CsStation one = csStationService.getOne(csStationWrapper);
			if (ObjectUtil.isEmpty(one)) {
				stringBuilder.append("系统不存在该站点，");
			} else {
				device.setStationCode(one.getStationCode());
			}
		}
		if (StrUtil.isNotEmpty(positionCodeName)) {
			LambdaQueryWrapper<CsStationPosition> positionWrapper = new LambdaQueryWrapper<>();
			positionWrapper.eq(CsStationPosition::getPositionName, positionCodeName).eq(CsStationPosition::getDelFlag, 0);
			CsStationPosition one = csStationPositionService.getOne(positionWrapper);
			if (ObjectUtil.isEmpty(one)) {
				stringBuilder.append("系统不存在该位置，");
			} else {
				device.setPositionCode(one.getPositionCode());
			}
		}
		if (StrUtil.isNotEmpty(manageUserName) && StrUtil.isNotEmpty(deviceLevel) && StrUtil.isNotEmpty(temporary)) {
			LoginUser loginUser = iSysBaseAPI.queryUser(manageUserName);
			if (ObjectUtil.isEmpty(loginUser)) {
				stringBuilder.append("系统不存在该用户，");
			}
			List<DictModel> deviceLevels = sysDictMapper.queryDictItemsByCode("device_level");
			DictModel levelmodel = Optional.ofNullable(deviceLevels).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(deviceLevel)).findFirst().orElse(null);
			if (levelmodel != null) {
				device.setDeviceLevel(levelmodel.getValue());
			} else {
				stringBuilder.append("系统不存在该设备等级，");

			}

			List<DictModel> temporarys = sysDictMapper.queryDictItemsByCode("device_temporary");
			DictModel model = Optional.ofNullable(temporarys).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(temporary)).findFirst().orElse(null);
			if (model != null) {
				device.setTemporary(model.getValue());
			} else {
				stringBuilder.append("系统不存在该临时设备状态，");

			}

		} else {
			stringBuilder.append("设备管理员，设备等级，临时设备不能为空，");
		}

		String orgCodeName = deviceModel.getOrgCodeName();
		String reuseType = deviceModel.getReuseType();

		if (StrUtil.isNotEmpty(orgCodeName)) {
			LambdaQueryWrapper<SysDepart> departWrapper = new LambdaQueryWrapper<>();
			departWrapper.eq(SysDepart::getDepartName, orgCodeName).eq(SysDepart::getDelFlag, 0);
			SysDepart one = sysDepartService.getOne(departWrapper);
			if (ObjectUtil.isEmpty(one)) {
				stringBuilder.append("系统不存在该班组，");
			} else {
				device.setOrgCode(one.getOrgCode());
			}
		}
		if (StrUtil.isNotEmpty(reuseType)) {
			List<DictModel> deviceLevels = sysDictMapper.queryDictItemsByCode("device_reuse_type");
			DictModel reuseTypeModel = Optional.ofNullable(deviceLevels).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(reuseType)).findFirst().orElse(null);
			if (reuseTypeModel != null) {
				device.setDeviceLevel(reuseTypeModel.getValue());
			} else {
				stringBuilder.append("系统不存在该设备复用类型，");
			}
		}
	}

	private void auxiliaryMassageCheck(DeviceModel deviceModel,Device device,StringBuilder stringBuilder) {
		String manufactorCodeName = deviceModel.getManufactorCodeName();
		String productionDate = deviceModel.getProductionDate();
		String factoryDate = deviceModel.getFactoryDate();
		String startDate = deviceModel.getStartDate();


		if (StrUtil.isNotEmpty(manufactorCodeName)) {
			LambdaQueryWrapper<CsManufactor> wrapper = new LambdaQueryWrapper<>();
			CsManufactor csManufactor = csManufactorService.getOne(wrapper.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0).eq(CsManufactor::getName, manufactorCodeName));
			if (ObjectUtil.isEmpty(csManufactor)) {
				stringBuilder.append("系统不存在该厂商，");
			} else {
				device.setManufactorCode(csManufactor.getCode());
			}
		}

		if (ObjectUtil.isNotEmpty(productionDate)) {
			boolean legalDate = isLegalDate(productionDate);
			if (!legalDate) {
				stringBuilder.append("生产日期格式错误，");
			}
		}
		if (ObjectUtil.isNotEmpty(factoryDate)) {
			boolean legalDate = isLegalDate(productionDate);
			if (!legalDate) {
				stringBuilder.append("出厂日期格式错误，");
			}
		}
		if (ObjectUtil.isNotEmpty(startDate)) {
			boolean legalDate = isLegalDate(productionDate);
			if (!legalDate) {
				stringBuilder.append("开始使用日期格式错误，");
			}
		}
	}

	private Result<?> getErrorExcel(int errorLines,List<DeviceModel> list,List<DeviceAssemblyErrorModel> deviceAssemblyErrorModels,List<String> errorMessage,int successLines ,String url,String type) throws IOException {
		//创建导入失败错误报告,进行模板导出
		Resource resource = new ClassPathResource("/templates/deviceError.xlsx");
		InputStream resourceAsStream = resource.getInputStream();

		//2.获取临时文件
		File fileTemp= new File("/templates/deviceError.xlsx");
		try {
			//将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
			FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		String path = fileTemp.getAbsolutePath();
		TemplateExportParams exportParams = new TemplateExportParams(path);
		Map<String, Object> errorMap = new HashMap<String, Object>();
		List<Map<String, String>> listMap = new ArrayList<>();
		for (int i = 0; i < deviceAssemblyErrorModels.size(); i++) {
			DeviceAssemblyErrorModel deviceAssemblyErrorModel = deviceAssemblyErrorModels.get(i);
			Map<String, String> lm = new HashMap<>();
			//错误报告获取信息
			lm.put("majorCodeName",deviceAssemblyErrorModel.getMajorCodeName());
			lm.put("systemCodeName",deviceAssemblyErrorModel.getSystemCodeName());
			lm.put("deviceTypeCodeName",deviceAssemblyErrorModel.getDeviceTypeCodeName());
			lm.put("code",deviceAssemblyErrorModel.getCode());
			lm.put("majorConamedeName",deviceAssemblyErrorModel.getName());
			lm.put("status",deviceAssemblyErrorModel.getStatus());
			lm.put("lineCodeName",deviceAssemblyErrorModel.getLineCodeName());
			lm.put("stationCodeName",deviceAssemblyErrorModel.getStationCodeName());
			lm.put("orgCodeName",deviceAssemblyErrorModel.getPositionCodeName());
			lm.put("manageUserName",deviceAssemblyErrorModel.getManageUserName());
			lm.put("deviceLevel",deviceAssemblyErrorModel.getDeviceLevel());
			lm.put("temporary",deviceAssemblyErrorModel.getTemporary());

			lm.put("baseTypeCodeName",deviceAssemblyErrorModel.getBaseTypeCodeName());
			lm.put("assemblyStatus",deviceAssemblyErrorModel.getAssemblyStatus());
			lm.put("assemblyCode",deviceAssemblyErrorModel.getAssemblyCode());
			lm.put("materialName",deviceAssemblyErrorModel.getMaterialName());
			lm.put("materialCode",deviceAssemblyErrorModel.getMaterialCode());
			lm.put("mistake",deviceAssemblyErrorModel.getMistake());
			lm.put("deviceMistake",deviceAssemblyErrorModel.getDeviceMistake());
			listMap.add(lm);
		}
		errorMap.put("maplist", listMap);
		Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
		sheetsMap.put(0, errorMap);
		Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);
		int size = 4;
		for (DeviceModel deviceModel : list) {
			for (int i = 0; i <= 12; i++) {
				//合并单元格
				PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0),size,size + deviceModel.getDeviceAssemblyModelList().size()-1,i,i);
			}
			PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0),size,size + deviceModel.getDeviceAssemblyModelList().size()-1,19,19);
			size = size + deviceModel.getDeviceAssemblyModelList().size();
		}

		try {
			String fileName = "设备主数据导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
			FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+fileName);
			url = fileName;
			workbook.write(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imporReturnRes(errorLines, successLines, errorMessage,true,url);
	}

	private List<DeviceAssemblyModel> deviceAssemblyCheck(DeviceModel deviceModel,int errorLines) {
		List<DeviceAssemblyModel> deviceAssemblyList = deviceModel.getDeviceAssemblyModelList();
		if (CollUtil.isNotEmpty(deviceAssemblyList)) {
			Map<Object, Integer> duplicateData = new HashMap<>();
			int i = 0;
			for (DeviceAssemblyModel deviceAssembly : deviceAssemblyList) {
				StringBuilder stringBuilder = new StringBuilder();

				String statusName = deviceAssembly.getStatusName();
				String baseTypeCodeName = deviceAssembly.getBaseTypeCodeName();
				String code = deviceAssembly.getAssemblyCode();
				String materialName = deviceAssembly.getMaterialName();
				String materialCode = deviceAssembly.getMaterialCode();

				if (StrUtil.isNotEmpty(statusName) && StrUtil.isNotEmpty(baseTypeCodeName) && StrUtil.isNotEmpty(code) && StrUtil.isNotEmpty(materialName) && StrUtil.isNotEmpty(materialCode)) {
					QueryWrapper<MaterialBase> queryWrapper = new QueryWrapper<>();
					queryWrapper.eq("code", deviceAssembly.getMaterialCode());
					queryWrapper.like("name", deviceAssembly.getMaterialName());
					MaterialBase one = iMaterialBaseService.getOne(queryWrapper);


					if (ObjectUtil.isEmpty(one)) {
						stringBuilder.append("系统不存在该组件,");
					} else {
						deviceAssembly.setSpecifications(one.getSpecifications());
						deviceAssembly.setBaseTypeCode(one.getBaseTypeCode());
						deviceAssembly.setDeviceTypeCode(one.getBaseTypeCode());
					}

					List<DictModel> deviceStatus = sysDictMapper.queryDictItemsByCode("device_assembly_status");
					DictModel model = Optional.ofNullable(deviceStatus).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(statusName)).findFirst().orElse(null);
					if (model != null) {
						deviceAssembly.setAssemblyStatus(model.getValue());
					} else {
						stringBuilder.append("系统不存在该组件状态，");
					}
				} else {
					stringBuilder.append("组件信息不能为空,");
				}

				//重复数据校验
				Integer s = duplicateData.get(deviceAssembly);
				if (s == null) {
					duplicateData.put(deviceAssembly, i);
				} else {
					stringBuilder.append("该数据存在相同数据,");
				}

				if (stringBuilder.length() > 0) {
					// 截取字符
					stringBuilder.deleteCharAt(stringBuilder.length() - 1);
					deviceAssembly.setMistake(stringBuilder.toString());
					errorLines++;
				}
				deviceAssembly.setDeviceCode(deviceModel.getCode());
				i++;
			}
		}
		return deviceAssemblyList;
	}
	/**
	 * 判断时间格式 格式必须为“YYYY-MM-dd”
	 * 2004-2-30 是无效的
	 * 2003-2-29 是无效的
	 * @param sDate
	 * @return
	 */
	private static boolean isLegalDate(String sDate) {
		int legalLen = 10;
		if ((sDate == null) || (sDate.length() != legalLen)) {
			return false;
		}

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = formatter.parse(sDate);
			return sDate.equals(formatter.format(date));
		} catch (Exception e) {
			return false;
		}
	}

	public static Result<?> imporReturnRes(int errorLines, int successLines, List<String> errorMessage, boolean isType,String failReportUrl ) throws IOException {
		if (isType) {
			if (errorLines != 0) {
				JSONObject result = new JSONObject(5);
				result.put("isSucceed", false);
				result.put("errorCount", errorLines);
				result.put("successCount", successLines);
				int totalCount = successLines + errorLines;
				result.put("totalCount", totalCount);
				result.put("failReportUrl", failReportUrl);
				Result res = Result.ok(result);
				res.setMessage("文件失败，数据有错误。");
				res.setCode(200);
				return res;
			} else {
				//是否成功
				JSONObject result = new JSONObject(5);
				result.put("isSucceed", true);
				result.put("errorCount", errorLines);
				result.put("successCount", successLines);
				int totalCount = successLines + errorLines;
				result.put("totalCount", totalCount);
				Result res = Result.ok(result);
				res.setMessage("文件导入成功！");
				res.setCode(200);
				return res;
			}
		} else {
			JSONObject result = new JSONObject(5);
			result.put("isSucceed", false);
			result.put("errorCount", errorLines);
			result.put("successCount", successLines);
			int totalCount = successLines + errorLines;
			result.put("totalCount", totalCount);
			Result res = Result.ok(result);
			res.setMessage("导入失败，文件类型不对。");
			res.setCode(200);
			return res;
		}

	}

}
