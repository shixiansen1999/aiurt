package com.aiurt.modules.train.mistakes.dto.resp;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 错题集 - app查看审核详情接口
 *
 * @author 华宜威
 * @date 2023-08-28 15:35:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BdExamMistakesAppReviewDetailRespDTO extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**错题集id*/
    @ApiModelProperty(value = "错题集id")
    private String id;

    /**考错题集名称，就是原考卷名称*/
    @ApiModelProperty(value = "错题集名称，就是原考卷名称")
    private String name;

    /**原考卷总分*/
    @ApiModelProperty(value = "原考卷总分")
    private Integer score;

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

    /**错题集状态，1未开答，2待审核，3已驳回，4已通过，默认1*/
    @ApiModelProperty(value = "错题集状态，1未开答，2待审核，3已驳回，4已通过，默认1")
    @Dict(dicCode = "bd_exam_mistake_state")
    private Integer state;

}
