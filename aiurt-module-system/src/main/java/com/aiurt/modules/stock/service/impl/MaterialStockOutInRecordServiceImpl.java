package com.aiurt.modules.stock.service.impl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.aiurt.modules.stock.dto.req.MaterialStockOutInRecordReqDTO;
import com.aiurt.modules.stock.dto.resp.MaterialStockOutInRecordRespDTO;
import com.aiurt.modules.stock.entity.MaterialStockOutInRecord;
import com.aiurt.modules.stock.mapper.MaterialStockOutInRecordMapper;
import com.aiurt.modules.stock.service.IMaterialStockOutInRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
