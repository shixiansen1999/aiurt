package com.aiurt.modules.faultproducereportlinedetail.dto;

import com.aiurt.modules.faultproducereportlinedetail.entity.FaultProduceReportLineDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FaultProduceReportLineDetailDTO extends FaultProduceReportLineDetail {
    /**专业编码*/
    private String majorCode;
    /**专业名称*/
    private String majorName;
}
