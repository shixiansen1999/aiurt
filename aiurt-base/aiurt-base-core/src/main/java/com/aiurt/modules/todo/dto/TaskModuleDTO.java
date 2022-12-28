package com.aiurt.modules.todo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 各个类型待办数量
 * @Author MrWei
 * @Date 2022/12/22 10:52
 **/
@Data
public class TaskModuleDTO {
    @ApiModelProperty("标记字段")
    private String field;
    @ApiModelProperty("数量")
    private Integer count;
    @ApiModelProperty("名称")
    private String name;
}
