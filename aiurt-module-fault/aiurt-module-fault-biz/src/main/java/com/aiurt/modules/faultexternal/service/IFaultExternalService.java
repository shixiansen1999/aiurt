package com.aiurt.modules.faultexternal.service;

import com.aiurt.modules.fault.dto.RepairRecordDTO;
import com.aiurt.modules.faultexternal.dto.FaultExternalDTO;
import com.aiurt.modules.faultexternal.entity.FalutExternalReceiveDTO;
import com.aiurt.modules.faultexternal.entity.FaultExternal;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 调度系统故障
 * @Author: aiurt
 * @Date:   2023-02-16
 * @Version: V1.0
 */
public interface IFaultExternalService extends IService<FaultExternal> {
    Result<?> addFaultExternal(FaultExternalDTO dto, HttpServletRequest req);

     void complete(RepairRecordDTO dto, LoginUser user);

    Page<FaultExternal> selectPage(Page<FaultExternal> page, FaultExternal faultExternal);
}
