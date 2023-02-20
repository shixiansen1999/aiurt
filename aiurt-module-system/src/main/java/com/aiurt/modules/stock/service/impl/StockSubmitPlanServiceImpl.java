package com.aiurt.modules.stock.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.common.util.XlsExport;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.entity.StockSubmitMaterials;
import com.aiurt.modules.stock.entity.StockSubmitPlan;
import com.aiurt.modules.stock.mapper.StockSubmitPlanMapper;
import com.aiurt.modules.stock.service.IStockSubmitMaterialsService;
import com.aiurt.modules.stock.service.IStockSubmitPlanService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockSubmitPlanServiceImpl extends ServiceImpl<StockSubmitPlanMapper, StockSubmitPlan> implements IStockSubmitPlanService {

	@Autowired
	private StockSubmitPlanMapper stockSubmitPlanMapper;
	@Autowired
	private IStockSubmitMaterialsService stockSubmitMaterialsService;
	@Autowired
	private IMaterialBaseService materialBaseService;
	@Autowired
	private SysBaseApiImpl sysBaseApi;

	@Override
	public StockSubmitPlan getSubmitPlanCode() throws ParseException {
		QueryWrapper<StockSubmitPlan> queryWrapper = new QueryWrapper<>();
		String str = "TBJH";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		str += sdf.format(new Date());
		queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		queryWrapper.likeRight("code",str);
		queryWrapper.orderByDesc("create_time");
		queryWrapper.last("limit 1");
		StockSubmitPlan stockSubmitPlan = stockSubmitPlanMapper.selectOne(queryWrapper);
		String format = "";
		if(stockSubmitPlan != null){
			String code = stockSubmitPlan.getCode();
			String numstr = code.substring(code.length()-3);
			format = String.format("%03d", Long.parseLong(numstr) + 1);
		}else{
			format = "001";
		}
		StockSubmitPlan stockSubmitPlanres = new StockSubmitPlan();
		stockSubmitPlanres.setCode(str + format);
		return stockSubmitPlanres;
	}

	@Override
	public void add(StockSubmitPlan stockSubmitPlan) {
		String userId = stockSubmitPlan.getUserId();
		String[] ids = new String[1];
		ids[0] = userId;
		List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(ids);
		if(loginUsers != null && loginUsers.size()>0){
			String orgId = loginUsers.get(0).getOrgId();
			stockSubmitPlan.setOrgId(orgId);
			stockSubmitPlan.setOrgCode(loginUsers.get(0).getOrgCode());
		}
		this.save(stockSubmitPlan);
		List<StockSubmitMaterials> stockSubmitMaterialsList = stockSubmitPlan.getStockSubmitMaterialsList();
		if(stockSubmitMaterialsList != null && stockSubmitMaterialsList.size()>0){
			stockSubmitMaterialsList.stream().forEach(s ->s.setSubmitPlanCode(stockSubmitPlan.getCode()));
			stockSubmitMaterialsService.saveBatch(stockSubmitMaterialsList);
		}
	}

	@Override
	public boolean edit(StockSubmitPlan stockSubmitPlan) {
		String code = stockSubmitPlan.getCode();
		QueryWrapper<StockSubmitMaterials> queryWrapper = new QueryWrapper<StockSubmitMaterials>();
		queryWrapper.eq("submit_plan_code",code);
		stockSubmitMaterialsService.remove(queryWrapper);
		List<StockSubmitMaterials> stockSubmitMaterialsList = stockSubmitPlan.getStockSubmitMaterialsList();
		if(stockSubmitMaterialsList != null && stockSubmitMaterialsList.size()>0){
			stockSubmitMaterialsList.stream().forEach(s ->s.setSubmitPlanCode(stockSubmitPlan.getCode()));
			stockSubmitMaterialsService.saveBatch(stockSubmitMaterialsList);
		}
		boolean ok = this.updateById(stockSubmitPlan);
		return ok;
	}

	@Override
	public void eqExport(String ids, StockSubmitPlan submitPlan, HttpServletRequest request, HttpServletResponse response) {

		// 过滤选中数据
		QueryWrapper<StockSubmitPlan> wrapper = QueryGenerator.initQueryWrapper(submitPlan, request.getParameterMap());
		if(ObjectUtil.isNotEmpty(submitPlan)&& StrUtil.isNotBlank(submitPlan.getOrgCode())) {
			wrapper.lambda().eq(StockSubmitPlan::getOrgCode, submitPlan.getOrgCode());
		}
		if (StrUtil.isNotEmpty(ids)) {
			String[] split = ids.split(",");
			List<String> strings = Arrays.asList(split);
			wrapper.lambda().in(StockSubmitPlan::getId, strings);
		}
		wrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		wrapper.orderByDesc("create_time");
		List<StockSubmitPlan> list = this.list(wrapper);
		//设置相应头
		response.setContentType("Application/excel");
		try {
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String("物资提报数据导出".getBytes("GBK"), "ISO8859_1")
					+ ".xls");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//读取excel模板
		XlsExport excel = new XlsExport();
		HSSFWorkbook hbook = excel.getWorkbook();
		HSSFCellStyle hstyle = hbook.createCellStyle();
		hstyle.setAlignment(HorizontalAlignment.CENTER);
		hstyle.setVerticalAlignment(VerticalAlignment.CENTER);
		//设置边框样式
		int rowIndex = 0;
		if(list != null && list.size()>0){
			for(StockSubmitPlan stockSubmitPlan : list){
				String code = stockSubmitPlan.getCode();
				QueryWrapper<StockSubmitMaterials> queryWrapper = new QueryWrapper<StockSubmitMaterials>();
				queryWrapper.eq("submit_plan_code",code).eq("del_flag", CommonConstant.DEL_FLAG_0);
				List<StockSubmitMaterials> materials = stockSubmitMaterialsService.list(queryWrapper);
				stockSubmitPlan.setStockSubmitMaterialsList(materials);
				String tbrid = stockSubmitPlan.getUserId()==null?"":stockSubmitPlan.getUserId();
				String tbrname = sysBaseApi.translateDictFromTable("sys_user", "realname", "id", tbrid);
				String typecode = stockSubmitPlan.getSubmitType()==null?"":stockSubmitPlan.getSubmitType();
				String typename = sysBaseApi.translateDict("stock_submit_plan_submit_type",typecode);
				String statuscode = stockSubmitPlan.getStatus()==null?"":stockSubmitPlan.getStatus().toString();
				String statusname = sysBaseApi.translateDict("stock_submit_plan_status",statuscode);
				excel.createRow(rowIndex++);
				excel.setCell(0, "基本信息");
				int num = 11;
				for(int i = 1; i<num; i++){
					excel.setCell(i, "");
				}
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "提报计划编号");
				excel.setCell(2, stockSubmitPlan.getCode()==null?"":stockSubmitPlan.getCode());
				excel.setCell(3, "");
				excel.setCell(4, "提报时间");
				Date submitTime = stockSubmitPlan.getSubmitTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				excel.setCell(5, submitTime==null?"":sdf.format(submitTime));
				excel.setCell(6, "");
				excel.setCell(7, "填报人");
				excel.setCell(8, tbrname==null?"":tbrname);
				excel.setCell(9, "");
				excel.setCell(10, "");
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "年份");
				excel.setCell(2, stockSubmitPlan.getYear()==null?"":stockSubmitPlan.getYear().toString());
				excel.setCell(3, "");
				excel.setCell(4, "提报类型");
				excel.setCell(5, typename==null?"":typename);
				excel.setCell(6, "");
				excel.setCell(7, "填报计划状态");
				excel.setCell(8, statusname==null?"":statusname);
				excel.setCell(9, "");
				excel.setCell(10, "");
				excel.createRow(rowIndex++);
				excel.setCell(0, "提报物资清单");
				for(int i = 1; i<num; i++){
					excel.setCell(i, "");
				}
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "所属专业");
				excel.setCell(2, "所属子系统");
				excel.setCell(3, "物资分类");
				excel.setCell(4, "物资编码");
				excel.setCell(5, "物资名称");
				excel.setCell(6, "物资类型");
				excel.setCell(7, "计划请购数量");
				excel.setCell(8, "单位");
				excel.setCell(9, "参考单价");
				excel.setCell(10, "参考总价");
				if(materials != null && materials.size()>0){
					for(StockSubmitMaterials stockSubmitMaterials : materials){
						String wzcode = stockSubmitMaterials.getMaterialsCode()==null?"":stockSubmitMaterials.getMaterialsCode();
						MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",wzcode));
						materialBase = materialBaseService.translate(materialBase);
						if (ObjectUtil.isEmpty(materialBase)){
							materialBase = new MaterialBase();
						}
						String zyname = sysBaseApi.translateDictFromTable("cs_major", "major_name", "major_code", materialBase.getMajorCode());
						String zxyname = sysBaseApi.translateDictFromTable("cs_subsystem", "system_name", "system_code", materialBase.getSystemCode());
						String wztype = materialBase.getType()==null?"":materialBase.getType().toString();
						String wztypename = sysBaseApi.translateDict("material_type",wztype);
						String unitcode = materialBase.getUnit()==null?"":materialBase.getUnit();
						String unitname = sysBaseApi.translateDict("materian_unit",unitcode);
						excel.createRow(rowIndex++);
						excel.setCell(0, "");
						excel.setCell(1, zyname==null?"":zyname);
						excel.setCell(2, zxyname==null?"":zxyname);
						excel.setCell(3, materialBase.getBaseTypeCodeCcName()==null?"":materialBase.getBaseTypeCodeCcName());
						excel.setCell(4, materialBase.getCode());
						excel.setCell(5, materialBase.getName());
						excel.setCell(6, wztypename==null?"":wztypename);
						excel.setCell(7, stockSubmitMaterials.getPlanBuyNumber()==null?"":stockSubmitMaterials.getPlanBuyNumber().toString());
						excel.setCell(8, unitname==null?"":unitname);
						excel.setCell(9, stockSubmitMaterials.getPrice()==null?"":stockSubmitMaterials.getPrice());
						excel.setCell(10, stockSubmitMaterials.getTotalPrices()==null?"":stockSubmitMaterials.getTotalPrices());
					}
				}
				excel.createRow(rowIndex++);
			}
		}
		excel.exportXls(response);
	}

	@Override
	public Result importExcel(MultipartFile file, ImportParams params) throws Exception {
		// 去掉 sql 中的重复数据
		Integer errorLines=0;
		Integer successLines=0;
		List<String> errorStrs = new ArrayList<>();
		try {
			InputStream inputStream = file.getInputStream();
			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			XSSFSheet sheetAt = wb.getSheetAt(0);
			int index = 0;
			boolean ifImport = true;
			boolean ifAdd = true;
			StockSubmitPlan stockSubmitPlan = new StockSubmitPlan();
			List<StockSubmitMaterials> stockSubmitMaterialsList = new ArrayList<>();
			for (Row row : sheetAt) {
				// 4.读取每一行的单元格
				if (index == 0 || index == 1 || index == 3 || index == 4) {
					index++;
					continue;
				}
				if(index == 2){
					try {
						int year = (int)row.getCell(2).getNumericCellValue();
						if(!"".equals(year)){
							if(String.valueOf(year).length()!= 4){
								errorStrs.add("第 " + index + " 行：年份格式不符合要求，忽略导入。");
								ifImport = false;
								break;
							}else{
								stockSubmitPlan.setYear(year);
							}
						}else{
							errorStrs.add("第 " + index + " 行：年份为空，忽略导入。");
							ifImport = false;
							break;
						}
					}catch (Exception e){
						errorStrs.add("第 " + index + " 行：年份格式不符合要求，忽略导入。");
						ifImport = false;
						break;
					}
					String tblx = row.getCell(4).getStringCellValue();
					if(tblx != null && !"".equals(tblx)){
						List<DictModel> tblxList = sysBaseApi.queryDictItemsByCode("stock_submit_plan_submit_type");
						List<DictModel> collect = tblxList.stream().filter(m -> m.getText().equals(tblx)).collect(Collectors.toList());
						if(collect != null && collect.size()>0){
							stockSubmitPlan.setSubmitType(collect.get(0).getValue());
						}else{
							errorStrs.add("第 " + index + " 行：无法根据提报类型找到对应数据，忽略导入。");
							ifImport = false;
							break;
						}
					}else{
						errorStrs.add("第 " + index + " 行：提报类型为空，忽略导入。");
						ifImport = false;
						break;
					}
					index++;
				}
				if(index >= 5){
					StockSubmitMaterials stockSubmitMaterials = new StockSubmitMaterials();
					//物资编码
					String code = null;
					Cell cell = row.getCell(1);
					CellType cellType = cell.getCellTypeEnum();
					// 如果是字符串，直接赋值，如果是数值，获取后进行转换
					if (CellType.STRING.equals(cellType)) {
						code = row.getCell(1).getStringCellValue();
					} else if (CellType.NUMERIC.equals(cellType)) {
						double numericCellValue = row.getCell(1).getNumericCellValue();
						int num = (int)numericCellValue;
						// 然后自己转化为字符串，并赋值给value
						code = String.valueOf(num);
					}
					if ("".equals(code)) {
						errorStrs.add("第 " + index + " 行：物资编码为空，忽略导入。");
						ifAdd = false;
						continue;
					}else{
						MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",code));
						if (materialBase != null) {
							stockSubmitMaterials.setMaterialsCode(code);
						}else{
							errorStrs.add("第 " + index + " 行：无法根据物资编码找到对应数据，忽略导入。");
							ifAdd = false;
							continue;
						}
					}
					//请购数量
					int qgsl =0;
					try {
						qgsl = (int)row.getCell(2).getNumericCellValue();
						if ("".equals(qgsl)) {
							errorStrs.add("第 " + index + " 行：请购数量为空，忽略导入。");
							ifAdd = false;
							continue;
						}else{
							stockSubmitMaterials.setPlanBuyNumber(qgsl);
						}
					}catch (Exception e){
						errorStrs.add("第 " + index + " 行：请购数量为空，忽略导入。");
						ifAdd = false;
						continue;
					}
					if(ifImport && ifAdd){
						MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",code));
						stockSubmitMaterials.setUnit(materialBase.getUnit());
						stockSubmitMaterials.setPrice(materialBase.getPrice());
						Double price = Double.parseDouble(materialBase.getPrice());
						String pricetotal = String.valueOf(price*qgsl);
						stockSubmitMaterials.setTotalPrices(pricetotal);
						stockSubmitMaterialsList.add(stockSubmitMaterials);
					}
				}
				if(!ifAdd){
					errorLines += 1;
				}
			}
			if(ifImport){
				String code = this.getSubmitPlanCode().getCode();
				String status = CommonConstant.STOCK_LEVEL2_SUBMIT_PLAN_1;
				LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
				String userId = user.getId();
				String orgId = user.getOrgId();
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				stockSubmitPlan.setCode(code);
				stockSubmitPlan.setStatus(status);
				stockSubmitPlan.setUserId(userId);
				stockSubmitPlan.setOrgId(orgId);
				stockSubmitPlan.setSubmitTime(sdf2.parse(sdf2.format(new Date())));
				this.save(stockSubmitPlan);
				if(stockSubmitMaterialsList != null && stockSubmitMaterialsList.size()>0){
					stockSubmitMaterialsList.stream().forEach(s ->s.setSubmitPlanCode(code));
					stockSubmitMaterialsService.saveBatch(stockSubmitMaterialsList);
				}
				successLines += (1 + stockSubmitMaterialsList.size());
			}else{
				errorLines +=1;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorStrs);
	}

	@Override
	public List<StockSubmitPlan> getOrgSelect() {
		return stockSubmitPlanMapper.getOrgSelect();
	}
}
