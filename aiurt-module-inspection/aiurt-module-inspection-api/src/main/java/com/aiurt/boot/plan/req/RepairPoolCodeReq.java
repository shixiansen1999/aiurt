package com.aiurt.boot.plan.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/6/3017:59
 */
@Data
public class RepairPoolCodeReq {
    @ApiModelProperty(value = "检修标准编码", required = true)
    private String code;
    @ApiModelProperty(value = "检修设备编码", required = true)
    private List<String> deviceCodes;
}
