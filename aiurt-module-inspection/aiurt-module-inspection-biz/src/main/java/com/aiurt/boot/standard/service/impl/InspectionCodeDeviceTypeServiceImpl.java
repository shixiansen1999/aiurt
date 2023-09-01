package com.aiurt.boot.standard.service.impl;

import com.aiurt.boot.standard.entity.InspectionCodeDeviceType;
import com.aiurt.boot.standard.mapper.InspectionCodeDeviceTypeMapper;
import com.aiurt.boot.standard.service.IInspectionCodeDeviceTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: inspection_code_device_type
 * @Author: aiurt
 * @Date:   2023-08-24
 * @Version: V1.0
 */
@Service
public class InspectionCodeDeviceTypeServiceImpl extends ServiceImpl<InspectionCodeDeviceTypeMapper, InspectionCodeDeviceType> implements IInspectionCodeDeviceTypeService {
    @Autowired
    private InspectionCodeDeviceTypeMapper inspectionCodeDeviceTypeMapper;

    @Override
    public List<InspectionCodeDeviceType> queryByInspectionCode(String code) {
        return inspectionCodeDeviceTypeMapper.queryByInspectionCode(code);
    }
}
