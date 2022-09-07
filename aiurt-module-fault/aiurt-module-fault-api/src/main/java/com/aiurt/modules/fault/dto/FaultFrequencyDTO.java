package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zwl
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultFrequencyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "专业子系统编码")
    @TableField(exist = false)
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @SystemFilterColumn
    private String subSystemCode;


    @ApiModelProperty(value = "专业子系统名称")
    @TableField(exist = false)
    private String subSystemName;


    @ApiModelProperty(value = "次数")
    @TableField(exist = false)
    private String number;
}
