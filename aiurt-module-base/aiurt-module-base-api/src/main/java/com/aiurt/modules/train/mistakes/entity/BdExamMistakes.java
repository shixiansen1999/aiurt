package com.aiurt.modules.train.mistakes.entity;

import com.aiurt.common.system.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 错题集实体类
 *
 * @author 华宜威
 * @date 2023-08-24 17:58:47
 */
@Data
@TableName("bd_exam_mistakes")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bd_exam_mistakes对象", description = "错题集")
public class BdExamMistakes extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**考试人员id，关联sys_user.id*/
    @ApiModelProperty(value = "考试人员id，关联sys_user.id")
    private String userId;

    /**培训任务id，关联bd_train_task.id*/
    @ApiModelProperty(value = "考培训任务id，关联bd_train_task.id")
    private String trainTaskId;

    /**考错题集名称，就是原考卷名称*/
    @ApiModelProperty(value = "错题集名称，就是原考卷名称")
    private String name;

    /**开始答错题集的时间*/
    @ApiModelProperty(value = "开始答错题集的时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**提交错题集的时间*/
    @ApiModelProperty(value = "提交错题集的时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /**答错题集总用时，单位秒*/
    @ApiModelProperty(value = "答错题集总用时，单位秒")
    private Integer useTime;

    /**错题集状态，1未开答，2答题中，3审核中，4已驳回，5已通过，默认1*/
    @ApiModelProperty(value = "错题集状态，1未开答，2答题中，3审核中，4已驳回，5已通过，默认1")
    private Integer state;

    /**伪删除 0未删除 1已删除*/
    @ApiModelProperty(value = "伪删除 0未删除 1已删除")
    private Integer delFlag;

}
