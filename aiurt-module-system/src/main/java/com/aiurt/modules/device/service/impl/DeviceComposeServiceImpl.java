package com.aiurt.modules.device.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.device.dto.DeviceComposeTreeDTO;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.mapper.DeviceComposeMapper;
import com.aiurt.modules.device.service.IDeviceComposeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: device_compose
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Service
public class DeviceComposeServiceImpl extends ServiceImpl<DeviceComposeMapper, DeviceCompose> implements IDeviceComposeService {

    /**
     * @param deviceTypeCode
     * @return
     */
    @Override
    public List<DeviceComposeTreeDTO> queryComposeTree(String deviceTypeCode) {
        if (StrUtil.isBlank(deviceTypeCode)) {
            return Collections.emptyList();
        }
        // 查询数据库
        List<DeviceCompose> deviceComposeList = baseMapper.queryByDeviceTypeCode(deviceTypeCode);

        if (CollUtil.isEmpty(deviceComposeList)) {
            return Collections.emptyList();
        }

        Map<String, DeviceCompose> baseTypeNameMap = deviceComposeList.stream().collect(Collectors.toMap(DeviceCompose::getBaseTypeCode,t->t, (t1,t2)->t1));

        Map<String, List<DeviceCompose>> baseTypeListMap = deviceComposeList.stream().collect(Collectors.groupingBy(DeviceCompose::getBaseTypeCode));

        List<DeviceComposeTreeDTO> resultList = new ArrayList<>();
        baseTypeListMap.forEach((code, list) -> {
            DeviceCompose deviceCompose = baseTypeNameMap.get(code);
            if (Objects.isNull(deviceCompose)) {
                return;
            }
            DeviceComposeTreeDTO dto = new DeviceComposeTreeDTO();
            dto.setId(deviceCompose.getBaseTypeId());
            dto.setKey(deviceCompose.getBaseTypeId());
            dto.setLabel(deviceCompose.getBaseTyeName());
            dto.setPid("0");
            dto.setValue(deviceCompose.getBaseTypeCode());
            dto.setTitle(deviceCompose.getBaseTyeName());
            dto.setIsLeaf(false);
            dto.setChildren(Collections.emptyList());
            if (CollUtil.isNotEmpty(list)) {
                List<DeviceComposeTreeDTO> dtoList = list.stream().map(compose -> {
                    DeviceComposeTreeDTO chilren = new DeviceComposeTreeDTO();
                    chilren.setId(compose.getId());
                    chilren.setPid(deviceCompose.getBaseTypeId());
                    chilren.setValue(compose.getMaterialCode());
                    chilren.setTitle(compose.getMaterialName());
                    chilren.setIsLeaf(true);
                    chilren.setChildren(Collections.emptyList());
                    chilren.setLabel(compose.getMaterialName());
                    chilren.setKey(compose.getId());
                    return chilren;
                }).collect(Collectors.toList());

                dto.setChildren(dtoList);
            }

            resultList.add(dto);

        });


        return resultList;
    }
}
