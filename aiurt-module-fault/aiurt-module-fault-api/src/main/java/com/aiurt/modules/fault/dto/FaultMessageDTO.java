package com.aiurt.modules.fault.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author lkj
 */
@Data
public class FaultMessageDTO {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;


    /**故障报修编码*/
    @Excel(name = "故障报修编码", width = 15)
    @ApiModelProperty(value = "故障报修编码")
    private String code;

    /**线路编码*/
    @Excel(name = "故障位置-线路编码", width = 15)
    @ApiModelProperty(value = "线路编码", required = true)
    private String lineCode;

    /**站点*/
    @Excel(name = "故障位置-站所编码", width = 15)
    @ApiModelProperty(value = "站点",  required = true)
    private String stationCode;

    /**位置*/
    @Excel(name = "故障位置-位置编码", width = 15)
    @ApiModelProperty(value = "位置")
    private String stationPositionCode;

    /**故障级别*/
    @Excel(name = "故障级别", width = 15)
    @ApiModelProperty(value = "故障级别")
    private String faultLevel;

    /**紧急程度*/
    @Excel(name = "紧急程度", width = 15)
    @ApiModelProperty(value = "fault_urgency,紧急程度,0:低,1:中,2高")
    private Integer urgency;

    @ApiModelProperty(value = "故障分类")
    private String faultTypeCode;

    /**报修方式*/
    @Excel(name = "报修方式", width = 15)
    @ApiModelProperty(value = "报修方式",example = "")
    private String faultModeCode;

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


}
