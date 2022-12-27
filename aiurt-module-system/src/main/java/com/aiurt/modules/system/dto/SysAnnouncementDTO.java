package com.aiurt.modules.system.dto;

import com.aiurt.modules.system.entity.SysAnnouncement;
import com.aiurt.modules.todo.entity.SysTodoList;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/12/2711:21
 */
@Data
public class SysAnnouncementDTO {
    @ApiModelProperty("消息列表")
    private List<SysAnnouncement> sysMsgList;
    @ApiModelProperty("通知列表")
    private List<SysAnnouncement> anntMsgList;
    @ApiModelProperty("待办任务列表")
    private List<SysTodoList> todoTaskList;
    @ApiModelProperty("消息数量")
    private Long sysMsgTotal;
    @ApiModelProperty("通知数量")
    private Long anntMsgTotal;
    @ApiModelProperty("待办任务数量")
    private Long todoTaskTotal;

}
