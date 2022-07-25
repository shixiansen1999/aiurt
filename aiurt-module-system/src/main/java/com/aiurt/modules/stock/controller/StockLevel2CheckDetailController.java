package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.entity.StockLevel2CheckDetail;
import com.aiurt.modules.stock.entity.StockLevel2Info;
import com.aiurt.modules.stock.service.IStockLevel2CheckDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 盘点物资
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "盘点物资")
@RestController
@RequestMapping("/stock/stockLevel2CheckDetail")
public class StockLevel2CheckDetailController {

    @Autowired
    private IStockLevel2CheckDetailService iStockLevel2CheckDetailService;
    @Autowired
    private IMaterialBaseService iMaterialBaseService;

    /**
     * 分页列表查询
     *
     * @param stockLevel2CheckDetail
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "盘点物资-分页列表查询")
    @ApiOperation(value = "盘点物资-分页列表查询", notes = "盘点物资-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockLevel2CheckDetail>> queryPageList(StockLevel2CheckDetail stockLevel2CheckDetail,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        QueryWrapper<StockLevel2CheckDetail> queryWrapper = QueryGenerator.initQueryWrapper(stockLevel2CheckDetail, req.getParameterMap());
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        queryWrapper.orderByDesc("create_time");
        Page<StockLevel2CheckDetail> page = new Page<StockLevel2CheckDetail>(pageNo, pageSize);
        IPage<StockLevel2CheckDetail> pageList = iStockLevel2CheckDetailService.page(page, queryWrapper);
        List<StockLevel2CheckDetail> records = pageList.getRecords();
        if(records != null && records.size()>0){
            for(StockLevel2CheckDetail stockLevel2CheckDetail1 : records){
                String materialCode = stockLevel2CheckDetail1.getMaterialCode();
                MaterialBase materialBase = iMaterialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",materialCode));
                stockLevel2CheckDetail1.setMaterialName(materialBase.getName());
                stockLevel2CheckDetail1.setType(materialBase.getType()==null?"":materialBase.getType());
                stockLevel2CheckDetail1.setUnit(materialBase.getUnit()==null?"":materialBase.getUnit());
            }
        }
        return Result.OK(pageList);
    }
}
