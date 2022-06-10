package com.aiurt.boot.modules.fault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.result.FaultRepairRecordResult;
import com.swsc.copsms.common.result.SpareResult;
import com.swsc.copsms.modules.fault.dto.FaultRepairRecordDTO;
import com.swsc.copsms.modules.fault.entity.FaultRepairRecord;

import java.util.List;

/**
 * @Description: 故障维修记录表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IFaultRepairRecordService extends IService<FaultRepairRecord> {
    /**
     * 指派
     * @param code
     * @param appointUserId
     * @param workType
     * @param planOrderCode
     * @param planOrderImg
     * @return
     */
    public Result assign(String code, String appointUserId, String workType, String planOrderCode, String planOrderImg);

    /**
     * 指派
     * @param code
     * @param appointUserId
     * @param workType
     * @param planOrderCode
     * @param planOrderImg
     * @return
     */
    public Result assignAgain(String code, String appointUserId, String workType, String planOrderCode, String planOrderImg);

    /**
     * 根据code查询故障维修记录
     * @param code
     * @return
     */
    List<FaultRepairRecordResult> getRepairRecord(String code);

    /**
     * app填写维修记录
     * @param dto
     * @return
     */
    public Result addRecord(FaultRepairRecordDTO dto);

    /**
     * 根据故障编号查询更换备件
     * @param code
     * @return
     */
    List<SpareResult> changeSpare(String code);

}
