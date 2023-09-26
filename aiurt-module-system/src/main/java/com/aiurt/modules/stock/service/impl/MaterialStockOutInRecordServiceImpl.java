package com.aiurt.modules.stock.service.impl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.material.mapper.MaterialRequisitionMapper;
import com.aiurt.modules.stock.dto.req.MaterialStockOutInRecordReqDTO;
import com.aiurt.modules.stock.dto.resp.MaterialStockOutInRecordRespDTO;
import com.aiurt.modules.stock.entity.MaterialStockOutInRecord;
import com.aiurt.modules.stock.entity.StockInOrderLevel2;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.aiurt.modules.stock.mapper.MaterialStockOutInRecordMapper;
import com.aiurt.modules.stock.mapper.StockInOrderLevel2Mapper;
import com.aiurt.modules.stock.mapper.StockIncomingMaterialsMapper;
import com.aiurt.modules.stock.service.IMaterialStockOutInRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 出入库记录表service的实现类
 *
 * @author 华宜威
 * @date 2023-09-18 16:55:06
 */
@Service
public class MaterialStockOutInRecordServiceImpl extends ServiceImpl<MaterialStockOutInRecordMapper, MaterialStockOutInRecord> implements IMaterialStockOutInRecordService {

    @Autowired
    private StockInOrderLevel2Mapper stockInOrderLevel2Mapper;
    @Autowired
    private StockIncomingMaterialsMapper stockIncomingMaterialsMapper;
    @Autowired
    private MaterialRequisitionMapper materialRequisitionMapper;

    @Override
    public IPage<MaterialStockOutInRecordRespDTO> pageList(MaterialStockOutInRecordReqDTO materialStockOutInRecordReqDTO) {
        // 将请求DTO转化成实体类进行查询
        int pageNo = materialStockOutInRecordReqDTO.getPageNo();
        int pageSize = materialStockOutInRecordReqDTO.getPageSize();
        Page<MaterialStockOutInRecord> page = new Page<>(pageNo, pageSize);

        MaterialStockOutInRecord materialStockOutInRecord = new MaterialStockOutInRecord();
        BeanUtils.copyProperties(materialStockOutInRecordReqDTO, materialStockOutInRecord);

        QueryWrapper<MaterialStockOutInRecord> queryWrapper = QueryGenerator.initQueryWrapper(materialStockOutInRecord, null);
        // 时间范围搜索
        Date searchBeginTime = materialStockOutInRecordReqDTO.getSearchBeginTime();
        Date searchEndTime = materialStockOutInRecordReqDTO.getSearchEndTime();
        DateTime beginTime = searchBeginTime != null ? DateUtil.beginOfDay(searchBeginTime) : null;
        DateTime endTime = searchEndTime != null ? DateUtil.endOfDay(searchEndTime) : null;
        queryWrapper.lambda().ge(searchBeginTime != null, MaterialStockOutInRecord::getConfirmTime, beginTime);
        queryWrapper.lambda().le(searchEndTime != null, MaterialStockOutInRecord::getConfirmTime, endTime);
        //出入库类型查询
        Integer materialRequisitionType = materialStockOutInRecordReqDTO.getMaterialRequisitionType();
        Integer isOutIn = materialStockOutInRecordReqDTO.getIsOutIn();
        queryWrapper.lambda().eq(ObjectUtil.isNotNull(materialRequisitionType), MaterialStockOutInRecord::getMaterialRequisitionType, materialRequisitionType);
        queryWrapper.lambda().eq(ObjectUtil.isNotNull(isOutIn), MaterialStockOutInRecord::getIsOutIn, isOutIn);

        queryWrapper.lambda().eq(MaterialStockOutInRecord::getDelFlag, CommonConstant.DEL_FLAG_0);
        this.page(page, queryWrapper);

        // 将实体类查询结果转化成响应DTO
        Page<MaterialStockOutInRecordRespDTO> pageList = new Page<>();
        BeanUtils.copyProperties(page, pageList);
        List<MaterialStockOutInRecordRespDTO> pageRecords = page.getRecords().stream().map(record -> {
            MaterialStockOutInRecordRespDTO respDTO = new MaterialStockOutInRecordRespDTO();
            BeanUtils.copyProperties(record, respDTO);
            return respDTO;
        }).collect(Collectors.toList());
        pageList.setRecords(pageRecords);
        return pageList;
    }


    @Override
    public void addInRecordFormLevel2(String id) {
        StockInOrderLevel2 stockInOrderLevel2 = stockInOrderLevel2Mapper.selectById(id);
        LambdaQueryWrapper<StockIncomingMaterials> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StockIncomingMaterials::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.eq(StockIncomingMaterials::getInOrderCode, stockInOrderLevel2.getOrderCode());
        List<StockIncomingMaterials> stockIncomingMaterialsList = stockIncomingMaterialsMapper.selectList(queryWrapper);
        this.addInRecordFormLevel2(stockInOrderLevel2, stockIncomingMaterialsList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addInRecordFormLevel2(StockInOrderLevel2 stockInOrderLevel2, List<StockIncomingMaterials> stockIncomingMaterialsList) {
        // 是否有申领单
        String materialRequisitionId = stockInOrderLevel2.getMaterialRequisitionId();
        String materialRequisitionCode = null;
        Integer materialRequisitionType = null;
        if (StrUtil.isNotEmpty(materialRequisitionId)) {
            MaterialRequisition materialRequisition = materialRequisitionMapper.selectById(materialRequisitionId);
            if (materialRequisition != null){
                materialRequisitionCode = materialRequisition.getCode();
                materialRequisitionType = materialRequisition.getMaterialRequisitionType();
            }
        }

        // 根据二级库入库单，以及入库清单，生成对应的出入库记录表的实体类
        String finalMaterialRequisitionCode = materialRequisitionCode;
        Integer finalMaterialRequisitionType = materialRequisitionType;
        List<MaterialStockOutInRecord> recordList = stockIncomingMaterialsList.stream().map(stockIncomingMaterials -> {
            MaterialStockOutInRecord record = new MaterialStockOutInRecord();
            record.setMaterialCode(stockIncomingMaterials.getMaterialCode());
            record.setWarehouseCode(stockInOrderLevel2.getWarehouseCode());
            record.setNum(stockIncomingMaterials.getNumber());
            record.setConfirmTime(stockInOrderLevel2.getEntryTime());
            record.setConfirmUserId(stockInOrderLevel2.getUserId());
            record.setOrderId(stockInOrderLevel2.getId());
            record.setOrderCode(stockInOrderLevel2.getOrderCode());
            // 入库单提交了就算确认
            record.setStatus(2);
            record.setMaterialRequisitionId(materialRequisitionId);
            record.setMaterialRequisitionCode(finalMaterialRequisitionCode);
            record.setMaterialRequisitionType(finalMaterialRequisitionType);
            record.setIsOutIn(1);
            record.setOutInType(stockInOrderLevel2.getInType());
            record.setBalance(stockIncomingMaterials.getBalance());
            record.setRemarks(stockInOrderLevel2.getNote());
            return record;
        }).collect(Collectors.toList());
        this.saveBatch(recordList);
    }
}
