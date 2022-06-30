package com.aiurt.modules.fault.service;

import com.aiurt.modules.fault.entity.FaultRepairParticipants;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 故障参与人
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
public interface IFaultRepairParticipantsService extends IService<FaultRepairParticipants> {

    /**
     * 根据维修记录查询参与人员
     * @param recordId 维修解落id
     * @return
     */
    List<FaultRepairParticipants> queryParticipantsByRecordId(String recordId);

}
