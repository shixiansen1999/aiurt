package com.aiurt.modules.subsystem.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.fault.dto.FaultSystemDeviceSumDTO;
import com.aiurt.modules.fault.dto.FaultSystemTimesDTO;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: cs_subsystem
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface CsSubsystemMapper extends BaseMapper<CsSubsystem> {

    /**
     * @param startTime
     * @param endTime
     * @param systemCode
     * @return
     */
    List<FaultSystemTimesDTO> getSystemFaultSumBySystemCode(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("systemCode")String systemCode);

    /**
     * @param systemCode
     * @return
     */
    List<FaultSystemDeviceSumDTO> getLineSystemBySystemCode(@Param("systemCode")String systemCode);
}
