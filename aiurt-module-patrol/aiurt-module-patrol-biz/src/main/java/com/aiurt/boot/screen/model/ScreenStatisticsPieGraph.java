package com.aiurt.boot.screen.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "ScreenStatisticsPieGraph", description = "大屏巡视模块-巡视任务完成情况对象")
public class ScreenStatisticsPieGraph {

    /**
     * 班组
     */
    @ApiModelProperty(value = "班组")
    private String orgName;
    /**
     * 已完成数
     */
    @ApiModelProperty(value = "已完成数")
    private Long finish;
    /**
     * 未完成数
     */
    @ApiModelProperty(value = "未完成数")
    private Long unfinish;
}
