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
    /**是否影响行车-text*/
    private String affectDriveName;
    /**是否影响客运服务-text*/
    private String affectPassengerServiceName;
    /**是否停止服务-text*/
    private String isStopServiceName;
}
