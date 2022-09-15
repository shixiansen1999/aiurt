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
@ApiModel(value = "ScreenStatisticsGraph", description = "大屏巡视模块-巡视任务完成情况对象")
public class ScreenStatisticsGraph {

    /**
     * 班组名称
     */
    @ApiModelProperty(value = "班组名称")
    private String orgName;
    /**
     * 已完成数
     */
    @ApiModelProperty(value = "已完成数")
    private Long finish;
    /**
     * 已完成率
     */
    @ApiModelProperty(value = "已完成率")
    private String finishRate;
    /**
     * 未完成数
     */
    @ApiModelProperty(value = "未完成数")
    private Long unfinish;
    /**
     * 未完成率
     */
    @ApiModelProperty(value = "未完成率")
    private String unfinishRate;
    /**
     * 总数
     */
    @ApiModelProperty(value = "总数")
    private Long total;
}
