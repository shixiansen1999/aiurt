package com.aiurt.modules.faultalarm.service;


import com.aiurt.modules.faultalarm.dto.req.AlmRecordReqDTO;
import com.aiurt.modules.faultalarm.dto.req.CancelAlarmReqDTO;
import com.aiurt.modules.faultalarm.dto.resp.AlmRecordRespDTO;
import com.aiurt.modules.faultalarm.entity.AlmRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 集中告警接口
 * @Author: wgp
 * @Date: 2023-06-05
 * @Version: V1.0
 */
public interface IFaultAlarmService extends IService<AlmRecord> {
    /**
     * 查询处理过的告警记录的分页列表
     *
     * @param almRecordReqDto 请求DTO，包含查询条件
     * @param pageNo          当前页码，默认为1
     * @param pageSize        每页显示的记录数，默认为10
     * @return 响应结果，包含分页后的处理过的告警记录列表
     */
    IPage<AlmRecordRespDTO> queryAlarmRecordPageList(AlmRecordReqDTO almRecordReqDto, Integer pageNo, Integer pageSize);

    /**
     * 取消告警
     *
     * @param cancelAlarmReqDTO 取消告警请求DTO
     */
    void cancelAlarm(CancelAlarmReqDTO cancelAlarmReqDTO);
    /**
     * 故障告警服务
     *
     * @param id 告警记录ID
     * @return 告警记录响应DTO
     */
    AlmRecordRespDTO faultAlarmService(String id);
}
