package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.modules.patrol.utils.NumberGenerateUtils;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.MaterialBase;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockInOrderLevel2;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockInOrderLevel2Detail;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2DTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2Excel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.MaterialVO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockInOrderLevel2VO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.StockInOrderLevel2Mapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IMaterialBaseService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockInOrderLevel2DetailService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockInOrderLevel2Service;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 二级入库单信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
@Service
public class StockInOrderLevel2ServiceImpl
        extends ServiceImpl<StockInOrderLevel2Mapper, StockInOrderLevel2> implements IStockInOrderLevel2Service {

    @Resource
    private IMaterialBaseService iMaterialBaseService;
    @Resource
    private IStockLevel2Service iStockLevel2Service;
    @Resource
    private IStockInOrderLevel2DetailService iStockInOrderLevel2DetailService;
    @Resource
    private StockInOrderLevel2Mapper stockInOrderLevel2Mapper;
    @Resource
    private ISysBaseAPI iSysBaseAPI;
    @Resource
    private NumberGenerateUtils numberGenerateUtils;


    /**
     * 添加入库单-添加
     * @param stockInOrderLevel2DTO
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addWarehouseIn(StockInOrderLevel2DTO stockInOrderLevel2DTO, HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        //模拟入库单号
        String before = "IS101";
        String codeNo = numberGenerateUtils.getCodeNo(before);
        StockInOrderLevel2 stockInOrderLevel2 = new StockInOrderLevel2();
        stockInOrderLevel2.setOrderCode(codeNo);
        stockInOrderLevel2.setStockInTime(stockInOrderLevel2DTO.getStockInTime());
        stockInOrderLevel2.setWarehouseCode(stockInOrderLevel2DTO.getWarehouseCode());
        stockInOrderLevel2.setNote(stockInOrderLevel2DTO.getNote());
        stockInOrderLevel2.setUpdateBy(userId);
        stockInOrderLevel2.setCreateBy(userId);
        stockInOrderLevel2Mapper.insert(stockInOrderLevel2);

        //新增入库单添加的物资信息
        List<MaterialVO> materialVOList = stockInOrderLevel2DTO.getMaterialVOList();

        List<StockDTO> dtoList = new ArrayList<>();
        materialVOList.forEach(e->{
            MaterialBase materialBase = iMaterialBaseService.getOne(
                    new QueryWrapper<MaterialBase>().eq(MaterialBase.CODE, e.getMaterialCode()), false);
            if(ObjectUtil.isNotEmpty(materialBase)){
                //去库存查找该物料的库存信息,为空则插入一条数据，否则更新库存
                StockLevel2 one = iStockLevel2Service.getOne(new QueryWrapper<StockLevel2>()
                        .eq(StockLevel2.MATERIAL_CODE, materialBase.getCode())
                        .eq(StockLevel2.WAREHOUSE_CODE, stockInOrderLevel2.getWarehouseCode()),false);
                if(ObjectUtil.isEmpty(one)){
                    StockLevel2 stockLevel2 = new StockLevel2();
                    BeanUtils.copyProperties(materialBase,stockLevel2);
                    BeanUtils.copyProperties(stockInOrderLevel2,stockLevel2);
                    stockLevel2.setMaterialCode(materialBase.getCode());
                    stockLevel2.setNum(e.getMaterialNum());
                    stockLevel2.setStockInTime(stockInOrderLevel2.getStockInTime());
                    stockLevel2.setUpdateBy(userId);
                    iStockLevel2Service.save(stockLevel2);
                }else{
                    one.setStockInTime(stockInOrderLevel2.getStockInTime());
                    one.setNum(one.getNum());
                    one.setUpdateBy(userId);
                    iStockLevel2Service.updateById(one);
                }

                //插入入库单的详情信息
                StockInOrderLevel2Detail level2Detail = new StockInOrderLevel2Detail();
                level2Detail.setOrderId(stockInOrderLevel2.getId());
                level2Detail.setOrderCode(stockInOrderLevel2.getOrderCode());
                level2Detail.setMaterialCode(e.getMaterialCode());
                level2Detail.setNum(e.getMaterialNum());
                level2Detail.setCreateBy(userId);
                level2Detail.setUpdateBy(userId);
                iStockInOrderLevel2DetailService.save(level2Detail);
                if (level2Detail.getId() != null){
                    dtoList.add(new StockDTO().setId(level2Detail.getId()).setNum(level2Detail.getNum()));
                }
            }
        });
        if (CollectionUtils.isNotEmpty(dtoList)) {
            iStockInOrderLevel2DetailService.addNumById(dtoList);
        }
        return codeNo;
    }

    /**
     * 二级入库单信息-分页列表查询
     * @param page
     * @param stockInOrderLevel2
     * @param startTime 入库时间范围开始时间
     * @param endTime 入库时间范围结束时间时间
     * @return
     */
    @Override
    public IPage<StockInOrderLevel2VO> queryPageList(Page<StockInOrderLevel2VO> page,
                                                     StockInOrderLevel2 stockInOrderLevel2,
                                                     String startTime,
                                                     String endTime) {
        IPage<StockInOrderLevel2VO> stockInOrderLevel2VOIPage = stockInOrderLevel2Mapper.queryPageList(page, stockInOrderLevel2, startTime, endTime);
        return stockInOrderLevel2VOIPage;
    }

    /**
     * 入库列表导出
     * @param selections 选择行的ids
     * @return
     */
    @Override
    public List<StockInOrderLevel2Excel> selectExcelData(List<Integer> selections) {
        List<StockInOrderLevel2Excel> list = stockInOrderLevel2Mapper.selectExcelData(selections);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
        }
        return list;
    }
}
