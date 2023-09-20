package com.aiurt.modules.stock.service.impl;


import com.aiurt.modules.stock.dto.MaterialStockOutInRecordReqDTO;
import com.aiurt.modules.stock.dto.MaterialStockOutInRecordRespDTO;
import com.aiurt.modules.stock.entity.MaterialStockOutInRecord;
import com.aiurt.modules.stock.mapper.MaterialStockOutInRecordMapper;
import com.aiurt.modules.stock.service.IMaterialStockOutInRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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

    @Override
    public IPage<MaterialStockOutInRecordRespDTO> pageList(MaterialStockOutInRecordReqDTO materialStockOutInRecordReqDTO) {
        // 将请求DTO转化成实体类进行查询
        int pageNo = materialStockOutInRecordReqDTO.getPageNo();
        int pageSize = materialStockOutInRecordReqDTO.getPageSize();
        Page<MaterialStockOutInRecord> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<MaterialStockOutInRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(materialStockOutInRecordReqDTO.getMaterialRequisitionType() != null, MaterialStockOutInRecord::getMaterialRequisitionType, materialStockOutInRecordReqDTO.getMaterialRequisitionType());
        queryWrapper.eq(materialStockOutInRecordReqDTO.getIsOutIn() != null, MaterialStockOutInRecord::getIsOutIn, materialStockOutInRecordReqDTO.getIsOutIn());
        queryWrapper.eq(materialStockOutInRecordReqDTO.getOutInType() != null, MaterialStockOutInRecord::getOutInType, materialStockOutInRecordReqDTO.getOutInType());
        queryWrapper.ge(materialStockOutInRecordReqDTO.getSearchBeginTime() != null, MaterialStockOutInRecord::getConfirmTime, materialStockOutInRecordReqDTO.getSearchBeginTime());
        queryWrapper.le(materialStockOutInRecordReqDTO.getSearchEndTime() != null, MaterialStockOutInRecord::getConfirmTime, materialStockOutInRecordReqDTO.getSearchEndTime());

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
}
