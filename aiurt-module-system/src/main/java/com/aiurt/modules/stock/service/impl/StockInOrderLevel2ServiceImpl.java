package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsExport;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.entity.*;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.aiurt.modules.stock.mapper.StockInOrderLevel2Mapper;
import com.aiurt.modules.stock.service.*;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.jeecg.common.system.vo.LoginUser;
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
	@Autowired
	private ICsMajorService csMajorService;
	@Autowired
	private ICsSubsystemService csSubsystemService;
	@Autowired
	private IStockLevel2CheckService iStockLevel2CheckService;
	@Autowired
	private IStockLevel2CheckDetailService iStockLevel2CheckDetailService;
	@Autowired
	private IStockLevel2InfoService iStockLevel2InfoService;
	@Autowired
	private ISysDepartService iSysDepartService;

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
		String userId = stockInOrderLevel2.getUserId();
		String[] ids = new String[1];
		ids[0] = userId;
		List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(ids);
		if(loginUsers != null && loginUsers.size()>0){
			stockInOrderLevel2.setOrgCode(loginUsers.get(0).getOrgCode());
		}
		this.save(stockInOrderLevel2);
		List<StockIncomingMaterials> stockIncomingMaterialsList = stockInOrderLevel2.getStockIncomingMaterialsList();
		if(stockIncomingMaterialsList != null && stockIncomingMaterialsList.size()>0){
			stockIncomingMaterialsList.stream().forEach(s ->s.setInOrderCode(stockInOrderLevel2.getOrderCode()));
			stockIncomingMaterialsService.saveBatch(stockIncomingMaterialsList);
		}
	}

	@Override
	public boolean edit(StockInOrderLevel2 stockInOrderLevel2) {
		String code = stockInOrderLevel2.getOrderCode();
		QueryWrapper<StockIncomingMaterials> queryWrapper = new QueryWrapper<StockIncomingMaterials>();
		queryWrapper.eq("in_order_code",code);
		stockIncomingMaterialsService.remove(queryWrapper);
		List<StockIncomingMaterials> stockIncomingMaterialsList = stockInOrderLevel2.getStockIncomingMaterialsList();
		if(stockIncomingMaterialsList != null && stockIncomingMaterialsList.size()>0){
			stockIncomingMaterialsList.stream().forEach(s ->s.setInOrderCode(stockInOrderLevel2.getOrderCode()));
			stockIncomingMaterialsService.saveBatch(stockIncomingMaterialsList);
		}
		boolean ok = this.updateById(stockInOrderLevel2);
		return ok;
	}

	@Override
	public boolean submitInOrderStatus(String status, String code, StockInOrderLevel2 stockInOrderLevel2) throws ParseException {
		String warehouseCode = stockInOrderLevel2.getWarehouseCode();
		List<StockLevel2Check> stockLevel2CheckList = iStockLevel2CheckService.list(new QueryWrapper<StockLevel2Check>().eq("del_flag", CommonConstant.DEL_FLAG_0)
				.eq("warehouse_code",warehouseCode).ne("status",CommonConstant.StOCK_LEVEL2_CHECK_STATUS_5));
		stockInOrderLevel2.setStatus(status);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date stockInTime = sdf.parse(sdf.format(new Date()));
		stockInOrderLevel2.setEntryTime(stockInTime);
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
					StockLevel2Info stockLevel2Info = iStockLevel2InfoService.getOne(new QueryWrapper<StockLevel2Info>().eq("warehouse_code",warehouseCode).eq("del_flag", CommonConstant.DEL_FLAG_0));
					String organizationId = stockLevel2Info.getOrganizationId();
					SysDepart sysDepart = iSysDepartService.getById(organizationId);
					MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",materialCode));
					StockLevel2 stockLevel2new = new StockLevel2();
					stockLevel2new.setMaterialCode(materialCode);
					stockLevel2new.setBaseTypeCode(materialBase.getBaseTypeCodeCc());
					stockLevel2new.setWarehouseCode(warehouseCode);
					stockLevel2new.setNum(number);
					stockLevel2new.setOrgCode(sysDepart.getOrgCode());
					stockLevel2new.setMajorCode(materialBase.getMajorCode());
					stockLevel2new.setSystemCode(materialBase.getSystemCode());
					stockLevel2new.setStockInTime(stockInTime);
					stockLevel2Service.save(stockLevel2new);
				}
				if(stockLevel2CheckList != null && stockLevel2CheckList.size()>0){
					for(StockLevel2Check stockLevel2Check : stockLevel2CheckList){
						String stockCheckCode = stockLevel2Check.getStockCheckCode();
						StockLevel2CheckDetail stockLevel2CheckDetail = iStockLevel2CheckDetailService.getOne(new QueryWrapper<StockLevel2CheckDetail>()
										.eq("material_code",materialCode).eq("warehouse_code",warehouseCode).eq("del_flag", CommonConstant.DEL_FLAG_0)
										.eq("stock_check_code",stockCheckCode));
						if(stockLevel2CheckDetail != null){
							Integer num = stockLevel2CheckDetail.getActualNum()==null?0:stockLevel2CheckDetail.getActualNum();
							stockLevel2CheckDetail.setBookNumber(num + number);
							iStockLevel2CheckDetailService.updateById(stockLevel2CheckDetail);
						}else{
							MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",materialCode));
							Double price = materialBase.getPrice()==null?0.00:Double.valueOf(materialBase.getPrice());
							StockLevel2CheckDetail stockLevel2CheckDetailknew = new StockLevel2CheckDetail();
							stockLevel2CheckDetailknew.setStockCheckCode(stockCheckCode);
							stockLevel2CheckDetailknew.setMaterialCode(materialCode);
							stockLevel2CheckDetailknew.setWarehouseCode(warehouseCode);
							stockLevel2CheckDetailknew.setBookNumber(number);
							stockLevel2CheckDetailknew.setBookValue((price * number)+"");
							iStockLevel2CheckDetailService.save(stockLevel2CheckDetailknew);
						}
					}
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
		List<StockInOrderLevel2> list = this.list(new QueryWrapper<StockInOrderLevel2>().in("id", strings).orderByDesc("create_time"));
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
				queryWrapper.eq("in_order_code",code).eq("del_flag", CommonConstant.DEL_FLAG_0);
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
				excel.setCell(7, "存放仓库");
				excel.setCell(8, "入库数量");
				excel.setCell(9, "单位");
				if(materials != null && materials.size()>0){
					for(StockIncomingMaterials stockIncomingMaterials : materials){
						String wzcode = stockIncomingMaterials.getMaterialCode()==null?"":stockIncomingMaterials.getMaterialCode();
						MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",wzcode));
						materialBase = materialBaseService.translate(materialBase);
						CsMajor csMajor = csMajorService.getOne(new QueryWrapper<CsMajor>().eq("major_code",materialBase.getMajorCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
//						String zyname = sysBaseApi.translateDictFromTable("cs_major", "major_name", "major_code", materialBase.getMajorCode());
						String zyname = csMajor==null?"":csMajor.getMajorName();
						CsSubsystem csSubsystem = csSubsystemService.getOne(new QueryWrapper<CsSubsystem>().eq("system_code",materialBase.getSystemCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
//						String zxyname = sysBaseApi.translateDictFromTable("cs_subsystem", "system_name", "system_code", materialBase.getSystemCode());
						String zxyname = csSubsystem==null?"":csSubsystem.getSystemName();
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
						excel.setCell(8, stockIncomingMaterials.getNumber()==null?"":stockIncomingMaterials.getNumber().toString());
						excel.setCell(9, unitname==null?"":unitname);
					}
				}
				excel.createRow(rowIndex++);
			}
		}
		excel.exportXls(response);
	}

	@Override
	public IPage<StockInOrderLevel2> pageList(Page<StockInOrderLevel2> page, StockInOrderLevel2 stockInOrderLevel2) {
		List<StockInOrderLevel2> baseList = baseMapper.pageList(page, stockInOrderLevel2);
		page.setRecords(baseList);
		return page;
	}
}
