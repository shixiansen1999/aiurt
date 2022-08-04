package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.common.util.XlsExport;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.entity.*;
import com.aiurt.modules.stock.mapper.StockLevel2CheckMapper;
import com.aiurt.modules.stock.service.*;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
	@Autowired
	private ISysDepartService iSysDepartService;

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
		StockLevel2Info stockLevel2Info = stockLevel2InfoService.getOne(new QueryWrapper<StockLevel2Info>().eq("warehouse_code",stockLevel2Check.getWarehouseCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
		String organizationId = stockLevel2Info.getOrganizationId();
		SysDepart sysDepart = iSysDepartService.getById(organizationId);
		stockLevel2Check.setOrgCode(sysDepart.getOrgCode());
		this.save(stockLevel2Check);
		List<StockLevel2> stockLevel2List = stockLevel2Service.list(new QueryWrapper<StockLevel2>().eq("warehouse_code",stockLevel2Check.getWarehouseCode()));
		if(stockLevel2List != null && stockLevel2List.size()>0){
			for(StockLevel2 stockLevel2 : stockLevel2List){
				MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",stockLevel2.getMaterialCode()))==null?new MaterialBase():materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",stockLevel2.getMaterialCode()));
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
		if(list != null && list.size()>0){
			for(StockLevel2Check stockLevel2Check : list){
				String code = stockLevel2Check.getStockCheckCode();
				QueryWrapper<StockLevel2CheckDetail> queryWrapper = new QueryWrapper<StockLevel2CheckDetail>();
				queryWrapper.eq("stock_check_code",code).eq("del_flag", CommonConstant.DEL_FLAG_0);
				List<StockLevel2CheckDetail> materials = stockLevel2CheckDetailService.list(queryWrapper);
				stockLevel2Check.setStockLevel2CheckDetailList(materials);
				String pdrid = stockLevel2Check.getCheckerId()==null?"":stockLevel2Check.getCheckerId();
				String tbrname = sysBaseApi.translateDictFromTable("sys_user", "realname", "id", pdrid);
				String warehouseCode = stockLevel2Check.getWarehouseCode()==null?"":stockLevel2Check.getWarehouseCode();
				StockLevel2Info stockLevel2Info = stockLevel2InfoService.getOne(new QueryWrapper<StockLevel2Info>().eq("del_flag", CommonConstant.DEL_FLAG_0).eq("warehouse_code",warehouseCode));
				String statuscode = stockLevel2Check.getStatus()==null?"":stockLevel2Check.getStatus().toString();
				String statusname = sysBaseApi.translateDict("stock_level2_check_status",statuscode);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				excel.createRow(rowIndex++);
				excel.setCell(0, "基本信息");
				for(int i = 1; i<11; i++){
					excel.setCell(i, "");
				}
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "盘点任务单号");
				excel.setCell(2, code==null?"":code);
				excel.setCell(3, "");
				excel.setCell(4, "盘点仓库");
				excel.setCell(5, stockLevel2Info==null?"":stockLevel2Info.getWarehouseName());
				excel.setCell(6, "");
				excel.setCell(7, "盘点人");
				excel.setCell(8, tbrname==null?"":tbrname);
				excel.setCell(9, "");
				excel.setCell(10, "");
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "计划开始时间");
				excel.setCell(2, stockLevel2Check.getPlanStartTime()==null?"": sdf.format(stockLevel2Check.getPlanStartTime()));
				excel.setCell(3, "");
				excel.setCell(4, "盘点开始时间");
				excel.setCell(5, stockLevel2Check.getCheckStartTime()==null?"": sdf.format(stockLevel2Check.getCheckStartTime()));
				excel.setCell(6, "");
				excel.setCell(7, "盘点结束时间");
				excel.setCell(8, stockLevel2Check.getCheckEndTime()==null?"": sdf.format(stockLevel2Check.getCheckEndTime()));
				excel.setCell(9, "");
				excel.setCell(10, "");
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "盘点单状态");
				excel.setCell(2, statusname==null?"":statusname);
				excel.setCell(3, "");
				excel.setCell(4, "备注");
				excel.setCell(5, stockLevel2Check.getNote()==null?"":stockLevel2Check.getNote());
				excel.setCell(6, "");
				excel.setCell(7, "");
				excel.setCell(8, "");
				excel.setCell(9, "");
				excel.setCell(10, "");
				excel.createRow(rowIndex++);
				excel.setCell(0, "物资盘点结果清单");
				for(int i = 1; i<11; i++){
					excel.setCell(i, "");
				}
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "物资编码");
				excel.setCell(2, "物资名称");
				excel.setCell(3, "物资类型");
				excel.setCell(4, "单位");
				excel.setCell(5, "账面价值");
				excel.setCell(6, "账面数量");
				excel.setCell(7, "实盘数量");
				excel.setCell(8, "盘盈数量");
				excel.setCell(9, "盘亏数量");
				excel.setCell(10, "备注");
				if(materials != null && materials.size()>0){
					for(StockLevel2CheckDetail stockLevel2CheckDetail : materials){
						String wzcode = stockLevel2CheckDetail.getMaterialCode()==null?"":stockLevel2CheckDetail.getMaterialCode();
						MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",wzcode));
						materialBase = materialBaseService.translate(materialBase);
						String wztype = materialBase.getType()==null?"":materialBase.getType().toString();
						String wztypename = sysBaseApi.translateDict("material_type",wztype);
						String unitcode = materialBase.getUnit()==null?"":materialBase.getUnit();
						String unitname = sysBaseApi.translateDict("materian_unit",unitcode);
						excel.createRow(rowIndex++);
						excel.setCell(0, "");
						excel.setCell(1, stockLevel2CheckDetail.getMaterialCode()==null?"":stockLevel2CheckDetail.getMaterialCode());
						excel.setCell(2, materialBase.getName()==null?"":materialBase.getName());
						excel.setCell(3, wztypename==null?"":wztypename);
						excel.setCell(4, unitname==null?"":unitname);
						excel.setCell(5, stockLevel2CheckDetail.getBookValue()==null?"":stockLevel2CheckDetail.getBookValue());
						excel.setCell(6, stockLevel2CheckDetail.getBookNumber()==null?"":stockLevel2CheckDetail.getBookNumber().toString());
						excel.setCell(7, stockLevel2CheckDetail.getActualNum()==null?"":stockLevel2CheckDetail.getActualNum().toString());
						excel.setCell(8, stockLevel2CheckDetail.getProfitNum()==null?"":stockLevel2CheckDetail.getProfitNum().toString());
						excel.setCell(9, stockLevel2CheckDetail.getLossNum()==null?"":stockLevel2CheckDetail.getLossNum().toString());
						excel.setCell(10, stockLevel2CheckDetail.getNote()==null?"":stockLevel2CheckDetail.getNote());
					}
				}
				excel.createRow(rowIndex++);
			}
		}
		excel.exportXls(response);
	}

	@Override
	public Result getStockOrgUsers(String warehouseCode) {
		StockLevel2Info stockLevel2Info = stockLevel2InfoService.getOne(new QueryWrapper<StockLevel2Info>().eq("warehouse_code",warehouseCode));
		String organizationId = stockLevel2Info.getOrganizationId();
		return Result.OK(sysBaseApi.getOrgUsersByOrgid(organizationId));
	}

	@Override
	public IPage<StockLevel2Check> pageList(Page<StockLevel2Check> page, StockLevel2Check stockLevel2Check) {
		List<StockLevel2Check> baseList = baseMapper.pageList(page, stockLevel2Check);
		page.setRecords(baseList);
		return page;
	}

}
