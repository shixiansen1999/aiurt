package com.aiurt.modules.fault.dto;

import com.aiurt.modules.basic.entity.DictEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : sbx
 * @Classname : FaultForSendMessageDTO
 * @Description : TODO
 * @Date : 2023/6/20 10:04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("获取需要发送消息的故障信息")
public class FaultForSendMessageDTO extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "故障报修编码")
    private String code;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "接收任务时间")
    private Date receviceTime;

    @ApiModelProperty(value = "维修负责人")
    private String appointUserName;
}
