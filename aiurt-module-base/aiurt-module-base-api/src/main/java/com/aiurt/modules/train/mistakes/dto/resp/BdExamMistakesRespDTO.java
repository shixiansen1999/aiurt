package com.aiurt.modules.train.mistakes.dto.resp;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 错题集接口的列表响应DTO
 *
 * @author 华宜威
 * @date 2023-08-25 11:39:41
 */
@Data
public class BdExamMistakesRespDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**错题集id*/
    @ApiModelProperty(value = "错题集id")
    private String id;

    /**考试人员id，关联sys_user.id*/
    @ApiModelProperty(value = "考试人员id，关联sys_user.id")
    private String userId;

    /**考试人员真实姓名*/
    @ApiModelProperty(value = "考试人员真实姓名")
    private String realName;

    /**培训任务id，关联bd_train_task.id*/
    @ApiModelProperty(value = "考培训任务id，关联bd_train_task.id")
    private String trainTaskId;

    /**关联考试计划，也就是考试计划名称*/
    @ApiModelProperty(value = "联考试计划，也就是考试计划名称")
    private String examTaskName;

    /**考错题集名称，就是原考卷名称*/
    @ApiModelProperty(value = "错题集名称，就是原考卷名称")
    private String name;

    /**培训部门id*/
    @ApiModelProperty(value = "训部门id")
    private String taskTeamId;

    /**培训部门编码*/
    @ApiModelProperty(value = "训部门编码")
    private String taskTeamOrgCode;

    /**培训部门名称*/
    @ApiModelProperty(value = "培训部门名称")
    private String taskTeamName;

    /**错题集状态，1未开答，2待审核，3已驳回，4已通过，默认1*/
    @ApiModelProperty(value = "错题集状态，1未开答，2待审核，3已驳回，4已通过，默认1")
    @Dict(dicCode = "bd_exam_mistake_state")
    private Integer state;

}
