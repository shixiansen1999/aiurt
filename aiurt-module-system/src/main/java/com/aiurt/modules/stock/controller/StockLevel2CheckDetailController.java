package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.entity.StockLevel2Check;
import com.aiurt.modules.stock.entity.StockLevel2CheckDetail;
import com.aiurt.modules.stock.entity.StockLevel2Info;
import com.aiurt.modules.stock.service.IStockLevel2CheckDetailService;
import com.aiurt.modules.stock.service.IStockLevel2CheckService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description: 盘点物资
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库管理-二级库盘点管理-盘点物资")
@RestController
@RequestMapping("/stock/stockLevel2CheckDetail")
public class StockLevel2CheckDetailController {

    @Autowired
    private IStockLevel2CheckDetailService iStockLevel2CheckDetailService;
    @Autowired
    private IMaterialBaseService iMaterialBaseService;
    @Autowired
    private IStockLevel2CheckService iStockLevel2CheckService;

    /**
     * 分页列表查询
     *
     * @param stockLevel2CheckDetail
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库管理-二级库盘点管理-盘点物资-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-盘点物资-分页列表查询", notes = "二级库管理-二级库盘点管理-盘点物资-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockLevel2CheckDetail>> queryPageList(StockLevel2CheckDetail stockLevel2CheckDetail,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Page<StockLevel2CheckDetail> page = new Page<StockLevel2CheckDetail>(pageNo, pageSize);
        IPage<StockLevel2CheckDetail> pageList = iStockLevel2CheckDetailService.pageList(page, stockLevel2CheckDetail);
        List<StockLevel2CheckDetail> records = pageList.getRecords();
        if(records != null && records.size()>0){
            for(StockLevel2CheckDetail stockLevel2CheckDetail1 : records){
                String materialCode = stockLevel2CheckDetail1.getMaterialCode();
                MaterialBase materialBase = iMaterialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",materialCode))==null?new MaterialBase():iMaterialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",materialCode));
                stockLevel2CheckDetail1.setMaterialName(materialBase.getName()==null?"":materialBase.getName());
                stockLevel2CheckDetail1.setType(materialBase.getType()==null?"":materialBase.getType());
                stockLevel2CheckDetail1.setUnit(materialBase.getUnit()==null?"":materialBase.getUnit());
            }
        }
        return Result.OK(pageList);
    }

    @AutoLog(value = "二级库管理-二级库盘点管理-盘点物资-编辑", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-盘点物资-编辑", notes = "二级库管理-二级库盘点管理-盘点物资-编辑")
    @PostMapping(value = "/edit")
    public Result<StockLevel2CheckDetail> edit(@RequestBody List<StockLevel2CheckDetail> stockLevel2CheckDetailList) {
        Result<StockLevel2CheckDetail> result = new Result<StockLevel2CheckDetail>();
        if (stockLevel2CheckDetailList == null) {
            result.onnull("未找到对应实体");
        } else {
            String stockCheckCode = stockLevel2CheckDetailList.get(0).getStockCheckCode();
            StockLevel2Check stockLevel2Check = iStockLevel2CheckService.getOne(new QueryWrapper<StockLevel2Check>().eq("del_flag", CommonConstant.DEL_FLAG_0).eq("stock_check_code",stockCheckCode));
            int count = 0;
            for(StockLevel2CheckDetail stockLevel2CheckDetail : stockLevel2CheckDetailList){
                count += stockLevel2CheckDetail.getActualNum()==null?0:stockLevel2CheckDetail.getActualNum();
            }
            stockLevel2Check.setCheckNum(count);
            iStockLevel2CheckService.updateById(stockLevel2Check);
            boolean ok = iStockLevel2CheckDetailService.updateBatchById(stockLevel2CheckDetailList);
            try{
            }catch (Exception e){
                throw new AiurtBootException(e.getMessage());
            }
            if (ok) {
                result.success("操作成功!");
            }
        }

        return result;
    }

    @AutoLog(value = "二级库管理-二级库盘点管理-盘点物资-提交", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-盘点物资-提交", notes = "二级库管理-二级库盘点管理-盘点物资-提交")
    @PostMapping(value = "/commitCheckInfo")
    public Result<StockLevel2CheckDetail> commitCheckInfo(@RequestBody List<StockLevel2CheckDetail> stockLevel2CheckDetailList) throws ParseException {
        Result<StockLevel2CheckDetail> result = new Result<StockLevel2CheckDetail>();
        if (stockLevel2CheckDetailList == null) {
            result.onnull("未找到对应实体");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String stockCheckCode = stockLevel2CheckDetailList.get(0).getStockCheckCode();
            StockLevel2Check stockLevel2Check = iStockLevel2CheckService.getOne(new QueryWrapper<StockLevel2Check>().eq("del_flag", CommonConstant.DEL_FLAG_0).eq("stock_check_code",stockCheckCode));
            stockLevel2Check.setCheckEndTime(sdf.parse(sdf.format(new Date())));
            stockLevel2Check.setStatus(CommonConstant.StOCK_LEVEL2_CHECK_STATUS_5);
            int count = 0;
            for(StockLevel2CheckDetail stockLevel2CheckDetail : stockLevel2CheckDetailList){
                count += stockLevel2CheckDetail.getActualNum()==null?0:stockLevel2CheckDetail.getActualNum();
            }
            stockLevel2Check.setCheckNum(count);
            iStockLevel2CheckService.updateById(stockLevel2Check);
            boolean ok = iStockLevel2CheckDetailService.updateBatchById(stockLevel2CheckDetailList);
            try{
            }catch (Exception e){
                throw new AiurtBootException(e.getMessage());
            }
            if (ok) {
                result.success("提交成功!");
            }
        }

        return result;
    }
}
