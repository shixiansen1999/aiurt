package com.aiurt.boot.plan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.plan.dto
 * @className: DeviceListDOT
 * @author: life-0
 * @date: 2022/8/1 16:06
 * @description: TODO
 * @version: 1.0
 */
@Data
public class DeviceListDTO {
    @ApiModelProperty(value = "站点多选Code")
    String siteCode;
    @ApiModelProperty(value = "站点CodeList")
    List<String> siteCodes;
    @ApiModelProperty(value = "适用系统code")
    String subsystemCode;
    @ApiModelProperty(value = "专业code")
    String majorCode;
    @ApiModelProperty(value = "设备类型code")
    String deviceTypeCode;
    @ApiModelProperty(value = "设备code")
    String deviceCode;
    @ApiModelProperty(value = "设备name")
    String deviceName;
    @ApiModelProperty(value = "设备类型多选字符串")
    String deviceTypeCodes;
    @ApiModelProperty(value = "设备类型多选")
    private List<String> deviceTypeCodeList;
}
