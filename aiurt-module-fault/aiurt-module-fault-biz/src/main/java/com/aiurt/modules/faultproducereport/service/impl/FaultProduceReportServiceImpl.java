package com.aiurt.modules.faultproducereport.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.aiurt.modules.faultproducereport.mapper.FaultProduceReportMapper;
import com.aiurt.modules.faultproducereport.service.IFaultProduceReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 生产日报
 * @Author: aiurt
 * @Date: 2023-02-23
 * @Version: V1.0
 */
@Service
public class FaultProduceReportServiceImpl extends ServiceImpl<FaultProduceReportMapper, FaultProduceReport> implements IFaultProduceReportService, IFlowableBaseUpdateStatusService {

    @Autowired
    private  FaultProduceReportMapper produceReportMapper;
    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        String businessKey = updateStateEntity.getBusinessKey();
        FaultProduceReport faultProduceReport = this.getById(businessKey);
        if (ObjectUtil.isEmpty(faultProduceReport)) {
            throw new AiurtBootException("未找到ID为【" + businessKey + "】的数据！");
        } else {
            int states = updateStateEntity.getStates();
            switch (states) {
                case 2:
                    faultProduceReport.setState(1);
                case 3:
                    faultProduceReport.setState(0);


            }
        }
    }
    /**
     * 保存或者编辑年演练计划信息
     *
     * @param faultProduceReport
     * @return
     */
    public String startProcess(FaultProduceReport faultProduceReport) {
        String id = faultProduceReport.getId();

            return id;
    }
    @Override
    public Result<FaultProduceReport> getDetail() {
        FaultProduceReport produceReport= produceReportMapper.getDetail();
        return  Result.OK(produceReport);
    }
}
