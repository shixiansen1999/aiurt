package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.enums.MaterialLendStatus;
import com.aiurt.common.enums.MaterialTypeEnum;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartLend;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.dto.SparePartLendExcel;
import com.aiurt.modules.sparepart.entity.dto.SparePartLendQuery;
import com.aiurt.modules.sparepart.entity.vo.SparePartLendVO;
import com.aiurt.modules.sparepart.mapper.SparePartLendMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockInfoMapper;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.sparepart.service.ISparePartLendService;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 备件借出表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Service
public class SparePartLendServiceImpl extends ServiceImpl<SparePartLendMapper, SparePartLend> implements ISparePartLendService {

    @Resource
    private SparePartLendMapper sparePartLendMapper;
    @Resource
    private ISparePartStockService iSparePartStockService;

    @Resource
    private SparePartStockInfoMapper sparePartStockInfoMapper;
    @Resource
    private ISparePartInOrderService iSparePartInOrderService;


    @Override
    public IPage<SparePartLendVO> queryPageList(Page<SparePartLendVO> page, SparePartLendQuery sparePartLendQuery) {
        IPage<SparePartLendVO> pageList=sparePartLendMapper.queryPageList(page,sparePartLendQuery);
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addLend(Result<?> result, SparePartLend sparePartLend) {
        //判断库存够不够
        SparePartStock one = iSparePartStockService.getOne(new QueryWrapper<SparePartStock>()
                .eq("warehouse_code", sparePartLend.getWarehouseCode())
                .eq("material_code", sparePartLend.getMaterialCode()), false);
        if(ObjectUtil.isNotEmpty(one)){
            if(one.getNum()<sparePartLend.getLendNum()){
                return result.error500("备件："+sparePartLend.getMaterialCode()+" 库存不足");
            }
        }
        //新增借出记录
        sparePartLend.setStatus(MaterialLendStatus.OFF_THE_STOCK.getCode());
        sparePartLendMapper.insert(sparePartLend);
        //新增出库信息
       /* SparePartOutOrder sparePartOutOrder = new SparePartOutOrder();
        sparePartOutOrder.setMaterialCode(sparePartLend.getMaterialCode());
        sparePartOutOrder.setNum(sparePartLend.getLendNum());
        sparePartOutOrder.setWarehouseCode(sparePartLend.getWarehouseCode());
        iSparePartOutOrderService.save(sparePartOutOrder);*/
        //库存减少
        one.setNum(one.getNum()-sparePartLend.getLendNum());
        iSparePartStockService.updateById(one);
        return result.success("添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnMaterial(SparePartLend sparePartLendEntity, Integer returnNum) {
        //借出信息修改
        sparePartLendEntity.setStatus(MaterialLendStatus.RETURNED.getCode());
        sparePartLendEntity.setBackNum(returnNum);
        sparePartLendEntity.setBackTime(new DateTime(DateTime.now()));
        sparePartLendMapper.updateById(sparePartLendEntity);
        //新增入库信息
        SparePartInOrder sparePartInOrder = new SparePartInOrder();
        sparePartInOrder.setMaterialCode(sparePartLendEntity.getMaterialCode());
        sparePartInOrder.setNum(returnNum);
        sparePartInOrder.setWarehouseCode(sparePartLendEntity.getWarehouseCode());
        iSparePartInOrderService.save(sparePartInOrder);
        //库存增加
        SparePartStock one = iSparePartStockService.getOne(new QueryWrapper<SparePartStock>()
                .eq("warehouse_code", sparePartLendEntity.getWarehouseCode())
                .eq("material_code", sparePartLendEntity.getMaterialCode()), false);
        one.setNum(one.getNum()+returnNum);
        iSparePartStockService.updateById(one);
        return true;
    }

    @Override
    public List<SparePartLendExcel> exportXls(SparePartLendQuery sparePartLendQuery) {
        List<SparePartLendExcel> list=sparePartLendMapper.queryExportXls(sparePartLendQuery);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
            list.get(i).setStatusString(MaterialLendStatus.getNameByCode(list.get(i).getStatus()));
            list.get(i).setTypeName(MaterialTypeEnum.getNameByCode(list.get(i).getType()));
        }
        return list;
    }

}
