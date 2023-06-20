package com.aiurt.modules.personnelportrait.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author
 * @description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "综合表现评分", description = "综合表现评分")
public class DashboardResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

//    /**
//     * 用户ID
//     */
//    @ApiModelProperty(value = "用户ID")
//    private String userId;

    /**
     * 分数
     */
    @ApiModelProperty(value = "分数")
    private BigDecimal score;

    /**
     * 等级:分值60-69，显示一般； 70-79，显示中等； 80-89，显示良好； 90-100显示优秀
     */
    @ApiModelProperty(value = "等级:分值60-69，显示一般； 70-79，显示中等； 80-89，显示良好； 90-100显示优秀")
    private String grade;

    /**
     * 班组排名
     */
    @ApiModelProperty(value = "班组排名")
    private Integer orgRank;
    /**
     * 班组人数
     */
    @ApiModelProperty(value = "班组人数")
    private Integer orgTotal;

//    /**
//     * 专业排名
//     */
//    @ApiModelProperty(value = "专业排名")
//    private Integer majorRank;
//    /**
//     * 专业人数
//     */
//    @ApiModelProperty(value = "专业人数")
//    private Integer majorTotal;

//    /**
//     * 绩效
//     */
//    @ApiModelProperty(value = "绩效")
//    private List<PerformanceResDTO> performances;

}
