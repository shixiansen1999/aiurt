package com.aiurt.modules.faultproducereport.service;

import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: 生产日报
 * @Author: aiurt
 * @Date:   2023-02-23
 * @Version: V1.0
 */
public interface IFaultProduceReportService extends IService<FaultProduceReport> {

    Result<FaultProduceReport> getDetail();
}
