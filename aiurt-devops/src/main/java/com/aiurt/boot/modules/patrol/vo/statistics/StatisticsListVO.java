package com.aiurt.boot.modules.patrol.vo.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: StatisticsListVO
 * @author: Mr.zhao
 * @date: 2021/11/19 11:12
 */
@ApiModel(value = "统计-列表vo", description = "")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class StatisticsListVO implements Serializable {

    public StatisticsListVO() {
        allSize = 0;
        successFlag = 0;
        errorFlag = 0;
        unFlag = 0;
        warmFlag = 0;
    }


    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "全部数量")
    private Integer allSize;

    @ApiModelProperty(value = "已完成")
    private Integer successFlag;

    @ApiModelProperty(value = "未完成")
    private Integer unFlag;

    @ApiModelProperty(value = "异常")
    private Integer warmFlag;

    @ApiModelProperty(value = "故障")
    private Integer errorFlag;

}
