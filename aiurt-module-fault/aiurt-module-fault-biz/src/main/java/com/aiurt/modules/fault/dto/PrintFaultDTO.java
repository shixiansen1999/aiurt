package com.aiurt.modules.fault.dto;

import lombok.Data;

/**
 * @className: PrintFaultDTO
 * @author: hqy
 * @date: 2023/8/24 14:42
 * @version: 1.0
 */
@Data
public class PrintFaultDTO {

    private String serialNumber;

    private String deviceName;

    private String deviceCode;

    private Integer num;


}
