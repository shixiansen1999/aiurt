package com.aiurt.boot.modules.apphome.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * The preson who loves you has gone night and night,
 * walking on the way.
 *
 * @purpose: UserTaskParam
 * @data: 2021/11/26 0:59
 * @author: Mr. zhao
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UserTaskAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    private List<String> userIds;

    @ApiModelProperty(value = "类型 1.巡检 2.检修 3.故障 4.工作日志")
    private Integer type;

    /**
     * 回调的 code与id 不能同时为空
     */
    @ApiModelProperty(value = "回调的编号code")
    private String recordCode;
    @ApiModelProperty(value = "回调的主键id")
    private Long recordId;

    /**
     * 任务编号: 必传字段
     */
    @ApiModelProperty(value = "任务编号")
    private String workCode;

    /**
     * 发生时间: 故障专用字段
     */
    @ApiModelProperty(value = "发生时间: 故障专用字段")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date productionTime;


    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 1.工作日志未上报 (废弃)
     * 2.检修,巡检
     * 3-4.普通故障(自检与保修)
     * 5-6.重大故障(自检与保修)
     * 9.工作日志未上报  最大,显示最下面
     */
    @ApiModelProperty(value = "级别字段,排序依据 ")
    private Integer level;

    /**
     * 保留字段,自行选用
     */
    @ApiModelProperty(value = "保留字段,自行选用")
    private String content;

    @ApiModelProperty(value = "保留字段,自行选用")
    private String note;

}
