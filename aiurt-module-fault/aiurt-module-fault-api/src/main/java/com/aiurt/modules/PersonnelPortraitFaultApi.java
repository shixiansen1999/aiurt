package com.aiurt.modules;

import com.aiurt.modules.fault.dto.FaultMaintenanceDTO;

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

}
