package com.aiurt.boot.modules.fault.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: swsc
 * 故障查询参数列表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Validated
public class FaultParam {
    /**
     * 系统编号
     */
    private String systemCode;

    /**
     * 故障编号
     */
    private String code;

    /**
     * 站点编号
     */
    private String stationCode;

    /**
     * 设备编码
     */
    private String devicesIds;

    /**
     * 状态
     */
    private String status;

    /**
     * 故障类型
     */
    private Integer faultType;

    /**
     * 登记开始时间
     */
    private String dayStart;

    /**
     * 登记结束时间
     */
    private String dayEnd;

    /**
     * 登记结束时间
     */
    private String repairWay;
}
