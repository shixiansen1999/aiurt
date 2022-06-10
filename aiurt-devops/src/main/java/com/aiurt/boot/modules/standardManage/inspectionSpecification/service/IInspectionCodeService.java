package com.aiurt.boot.modules.standardManage.inspectionSpecification.service;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.standardManage.inspectionSpecification.entity.InspectionCode;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 检修标准管理
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IInspectionCodeService extends IService<InspectionCode> {

    Result copy(String id);

    Result addAnnualPlan(String id) throws Exception;

    Result addAnnualNewPlan(String id) throws Exception;
}
