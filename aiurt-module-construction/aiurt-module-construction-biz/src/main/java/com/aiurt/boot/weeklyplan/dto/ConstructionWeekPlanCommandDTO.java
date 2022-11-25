package com.aiurt.boot.weeklyplan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: ConstructionWeekPlanCommandDTO对象
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
@Api(tags = "ConstructionWeekPlanCommandDTO对象")
@Data
@Accessors(chain = true)
public class ConstructionWeekPlanCommandDTO {
    /**
     * 作业编码
     */
    @ApiModelProperty(value = "作业编码")
    private String lineCode;
    /**
     * 所属周
     */
    @ApiModelProperty(value = "所属周")
    private Integer week;
    /**
     * 所属周的开始日期
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "所属周的开始日期yyyy-MM-dd")
    private Date starDate;
    /**
     * 所属周的结束日期
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "所属周的结束日期yyyy-MM-dd")
    private Date endDate;
    /**
     * 计划类型(0正常计划 1计划补修 2日计划补充)
     */
    @ApiModelProperty(value = "计划类型(0正常计划 1计划补修 2日计划补充)")
    private Integer planChange;
    /**
     * 审批状态(0待提审、1待审核、2审核中、3已驳回、4已取消、5已通过)
     */
    @ApiModelProperty(value = "审批状态(0待提审、1待审核、2审核中、3已驳回、4已取消、5已通过)")
    private Integer formStatus;
    /**
     * 周计划查询列表传；1待审核、2审核中、3已驳回、4已取消、5已通过
     * 周计划申报列表传：0待提审、3已驳回、4已取消状态值；
     * 周计划审核列表传：0待审核、2审核中状态值；
     * 周计划变更列表传：0待提审、3已驳回、4已取消状态值；
     */
    @ApiModelProperty(value = "周计划查询列表传；1待审核、2审核中、3已驳回、4已取消、5已通过" +
            "周计划申报列表传：0待提审、3已驳回、4已取消状态值；" +
            "周计划审核列表传：0待审核、2审核中状态值；" +
            "周计划变更列表传：0待提审、3已驳回、4已取消状态值")
    private List<Integer> formStatusList;
    /**
     * 周计划变更列表时传：1计划补修 2日计划补充状态值
     */
    @ApiModelProperty(value = "周计划变更列表时传：1计划补修 2日计划补充状态值")
    private List<Integer> planChangeList;

}
