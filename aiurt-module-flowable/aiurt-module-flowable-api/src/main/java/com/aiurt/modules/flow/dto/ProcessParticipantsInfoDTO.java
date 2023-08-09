package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
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

    private List<ProcessParticipantsInfoDetailsDTO> data;
}
