package com.aiurt.modules.fault.mapper;


import com.aiurt.modules.fault.dto.FaultDeviceRepairDTO;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: fault_device
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface FaultDeviceMapper extends BaseMapper<FaultDevice> {

    /**
     * 根据故障编码
     * @param faultCode
     * @return
     */
    List<FaultDevice> queryByFaultCode(String faultCode);

    /**
     * 分页查询设备送修
     * @param page
     * @param FaultDeviceRepairDTO
     * @return
     */
    IPage<FaultDeviceRepairDTO> queryRepairDeviceList(@Param("page") Page<FaultDeviceRepairDTO> page,
                                                      @Param("condition") FaultDeviceRepairDTO FaultDeviceRepairDTO);

    /**
     * 根据故障接报人查找所在部门工班长的用户
     * @param receiveUserName
     * @return
     */
    List<String> queryUserId(String receiveUserName);

    /**
     * 查询角色为送修经办人的用户
     * @return
     */
    String queryRepairUserName();
}
