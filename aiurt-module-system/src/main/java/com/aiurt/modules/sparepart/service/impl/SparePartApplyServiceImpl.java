package com.aiurt.modules.sparepart.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.enums.MaterialApplyStatusEnum;
import com.aiurt.common.enums.MaterialTypeEnum;
import com.aiurt.modules.position.mapper.CsStationPositionMapper;
import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.entity.SparePartApplyMaterial;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.entity.dto.StockApplyExcel;
import com.aiurt.modules.sparepart.mapper.SparePartApplyMapper;
import com.aiurt.modules.sparepart.mapper.SparePartApplyMaterialMapper;
import com.aiurt.modules.sparepart.service.ISparePartApplyMaterialService;
import com.aiurt.modules.sparepart.service.ISparePartApplyService;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.aiurt.modules.stock.entity.StockOutOrderLevel2;
import com.aiurt.modules.stock.entity.StockOutboundMaterials;
import com.aiurt.modules.stock.entity.StockSubmitPlan;
import com.aiurt.modules.stock.mapper.StockOutOrderLevel2Mapper;
import com.aiurt.modules.stock.mapper.StockOutboundMaterialsMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description: spare_part_apply
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Service
public class SparePartApplyServiceImpl extends ServiceImpl<SparePartApplyMapper, SparePartApply> implements ISparePartApplyService {
    @Autowired
    private SparePartApplyMapper sparePartApplyMapper;
    @Autowired
    private ISparePartApplyMaterialService sparePartApplyMaterialService;
    @Autowired
    private StockOutboundMaterialsMapper stockOutboundMaterialsMapper;
    @Autowired
    private ISparePartStockInfoService sparePartStockInfoService;
    @Autowired
    private StockOutOrderLevel2Mapper stockOutOrderLevel2Mapper;
    /**
     * 分页列表查询
     * @param page
     * @param sparePartApply
     * @return
     */
    @Override
    public List<SparePartApply> selectList(Page page, SparePartApply sparePartApply){
        return sparePartApplyMapper.readAll(page,sparePartApply);
    }
    /**
     * 添加
     *
     * @param sparePartApply
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(SparePartApply sparePartApply) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //申领单号 自动生成
        String code = getCode();
        sparePartApply.setCode(code);
        sparePartApply.setApplyUserId(user.getUsername());
        LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SparePartStockInfo::getOrganizationId,user.getOrgId());
        wrapper.eq(SparePartStockInfo::getDelFlag,CommonConstant.DEL_FLAG_0);
        SparePartStockInfo info = sparePartStockInfoService.getOne(wrapper);
        if(null!=info && null!=info.getWarehouseCode()){
            sparePartApply.setCustodialWarehouseCode(info.getWarehouseCode());
        }
        sparePartApplyMapper.insert(sparePartApply);
        //物资清单
        if(!sparePartApply.getStockLevel2List().isEmpty()){
            sparePartApply.getStockLevel2List().stream().forEach(sparePartApplyMaterial ->{
                sparePartApplyMaterial.setApplyId(sparePartApply.getId());
                sparePartApplyMaterial.setWarehouseCode(sparePartApply.getApplyWarehouseCode());
                sparePartApplyMaterial.setApplyCode(code);
            });
            sparePartApplyMaterialService.saveBatch(sparePartApply.getStockLevel2List());
        }

        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param sparePartApply
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartApply sparePartApply) {
        SparePartApply partApply = getById(sparePartApply.getId());
        //删除原有物资
        QueryWrapper<SparePartApplyMaterial> queryWrapper = new QueryWrapper<SparePartApplyMaterial>();
        queryWrapper.eq("apply_code",partApply.getCode());
        sparePartApplyMaterialService.remove(queryWrapper);
        //物资清单
        if(!sparePartApply.getStockLevel2List().isEmpty()){
            sparePartApply.getStockLevel2List().stream().forEach(sparePartApplyMaterial ->{
                sparePartApplyMaterial.setApplyId(sparePartApply.getId());
                sparePartApplyMaterial.setWarehouseCode(sparePartApply.getApplyWarehouseCode());
                sparePartApplyMaterial.setApplyCode(partApply.getCode());
            });
            sparePartApplyMaterialService.saveBatch(sparePartApply.getStockLevel2List());
        }
        sparePartApplyMapper.updateById(sparePartApply);
        return Result.OK("编辑成功！");
    }
    /**
     * 提交
     *
     * @param sparePartApply
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> submit(SparePartApply sparePartApply) {
        SparePartApply partApply = getById(sparePartApply.getId());
        //1.修改状态为“待确认”
        sparePartApplyMapper.updateById(sparePartApply);
        //2.插入二级库出库表
        StockOutOrderLevel2 stockOutOrderLevel = new StockOutOrderLevel2();
        //生成出库单号
        String code = getStockOutCode();
        //EJCK+日期+自增3位
        stockOutOrderLevel.setOrderCode(code);
        //出库仓库为申领仓库
        stockOutOrderLevel.setWarehouseCode(partApply.getApplyWarehouseCode());
        //出库人为出库确认人，保管人为备件申领人
        stockOutOrderLevel.setCustodialId(partApply.getApplyUserId());
        stockOutOrderLevel.setCustodialWarehouseCode(partApply.getCustodialWarehouseCode());
        stockOutOrderLevel2Mapper.insert(stockOutOrderLevel);
        //3.插入出库物资
        List<SparePartApplyMaterial> list = sparePartApplyMaterialService.list(new LambdaQueryWrapper<SparePartApplyMaterial>().eq(SparePartApplyMaterial::getApplyId,sparePartApply.getId()));
        list.forEach(applyMaterial ->{
            StockOutboundMaterials stockOutboundMaterials = new StockOutboundMaterials();
            stockOutboundMaterials.setOutOrderCode(applyMaterial.getOrderCode());
            stockOutboundMaterials.setMaterialCode(applyMaterial.getMaterialCode());
            stockOutboundMaterials.setWarehouseCode(partApply.getApplyWarehouseCode());
            stockOutboundMaterials.setInventory(applyMaterial.getInventory());
            stockOutboundMaterials.setApplyOutput(applyMaterial.getApplyNum());
            stockOutboundMaterialsMapper.insert(stockOutboundMaterials);
        });
        return Result.OK("编辑成功！");
    }

    /**
     * 生成申领单号
     * @param
     * @return
     */
    @Override
    public String getCode() {
        LambdaQueryWrapper<SparePartApply> queryWrapper = new LambdaQueryWrapper<>();
        String str = "SL";
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        str += date.format(new Date());
        queryWrapper.eq(SparePartApply::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.likeRight(SparePartApply::getCreateTime,str);
        queryWrapper.orderByDesc(SparePartApply::getCreateTime);
        queryWrapper.last("limit 1");
        SparePartApply sparePartApply = sparePartApplyMapper.selectOne(queryWrapper);
        String format = "";
        if(sparePartApply != null){
            String code = sparePartApply.getCode();
            String numstr = code.substring(code.length()-3);
            format = String.format("%03d", Long.parseLong(numstr) + 1);
        }else{
            format = "001";
        }
        String code = str + format;
        return code;
    }
    /**
     * 生成出库单号
     * @param
     * @return
     */
    @Override
    public String getStockOutCode() {
        LambdaQueryWrapper<StockOutOrderLevel2> queryWrapper = new LambdaQueryWrapper<>();
        String str = "EJCK";
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        str += date.format(new Date());
        queryWrapper.eq(StockOutOrderLevel2::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.likeRight(StockOutOrderLevel2::getCreateTime,str);
        queryWrapper.orderByDesc(StockOutOrderLevel2::getCreateTime);
        queryWrapper.last("limit 1");
        StockOutOrderLevel2 orderLevel2 = stockOutOrderLevel2Mapper.selectOne(queryWrapper);
        String format = "";
        if(orderLevel2 != null){
            String code = orderLevel2.getOrderCode();
            String numstr = code.substring(code.length()-3);
            format = String.format("%03d", Long.parseLong(numstr) + 1);
        }else{
            format = "001";
        }
        String code = str + format;
        return code;
    }
    @Override
    public List<StockApplyExcel> exportXls(List<String> ids) {
        List<StockApplyExcel> excelList = sparePartApplyMapper.selectExportXls(ids);
        AtomicReference<Integer> flag= new AtomicReference<>(1);
        excelList.forEach(e->{
            e.setSerialNumber(flag.getAndSet(flag.get() + 1));
        });
        return excelList;
    }
}
