package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsExport;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.entity.*;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.aiurt.modules.stock.mapper.StockInOrderLevel2Mapper;
import com.aiurt.modules.stock.service.IStockIncomingMaterialsService;
import com.aiurt.modules.stock.service.IStockInOrderLevel2Service;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockInOrderLevel2ServiceImpl extends ServiceImpl<StockInOrderLevel2Mapper, StockInOrderLevel2> implements IStockInOrderLevel2Service {

	@Autowired
	private StockInOrderLevel2Mapper stockInOrderLevel2Mapper;
	@Autowired
	private IStockIncomingMaterialsService stockIncomingMaterialsService;
	@Autowired
	private IMaterialBaseService materialBaseService;
	@Autowired
	private IStockLevel2Service stockLevel2Service;
	@Autowired
	private SysBaseApiImpl sysBaseApi;

	@Override
	public StockInOrderLevel2 getInOrderCode() throws ParseException {
		QueryWrapper<StockInOrderLevel2> queryWrapper = new QueryWrapper<>();
		String str = "RK";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		str += sdf.format(new Date());
		queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		queryWrapper.likeRight("order_code",str);
		queryWrapper.orderByDesc("create_time");
		queryWrapper.last("limit 1");
		StockInOrderLevel2 stockInOrderLevel2 = stockInOrderLevel2Mapper.selectOne(queryWrapper);
		String format = "";
		if(stockInOrderLevel2 != null){
			String code = stockInOrderLevel2.getOrderCode();
			String numstr = code.substring(code.length()-3);
			format = String.format("%03d", Long.parseLong(numstr) + 1);
		}else{
			format = "001";
		}
		StockInOrderLevel2 stockInOrderLevel2res = new StockInOrderLevel2();
		stockInOrderLevel2res.setOrderCode(str + format);;
		return stockInOrderLevel2res;
	}

	@Override
	public void add(StockInOrderLevel2 stockInOrderLevel2) {
		List<StockIncomingMaterials> stockIncomingMaterialsList = stockInOrderLevel2.getStockIncomingMaterialsList();
		if(stockIncomingMaterialsList != null && stockIncomingMaterialsList.size()>0){
			stockIncomingMaterialsService.saveBatch(stockIncomingMaterialsList);
		}
		this.save(stockInOrderLevel2);
	}

	@Override
	public boolean edit(StockInOrderLevel2 stockInOrderLevel2) {
		String code = stockInOrderLevel2.getOrderCode();
		QueryWrapper<StockIncomingMaterials> queryWrapper = new QueryWrapper<StockIncomingMaterials>();
		queryWrapper.eq("in_order_code",code);
		stockIncomingMaterialsService.remove(queryWrapper);
		List<StockIncomingMaterials> stockIncomingMaterialsList = stockInOrderLevel2.getStockIncomingMaterialsList();
		if(stockIncomingMaterialsList != null && stockIncomingMaterialsList.size()>0){
			stockIncomingMaterialsService.saveBatch(stockIncomingMaterialsList);
		}
		boolean ok = this.updateById(stockInOrderLevel2);
		return ok;
	}

	@Override
	public boolean submitPlan(String status, String code) {
		StockInOrderLevel2 stockInOrderLevel2 = this.getOne(new QueryWrapper<StockInOrderLevel2>().eq("code",code));
		stockInOrderLevel2.setStatus(status);
		String warehouseCode = stockInOrderLevel2.getWarehouseCode();//仓库编号
		Date stockInTime = stockInOrderLevel2.getEntryTime();
		List<StockIncomingMaterials> stockIncomingMaterialsList = stockIncomingMaterialsService.list(new QueryWrapper<StockIncomingMaterials>().eq("in_order_code",code).eq("del_flag", CommonConstant.DEL_FLAG_0));
		if(stockIncomingMaterialsList != null && stockIncomingMaterialsList.size()>0){
			for(StockIncomingMaterials stockIncomingMaterials : stockIncomingMaterialsList){
				String materialCode = stockIncomingMaterials.getMaterialCode();//物资编号
				Integer number = stockIncomingMaterials.getNumber();//数量
				StockLevel2 stockLevel2 = stockLevel2Service.getOne(new QueryWrapper<StockLevel2>().eq("material_code",materialCode).eq("warehouse_code",warehouseCode).eq("del_flag", CommonConstant.DEL_FLAG_0));
				if(stockLevel2 != null){
					Integer num = stockLevel2.getNum();
					stockLevel2.setNum(num + number);
					stockLevel2.setStockInTime(stockInTime);
					stockLevel2Service.updateById(stockLevel2);
				}else{
					MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",materialCode));
					StockLevel2 stockLevel2new = new StockLevel2();
					stockLevel2new.setMaterialCode(materialCode);
					stockLevel2new.setBaseTypeCode(materialBase.getBaseTypeCodeCc());
					stockLevel2new.setWarehouseCode(warehouseCode);
					stockLevel2new.setNum(number);
					stockLevel2new.setMajorCode(materialBase.getMajorCode());
					stockLevel2new.setSystemCode(materialBase.getSystemCode());
					stockLevel2new.setStockInTime(stockInTime);
					stockLevel2Service.save(stockLevel2new);
				}
			}
		}
		boolean ok = this.updateById(stockInOrderLevel2);
		return ok;
	}

	@Override
	public void eqExport(String ids, HttpServletRequest request, HttpServletResponse response) {
		String[] split = ids.split(",");
		List<String> strings = Arrays.asList(split);
		// 过滤选中数据
		List<StockInOrderLevel2> list = this.list(new QueryWrapper<StockInOrderLevel2>().in("id", strings));
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
			for(StockInOrderLevel2 stockInOrderLevel2 : list){
				String code = stockInOrderLevel2.getOrderCode();
				QueryWrapper<StockIncomingMaterials> queryWrapper = new QueryWrapper<StockIncomingMaterials>();
				queryWrapper.eq("in_order_code",code);
				List<StockIncomingMaterials> materials = stockIncomingMaterialsService.list(queryWrapper);
				stockInOrderLevel2.setStockIncomingMaterialsList(materials);
				String userid = stockInOrderLevel2.getUserId()==null?"":stockInOrderLevel2.getUserId();
				String username = sysBaseApi.translateDictFromTable("sys_user", "realname", "id", userid);
				String warehouseCode = stockInOrderLevel2.getWarehouseCode()==null?"":stockInOrderLevel2.getWarehouseCode();
				String warehouseCodename = sysBaseApi.translateDictFromTable("stock_level2_info","warehouse_name","warehouse_code",warehouseCode);
				String statuscode = stockInOrderLevel2.getStatus()==null?"":stockInOrderLevel2.getStatus().toString();
				String statusname = sysBaseApi.translateDict("stock_in_order_level2_status",statuscode);
				excel.createRow(rowIndex++);
				excel.setCell(0, "基本信息");
				for(int i = 1; i<10; i++){
					excel.setCell(i, "");
				}
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "入库单号");
				excel.setCell(2, code==null?"":code);
				excel.setCell(3, "");
				excel.setCell(4, "入库时间");
				Date entryTime = stockInOrderLevel2.getEntryTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				excel.setCell(5, sdf.format(entryTime));
				excel.setCell(6, "");
				excel.setCell(7, "入库人");
				excel.setCell(8, username==null?"":username);
				excel.setCell(9, "");
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "入库仓库");
				excel.setCell(2, warehouseCodename==null?"":warehouseCodename);
				excel.setCell(3, "");
				excel.setCell(4, "入库单状态");
				excel.setCell(5, statusname==null?"":statusname);
				excel.setCell(6, "");
				excel.setCell(7, "备注");
				excel.setCell(8, stockInOrderLevel2.getNote()==null?"":stockInOrderLevel2.getNote());
				excel.setCell(9, "");
				excel.createRow(rowIndex++);
				excel.setCell(0, "提报物资清单");
				for(int i = 1; i<10; i++){
					excel.setCell(i, "");
				}
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "所属专业");
				excel.setCell(2, "所属子系统");
				excel.setCell(3, "物资分类");
				excel.setCell(4, "物资编号");
				excel.setCell(5, "物资名称");
				excel.setCell(6, "物资类型");
				excel.setCell(9, "存放仓库");
				excel.setCell(7, "入库数量");
				excel.setCell(8, "单位");
				if(materials != null && materials.size()>0){
					for(StockIncomingMaterials stockIncomingMaterials : materials){
						String wzcode = stockIncomingMaterials.getMaterialCode()==null?"":stockIncomingMaterials.getMaterialCode();
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
						excel.setCell(7, warehouseCodename==null?"":warehouseCodename);
						excel.setCell(9, stockIncomingMaterials.getNumber()==null?"":stockIncomingMaterials.getNumber().toString());
						excel.setCell(8, unitname==null?"":unitname);
					}
				}
				excel.createRow(rowIndex++);
			}
		}
		excel.exportXls(response);
	}
}
