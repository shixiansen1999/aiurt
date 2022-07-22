package com.aiurt.modules.situation.dto;

import com.aiurt.modules.situation.entity.SysAnnouncement;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author lkj
 */
@Data
@ApiModel("消息通知")
public class SysAnnouncementDTO {
    private static final long serialVersionUID = 1L;

    /**
     * 已读
     * */
    private Integer readCount;

    /**
     * 未读
     * */
    private Integer unreadCount;

    private List<SysAnnouncement> sysAnnouncementList;
}
