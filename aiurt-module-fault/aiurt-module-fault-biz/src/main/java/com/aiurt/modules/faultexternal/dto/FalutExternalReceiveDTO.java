package com.aiurt.modules.faultexternal.dto;

import com.aiurt.modules.faultexternal.entity.FaultExternal;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="运维系统接收故障报修信息对象", description="运维系统接收故障报修信息对象")
public class FalutExternalReceiveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "message")
    private String message;

    @ApiModelProperty(value = "FaultExternal对象")
    private FaultExternal data;

}
