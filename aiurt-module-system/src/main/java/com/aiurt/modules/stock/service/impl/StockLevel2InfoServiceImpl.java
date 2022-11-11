package com.aiurt.modules.stock.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.stock.entity.StockLevel2Info;
import com.aiurt.modules.stock.entity.StockLevel2InfoVo;
import com.aiurt.modules.stock.mapper.StockLevel2InfoMapper;
import com.aiurt.modules.stock.service.IStockLevel2InfoService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

	@Override
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		// 错误信息
		List<String> errorMessage = new ArrayList<>();
		int successLines = 0, errorLines = 0;
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			String type = FilenameUtils.getExtension(file.getOriginalFilename());
			if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
				return imporReturnRes(errorLines, successLines, errorMessage, false);
			}
			ImportParams params = new ImportParams();
			params.setTitleRows(1);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<StockLevel2InfoVo> slList = ExcelImportUtil.importExcel(file.getInputStream(), StockLevel2InfoVo.class, params);
				List<StockLevel2InfoVo> stockLevel2InfoList = slList.stream()
						.filter(item -> existFieldNotEmpty(item))
						.collect(Collectors.toList());

				List<StockLevel2Info> list = new ArrayList<>();
				for (int i = 0; i < stockLevel2InfoList.size(); i++) {
					StockLevel2InfoVo stockLevel2InfoVo = stockLevel2InfoList.get(i);
					boolean error = true;
					if (ObjectUtil.isNull(stockLevel2InfoVo.getWarehouseCode())) {
						errorMessage.add("仓库编码为必填项，忽略导入");
						errorLines++;
						error = false;
					} else {
						StockLevel2Info stockLevel2Info = stockLevel2InfoMapper.selectOne(new QueryWrapper<StockLevel2Info>().lambda().eq(StockLevel2Info::getWarehouseCode, stockLevel2InfoVo.getWarehouseCode()).eq(StockLevel2Info::getDelFlag, 0));
						if (stockLevel2Info != null) {
							errorMessage.add(stockLevel2InfoVo.getWarehouseCode() + "仓库编码已经存在，忽略导入");
							if (error) {
								errorLines++;
								error = false;
							}
						}
					}
					if (ObjectUtil.isNull(stockLevel2InfoVo.getWarehouseName()) & ObjectUtil.isNotNull(stockLevel2InfoVo.getWarehouseCode())) {
						errorMessage.add("仓库名称为必填项，忽略导入");
						errorLines++;
					}
					if (ObjectUtil.isNull(stockLevel2InfoVo.getWarehouseName())) {
						errorMessage.add("仓库名称为必填项，忽略导入");
					} else {
						StockLevel2Info stockLevel2Info = stockLevel2InfoMapper.selectOne(new QueryWrapper<StockLevel2Info>().lambda().eq(StockLevel2Info::getWarehouseName, stockLevel2InfoVo.getWarehouseName()).eq(StockLevel2Info::getDelFlag, 0));
						if (stockLevel2Info != null) {
							errorMessage.add(stockLevel2InfoVo.getWarehouseCode() + "仓库名称已经存在，忽略导入");
							if (error) {
								errorLines++;
							}
						}
					}
					if (ObjectUtil.isNull(stockLevel2InfoVo.getOrganizationId()) & ObjectUtil.isNotNull(stockLevel2InfoVo.getWarehouseName()) & ObjectUtil.isNotNull(stockLevel2InfoVo.getWarehouseCode())) {
						errorMessage.add("组织机构ID为必填项，忽略导入");
						errorLines++;
					}
					if(ObjectUtil.isNull(stockLevel2InfoVo.getOrganizationId())){
						errorMessage.add("组织机构ID为必填项，忽略导入");
					}
					if (ObjectUtil.isNull(stockLevel2InfoVo.getStatus())& ObjectUtil.isNotNull(stockLevel2InfoVo.getOrganizationId()) & ObjectUtil.isNotNull(stockLevel2InfoVo.getWarehouseName()) & ObjectUtil.isNotNull(stockLevel2InfoVo.getWarehouseCode())) {
						errorMessage.add("状态为必填项，忽略导入");
						errorLines++;
					}
					if (ObjectUtil.isNull(stockLevel2InfoVo.getStatus())) {
						errorMessage.add("状态为必填项，忽略导入");
					}
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
		return imporReturnRes(errorLines, successLines, errorMessage, true);
	}

	/**
	 * 校验字段属性是否存在不为空字段
	 *
	 * @param
	 * @return
	 */
	private static <T> boolean existFieldNotEmpty(T t) {
		if (ObjectUtil.isEmpty(t)) {
			return false;
		}
		try {
			Field[] fields = t.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if (ObjectUtil.isNotEmpty(field.get(t))) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Result<?> imporReturnRes(int errorLines,int successLines,List<String> errorMessage,boolean isType) throws IOException {
		if(isType)
		{
			if (errorLines != 0) {
				JSONObject result = new JSONObject(5);
				result.put("isSucceed", false);
				result.put("errorCount", errorLines);
				result.put("successCount", successLines);
				int totalCount = successLines + errorLines;
				result.put("totalCount", totalCount);
				result.put("failReportUrl", "");
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
