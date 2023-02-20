package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author lkj
 */
@Data
public class PatrolMessageDTO {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
    /**
     * 任务编号
     */
    @Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
    private java.lang.String code;
    /**
     * 任务名称
     */
    @Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private java.lang.String name;

    /**
     * 巡检开始时间(HH:mm)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @ApiModelProperty(value = "巡检开始时间(HH:mm)")
    private java.util.Date startTime;
    /**
     * 巡检结束时间(HH:mm)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @ApiModelProperty(value = "巡检结束时间(HH:mm)")
    private java.util.Date endTime;

    /**
     * 业务类型
     */
    private String busType;
    /**
     * 消息类型：org.jeecg.common.constant.enums.MessageTypeEnum
     *  XT("system",  "系统消息")
     *  YJ("email",  "邮件消息")
     *  DD("dingtalk", "钉钉消息")
     *  QYWX("wechat_enterprise", "企业微信")
     */
    protected String messageType;

    /**
     * 模板消息对应的模板编码
     */
    protected String templateCode;
    /**
     * 摘要
     */
    private java.lang.String msgAbstract;
    /**
     * 发布内容
     */
    private String publishingContent;
}
