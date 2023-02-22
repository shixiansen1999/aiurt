package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class RepairTaskMessageDTO {
    /**主键id,自动递增*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id,自动递增")
    private java.lang.String id;
    /**编号,示例:JX20211105 */
    @Excel(name = "编号,示例:JX20211105 ", width = 15)
    @ApiModelProperty(value = "编号,示例:JX20211105 ")
    private java.lang.String code;
    /**检修周期类型，0周检、1月检、2双月检、3季检、4半年检、5年检*/
    @Excel(name = "检修周期类型，0周检、1月检、2双月检、3季检、4半年检、5年检", width = 15)
    @ApiModelProperty(value = "检修周期类型，0周检、1月检、2双月检、3季检、4半年检、5年检")
    private java.lang.Integer type;
    /**计划开始时间，精确到分钟*/
    @Excel(name = "计划开始时间，精确到分钟", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "计划开始时间，精确到分钟")
    private java.util.Date startTime;
    /**计划结束时间，精确到分钟*/
    @Excel(name = "计划结束时间，精确到分钟", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "计划结束时间，精确到分钟")
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
