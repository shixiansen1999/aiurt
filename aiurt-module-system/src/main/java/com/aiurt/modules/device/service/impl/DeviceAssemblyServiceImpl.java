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

        if(materialBaseList != null && materialBaseList.size()>0) {
            for (MaterialBase materialBase : materialBaseList) {
                if (ObjectUtils.isNotEmpty(materialBase.getAddNumber())) {
                    for (int i = 0; i < materialBase.getAddNumber(); i++) {
                        DeviceAssembly deviceAssembly = new DeviceAssembly();
                        DeviceAssembly deviceAssemblyOld = deviceAssemblyMapper.selectOne(new LambdaQueryWrapper<DeviceAssembly>().likeRight(DeviceAssembly::getCode, materialBase.getCode())
                                .eq(DeviceAssembly::getDelFlag, 0).orderByDesc(DeviceAssembly::getCreateTime).last("limit 1"));
                        String code = materialBase.getCode();
                        String format = "";
                        if (deviceAssemblyOld != null) {
                            String codeold = deviceAssemblyOld.getCode();
                            String numstr = codeold.substring(codeold.length() - 3);
                            format = String.format("%03d", Long.parseLong(numstr) + 1 +materialBase.getAddNumber() - i-1);
                        } else {
                            format = "001";
                        }
                        Device device = deviceService.getOne(new QueryWrapper<Device>().eq("code", materialBase.getDeviceCode()));
                        deviceAssembly.setDeviceCode(materialBase.getDeviceCode() == null ? "" : materialBase.getDeviceCode());
                        deviceAssembly.setCode(code + format);
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
                    DeviceAssembly deviceAssemblyOld = deviceAssemblyMapper.selectOne(new LambdaQueryWrapper<DeviceAssembly>().likeRight(DeviceAssembly::getCode, materialBase.getCode())
                            .eq(DeviceAssembly::getDelFlag, 0).orderByDesc(DeviceAssembly::getCreateTime).last("limit 1"));
                    String code = materialBase.getCode();
                    String format = "";
                    if (deviceAssemblyOld != null) {
                        String codeold = deviceAssemblyOld.getCode();
                        String numstr = codeold.substring(codeold.length() - 3);
                        format = String.format("%03d", Long.parseLong(numstr) + 1);
                    } else {
                        format = "001";
                    }
                    Device device = deviceService.getOne(new QueryWrapper<Device>().eq("code", materialBase.getDeviceCode()));
                    deviceAssembly.setDeviceCode(materialBase.getDeviceCode() == null ? "" : materialBase.getDeviceCode());
                    deviceAssembly.setCode(code + format);
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
