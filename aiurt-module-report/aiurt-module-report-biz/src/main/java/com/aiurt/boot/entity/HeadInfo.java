package com.aiurt.boot.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeadInfo implements Serializable {

    private static final long serialVersionUID = 8958746611747526363L;
    /**
     * 表头名称
     */
    @ApiModelProperty(value = "表头名称")
    private String title;
    /**
     * 表头字段名称
     */
    @ApiModelProperty(value = "表头字段名称")
    private String dataIndex;
    /**
     * 表头信息
     */
    @ApiModelProperty(value = "表头信息")
    private List<HeadInfo> children;
}
