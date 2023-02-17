package com.aiurt.modules.system.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023/2/16
 * @time: 14:26
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023-02-16 14:26
 */
@Data
public class SysMessageTypeDTO {
    @ApiModelProperty(value = "消息类型名称")
    private String title;

    @ApiModelProperty(value = "消息数量")
    private Integer count;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date intervalTime;

    @ApiModelProperty(value = "消息标识")
    private String messageFlag;

    @ApiModelProperty(value = "消息内容")
    private String titleContent;
}
