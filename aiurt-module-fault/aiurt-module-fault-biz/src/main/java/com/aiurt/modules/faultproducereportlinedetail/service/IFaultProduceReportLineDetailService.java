package com.aiurt.modules.faultproducereportlinedetail.service;

import com.aiurt.modules.faultproducereportlinedetail.dto.FaultProduceReportLineDetailDTO;
import com.aiurt.modules.faultproducereportlinedetail.entity.FaultProduceReportLineDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 专业故障清单
 * @Author: aiurt
 * @Date:   2023-02-23
 * @Version: V1.0
 */
public interface IFaultProduceReportLineDetailService extends IService<FaultProduceReportLineDetail> {

    void updateListByIds(List<FaultProduceReportLineDetailDTO> reportLineDetailDTOList);

}
