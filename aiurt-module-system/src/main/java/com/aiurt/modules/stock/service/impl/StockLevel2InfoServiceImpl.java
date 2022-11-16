package com.aiurt.modules.stock.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.manufactor.entity.vo.CsManuFactorImportVo;
import com.aiurt.modules.stock.entity.StockLevel2Info;
import com.aiurt.modules.stock.entity.StockLevel2InfoVo;
import com.aiurt.modules.stock.mapper.StockLevel2InfoMapper;
import com.aiurt.modules.stock.service.IStockLevel2InfoService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.export.ExcelExportServer;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockLevel2InfoServiceImpl extends ServiceImpl<StockLevel2InfoMapper, StockLevel2Info> implements IStockLevel2InfoService {

	@Autowired
	private StockLevel2InfoMapper stockLevel2InfoMapper;
	@Autowired
	private SysBaseApiImpl sysBaseApi;
	@Value("${jeecg.path.upload}")
	private String upLoadPath;
	@Autowired
	private ISysBaseAPI sysBaseAPI;

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
			String type = FilenameUtils.getExtension(file.getOriginalFilename());
			if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
				return imporReturnRes(errorLines, successLines, errorMessage, false,url);
			}
			ImportParams params = new ImportParams();
			params.setTitleRows(1);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<StockLevel2InfoVo> slList = ExcelImportUtil.importExcel(file.getInputStream(), StockLevel2InfoVo.class, params);
				List<StockLevel2InfoVo> stockLevel2InfoList = slList.parallelStream()
						.filter(c->c.getWarehouseCode()!=null||c.getWarehouseName()!=null||c.getOrganizationId()!=null||c.getStatus()!=null||c.getRemark() !=null)
						.collect(Collectors.toList());

				List<StockLevel2Info> list = new ArrayList<>();
				for (int i = 0; i < stockLevel2InfoList.size(); i++) {
					StockLevel2InfoVo stockLevel2InfoVo = stockLevel2InfoList.get(i);
					boolean error = true;
					StringBuffer sb = new StringBuffer();
					if (ObjectUtil.isNull(stockLevel2InfoVo.getWarehouseCode())) {
						errorMessage.add("二级库编码为必填项，忽略导入");
						sb.append("二级库编码为必填项;");
						errorLines++;
						error = false;
					} else {
						StockLevel2Info stockLevel2Info = stockLevel2InfoMapper.selectOne(new QueryWrapper<StockLevel2Info>().lambda().eq(StockLevel2Info::getWarehouseCode, stockLevel2InfoVo.getWarehouseCode()).eq(StockLevel2Info::getDelFlag, 0));
						if (stockLevel2Info != null) {
							errorMessage.add(stockLevel2InfoVo.getWarehouseCode() + "二级库编码已经存在，忽略导入");
							sb.append("二级库编码已经存在;");
							if (error) {
								errorLines++;
								error = false;
							}
						}
					}
					if (ObjectUtil.isNull(stockLevel2InfoVo.getWarehouseName())) {
						errorMessage.add("二级库名称为必填项，忽略导入");
						sb.append("二级库名称为必填项;");
						if(error){
							errorLines++;
							error = false;
						}
					} else {
						StockLevel2Info stockLevel2Info = stockLevel2InfoMapper.selectOne(new QueryWrapper<StockLevel2Info>().lambda().eq(StockLevel2Info::getWarehouseName, stockLevel2InfoVo.getWarehouseName()).eq(StockLevel2Info::getDelFlag, 0));
						if (stockLevel2Info != null) {
							errorMessage.add(stockLevel2InfoVo.getWarehouseCode() + "二级库名称已经存在，忽略导入");
							sb.append("二级库名称已经存在;");
							if (error) {
								errorLines++;
								error =false;
							}
						}
					}
					if(ObjectUtil.isNull(stockLevel2InfoVo.getOrganizationId())){
						errorMessage.add("组织机构为必填项，忽略导入");
						sb.append("组织机构为必填项;");
						if(error){
							errorLines++;
							error = false;
						}
					}
					if (ObjectUtil.isNull(stockLevel2InfoVo.getStatus())) {
						errorMessage.add("状态为必填项，忽略导入");
						sb.append("状态为必填项;");
						if(error){
							errorLines++;
							error = false;
						}
					}
					stockLevel2InfoVo.setErrorCause(String.valueOf(sb));
					StockLevel2Info stockLevel2Info = new StockLevel2Info();
					BeanUtils.copyProperties(stockLevel2InfoVo, stockLevel2Info);
					list.add(stockLevel2Info);
					successLines++;
				}
				if (errorLines == 0) {
					for (StockLevel2Info stockLevel2Info : list) {
						stockLevel2InfoMapper.insert(stockLevel2Info);
					}
				} else {
					successLines = 0;
					//1.获取文件流
					Resource resource = new ClassPathResource("/templates/stockLevel2Info.xlsx");
					InputStream resourceAsStream = resource.getInputStream();

					//2.获取临时文件
					File fileTemp= new File("/templates/stockLevel2Info.xlsx");
					try {
						//将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
						FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
					String path = fileTemp.getAbsolutePath();
					TemplateExportParams exportParams = new TemplateExportParams(path);
					Map<String, Object> errorMap = new HashMap<String, Object>();
					errorMap.put("title", "二级仓库管理错误清单");
					List<Map<String, Object>> listMap = new ArrayList<>();
					for (StockLevel2InfoVo dto : stockLevel2InfoList) {
						//获取一条排班记录
						Map<String, Object> lm = new HashMap<String, Object>();
						//状态字典值翻译
						List<DictModel> status = sysBaseApi.getDictItems("stock_level2_info_status");
						status= status.stream().filter(f -> (String.valueOf(dto.getStatus())).equals(f.getValue())).collect(Collectors.toList());
						String statu = null;
						if(CollUtil.isNotEmpty(status)){
							 statu = status.stream().map(DictModel::getText).collect(Collectors.joining());
						}else{
							statu ="";
						}
						//组织机构字典值翻译
						String departName = null;
						if(ObjectUtil.isNotNull(dto.getOrganizationId())){
							SysDepartModel sysDepartModel = sysBaseApi.selectAllById(dto.getOrganizationId());
							 departName= sysDepartModel.getDepartName();
						}else{
							departName = dto.getOrganizationId();
						}
						//错误报告获取信息
						lm.put("WarehouseCode", dto.getWarehouseCode());
						lm.put("WarehouseName", dto.getWarehouseName());
						lm.put("OrganizationId", departName);
						lm.put("status", statu);
						lm.put("remark", dto.getRemark());
						lm.put("mistake", dto.getErrorCause());
						listMap.add(lm);
					}
					errorMap.put("maplist", listMap);
					Workbook workbook = ExcelExportUtil.exportExcel(exportParams, errorMap);
					String filename = "二级仓库管理错误清单"+"_" + System.currentTimeMillis()+"."+type;
					FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+filename);
					url =filename;
					workbook.write(out);
				}
			} catch (Exception e) {
				errorMessage.add("发生异常：" + e.getMessage());
				log.error(e.getMessage(), e);
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return imporReturnRes(errorLines, successLines, errorMessage, true,url);
	}


	public static Result<?> imporReturnRes(int errorLines,int successLines,List<String> errorMessage,boolean isType,String failReportUrl) throws IOException {
		if(isType)
		{
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
		}
		else
		{
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
