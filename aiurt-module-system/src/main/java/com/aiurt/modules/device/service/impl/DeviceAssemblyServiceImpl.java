package com.aiurt.modules.device.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.device.dto.DeviceAssemblyDTO;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.mapper.DeviceAssemblyMapper;
import com.aiurt.modules.device.service.IDeviceAssemblyService;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;


/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class DeviceAssemblyServiceImpl extends ServiceImpl<DeviceAssemblyMapper, DeviceAssembly> implements IDeviceAssemblyService {
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private DeviceAssemblyMapper deviceAssemblyMapper;
    @Override
    public List<DeviceAssembly> fromMaterialToAssembly(List<MaterialBase> materialBaseList) {
        List<DeviceAssembly> deviceAssemblyList = new ArrayList<>();
        QueryWrapper<DeviceAssembly> queryWrapper = new QueryWrapper();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        List<DeviceAssembly> deviceAssemblies = deviceAssemblyMapper.selectList(queryWrapper);
        Set<String> assemblyCodeSet = deviceAssemblies.stream().map(DeviceAssembly::getCode).collect(Collectors.toSet());
        if(materialBaseList != null && materialBaseList.size()>0) {
            for (MaterialBase materialBase : materialBaseList) {
                if (ObjectUtils.isNotEmpty(materialBase.getAddNumber())) {
                    Integer num = 1;
                    for (int i = 0; i < materialBase.getAddNumber(); i++) {
                        DeviceAssembly deviceAssembly = new DeviceAssembly();
                        String code = materialBase.getCode();
                        String format = "";
                        do {
                            String number = String.format("%03d",num );
                            format = code+ number;
                            num = num + 1;
                        } while (assemblyCodeSet.contains(format));
                        assemblyCodeSet.add(format);
                        Device device = deviceService.getOne(new QueryWrapper<Device>().eq("code", materialBase.getDeviceCode()));
                        deviceAssembly.setDeviceCode(materialBase.getDeviceCode() == null ? "" : materialBase.getDeviceCode());
                        deviceAssembly.setCode(format);
                        deviceAssembly.setSpecifications(materialBase.getSpecifications() == null ? "" : materialBase.getSpecifications());
                        deviceAssembly.setMaterialCode(materialBase.getCode() == null ? "" : materialBase.getCode());
                        deviceAssembly.setBaseTypeCode(materialBase.getBaseTypeCode() == null ? "" : materialBase.getBaseTypeCode());
                        deviceAssembly.setManufactorCode(materialBase.getManufactorCode() == null ? "" : materialBase.getManufactorCode());
                        deviceAssembly.setPrice(materialBase.getPrice() == null ? "" : materialBase.getPrice());
                        deviceAssembly.setDeviceTypeCode(device.getDeviceTypeCode() == null ? "" : device.getDeviceTypeCode());
                        deviceAssembly.setUnit(materialBase.getUnit() == null ? "" : materialBase.getUnit());
                        deviceAssembly.setMaterialName(materialBase.getName());
                        deviceAssembly.setConsumablesType(materialBase.getConsumablesType());
                        deviceAssemblyList.add(deviceAssembly);
                    }
                } else {
                    DeviceAssembly deviceAssembly = new DeviceAssembly();
                    String code = materialBase.getCode();
                    String format = "";
                    Integer num = 1;
                    do {
                        String number = String.format("%03d",num );
                        format = code+ number;
                        num = num + 1;
                    } while (assemblyCodeSet.contains(format));
                    Device device = deviceService.getOne(new QueryWrapper<Device>().eq("code", materialBase.getDeviceCode()));
                    deviceAssembly.setDeviceCode(materialBase.getDeviceCode() == null ? "" : materialBase.getDeviceCode());
                    deviceAssembly.setCode(format);
                    deviceAssembly.setSpecifications(materialBase.getSpecifications() == null ? "" : materialBase.getSpecifications());
                    deviceAssembly.setMaterialCode(materialBase.getCode() == null ? "" : materialBase.getCode());
                    deviceAssembly.setBaseTypeCode(materialBase.getBaseTypeCode() == null ? "" : materialBase.getBaseTypeCode());
                    deviceAssembly.setManufactorCode(materialBase.getManufactorCode() == null ? "" : materialBase.getManufactorCode());
                    deviceAssembly.setPrice(materialBase.getPrice() == null ? "" : materialBase.getPrice());
                    deviceAssembly.setDeviceTypeCode(device.getDeviceTypeCode() == null ? "" : device.getDeviceTypeCode());
                    deviceAssembly.setUnit(materialBase.getUnit() == null ? "" : materialBase.getUnit());
                    deviceAssembly.setMaterialName(materialBase.getName());
                    deviceAssembly.setConsumablesType(materialBase.getConsumablesType());
                    deviceAssemblyList.add(deviceAssembly);
                }
            }
        }
        return deviceAssemblyList;
    }

    /**
     * 根据设备编码查询组件
     *
     * @param deviceCode
     * @return
     */
    @Override
    public List<DeviceAssemblyDTO> queryDeviceAssemblyByDeviceCode(String deviceCode) {
        if (StrUtil.isBlank(deviceCode)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<DeviceAssembly> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceAssembly::getDelFlag, CommonConstant.DEL_FLAG_0).eq(DeviceAssembly::getStatus, 0)
        .eq(DeviceAssembly::getDeviceCode, deviceCode);
        List<DeviceAssembly> list = list(queryWrapper);
        List<DeviceAssemblyDTO> resultList = list.stream().map(deviceAssembly -> {
           DeviceAssemblyDTO build = DeviceAssemblyDTO.builder()
                    .key(deviceAssembly.getId())
                    .code(deviceAssembly.getCode())
                    .label(String.format("%s-%s", deviceAssembly.getMaterialName(), deviceAssembly.getCode()))
                    .title(String.format("%s-%s", deviceAssembly.getMaterialName(), deviceAssembly.getCode()))
                    .materialName(deviceAssembly.getMaterialName()).value(deviceAssembly.getCode()).build();
            return build;
        }).collect(Collectors.toList());
        return resultList;
    }
}
