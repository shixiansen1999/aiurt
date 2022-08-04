package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.aiurt.modules.stock.service.IStockIncomingMaterialsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 二级库入库物资
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库管理-二级库入库管理-入库物资")
@RestController
@RequestMapping("/stock/stockIncomingMaterials")
public class StockIncomingMaterialsController {

    @Autowired
    private IStockIncomingMaterialsService iStockIncomingMaterialsService;
    @Autowired
    private IMaterialBaseService materialBaseService;

    /**
     * 分页列表查询
     *
     * @param stockIncomingMaterials
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库管理-二级库入库管理-入库物资-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockInOrderLevel2List")
    @ApiOperation(value = "二级库管理-二级库入库管理-入库物资-分页列表查询", notes = "二级库管理-二级库入库管理-入库物资-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockIncomingMaterials>> queryPageList(StockIncomingMaterials stockIncomingMaterials,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<StockIncomingMaterials>> result = new Result<IPage<StockIncomingMaterials>>();
        QueryWrapper<StockIncomingMaterials> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        String inOrderCode = stockIncomingMaterials==null?"":stockIncomingMaterials.getInOrderCode();
        if(inOrderCode != null && !"".equals(inOrderCode)){
            queryWrapper.eq("in_order_code",inOrderCode);
        }
        queryWrapper.orderByDesc("create_time");
        Page<StockIncomingMaterials> page = new Page<StockIncomingMaterials>(pageNo, pageSize);
        IPage<StockIncomingMaterials> pageList = iStockIncomingMaterialsService.page(page, queryWrapper);
        List<StockIncomingMaterials> records = pageList.getRecords();
        if(records != null && records.size()>0){
            for(StockIncomingMaterials materials : records){
                String materialCode = materials.getMaterialCode();
                MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code", materialCode));
                materials.setMajorCode(materialBase.getMajorCode());
                materials.setSystemCode(materialBase.getSystemCode());
                materials.setBaseTypeCodeCc(materialBase.getBaseTypeCodeCc());
                materials.setName(materialBase.getName());
                materials.setUnit(materialBase.getUnit());
                materials.setType(materialBase.getType());
                String baseTypeCodeCcName = iStockIncomingMaterialsService.getCcName(materials);
                materials.setBaseTypeCodeCcName(baseTypeCodeCcName);
            }
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
}
