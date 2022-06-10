package com.aiurt.boot.modules.fault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.common.result.FaultRepairRecordResult;
import com.aiurt.boot.common.result.SpareResult;
import com.aiurt.boot.modules.fault.dto.FaultRepairDTO;
import com.aiurt.boot.modules.fault.dto.FaultRepairRecordDTO;
import com.aiurt.boot.modules.fault.entity.FaultRepairRecord;
import com.aiurt.boot.modules.fault.param.AssignParam;

import javax.servlet.http.HttpServletRequest;
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
     * @param param
     */
     Result assign(AssignParam param);

    /**
     * 指派
     * @param param
     */
     Result assignAgain(AssignParam param);

    /**
     * 根据code查询故障维修记录
     * @param code
     * @return
     */
    List<FaultRepairRecordResult> getRepairRecord(String code);

    /**
     * app填写维修记录
     * @param dto
     * @param req
     * @return
     */
     Result addRecord(FaultRepairRecordDTO dto,HttpServletRequest req);

    /**
     * 根据故障编号查询更换备件
     * @param code
     * @return
     */
    List<SpareResult> changeSpare(String code);

    /**
     * 查询最后一条维修记录
     * @param code
     * @param req
     * @return
     */
    FaultRepairRecordResult getDetail(String code,HttpServletRequest req);

    /**
     * 编辑维修记录
     * @param dto
     * @return
     */
    Result editRecord(FaultRepairDTO dto);

}
