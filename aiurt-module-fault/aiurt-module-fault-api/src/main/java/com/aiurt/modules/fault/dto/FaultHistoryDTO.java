package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author
 * @description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "处理过的故障设备", description = "处理过的故障设备")
public class FaultHistoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 故障设备名
     */
    private String name;
    /**
     * 处理数
     */
    private Integer value;

}
