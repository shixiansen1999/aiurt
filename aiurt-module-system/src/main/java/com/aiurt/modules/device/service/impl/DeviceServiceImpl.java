package com.aiurt.modules.device.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.modules.device.Model.DeviceErrorModel;
import com.aiurt.modules.device.Model.DeviceModel;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.mapper.DeviceAssemblyMapper;
import com.aiurt.modules.device.mapper.DeviceMapper;
import com.aiurt.modules.device.mapper.DeviceTypeMapper;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.service.ICsManufactorService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.impl.CsSubsystemServiceImpl;
import com.aiurt.modules.system.mapper.SysDictMapper;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
		// 错误信息
		List<String> errorMessage = new ArrayList<>();
		int successLines = 0, errorLines = 0;
		String url = null;

		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(1);
			params.setHeadRows(1);
			params.setNeedSave(true);
			List<DeviceErrorModel> deviceErrorModels = new ArrayList<DeviceErrorModel>();
			try {
				List<DeviceModel> list = ExcelImportUtil.importExcel(file.getInputStream(), DeviceModel.class, params);
				for (DeviceModel deviceModel : list) {
					if (ObjectUtil.isNotEmpty(deviceModel)) {
						StringBuilder stringBuilder = new StringBuilder();

						String majorCode = deviceModel.getMajorCode();
						String systemCode = deviceModel.getSystemCode();
						String deviceTypeCode = deviceModel.getDeviceTypeCode();
						String code = deviceModel.getCode();
						String name = deviceModel.getName();
						String status = deviceModel.getStatus();
						if (StrUtil.isNotEmpty(majorCode)&&StrUtil.isNotEmpty(deviceTypeCode) &&StrUtil.isNotEmpty(code) && StrUtil.isNotEmpty(name)&& StrUtil.isNotEmpty(status) ) {
							JSONObject csMajor = iSysBaseAPI.getCsMajorByCode(majorCode);
							if (ObjectUtil.isNotEmpty(csMajor)) {
								QueryWrapper<DeviceType> deviceTypeQueryWrapper = new QueryWrapper<DeviceType>();
								deviceTypeQueryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
								deviceTypeQueryWrapper.eq("major_code", majorCode);

								if (ObjectUtil.isNotEmpty(systemCode)) {
									LambdaQueryWrapper<CsSubsystem> wrapper = new LambdaQueryWrapper<>();
									wrapper.eq(CsSubsystem::getSystemCode,systemCode).eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
									CsSubsystem subsystem = csSubsystemService.getOne(wrapper);
									if (ObjectUtil.isNotEmpty(subsystem)) {
										deviceTypeQueryWrapper.eq("system_code", systemCode);
									} else {
										stringBuilder.append("系统不存在该专业下的子系统，");
									}
								}
								List<DeviceType> deviceTypeList = deviceTypeService.list(deviceTypeQueryWrapper);
								if (CollUtil.isNotEmpty(deviceTypeList)) {
									List<DeviceType> typeList = deviceTypeList.stream().filter(deviceType -> deviceType.getCode().equals(deviceTypeCode)).collect(Collectors.toList());
									if (CollUtil.isNotEmpty(typeList)) {
										String codeCc = this.getCodeByCc(deviceTypeCode);
										String str = majorCode + systemCode + codeCc;
										Device device = this.getOne(new LambdaQueryWrapper<Device>().likeRight(Device::getCode, str)
												.eq(Device::getDelFlag, 0).orderByDesc(Device::getCreateTime).last("limit 1"));
										if (ObjectUtil.isEmpty(device) || device.getCode().equals(code)) {
											stringBuilder.append("系统不存在该设备类型的设备编号，");
										}
									} else {
										stringBuilder.append("系统不存在该专业子系统的设备类型，设备编号，");
									}
								} else {
									stringBuilder.append("系统不存在该专业子系统的设备类型，设备编号，");
								}

							}else {
								stringBuilder.append("系统不存在该专业，");
							}

							List<DictModel> deviceStatus = sysDictMapper.queryDictItemsByCode("device_status");
							List<DictModel> models = Optional.ofNullable(deviceStatus).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(status)).collect(Collectors.toList());
							if (CollUtil.isEmpty(models)) {
								stringBuilder.append("系统不存在该设备状态，");
							}

						}else {
							stringBuilder.append("所属专业,设备类型，设备编号，设备名称，设备状态不能为空，");
						}


						String positionCode = deviceModel.getPositionCode();
						String manageUserName = deviceModel.getManageUserName();
						String deviceLevel = deviceModel.getDeviceLevel();
						String temporary = deviceModel.getTemporary();
						if (StrUtil.isNotEmpty(positionCode) && StrUtil.isNotEmpty(manageUserName) && StrUtil.isNotEmpty(deviceLevel) && StrUtil.isNotEmpty(temporary)) {
							String position = iSysBaseAPI.getPosition(positionCode);
							if (StrUtil.isEmpty(position)) {
								stringBuilder.append("系统不存在该位置，");
							}

							String userName = iSysBaseAPI.getUserName(manageUserName);
							if (StrUtil.isEmpty(userName)) {
								stringBuilder.append("系统不存在该用户，");
							}

							List<DictModel> deviceLevels = sysDictMapper.queryDictItemsByCode("device_level");
							List<DictModel> models1 = Optional.ofNullable(deviceLevels).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(deviceLevel)).collect(Collectors.toList());
							if (CollUtil.isEmpty(models1)) {
								stringBuilder.append("系统不存在该设备等级，");
							}

							List<DictModel> temporarys = sysDictMapper.queryDictItemsByCode("device_temporary");
							List<DictModel> models2 = Optional.ofNullable(temporarys).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(temporary)).collect(Collectors.toList());
							if (CollUtil.isEmpty(models2)) {
								stringBuilder.append("系统不存在该临时设备状态，");
							}

						} else {
							stringBuilder.append("设备位置，设备管理员，设备等级，临时设备不能为空，");
						}

						String orgCode = deviceModel.getOrgCode();
						String picture = deviceModel.getPicturePath();

						String manufactorCode = deviceModel.getManufactorCode();
						String productionDate = deviceModel.getProductionDate();
						String factoryDate = deviceModel.getFactoryDate();
						String startDate = deviceModel.getStartDate();

						if (StrUtil.isNotEmpty(orgCode)) {
							SysDepartModel departByOrgCode = iSysBaseAPI.getDepartByOrgCode(orgCode);
							if (ObjectUtil.isEmpty(departByOrgCode)) {
								stringBuilder.append("系统不存在该班组，");
							}
						}

						if (StrUtil.isNotEmpty(manufactorCode)) {
							LambdaQueryWrapper<CsManufactor> wrapper = new LambdaQueryWrapper<>();
							CsManufactor csManufactor = csManufactorService.getOne(wrapper.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0).eq(CsManufactor::getCode, manufactorCode));
							if (ObjectUtil.isEmpty(csManufactor)) {
								stringBuilder.append("系统不存在该厂商，");
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

						if (stringBuilder.length() > 0) {
							// 截取字符
							stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
							deviceModel.setMistake(stringBuilder.toString());
							errorLines++;
						}
						DeviceErrorModel deviceErrorModel = new DeviceErrorModel();
						BeanUtil.copyProperties(deviceModel,deviceErrorModel);
						deviceErrorModels.add(deviceErrorModel);
					}
				}

				if (errorLines > 0) {
					Workbook wb = ExcelExportUtil.exportExcel(new ExportParams(null, "sheetName"), DeviceErrorModel.class, deviceErrorModels);
					try {
						String fileName = "设备主数据导入错误清单"+"_" + System.currentTimeMillis()+".xlsx";
						FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+fileName);
						url = fileName;
						wb.write(out);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return imporReturnRes(errorLines, successLines, errorMessage,true,url);
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
