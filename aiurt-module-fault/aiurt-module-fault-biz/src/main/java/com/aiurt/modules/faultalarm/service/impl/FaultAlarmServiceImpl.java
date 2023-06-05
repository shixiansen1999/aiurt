package com.aiurt.modules.faultalarm.service.impl;

import com.aiurt.modules.faultalarm.dto.req.AlmRecordReqDTO;
import com.aiurt.modules.faultalarm.dto.req.CancelAlarmReqDTO;
import com.aiurt.modules.faultalarm.dto.resp.AlmRecordRespDTO;
import com.aiurt.modules.faultalarm.entity.AlmRecord;
import com.aiurt.modules.faultalarm.mapper.AlmRecordMapper;
import com.aiurt.modules.faultalarm.service.IFaultAlarmService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description: 集中告警实现类
 * @Author: wgp
 * @Date: 2023-06-05
 * @Version: V1.0
 */
@Slf4j
@Service
public class FaultAlarmServiceImpl extends ServiceImpl<AlmRecordMapper, AlmRecord> implements IFaultAlarmService {

    @Override
    public IPage<AlmRecordRespDTO> queryAlarmRecordPageList(AlmRecordReqDTO almRecordReqDto, Integer pageNo, Integer pageSize) {
        return null;
    }

    @Override
    public void cancelAlarm(CancelAlarmReqDTO cancelAlarmReqDTO) {

    }

    @Override
    public AlmRecordRespDTO faultAlarmService(String id) {
        return null;
    }
}
