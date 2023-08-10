package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.system.vo.SysUserModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:wgp
 * @create: 2023-08-08 11:58
 * @Description:
 */
@Data
public class ProcessParticipantsInfoDTO implements Serializable {

    @ApiModelProperty("维度标题")
    private String title;

    @ApiModelProperty("流程节点id")
    private String nodeId;

    private List<ProcessParticipantsInfoDTO> options;

    private List<SysUserModel> data;
}
