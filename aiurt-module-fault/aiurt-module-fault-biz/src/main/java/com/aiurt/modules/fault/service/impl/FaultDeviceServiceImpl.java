package com.aiurt.modules.fault.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.mapper.FaultDeviceMapper;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: fault_device
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Service
public class FaultDeviceServiceImpl extends ServiceImpl<FaultDeviceMapper, FaultDevice> implements IFaultDeviceService {
@Autowired
private ISysBaseAPI iSysBaseAPI;
    @Override
    public List<FaultDevice> queryByFaultCode(String faultCode) {
        List<FaultDevice> faultDeviceList = baseMapper.queryByFaultCode(faultCode);
        faultDeviceList.forEach(faultDevice ->{
            if(ObjectUtil.isNotEmpty(faultDevice.getMaterialCodes())){
                String materialNames = iSysBaseAPI.getMaterialNameByCodes(faultDevice.getMaterialCodes());
                faultDevice.setMaterialNames(materialNames);
            }
        } );
        return faultDeviceList;
    }
}
