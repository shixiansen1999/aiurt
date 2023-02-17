package com.aiurt.modules.system.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023/2/17
 * @time: 12:12
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023-02-17 12:12
 */
@Data
public class SysMessageInfoDTO {
    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String msgContent;

    @ApiModelProperty("创建时间")
    private String intervalTime;

    @ApiModelProperty("已读未读标识")
    private String readFlag;

    @ApiModelProperty("消息类型：1:通知公告2:系统消息3:特情消息")
    private String  msgCategory;

    @ApiModelProperty("摘要")
    private String msgAbstract;

    @ApiModelProperty("发布内容")
    private String publishingContent;



}
