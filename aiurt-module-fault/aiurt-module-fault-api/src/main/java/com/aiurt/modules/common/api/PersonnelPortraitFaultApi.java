package com.aiurt.modules.common.api;

import com.aiurt.modules.fault.dto.FaultDeviceDTO;
import com.aiurt.modules.fault.dto.FaultHistoryDTO;
import com.aiurt.modules.fault.dto.FaultMaintenanceDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.baomidou.mybatisplus.core.metadata.IPage;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author
 * @description 报表统计-人员画像
 */
public interface PersonnelPortraitFaultApi {

    /**
     * 人员画像擅长维修统计
     *
     * @return
     */
    List<FaultMaintenanceDTO> personnelPortraitStatic(List<String> userIds);

    /**
     * 处理的设备TOP5
     */
    List<FaultHistoryDTO> repairDeviceTopFive(String userId);

    /**
     * 历史维修记录(更多)
     *
     * @param fault
     * @return
     */
    IPage<Fault> selectFaultRecordPageList(Fault fault, Integer pageNo, Integer pageSize, HttpServletRequest request);

    /**
     * 历史维修记录-设备故障信息列表
     *
     * @param userId
     * @return
     */
    List<FaultDeviceDTO> deviceInfo(String userId);
}
