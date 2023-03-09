package com.aiurt.modules.system.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023/2/16
 * @time: 14:26
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023-02-16 14:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysMessageTypeDTO {
    @ApiModelProperty(value = "主键id")
    private String id;

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

    @ApiModelProperty(value = "消息标识（1：业务，2：流程，0：系统公告，消息，特情）")
    private String messageFlag;

    @ApiModelProperty("消息类型：1:通知公告2:系统消息3:特情消息")
    private String  msgCategory;

    @ApiModelProperty(value = "消息类型")
    private String busType;

    @ApiModelProperty(value = "消息内容")
    private String titleContent;

    @ApiModelProperty(value = "头像值")
    private String value;

}
