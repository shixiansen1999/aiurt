package com.aiurt.boot.monthlyplan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author: Sand Sculpture King
 * @date: 2021年05月25日 14:18
 */
@Data
@ApiModel(value = "作业类别下拉框对象" ,description = "下拉框内容")
public class BdWorkTypeDTO implements Serializable {
    @ApiModelProperty(value = "类别id")
    private String id;
    @ApiModelProperty(value = "类别名称")
    private String name;

}
