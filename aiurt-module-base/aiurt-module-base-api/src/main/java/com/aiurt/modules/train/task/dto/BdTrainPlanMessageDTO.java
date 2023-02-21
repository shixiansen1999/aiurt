package com.aiurt.modules.train.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LKJ
 */
@Data
public class BdTrainPlanMessageDTO {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

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
