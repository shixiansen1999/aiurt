package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-08 9:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultLargeInfoDTO extends DictEntity {
    private static final long serialVersionUID = 1L;

    /**专业子系统编码*/
    @ApiModelProperty(value = "专业子系统编码")
    @SystemFilterColumn
    private String subSystemCode;

    @ApiModelProperty(value = "专业子系统编码")
    private String systemName;


    /**站点*/
    @ApiModelProperty(value = "站点",  required = true)
    private String stationCode;

    @ApiModelProperty(value = "站点",  required = true)
    private String stationName;

    /**故障发生时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "dd日 HH:mm")
    @DateTimeFormat(pattern="dd日 HH:mm" )
    @ApiModelProperty(value = "故障发生时间dd HH:mm",  required = true)
    private Date happenTime;

    @ApiModelProperty("维修负责人")
    private String appointUserName;

    @ApiModelProperty("故障状态")
    private String status;

    @ApiModelProperty("故障状态名称")
    private String statusName;

    /**线路编码*/
    @ApiModelProperty(value = "线路编码", required = true)
    private String lineCode;


}
