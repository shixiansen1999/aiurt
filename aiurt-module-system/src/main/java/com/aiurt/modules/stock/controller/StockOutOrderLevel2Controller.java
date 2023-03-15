package com.aiurt.modules.stock.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.stock.entity.StockLevel2Check;
import com.aiurt.modules.stock.entity.StockOutOrderLevel2;
import com.aiurt.modules.stock.service.IStockLevel2CheckService;
import com.aiurt.modules.stock.service.IStockOutOrderLevel2Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: 二级库出库管理
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库管理-二级库出库管理")
@RestController
@RequestMapping("/stock/stockOutOrderLevel2")
public class StockOutOrderLevel2Controller {

    @Autowired
    private IStockOutOrderLevel2Service iStockOutOrderLevel2Service;
	@Autowired
	private IStockLevel2CheckService iStockLevel2CheckService;
	@Autowired
	private ISysBaseAPI sysBaseApi;
	@Autowired
	private ISysParamAPI iSysParamAPI;
	@Autowired
	private ISTodoBaseAPI isTodoBaseAPI;
    /**
     * 分页列表查询
     *
     * @param stockOutOrderLevel2
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库管理-二级库出库管理-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockLevel2SecondaryList")
    @ApiOperation(value = "二级库管理-二级库出库管理-分页列表查询", notes = "二级库管理-二级库出库管理-分页列表查询")
    @GetMapping(value = "/list")
	@PermissionData(pageComponent = "secondLevelWarehouse/StockLevel2SecondaryList")
    public Result<IPage<StockOutOrderLevel2>> queryPageList(StockOutOrderLevel2 stockOutOrderLevel2,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<StockOutOrderLevel2>> result = new Result<IPage<StockOutOrderLevel2>>();
        Page<StockOutOrderLevel2> page = new Page<StockOutOrderLevel2>(pageNo, pageSize);
        IPage<StockOutOrderLevel2> pageList = iStockOutOrderLevel2Service.pageList(page, stockOutOrderLevel2);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

	@AutoLog(value = "二级库管理-二级库出库管理-保管仓库下拉", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockLevel2SecondaryList")
	@ApiOperation(value = "二级库管理-二级库出库管理-保管仓库下拉", notes = "二级库管理-二级库出库管理-保管仓库下拉")
	@GetMapping(value = "/sparePartsStocklist")
	@PermissionData(pageComponent = "secondLevelWarehouse/StockLevel2SecondaryList")
	public Result<List<StockOutOrderLevel2>> sparePartsWarelist(HttpServletRequest req) {
		Result<List<StockOutOrderLevel2>> result = new Result<List<StockOutOrderLevel2>>();
		List<StockOutOrderLevel2> pageList = iStockOutOrderLevel2Service.selectList();
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

    /**
     * 二级库出库管理详情查询
     * @param id
     * @return
     */
	@AutoLog(value = "二级库管理-二级库出库管理-详情查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockLevel2SecondaryList")
    @ApiOperation(value = "二级库管理-二级库出库管理-详情查询", notes = "二级库管理-二级库出库管理-详情查询")
    @GetMapping(value = "/queryById")
    public Result<SparePartApply> queryById(@RequestParam(name = "id", required = true) String id) {
		SparePartApply sparePartApply = iStockOutOrderLevel2Service.getList(id);
		return Result.ok(sparePartApply);
    }

	/**
	 * 二级库出库管理提交
	 * @return
	 */
	@AutoLog(value = "二级库管理-二级库出库管理-确认出库", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/secondLevelWarehouse/StockLevel2SecondaryList")
	@ApiOperation(value = "二级库管理-二级库出库管理-确认出库", notes = "二级库管理-二级库出库管理-确认出库")
	@PostMapping(value = "/confirmOutOrder")
	public Result<?> confirmOutOrder(@RequestBody SparePartApply sparePartApply) {
		try {
			String orderCode = sparePartApply.getOrderCode();
			StockOutOrderLevel2 stockOutOrderLevel2 = iStockOutOrderLevel2Service.getOne(new QueryWrapper<StockOutOrderLevel2>().eq("order_code",orderCode).eq("del_flag", CommonConstant.DEL_FLAG_0));
			String warehouseCode = stockOutOrderLevel2.getWarehouseCode();
			List<StockLevel2Check> stockLevel2CheckList = iStockLevel2CheckService.list(new QueryWrapper<StockLevel2Check>().eq("del_flag", CommonConstant.DEL_FLAG_0)
					.eq("warehouse_code",warehouseCode).eq("status",CommonConstant.STOCK_LEVEL2_CHECK_STATUS_4));
			if(stockLevel2CheckList != null && stockLevel2CheckList.size()>0){
				return Result.error("盘点任务执行期间，物资暂时无法进行出入库操作");
			}
			iStockOutOrderLevel2Service.confirmOutOrder(sparePartApply, stockOutOrderLevel2);

			try {
				LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
				//发送通知
				MessageDTO messageDTO = new MessageDTO(user.getUsername(),sparePartApply.getApplyUserId(), "备件入库确认" + DateUtil.today(), null);

				//构建消息模板
				HashMap<String, Object> map = new HashMap<>();
				map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, stockOutOrderLevel2.getId());
				map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_STOCKLEVEL2SECONDARY.getType());
				messageDTO.setData(map);
				//业务类型，消息类型，消息模板编码，摘要，发布内容
				SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
				messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
				messageDTO.setMsgAbstract("备件申领通过");
				messageDTO.setPublishingContent("备件申领到库，请尽快确认");
				messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
				sysBaseApi.sendTemplateMessage(messageDTO);
				// 更新待办
				isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.SPAREPART_APPLY.getType(), sparePartApply.getId(), user.getUsername(), "1");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return Result.ok("出库成功！");
		}catch (Exception e){
		    e.printStackTrace();
			return Result.ok("出库失败！");
		}
	}

	/**
	 *   添加
	 *
	 * @param stockOutOrderLevel2
	 * @return
	 */
	@AutoLog(value = "二级库管理-二级库出库管理-添加", operateType = 2, operateTypeAlias = "添加", permissionUrl = "/secondLevelWarehouse/StockLevel2SecondaryList")
	@ApiOperation(value="stock_out_order_level2-添加", notes="stock_out_order_level2-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody StockOutOrderLevel2 stockOutOrderLevel2) {
		iStockOutOrderLevel2Service.save(stockOutOrderLevel2);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param stockOutOrderLevel2
	 * @return
	 */
	@AutoLog(value = "二级库管理-二级库出库管理-编辑", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/secondLevelWarehouse/StockLevel2SecondaryList")
	@ApiOperation(value="stock_out_order_level2-编辑", notes="stock_out_order_level2-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody StockOutOrderLevel2 stockOutOrderLevel2) {
		iStockOutOrderLevel2Service.updateById(stockOutOrderLevel2);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "二级库管理-二级库出库管理-通过id删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/secondLevelWarehouse/StockLevel2SecondaryList")
	@ApiOperation(value="stock_out_order_level2-通过id删除", notes="stock_out_order_level2-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		iStockOutOrderLevel2Service.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "二级库管理-二级库出库管理-批量删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/secondLevelWarehouse/StockLevel2SecondaryList")
	@ApiOperation(value="stock_out_order_level2-批量删除", notes="stock_out_order_level2-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.iStockOutOrderLevel2Service.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

}
