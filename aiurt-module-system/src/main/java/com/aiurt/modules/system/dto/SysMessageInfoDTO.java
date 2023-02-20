package com.aiurt.modules.system.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023/2/17
 * @time: 12:12
 */

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023-02-17 12:12
 */
@Data
public class SysMessageInfoDTO extends DictEntity {
    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String msgContent;

    @ApiModelProperty("创建时间")
    private String intervalTime;

    @ApiModelProperty("已读未读标识（0未读，1已读）")
    @Dict(dicCode = "read_flag")
    private String readFlag;

    @ApiModelProperty("消息类型：1:通知公告2:系统消息3:特情消息")
    private String  msgCategory;

    @ApiModelProperty("摘要")
    private String msgAbstract;

    @ApiModelProperty("发布内容")
    private String publishingContent;

    @ApiModelProperty(value = "任务类型（待办池类型：0：待办、1：已办、2：待阅、3：已阅）")
    @Dict(dicCode = "todo_task_state")
    private String todoType;



}
