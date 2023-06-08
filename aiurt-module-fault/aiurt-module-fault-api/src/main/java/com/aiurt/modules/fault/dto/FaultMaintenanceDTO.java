package com.aiurt.modules.fault.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultMaintenanceDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户账号
     */
    private String username;
    /**
     * 设备类型编号
     */
    private String deviceTypeCode;
    /**
     * 设备类型名称
     */
    private String deviceTypeName;
    /**
     * 维修最多的设备类型数量
     */
    private Integer num;
}
