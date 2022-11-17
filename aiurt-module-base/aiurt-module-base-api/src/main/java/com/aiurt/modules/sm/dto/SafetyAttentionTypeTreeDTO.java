package com.aiurt.modules.sm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author MrWei
 * @Date 2022/11/17 12:13
 **/
@Data
public class SafetyAttentionTypeTreeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对应SafetyAttentionType中的id字段,前端数据树中的key
     */
    @ApiModelProperty("前端数据树中的key")
    private String key;

    /**
     * 对应SafetyAttentionType的id字段,前端数据树中的value
     */
    @ApiModelProperty("前端数据树中的value")
    private String value;

    /**
     * 对应depart_name字段,前端数据树中的title
     */
    @ApiModelProperty("前端数据树中的title")
    private String title;

    /**
     * 父节点id
     */
    @ApiModelProperty("父节点id")
    private String pid;

    @ApiModelProperty("孩子节点")
    private List<SafetyAttentionTypeTreeDTO> children = new ArrayList<>();
}
