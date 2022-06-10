package com.aiurt.boot.modules.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.device.entity.DeviceSmallType;
import com.aiurt.boot.modules.device.entity.DeviceType;
import com.aiurt.boot.modules.device.mapper.DeviceSmallTypeMapper;
import com.aiurt.boot.modules.device.mapper.DeviceTypeMapper;
import com.aiurt.boot.modules.device.service.IDeviceTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 设备分类
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class DeviceTypeServiceImpl extends ServiceImpl<DeviceTypeMapper, DeviceType> implements IDeviceTypeService {

    @Resource
    private DeviceTypeMapper deviceTypeMapper;

    @Resource
    private DeviceSmallTypeMapper deviceSmallTypeMapper;

    @Override
    public Integer existCode(String code) {
        Integer existFlag=deviceTypeMapper.getDeviceType(code);
        if(existFlag!=null && existFlag!=0){
            return 1;
        }
        return 0;
    }

    //已作废
    @Override
    public List<DeviceType> getDeviceTypeBySystemCode(String systemCode) {
        List<DeviceType> list=deviceTypeMapper.getDeviceTypeListBySystemCode(systemCode);
        list.forEach(l->{
            l.setSystemName(l.getName());

            final List<DeviceSmallType> smallTypes = deviceSmallTypeMapper.selectList(new LambdaQueryWrapper<DeviceSmallType>().eq(DeviceSmallType::getDeviceTypeId, l.getId()));
            l.setDeviceSmallTypeList(smallTypes);
        });// @TODO:这是什么操作
        return list;
    }
}
