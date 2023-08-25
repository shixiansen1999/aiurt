package com.aiurt.boot.standard.service;

import com.aiurt.boot.standard.entity.InspectionCodeDeviceType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: inspection_code_device_type
 * @Author: aiurt
 * @Date:   2023-08-24
 * @Version: V1.0
 */
public interface IInspectionCodeDeviceTypeService extends IService<InspectionCodeDeviceType> {

    /**
     * @param code
     * @return
     */
    List<InspectionCodeDeviceType> queryByInspectionCode(String code);

}
