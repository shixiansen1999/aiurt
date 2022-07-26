package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.common.util.XlsExport;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.entity.*;
import com.aiurt.modules.stock.mapper.StockLevel2CheckMapper;
import com.aiurt.modules.stock.service.*;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockLevel2CheckServiceImpl extends ServiceImpl<StockLevel2CheckMapper, StockLevel2Check> implements IStockLevel2CheckService {

	@Autowired
	private StockLevel2CheckMapper stockLevel2CheckMapper;
	@Autowired
	private IStockLevel2CheckDetailService stockLevel2CheckDetailService;
	@Autowired
	private IMaterialBaseService materialBaseService;
	@Autowired
	private SysBaseApiImpl sysBaseApi;
	@Autowired
	private IStockLevel2InfoService stockLevel2InfoService;
	@Autowired
	private IStockLevel2Service stockLevel2Service;

	@Override
	public StockLevel2Check getStockCheckCode() throws ParseException {
		QueryWrapper<StockLevel2Check> queryWrapper = new QueryWrapper<>();
		String str = "PD";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		str += sdf.format(new Date());
		queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		queryWrapper.likeRight("stock_check_code",str);
		queryWrapper.orderByDesc("create_time");
		queryWrapper.last("limit 1");
		StockLevel2Check stockLevel2Check = stockLevel2CheckMapper.selectOne(queryWrapper);
		String format = "";
		if(stockLevel2Check != null){
			String code = stockLevel2Check.getStockCheckCode();
			String numstr = code.substring(code.length()-3);
			format = String.format("%03d", Long.parseLong(numstr) + 1);
		}else{
			format = "001";
		}
		StockLevel2Check stockLevel2Checkres = new StockLevel2Check();
		stockLevel2Checkres.setStockCheckCode(str + format);
		return stockLevel2Checkres;
	}

	@Override
	public void add(StockLevel2Check stockLevel2Check) {
		this.save(stockLevel2Check);
		List<StockLevel2> stockLevel2List = stockLevel2Service.list(new QueryWrapper<StockLevel2>().eq("warehouse_code",stockLevel2Check.getWarehouseCode()));
		if(stockLevel2List != null && stockLevel2List.size()>0){
			for(StockLevel2 stockLevel2 : stockLevel2List){
				MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",stockLevel2.getMaterialCode()));
				Double price = materialBase.getPrice()==null?0.00:Double.parseDouble(materialBase.getPrice());
				Double totalPrice =price * stockLevel2.getNum();
				StockLevel2CheckDetail stockLevel2CheckDetail = new StockLevel2CheckDetail();
				stockLevel2CheckDetail.setStockCheckCode(stockLevel2Check.getStockCheckCode());
				stockLevel2CheckDetail.setWarehouseCode(stockLevel2Check.getWarehouseCode());
				stockLevel2CheckDetail.setMaterialCode(stockLevel2.getMaterialCode());
				stockLevel2CheckDetail.setBookNumber(stockLevel2.getNum());
				stockLevel2CheckDetail.setBookValue(totalPrice.toString());
				stockLevel2CheckDetailService.save(stockLevel2CheckDetail);
			}
		}
	}

	@Override
	public boolean edit(StockLevel2Check stockLevel2Check) {
		String code = stockLevel2Check.getStockCheckCode();
		QueryWrapper<StockLevel2CheckDetail> queryWrapper = new QueryWrapper<StockLevel2CheckDetail>();
		queryWrapper.eq("stock_check_code",code);
		stockLevel2CheckDetailService.remove(queryWrapper);
		List<StockLevel2> stockLevel2List = stockLevel2Service.list(new QueryWrapper<StockLevel2>().eq("warehouse_code",stockLevel2Check.getWarehouseCode()));
		if(stockLevel2List != null && stockLevel2List.size()>0){
			for(StockLevel2 stockLevel2 : stockLevel2List){
				MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",stockLevel2.getMaterialCode()));
				Double price = materialBase.getPrice()==null?0.00:Double.parseDouble(materialBase.getPrice());
				Double totalPrice =price * stockLevel2.getNum();
				StockLevel2CheckDetail stockLevel2CheckDetail = new StockLevel2CheckDetail();
				stockLevel2CheckDetail.setStockCheckCode(stockLevel2Check.getStockCheckCode());
				stockLevel2CheckDetail.setWarehouseCode(stockLevel2Check.getWarehouseCode());
				stockLevel2CheckDetail.setMaterialCode(stockLevel2.getMaterialCode());
				stockLevel2CheckDetail.setBookNumber(stockLevel2.getNum());
				stockLevel2CheckDetail.setBookValue(totalPrice.toString());
				stockLevel2CheckDetailService.save(stockLevel2CheckDetail);
			}
		}
		boolean ok = this.updateById(stockLevel2Check);
		return ok;
	}

	@Override
	public void eqExport(String ids, HttpServletRequest request, HttpServletResponse response) {
		String[] split = ids.split(",");
		List<String> strings = Arrays.asList(split);
		// 过滤选中数据
		List<StockLevel2Check> list = this.list(new QueryWrapper<StockLevel2Check>().in("id", strings));
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
		/*if(list != null && list.size()>0){
			for(StockLevel2Check stockLevel2Check : list){
				String code = stockLevel2Check.getCode();
				QueryWrapper<StockLevel2CheckDetail> queryWrapper = new QueryWrapper<StockLevel2CheckDetail>();
				queryWrapper.eq("submit_plan_code",code);
				List<StockLevel2CheckDetail> materials = stockLevel2CheckDetailService.list(queryWrapper);
				stockLevel2Check.setStockLevel2CheckDetailList(materials);
				String tbrid = stockLevel2Check.getUserId()==null?"":stockLevel2Check.getUserId();
				String tbrname = sysBaseApi.translateDictFromTable("sys_user", "realname", "id", tbrid);
				String typecode = stockLevel2Check.getSubmitType()==null?"":stockLevel2Check.getSubmitType();
				String typename = sysBaseApi.translateDict("stock_submit_plan_submit_type",typecode);
				String statuscode = stockLevel2Check.getStatus()==null?"":stockLevel2Check.getStatus().toString();
				String statusname = sysBaseApi.translateDict("stock_submit_plan_status",statuscode);
				excel.createRow(rowIndex++);
				excel.setCell(0, "基本信息");
				for(int i = 1; i<11; i++){
					excel.setCell(i, "");
				}
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "提报计划编号");
				excel.setCell(2, stockLevel2Check.getCode()==null?"":stockLevel2Check.getCode());
				excel.setCell(3, "");
				excel.setCell(4, "提报时间");
				Date submitTime = stockLevel2Check.getSubmitTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				excel.setCell(5, sdf.format(submitTime));
				excel.setCell(6, "");
				excel.setCell(7, "填报人");
				excel.setCell(8, tbrname==null?"":tbrname);
				excel.setCell(9, "");
				excel.setCell(10, "");
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "年份");
				excel.setCell(2, stockLevel2Check.getYear()==null?"":stockLevel2Check.getYear().toString());
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
				for(int i = 1; i<11; i++){
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
					for(StockLevel2CheckDetail stockLevel2CheckDetail : materials){
						String wzcode = stockLevel2CheckDetail.getMaterialsCode()==null?"":stockLevel2CheckDetail.getMaterialsCode();
						MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",wzcode));
						materialBase = materialBaseService.translate(materialBase);
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
						excel.setCell(7, stockLevel2CheckDetail.getPlanBuyNumber()==null?"":stockLevel2CheckDetail.getPlanBuyNumber().toString());
						excel.setCell(8, unitname==null?"":unitname);
						excel.setCell(9, stockLevel2CheckDetail.getPrice()==null?"":stockLevel2CheckDetail.getPrice());
						excel.setCell(10, stockLevel2CheckDetail.getTotalPrices()==null?"":stockLevel2CheckDetail.getTotalPrices());
					}
				}
				excel.createRow(rowIndex++);
			}
		}*/
		excel.exportXls(response);
	}

	@Override
	public Result getStockOrgUsers(String warehouseCode) {
		StockLevel2Info stockLevel2Info = stockLevel2InfoService.getOne(new QueryWrapper<StockLevel2Info>().eq("warehouse_code",warehouseCode));
		String organizationId = stockLevel2Info.getOrganizationId();
		return Result.OK(sysBaseApi.getOrgUsersByOrgid(organizationId));
	}

}
