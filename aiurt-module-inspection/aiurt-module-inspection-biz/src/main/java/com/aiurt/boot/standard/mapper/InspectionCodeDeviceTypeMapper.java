package com.aiurt.boot.standard.mapper;


import com.aiurt.boot.standard.entity.InspectionCodeDeviceType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: inspection_code_device_type
 * @Author: aiurt
 * @Date:   2023-08-24
 * @Version: V1.0
 */
public interface InspectionCodeDeviceTypeMapper extends BaseMapper<InspectionCodeDeviceType> {

    /**
     * @param code
     * @return
     */
    List<InspectionCodeDeviceType> queryByInspectionCode(String code);

}
